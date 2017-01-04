/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.zk;

import org.apache.curator.RetryPolicy;

/**
 * Created by ningweiyu on 17/1/4.
 */
public class ZkClientConfig {

    public String ip;

    public RetryPolicy retryPolicy;
}
