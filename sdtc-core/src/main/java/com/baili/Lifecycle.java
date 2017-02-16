/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili;

/**
 * Created by Wenning on 17/1/2.
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
