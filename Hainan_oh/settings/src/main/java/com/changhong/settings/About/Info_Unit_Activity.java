package com.changhong.settings.About;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.widget.TextView;

//import androidx.core.app.NavUtils;

import com.changhong.settings.R;

public class Info_Unit_Activity extends Activity{
    private String TAG = "king_Info_Unit";

    private TextView device_model_, serial_32, serial_15, mac_address_;
    private Context mContext = Info_Unit_Activity.this;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_unit);
        init();
    }

    private void init() {
        device_model_ = (TextView) findViewById(R.id.device_model_);
        serial_32 = (TextView) findViewById(R.id.serial_32);
        serial_15 = (TextView) findViewById(R.id.serial_15);
        mac_address_ = (TextView) findViewById(R.id.mac_address_);

        String devices_type = SystemProperties.get("ro.product.model");
        String serial_num = SystemProperties.get("ro.serialno");
        String stbid = SystemProperties.get("ro.deviceid");
        String mac_address = SystemProperties.get("ro.mac");

        device_model_.setText(devices_type);
        serial_32.setText(serial_num);
        serial_15.setText(stbid);
        mac_address_.setText(mac_address);

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
