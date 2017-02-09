/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.remoting.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.baili.remoting.RemotingConfig;
import com.baili.remoting.protocol.RemotingProtocol;
import com.baili.util.AddressUtil;

/**
 * Created by ningweiyu on 17/1/4.
 */
public class NettyServerTest {

    public static void main(String[] args) {
        /*Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                NettyRemotingServer server = new NettyRemotingServer();
                server.start();
            }
        });*/

        //serverThread.start();

        NettyRemotingServer server = new NettyRemotingServer();
        server.start();

        final NettyRemotingClient client = new NettyRemotingClient();
        client.start();

        final SocketAddress socketAddress = new InetSocketAddress(AddressUtil.getLocalInetAddress(),
                RemotingConfig.SERVER_PORT);
        final RemotingProtocol protocol = new RemotingProtocol();
        protocol.setOpaque(123L);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<1 ; i++){
                    client.async(socketAddress, protocol, null);
                }

            }
        }).start();

    }
}
