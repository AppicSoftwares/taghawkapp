package com.taghawk.ui.profile;


import android.content.Intent;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.taghawk.R;
import com.taghawk.adapters.ProductListWithMoreAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.databinding.FragmentSellingProductsBinding;
import com.taghawk.databinding.LayoutProductMenuBinding;
import com.taghawk.model.AddProduct.AddProductData;
import com.taghawk.model.AddProduct.AddProductModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.profileresponse.ProfileProductsResponse;
import com.taghawk.ui.create.FeturedPostActivity;
import com.taghawk.ui.home.EditProductActivity;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.PaginationGridScrollListener;

import java.util.ArrayList;

public class SellingProductsFragment extends BaseFragment implements View.OnClickListener, PaginationGridScrollListener.PaginationListenerCallbacks, SwipeRefreshLayout.OnRefreshListener {

    private SellingProductsViewModel sellingProductsViewModel;
    private FragmentSellingProductsBinding mBinding;
    private AppCompatActivity mActivity;
    private ArrayList<ProductDetailsData> mProductList;
    private ProductListWithMoreAdapter adapter;
    private PaginationGridScrollListener mPageScrollListener;
    private int position;
    private PopupWindow menuPop;
    private String sellerId = "";

    /**
     * This method is used to return the instance of this fragment
     *
     * @return new instance of {@link ProfileFragment}
     */
    public static SellingProductsFragment getInstance() {
        return new SellingProductsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgumentData();
        setUpViewModel();
    }

    private void getArgumentData() {

        if (getArguments() != null) {
            sellerId = getArguments().getString(AppConstants.BUNDLE_DATA);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentSellingProductsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setUpListeners();
    }

    private void setUpViews() {
//        getLoadingStateObserver().onChanged(true);
        sellingProductsViewModel.getProfileProductsList(true, mProductList.size(), sellerId);
    }

    /**
     * Method to set Up View Model
     */
    private void setUpViewModel() {
        mProductList = new ArrayList<>();
        sellingProductsViewModel = ViewModelProviders.of(this).get(SellingProductsViewModel.class);
        sellingProductsViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        sellingProductsViewModel.profileProductsLiveData().observe(this, new Observer<ProfileProductsResponse>() {
            @Override
            public void onChanged(@Nullable ProfileProductsResponse profileProductsResponse) {
                getLoadingStateObserver().onChanged(false);
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
        sellingProductsViewModel.getDeleteLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse.getCode() == 200) {

                    try {
                        mProductList.remove(position);
                        adapter.notifyDataSetChanged();
                        if (mProductList.size() == 0) {
                            noData(View.GONE, View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        setUpViews();
    }

    private void noData(int visible, int gone) {
        mBinding.rvProductListing.setVisibility(visible);
        mBinding.includeEmpty.tvEmptyMsg.setText(getString(R.string.product_empty_msg_));
        mBinding.tvNoData.setVisibility(gone);
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
        adapter = new ProductListWithMoreAdapter(mProductList, this, true, sellerId);
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
            case R.id.card:
                position = (int) v.getTag();
                openProductDetailScreen(position);
                break;
            case R.id.iv_more:
                position = (int) v.getTag();
                showMenu(v);
                break;
            case R.id.tv_promote:

                break;
            case R.id.iv_edit:
                menuPop.dismiss();
                if (mProductList != null) {
                    openEditActivity(mProductList.get(position));
                }
                break;
            case R.id.tv_delete:
                menuPop.dismiss();
                if (mProductList != null && mProductList.get(position).isMyProduct()) {
                    DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.delete_product), getString(R.string.product_delete_msg), getString(R.string.delete), getString(R.string.cencel), new DialogCallback() {
                        @Override
                        public void submit(String data) {
                            sellingProductsViewModel.deleteProduct(mProductList.get(position).getProductId());
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
                break;

        }
    }

    private void openEditActivity(ProductDetailsData productDetailsData) {

        Intent intent = new Intent(mActivity, EditProductActivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, productDetailsData);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.EDIT_PRODUCT);
    }

    private void openProductDetailScreen(int position) {
        Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
        intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, mProductList.get(position).getProductId());
        mActivity.startActivity(intent);
    }

    @Override
    public void loadMoreItems() {
//        getLoadingStateObserver().onChanged(true);
        sellingProductsViewModel.getProfileProductsList(false, mProductList.size(), sellerId);
    }

    private void openFeatureActivity(ProductDetailsData productDetailsModelData) {
        Intent intent = new Intent(mActivity, FeturedPostActivity.class);
        AddProductModel model = new AddProductModel();
        AddProductData data = new AddProductData();
        data.setId(productDetailsModelData.getProductId());
        data.setImages(productDetailsModelData.getImageList());
        data.setFirmPrice(Double.valueOf(productDetailsModelData.getFirmPrice()));
        model.setmAddProductData(data);
        intent.putExtra("DATA", model);
        intent.putExtra("SHARED_TAG_DATA", productDetailsModelData.getmSharedTagList());
        startActivity(intent);
    }

    private void showMenu(View view) {
        LayoutProductMenuBinding popBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_product_menu, null, false);
        menuPop = new PopupWindow(mActivity);
        menuPop.setContentView(popBinding.getRoot());
        menuPop.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        menuPop.setFocusable(true);
        menuPop.setOutsideTouchable(true);
        popBinding.tvPromote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuPop.dismiss();
                if (!mProductList.get(position).isPromoted())
                    openFeatureActivity(mProductList.get(position));
                else {
                    showToastShort(getString(R.string.already_promoted));
                }
            }
        });
        popBinding.tvDelete.setOnClickListener(this);
        popBinding.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuPop.dismiss();
                openEditActivity(mProductList.get(position));
            }
        });
        menuPop.showAsDropDown(view, -300, -35);
    }

    @Override
    public void onRefresh() {
        setUpViews();
    }
}
