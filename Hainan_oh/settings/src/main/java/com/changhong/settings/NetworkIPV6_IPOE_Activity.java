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

public class NetworkIPV6_IPOE_Activity extends Activity implements View.OnClickListener {
    private static final String TAG = "ipv6_IPOE_Activity";
    private EditText ipoe_connection_account6, ipoe_connection_password6;
    private Button button_ipoepassword_visibility6, button_confirm_ippoe_connection6;
    private EthernetManager mEthManager;
    private Context mContext;
    private IpConfiguration mIpConfigV6;
    private String mUsername, mPassword;
    private boolean isDisplay_password = false;

    private final BroadcastReceiver mEthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIpConfigV6 = mEthManager.getIpv6Configuration();
            Log.i(TAG, "now mode: " + mIpConfigV6.ipAssignment);
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            int message;
            int rel = -1;

            if (ChEthernetManager.IPV6_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Log.e(TAG, "onReceive:king_king_king " + intent.getAction());
                Log.e(TAG, "onReceive: 1111");
                if (IpConfiguration.IpAssignment.IPOE == mIpConfigV6.ipAssignment) {
                    Toast.makeText(mContext, "IPoE/DHCP+连接中...", Toast.LENGTH_SHORT).show();
                }
                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
                Log.i(TAG, "ipv6 message: " + message);
                switch (message) {
                    case ChEthernetManager.EVENT_CONNECT_SUCCESSED:
                        if (NetworkV4V6Utils.isV6IpOk(mEthManager)) {
                            Log.e(TAG, "onReceive: 22222222");
                            if (IpConfiguration.IpAssignment.IPOE == mIpConfigV6.ipAssignment) {
                                Toast.makeText(mContext, "网络已连接", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        break;
                    case ChEthernetManager.EVENT_CONNECT_FAILED:
                        if (IpConfiguration.IpAssignment.IPOE == mIpConfigV6.ipAssignment) {
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

        IntentFilter filterV6 = new IntentFilter(ChEthernetManager.IPV6_STATE_CHANGED_ACTION);
        filterV6.addAction(ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mEthReceiver, filterV6);
    }

    private void unregisterRecv() {
        if (mEthReceiver != null) {
            mContext.unregisterReceiver(mEthReceiver);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipv6_ipoe);
        mContext = NetworkIPV6_IPOE_Activity.this;

        initView();
        initData();
    }

    private void initData() {
        mEthManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        mIpConfigV6 = mEthManager.getIpv6Configuration();

//        Log.e(TAG, "initData: ipv6config::",+mIpConfigV6.toString());

        mUsername = Settings.Secure.getString(mContext.getContentResolver(), "dhcp_user");
        mPassword = Settings.Secure.getString(mContext.getContentResolver(), "dhcp_pswd");

        Log.e(TAG, "initData: 111"+ mUsername );
        Log.e(TAG, "initData: 222"+mPassword );
        if ((TextUtils.isEmpty(mUsername) && TextUtils.isEmpty(mPassword))) {
            mUsername = "";
            mPassword = "";
        }

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
    protected void onStart() {
        super.onStart();
        registerRecv();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterRecv();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm_ippoe_connection6:
                connectIPv6_IPOE();
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
        if (!mEthManager.getNetLinkStatus()) {
            Log.i(TAG, "link down, stop connect pppoe.");
            Toast.makeText(mContext, getResources().getString(R.string.setting_net_link_down), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "-------- connectIPOE --------");
        mUsername = ipoe_connection_account6.getText().toString();
        mPassword = ipoe_connection_password6.getText().toString();


        mEthManager.disconnectIpv6();
        mEthManager.enableIpv6(false);


        mIpConfigV6.ipAssignment = IpConfiguration.IpAssignment.IPOE;
        mIpConfigV6.ipoeUserName = mUsername;
        mIpConfigV6.ipoePassword = mPassword;

        mEthManager.setIpv6Configuration(mIpConfigV6);
        mEthManager.enableIpv6(true);
        mEthManager.connectIpv6();
//        mEthManager.enableIpv6(false);
        Log.e(TAG, "connectIPOE: " + mIpConfigV6);
//            mEthManager.setEthernetEnabled(true);
        Log.e(TAG, "connectIPOE: 设置完IPv6的IPOE后的网络模式：" + mEthManager.getIpv6Configuration());
        Settings.Secure.putString(mContext.getContentResolver(), "dhcp_user", mUsername);
        Settings.Secure.putString(mContext.getContentResolver(), "dhcp_pswd", mPassword);
        //点击连接后，记录用户名和密码到prop属性
        Log.i(TAG, "set IPOE>>>" + "username:" + mUsername + " password:" + mPassword);
        Log.e(TAG, "IpConfigV4: " + mEthManager.getIpv6Configuration());
        Toast.makeText(mContext, "网络正在连接...", Toast.LENGTH_SHORT).show();

        Settings.Secure.putInt(getContentResolver(), "default_eth_mod", 1);
    }
}