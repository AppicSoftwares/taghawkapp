package com.taghawk.ui.create;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.model.home.ImageList;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductPreviewActivity extends BaseActivity {
    private HashMap<String, Object> hashMapData;
    private ArrayList<ImageList> imageList;

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

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("ADD_PRODUCT_DATA")) {
            hashMapData = (HashMap<String, Object>) getIntent().getExtras().getSerializable("ADD_PRODUCT_DATA");
            imageList = getIntent().getExtras().getParcelableArrayList("IMAGES");
        }
    }

    private void addInitialFragment() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);
        ProductPreviewFragment fragment = new ProductPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ADD_PRODUCT_DATA", hashMapData);
        bundle.putParcelableArrayList("IMAGES", imageList);
        fragment.setArguments(bundle);
        addFragment(R.id.home_container, fragment, ProductPreviewFragment.class.getSimpleName());
    }
}
