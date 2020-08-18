package com.taghawk.ui.profile;

import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class ProfileEditActivity extends BaseActivity {
    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addIntinalFragment();
    }

    private void addIntinalFragment() {
        ProfileEditFragment fragment = new ProfileEditFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConstants.BUNDLE_DATA, true);
        fragment.setArguments(bundle);
        replaceFragment(R.id.home_container, fragment, ProfileEditFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
