package com.changhong.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.settings.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.changhong.settings.MyApplication.flag;
public class BluetoothMainActivity extends Activity {
    ProgressBar progress;

    private static WindowManager wm;
    private static WindowManager.LayoutParams params;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private View selectView;
    private TextView tvOpenClose,tv_bonded_title;
    private TextView tvCanBeFind;
    private RelativeLayout btnStopScan;
    private ListView lvAllDeviceList;
    private ListView lvPairedDeviceList;
    private LinearLayout llScanView;
    private LinearLayout llControlView;

    private AllListAdapter allListAdapter;
    public static PairedListAdapter pairedListAdapter;
    private Set<BluetoothDevice> bondDevices;

    public static List<Map<String, Object>> pairedDevicesList = new ArrayList<Map<String, Object>>();
    public static List<Map<String, Object>> allDevicesList = new ArrayList<Map<String, Object>>();
    private static final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private String TAG = "chbluetoothset_connect";
    private BluetoothProfile mProfile;

    //private int connect_position;
    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice connectDevice;

    private BluetoothSocket mBlueSocket;
    private Context mContext = BluetoothMainActivity.this;

    //源码方式
    private BluetoothProfile mBluetoothProfile;
    private BluetoothDevice mConnectDevice;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            //找到设备
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//                llControlView.setVisibility(View.VISIBLE);
//                llScanView.setVisibility(View.VISIBLE);
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:

                        Constent.LOGI("Bluetooth STATE_OFF .");
//                        llControlView.setVisibility(View.INVISIBLE);
//                        llScanView.setVisibility(View.INVISIBLE);

//                        tvOpenClose.setClickable(true);
//                        tvOpenClose.setTextColor(getResources().getColor(R.color.white));


                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        Constent.LOGI("Bluetooth STATE_TURNING_OFF .");
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
//                        llControlView.setVisibility(View.INVISIBLE);
//                        llScanView.setVisibility(View.INVISIBLE);

                        break;
                    case BluetoothAdapter.STATE_ON:

                        Constent.LOGI("Bluetooth STATE_ON .");
                        //2019.2.22修改
                        //在打开时显示已配对设备
                        showControlView(true);

//                        llScanView.setVisibility(View.VISIBLE);
                        if (mBluetoothAdapter.isEnabled()) {
                            mBluetoothAdapter.startDiscovery();
//                            tvOpenClose.setClickable(true);
//                            tvOpenClose.setTextColor(getResources().getColor(R.color.white));
//                            if (tvCanBeFind.getText() != null && tvCanBeFind.getText().toString().equals(getResources().getString(R.string.open))) {
//                                mBluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 86400000);
//                            } else {
                                mBluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE, 86400000);
//                            }
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:

                        Constent.LOGI("Bluetooth STATE_TURNING_ON ");
//                        llControlView.setVisibility(View.VISIBLE);

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
                        } else {
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
//                    if (device.getName() == null || device.getName().isEmpty()) {
//                        for (int i = 0; i < allDevicesList.size(); i++) {
//                            if (device.getAddress().equals(allDevicesList.get(i).get("device_addr"))) {
//                                BluetoothDevice bluetoothDevice = (BluetoothDevice) allDevicesList.get(i).get("device_info");
//
//                                if (bluetoothDevice != null && bluetoothDevice.getAddress().equals(device.getAddress())) {
//
//                                    Constent.LOGI("get one repeat bluetooth ,return .");
//                                    return;
//                                }
//
//                                Constent.LOGI("get two same name bluetooth -> " + device.getName() + " bluetoothDevice = " + bluetoothDevice);
//
//                            }
//
//                        }
//                    } else {
//                        for (int i = 0; i < allDevicesList.size(); i++) {
//                            if (device.getName().equals(allDevicesList.get(i).get("device_name"))) {
//                                BluetoothDevice bluetoothDevice = (BluetoothDevice) allDevicesList.get(i).get("device_info");
//
//                                if (bluetoothDevice != null && bluetoothDevice.getAddress().equals(device.getAddress())) {
//
//                                    Constent.LOGI("get one repeat bluetooth ,return .");
//                                    return;
//                                }
//
//                                Constent.LOGI("get two same name bluetooth -> " + device.getName() + " bluetoothDevice = " + bluetoothDevice);
//
//                            }
//
//                        }
//                    }

                    //19 2.22如此判断可以使蓝牙设置在搜索到有名字的同地址设备的时候可以去更新之前的只显示mac的设备
                    for (int i = 0; i < allDevicesList.size(); i++) {
                        BluetoothDevice bluetoothDevice = (BluetoothDevice) allDevicesList.get(i).get("device_info");

                        if (bluetoothDevice != null && bluetoothDevice.getAddress().equals(device.getAddress())) {
                            if (device.getName() == null || device.getName().isEmpty()) {
                                //Constent.LOGI("get one addr repeat bluetooth ,return .");
                                return;
                            } else if (allDevicesList.get(i).get("device_name") == null || allDevicesList.get(i).get("device_name").equals("")) {
                                allDevicesList.remove(i);
                                Constent.LOGI("找到一个同地址名字不为空的，替换前边名字为空的设备");
                            } else if (device.getName().equals(bluetoothDevice.getName())) {
                                //Constent.LOGI("get one name repeat bluetooth ,return .");
                                return;
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

                    //19 2.22增加排序操作
                    Collections.sort(allDevicesList, new Comparator<Map<String, Object>>() {
                        @Override
                        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                            return compareFile(o1, o2);
                        }

                    });

                    if (allListAdapter != null) {
                        allListAdapter.notifyDataSetChanged();
                    }
                    if (pairedListAdapter != null) {
//                        tv_bonded_title.setVisibility(View.VISIBLE);
                        pairedListAdapter.notifyDataSetChanged();
                    }

                }
            }
            //搜索完成
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (allListAdapter != null) {
                    allListAdapter.notifyDataSetChanged();
                }
                if (pairedListAdapter != null) {
//                    tv_bonded_title.setVisibility(View.VISIBLE);
                    pairedListAdapter.notifyDataSetChanged();
                }
//                if (llScanView.getVisibility() == View.VISIBLE) {
//                    llScanView.setVisibility(View.INVISIBLE);
//                }
//                btnStopScan.setText(getResources().getString(R.string.startscan));
                /*for (int i = 0; i < allDevicesList.size(); i++) {
                    BluetoothDevice tmpDevice = (BluetoothDevice) allDevicesList.get(i).get(Constent.ListDeviceInfo);

                    BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(tmpDevice.getAddress());
                    if (remoteDevice == null) {
                        Constent.LOGE("onclick get remoteDevice  null .");
                        return;
                    }
                    Log.i(TAG, "Enter the loop++name：" + remoteDevice.getName());

                    if (remoteDevice.getName().equals("MobileBoxRemote")) {
                        Log.i(TAG, "get device");
                        Log.i(TAG, "srart Bonding!");
                        remoteDevice.createBond();

                    }
                }*/
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING://正在配对
                        if (device.getName() == null || device.getName().isEmpty()) {
                            Constent.LOGI(device.getAddress() + "   BOND_BONDING");
                        } else {
                            Constent.LOGI(device.getName() + "   BOND_BONDING");
                        }

                        break;
                    case BluetoothDevice.BOND_BONDED://配对结束

                        if (device.getName() == null || device.getName().isEmpty()) {
                            Constent.LOGI(device.getAddress() + "   BOND_BONDED");
                        } else {
                            Constent.LOGI(device.getName() + "   BOND_BONDED");
                        }


                        moveUnpairToPairedList(device);
                        int profile = device.getBluetoothClass().getMajorDeviceClass();
                        switch (profile) {
                            case 1024:
                                //Toast.makeText(mContext, "配对设备为音频设备，自动", Toast.LENGTH_SHORT).show();
                                connectDevice(device);
                                break;
                            case 1280:
                                //Toast.makeText(mContext, "配对设备为输入设备，自动连接", Toast.LENGTH_SHORT).show();
                                connectDevice(device);
                                break;
                            default:
                                break;
                        }
                        //connectDevice(device);
                        //Toast.makeText(MainActivity.this, "配对成功！", Toast.LENGTH_SHORT).show();

                        break;
                    case BluetoothDevice.BOND_NONE://取消配对/未配对

                        if (device.getName() == null || device.getName().isEmpty()) {
                            Constent.LOGI(device.getAddress() + "   BOND_NONE");
                        } else {
                            Constent.LOGI(device.getName() + "   BOND_NONE");
                        }
                        movePairToUnPairList(device);

                    default:
                        break;
                }


                if (pairedListAdapter != null) {
                    //pairedListAdapter.updateBondState(conn);
//                    tv_bonded_title.setVisibility(View.VISIBLE);
                    pairedListAdapter.notifyDataSetChanged();
                }


            }
            //蓝牙音箱连接成功会收这个广播
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.i("chbluetoothset_connect", "in connected!!! 1");
                flag = true;
                if (pairedListAdapter != null) {
//                    tv_bonded_title.setVisibility(View.VISIBLE);
                    //pairedListAdapter.updateBondState(connect_position);
                    pairedListAdapter.updateBondState(connectDevice);

                    //pairedListAdapter.notifyDataSetChanged();
                }
//                //连接成功
//                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                //Toast.makeText(mContext, "连接成功！", Toast.LENGTH_SHORT).show();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(2000);
//                            Message message = new Message();
//                            message.what = 1;
//                            message.obj = device.getAddress();
//                            mHandler.sendMessage(message);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();

            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.i("chbluetoothset_connect", "in disconnected!!!");
                flag = false;
                if (pairedListAdapter != null) {
//                    tv_bonded_title.setVisibility(View.VISIBLE);
                    //pairedListAdapter.updateBondState(-1);
                    //pairedListAdapter.notifyDataSetChanged();
                }
            }
            //源码连接方式
            //遥控器连接成功会收这个广播
            else if (action.equals("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED")) {
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                //携带的连接状态变化的bluetoothDevice对象
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG, "in connect_change, device:" + device.getAddress() + "&state:" + state);
                if (state == BluetoothProfile.STATE_CONNECTED) {
                    Log.i(TAG, "in connected!!! 2");
//                    //连接成功
//                    //Toast.makeText(mContext, "连接成功！", Toast.LENGTH_SHORT).show();
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(2000);
//                                Message message = new Message();
//                                message.what = 1;
//                                message.obj = device.getAddress();
//                                mHandler.sendMessage(message);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();

                } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                    //连接失败
                    //Toast.makeText(mContext, "连接失败！", Toast.LENGTH_SHORT).show();

                }
            }
        }
    };
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_bluetooth);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (null == mBluetoothAdapter) {
            Toast.makeText(this, getResources().getString(R.string.nobluetooth), Toast.LENGTH_SHORT).show();
            finish();
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");
        //状态改变
        registerReceiver(mReceiver, filter);

        initView();
        if (mBluetoothAdapter.isEnabled()) {
            Constent.LOGI("Bluetooth is enabled .");
//            tvOpenClose.setText(getResources().getString(R.string.open));
            if (!mBluetoothAdapter.isDiscovering()) {
                progress.setVisibility(View.VISIBLE);
                mBluetoothAdapter.startDiscovery();
            }

            showControlView(true);
        } else {

            Constent.LOGI("Bluetooth is not enabled .");
//            tvOpenClose.setText(getResources().getString(R.string.close));
            progress.setVisibility(View.INVISIBLE);
            showControlView(false);

        }
        //}
        if (pairedListAdapter != null) {
//            tv_bonded_title.setVisibility(View.VISIBLE);
            pairedListAdapter.notifyDataSetChanged();
        }
    }
    public void initView(){
        tv_bonded_title=findViewById(R.id.tv_bonded_title);
        btnStopScan=findViewById(R.id.rl_bluetooth_progress);
        progress=findViewById(R.id.progress);
        lvPairedDeviceList=findViewById(R.id.paired_devices);
        lvAllDeviceList=findViewById(R.id.all_devices);
        btnStopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pairedDevicesList.size()!=0) {
                    tv_bonded_title.setVisibility(View.VISIBLE);
                }else{
                    tv_bonded_title.setVisibility(View.GONE);
                }
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                    progress.setVisibility(View.INVISIBLE);

//                    btnStopScan.setText(getResources().getString(R.string.startscan));
                } else {
                    mBluetoothAdapter.startDiscovery();
//                    btnStopScan.setText(getResources().getString(R.string.stopscan));
                    progress.setVisibility(View.VISIBLE);
                    showControlView(true);

                }


            }
        });
        lvPairedDeviceList = (ListView) findViewById(R.id.paired_devices);
        lvPairedDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                createView(BluetoothMainActivity.this, i);
            }
        });


        lvAllDeviceList = (ListView) findViewById(R.id.all_devices);
        lvAllDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //这里要弹出配对框，需要有原生设置， 是原生设置弹的框，
            //解决方法是把原生设置改个包名，加进去
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemClick: dianle");
                if (mBluetoothAdapter.isDiscovering()) {
                    Constent.LOGD("Now we cancel discovery .");
                    mBluetoothAdapter.cancelDiscovery();
                }

                BluetoothDevice tmpDevice = (BluetoothDevice) allDevicesList.get(i).get(Constent.ListDeviceInfo);

                if (tmpDevice == null) {

                    Constent.LOGE("onclick get tmpDevice  null .");
                    return;
                }

                BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(tmpDevice.getAddress());
                if (remoteDevice == null) {

                    Constent.LOGE("onclick get remoteDevice  null .");
                    return;
                }

                if (remoteDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    //Toast.makeText(MainActivity.this, getResources().getString(R.string.bondingKey), Toast.LENGTH_SHORT).show();
                    remoteDevice.createBond();
                    tv_bonded_title.setVisibility(View.VISIBLE);

                } else if (remoteDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    remoteDevice.removeBond();


                } else if (remoteDevice.getBondState() == BluetoothDevice.BOND_BONDING) {

                }

            }
        });

    }

    private void createView(final Context mContext, int i) {
        final int position = i;
        Log.i(TAG, "---------createView()");
        selectView = View.inflate(mContext, R.layout.dialog_bluetooth_command, null);
        TextView text_message=selectView.findViewById(R.id.text_message);
        Button bt_connect =(Button) selectView.findViewById(R.id.bt_connect);
        bt_connect.setVisibility(View.VISIBLE);
        Button bt_unpair = (Button) selectView.findViewById(R.id.bt_unbond);
        Button bt_cancel = (Button) selectView.findViewById(R.id.bt_cancel);

        String message="";
        if (Constent.ListDeviceName!=null && Constent.ListDeviceName!="") {
             message= (String) pairedDevicesList.get(position).get(Constent.ListDeviceName);
        }else{
             message = (String) pairedDevicesList.get(position).get(Constent.ListDeviceAddress);
        }
        String state= (String) pairedDevicesList.get(position).get("connect_state");
        Log.e(TAG, "connect_state: "+ state);
        String text_state="";
        if(state=="1"){
            text_state="已连接";
        }else{
            text_state="已配对";
        }
        text_message.setText("["+message+"]"+text_state);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                wm.removeViewImmediate(selectView);

                BluetoothDevice tmpDevice = (BluetoothDevice) pairedDevicesList.get(position).get(Constent.ListDeviceInfo);

                Log.i(TAG, "clicked#########");

                if (tmpDevice == null) {
                    Log.i(TAG, "the tmpDevice is empty!!!!!!!!!!!!");
                    return;
                }
                Log.i(TAG, "connectdevice!!!!!!!!!!!!---1");
                BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(tmpDevice.getAddress());
                Log.i(TAG, "connectdevice!!!!!!!!!!!!---2");
                if (remoteDevice == null) {
                    Log.i(TAG, "the remoteDevice is empty too###########");
                    return;
                }
                Log.i(TAG, "connectdevice!!!!!!!!!!!!---3");
                connectDevice(tmpDevice);
            }
        });
        bt_unpair.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i(TAG, "btnCancel click");
                quitBond(position);
                wm.removeViewImmediate(selectView);

                BluetoothDevice tmpDevice = (BluetoothDevice) pairedDevicesList.get(position).get(Constent.ListDeviceInfo);
                if (tmpDevice == connectDevice) {
                    if (bluetoothGatt != null) {
                        bluetoothGatt.disconnect();

                    }
                }
                if(pairedDevicesList.size()==0){
                    tv_bonded_title.setVisibility(View.GONE);
                }else{
                    tv_bonded_title.setVisibility(View.VISIBLE);
                }
            }
        });

        wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();


        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.width = (int) mContext.getResources().getDimension(R.dimen._1036dp);
        params.height = (int) mContext.getResources().getDimension(R.dimen._500dp);

        params.x = Gravity.CENTER_HORIZONTAL;
        params.y = Gravity.CENTER_VERTICAL;
        params.format = PixelFormat.RGBA_8888;


        wm.addView(selectView, params);

        selectView.setVisibility(View.VISIBLE);
        bt_connect.setFocusable(true);
        bt_connect.setFocusableInTouchMode(true);
        bt_connect.requestFocus();
        bt_unpair.setOnKeyListener(backKeyListener);
        bt_connect.setOnKeyListener(backKeyListener);
    }

    private View.OnKeyListener backKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                Log.i(TAG, "in Key_Back_Listener");
                wm.removeViewImmediate(selectView);
            }
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        getConnectState2();
        Constent.LOGI("chbluetoothset onResume .");

    }

    @Override
    protected void onStop() {
        super.onStop();

        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        Constent.LOGI("chbluetoothset onDestroy .");
        unregisterReceiver(mReceiver);
        Log.e(TAG, "here11111" );
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        int flag = -1;
//        int a2dp = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
//        int headset = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
//        int health = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEALTH);
//        int health1 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.GATT);
//        int health2 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.GATT_SERVER);
//        int health3 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET);
////        int health4 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEARING_AID);
//        int health5 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HID_DEVICE);
//        int health6 = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.SAP);
//
//        if (a2dp == BluetoothProfile.STATE_CONNECTED) {
//            flag = a2dp;
//            Log.e(TAG, "getConnectState2:a2dp " );
//        } else if (headset == BluetoothProfile.STATE_CONNECTED) {
//            flag = headset;
//            Log.e(TAG, "getConnectState2:headset " );
//        } else if (health == BluetoothProfile.STATE_CONNECTED) {
//            flag = health;
//            Log.e(TAG, "getConnectState2:health " );
//        } else if (health1 == BluetoothProfile.STATE_CONNECTED) {
//            flag = health1;
//            Log.e(TAG, "getConnectState2:health1 " );
//        } else if (health2 == BluetoothProfile.STATE_CONNECTED) {
//            flag = health2;
//            Log.e(TAG, "getConnectState2:health2 " );
//        } else if (health3 == BluetoothProfile.STATE_CONNECTED) {
//            flag = health3;
//            Log.e(TAG, "getConnectState2:health3 " );
////        } else if (health4 == BluetoothProfile.STATE_CONNECTED) {
////            flag = health4;
//        } else if (health5 == BluetoothProfile.STATE_CONNECTED) {
//            flag = health5;
//            Log.e(TAG, "getConnectState2:health5 " );
//        } else if (health6 == BluetoothProfile.STATE_CONNECTED) {
//            flag = health6;
//            Log.e(TAG, "getConnectState2:health6 " );
//        }
//        Log.e(TAG, "flag is " + flag);
//        if (flag != -1) {
//            Log.e(TAG, "here22222");
//            bluetoothAdapter.closeProfileProxy(flag,mBluetoothProfile);
//        }
    }

    private void quitBond(int i) {
        if (mBluetoothAdapter.isDiscovering()) {
            Constent.LOGD("Now we cancel discovery .");
            mBluetoothAdapter.cancelDiscovery();
        }

        BluetoothDevice tmpDevice = (BluetoothDevice) pairedDevicesList.get(i).get(Constent.ListDeviceInfo);

        if (tmpDevice == null) {

            Constent.LOGE("onclick get tmpDevice  null .");
            return;
        }
        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(tmpDevice.getAddress());

        if (remoteDevice == null) {

            Constent.LOGE("onclick get remoteDevice  null .");
            return;
        }

        remoteDevice.removeBond();

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

                    //19 2.22增加排序操作，将修改更新移至后边
//                    pairedListAdapter.notifyDataSetChanged();
//                    allListAdapter.notifyDataSetChanged();
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

//                    pairedListAdapter.notifyDataSetChanged();
//                    allListAdapter.notifyDataSetChanged();

                    Constent.LOGI("movePairToUnPairList -> " + bluetoothDevice.getName());

                }
            }

            //19 2.22修改 增加排序操作
            Collections.sort(allDevicesList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return compareFile(o1, o2);
                }

            });

            pairedListAdapter.notifyDataSetChanged();
            allListAdapter.notifyDataSetChanged();

            //改自10.19
       /* Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constent.ListDeviceName, bluetoothDevice.getName());
        map.put(Constent.ListDeviceInfo, bluetoothDevice);

        allDevicesList.add(map);

        pairedListAdapter.notifyDataSetChanged();
        allListAdapter.notifyDataSetChanged();

        Constent.LOGI("movePairToUnPairList -> " + bluetoothDevice.getName());*/
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
            int flag1=1;
            for (int i=0;i<pairedDevicesList.size();i++){
                if(pairedDevicesList.get(i).get(Constent.ListDeviceAddress)==bluetoothDevice.getAddress()){
                    flag1=0;
                }
            }
            if (flag1==1){
                map.put(Constent.ListDeviceAddress, bluetoothDevice.getAddress());
                map.put(Constent.ListDeviceInfo, bluetoothDevice);
                pairedDevicesList.add(map);
            }
        } else {
            int flag1=1;
            for (int i=0;i<pairedDevicesList.size();i++){
                if(pairedDevicesList.get(i).get(Constent.ListDeviceAddress)==bluetoothDevice.getAddress()){
                    flag1=0;
                }
            }
            if (flag1==1){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(Constent.ListDeviceAddress, bluetoothDevice.getAddress());
                map.put(Constent.ListDeviceName, bluetoothDevice.getName());
                map.put(Constent.ListDeviceInfo, bluetoothDevice);
                pairedDevicesList.add(map);
            }



        }

        pairedListAdapter.notifyDataSetChanged();
        allListAdapter.notifyDataSetChanged();

    }

    private void showControlView(boolean bShow) {
//        if (!bShow) {
////            llControlView.setVisibility(View.INVISIBLE);
//            return;
//        }

        if (mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE) {
            Constent.LOGD("get scan mode SCAN_MODE_CONNECTABLE .");
//            tvCanBeFind.setText(getResources().getString(R.string.close));
        } else if (mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

            Constent.LOGD("get scan mode SCAN_MODE_CONNECTABLE_DISCOVERABLE .");
//            tvCanBeFind.setText(getResources().getString(R.string.open));
        } else {
            Constent.LOGD("get scan mode SCAN_MODE_NONE .");
            mBluetoothAdapter.setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE, 86400000);
//            tvCanBeFind.setText(getResources().getString(R.string.close));
        }

        if (mBluetoothAdapter.isDiscovering()) {
//            btnStopScan.setText(getResources().getString(R.string.stopscan));
//            llScanView.setVisibility(View.VISIBLE);
        } else {
//            btnStopScan.setText(getResources().getString(R.string.startscan));
//            llScanView.setVisibility(View.INVISIBLE);
        }


        Map<String, Object> map;

        bondDevices = mBluetoothAdapter.getBondedDevices();

        pairedDevicesList.clear();
        Log.e(TAG, "bondDevices.size(): "+bondDevices.size());
        if(bondDevices.size()==0) {

            tv_bonded_title.setVisibility(View.GONE);
        }
        if(bondDevices.size()>0) {
            tv_bonded_title.setVisibility(View.VISIBLE);
            for (BluetoothDevice bluetoothDevice : bondDevices) {

                if (bluetoothDevice.getName() == null || bluetoothDevice.getName().isEmpty()) {
//                    pairedDevicesList.get(i).put("connect_state", "1");
                    map = new HashMap<String, Object>();
                    Log.e(TAG, "bluetoothDevice.getAddress: "+bluetoothDevice.getAddress());
                    map.put(Constent.ListDeviceAddress, bluetoothDevice.getAddress());
                    map.put(Constent.ListDeviceInfo, bluetoothDevice);
                    Log.e(TAG, "bluetoothDevice.ListDeviceInfo: "+bluetoothDevice);

                    pairedDevicesList.add(map);
                    Constent.LOGD("get bond bluetooth -> " + bluetoothDevice.getAddress() + " & paired -> " + bluetoothDevice.getBondState());
                } else {
                    map = new HashMap<String, Object>();
                    Log.e(TAG, "bluetoothDevice.getName: "+bluetoothDevice.getName());
                    Log.e(TAG, "bluetoothDevice.getAddress: "+bluetoothDevice.getAddress());
                    Log.e(TAG, "bluetoothDevice.ListDeviceInfo: "+bluetoothDevice.getBondState());
                    Log.e(TAG, "showControlView: "+ Settings.Secure.getString(BluetoothMainActivity.this.getContentResolver(),"connect_address") );
                    int flag=1;
                    for (int i=0;i<pairedDevicesList.size();i++){
                        if (pairedDevicesList.get(i).get(Constent.ListDeviceAddress)==bluetoothDevice.getAddress()){
                            flag=0;
                        }
                    }
                    if(flag==1){
                        map.put(Constent.ListDeviceName, bluetoothDevice.getName());
                        map.put(Constent.ListDeviceInfo, bluetoothDevice);
                        map.put(Constent.ListDeviceAddress, bluetoothDevice.getAddress());
                        pairedDevicesList.add(map);
                    }
                    Constent.LOGD("get bond bluetooth -> " + bluetoothDevice.getName() + " & paired -> " + bluetoothDevice.getBondState());
                }

            }
        }
        pairedListAdapter = new PairedListAdapter(R.layout.paired_list_item, pairedDevicesList, BluetoothMainActivity.this);
        if(pairedDevicesList.size()>0) {
            Log.e(TAG, "pairedDevicesList: " + pairedDevicesList.get(0).get(Constent.ListDeviceInfo));
        }
        lvPairedDeviceList.setAdapter(pairedListAdapter);
        getConnectState2();

        allDevicesList.clear();
        allListAdapter = new AllListAdapter(R.layout.all_list_item, allDevicesList, BluetoothMainActivity.this);
        lvAllDeviceList.setAdapter(allListAdapter);

    }

    private void getConnectState2() {
        Intent regIntent = new Intent(this, ConnectBluetoothService.class);
        startService(regIntent);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.i(TAG, "in handler, address:" + msg.obj.toString());
                    setConnectedAddress(msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    };


    private void setConnectedAddress(String address) {
        Log.i(TAG, "in setConnectedAddress:" + address);
        Log.i(TAG, "pairedDevicesList.size():" + pairedDevicesList.size());
        for (int i = 0; i < pairedDevicesList.size(); i++) {
            BluetoothDevice item = (BluetoothDevice) pairedDevicesList.get(i).get(Constent.ListDeviceInfo);
            Log.i(TAG, "item:" + i + "&address:" + item.getAddress());
            if (item.getAddress().equals(address)) {
                Log.i(TAG, "设置了position:" + i + "&connect_state=1");
                pairedDevicesList.get(i).put("connect_state", "1");
            }
        }
        Map<String, Object> map;

        bondDevices = mBluetoothAdapter.getBondedDevices();

        pairedDevicesList.clear();
        Log.e(TAG, "bondDevices.size(): "+bondDevices.size());
        if(bondDevices.size()>0) {
            for (BluetoothDevice bluetoothDevice : bondDevices) {

                if (bluetoothDevice.getName() == null || bluetoothDevice.getName().isEmpty()) {
//                    pairedDevicesList.get(i).put("connect_state", "1");
                    boolean isConnected=false;
                    map = new HashMap<String, Object>();
                    try {
                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                        isConnectedMethod.setAccessible(true);
                        isConnected = (boolean) isConnectedMethod.invoke(bluetoothDevice, (Object[]) null);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.e(TAG, "bluetoothDevice.getAddress: "+bluetoothDevice.getAddress());
                    if (isConnected) {
                        Log.e("qqq", "connected:" + bluetoothDevice.getName());
                        map.put("connect_state", "1");
                    }
                    else{
                        Log.e("qqq", "unconnected:" + bluetoothDevice.getName());
                        map.put("connect_state", "");
                    }
                    map.put(Constent.ListDeviceAddress, bluetoothDevice.getAddress());
                    map.put(Constent.ListDeviceInfo, bluetoothDevice);
                    Log.e(TAG, "bluetoothDevice.ListDeviceInfo: "+bluetoothDevice);

                    pairedDevicesList.add(map);
                    Constent.LOGD("get bond bluetooth -> " + bluetoothDevice.getAddress() + " & paired -> " + bluetoothDevice.getBondState());
                } else {
                    boolean isConnected=false;
                    map = new HashMap<String, Object>();
                    try {
                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                        isConnectedMethod.setAccessible(true);
                        isConnected = (boolean) isConnectedMethod.invoke(bluetoothDevice, (Object[]) null);

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Log.e(TAG, "bluetoothDevice.getName: "+bluetoothDevice.getName());
                    Log.e(TAG, "bluetoothDevice.getAddress: "+bluetoothDevice.getAddress());
                    Log.e(TAG, "bluetoothDevice.ListDeviceInfo: "+bluetoothDevice.getBondState());
                    Log.e(TAG, "showControlView: "+Settings.Secure.getString(BluetoothMainActivity.this.getContentResolver(),"connect_address") );
                    int flag=1;
                    for (int i=0;i<pairedDevicesList.size();i++){
                        if (pairedDevicesList.get(i).get(Constent.ListDeviceAddress)==bluetoothDevice.getAddress()){
                            flag=0;
                        }
                    }
                    if(flag==1){
                        if (isConnected) {
                            Log.e("qqq", "connected:" + bluetoothDevice.getName());
                            map.put("connect_state", "1");
                        }
                        else{
                            Log.e("qqq", "unconnected:" + bluetoothDevice.getName());
                            map.put("connect_state", "");
                        }
                        map.put(Constent.ListDeviceName, bluetoothDevice.getName());
                        map.put(Constent.ListDeviceInfo, bluetoothDevice);
                        map.put(Constent.ListDeviceAddress, bluetoothDevice.getAddress());
                        pairedDevicesList.add(map);
                    }
                    Constent.LOGD("get bond bluetooth -> " + bluetoothDevice.getName() + " & paired -> " + bluetoothDevice.getBondState());
                }

            }
        }
        pairedListAdapter.notifyDataSetChanged();
    }

    public static int compareFile(Map<String, Object> object1, Map<String, Object> object2) {
        String name1 = (String) object1.get(Constent.ListDeviceName);
        String name2 = (String) object2.get(Constent.ListDeviceName);
        Constent.LOGI("name1:" + name1 + "  name2:" + name2);
        //将名字含有中文的设备排在前边
        if (name1 != null && name2 != null) {
            if (isContainChinese(name1) || isContainChinese(name2)) {
                if (isContainChinese(name1) && isContainChinese(name2)) {
                    return 0;
                } else if (isContainChinese(name1) && !isContainChinese(name2)) {
                    return -1;
                } else if (!isContainChinese(name1) && isContainChinese(name2)) {
                    return 1;
                }
            }
        }
        //将有名字的排在没名字的前边
        else if (name1 == null && name2 == null) {
            return 0;
        } else if (name1 != null && name2 == null) {
            return -1;
        } else if (name1 == null && name2 != null) {
            return 1;
        }
        return 0;
    }

    //判断String中是否包含中文
    public static boolean isContainChinese(String str) {

        //\u4e00”和“\u9fa5”是unicode编码，并且正好是中文编码的开始和结束的两个值，
        // 所以这个正则表达式可以用来判断字符串中是否包含中文。
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);

        if (m.find()) {

            return true;
        }
        //这是判断是否全是中文
//        if (m.matches()) {
//            return true;
//        }
        return false;
    }

    private void connectDevice(BluetoothDevice bluetoothDevice) {
        int deviceType = bluetoothDevice.getBluetoothClass().getMajorDeviceClass();
        int profile = 0;
        //這個轉換是必須的
        switch (deviceType) {
            case 256://电脑设备
                profile = 256;
                break;
            case 512://手机
                //手机使用PAN协议连接
                profile = 5;
                break;
            case 1024://蓝牙音频设备
                profile = 2;
                break;
            case 1280://输入设备 遥控器
                profile = 4;
                break;
            case 2304://HEALTH
                profile = 3;
                break;
            default:
                break;
        }
        //获取蓝牙输入设备代理对象
        Log.i(TAG, "connectdevice!!!!!!!!!!!!---4,profile：" + profile);
        mBluetoothAdapter.getProfileProxy(mContext, connectListener, profile);
        Log.i(TAG, "connectdevice!!!!!!!!!!!!---5");
        mConnectDevice = bluetoothDevice;
        Settings.Secure.putString(BluetoothMainActivity.this.getContentResolver(),"connect_address",bluetoothDevice.getAddress());
        Log.e(TAG, "connectDevice: "+ bluetoothDevice.getAddress());
        Log.i(TAG, "connectdevice!!!!!!!!!!!!---6");
    }

    private BluetoothProfile.ServiceListener connectListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
            Log.i(TAG, "in connectListener, profile:" + profile + "| bluetoothprofile:" + bluetoothProfile);
            //2:蓝牙设备 4:输入设备 5:手机PAN协议连接方式
            if (profile == 2 || profile == 4 || profile == 5) {
                mBluetoothProfile = bluetoothProfile;
                try {
                    Method method = bluetoothProfile.getClass()
                            .getMethod("connect", new Class[]{BluetoothDevice.class});
                    method.invoke(bluetoothProfile, mConnectDevice);
                    Log.i(TAG, "反射调用连接方法成功");
                    //连接成功
                    //Toast.makeText(mContext, "连接成功！", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                Message message = new Message();
                                message.what = 1;
                                message.obj = mConnectDevice.getAddress();
                                mHandler.sendMessage(message);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //Toast.makeText(mContext, "反射调用连接方法成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "反射调用连接方法异常：" + e.getMessage());
                    //Toast.makeText(mContext, "反射调用连接方法异常", Toast.LENGTH_SHORT).show();
                }
            }

            //关闭ProfileProxy时需要传入正确的profile
            mBluetoothAdapter.closeProfileProxy(profile, mBluetoothProfile);
        }

        @Override
        public void onServiceDisconnected(int profile) {
            Log.i(TAG, "!!!!获取Proxy失败！！！");
        }
    };



}
