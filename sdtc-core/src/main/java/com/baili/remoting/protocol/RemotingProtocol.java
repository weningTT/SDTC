/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.remoting.protocol;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * <pre>
 * |<-                                                                                                          ->|
 * +----------------+-----------------+---------------------+---------------+-----------------------+-------------+
 * | length (4 bytes) |sign (4 bytes) |header size(4 bytes) | header data   | data size (4 bytes)   |data         |
 * +----------------+-----------------+---------------------+---------------+-----------------------+-------------+
 * |<-                                                                                                          ->|
 * </pre>
 *
 * Created by ningweiyu on 17/1/6.
 */
public class RemotingProtocol {

    private int version = 0;

    // the unique index of the request and corresponding response
    private long opaque;

    private transient RemotingProtocolBody body;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getOpaque() {
        return opaque;
    }

    public void setOpaque(long opaque) {
        this.opaque = opaque;
    }

    public RemotingProtocolBody getBody() {
        return body;
    }

    public void setBody(RemotingProtocolBody body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
