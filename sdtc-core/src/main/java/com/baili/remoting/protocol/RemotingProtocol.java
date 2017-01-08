/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.remoting.protocol;

/**
 *
 * <pre>
 * |<-                                                                                        ->|
 * +----------------+-----------------+---------------------+---------------+-----------------------+-------------+
 * | length (4 bytes) |sign (4 bytes) |header size(4 bytes) | header data   | data size (4 bytes)   |data         |
 * +----------------+-----------------+---------------------+---------------+-----------------------+-------------+
 * |<-                                                                                        ->|
 * </pre>
 *
 * Created by ningweiyu on 17/1/6.
 */
public class RemotingProtocol {

    private int version = 0;

    private int opaque;

    private transient RemotingProtocolBody body;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public RemotingProtocolBody getBody() {
        return body;
    }

    public void setBody(RemotingProtocolBody body) {
        this.body = body;
    }
}
