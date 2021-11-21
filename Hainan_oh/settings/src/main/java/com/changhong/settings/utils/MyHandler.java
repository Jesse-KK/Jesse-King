package com.changhong.settings.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.changhong.settings.R;

/**
 * Created by Eileen
 * 2019/8/27
 * email: lingling.qiu@changhong.com
 */
public class MyHandler extends Handler {

    private Context mContext = null;
    private String mInfo = null;
    public static final int DHCP_CONNECT_OK = 1;
    public static final int IPOE_CONNECT_OK = 2;
    public static final int PPPOE_CONNECT_OK = 3;
    public static final int MANUAL_CONNECT_OK = 4;
    public static final int DHCP_CONNECT_FAILED = 5;
    public static final int IPOE_CONNECT_FAILED = 6;
    public static final int PPPOE_CONNECT_FAILED = 7;
    public static final int MANUAL_CONNECT_FAILED = 8;
    public static final int ETH_CONNECTING = 9;
    public static final int ETH_CONNECTED = 10;
    public static final int IPOE_WARN = 11;
    public static final int STATIC_IP_NULL = 12;
    public static final int STATIC_NETMASK_NULL = 13;
    public static final int STATIC_GATEWAY_NULL = 14;
    public static final int STATIC_DNS1_NULL = 15;
    public static final int STATIC_IP_ERROR = 16;
    public static final int STATIC_NETMASK_ERROR = 17;
    public static final int STATIC_GATEWAY_ERROR = 18;
    public static final int STATIC_DNS1_ERROR = 19;
    public static final int STATIC_DNS2_ERROR = 20;
    public static final int LINK_DOWN = 21;
    public static final int WIFI_AP_IS_OPEN = 22;
    public static final int WIFI_AP_IS_CLOSE = 23;
    public static final int WIFI_AP_SSID_NULL = 24;
    public static final int WIFI_AP_PWD_NULL = 25;
    public static final int WIFI_AP_SETTING = 26;
    public static final int WIFI_AP_SET_SUCCESS = 27;
    public static final int IPV6_CONNECTED = 28;
    public static final int IPV6_CONNECTING = 29;
    public static final int IPV6_CONNECT_SUCCESS = 30;
    public static final int IPV6_CONNECT_FAILED = 31;
    public static final int IPV6_DISABLE = 32;
    public static final int IPV6_ENABLED = 33;

    public MyHandler(Context context) {
        mContext = context;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case DHCP_CONNECT_OK:
                mInfo = mContext.getResources().getString(R.string.eth_dhcp_connect_ok);
                break;
            case IPOE_CONNECT_OK:
                mInfo = mContext.getResources().getString(R.string.eth_ipoe_connect_ok);
                break;
            case PPPOE_CONNECT_OK:
                mInfo = mContext.getResources().getString(R.string.eth_pppoe_connect_ok);
                break;
            case MANUAL_CONNECT_OK:
                mInfo = mContext.getResources().getString(R.string.eth_manual_connect_ok);
                break;
            case DHCP_CONNECT_FAILED:
                mInfo = mContext.getResources().getString(R.string.eth_dhcp_connect_failed);
                break;
            case IPOE_CONNECT_FAILED:
                mInfo = mContext.getResources().getString(R.string.eth_ipoe_connect_failed);
                break;
            case PPPOE_CONNECT_FAILED:
                mInfo = mContext.getResources().getString(R.string.eth_pppoe_connect_failed);
                break;
            case MANUAL_CONNECT_FAILED:
                mInfo = mContext.getResources().getString(R.string.eth_manual_connect_failed);
                break;
            case ETH_CONNECTING:
                mInfo = mContext.getResources().getString(R.string.eth_connecting);
                break;
            case ETH_CONNECTED:
                mInfo = mContext.getResources().getString(R.string.eth_connected);
                break;
            case IPOE_WARN:
                mInfo = mContext.getResources().getString(R.string.eth_ipoe_warn);
                break;
            case STATIC_IP_NULL:
                mInfo = mContext.getResources().getString(R.string.eth_manual_ip_null);
                break;
            case STATIC_NETMASK_NULL:
                mInfo = mContext.getResources().getString(R.string.eth_manual_mask_null);
                break;
            case STATIC_GATEWAY_NULL:
                mInfo = mContext.getResources().getString(R.string.eth_manual_gateway_null);
                break;
            case STATIC_DNS1_NULL:
                mInfo = mContext.getResources().getString(R.string.eth_manual_dns1_null);
                break;
            case STATIC_IP_ERROR:
                mInfo = mContext.getResources().getString(R.string.eth_manual_ip_error);
                break;
            case STATIC_NETMASK_ERROR:
                mInfo = mContext.getResources().getString(R.string.eth_manual_mask_error);
                break;
            case STATIC_GATEWAY_ERROR:
                mInfo = mContext.getResources().getString(R.string.eth_manual_gateway_error);
                break;
            case STATIC_DNS1_ERROR:
                mInfo = mContext.getResources().getString(R.string.eth_manual_dns1_error);
                break;
            case STATIC_DNS2_ERROR:
                mInfo = mContext.getResources().getString(R.string.eth_manual_dns2_error);
                break;
            case LINK_DOWN:
                mInfo = mContext.getResources().getString(R.string.eth_no_cable);
                break;
            case WIFI_AP_IS_OPEN:
                mInfo = mContext.getResources().getString(R.string.eth_ap_opening);
                break;
            case WIFI_AP_IS_CLOSE:
                mInfo = mContext.getResources().getString(R.string.eth_ap_closing);
                break;
            case WIFI_AP_SSID_NULL:
                mInfo = mContext.getResources().getString(R.string.eth_ap_ssid_null);
                break;
            case WIFI_AP_PWD_NULL:
                mInfo = mContext.getResources().getString(R.string.eth_ap_pwd_null);
                break;
            case WIFI_AP_SETTING:
                mInfo = mContext.getResources().getString(R.string.eth_ap_is_setting);
                break;
            case WIFI_AP_SET_SUCCESS:
                mInfo = mContext.getResources().getString(R.string.eth_ap_set_success);
                break;
            case IPV6_CONNECTED:
                mInfo = mContext.getResources().getString(R.string.eth_ipv6_connected);
                break;
            case IPV6_CONNECTING:
                mInfo = mContext.getResources().getString(R.string.eth_ipv6_connecting);
                break;
            case IPV6_CONNECT_SUCCESS:
                mInfo = mContext.getResources().getString(R.string.eth_ipv6_connect_success);
                break;
            case IPV6_CONNECT_FAILED:
                mInfo = mContext.getResources().getString(R.string.eth_ipv6_connect_failed);
                break;
            case IPV6_DISABLE:
                mInfo = mContext.getResources().getString(R.string.eth_ipv6_disable);
                break;
            case IPV6_ENABLED:
                mInfo = mContext.getResources().getString(R.string.eth_ipv6_enabled);
                break;

        }

        Toast.makeText(mContext, mInfo, Toast.LENGTH_SHORT).show();
    }

    private void clear() {
        this.removeCallbacksAndMessages(null);
    }

    public void setInfo(String toastInfo) {
        mInfo = toastInfo;
    }


}
