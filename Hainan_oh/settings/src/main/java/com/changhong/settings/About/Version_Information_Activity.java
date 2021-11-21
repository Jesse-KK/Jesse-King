package com.changhong.settings.About;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.changhong.settings.R;

public class Version_Information_Activity extends Activity {
    private static final String TAG = "版本信息";
    private TextView device_model_, epg_version, mac_address_;
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
        epg_version = (TextView)findViewById(R.id.epg_version);

        String software_version = SystemProperties.get("ro.build.version.incremental");

        String epg_version_string = "";
        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo("tv.icntv.ott", 0);
            epg_version_string = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "get package info err: " + e);
        }
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
        epg_version.setText(epg_version_string);


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
