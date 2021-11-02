package com.changhong.settings;

import android.content.Context;
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

public class AllListAdapter extends BaseAdapter{

    private int layout;
    private Context mContext;
    List<Map<String, Object>> mydata;


    public AllListAdapter(int layout , List<Map<String, Object>> list , Context context) {

        this.mydata = list;
        this.mContext=context;
        this.layout=layout;
    }

    public void updateBondState()
    {

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

        ViewHolder viewHolder;


        if(view == null)
        {
            viewHolder = new ViewHolder();

            view= LayoutInflater.from(mContext).inflate(layout,null);
            viewHolder.tvName=(TextView) view.findViewById(R.id.idbluetoothname);
            view.setTag(viewHolder);

        }
        else
        {
            viewHolder=(ViewHolder)view.getTag();
        }
        if (mydata.get(position).get("device_name") == null || mydata.get(position).get("device_name").equals("")){
            viewHolder.tvName.setText(mydata.get(position).get("device_addr") + "");
        }else {
            viewHolder.tvName.setText(mydata.get(position).get("device_name") + "");
        }

        return view;
    }

    private final  class ViewHolder
    {

        TextView tvName = null;

    }


}
