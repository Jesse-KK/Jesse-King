package com.changhong.settings.Security;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.changhong.settings.R;

public class Input_Password_Activity extends Activity implements View.OnClickListener {
    private EditText input_password_protection;
    private Button button_display_password, button_confirm_input_password;
    private boolean isDisplay_password = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password);
        init();
    }

    private void init() {
        input_password_protection = (EditText) findViewById(R.id.input_password_protection);
        input_password_protection.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
        button_display_password = (Button) findViewById(R.id.button_display_password);
        button_confirm_input_password = (Button) findViewById(R.id.button_confirm_input_password);


        button_display_password.setOnClickListener(this);
        button_confirm_input_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_display_password:
                if (isDisplay_password == false) {
                    input_password_protection.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    button_display_password.setText("隐藏");
                    isDisplay_password = true;
                } else {
                    input_password_protection.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    button_display_password.setText("显示");
                    isDisplay_password = false;
                }
                break;
            case R.id.button_confirm_input_password:
                Editable s = input_password_protection.getText();
                String ss = s.toString();
                Settings.Secure.putString(Input_Password_Activity.this.getContentResolver(), "password_protect", ss);
                Intent intent = new Intent();
                intent.setClass(Input_Password_Activity.this, Security_Password_Activity.class);
                startActivity(intent);
                Input_Password_Activity.this.finish();
                break;
        }
    }
}
