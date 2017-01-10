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
import com.baili.remoting.protocol.RemotingProtocolBody;
import com.baili.remoting.protocol.RemotingSubmitRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by ningweiyu on 17/1/6.
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelHandler.class);

    public NettyDecoder() {

        super(1024 * 1024, 0, 4, 0, 4);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {

        LOGGER.info("NettyDecoder");
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        byte[] tmpBuf = new byte[frame.capacity()];
        frame.getBytes(0, tmpBuf);
        frame.release();

        ByteBuffer byteBuffer = ByteBuffer.wrap(tmpBuf);

        int length = byteBuffer.limit();
        int sign = byteBuffer.getInt();

        // header
        int headerSize = byteBuffer.getInt();
        byte[] headerBytes = new byte[headerSize];
        byteBuffer.get(headerBytes);

        String json = new String(headerBytes, RemotingConfig.DEFAULT_ENCODING);
        RemotingProtocol protocol = JSON.parseObject(json, RemotingProtocol.class);

        // body
        int remain = length - 4 - 4 - headerSize;
        if (remain > 0) {

            int bodySize = byteBuffer.getInt();
            byte[] bodyBytes = new byte[bodySize];
            byteBuffer.get(bodyBytes);

            String bodyJson = new String(headerBytes, RemotingConfig.DEFAULT_ENCODING);
            RemotingProtocolBody protocolBody = JSON.parseObject(bodyJson, RemotingSubmitRequest.class);
            protocol.setBody(protocolBody);
        }

        validateSign(protocol, sign);

        return protocol;
    }

    private void validateSign(RemotingProtocol protocol, int sign){
        return;
    }
}

