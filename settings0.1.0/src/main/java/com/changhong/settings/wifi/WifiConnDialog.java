package com.changhong.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.settings.utils.WifiAdminUtils;
//import com.android.settings.utils.WifiConnectUtils;
import com.changhong.settings.R;
import com.changhong.settings.utils.WifiAdminUtils;
import com.changhong.settings.utils.WifiConnectUtils;

import java.util.List;


/**
 * Created by fdw on 2017/9/13..
 */
public class WifiConnDialog extends Dialog {
    private Context context;
    private ScanResult scanResult;
    private TextView txtWifiName;
    private TextView txtSinglStrength;
    private TextView txtSecurityLevel;
    private TextView txtWifidesc;
    private Button txtBtnConn;
    private Button txtBtnCancel;
    private LinearLayout ll_ap_static;
    public EditText edtPassword, et_static_ip, et_static_mask, et_static_gateway, et_static_dns1, et_static_dns2;
    private CheckBox cbxShowPass;
//    private RadioGroup rg_mode;
    private RadioButton rb_dhcp, rb_static;
    // wifi列表的listview
    private ListView mListView;
    // wifi列表item的指针
    private int mPosition;
    // wifi列表的适配器
    private MyListViewAdapter mAdapter;
    // wifi列表的数据
    private List<ScanResult> mScanResultList;
    private String wifiName;
    private String securigyLevel;
    private int level;
    private boolean isConnect = false;

    public WifiConnDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * 传wifi名称和信号等构造函数
     *
     * @param context
     * @param wifiName
     * @param singlStren
     * @param securityLevl
     */
    private WifiConnDialog(Context context, int theme, String wifiName,
                           int singlStren, String securityLevl) {
        super(context, theme);
        this.context = context;
        this.wifiName = wifiName;
        this.level = singlStren;
        this.securigyLevel = securityLevl;
    }

    /**
     * 传当前wifi信息
     *
     * @param context
     * @param scanResult
     * @param onNetworkChangeListener
     */
    public WifiConnDialog(Context context, int theme, ListView mListView,
                          int mPosition, MyListViewAdapter mAdapter, ScanResult scanResult,
                          List<ScanResult> mScanResultList,
                          OnNetworkChangeListener onNetworkChangeListener) {
        this(context, theme, scanResult.SSID, scanResult.level, scanResult.capabilities);
        this.mListView = mListView;
        this.mPosition = mPosition;
        this.mAdapter = mAdapter;
        this.txtWifidesc = (TextView) mListView.findViewById(R.id.tv_wifi_secure);
        this.scanResult = scanResult;
        this.mScanResultList = mScanResultList;
        this.onNetworkChangeListener = onNetworkChangeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_center_wifi_connect);

        initView();
        setListener();
    }

    private void setListener() {

        edtPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    cbxShowPass.setEnabled(false);

                } else {
                    cbxShowPass.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cbxShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    // 文本正常显示
                    edtPassword
                            .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Editable etable = edtPassword.getText();
                    Selection.setSelection(etable, etable.length());

                } else {
                    // 文本以密码形式显示
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    // 下面两行代码实现: 输入框光标一直在输入文本后面
                    Editable etable = edtPassword.getText();
                    Selection.setSelection(etable, etable.length());

                }
            }
        });

        txtBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("txtBtnCancel");
                WifiConnDialog.this.dismiss();
            }
        });

        txtBtnConn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                String ip = et_static_ip.getText().toString();
//                String mask = et_static_mask.getText().toString();
//                String gateway = et_static_gateway.getText().toString();
//                String dns = et_static_dns1.getText().toString();
//                String dns2 = et_static_dns2.getText().toString();

                WifiConnectUtils.WifiCipherType type = null;
                if (scanResult.capabilities.toUpperCase().contains("WPA")) {
                    type = WifiConnectUtils.WifiCipherType.WIFICIPHER_WPA;
                } else if (scanResult.capabilities.toUpperCase()
                        .contains("WEP")) {
                    type = WifiConnectUtils.WifiCipherType.WIFICIPHER_WEP;
                } else {
                    type = WifiConnectUtils.WifiCipherType.WIFICIPHER_NOPASS;
                }
                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Log.i("empty", "onClick: ");
                    Toast.makeText(context, context.getResources().getString(R.string.network_wifi_ssid_tip),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtPassword.getText().toString().length() < 8) {
                    Toast.makeText(context, context.getResources().getString(R.string.input_password_length),
                            Toast.LENGTH_SHORT).show();
                    edtPassword.setText("");
                    return;
                }
                // 去连接网络
                WifiAdminUtils mWifiAdmin = new WifiAdminUtils(context);
                /**是否去连接了 */
//                if (rb_dhcp.isChecked()) {
                    if (WifiConnDialog.this != null) {
                        dismiss();
//                        Toast.makeText(context,context.getResources().
//                                getString(R.string.toast_pppoe_connectting), Toast.LENGTH_SHORT).show();
                    }
                    isConnect = mWifiAdmin.connect(scanResult.SSID, edtPassword.getText().toString().trim(), type);
//                } else if (rb_static.isChecked()) {
//                    Log.i("WifiListActivity", "onClick: " + ip + " @@@ " + mask + " ### " + gateway + " $$$ " + dns + " " + dns2);
//                    if (checkIP(ip, true) && checkIP(mask, false) && checkIP(gateway, false) && checkIP(dns, false)) {
//                        if (ip.contains(".") && gateway.contains(".") &&!ip.substring(0, ip.lastIndexOf(".")).
//                                equals(gateway.substring(0, gateway.lastIndexOf(".")))) {
//                            Log.i("WifiListActivity", "startStatic: " + gateway.substring(0, gateway.lastIndexOf(".")) + " ### "
//                                    + ip.substring(0, ip.lastIndexOf(".")));
//                            Toast.makeText(context, context.getResources().getString(R.string.network_ip_getway_not_segment),
//                                    Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        if (checkInfo(ip, true, false) && checkInfo(mask, false, true) && checkInfo(gateway,
//                                false, false) && checkInfo(dns, false, false) && (TextUtils.isEmpty(dns2)
//                                || checkInfo(dns2, false, false))) {
//                            if (WifiConnDialog.this != null) {
//                                dismiss();
//                            }
//                            Log.i("WifiListActivity", "yes,excuted!!!");
//                            isConnect = mWifiAdmin.connect(scanResult.SSID, edtPassword.getText().toString().trim(), type);
//                            mWifiAdmin.set_static(scanResult.SSID, ip, gateway, dns);
//                        } else {
//                            Log.i("WifiListActivity", "startStatic: return right away!!!!!!!");
//                        }
//                    }
//                }

                Log.d("WifiListActivity", isConnect + "是否去连接的值");
                if (isConnect) {
                    Log.d("WifiListActivity", "去连接wifi了");
                    onNetworkChangeListener.onNetWorkConnect();
                } else {
                    Log.d("WifiListActivity", "没有去连接wifi");
                    onNetworkChangeListener.onNetWorkConnect();
                }
//				return isConnect;
            }
        });

//        rg_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                if (rb_dhcp.isChecked()) {
//
//                    ll_ap_static.setVisibility(View.GONE);
//
//                } else if (rb_static.isChecked()) {
//
//                    ll_ap_static.setVisibility(View.VISIBLE);
//
//                }
//            }
//        });
    }

    private void initView() {
        txtWifiName = (TextView) findViewById(R.id.tv_wifi_name);
        txtSinglStrength = (TextView) findViewById(R.id.tv_wifi_strength);
        txtSecurityLevel = (TextView) findViewById(R.id.tv_wifi_secure);
        edtPassword = (EditText) findViewById(R.id.tv_wifi_password);
        Log.i("cheng", "initView: RRRRRRRR" + edtPassword + "%%%$%$$%" + txtWifiName);
        edtPassword.requestFocus();
        cbxShowPass = (CheckBox) findViewById(R.id.show_password);
        cbxShowPass.setEnabled(false);
//        ll_ap_static=(LinearLayout) findViewById(R.id.ll_ap_static);
//        rg_mode = (RadioGroup) findViewById(R.id.rg_mode);
//        rb_dhcp = (RadioButton) findViewById(com.android.settings.R.id.rb_dhcp);
//        rb_dhcp.setChecked(true);
//        rb_static = (RadioButton) findViewById(com.android.settings.R.id.rb_static);
//        et_static_ip = (EditText) findViewById(com.android.settings.R.id.iptxt_eth_static_ip);
//        et_static_mask = (EditText) findViewById(com.android.settings.R.id.iptxt_eth_static_mask);
//        et_static_gateway = (EditText) findViewById(com.android.settings.R.id.iptxt_eth_static_gateway);
//        et_static_dns1 = (EditText) findViewById(com.android.settings.R.id.iptxt_eth_static_dns);
//        et_static_dns2 = (EditText) findViewById(com.android.settings.R.id.iptxt_eth_static_dns2);

        txtBtnCancel = (Button) findViewById(R.id.dialog_wifi_cancel);
        txtBtnConn = (Button) findViewById(R.id.dialog_wifi_connect);

        if (TextUtils.isEmpty(wifiName)) {
            txtWifiName.setText(scanResult.BSSID);
        } else {
            txtWifiName.setText(wifiName);
        }
        txtSinglStrength.setText(WifiAdminUtils.singlLevToStr(level));
        txtSecurityLevel.setText(securigyLevel);
        cbxShowPass.setEnabled(false);


    }

    private void showShortToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private OnNetworkChangeListener onNetworkChangeListener;

    private boolean checkIP(String string, boolean isIp) {

        Log.d("WifiListActivity", "tv_IpAddress.getText() -->  " + et_static_ip.getText().toString());

        if (!TextUtils.isEmpty(string)) {
            if (isIp && ("0.0.0.0".equals(et_static_ip.getText().toString()))) {
                Toast.makeText(context, context.getResources().getString(R.string.eth_manual_ip_error),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (et_static_ip.getText().toString().length() > 0) {

                Log.i("WifiListActivity", "checkIP()  ,static ip is useful !");
                return true;
            }
        }
        Toast.makeText(context, context.getResources().getString(R.string.cant_empty),
                Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean checkInfo(String string, boolean isIP, boolean isMask) {
        Log.i("WifiListActivity", "checkInfo: @@@@@@@@@@@@@@@@@@@@@@@@" + string);
        String s1 = "0", s2 = "0.0", s3 = "0", s4 = "0.0", s5 = "0", s6 = "0";
        int i1 = 0, i2 = 0, i3 = 0, i4 = 0;
        if (string.contains(".") && !string.endsWith(".") && !string.startsWith(".")) {
            s1 = string.substring(0, string.indexOf("."));
            i1 = Integer.parseInt(s1);
            s2 = string.substring(string.indexOf(".") + 1);
        } else {
            return false;
        }
        if (s2.contains(".") && !s2.endsWith(".") && !s2.startsWith(".")) {
            s3 = s2.substring(0, s2.indexOf("."));
            i2 = Integer.parseInt(s3);
            s4 = s2.substring(s2.indexOf(".") + 1);
        } else {
            return false;
        }
        if (s4.contains(".") && !s4.endsWith(".") && !s4.startsWith(".")) {
            s5 = s4.substring(0, s4.indexOf("."));
            i3 = Integer.parseInt(s5);
            s6 = string.substring(string.lastIndexOf(".") + 1);
            i4 = Integer.parseInt(s6);
        } else {
            return false;
        }
        Log.i("WifiListActivity", "splitString: " + s1 + "  " + s3 + "  " + s5 + "  " + s6);
        if (isIP) {
            if (i1 > 0 && i1 < 224) {
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.eth_manual_ip_error),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (i4 >= 0 && i4 < 255) {
            } else if (i4 == 255) {
                Toast.makeText(context, context.getResources().getString(
                        R.string.network_ip_mask_not_segment), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.err),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if ((i2 >= 0 && i2 < 256) && (i3 >= 0 && i3 < 256)) {
            } else {
                Log.i("WifiListActivity", "checkInfo: error info");
                Toast.makeText(context, context.getResources().getString(
                        R.string.check_info), Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } else {
            if ((i1 >= 0 && i1 < 256) && (i2 >= 0 && i2 < 256) && (i3 >= 0 && i3 < 256) && (i4 >= 0 && i4 < 256)) {
                if (isMask) {
                    if (!string.equals("255.255.255.0")) {
                        Toast.makeText(context, context.getResources().getString(R.string.eth_manual_mask_error),
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return true;
            } else {
                Log.i("WifiListActivity", "checkInfo: error info");
                Toast.makeText(context, context.getResources().getString(R.string.eth_manual_ip_error),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }
}
