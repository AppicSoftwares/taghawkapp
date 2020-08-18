package com.taghawk.ui.home.product_details;

import android.app.Activity;
import android.content.Intent;
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
import com.taghawk.adapters.MyProductListAdapter;
import com.taghawk.adapters.ProductListAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentViewAllProductBinding;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.model.profileresponse.ProfileProductsResponse;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.tag.TagViewModel;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;

public class MyProductsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentViewAllProductBinding mBinding;

    private ArrayList<ProductDetailsData> mProductList;

    private Activity mActivity;
    private MyProductListAdapter adapter;
    private int currentPageNumber = 1;
    private int limit = 20;
    private boolean isLoading;
    private HomeViewModel mHomeViewModel;
    private int type = AppConstants.ACTIVITY_RESULT.SHARE_PRODUCT;
    private int previousSelectedPosition = -1;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentViewAllProductBinding.inflate(inflater, container, false);
        initView();
        setUpList();
        return mBinding.getRoot();
    }

    private void initView() {
        mActivity = getActivity();
        mBinding.swipe.setOnRefreshListener(this);
        mBinding.includeHeader.tvTitle.setText(getString(R.string.share_product));
        mBinding.includeHeader.tvReset.setText(getString(R.string.done_lowercase));
        mBinding.includeHeader.tvReset.setVisibility(View.VISIBLE);
        mBinding.includeHeader.ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        mBinding.includeHeader.tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (previousSelectedPosition != -1) {
                    Intent intent = new Intent();
                    intent.putExtra(AppConstants.BUNDLE_DATA, mProductList.get(previousSelectedPosition));
                    mActivity.setResult(Activity.RESULT_OK, intent);
                    mActivity.finish();
                } else
                    showToastShort(getString(R.string.select_any_product_to_share));
            }
        });
    }

    private void setUpList() {
        mProductList = new ArrayList<>();
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        adapter = new MyProductListAdapter(mProductList, new RecyclerViewCallback() {
            @Override
            public void onClick(int position, View view) {
                if (previousSelectedPosition != position) {
                    if (previousSelectedPosition != -1) {
                        mProductList.get(previousSelectedPosition).setViewType(ProductDetailsData.VIEW_TYPE_PRODUCT);
                        adapter.notifyItemChanged(previousSelectedPosition);
                    }
                    previousSelectedPosition = position;
                    mProductList.get(previousSelectedPosition).setViewType(ProductDetailsData.VIEW_TYPE_PRODUCT_SELECT);
                    adapter.notifyItemChanged(previousSelectedPosition);
                }
            }
        });
        mBinding.rvProductListing.setLayoutManager(layoutManager);
        mBinding.rvProductListing.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing view model
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mHomeViewModel.profileProductsLiveData().observe(this, new Observer<ProfileProductsResponse>() {
            @Override
            public void onChanged(@Nullable ProfileProductsResponse profileProductsResponse) {
                getLoadingStateObserver().onChanged(false);
                if (profileProductsResponse != null) {
                    mBinding.swipe.setRefreshing(false);
                    mProductList.clear();
                    mProductList.addAll(profileProductsResponse.getData());
                    adapter.notifyDataSetChanged();
                    if (mProductList.size() > 0) {
                        noData(View.VISIBLE, View.GONE, getString(R.string.oops_it_s_empty), getString(R.string.no_results));
                    } else {
                        noData(View.GONE, View.VISIBLE, getString(R.string.oops_it_s_empty), getString(R.string.no_results));
                    }

                } else {
                    noData(View.GONE, View.VISIBLE, getString(R.string.oops_it_s_empty), getString(R.string.no_results));
                }
            }
        });
        mHomeViewModel.getProfileProductsList(1, 200);
    }

    private void noData(int visible, int gone, String errorTitle, String errorMsg) {
        mBinding.rvProductListing.setVisibility(visible);
        mBinding.includeEmpty.tvTitle.setText(errorTitle);
        mBinding.includeEmpty.tvEmptyMsg.setText(errorMsg);
        mBinding.tvNoData.setVisibility(gone);
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        if (mBinding.swipe != null && mBinding.swipe.isRefreshing())
            mBinding.swipe.setRefreshing(false);
        getLoadingStateObserver().onChanged(false);
        if (mProductList != null && mProductList.size() > 0) {
            noData(View.VISIBLE, View.GONE, "", "");
        } else {
            noData(View.GONE, View.VISIBLE, getString(R.string.oops), getString(R.string.please_again_later));
        }
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        super.onErrorOccurred(throwable);
        if (mBinding.swipe != null && mBinding.swipe.isRefreshing())
            mBinding.swipe.setRefreshing(false);
        getLoadingStateObserver().onChanged(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onRefresh() {
        if (AppUtils.isConnection(mActivity))
            mHomeViewModel.getProfileProductsList(1, 200);
        else
            mBinding.swipe.setRefreshing(false);
    }

}
