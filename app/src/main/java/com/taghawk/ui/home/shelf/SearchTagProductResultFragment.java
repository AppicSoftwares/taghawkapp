package com.taghawk.ui.home.shelf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.taghawk.R;
import com.taghawk.adapters.ProductListAdapter;
import com.taghawk.adapters.ProductListHorizontalAdapter;
import com.taghawk.adapters.ProductResultAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.AdapterCategoryCatHomeBinding;
import com.taghawk.databinding.FragmentHomeBinding;
import com.taghawk.databinding.LayoutSortBinding;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.category.CategoryListResponse;
import com.taghawk.model.category.CategoryResponse;
import com.taghawk.model.home.LikeUnLike;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.ui.cart.CartActivity;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.ui.home.HomeFragment;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.home.category.CategoryActivity;
import com.taghawk.ui.home.category.CategoryListViewModel;
import com.taghawk.ui.home.filter.FilterActivity;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.ui.home.search.SearchTagShelfAcivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.FilterManager;
import com.taghawk.util.GPSTracker;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchTagProductResultFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    protected String sordedOrder = "-1";
    int limit = 20;
    private Activity mActivity;
    private FragmentHomeBinding mBinding;
    private HomeViewModel mHomeViewModel;
    private ArrayList<ProductListModel> mProductList = new ArrayList<>();
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
    private ProductListHorizontalAdapter adapter1;
    private String tagName, tagId;
    private boolean isGrid = true;

    //AKM
    private HomeCategoryListAdapter categoryListAdapter;
    private ArrayList<CategoryListResponse> mCategoryList;
    private CategoryListViewModel categoryLisViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        if ((categoryId != null && categoryId.length() > 0 && search != null && search.length() == 0) || (searchTitle.length() > 0 && search != null && search.length() == 0)) {
            setUpListwithThreeItems();
            //AKM
            mBinding.llMenu.setVisibility(View.VISIBLE);
            //mBinding.ivView.setVisibility(View.GONE);
            //AKM: add Category List
            getCatList();
        } else {
            mBinding.llMenu.setVisibility(View.GONE);
            setUpList();
        }
        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ISearchHost) {
            mSearchHost = (ISearchHost) context;
        } else
            throw new IllegalStateException("Host must implement IHomeHost");
    }

    private void initView() {
        mActivity = getActivity();
        gpsTracker = new GPSTracker(mActivity);
        getDataAgrument();
        setupLocation();

        mBinding.cardTag.setVisibility(View.GONE);
        mBinding.includeHeader.ivBack.setVisibility(View.VISIBLE);
        mBinding.includeHeader.tvSearch.setVisibility(View.GONE);
        mBinding.includeHeader.tvSearch.setMaxLines(1);
        mBinding.includeHeader.tvTitle.setVisibility(View.VISIBLE);
        mBinding.includeHeader.tvTitle.setMaxLines(1);
        mBinding.includeHeader.ivGift.setVisibility(View.GONE);
        mBinding.includeHeader.viewSpace.setVisibility(View.GONE);
        mBinding.includeHeader.tvTitle.setOnClickListener(this);
        mBinding.includeHeader.ivCategory.setVisibility(View.GONE);
        mBinding.includeHeader.ivBack.setOnClickListener(this);
        mBinding.includeHeader.ivCategory.setOnClickListener(this);
        mBinding.tvSort.setOnClickListener(this);
        mBinding.ivFilter.setOnClickListener(this);
        mBinding.swipe.setOnRefreshListener(this);
        mBinding.includeHeader.tvSearch.setOnClickListener(this);
        mBinding.ivCart.setOnClickListener(this);
        mBinding.ivView.setOnClickListener(this);
        mBinding.cardcatall.setOnClickListener(this);
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
            private int limit = 10;

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
                            mHomeViewModel.getTagProductListPagination(mProductList, search, filterParms, lat, lng, sortedBy, sordedOrder, currentPageNumber + 1, limit, false, true, "", tagId);
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
                    if (categoryId != null && categoryId.length() > 0 && isClear) {
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
                    if (!isGrid) {
                        if(adapter1 != null)
                            adapter1.notifyDataSetChanged();
                        else
                            setUpLinearListwithThreeItems();
                    }else{
                        if(productAdapter != null)
                            productAdapter.notifyDataSetChanged();
                        else
                            setUpListwithThreeItems();
                    }
                    /*if ((categoryId != null && categoryId.length() > 0 && search != null && search.length() == 0) || (searchTitle.length() > 0 && search != null && search.length() == 0)) {
                        productAdapter.notifyDataSetChanged();
                    } else
                        adapter.notifyDataSetChanged();*/
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

        if (DataManager.getInstance().getFilterLatitude() != null && DataManager.getInstance().getFilterLatitude().length() > 0) {
//            lat = Double.valueOf(DataManager.getInstance().getFilterLatitude());
//            lng = Double.valueOf(DataManager.getInstance().getFilterLongitude());
            mHomeViewModel.getTagProductList(search, filterParms, Double.valueOf(DataManager.getInstance().getFilterLatitude()), Double.valueOf(DataManager.getInstance().getFilterLongitude()), sortedBy, sordedOrder, pageNumber, limit, false, false, categoryId, tagId);
        } else {
            if (gpsTracker != null) {
                mHomeViewModel.getTagProductList(search, filterParms, gpsTracker.getLatitude(), gpsTracker.getLongitude(), sortedBy, sordedOrder, pageNumber, limit, false, false, categoryId, tagId);

            } else
                mHomeViewModel.getTagProductList(search, filterParms, 0.0, 0.0, sortedBy, sordedOrder, pageNumber, limit, false, false, categoryId, tagId);

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
//            searchTitle = getArguments().getString(AppConstants.TAG_KEY_CONSTENT.NAME);
            tagId = getArguments().getString(AppConstants.BUNDLE_DATA);
            search = getArguments().getString(AppConstants.KEY_CONSTENT.SEARCH_KEY);
            categoryId = getArguments().getString("CATEGORY");
            sortedBy = getArguments().getString("SORTEDBY", "created");
            sordedOrder = getArguments().getString("SORTEDORDER", "-1");
            setSortData(sortedBy, sordedOrder);
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
                setSortData(filterParms.get(AppConstants.KEY_CONSTENT.SORD_BY).toString(), filterParms.get(AppConstants.KEY_CONSTENT.SORT_ORDER).toString());
            }
        }
    }

    private void setSortData(String sortedBy, String sordedOrder) {

        if (sortedBy.equalsIgnoreCase("created")) {
            mBinding.tvSort.setText(getString(R.string.newest));
        } else if (sortedBy.equalsIgnoreCase("firmPrice") && sordedOrder.equals("1")) {
            mBinding.tvSort.setText(getString(R.string.price_low_to_high));
        } else if (sortedBy.equalsIgnoreCase("firmPrice") && sordedOrder.equals("-1")) {
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

                mSearchHost.openSearchSuggestion(searchTitle, categoryId, sortedBy, sordedOrder);
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
                mBinding.tvSort.setText(getString(R.string.closest));

//                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {
//                    checkGps();
//                }
                sortedBy = "";
                sordedOrder = "";
                hitSortApi("", "", lat, lng);
                break;
            case R.id.tv_newest:
                sortedBy = "created";
                sordedOrder = "-1";
                mBinding.tvSort.setText(getString(R.string.newest));
                popup.dismiss();
                hitSortApi("created", "-1", lat, lng);

                break;
            case R.id.tv_low_to_high:
                sortedBy = "firmPrice";
                sordedOrder = "1";
                mBinding.tvSort.setText(getString(R.string.price_low_to_high));
                popup.dismiss();
                hitSortApi("firmPrice", "1", lat, lng);

                break;
            case R.id.tv_price_high_low:
                sortedBy = "firmPrice";
                sordedOrder = "-1";
                mBinding.tvSort.setText(getString(R.string.price_high_to_low));
                popup.dismiss();
                hitSortApi("firmPrice", "-1", lat, lng);
                break;

                //AKM
            case R.id.iv_view:
                //String a = PreferenceManager.getInstance(getActivity()).getfargtype();
                //if ((categoryId != null && categoryId.length() > 0 && search != null && search.length() == 0) || (searchTitle.length() > 0 && search != null && search.length() == 0)) {

                    if (isGrid) {
                        isGrid = false;
                        mBinding.ivView.setImageResource(R.drawable.ic_list);
                        //PreferenceManager.getInstance(getActivity()).putfargtype("2");
                        //switchview("1");
                        setUpLinearListwithThreeItems();
                    } else {
                        isGrid = true;
                        mBinding.ivView.setImageResource(R.drawable.ic_squares);
                        //PreferenceManager.getInstance(getActivity()).putfargtype("1");
                        //switchview("1");
                        setUpListwithThreeItems();
                    }
                //}
                break;
                //AKM
            case R.id.cardcatall:
/*
                mBinding.tvSort.setText(getString(R.string.newest));
                sortedBy = "created";
                sordedOrder = "-1";*/
                isClear = true;
                categoryId = "";
                //((SearchTagShelfAcivity) mActivity).setCategoryName("All Categories");
                searchTitle = "All Categories";
                mBinding.includeHeader.tvTitle.setHint(getString(R.string.search_all_cat));
                //mBinding.includeHeader.tvSearch.setText("Search in All Categories");
                //((HomeActivity) mActivity).setCatid("");
                /*  setupViewPager("2");*/
                mProductList.clear();

                mHomeViewModel.getTagProductList(search, filterParms, lat, lng, sortedBy, sordedOrder, 1, limit, false, true, null, tagId);

                if(categoryListAdapter != null) {
                    categoryListAdapter.posClicked = -2; // to set ring in all cat
                    categoryListAdapter.notifyDataSetChanged();
                }
                break;

        }
    }

    private void openCartActivity() {
        Intent intent = new Intent(mActivity, CartActivity.class);
        startActivity(intent);
    }

    public void backPressedAction() {
        Intent intent = new Intent();
        if (filterParms != null) {
            filterParms.remove(AppConstants.KEY_CONSTENT.SEARCH_KEY);
        }
        if (filterParms != null) {
            filterParms.remove(AppConstants.KEY_CONSTENT.CATEGORY_ID);
        }
        if (filterParms != null && filterParms.containsKey(AppConstants.KEY_CONSTENT.COMMUNITY_ID))
            filterParms.remove(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID);
        intent.putExtra("FILTER_DATA", filterParms);
        FilterManager.getInstance().setmFilterMap(filterParms);
//        mActivity.setResult(Activity.RESULT_OK, intent);
        mActivity.finish();
    }

    private void openCategory() {
        Intent intent = new Intent(mActivity, CategoryActivity.class);
        startActivityForResult(intent, 100);
    }

    private void openSearchScreen() {

        Intent intent = new Intent(mActivity, SearchTagShelfAcivity.class);
        intent.putExtra("IS_FROM", 3);
        intent.putExtra("SEARCH_TITTLE", searchTitle);
        //AKM
        intent.putExtra("CATEGORY", categoryId);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.CLEAR_CATEGORY);

    }

    @Override
    public void onRefresh() {
        mHomeViewModel.getTagProductList(search, filterParms, lat, lng, sortedBy, sordedOrder, 1, limit, true, false, categoryId, tagId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case AppConstants.ACTIVITY_RESULT.FILTER:
                if (resultCode == Activity.RESULT_OK) {
                    isFilter = true;
                    if (data != null && data.getExtras() != null && data.getExtras().containsKey("FILTER_DATA")) {
                        setupLocation();
                        filterParms = (HashMap<String, Object>) data.getExtras().get("FILTER_DATA");
                        if (filterParms != null && filterParms.size() > 0) {

                            mHomeViewModel.getTagProductList(search, filterParms, lat, lng, sortedBy, sordedOrder, 1, limit, false, true, categoryId, tagId);
                        }
                    } else {
                        if (filterParms != null)
                            filterParms.clear();
                        if (gpsTracker != null)
                            mHomeViewModel.getTagProductList(search, filterParms, gpsTracker.getLatitude(), gpsTracker.getLongitude(), sortedBy, sordedOrder, 1, limit, false, true, categoryId, tagId);
                        else
                            mHomeViewModel.getTagProductList(search, filterParms, 0.0, 0.0, "", "", 1, limit, false, true, categoryId, tagId);

//                        mHomeViewModel.getTagProductList(search, filterParms, 0.0, 0.0, "", "", 1, limit, false, true, "", tagId);
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

                    mHomeViewModel.getTagProductList(search, filterParms, 0.0, 0.0, "", "", pageNumber, limit, false, true, categoryId, tagId);
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
        if (filterParms == null) {
            filterParms = new HashMap<>();
        }
        if (sordtedBy != null && sordtedBy.length() > 0)
            filterParms.put(AppConstants.KEY_CONSTENT.SORD_BY, sortedBy);
        else
            filterParms.remove(AppConstants.KEY_CONSTENT.SORD_BY);
        if (sortOrder != null && sortOrder.length() > 0)
            filterParms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, sordedOrder);
        else
            filterParms.remove(AppConstants.KEY_CONSTENT.SORT_ORDER);

        mHomeViewModel.getTagProductList(search, filterParms, lat, lng, sortedBy, sordedOrder, 1, limit, true, true, categoryId, tagId);
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
//                hitSortApi("", "", gpsTracker.getLocation().getLatitude(), gpsTracker.getLongitude());
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
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        mBinding.rvProductListing.setLayoutManager(layoutManager);
        productAdapter = new ProductListAdapter(mProductList);
        mBinding.rvProductListing.setAdapter(productAdapter);
    }

    private void setUpLinearListwithThreeItems() {
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity.getApplicationContext());
        mBinding.rvProductListing.setLayoutManager(mLayoutManager);
        //productAdapter = new ProductListAdapter(mProductList);
        adapter1 = new ProductListHorizontalAdapter(mProductList);
        mBinding.rvProductListing.setAdapter(adapter1);
    }

    private void setupLocation() {
        if (FilterManager.getInstance().getmFilterMap() == null) {
            lat = gpsTracker.getLatitude();
            lng = gpsTracker.getLongitude();
        } else {
            lat = (Double) FilterManager.getInstance().getmFilterMap().get(AppConstants.KEY_CONSTENT.LAT);
            lng = (Double) FilterManager.getInstance().getmFilterMap().get(AppConstants.KEY_CONSTENT.LONGI);
        }
    }

    /**
     * This interface is used to interact with the host {@link HomeActivity}
     */
    public interface ISearchHost {
        void backPressed();

        void setCategoryId(String id);

        void setCategoryName(String name);

        void openSearchSuggestion(String title, String categoryId, String sortedBy, String sortedOrder);
    }

    private void getCatList() {
        mCategoryList = new ArrayList<>();
        categoryListAdapter = new HomeCategoryListAdapter(mActivity, mCategoryList);

        mBinding.rvCategoryList.setAdapter(categoryListAdapter);

        categoryLisViewModel = ViewModelProviders.of(this).get(CategoryListViewModel.class);
        categoryLisViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        categoryLisViewModel.getCategoryListViewModel().observe(this, new Observer<CategoryResponse>() {
            @Override
            public void onChanged(@Nullable CategoryResponse categoryResponse) {
                //hideProgressDialog();
                if (categoryResponse != null) {
                    getLoadingStateObserver().onChanged(false);

                    ArrayList<CategoryListResponse> mCategoryList1 = new ArrayList<>();
                    mCategoryList1.addAll(categoryResponse.getmCategory());

                    ArrayList<CategoryListResponse> mCategoryList2 = new ArrayList<>();

                    for (int i = 0; i < mCategoryList1.size(); i++) {

                        if (mCategoryList1.get(i).getName().contains("Furniture")) {
                            mCategoryList2.add(mCategoryList1.get(i));
                        } else if (mCategoryList1.get(i).getName().contains("Household")) {
                            mCategoryList2.add(mCategoryList1.get(i));
                        } else if (mCategoryList1.get(i).getName().contains("Electronic")) {
                            mCategoryList2.add(mCategoryList1.get(i));
                        } else if (mCategoryList1.get(i).getName().contains("Clothe")) {
                            mCategoryList2.add(mCategoryList1.get(i));
                        } else if (mCategoryList1.get(i).getName().contains("Footwear")) {
                            mCategoryList2.add(mCategoryList1.get(i));
                        } else if (mCategoryList1.get(i).getName().contains("Beauty")) {
                            mCategoryList2.add(mCategoryList1.get(i));
                        } else if (mCategoryList1.get(i).getName().contains("Accessorie")) {
                            mCategoryList2.add(mCategoryList1.get(i));
                        } else if (i == mCategoryList1.size() - 1) {
//
                            CategoryListResponse categoryListResponse = new CategoryListResponse();
                            categoryListResponse.setName("more");
                            categoryListResponse.setId("more");
                            mCategoryList2.add(categoryListResponse);
                        }
                    }
                    mCategoryList.add(mCategoryList2.get(5));
                    mCategoryList.add(mCategoryList2.get(6));
                    mCategoryList.add(mCategoryList2.get(3));
                    mCategoryList.add(mCategoryList2.get(2));
                    mCategoryList.add(mCategoryList2.get(4));
                    mCategoryList.add(mCategoryList2.get(1));
                    mCategoryList.add(mCategoryList2.get(0));
                    mCategoryList.add(mCategoryList2.get(7));

                    categoryListAdapter.notifyDataSetChanged();
                }
            }
        });
        /*if (AppUtils.isInternetAvailable(mActivity))

        else showNoNetworkError();*/
        categoryLisViewModel.hitGetCategory();
    }

    public  class HomeCategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private ArrayList<CategoryListResponse> mCategoryList;
        private int posClicked = -2;

        public HomeCategoryListAdapter(Context context, ArrayList<CategoryListResponse> mCategoryList) {
            this.context = context;
            this.mCategoryList = mCategoryList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            AdapterCategoryCatHomeBinding mBinding = AdapterCategoryCatHomeBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            //DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_category_view, viewGroup, false);
            return new HomeCategoryListAdapter.CategoryListViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
            HomeCategoryListAdapter.CategoryListViewHolder holder = null;
            holder = (SearchTagProductResultFragment.HomeCategoryListAdapter.CategoryListViewHolder) viewHolder;
            final Bitmap[] bitmapImage = {null};
            try {
                Picasso.get()
                        .load(mCategoryList.get(position).getImageUrl())
                        .error(R.mipmap.ic_launcher)
                        .into(new Target() {

                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                ((CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setImageBitmap(bitmap);

                                if(posClicked == position || posClicked == -2){

                                    if(bitmap != null)
                                        Palette.from(bitmap)
                                                .generate(new Palette.PaletteAsyncListener() {
                                                    @Override
                                                    public void onGenerated(Palette palette) {
                                                        Palette.Swatch textSwatch = palette.getVibrantSwatch();

                                                        if (textSwatch == null) {
                                                            textSwatch = palette.getDarkVibrantSwatch();
                                                            //Toast.makeText(MainActivity.this, "Null swatch :(", Toast.LENGTH_SHORT).show();
                                                        }


                                                        if (textSwatch == null)
                                                            textSwatch = palette.getLightVibrantSwatch();
                                                        //textSwatch = palette.getLightMutedSwatch();

                                                        if (textSwatch == null)
                                                            textSwatch = palette.getDominantSwatch();

                                                        if (textSwatch == null)
                                                            return;

                                                        GradientDrawable gradient = new GradientDrawable();
                                                        gradient.setShape(GradientDrawable.OVAL);
                                                        gradient.setCornerRadius(6.f);
                                                        gradient.setColor(textSwatch.getRgb());

                                                        if(mCategoryList.get(position).getName().equalsIgnoreCase("HouseHold")){
                                                            gradient.setColor(context.getResources().getColor(R.color.householdDark));
                                                        }
                                                        ((HomeCategoryListAdapter.CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setBackgroundDrawable(gradient);
                                                    }
                                                });
                                    //((HomeCategoryListAdapter.CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setSelected(false);
                                    ((HomeCategoryListAdapter.CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setAlpha(1f);
                                }else{
                                    ((HomeCategoryListAdapter.CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setBackgroundDrawable(null);
                                    ((HomeCategoryListAdapter.CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setAlpha(.6f);
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                e.printStackTrace();

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }

            ((HomeCategoryListAdapter.CategoryListViewHolder) viewHolder).viewBinding.tvCategoryName.setText(mCategoryList.get(position).getName());
            if (position == mCategoryList.size() - 1) {
                ((HomeCategoryListAdapter.CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setImageResource(R.drawable.moreicn);
                ((HomeCategoryListAdapter.CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setBackgroundDrawable(null);
            }





        }

        public void setGradientColors(int bottomColor, int topColor) {

        }

        @Override
        public int getItemCount() {
            return mCategoryList.size();
        }

        private class CategoryListViewHolder extends RecyclerView.ViewHolder {
            AdapterCategoryCatHomeBinding viewBinding;

            public CategoryListViewHolder(final AdapterCategoryCatHomeBinding viewBinding) {
                super(viewBinding.getRoot());
                this.viewBinding = viewBinding;
                viewBinding.cardcat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCategoryList.get(getAdapterPosition()).getName().equals("more")) {
                            openCategory();
                            posClicked = -1;
                        } else {
                            isClear = true;
                            categoryId = mCategoryList.get(getAdapterPosition()).getId();
                            //((SearchTagShelfAcivity) mActivity).setCategoryName( mCategoryList.get(getAdapterPosition()).getName());
                            searchTitle = mCategoryList.get(getAdapterPosition()).getName();
                            mBinding.includeHeader.tvTitle.setHint(getString(R.string.search_in) + " " + mCategoryList.get(getAdapterPosition()).getName());
                            //((SearchTagShelfAcivity) mActivity).setCategoryId(categoryId);
                            //mBinding.includeHeader.tvSearch.setText("Search in " + searchTitle);
                            Log.d("vihsalsklfdh", categoryId + "qwerty" + searchTitle);
                            posClicked = getAdapterPosition();
                            //currentFragmentMethod2("created", categoryId, "-1", lat, lng);

                            //AKM
                            mProductList.clear();
                            mHomeViewModel.getTagProductList(search, filterParms, lat, lng, sortedBy, sordedOrder, 1, limit, false, true, categoryId, tagId);
                        }
                        notifyDataSetChanged();

                    }
                });
            }

        }
    }
}


