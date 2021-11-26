package com.changhong.settings;

import android.app.Activity;
import android.content.Context;
import android.net.IpInfo;
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
        Log.e(TAG, "initData:ipv6的信息为：：：：：： "+ipv6Info);
        if (ipv6Info!=null) {
            Log.e(TAG, "initData: gegegegegege" );
            if (ipv6Info.ip != null) {
                ip_address.setText(ipv6Info.ip.getHostAddress());
//                prefixV4 = NetworkUtils.netmaskIntToPrefixLength(NetworkUtils.inetAddressToInt(NetworkUtils.numericToInetAddress(mask)));
//    original   mask_address.setText(NetworkUtils.intToInetAddress(NetworkUtils.prefixLengthToNetmaskInt(ipv6Info.mask)).getHostAddress());
                //因为在setText()中传入的int类型的数据，
                // 系统会自动去匹配资源文件里的数据，而非自己设置的,要用valueOf!!
                mask_address.setText(String.valueOf(ipv6Info.mask));
                Log.e(TAG, "ipv6的子网掩码为："+ipv6Info.mask );
            }
            if (ipv6Info.gateway != null){
                gateway_address.setText(ipv6Info.gateway.getHostAddress());
            }if(ipv6Info.dnsServers.get(0)!=null) {
                dns_address.setText(ipv6Info.dnsServers.get(0).getHostAddress());
            }
//            ip_address.setText(ipv6Info.ip.getHostAddress());
//            mask_address.setText(NetworkUtils.intToInetAddress(NetworkUtils.prefixLengthToNetmaskInt(ipv6Info.mask)).getHostAddress());
//            gateway_address.setText(ipv6Info.gateway.getHostAddress());
//            dns_address.setText(ipv6Info.dnsServers.get(0).getHostAddress());
        }
    }

    public void initView(){
        ip_address=(TextView) findViewById(R.id.ip_address);
        mask_address=(TextView) findViewById(R.id.mask_address6);
        gateway_address=(TextView) findViewById(R.id.gateway_address);
        dns_address=(TextView) findViewById(R.id.dns_address);
    }

    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }
}
