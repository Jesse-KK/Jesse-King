package com.changhong.settings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BluetoothReceiver extends BroadcastReceiver {
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> bondDevices;
    private static List<Map<String, Object>> pairedDevicesList = new ArrayList<Map<String, Object>>();
    private static List<Map<String, Object>> allDevicesList = new ArrayList<Map<String, Object>>();


    Context paraContext;

    public BluetoothReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        paraContext = context;
        String action = intent.getAction();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (null == mBluetoothAdapter) {
            Toast.makeText(context, "本设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            //finish();
        }
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i("cheng", "I have receive the Boot receiver");
           /* first = true;
        Intent intent1 = new Intent(context, Settings.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);*/
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
                Log.i("Cheng", "I have open the bluetooth");
                //Toast.makeText(Settings.this, "收到开机广播", Toast.LENGTH_SHORT).show();
                quitBond();
                if (!mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.startDiscovery();
                }

                // showControlView(true);
            } else {
                Constent.LOGI("Bluetooth is enabled .");
                /*if (pairedDevicesList!=null) {
                    for (int i = 0; i < pairedDevicesList.size(); i++) {
                        BluetoothDevice tmpDevice = (BluetoothDevice) pairedDevicesList.get(i).get(Constent.ListDeviceInfo);

                        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(tmpDevice.getAddress());
                        if (remoteDevice == null) {
                            Constent.LOGE("onclick get remoteDevice  null .");
                            return;
                        }
                        Log.i("MyReceiver", "Enter the loop++name: " + remoteDevice.getName());

                        if (remoteDevice.getName() != null && remoteDevice.getName().equals("MobileBoxRemote")) {
                            Log.i("MyReceiver", "get device");
                            Log.i("MyReceiver", "stop Bonding!");
                            //Toast.makeText(paraContext, "请按下“OK”键与遥控器配对", Toast.LENGTH_SHORT).show();
                            remoteDevice.removeBond();
                        }
                    }
                }*/
                quitBond();
                if (!mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.startDiscovery();
                }
                //showControlView(true);
            }
        }/*else if(action.equals("android.intent.action.ACTION_SHUTDOWN")){
            Log.i("cheng","shutdown");
            mBluetoothAdapter.disable();
        }*/
        //找到设备
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Constent.LOGI("Bluetooth STATE_OFF .");

                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Constent.LOGI("Bluetooth STATE_TURNING_OFF .");
                    break;
                case BluetoothAdapter.STATE_ON:

                    Constent.LOGI("Bluetooth STATE_ON .");
                    if (mBluetoothAdapter.isEnabled()) {
                        if (!mBluetoothAdapter.isDiscovering()) {
                            mBluetoothAdapter.startDiscovery();
                        }
                        mBluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 86400000);
                    }
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:

                    Constent.LOGI("Bluetooth STATE_TURNING_ON ");

                    break;
            }


        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (device == null) {
                Constent.LOGI("get one  null bluetooth .");
                return;
            }

            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                if (device.getName() == null && device.getAddress() == null) {

                    Constent.LOGI("get one name null && add null bluetooth .");
                    //Constent.LOGI("device.getUuids：" + device.getUuids().toString());
//                        Constent.LOGI("device.getAddress：" + device.getAddress());
                    return;

                }

                for (int j = 0; j < pairedDevicesList.size(); j++) {

                    if (device.getName() == null || device.getName().isEmpty()) {
                        if (device.getAddress().equals(pairedDevicesList.get(j).get(Constent.ListDeviceAddress))) {
                            BluetoothDevice bluetoothDevice = (BluetoothDevice) pairedDevicesList.get(j).get(Constent.ListDeviceInfo);

                            if (bluetoothDevice != null && bluetoothDevice.getAddress().equals(device.getAddress())) {

                                Constent.LOGI("get one paired repeat bluetooth ,return .");
                                return;
                            }

                            Constent.LOGI("get paired two same address bluetooth -> " + device.getAddress() + " bluetoothDevice = " + bluetoothDevice);

                        }
                    }else {
                        if (device.getName().equals(pairedDevicesList.get(j).get(Constent.ListDeviceName))) {
                            BluetoothDevice bluetoothDevice = (BluetoothDevice) pairedDevicesList.get(j).get(Constent.ListDeviceInfo);

                            if (bluetoothDevice != null && bluetoothDevice.getAddress().equals(device.getAddress())) {

                                Constent.LOGI("get one paired repeat bluetooth ,return .");
                                return;
                            }

                            Constent.LOGI("get paired two same name bluetooth -> " + device.getName() + " bluetoothDevice = " + bluetoothDevice);

                        }
                    }
                }
                if (device.getName() == null || device.getName().isEmpty()) {
                    for (int i = 0; i < allDevicesList.size(); i++) {
                        if (device.getAddress().equals(allDevicesList.get(i).get("device_addr"))) {
                            BluetoothDevice bluetoothDevice = (BluetoothDevice) allDevicesList.get(i).get("device_info");

                            if (bluetoothDevice != null && bluetoothDevice.getAddress().equals(device.getAddress())) {

                                Constent.LOGI("get one repeat bluetooth ,return .");
                                return;
                            }

                            Constent.LOGI("get two same name bluetooth -> " + device.getName() + " bluetoothDevice = " + bluetoothDevice);

                        }

                    }
                } else {
                    for (int i = 0; i < allDevicesList.size(); i++) {
                        if (device.getName().equals(allDevicesList.get(i).get("device_name"))) {
                            BluetoothDevice bluetoothDevice = (BluetoothDevice) allDevicesList.get(i).get("device_info");

                            if (bluetoothDevice != null && bluetoothDevice.getAddress().equals(device.getAddress())) {

                                Constent.LOGI("get one repeat bluetooth ,return .");
                                return;
                            }

                            Constent.LOGI("get two same name bluetooth -> " + device.getName() + " bluetoothDevice = " + bluetoothDevice);

                        }

                    }
                }
                if (device.getName() == null || device.getName().isEmpty()) {
                    Constent.LOGD("in nameNull addNotNull device" + device.getAddress());
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(Constent.ListDeviceAddress, device.getAddress());
                    map.put(Constent.ListDeviceInfo, device);
                    Constent.LOGD("get pair bluetooth -> " + device.getAddress());
                    allDevicesList.add(map);
                } else {
                    Constent.LOGD("in nameNotNull device" + device.getAddress());
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(Constent.ListDeviceName, device.getName());
                    map.put(Constent.ListDeviceInfo, device);
                    Constent.LOGD("get pair bluetooth -> " + device.getName());
                    allDevicesList.add(map);
                }

            }
        }
        //搜索完成
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            for (int i = 0; i < allDevicesList.size(); i++) {
                BluetoothDevice tmpDevice = (BluetoothDevice) allDevicesList.get(i).get(Constent.ListDeviceInfo);

                BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(tmpDevice.getAddress());
                if (remoteDevice == null) {
                    Constent.LOGE("onclick get remoteDevice  null .");
                    return;
                }
                if (remoteDevice.getName() == null || remoteDevice.getName().isEmpty()) {

                } else {
                    Log.i("MyReceiver", "Enter the loop++name: " + remoteDevice.getName()
                    + "***DeviceType:" + remoteDevice.getBluetoothClass().getMajorDeviceClass());

                    if (remoteDevice.getName() != null && remoteDevice.getName().equals("MobileBoxRemote")) {
                        Log.i("MyReceiver", "get device");
                        Log.i("MyReceiver", "srart Bonding!");
                        if (pairedDevicesList.size() == 0 || remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                            Toast.makeText(paraContext, "请按下“OK”键与遥控器配对", Toast.LENGTH_SHORT).show();
                            remoteDevice.createBond();
                        }
                    }
                }
            }
        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (device.getBondState()) {
                case BluetoothDevice.BOND_BONDING://正在配对
                    if (device.getName() == null || device.getName().isEmpty()) {

                    } else {
                        Constent.LOGI(device.getName() + "   BOND_BONDING");
                    }
                    break;
                case BluetoothDevice.BOND_BONDED://配对结束
                    if (device.getName() == null || device.getName().isEmpty()) {

                    } else {
                        Constent.LOGI(device.getName() + "   BOND_BONDED");
                    }
                    moveUnpairToPairedList(device);
                    //connectDevice(device);
                    //20200528 修改， 给谢波新疆市场修改，取消配对成功提示，避免与自动配对程序的提示信息重复
                    //Toast.makeText(paraContext, "配对成功！", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.BOND_NONE://取消配对/未配对
                    if (device.getName() == null || device.getName().isEmpty()) {

                    } else {
                        Constent.LOGI(device.getName() + "   BOND_NONE");
                    }
                    movePairToUnPairList(device);
                default:
                    break;
            }

        }


    }

    private void moveUnpairToPairedList(BluetoothDevice bluetoothDevice) {
        if (pairedDevicesList == null || allDevicesList == null) {
            Constent.LOGE("updateUnPairList pairedDevicesList is " + pairedDevicesList + "  & "
                    + "  allDevicesList is " + allDevicesList + " , return .");
            return;
        }
        for (int i = 0; i < allDevicesList.size(); i++) {
            BluetoothDevice tmpdevice = (BluetoothDevice) allDevicesList.get(i).get(Constent.ListDeviceInfo);
            if (tmpdevice.getName() == null || tmpdevice.getName().isEmpty()) {
                if (tmpdevice.getAddress().equals(bluetoothDevice.getAddress())) {
                    Constent.LOGI("remove the paired device -> " + bluetoothDevice.getAddress());
                    allDevicesList.remove(i);
                }
            } else if (tmpdevice.getName().equals(bluetoothDevice.getName())
                    && tmpdevice.getAddress().equals(bluetoothDevice.getAddress())) {
                Constent.LOGI("remove the paired device -> " + bluetoothDevice.getName());
                allDevicesList.remove(i);
            }

        }

        if (bluetoothDevice.getName() == null || bluetoothDevice.getName().isEmpty()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(Constent.ListDeviceAddress, bluetoothDevice.getAddress());
            map.put(Constent.ListDeviceInfo, bluetoothDevice);

            pairedDevicesList.add(map);
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(Constent.ListDeviceName, bluetoothDevice.getName());
            map.put(Constent.ListDeviceInfo, bluetoothDevice);

            pairedDevicesList.add(map);
        }


    }

    private void movePairToUnPairList(BluetoothDevice bluetoothDevice) {
        if (pairedDevicesList == null || allDevicesList == null) {
            Constent.LOGE("updateUnPairList pairedDevicesList is " + pairedDevicesList + "  & "
                    + "  allDevicesList is " + allDevicesList + " , return .");

            return;
        }
        for (int i = 0; i < pairedDevicesList.size(); i++) {
            BluetoothDevice tmpdevice = (BluetoothDevice) pairedDevicesList.get(i).get(Constent.ListDeviceInfo);
            if (tmpdevice.getName() == null || tmpdevice.getName().isEmpty()) {
                if (tmpdevice.getAddress().equals(bluetoothDevice.getAddress())) {

                    pairedDevicesList.remove(i);

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(Constent.ListDeviceAddress, bluetoothDevice.getAddress());
                    map.put(Constent.ListDeviceInfo, bluetoothDevice);

                    allDevicesList.add(map);


                    Constent.LOGI("movePairToUnPairList -> " + bluetoothDevice.getAddress());

                }
            } else {
                if (tmpdevice.getName().equals(bluetoothDevice.getName())
                        && tmpdevice.getAddress().equals(bluetoothDevice.getAddress())) {

                    pairedDevicesList.remove(i);

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(Constent.ListDeviceName, bluetoothDevice.getName());
                    map.put(Constent.ListDeviceInfo, bluetoothDevice);

                    allDevicesList.add(map);
                    Constent.LOGI("movePairToUnPairList -> " + bluetoothDevice.getName());
                }

            }
        }
        //改自10.19
       /* Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constent.ListDeviceName, bluetoothDevice.getName());
        map.put(Constent.ListDeviceInfo, bluetoothDevice);

        allDevicesList.add(map);
        Constent.LOGI("movePairToUnPairList -> " + bluetoothDevice.getName());*/
    }
    private void quitBond(){
        if (pairedDevicesList!=null) {
            Log.i("MyReceiver","the  pairedDevicesList's size is "+pairedDevicesList.size());
            for (int i = 0; i < pairedDevicesList.size(); i++) {

                BluetoothDevice tmpDevice = (BluetoothDevice) pairedDevicesList.get(i).get(Constent.ListDeviceInfo);

                BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(tmpDevice.getAddress());
                if (remoteDevice == null) {
                    Constent.LOGE("onclick get remoteDevice  null .");
                    return;
                }
                if (remoteDevice.getName() == null || remoteDevice.getName().isEmpty()) {

                } else {
                    Log.i("MyReceiver1", "Enter the loop++name: " + remoteDevice.getName());

                    if (remoteDevice.getName() != null && remoteDevice.getName().equals("MobileBoxRemote")) {
                        Log.i("MyReceiver1", "get device");
                        Log.i("MyReceiver1", "stop Bonding!");
                        //Toast.makeText(paraContext, "请按下“OK”键与遥控器配对", Toast.LENGTH_SHORT).show();
                        remoteDevice.removeBond();
                    }
                }
            }
        }else{
            Log.i("MyReceiver","pairedDevicesList is empty "+pairedDevicesList.size());
        }
    }
}
