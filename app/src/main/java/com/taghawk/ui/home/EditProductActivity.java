package com.taghawk.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.util.AppUtils;

public class EditProductActivity extends BaseActivity {


    private EditProductFragment fragment;
    private ProductDetailsData productData;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        addEditFragment();
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.BUNDLE_DATA)) {
            productData = (ProductDetailsData) getIntent().getExtras().get(AppConstants.BUNDLE_DATA);
        }
    }

    private void addEditFragment() {
        fragment = new EditProductFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.BUNDLE_DATA, productData);
        fragment.setArguments(bundle);
        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);
        addFragmentWithBackstack(R.id.home_container, fragment, EditProductFragment.class.getSimpleName());
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
