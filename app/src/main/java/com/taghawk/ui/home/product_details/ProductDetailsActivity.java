package com.taghawk.ui.home.product_details;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.util.AppUtils;

public class ProductDetailsActivity extends BaseActivity {

    private String productId;
    private String notificationID;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getIntentData();
        addInitialFragment();
    }

    private void getIntentData() {
//        RelativeLayout relative = (RelativeLayout) findViewById(R.id.main);
//        relative.setBackgroundResource(0);

        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.NOTIFICATION_ACTION.ENTITY_ID)) {
            productId = getIntent().getExtras().getString(AppConstants.NOTIFICATION_ACTION.ENTITY_ID);
            notificationID = getIntent().getExtras().getString(AppConstants.NOTIFICATION_ACTION.NOTIFICATION_ID);
        }


    }

    private void addInitialFragment() {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, productId);
        bundle.putString(AppConstants.NOTIFICATION_ACTION.NOTIFICATION_ID, notificationID);
        fragment.setArguments(bundle);
        addFragment(R.id.home_container, fragment, ProductDetailsFragment.class.getSimpleName());
    }
}
