/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.zk;

import org.apache.curator.RetryPolicy;

/**
 * Created by Wenning on 17/1/4.
 */
public class ZkClientConfig {

    public String ip;

    public RetryPolicy retryPolicy;
}
