package com.taghawk.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.taghawk.R;
import com.taghawk.adapters.ProductListWithMoreAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentSoldProductsBinding;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.profileresponse.ProfileProductsResponse;
import com.taghawk.util.PaginationGridScrollListener;

import java.util.ArrayList;

public class SoldProductsFragment extends BaseFragment implements View.OnClickListener, PaginationGridScrollListener.PaginationListenerCallbacks, SwipeRefreshLayout.OnRefreshListener {

    private SoldProductsViewModel mSoldProductsViewModel;
    private FragmentSoldProductsBinding mBinding;
    private AppCompatActivity mActivity;
    private ArrayList<ProductDetailsData> mProductList;
    private ProductListWithMoreAdapter adapter;
    private PaginationGridScrollListener mPageScrollListener;
    private String sellerId;

    /**
     * This method is used to return the instance of this fragment
     *
     * @return new instance of {@link ProfileFragment}
     */
    public static SoldProductsFragment getInstance() {
        return new SoldProductsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgumentData();
        setUpViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentSoldProductsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    private void getArgumentData() {

        if (getArguments() != null) {
            sellerId = getArguments().getString(AppConstants.BUNDLE_DATA);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setUpListeners();
        setUpViews();
    }

    private void setUpViews() {
        mSoldProductsViewModel.getProfileProductsList(true, mProductList.size(), sellerId);
    }

    /**
     * Method to set Up View Model
     */
    private void setUpViewModel() {
        mSoldProductsViewModel = ViewModelProviders.of(this).get(SoldProductsViewModel.class);
        mSoldProductsViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mSoldProductsViewModel.profileProductsLiveData().observe(this, new Observer<ProfileProductsResponse>() {
            @Override
            public void onChanged(@Nullable ProfileProductsResponse profileProductsResponse) {
                if (profileProductsResponse != null) {
                    mBinding.swipe.setRefreshing(false);
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
                        noData(View.VISIBLE, View.GONE);
                    } else {
                        noData(View.GONE, View.VISIBLE);
                    }

                } else {
                    noData(View.GONE, View.VISIBLE);
                }
            }
        });
    }

    private void noData(int recycler, int label) {
        mBinding.rvProductListing.setVisibility(recycler);
        mBinding.tvNoData.setVisibility(label);
        mBinding.includeEmpty.tvEmptyMsg.setText(getString(R.string.product_empty_msg_sold));
    }

    // init views and listener
    private void initView() {
        mActivity = (AppCompatActivity) getActivity();
        mProductList = new ArrayList<>();
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case AppConstants.LIST_VIEW_TYPE:
                        return 1;
                    case AppConstants.LOADER_VIEW_TYPE:
                        return 3;
                    default:
                        return -1;
                }

            }
        });
        mBinding.rvProductListing.setLayoutManager(manager);
        adapter = new ProductListWithMoreAdapter(mProductList, this, false, "");
        mBinding.rvProductListing.setAdapter(adapter);
        mBinding.swipe.setOnRefreshListener(this);
    }

    /**
     * Method to set Up Listeners
     */
    private void setUpListeners() {

        mPageScrollListener = new PaginationGridScrollListener((GridLayoutManager) mBinding.rvProductListing.getLayoutManager(), this);
        mPageScrollListener.setFetchingStatus(false);
        mBinding.rvProductListing.addOnScrollListener(mPageScrollListener);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.iv_settings:
//
//                break;
        }
    }


    @Override
    public void loadMoreItems() {
        mSoldProductsViewModel.getProfileProductsList(false, mProductList.size(), sellerId);
    }

    @Override
    public void onRefresh() {
        setUpViews();
    }
}
