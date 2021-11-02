package com.changhong.settings;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;

import static android.net.ConnectivityManager.TYPE_WIFI;

public class WifiIPv6InfoActivity extends Activity {
    private static final String TAG ="WifiInfoActivity" ;
    private WifiManager mWifiManager;
    private Context mContext= WifiIPv6InfoActivity.this;
    private InetAddress ipV6;
    private TextView ip_address,mask_address,gateway_address,dns_address;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_ipv6_wifiinfo);
        Log.e(TAG, "WifiIPv6InfoActivity: onCreate");
        ip_address=findViewById(R.id.ip_address);
        mask_address=findViewById(R.id.mask_address);
        gateway_address=findViewById(R.id.gateway_address);
        dns_address=findViewById(R.id.dns_address);
        mWifiManager= (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        getWifiInfo();
        ip_address.setText(ipV6.getHostAddress());
//        mask_address.setText(ipV6);
    }

    private void getWifiInfo() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.i(TAG, "initView: assert cm!=null");
        assert cm != null;
        try {
            LinkProperties prop = cm.getLinkProperties(TYPE_WIFI);
            Log.i(TAG, "initView: assert prop!=null");
            assert prop != null;
            Class clazz=prop.getClass();
            Method method=clazz.getMethod("getAllAddresses");
            method.setAccessible(true);
            List<InetAddress> list= (List<InetAddress>) method.invoke(prop);
            Iterator<InetAddress> iter = list.iterator();
            Log.i(TAG, "initView: iter!=null");
            assert iter != null;
            InetAddress a6 = null;
            while (iter.hasNext()) {
                a6 = iter.next();
                if (a6 instanceof Inet6Address) {
                    ipV6=a6;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    private void showWifiV6(String ipv6) {
//        Log.i(TAG, "into showWifiV6: ipv6="+ipv6);
//
//        TextView temp = new TextView(this);
//        temp.setText(getResources().getString(R.string.wifi_info_ip_add_v6));
//        int height = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getApplicationContext().getResources().getDisplayMetrics()));
//        int top = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getApplicationContext().getResources().getDisplayMetrics()));
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                height);
//        params.setMargins(0,top,0,0);
//
//        temp.setTextColor(getResources().getColor(R.color.colorAccent));
//        int textSize = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getApplicationContext().getResources().getDisplayMetrics()));
//        temp.setTextSize(textSize);
//        temp.setLayoutParams(params);
//
//        EditText et = new EditText(this);
//        et.setText(ipv6);
//        height = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getApplicationContext().getResources().getDisplayMetrics()));
//        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                height);
//        top = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getApplicationContext().getResources().getDisplayMetrics()));
//        param1.setMargins(0,top,0,0);
//        et.setTextColor(getResources().getColor(R.color.black));
//        int textSize1 = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 25, getApplicationContext().getResources().getDisplayMetrics()));
//        et.setTextSize(textSize1);
//        et.setLayoutParams(param1);
//        et.setFocusable(false);
//        et.setFocusableInTouchMode(false);
//        int left = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getApplicationContext().getResources().getDisplayMetrics()));
//        int right = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getApplicationContext().getResources().getDisplayMetrics()));
//        et.setPadding(left,0,right,0);
//
//        ll_wifi_ipv6_ip.addView(temp);
//        ll_wifi_ipv6_ip_value.addView(et,param1);
//
//    }

    private String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

}
