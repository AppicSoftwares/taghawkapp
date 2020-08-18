package com.taghawk.ui.home.filter;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

import java.util.HashMap;

public class FilterActivity extends BaseActivity {

    FilterFragment fragment;
    private HashMap<String, Object> mParms;
    private Address loaction;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        addFilterFragment();
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("FILTER_DATA")) {
            mParms = (HashMap<String, Object>) getIntent().getExtras().get("FILTER_DATA");
            loaction = (Address) getIntent().getExtras().get("LOCATION");
        }
    }

    private void addFilterFragment() {
        fragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("FILTER_DATA", mParms);
        bundle.putParcelable("LOCATION", loaction);
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, FilterFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.ACTIVITY_RESULT.GPS_ENABLE)
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION:
                if (fragment != null) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
        }
    }
}
