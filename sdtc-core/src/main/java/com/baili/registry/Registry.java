/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.registry;

import com.baili.node.Node;

/**
 * The registry service used for meta information management of node (client, master, worker).
 * Typically implemented by zookeeper or redis.
 *
 * Created by Wenning on 17/1/2.
 */
public interface Registry {

    /**
     * Register the node.
     */
    void register(Node node);

    /**
     * Unregister the node.
     */
    void unregister(Node node);

    /**
     * Watch the node
     * @param node
     * @param listener
     */
    void subscribe(Node node, NotifyListener listener);

    /**
     * Give up watching the node
     * @param node
     * @param listener
     */
    void unsubscribe(Node node, NotifyListener listener);

}
