/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.exception;

import java.io.Serializable;

/**
 * Created by Wenning on 17/1/4.
 */
public class SdtcCoreException extends RuntimeException {

    private static final long serialVersionUID = -749091381642688310L;

    public SdtcCoreException(String message){
        super(message);
    }

    public SdtcCoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
