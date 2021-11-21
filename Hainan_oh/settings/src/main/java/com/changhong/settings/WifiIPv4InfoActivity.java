package com.changhong.settings;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

public class WifiIPv4InfoActivity extends Activity {
    private static final String TAG ="WifiInfoActivity" ;
    private WifiManager mWifiManager;
    private Context mContext= WifiIPv4InfoActivity.this;
    private TextView ip_address,mask_address,gateway_address,dns_address;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ipv4_wifiinfo);
        ip_address=(TextView)findViewById(R.id.ip_address);
        mask_address=(TextView)findViewById(R.id.mask_address);
        gateway_address=(TextView)findViewById(R.id.gateway_address);
        dns_address=(TextView)findViewById(R.id.dns_address);
        mWifiManager= (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        getWifiInfo();
    }

    private void getWifiInfo() {
        Log.i(TAG, "getWifiInfo: Been excuted!!!");
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        String SSID = "";
        int ip1 = -1;
        if (mWifiInfo != null) {
            SSID = mWifiInfo.getSSID();
            ip1 = mWifiInfo.getIpAddress();
        }

        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        String IP = "", Mask = "", Gateway = "", Dns1 = "", Dns2 = "";
        IP = intToIp(dhcpInfo.ipAddress);
        Gateway = intToIp(dhcpInfo.gateway);
        Mask = intToIp(dhcpInfo.netmask);
        Dns1 = intToIp(dhcpInfo.dns1);
        Log.i(TAG, IP + " " + Gateway + " " + Mask + " " + Dns1 + " " + " ##$#$#$#$#$$$#$##$#$#$##$" + ",SSID=" + SSID + ",ip1=" + ip1);
        if (!IP.equals("0.0.0.0")) {
            ip_address.setText(IP);
        }
        if (!Mask.equals("0.0.0.0")) {
            mask_address.setText(Mask);
        }
        if (!Gateway.equals("0.0.0.0")) {
            gateway_address.setText(Gateway);
        }
        if (!Dns1.equals("0.0.0.0")) {
            dns_address.setText(Dns1);
        }
    }

    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

}
