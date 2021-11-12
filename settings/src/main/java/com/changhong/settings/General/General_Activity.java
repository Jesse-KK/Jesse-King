package com.changhong.settings.General;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.changhong.settings.BluetoothMainActivity;
import com.changhong.settings.R;

public class General_Activity extends Activity implements View.OnKeyListener, View.OnClickListener {
    private RelativeLayout setting_bluetooth, setting_reset;
    private TextView bluetooth_close_on;
    private boolean isBluetoothOn = false;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        init();
    }

    private void init() {
        setting_bluetooth = (RelativeLayout) findViewById(R.id.setting_bluetooth);
        setting_reset = (RelativeLayout) findViewById(R.id.setting_reset);
        bluetooth_close_on = (TextView) findViewById(R.id.bluetooth_close_on);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            isBluetoothOn = true;
            bluetooth_close_on.setText("开启");
        } else {
            isBluetoothOn = false;
            bluetooth_close_on.setText("关闭");
        }
        setting_bluetooth.setOnKeyListener(this);
        setting_bluetooth.setOnClickListener(this);
        setting_reset.setOnClickListener(this);

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (isBluetoothOn == true) {
                    isBluetoothOn = false;
                    mBluetoothAdapter.disable();
                    bluetooth_close_on.setText("关闭");

                } else {
                    isBluetoothOn = true;
                    mBluetoothAdapter.enable();
                    bluetooth_close_on.setText("开启");
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_bluetooth:
                if (isBluetoothOn == true) {
                    startActivity(new Intent(General_Activity.this, BluetoothMainActivity.class));
                }
                break;
            case R.id.setting_reset:
                startActivity(new Intent(General_Activity.this, Reset_Setting_Activity.class));
                break;
            default:
                break;

        }
    }
}
