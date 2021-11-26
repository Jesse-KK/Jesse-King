package com.changhong.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.settings.R;
//import com.android.settings.utils.WifiAdminUtils;
import com.changhong.settings.R;
import com.changhong.settings.utils.WifiAdminUtils;
import com.changhong.settings.utils.WifiUtils;


/**
 * Created by Cheng on 2018/3/2.
 */

public class WifiConnect_NopassDialog extends Dialog {
    private ScanResult scanResult;
    private Context mContext;
    private int level;
    public WifiConnect_NopassDialog(Context context, int theme, ScanResult scanResult) {
        super(context,theme);
        this.scanResult = scanResult;
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wifi_out_no_pass);
        level=scanResult.level;
        TextView tv_ssid=(TextView) findViewById(R.id.tv_wifi_name);
        TextView tv_wifi_secure=(TextView) findViewById(R.id.tv_wifi_secure);
        TextView tv_wifi_strength=(TextView) findViewById(R.id.tv_wifi_strength);
        if (!TextUtils.isEmpty(scanResult.SSID)){
           tv_ssid.setText(scanResult.SSID);
        }else {
            tv_ssid.setText(scanResult.BSSID);
        }
        tv_wifi_strength.setText(WifiAdminUtils.singlLevToStr(level));
        tv_wifi_secure.setText("开放的");
        findViewById(R.id.dialog_wifi_connect).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean iswifi = new WifiAdminUtils(mContext).connectSpecificAP(scanResult);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (iswifi) {
                            Toast.makeText(mContext, "连接成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "连接失败！", Toast.LENGTH_SHORT).show();
                        }
                        dismiss();
                    }
                });

        findViewById(R.id.dialog_wifi_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                }
        );

    }
}
