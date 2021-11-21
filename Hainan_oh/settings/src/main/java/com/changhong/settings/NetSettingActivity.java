package com.changhong.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
//import android.net.pppoe.PppoeManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.tech.NfcB;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changhong.settings.utils.NetworkV4V6Utils;
//import com.changhong.settings.utils.WifiAPUtil;
import com.changhong.settings.utils.WifiAPUtil;
import com.changhong.settings.wifi.WiFiActivity;

import java.lang.reflect.Method;
import java.util.zip.Inflater;

/**
 * Created by Eileen
 * 2019/12/18
 * email: lingling.qiu@changhong.com
 * <p>
 * 2020/3/31 需求文档1.3: 合并 ipv4 & ipv6，将网络信息移入网络配置模块
 */
public class NetSettingActivity extends Activity implements View.OnClickListener, RadioButton.OnCheckedChangeListener, View.OnKeyListener {
    private static final String TAG = "NetSettingActivity";
    private View wifi, protocol, ipv4, ipv6, hotspot, wifi_title;
    private View netinfo;
    boolean k = false, k1 = false, k2 = false, k3 = false, k4 = false;
    WifiManager wifiManager;
    EthernetManager mEthManager;
    Context mContext = NetSettingActivity.this;
    private static final int MSG_UPDATE_BUTTON_STATUS = 1000;
    private static final String DHCP_IPV6_OPTION = "persist.sys.ipv6.mode";
    private static final String MODE_AUTO = "3";
    private static final String MODE_STATEFULL = "0";
    private static final String MODE_STATELESS = "1";
    private NetworkV4V6Utils.EthStack mStack;
    private int net_priority;//网络优先级标志位


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_network);
        SystemProperties.set("sys.settings.support.ap.flags", "1");//支持wifi热点
        SystemProperties.set("sys.settings.support.net.flags", "7");//支持WiFi功能

        initView();
        initData();


    }

    private void initData() {
        //        eth_ipv4.setOnClickListener(this);
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mEthManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        mStack = NetworkV4V6Utils.getEthStack(mEthManager);
        Log.e(TAG, "mEthManageripv6: " + mEthManager.isEnableIpv6());
        Log.e(TAG, "mEthManageripv4: " + mEthManager.isEnableIpv4());
        if (mEthManager.isEnableIpv6()) {
            TextView textView = (TextView) findViewById(R.id.ipv6_change);
            k3 = true;
            textView.setText("开启");
        } else {
            TextView textView = (TextView) findViewById(R.id.ipv6_change);
            k3 = false;
            textView.setText("关闭");
        }
        if (WifiAPUtil.getInstance(NetSettingActivity.this).getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
            TextView textView = (TextView) findViewById(R.id.tv_wifiap_manual_change);
            k4 = true;
            textView.setText("开启");
        } else {
            TextView textView = (TextView) findViewById(R.id.tv_wifiap_manual_change);
            k4 = false;
            textView.setText("关闭");
        }
        if (mEthManager.isEnableIpv4()) {
            TextView textView = (TextView) findViewById(R.id.ipv4_change);
            k2 = true;
            textView.setText("开启");
        } else {
            TextView textView = (TextView) findViewById(R.id.ipv4_change);
            k2 = false;
            textView.setText("关闭");
        }
        if (NetworkV4V6Utils.EthStack.IPV4 == mStack) {
            TextView textView = (TextView) findViewById(R.id.tv_protocol_manual_change);
            textView.setText("IPv4");
            k1 = false;

        } else if (NetworkV4V6Utils.EthStack.IPV6 == mStack) {
            TextView textView = (TextView) findViewById(R.id.tv_protocol_manual_change);
            textView.setText("IPv6");
            k1 = true;
        }
        Log.e(TAG, "onCreate: " + wifiManager.getWifiState());
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            k = true;
            TextView textView = (TextView) findViewById(R.id.tv_wifi_manual_change);
            textView.setText("开启");
        } else {
            k = false;
            TextView textView = (TextView) findViewById(R.id.tv_wifi_manual_change);
            textView.setText("关闭");
        }

        try {
            net_priority = Settings.Secure.getInt(mContext.getContentResolver(), "default_network_priority");

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextView protocol_yao = (TextView) findViewById(R.id.tv_protocol_manual_change);
        if (net_priority == 4) {
            protocol_yao.setText("IPv4");
        }
        if (net_priority == 6) {
            protocol_yao.setText("IPv6");
        }
//        wifiManager.setWifiEnabled(false);
    }

    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    private void initView() {
        wifi = (RelativeLayout) findViewById(R.id.wifi);
        hotspot = (RelativeLayout) findViewById(R.id.hotspot);
        protocol = (RelativeLayout) findViewById(R.id.protocol);
        netinfo = (RelativeLayout) findViewById(R.id.netinfo);
        ipv4 = (RelativeLayout) findViewById(R.id.eth_ipv4);
        ipv6 = (RelativeLayout) findViewById(R.id.eth_ipv6);
        wifi_title = (TextView) findViewById(R.id.wifi_title);
        wifi.setOnKeyListener(this);
        wifi.setOnClickListener(this);
        protocol.setOnKeyListener(this);
        netinfo.setOnClickListener(this);
        ipv4.setOnKeyListener(this);
        ipv4.setOnClickListener(this);
        ipv6.setOnKeyListener(this);
        ipv6.setOnClickListener(this);
        hotspot.setOnClickListener(this);
        hotspot.setOnKeyListener(this);

        try {
            int flag = Settings.Secure.getInt(mContext.getContentResolver(), "cmcc_wifi_disabled");
            if (flag == 1) {
                wifi_title.setVisibility(View.GONE);
                wifi.setVisibility(View.GONE);
                hotspot.setVisibility(View.GONE);
            } else {
                wifi_title.setVisibility(View.VISIBLE);
                wifi.setVisibility(View.VISIBLE);
                hotspot.setVisibility(View.VISIBLE);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick: " + v.getId());
        switch (v.getId()) {
            case R.id.wifi:
                if (k == true) {
                    Intent intent1 = new Intent(NetSettingActivity.this, WiFiActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.hotspot:
                if (k4 == true) {
                    Intent intent1 = new Intent(NetSettingActivity.this, WifiAPActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.eth_ipv4:
                if (k2 == true) {
                    Intent intent3 = new Intent(NetSettingActivity.this, EthernetIPv4Activity.class);
                    startActivity(intent3);
                }
                break;
            case R.id.eth_ipv6:
                if (k3 == true) {
                    startActivity(new Intent(mContext, EthernetIPv6Activity.class));
                }
                break;
            case R.id.netinfo:
                Intent intent6 = new Intent(NetSettingActivity.this, InternetInfoActivity.class);
                startActivity(intent6);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.wifi:
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        if (k == false) {
                            k = true;
                        } else {
                            k = false;
                        }
                        Log.e(TAG, "onKey: " + k);
                        TextView textView = (TextView) findViewById(R.id.tv_wifi_manual_change);
                        TextView textViewhotspot = (TextView) findViewById(R.id.tv_wifiap_manual_change);
                        if (k == false) {
                            textView.setText("关闭");
                            wifiManager.setWifiEnabled(false);
                            Log.e(TAG, "onKey: " + wifiManager.getWifiState());
                        } else if (k == true) {
                            textView.setText("开启");
//                            wifiManager.setWifiEnabled(true);
                            WifiAPUtil.getInstance(NetSettingActivity.this).closeWifiAp();
                            k4 = false;
                            textViewhotspot.setText("关闭");
                            Log.e(TAG, "onKey: " + wifiManager.getWifiState());
                        }
                    }
                    break;
                case R.id.hotspot:
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        if (k4 == false) {
                            k4 = true;
                        } else {
                            k4 = false;
                        }
                        Log.e(TAG, "onKey: " + k4);
                        TextView textView = (TextView) findViewById(R.id.tv_wifiap_manual_change);
                        TextView textViewwifi = (TextView) findViewById(R.id.tv_wifi_manual_change);
                        if (k4 == false) {
                            textView.setText("关闭");
                            WifiAPUtil.getInstance(NetSettingActivity.this).closeWifiAp();
                        } else if (k4 == true) {
                            textView.setText("开启");
                            WifiAPUtil.getInstance(NetSettingActivity.this).setWifiApEnabled();
                            k = false;
                            textViewwifi.setText("关闭");
                        }
                    }
                    break;
                case R.id.protocol:
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        if (k1 == false) {
                            k1 = true;
                        } else {
                            k1 = false;
                        }
                        Log.e(TAG, "onKey1: " + k1);
                        TextView textView = (TextView) findViewById(R.id.tv_protocol_manual_change);
                        if (k1 == false) {
                            textView.setText("IPv4");
                            Settings.Secure.putInt(mContext.getContentResolver(), "default_network_priority", 4);
//                            mEthManager.enableIpv6(false);
//                            mEthManager.enableIpv4(true);
//                            mEthManager.connectIpv4();

                        } else if (k1 == true) {
                            textView.setText("IPv6");
                            Settings.Secure.putInt(mContext.getContentResolver(), "default_network_priority", 6);
//                            mEthManager.enableIpv4(false);
//                            mEthManager.enableIpv6(true);
//                            mEthManager.connectIpv6();

                        }
                    }
                    break;
                case R.id.eth_ipv4:
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        if (k2 == false) {
                            k2 = true;
                        } else {
                            k2 = false;
                        }
                        Log.e(TAG, "onKey1: " + k2);
                        TextView textView = (TextView) findViewById(R.id.ipv4_change);
                        if (k2 == false) {
                            textView.setText("关闭");
                            mEthManager.enableIpv4(false);
                        } else if (k2 == true) {
                            textView.setText("开启");
                            mEthManager.enableIpv4(true);
                        }
                    }
                    break;
                case R.id.eth_ipv6:
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        if (k3 == false) {
                            k3 = true;
                        } else {
                            k3 = false;
                        }
                        Log.e(TAG, "onKey1: " + k3);
                        TextView textView = (TextView) findViewById(R.id.ipv6_change);
                        if (k3 == false) {
                            textView.setText("关闭");
                            mEthManager.enableIpv6(false);
                        } else if (k3 == true) {
                            textView.setText("开启");
                            mEthManager.enableIpv6(true);
                        }
                    }
                    break;
            }
        }
        return false;
    }
}