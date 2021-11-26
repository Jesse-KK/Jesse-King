package com.changhong.settings.Image_Sound;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.changhong.settings.General.RecoveryDialog;
import com.changhong.settings.R;

public class ResolutionDialog extends Dialog {
    public static final String TAG = "RecoveryDialog Start";
    private RecoveryDialog.OnCallback mCall = null;
    private Context mContext;
    private TextView text_resolution;
    private Button button_confirm_change_resolution, button_cancel_change_resolution;

    public static interface OnCallback {
        public void OkcallBack();

        public void CancelcallBack();

        public void MidcallBack();
    }

    public ResolutionDialog(Context context, int theme, RecoveryDialog.OnCallback call) {
        super(context, theme);
        Log.i(TAG, "RecoveryDialog: ");
        mCall = call;
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_resolution);
        init();
    }

    public void init() {
        button_confirm_change_resolution = (Button) findViewById(R.id.button_confirm_change_resolution);
        button_cancel_change_resolution = (Button) findViewById(R.id.button_cancel_change_resolution);
        text_resolution = (TextView)findViewById(R.id.text_resolution);
        button_cancel_change_resolution.requestFocus();

        button_confirm_change_resolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCall != null) {
                    mCall.OkcallBack();
                    dismiss();
                }
            }
        });
        button_cancel_change_resolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCall.CancelcallBack();
                dismiss();
            }
        });
    }
    public void SetText(String text) {
        text_resolution.setText(text);
    }
}
