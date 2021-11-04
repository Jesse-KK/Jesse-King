package com.example.install_silent_4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class UsbReceiver extends BroadcastReceiver {

    private static final String TAG = "king_install";
    private String apkFileName;
    private String myPackageName;
    private String filePath;
    static String mountPath;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_MEDIA_CHECKING:
            case Intent.ACTION_MEDIA_EJECT:
            case Intent.ACTION_MEDIA_UNMOUNTED:
                break;
            case Intent.ACTION_MEDIA_MOUNTED:
                // 获取挂载路径, 读取U盘文件
                Uri uri = intent.getData();
                if (uri != null) {
                    String usbPath = uri.getPath() + "/diagnose/";
                    File rootFile = new File(usbPath);
                    int fileNum = 0;
                    for (File file : rootFile.listFiles()) {
                        if (((file.getName()) != null) && (file.getName().endsWith("apk"))) {
                            fileNum++;
                            apkFileName = file.getName();
                        }
                    }
                    if (fileNum == 1) {
                        Log.e(TAG, "U盘路径：" + usbPath);
                        Log.e(TAG, "开始安装");
                        filePath = usbPath + apkFileName;
                        //打开应用
                        openApk(context);
                    }

                }
                break;
            default:
                break;
        }
    }

    private void openApk(Context context) {
        Intent intent;
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            myPackageName = appInfo.packageName;
        }
        if (isPkgInstalled(context, myPackageName)) {
            intent = pm.getLaunchIntentForPackage(myPackageName);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
//                         e.printStackTrace();
            }
        } else {
            slientInstall(filePath);
            intent = pm.getLaunchIntentForPackage(myPackageName);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
//                         e.printStackTrace();
            }
        }
    }

    /**
     * 静默安装 并启动
     *
     * @param tempPath apk文件路径
     * @return
     */
    public boolean slientInstall(String tempPath) {
        // 进行资源的转移 将assets下的文件转移到可读写文件目录下
//		createFile();

        File file = new File(tempPath);
        boolean result = false;
        Process process = null;
        OutputStream out = null;
        if (file.exists()) {
            System.out.println(file.getPath() + "==");
            try {
                process = Runtime.getRuntime().exec("sh");
                out = process.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(out);
                // 获取文件所有权限
                dataOutputStream.writeBytes("chmod 777 " + file.getPath()
                        + "\n");
                // 进行静默安装命令
                dataOutputStream
                        .writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "
                                + file.getPath() + myPackageName);

                // 关闭流操作
                dataOutputStream.close();
                out.close();
                int value = process.waitFor();
                // 代表成功
                if (value == 0) {
                    result = true;
                    // 失败
                } else if (value == 1) {
                    result = false;
                    // 未知情况
                } else {
                    result = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!result) {
                result = true;
            }
        }
        return result;
    }

    public boolean isPkgInstalled(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}