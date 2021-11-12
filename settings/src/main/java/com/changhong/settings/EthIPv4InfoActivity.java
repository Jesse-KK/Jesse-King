package com.changhong.settings;

import android.app.Activity;
import android.content.Context;
import android.net.IpInfo;
import android.net.NetworkUtils;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.changhong.settings.R;

public class EthIPv4InfoActivity extends Activity {
    private static final String TAG = "IPv4InfoActivity";
    private Context mContext= EthIPv4InfoActivity.this;
    private TextView ip_address,mask_address,gateway_address,dns_address;
    private EthernetManager mEthManager;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ipv4_ethinfo);
        initView();
        initData();
    }


    public void initData() {
        mEthManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        IpInfo ipv4Info = mEthManager.getIPv4Info();
        if (ipv4Info != null) {
            if (ipv4Info.ip != null) {
                ip_address.setText(ipv4Info.ip.getHostAddress());
                mask_address.setText(NetworkUtils.intToInetAddress(NetworkUtils.prefixLengthToNetmaskInt(ipv4Info.mask)).getHostAddress());
            }
            if (ipv4Info.gateway != null){
                gateway_address.setText(ipv4Info.gateway.getHostAddress());
            }if(ipv4Info.dnsServers.get(0)!=null)
            dns_address.setText(ipv4Info.dnsServers.get(0).getHostAddress());
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
