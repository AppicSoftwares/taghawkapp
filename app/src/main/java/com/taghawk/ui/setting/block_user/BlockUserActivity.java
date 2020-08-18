package com.taghawk.ui.setting.block_user;

import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;

public class BlockUserActivity extends BaseActivity {

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment(R.id.home_container, new BlockUserFragment(), BlockUserFragment.class.getSimpleName());
    }
}
