package com.changhong.settings.Image_Sound;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.display.DisplayManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.changhong.settings.R;

public class Scaling_Activity extends Activity {
    private static final String TAG = "ty_ScalingActivity";
    private LinearLayout ll_scaling;
    private ImageButton ib_scaling;
    private TextView tv_tips;
    private int mScale_hstep = 1;
    private int top_margin = 0;
    private int left_margin = 0;
    private int right_margin = 0;
    private int bottom_margin = 0;
    private final int REFRESH_LAYOUT = 1;
    private final int REFRESH_ARROW = 2;
    private DisplayManager mDisplayManager;
    private Context mContext;
    private LinearLayout ll_forchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaling);
        mContext = this;
        mDisplayManager = (DisplayManager) mContext.getSystemService(Context.DISPLAY_MANAGER_SERVICE);
        int[] curmargin = mDisplayManager.getScreenMargin();
        if (curmargin.length >= 4) {
            left_margin = curmargin[0];
            top_margin = curmargin[1];
            right_margin = curmargin[2];
            bottom_margin = curmargin[3];
        }
        Log.i(TAG, "onCreate: mDisplayManager.getScreenMargin() left_margin="+left_margin+",top_margin="+top_margin+",right_margin="+right_margin+",bottom_margin="+bottom_margin);

        init();

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_LAYOUT:
                    if (top_margin < 0)
                        top_margin = 0;
                    if (left_margin < 0)
                        left_margin = 0;
                    if (right_margin < 0)
                        right_margin = 0;
                    if (bottom_margin < 0)
                        bottom_margin = 0;
                    if (top_margin > 136)
                        top_margin = 136;
                    if (left_margin > 200)
                        left_margin = 200;
                    if (right_margin > 200)
                        right_margin = 200;
                    if (bottom_margin > 136)
                        bottom_margin = 136;
                    Log.i(TAG, "handleMessage: mDisplayManager.setGraphicOutRange(left_margin, top_margin, right_margin, bottom_margin)="
                            + left_margin + "," + top_margin + "," + right_margin + "," + bottom_margin);
                    mDisplayManager.setScreenMargin(left_margin, top_margin, right_margin, bottom_margin);
                    mDisplayManager.saveParams();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: keyCode=" + keyCode);
        ll_forchange.setVisibility(View.VISIBLE);
        ll_forchange.setVisibility(View.INVISIBLE);
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (ib_scaling.getTag().equals(R.drawable.displayrect_top)) {
                setTopUp();
            } else if (ib_scaling.getTag().equals(R.drawable.displayrect_bottom)) {
                setbottomUp();
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (ib_scaling.getTag().equals(R.drawable.displayrect_top)) {
                setTopDown();
            } else if (ib_scaling.getTag().equals(R.drawable.displayrect_bottom)) {
                setbottomDown();
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (ib_scaling.getTag().equals(R.drawable.displayrect_left)) {
                setLeftLeft();
            } else if (ib_scaling.getTag().equals(R.drawable.displayrect_right)) {
                setRightLeft();
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (ib_scaling.getTag().equals(R.drawable.displayrect_left)) {
                setLeftRight();
            } else if (ib_scaling.getTag().equals(R.drawable.displayrect_right)) {
                setRightRight();
            }
        }

        return super.onKeyDown(keyCode, event);
    }
    private void setRightRight(){
        Log.i(TAG, "setRightRight: ");
        if (top_margin > 0 || bottom_margin > 0 || left_margin > 0
                || right_margin > 0) {
            right_margin = right_margin - mScale_hstep;
            if (right_margin < 0)
                right_margin = 0;
            if (right_margin > 200)
                right_margin = 200;
            Log.i(TAG, "right_margin= " + right_margin);
        }
        mHandler.sendEmptyMessage(REFRESH_LAYOUT);
    }
    private void setLeftRight(){
        Log.i(TAG, "setLeftRight: ");
        if (left_margin < 200) {
            left_margin = left_margin + mScale_hstep;
            if (left_margin < 0)
                left_margin = 0;
            if (left_margin > 200)
                left_margin = 200;
            Log.i(TAG, "left_margin= " + left_margin);
        }
        mHandler.sendEmptyMessage(REFRESH_LAYOUT);
    }
    private void setRightLeft(){
        Log.i(TAG, "setRightLeft: ");
        if (right_margin < 200) {
            right_margin = right_margin + mScale_hstep;
            if (right_margin < 0)
                right_margin = 0;
            if (right_margin > 200)
                right_margin = 200;
            Log.i(TAG, "right_margin= " + right_margin);
        }
        mHandler.sendEmptyMessage(REFRESH_LAYOUT);
    }
    private void setLeftLeft(){
        Log.i(TAG, "setLeftLeft: ");
        if (top_margin > 0 || bottom_margin > 0 || left_margin > 0
                || right_margin > 0) {
            left_margin = left_margin - mScale_hstep;
            if (left_margin < 0)
                left_margin = 0;
            if (left_margin > 200)
                left_margin = 200;
            Log.i(TAG, "left_margin= " + left_margin);
        }
        mHandler.sendEmptyMessage(REFRESH_LAYOUT);
    }

    private void setbottomDown(){
        Log.i(TAG, "setbottomDown: ");
        mHandler.removeMessages(REFRESH_LAYOUT);
        if (top_margin > 0 || bottom_margin > 0 || left_margin > 0
                || right_margin > 0) {
            bottom_margin = bottom_margin - mScale_hstep;
            if (bottom_margin < 0)
                bottom_margin = 0;
            if (bottom_margin > 136)
                bottom_margin = 136;
            Log.i(TAG, "bottom_margin= " + bottom_margin);
        }
        mHandler.sendEmptyMessage(REFRESH_LAYOUT);
    }
    private void setTopDown(){
        Log.i(TAG, "setTopDown: ");
        if (top_margin < 136) {
            top_margin = top_margin + mScale_hstep;
            if (top_margin < 0)
                top_margin = 0;
            if (top_margin > 136)
                top_margin = 136;
            Log.i(TAG, "top_margin= " + top_margin);
        }
        mHandler.sendEmptyMessage(REFRESH_LAYOUT);
    }
    private void setbottomUp(){
        Log.i(TAG, "setbottomUp: ");
        if (bottom_margin < 136) {
            bottom_margin = bottom_margin + mScale_hstep;
            if (bottom_margin < 0)
                bottom_margin = 0;
            if (bottom_margin > 136)
                bottom_margin = 136;
            Log.i(TAG, "bottom_margin= " + bottom_margin);
        }
        mHandler.sendEmptyMessage(REFRESH_LAYOUT);
    }
    private void setTopUp(){
        Log.i(TAG, "setTopUp: ");
        mHandler.removeMessages(REFRESH_LAYOUT);
        if (top_margin > 0 || bottom_margin > 0 || left_margin > 0
                || right_margin > 0) {
            top_margin = top_margin - mScale_hstep;
            if (top_margin < 0)
                top_margin = 0;
            if (top_margin > 136)
                top_margin = 136;
            Log.i(TAG, "top_margin= " + top_margin);
        }
        mHandler.sendEmptyMessage(REFRESH_LAYOUT);
    }

    private void setLeft() {
        Log.i(TAG, "setLeft: ");
        if (top_margin > 0 || bottom_margin > 0 || left_margin > 0
                || right_margin > 0) {
            left_margin = left_margin - mScale_hstep;
            right_margin = left_margin;
            if (top_margin < 0)
                top_margin = 0;
            if (left_margin < 0)
                left_margin = 0;
            if (right_margin < 0)
                right_margin = 0;
            if (bottom_margin < 0)
                bottom_margin = 0;
            if (top_margin > 136)
                top_margin = 136;
            if (left_margin > 200)
                left_margin = 200;
            if (right_margin > 200)
                right_margin = 200;
            if (bottom_margin > 136)
                bottom_margin = 136;
            Log.i(TAG, "left_margin= " + left_margin);
            Log.i(TAG, "top_margin= " + top_margin);
        }
        mHandler.sendEmptyMessage(REFRESH_LAYOUT);
    }

    private void setRight() {
        Log.i(TAG, "setRight: ");
        if (left_margin < 200) {
            left_margin = left_margin + mScale_hstep;
            right_margin = left_margin;
            if (top_margin < 0)
                top_margin = 0;
            if (left_margin < 0)
                left_margin = 0;
            if (right_margin < 0)
                right_margin = 0;
            if (bottom_margin < 0)
                bottom_margin = 0;
            if (top_margin > 136)
                top_margin = 136;
            if (left_margin > 200)
                left_margin = 200;
            if (right_margin > 200)
                right_margin = 200;
            if (bottom_margin > 136)
                bottom_margin = 136;
            Log.i(TAG, "left_margin= " + left_margin);
            Log.i(TAG, "top_margin= " + top_margin);
        }
        mHandler.sendEmptyMessage(REFRESH_LAYOUT);
    }

    private void init() {
        Log.i(TAG, "into init: ");
        ll_scaling = (LinearLayout) findViewById(R.id.ll_scaling);
        ib_scaling = (ImageButton) findViewById(R.id.ib_scaling);
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        ib_scaling.setTag(R.drawable.displayrect_left);
        ll_forchange=(LinearLayout)findViewById(R.id.ll_forchange);
        ib_scaling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
                Log.i(TAG, "onClick: ib_scaling.getTag()=" + ib_scaling.getTag());
                if (ib_scaling.getTag().equals(R.drawable.displayrect_left)) {
                    Log.i(TAG, "onClick: R.drawable.displayrect_left");
                    ib_scaling.setBackground(getResources().getDrawable(R.drawable.displayrect_top));
                    ib_scaling.setTag(R.drawable.displayrect_top);
                    ll_scaling.setBackground(getResources().getDrawable(R.drawable.top));
                    tv_tips.setText(getResources().getString(R.string.scaling_tips2_up));

                } else if (ib_scaling.getTag().equals(R.drawable.displayrect_top)) {
                    Log.i(TAG, "onClick: R.drawable.displayrect_top");
                    ib_scaling.setBackground(getResources().getDrawable(R.drawable.displayrect_right));
                    ib_scaling.setTag(R.drawable.displayrect_right);
                    ll_scaling.setBackground(getResources().getDrawable(R.drawable.right));
                    tv_tips.setText(getResources().getString(R.string.scaling_tips2_right));

                } else if (ib_scaling.getTag().equals(R.drawable.displayrect_right)) {
                    Log.i(TAG, "onClick: R.drawable.displayrect_right");
                    ib_scaling.setBackground(getResources().getDrawable(R.drawable.displayrect_bottom));
                    ib_scaling.setTag(R.drawable.displayrect_bottom);
                    ll_scaling.setBackground(getResources().getDrawable(R.drawable.bottom));
                    tv_tips.setText(getResources().getString(R.string.scaling_tips2_down));

                } else if (ib_scaling.getTag().equals(R.drawable.displayrect_bottom)) {
                    Log.i(TAG, "onClick: R.drawable.displayrect_bottom");
                    ib_scaling.setBackground(getResources().getDrawable(R.drawable.displayrect_left));
                    ib_scaling.setTag(R.drawable.displayrect_left);
                    ll_scaling.setBackground(getResources().getDrawable(R.drawable.left));
                    tv_tips.setText(getResources().getString(R.string.scaling_tips2_left));

                }
            }
        });
    }
}
