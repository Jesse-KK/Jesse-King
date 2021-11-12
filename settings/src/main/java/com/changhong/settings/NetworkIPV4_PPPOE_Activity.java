package com.changhong.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.IpConfiguration;
import android.net.IpInfo;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
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
    private NetworkV4V6Utils.EthStack mStack;
    private EthernetManager mEthManager;
    private static final int MESSAGE_SET_SECURTDHCP = 200004;
    private static final int MESSAGE_UPDATEETHINFO = 200005;
    private NetworkBroadcast mNetworkBroadcast;
    public static boolean isfinish = false;
    private IpConfiguration mIpConfigV4 = null;
    private String stack;
    private String username, password;
    private boolean isDisplay_password = false;

    //20201118 参考广西修改，解决pppoe连接失败问题
    private String mUsername, mPassword;

//    private final BroadcastReceiver mEthReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (TextUtils.isEmpty(action)) {
//                return;
//            }
//
//            int message = -1;
//            int rel = -1;
//
//            if (ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION.equals(action)) {
//                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
//                if (ChEthernetManager.EVENT_PHY_LINK_DOWN == message) {
//                    Toast.makeText(mContext, getString(R.string.setting_net_link_down), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//            if (mIpConfigV4 != null) {
//                if (IpConfiguration.IpAssignment.PPPOE != mIpConfigV4.ipAssignment) {
//                    Log.i(TAG, "ipv4 mode is not pppoe, now mode: " + mIpConfigV4.ipAssignment);
//                    return;
//                }
//            }
//
//            if (ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION.equals(intent.getAction())
//                    || ChEthernetManager.IPV4_STATE_CHANGED_ACTION.equals(intent.getAction())) {
//
//                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
//                Log.i(TAG, "ipv4 message: " + message);
//                switch (message) {
//                    case ChEthernetManager.EVENT_CONNECT_SUCCESSED:
//                        Log.i(TAG, "pppoe v4 connect ok.");
//                        if (NetworkV4V6Utils.isV4IpOk(mEthManager)) {
//                            Toast.makeText(mContext, getString(R.string.ipv4_connect_success), Toast.LENGTH_SHORT).show();
//                        }
//
//                        break;
//                    case ChEthernetManager.EVENT_CONNECT_FAILED:
//                        Log.i(TAG, "pppoe v4 connect failed.");
//                        Toast.makeText(mContext, getString(R.string.ipv4_connect_failed), Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//    };
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_SET_SECURTDHCP:
                    mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATEETHINFO, 5000);
                    break;
                case MESSAGE_UPDATEETHINFO:
                    IpInfo ipv6Info = mEthManager.getIPv6Info();
                    String IP_v6 = "";

                    if (ipv6Info != null && ipv6Info.ip != null) {
                        IP_v6 = ipv6Info.ip.getHostAddress();
                    }
                    Log.i(TAG, "handleMessage: IP_v6=" + IP_v6);
                    if (!isfinish) {
                        Log.i(TAG, "handleMessage: try again");
                        mHandler.sendEmptyMessage(MESSAGE_SET_SECURTDHCP);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pppoe_connection);
        init();
    }

    private void init() {
        mEthManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        mContext = NetworkIPV4_PPPOE_Activity.this;
        pppoe_connection_account = (EditText) findViewById(R.id.pppoe_connection_account);
        pppoe_connection_password = (EditText) findViewById(R.id.pppoe_connection_password);
        button_pppoepassword_visibility = (Button) findViewById(R.id.button_pppoepassword_visibility);
        button_confirm_pppoe_connection = (Button) findViewById(R.id.button_confirm_pppoe_connection);

        button_pppoepassword_visibility.setOnClickListener(this);
        button_confirm_pppoe_connection.setOnClickListener(this);

    }

    private void initData() {
        Log.i(TAG, "initData: ");
        String username = Settings.Secure.getString(mContext.getContentResolver(),"pppoe_username");
        String password = Settings.Secure.getString(mContext.getContentResolver(),"pppoe_pswd");
        Log.e(TAG, "initData: username：" + username + "  password:" + password);
        if ((TextUtils.isEmpty(username) && TextUtils.isEmpty(password))) {
            username = "";
            password = "";
        }
        //初始化时，显示上一次设置的用户名和密码
        pppoe_connection_account.setText(username);
        pppoe_connection_password.setText(password);
//        boolean isEnableIpv4 = mEthManager.isEnableIpv4();
//        boolean isEnableIpv6 = mEthManager.isEnableIpv6();
//        if (stack.equals("unset")) {//没点击切换栈，自己判断
//            if (isEnableIpv4 && isEnableIpv6) {
//                stack = "ipv4_ipv6";
//            } else if (isEnableIpv6) {
//                stack = "ipv6";
//            } else {
//                stack = "ipv4";
//            }
//        }
        stack="ipv4";

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm_pppoe_connection:
                connectPppoe2();
                break;
            case R.id.button_pppoepassword_visibility:
                if (isDisplay_password == false) {
                    button_pppoepassword_visibility.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    button_pppoepassword_visibility.setText("隐藏");
                    isDisplay_password = true;
                } else {
                    button_pppoepassword_visibility.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    button_pppoepassword_visibility.setText("显示");
                    isDisplay_password = false;
                }
                break;


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isfinish = true;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetworkBroadcast != null) {
            mContext.unregisterReceiver(mNetworkBroadcast);
        }
    }

    private void registerReceiver() {
        mNetworkBroadcast = new NetworkBroadcast();
        IntentFilter filter = new IntentFilter();
        //添加动作，监听网络
        filter.addAction(ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        filter.addAction(ChEthernetManager.IPV6_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mNetworkBroadcast, filter);
    }

    class NetworkBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int state = -1;
            if (action.equals(ChEthernetManager.IPV4_STATE_CHANGED_ACTION)) {
                state = intent.getIntExtra(
                        ChEthernetManager.EXTRA_ETHERNET_STATE, -1);
            }
            button_confirm_pppoe_connection.setClickable(true);
            Log.i(TAG, "receive  state is  " + state);
            getPppoeNetInfo(state);
        }
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
                isfinish = true;
                break;
            case ChEthernetManager.EVENT_CONNECT_FAILED:
                Log.i(TAG, "getPppoeNetInfo: EthernetManager.EVENT_IPV6_CONNECT_FAILED");
                Toast.makeText(mContext, getResources().
                        getString(R.string.ipv6_connect_failed), Toast.LENGTH_SHORT).show();
                isfinish = true;
                break;
        }
    }

    private void connectPppoe2() {

        if (!mEthManager.getNetLinkStatus()) {
            Log.i(TAG, "link down, stop connect pppoe.");
            Toast.makeText(mContext, getResources().getString(R.string.setting_net_link_down), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "-------- connectPppoe2 --------");

        //这里先判断PPPOE是否已经连接，如果已经连接，则提示不再重新连接
        mUsername = pppoe_connection_account.getText().toString();
        mPassword = pppoe_connection_password.getText().toString();
        mStack = NetworkV4V6Utils.getEthStack(mEthManager);
        mIpConfigV4 = mEthManager.getIpv4Configuration();
//        username=mIpConfigV4.pppoeUserName;
//        password=mIpConfigV4.pppoePassword;
        username = mUsername;
        password = mPassword;
        Log.e(TAG, "connectPppoe2: username:" + username + "  password:" + password);

        if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
            Log.i(TAG, "user or pwd empty.");
            Toast.makeText(mContext, getResources().getString(R.string.eth_ipoe_warn), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(mContext, getResources().getString(R.string.eth_connecting), Toast.LENGTH_SHORT).show();
        IpConfiguration IpConfiguration_v6 = new IpConfiguration();
        IpConfiguration_v6.setIpAssignment(IpConfiguration.IpAssignment.PPPOE);

        IpConfiguration_v6.pppoeUserName = username;
        IpConfiguration_v6.pppoePassword = password;

        mEthManager.setIpv4Configuration(IpConfiguration_v6);
//        mEthManager.setIpv6Configuration(IpConfiguration_v6);

        //先断开所有连接
        mEthManager.disconnectIpv4();
//        mEthManager.disconnectIpv6();
        mEthManager.enableIpv4(false);
//        mEthManager.enableIpv6(false);
//        if (mStack.equals(NetworkV4V6Utils.EthStack.IPV4V6)) {
//            Log.i(TAG, "in ipv4v6");
//            mEthManager.enableIpv4(true);
//            mEthManager.enableIpv6(true);
//
//            mEthManager.connectIpv4();
//            mEthManager.connectIpv6();
//
//
//        } else if (mStack.equals(NetworkV4V6Utils.EthStack.IPV6)) {
//            Log.i(TAG, "in ipv6");
//            mEthManager.enableIpv6(true);
//            mEthManager.connectIpv6();
//
//        } else {
//            Log.i(TAG, "in ipv4");
//            mEthManager.enableIpv4(true);
//            mEthManager.connectIpv4();
//        }
        mEthManager.enableIpv4(true);
        mEthManager.connectIpv4();
        //点击连接后，记录用户名和密码到prop属性
        Log.i(TAG, "set PPPOE>>>" + "username:" + mUsername + " password:" + mPassword);
        Settings.Secure.putInt(mContext.getContentResolver(), "default_eth_mod", 2);
        finish();
    }
}
