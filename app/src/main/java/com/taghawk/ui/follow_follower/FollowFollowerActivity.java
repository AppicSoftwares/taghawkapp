package com.taghawk.ui.follow_follower;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class FollowFollowerActivity extends BaseActivity {
    private int type;
    private String userId;
    private boolean isOtherProfile;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        openFollowFollowingFragment();
    }

    private void getIntentData() {
        type = getIntent().getExtras().getInt(AppConstants.BUNDLE_DATA);
        userId = getIntent().getExtras().getString(AppConstants.KEY_CONSTENT.USER_ID);
        isOtherProfile = getIntent().getExtras().getBoolean("IS_OTHER_PROFILE", true);
    }

    private void openFollowFollowingFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.BUNDLE_DATA, type);
        bundle.putString(AppConstants.KEY_CONSTENT.USER_ID, userId);
        bundle.putBoolean("IS_OTHER_PROFILE", isOtherProfile);
        FollowFollowerFragment fragment = new FollowFollowerFragment();
        fragment.setArguments(bundle);
        addFragment(R.id.home_container, fragment, FollowFollowerFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {

        setResult(Activity.RESULT_OK);
        finish();
    }
}
