/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.remoting.netty;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.baili.remoting.RemotingConfig;
import com.baili.remoting.protocol.RemotingProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by ningweiyu on 17/1/6.
 */
@ChannelHandler.Sharable
public class NettyEncoder extends MessageToByteEncoder<RemotingProtocol> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelHandler.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, RemotingProtocol msg, ByteBuf out) throws Exception {

        LOGGER.info("NettyEncoder");
        if (msg == null) {
            return;
        }

        byte[] headerBytes = JSON.toJSONString(msg).getBytes(RemotingConfig.DEFAULT_ENCODING);

        // length + sign + header size + header data
        int length = 4 + 4 + 4 + headerBytes.length;

        byte[] bodyBytes = null;
        if (msg.getBody() != null) {
            bodyBytes = JSON.toJSONString(msg.getBody()).getBytes(RemotingConfig.DEFAULT_ENCODING);
            // body size + body data
            length += 4 + bodyBytes.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(length);

        // length
        buffer.putInt(length);

        // sign
        buffer.putInt(0);

        // header size
        buffer.putInt(headerBytes.length);

        // header data
        buffer.put(headerBytes);

        if (bodyBytes != null) {
            // body size
            buffer.putInt(bodyBytes.length);

            // body
            buffer.put(bodyBytes);
        }

        buffer.flip();
        out.writeBytes(buffer);
    }
}
