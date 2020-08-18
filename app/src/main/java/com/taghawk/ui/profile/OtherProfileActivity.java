package com.taghawk.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.util.AppUtils;

public class OtherProfileActivity extends BaseActivity {

    private String sellerId;
    OtherProfileFragment fragment;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addOtherProfileFragment();
    }

    private void addOtherProfileFragment() {
        getIntentData();

        fragment = new OtherProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.BUNDLE_DATA, sellerId);
        fragment.setArguments(bundle);

        replaceFragment(R.id.home_container, fragment, OtherProfileFragment.class.getSimpleName());
    }

    private void getIntentData() {
        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.BUNDLE_DATA)) {

            sellerId = getIntent().getExtras().getString(AppConstants.BUNDLE_DATA);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.FOLLOWFOLLOWING:
                if (resultCode == Activity.RESULT_OK) {
                    if (fragment != null) {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
