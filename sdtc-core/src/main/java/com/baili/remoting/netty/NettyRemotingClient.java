/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.remoting.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baili.common.thread.NamedThreadFactory;
import com.baili.remoting.Channel;
import com.baili.remoting.RemotingClient;
import com.baili.remoting.RemotingClientCallback;
import com.baili.remoting.RemotingConfig;
import com.baili.remoting.ResponseEntity;
import com.baili.remoting.protocol.RemotingProtocol;
import com.baili.util.AddressUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * Created by Wenning on 17/1/5.
 */
public class NettyRemotingClient implements RemotingClient{

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup workerSelectorGroup;

    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

    private final InetSocketAddress localAddress;

    // ip:port => channel
    private final ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    // opaque => the response waiting to be processed
    private final ConcurrentHashMap<Long, ResponseEntity> responseMap = new ConcurrentHashMap<>();

    private final Lock newChannelLock = new ReentrantLock();

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRemotingClient.class);

    public NettyRemotingClient() {

        this.workerSelectorGroup = new NioEventLoopGroup(
                RemotingConfig.CLIENT_WORK_SELECTOR_GROUP_THREAD,
                new NamedThreadFactory("NettyClientWorkerGroup"));

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                RemotingConfig.CLIENT_WORKER_THREAD,
                new NamedThreadFactory("NettyClientWorkerThread")
        );

        localAddress = new InetSocketAddress(AddressUtil.getLocalInetAddress(), RemotingConfig.Client_PORT);

    }

    @Override
    public InetSocketAddress getLocalAddress() {

        return localAddress;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public void start() {

        this.bootstrap.group(this.workerSelectorGroup)
                .localAddress(getLocalAddress())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                defaultEventExecutorGroup,
                                new NettyEncoder(),
                                new NettyDecoder(),
                                new IdleStateHandler(0, 0, 60),
                                new NettyConnectManageHandler(),
                                new NettyChannelHandler());
                    }
                });

    }

    @Override
    public void stop() {

        this.workerSelectorGroup.shutdownGracefully();
        this.defaultEventExecutorGroup.shutdownGracefully();
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void async(SocketAddress socketAddress,
                      final RemotingProtocol protocol,
                      final RemotingClientCallback callback) {

        final long opaque = protocol.getOpaque();
        ResponseEntity responseEntity = new ResponseEntity(protocol, callback);
        responseMap.put(opaque, responseEntity);

        try {
            Channel channel = getAndCreateChannel((InetSocketAddress) socketAddress);

            channel.writeAndFlush(protocol).addListener(new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        // TODO delete me
                        LOGGER.info("async protocol success. protocol=" + protocol);
                    } else {
                        LOGGER.error("async protocol fail. protocol=" + protocol);
                        responseMap.remove(opaque);
                    }
                }
            });
        } catch (Exception e) {
            responseMap.remove(opaque);
        }

    }

    private Channel getAndCreateChannel(InetSocketAddress socketAddress){

        String address = socketAddress.getHostString() + ":" + socketAddress.getPort();
        LOGGER.info("addr is " + address);
        Channel channel = this.channelMap.get(address);

        if (channel != null && channel.isActive()) {
            return channel;
        }

        if (newChannelLock.tryLock()) {

            try {
                boolean needCreate = true;
                if (channel != null) {

                    if (channel.isActive()) {
                        return channel;
                    } else if (!channel.isDone()) {
                        needCreate = false;
                    } else {
                        this.channelMap.remove(address);
                    }
                }

                if (needCreate) {
                    ChannelFuture channelFuture = this.bootstrap.connect(socketAddress).sync();
                    channel = new NettyChannel(channelFuture);
                    LOGGER.info("connect success? " + channelFuture.channel().isActive());
                }
            } catch (Exception e) {
                LOGGER.error("connect server fail. host=" + socketAddress.getHostName() + ",port=" + socketAddress.getPort());
            } finally {
                newChannelLock.unlock();
            }

        }

        return channel;
    }

    class NettyConnectManageHandler extends ChannelDuplexHandler {
        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                            SocketAddress localAddress, ChannelPromise promise) throws Exception {
            final String local = localAddress == null ? "UNKNOW" : localAddress.toString();
            final String remote = remoteAddress == null ? "UNKNOW" : remoteAddress.toString();
            LOGGER.info("CLIENT : CONNECT  {} => {}", local, remote);
            super.connect(ctx, remoteAddress, localAddress, promise);

        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {

            LOGGER.info("CLIENT : DISCONNECT");
            super.disconnect(ctx, promise);
        }


        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            LOGGER.info("CLIENT : CLOSE");
            super.close(ctx, promise);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

            LOGGER.error("CLIENT : exceptionCaught exception.", cause);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;

                if (event.state().equals(IdleState.READER_IDLE)) {
                    System.out.println("READER_IDLE");
                    // 超时关闭channel
                    ctx.close();
                } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                    System.out.println("WRITER_IDLE");
                } else if (event.state().equals(IdleState.ALL_IDLE)) {
                    System.out.println("ALL_IDLE");
                    // 发送心跳
                    ctx.channel().write("ping\n");
                }
            }

            super.userEventTriggered(ctx, evt);
        }
    }
}
