package com.taghawk;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.lifecycle.LifecycleObserver;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.taghawk.bluesnap.BlueSnapDetails;
import com.taghawk.custom_view.PositionedLinkedHashmap;
import com.taghawk.data.DataManager;
import com.taghawk.interfaces.LifeCycleListener;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.util.AppLifecycleHandler;
import com.taghawk.util.ResourceUtils;



import java.util.HashMap;

import io.branch.referral.Branch;
import siftscience.android.Sift;

//@ReportsCrashes(
//        mailTo = "amar.kumar@appinventiv.com",
//        mode = ReportingInteractionMode.TOAST,
//        resToastText = R.string.crash_toast_text)
public class TagHawkApplication extends MultiDexApplication implements LifeCycleListener, LifecycleObserver {

    private static Context context;
    public static Context getContext() {
        return context;
    }
    private PositionedLinkedHashmap<String, ChatModel> chatInboxMap;
    private static TagHawkApplication tagHawkApplication;
    private boolean isMessageTabVisible = false;

    public static HandlerThread handlerThread;
    public static Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        //bluesnap integration related code
        handlerThread = new HandlerThread("MerchantTokenURLConnection");
        handlerThread.start();
        mainHandler = new Handler(handlerThread.getLooper());

        //sift sdk integration code
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksHandler());

        context = getApplicationContext();
        tagHawkApplication = this;
        ResourceUtils.init(this);
//        ACRA.init(this);
        Branch.getAutoInstance(this);
        FirebaseApp.initializeApp(this);
        DataManager dataManager = DataManager.init(context);
        dataManager.initApiManager();
        AppLifecycleHandler appLifecycleHandler = new AppLifecycleHandler(this);
        registerLifecycleHandler(appLifecycleHandler);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        // Branch logging for debugging
        Branch.enableDebugMode();

        // Branch object initialization
        Branch.getAutoInstance(this);
    }

    //get current class instance
    public static TagHawkApplication getInstance() {
        return tagHawkApplication;
    }

    public PositionedLinkedHashmap<String, ChatModel> getChatInboxMap() {
        return chatInboxMap;
    }

    public void setChatInboxMap(PositionedLinkedHashmap<String, ChatModel> chatInboxMap) {
        this.chatInboxMap = chatInboxMap;
    }

    public boolean isMessageTabVisible() {
        return isMessageTabVisible;
    }

    public void setMessageTabVisible(boolean messageTabVisible) {
        isMessageTabVisible = messageTabVisible;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void registerLifecycleHandler(AppLifecycleHandler appLifecycleHandler) {
        registerActivityLifecycleCallbacks(appLifecycleHandler);
        registerComponentCallbacks(appLifecycleHandler);
    }

    @Override
    public void onAppBackgrounded() {
    }

    @Override
    public void onAppForegrounded() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private static final class ActivityLifecycleCallbacksHandler
            implements ActivityLifecycleCallbacks {
        public void onActivityCreated(Activity activity, Bundle bundle) {
            Sift.open(activity, new Sift.Config.Builder()
                    .withAccountId(BlueSnapDetails.SIFT_ACCOUNT_ID)   //account id
                    .withBeaconKey(BlueSnapDetails.SIFT_BEACON_KEY)   //beacon key

                    .build());
            Sift.collect();
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        public void onActivityPaused(Activity activity) {
            Sift.pause();
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        public void onActivityResumed(Activity activity) {
            Sift.resume(activity);
        }
        public void onActivityDestroyed(Activity activity) {
            Sift.close();
        }
    }

}
