package com.taghawk.ui.create;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.model.AddProduct.AddProductModel;
import com.taghawk.model.tag.TagData;
import com.taghawk.ui.home.HomeActivity;

import java.util.ArrayList;

public class FeturedPostActivity extends BaseActivity {
    private AddProductModel addProductData;
    private ArrayList<TagData> mSharedTagsList;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gettIntentData();
        addInitialFragment();
    }

    private void gettIntentData() {
        if (getIntent() != null) {
            addProductData = (AddProductModel) getIntent().getExtras().get("DATA");
            mSharedTagsList = getIntent().getParcelableArrayListExtra("SHARED_TAG_DATA");

        }
    }

    private void addInitialFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("DATA", addProductData);
        bundle.putParcelableArrayList("SHARED_TAG_DATA", mSharedTagsList);
        FeaturePostFrgament frgament = new FeaturePostFrgament();
        frgament.setArguments(bundle);
        addFragment(R.id.home_container, frgament, FeaturePostFrgament.class.getSimpleName());

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("IS_FROM_FEATURE_POST", true);
        startActivity(intent);
        finish();
    }
}
