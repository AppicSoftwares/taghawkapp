package com.taghawk.ui.cart;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.util.AppUtils;

public class CartActivity extends BaseActivity {
    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addInitialFragment();
    }


    // Add Cart Fragment
    private void addInitialFragment() {

        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);
        addFragment(R.id.home_container, new CartFragment(), CartFragment.class.getSimpleName());
    }
}
