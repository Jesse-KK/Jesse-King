package com.changhong.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
//import android.net.pppoe.PppoeManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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
import com.changhong.settings.wifi.WiFiActivity;

import java.util.zip.Inflater;

/**
 * Created by Eileen
 * 2019/12/18
 * email: lingling.qiu@changhong.com
 * <p>
 * 2020/3/31 需求文档1.3: 合并 ipv4 & ipv6，将网络信息移入网络配置模块
 */
public class NetSettingActivity extends Activity implements View.OnClickListener, RadioButton.OnCheckedChangeListener,View.OnKeyListener {
    private static final String TAG = "NetSettingActivity";
    private View wifi,protocol,ipv4,ipv6;
    private View netinfo;
    private View eth_ipv4;
    boolean k=false,k1=false,k2=false,k3=false;
    WifiManager wifiManager;
    EthernetManager mEthManager;
    Context mContext=NetSettingActivity.this;
    private static final int MSG_UPDATE_BUTTON_STATUS = 1000;
    private static final String DHCP_IPV6_OPTION = "persist.sys.ipv6.mode";
    private static final String MODE_AUTO = "3";
    private static final String MODE_STATEFULL = "0";
    private static final String MODE_STATELESS = "1";
    private NetworkV4V6Utils.EthStack mStack;
    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_network);
        wifi = (RelativeLayout) findViewById(R.id.wifi);
        protocol = (RelativeLayout)findViewById(R.id.protocol);
        netinfo = (RelativeLayout) findViewById(R.id.netinfo);
        ipv4 = (RelativeLayout)findViewById(R.id.eth_ipv4);
        ipv6 = (RelativeLayout)findViewById(R.id.eth_ipv6);
        wifi.setOnKeyListener(this);
        wifi.setOnClickListener(this);
        protocol.setOnKeyListener(this);
        netinfo.setOnClickListener(this);
        ipv4.setOnKeyListener(this);
        ipv4.setOnClickListener(this);
        ipv6.setOnKeyListener(this);
        ipv6.setOnClickListener(this);
//        eth_ipv4.setOnClickListener(this);
        wifiManager= (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mEthManager=(EthernetManager)mContext.getSystemService(Context.ETHERNET_SERVICE);
        mStack = NetworkV4V6Utils.getEthStack(mEthManager);
        Log.e(TAG, "mEthManageripv6: "+mEthManager.isEnableIpv6() );
        Log.e(TAG, "mEthManageripv4: "+mEthManager.isEnableIpv4() );
        if (mEthManager.isEnableIpv6()){
            TextView textView=(TextView) findViewById(R.id.ipv6_change);
            k3=true;
            textView.setText("开启");
        }else{
            TextView textView=(TextView) findViewById(R.id.ipv6_change);
            k3=false;
            textView.setText("关闭");
        }
        if (mEthManager.isEnableIpv4()){
            TextView textView=(TextView) findViewById(R.id.ipv4_change);
            k2=true;
            textView.setText("开启");
        }else{
            TextView textView=(TextView) findViewById(R.id.ipv4_change);
            k2=false;
            textView.setText("关闭");
        }
        if (NetworkV4V6Utils.EthStack.IPV4 == mStack) {
            TextView textView=(TextView) findViewById(R.id.tv_protocol_manual_change);
            textView.setText("IPv4");
            k1=false;

        } else if (NetworkV4V6Utils.EthStack.IPV6 == mStack) {
            TextView textView=(TextView) findViewById(R.id.tv_protocol_manual_change);
            textView.setText("IPv6");
            k1=true;
        }
        Log.e(TAG, "onCreate: "+wifiManager.getWifiState() );
        if(wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED){
            k=true;
            TextView textView=(TextView) findViewById(R.id.tv_wifi_manual_change);
            textView.setText("开启");
        }else{
            k=false;
            TextView textView=(TextView) findViewById(R.id.tv_wifi_manual_change);
            textView.setText("关闭");
        }
//        wifiManager.setWifiEnabled(false);
    }
    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick: "+v.getId());
        switch (v.getId()){
            case R.id.wifi:
                if(k==true) {
                    Intent intent1 = new Intent(NetSettingActivity.this, WiFiActivity.class);
                    startActivity(intent1);
                }
                break;
//            case R.id.hotspot:
            case R.id.eth_ipv4:
                Intent intent3 = new Intent(NetSettingActivity.this, EthernetIPv4Activity.class);
                startActivity(intent3);
                break;
//            case R.id.eth_ipv6:
//            case R.id.protocol:
            case R.id.netinfo:
                Intent intent6 = new Intent(NetSettingActivity.this, InternetInfoActivity.class);
                startActivity(intent6);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.wifi:
                    if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                        if (k == false) {
                            k = true;
                        } else {
                            k = false;
                        }
                        Log.e(TAG, "onKey: " + k);
                        TextView textView=(TextView) findViewById(R.id.tv_wifi_manual_change);
                        if(k == false) {
                            textView.setText("关闭");
                            wifiManager.setWifiEnabled(false);
                            Log.e(TAG, "onKey: "+wifiManager.getWifiState());
                        }else if(k==true){
                            textView.setText("开启");
                            wifiManager.setWifiEnabled(true);
                            Log.e(TAG, "onKey: "+wifiManager.getWifiState());
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
                        TextView textView=(TextView) findViewById(R.id.tv_protocol_manual_change);
                        if(k1 == false) {
                            textView.setText("IPv4");
                            mEthManager.enableIpv6(false);
                            mEthManager.enableIpv4(true);
                            mEthManager.connectIpv4();

                        }else if(k1==true){
                            textView.setText("IPv6");
                            mEthManager.enableIpv4(false);
                            mEthManager.enableIpv6(true);
                            mEthManager.connectIpv6();

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
                        TextView textView=(TextView) findViewById(R.id.ipv4_change);
                        if(k2 == false) {
                            textView.setText("关闭");
                            mEthManager.enableIpv4(false);
                        }else if(k2==true){
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
                        TextView textView=(TextView) findViewById(R.id.ipv6_change);
                        if(k3 == false) {
                            textView.setText("关闭");
                            mEthManager.enableIpv6(false);
                        }else if(k3==true){
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
