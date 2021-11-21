package com.changhong.settings.About;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.PppoeManager;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

//import androidx.annotation.Nullable;

import com.changhong.settings.R;

import static com.android.internal.os.MemoryPowerCalculator.TAG;

public class Diagnose_Business_Activity extends Activity implements View.OnClickListener {
    private Context mContext = Diagnose_Business_Activity.this;
    private EthernetManager EthernetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EthernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.changhong.diagnostic",
                "com.changhong.diagnostic.DiagnosticActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("com.changhong.diagnostic");
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "start activity err: " + e);
        }
    }
}
