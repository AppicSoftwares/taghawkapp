package com.taghawk.ui.setting;

import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;

public class SettingActvity extends BaseActivity {

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
        addFragment(R.id.home_container, new SettingFragment(), SettingFragment.class.getSimpleName());
    }
}
