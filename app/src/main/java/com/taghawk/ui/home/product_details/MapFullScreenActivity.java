package com.taghawk.ui.home.product_details;

import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class MapFullScreenActivity extends BaseActivity {
    private Double latitude, longitude;
    private MapFullScreenFragment mapFullScreenFragment;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        setBundleData();
        addFragment(R.id.home_container, mapFullScreenFragment, MapFullScreenFragment.class.getSimpleName());
    }

    private void setBundleData() {
        Bundle bundle = new Bundle();
        bundle.putDouble(AppConstants.KEY_CONSTENT.LAT, latitude);
        bundle.putDouble(AppConstants.KEY_CONSTENT.LONGI, longitude);
        mapFullScreenFragment = new MapFullScreenFragment();
        mapFullScreenFragment.setArguments(bundle);
    }

    private void getIntentData() {

        if (getIntent() != null) {
            latitude = getIntent().getExtras().getDouble(AppConstants.KEY_CONSTENT.LAT);
            longitude = getIntent().getExtras().getDouble(AppConstants.KEY_CONSTENT.LONGI);
        }

    }
}
