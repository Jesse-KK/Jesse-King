package com.changhong.settings.utils;

public class ChEthernetManager {
    public static final String ETHERNET_STATE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGE";
    public static final String IPV6_STATE_CHANGED_ACTION = "android.net.ethernet.IPV6_STATE_CHANGE";
    public static final String IPV4_STATE_CHANGED_ACTION = "android.net.ethernet.IPV4_STATE_CHANGE";
    public static final String EXTRA_ETHERNET_FAILED_REASON = "ethernet_failed_reason";
    public static final String EXTRA_ETHERNET_STATE = "ethernet_state";
    public static final int EVENT_PHY_LINK_UP = 18;
    public static final int EVENT_PHY_LINK_DOWN = 19;
    public static final int EVENT_CONNECT_SUCCESSED = 100;
    public static final int EVENT_CONNECT_FAILED = 101;
    public static final int EVENT_DISCONNECT_SUCCESSED = 102;
    public static final int EVENT_DISCONNECT_FAILED = 103;
    public static final int FAILED_REASON_DISCOVER_TIMEOUT = 1;
    public static final int FAILED_REASON_REQUEST_TIMEOUT = 2;
    public static final int FAILED_REASON_IPOE_AUTH_FAILED = 3;
    public static final int FAILED_REASON_PPPOE_AUTH_FAILED = 4;
    public static final int FAILED_REASON_PPPOE_TIMEDOUT = 5;
    public static final int FAILED_REASON_INVALID_PARAMETER = 6;
    public static final int EVENT_STATIC_CONNECT_FAILED = 15;
    public static final int EVENT_STATIC_CONNECT_SUCCESSED = 14;

}
