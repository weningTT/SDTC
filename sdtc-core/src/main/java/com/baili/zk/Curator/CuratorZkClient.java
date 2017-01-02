/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baili.zk.Curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;

import com.baili.zk.AbstractZkClient;

/**
 * Created by ningweiyu on 17/1/2.
 */
public class CuratorZkClient extends AbstractZkClient<CuratorWatcher> {

    private CuratorFramework client;

    public CuratorZkClient(String url){
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(url)
                .retryPolicy(new RetryOneTime(1000));
        client = builder.build();
        client.start();
    }

    public void create(String path, boolean isEphemeral) {
        CreateMode mode = isEphemeral ? CreateMode.EPHEMERAL : CreateMode.PERSISTENT;
        try {
            String res = client.create().withMode(mode).forPath(path);
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        client.close();
    }

    public static void main(String[] args) {
        String url = "123.56.42.10";
        CuratorZkClient client = new CuratorZkClient(url);
        client.create("/curator", false);
        client.stop();
    }
}
