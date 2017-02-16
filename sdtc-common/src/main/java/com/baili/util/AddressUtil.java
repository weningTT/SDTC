/*
 * Copyright (C) 2017 Baili, Inc. All Rights Reserved.
 */
package com.baili.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Wenning on 17/1/8.
 */
public class AddressUtil {

    private static InetAddress localInetAddress;

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressUtil.class);

    public static InetAddress getLocalInetAddress() {

        if (localInetAddress != null) {

            return localInetAddress;
        } else {

            Enumeration<NetworkInterface> netInterfaces;
            try {
                netInterfaces = NetworkInterface.getNetworkInterfaces();
                while (netInterfaces.hasMoreElements()) {
                    NetworkInterface netInterface = netInterfaces.nextElement();
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {

                            localInetAddress = address;
                            LOGGER.info(address.getHostAddress());
                            return address;
                        }
                    }
                }
            } catch (SocketException e) {
                LOGGER.error("getLocalInetAddress fail.", e);
            }

            return null;
        }
    }
}
