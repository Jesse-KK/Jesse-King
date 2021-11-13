package com.changhong.settings;

import android.app.Activity;
import android.content.Context;
import android.net.IpConfiguration;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.changhong.settings.utils.ChEthernetManager;

public class NetworkIPV6_PPPOE_Activity extends Activity implements View.OnClickListener {
    private final static String TAG = "ipv4_PPPOE_Activity";
    private Context mContext;
    private EditText pppoe_connection_account6, pppoe_connection_password6;
    private Button button_pppoepassword_visibility6, button_confirm_pppoe_connection6;
    private EthernetManager mEthManager;
    private IpConfiguration mIpConfigV6 = null;
    private boolean isDisplay_password = false;
    private String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pppoe_ipv6);
        mContext = NetworkIPV6_PPPOE_Activity.this;
        init();
        initData();
    }

    private void init() {
        mEthManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        pppoe_connection_account6 = (EditText) findViewById(R.id.pppoe_connection_account6);
        pppoe_connection_password6 = (EditText) findViewById(R.id.pppoe_connection_password6);
        button_pppoepassword_visibility6 = (Button) findViewById(R.id.button_pppoepassword_visibility6);
        button_confirm_pppoe_connection6 = (Button) findViewById(R.id.button_confirm_pppoe_connection6);

        button_pppoepassword_visibility6.setOnClickListener(this);
        button_confirm_pppoe_connection6.setOnClickListener(this);

    }

    private void initData() {
        Log.i(TAG, "initData: ");
        mIpConfigV6 = mEthManager.getIpv6Configuration();
        username = Settings.Secure.getString(mContext.getContentResolver(), "pppoe_username");
        password = Settings.Secure.getString(mContext.getContentResolver(), "pppoe_pswd");
        Log.e(TAG, "initData: username：" + username + "  password:" + password);
        if ((TextUtils.isEmpty(username) && TextUtils.isEmpty(password))) {
            username = "";
            password = "";
        }
        //初始化时，显示上一次设置的用户名和密码
        pppoe_connection_account6.setText(username);
        pppoe_connection_password6.setText(password);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm_pppoe_connection6:
                connectPppoe();
                break;
            case R.id.button_pppoepassword_visibility6:
                if (isDisplay_password == false) {
                    button_pppoepassword_visibility6.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    button_pppoepassword_visibility6.setText("隐藏");
                    isDisplay_password = true;
                } else {
                    button_pppoepassword_visibility6.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    button_pppoepassword_visibility6.setText("显示");
                    isDisplay_password = false;
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void getPppoeNetInfo(int state) {

        switch (state) {
            case ChEthernetManager.EVENT_PHY_LINK_DOWN:
                Log.i(TAG, "getPppoeNetInfo: EthernetManager.EVENT_PHY_LINK_DOWN");
                Toast.makeText(mContext, getResources().
                        getString(R.string.setting_net_link_down), Toast.LENGTH_SHORT).show();
                break;
            case ChEthernetManager.EVENT_CONNECT_SUCCESSED:
                Log.i(TAG, "getPppoeNetInfo: EthernetManager.EVENT_IPV6_CONNECT_SUCCESSED");
                Toast.makeText(mContext, getResources().
                        getString(R.string.ipv6_connect_success), Toast.LENGTH_SHORT).show();
                break;
            case ChEthernetManager.EVENT_CONNECT_FAILED:
                Log.i(TAG, "getPppoeNetInfo: EthernetManager.EVENT_IPV6_CONNECT_FAILED");
                Toast.makeText(mContext, getResources().
                        getString(R.string.ipv6_connect_failed), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void connectPppoe() {

        if (!mEthManager.getNetLinkStatus()) {
            Log.i(TAG, "link down, stop connect pppoe.");
            Toast.makeText(mContext, getResources().getString(R.string.setting_net_link_down), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "-------- connectPppoe2 --------");

//        //这里先判断PPPOE是否已经连接，如果已经连接，则提示不再重新连接
//        mUsername = pppoe_connection_account.getText().toString();
//        mPassword = pppoe_connection_password.getText().toString();
//        mStack = NetworkV4V6Utils.getEthStack(mEthManager);
//        mIpConfigV4 = mEthManager.getIpv4Configuration();
////        username=mIpConfigV4.pppoeUserName;
////        password=mIpConfigV4.pppoePassword;
//
//        Log.e(TAG, "connectPppoe2: username:" + username + "  password:" + password);
//
//        if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
//            Log.i(TAG, "user or pwd empty.");
//            Toast.makeText(mContext, getResources().getString(R.string.eth_ipoe_warn), Toast.LENGTH_SHORT).show();
//            return;
//        }
//        Toast.makeText(mContext, getResources().getString(R.string.eth_connecting), Toast.LENGTH_SHORT).show();

        mEthManager.disconnectIpv6();
        mEthManager.enableIpv6(false);

        mIpConfigV6.ipAssignment = IpConfiguration.IpAssignment.PPPOE;
        mIpConfigV6.pppoeUserName = username;
        mIpConfigV6.pppoePassword = password;
        mIpConfigV6.setIpAssignment(IpConfiguration.IpAssignment.PPPOE);

        mEthManager.enableIpv6(true);
        mEthManager.connectIpv6();
        //点击连接后，记录用户名和密码到prop属性
        Log.i(TAG, "set PPPOE>>>" + "username:" + username + " password:" + password);
        Settings.Secure.putInt(mContext.getContentResolver(), "default_eth_mod", 2);
        finish();
    }
}
