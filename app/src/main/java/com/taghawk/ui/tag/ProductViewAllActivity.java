package com.taghawk.ui.tag;

import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class ProductViewAllActivity extends BaseActivity {
    private String communityId;

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

    private void addInitialFragment() {

        ProductViewAllFragment fragment = new ProductViewAllFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.BUNDLE_DATA, communityId);
        fragment.setArguments(bundle);
        addFragment(R.id.home_container, fragment, ProductViewAllFragment.class.getSimpleName());
    }

    private void getIntentData() {
        communityId = getIntent().getExtras().getString(AppConstants.BUNDLE_DATA);
    }
}
