package com.changhong.settings.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.util.Log;

import com.changhong.settings.R;

public class WifiUtils {
    private static final String TAG = "qll_WifiUtils";
    static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;

    enum PskType {
        UNKNOWN,
        WPA,
        WPA2,
        WPA_WPA2
    }

    public  static String getSecurityString(Context context, ScanResult scanResult, boolean strNoPsk) {
        Log.i(TAG, "!!-------getSecurityString-----!!");

        if (context == null) {
            Log.i(TAG, "context is null, return.");
            return "";
        }
        PskType pskType = PskType.UNKNOWN;
        int security = -1;
        if (scanResult != null) {
            security = getSecurity(scanResult);
            pskType = getPskType(scanResult);
        } else {
            Log.i(TAG, "scanResult is null.");
        }
        switch(security) {
            case SECURITY_EAP:
                return strNoPsk ? context.getString(R.string.wifi_security_short_eap) :
                        context.getString(R.string.wifi_security_eap);
            case SECURITY_PSK:
                switch (pskType) {
                    case WPA:
                        return strNoPsk ? context.getString(R.string.wifi_security_short_wpa) :
                                context.getString(R.string.wifi_security_wpa);
                    case WPA2:
                        return strNoPsk ? context.getString(R.string.wifi_security_short_wpa2) :
                                context.getString(R.string.wifi_security_wpa2);
                    case WPA_WPA2:
                        return strNoPsk ? context.getString(R.string.wifi_security_short_wpa_wpa2) :
                                context.getString(R.string.wifi_security_wpa_wpa2);
                    case UNKNOWN:
                    default:
                        return strNoPsk ? context.getString(R.string.wifi_security_short_psk_generic)
                                : context.getString(R.string.wifi_security_psk_generic);
                }
            case SECURITY_WEP:
                return strNoPsk ? context.getString(R.string.wifi_security_short_wep) :
                        context.getString(R.string.wifi_security_wep);
            case SECURITY_NONE:
            default:
                return strNoPsk ? "" : context.getString(R.string.wifi_security_none);
        }
    }

    private static PskType getPskType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return PskType.WPA_WPA2;
        } else if (wpa2) {
            return PskType.WPA2;
        } else if (wpa) {
            return PskType.WPA;
        } else {
            Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
            return PskType.UNKNOWN;
        }
    }

    private static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    public static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }
}
