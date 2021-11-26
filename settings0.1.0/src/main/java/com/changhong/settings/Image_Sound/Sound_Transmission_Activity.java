package com.changhong.settings.Image_Sound;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.display.DisplayManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import androidx.annotation.Nullable;

import com.changhong.settings.R;

public class Sound_Transmission_Activity extends Activity implements View.OnKeyListener {
    private RelativeLayout sound_transmission;
    private TextView transmission_close_on;
    private Context mContext;
    private  DisplayManager mDisplayManager;

    //开启透传标志位
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_transmission);
        mContext = Sound_Transmission_Activity.this;
        mDisplayManager = (DisplayManager) mContext.getSystemService(Context.DISPLAY_MANAGER_SERVICE);
        sound_transmission = (RelativeLayout) findViewById(R.id.sound_transmission);
        transmission_close_on = (TextView) findViewById(R.id.transmission_close_on);

        sound_transmission.setOnKeyListener(this);

        try {
            flag = Settings.Secure.getInt(mContext.getContentResolver(),"hdmi_passthrough");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (flag==0){
            transmission_close_on.setText("关闭");
        }else {
            transmission_close_on.setText("开启");
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (flag == DisplayManager.DISPLAY_HDMI_MODE_PCM) {
                    flag = DisplayManager.DISPLAY_HDMI_MODE_RAW;
                    mDisplayManager.setHDMIPassThrough(DisplayManager.DISPLAY_HDMI_MODE_RAW);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_already_set_audio)
                            + "HDMI透传", Toast.LENGTH_SHORT).show();
                    Settings.Secure.putInt(getContentResolver(),"hdmi_passthrough",DisplayManager.DISPLAY_HDMI_MODE_RAW);
                    transmission_close_on.setText("开启");
                } else {
                    flag = DisplayManager.DISPLAY_HDMI_MODE_PCM;
                    mDisplayManager.setHDMIPassThrough(DisplayManager.DISPLAY_HDMI_MODE_PCM);
                    Toast.makeText(getApplicationContext(),"透传已经关闭",Toast.LENGTH_SHORT).show();
                    Settings.Secure.putInt(getContentResolver(),"hdmi_passthrough",DisplayManager.DISPLAY_HDMI_MODE_PCM);
                    transmission_close_on.setText("关闭");
                }
            }
        } else if (event.getAction() == KeyEvent.ACTION_DOWN) {

        }
        return false;
    }
}
