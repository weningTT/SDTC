/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.zk;

import com.baili.Lifecycle;

/**
 * Created by ningweiyu on 17/1/2.
 */
public abstract class AbstractZkClient<TargetChildListener> implements ZkClient {
    protected volatile Boolean isRunning;
}
