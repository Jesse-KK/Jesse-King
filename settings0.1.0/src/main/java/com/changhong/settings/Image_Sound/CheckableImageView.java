package com.changhong.settings.Image_Sound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Checkable;
import android.widget.ImageView;


@SuppressLint("AppCompatCustomView")
public class CheckableImageView extends ImageView implements Checkable {
    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };
    private boolean mChecked;

    public CheckableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableImageView(Context context) {
        super(context);
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked != mChecked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
        refreshDrawableState();
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setChecked(mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setCheckable(true);
        info.setChecked(mChecked);
    }

}