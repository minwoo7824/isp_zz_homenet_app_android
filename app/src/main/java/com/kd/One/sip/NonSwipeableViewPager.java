package com.kd.One.sip;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NonSwipeableViewPager extends ViewPager {
    private boolean isEnabled = false;

    public NonSwipeableViewPager(Context context) {
        super(context);
        isEnabled = false;
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isEnabled = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isEnabled ? false : super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isEnabled ? false : super.onTouchEvent(event);
    }

    public void setIsEnabled(boolean enabled){
        isEnabled = enabled;
    }
}

