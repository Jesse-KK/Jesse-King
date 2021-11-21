package com.changhong.settings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.changhong.settings.utils.CommonUtils;

import java.net.Inet6Address;
import java.net.InetAddress;

/*
* 这个类没有用到
* */
public class StaticIPv6Activity_king extends Activity implements View.OnClickListener {

    private static final String TAG = "StaticIPv6Activity_king";
    private EditText ip_1, ip_2, ip_3, ip_4, ip_5, ip_6, ip_7, ip_8;
    private EditText length_mask;
    private EditText gateway_1, gateway_2, gateway_3, gateway_4, gateway_5, gateway_6, gateway_7, gateway_8;
    private EditText dns_1, dns_2, dns_3, dns_4, dns_5, dns_6, dns_7, dns_8;
    private EditText spare_dns1, spare_dns2, spare_dns3, spare_dns4, spare_dns5, spare_dns6, spare_dns7, spare_dns8;
    private Button button_confirm__static_ipv6_connection;
    private EthernetManager mEthManager;
    private IpConfiguration mIpConfigV6;
    private EditText[] etIpList, etGatewayList, etDns1List, etDns2List;
    private String mDefaultIp = "::";
    private String mDefaultIp1 = ":::::::";
    private Context mContext = StaticIPv6Activity_king.this;
    private static final String PPPOE_INTERFACE = "ppp0";


    private BroadcastReceiver mEthReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null == action) {
                Log.i(TAG, "action is null, rerurn.");
                return;
            }

            Log.i(TAG, "action: " + action);
            if (IpConfiguration.IpAssignment.STATIC != mIpConfigV6.ipAssignment) {
                Log.i(TAG, "not static v6 mode, now mode: " + mIpConfigV6.ipAssignment);
                return;
            }

            int message = -1;
            int rel = -1;
            String connectMsg = null;
            if (EthernetManager.IPV6_STATE_CHANGED_ACTION.equals(action)
                    || EthernetManager.ETHERNET_STATE_CHANGED_ACTION.equals(action)) {
                message = intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE, rel);
                Log.i(TAG, "message: " + message);
                switch (message) {
                    case EthernetManager.EVENT_PHY_LINK_DOWN:
                        connectMsg = mContext.getResources().getString(R.string.eth_link_down);
                        CommonUtils.showToastShort(mContext, connectMsg);
                        break;
                    case EthernetManager.EVENT_IPV6_CONNECT_SUCCESSED:
                    case EthernetManager.EVENT_STATIC6_CONNECT_SUCCESSED:
                        connectMsg = mContext.getResources().getString(R.string.eth_static_connect_success);
                        CommonUtils.showToastShort(mContext, connectMsg);
                        break;
                    case EthernetManager.EVENT_IPV6_CONNECT_FAILED:
                    case EthernetManager.EVENT_STATIC6_CONNECT_FAILED:
                        connectMsg = mContext.getResources().getString(R.string.eth_status_connect_failed);
                        CommonUtils.showToastShort(mContext, connectMsg);
                        break;
                }

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "-------- onDestroy --------");

        mContext.unregisterReceiver(mEthReceiver);
    }

    private void registerRecv() {
        Log.i(TAG, "-------- registerRecv --------");
        IntentFilter filter = new IntentFilter(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        filter.addAction(EthernetManager.IPV6_STATE_CHANGED_ACTION);

        mContext.registerReceiver(mEthReceiver, filter);
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ipv6_static);
        initView();
        initData();
        initIp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerRecv();
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

        TextChangeListen[] editWatcher = new TextChangeListen[32];

        for (int i = 0; i < 8; i++) {
            editWatcher[i] = new TextChangeListen(etIpList[i]);
            etIpList[i].addTextChangedListener(editWatcher[i]);
        }

        for (int i = 8; i < 16; i++) {
            editWatcher[i] = new TextChangeListen(etGatewayList[i - 8]);
            etGatewayList[i - 8].addTextChangedListener(editWatcher[i]);
        }

        for (int i = 16; i < 24; i++) {
            editWatcher[i] = new TextChangeListen(etDns1List[i - 16]);
            etDns1List[i - 16].addTextChangedListener(editWatcher[i]);
        }

        for (int i = 24; i < 32; i++) {
            editWatcher[i] = new TextChangeListen(etDns2List[i - 24]);
            etDns2List[i - 24].addTextChangedListener(editWatcher[i]);
        }

        button_confirm__static_ipv6_connection = (Button) findViewById(R.id.button_confirm__static_ipv6_connection);
        button_confirm__static_ipv6_connection.setOnClickListener(this);

    }

    public class TextChangeListen implements TextWatcher {

        public EditText editText;

        public TextChangeListen(EditText editText) {
            super();
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.length() == 4) {
                try {
                    if (Integer.parseInt(s.toString(), 16) <= 65535) {
                        if (this.editText == ip_1) {
                            ip_2.requestFocus();
                        } else if (this.editText == ip_2) {
                            ip_3.requestFocus();
                        } else if (this.editText == ip_3) {
                            ip_4.requestFocus();
                        } else if (this.editText == ip_4) {
                            ip_5.requestFocus();
                        } else if (this.editText == ip_5) {
                            ip_6.requestFocus();
                        } else if (this.editText == ip_6) {
                            ip_7.requestFocus();
                        } else if (this.editText == ip_7) {
                            ip_8.requestFocus();
                        } else if (this.editText == gateway_1) {
                            gateway_2.requestFocus();
                        } else if (this.editText == gateway_2) {
                            gateway_3.requestFocus();
                        } else if (this.editText == gateway_3) {
                            gateway_4.requestFocus();
                        } else if (this.editText == gateway_4) {
                            gateway_5.requestFocus();
                        } else if (this.editText == gateway_5) {
                            gateway_6.requestFocus();
                        } else if (this.editText == gateway_6) {
                            gateway_7.requestFocus();
                        } else if (this.editText == gateway_7) {
                            gateway_8.requestFocus();
                        } else if (this.editText == dns_1) {
                            dns_2.requestFocus();
                        } else if (this.editText == dns_2) {
                            dns_3.requestFocus();
                        } else if (this.editText == dns_3) {
                            dns_4.requestFocus();
                        } else if (this.editText == dns_4) {
                            dns_5.requestFocus();
                        } else if (this.editText == dns_5) {
                            dns_6.requestFocus();
                        } else if (this.editText == dns_6) {
                            dns_7.requestFocus();
                        } else if (this.editText == dns_7) {
                            dns_8.requestFocus();
                        } else if (this.editText == spare_dns1) {
                            spare_dns2.requestFocus();
                        } else if (this.editText == spare_dns2) {
                            spare_dns3.requestFocus();
                        } else if (this.editText == spare_dns3) {
                            spare_dns4.requestFocus();
                        } else if (this.editText == spare_dns5) {
                            spare_dns5.requestFocus();
                        } else if (this.editText == spare_dns5) {
                            spare_dns6.requestFocus();
                        } else if (this.editText == spare_dns6) {
                            spare_dns6.requestFocus();
                        } else if (this.editText == spare_dns7) {
                            spare_dns8.requestFocus();
                        }
                    } else {
                        this.editText.setText("0");
                        Log.i(TAG, "ip 格式错误");
                        CommonUtils.showToastShort(mContext, mContext.getResources().getString(R.string.eth_static_ipv6_format_error));

                    }
                } catch (NumberFormatException e) {
                    this.editText.setText("");
                    CommonUtils.showToastShort(mContext, mContext.getResources().getString(R.string.eth_static_input_0_2_f));
                }

            } else if (s.length() == 0) {
//                this.editText.setText("0");
            } else {
                try {
                    Integer.parseInt(s.toString(), 16);
                } catch (NumberFormatException e) {
                    this.editText.setText("0");
                    CommonUtils.showToastShort(mContext, mContext.getResources().getString(R.string.eth_static_input_0_2_f));
                }
            }
        }
    }

    private void initData() {
        mEthManager = (EthernetManager) getSystemService(Context.ETHERNET_SERVICE);
        mIpConfigV6 = mEthManager.getIpv6Configuration();

        etIpList = new EditText[8];
        etGatewayList = new EditText[8];
        etDns1List = new EditText[8];
        etDns2List = new EditText[8];
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm__static_ipv6_connection:
                startStatic();
                finish();
                break;
            default:
                break;

        }
    }

    private void initIp() {

        Log.i(TAG, "-------- initIp --------");

        String ip = null, gateway = null, dns1 = null, dns2 = null;
        int prefix = -1;
        StaticIpConfiguration staticConfig = mIpConfigV6.staticIpConfiguration;
        if (null != staticConfig) {
            Log.i(TAG, "static config is null");
            LinkAddress address = staticConfig.ipAddress;
            ip = address.getAddress().getHostAddress();
            prefix = address.getNetworkPrefixLength();

            dns1 = staticConfig.dnsServers.get(0).getHostAddress();
            if (staticConfig.dnsServers.size() >= 2) {
                dns2 = staticConfig.dnsServers.get(1).getHostAddress();
            }
        }
        if (null == ip) {
            ip = mEthManager.getIpv6DatabaseAddress();
            prefix = mEthManager.getIpv6DatabasePrefixlength();
            gateway = mEthManager.getIpv6DatabaseGateway();
            dns1 = mEthManager.getIpv6DatabaseDns1();
            dns2 = mEthManager.getIpv6DatabaseDns2();

        }

        if (null == ip || mDefaultIp.equals(ip)) {
            Log.i(TAG, "set dynamic ip");

            String iface = mEthManager.getInterfaceName();
            if (EthernetManager.ETHERNET_CONNECT_MODE_DHCP.equals(mEthManager.getEthernetMode6())) {
                ip = mEthManager.getDhcpv6Ipaddress(iface);
                prefix = Integer.parseInt(mEthManager.getDhcpv6Prefixlen(iface));
                gateway = mEthManager.getDhcpv6Gateway(iface);
                dns1 = mEthManager.getDhcpv6Dns(iface, 1);
                dns2 = mEthManager.getDhcpv6Dns(iface, 2);
            } else if (EthernetManager.ETHERNET_CONNECT_MODE_STATELESS.equals(mEthManager.getEthernetMode6())) {
                ip = mEthManager.getStatelessIpv6Address();
                prefix = mEthManager.getStatelessIpv6Prefixlength();
                gateway = mEthManager.getDhcpv6Gateway(iface);
                dns1 = mEthManager.getStatelessIpv6Dns1();
                dns2 = mEthManager.getStatelessIpv6Dns2();
            } else if (EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(mEthManager.getEthernetMode6())) {
                iface = PPPOE_INTERFACE;
                if (!mEthManager.isIpv6PppoeStateless()) {//use statefull mode
                    Log.i(TAG, "get pppoe statefull");
                    ip = mEthManager.getDhcpv6Ipaddress(iface);
                    gateway = mEthManager.getDhcpv6Gateway(iface);
                    prefix = Integer.parseInt(mEthManager.getDhcpv6Prefixlen(iface));
                    dns1 = mEthManager.getDhcpv6Dns(iface, 1);
                    dns2 = mEthManager.getDhcpv6Dns(iface, 2);

                    Log.i(TAG, "showIpv6Info, mode pppoe statefull ipv6, " +
                            "ip:" + mEthManager.getDhcpv6Ipaddress(iface) +
                            ", gw:" + mEthManager.getDhcpv6Gateway(iface) +
                            ", prefix:" + mEthManager.getDhcpv6Prefixlen(iface) +
                            ", dns1:" + mEthManager.getDhcpv6Dns(iface, 1) +
                            ", dns2:" + mEthManager.getDhcpv6Dns(iface, 2));
                } else {
                    Log.i(TAG, "get pppoe stateless");
                    ip = mEthManager.getStatelessIpv6Address();
                    gateway = mEthManager.getStatelessIpv6Gateway(iface);
                    prefix = mEthManager.getStatelessIpv6Prefixlength();
                    dns1 = mEthManager.getStatelessIpv6Dns1();
                    dns2 = mEthManager.getStatelessIpv6Dns2();

                    Log.i(TAG, "showIpv6Info, mode pppoe stateless ipv6, " +
                            "ip:" + mEthManager.getStatelessIpv6Address() +
                            ", gw:" + mEthManager.getStatelessIpv6Gateway(iface) +
                            ", prefix:" + mEthManager.getStatelessIpv6Prefixlength() +
                            ", dns1:" + mEthManager.getStatelessIpv6Dns1() +
                            ", dns2:" + mEthManager.getStatelessIpv6Dns2());
                }
            }
        }

        setIp(ip);
        setNetmask(prefix);
        setGateway(gateway);
        setMainDns(dns1);
        setSubDns(dns2);
    }

    private void setIp(String ip) {
        Log.i(TAG, "-------- setIp --------");
        if (null == ip) {
            ip = mDefaultIp;
        }

        InetAddress inetIp = NetworkUtils.numericToInetAddress(ip);
        if (!(inetIp instanceof Inet6Address)) {
            Log.i(TAG, "not Inet6Address, return.");
            return;
        }

        Log.i(TAG, "ip: " + ip);
        String[] subIp = ip.split(":");
        if (subIp.length != 8) {

            String[] tmpIp = new String[8];
            // 补 0 操作
            for (int i = 0; i < subIp.length; i++) {
                if (TextUtils.isEmpty(subIp[i])) {
                    break;
                }
                tmpIp[i] = subIp[i];
            }
            for (int i = subIp.length - 1; i >= 0; i--) {
                if (TextUtils.isEmpty(subIp[i])) {
                    break;
                }
                tmpIp[i + 8 - subIp.length] = subIp[i];
            }
            for (int i = 0; i < 8; i++) {
                if (TextUtils.isEmpty(tmpIp[i])) {
                    tmpIp[i] = "0";
                }
                etIpList[i].setText(tmpIp[i]);
            }

        } else {
            for (int i = 0; i < 8; i++) {
                if (TextUtils.isEmpty(subIp[i])) {
                    subIp[i] = "0";
                }
                etIpList[i].setText(subIp[i]);
            }
        }

    }

    private void setNetmask(int prefix) {
        Log.i(TAG, "-------- setNetmask --------");
        if (prefix < 0 || prefix > 128) {
            return;
        }

        Log.i(TAG, "prefix: " + prefix);
        length_mask.setText(String.valueOf(prefix));
    }

    private void setGateway(String gateway) {
        Log.i(TAG, "-------- setGateway --------");
        if (null == gateway) {
            gateway = mDefaultIp;
        }

        InetAddress inetIp = NetworkUtils.numericToInetAddress(gateway);
        if (!(inetIp instanceof Inet6Address)) {
            Log.i(TAG, "not Inet6Address, return.");
            return;
        }

        Log.i(TAG, "gateway: " + gateway);
        String[] subIp = gateway.split(":");
        if (subIp.length != 8) {

            String[] tmpIp = new String[8];
            // 补 0 操作
            for (int i = 0; i < subIp.length; i++) {
                if (TextUtils.isEmpty(subIp[i])) {
                    break;
                }
                tmpIp[i] = subIp[i];
            }
            for (int i = subIp.length - 1; i >= 0; i--) {
                if (TextUtils.isEmpty(subIp[i])) {
                    break;
                }
                tmpIp[i + 8 - subIp.length] = subIp[i];
            }
            for (int i = 0; i < 8; i++) {
                if (TextUtils.isEmpty(tmpIp[i])) {
                    tmpIp[i] = "0";
                }
                etGatewayList[i].setText(tmpIp[i]);
            }

        } else {
            for (int i = 0; i < 8; i++) {
                if (TextUtils.isEmpty(subIp[i])) {
                    subIp[i] = "0";
                }
                etGatewayList[i].setText(subIp[i]);
            }
        }
    }

    private void setMainDns(String mainDns) {
        Log.i(TAG, "-------- setMainDns --------");
        if (null == mainDns) {
            mainDns = mDefaultIp;
        }

        InetAddress inetIp = NetworkUtils.numericToInetAddress(mainDns);
        if (!(inetIp instanceof Inet6Address)) {
            Log.i(TAG, "not Inet6Address, return.");
            return;
        }

        Log.i(TAG, "mainDns: " + mainDns);
        String[] subIp = mainDns.split(":");
        if (subIp.length != 8) {

            String[] tmpIp = new String[8];
            // 补 0 操作
            for (int i = 0; i < subIp.length; i++) {
                if (TextUtils.isEmpty(subIp[i])) {
                    break;
                }
                tmpIp[i] = subIp[i];
            }
            for (int i = subIp.length - 1; i >= 0; i--) {
                if (TextUtils.isEmpty(subIp[i])) {
                    break;
                }
                tmpIp[i + 8 - subIp.length] = subIp[i];
            }
            for (int i = 0; i < 8; i++) {
                if (TextUtils.isEmpty(tmpIp[i])) {
                    tmpIp[i] = "0";
                }
                etDns1List[i].setText(tmpIp[i]);
            }

        } else {
            for (int i = 0; i < 8; i++) {
                if (TextUtils.isEmpty(subIp[i])) {
                    subIp[i] = "0";
                }
                etDns1List[i].setText(subIp[i]);
            }
        }
    }

    private void setSubDns(String subDns) {
        Log.i(TAG, "-------- setSubDns --------");
        if (TextUtils.isEmpty(subDns)) {
            return;
        }

        InetAddress inetIp = NetworkUtils.numericToInetAddress(subDns);
        if (!(inetIp instanceof Inet6Address)) {
            Log.i(TAG, "not Inet6Address, return.");
            return;
        }

        Log.i(TAG, "subDns: " + subDns);
        String[] subIp = subDns.split(":");
        if (subIp.length != 8) {

            String[] tmpIp = new String[8];
            // 补 0 操作
            for (int i = 0; i < subIp.length; i++) {
                if (TextUtils.isEmpty(subIp[i])) {
                    break;
                }
                tmpIp[i] = subIp[i];
            }
            for (int i = subIp.length - 1; i >= 0; i--) {
                if (TextUtils.isEmpty(subIp[i])) {
                    break;
                }
                tmpIp[i + 8 - subIp.length] = subIp[i];
            }
            for (int i = 0; i < 8; i++) {
                if (TextUtils.isEmpty(tmpIp[i])) {
                    tmpIp[i] = "0";
                }
                etDns2List[i].setText(tmpIp[i]);
            }

        } else {
            for (int i = 0; i < 8; i++) {
                if (TextUtils.isEmpty(subIp[i])) {
                    subIp[i] = "0";
                }
                etDns2List[i].setText(subIp[i]);
            }
        }
    }

    private String getIp() {
        String ip = ip_1.getText().toString() + ":"
                + ip_2.getText().toString() + ":"
                + ip_3.getText().toString() + ":"
                + ip_4.getText().toString() + ":"
                + ip_5.getText().toString() + ":"
                + ip_6.getText().toString() + ":"
                + ip_7.getText().toString() + ":"
                + ip_8.getText().toString();
        Log.i(TAG, "user ip: " + ip);
        return ip;
    }

    private int getPrefix() {
        int prefix = Integer.parseInt(length_mask.getText().toString());
        Log.i(TAG, "user prefix: " + prefix);
        return prefix;
    }

    private String getGateway() {
        String gateway = gateway_1.getText().toString() + ":"
                + gateway_2.getText().toString() + ":"
                + gateway_3.getText().toString() + ":"
                + gateway_4.getText().toString() + ":"
                + gateway_5.getText().toString() + ":"
                + gateway_6.getText().toString() + ":"
                + gateway_7.getText().toString() + ":"
                + gateway_8.getText().toString();
        Log.i(TAG, "user gateway: " + gateway);
        return gateway;
    }

    private String getMainDns() {
        String dns1 = dns_1.getText().toString() + ":"
                + dns_2.getText().toString() + ":"
                + dns_3.getText().toString() + ":"
                + dns_4.getText().toString() + ":"
                + dns_5.getText().toString() + ":"
                + dns_6.getText().toString() + ":"
                + dns_7.getText().toString() + ":"
                + dns_8.getText().toString();
        Log.i(TAG, "user dns1: " + dns1);
        return dns1;
    }

    private String getSubDns() {
        String dns2 = spare_dns1.getText().toString() + ":"
                + spare_dns2.getText().toString() + ":"
                + spare_dns3.getText().toString() + ":"
                + spare_dns4.getText().toString() + ":"
                + spare_dns5.getText().toString() + ":"
                + spare_dns6.getText().toString() + ":"
                + spare_dns7.getText().toString() + ":"
                + spare_dns8.getText().toString();
        Log.i(TAG, "user dns2: " + dns2);
        if (dns2.equals(mDefaultIp1) || dns2.equals(mDefaultIp)) {
            return "";
        }

        return dns2;
    }

    private int validateIpv6ConfigFields(LinkProperties linkProperties) {
        Log.i(TAG, "-------- validateIpv6ConfigFields --------");
        String errorMsg = mContext.getResources().getString(R.string.eth_ip_error);
        // check ip
        String tmpIp = getIp();

        InetAddress inetAddr = null;
        try {
            inetAddr = NetworkUtils.numericToInetAddress(tmpIp);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "ipv6 ip numericToInetAddress error: " + e);
            CommonUtils.showToastShort(mContext, errorMsg);
        }

        if (inetAddr instanceof Inet6Address) {

            Log.i(TAG, "ipv6 ip check ok!");
        } else {
            Log.w(TAG, "ipv6 ip is not Inet6Address.");
            CommonUtils.showToastShort(mContext, errorMsg);
            return -1;
        }

        // check prefix
        errorMsg = mContext.getResources().getString(R.string.eth_gateway_error);
        int intPrefixV6 = getPrefix();
        try {
            if (intPrefixV6 < 0 || intPrefixV6 > 128) {
                Log.w(TAG, "ipv6 prefix length error.");
                CommonUtils.showToastShort(mContext, errorMsg);
                return -1;
            }
            linkProperties.addLinkAddress(new LinkAddress(inetAddr, intPrefixV6));
            Log.i(TAG, "check ipv6 prefix ok!");
        } catch (Exception e) {
            Log.e(TAG, "ipv6 prefix invalid: " + e);
            CommonUtils.showToastShort(mContext, errorMsg);
            return -1;
        }

        String tmpGateway = getGateway();
        errorMsg = mContext.getResources().getString(R.string.eth_ip_settings_invalid_gateway);

        InetAddress gatewayAddr = null;
        try {
            gatewayAddr = NetworkUtils.numericToInetAddress(tmpGateway);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "ipv6 gateway numericToInetAddress error: " + e);
            CommonUtils.showToastShort(mContext, errorMsg);
            return -1;
        }
        if (gatewayAddr instanceof Inet6Address) {
            Log.i(TAG, "ipv6 gateway check ok!");
        } else {
            Log.i(TAG, "ipv6 gateway is not InetAddress.");
            CommonUtils.showToastShort(mContext, errorMsg);
            return -1;
        }
        linkProperties.addRoute(new RouteInfo(gatewayAddr));

        // check dns1
        String tmpDns1 = getMainDns();
        InetAddress dnsAddr = null;
        errorMsg = mContext.getResources().getString(R.string.eth_ip_settings_invalid_dns);
        try {
            dnsAddr = NetworkUtils.numericToInetAddress(tmpDns1);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "ipv6 dns1 numericToInetAddress error: " + e);
            CommonUtils.showToastShort(mContext, errorMsg);
            return -1;
        }
        if (dnsAddr instanceof Inet6Address) {
            Log.i(TAG, "ipv6 dns1 check ok!");
        } else {
            Log.i(TAG, "ipv6 dns1 is not Inet6Address.");
            CommonUtils.showToastShort(mContext, errorMsg);
            return -1;
        }
        linkProperties.addDns(dnsAddr);


        String tmpSubDns = getSubDns();
        errorMsg = mContext.getResources().getString(R.string.eth_static_error_dns2);
        InetAddress dns2Addr = null;
        if (!TextUtils.isEmpty(tmpSubDns)) {
            try {
                dns2Addr = NetworkUtils.numericToInetAddress(tmpSubDns);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "ipv6 dns2 numericToInetAddress error: " + e);
                CommonUtils.showToastShort(mContext, errorMsg);
                return -1;
            }
            if (dns2Addr instanceof Inet6Address) {
                Log.i(TAG, "ipv6 dns2 check ok!");
            } else {
                Log.i(TAG, "ipv6 dns2 is not Inet6Address.");
                CommonUtils.showToastShort(mContext, errorMsg);
                return -1;
            }
            linkProperties.addDns(dnsAddr);
        }

        return 0;
    }

    private void startStatic() {
        Log.i(TAG, "-------- startStatic --------");
        if (!mEthManager.getNetLinkStatus()) {
            Log.i(TAG, "link down.");
            return;
        }

        if (0 != validateIpv6ConfigFields(new LinkProperties())) {
            Log.i(TAG, "invalid config fields, return.");
            return;
        }

        String connectMsg = mContext.getResources().getString(R.string.eth_connecting_wait);
        CommonUtils.showToastShort(mContext, connectMsg);

        /*InetAddress ip = NetworkUtils.numericToInetAddress(getIp());
        int prefix = getPrefix();
        InetAddress gateway = NetworkUtils.numericToInetAddress(getGateway());
        InetAddress dns1 = NetworkUtils.numericToInetAddress(getMainDns());
        InetAddress dns2 = NetworkUtils.numericToInetAddress(getSubDns());

        DhcpInfo dhcpInfo = new DhcpInfo();
        dhcpInfo.ipAddress = NetworkUtils.inetAddressToInt(ip);
        dhcpInfo.netmask = NetworkUtils.inetAddressToInt(netmask);
        dhcpInfo.gateway = NetworkUtils.inetAddressToInt(gateway);
        dhcpInfo.dns1 = NetworkUtils.inetAddressToInt(dns1);
        dhcpInfo.dns2 = NetworkUtils.inetAddressToInt(dns2);*/

        mEthManager.setIpv6DatabaseInfo(getIp(),
                getPrefix(),
                getGateway(),
                getMainDns(),
                getSubDns());


        mEthManager.enableIpv6(false);
        mEthManager.setEthernetMode6(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL);
        mIpConfigV6.ipAssignment = IpConfiguration.IpAssignment.STATIC;
        /*mIpConfig.staticIpConfiguration = staticConfig;
        mIpConfig.ipAssignment = IpConfiguration.IpAssignment.STATIC;*/
        mEthManager.enableIpv6(true);
    }

}
