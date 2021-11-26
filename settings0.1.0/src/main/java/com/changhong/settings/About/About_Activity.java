package com.changhong.settings.About;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.changhong.settings.R;

public class About_Activity extends Activity implements View.OnClickListener {

    private static final String TAG = "king_About";
    private Context mContext = About_Activity.this;
    private RelativeLayout device_message, service_frame_version_detaile, service_type, setting_net_diagnose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Log.i(TAG, "onCreate: About");
        initView();
    }

    private void initView() {
        device_message = (RelativeLayout) findViewById(R.id.device_message);
        device_message.setOnClickListener(this);
        service_frame_version_detaile = (RelativeLayout) findViewById(R.id.service_frame_version_detaile);
        service_frame_version_detaile.setOnClickListener(this);
//        service_type = (RelativeLayout) findViewById(R.id.service_type);
//        service_type.setOnClickListener(this);
        setting_net_diagnose = (RelativeLayout) findViewById(R.id.setting_net_diagnose);
        setting_net_diagnose.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.device_message:
                intent.setClass(mContext, Info_Unit_Activity.class);
                break;
            case R.id.service_frame_version_detaile:
                intent.setClass(mContext, Version_Information_Activity.class);
                break;
//            case R.id.service_type:
//                intent.setClass(mContext, Certificate_Information_Activity.class);
//                break;
            case R.id.setting_net_diagnose:
                intent.setClass(mContext, Diagnose_Activity.class);
                break;
        }
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}

