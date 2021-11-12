package com.changhong.settings.General;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.changhong.settings.R;

public class RecoveryDialog extends Dialog {
    public static final String TAG = "RecoveryDialog Start";
    private OnCallback mCall = null;
    private Context mContext;
    private Button button_confirm_reset, button_cancel_reset;

    public static interface OnCallback {
        public void OkcallBack();

        public void CancelcallBack();

        public void MidcallBack();
    }

    public RecoveryDialog(Context context, int theme, OnCallback call) {
        super(context, theme);
        Log.i(TAG, "RecoveryDialog: ");
        mCall = call;
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_recovery);
        init();
    }

    public void init() {
        button_confirm_reset = (Button) findViewById(R.id.button_confirm_reset);
        button_cancel_reset = (Button) findViewById(R.id.button_cancel_reset);
        button_cancel_reset.requestFocus();

        button_confirm_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCall != null) {
                    mCall.OkcallBack();
                    dismiss();
                }
            }
        });
        button_cancel_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCall.CancelcallBack();
                dismiss();
            }
        });
    }
}