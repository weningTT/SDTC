/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.remoting.netty;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baili.common.thread.NamedThreadFactory;
import com.baili.exception.RemotingException;
import com.baili.remoting.Channel;
import com.baili.remoting.RemotingConfig;
import com.baili.remoting.RemotingServer;
import com.baili.util.AddressUtil;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * Created by Wenning on 17/1/5.
 */
public class NettyRemotingServer implements RemotingServer {

    private final ServerBootstrap bootstrap = new ServerBootstrap();

    private final EventLoopGroup bossSelectorGroup;

    private final EventLoopGroup workerSelectorGroup;

    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

    private final InetSocketAddress localAddress;

    private ChannelFuture channelFuture;

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRemotingServer.class);

    public NettyRemotingServer(){

        this.bossSelectorGroup = new NioEventLoopGroup(
                RemotingConfig.SERVER_BOSS_SELECTOR_GROUP_THREAD,
                new NamedThreadFactory("NettyServerBossGroup"));

        this.workerSelectorGroup = new NioEventLoopGroup(
                RemotingConfig.SERVER_WORK_SELECTOR_GROUP_THREAD,
                new NamedThreadFactory("NettyServerWorkerGroup"));

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                RemotingConfig.SERVER_WORKER_THREAD,
                new NamedThreadFactory("NettyServerWorkerThread")
        );

        localAddress = new InetSocketAddress(AddressUtil.getLocalInetAddress(), RemotingConfig.SERVER_PORT);

    }

    @Override
    public Collection<Channel> getChannels() {
        return null;
    }

    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        return null;
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

        this.bootstrap.group(this.bossSelectorGroup, this.workerSelectorGroup)
                .localAddress(getLocalAddress())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(defaultEventExecutorGroup,
                                new NettyEncoder(),
                                new NettyDecoder(),
                                new IdleStateHandler(0, 0, 60),
                                new NettyConnectManageHandler(),
                                new NettyChannelHandler());
                    }
                });

        try {
            channelFuture = this.bootstrap.bind().sync();
        } catch (InterruptedException e) {
            throw new RemotingException("the bootstrap fails to bind.", e);
        }
    }

    @Override
    public void stop() {

        this.bossSelectorGroup.shutdownGracefully();
        this.workerSelectorGroup.shutdownGracefully();
        this.defaultEventExecutorGroup.shutdownGracefully();
    }

    @Override
    public boolean isRunning() {
        return channelFuture.channel().isActive();
    }

    class NettyConnectManageHandler extends ChannelDuplexHandler {
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("SERVER : channelRegistered");
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("SERVER : channelUnregistered");
            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("SERVER: channelActive");
            super.channelActive(ctx);

        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            LOGGER.info("SERVER: channelInactive");
            super.channelInactive(ctx);

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

            LOGGER.error("SERVER: exceptionCaught.", cause);

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

