package com.taghawk.ui.tag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.util.AppUtils;

public class TagDetailsActivity extends BaseActivity {

    private String tagId = "";
    private String notificationID;
    TagDetailsFragment fragment;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        addInitialFragment();
    }

    private void getIntentData() {
        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("TAG_ID")) {
            tagId = getIntent().getExtras().getString("TAG_ID");
            notificationID = getIntent().getExtras().getString(AppConstants.NOTIFICATION_ACTION.NOTIFICATION_ID);

        }
    }

    private void addInitialFragment() {
        fragment = new TagDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TAG_ID", tagId);
        bundle.putString(AppConstants.NOTIFICATION_ACTION.NOTIFICATION_ID, notificationID);
        fragment.setArguments(bundle);
        addFragment(R.id.home_container, fragment, TagDetailsFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
