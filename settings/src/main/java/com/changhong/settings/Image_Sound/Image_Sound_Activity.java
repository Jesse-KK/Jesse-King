package com.changhong.settings.Image_Sound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import androidx.annotation.Nullable;

import com.changhong.settings.R;

public class Image_Sound_Activity extends Activity implements View.OnClickListener, View.OnKeyListener {

    private static final String TAG = "Image_Sound_Activity";
    private RelativeLayout resolution_setting, image_size, clarity_setting, image_ratio, sound_transmission;
    private Context mContext;
    private TextView clarity_setting_switch, image_ratio_switch;

    private int current_clarity;//清晰度标志位
    private static final int CLARITY_PRIORITY = 0;
    private static final int SMOOTH_PRIORITY = 1;
    private static final int AUTO_CHANGE = 2;

    private int current_ratio;//画面比例标志位
    private static final int STRETCH_FULL_SCREEN = 0;
    private static final int ADAPTIVE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_sound);
        mContext = Image_Sound_Activity.this;
        init();
        //清晰度开关
        Settings.Secure.putInt(mContext.getContentResolver(), "clarity_mode", CLARITY_PRIORITY);
        Settings.Secure.putInt(mContext.getContentResolver(), "clarity_mode", SMOOTH_PRIORITY);
        Settings.Secure.putInt(mContext.getContentResolver(), "clarity_mode", AUTO_CHANGE);
        //画面比例开关
        Settings.Secure.putInt(mContext.getContentResolver(), "ratio_mode", STRETCH_FULL_SCREEN);
        Settings.Secure.putInt(mContext.getContentResolver(), "ratio_mode", ADAPTIVE);

    }

    public void init() {
        resolution_setting = (RelativeLayout) findViewById(R.id.resolution_setting);
        image_size = (RelativeLayout) findViewById(R.id.image_size);
        clarity_setting = (RelativeLayout) findViewById(R.id.clarity_setting);
        image_ratio = (RelativeLayout) findViewById(R.id.image_ratio);
        sound_transmission = (RelativeLayout) findViewById(R.id.sound_transmission);

        clarity_setting_switch = (TextView) findViewById(R.id.clarity_setting_switch);
        image_ratio_switch = (TextView) findViewById(R.id.image_ratio_switch);
        try {
            current_clarity = Settings.Secure.getInt(mContext.getContentResolver(), "clarity_mode");
            current_ratio = Settings.Secure.getInt(mContext.getContentResolver(), "ratio_mode");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        resolution_setting.setOnClickListener(this);
        image_size.setOnClickListener(this);
        clarity_setting.setOnKeyListener(this);
        image_ratio.setOnKeyListener(this);
        sound_transmission.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.resolution_setting:
                intent.setClass(mContext, Resolution_Activity.class);
                break;
            case R.id.image_size:
                intent.setClass(mContext, Scaling_Activity.class);
                break;
            case R.id.sound_transmission:
                intent.setClass(mContext, Sound_Transmission_Activity.class);
                break;
            default:
                break;
        }
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.clarity_setting:
                    Log.e(TAG, "onKey: 清晰度id是："+clarity_setting.getId() );
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        Log.e(TAG, "onKey: " + "按下了右键");
                        if (current_clarity == CLARITY_PRIORITY) {
                            current_clarity = SMOOTH_PRIORITY;
                            clarity_setting_switch.setText(getResources().getText(R.string.setting_quality_smooth));
                        } else if (current_clarity == SMOOTH_PRIORITY) {
                            current_clarity = AUTO_CHANGE;
                            clarity_setting_switch.setText(getResources().getText(R.string.setting_quality_auto));
                        } else if (current_clarity == AUTO_CHANGE) {
                            current_clarity = CLARITY_PRIORITY;
                            clarity_setting_switch.setText(getResources().getText(R.string.setting_quality_clarity));
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        Log.e(TAG, "onKey: " + "按下了左键");
                        if (current_clarity == CLARITY_PRIORITY) {
                            current_clarity = AUTO_CHANGE;
                            clarity_setting_switch.setText(getResources().getText(R.string.setting_quality_auto));
                        } else if (current_clarity == AUTO_CHANGE) {
                            current_clarity = SMOOTH_PRIORITY;
                            clarity_setting_switch.setText(getResources().getText(R.string.setting_quality_smooth));
                        } else if (current_clarity == SMOOTH_PRIORITY) {
                            current_clarity = CLARITY_PRIORITY;
                            clarity_setting_switch.setText(getResources().getText(R.string.setting_quality_clarity));
                        }
                    }
                        break;
                case R.id.image_ratio:
                    Log.e(TAG, "onKey: 画面比例id是："+image_ratio.getId() );
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        Log.e(TAG, "onKey:按下了画面比例的左右键 ");
                        if (current_ratio == ADAPTIVE) {
                            current_ratio = STRETCH_FULL_SCREEN;
                            image_ratio_switch.setText(getResources().getText(R.string.setting_ratio_full));
                        } else {
                            current_ratio = ADAPTIVE;
                            image_ratio_switch.setText(getResources().getText(R.string.setting_ratio_adaptive));
                        }
                        break;
                    }
                default:
                    break;
            }
        }else {
            return false;
        }
        return false;
    }
}