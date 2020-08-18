package com.taghawk.ui.home.product_details;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.ui.tag.ProductViewAllFragment;

public class MyProductsActivity extends BaseActivity {

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
        addFragment(R.id.home_container, new MyProductsFragment(), ProductViewAllFragment.class.getSimpleName());
    }
}
