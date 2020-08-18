package com.taghawk.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

import com.taghawk.interfaces.LifeCycleListener;


/**
 * Created by appinventiv on 9/2/18.
 */

public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks,ComponentCallbacks2 {
    private LifeCycleListener lifeCycleListener;
    private boolean appInForeground;

    public AppLifecycleHandler(LifeCycleListener lifeCycleListener)
    {
        this.lifeCycleListener=lifeCycleListener;
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (!appInForeground) {
            appInForeground = true;
            lifeCycleListener.onAppForegrounded();
        }

    }

    @Override
    public void onTrimMemory(int i) {
        if (i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            appInForeground=false;
            // lifecycleDelegate instance was passed in on the constructor
            lifeCycleListener.onAppBackgrounded();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


}
