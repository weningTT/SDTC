/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.remoting.netty;

import com.baili.remoting.protocol.RemotingProtocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by ningweiyu on 17/1/7.
 */
public class NettyChannelHandler extends SimpleChannelInboundHandler<RemotingProtocol> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemotingProtocol msg) throws Exception {

    }
}
