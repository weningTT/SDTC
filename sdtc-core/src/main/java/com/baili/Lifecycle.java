/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili;

/**
 * Created by ningweiyu on 17/1/2.
 */
public interface Lifecycle {

    /**
     * Start this component.
     */
    void start();

    /**
     * Stop this component.
     */
    void stop();

    /**
     * Check whether this component is currently running
     * @return
     */
    boolean isRunning();
}
