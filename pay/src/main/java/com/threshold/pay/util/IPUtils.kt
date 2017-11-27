package com.threshold.pay.util

import java.net.NetworkInterface
import java.util.*

/**
 * Created by Threshold on 2017/9/30.
 */
object IPUtils {

    fun getIPV4Address(): String {
        return getIPAddress(true)
    }

    fun getIPV6Address(): String {
        return getIPAddress(false)
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    private fun getIPAddress(useIPv4: Boolean): String {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                val addresses = Collections.list(networkInterface.inetAddresses)
                for (inetAddress in addresses) {
                    if (!inetAddress.isLoopbackAddress) {
                        val hostAddress = inetAddress.hostAddress
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(hostAddress);
                        val isIPv4 = hostAddress.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4)
                                return hostAddress
                        } else {
                            if (!isIPv4) {
                                val delim = hostAddress.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) hostAddress.toUpperCase() else hostAddress.substring(0, delim).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        // for now eat exceptions
        return "0.0.0.0"
    }
}