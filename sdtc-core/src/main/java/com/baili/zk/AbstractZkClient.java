/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.zk;

/**
 * Created by Wenning on 17/1/2.
 */
public abstract class AbstractZkClient<TargetChildListener> implements ZkClient {
    protected volatile Boolean isRunning;
}
