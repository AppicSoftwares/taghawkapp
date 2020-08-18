package com.taghawk.ui.chat;

import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class GroupMembersActivity extends BaseActivity {

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addInitialFragement();
    }

    private void addInitialFragement() {
        GroupMemberFragment fragment = new GroupMemberFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, getIntent().getParcelableExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA));
        bundle.putParcelable(AppConstants.BUNDLE_DATA, getIntent().getParcelableExtra(AppConstants.BUNDLE_DATA));
        fragment.setArguments(bundle);
        replaceFragment(R.id.home_container, fragment, GroupMemberFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
