/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.zk.Curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;

import com.baili.exception.SdtcCoreException;
import com.baili.zk.AbstractZkClient;
import com.baili.zk.ZkClientConfig;

/**
 * Created by Wenning on 17/1/2.
 */
public class CuratorZkClient extends AbstractZkClient<CuratorWatcher> {

    private CuratorFramework client;

    public CuratorZkClient(ZkClientConfig config) {

        // build
        CuratorFrameworkFactory.Builder builder =
                CuratorFrameworkFactory.builder()
                .connectString(config.ip)
                .retryPolicy(config.retryPolicy);

        client = builder.build();
    }

    public String createEphemeral(String path) {

        try {
            return client.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path);
        } catch (Exception e) {
            throw new SdtcCoreException("createEphemeral fail");
        }
    }

    public String createEphemeral(String path, byte[] data) {

        try {
            return client.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path, data);
        } catch (Exception e) {
            throw new SdtcCoreException("createEphemeral fail");
        }
    }

    public String createPersistent(String path) {

        try {
            return client.create()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path);
        } catch (Exception e) {
            throw new SdtcCoreException("createPersistent fail");
        }
    }

    public String createPersistent(String path, byte[] data) {

        try {
            return client.create()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path, data);
        } catch (Exception e) {

            throw new SdtcCoreException("createPersistent fail");
        }
    }

    public void start() {

        synchronized(isRunning){

            if (!isRunning) {

                client.start();
                isRunning = true;
            }
        }
    }

    public void stop() {

        synchronized(isRunning) {

            if (isRunning) {

                client.close();
                isRunning = false;
            }
        }
    }

    public boolean isRunning(){

        return isRunning;
    }

}
