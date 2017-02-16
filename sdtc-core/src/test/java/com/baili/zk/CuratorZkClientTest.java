/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.zk;

import org.apache.curator.retry.RetryOneTime;

import com.baili.zk.Curator.CuratorZkClient;

/**
 * Created by Wenning on 17/1/4.
 */
public class CuratorZkClientTest {

    // for test
    public static void main(String[] args) {

        ZkClientConfig config = new ZkClientConfig();
        config.ip = "123.56.42.10";
        config.retryPolicy = new RetryOneTime(1000);

        CuratorZkClient client = new CuratorZkClient(config);

        client.createPersistent("/curator");
        client.stop();
    }
}
