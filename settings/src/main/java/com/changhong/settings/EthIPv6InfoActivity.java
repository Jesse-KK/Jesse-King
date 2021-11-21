package com.changhong.settings;

import android.app.Activity;
import android.content.Context;
import android.net.IpInfo;
import android.net.NetworkUtils;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class EthIPv6InfoActivity extends Activity {
    private static final String TAG = "EthIPv6InfoActivity";
    private Context mContext= EthIPv6InfoActivity.this;
    private TextView ip_address,mask_address,gateway_address,dns_address;
    private EthernetManager mEthManager;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ipv6_ethinfo);
        Log.e(TAG, "EthIPv6InfoActivity: onCreate");
        initView();
        initData();
    }

    public void initData(){

        mEthManager=(EthernetManager)mContext.getSystemService(Context.ETHERNET_SERVICE);
        IpInfo ipv6Info=mEthManager.getIPv6Info();
        Log.e(TAG, "initData: "+ipv6Info);
        if (ipv6Info!=null) {
            Log.e(TAG, "initData: gegegegegege" );
            ip_address.setText(ipv6Info.ip.getHostAddress());
            mask_address.setText(NetworkUtils.intToInetAddress(NetworkUtils.prefixLengthToNetmaskInt(ipv6Info.mask)).getHostAddress());
            gateway_address.setText(ipv6Info.gateway.getHostAddress());
            dns_address.setText(ipv6Info.dnsServers.get(0).getHostAddress());
        }
    }

    public void initView(){
        ip_address=findViewById(R.id.ip_address);
        mask_address=findViewById(R.id.mask_address);
        gateway_address=findViewById(R.id.gateway_address);
        dns_address=findViewById(R.id.dns_address);
    }

    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }
}
