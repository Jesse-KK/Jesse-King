package com.changhong.settings;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by zb on 2017/4/22.
 */

public class PairedListAdapter extends BaseAdapter {

    private final static String TAG = "chbluetoothset_connect";
    private int layout;
    private Context mContext;
    List<Map<String, Object>> mydata;
    //private int connect_position;
    private BluetoothDevice connectDevice;

    public PairedListAdapter(int layout, List<Map<String, Object>> list, Context context) {

        this.mydata = list;
        this.mContext = context;
        this.layout = layout;
    }

    public void updateBondState(BluetoothDevice connectDevice) {
        this.connectDevice = connectDevice;
    }


    @Override
    public int getCount() {

        return mydata.size();
    }

    @Override
    public Object getItem(int position) {
        return mydata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        BluetoothDevice bluetoothDevice = (BluetoothDevice) mydata.get(position).get(Constent.ListDeviceInfo);
        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();

            view = LayoutInflater.from(mContext).inflate(layout, null);
            viewHolder.tvName = (TextView) view.findViewById(R.id.idbluetoothname);
            viewHolder.tv_connected = (TextView) view.findViewById(R.id.tv_connected);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //Log.i(TAG, "position:" + position + "&connect_state:" + mydata.get(position).get("connect_state"));
        if (mydata.get(position).get("connect_state") == null || mydata.get(position).get("connect_state").equals("")) {
            //Log.i(TAG, "position:" + position +" 设置了已配对");
            viewHolder.tv_connected.setText("已配对");
        } else {
            //Log.i(TAG, "position:" + position +" 设置了已连接");
            viewHolder.tv_connected.setText("已连接");
        }

        if (mydata.get(position).get("device_name") == null || mydata.get(position).get("device_name").equals("")){
            viewHolder.tvName.setText(mydata.get(position).get("device_addr") + "");
            viewHolder.tvMac.setText(mydata.get(position).get("device_addr") + "");
            Log.e(TAG, "idbluetoothmac: "+mydata.get(position).get("device_addr"));
        }else {
            viewHolder.tvName.setText(mydata.get(position).get("device_name") + "");
//            viewHolder.tvMac.setText(mydata.get(position).get("device_addr") + "");
//            Log.e(TAG, "idbluetoothmac: "+mydata.get(position).get("device_addr"));
        }


        return view;
    }

    private final class ViewHolder {

        TextView tvName = null;
        TextView tvMac = null;
        TextView tv_connected = null;
    }

    public void setConnected(int position, View view) {

        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();

            view = LayoutInflater.from(mContext).inflate(layout, null);
            viewHolder.tvName = (TextView) view.findViewById(R.id.idbluetoothname);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

            viewHolder.tv_connected.setText("已连接");

    }


}
