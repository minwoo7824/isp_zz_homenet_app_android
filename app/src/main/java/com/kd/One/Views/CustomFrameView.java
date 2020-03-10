package com.kd.One.Views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CustomFrameView extends RelativeLayout {
    public CustomFrameView(Context context) {
        super(context);
    }

    public CustomFrameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width/19 * 11;

        setMeasuredDimension(width, height);
    }
}
