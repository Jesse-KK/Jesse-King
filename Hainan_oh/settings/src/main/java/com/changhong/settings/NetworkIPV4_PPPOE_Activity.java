package com.changhong.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.changhong.settings.utils.NetworkV4V6Utils;

public class NetworkIPV4_PPPOE_Activity extends Activity implements View.OnClickListener {

    private final static String TAG = "ipv4_PPPOE_Activity";
    private Context mContext;
    private EditText pppoe_connection_account, pppoe_connection_password;
    private Button button_pppoepassword_visibility, button_confirm_pppoe_connection;
    private EthernetManager mEthManager;
    private IpConfiguration mIpConfigV4 = null;
    private boolean isDisplay_password = false;
    private String username,password;
    private final BroadcastReceiver mEthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIpConfigV4 = mEthManager.getIpv4Configuration();
            Log.i(TAG, "now mode: " + mIpConfigV4.ipAssignment);
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            int message;
            int rel = -1;

            if (ChEthernetManager.IPV4_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Log.e(TAG, "onReceive:king_king_king " + intent.getAction());
                Log.e(TAG, "onReceive: 1111");
                if (IpConfiguration.IpAssignment.PPPOE == mIpConfigV4.ipAssignment) {
                    Toast.makeText(mContext,"PPPOE连接中...",Toast.LENGTH_SHORT).show();
                }
                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
                Log.i(TAG, "ipv4 message: " + message);
                switch (message) {
                    case ChEthernetManager.EVENT_CONNECT_SUCCESSED:
                        if (NetworkV4V6Utils.isV4IpOk(mEthManager)) {
                            Log.e(TAG, "onReceive: 22222222");
                            if (IpConfiguration.IpAssignment.PPPOE == mIpConfigV4.ipAssignment) {
                                Toast.makeText(mContext,"网络已连接",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        break;
                    case ChEthernetManager.EVENT_CONNECT_FAILED:
                        if (IpConfiguration.IpAssignment.PPPOE == mIpConfigV4.ipAssignment) {
                            Toast.makeText(mContext,"网络连接失败",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                }
                return;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pppoe_connection);
        mContext = NetworkIPV4_PPPOE_Activity.this;
        init();
        initData();
    }

    private void init() {
        mEthManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        pppoe_connection_account = (EditText) findViewById(R.id.pppoe_connection_account);
        pppoe_connection_password = (EditText) findViewById(R.id.pppoe_connection_password);
        button_pppoepassword_visibility = (Button) findViewById(R.id.button_pppoepassword_visibility);
        button_confirm_pppoe_connection = (Button) findViewById(R.id.button_confirm_pppoe_connection);
        pppoe_connection_password.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
        button_pppoepassword_visibility.setOnClickListener(this);
        button_confirm_pppoe_connection.setOnClickListener(this);

    }

    private void initData() {
        Log.i(TAG, "initData: ");
        mIpConfigV4 = mEthManager.getIpv4Configuration();
        username = Settings.Secure.getString(mContext.getContentResolver(), "pppoe_username");
        password = Settings.Secure.getString(mContext.getContentResolver(), "pppoe_pswd");
        Log.e(TAG, "initData: username：" + username + "  password:" + password);
        if ((TextUtils.isEmpty(username) && TextUtils.isEmpty(password))) {
            username = "";
            password = "";
        }
        //初始化时，显示上一次设置的用户名和密码
        pppoe_connection_account.setText(username);
        pppoe_connection_password.setText(password);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm_pppoe_connection:
                connectPppoe();
                break;
            case R.id.button_pppoepassword_visibility:
                if (isDisplay_password == false) {
                    pppoe_connection_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    button_pppoepassword_visibility.setText("隐藏");
                    isDisplay_password = true;
                } else {
                    pppoe_connection_password.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    button_pppoepassword_visibility.setText("显示");
                    isDisplay_password = false;
                }
                break;
            default:
                break;
        }

    }

    private void registerRecv() {

        IntentFilter filterV4 = new IntentFilter(ChEthernetManager.IPV4_STATE_CHANGED_ACTION);
        filterV4.addAction(ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mEthReceiver, filterV4);

//        IntentFilter filterV6 = new IntentFilter(ChEthernetManager.IPV6_STATE_CHANGED_ACTION);
//        mContext.registerReceiver(mEthReceiverV6, filterV6);
    }

    private void unregisterRecv() {
        if (mEthReceiver != null) {
            mContext.unregisterReceiver(mEthReceiver);
        }
//        if (mEthReceiverV6 != null) {
//            mContext.unregisterReceiver(mEthReceiverV6);
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        registerRecv();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterRecv();
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
        username=pppoe_connection_account.getText().toString();
        password=pppoe_connection_password.getText().toString();
        mEthManager.disconnectIpv4();
        mEthManager.enableIpv4(false);
        mIpConfigV4.ipAssignment = IpConfiguration.IpAssignment.PPPOE;
        mIpConfigV4.pppoeUserName = username;
        mIpConfigV4.pppoePassword = password;
        mEthManager.setIpv4Configuration(mIpConfigV4);
        mEthManager.enableIpv4(true);
        mEthManager.connectIpv4();
        Log.e(TAG, "mIpConfigV4: "+mIpConfigV4);
        Settings.Secure.putString(mContext.getContentResolver(), "pppoe_username",username);
        Settings.Secure.putString(mContext.getContentResolver(), "pppoe_pswd",password);
        //点击连接后，记录用户名和密码到prop属性
        Log.i(TAG, "set PPPOE>>>" + "username:" + username + " password:" + password);
        Settings.Secure.putInt(mContext.getContentResolver(), "default_eth_mod", 2);
        Log.e(TAG, "IpConfigV4: "+mEthManager.getIpv4Configuration() );
        Toast.makeText(mContext,"网络正在连接...",Toast.LENGTH_SHORT).show();
    }
}
