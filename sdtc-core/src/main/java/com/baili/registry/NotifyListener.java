/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.registry;

import java.util.List;

import com.baili.node.Node;

/**
 * Created by ningweiyu on 17/1/2.
 */
public interface NotifyListener {

    /**
     * notify the listeners
     * @param nodes
     */
    void notify(List<Node> nodes);
}
