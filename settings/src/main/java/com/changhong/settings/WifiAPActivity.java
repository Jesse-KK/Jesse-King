package com.changhong.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.changhong.settings.utils.MyHandler;
import com.changhong.settings.utils.WifiAPUtil;

public class WifiAPActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "qll_WifiAPActivity";
    private Context mContext = null;
    private WifiManager mWifiManager = null;
    private WifiAPUtil mWifiAPUtil = null;
    private String ssid, password;
    private WifiAPUtil.WifiSecurityType mWifiType;
    private MyHandler mHander = null;

    private EditText etAccount, etPwd;
    private Button btnConfirm, btnCancel,btnShow;
    private RadioButton rbNoWPA, rbWPA, rbWPA2;
    private ImageView imgSwitchAPBG, imgSwitchAP;
    private LinearLayout llPwdContainer;
    private FrameLayout flContainer;
    private boolean isFirstBoot = true; // 首次收到开启成功的广播不要 finish，以及用户操作开关时收到广播不能finish
    private boolean isBtnClickable = true; // 防止用户连续点击 btn

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ap_startup:
                // set wifi_ap info
                setWifiAP();
//                finish();
                break;
            case R.id.btn_ap_cancel:
                finish();
                break;
            case R.id.button_ipoepassword_visibility6:
                etPwd.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
    }

    private BroadcastReceiver mWifiApReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (WifiAPUtil.WIFI_AP_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int state = intent.getIntExtra(WifiAPUtil.EXTRA_WIFI_AP_STATE, -1);

                switch (state) {
                    case WifiAPUtil.WIFI_AP_STATE_DISABLED:
                        isBtnClickable = true;
                        /*imgSwitchAP.setBackgroundResource(R.drawable.btn_toggle_off);
                        flContainer.setVisibility(View.INVISIBLE);*/
                        Log.i(TAG, "onReceive: wifi ap is disable.");
                        break;
                    case WifiAPUtil.WIFI_AP_STATE_ENABLED:
                        if (isFirstBoot) {
                            isBtnClickable = true;
//                            imgSwitchAP.setBackgroundResource(R.drawable.btn_toggle_on);
//                            flContainer.setVisibility(View.VISIBLE);
                            isFirstBoot = false;
                            Log.i(TAG, "onReceive: wifi ap is enable.");
                        } else {
                            isBtnClickable = true;
                            mHander.sendEmptyMessage(MyHandler.WIFI_AP_SET_SUCCESS);
//                            imgSwitchAP.setBackgroundResource(R.drawable.btn_toggle_on);
//                            flContainer.setVisibility(View.VISIBLE);
                            /*Intent intent1 = new Intent();
                            intent.putExtra("setWiFiAP", true);
                            setResult(RESULT_OK, intent1);*/
                            finish();
                        }

                        break;
                    case WifiAPUtil.WIFI_AP_STATE_ENABLING:
                        isBtnClickable = false;
                        Log.i(TAG, "wifi ap enabling.");
                        break;
                    case WifiAPUtil.WIFI_AP_STATE_DISABLING:
                        isBtnClickable = false;
                        Log.i(TAG, "wifi ap disabling.");
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_ap);

        initData();

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWifiApReceiver != null) {
            unregisterReceiver(mWifiApReceiver);
        }

        if (mHander != null) {
            mHander.removeCallbacksAndMessages(null);
        }

    }

    private void initView() {
        etAccount = (EditText) findViewById(R.id.ap_name);
        etPwd = (EditText) findViewById(R.id.wifiappassword);
        btnConfirm = (Button) findViewById(R.id.btn_ap_startup);
        btnCancel = (Button) findViewById(R.id.btn_ap_cancel);
        btnShow=(Button)findViewById(R.id.button_ipoepassword_visibility6);
//        rbNoWPA.setOnClickListener(this);
//        rbWPA.setOnClickListener(this);
//        rbWPA2.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnShow.setOnClickListener(this);
//        imgSwitchAPBG.setOnClickListener(this);

//        int state = mWifiAPUtil.getWifiApState();
//        if (WifiAPUtil.WIFI_AP_STATE_ENABLED == state) {
//            flContainer.setVisibility(View.VISIBLE);
//        } else {
//            flContainer.setVisibility(View.INVISIBLE);
//        }

        etAccount.setText(ssid);
        etPwd.setText(password);

        setSecurity();
    }

    private void setSecurity() {
//        if (mWifiType == WifiAPUtil.WifiSecurityType.WIFICIPHER_NOPASS) {
//            rbNoWPA.setChecked(true);
//            llPwdContainer.setVisibility(View.GONE);
//        } else if (mWifiType == WifiAPUtil.WifiSecurityType.WIFICIPHER_WPA) {
//            rbWPA.setChecked(true);
//            llPwdContainer.setVisibility(View.VISIBLE);
//        } else if (mWifiType == WifiAPUtil.WifiSecurityType.WIFICIPHER_WPA2) {
//            llPwdContainer.setVisibility(View.VISIBLE);
//            rbWPA2.setChecked(true);
//        }
    }

    private void initData() {

        mContext = WifiAPActivity.this;
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiAPUtil = WifiAPUtil.getInstance(mContext);
        mHander = new MyHandler(mContext);

        // 注册 wifi ap 的广播
        IntentFilter filter = new IntentFilter(WifiAPUtil.WIFI_AP_STATE_CHANGED_ACTION);
        registerReceiver(mWifiApReceiver, filter);

        ssid = mWifiAPUtil.getValidApSsid();
        password = mWifiAPUtil.getValidPassword();
        mWifiType = mWifiAPUtil.getValidSecurity();

        if (TextUtils.isEmpty(ssid)) {
            ssid = "AndroidAP";
        }
        if (TextUtils.isEmpty(password)) {
            password = "12345678";
        }

        Log.i(TAG, "initData: ssid is " + ssid + " and the password is " + password + " and the security is " + mWifiType);

        if (mWifiType == null || mWifiType == WifiAPUtil.WifiSecurityType.WIFICIPHER_INVALID) {
            mWifiType = WifiAPUtil.WifiSecurityType.WIFICIPHER_WPA2;
        }
    }

    private void switchWifiAP() {
        Log.i(TAG, "switchWifiAP: ");
        // 首先判断 wifi 的状态

        int state = mWifiAPUtil.getWifiApState();

        if (state == WifiAPUtil.WIFI_AP_STATE_ENABLED) {
            mHander.sendEmptyMessage(MyHandler.WIFI_AP_IS_CLOSE);
            mWifiAPUtil.closeWifiAp();
//            imgSwitchAP.setBackgroundResource(R.drawable.btn_toggle_off);
            flContainer.setVisibility(View.INVISIBLE);
            isFirstBoot = true;
        } else {
            mHander.sendEmptyMessage(MyHandler.WIFI_AP_IS_OPEN);
            mWifiAPUtil.turnOnWifiAP(ssid, password, mWifiType);
//            imgSwitchAP.setBackgroundResource(R.drawable.btn_toggle_on);
        }

    }

    private void setWifiAP() {

        Log.i(TAG, "setWifiAP: ");
        ssid = etAccount.getText().toString();
        password = etPwd.getText().toString();

        if (TextUtils.isEmpty(ssid)) {
            Log.i(TAG, "ssid null.");
            mHander.sendEmptyMessage(MyHandler.WIFI_AP_SSID_NULL);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Log.i(TAG, "pwd null.");
            mHander.sendEmptyMessage(MyHandler.WIFI_AP_PWD_NULL);
            return;
        }

        if (WifiAPUtil.WifiSecurityType.WIFICIPHER_WPA.equals(mWifiType) ||
                WifiAPUtil.WifiSecurityType.WIFICIPHER_WPA2.equals(mWifiType)) {

            mHander.sendEmptyMessage(MyHandler.WIFI_AP_IS_OPEN);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mWifiAPUtil.turnOnWifiAP(ssid, password, mWifiType);

                }
            }).start();

        }


    }
}
