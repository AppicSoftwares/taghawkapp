package com.taghawk.ui.gift;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.taghawk.R;
import com.taghawk.adapters.PromoteMultipleProductFromRewardsAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentRewardsRedeemBinding;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.gift.GiftRewardsPromotionData;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.profileresponse.ProfileProductsResponse;
import com.taghawk.util.AppUtils;
import com.taghawk.util.PaginationGridScrollListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RedeemRewardsFragment extends BaseFragment implements View.OnClickListener, PaginationGridScrollListener.PaginationListenerCallbacks, SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<ProductDetailsData> mProductList;
    private GiftRewardsPromotionViewModel mGiftPromotionViewModel;
    private Activity mActivity;
    private PromoteMultipleProductFromRewardsAdapter adapter;
    private FragmentRewardsRedeemBinding mBinding;
    private GiftRewardsPromotionData mData;
    private int rewards, position;
    private HashMap<Integer, ProductDetailsData> mSelectedList;
    private PaginationGridScrollListener mPageScrollListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentRewardsRedeemBinding.inflate(inflater);
        initView();

        return mBinding.getRoot();
    }

    private void initView() {
        mActivity = getActivity();
        mSelectedList = new HashMap();
        mBinding.includeHeader.tvTitle.setVisibility(View.VISIBLE);
        mBinding.includeHeader.tvTitle.setText(getString(R.string.coupon_redeem));
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.swipe.setOnRefreshListener(this);
        mBinding.tvRedeem.setOnClickListener(this);
        getArgumentsData();
        setUpView();
        setUpListeners();

    }

    private void getArgumentsData() {
        if (getArguments() != null) {
            mData = getArguments().getParcelable(AppConstants.BUNDLE_DATA);
            rewards = getArguments().getInt("REWARDS");
            setData();
        }
    }

    private void setData() {
        mBinding.tvRewardsPoints.setText(rewards + " " + getString(R.string.points));
        mBinding.includePromotion.tvPromotionDays.setText(mData.getDays() + " " + getString(R.string.days_promotion));
        mBinding.includePromotion.rewardsPoints.setText(mData.getRewardPoints() + " " + getString(R.string.points));
    }

    //getProducts of user
    private void callApi(boolean isRefresing) {
        if (AppUtils.isInternetAvailable(mActivity)) {
            if (!isRefresing)
                getLoadingStateObserver().onChanged(true);
            mGiftPromotionViewModel.getProfileProductsList(true, mProductList.size(), DataManager.getInstance().getUserDetails().getUserId());
        } else {
            showNoNetworkError();
        }
    }


    private void setUpView() {
        mProductList = new ArrayList<>();
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        adapter = new PromoteMultipleProductFromRewardsAdapter(mProductList, this);
        mBinding.rvProducts.setLayoutManager(layoutManager);
        mBinding.rvProducts.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewModel();
    }

    private void setUpViewModel() {
        mGiftPromotionViewModel = ViewModelProviders.of(this).get(GiftRewardsPromotionViewModel.class);
        mGiftPromotionViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mGiftPromotionViewModel.productsLiveData().observe(this, new Observer<ProfileProductsResponse>() {
            @Override
            public void onChanged(@Nullable ProfileProductsResponse profileProductsResponse) {
                getLoadingStateObserver().onChanged(false);
                if (mBinding.swipe != null)
                    mBinding.swipe.setRefreshing(false);
                if (profileProductsResponse != null) {

                    if (profileProductsResponse.getPage() == 1) {
                        mProductList.clear();
                    } else if (!profileProductsResponse.isLoading()) {
                        mProductList.remove(mProductList.size() - 1);
                        adapter.notifyItemRemoved(mProductList.size() - 1);
                        mPageScrollListener.setFetchingStatus(false);
                    }
                    mProductList.addAll(profileProductsResponse.getData());
                    adapter.notifyDataSetChanged();
                    if (mProductList.size() > 0) {
                        noData(View.GONE, View.VISIBLE);
                    } else {
                        noData(View.VISIBLE, View.GONE);
                    }

                } else {
                    noData(View.VISIBLE, View.GONE);
                }
            }
        });
        mGiftPromotionViewModel.redeemLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                showToastShort(commonResponse.getMessage());
                ((GiftRewardPromotionActivity) mActivity).updateRewardsPoints(rewards - mData.getRewardPoints() * mSelectedList.size());
            }
        });
        callApi(false);
    }

    private void noData(int gone, int visible) {
        mBinding.rvProducts.setVisibility(visible);
        mBinding.includeEmpty.tvEmptyMsg.setText(getString(R.string.product_empty_msg_for_promotion));
        mBinding.tvNoData.setVisibility(gone);
    }

    /**
     * Method to set Up Listeners
     */
    private void setUpListeners() {

        mPageScrollListener = new PaginationGridScrollListener((GridLayoutManager) mBinding.rvProducts.getLayoutManager(), this);
        mPageScrollListener.setFetchingStatus(false);
        mBinding.rvProducts.addOnScrollListener(mPageScrollListener);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                ((GiftRewardPromotionActivity) mActivity).onBackPressed();
                break;
            case R.id.ll_main:
            case R.id.rb_selection:
                position = (int) v.getTag();
                if (mSelectedList != null && mProductList != null) {
                    if (mProductList.get(position).isSelected()) {
                        mProductList.get(position).setSelected(false);
                        if (mSelectedList.size() > 0) {
                            mSelectedList.remove(position);
                            adapter.notifyItemChanged(position);
                        }
                    } else {
                        mProductList.get(position).setSelected(true);
                        mSelectedList.put(position, mProductList.get(position));
                        adapter.notifyItemChanged(position);
                    }
                }
                break;
            case R.id.tv_redeem:
                if (AppUtils.isInternetAvailable(mActivity)) {
                    if (mSelectedList != null & mSelectedList.size() > 0)
                        mGiftPromotionViewModel.promoteMultiple(mSelectedList, mData.getDays(), mData.getRewardPoints(), rewards);
                    else {
                        showToastShort("Please select products for promote");
                    }
                } else {
                    showNoNetworkError();
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        callApi(true);
    }

    @Override
    public void loadMoreItems() {
        if (AppUtils.isInternetAvailable(mActivity)) {
            mGiftPromotionViewModel.getProfileProductsList(false, mProductList.size(), DataManager.getInstance().getUserDetails().getUserId());
        } else {
            showNoNetworkError();
        }
    }
}
