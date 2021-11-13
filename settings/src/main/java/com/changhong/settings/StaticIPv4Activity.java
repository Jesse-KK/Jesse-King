package com.changhong.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.IpConfiguration;
import android.net.IpInfo;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.changhong.settings.utils.ChEthernetManager;
import com.changhong.settings.utils.CommonUtils;
import com.changhong.settings.utils.NetworkV4V6Utils;

import java.net.Inet4Address;
import java.net.InetAddress;

public class StaticIPv4Activity extends Activity implements View.OnClickListener {
    private static final String TAG = "StaticIPv4Activity";
    private EditText ip_1;
    private EditText ip_2;
    private EditText ip_3;
    private EditText ip_4;
    private EditText mask_1;
    private EditText mask_2;
    private EditText mask_3;
    private EditText mask_4;
    private EditText gateway_1;
    private EditText gateway_2;
    private EditText gateway_3;
    private EditText gateway_4;
    private EditText dns_1;
    private EditText dns_2;
    private EditText dns_3;
    private EditText dns_4;
    private EditText dns2_1;
    private EditText dns2_2;
    private EditText dns2_3;
    private EditText dns2_4;
    private String sip_1;
    private String sip_2;
    private String sip_3;
    private String sip_4;
    private String smask_1;
    private String smask_2;
    private String smask_3;
    private String smask_4;
    private String sgateway_1;
    private String sgateway_2;
    private String sgateway_3;
    private String sgateway_4;
    private String sdns_1;
    private String sdns_2;
    private String sdns_3;
    private String sdns_4;
    private String sdns2_1;
    private String sdns2_2;
    private String sdns2_3;
    private String sdns2_4;
    private String ip;
    private String mask;
    private String gateway;
    private String dns;
    private String dns2;
    private EthernetManager mEthManager;
    private String stack;
    private IpConfiguration mIpConfigV4;
    private StaticIpConfiguration mStaticConfigV4;
    private Button button_confirm__static_ipv4_connection;
    private Context mContext=StaticIPv4Activity.this;
    private LinkProperties mLinkProperties = new LinkProperties();


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
//            if (IpConfiguration.IpAssignment.DHCP != mIpConfigV4.ipAssignment) {
//                Log.i(TAG, "ipv4 mode is not dhcp, now mode: " + mIpConfigV4.ipAssignment);
//                return;
//            }

            if (ChEthernetManager.IPV4_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Log.e(TAG, "onReceive:king_king_king " + intent.getAction());
                Log.e(TAG, "onReceive: 1111");
                if (IpConfiguration.IpAssignment.STATIC == mIpConfigV4.ipAssignment) {
                    CommonUtils.showToastShort(mContext, "静态IP连接中...");
                }
                message = intent.getIntExtra(ChEthernetManager.EXTRA_ETHERNET_STATE, rel);
                Log.i(TAG, "ipv4 message: " + message);
                switch (message) {
                    case ChEthernetManager.EVENT_CONNECT_SUCCESSED:
                        if (NetworkV4V6Utils.isV4IpOk(mEthManager)) {
                            Log.e(TAG, "onReceive: 22222222");
                            if (IpConfiguration.IpAssignment.STATIC == mIpConfigV4.ipAssignment) {
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
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ipv4_static);
        initData();
        init();
        showIpv4Info();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        registerRecv();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterRecv();
    }

    private void registerRecv() {

        IntentFilter filterV4 = new IntentFilter(ChEthernetManager.IPV4_STATE_CHANGED_ACTION);
//        filterV4.addAction(ChEthernetManager.ETHERNET_STATE_CHANGED_ACTION);
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

    public void init() {
        button_confirm__static_ipv4_connection = (Button) findViewById(R.id.button_confirm__static_ipv4_connection);
        button_confirm__static_ipv4_connection.setOnClickListener(this);
        ip_1 = (EditText) findViewById(R.id.ip_1);
        ip_2 = (EditText) findViewById(R.id.ip_2);
        ip_3 = (EditText) findViewById(R.id.ip_3);
        ip_4 = (EditText) findViewById(R.id.ip_4);
        mask_1 = (EditText) findViewById(R.id.mask_1);
        mask_2 = (EditText) findViewById(R.id.mask_2);
        mask_3 = (EditText) findViewById(R.id.mask_3);
        mask_4 = (EditText) findViewById(R.id.mask_4);
        gateway_1 = (EditText) findViewById(R.id.gateway_1);
        gateway_2 = (EditText) findViewById(R.id.gateway_2);
        gateway_3 = (EditText) findViewById(R.id.gateway_3);
        gateway_4 = (EditText) findViewById(R.id.gateway_4);
        dns_1 = (EditText) findViewById(R.id.dns_1);
        dns_2 = (EditText) findViewById(R.id.dns_2);
        dns_3 = (EditText) findViewById(R.id.dns_3);
        dns_4 = (EditText) findViewById(R.id.dns_4);
        dns2_1 = (EditText) findViewById(R.id.dns2_1);
        dns2_2 = (EditText) findViewById(R.id.dns2_2);
        dns2_3 = (EditText) findViewById(R.id.dns2_3);
        dns2_4 = (EditText) findViewById(R.id.dns2_4);
    }

    private void initData() {
        mEthManager = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
//        mPppoeManager = (PppoeManager) getSystemService(Context.PPPOE_SERVICE);

        boolean isEnableIpv4 = mEthManager.isEnableIpv4();
        boolean isEnableIpv6 = mEthManager.isEnableIpv6();
        if (isEnableIpv4 && isEnableIpv6) {
            stack = "ipv4_ipv6";
        } else if (isEnableIpv6) {
            stack = "ipv6";
        } else {
            stack = "ipv4";
        }
        Log.i(TAG, "onClick: isEnableIpv4=" + isEnableIpv4 + ",isEnableIpv6=" + isEnableIpv6 + ",stack=" + stack);

        mIpConfigV4 = mEthManager.getIpv4Configuration();
        if (mIpConfigV4 == null) {
            mIpConfigV4 = new IpConfiguration();
        }
        mStaticConfigV4 = mIpConfigV4.getStaticIpConfiguration();
        Log.e(TAG, "initData: " + mStaticConfigV4);
        if (mStaticConfigV4 == null) {
            mStaticConfigV4 = new StaticIpConfiguration();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm__static_ipv4_connection:
                connectStatic();
                break;
            default:
                break;
        }
    }

    private void connectStatic() {
        if (validateIpConfigFields(mLinkProperties) == 0) {
            startStaticV4();
        }
    }

    private void startStaticV4() {
        mEthManager.enableIpv4(true);
        mEthManager.disconnectIpv4();
        Log.i(TAG, "!!-------startStaticV4-----!!");
        final DhcpInfo dhcpInfo = new DhcpInfo();
        try {
            InetAddress ipaddr = NetworkUtils.numericToInetAddress(ip);
            dhcpInfo.ipAddress = NetworkUtils.inetAddressToInt(ipaddr);

            InetAddress getwayaddr = NetworkUtils.numericToInetAddress(gateway);
            dhcpInfo.gateway = NetworkUtils.inetAddressToInt(getwayaddr);

            dhcpInfo.netmask = NetworkUtils.inetAddressToInt(NetworkUtils.numericToInetAddress(mask));

            InetAddress idns1 = NetworkUtils.numericToInetAddress(dns);
            dhcpInfo.dns1 = NetworkUtils.inetAddressToInt(idns1);

            InetAddress idns2 = NetworkUtils.numericToInetAddress(dns2);
            dhcpInfo.dns2 = NetworkUtils.inetAddressToInt(idns2);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Invalid ipv4 address");
        }
        Log.i(TAG, "set ETHERNET_CONNECT_MODE_MANUAL -> " + dhcpInfo.toString());
        // 20191127 新接口
        mIpConfigV4.staticIpConfiguration = mStaticConfigV4;
        Log.e(TAG, "startStaticV4: "+mStaticConfigV4 );
        mIpConfigV4.ipAssignment = IpConfiguration.IpAssignment.STATIC;

//        mEthManager.enableIpv4(false);
//        mEthManager.setEthernetEnabled(false);
//        mEthManager.setEthernetMode(ChEthernetManager.ETHERNET_CONNECT_MODE_MANUAL, dhcpInfo);
        mEthManager.setIpv4Configuration(mIpConfigV4);
//        mEthManager.setEthernetEnabled(true);
        mEthManager.connectIpv4();
    }


    private int validateIpConfigFields(LinkProperties linkProperties) {
        Log.i(TAG, "!!-------validateIpConfigFields-----!!");
        linkProperties.clear();
        sip_1 = ip_1.getText().toString();
        sip_2 = ip_2.getText().toString();
        sip_3 = ip_3.getText().toString();
        sip_4 = ip_4.getText().toString();
        smask_1 = mask_1.getText().toString();
        smask_2 = mask_2.getText().toString();
        smask_3 = mask_3.getText().toString();
        smask_4 = mask_4.getText().toString();
        sgateway_1 = gateway_1.getText().toString();
        sgateway_2 = gateway_2.getText().toString();
        sgateway_3 = gateway_3.getText().toString();
        sgateway_4 = gateway_4.getText().toString();
        sdns_1 = dns_1.getText().toString();
        sdns_2 = dns_2.getText().toString();
        sdns_3 = dns_3.getText().toString();
        sdns_4 = dns_4.getText().toString();
        sdns2_1 = dns2_1.getText().toString();
        sdns2_2 = dns2_2.getText().toString();
        sdns2_3 = dns2_3.getText().toString();
        sdns2_4 = dns2_4.getText().toString();
        ip = sip_1 + "." + sip_2 + "." + sip_3 + "." + sip_4;
        mask = smask_1 + "." + smask_2 + "." + smask_3 + "." + smask_4;
        gateway = sgateway_1 + "." + sgateway_2 + "." + sgateway_3 + "." + sgateway_4;
        dns = sdns_1 + "." + sdns_2 + "." + sdns_3 + "." + sdns_4;
        dns2 = sdns2_1 + "." + sdns2_2 + "." + sdns2_3 + "." + sdns2_4;
        if (TextUtils.isEmpty(ip)) {
            String ipErr = mContext.getString(R.string.eth_ip_settings_invalid_ip_address);
            CommonUtils.showToastShort(mContext, ipErr);
            return R.string.eth_ip_settings_invalid_ip_address;
        }

        InetAddress inetAddr = null;
        try {
            inetAddr = NetworkUtils.numericToInetAddress(ip);
            Log.e(TAG, "validateIpConfigFields: "+inetAddr.getHostAddress());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "validateIpConfigFields: qqqqqq" );
            String ipErr = mContext.getString(R.string.eth_ip_settings_invalid_ip_address);
            CommonUtils.showToastShort(mContext, ipErr);
            return R.string.eth_ip_settings_invalid_ip_address;
        }

        if (inetAddr instanceof Inet4Address) {
            Log.d(TAG, "ipv4 address check ok!");
        } else {
            String ipErr = mContext.getString(R.string.eth_ip_settings_invalid_ip_address);
            CommonUtils.showToastShort(mContext, ipErr);
            return R.string.eth_ip_settings_invalid_ip_address;
        }

        int networkPrefixLength = -1;
        try {
            networkPrefixLength = NetworkUtils.netmaskIntToPrefixLength(
                    NetworkUtils.inetAddressToInt(NetworkUtils.numericToInetAddress(mask)));
            if (networkPrefixLength < 0 || networkPrefixLength > 32) {
                String ipErr = mContext.getString(R.string.eth_ip_settings_invalid_network_prefix_length);
                CommonUtils.showToastShort(mContext, ipErr);
                return R.string.eth_ip_settings_invalid_network_prefix_length;
            }
            LinkAddress linkAddress = new LinkAddress(inetAddr, networkPrefixLength);
            linkProperties.addLinkAddress(linkAddress);
            mStaticConfigV4.ipAddress = linkAddress;
            Log.e(TAG, "validateIpConfigFieldsipAddress: "+ mStaticConfigV4.ipAddress);
        } catch (NumberFormatException e) {
            // Set the hint as default after user types in ip address
            mask_1.setText("255");
            mask_2.setText("255");
            mask_3.setText("255");
            mask_4.setText("0");
        }

        if (TextUtils.isEmpty(gateway)) {
            try {
                //Extract a default gateway from IP address
                InetAddress netPart = NetworkUtils.getNetworkPart(inetAddr, networkPrefixLength);
                byte[] addr = netPart.getAddress();
                addr[addr.length - 1] = 1;
                String addre=InetAddress.getByAddress(addr).getHostAddress();
                String[] sAddr=addre.split("//.");
                gateway_1.setText(sAddr[0]);
                gateway_2.setText(sAddr[1]);
                gateway_3.setText(sAddr[2]);
                gateway_4.setText(sAddr[3]);
                mStaticConfigV4.gateway = InetAddress.getByAddress(addr);
            } catch (RuntimeException ee) {
            } catch (java.net.UnknownHostException u) {
            }
        } else {
            InetAddress gatewayAddr = null;
            try {
                gatewayAddr = NetworkUtils.numericToInetAddress(gateway);
            } catch (IllegalArgumentException e) {
                String ipErr = mContext.getString(R.string.eth_ip_settings_invalid_gateway);
                CommonUtils.showToastShort(mContext, ipErr);
                return R.string.eth_ip_settings_invalid_gateway;
            }
            if (gatewayAddr instanceof Inet4Address) {
                Log.d(TAG, "ipv4 gateway check ok!");
            } else {
                String ipErr = mContext.getString(R.string.eth_ip_settings_invalid_gateway);
                CommonUtils.showToastShort(mContext, ipErr);
                return R.string.eth_ip_settings_invalid_gateway;
            }
            linkProperties.addRoute(new RouteInfo(gatewayAddr));
            mStaticConfigV4.gateway = gatewayAddr;
        }

        InetAddress dnsAddr = null;

        if (TextUtils.isEmpty(dns)) {
            //If everything else is valid, provide hint as a default option
            dns_1.setText("8");
            dns_2.setText("8");
            dns_3.setText("8");
            dns_4.setText("8");
        } else {
            try {
                dnsAddr = NetworkUtils.numericToInetAddress(dns);
            } catch (IllegalArgumentException e) {
                String ipErr = mContext.getString(R.string.eth_ip_settings_invalid_dns);
                CommonUtils.showToastShort(mContext, ipErr);
                return R.string.eth_ip_settings_invalid_dns;
            }
            if (dnsAddr instanceof Inet4Address) {
                Log.d(TAG, "ipv4 dns1 check ok!");
            } else {
                String ipErr = mContext.getString(R.string.eth_ip_settings_invalid_dns);
                CommonUtils.showToastShort(mContext, ipErr);
                return R.string.eth_ip_settings_invalid_dns;
            }
//            linkProperties.addDns(dnsAddr);
            mStaticConfigV4.dnsServers.clear();
            mStaticConfigV4.dnsServers.add(dnsAddr);
        }

        if (dns2_1.length() > 0 && dns2_2.length() > 0 && dns2_3.length() > 0 && dns2_4.length() > 0) {
            try {
                dnsAddr = NetworkUtils.numericToInetAddress(dns2);
            } catch (IllegalArgumentException e) {
                String ipErr = mContext.getString(R.string.eth_ip_settings_invalid_dns);
                CommonUtils.showToastShort(mContext, ipErr);
                return R.string.eth_ip_settings_invalid_dns;
            }
            if (dnsAddr instanceof Inet4Address) {
                Log.d(TAG, "ipv4 dns2 check ok!");
            } else {
                String ipErr = mContext.getString(R.string.eth_ip_settings_invalid_dns);
                CommonUtils.showToastShort(mContext, ipErr);
                return R.string.eth_ip_settings_invalid_dns;
            }
//            linkProperties.addDns(dnsAddr);
            mStaticConfigV4.dnsServers.add(dnsAddr);
        }
        return 0;
    }


    private void showIpv4Info() {
        Log.i(TAG, "!!-------showIpv4Info-----!!");
        if (!mEthManager.isEnableIpv4()) {
            Log.i(TAG, "showIpv4Info: !mEthManager.isEnableIpv4()");
            ip_1.setText("");
            ip_2.setText("");
            ip_3.setText("");
            ip_4.setText("");
            mask_1.setText("");
            mask_2.setText("");
            mask_3.setText("");
            mask_4.setText("");
            gateway_1.setText("");
            gateway_2.setText("");
            gateway_3.setText("");
            gateway_4.setText("");
            dns_1.setText("");
            dns_2.setText("");
            dns_3.setText("");
            dns_4.setText("");
            dns2_1.setText("");
            dns2_2.setText("");
            dns2_3.setText("");
            dns2_4.setText("");
            return;
        }

//        DhcpInfo dinfo = null;
        String IP = "", Gateway = "", Dns1 = "", Dns2 = "";
        String Mask = "";
//        if (Settings.Secure.getInt(getContentResolver(), "default_eth_mod", 0) == 1) {
////            dinfo = mEthManager.getDhcpInfo();
////        } else if (Settings.Secure.getInt(getContentResolver(), "default_eth_mod", 0) == 2) {
////            dinfo = mPppoeManager.getDhcpInfo();
////            Log.i(TAG, "getEthernetInfo: isPPPOE!!!!!!");
////        } else {
////            dinfo = mEthManager.getDhcpInfo();
////        }
        IpInfo dinfo = mEthManager.getIPv4Info();
        if (dinfo != null) {
            Log.i(TAG, "dinfo.ipAddress：" + dinfo + " formated:" + dinfo.ip);
            IP = dinfo.ip.getHostAddress();
            Mask = NetworkUtils.intToInetAddress(NetworkUtils.prefixLengthToNetmaskInt(dinfo.mask)).getHostAddress();
            Log.e(TAG, "showIpv4InfoMask: " + Mask);
            Gateway = dinfo.gateway.getHostAddress();
            if (dinfo.dnsServers.size() >= 1) {
                Dns1 = dinfo.dnsServers.get(0).getHostAddress();
            }
            if (dinfo.dnsServers.size() >= 2) {
                Dns2 = dinfo.dnsServers.get(1).getHostAddress();
            }
        } else {
            if (IpConfiguration.IpAssignment.DHCP != mIpConfigV4.ipAssignment) {
                IP = dinfo.ip.getHostAddress();
                Gateway = "";
                Dns1 = "";
                Dns2 = "";
                Mask = "255.255.255.0";
                String defIpAddress = "0.0.0.0";
                Log.i(TAG, "showIpv4Info ipAddress = " + IP);
                if (IP != null && IP.length() > 0 && !defIpAddress.equals(IP)) { // set ip from DB
                    Gateway = dinfo.gateway.getHostAddress();
                    Mask = NetworkUtils.intToInetAddress(NetworkUtils.prefixLengthToNetmaskInt(dinfo.mask)).getHostAddress();
                    Dns1 = dinfo.dnsServers.get(0).getHostAddress();
                    if (dinfo.dnsServers.size() >= 2) {
                        Dns2 = dinfo.dnsServers.get(1).getHostAddress();
                    }
                }
            }
        }
        Log.i(TAG, "showIpv4Info: IP=" + IP + ",Gateway=" + Gateway + ",Mask=" + Mask + ",Dns1=" + Dns1 + ",Dns2=" + Dns2);
        String[] sIP = IP.split("\\.");
        String[] sGateway = Gateway.split("\\.");
        String[] sMask = Mask.split("\\.");
        String[] sDns1 = Dns1.split("\\.");
        String[] sDns2 = Dns2.split("\\.");
        Log.e(TAG, "sIP.length: " + sIP.length);
        if (sIP.length == 4) {
            ip_1.setText(sIP[0]);
            ip_2.setText(sIP[1]);
            ip_3.setText(sIP[2]);
            ip_4.setText(sIP[3]);
            Log.e(TAG, "sIP: " + sIP[0]);
            Log.e(TAG, "sIP: " + sIP[1]);
            Log.e(TAG, "sIP: " + sIP[2]);
            Log.e(TAG, "sIP: " + sIP[3]);

        }
        if (sGateway.length == 4) {
            gateway_1.setText(sGateway[0]);
            gateway_2.setText(sGateway[1]);
            gateway_3.setText(sGateway[2]);
            gateway_4.setText(sGateway[3]);
        }
        if (sMask.length == 4) {
            mask_1.setText(sMask[0]);
            mask_2.setText(sMask[1]);
            mask_3.setText(sMask[2]);
            mask_4.setText(sMask[3]);
        }
        if (sDns1.length == 4) {
            dns_1.setText(sDns1[0]);
            dns_2.setText(sDns1[1]);
            dns_3.setText(sDns1[2]);
            dns_4.setText(sDns1[3]);
        }
        if (sDns2.length == 4) {
            dns2_1.setText(sDns2[0]);
            dns2_2.setText(sDns2[1]);
            dns2_3.setText(sDns2[2]);
            dns2_4.setText(sDns2[3]);
        }
    }
}
