package com.changhong.settings.About;

import android.annotation.Nullable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


//import androidx.core.app.NavUtils;

import com.changhong.settings.R;

public class Diagnose_Activity extends Activity implements View.OnClickListener {
    private static final String TAG = "设备诊断";
    private Context mContext = Diagnose_Activity.this;
    private RelativeLayout device_message, service_frame_version_detaile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_diagnose);
        Log.i(TAG, "onCreate: About");
        initView();
    }

    private void initView() {
        device_message = (RelativeLayout) findViewById(R.id.diagnose_terminal);
        device_message.setOnClickListener(this);
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
            case R.id.diagnose_terminal:
                intent.setClass(mContext, Diagnose_Terminal_Activity.class);
                break;
            case R.id.diagnose_business:
                intent.setClass(mContext, Diagnose_Business_Activity.class);
                break;
            default:
                break;
        }
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
