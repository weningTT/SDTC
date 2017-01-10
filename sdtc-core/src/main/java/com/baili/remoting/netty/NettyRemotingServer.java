/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * Created by ningweiyu on 17/1/5.
 */
public class NettyRemotingServer implements RemotingServer {

    private final ServerBootstrap bootstrap = new ServerBootstrap();

    private final EventLoopGroup bossSelectorGroup;

    private final EventLoopGroup workerSelectorGroup;

    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

    private final InetSocketAddress localAddress;

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
                        ch.pipeline().addLast(
                                defaultEventExecutorGroup,
                                new NettyEncoder(),
                                new NettyDecoder(),
                                new IdleStateHandler(0, 0, 60),
                                new NettyChannelHandler()
                        );
                    }
                });

        try {
            this.bootstrap.bind().sync();
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
        return false;
    }
}
