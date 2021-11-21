package com.changhong.settings;

import android.app.Application;
import android.util.Log;

/**
 * Created by Cheng on 2017/9/8.
 */

public class MyApplication extends Application {

    public static boolean flag = false;
    public static boolean first = false;
    private static final String TAG = "CH_JM_MyApplication";
    private boolean wifiFlag = false;

    public boolean getwifiFlag() {
        Log.i(TAG, "getwifiFlag: wifiFlag="+wifiFlag);
        return wifiFlag;
    }

    public void setwifiFlag(boolean setwifiFlag) {
        this.wifiFlag = setwifiFlag;
        Log.i(TAG, "setwifiFlag: wifiFlag=" + wifiFlag);
    }
    public MyApplication() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
