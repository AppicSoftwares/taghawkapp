package com.taghawk.ui.home.search;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.taghawk.R;
import com.taghawk.adapters.ProductListAdapter;
import com.taghawk.adapters.ProductResultAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentHomeBinding;
import com.taghawk.databinding.LayoutSortBinding;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.home.LikeUnLike;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.ui.cart.CartActivity;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.home.category.CategoryActivity;
import com.taghawk.ui.home.filter.FilterActivity;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.util.FilterManager;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.PermissionUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchResultFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    protected String sordedOrder = "-1";
    int limit = 21;
    private Activity mActivity;
    private FragmentHomeBinding mBinding;
    private HomeViewModel mHomeViewModel;
    private ArrayList<ProductListModel> mProductList;
    private ProductResultAdapter adapter;
    private int pageNumber = 1;
    private String search = "";
    private boolean isLoading;
    private int currentPageNumber = 1;
    private boolean isClear;
    private String categoryId = "";
    private ISearchHost mSearchHost;
    private View.OnClickListener onClickListener;
    private int position;
    private HashMap<String, Object> filterParms;
    private boolean isFilter;
    private String sortedBy = "created";
    private Double lat = 0.0, lng = 0.0;
    private PopupWindow popup;
    private GPSTracker gpsTracker;
    private String searchTitle = "";
    private int spanCount = 2;
    private ProductListAdapter productAdapter;
    private boolean isCloset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        if (categoryId.length() > 0 && search.length() == 0) {
            setUpListwithThreeItems();
        } else
            setUpList();
        return mBinding.getRoot();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchFragment.ISearchHost) {
            mSearchHost = (ISearchHost) context;
        } else
            throw new IllegalStateException("Host must implement IHomeHost");
    }


    private void initView() {
        mActivity = getActivity();
        gpsTracker = new GPSTracker(mActivity);
        getDataAgrument();
        setupLocation();
        mBinding.llMenu.setVisibility(View.GONE);
        mBinding.ivView.setVisibility(View.GONE);
        mBinding.cardTag.setVisibility(View.GONE);
        mBinding.includeHeader.ivBack.setVisibility(View.VISIBLE);
        mBinding.includeHeader.tvSearch.setVisibility(View.GONE);
        mBinding.includeHeader.tvTitle.setVisibility(View.VISIBLE);
        mBinding.includeHeader.ivGift.setVisibility(View.GONE);
        mBinding.includeHeader.viewSpace.setVisibility(View.GONE);
        mBinding.includeHeader.tvTitle.setOnClickListener(this);
        mBinding.includeHeader.ivCategory.setVisibility(View.GONE);
        mBinding.includeHeader.ivBack.setOnClickListener(this);
        mBinding.includeHeader.ivCategory.setOnClickListener(this);
        mBinding.tvSort.setOnClickListener(this);
        mBinding.ivFilter.setOnClickListener(this);
        mBinding.ivCart.setOnClickListener(this);
        mBinding.swipe.setOnRefreshListener(this);
        mBinding.includeHeader.tvSearch.setOnClickListener(this);
    }

    private void setupLocation() {
        if (FilterManager.getInstance().getmFilterMap() == null) {
            lat = gpsTracker.getLatitude();
            lng = gpsTracker.getLongitude();
        } else {
            if (FilterManager.getInstance().getmFilterMap().containsKey(AppConstants.KEY_CONSTENT.LONGI)) {
                lat = Double.valueOf(FilterManager.getInstance().getmFilterMap().get(AppConstants.KEY_CONSTENT.LAT).toString());
                lng = Double.valueOf(FilterManager.getInstance().getmFilterMap().get(AppConstants.KEY_CONSTENT.LONGI).toString());
            }
        }
    }

    private void setUpList() {
        mProductList = new ArrayList<>();
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        adapter = new ProductResultAdapter(mProductList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = (int) v.getTag();

                switch (v.getId()) {
                    case R.id.card_main:

                        Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
                        intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, mProductList.get(position).get_id());
                        v.getContext().startActivity(intent);

                        break;
                    case R.id.is_liked:
                        int status;
                        if (mProductList != null) {
                            if (mProductList.get(position).getLiked()) {
                                status = 0;
                                setLikeUnLike(R.drawable.ic_like_unfill, v);
                            } else {
                                status = 1;
                                setLikeUnLike(R.drawable.ic_like_fill, v);

                            }
                            hitLikeUnLike(mProductList.get(position).get_id(), status, 100);
                        }
                        break;
                }
            }
        });
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
                            setupLocation();
                            mHomeViewModel.getProductSearchFilterList(search, filterParms, lat, lng, sortedBy, sordedOrder, currentPageNumber + 1, limit, false, true, categoryId);
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
                    if (mBinding.swipe.isRefreshing()) {
                        mProductList.clear();
                    }
                    isLoading = productListingModel.getNextHit() > 0;
                    if (categoryId.length() > 0 && isClear) {
                        mProductList.clear();
                        isClear = false;
                    }
                    if (isFilter) {
                        mProductList.clear();
                        isFilter = false;
                    }
                    currentPageNumber = productListingModel.getCurrentPage();
                    mProductList.addAll(productListingModel.getmProductList());
                    if (mProductList.size() > 0) {
                        mBinding.tvNoData.setVisibility(View.GONE);
                        mBinding.rvProductListing.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.tvNoData.setVisibility(View.VISIBLE);
                        mBinding.includeHeaderEmpty.tvEmptyMsg.setText(getString(R.string.product_empty_msg));
                        mBinding.includeHeaderEmpty.tvTitle.setText(getString(R.string.no_data_found));
                        mBinding.rvProductListing.setVisibility(View.GONE);
                    }
                    if (categoryId.length() > 0 && search.length() == 0) {
                        productAdapter.notifyDataSetChanged();
                    } else
                        adapter.notifyDataSetChanged();
                    hideKeyboard();
                }

                if (mBinding.swipe != null && mBinding.swipe.isRefreshing())
                    mBinding.swipe.setRefreshing(false);
            }
        });
        mHomeViewModel.getLikeViewModel().observe(this, new Observer<LikeUnLike>() {
            @Override
            public void onChanged(@Nullable LikeUnLike likeUnLike) {
                mProductList.get(position).setLiked(likeUnLike.getLikeUnLikeModel().isLiked());
                adapter.notifyItemChanged(position);
            }
        });

        getLoadingStateObserver().onChanged(true);

        callProductAPi();
    }

    private void callProductAPi() {
        if (filterParms == null) {
            filterParms = new HashMap<>();
            if (DataManager.getInstance().getSortBy() != null) {
                if (DataManager.getInstance().getSortBy().length() == 0) {
                    filterParms.remove(AppConstants.KEY_CONSTENT.SORD_BY);
                    filterParms.remove(AppConstants.KEY_CONSTENT.SORT_ORDER);
                    this.sortedBy = "";
                    this.sordedOrder = "";
                } else {
                    this.sortedBy = DataManager.getInstance().getSortBy();
                    this.sordedOrder = DataManager.getInstance().getSortorder();
                    filterParms.put(AppConstants.KEY_CONSTENT.SORD_BY, DataManager.getInstance().getSortBy());
                    filterParms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, DataManager.getInstance().getSortorder());
                }
                setSortingTitle();
            } else {
                this.sortedBy = "created";
                this.sordedOrder = "-1";
                filterParms.put(AppConstants.KEY_CONSTENT.SORD_BY, sortedBy);
                filterParms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, sordedOrder);

            }


        }
        if (DataManager.getInstance().getFilterLatitude() != null && DataManager.getInstance().getFilterLatitude().length() > 0) {
            mHomeViewModel.getProductSearchFilterList(search, filterParms, Double.valueOf(DataManager.getInstance().getFilterLatitude()), Double.valueOf(DataManager.getInstance().getFilterLongitude()), sortedBy, sordedOrder, pageNumber, limit, false, false, categoryId);
        } else if (gpsTracker != null) {
            mHomeViewModel.getProductSearchFilterList(search, filterParms, gpsTracker.getLatitude(), gpsTracker.getLongitude(), sortedBy, sordedOrder, pageNumber, limit, false, false, categoryId);
        } else
            mHomeViewModel.getProductSearchFilterList(search, filterParms, 0.0, 0.0, sortedBy, sordedOrder, pageNumber, limit, false, false, categoryId);
    }

    private void setSortingTitle() {
        switch (DataManager.getInstance().getSortBy()) {
            case "":
                mBinding.tvSort.setText(getString(R.string.closest));
                break;
            case "created":
                mBinding.tvSort.setText(getString(R.string.newest));
                break;
            case "firmPrice":
                if (DataManager.getInstance().getSortorder().equalsIgnoreCase("-1"))
                    mBinding.tvSort.setText(getString(R.string.price_high_to_low));
                else
                    mBinding.tvSort.setText(getString(R.string.price_low_to_high));
                break;
        }
    }

    private void noData(int gone, int visible) {
        if (mProductList != null && mProductList.size() > 0) {
            mBinding.tvNoData.setVisibility(View.GONE);
            mBinding.rvProductListing.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvNoData.setVisibility(View.VISIBLE);
            mBinding.includeHeaderEmpty.tvTitle.setText(getString(R.string.no_data_found));
            mBinding.includeHeaderEmpty.tvEmptyMsg.setText(getString(R.string.product_empty_msg));
            mBinding.rvProductListing.setVisibility(View.GONE);
        }
    }

    public void getDataAgrument() {
        if (getArguments() != null) {
            search = getArguments().getString(AppConstants.KEY_CONSTENT.SEARCH_KEY);
            categoryId = getArguments().getString("CATEGORY");
            filterParms = (HashMap<String, Object>) getArguments().getSerializable("FILTER_DATA");
            searchTitle = getArguments().getString("SEARCH_TITTLE");
            if (searchTitle != null && searchTitle.length() > 0 && search != null && search.length() > 0) {
                setSpanableTextColor(search, searchTitle);
            } else if (search != null && search.length() > 0) {
                mBinding.includeHeader.tvTitle.setHint(search);
            } else {
                if (searchTitle != null && searchTitle.length() > 0)
                    mBinding.includeHeader.tvTitle.setMaxLines(1);
                    mBinding.includeHeader.tvTitle.setHint(getString(R.string.search_in) + " " + searchTitle);
            }
            if (filterParms != null && filterParms.containsKey(AppConstants.KEY_CONSTENT.SORD_BY)) {
                setSortData();
                sortedBy = filterParms.get(AppConstants.KEY_CONSTENT.SORD_BY).toString();
                sordedOrder = filterParms.get(AppConstants.KEY_CONSTENT.SORT_ORDER).toString();
            }


        }
    }

    private void setSortData() {

        if (filterParms.get(AppConstants.KEY_CONSTENT.SORD_BY).toString().equalsIgnoreCase("created")) {
            mBinding.tvSort.setText(getString(R.string.newest));
        } else if (filterParms.get(AppConstants.KEY_CONSTENT.SORD_BY).toString().equalsIgnoreCase("firmPrice") && filterParms.get(AppConstants.KEY_CONSTENT.SORT_ORDER).equals("1")) {
            mBinding.tvSort.setText(getString(R.string.price_low_to_high));
        } else if (filterParms.get(AppConstants.KEY_CONSTENT.SORD_BY).toString().equalsIgnoreCase("firmPrice") && filterParms.get(AppConstants.KEY_CONSTENT.SORT_ORDER).equals("-1")) {
            mBinding.tvSort.setText(getString(R.string.price_high_to_low
            ));
        } else
            mBinding.tvSort.setText(getString(R.string.closest));


    }

    private void setSpanableTextColor(String search, String searchTitle) {
        Spannable spannable = new SpannableString(search + " in " + searchTitle);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, search.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.editext_gray)), search.length(), search.length() + 4 + searchTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.includeHeader.tvTitle.setText(spannable, TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        if (mBinding.swipe != null && mBinding.swipe.isRefreshing())
            mBinding.swipe.setRefreshing(false);
        noData(View.GONE, View.VISIBLE);
        getLoadingStateObserver().onChanged(false);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_category:
                openCategory();
                break;
            case R.id.ll_tags:
//                mBinding.llTags.setBackgroundResource(mBinding.llTags.getContext().getResources().getDrawable(R.drawable.));
                showToastLong(getString(R.string.under_development));
                break;
            case R.id.iv_cart:
                openCartActivity();
                break;
            case R.id.iv_filter:
                openFilterScreen();
                break;

            case R.id.tv_title:
//                String searchTitle = mBinding.includeHeader.tvTitle.getHint().toString();
//                if (mBinding.includeHeader.tvTitle.getHint().toString().contains("Search in")) {
//                    mSearchHost.openSearchSuggestion(this.searchTitle);
//                } else
                mSearchHost.openSearchSuggestion(searchTitle);
                break;
            case R.id.iv_back:
                backPressedAction();
                break;
            case R.id.tv_sort:
                showsortingPopUp();
                break;
            case R.id.tv_search:
                openSearchScreen();
                break;
            case R.id.tv_closest:
                popup.dismiss();
                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {
                    checkGps();
                }
                break;
            case R.id.tv_newest:
                mBinding.tvSort.setText(getString(R.string.newest));
                popup.dismiss();
                hitSortApi("created", "-1", lat, lng);

                break;
            case R.id.tv_low_to_high:
                mBinding.tvSort.setText(getString(R.string.price_low_to_high));
                popup.dismiss();
                hitSortApi("firmPrice", "1", lat, lng);

                break;
            case R.id.tv_price_high_low:
                mBinding.tvSort.setText(getString(R.string.price_high_to_low));
                popup.dismiss();
                hitSortApi("firmPrice", "-1", lat, lng);
                break;

        }
    }

    private void openCartActivity() {
        Intent intent = new Intent(mActivity, CartActivity.class);
        startActivity(intent);
    }

    private void backPressedAction() {
    /*    Intent intent = new Intent();
        if (filterParms != null) {
            filterParms.remove(AppConstants.KEY_CONSTENT.SEARCH_KEY);
        }
        if (filterParms != null) {
            filterParms.remove(AppConstants.KEY_CONSTENT.CATEGORY_ID);
        }
        intent.putExtra("FILTER_DATA", filterParms);
        FilterManager.getInstance().setmFilterMap(filterParms);
        mActivity.setResult(Activity.RESULT_OK, intent);*/
        mActivity.finish();
    }

    private void openCategory() {
        Intent intent = new Intent(mActivity, CategoryActivity.class);
        startActivityForResult(intent, 100);
    }

    private void openSearchScreen() {
        Intent intent = new Intent(mActivity, SearchAcivity.class);
        intent.putExtra("SEARCH_TITTLE", searchTitle);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {

        mHomeViewModel.getProductSearchFilterList(search, filterParms, lat, lng, sortedBy, sordedOrder, 1, limit, true, false, categoryId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case AppConstants.ACTIVITY_RESULT.FILTER:
                if (resultCode == Activity.RESULT_OK) {
                    isFilter = true;
                    if (data != null && data.getExtras() != null && data.getExtras().containsKey("FILTER_DATA")) {
                        filterParms = (HashMap<String, Object>) data.getExtras().get("FILTER_DATA");
                        if (filterParms != null && filterParms.size() > 0)
                            mHomeViewModel.getProductSearchFilterList(search, filterParms, 0.0, 0.0, sortedBy, sordedOrder, 1, limit, false, true, categoryId);
                    } else {
                        if (filterParms != null)
                            filterParms.clear();
                        mBinding.tvSort.setText(getString(R.string.newest));
                        sortedBy = "created";
                        sordedOrder = "-1";
                        if (gpsTracker != null)
                            mHomeViewModel.getProductSearchFilterList(search, filterParms, gpsTracker.getLatitude(), gpsTracker.getLongitude(), sortedBy, sordedOrder, 1, limit, false, true, categoryId);
                        else
                            mHomeViewModel.getProductSearchFilterList(search, filterParms, 0.0, 0.0, sortedBy, sordedOrder, 1, limit, false, true, categoryId);
                    }
                }
                break;
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    isClear = true;
                    categoryId = data.getExtras().getString("CATEGORY_ID");
                    searchTitle = data.getExtras().getString("TITLE");
                    mSearchHost.setCategoryId(categoryId);
                    mSearchHost.setCategoryName(searchTitle);
                    if (search != null && search.length() > 0) {
                        setSpanableTextColor(search, searchTitle);
                    } else
                        mBinding.includeHeader.tvTitle.setMaxLines(1);
                        mBinding.includeHeader.tvTitle.setHint(getString(R.string.search_in) + " " + searchTitle);
                    if (filterParms != null && filterParms.containsKey(AppConstants.KEY_CONSTENT.SORD_BY) && filterParms.containsKey(AppConstants.KEY_CONSTENT.LAT)) {
                        lat = Double.valueOf(filterParms.get(AppConstants.KEY_CONSTENT.LAT).toString());
                        lng = Double.valueOf(filterParms.get(AppConstants.KEY_CONSTENT.LONGI).toString());
                        sortedBy = filterParms.get(AppConstants.KEY_CONSTENT.SORD_BY).toString();
                        sordedOrder = filterParms.get(AppConstants.KEY_CONSTENT.SORT_ORDER).toString();
                    } else {
                        lat = 0.0;
                        lng = 0.0;
                        sortedBy = "created";
                        sordedOrder = "-1";
                    }
                    mHomeViewModel.getProductSearchFilterList(search, filterParms, lat, lng, sortedBy, sordedOrder, pageNumber, limit, false, true, categoryId);
                }
                break;
        }
    }

    private void setLikeUnLike(int imageResource, View mBinding) {
        ((AppCompatImageView) mBinding).setImageDrawable(mBinding.getContext().getResources().getDrawable(imageResource));
    }

    private void hitLikeUnLike(String productId, int isLike, int requestCode) {
        mHomeViewModel.getLikeUnLike(productId, isLike, requestCode);
    }

    public void hitSortApi(String sordtedBy, String sortOrder, Double lat, Double lng) {
        isFilter = true;
        this.sortedBy = sordtedBy;
        this.sordedOrder = sortOrder;
        this.lat = lat;
        this.lng = lng;
        DataManager.getInstance().saveSortBy(sordtedBy);
        DataManager.getInstance().saveSortOrder(sortOrder);
        if (filterParms == null) {
            filterParms = new HashMap<>();
        }
        if (sordtedBy.equalsIgnoreCase("") || sortOrder.equalsIgnoreCase("")) {
            if (filterParms.containsKey(AppConstants.KEY_CONSTENT.SORD_BY)) {
                filterParms.remove(AppConstants.KEY_CONSTENT.SORD_BY);
                filterParms.remove(AppConstants.KEY_CONSTENT.SORT_ORDER);
            }
            isCloset = true;
            mHomeViewModel.getProductSearchFilterList(search, filterParms, lat, lng, "closest", sordedOrder, 1, limit, true, true, categoryId);
        } else {
            isCloset = false;
            filterParms.put(AppConstants.KEY_CONSTENT.SORD_BY, sortedBy);
            filterParms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, sordedOrder);
            mHomeViewModel.getProductSearchFilterList(search, filterParms, lat, lng, sortedBy, sordedOrder, 1, limit, true, true, categoryId);
        }
    }

    private void showsortingPopUp() {

        LayoutSortBinding popBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_sort, null, false);
        popup = new PopupWindow(mActivity);
        popup.setContentView(popBinding.getRoot());
        popup.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        setupLocation();
        popBinding.tvClosest.setOnClickListener(this);
        popBinding.tvNewest.setOnClickListener(this);
        popBinding.tvPriceHighLow.setOnClickListener(this);
        popBinding.tvLowToHigh.setOnClickListener(this);
        popup.showAsDropDown(mBinding.tvSort);
    }

    private void checkGps() {
        if (gpsTracker == null)
            gpsTracker = new GPSTracker(mActivity);
        if (gpsTracker.isGPSEnable()) {
            if (gpsTracker.getLocation() != null) {
                mBinding.tvSort.setText(getString(R.string.closest));
                hitSortApi("", "", lat, lng);
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void openFilterScreen() {


        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mActivity, FilterActivity.class);
                intent.putExtra("FILTER_DATA", filterParms);
                startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.FILTER);
            }
        });
    }

    private void setUpListwithThreeItems() {
        mProductList = new ArrayList<>();
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        mBinding.rvProductListing.setLayoutManager(layoutManager);
        productAdapter = new ProductListAdapter(mProductList);
        mBinding.rvProductListing.setAdapter(productAdapter);
        mBinding.rvProductListing.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int limit = 21;

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
                            setupLocation();
                            mHomeViewModel.getProductSearchFilterList(search, filterParms, lat, lng, sortedBy, sordedOrder, currentPageNumber + 1, limit, false, false, categoryId);
                        }
                    }
                }
            }
        });
    }

    /**
     * This interface is used to interact with the host {@link HomeActivity}
     */
    public interface ISearchHost {
        void backPressed();

        void setCategoryId(String id);

        void setCategoryName(String name);

        void openSearchSuggestion(String title);
    }

}



