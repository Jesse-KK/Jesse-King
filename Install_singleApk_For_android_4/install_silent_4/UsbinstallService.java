package com.example.install_silent_4;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;

public class UsbinstallService extends Service {
    private static final String TAG = "king_service";
    private String apkFileName;
    private String myPackageName;
    private String filePath;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File rootFile = new File(UsbReceiver.mountPath);
                for (File file : rootFile.listFiles()) {
                    if ((file.getName()) != null) {
                        Log.e(TAG, "run: hhh"+file.length() );
                        Log.e(TAG, "U盘路径：" + UsbReceiver.mountPath);
                        Log.e(TAG, "开始安装");
                        apkFileName = file.getName();
                        filePath = UsbReceiver.mountPath + apkFileName;
                    }

                }
                Log.e(TAG, "run: " + rootFile.length());

            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }
}
