package com.changhong.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.changhong.settings.R;
import com.changhong.settings.utils.WifiAdminUtils;

/**
 * Created by Cheng on 2018/3/2.
 */

public class WifiConnect_savedDialog extends Dialog {
    private Context mContext;
    private WifiConfiguration conf;
    private ScanResult scanResult;
    private WifiAdminUtils mWifiAdmin;
    private OnNetworkChangeListener onNetworkChangeListener;
    private WifiManager wifiManager;

    public WifiConnect_savedDialog(Context context, int theme, ScanResult scanResult, WifiConfiguration configuration
            , OnNetworkChangeListener onNetworkChangeListener) {
        super(context,theme);
        this.mContext = context;
        this.scanResult = scanResult;
        this.conf = configuration;
        this.onNetworkChangeListener = onNetworkChangeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_center_wifi_select);

        initView();
    }

    private void initView() {
        wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiAdmin = new WifiAdminUtils(mContext);
        TextView tv_ssid = (TextView) findViewById(R.id.tv_wifi_name);
        if (!TextUtils.isEmpty(scanResult.SSID)) {
            tv_ssid.setText(scanResult.SSID);
        } else {
            tv_ssid.setText(scanResult.BSSID);
        }

        TextView tv_security = (TextView) findViewById(R.id.tv_wifi_secure);
        tv_security.setText(scanResult.capabilities);
        TextView tv_wifi_strength=(TextView) findViewById(R.id.tv_wifi_strength);
        tv_wifi_strength.setText(WifiAdminUtils.singlLevToStr(scanResult.level));
        TextView tv_wifi_status=(TextView) findViewById(R.id.tv_wifi_status);
        tv_wifi_status.setText("未连接");
//        TextView tv_sigal = (TextView) findViewById(R.id.tv_wifi_signal);
//        tv_sigal.setText(WifiAdminUtils.singlLevToStr(scanResult.level));

        Button bt_cancel = (Button) findViewById(R.id.dialog_wifi_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        Button bt_forget = (Button) findViewById(R.id.dialog_wifi_forget);
        bt_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //int netId = mWifiAdmin.getConnNetId();
                int netId=conf.networkId;
                wifiManager.removeNetwork(netId);
                wifiManager.saveConfiguration();
                Log.i("cheng", "onClick: To forget password !!!!!!!!!  "+netId);
                //mWifiAdmin.forgetPsd(netId);
                onNetworkChangeListener.onNetWorkDisConnect();
                dismiss();
            }
        });


        Button bt_connect = (Button) findViewById(R.id.dialog_wifi_connect);
        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiManager.enableNetwork(conf.networkId, true);
                dismiss();
            }
        });
    }
}
