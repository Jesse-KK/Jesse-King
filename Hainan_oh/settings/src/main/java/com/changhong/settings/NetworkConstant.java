package com.changhong.settings;

public class NetworkConstant {
    // ethernet common message constant
    public static final int MSG_CONNECTED = 1;
    public static final int MSG_CONNECTING = 2;
    public static final int MSG_CONNECT_FAILED = 3;
    public static final int MSG_CONNECT_SUCCESS = 4;
    public static final int MSG_DISCONNECT_SUCCESS = 5;
    public static final int MSG_DISCONNECT_FAILED = 6;
    public static final int MSG_LINK_DOWN = 7;
    public static final int MSG_LINK_UP = 8;
    public static final int MSG_RECONNECTING = 9;
    public static final int MSG_ACCOUNT_NULL = 10;
    public static final int MSG_IP_NULL = 11;
    public static final int MSG_INVALID_IP = 12;
    public static final int MSG_INVALID_PREFIX = 13;
    public static final int MSG_INVALID_GATEWAY = 14;
    public static final int MSG_INVALID_DNS1 = 15;
    public static final int MSG_INVALID_DNS2 = 16;

    public static final int MSG_CONNECTED_V4 = 100;
    public static final int MSG_CONNECTING_V4 = 101;
    public static final int MSG_CONNECT_FAILED_V4 = 102;
    public static final int MSG_CONNECT_SUCCESS_V4 = 103;
    public static final int MSG_DISCONNECT_SUCCESS_V4 = 104;
    public static final int MSG_DISCONNECT_FAILED_V4 = 105;
    public static final int MSG_LINK_DOWN_V4 = 106;
    public static final int MSG_LINK_UP_V4 = 107;
    public static final int MSG_RECONNECTING_V4 = 108;
    public static final int MSG_IP_NULL_V4 = 109;
    public static final int MSG_INVALID_IP_V4 = 110;
    public static final int MSG_INVALID_PREFIX_V4 = 111;
    public static final int MSG_INVALID_GATEWAY_V4 = 112;
    public static final int MSG_INVALID_DNS1_V4 = 113;
    public static final int MSG_INVALID_DNS2_V4 = 114;

    public static final int MSG_CONNECTED_V6 = 1000;
    public static final int MSG_CONNECTING_V6 = 1001;
    public static final int MSG_CONNECT_FAILED_V6 = 1002;
    public static final int MSG_CONNECT_SUCCESS_V6 = 1003;
    public static final int MSG_DISCONNECT_SUCCESS_V6 = 1004;
    public static final int MSG_DISCONNECT_FAILED_V6 = 1005;
    public static final int MSG_LINK_DOWN_V6 = 1006;
    public static final int MSG_LINK_UP_V6 = 1007;
    public static final int MSG_RECONNECTING_V6 = 1008;
    public static final int MSG_IP_NULL_V6 = 1009;
    public static final int MSG_INVALID_IP_V6 = 1010;
    public static final int MSG_INVALID_PREFIX_V6 = 1011;
    public static final int MSG_INVALID_GATEWAY_V6 = 1012;
    public static final int MSG_INVALID_DNS1_V6 = 1013;
    public static final int MSG_INVALID_DNS2_V6 = 1014;

    //
    public static final String STACK_V4 = "stack_v4";
    public static final String STACK_V6 = "stack_v6";
    public static final String STACK_V4V6 = "stack_doublbe";

    //
    public static final String STATIC_FRAGMENT_CHANGE_ACTION = "com.changhong.settings.STATIC_FRAGMENT_CHANGE_ACTION";
    public static final String EXTRA_STATIC_STATE = "extra_static_state";
    public static final String STATE_STATIC_V4_START = "v4_start";
    public static final String STATE_STATIC_V6_START = "v6_start";
    public static final String STATE_STATIC_V4V6_START = "v4v6_start";
    public static final String STATE_STATIC_V4_PAUSE = "v4_pause";
    public static final String STATE_STATIC_V6_PAUSE = "v6_pause";
    public static final String STATE_STATIC_V4V6_PAUSE = "v4v6_pause";

    //
    public static final String ANDROID_SETTINGS_PACKAGE = "com.google.android.settings";
    public static final String IS_PPPOE_IPV6_STACK = "is_pppoe_ipv6_stack";
    public static final String ETHERNET_SETTINGS_PREFERENCE_NAME = "EthernetSettings";
    public static final String PPPOE_INTERFACE = "ppp0";

    //
    public static final String PERSIST_ETH_VLAN_ENABLED = "persist.ethernet.vlan.enabled";

    //
    public static final String DEFAULT_DNS_V4 = "0.0.0.0";

    public static final String OPTION_AUTO = "auto";
    public static final String OPTION_STATEFULL = "statefull";
    public static final String OPTION_STATELESS = "stateless";

    //
    public static final String INIT_ETH_ICON_ACTION = "com.changhong.settings.SHOW_ETH_ICON_ACTION";

}
