/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.node;

import java.io.Serializable;

/**
 * Created by ningweiyu on 17/1/2.
 */
public class Node implements Serializable {


    /**
     * whether this node is working or not.
     */
    protected volatile boolean isRunning;

}
