/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.zk;

/**
 * Created by ningweiyu on 17/1/2.
 */
public interface ZkClient {

    /**
     * create a zk path
     * @param path
     * @param isEphemeral
     */
    void create(String path, boolean isEphemeral);
}
