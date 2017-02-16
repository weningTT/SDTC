/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.remoting;

import io.netty.channel.ChannelFuture;

/**
 * Created by Wenning on 17/1/5.
 */
public interface Channel {

    /**
     * Return {@code true} if the {@link Channel} is active and so connected.
     */
    boolean isActive();

    boolean isDone();

    /**
     * Shortcut for call {@link #write(Object)} and {@link #flush()}.
     */
    ChannelFuture writeAndFlush(Object msg);
}
