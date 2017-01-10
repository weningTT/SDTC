/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.remoting.netty;

import com.baili.remoting.Channel;

import io.netty.channel.ChannelFuture;

/**
 * Created by ningweiyu on 17/1/8.
 */
public class NettyChannel implements Channel {

    private ChannelFuture channelFuture;

    public NettyChannel(ChannelFuture channelFuture){
        this.channelFuture = channelFuture;
    }

    @Override
    public boolean isActive() {
        return this.channelFuture.channel() != null
                && this.channelFuture.channel().isActive();
    }

    @Override
    public boolean isDone() {
        return this.channelFuture.isDone();
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return this.channelFuture.channel().writeAndFlush(msg);
    }

}
