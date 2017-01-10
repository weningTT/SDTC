/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.remoting;

import com.baili.remoting.protocol.RemotingProtocol;

/**
 * Created by ningweiyu on 17/1/10.
 * opaque see {@link com.baili.remoting.protocol.RemotingProtocol}
 */
public class ResponseEntity {

    private RemotingProtocol request;

    private RemotingClientCallback clientCallback;

    public ResponseEntity (RemotingProtocol request, RemotingClientCallback clientCallback) {
        this.request = request;
        this.clientCallback = clientCallback;
    }
}
