package com.changhong.settings.Update_king;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


//import androidx.annotation.RequiresApi;

import com.changhong.settings.R;

public class Update_System_Auto_Activity extends Activity implements View.OnClickListener {
    private RelativeLayout check_system_update, toast_message_update;
    private TextView version_soft_check, system_address, check_system;
    private ProgressBar check_system_update_bar;
    private int i=0;
    private static final String UnchangingAddress = "http://tview.itv.cmvideo.cn";
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        toast_message_update.setVisibility(View.INVISIBLE);
        check_system_update_bar.setVisibility(View.INVISIBLE);
        check_system.setText("检测更新");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_system_auto);

        initView();
    }

    private void initView() {
        check_system_update = (RelativeLayout) findViewById(R.id.check_system_update);
        toast_message_update = (RelativeLayout) findViewById(R.id.toast_message_update);
        check_system_update_bar = (ProgressBar) findViewById(R.id.check_system_update_bar);
        check_system_update_bar.setVisibility(View.INVISIBLE);
        toast_message_update.setVisibility(View.GONE);
        check_system_update.setOnClickListener(this);
        version_soft_check = (TextView) findViewById(R.id.version_soft_check);
        system_address = (TextView) findViewById(R.id.system_address);
        check_system = (TextView) findViewById(R.id.check_system);



        String software_version = SystemProperties.get("ro.build.version.incremental");
        version_soft_check.setText(software_version);
        system_address.setText(UnchangingAddress);


    }

    @Override
    public void onClick(View v) {
        check_system.setText("正在检测");
        Log.e("xixi", "handleMessage: " );
        check_system_update_bar.setVisibility(View.VISIBLE);
        toast_message_update.setVisibility(View.VISIBLE);
        Animation animation=AnimationUtils.loadAnimation(Update_System_Auto_Activity.this,R.anim.fade_out);
        toast_message_update.startAnimation(animation);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mhandler.sendEmptyMessage(0);
            }
        }.start();
//        toast_message_update.setVisibility(View.INVISIBLE);
//        check_system_update_bar.setVisibility(View.INVISIBLE);
    }
}