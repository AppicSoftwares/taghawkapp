package com.taghawk.ui.tag;

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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.taghawk.R;
import com.taghawk.adapters.ProductListAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentViewAllProductBinding;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.ui.home.HomeViewModel;

import java.util.ArrayList;

public class ProductViewAllFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentViewAllProductBinding mBinding;

    private ArrayList<ProductListModel> mProductList;

    private Activity mActivity;
    private ProductListAdapter adapter;
    private int currentPageNumber = 1;
    private int limit = 20;
    private boolean isLoading;
    private TagViewModel mTagDetailsViewModel;
    private HomeViewModel mHomeViewModel;
    private String communityId;


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
        getIntentData();
        mBinding.includeHeader.tvTitle.setText(getString(R.string.tag_shelf));
        mBinding.includeHeader.ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
    }

    private void getIntentData() {
        communityId = getArguments().getString(AppConstants.BUNDLE_DATA);
    }

    private void setUpList() {
        mProductList = new ArrayList<>();
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        adapter = new ProductListAdapter(mProductList);
        mBinding.rvProductListing.setLayoutManager(layoutManager);
        mBinding.rvProductListing.setAdapter(adapter);
        mBinding.rvProductListing.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItems = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (isLoading) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItems
                                && firstVisibleItemPosition >= 0) {
                            isLoading = false;
                            mHomeViewModel.getTagProducts(communityId, currentPageNumber++, limit++, true);

                        }
                    }
                }

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing view model
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mHomeViewModel.getProductListing().observe(this, new Observer<ProductListingModel>() {
            @Override
            public void onChanged(@Nullable ProductListingModel productListingModel) {
                getLoadingStateObserver().onChanged(false);
                if (productListingModel != null && productListingModel.getCode() == 200) {
                    if (mBinding.swipe.isRefreshing()) {
                        mProductList.clear();
                    }
                    if (productListingModel.getNextHit() > 0) {
                        isLoading = true;
                    } else {
                        isLoading = false;
                    }
                    currentPageNumber = productListingModel.getCurrentPage();
                    mProductList.addAll(productListingModel.getmProductList());
                    adapter.notifyDataSetChanged();
                    if (mProductList.size() > 0) {
                        noData(View.VISIBLE, View.GONE, "", "");
                    } else {
                        noData(View.GONE, View.VISIBLE, getString(R.string.no_data_found), getString(R.string.no_data_found_));
                    }

                }
                if (mBinding.swipe != null && mBinding.swipe.isRefreshing())
                    mBinding.swipe.setRefreshing(false);
            }
        });
        mHomeViewModel.getTagProducts(communityId, currentPageNumber, limit, false);
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
        String search = "";
        currentPageNumber = 1;
        limit = 20;
        mHomeViewModel.getTagProducts(communityId, currentPageNumber, limit, true);
    }

}
