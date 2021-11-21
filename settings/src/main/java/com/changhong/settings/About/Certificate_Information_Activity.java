package com.changhong.settings.About;

import android.annotation.Nullable;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;


import com.changhong.settings.R;

public class Certificate_Information_Activity extends Activity {
    private static final String TAG = "认证信息";
    private TextView address_certification, account_certification;
    private Context mContext = Certificate_Information_Activity.this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_information);
        init();
    }

    private void init() {
        address_certification = (TextView) findViewById(R.id.address_certification);
        account_certification = (TextView) findViewById(R.id.account_certification);
        address_certification.setText("http://bngt.itv.cmvideo.cm:8095");
        account_certification.setText("yktaccount");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

}
