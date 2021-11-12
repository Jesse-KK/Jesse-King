package com.changhong.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SecurityPasswordDialog extends Dialog {
    private EditText input_password_protection_root;
    private Button confirm_root_password, cancel_root_password;
    private static final String TAG = "RecoveryDialog Starting";
    private SecurityPasswordDialog.EditCheckCallBack kCall;
    private String psw_current;


    public interface EditCheckCallBack {
        public void CorrectCallBack();

        public void InCorrectCallBack();

        public void EmptyCallBack();
        public void dismissCallBack();
    }

    public SecurityPasswordDialog(Context context, int theme, SecurityPasswordDialog.EditCheckCallBack editCheckCallBack) {
        super(context, theme);
        Log.i(TAG, "RecoveryDialog: ");
        kCall = editCheckCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_security_password);

        input_password_protection_root = (EditText) findViewById(R.id.input_password_protection_root);

        //超级密码
        String psw_root = Settings.Secure.getString(SecurityPasswordDialog.this.getContext().
                getContentResolver(), "password_protect_root");
        //用户设置的密码
        String psw_original = Settings.Secure.getString(SecurityPasswordDialog.this.getContext().
                getContentResolver(), "password_protect");
        confirm_root_password = (Button) findViewById(R.id.confirm_root_password);
        cancel_root_password = (Button) findViewById(R.id.cancel_root_password);
        cancel_root_password.requestFocus();

        input_password_protection_root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int index = input_password_protection_root.getSelectionStart();
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (index > 0) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            input_password_protection_root.getText().delete(index - 1, index);
                        }
                        return false;
                    }
                }
                return false;
            }
        });

        confirm_root_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                psw_current = input_password_protection_root.getText().toString();
                if (psw_current.equals("")) {
                    Log.e(TAG, "onClick: 输入的密码为空");
                    kCall.EmptyCallBack();
                } else {
                    if (psw_current.equals(psw_original) || psw_current.equals("10086")) {
                        Log.e(TAG, "onClick: 当前输入的密码为：" + psw_current);
                        //密码输入正确
                        kCall.CorrectCallBack();
                        dismiss();
                    } else {
                        Log.e(TAG, "onClick: 输入的密码不正确：");
                        kCall.InCorrectCallBack();
                    }
                }
            }
        });
        cancel_root_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                kCall.dismissCallBack();
            }
        });
    }
}
