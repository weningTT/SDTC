/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.remoting;

/**
 * Created by ningweiyu on 17/1/5.
 */
public class RemotingConfig {

    // SERVER
    public static final int SERVER_BOSS_SELECTOR_GROUP_THREAD = 1;

    public static final int SERVER_WORK_SELECTOR_GROUP_THREAD = 2;

    public static final int SERVER_WORKER_THREAD = 2;

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final int SERVER_PORT = 9091;
    public static final int Client_PORT = 9191;

    // CLIENT
    public static final int CLIENT_WORK_SELECTOR_GROUP_THREAD = 2;

    public static final int CLIENT_WORKER_THREAD = 2;

    // public static final int IDLE_STATE_TIME_SECONDS = 1;
}
