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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.settings.utils.ChEthernetManager;
import com.changhong.settings.utils.NetworkV4V6Utils;

public class EthernetIPv4Activity extends Activity implements View.OnClickListener{
    private static final String TAG = "EthernetIPv4Activity";
    private View rl_setting_net_ipv4_dhcp,rl_setting_net_ipv4_ipoe,rl_setting_net_ipv4_pppoe,rl_setting_net_ipv4_staticip;
    private TextView dhcp_state,pppoe_state,ipoe_state,static_state;
    private EthernetManager mEthManager;
    private static final int MSG_UPDATE_BUTTON_STATUS = 1000;
    private static final String DHCP_IPV6_OPTION = "persist.sys.ipv6.mode";
    private static final String MODE_AUTO = "3";
    private static final String MODE_STATEFULL = "0";
    private static final String MODE_STATELESS = "1";
    private Context mContext=EthernetIPv4Activity.this;
    private static final int MESSAGE_UPDATEETHINFO = 100005;
    private String stack;
    private String mIpv6Option;
    private NetworkV4V6Utils.EthStack mStack;
    private IpConfiguration mIpConfigV4,mIpConfigV6;
    private Handler mNetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_BUTTON_STATUS:
//                    setBtnConfirmClickable(true);
                    break;
            }
        }
    };


    private final BroadcastReceiver mEthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            int message = -1;
            int rel = -1;
            if (ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION.equals(action)) {
                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
                if (ChEthernetManager.EVENT_PHY_LINK_DOWN == message) {
                    if (IpConfiguration.IpAssignment.DHCP == mIpConfigV4.ipAssignment) {
                        dhcp_state.setText("未连接");
                    } else if (IpConfiguration.IpAssignment.PPPOE == mIpConfigV4.ipAssignment) {
                        pppoe_state.setText("未连接");
                    } else if (IpConfiguration.IpAssignment.IPOE == mIpConfigV4.ipAssignment) {
                        ipoe_state.setText("未连接");
                    } else if (IpConfiguration.IpAssignment.STATIC == mIpConfigV4.ipAssignment) {
                        static_state.setText("未连接");
                    }
                }
            }

            if (IpConfiguration.IpAssignment.DHCP != mIpConfigV4.ipAssignment) {
                Log.i(TAG, "ipv4 mode is not dhcp, now mode: " + mIpConfigV4.ipAssignment);
                return;
            }

            if (ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION.equals(intent.getAction())
                    || ChEthernetManager.IPV4_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                    if(IpConfiguration.IpAssignment.DHCP==mIpConfigV4.ipAssignment){
                        dhcp_state.setText("连接中");
                    }else if(IpConfiguration.IpAssignment.PPPOE==mIpConfigV4.ipAssignment){
                        pppoe_state.setText("连接中");
                    }else if(IpConfiguration.IpAssignment.IPOE==mIpConfigV4.ipAssignment){
                        ipoe_state.setText("连接中");
                    }else if(IpConfiguration.IpAssignment.STATIC==mIpConfigV4.ipAssignment){
                        static_state.setText("连接中");
                    }
                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
                Log.i(TAG, "ipv4 message: " + message);
                switch (message) {
                    case ChEthernetManager.EVENT_CONNECT_SUCCESSED:
                        Log.i(TAG, "dhcp v4 connect ok.");
                    if (NetworkV4V6Utils.isV4IpOk(mEthManager)){
                            if(IpConfiguration.IpAssignment.DHCP==mIpConfigV4.ipAssignment){
                                dhcp_state.setText("已连接");
                            }else if(IpConfiguration.IpAssignment.PPPOE==mIpConfigV4.ipAssignment){
                                pppoe_state.setText("已连接");
                            }else if(IpConfiguration.IpAssignment.IPOE==mIpConfigV4.ipAssignment){
                                ipoe_state.setText("已连接");
                            }else if(IpConfiguration.IpAssignment.STATIC==mIpConfigV4.ipAssignment){
                                static_state.setText("已连接");
                            }
                        }

                        break;
                    case ChEthernetManager.EVENT_CONNECT_FAILED:
                        Log.i(TAG, "dhcp v4 connect failed.");
                        int failReson = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_FAILED_REASON, -1);
                        if (failReson == ChEthernetManager.FAILED_REASON_IPOE_AUTH_FAILED
                                || failReson == ChEthernetManager.FAILED_REASON_PPPOE_AUTH_FAILED
                                || failReson == ChEthernetManager.FAILED_REASON_PPPOE_TIMEDOUT) {
                            return;
                        }
                            if(IpConfiguration.IpAssignment.DHCP==mIpConfigV4.ipAssignment){
                                dhcp_state.setText("未连接");
                            }else if(IpConfiguration.IpAssignment.PPPOE==mIpConfigV4.ipAssignment){
                                pppoe_state.setText("未连接");
                            }else if(IpConfiguration.IpAssignment.IPOE==mIpConfigV4.ipAssignment){
                                ipoe_state.setText("未连接");
                            }else if(IpConfiguration.IpAssignment.STATIC==mIpConfigV4.ipAssignment){
                                static_state.setText("未连接");
                            }
//                        Toast.makeText(context, getString(R.string.ipv4_connect_failed), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mEthReceiverV6 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (IpConfiguration.IpAssignment.DHCP != mIpConfigV6.ipAssignment
                    && IpConfiguration.IpAssignment.AUTO != mIpConfigV6.ipAssignment) {
                Log.i(TAG, "ipv6 mode is not dhcp, now mode: " + mIpConfigV6.ipAssignment);
                return;
            }
            int message = -1;
            int rel = -1;
            String connectMsg = "";
            if (ChEthernetManager.IPV6_STATE_CHANGED_ACTION.equals(intent.getAction())) {

                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
                switch (message) {
                    case ChEthernetManager.EVENT_CONNECT_SUCCESSED:
                        Log.i(TAG, "dhcp v6 connect ok.");
                        if (NetworkV4V6Utils.isV6IpOk(mEthManager)){
                            Toast.makeText(context, getString(R.string.ipv6_connect_success), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ChEthernetManager.EVENT_CONNECT_FAILED:
                        Log.i(TAG, "dhcp v6 connect failed.");
                        int failReson = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_FAILED_REASON, -1);
                        if (failReson == ChEthernetManager.FAILED_REASON_IPOE_AUTH_FAILED
                                || failReson == ChEthernetManager.FAILED_REASON_PPPOE_AUTH_FAILED
                                || failReson == ChEthernetManager.FAILED_REASON_PPPOE_TIMEDOUT) {
                            return;
                        }
                        Toast.makeText(context, getString(R.string.ipv6_connect_failed), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ipv4_net);
        initData();
        initView();
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
        Log.i(TAG, "onStop: ");
        unregisterRecv();
        mNetHandler.removeCallbacksAndMessages(null);
    }

    private void initData() {

        mEthManager = (EthernetManager) mContext.getSystemService(mContext.ETHERNET_SERVICE);
        mIpConfigV4 = mEthManager.getIpv4Configuration();
        mIpConfigV6 = mEthManager.getIpv6Configuration();

        mStack = NetworkV4V6Utils.getEthStack(mEthManager);
        Log.e(TAG, "mStack: "+mStack);
        mIpv6Option = SystemProperties.get(DHCP_IPV6_OPTION, MODE_AUTO);
    }

    public void initView(){
        dhcp_state=(TextView)findViewById(R.id.dhcp_state);
        ipoe_state=(TextView)findViewById(R.id.ipoe_state);
        pppoe_state=(TextView)findViewById(R.id.pppoe_state);
        static_state=(TextView)findViewById(R.id.static_state);
        rl_setting_net_ipv4_dhcp=findViewById(R.id.rl_setting_net_ipv4_dhcp);
        rl_setting_net_ipv4_ipoe=findViewById(R.id.rl_setting_net_ipv4_ipoe);
        rl_setting_net_ipv4_pppoe=findViewById(R.id.rl_setting_net_ipv4_pppoe);
        rl_setting_net_ipv4_staticip=findViewById(R.id.rl_setting_net_ipv4_staticip);
        rl_setting_net_ipv4_dhcp.setOnClickListener(this);
        rl_setting_net_ipv4_ipoe.setOnClickListener(this);
        rl_setting_net_ipv4_pppoe.setOnClickListener(this);
        rl_setting_net_ipv4_staticip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting_net_ipv4_dhcp:
                if (!mEthManager.getNetLinkStatus()) {
                    Log.i(TAG, "link down");
                    Toast.makeText(mContext, "link_down", Toast.LENGTH_SHORT).show();
                    return;
                } else {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
////                            mNetHandler.sendEmptyMessageDelayed(MSG_UPDATE_BUTTON_STATUS, 3000);
//                        }
//                    }).start();
//
                    connectDhcp_sc();
                }
                break;
            case R.id.rl_setting_net_ipv4_staticip:
                Intent intent1=new Intent(EthernetIPv4Activity.this,StaticIPv4Activity.class);
                startActivity(intent1);
                break;
            case R.id.rl_setting_net_ipv4_pppoe:
                startActivity(new Intent().setClass(mContext,Network_PPPOE_Activity.class));

        }
    }

    private void registerRecv() {

        IntentFilter filterV4 = new IntentFilter(ChEthernetManager.IPV4_STATE_CHANGED_ACTION);
        filterV4.addAction(ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mEthReceiver, filterV4);

        IntentFilter filterV6 = new IntentFilter(ChEthernetManager.IPV6_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mEthReceiverV6, filterV6);
    }

    private void unregisterRecv() {
        if (mEthReceiver != null) {
            mContext.unregisterReceiver(mEthReceiver);
        }
        if (mEthReceiverV6 != null) {
            mContext.unregisterReceiver(mEthReceiverV6);
        }
    }

    private void connectDhcp_sc() {
        if (!mEthManager.getNetLinkStatus()) {
            Toast.makeText(mContext, "link down", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "link down.");
            return;
        }
        Log.i(TAG, "in btnSAVE");
        Toast.makeText(mContext, "Connecting now,please wait a moment...", Toast.LENGTH_SHORT).show();


        IpConfiguration ipConfiguration = new IpConfiguration();

        ipConfiguration.setIpAssignment(IpConfiguration.IpAssignment.DHCP);
        android.provider.Settings.Secure.putInt(mContext.getContentResolver(), "dhcp_option", 0);

        Log.i(TAG, "in connectDhcp, mStack:" + mStack);
        mEthManager.setIpv4Configuration(ipConfiguration);
        mEthManager.setIpv6Configuration(ipConfiguration);

        //先断开ipv6连接
        mEthManager.disconnectIpv4();
        mEthManager.disconnectIpv6();
        mEthManager.enableIpv6(false);
        mEthManager.enableIpv4(false);
        if (NetworkV4V6Utils.EthStack.IPV4V6 == mStack)  {
            if (MODE_AUTO.equals(mIpv6Option)) {
                SystemProperties.set(DHCP_IPV6_OPTION, MODE_AUTO);
            } else if (MODE_STATEFULL.equals(mIpv6Option)) {
                //mEthManager.setV6DHCPStateful(true);
            } else if (MODE_STATELESS.equals(mIpv6Option)) {
                //mEthManager.setV6DHCPStateful(false);
            }
            mEthManager.enableIpv4(true);
            mEthManager.enableIpv6(true);

            mEthManager.connectIpv4();
            mEthManager.connectIpv6();

        } else if (NetworkV4V6Utils.EthStack.IPV6 == mStack) {
            if (MODE_AUTO.equals(mIpv6Option)) {
                SystemProperties.set(DHCP_IPV6_OPTION, MODE_AUTO);
            } else if (MODE_STATEFULL.equals(mIpv6Option)) {
                //mEthManager.setV6DHCPStateful(true);
            } else if (MODE_STATELESS.equals(mIpv6Option)) {
                //mEthManager.setV6DHCPStateful(false);
            }
            mEthManager.enableIpv6(true);
            mEthManager.connectIpv6();
        } else {
            mEthManager.enableIpv4(true);
            mEthManager.connectIpv4();
        }
        Settings.Secure.putInt(mContext.getContentResolver(), "default_eth_mod", 0);
//        mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATEETHINFO, 5000);

    }

    private void getEthNetInfo(int state) {
        switch (state) {
            case ChEthernetManager.EVENT_CONNECT_SUCCESSED:
                Log.i(TAG, "getEthNetInfo: EthernetManager.EVENT_IPV6_CONNECT_SUCCESSED");
                Toast.makeText(mContext, "IPV6 Connected", Toast.LENGTH_SHORT).show();
                break;
            case ChEthernetManager.EVENT_CONNECT_FAILED:
                Log.i(TAG, "getEthNetInfo: EthernetManager.EVENT_IPV6_CONNECT_FAILED");
                Toast.makeText(mContext, "IPV6 Connect Failed", Toast.LENGTH_SHORT).show();
                break;
            case ChEthernetManager.EVENT_PHY_LINK_DOWN:
                Log.i(TAG, "getEthNetInfo: EthernetManager.EVENT_DHCP_CONNECT_FAILED");
                Toast.makeText(mContext, "Cable Down", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
