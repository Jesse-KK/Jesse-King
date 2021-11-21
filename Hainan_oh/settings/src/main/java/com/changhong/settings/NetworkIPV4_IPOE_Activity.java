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

public class NetworkIPV4_IPOE_Activity extends Activity implements View.OnClickListener {
    private static final String TAG = "ipv4_IPOE_Activity";
    private EditText ipoe_connection_account, ipoe_connection_password;
    private Button button_ipoepassword_visibility, button_confirm_ippoe_connection;
    private EthernetManager mEthManager;
    private Context mContext;
    private IpConfiguration mIpConfigV4;
    private String mUsername, mPassword;
    private boolean isDisplay_password = false;
    private NetworkV4V6Utils.EthStack mStack;
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
                if (IpConfiguration.IpAssignment.IPOE == mIpConfigV4.ipAssignment) {
                    Toast.makeText(mContext, "IPoE/DHCP+连接中...", Toast.LENGTH_SHORT).show();
                }
                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
                Log.i(TAG, "ipv4 message: " + message);
                switch (message) {
                    case ChEthernetManager.EVENT_CONNECT_SUCCESSED:
                        if (NetworkV4V6Utils.isV4IpOk(mEthManager)) {
                            Log.e(TAG, "onReceive: 22222222");
                            if (IpConfiguration.IpAssignment.IPOE == mIpConfigV4.ipAssignment) {
                                Toast.makeText(mContext, "网络已连接", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        break;
                    case ChEthernetManager.EVENT_CONNECT_FAILED:
                        if (IpConfiguration.IpAssignment.IPOE == mIpConfigV4.ipAssignment) {
                            Toast.makeText(mContext, "网络连接失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                }
                return;
            }
        }
    };

    private void registerRecv() {

        IntentFilter filterV4 = new IntentFilter(ChEthernetManager.IPV4_STATE_CHANGED_ACTION);
        filterV4.addAction(ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mEthReceiver, filterV4);
    }

    private void unregisterRecv() {
        if (mEthReceiver != null) {
            mContext.unregisterReceiver(mEthReceiver);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipv4_ipoe);
        mContext = NetworkIPV4_IPOE_Activity.this;

        initView();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerRecv();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterRecv();
    }

    private void initData() {
        mIpConfigV4 = mEthManager.getIpv4Configuration();
        mUsername = Settings.Secure.getString(mContext.getContentResolver(), "dhcp_user");
        mPassword = Settings.Secure.getString(mContext.getContentResolver(), "dhcp_pswd");
        Log.e(TAG, "initData: username：" + mUsername + "  password:" + mPassword);
        if ((TextUtils.isEmpty(mUsername) && TextUtils.isEmpty(mPassword))) {
            mUsername = "";
            mPassword = "";
        }
        //初始化时，显示上一次设置的用户名和密码
        ipoe_connection_account.setText(mUsername);
        ipoe_connection_password.setText(mPassword);

    }

    private void initView() {
        mEthManager = (EthernetManager) mContext.getSystemService(mContext.ETHERNET_SERVICE);
        mStack = NetworkV4V6Utils.getEthStack(mEthManager);
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
        if (!mEthManager.getNetLinkStatus()) {
            Log.i(TAG, "link down, stop connect pppoe.");
            Toast.makeText(mContext, getResources().getString(R.string.setting_net_link_down), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "-------- connectIPOE --------");
        //先断开ipv4连接
        mUsername = ipoe_connection_account.getText().toString();
        mPassword = ipoe_connection_password.getText().toString();

        mEthManager.disconnectIpv4();
        mEthManager.enableIpv4(false);

        mIpConfigV4.ipAssignment = IpConfiguration.IpAssignment.IPOE;
        mIpConfigV4.ipoeUserName = mUsername;
        mIpConfigV4.ipoePassword = mPassword;

        mEthManager.setIpv4Configuration(mIpConfigV4);
        mEthManager.enableIpv4(true);
        mEthManager.connectIpv4();
        Log.e(TAG, "connectIPOE: " + mIpConfigV4);
//            mEthManager.setEthernetEnabled(true);
        Log.e(TAG, "connectIPOE: 设置完IPv4的IPOE后的网络模式：" + mEthManager.getIpv4Configuration());
        Log.e(TAG, "connectIPOE: 当前的栈为：" + mStack);
        Log.e(TAG, "mIpConfigV4: " + mIpConfigV4);
        Settings.Secure.putString(mContext.getContentResolver(), "dhcp_user", mUsername);
        Settings.Secure.putString(mContext.getContentResolver(), "dhcp_pswd", mPassword);
        //点击连接后，记录用户名和密码到prop属性
        Log.i(TAG, "set IPOE>>>" + "username:" + mUsername + " password:" + mPassword);
        Log.e(TAG, "IpConfigV4: " + mEthManager.getIpv4Configuration());
        Toast.makeText(mContext, "网络正在连接...", Toast.LENGTH_SHORT).show();

        Settings.Secure.putInt(getContentResolver(), "default_eth_mod", 1);
    }
}
