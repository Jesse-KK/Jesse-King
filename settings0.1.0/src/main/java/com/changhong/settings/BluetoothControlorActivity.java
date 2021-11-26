package com.changhong.settings;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class BluetoothControlorActivity extends Activity {

    private static final String TAG = "BluetoothControlor";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_controlor);
        mContext = BluetoothControlorActivity.this;
        registerRec();
    }

    private void registerRec() {
        IntentFilter filter = new IntentFilter("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");
        registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED")) {
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                //携带的连接状态变化的bluetoothDevice对象
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG, "in connect_change, device:" + device.getAddress() + "&state:" + state);
                if (state == BluetoothProfile.STATE_CONNECTED) {
                    Log.i(TAG, "in connected!!! 2");
                    //连接成功
                    Toast.makeText(mContext, "蓝牙遥控器连接成功！", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                    //连接失败
                    Toast.makeText(mContext, "蓝牙遥控器连接失败！", Toast.LENGTH_SHORT).show();

                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterRece();
    }

    private void unregisterRece() {
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
    }
}
