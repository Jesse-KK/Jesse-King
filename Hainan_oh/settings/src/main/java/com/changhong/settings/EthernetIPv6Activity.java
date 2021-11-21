package com.changhong.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.IpConfiguration;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.settings.utils.ChEthernetManager;
import com.changhong.settings.utils.NetworkV4V6Utils;

import javax.security.auth.login.LoginException;

public class EthernetIPv6Activity extends Activity implements View.OnClickListener {
    public static final String TAG = "EthernetIPv6Activity";
    private RelativeLayout rl_setting_net_ipv6_dhcp, rl_setting_net_ipv6_ipoe, rl_setting_net_ipv6_pppoe, rl_setting_net_ipv6_staticip;
    private TextView dhcp_state_ipv6, ipoe_state_ipv6, pppoe_state_ipv6, static_state_ipv6;
    private Context mContext;
    private EthernetManager mEthManager;
    private NetworkV4V6Utils.EthStack mStack;
    private IpConfiguration mIpConfigV6;
    private String mIpv6Option = "";

    private final int MESSAGE_UPDATEETHINFO = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_UPDATEETHINFO:
                    Log.e(TAG, "handleMessage: " + mIpConfigV6.ipAssignment);
                    break;
                default:
                    break;
            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipv6_net);
        mContext = EthernetIPv6Activity.this;

        initView();
        initData();


    }

    private void initData() {
        mEthManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        mIpConfigV6 = mEthManager.getIpv6Configuration();
        mStack = NetworkV4V6Utils.getEthStack(mEthManager);

        mIpv6Option = NetworkConstant.OPTION_STATEFULL;
//        mIpv6Option = SystemProperties.get(DHCP_IPV6_OPTION, MODE_AUTO);
    }

    private void initView() {
        rl_setting_net_ipv6_dhcp = (RelativeLayout) findViewById(R.id.rl_setting_net_ipv6_dhcp);
        rl_setting_net_ipv6_ipoe = (RelativeLayout) findViewById(R.id.rl_setting_net_ipv6_ipoe);
        rl_setting_net_ipv6_pppoe = (RelativeLayout) findViewById(R.id.rl_setting_net_ipv6_pppoe);
        rl_setting_net_ipv6_staticip = (RelativeLayout) findViewById(R.id.rl_setting_net_ipv6_staticip);
        dhcp_state_ipv6 = (TextView) findViewById(R.id.dhcp_state_ipv6);
        ipoe_state_ipv6 = (TextView) findViewById(R.id.ipoe_state_ipv6);
        pppoe_state_ipv6 = (TextView) findViewById(R.id.pppoe_state_ipv6);
        static_state_ipv6 = (TextView) findViewById(R.id.static_state_ipv6);
        rl_setting_net_ipv6_dhcp.setOnClickListener(this);
        rl_setting_net_ipv6_ipoe.setOnClickListener(this);
        rl_setting_net_ipv6_pppoe.setOnClickListener(this);
        rl_setting_net_ipv6_staticip.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.rl_setting_net_ipv6_dhcp:
                if (!mEthManager.getNetLinkStatus()) {
                    Log.i(TAG, "link down");
                    Toast.makeText(mContext, "link_down", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessageDelayed(MESSAGE_UPDATEETHINFO, 3000);
                        }
                    }).start();

                    connectIPv6_DHCP();

                    Log.e(TAG, "onClick:现在的状态是: " + mIpConfigV6.ipAssignment);
                }
                break;
            case R.id.rl_setting_net_ipv6_ipoe:
                startActivity(intent.setClass(mContext, NetworkIPV6_IPOE_Activity.class));
                break;
            case R.id.rl_setting_net_ipv6_pppoe:
                startActivity(intent.setClass(mContext, NetworkIPV6_PPPOE_Activity.class));
                break;
            case R.id.rl_setting_net_ipv6_staticip:
                startActivity(intent.setClass(mContext, StaticIPv6Activity.class));
                break;
            default:
                break;
        }
    }

    private final BroadcastReceiver mEthReceiverIPv6 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIpConfigV6 = mEthManager.getIpv6Configuration();
            Log.i(TAG, "now mode: 现在的网络模式===" + mIpConfigV6.ipAssignment);
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                Log.e(TAG, "onReceive: 空的空的");
                return;
            }
            int message;
            int rel = -1;
            if (ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION.equals(action)) {
                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
                if (ChEthernetManager.EVENT_PHY_LINK_DOWN == message) {
                    if (IpConfiguration.IpAssignment.DHCP == mIpConfigV6.ipAssignment) {
                        dhcp_state_ipv6.setText("未连接");
                    } else if (IpConfiguration.IpAssignment.PPPOE == mIpConfigV6.ipAssignment) {
                        pppoe_state_ipv6.setText("未连接");
                    } else if (IpConfiguration.IpAssignment.IPOE == mIpConfigV6.ipAssignment) {
                        ipoe_state_ipv6.setText("未连接");
                    } else if (IpConfiguration.IpAssignment.STATIC == mIpConfigV6.ipAssignment) {
                        static_state_ipv6.setText("未连接");
                    }
                }
            }

            if (ChEthernetManager.IPV6_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Log.e(TAG, "onReceive:king_king_king " + intent.getAction());
                updateText();
                if (mIpConfigV6.ipAssignment == IpConfiguration.IpAssignment.AUTO) {
                    Log.e(TAG, "onReceive:AUTO,AUTO,AUTO");
                }
                if (IpConfiguration.IpAssignment.DHCP == mIpConfigV6.ipAssignment) {
                    Toast.makeText(mContext, "DHCP连接中...", Toast.LENGTH_SHORT);
                    dhcp_state_ipv6.setText("连接中");
                } else if (IpConfiguration.IpAssignment.PPPOE == mIpConfigV6.ipAssignment) {
                    Toast.makeText(mContext, "IPoE/DHCP+连接中...", Toast.LENGTH_SHORT);
                    pppoe_state_ipv6.setText("连接中");
                } else if (IpConfiguration.IpAssignment.IPOE == mIpConfigV6.ipAssignment) {
                    Toast.makeText(mContext, "PPPoeE连接中...", Toast.LENGTH_SHORT);
                    ipoe_state_ipv6.setText("连接中");
                } else if (IpConfiguration.IpAssignment.STATIC == mIpConfigV6.ipAssignment) {
                    Toast.makeText(mContext, "静态IP连接中...", Toast.LENGTH_SHORT);
                    static_state_ipv6.setText("连接中");
                }
                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
                Log.i(TAG, "ipv6 message: " + message);
                switch (message) {
                    case ChEthernetManager.EVENT_CONNECT_SUCCESSED:
                        if (NetworkV4V6Utils.isV6IpOk(mEthManager)) {
                            Log.e(TAG, "onReceive: 连接成功");
                            Log.e(TAG, "onReceive: 连接成功后的模式是：" + mIpConfigV6.ipAssignment);
                            if (IpConfiguration.IpAssignment.DHCP == mIpConfigV6.ipAssignment) {
                                dhcp_state_ipv6.setText("已连接");
                            } else if (IpConfiguration.IpAssignment.PPPOE == mIpConfigV6.ipAssignment) {
                                Toast.makeText(mContext, "网络已连接", Toast.LENGTH_SHORT);
                                pppoe_state_ipv6.setText("已连接");
                            } else if (IpConfiguration.IpAssignment.IPOE == mIpConfigV6.ipAssignment) {
                                Toast.makeText(mContext, "网络已连接", Toast.LENGTH_SHORT);
                                ipoe_state_ipv6.setText("已连接");
                            } else if (IpConfiguration.IpAssignment.STATIC == mIpConfigV6.ipAssignment) {
                                Toast.makeText(mContext, "网络已连接", Toast.LENGTH_SHORT);
                                static_state_ipv6.setText("已连接");
                            }
                        }

                        break;
                    case ChEthernetManager.EVENT_CONNECT_FAILED:
                        Log.i(TAG, "dhcp v6 connect failed.");
                        int failReson = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_FAILED_REASON, -1);
                        if (failReson == ChEthernetManager.FAILED_REASON_IPOE_AUTH_FAILED
                                || failReson == ChEthernetManager.FAILED_REASON_PPPOE_AUTH_FAILED
                                || failReson == ChEthernetManager.FAILED_REASON_PPPOE_TIMEDOUT) {
                            Log.e(TAG, "onReceive: 连接失败的值未：" + failReson);
                            return;
                        }
                        Log.e(TAG, "onReceive: 现在这个值===" + failReson);
                        if (IpConfiguration.IpAssignment.DHCP == mIpConfigV6.ipAssignment) {
                            dhcp_state_ipv6.setText("未连接");
                        } else if (IpConfiguration.IpAssignment.PPPOE == mIpConfigV6.ipAssignment) {
                            pppoe_state_ipv6.setText("未连接");
                        } else if (IpConfiguration.IpAssignment.IPOE == mIpConfigV6.ipAssignment) {
                            ipoe_state_ipv6.setText("未连接");
                        } else if (IpConfiguration.IpAssignment.STATIC == mIpConfigV6.ipAssignment) {
                            static_state_ipv6.setText("未连接");
                        }
                        break;
                    default:
                        break;
                }
                return;
            }
        }
    };

    private void updateText() {
        dhcp_state_ipv6.setText("");
        pppoe_state_ipv6.setText("");
        ipoe_state_ipv6.setText("");
        static_state_ipv6.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();
        registerRecv();
    }

    private void registerRecv() {
        IntentFilter filterV6 = new IntentFilter(ChEthernetManager.IPV6_STATE_CHANGED_ACTION);
        filterV6.addAction(ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mEthReceiverIPv6, filterV6);
    }

    private void unregisterRecv() {
        if (mEthReceiverIPv6 != null) {
            mContext.unregisterReceiver(mEthReceiverIPv6);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterRecv();
        Log.e(TAG, "onDestroy: ipv6已经被销毁了没了没了");
    }

    private void connectIPv6_DHCP() {
        if (!mEthManager.getNetLinkStatus()) {
            Toast.makeText(mContext, "link down", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "link down.");
            return;
        }
//        Log.i(TAG, "in btnSAVE");

        Toast.makeText(mContext, "Connecting now,please wait a moment...", Toast.LENGTH_SHORT).show();

        IpConfiguration ipConfigurationV6 = new IpConfiguration();

        ipConfigurationV6.ipAssignment = IpConfiguration.IpAssignment.DHCP;

        mEthManager.setIpv6Configuration(ipConfigurationV6);
        Log.e(TAG, "整个ipvconfiguration是这个------" + ipConfigurationV6);
        Log.e(TAG, "connectIPV6:Stack：：： " + mStack);
        mEthManager.disconnectIpv6();
        mEthManager.enableIpv6(false);
        mEthManager.enableIpv6(true);
        mEthManager.connectIpv6();
        Log.e(TAG, "connectIPv6_DHCP: ++++++++++++++++++");

        Log.e(TAG, "connectIPv6_DHCP: 设置完IPv6的DHCP后的网络模式：" + mEthManager.getIpv6Configuration());

        Settings.Secure.putInt(mContext.getContentResolver(), "default_eth_mod", 0);


//        if (NetworkConstant.OPTION_AUTO.equals(mIpv6Option)) {
//            Log.i(TAG, "open dhcpv6 auto.");
//            mEthManager.setIpv6MOAutoState(true);
//        } else {
//            Log.i(TAG, "close dhcpv6 auto.");
//            mEthManager.setIpv6MOAutoState(false);
//        }
//        Log.i(TAG, "--> startDhcpV6 !");
//        mEthManager.setDhcpOption60(false, mEthManager.getDhcpOption60Login(),
//                mEthManager.getDhcpOption60Password());
//        mEthManager.setDhcpOption125(false, mEthManager.getDhcpOption125Info());
//
//        mEthManager.enableIpv6(false);
//
//        if (NetworkConstant.OPTION_STATELESS.equals(mIpv6Option)) {
//            Log.i(TAG, "open dhcpv6 stateless mode.");
//            mEthManager.setEthernetMode6(EthernetManager.ETHERNET_CONNECT_MODE_STATELESS);
//        } else {
//            Log.i(TAG, "open dhcpv6 dhcp mode");
//            mEthManager.setEthernetMode6(EthernetManager.ETHERNET_CONNECT_MODE_DHCP);
//        }
//        mEthManager.enableIpv6(true);
//
//        Log.e(TAG, "connectIPv6_DHCP: "+mIpConfigV6.ipAssignment );
    }


}
