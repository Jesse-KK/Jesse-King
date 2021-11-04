package com.example.install_silent_4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            //  Toast.makeText(context,"升级了一个安装包，重新启动此程序", Toast.LENGTH_SHORT).show();
            String packageName = intent.getDataString();
            Log.e("更新成功", "" + packageName);

        }
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
            System.out.println("安装了:" + packageName + "包名的程序");


        }
        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString();
            System.out.println("卸载了:" + packageName + "包名的程序");

        }

    }
}