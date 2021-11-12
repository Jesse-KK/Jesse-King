package com.changhong.settings;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

//import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;


public class ConnectBluetoothService extends Service {
    private static final String TAG="ConnectBluetoothService";
    private BluetoothAdapter mBluetoothAdapter;
    private Context context=ConnectBluetoothService.this;
    private BluetoothManager bluetoothManager;
    public PairedListAdapter pairedListAdapter=BluetoothMainActivity.pairedListAdapter;
    private BluetoothProfile.ServiceListener proxyListener = new BluetoothProfile.ServiceListener() {
    private List<Map<String, Object>> pairedDevicesList = BluetoothMainActivity.pairedDevicesList;
    private List<Map<String, Object>> allDevicesList = BluetoothMainActivity.allDevicesList;
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {

            Log.i(TAG, "BluetoothProfile profile: " + profile);
            if (proxy != null) {
                //获取连接的设备
                List<BluetoothDevice> connectedDeviceList = proxy.getConnectedDevices();
                if (connectedDeviceList != null) {
                    Log.i(TAG, "connectedDevices size: " + connectedDeviceList.size());
                    for (int i = 0; i < connectedDeviceList.size(); i++) {
                        BluetoothDevice connectDevice = connectedDeviceList.get(i);
                        Log.i(TAG, "ConnectedDevice : " + connectDevice.getName() + "|" + connectDevice.getAddress());
                        for (int j = 0; j < pairedDevicesList.size(); j++) {
                            BluetoothDevice pairedDevice = (BluetoothDevice) pairedDevicesList.get(j).get(Constent.ListDeviceInfo);
                            Log.i(TAG, "与 " + connectDevice.getAddress() + "相比：" + pairedDevice.getAddress());
                            if (pairedDevice.getAddress().equals(connectDevice.getAddress())) {
                                Log.i(TAG, "in set connect_state>1" + "device:" + pairedDevice.getName());
                                pairedDevicesList.get(j).put("connect_state", "1");
                            }
                        }
                    }
                    //9.2发现此处有空指针的风险，但是系统层默认开启蓝牙后未复现该问题，故没做修改
                    //20200923 此处之前就发现有空指针风险，但是未做修改，此时修改一下
                    if (pairedListAdapter != null) {
                        pairedListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
// TODO Auto-generated method stub

        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        int flag = -1;

        int a2dp = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
        int headset = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
        int health = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);
        int health1 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.GATT);
        int health2 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.GATT_SERVER);
        int health3 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
//        int health4 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEARING_AID);
        int health5 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HID_DEVICE);
        int health6 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.SAP);

        if (a2dp == BluetoothProfile.STATE_CONNECTED) {
            flag = a2dp;
            Log.e(TAG, "getConnectState2:a2dp " );
        } else if (headset == BluetoothProfile.STATE_CONNECTED) {
            flag = headset;
            Log.e(TAG, "getConnectState2:headset " );
        } else if (health == BluetoothProfile.STATE_CONNECTED) {
            flag = health;
            Log.e(TAG, "getConnectState2:health " );
        } else if (health1 == BluetoothProfile.STATE_CONNECTED) {
            flag = health1;
            Log.e(TAG, "getConnectState2:health1 " );
        } else if (health2 == BluetoothProfile.STATE_CONNECTED) {
            flag = health2;
            Log.e(TAG, "getConnectState2:health2 " );
        } else if (health3 == BluetoothProfile.STATE_CONNECTED) {
            flag = health3;
            Log.e(TAG, "getConnectState2:health3 " );
//        } else if (health4 == BluetoothProfile.STATE_CONNECTED) {
//            flag = health4;
        } else if (health5 == BluetoothProfile.STATE_CONNECTED) {
            flag = health5;
            Log.e(TAG, "getConnectState2:health5 " );
        } else if (health6 == BluetoothProfile.STATE_CONNECTED) {
            flag = health6;
            Log.e(TAG, "getConnectState2:health6 " );
        }
        Log.e(TAG, "flag is " + flag);
        if (flag != -1) {
            mBluetoothAdapter.getProfileProxy(context, proxyListener, flag);
        }
    }

    @Override
    public Context getBaseContext() {
        return super.getBaseContext();
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
