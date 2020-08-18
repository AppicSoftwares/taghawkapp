package com.taghawk.camera2basic;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.TextureView;

public class SquaredTextureView  extends TextureView {
    public SquaredTextureView(Context context) {
        super(context);
    }

    public SquaredTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquaredTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SquaredTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, heightMeasureSpec);
    }

    public static int getHieghtInSixteenNine(int width) {
        float calculatedHieght = (float) ((0.5625) * width);
        return Math.round(calculatedHieght);
    }
}