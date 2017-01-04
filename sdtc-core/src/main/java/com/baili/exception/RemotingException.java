/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.exception;

/**
 * Created by ningweiyu on 17/1/4.
 */
public class RemotingException extends RuntimeException {

    private static final long serialVersionUID = -7035943862901380073L;

    public RemotingException(String message){
        super(message);
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }
}
