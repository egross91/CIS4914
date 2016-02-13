package com.meetup.networking.utils;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class NetworkInfoUtil {
    public static String getIPAAddress(boolean useIPv4) throws RuntimeException {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface intrface : interfaces) {
                List<InetAddress> addresses = Collections.list(intrface.getInetAddresses());

                for (InetAddress address : addresses) {
                    if (!address.isLoopbackAddress()) {
                        String addressStr = address.getHostAddress();
                        boolean isIPv4 = (addressStr.indexOf(':') < 0);

                        if (useIPv4) {
                            if (isIPv4) {
                                return addressStr;
                            }
                        } else {
                            if (!isIPv4) {
                                int delimiter = addressStr.indexOf('%');
                                return (delimiter < 0) ? addressStr.toUpperCase() :
                                                         addressStr.substring(0, delimiter).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Nothing was found that we wanted.
        return null;
    }
}
