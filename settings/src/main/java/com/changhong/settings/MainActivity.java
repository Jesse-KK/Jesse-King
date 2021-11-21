package com.changhong.settings;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.changhong.settings.About.About_Activity;
import com.changhong.settings.General.General_Activity;
import com.changhong.settings.Image_Sound.Image_Sound_Activity;
import com.changhong.settings.Security.Security_Password_Activity;
import com.changhong.settings.Update_king.Update_System_Auto_Activity;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "q_Settings";
    private RelativeLayout netWork, commons, setting_main_2, setting_main_4, setting_main_5, setting_main_6;
    private Context mContext;
    private SecurityPasswordDialog dialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        Settings.Secure.putInt(mContext.getContentResolver(),"password_protect_state",1);
        Settings.Secure.putInt(mContext.getContentResolver(),"password_protect_state",0);

        initView();
        // if app is first launched, we can have focus.
        Instrumentation inst = new Instrumentation();
        inst.setInTouchMode(false);
        //密码保护的框框
        if (isSecurityOpen()) {
            showDialogSecurityPasswordProtection();
        }

    }

    private boolean isSecurityOpen() {
        try {
            int flag = Settings.Secure.getInt(mContext.getContentResolver(), "password_protect_state");
            if (flag == 1) {
                return true;
            } else return false;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showDialogSecurityPasswordProtection() {
        dialog = new SecurityPasswordDialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen,
               new SecurityPasswordDialog.EditCheckCallBack() {
            @Override
            public void CorrectCallBack() {
                Toast.makeText(mContext,"Your Password is CORRECT," +
                        "System Security is processing...",Toast.LENGTH_LONG).show();
            }

            @Override
            public void InCorrectCallBack() {
                Toast.makeText(mContext,"Your Password is NOT CORRECT," +
                        "Please try again",Toast.LENGTH_LONG).show();
            }

            @Override
            public void EmptyCallBack() {
                Toast.makeText(mContext,"Yout Password is EMPTY,Please try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
        dialog.getWindow()
                .setType(WindowManager.LayoutParams.TYPE_APPLICATION);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

    }

    private void initView() {
        netWork = (RelativeLayout) findViewById(R.id.setting_main_1);
        setting_main_2 = (RelativeLayout) findViewById(R.id.setting_main_2);
        commons = (RelativeLayout) findViewById(R.id.setting_main_3);
        setting_main_4 = (RelativeLayout) findViewById(R.id.setting_main_4);
        setting_main_5 = (RelativeLayout) findViewById(R.id.setting_main_5);
        setting_main_6 = (RelativeLayout) findViewById(R.id.setting_main_6);
        setting_main_2.setOnClickListener(this);
        setting_main_4.setOnClickListener(this);
        setting_main_5.setOnClickListener(this);
        setting_main_6.setOnClickListener(this);
        netWork.setOnClickListener(this);
        commons.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.setting_main_1:
                intent.setClass(mContext, NetSettingActivity.class);
                break;
            case R.id.setting_main_2:
                intent.setClass(mContext, Image_Sound_Activity.class);
                break;
            case R.id.setting_main_3:
                intent.setClass(mContext, General_Activity.class);
                break;
            case R.id.setting_main_4:
                intent.setClass(mContext, Security_Password_Activity.class);
                break;
            case R.id.setting_main_5:
                intent.setClass(mContext, Update_System_Auto_Activity.class);
                break;
            case R.id.setting_main_6:
                intent.setClass(mContext, About_Activity.class);
                break;
        }
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
