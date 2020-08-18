package com.taghawk.util;

import android.content.Context;
import android.graphics.drawable.Drawable;


import androidx.core.content.ContextCompat;

public class ResourceUtils {

    private static ResourceUtils resourceUtils;
    private Context context;

    private ResourceUtils(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        if (resourceUtils == null) {
            synchronized (ResourceUtils.class) {
                if (resourceUtils == null) {
                    resourceUtils = new ResourceUtils(context);
                }
            }
        }
    }

    public static ResourceUtils getInstance() {
        if (resourceUtils == null) {
            throw new IllegalStateException("Must call init() before getInstance()");
        }
        return resourceUtils;
    }


    public int getColor(int colorResId) {
        return ContextCompat.getColor(context, colorResId);
    }

    public Drawable getDrawable(int drawableResId) {
        return ContextCompat.getDrawable(context, drawableResId);
    }

    public CharSequence getString(int resId) {
        return context.getString(resId);
    }
}
