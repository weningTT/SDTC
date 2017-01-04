/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.remoting;

import java.net.InetSocketAddress;

import com.baili.Lifecycle;
import com.baili.common.URL;
import com.baili.exception.RemotingException;

/**
 * Created by ningweiyu on 17/1/4.
 */
public interface EndPoint extends Lifecycle {

    URL getURL();

    InetSocketAddress getLocalAddress();

    InetSocketAddress getRemoteAddress();

    ChannleHandler getChannelHandler();

    void send(Object message) throws RemotingException;

    // void send(Object message, long timeout) throws RemotingException;
}