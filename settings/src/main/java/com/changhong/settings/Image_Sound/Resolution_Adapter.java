package com.changhong.settings.Image_Sound;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Resolution_Adapter extends BaseAdapter {
    private Context context;
    private List<HashMap<String, Object>> list;
    private LayoutInflater layoutInflater;
    private TextView tv;
    private CheckableImageView image_check;
    private int selectedIndex;

    public Resolution_Adapter(Context context, List<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;//list中RadioButton状态为false
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void setSelectedIndex(int position) {
        this.selectedIndex = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewCache viewCache = new ViewCache();
        if (convertView == null) {
            convertView = layoutInflater.inflate(com.changhong.settings.R.layout.resolution_list_item, parent, false);
            tv = (TextView) convertView.findViewById(com.changhong.settings.R.id.mode_list);
            image_check = (CheckableImageView) convertView.findViewById(com.changhong.settings.R.id.image_check);
            viewCache.tv = tv;
            viewCache.image_check = image_check;
            convertView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) convertView.getTag();
            tv = viewCache.tv;
            image_check = viewCache.image_check;
        }
        image_check.setClickable(false);
        if (selectedIndex == position) {
            viewCache.image_check.setChecked(true);
            list.get(position).put("boolean",true);
        }else {
            viewCache.image_check.setChecked(false);
            list.get(position).put("boolean",false);
        }
        tv.setText(list.get(position).get("list") + "");
        return convertView;
    }

    public class ViewCache {
        TextView tv;
        CheckableImageView image_check;

    }
}
