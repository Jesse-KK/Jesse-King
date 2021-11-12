package com.changhong.settings.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.settings.R;
import com.changhong.settings.utils.WifiAdminUtils;

import java.util.List;


/**
 * Created by fdw on 2017/9/13.
 */
public class MyListViewAdapter extends BaseAdapter {

    private List<ScanResult> datas;
    private Context context;
    // 取得WifiManager对象
    private WifiManager mWifiManager;
    private ConnectivityManager cm;
    private String language = "";
    public void setDatas(List<ScanResult> datas) {
        this.datas = datas;
    }

    public MyListViewAdapter(Context context, List<ScanResult> datas) {
        super();
        this.datas = datas;
        this.context = context;
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        language = context.getResources().getConfiguration().locale.getCountry();

    }

    @Override
    public int getCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder tag = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.view_wifi_list, null);
            tag = new Holder();
            tag.txtWifiName = (TextView) convertView
                    .findViewById(R.id.tv_wifi_name);
//            tag.imgWifiLevelIco = (ImageView) convertView
//                    .findViewById(R.id.iv_wifi_lock);
            convertView.setTag(tag);
        }
        // 设置数据
        Holder holder = (Holder) convertView.getTag();
        // Wifi 名字
        // Wifi 描述
        String desc = "";
        String descOri = datas.get(position).capabilities;
        int level = datas.get(position).level;
        Log.i("cheng", descOri + "????????");
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String g1 = wifiInfo.getSSID();
        String g2 = "\"" + datas.get(position).SSID + "\"";
        NetworkInfo.State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        SupplicantState state = wifiInfo.getSupplicantState();
        Log.i("info", state + "????????????");
        WifiConfiguration conf = WifiAdminUtils.isExsits(datas.get(position).SSID);
        if (state == SupplicantState.ASSOCIATING || state == SupplicantState.FOUR_WAY_HANDSHAKE ||
                wifi == NetworkInfo.State.CONNECTING) {
            ImageView wifiConnect=(ImageView)convertView.findViewById(R.id.iv_wifi_lock);
            Resources resources=context.getResources();
            wifiConnect.setImageDrawable(resources.getDrawable(R.drawable.selector_lock_item));
            if (null != g2 && null != g1 && g2.endsWith(g1)) {
                if (language.equalsIgnoreCase("CN")) {
                    desc = "连接中...";
                } else if (language.equalsIgnoreCase("TW")) {
                    desc = "連接中...";
                } else {
                    desc = "Connection...";
                }

            }
        } else if (state == SupplicantState.COMPLETED || wifi == NetworkInfo.State.CONNECTED) {
            if (null != g2 && null != g1 && g2.endsWith(g1)) {
                if (language.equalsIgnoreCase("CN")) {
                    desc = "已连接";
                } else if (language.equalsIgnoreCase("TW")) {
                    desc = "已連接";
                } else {
                    desc = "Already connected";
                }
            } else {
                if (conf != null) {
                    ImageView wifiConnect = (ImageView) convertView.findViewById(R.id.iv_wifi_lock);
                    Resources resources = context.getResources();
                    wifiConnect.setImageDrawable(resources.getDrawable(R.drawable.selector_lock_item));
                    if (language.equalsIgnoreCase("CN")) {
                        desc = "已保存";
                    } else if (language.equalsIgnoreCase("TW")) {
                        desc = "已保存";
                    } else {
                        desc = "Already preserved";
                    }
                }
            }
        }
        String desc1 = "";
        if (descOri.toUpperCase().contains("WPA-PSK")) {
            desc1 = "通过WPA进行保护";
        }
        if (descOri.toUpperCase().contains("WPA2-PSK")) {
            desc1 = "通过WPA2进行保护";
        }
        if (descOri.toUpperCase().contains("WPA-PSK")
                && descOri.toUpperCase().contains("WPA2-PSK")) {
            desc1 = "通过WPA/WPA2进行保护";
        }
        if (TextUtils.isEmpty(datas.get(position).SSID)) {
            if(!TextUtils.isEmpty(desc)){
                holder.txtWifiName.setText(datas.get(position).BSSID);
                ImageView wifiConnect=(ImageView)convertView.findViewById(R.id.iv_wifi_lock);
                Resources resources=context.getResources();
                wifiConnect.setImageDrawable(resources.getDrawable(R.drawable.selector_lock_item));
            }else{
                holder.txtWifiName.setText(datas.get(position).BSSID+"     ("+desc+")");
                if (state == SupplicantState.COMPLETED || wifi == NetworkInfo.State.CONNECTED){
                    if (null != g2 && null != g1 && g2.endsWith(g1)) {
                        ImageView wifiConnect = (ImageView) convertView.findViewById(R.id.iv_wifi_lock);
                        Resources resources = context.getResources();
                        wifiConnect.setImageDrawable(resources.getDrawable(R.drawable.selector_alreadyconnect_item));
                    }
                }
                else{
                    ImageView wifiConnect=(ImageView)convertView.findViewById(R.id.iv_wifi_lock);
                    Resources resources=context.getResources();
                    wifiConnect.setImageDrawable(resources.getDrawable(R.drawable.selector_lock_item));
                }
            }
        } else {
            if (TextUtils.isEmpty(desc)){
                holder.txtWifiName.setText(datas.get(position).SSID);
                ImageView wifiConnect=(ImageView)convertView.findViewById(R.id.iv_wifi_lock);
                Resources resources=context.getResources();
                wifiConnect.setImageDrawable(resources.getDrawable(R.drawable.selector_lock_item));
            }else{
                holder.txtWifiName.setText(datas.get(position).SSID+"     ("+desc+")");
                if (state == SupplicantState.COMPLETED || wifi == NetworkInfo.State.CONNECTED){
                    if (null != g2 && null != g1 && g2.endsWith(g1)) {
                        ImageView wifiConnect = (ImageView) convertView.findViewById(R.id.iv_wifi_lock);
                        Resources resources = context.getResources();
                        wifiConnect.setImageDrawable(resources.getDrawable(R.drawable.selector_alreadyconnect_item));
                    }
                }
                else{
                    ImageView wifiConnect=(ImageView)convertView.findViewById(R.id.iv_wifi_lock);
                    Resources resources=context.getResources();
                    wifiConnect.setImageDrawable(resources.getDrawable(R.drawable.selector_lock_item));
                }
            }

        }
        if (desc1.equals("")) {
            if (TextUtils.isEmpty(datas.get(position).SSID)) {
                holder.txtWifiName.setText(datas.get(position).BSSID + "     (开放的)");
                if ((state == SupplicantState.COMPLETED || wifi == NetworkInfo.State.CONNECTED) && null != g2 && null != g1 && g2.endsWith(g1)){
                    holder.txtWifiName.setText(datas.get(position).BSSID+"     ("+desc+")");
                }
            }else {
                holder.txtWifiName.setText(datas.get(position).SSID + "     (开放的)");
                if ((state == SupplicantState.COMPLETED || wifi == NetworkInfo.State.CONNECTED) && null != g2 && null != g1 && g2.endsWith(g1)){
                    holder.txtWifiName.setText(datas.get(position).SSID+"     ("+desc+")");
                }
            }
        }
        return convertView;
    }

    public static class Holder {
        public TextView txtWifiName;
//        public TextView txtWifiDesc;
//        public ImageView imgWifiLevelIco;
    }
}
