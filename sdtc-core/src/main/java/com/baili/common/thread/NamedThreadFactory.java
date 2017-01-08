/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ningweiyu on 17/1/5.
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_NUM = new AtomicInteger(1);

    private final ThreadGroup threadGroup;

    private final AtomicInteger threadNum = new AtomicInteger(1);

    private final String namePrefix;

    public NamedThreadFactory() {
        this("POOL-" + POOL_NUM.incrementAndGet());
    }

    public NamedThreadFactory(String namePrefix) {
        this.threadGroup = Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {

        return new Thread(
                threadGroup,
                r,
                namePrefix + threadNum.incrementAndGet(),
                0);
    }
}
