/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.remoting;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Created by Wenning on 17/1/5.
 */
public interface RemotingServer extends EndPoint {

    /**
     * is bound.
     *
     * @return bound
     */
    // boolean isBound();

    /**
     * get channels.
     *
     * @return channels
     */
    Collection<Channel> getChannels();

    /**
     * get channel.
     *
     * @param remoteAddress
     * @return channel
     */
    Channel getChannel(InetSocketAddress remoteAddress);
}
