package com.taghawk.ui.tag;

import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class MyTagsActivity extends BaseActivity {

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
        addFragment(R.id.home_container, new MyTagsFragment(), MyTagsFragment.class.getSimpleName());
    }
}
