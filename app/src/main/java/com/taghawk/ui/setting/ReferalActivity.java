package com.taghawk.ui.setting;

import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;

public class ReferalActivity extends BaseActivity {

    private boolean isOpenAddBankAccount = false;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addInitialFragment();
    }

    private void addInitialFragment() {
        FragmentReferFriend fragment = new FragmentReferFriend();
        replaceFragment(R.id.home_container, fragment, FragmentReferFriend.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
