package com.changhong.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.settings.R;
import com.changhong.settings.utils.WifiAdminUtils;
import com.changhong.settings.utils.WifiUtils;

import java.util.List;


/**
 * Created by fdw on 2017/9/13..
 */
public class WifiStatusDialog extends Dialog {
    // Wifi管理类
    private WifiAdminUtils mWifiAdmin;
    private Context context;
    private ScanResult scanResult;
    private TextView txtWifiName;
    private TextView txtConnStatus;
    private TextView txtSinglStrength;
    private TextView txtSecurityLevel;
    private TextView txtIpAddress;
    private Button txtBtnDisConn;
    private Button txtBtnCancel;
    private Button txtBtnDisForget;
    private String wifiName;
    private String securigyLevel;
    private LinearLayout llIpv6Status, llIpv4Status;
    private TextView tvIpv6Address;

    private int level;

    private static final String TAG = "qll_WifiStatusDialog";


    public WifiStatusDialog(Context context) {
        super(context);
        this.mWifiAdmin = new WifiAdminUtils(context);
    }

    private WifiStatusDialog(Context context, int theme, String wifiName,
                             int singlStren, String securityLevl) {
        super(context,theme);
        this.context = context;
        this.wifiName = wifiName;
        this.level = singlStren;
        this.securigyLevel = securityLevl;
        this.mWifiAdmin = new WifiAdminUtils(context);
    }

    public WifiStatusDialog(Context context, int theme, ScanResult scanResult,
                            OnNetworkChangeListener onNetworkChangeListener) {
        this(context, theme, scanResult.SSID, scanResult.level,
                scanResult.capabilities);
        this.scanResult = scanResult;
        this.mWifiAdmin = new WifiAdminUtils(context);
        this.onNetworkChangeListener = onNetworkChangeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_center_wifi_alreadytselect);
        setCanceledOnTouchOutside(false);

        initView();
        setListener();
    }

    private void setListener() {

        txtBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("txtBtnCancel");
                WifiStatusDialog.this.dismiss();
            }
        });

        txtBtnDisConn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 断开连接
                int netId = mWifiAdmin.getConnNetId();

                WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    Log.i(TAG, "forget wifi info: " + mWifiAdmin.getWifiInfo());
                    wifiManager.removeNetwork(netId);
                    wifiManager.saveConfiguration();
                    onNetworkChangeListener.onNetWorkDisConnect();
                } else {
                    Log.i(TAG, "wifimanager is null, cannot forget wifi: " + mWifiAdmin.getWifiInfo());
                }

                WifiStatusDialog.this.dismiss();
            }
        });
    }

    private void initView() {
        txtWifiName = (TextView) findViewById(R.id.tv_wifi_name);
        txtConnStatus = (TextView) findViewById(R.id.tv_wifi_status);
        txtSinglStrength = (TextView) findViewById(R.id.tv_wifi_strength);
        txtSecurityLevel = (TextView) findViewById(R.id.tv_wifi_secure);
//        txtIpAddress = (TextView) findViewById(R.id.tv_wifi_ip);
//
//        tvIpv6Address = (TextView) findViewById(R.id.tv_wifi_dialog_ipv6_ip);
//        llIpv6Status = (LinearLayout) findViewById(R.id.ll_wifi_dialog_ipv6);
//        llIpv4Status = (LinearLayout) findViewById(R.id.ll_wifi_dialog_ipv4);

        txtBtnDisConn = (Button) findViewById(R.id.dialog_wifi_disconnect);
        txtBtnDisForget = (Button) findViewById(R.id.dialog_wifi_forget);
        txtBtnCancel = (Button) findViewById(R.id.dialog_wifi_cancel);
        if (TextUtils.isEmpty(wifiName)) {
            txtWifiName.setText(scanResult.BSSID);
        } else {
            txtWifiName.setText(wifiName);
        }

        txtConnStatus.setText("已连接");
        txtSinglStrength.setText(WifiAdminUtils.singlLevToStr(level));
        txtSecurityLevel.setText(WifiUtils.getSecurityString(context, scanResult, false));

        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        WifiConfiguration configuration = null;
        if (list != null) {
            for (WifiConfiguration tmpConfig : list) {
//                Log.i(TAG, "configuration: " + tmpConfig.toString());
                Log.i(TAG, tmpConfig.SSID +" status: " + tmpConfig.status);

                if (tmpConfig.status == WifiConfiguration.Status.CURRENT) {
                    configuration = tmpConfig;
                    break;
                }
            }
        } else {
            Log.i(TAG, "configured networks is null.");
        }

        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

//        if (configuration != null && configuration.ipAssignment != WifiConfiguration.IpAssignment.NONE) {
//            LinkProperties prop = cm.getLinkProperties(ConnectivityManager.TYPE_WIFI);
//            Iterator<InetAddress> iterator = prop.getAllAddresses().iterator();
//            InetAddress a4 = null;
//            String ip = null;
//            if (iterator.hasNext()) {
//                a4 = iterator.next();
//                if (!(a4 instanceof Inet6Address)) {
//                    ip = a4.getHostAddress();
//                }
//            }
//
//            if (!TextUtils.isEmpty(ip)) {
//                txtIpAddress.setText(ip);
//                llIpv4Status.setVisibility(View.VISIBLE);
//            } else {
//                txtIpAddress.setText("0.0.0.0");
//                llIpv4Status.setVisibility(View.VISIBLE);
//            }
//        } else {
//            llIpv4Status.setVisibility(View.GONE);
//        }
//        if (configuration != null && configuration.ipv6Assignment != WifiConfiguration.Ipv6Assignment.NONE) {
//            Log.i(TAG, "current wifi is: " + configuration.SSID);
//
//            LinkProperties prop = cm.getLinkProperties(ConnectivityManager.TYPE_WIFI);
//            Iterator<InetAddress> iterator = prop.getAllAddresses().iterator();
//            InetAddress a6 = null;
//            String ip = null;
//            while (iterator.hasNext()) {
//                a6 = iterator.next();
//                if (a6 instanceof Inet6Address) {
//                    ip = a6.getHostAddress();
//                }
//            }
//            if (!TextUtils.isEmpty(ip)) {
//                tvIpv6Address.setText(ip);
//                llIpv6Status.setVisibility(View.VISIBLE);
//            } else {
//
//                llIpv6Status.setVisibility(View.GONE);
//            }
//
//        } else {
//            Log.i(TAG, "current wifi configuration is null or ipv6Assignment is none.");
//            llIpv6Status.setVisibility(View.GONE);
//        }

    }

    private void showShortToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private OnNetworkChangeListener onNetworkChangeListener;

}
