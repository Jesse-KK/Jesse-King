package com.changhong.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.IpConfiguration;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NetworkIPV6_IPOE_Activity extends Activity implements View.OnClickListener {
    private static final String TAG = "ipv6_IPOE_Activity";
    private EditText ipoe_connection_account6, ipoe_connection_password6;
    private Button button_ipoepassword_visibility6, button_confirm_ippoe_connection6;
    private EthernetManager mEthManager;
    private Context mContext;
    private IpConfiguration mIpConfigV6;
    private String mUsername, mPassword;
    private boolean isDisplay_password = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipv6_ipoe);
        mContext = NetworkIPV6_IPOE_Activity.this;

        initView();
        initData();
    }

    private void initData() {
        mEthManager = (EthernetManager) mContext.getSystemService(mContext.ETHERNET_SERVICE);
        mIpConfigV6 = mEthManager.getIpv6Configuration();

//        Log.e(TAG, "initData: ipv6config::",+mIpConfigV6.toString());

        mUsername = Settings.Secure.getString(mContext.getContentResolver(), "dhcp_user");
        mPassword = Settings.Secure.getString(mContext.getContentResolver(), "dhcp_pswd");

        Log.e(TAG, "initData: 111"+ mUsername );
        Log.e(TAG, "initData: 222"+mPassword );

        ipoe_connection_account6.setText(mUsername);
        ipoe_connection_password6.setText(mPassword);
    }

    private void initView() {
        ipoe_connection_account6 = (EditText) findViewById(R.id.ipoe_connection_account6);
        ipoe_connection_password6 = (EditText) findViewById(R.id.ipoe_connection_password6);
        button_ipoepassword_visibility6 = (Button) findViewById(R.id.button_ipoepassword_visibility6);
        button_confirm_ippoe_connection6 = (Button) findViewById(R.id.button_confirm_ippoe_connection6);
        ipoe_connection_password6.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
        button_ipoepassword_visibility6.setOnClickListener(this);
        button_confirm_ippoe_connection6.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm_ippoe_connection6:
                connectIPv6_IPOE();
                startActivity(new Intent().setClass(mContext,EthernetIPv6Activity.class));
                this.finish();
                break;
            case R.id.button_ipoepassword_visibility6:
                if (isDisplay_password == false) {
                    ipoe_connection_password6.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    button_ipoepassword_visibility6.setText("隐藏");
                    isDisplay_password = true;
                } else {
                    ipoe_connection_password6.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    button_ipoepassword_visibility6.setText("显示");
                    isDisplay_password = false;
                }
                break;
            default:
                break;

        }
    }

    private void connectIPv6_IPOE() {
        Log.e(TAG, "connectIPv6_IPOE: start");
        //先断开ipv6连接
//        mEthManager.disconnectIpv4();
        mEthManager.disconnectIpv6();
        mEthManager.enableIpv6(false);
//        mEthManager.enableIpv4(false);

        mIpConfigV6.ipAssignment = IpConfiguration.IpAssignment.IPOE;
        mIpConfigV6.ipoeUserName = mUsername;
        mIpConfigV6.ipoePassword = mPassword;
        mIpConfigV6.setIpAssignment(IpConfiguration.IpAssignment.IPOE);

//        Settings.Secure.putInt(getContentResolver(), "dhcp_option", 1);
//        mEthManager.enableIpv4(false);
        mEthManager.enableIpv6(true);
        mEthManager.setIpv6Configuration(mIpConfigV6);
        Log.e(TAG, "connectIPOE: "+mIpConfigV6);
        mEthManager.connectIpv6();
//            mEthManager.setEthernetEnabled(true);

        Settings.Secure.putInt(getContentResolver(), "default_eth_mod", 1);
    }
}