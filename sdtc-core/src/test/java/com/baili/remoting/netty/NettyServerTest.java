/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.remoting.netty;

/**
 * Created by ningweiyu on 17/1/4.
 */
public class NettyServerTest {

    public static void main(String[] args) {
        NettyRemotingServer server = new NettyRemotingServer();
        server.start();
    }
}
