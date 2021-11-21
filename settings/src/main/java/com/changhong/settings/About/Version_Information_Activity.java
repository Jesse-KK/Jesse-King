package com.changhong.settings.About;

import android.annotation.Nullable;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.KeyEvent;
import android.widget.TextView;

import com.changhong.settings.R;

public class Version_Information_Activity extends Activity {
    private static final String TAG = "版本信息";
    private TextView device_model_, serial_15, mac_address_;
    private Context mContext = Version_Information_Activity.this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_infomation);
        init();
    }

    private void init() {
        device_model_ = (TextView) findViewById(R.id.version_soft);
        mac_address_ = (TextView) findViewById(R.id.version_android);

        String software_version = SystemProperties.get("ro.build.version.incremental");

        //牌照方版本
       /* String epg_version = "";
        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo("ro.product.brand", 0);
            epg_version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "get package info err: " + e);
        }*/
        String android_version = SystemProperties.get("ro.build.version.release");

        device_model_.setText(software_version);
        mac_address_.setText("Android  "+android_version);

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
