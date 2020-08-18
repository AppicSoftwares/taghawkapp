package com.taghawk.ui.splash;



import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.ui.onboard.login.LoginActivity;
import com.taghawk.ui.walkthrough.WalkThroughActivity;
import com.taghawk.util.AppUtils;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class SplashActivity extends BaseActivity {

    private static final int SPLASH_TIME_OUT = 2000;
    /**
     * A {@link SplashViewModel} object to handle all the actions and business logic of splash
     */
    private SplashViewModel mSplashViewModel;
    private String linkId = "";
    private String deepLinkType;
    private String productId = "";
    private String type = "", userId = "";
    private boolean isForeground = true;
    private Branch.BranchReferralInitListener branchReferralInitListener =
            new Branch.BranchReferralInitListener() {
                @Override
                public void onInitFinished(@Nullable JSONObject referringParams, @Nullable BranchError error) {
                    if(referringParams != null)
                        Log.d("BRANCH SDK", referringParams.toString());
                    else
                        Log.d("BRANCH SDK", "no response");
                }

            };

    //hash key generate------------------
    public static void printHashKey(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("AppLog", "key:" + hashKey + "=");
                Log.d("keyhash", hashKey);

            }
        } catch (Exception e) {
            Log.e("AppLog", "error:", e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSplashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        printHashKey(this);
        checkIntentData();

        String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DataManager.getInstance().saveDeviceId(androidId);
        Log.e("androidId", "" + androidId);

        if (isForeground)
            showSplashScreen();

    }

    private void checkIntentData() {

        Intent appLinkIntent = getIntent();

        if (appLinkIntent != null) {
            if (appLinkIntent.getData() != null) {
                Uri uri = Uri.parse(appLinkIntent.getData().getQueryParameter("url"));
                linkId = appLinkIntent.getData().getQueryParameter("token");
                //for reset password
                deepLinkType = appLinkIntent.getData().getQueryParameter("type");
                // rest for all
                type = uri.getQueryParameter("type");
                productId = uri.getQueryParameter("id");
                if (type != null && type.equalsIgnoreCase("5")) {
                    userId = uri.getQueryParameter("user_id");

                } else if (deepLinkType != null && deepLinkType.length() > 0 && deepLinkType.equalsIgnoreCase(AppConstants.NOTIFICATION_ACTION.VERIFY_EMAIL)) {
                    type = deepLinkType;
                    deepLinkType = "";
                    productId = linkId;
                }
            } else if (getIntent() != null && getIntent().getExtras() != null && (getIntent().getExtras().containsKey(AppConstants.NOTIFICATION_TYPE))) {
                type = getIntent().getExtras().getString(AppConstants.NOTIFICATION_TYPE);
                productId = getIntent().getExtras().getString(AppConstants.NOTIFICATION_ACTION.ENTITY_ID);
            } else if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("type")) {
                type = getIntent().getExtras().getString("type");
                productId = getIntent().getExtras().getString(AppConstants.NOTIFICATION_ACTION.ENTITY_ID);
            }
        } else if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.NOTIFICATION_TYPE)) {
            type = getIntent().getExtras().getString("type");
            productId = getIntent().getExtras().getString(AppConstants.NOTIFICATION_ACTION.ENTITY_ID);
        }
    }

    private void showSplashScreen() {
        AppUtils.printHashKey(this);
        Handler mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                if (mSplashViewModel.getAccessTokenFromPref() != null) {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                    intent.putExtra(AppConstants.DEEP_INK_CONSTENT.TYPE, type);
                    intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, productId);
                    intent.putExtra(AppConstants.DEEP_INK_CONSTENT.USER_ID, userId);

                } else if (deepLinkType != null && deepLinkType.length() > 0 && linkId != null && linkId.length() > 0) {
                    intent = moveToLogin(deepLinkType, linkId);
                } else {
                    intent = moveToWalkin(deepLinkType, linkId);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @NonNull
    private Intent moveToWalkin(String deepLinkType, String linkId) {
        Intent intent;
        intent = new Intent(SplashActivity.this, WalkThroughActivity.class);
        intent.putExtra(AppConstants.DEEP_INK_CONSTENT.RESET_PASSWORD, deepLinkType);
        intent.putExtra(AppConstants.DEEP_INK_CONSTENT.LINK_ID, linkId);
        return intent;
    }

    @NonNull
    private Intent moveToLogin(String deepLinkType, String linkId) {
        Intent intent;
        intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.putExtra(AppConstants.DEEP_INK_CONSTENT.RESET_PASSWORD, deepLinkType);
        intent.putExtra(AppConstants.DEEP_INK_CONSTENT.LINK_ID, linkId);
        return intent;
    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onResume() {
        if (AppUtils.isForground()) {
            isForeground = true;
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch.getInstance().initSession(branchReferralInitListener, getIntent() != null ?
                getIntent().getData() : null, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        Branch.getInstance().reInitSession(this, branchReferralInitListener);
    }
}
