package com.taghawk.custom_view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;

import com.taghawk.R;
import com.taghawk.util.PathProvider;


public class CurveViewContainer extends FrameLayout {

    Context mContext;
    Path mClipPath;
    Path mOutlinePath;
    int width = 0;
    int height = 0;
    int gravity = ViewHelper.Gravity.TOP;
    int curvatureHeight = 50;
    int curvatureDirection = ViewHelper.CurvatureDirection.OUTWARD;
    Paint mPaint;
    private PorterDuffXfermode porterDuffXfermode;

    public CurveViewContainer(Context context) {
        super(context);
        init(context, null);
    }

    public CurveViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);

        mClipPath = new Path();
        mOutlinePath = new Path();

        TypedArray styledAttributes = mContext.obtainStyledAttributes(attrs, R.styleable.CurveViewContainer, 0, 0);
        curvatureHeight = (int) styledAttributes.getDimension(R.styleable.CurveViewContainer_curvature, getResources().getDimension(R.dimen.curve));//getDpForPixel(curvatureHeight)
        if (styledAttributes.getInt(R.styleable.CurveViewContainer_gravity, Gravity.BOTTOM) == Gravity.BOTTOM) {
            gravity = ViewHelper.Gravity.BOTTOM;
        } else {
            gravity = ViewHelper.Gravity.TOP;
        }
        styledAttributes.recycle();
    }

    public int getCurvatureHeight() {
        return curvatureHeight;
    }

    public void setCurvatureHeight(float curvatureHeight) {
        this.curvatureHeight = (int) curvatureHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        mClipPath = PathProvider.getClipPath(width, height, curvatureHeight, curvatureDirection, gravity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            ViewCompat.setElevation(this, ViewCompat.getElevation(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                setOutlineProvider(getOutlineProvider());
            } catch (Exception e) {
                Log.d(getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public ViewOutlineProvider getOutlineProvider() {
        return new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                try {
                    outline.setConvexPath(PathProvider.getOutlinePath(width, height, curvatureHeight, curvatureDirection, gravity));
                } catch (Exception e) {
                    Log.d("Outline Path", e.getMessage());
                }
            }
        };
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        mPaint.setXfermode(porterDuffXfermode);
        canvas.drawPath(mClipPath, mPaint);
        if (!isInEditMode())
            canvas.restoreToCount(saveCount);
        mPaint.setXfermode(null);
    }

    static public class Gravity {
        static final int TOP = 0;
        static final int BOTTOM = 1;
    }
}
