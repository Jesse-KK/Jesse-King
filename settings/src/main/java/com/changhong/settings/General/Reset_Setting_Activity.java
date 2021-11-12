package com.changhong.settings.General;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.changhong.settings.R;

public class Reset_Setting_Activity extends Activity implements View.OnClickListener {
    private Context mContext;
    private RelativeLayout toast_message_recovery;
    private RecoveryDialog dialog = null;
    private EditText input_password_resetting;
    private Button button_confirm_resetting;
    private String password;
    private static final String TAG = "King_Resetting";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            toast_message_recovery.setVisibility(View.INVISIBLE);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_setting);
        mContext = Reset_Setting_Activity.this;
        init();
    }

    private void init() {
        input_password_resetting = (EditText) findViewById(R.id.input_password_resetting);
        input_password_resetting.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
        button_confirm_resetting = (Button) findViewById(R.id.button_confirm_resetting);
        toast_message_recovery = (RelativeLayout) findViewById(R.id.toast_message_recovery);
        button_confirm_resetting.setOnClickListener(this);
        toast_message_recovery.setVisibility(View.GONE);


    }

    @Override
    public void onClick(View v) {
        Editable text = input_password_resetting.getText();
        password = text.toString();
        if (password.equals("10086")) {
            ShowDialog();
        }else {
            toast_message_recovery.setVisibility(View.VISIBLE);
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            }.start();
        }
    }

    private void ShowDialog() {//显示询问对话框
        dialog = new RecoveryDialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen,
                new RecoveryDialog.OnCallback() {
                    @Override
                    public void OkcallBack() {
                        Log.i(TAG, "OkcallBack: ");

                        //Android 4.4的reset
                        mContext.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));

                        //Android 9.0的reset
                       /* Intent intent = new Intent("android.intent.action.FACTORY_RESET");
                        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                        intent.setPackage("android");
                        mContext.sendBroadcast(intent);*/
                        //发送了一个名为ACTION_FACTORY_RESET（android.intent.action.FACTORY_RESET）的广播。
                        // 这里值得注意的是：以前的广播为android.intent.action.MASTER_CLEAR，但是已经被ACTION_FACTORY_RESET 替换
                    }

                    @Override
                    public void CancelcallBack() {
                        Log.i(TAG, "CancelcallBack: ");
                    }

                    @Override
                    public void MidcallBack() {

                    }
                }
        );
        dialog.getWindow()
                .setType(WindowManager.LayoutParams.TYPE_APPLICATION);
        dialog.show();
        dialog.setCancelable(false);
    }
}
