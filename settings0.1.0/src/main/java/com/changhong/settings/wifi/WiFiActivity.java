package com.changhong.settings.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.settings.R;
import com.changhong.settings.utils.WifiAdminUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WiFiActivity extends Activity {
    private final static String TAG = "WiFiActivity";
    private Context mContext = WiFiActivity.this;

    private static final int REFRESH_CONN = 100;
    // Wifi管理类
    private WifiAdminUtils mWifiAdmin;
    // 扫描结果列表
    private List<ScanResult> list = new ArrayList<>();
    // 显示列表
    private ListView listView;
    private MyListViewAdapter mAdapter;
    //下标
    private int mPosition;
    private WifiReceiver mReceiver;
    private WifiManager wifiManager;
    private View pb_wifi;
    private ImageView wifiConnect;
    private WifiConnDialog mDialog = null;
    //网络连接状态改变
    private ScanResult mScan;
    private OnNetworkChangeListener mOnNetworkChangeListener = new OnNetworkChangeListener() {

        @Override
        public void onNetWorkDisConnect() {
            Log.e(TAG, "onNetWorkDisConnect: ============getWifiListInfobefore===========" );
            getWifiListInfo();
            Log.e(TAG, "onNetWorkDisConnect: ============getWifiListInfoafter===========" );
            mAdapter.setDatas(list);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNetWorkConnect() {
            Log.e(TAG, "onNetWorkConnect: ============getWifiListInfobefore===========" );
            getWifiListInfo();//WifiListInfo();
            Log.e(TAG, "onNetWorkConnect: ============getWifiListInfoafter===========" );
            mAdapter.setDatas(list);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_wifi);

        //初始化view
        initView();
        listView.requestFocus();

        //设置监听
        setListener();
        //定时刷新监听
        refreshWifiStatusOnTime();
        mHandler.sendEmptyMessageDelayed(REFRESH_CONN,10000);
        registerReceiver();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        mWifiAdmin = new WifiAdminUtils(mContext);
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
//        TextView tv_title = (TextView) findViewById(R.id.tv_title);
//        tv_title.requestFocus();
        listView = (ListView) findViewById(R.id.wifi_setting_list);
        pb_wifi = findViewById(R.id.custom_dialog_progress);
        pb_wifi.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
//        getWifiListInfo();//要删除
        Log.e(TAG, "initView: 11111111111111111111111111111");
        /*listView.setFocusable(true);
        listView.setFocusableInTouchMode(true);
        listView.requestFocus();*/
        mAdapter = new MyListViewAdapter(mContext, list);
        listView.setAdapter(mAdapter);
        //检查当前wifi状态

        if (!wifiManager.isWifiEnabled()) {

        }
    }

    private void getWifiListInfo() {
        Log.d(TAG, "getWifiListInfo");
        if (wifiManager.isWifiEnabled()) {
            mWifiAdmin.startScan();
            List<ScanResult> tmpList = mWifiAdmin.getWifiList();

            List<ScanResult> mylist = new ArrayList<ScanResult>();
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String g1 = wifiInfo.getSSID();
            Log.e(TAG, "getWifiListInfo: wifi的Service Set Identifier = "+g1 );
            if (g1 != null) {
                g1 = g1.replaceAll("\"", "");
            }
            int i = 0;
            for (ScanResult scanResult : tmpList) {
                Log.e(TAG, "getWifiListInfo1111: " +" %%%%% " + scanResult.SSID);
                if (g1 != null && scanResult.SSID != null && scanResult.SSID.equals(g1)) {
                    //Log.i(TAG, "getWifiListInfo: add connected data");
                    mylist.add(0, scanResult);
                    i++;
                    break;
                }

            }

            for (ScanResult scanResult : tmpList) {
                if (g1 != null && scanResult.SSID != null && scanResult.SSID.equals(g1)) {
                    //Log.i(TAG, "getWifiListInfo: " + scanResult.SSID);
                    continue;
                }
//                Log.i(TAG, "getWifiListInfo 1: " + scanResult.SSID);
                if (!TextUtils.isEmpty(scanResult.SSID)){
//                    Log.i(TAG, "getWifiListInfo: scanResult.SSID="+scanResult.SSID+" BSSID == "+scanResult.BSSID);
                    mylist.add(i, scanResult);
//                    pb_wifi.setVisibility(View.GONE);
                    i++;
                }else {
                    Log.i(TAG, "getWifiListInfo: ssid is empty "+scanResult.SSID);
                }
            }
            if (mylist == null) {
                list.clear();
            } else {
               /* Collections.sort(mylist, new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult o1, ScanResult o2) {
                        int j = o2.level - o1.level;
                        //Log.i(TAG, "compare: the level1 is "+o1.level+" and the level2 is "+o2.level +" the result is "+j);
                        return j;
                    }
                });*/

                Log.i(TAG, "getWifiListInfo: ---------------------------start--------------------------");
                List<ScanResult> mylist2 = new ArrayList<ScanResult>();
                int a=0;
                for(ScanResult scanResult:mylist){
                    Log.i(TAG, "getWifiListInfo: scanResult.SSID="+scanResult.SSID);
                    if (!TextUtils.isEmpty(scanResult.SSID)){
                        mylist2.add(a,scanResult);
                        a=a+1;
                    }
                }
                Log.i(TAG, "getWifiListInfo: ---------------------------end--------------------------");
                list.clear();
                list = mylist2;
                listView.setVisibility(View.VISIBLE);
                pb_wifi.setVisibility(View.GONE);
            }
        }
    }
    private void sortScanresult(){

    }

    public boolean isMac(String macStr) {
        boolean bool = false;
        if (macStr == null) {
            return false;
        }
        macStr = macStr.trim().replaceAll("\\s", "").toUpperCase();
        if (macStr.contains(":")) {
            bool = macStr.matches("^[\\da-fA-F]{2}(:[\\da-fA-F]{2}){5}$");
        }
        return bool;
    }

    /**
     * 设置监听事件
     */
    private void setListener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                mPosition = pos - 1;

                ScanResult scanResult = list.get(mPosition + 1);
                String desc = "";
                String descOri = scanResult.capabilities;
                if (descOri.toUpperCase().contains("WPA-PSK")) {
                    desc = "通过WPA进行保护";
                }
                if (descOri.toUpperCase().contains("WPA2-PSK")) {
                    desc = "通过WPA2进行保护";
                }
                if (descOri.toUpperCase().contains("WPA-PSK")
                        && descOri.toUpperCase().contains("WPA2-PSK")) {
                    desc = "通过WPA/WPA2进行保护";
                }

                if (desc.equals("")) {
                    isConnectSelf(scanResult);
                    return;
                }
                isConnect(scanResult);
            }

            /**
             * 有密码验证连接
             * @param scanResult
             */
            private void isConnect(ScanResult scanResult) {
                mScan = scanResult;
                if (mWifiAdmin.isConnect(scanResult)) {
                    // 已连接，显示连接状态对话框
                    WifiStatusDialog mStatusDialog = new WifiStatusDialog(mContext, android.R.style.
                            Theme_Dialog, scanResult, mOnNetworkChangeListener);
                    mStatusDialog.show();
                } else {
                    // 未连接显示连接输入对话框

                    WifiConfiguration conf = null;
                    if (scanResult.SSID != null) {
                        conf = WifiAdminUtils.isExsits(scanResult.SSID);
                    }
                    if (conf != null) {
                        // mConnectConfig = wifiManager.enableNetwork(conf.networkId, true);
                        new WifiConnect_savedDialog(mContext, android.R.style.Theme_Dialog,
                                scanResult, conf
                                , mOnNetworkChangeListener).show();
                    } else {
                        mDialog = new WifiConnDialog(mContext, android.R.style.Theme_Dialog,
                                listView, mPosition, mAdapter,
                                scanResult, list, mOnNetworkChangeListener);
                        Log.e(TAG, "cyj:123 ");
                        mDialog.show();
                        Log.e(TAG, "cyj:1234 ");
//                    }
                    }
                }
            }

            /**
             * 无密码直连
             * @param scanResult
             */
            private void isConnectSelf(ScanResult scanResult) {
                if (mWifiAdmin.isConnect(scanResult)) {
                    // 已连接，显示连接状态对话框
                    WifiStatusDialog mStatusDialog = new WifiStatusDialog(mContext, android.R.style.
                            Theme_Dialog,
                            scanResult, mOnNetworkChangeListener);
                    mStatusDialog.show();
                } else {
                    WifiConnect_NopassDialog nopassDialog = new WifiConnect_NopassDialog
                            (mContext, android.R.style.Theme_Dialog, scanResult);
                    nopassDialog.show();
                    //
                }
            }
        });
    }

    private Handler mHandler = new MyHandler(this);

    protected boolean isUpdate = true;

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.d("cheng", "onstop??????????????");
        isUpdate = false;
    }

    private static class MyHandler extends Handler {

        private WeakReference<WiFiActivity> reference;

        public MyHandler(WiFiActivity activity) {
            this.reference = new WeakReference<WiFiActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            WiFiActivity activity = reference.get();

            switch (msg.what) {
                case REFRESH_CONN:
                    Log.d(TAG, "excuted!!!!!");
                    Log.e(TAG, "handleMessage: ============getWifiListInfobefore===========" );
                    activity.getWifiListInfo();
                    Log.e(TAG, "handleMessage: ============getWifiListInfobefore===========" );
                    activity.mAdapter.setDatas(activity.list);
                    activity.mAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 定时刷新Wifi列表信息
     *
     * @author fdw
     */
    private void refreshWifiStatusOnTime() {
        new Thread() {
            public void run() {
                while (isUpdate) {
                    try {

                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(REFRESH_CONN);
                }
            }
        }.start();
    }

    private void registerReceiver() {
        mReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isUpdate = false;
        unregisterReceiver();
    }

    /**
     * 取消广播
     */
    private void unregisterReceiver() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

    private class WifiReceiver extends BroadcastReceiver {
        protected static final String TAG = "WifiSettingActivity";
        //记录网络断开的状态
        private boolean isDisConnected = false;
        //记录正在连接的状态
        private boolean isConnecting = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                // wifi连接上与否
                Log.d(TAG, "网络已经改变");
                NetworkInfo info = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState() != null && info.getState().equals(NetworkInfo.State.DISCONNECTING)) {
                    Log.d(TAG, "wifi正在断开");
                } else if (info.getState() != null && info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    if (!isDisConnected) {
                        Log.d(TAG, "wifi has been disconnected ###############3");
                        isDisConnected = true;
                    }
                } else if (info.getState() != null && info.getState().equals(NetworkInfo.State.CONNECTING)) {
                    if (!isConnecting) {
                        Log.d(TAG, "正在连接...");
                        Toast.makeText(mContext, "正在连接...", Toast.LENGTH_SHORT).show();
                        isConnecting = true;
                    }
                } else if (info.getState() != null && info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    Toast.makeText(mContext, "连接到网络:" + wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "连接到网络：" + wifiInfo.getBSSID());

//                    if (mDialog != null) {
////                        mDialog.dismiss();
//                    }
                }

            } else if (intent.getAction() != null && intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
                switch (error) {
                    case WifiManager.ERROR_AUTHENTICATING:
                        Log.d(TAG, "密码认证错误Code为：" + error);
                        Toast.makeText(mContext, "wifi密码错误！", Toast.LENGTH_SHORT).show();
//                        if (mDialog != null) {
////                            mDialog.edtPassword.setText("");
//                            WifiConfiguration conf = null;
//                            if (mScan != null && mScan.SSID != null) {
////                                conf = WifiAdminUtils.isExsits(mScan.SSID);
//                            }
//                            if (conf != null) {
//                                int netId = conf.networkId;
//                                wifiManager.removeNetwork(netId);
//                                wifiManager.saveConfiguration();
////                                mDialog.show();
//                            }
//                        }
                        break;
                    default:
                        break;
                }
                // 监听wifi的打开与关闭，与wifi的连接无关
            } else if (intent.getAction() != null && intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Log.e("info", "wifiState" + wifiState);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLING:
                        Log.d(TAG, "wifi is enabling");

                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.d(TAG, "Wifi has been enabled");


                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        Log.e("TAG", "wifi is closing");

                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.e("TAG", "wifi has been clesed");

                        break;
                }
            } else if (intent.getAction() != null && intent.getAction().equals("android.net.wifi.SCAN_RESULTS")) {
                //Log.i(TAG, "onReceive: scan Result $$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                pb_wifi.setVisibility(View.GONE);
                Log.e(TAG, "WifiReceiver: ============getWifiListInfobefore===========" );
                getWifiListInfo();
                Log.e(TAG, "WifiReceiver: ============getWifiListInfoafter===========" );
                mAdapter.setDatas(list);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
