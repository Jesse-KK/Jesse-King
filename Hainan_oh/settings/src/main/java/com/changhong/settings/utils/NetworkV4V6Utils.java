package com.changhong.settings.utils;

import android.net.IpConfiguration;
import android.net.IpInfo;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

public class NetworkV4V6Utils {

    private static final String TAG = "ty_NetworkActivity";

    public enum EthMode {
        DHCP, IPOE, STATIC, PPPOE, UNKNOWN
    }

    public enum EthStack {
        IPV4, IPV6, IPV4V6, UNKNOWN
    }

    public enum CheckIpResult {
        IP_ERR, MASK_ERR, PREFIX_ERR, GATEWAY_ERR, DNS1_ERR, DNS2_ERR, CHECK_OK
    }

    enum Ipv6Option {
        AUTO, STATEFUL, STATELESS
    }

    static boolean isEthIpv4Connected(EthernetManager manager) {
        IpInfo info = manager.getIPv4Info();
        if (null != info && info.ip instanceof Inet4Address) {
            Log.i(TAG, "ipv4 ip: " + info.ip.getHostAddress());
            return true;
        } else {
            return false;
        }
    }

    static boolean isEthIpv6Connected(EthernetManager manager) {
        //return manager.checkDhcpv6Status(manager.getInterfaceName());
        IpInfo info = manager.getIPv6Info();
        if (null != info && info.ip instanceof Inet6Address) {
            Log.i(TAG, "ipv6 ip: " + info.ip.getHostAddress());
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEthConnected(EthernetManager manager) {
        if (!manager.getNetLinkStatus()) {
            Log.i(TAG, "link down, return false");
            return false;
        }
        return (isEthIpv4Connected(manager) || isEthIpv6Connected(manager));
    }

    public static EthMode getEthMode(EthernetManager manager) {
        IpConfiguration ipConfigV4 = manager.getIpv4Configuration();
        IpConfiguration ipConfigV6 = manager.getIpv6Configuration();
        if (IpConfiguration.IpAssignment.UNASSIGNED != ipConfigV4.ipAssignment) {
            switch (ipConfigV4.ipAssignment) {
                case PPPOE:
                    return EthMode.PPPOE;
                case DHCP:
                    return EthMode.DHCP;
                case STATIC:
                    return EthMode.STATIC;
                case IPOE:
                    return EthMode.IPOE;
                default:
                    return EthMode.UNKNOWN;
            }
        } else if (IpConfiguration.IpAssignment.UNASSIGNED != ipConfigV6.ipAssignment) {
            switch (ipConfigV6.ipAssignment) {
                case PPPOE:
                    return EthMode.PPPOE;
                case DHCP:
                    return EthMode.DHCP;
                case STATIC:
                    return EthMode.STATIC;
                case IPOE:
                    return EthMode.IPOE;
                default:
                    return EthMode.UNKNOWN;
            }
        }
        return EthMode.UNKNOWN;
    }

    NetInfo getEthIpv4Info(EthernetManager manager) {
        return new NetInfo(manager).getEthIpv4Info();
    }

    NetInfo getEthIpv6Info(EthernetManager manager) {
        return new NetInfo(manager).getEthIpv6Info();
    }

    public static NetInfo getNetInfo() {
        return new NetInfo();
    }

    NetInfo getWifiIpv4Info(WifiManager manager) {
        return new NetInfo(manager).getWifiIpv4Info();
    }

    NetInfo getWifiIpv6Info(WifiManager manager) {
        return new NetInfo(manager).getWifiIpv6Info();
    }

    public static CheckIpResult validateIpv4ConfigFields(NetInfo info) {

        LinkProperties linkProperties = new LinkProperties();

        // CHECK IP
        String tmpIp = info.ip;
        if (TextUtils.isEmpty(tmpIp)) {
            Log.i(TAG, "ipv4 ip is empty.");
            return CheckIpResult.IP_ERR;
        }

        InetAddress inetAddr = null;
        try {
            inetAddr = NetworkUtils.numericToInetAddress(tmpIp);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "ipv4 ip numericToInetAddress err: " + e);
            return CheckIpResult.IP_ERR;
        }

        if (inetAddr instanceof Inet4Address) {
            Log.d(TAG, "ipv4 address check ok!");
        } else {
            Log.i(TAG, "ipv4 ip is not Inet4Address.");
            return CheckIpResult.IP_ERR;
        }

        // check ipv4 prefix
        int prefixV4 = -1;
        try {
            prefixV4 = NetworkUtils.netmaskIntToPrefixLength(
                    NetworkUtils.inetAddressToInt(NetworkUtils.numericToInetAddress(info.mask)));
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "invalid net mask: ", e);
            return CheckIpResult.MASK_ERR;
        }

        try {
            if (prefixV4 < 0 || prefixV4 > 32) {
                Log.i(TAG, "ipv4 prefix length invalid.");
                return CheckIpResult.MASK_ERR;
            }
            linkProperties.addLinkAddress(new LinkAddress(inetAddr, prefixV4));

        } catch (Exception e) {
            Log.i(TAG, "ipv4 prefix invalid: " + e.toString());
            return CheckIpResult.MASK_ERR;
        }

        // check ipv4 gateway
        String gateway = info.gateway;
        if (TextUtils.isEmpty(gateway)) {
            return CheckIpResult.GATEWAY_ERR;
        } else {
            InetAddress gatewayAddr = null;
            try {
                gatewayAddr = NetworkUtils.numericToInetAddress(gateway);
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "numericToInetAddress error: " + e.toString());
                return CheckIpResult.GATEWAY_ERR;
            }
            if (gatewayAddr instanceof Inet4Address) {
                Log.d(TAG, "ipv4 gateway check ok!");
//                linkProperties.addRoute(new RouteInfo(gatewayAddr));
            } else {
                Log.w(TAG, "gateway v4 is not inet4address");
                return CheckIpResult.GATEWAY_ERR;
            }
        }

        String dns1 = info.dns1;
        InetAddress dnsAddr = null;
        if (TextUtils.isEmpty(dns1)) {
            return CheckIpResult.DNS1_ERR;
        } else {
            try {
                dnsAddr = NetworkUtils.numericToInetAddress(dns1);
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "numericToInetAddress ipv4 dns1 error: " + e);
                return CheckIpResult.DNS1_ERR;
            }
            if (dnsAddr instanceof Inet4Address) {
                Log.d(TAG, "ipv4 dns1 check ok!");
            } else {
                Log.w(TAG, "ipv4 dns1 is not Inet4Address.");
                return CheckIpResult.DNS1_ERR;
            }
//            linkProperties.addDns(dnsAddr);
        }

        String dns2 = info.dns2;
        InetAddress dns2Addr = null;
        if (TextUtils.isEmpty(dns2)) {
            return CheckIpResult.CHECK_OK;
        } else {
            try {
                dns2Addr = NetworkUtils.numericToInetAddress(dns2);
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "ipv4 dns2 numericToInetAddress error: " + e);
                return CheckIpResult.DNS2_ERR;
            }
            if (dns2Addr instanceof Inet4Address) {
                Log.d(TAG, "ipv4 dns2 check ok!");
            } else {
                Log.w(TAG, "ipv4 dns2 is not Inet4Address");
                return CheckIpResult.DNS2_ERR;
            }
//            linkProperties.addDns(dns2Addr);
        }

        return CheckIpResult.CHECK_OK;
    }

    public static CheckIpResult validateIpv6ConfigFields(NetInfo info) {
        LinkProperties linkProperties = new LinkProperties();

        // check ip
        String ip = info.ip;
        if (TextUtils.isEmpty(ip)) {
            Log.w(TAG, "ipv6 ip is empty.");
            return CheckIpResult.IP_ERR;
        }

        InetAddress inetAddr = null;
        try {
            inetAddr = NetworkUtils.numericToInetAddress(ip);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "ipv6 ip numericToInetAddress error: " + e);
            return CheckIpResult.IP_ERR;
        }
        if (inetAddr instanceof Inet6Address) {

            Log.i(TAG, "ipv6 ip check ok!");
        } else {
            Log.w(TAG, "ipv6 ip is not Inet6Address.");
            return CheckIpResult.IP_ERR;
        }

        // check prefix
        int intPrefixV6 = info.prefix;
        try {
            if (intPrefixV6 < 0 || intPrefixV6 > 128) {
                Log.w(TAG, "ipv6 prefix length error.");
                return CheckIpResult.MASK_ERR;
            }
            linkProperties.addLinkAddress(new LinkAddress(inetAddr, intPrefixV6));
            Log.i(TAG, "check ipv6 prefix ok!");
        } catch (Exception e) {
            Log.e(TAG, "ipv6 prefix invalid: " + e);
            return CheckIpResult.MASK_ERR;
        }

        String gateway = info.gateway;
        if (TextUtils.isEmpty(gateway)) {
            return CheckIpResult.GATEWAY_ERR;
        } else {
            InetAddress gatewayAddr = null;
            try {
                gatewayAddr = NetworkUtils.numericToInetAddress(gateway);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "ipv6 gateway numericToInetAddress error: " + e);
                return CheckIpResult.GATEWAY_ERR;
            }
            if (gatewayAddr instanceof Inet6Address) {
                Log.i(TAG, "ipv6 gateway check ok!");

            } else {
                Log.i(TAG, "ipv6 gateway is not InetAddress.");
                return CheckIpResult.GATEWAY_ERR;
            }
//            linkProperties.addRoute(new RouteInfo(gatewayAddr));
        }

        // check dns1
        String dns1 = info.dns1;
        InetAddress dnsAddr = null;
        if (TextUtils.isEmpty(dns1)) {
            //If everything else is valid, provide hint as a default option
            Log.i(TAG, "ipv6 dns1 is empty.");
            return CheckIpResult.DNS1_ERR;
        } else {
            try {
                dnsAddr = NetworkUtils.numericToInetAddress(dns1);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "ipv6 dns1 numericToInetAddress error: " + e);
                return CheckIpResult.DNS1_ERR;
            }
            if (dnsAddr instanceof Inet6Address) {
                Log.i(TAG, "ipv6 dns1 check ok!");
            } else {
                Log.i(TAG, "ipv6 dns1 is not Inet6Address.");
                return CheckIpResult.DNS1_ERR;

            }
//            linkProperties.addDns(dnsAddr);
        }

        String dns2 = info.dns2;
        if (dns2.length() > 0) {

            try {
                dnsAddr = NetworkUtils.numericToInetAddress(dns2);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "ipv6 dns2 numericToInetAddress error: " + e);
                return CheckIpResult.DNS2_ERR;
            }
            if (dnsAddr instanceof Inet6Address) {
                Log.i(TAG, "ipv6 dns2 check ok!");
            } else {
                Log.i(TAG, "ipv6 dns2 is not Inet6Address.");
                return CheckIpResult.DNS2_ERR;
            }
//            linkProperties.addDns(dnsAddr);
            return CheckIpResult.CHECK_OK;
        } else {
            return CheckIpResult.CHECK_OK;
        }
    }

   public static EthStack getEthStack(EthernetManager manager) {
        Log.i(TAG, "-------- getEthStack --------");
        if (manager.isEnableIpv6() && manager.isEnableIpv4()) {
            return EthStack.IPV4V6;
        } else if (manager.isEnableIpv4()) {
            return EthStack.IPV4;
        } else if (manager.isEnableIpv6()) {
            return EthStack.IPV6;
        } else {
            IpConfiguration ipConfigV4 = manager.getIpv4Configuration();
            IpConfiguration ipConfigV6 = manager.getIpv6Configuration();

            if (IpConfiguration.IpAssignment.UNASSIGNED != ipConfigV4.ipAssignment
                    && IpConfiguration.IpAssignment.UNASSIGNED != ipConfigV6.ipAssignment) {
                return EthStack.IPV4V6;
            } else if (IpConfiguration.IpAssignment.UNASSIGNED != ipConfigV4.ipAssignment) {
                return EthStack.IPV4;
            } else if (IpConfiguration.IpAssignment.UNASSIGNED != ipConfigV6.ipAssignment) {
                return EthStack.IPV6;
            }
        }
        return EthStack.UNKNOWN;
    }

    // not test
    public static void disconnectIpv4(EthernetManager manager) {
        IpConfiguration ipConfig = manager.getIpv4Configuration();
        ipConfig.ipAssignment = IpConfiguration.IpAssignment.UNASSIGNED;
        manager.enableIpv4(false);
        manager.setIpv4Configuration(ipConfig);

    }

    // not test
    public static void disconnectIpv6(EthernetManager manager) {
        IpConfiguration ipConfig = manager.getIpv6Configuration();
        ipConfig.ipAssignment = IpConfiguration.IpAssignment.UNASSIGNED;
        manager.enableIpv6(false);
        manager.setIpv6Configuration(ipConfig);
    }

    public static class NetInfo {
        public String ip, mask, gateway, dns1, dns2;
        public int prefix;
        private EthernetManager ethManager;
        private WifiManager wifiManager;
        NetInfo(EthernetManager manager) {
            // init eth ip address
            this.ethManager = manager;
        }

        NetInfo(WifiManager manager) {
            // init wifi ip address
            wifiManager = manager;
        }

        private NetInfo() {

        }

        NetInfo getEthIpv4Info() {

            NetInfo netInfo = new NetInfo();
            IpInfo info = ethManager.getIPv4Info();
            ip = info.ip.getHostAddress();
            mask = NetworkUtils.intToInetAddress(
                    NetworkUtils.prefixLengthToNetmaskInt(info.mask)).getHostAddress();
            gateway = info.gateway.getHostAddress();
            dns1 = info.dnsServers.get(0).getHostAddress();
            if (info.dnsServers.size() > 1) {
                dns2 = info.dnsServers.get(1).getHostAddress();
            } else {
                dns2 = "";
            }

            return netInfo;
        }

        NetInfo getEthIpv6Info() {
            NetInfo netInfo = new NetInfo();
            IpInfo info = ethManager.getIPv6Info();
            ip = info.ip.getHostAddress();
            prefix = info.mask;
            gateway = info.gateway.getHostAddress();
            dns1 = info.dnsServers.get(0).getHostAddress();
            if (info.dnsServers.size() > 1) {
                dns2 = info.dnsServers.get(1).getHostAddress();
            } else {
                dns2 = "";
            }
            return netInfo;
        }

        NetInfo getWifiIpv4Info() {
            NetInfo netInfo = new NetInfo();
            return netInfo;
        }

        NetInfo getWifiIpv6Info() {
            NetInfo netInfo = new NetInfo();
            return netInfo;
        }
    }

    // 2020/4/14 Eileen ADD BEGIN:
    /*boolean isEthIpv4Connected(EthernetManager manager) {
        IpInfo ipv4Info = manager.getIPv4Info();
        if (null == ipv4Info || null == ipv4Info.ip) {
            Log.i(TAG, "ipv4 info is null.");
            return false;
        }

        InetAddress ip = ipv4Info.ip;
        if (ip instanceof Inet4Address) {
            Log.i(TAG, "ipv4 ip: " + ip.getHostAddress());
            return true;
        } else {
            return false;
        }
    }

    boolean isEthIpv6Connected(EthernetManager manager) {
        IpInfo ipv6Info = manager.getIPv6Info();
        if (null == ipv6Info || null == ipv6Info.ip) {
            Log.i(TAG, "ipv6 info is null.");
            return false;
        }

        InetAddress ip = ipv6Info.ip;
        if (ip instanceof Inet6Address) {
            Log.i(TAG, "ipv6 ip: " + ip.getHostAddress());
            return true;
        } else {
            return false;
        }
    }*/
    // 2020/4/14 Eileen ADD END

    public static boolean isV4IpOk(EthernetManager mEthManager){
        Log.i(TAG, "isV4IpOk: ");
        IpInfo ipv4Info = mEthManager.getIPv4Info();

        String IP_v4 = "";
        if (ipv4Info != null && ipv4Info.ip != null) {
            IP_v4 = ipv4Info.ip.getHostAddress();
        }
        Log.i(TAG, "isV4IpOk: IP_v4=" + IP_v4 );
        if ((!TextUtils.isEmpty(IP_v4) && !IP_v4.equals("0.0.0.0"))) {
            return true;
        }
        return false;
    }
    public static boolean isV6IpOk(EthernetManager mEthManager){
        Log.i(TAG, "isV6IpOk: ");
        IpInfo ipv6Info = mEthManager.getIPv6Info();

        String IP_v6 = "";
        if (ipv6Info != null && ipv6Info.ip != null) {
            IP_v6 = ipv6Info.ip.getHostAddress();
        }
        Log.i(TAG, "isV6IpOk:IP_v6=" + IP_v6);
        if ((!TextUtils.isEmpty(IP_v6) && !IP_v6.equals("::1"))) {
            return true;
        }
        return false;
    }
}
