/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.remoting;

import java.net.SocketAddress;

import com.baili.remoting.protocol.RemotingProtocol;

/**
 * Created by Wenning on 17/1/5.
 */
public interface RemotingClient extends EndPoint {

    public void async(SocketAddress socketAddress, RemotingProtocol protocol, RemotingClientCallback callback);

    // reconnect

}
