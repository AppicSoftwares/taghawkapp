package com.taghawk.ui.review_rating;

import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.util.AppUtils;

public class ReviewRatingActivity extends BaseActivity {

    private String sellerUserId, sellerUserName, sellerJoinFrom, sellerImage;
    private Double sellerRating;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        addInitialFrgament();
    }

    private void addInitialFrgament() {
        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);

        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY_CONSTENT.SELLER_ID, sellerUserId);
        bundle.putString(AppConstants.KEY_CONSTENT.FULL_NAME, sellerUserName);
        bundle.putString(AppConstants.KEY_CONSTENT.JOIN_FROM, sellerJoinFrom);
        bundle.putDouble(AppConstants.KEY_CONSTENT.SELLER_RATING, sellerRating);
        bundle.putString(AppConstants.KEY_CONSTENT.IMAGES, sellerImage);
        ReviewRatingFragment fragment = new ReviewRatingFragment();
        fragment.setArguments(bundle);
        replaceFragment(R.id.home_container, fragment, ReviewRatingFragment.class.getSimpleName());

    }

    private void getIntentData() {

        sellerRating = getIntent().getExtras().getDouble(AppConstants.KEY_CONSTENT.SELLER_RATING);
        sellerUserId = getIntent().getExtras().getString(AppConstants.KEY_CONSTENT.SELLER_ID);
        sellerUserName = getIntent().getExtras().getString(AppConstants.KEY_CONSTENT.FULL_NAME);
        sellerJoinFrom = getIntent().getExtras().getString(AppConstants.KEY_CONSTENT.JOIN_FROM);
        sellerImage = getIntent().getExtras().getString(AppConstants.KEY_CONSTENT.IMAGES);

    }

}
