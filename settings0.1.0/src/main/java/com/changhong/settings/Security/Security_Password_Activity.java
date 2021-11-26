package com.changhong.settings.Security;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import androidx.annotation.Nullable;

import com.changhong.settings.R;


public class Security_Password_Activity extends Activity implements View.OnKeyListener {
    private RelativeLayout pwd_protect;
    private TextView protection_close_on;

    boolean protection = false;

    private String TAG = "King_密码保护";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_pwd);
        init();
        try {
            int flag=Settings.Secure.getInt(Security_Password_Activity.this.getContentResolver(),"password_protect_state");
            if(flag==1){
                protection = true;
                protection_close_on.setText("开启");
            }else {
                protection=false;
                protection_close_on.setText("关闭");
            }
        } catch (Settings.SettingNotFoundException e) {
          Log.e(TAG,"error!!!!");
            e.printStackTrace();
        }


    }

    private void init() {
        pwd_protect = (RelativeLayout) findViewById(R.id.pwd_protect);
        pwd_protect.setOnKeyListener(this);
        protection_close_on = (TextView) findViewById(R.id.protection_close_on);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction()==KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (protection == false) {
                    protection = true;
                    protection_close_on.setText("开启");
                    Settings.Secure.putInt(Security_Password_Activity.this.getContentResolver(),"password_protect_state",1);
                } else {
                    protection = false;
                    protection_close_on.setText("关闭");
                    Settings.Secure.putInt(Security_Password_Activity.this.getContentResolver(),"password_protect_state",0);
                }
                Log.e(TAG, "onKey: 按下了左右键");
            }
            if ((protection == true) && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                Log.e(TAG,"按下了确定键");
                Intent intent = new Intent();
                intent.setClass(Security_Password_Activity.this, Input_Password_Activity.class);
                startActivity(intent);
            }
        } else if (event.getAction()==KeyEvent.ACTION_DOWN) {
            return false;
        } else return false;
        return false;
    }

}
