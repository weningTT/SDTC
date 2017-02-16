/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.remoting.protocol;

import java.io.Serializable;

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
 */
public class RemotingProtocol implements Serializable{

    private static final long serialVersionUID = -7275800318233609129L;

    private int version = 0;

    // the unique index of the request and corresponding response
    private long opaque;

    private String method;

    private transient RemotingProtocolBody body;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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
