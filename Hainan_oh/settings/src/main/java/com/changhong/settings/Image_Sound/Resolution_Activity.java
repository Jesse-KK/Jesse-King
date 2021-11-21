package com.changhong.settings.Image_Sound;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.display.DisplayManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.changhong.settings.General.RecoveryDialog;
import com.changhong.settings.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Resolution_Activity extends Activity {
    private static final String TAG = "RESOLUTION STARTING";
    private final int FRESH_FMT = 3;
    List<HashMap<String, Object>> list;
    private Context mContext;
    private String[] mList_Tv = null;
    private String[] mtv_value = null;
    private String curOutputMode;//当前的分辨率，如720p60hz
    private String newMode;
    private ResolutionDialog dialog = null;//点击某个分辨率选项会弹个对话框
    private Timer mtimer = null;
    private Resolution_Adapter myAdapter;
    private ListView list_resolution;
    private int clickedPosition;
    private int finalPosition;
    private int mTvFmt = 0;
    private int mTvCount = 0;
    private int mTvOldFmt = 0;
    private int mTvNewFmt = 0;
    private HashMap<String, String> map_value = new HashMap<String, String>();
    private HashMap<String, String> map_key = new HashMap<String, String>();
    private int cur_mode_position = 0;
    private DisplayManager mDisplayManager;
    private HashMap<Integer, String> mSupportsMapText = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolution);
        list_resolution =(ListView) findViewById(R.id.display_resolution_list);
        mContext = Resolution_Activity.this;
        mDisplayManager = (DisplayManager) mContext.getSystemService(Context.DISPLAY_MANAGER_SERVICE);

        list_resolution.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            //这个handler处理：当用户点击了某个分辨率后，10s没有继续操作，就让弹框消失，并让分辨率复原
            switch (msg.what) {
                case FRESH_FMT:
                    cancelTimer();
                    if (mTvNewFmt != mTvOldFmt) {
                        dialog.dismiss();
                        mDisplayManager.setDisplayStandard(mTvOldFmt);
                        mDisplayManager.saveParams();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.tip_re2) + curOutputMode, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mList_Tv = new String[25];
        mtv_value = new String[25];
        int num = 0;
        InitMaps();
        HashMap<Integer, String> AllSupportStandardsText = getAllSupportStandardsText();
        if (AllSupportStandardsText.size() <= 0) {
            AllSupportStandardsText.put(13, getResources().getString(R.string.self));
//            AllSupportStandardsText.put(11, "PAL");
//            AllSupportStandardsText.put(12, "NTSC");
        }
        Log.i(TAG, "onStart: AllSupportStandardsText=" + AllSupportStandardsText.toString());

        mList_Tv[num] = getResources().getString(R.string.self);
        mtv_value[num] = "13";
        num++;
        if (AllSupportStandardsText.containsKey(0)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(0)");
            mList_Tv[num] = "1080P-60HZ";
            mtv_value[num] = "0";
            num++;
        }
        if (AllSupportStandardsText.containsKey(1)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(1)");
            mList_Tv[num] = "1080P-50HZ";
            mtv_value[num] = "1";
            num++;
        }
        if (AllSupportStandardsText.containsKey(2)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(2)");
            mList_Tv[num] = "1080P-30HZ";
            mtv_value[num] = "2";
            num++;
        }
        if (AllSupportStandardsText.containsKey(3)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(3)");
            mList_Tv[num] = "1080P-25HZ";
            mtv_value[num] = "3";
            num++;
        }
        if (AllSupportStandardsText.containsKey(4)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(4)");
            mList_Tv[num] = "1080P-24HZ";
            mtv_value[num] = "4";
            num++;
        }
        if (AllSupportStandardsText.containsKey(5)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(5)");
            mList_Tv[num] = "1080I-60HZ";
            mtv_value[num] = "5";
            num++;
        }
        if (AllSupportStandardsText.containsKey(6)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(6)");
            mList_Tv[num] = "1080I-50HZ";
            mtv_value[num] = "6";
            num++;
        }
        if (AllSupportStandardsText.containsKey(7)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(7)");
            mList_Tv[num] = "720P-60HZ";
            mtv_value[num] = "7";
            num++;
        }
        if (AllSupportStandardsText.containsKey(8)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(8)");
            mList_Tv[num] = "720P-50HZ";
            mtv_value[num] = "8";
            num++;
        }
        if (AllSupportStandardsText.containsKey(9)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(9)");
            mList_Tv[num] = "576-50HZ";
            mtv_value[num] = "9";
            num++;
        }
        if (AllSupportStandardsText.containsKey(10)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(10)");
            mList_Tv[num] = "480-60HZ";
            mtv_value[num] = "10";
            num++;
        }
        if (AllSupportStandardsText.containsKey(11)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(11)");
            mList_Tv[num] = "PAL";
            mtv_value[num] = "11";
            num++;
        }
        if (AllSupportStandardsText.containsKey(12)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(12)");
            mList_Tv[num] = "NTSC";
            mtv_value[num] = "12";
            num++;
        }
        if (AllSupportStandardsText.containsKey(256)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(256)");
            mList_Tv[num] = "2160P-24HZ";
            mtv_value[num] = "256";
            num++;
        }
        if (AllSupportStandardsText.containsKey(257)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(257)");
            mList_Tv[num] = "2160P-25HZ";
            mtv_value[num] = "257";
            num++;
        }
        if (AllSupportStandardsText.containsKey(258)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(258)");
            mList_Tv[num] = "2160P-30HZ";
            mtv_value[num] = "258";
            num++;
        }
        if (AllSupportStandardsText.containsKey(259)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(259)");
            mList_Tv[num] = "2160P-60HZ";
            mtv_value[num] = "259";
            num++;
        }
        if (AllSupportStandardsText.containsKey(260)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(260)");
            mList_Tv[num] = "2160P-50HZ";
            mtv_value[num] = "260";
            num++;
        }
        if (AllSupportStandardsText.containsKey(512)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(512)");
            mList_Tv[num] = "4k-24HZ";
            mtv_value[num] = "512";
            num++;
        }
        if (AllSupportStandardsText.containsKey(513)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(513)");
            mList_Tv[num] = "4k-25HZ";
            mtv_value[num] = "513";
            num++;
        }
        if (AllSupportStandardsText.containsKey(514)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(514)");
            mList_Tv[num] = "4k-30HZ";
            mtv_value[num] = "514";
            num++;
        }
        if (AllSupportStandardsText.containsKey(515)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(515)");
            mList_Tv[num] = "4k-60HZ";
            mtv_value[num] = "515";
            num++;
        }
        if (AllSupportStandardsText.containsKey(516)) {
            Log.i(TAG, "onResume: AllSupportStandardsText.containsKey(516)");
            mList_Tv[num] = "4k-50HZ";
            mtv_value[num] = "516";
            num++;
        }
        mTvCount = mtv_value.length;
        Log.i(TAG, "mTvCount=" + mTvCount);
        AddMap();
        mTvOldFmt = mDisplayManager.getCurrentStandard();
        Log.i(TAG, "onStart: mTvOldFmt=" + mTvOldFmt);
        String info = map_key.get(mTvOldFmt + "");
        if (TextUtils.isEmpty(info)) {
            Log.i(TAG, "onStart: info = map_key.get(mTvOldFmt + \"\")=null");
//            mList_Tv = mContext.getResources().getStringArray(
//                    R.array.set_video_tv_list_all_1);
//
//            mtv_value = mContext.getResources().getStringArray(
//                    R.array.set_video_tv_value_all_1);
//            map_value.clear();
//            map_key.clear();
//            AddMap();
        }
        mTvFmt = getIndex(mTvOldFmt);
        Log.i(TAG, "mTvFmt=" + mTvFmt);
        InitModeDialog(mContext);
    }

    public HashMap<Integer, String> getAllSupportStandardsText() {
        int[] supportRet = mDisplayManager.getAllSupportStandards();
        HashMap<Integer, String> supportRetText = new HashMap();

        for (int i = 0; i < supportRet.length; i++) {
            if (this.mSupportsMapText.containsKey(supportRet[i])) {
                supportRetText.put(supportRet[i], this.mSupportsMapText.get(supportRet[i]));
            }
        }
        return supportRetText;
    }

    private void AddMap() {
        mTvCount = mtv_value.length;
        Log.i(TAG, "AddMap: mTvCount=mtv_value.length=" + mTvCount);
        for (int i = 0; i < mTvCount; i++) {
            map_value.put(mList_Tv[i], mtv_value[i]);
            map_key.put(mtv_value[i], mList_Tv[i]);
        }
    }

    private void InitModeDialog(Context context) {
        Log.i(TAG, "InitModeDialog: ");
        //初始化设置分辨率界面
        mContext = context;

        //将要显示给用户的分辨率选项add进list
        list = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < mList_Tv.length; i++) {
            if (!TextUtils.isEmpty(mList_Tv[i])) {
                HashMap<String, Object> map;
                map = new HashMap<String, Object>();
                map.put("list", mList_Tv[i]);
                map.put("boolean", false);//初始化为未选
                list.add(map);
            }
        }
        cur_mode_position = mTvFmt;
        Log.i(TAG, "cur_mode_position=" + cur_mode_position);
        //给listview设置适配器
        myAdapter = new Resolution_Adapter(mContext, list);
        String info = map_key.get(mTvOldFmt + "");
        list_resolution.setAdapter(myAdapter);

        for (int i = 0; i < mList_Tv.length; i++) {//默认选中当前的分辨率选项
            if (!TextUtils.isEmpty(info)) {
                if (info.equals(mList_Tv[i])) {
                    curOutputMode = info;
                    list_resolution.setSelection(i);
                    list_resolution.requestFocus();
                    myAdapter.setSelectedIndex(i);
                    finalPosition = i;
                    myAdapter.notifyDataSetChanged();
                }
            }
        }


        list_resolution.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //设置监听事件，当选中的分辨率为当前分辨率，给用户提示分辨率未变，否则将选中的分辨率给用户预览，并显示询问对话框。
                Log.i(TAG, "onItemClick: ");
                clickedPosition = position;
                newMode = mList_Tv[position];
                Log.i(TAG, "mList_Tv[position]=" + mList_Tv[position]);
                Log.i(TAG, "map_value.get(mList_Tv[position] + \"\")=" + map_value.get(mList_Tv[position] + ""));
                mTvNewFmt = Integer.parseInt(map_value.get(mList_Tv[position] + ""));
                Log.i(TAG, "onItemClick mTvNewFmt = " + mTvNewFmt);
                Log.i(TAG, "onItemClick mTvOldFmt = " + mTvOldFmt);
                if (mTvOldFmt != mTvNewFmt) {
                    mDisplayManager.setDisplayStandard(mTvNewFmt);
                    ShowDialog(true);
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.tip_re2), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ShowDialog(final boolean reset) {//显示询问对话框
        Log.i(TAG, "ShowDialog: ");
        dialog = new ResolutionDialog(mContext,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen,
                new RecoveryDialog.OnCallback() {
                    @Override
                    public void OkcallBack() {//点击确定，关闭定时器，将系统设置为选中的分辨率
                        cancelTimer();
                        myAdapter.setSelectedIndex(clickedPosition);
                        myAdapter.notifyDataSetChanged();
                        mTvOldFmt = mTvNewFmt;
//                        int j = mDisplay.saveParam();
//                        mDisplay.setOptimalFormatEnable(0);

                        mDisplayManager.saveParams();
                        finalPosition = clickedPosition;
                        curOutputMode = newMode;

                    }

                    @Override
                    public void CancelcallBack() {//点击取消，关闭定时器，将系统分辨率复原
                        cancelTimer();
//                        mDisplay.setFmt(mTvOldFmt);
//                        mDisplay.saveParam();
                        mDisplayManager.setDisplayStandard(mTvOldFmt);
                        mDisplayManager.saveParams();

                        myAdapter.setSelectedIndex(finalPosition);
                        myAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void MidcallBack() {
                        // TODO Auto-generated method stub
                    }
                });
        dialog.getWindow()
                .setType(WindowManager.LayoutParams.TYPE_APPLICATION);
        dialog.show();
        dialog.setCancelable(false);
        dialog.SetText(getResources().getString(R.string.tip_resolution) + "（" + mList_Tv[clickedPosition] + "）？");
        startTimer();
    }

    private void startTimer() {//开启定时器，10s后mhandler发送message
        if (mtimer == null) {
            mtimer = new Timer();
            mtimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mhandler.sendEmptyMessage(FRESH_FMT);
                }
            }, 10000);//设定十秒计时器
        }
    }

    private void InitMaps() {
        mSupportsMapText.clear();
        this.mSupportsMapText.put(0, "1080P-60HZ");
        this.mSupportsMapText.put(1, "1080P-50HZ");
        this.mSupportsMapText.put(2, "1080P-30HZ");
        this.mSupportsMapText.put(3, "1080P-25HZ");
        this.mSupportsMapText.put(4, "1080P-24HZ");
        this.mSupportsMapText.put(5, "1080I-60HZ");
        this.mSupportsMapText.put(6, "1080I-50HZ");
        this.mSupportsMapText.put(7, "720P-60HZ");
        this.mSupportsMapText.put(8, "720P-50HZ");
        this.mSupportsMapText.put(9, "576-50HZ");
        this.mSupportsMapText.put(10, "480-60HZ");
        this.mSupportsMapText.put(11, "PAL");
        this.mSupportsMapText.put(12, "NTSC");
        this.mSupportsMapText.put(256, "2160P-24HZ");
        this.mSupportsMapText.put(257, "2160P-25HZ");
        this.mSupportsMapText.put(258, "2160P-30HZ");
        this.mSupportsMapText.put(259, "2160P-60HZ");
        this.mSupportsMapText.put(260, "2160P-50HZ");

        this.mSupportsMapText.put(512, "4k-24HZ");
        this.mSupportsMapText.put(513, "4k-25HZ");
        this.mSupportsMapText.put(514, "4k-30HZ");
        this.mSupportsMapText.put(515, "4k-60HZ");
        this.mSupportsMapText.put(516, "4k-50HZ");
    }

    private void cancelTimer() {//关闭定时器
        if (mtimer != null) {
            mtimer.cancel();
            mtimer = null;
        }
    }

    private int getIndex(int fmt) {
        int ret = 0;
        String info = map_key.get(fmt + "");
        if (info == null)
            return ret;
        for (int i = 0; i < mTvCount; i++) {
            if (info.equals(mList_Tv[i])) {
                ret = i;
                break;
            }
        }
        return ret;
    }
}
