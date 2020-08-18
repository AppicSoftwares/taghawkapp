package com.taghawk.ui.gift;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.ActivityCategoryListBinding;
import com.taghawk.model.gift.GiftRewardsPromotionData;

// gift Activity
public class GiftRewardPromotionActivity extends BaseActivity {

    private ActivityCategoryListBinding mBinding;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }


    private void addRewardsFragment() {
        addFragmentWithBackstack(R.id.home_container, new GiftRewardsPromotionFragment(), GiftRewardsPromotionFragment.class.getSimpleName());
    }

    //Rewards redeem fragment
    public void addRewardsRedeemFragment(GiftRewardsPromotionData data, int rewards) {
        RedeemRewardsFragment fragment = new RedeemRewardsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.BUNDLE_DATA, data);
        bundle.putInt("REWARDS", rewards);
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, RedeemRewardsFragment.class.getSimpleName());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //inital fragment
        addRewardsFragment();
    }

    @Override
    public void onBackPressed() {

        if (getCurrentFragment() != null && getCurrentFragment() instanceof GiftRewardsPromotionFragment) {
            finish();
        } else {
            popFragment();
        }
    }

    public void updateRewardsPoints(int rewardsPoints) {
        popFragment();
        Fragment fragment = getCurrentFragment();
        if (fragment != null && fragment instanceof GiftRewardsPromotionFragment) {
            ((GiftRewardsPromotionFragment) fragment).updateRewardsPoints(rewardsPoints);
        }

    }
}
