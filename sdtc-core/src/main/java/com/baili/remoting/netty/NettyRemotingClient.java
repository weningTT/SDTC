/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * Created by ningweiyu on 17/1/5.
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

        localAddress = new InetSocketAddress(AddressUtil.getLocalInetAddress(), RemotingConfig.SERVER_PORT);

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
                    ChannelFuture channelFuture = this.bootstrap.connect(socketAddress);
                    channel = new NettyChannel(channelFuture);
                }
            } catch (Exception e) {
                LOGGER.error("connect server fail. host=" + socketAddress.getHostName() + ",port=" + socketAddress.getPort());
            } finally {
                newChannelLock.unlock();
            }

        }

        return channel;
    }
}
