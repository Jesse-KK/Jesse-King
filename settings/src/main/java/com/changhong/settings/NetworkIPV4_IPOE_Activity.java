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

public class NetworkIPV4_IPOE_Activity extends Activity implements View.OnClickListener {
    private static final String TAG = "ipv4_IPOE_Activity";
    private EditText ipoe_connection_account, ipoe_connection_password;
    private Button button_ipoepassword_visibility, button_confirm_ippoe_connection;
    private EthernetManager mEthManager;
    private Context mContext;
    private IpConfiguration mIpConfigV4;
    private String mUsername, mPassword;
    private boolean isDisplay_password = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipv4_ipoe);
        mContext = NetworkIPV4_IPOE_Activity.this;

        initView();
        initData();
    }

    private void initData() {
        mEthManager = (EthernetManager) mContext.getSystemService(mContext.ETHERNET_SERVICE);
        mIpConfigV4 = mEthManager.getIpv4Configuration();

        mUsername = Settings.Secure.getString(mContext.getContentResolver(), "dhcp_user");
        mPassword = Settings.Secure.getString(mContext.getContentResolver(), "dhcp_pswd");

        Log.e(TAG, "initData: 111"+ mUsername );
        Log.e(TAG, "initData: 222"+mPassword );

        ipoe_connection_account.setText(mUsername);
        ipoe_connection_password.setText(mPassword);
    }

    private void initView() {
        ipoe_connection_account = (EditText) findViewById(R.id.ipoe_connection_account);
        ipoe_connection_password = (EditText) findViewById(R.id.ipoe_connection_password);
        button_ipoepassword_visibility = (Button) findViewById(R.id.button_ipoepassword_visibility);
        button_confirm_ippoe_connection = (Button) findViewById(R.id.button_confirm_ippoe_connection);
        ipoe_connection_password.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
        button_ipoepassword_visibility.setOnClickListener(this);
        button_confirm_ippoe_connection.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm_ippoe_connection:
                connectIPOE();
                startActivity(new Intent().setClass(mContext,EthernetIPv4Activity.class));
                this.finish();
                break;
            case R.id.button_ipoepassword_visibility:
                if (isDisplay_password == false) {
                    ipoe_connection_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    button_ipoepassword_visibility.setText("隐藏");
                    isDisplay_password = true;
                } else {
                    ipoe_connection_password.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    button_ipoepassword_visibility.setText("显示");
                    isDisplay_password = false;
                }
                break;
            default:
                break;

        }
    }

    private void connectIPOE() {
        //先断开ipv4连接
        mEthManager.disconnectIpv4();
//        mEthManager.disconnectIpv6();
//        mEthManager.enableIpv6(false);
        mEthManager.enableIpv4(false);

        mIpConfigV4.ipAssignment = IpConfiguration.IpAssignment.IPOE;
        mIpConfigV4.ipoeUserName = mUsername;
        mIpConfigV4.ipoePassword = mPassword;
        mIpConfigV4.setIpAssignment(IpConfiguration.IpAssignment.IPOE);

//        Settings.Secure.putInt(getContentResolver(), "dhcp_option", 1);
        mEthManager.enableIpv4(true);
//        mEthManager.enableIpv6(false);
        mEthManager.setIpv4Configuration(mIpConfigV4);
        Log.e(TAG, "connectIPOE: "+mIpConfigV4);
        mEthManager.connectIpv4();
//            mEthManager.setEthernetEnabled(true);

        Settings.Secure.putInt(getContentResolver(), "default_eth_mod", 1);
    }
}
