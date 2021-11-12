package com.changhong.settings.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.VisibleForTesting;


import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static android.content.Context.WIFI_SERVICE;
import static android.security.KeyStore.getApplicationContext;

/**
 * Created by Cheng on 2017/8/21.
 */
public class WifiAPUtil {
    private static final String TAG = "qll_wifiaputil";
    public static final boolean DEBUG = true;
    public static final int MESSAGE_AP_STATE_ENABLED = 1;

    public static final int MESSAGE_AP_STATE_FAILED = 2;
    private static final String DEFAULT_PASSWORD = "12345678";
    private static WifiAPUtil sInstance;

    public static Handler mHandler;
    private static Context mContext;
    private WifiManager mWifiManager;

    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";

    public static final int WIFI_AP_STATE_DISABLING = 10;

    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;

    @VisibleForTesting

    public  enum WifiSecurityType {
        WIFICIPHER_NOPASS, WIFICIPHER_WPA, WIFICIPHER_WEP, WIFICIPHER_INVALID, WIFICIPHER_WPA2
    }

    public WifiAPUtil(Context context) {
        if (DEBUG) {
            Log.d(TAG, "WifiAPutils construct");
        }
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        // IntentFilter filter = new IntentFilter();
        //filter.addAction(WIFI_AP_STATE_CHANGED_ACTION);
        //context.registerReceiver(mWifiStateBroadcastReceiver, filter);
    }


    protected void finalize() {
        if (DEBUG) Log.d(TAG, "finalize");
        // mContext.unregisterReceiver(mWifiStateBroadcastReceiver);
    }

    public static WifiAPUtil getInstance(Context c) {
        if (null == sInstance) {
            sInstance = new WifiAPUtil(c);
        }
        return sInstance;
    }

    //开启热点
    public boolean turnOnWifiAP(String str, String password, WifiSecurityType type) {
        Log.e(TAG, "come in" );
        String ssid = str;
        WifiConfiguration wcfg = new WifiConfiguration();
        wcfg.SSID = new String(ssid);
        wcfg.networkId = 1;
        wcfg.allowedAuthAlgorithms.clear();
        wcfg.allowedGroupCiphers.clear();
        wcfg.allowedKeyManagement.clear();
        wcfg.allowedPairwiseCiphers.clear();
        wcfg.allowedProtocols.clear();
        if (type == WifiSecurityType.WIFICIPHER_NOPASS) {
            if (DEBUG) {
                Log.d(TAG, "WifiAP is NOPASS");
            }
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN, true);
            wcfg.wepKeys[0] = "";
            wcfg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wcfg.wepTxKeyIndex = 0;
        } else if (type == WifiSecurityType.WIFICIPHER_WPA) {
            if (DEBUG) Log.d(TAG, "WifiAP is WPA");
            //判断密码必须大于8位，否则使用默认密码。
            if (null != password && password.length() >= 8) {
                wcfg.preSharedKey = password;
            } else {
                wcfg.preSharedKey = DEFAULT_PASSWORD;
            }
            wcfg.hiddenSSID = false;
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wcfg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        } else if (type == WifiSecurityType.WIFICIPHER_WPA2) {
            if (DEBUG) Log.d(TAG, "WifiAP is WPA2");
            //判断密码必须大于8位，否则额使用默认密码。
            if (null != password && password.length() >= 8) {
                wcfg.preSharedKey = password;
            } else {
                wcfg.preSharedKey = DEFAULT_PASSWORD;
            }
            wcfg.hiddenSSID = false;
            wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wcfg.allowedKeyManagement.set(4);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wcfg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wcfg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wcfg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        }
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            Method method = mWifiManager.getClass().getMethod("setWifiApConfiguration", wcfg.getClass());
            boolean rt = (boolean) method.invoke(mWifiManager, wcfg);
            if (DEBUG) Log.d(TAG, "rt=" + rt);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
//        startTethering();
        return setWifiApEnabled();
//        return false;
    }

    //获取热点状态
    public int getWifiApState() {
        int state = -1;
        try {
            Method method2 = mWifiManager.getClass().getMethod("getWifiApState");
            state = (int) method2.invoke(mWifiManager);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        if (DEBUG) Log.i("WifiAP", "getWifiApState.state" + state);
        return state;
    }


    public boolean setWifiApEnabled() {
//开启热i点时需要wifi关闭
        WifiManager wifiManager= (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.disconnect();
        wifiManager.setWifiEnabled(false);
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mConnectivityManager.stopTethering(0);
        final ConnectivityManager.OnStartTetheringCallback mOnStartTetheringCallback =
                new ConnectivityManager.OnStartTetheringCallback() {
                    @Override
                    public void onTetheringFailed() {
                        super.onTetheringFailed();
                        Log.e(TAG, "onTetheringFailed: " );
                    }
                    @Override
                    public void onTetheringStarted() {
                        super.onTetheringStarted();
                        Log.e(TAG, "onTetheringStarted: " );
                    }
                };
        while (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_DISABLED) {
            mWifiManager.setWifiEnabled(false);
            try {
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
        //确保wifi热点已经关闭
//        while (getWifiApState() != WIFI_AP_STATE_DISABLED) {
//            try {
//                Log.e(TAG, "getWifiApState: "+getWifiApState());
//                Method method1 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
//                method1.invoke(mWifiManager, null, false);

//                Thread.sleep(30000);
//                Log.e(TAG, "getWifiApState: "+getWifiApState());
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
        //开启wifi热点
        try {
//            mConnectivityManager.stopTethering(ConnectivityManager.TETHERING_WIFI);
//            Method method1 = mConnectivityManager.getClass().getMethod("startTethering", Integer.class, boolean.class,clazz,Handler.class);
////////            Log.e(TAG, "method1: "+method1 );
////////            method1.invoke(0, true /* showProvisioningUi */,
////////                    mOnStartTetheringCallback, new Handler(Looper.getMainLooper()));
            //ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            mConnectivityManager.startTethering(0, true, mOnStartTetheringCallback, new Handler(Looper.getMainLooper()));
            Log.e(TAG, "我的wifi热点已经为你打开" );
            /*mConnectivityManager.startTethering(TETHERING_WIFI, true *//* showProvisioningUi *//*,
                    null, new Handler(Looper.getMainLooper()));*/

//            Method method1 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
//
//            method1.invoke(mWifiManager, null, true);
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    //关闭wifi热点
    public void closeWifiAp() {
        if (getWifiApState() != WIFI_AP_STATE_DISABLED) {

            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
//                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
                ConnectivityManager connectivityManager= (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//                Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
////                method2.invoke(mWifiManager, config, false);
                connectivityManager.stopTethering(0);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }


    //获取热点ssid
    public String getValidApSsid() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration) method.invoke(mWifiManager);
            return configuration.SSID;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取焦点密码
    public String getValidPassword() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration) method.invoke(mWifiManager);
            return configuration.preSharedKey;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取热点安全类型
    public WifiSecurityType getValidSecurity() {
        WifiConfiguration configuration;

        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            configuration = (WifiConfiguration) method.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
            return WifiSecurityType.WIFICIPHER_INVALID;
        }
        if (DEBUG) Log.i(TAG, "getSecurity security=" + configuration.allowedKeyManagement);
        if (configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE)) {
            return WifiSecurityType.WIFICIPHER_NOPASS;
        } else if (configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return WifiSecurityType.WIFICIPHER_WPA;
        } else if (configuration.allowedKeyManagement.get(4)) {
            return WifiSecurityType.WIFICIPHER_WPA2;
        }
        return WifiSecurityType.WIFICIPHER_INVALID;
    }

}
