package com.taghawk.ui.home.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.taghawk.BuildConfig;
import com.taghawk.R;
import com.taghawk.adapters.ProductListAdapter;
import com.taghawk.adapters.ProductListHorizontalAdapter;
import com.taghawk.adapters.ViewPagerAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.data.DataManager;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.FragmentProductListBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.CommonDataModel;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.model.update_rating_notification.RatingData;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.ui.home.HomeFragment;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.FilterManager;
import com.taghawk.util.GPSTracker;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    String type;
    /**
     * A {@link HomeViewModel} object to handle all the actions and business logic
     */
    private HomeViewModel mHomeViewModel;
    private HomeFragment.IHomeHost mHomeHost;
    private FragmentProductListBinding mBinding;
    private Activity mActivity;
    private ProductListAdapter adapter;
    private ProductListHorizontalAdapter adapter1;
    private ArrayList<ProductListModel> mProductList;
    private int currentPageNumber = 1;
    private int limit = 15;
    private int lastPage;
    private boolean isLoading;
    private boolean isPagin;
    private String categoryId = "", searchTitle = "";
    private boolean isClear;
    private HashMap<String, Object> parms;
    private boolean isFilter;
    private PopupWindow popup;
    private GPSTracker gpsTracker;
    private Double lat = 0.0, lng = 0.0;
    private ViewPagerAdapter viewPagerAdapter;
    private String sortedBy = "created", sordedOrder = "-1";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentProductListBinding.inflate(inflater, container, false);
        initView();
//        onRefresh();

        return mBinding.getRoot();
    }

    private void getCommonData() {
        mHomeViewModel.getCommonData();
       /* adapter.notifyDataSetChanged();
        adapter1.notifyDataSetChanged();*/

    }

    // initialize views
    private void initView() {
        type = PreferenceManager.getInstance(getActivity()).getfargtype();
        mActivity = getActivity();
        gpsTracker = new GPSTracker(mActivity);
        mBinding.swipe.setOnRefreshListener(this);
        categoryId = ((HomeActivity) mActivity).getCatid();
        setupLocation();
        if (parms == null) {
            parms = new HashMap<>();
            parms = ((HomeActivity) mActivity).getProductFilterParms();
        }
        mBinding.rvProductListing.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    ((HomeActivity) mActivity).handleDirection(View.GONE);
                } else if (dy <= 0) {
                    ((HomeActivity) mActivity).handleDirection(View.VISIBLE);
                }
            }
        });
    }

    private void setUpList() {
        type = PreferenceManager.getInstance(getActivity()).getfargtype();
        if (type.equals("1")) {
            final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
            adapter = new ProductListAdapter(mProductList);
            adapter1 = new ProductListHorizontalAdapter(mProductList);
            mBinding.rvProductListing.setLayoutManager(layoutManager);
            mBinding.rvProductListing.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            layoutManager.smoothScrollToPosition(mBinding.rvProductListing, null, 0);
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
                                if ((sortedBy != null && sortedBy.length() > 0) || (sordedOrder != null && sordedOrder.length() > 0) || (lat > 0 && lng > 0) || (parms != null && parms.size() > 0)) {
                                    mHomeViewModel.getProductFilterListPagination(mProductList, parms, lat, lng, sortedBy, sordedOrder, currentPageNumber + 1, limit, true, true, categoryId);
                                } else if (gpsTracker != null)
                                    mHomeViewModel.getProductListPagination(mProductList, "", currentPageNumber + 1, limit, true, true, categoryId, false, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                                else
                                    mHomeViewModel.getProductListPagination(mProductList, "", currentPageNumber + 1, limit, true, true, categoryId, false, 0.0, 0.0);
                            }
                        }
                    }

                }
            });
        } else {
            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity.getApplicationContext());
            adapter = new ProductListAdapter(mProductList);
            adapter1 = new ProductListHorizontalAdapter(mProductList);
            mBinding.rvProductListing.setLayoutManager(mLayoutManager);
            mBinding.rvProductListing.setAdapter(adapter1);
            adapter1.notifyDataSetChanged();
            mLayoutManager.smoothScrollToPosition(mBinding.rvProductListing, null, 0);
            mBinding.rvProductListing.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        int visibleItemCount = mLayoutManager.getChildCount();
                        int totalItems = mLayoutManager.getItemCount();
                        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

                        if (isLoading) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItems
                                    && firstVisibleItemPosition >= 0) {
                                isLoading = false;
                                if ((sortedBy != null && sortedBy.length() > 0) || (sordedOrder != null && sordedOrder.length() > 0) || (lat > 0 && lng > 0) || (parms != null && parms.size() > 0)) {
                                    mHomeViewModel.getProductFilterListPagination(mProductList, parms, lat, lng, sortedBy, sordedOrder, currentPageNumber + 1, limit, true, true, categoryId);
                                } else if (gpsTracker != null)
                                    mHomeViewModel.getProductListPagination(mProductList, "", currentPageNumber + 1, limit, true, true, categoryId, false, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                                else
                                    mHomeViewModel.getProductListPagination(mProductList, "", currentPageNumber + 1, limit, true, true, categoryId, false, 0.0, 0.0);
                            }
                        }
                    }

                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing view model

        Log.d("vihsalsklfdh", categoryId + "qwerty" + searchTitle);
        mProductList = new ArrayList<>();
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mHomeViewModel.getProductListing().observe(this, new Observer<ProductListingModel>() {
            @Override
            public void onChanged(@Nullable ProductListingModel productListingModel) {
                isPagin = false;
                getLoadingStateObserver().onChanged(false);
                if (productListingModel != null && productListingModel.getCode() == 200) {
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
//                    mProductList.clear();
                    mProductList.addAll(productListingModel.getmProductList());

//                    setUpList();
                    adapter.notifyDataSetChanged();
                    adapter1.notifyDataSetChanged();

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
        mHomeViewModel.getmCommonLiveData().observe(this, new Observer<CommonDataModel>() {
            @Override
            public void onChanged(@Nullable CommonDataModel commonResponse) {
                if (commonResponse != null) {
                    perfromUpdateActionIfAny(commonResponse);
                }
            }
        });
        mHomeViewModel.getmFeedBackLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
            }
        });
        DataManager.getInstance().saveSortBy("created");
        DataManager.getInstance().saveSortOrder("-1");
        if (parms != null && parms.size() > 0) {
            if (gpsTracker != null) {
                if (FilterManager.getInstance().getmFilterMap() != null) {
                    mHomeViewModel.getProductFilterList(parms, 0.0, 0.0, "created", "-1", 1, limit, false, true, categoryId);

                } else
                    mHomeViewModel.getProductFilterList(parms, gpsTracker.getLatitude(), gpsTracker.getLongitude(), "created", "-1", 1, limit, false, true, categoryId);

//                mHomeViewModel.getProductFilterList(parms, gpsTracker.getLatitude(), gpsTracker.getLongitude(), "created", "-1", 1, limit, true, true, categoryId);
            } else {
                mHomeViewModel.getProductFilterList(parms, 0.0, 0.0, "created", "-1", 1, limit, true, true, categoryId);

            }
        } else {
            if (gpsTracker != null)
                mHomeViewModel.getProductList("", currentPageNumber, limit, false, true, categoryId, false, gpsTracker.getLatitude(), gpsTracker.getLongitude());
            else
                mHomeViewModel.getProductList("", currentPageNumber, limit, false, true, categoryId, false, 0.0, 0.0);

        }
        setUpList();

        getCommonData();
    }

    //This Function is used to show Rating dialog
    public void showRatingDialog(final RatingData bean) {
        DialogUtil.getInstance().CustomRateBottomSheetDialog(mActivity, bean.getFullName(), bean.getTitle(), new OnDialogItemClickListener() {
            @Override
            public void onPositiveBtnClick() {
                DialogUtil.getInstance().CustomGiveRatingBottomSheetDialog(mActivity, bean.getFullName(), bean.getTitle(), bean.getProfilePicture(), new OnDialogViewClickListener() {
                    @Override
                    public void onSubmit(String txt, int rating) {
                        mHomeViewModel.giveRating(bean.getSellerId(), bean.getProductId(), rating, txt);
                    }
                });
            }

            @Override
            public void onNegativeBtnClick() {
                DialogUtil.getInstance().CustomDenyBottomSheetDialog(mActivity, new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {
                    }

                    @Override
                    public void onNegativeBtnClick() {
                        mHomeViewModel.denyRating(bean.getSellerId(), bean.getProductId());

                    }
                });
            }
        });
    }

    // This function is use for show hide empty place holder
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
        isPagin = false;
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
        isPagin = false;
        getLoadingStateObserver().onChanged(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    //This Function is use for check Gps is enable or not
    private void checkGps() {
        if (gpsTracker == null)
            gpsTracker = new GPSTracker(mActivity);
        if (gpsTracker.isGPSEnable()) {
            if (gpsTracker.getLocation() != null) {
//                mBinding.tvSort.setText(getString(R.string.closest));
//                hitSortApi("", "", gpsTracker.getLocation().getLatitude(), gpsTracker.getLongitude());
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    // This Function is use for perform sorting operation
    public void hitSortApi(String sordtedBy, String sortOrder, Double lat, Double lng, String categoryId) {
        isFilter = true;
        this.sortedBy = sordtedBy;
        this.sordedOrder = sortOrder;
        this.lat = lat;
        this.lng = lng;

        DataManager.getInstance().saveSortBy(sordtedBy);
        DataManager.getInstance().saveSortOrder(sortOrder);
        Log.d("adjsfklja", categoryId);
        if (mHomeViewModel != null) {
            mHomeViewModel.getProductFilterList(parms, lat, lng, sortedBy, sordedOrder, 1, limit, true, true, categoryId);
        } else {

            Log.d("ghf", "kghjg");
        }
    }

    public void chnageview(String sordtedBy) {
        setUpList();
    }


    public void hitallApi() {
        if (mHomeViewModel != null) {
            mProductList.clear();
            /*if (gpsTracker != null)
                mHomeViewModel.getProductList("", currentPageNumber, limit, true, true, categoryId, false, gpsTracker.getLatitude(), gpsTracker.getLongitude());
            else

                mHomeViewModel.getProductList("", currentPageNumber, limit, true, true, categoryId, false, 0.0, 0.0);
        }*/
            categoryId = ((HomeActivity) mActivity).getCatid();
            if (gpsTracker != null)
                mHomeViewModel.getProductFilterList(parms, 0.0, 0.0, sortedBy, sordedOrder, 1, limit, false, true, categoryId);
            else
                mHomeViewModel.getProductFilterList(parms, 0.0, 0.0, "", "", 1, limit, false, true, categoryId);

        }
    }

    public void hitSortApi() {
        Log.d("djshf","1");
        if (mHomeViewModel != null) {
            isFilter = true;
            if (parms != null && parms.size() > 0) {
                if (FilterManager.getInstance().getmFilterMap() != null) {
                    Log.d("djshf","4");
                    mHomeViewModel.getProductFilterList(parms, 0.0, 0.0, "", "", 1, limit, false, true, categoryId);
                } else {
                    Log.d("djshf","2");
                    if (gpsTracker != null)

                        mHomeViewModel.getProductFilterList(parms, gpsTracker.getLatitude(), gpsTracker.getLongitude(), sortedBy, sordedOrder, 1, limit, false, true, categoryId);
                    else
                        mHomeViewModel.getProductFilterList(parms, 0.0, 0.0, "", "", 1, limit, false, true, categoryId);

                }
            } else {
                Log.d("djshf","3");
                if (gpsTracker != null)
                    mHomeViewModel.getProductList("", currentPageNumber, limit, false, true, categoryId, false, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                else
                    mHomeViewModel.getProductList("", currentPageNumber, limit, false, true, categoryId, false, 0.0, 0.0);
            }
        }
    }

    public void hitSortApi2() {
        Log.d("djshf","1");
        if (mHomeViewModel != null) {
            isFilter = true;
            if (parms != null && parms.size() > 0) {
                if (FilterManager.getInstance().getmFilterMap() != null) {
                    Log.d("djshf","4");
                    mHomeViewModel.getProductFilterList(parms, 0.0, 0.0, "", "", 1, limit, false, true, "");
                } else {
                    Log.d("djshf","2");
                    if (gpsTracker != null)

                        mHomeViewModel.getProductFilterList(parms, gpsTracker.getLatitude(), gpsTracker.getLongitude(), sortedBy, sordedOrder, 1, limit, false, true, "");
                    else
                        mHomeViewModel.getProductFilterList(parms, 0.0, 0.0, "", "", 1, limit, false, true, "");

                }
            } else {
                Log.d("djshf","3");
                if (gpsTracker != null)
                    mHomeViewModel.getProductList("", currentPageNumber, limit, false, true, "", false, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                else
                    mHomeViewModel.getProductList("", currentPageNumber, limit, false, true, "", false, 0.0, 0.0);
            }
        }
    }

    //    http://13.209.26.65:2000/api/v2/product?pageNo=1&limit=15&lat=26.8545621&long=75.766634 (860ms)
//    http://13.209.26.65:2000/api/v2/product?pageNo=1&sortOrder=-1&limit=15&sortBy=created&lat=26.8545621&long=75.766634
    public void hitSortApi1(String sordtedBy, String categoryId, String sortOrder, Double lat, Double lng) {
        mProductList.clear();
        if (mHomeViewModel != null) {

            if (FilterManager.getInstance().getmFilterMap() != null) {
                mHomeViewModel.getProductFilterList(parms, 0.0, 0.0, "", "", 1, limit, false, true, categoryId);
            } else {
                if (gpsTracker != null)
                    mHomeViewModel.getProductFilterList(parms, gpsTracker.getLatitude(), gpsTracker.getLongitude(), sortedBy, sordedOrder, 1, limit, false, true, categoryId);
                else
                    mHomeViewModel.getProductFilterList(parms, 0.0, 0.0, "", "", 1, limit, false, true, categoryId);

            }
        } else {

            mHomeViewModel.getProductFilterList(parms, lat, lng, sortedBy, sordedOrder, 1, limit, true, true, categoryId);
              /*  else
                    mHomeViewModel.getProductList("", currentPageNumber, limit, false, true, categoryId, false, 0.0, 0.0);
 */
        }

    }


    @Override
    public void onRefresh() {
        setupLocation();
        categoryId = ((HomeActivity) mActivity).getCatid();
        Log.d("dklhf", "dkslfhjkls");
        String search = "";
        if (FilterManager.getInstance().getmFilterMap() != null && FilterManager.getInstance().getmFilterMap().containsKey(AppConstants.KEY_CONSTENT.LAT)) {
            lat = Double.valueOf(FilterManager.getInstance().getmFilterMap().get(AppConstants.KEY_CONSTENT.LAT).toString());
            lng = Double.valueOf(FilterManager.getInstance().getmFilterMap().get(AppConstants.KEY_CONSTENT.LONGI).toString());
        } else if (gpsTracker != null) {
            lat = gpsTracker.getLatitude();
            lng = gpsTracker.getLongitude();
        }
        if ((sortedBy != null && sortedBy.length() > 0) || (sordedOrder != null && sordedOrder.length() > 0) || (lat > 0 && lng > 0) || (parms != null && parms.size() > 0)) {
            mHomeViewModel.getProductFilterList(parms, lat, lng, sortedBy, sordedOrder, 1, limit, true, true, categoryId);
        } else if (FilterManager.getInstance().getmFilterMap() != null && FilterManager.getInstance().getmFilterMap().size() > 0)
            mHomeViewModel.getProductList(search, 1, limit, true, true, categoryId, false, lat, lng);
        else if (gpsTracker != null)
            mHomeViewModel.getProductList(search, 1, limit, true, true, categoryId, false, gpsTracker.getLatitude(), gpsTracker.getLongitude());
        else
            mHomeViewModel.getProductList(search, 1, limit, true, true, categoryId, false, 0.0, 0.0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            categoryId = ((HomeActivity) mActivity).getCatid();
            switch (requestCode) {

                case 100:
                    if (resultCode == Activity.RESULT_OK) {
                        searchTitle = "All";
                  /*  isClear = true;
                    categoryId = data.getExtras().getString("CATEGORY_ID");
                    searchTitle = data.getExtras().getString("TITLE");
                    if (searchTitle.length() == 0)
                        mProductList.clear();
                    mHomeViewModel.getProductFilterList(parms, lat, lng, sortedBy, sordedOrder, 1, limit, false, false, categoryId);
//                    mHomeViewModel.getProductList("", 1, limit, false, true, categoryId);
      */
                    }
                    break;
                case AppConstants.ACTIVITY_RESULT.FILTER:
                    if (resultCode == Activity.RESULT_OK) {
                        isFilter = true;

                        if (data != null && data.getExtras() != null && data.getExtras().containsKey("FILTER_DATA")) {
                            parms = (HashMap<String, Object>) data.getExtras().get("FILTER_DATA");
                            removeCategoryId();
                            setupLocation();
                            if (parms != null && parms.size() > 0)
                                mHomeViewModel.getProductFilterList(parms, lat, lng, sortedBy, sordedOrder, 1, limit, false, true, categoryId);
                        } else {
                            if (parms != null)
                                parms.clear();
                            if (mHomeViewModel != null) {
                                sordedOrder = "-1";
                                sortedBy = "created";
                                if (gpsTracker != null) {
                                    mHomeViewModel.getProductFilterList(parms, gpsTracker.getLatitude(), gpsTracker.getLongitude(), sortedBy, sordedOrder, 1, limit, false, true, categoryId);
                                } else {
                                    if (FilterManager.getInstance().getmFilterMap() != null && FilterManager.getInstance().getmFilterMap().containsKey(AppConstants.KEY_CONSTENT.LAT)) {
                                        mHomeViewModel.getProductFilterList(parms, Double.valueOf(FilterManager.getInstance().getmFilterMap().get(AppConstants.KEY_CONSTENT.LAT).toString()), Double.valueOf(FilterManager.getInstance().getmFilterMap().get(AppConstants.KEY_CONSTENT.LONGI).toString()), "created", "-1", 1, limit, false, true, categoryId);
                                    } else {
                                        mHomeViewModel.getProductFilterList(parms, 0.0, 0.0, sortedBy, sordedOrder, 1, limit, false, true, categoryId);
                                    }
                                }
                            }
                        }
                    }
                    break;
                case AppConstants.REQUEST_CODE.TAG_SEACHING_RESULT:
                    if (mProductList != null) {
                        mProductList.clear();
                    }
                    if (DataManager.getInstance().getFilterLatitude() != null && DataManager.getInstance().getFilterLongitude() != null && DataManager.getInstance().getFilterLatitude().length() > 1 && DataManager.getInstance().getFilterLongitude().length() > 0) {
                        if (parms != null && parms.get(AppConstants.KEY_CONSTENT.LAT) != null && !parms.get(AppConstants.KEY_CONSTENT.LAT).toString().equalsIgnoreCase(DataManager.getInstance().getFilterLatitude())) {
                            mHomeViewModel.getProductFilterList(FilterManager.getInstance().getmFilterMap(), Double.valueOf(DataManager.getInstance().getFilterLatitude()), Double.valueOf(DataManager.getInstance().getFilterLongitude()), sortedBy, sordedOrder, 1, limit, false, true, categoryId);
                        } else if (data != null && data.getExtras() != null && data.getExtras().containsKey("FILTER_DATA")) {
//                        if (mProductList != null) {
//                            mProductList.clear();
                            Log.d("fkadgs", sortedBy + "s" + sordedOrder + "s" + limit + "s" + categoryId);
                            try {
                                mHomeViewModel.getProductFilterList(FilterManager.getInstance().getmFilterMap(), Double.valueOf(DataManager.getInstance().getFilterLatitude()), Double.valueOf(DataManager.getInstance().getFilterLongitude()), sortedBy, sordedOrder, 1, limit, false, true, categoryId);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //                        }
                        }
                    } else {
                        mHomeViewModel.getProductFilterList(FilterManager.getInstance().getmFilterMap(), 0.0, 0.0, "", "", 1, limit, false, true, categoryId);
                    }
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //This funstion is use for remove categoryid from saved filter data
    private void removeCategoryId() {
        try {
            if (FilterManager.getInstance().getmFilterMap() != null) {

                FilterManager.getInstance().getmFilterMap().remove(AppConstants.KEY_CONSTENT.PRODUCT_CATEGORY_ID);

            }
        } catch (Exception e) {
        }
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

    public void updateListByFilter() {
        isFilter = true;
        if (FilterManager.getInstance().getmFilterMap() != null) {
            parms = FilterManager.getInstance().getmFilterMap();
            mHomeViewModel.getProductFilterList(FilterManager.getInstance().getmFilterMap(), 0.0, 0.0, "", "", 1, limit, false, true, "");
        } else {
            mHomeViewModel.getProductFilterList(FilterManager.getInstance().getmFilterMap(), 0.0, 0.0, "", "", 1, limit, false, true, "");
        }
    }

    private void perfromUpdateActionIfAny(CommonDataModel commonResponse) {
        if ((Integer.valueOf(commonResponse.getCommonDataResponse().getCommonResponseData().getVersionName()) > BuildConfig.VERSION_CODE) && (commonResponse.getCommonDataResponse().getCommonResponseData().getType().equalsIgnoreCase(AppConstants.UpdateType.FORCE))) {
            getCustomBottomDialog(getString(R.string.app_update), getString(R.string.update_msg), new OnDialogItemClickListener() {
                @Override
                public void onPositiveBtnClick() {
                    AppUtils.launchMarket(mActivity);
                }

                @Override
                public void onNegativeBtnClick() {
                }
            });
        } else if ((Integer.valueOf(commonResponse.getCommonDataResponse().getCommonResponseData().getVersionName()) > BuildConfig.VERSION_CODE) && (commonResponse.getCommonDataResponse().getCommonResponseData().getType().equalsIgnoreCase(AppConstants.UpdateType.NORMAL))) {
            DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.app_update), getString(R.string.update_msg), getString(R.string.update), getString(R.string.cancel), new DialogCallback() {
                @Override
                public void submit(String data) {
                    AppUtils.launchMarket(mActivity);
                }

                @Override
                public void cancel() {
                }
            });
        }
    }
}
