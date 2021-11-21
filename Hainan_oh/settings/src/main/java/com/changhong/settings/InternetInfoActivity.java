package com.changhong.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.IpConfiguration;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changhong.settings.utils.ChEthernetManager;

import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;

import static android.net.ConnectivityManager.TYPE_WIFI;

public class InternetInfoActivity extends Activity implements View.OnClickListener {
    private View wifiIpv4Info, ethIpv4Info, wifiIpv6Info, ethIpv6Info, wifi_ipV4, wifi_ipV6;
    private String wifi_ipv6 = "";
    private TextView tv_wifi_ipv4_change, tv_wifi_ipv6_change, tv_eth_ipv4_change, tv_eth_ipv6_change;
    private EthernetManager mEthManager;
    private WifiManager mWifiManger;
    private Context mContext;
    private IntenterBroadcastReceiver receiver;
    private static final String TAG = "InternetInfoActivity";

    class IntenterBroadcastReceiver extends BroadcastReceiver {

        private ConnectivityManager mConnectivityManager;
        private NetworkInfo netInfo;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {
                    /////////////网络连接
                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        /////WiFi网络
                        checkWifiNetStatus();
                        Log.i(TAG, "onReceive: wifi has been connected !!!!!!!!!!!!!!!!!!111");

                    } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                        /////有线网络
                        checkEthNetStatus();
                    }
                } else {
                    Log.e(TAG, "The intertnet is inavailable##############");
                    //网络断开
                    tv_wifi_ipv4_change.setText("未连接");
                    tv_wifi_ipv6_change.setText("未连接");
//                    setDefInfo(false);
                }
            }

            int state = -1;
            if (action.equals(ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION)
                    || action.equals(ChEthernetManager.IPV6_STATE_CHANGED_ACTION)) {
                state = intent.getIntExtra(
                        ChEthernetManager.EXTRA_ETHERNET_STATE, -1);
            }
//            if (action.equals(ChEthernetManager.PPPOE_STATE_CHANGED_ACTION)) {
//                state = intent.getIntExtra(
//                        PppoeManager.EXTRA_PPPOE_STATE, -1);
//            }
            Log.i(TAG, "receive  state is  " + state);
            if (state == ChEthernetManager.EVENT_PHY_LINK_DOWN) {
                Log.i(TAG, "onReceive: The netLine has out !!!!!");
                tv_eth_ipv4_change.setText("未连接");
            } else if (state != -1) {
//                tv_eth_ipv4_change.setText("已连接");
                checkEthNetStatus();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_network_info);
        mContext = InternetInfoActivity.this;

        wifi_ipV6 = (RelativeLayout) findViewById(R.id.wifi_ipV6);
        wifi_ipV4 = (RelativeLayout) findViewById(R.id.wifi_ipV4);
        try {
            int flag = Settings.Secure.getInt(mContext.getContentResolver(), "cmcc_wifi_disabled");
            if (flag == 1) {
                wifi_ipV4.setVisibility(View.GONE);
                wifi_ipV6.setVisibility(View.GONE);
            } else {
                wifi_ipV6.setVisibility(View.VISIBLE);
                wifi_ipV4.setVisibility(View.VISIBLE);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }


        mEthManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        mWifiManger = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        tv_wifi_ipv4_change = (TextView) findViewById(R.id.tv_wifi_ipv4_change);
        tv_wifi_ipv6_change = (TextView) findViewById(R.id.tv_wifi_ipv6_change);
        tv_eth_ipv4_change = (TextView) findViewById(R.id.tv_eth_ipv4_change);
        tv_eth_ipv6_change = (TextView) findViewById(R.id.tv_eth_ipv6_change);
        checkWifiNetStatus();
        checkEthNetStatus();
        wifiIpv4Info = findViewById(R.id.wifi_ipV4);
        wifiIpv6Info = findViewById(R.id.wifi_ipV6);
        ethIpv4Info = findViewById(R.id.eth_ipV4);
        ethIpv6Info = findViewById(R.id.eth_ipV6);
        wifiIpv4Info.setOnClickListener(this);
        wifiIpv6Info.setOnClickListener(this);
        ethIpv4Info.setOnClickListener(this);
        ethIpv6Info.setOnClickListener(this);
        receiver = new IntenterBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        filter.addAction(PppoeManager.PPPOE_STATE_CHANGED_ACTION);
        filter.addAction(ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        filter.addAction(ChEthernetManager.IPV6_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter);
    }

    public void checkEthNetStatus() {
        IpConfiguration.IpAssignment ipAssignment_4 = mEthManager.getIpv4Configuration().ipAssignment;
        IpConfiguration.IpAssignment ipAssignment_6 = mEthManager.getIpv6Configuration().ipAssignment;
        if (mEthManager.getIPv4Info() != null) {
            if (mEthManager.getIPv4Info().ip != null) {
                if (mEthManager.getIPv4Info().ip.getHostAddress() != "0.0.0.0") {
//                    tv_eth_ipv4_change.setText("已连接");
                    if (ipAssignment_4 == IpConfiguration.IpAssignment.DHCP) {
                        tv_eth_ipv4_change.setText("DHCP");
                    }
                    if (ipAssignment_4 == IpConfiguration.IpAssignment.IPOE) {
                        tv_eth_ipv4_change.setText("IPOE/DHCP+");
                    }
                    if (ipAssignment_4 == IpConfiguration.IpAssignment.PPPOE) {
                        tv_eth_ipv4_change.setText("PPPOE");
                    }
                    if (ipAssignment_4 == IpConfiguration.IpAssignment.STATIC) {
                        tv_eth_ipv4_change.setText("静态IP");
                    }

                } else {
                    tv_eth_ipv4_change.setText("未连接");
                }
            } else {
                tv_eth_ipv4_change.setText("未连接");
            }
        } else {
            tv_eth_ipv4_change.setText("未连接");
        }
        if (mEthManager.getIPv6Info() != null) {
//            tv_eth_ipv6_change.setText("已连接");
            if (ipAssignment_6 == IpConfiguration.IpAssignment.DHCP) {
                tv_eth_ipv6_change.setText("DHCP");
            }
            if (ipAssignment_6 == IpConfiguration.IpAssignment.IPOE) {
                tv_eth_ipv6_change.setText("IPOE/DHCP+");
            }
            if (ipAssignment_6 == IpConfiguration.IpAssignment.PPPOE) {
                tv_eth_ipv6_change.setText("PPPOE");
            }
            if (ipAssignment_6 == IpConfiguration.IpAssignment.STATIC) {
                tv_eth_ipv6_change.setText("静态IP");
            }
        } else {
            tv_eth_ipv6_change.setText("未连接");
        }
    }

    public void checkWifiNetStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.i(TAG, "initView: assert cm!=null");
        assert cm != null;
        try {
            LinkProperties prop = cm.getLinkProperties(TYPE_WIFI);
            Log.i(TAG, "initView: assert prop!=null");
            assert prop != null;
            Class clazz = prop.getClass();
            Method method = clazz.getMethod("getAllAddresses");
            method.setAccessible(true);
            List<InetAddress> list = (List<InetAddress>) method.invoke(prop);
            Iterator<InetAddress> iter = list.iterator();
            Log.i(TAG, "initView: iter!=null");
            assert iter != null;
            InetAddress a6 = null;
            while (iter.hasNext()) {
                a6 = iter.next();
                if (a6 instanceof Inet6Address) {
                    wifi_ipv6 = a6.getHostAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mWifiManger.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            if (mWifiManger.getDhcpInfo() != null) {
                if (intToIp(mWifiManger.getDhcpInfo().ipAddress) != "0.0.0.0" && mEthManager.isEnableIpv4()) {
                    tv_wifi_ipv4_change.setText("已连接");
                } else {
                    tv_wifi_ipv4_change.setText("未连接");
                }
            } else {
                tv_wifi_ipv4_change.setText("未连接");
            }
            if (wifi_ipv6 != "" && mEthManager.isEnableIpv6()) {
                tv_wifi_ipv6_change.setText("已连接");
            } else {
                tv_wifi_ipv6_change.setText("未连接");
            }
        } else {
            tv_wifi_ipv4_change.setText("未连接");
            tv_wifi_ipv6_change.setText("未连接");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wifi_ipV4:
                Intent intent1 = new Intent(InternetInfoActivity.this, WifiIPv4InfoActivity.class);
                startActivity(intent1);
                break;
            case R.id.eth_ipV4:
                Intent intent2 = new Intent(InternetInfoActivity.this, EthIPv4InfoActivity.class);
                startActivity(intent2);
                break;
            case R.id.eth_ipV6:
                Intent intent3 = new Intent(InternetInfoActivity.this, EthIPv6InfoActivity.class);
                startActivity(intent3);
                break;
            case R.id.wifi_ipV6:
                Intent intent4 = new Intent(InternetInfoActivity.this, WifiIPv6InfoActivity.class);
                startActivity(intent4);
                break;
        }
    }


    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }
}
