package com.changhong.settings.About;

import android.annotation.Nullable;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ethernet.EthernetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


//import androidx.core.app.NavUtils;

import com.changhong.settings.R;

public class Diagnose_Activity extends Activity implements View.OnClickListener {
    private static final String TAG = "设备诊断";
    private Context mContext;
    private EthernetManager ethernetManager;
    private RelativeLayout  service_frame_version_detaile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_diagnose);
        mContext = Diagnose_Activity.this;
        ethernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        Log.i(TAG, "onCreate: About");
        initView();
    }

    private void initView() {
        service_frame_version_detaile = (RelativeLayout) findViewById(R.id.diagnose_business);
        service_frame_version_detaile.setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.diagnose_business:
                intent.setComponent(new ComponentName("com.changhong.diagnostic",
                        "com.changhong.diagnostic.DiagnosticActivity"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "start activity err: " + e);
                }
                break;
            default:
                break;
        }
    }
}
