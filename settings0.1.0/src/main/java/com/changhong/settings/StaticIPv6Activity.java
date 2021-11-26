package com.changhong.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.IpConfiguration;
import android.net.IpInfo;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.StaticIpConfiguration;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
//import android.service.gatekeeper.GateKeeperResponse;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.changhong.settings.utils.ChEthernetManager;
import com.changhong.settings.utils.CommonUtils;
import com.changhong.settings.utils.NetworkV4V6Utils;

import java.net.InetAddress;

import static com.changhong.settings.utils.NetworkV4V6Utils.getNetInfo;

public class StaticIPv6Activity extends Activity implements View.OnClickListener {
    private static final String TAG = "StaticIPv6Activity";
    private EditText ip_1, ip_2, ip_3, ip_4, ip_5, ip_6, ip_7, ip_8;
    private EditText length_mask;
    private EditText gateway_1, gateway_2, gateway_3, gateway_4, gateway_5, gateway_6, gateway_7, gateway_8;
    private EditText dns_1, dns_2, dns_3, dns_4, dns_5, dns_6, dns_7, dns_8;
    private EditText spare_dns1, spare_dns2, spare_dns3, spare_dns4, spare_dns5, spare_dns6, spare_dns7, spare_dns8;
    private Button button_confirm__static_ipv6_connection;
    private String sip_1, sip_2, sip_3, sip_4, sip_5, sip_6, sip_7, sip_8;
    private String slength_mask;
    private String sgateway_1, sgateway_2, sgateway_3, sgateway_4, sgateway_5, sgateway_6, sgateway_7, sgateway_8;
    private String sdns_1, sdns_2, sdns_3, sdns_4, sdns_5, sdns_6, sdns_7, sdns_8;
    private String sdns2_1, sdns2_2, sdns2_3, sdns2_4, sdns2_5, sdns2_6, sdns2_7, sdns2_8;
    private String ip;
    private String mask;
    private String gateway;
    private String dns;
    private String dns2;
    private EthernetManager mEthManager;
    private IpConfiguration mIpConfigV6;
    private StaticIpConfiguration mStaticConfigV6;
    private NetworkV4V6Utils.NetInfo mNetInfoV6 = null;
    private Context mContext = StaticIPv6Activity.this;


    private final BroadcastReceiver mEthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIpConfigV6 = mEthManager.getIpv6Configuration();
            Log.i(TAG, "now mode: 现在的状态是：" + mIpConfigV6.ipAssignment);
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            int message;
            int rel = -1;
            if (IpConfiguration.IpAssignment.STATIC != mIpConfigV6.ipAssignment) {
                Log.i(TAG, "ipv4 mode is not static, now mode: " + mIpConfigV6.ipAssignment);
                return;
            }

            if (ChEthernetManager.IPV6_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Log.e(TAG, "onReceive:king_king_king " + intent.getAction());
                Log.e(TAG, "onReceive: Jesse_Pinkman");
                if (IpConfiguration.IpAssignment.STATIC == mIpConfigV6.ipAssignment) {
                    CommonUtils.showToastShort(mContext, "静态IP连接中...");
                }
                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
                Log.i(TAG, "ipv4 message: " + message);
                switch (message) {
                    case ChEthernetManager.EVENT_CONNECT_SUCCESSED:
                        if (NetworkV4V6Utils.isV6IpOk(mEthManager)) {
                            Log.e(TAG, "onReceive:King king  KING");
                            if (IpConfiguration.IpAssignment.STATIC == mIpConfigV6.ipAssignment) {
                                CommonUtils.showToastShort(mContext, "网络已连接");
                            }
                        }
                        break;
                    default:
                        break;
                }
                return;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_confirm__static_ipv6_connection:
                connectStaticIpv6();
                finish();
                break;
            default:
                break;
        }

    }

    private void connectStaticIpv6() {
        if (validateIpConfigFields() != 0) {
            startStaticIpv6();
        }

    }

    private int validateIpConfigFields() {
        Log.i(TAG, "!!-------validateIpConfigFields-----!!");
        sip_1 = ip_1.getText().toString();
        sip_2 = ip_2.getText().toString();
        sip_3 = ip_3.getText().toString();
        sip_4 = ip_4.getText().toString();
        sip_5 = ip_5.getText().toString();
        sip_6 = ip_6.getText().toString();
        sip_7 = ip_7.getText().toString();
        sip_8 = ip_8.getText().toString();
        slength_mask = length_mask.getText().toString();
        sgateway_1 = gateway_1.getText().toString();
        sgateway_2 = gateway_2.getText().toString();
        sgateway_3 = gateway_3.getText().toString();
        sgateway_4 = gateway_4.getText().toString();
        sgateway_5 = gateway_5.getText().toString();
        sgateway_6 = gateway_6.getText().toString();
        sgateway_7 = gateway_7.getText().toString();
        sgateway_8 = gateway_8.getText().toString();
        sdns_1 = dns_1.getText().toString();
        sdns_2 = dns_2.getText().toString();
        sdns_3 = dns_3.getText().toString();
        sdns_4 = dns_4.getText().toString();
        sdns_5 = dns_5.getText().toString();
        sdns_6 = dns_6.getText().toString();
        sdns_7 = dns_7.getText().toString();
        sdns_8 = dns_8.getText().toString();

        sdns2_1 = spare_dns1.getText().toString();
        sdns2_2 = spare_dns2.getText().toString();
        sdns2_3 = spare_dns3.getText().toString();
        sdns2_4 = spare_dns4.getText().toString();
        sdns2_5 = spare_dns5.getText().toString();
        sdns2_6 = spare_dns6.getText().toString();
        sdns2_7 = spare_dns7.getText().toString();
        sdns2_8 = spare_dns8.getText().toString();
        ip = sip_1 + ":" + sip_2 + ":" + sip_3 + ":" + sip_4
                + ":" + sip_5 + ":" + sip_6 + ":" + sip_7 + ":" + sip_8;
        Log.e(TAG, "validateIpConfigFields: 当前的ip为："+ip );
        mask = slength_mask;
        gateway = sgateway_1 + ":" + sgateway_2 + ":" + sgateway_3 + ":" + sgateway_4
                + ":" + sgateway_5 + ":" + sgateway_6 +  sgateway_7 + ":" + sgateway_8;
        Log.e(TAG, "validateIpConfigFields: 当前的网关地址为："+gateway );
        dns = sdns_1 + ":" + sdns_2 + ":" + sdns_3 + ":" + sdns_4
                + ":" + sdns_5 + ":" + sdns_6 +  sdns_7 + ":" + sdns_8;
        dns2 = sdns2_1 + ":" + sdns2_2 + ":" + sdns2_3 + ":" + sdns2_4
                + ":" + sdns2_5 + ":" + sdns2_6 +  sdns2_7 + ":" + sdns2_8;
        NetworkV4V6Utils.CheckIpResult result;
        mNetInfoV6 = getNetInfo();
        mNetInfoV6.ip = ip;
        mNetInfoV6.mask = mask;
        mNetInfoV6.gateway = gateway;
        mNetInfoV6.dns1 = dns;
        mNetInfoV6.dns2 = dns2;
        if (mNetInfoV6.dns2.isEmpty()) {
            mNetInfoV6.dns2 = "0:0:0:0:0:0:0:0";
            Log.e(TAG, "validateIpConfigFields:地址为空： " + mNetInfoV6.dns2);
        }
        if (mNetInfoV6.dns2.equals(":::::::")) {
            mNetInfoV6.dns2 = "0:0:0:0:0:0:0:0";
            Log.e(TAG, "validateIpConfigFields:格式正确的dns： " + mNetInfoV6.dns2);
        }
        result = NetworkV4V6Utils.validateIpv6ConfigFields(mNetInfoV6);
        switch (result){
            case IP_ERR:
                Toast.makeText(mContext, getString(R.string.eth_ip_error), Toast.LENGTH_SHORT).show();
                break;
            case GATEWAY_ERR:
                Toast.makeText(mContext, getString(R.string.eth_gateway_error), Toast.LENGTH_SHORT).show();
                break;
            case MASK_ERR:
                Toast.makeText(mContext, getString(R.string.eth_prefix_error_v4), Toast.LENGTH_SHORT).show();
                break;
            case PREFIX_ERR:
                Toast.makeText(mContext, getString(R.string.eth_prefix_error_v6), Toast.LENGTH_SHORT).show();
                break;
            case DNS1_ERR:
                Toast.makeText(mContext, getString(R.string.eth_dns1_error), Toast.LENGTH_SHORT).show();
                break;
            case DNS2_ERR:
                Toast.makeText(mContext, getString(R.string.eth_dns2_error), Toast.LENGTH_SHORT).show();
                break;
            case CHECK_OK:
                return -1;
            default:
                return 0;
        }
        return 0;
    }

    private void startStaticIpv6() {
        Log.i(TAG, "into setStaticIP: ");

        InetAddress ip = NetworkUtils.numericToInetAddress(mNetInfoV6.ip);
        InetAddress gateway = NetworkUtils.numericToInetAddress(mNetInfoV6.gateway);
        InetAddress dns1 = NetworkUtils.numericToInetAddress(mNetInfoV6.dns1);
        InetAddress dns2 = NetworkUtils.numericToInetAddress(mNetInfoV6.dns2);
        Log.i(TAG, "---startStaticV6 ,ip="+ip+", dns1:" + dns1 + " dns2:" + dns2);

        IpConfiguration ipv6Config = new IpConfiguration();
        ipv6Config.ipAssignment = IpConfiguration.IpAssignment.STATIC;
        ipv6Config.staticIpConfiguration = new StaticIpConfiguration();
        int prefixV6 = mNetInfoV6.prefix;
        ipv6Config.staticIpConfiguration.ipAddress = new LinkAddress(ip, prefixV6);
        ipv6Config.staticIpConfiguration.gateway = gateway;
        //此处添加dns需先添加dns2,再添加dns1，否则获取的时候会先获取到dns2
        ipv6Config.staticIpConfiguration.dnsServers.add(dns2);
        ipv6Config.staticIpConfiguration.dnsServers.add(dns1);
        mEthManager.setIpv6Configuration(ipv6Config);


        //需要先断开再连接，否则静态ip设置不进去
        synchronized (this){
            mEthManager.disconnectIpv6();
        }
        mEthManager.enableIpv6(false);
        mEthManager.enableIpv6(true);
        mEthManager.connectIpv6();
        Log.e(TAG, "startStaticIpv6: 设置完静态ipv6的网络模式：111"+ mIpConfigV6);
        Log.e(TAG, "startStaticIpv6: 连接完静态ipv6的ipconfig：111"+ ipv6Config);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ipv6_static);
        initView();
        initData();
        showIpv6Info();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerRecv();
    }

    private void registerRecv() {
        IntentFilter filterV6 = new IntentFilter(ChEthernetManager.IPV6_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mEthReceiver, filterV6);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterRecv();
    }

    private void unregisterRecv() {
        if (mEthReceiver != null) {
            mContext.unregisterReceiver(mEthReceiver);
        }
    }

    private void initView() {
        ip_1 = (EditText) findViewById(R.id.ip_1);
        ip_2 = (EditText) findViewById(R.id.ip_2);
        ip_3 = (EditText) findViewById(R.id.ip_3);
        ip_4 = (EditText) findViewById(R.id.ip_4);
        ip_5 = (EditText) findViewById(R.id.ip_5);
        ip_6 = (EditText) findViewById(R.id.ip_6);
        ip_7 = (EditText) findViewById(R.id.ip_7);
        ip_8 = (EditText) findViewById(R.id.ip_8);
        length_mask = (EditText) findViewById(R.id.length_mask);
        gateway_1 = (EditText) findViewById(R.id.gateway_1);
        gateway_2 = (EditText) findViewById(R.id.gateway_2);
        gateway_3 = (EditText) findViewById(R.id.gateway_3);
        gateway_4 = (EditText) findViewById(R.id.gateway_4);
        gateway_5 = (EditText) findViewById(R.id.gateway_5);
        gateway_6 = (EditText) findViewById(R.id.gateway_6);
        gateway_7 = (EditText) findViewById(R.id.gateway_7);
        gateway_8 = (EditText) findViewById(R.id.gateway_8);
        dns_1 = (EditText) findViewById(R.id.dns_1);
        dns_2 = (EditText) findViewById(R.id.dns_2);
        dns_3 = (EditText) findViewById(R.id.dns_3);
        dns_4 = (EditText) findViewById(R.id.dns_4);
        dns_5 = (EditText) findViewById(R.id.dns_5);
        dns_6 = (EditText) findViewById(R.id.dns_6);
        dns_7 = (EditText) findViewById(R.id.dns_7);
        dns_8 = (EditText) findViewById(R.id.dns_8);
        spare_dns1 = (EditText) findViewById(R.id.spare_dns1);
        spare_dns2 = (EditText) findViewById(R.id.spare_dns2);
        spare_dns3 = (EditText) findViewById(R.id.spare_dns3);
        spare_dns4 = (EditText) findViewById(R.id.spare_dns4);
        spare_dns5 = (EditText) findViewById(R.id.spare_dns5);
        spare_dns6 = (EditText) findViewById(R.id.spare_dns6);
        spare_dns7 = (EditText) findViewById(R.id.spare_dns7);
        spare_dns8 = (EditText) findViewById(R.id.spare_dns8);
        button_confirm__static_ipv6_connection = (Button) findViewById(R.id.button_confirm__static_ipv6_connection);
        button_confirm__static_ipv6_connection.setOnClickListener(this);
    }

    private void initData() {
        mEthManager = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
        mIpConfigV6 = mEthManager.getIpv6Configuration();
        if (mIpConfigV6 == null) {
            mIpConfigV6 = new IpConfiguration();
        }
        mStaticConfigV6 = mIpConfigV6.getStaticIpConfiguration();
        Log.e(TAG, "initData: " + mStaticConfigV6);
        if (mStaticConfigV6 == null) {
            mStaticConfigV6 = new StaticIpConfiguration();
        }
    }

    private void showIpv6Info() {
        Log.i(TAG, "!!-------showIpv6Info-----!!");
        if (!mEthManager.isEnableIpv6()) {
            Log.i(TAG, "showIpv4Info: !mEthManager.isEnableIpv6()");
            ip_1.setText("");
            ip_2.setText("");
            ip_3.setText("");
            ip_4.setText("");
            ip_5.setText("");
            ip_6.setText("");
            ip_7.setText("");
            ip_8.setText("");
            length_mask.setText("");
            gateway_1.setText("");
            gateway_2.setText("");
            gateway_3.setText("");
            gateway_4.setText("");
            gateway_5.setText("");
            gateway_6.setText("");
            gateway_7.setText("");
            gateway_8.setText("");
            dns_1.setText("");
            dns_2.setText("");
            dns_3.setText("");
            dns_4.setText("");
            dns_5.setText("");
            dns_6.setText("");
            dns_7.setText("");
            dns_8.setText("");
            spare_dns1.setText("");
            spare_dns2.setText("");
            spare_dns3.setText("");
            spare_dns4.setText("");
            spare_dns5.setText("");
            spare_dns6.setText("");
            spare_dns7.setText("");
            spare_dns8.setText("");
            return;
        }
        String IP = "", Gateway = "", Dns1 = "", Dns2 = "";
        int Mask = 0;
        IpInfo info = mEthManager.getIPv6Info();
        if (info != null) {
            Log.e(TAG, "showIpv6Info:NOT NULL:-------------");
            Log.e(TAG, "showIpv6Info: " + info);
            Log.e(TAG, "showIpv6Info: ip地址为：" + info.ip);
            IP = info.ip.getHostAddress();
            Mask = info.mask;
            Log.e(TAG, "showIpv6Info: Mask的前缀长度为：" + Mask);
            Gateway = info.gateway.getHostAddress();
            if (info.dnsServers.size() >= 1) {
                Dns1 = info.dnsServers.get(0).getHostAddress();
            }
            if (info.dnsServers.size() >= 2) {
                Dns2 = info.dnsServers.get(1).getHostAddress();
            }
        } else {
            if (mIpConfigV6.ipAssignment != IpConfiguration.IpAssignment.DHCP) {
                IP = info.ip.getHostAddress();
                Log.e(TAG, "showIpv6Info: 静态IP为：——————"+IP );
                Gateway = "";
                Mask = 0;
                Dns1 = "";
                Dns2 = "";
                String defIpAddress = "0:0:0:0:0:0:0:0";
                if (IP != null && IP.length() > 0 && !(defIpAddress.equals(IP))) {
                    Gateway = info.gateway.getHostAddress();
                    Mask = info.mask;
                    Dns1 = info.dnsServers.get(0).getHostAddress();
                    if (info.dnsServers.size() >= 2) {
                        Dns2 = info.dnsServers.get(1).getHostAddress();
                    }
                }
            }
        }
        Log.i(TAG, "showIpv4Info: IP=" + IP + ",Gateway=" +
                Gateway + ",Mask=" + Mask + ",Dns1=" + Dns1 + ",Dns2=" + Dns2);
        String[] sIP = IP.split(":");
        String[] sGateway = Gateway.split(":");
        String[] sDns1 = Dns1.split(":");
        String[] sDns2 = Dns2.split(":");
        Log.e(TAG, "sIP.length: " + sIP.length);
        if (sIP.length == 8) {
            ip_1.setText(sIP[0]);
            ip_2.setText(sIP[1]);
            ip_3.setText(sIP[2]);
            ip_4.setText(sIP[3]);
            ip_5.setText(sIP[4]);
            ip_6.setText(sIP[5]);
            ip_7.setText(sIP[6]);
            ip_8.setText(sIP[7]);
            Log.e(TAG, "sIP: " + sIP[0]);
            Log.e(TAG, "sIP: " + sIP[1]);
            Log.e(TAG, "sIP: " + sIP[2]);
            Log.e(TAG, "sIP: " + sIP[3]);
            Log.e(TAG, "sIP: " + sIP[4]);
            Log.e(TAG, "sIP: " + sIP[5]);
            Log.e(TAG, "sIP: " + sIP[6]);
            Log.e(TAG, "sIP: " + sIP[7]);
        }
        if (Mask != 0) {
            length_mask.setText(String.valueOf(Mask));
            Log.e(TAG, "掩码长度设置进去了，MASK=" + Mask);
        }
        if (sGateway.length == 8) {
            gateway_1.setText(sGateway[0]);
            gateway_2.setText(sGateway[1]);
            gateway_3.setText(sGateway[2]);
            gateway_4.setText(sGateway[3]);
            gateway_5.setText(sGateway[4]);
            gateway_6.setText(sGateway[5]);
            gateway_7.setText(sGateway[6]);
            gateway_8.setText(sGateway[7]);
        }
        if (sDns1.length == 8) {
            dns_1.setText(sDns1[0]);
            dns_2.setText(sDns1[1]);
            dns_3.setText(sDns1[2]);
            dns_4.setText(sDns1[3]);
            dns_5.setText(sDns1[4]);
            dns_6.setText(sDns1[5]);
            dns_7.setText(sDns1[6]);
            dns_8.setText(sDns1[7]);
        }
        if (sDns2.length == 8) {
            spare_dns1.setText(sDns2[0]);
            spare_dns2.setText(sDns2[1]);
            spare_dns3.setText(sDns2[2]);
            spare_dns4.setText(sDns2[3]);
            spare_dns5.setText(sDns2[4]);
            spare_dns6.setText(sDns2[5]);
            spare_dns7.setText(sDns2[6]);
            spare_dns8.setText(sDns2[7]);
        }

    }
}
