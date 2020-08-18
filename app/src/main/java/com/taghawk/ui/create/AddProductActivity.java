package com.taghawk.ui.create;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class AddProductActivity extends BaseActivity {

    private HashMap<String, Object> hashMapData;
    private CreateProductFragment fragment;
    private ArrayList<String> mImageList;

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

    //get data from intent
    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mImageList = new ArrayList<>();
            mImageList = (ArrayList<String>) getIntent().getExtras().getSerializable(AppConstants.BUNDLE_DATA);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null) {
            mImageList = new ArrayList<>();
            mImageList = (ArrayList<String>) intent.getExtras().getSerializable(AppConstants.BUNDLE_DATA);
        }
        popFragment();
        addInitialFragment();
    }

    // Add addProduct Fragment
    private void addInitialFragment() {

        fragment = new CreateProductFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.BUNDLE_DATA, mImageList);
        fragment.setArguments(bundle);
        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);
        addFragmentWithBackstack(R.id.home_container, fragment, CreateProductFragment.class.getSimpleName());
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
            case AppConstants.ACTIVITY_RESULT.CAMERA_PERMISSION:
                if (fragment != null) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
