/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.zk;

import com.baili.Lifecycle;

/**
 * Created by ningweiyu on 17/1/2.
 */
public interface ZkClient extends Lifecycle {

    /**
     * create a zk ephemeral path
     * @param path
     */
    String createEphemeral(String path);

    /**
     * create a zk ephemeral path and data
     * @param path
     * @param data
     */
    String createEphemeral(String path, byte[] data);

    /**
     * create a zk persistent path
     * @param path
     */
    String createPersistent(String path);

    /**
     * create a zk persistent path and data
     * @param path
     */
    String createPersistent(String path, byte[] data);
}
