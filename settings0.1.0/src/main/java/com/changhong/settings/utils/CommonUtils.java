package com.changhong.settings.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class CommonUtils {

    private static final String TAG = "qll_CommonUtils";

    public static void showToastShort(Context context, String message) {

        if (context == null) {
            Log.i(TAG, "context is null , return.");
            return;
        }

        Log.i(TAG, "toast: " + message);
        if (TextUtils.isEmpty(message)) {
            Log.i(TAG, "message is null, return.");
            return;
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String message) {

        if (context == null) {
            Log.i(TAG, "context is null , return.");
            return;
        }

        Log.i(TAG, "toast: " + message);
        if (TextUtils.isEmpty(message)) {
            Log.i(TAG, "message is null, return.");
            return;
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
