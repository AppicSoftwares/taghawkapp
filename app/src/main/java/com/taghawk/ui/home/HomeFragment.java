package com.taghawk.ui.home;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.taghawk.R;
import com.taghawk.adapters.ViewPagerAdapter;
import com.taghawk.base.BaseActivity;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.AdapterCategoryCatHomeBinding;
import com.taghawk.databinding.FragmentHomeBinding;
import com.taghawk.databinding.LayoutSortBinding;
import com.taghawk.databinding.LayoutTagSortBinding;
import com.taghawk.databinding.LayoutTagType2Binding;
import com.taghawk.databinding.LayoutTagTypeSortBinding;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.category.CategoryListResponse;
import com.taghawk.model.category.CategoryResponse;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.update_rating_notification.RatingData;
import com.taghawk.ui.OnButtonPressListener;
import com.taghawk.ui.cart.CartActivity;
import com.taghawk.ui.gift.GiftRewardPromotionActivity;
import com.taghawk.ui.home.category.CategoryActivity;
import com.taghawk.ui.home.category.CategoryListViewModel;
import com.taghawk.ui.home.filter.FilterActivity;
import com.taghawk.ui.home.filter.TagFilterActivity;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.ui.home.search.SearchAcivity;
import com.taghawk.ui.home.shelf.SearchTagProductResultFragment;
import com.taghawk.ui.home.tabs.ProductListFragment;
import com.taghawk.ui.home.tabs.TagListingFragment;
import com.taghawk.ui.myListener;
import com.taghawk.ui.tag.AddTagActivity;
import com.taghawk.ui.tag.TagDetailsActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.FilterManager;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.PermissionUtility;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * A simple {@link BaseFragment} subclass.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, ViewPager.OnPageChangeListener {
    public FragmentHomeBinding mBinding;
    myListener listener;
    Context context;
    OnButtonPressListener buttonListener;
    /**
     * This method is used to return the instance of this fragment
     *
     * @return new instance of {@link HomeFragment}
     */
    SendMessage SM;
    /*
     * A {@link HomeViewModel} object to handle all the actions and business logic
     */
    private float originalBackgroundTranslationX;
    private ArgbEvaluator argbEvaluator;
    private HomeViewModel mHomeViewModel;
    private IHomeHost mHomeHost;
    private Activity mActivity;
    private BaseActivity activity;
    private ArrayList<ProductListModel> mProductList;
    private int currentPageNumber = 1;
    private int limit = 21;
    private int lastPage;
    private boolean isLoading;
    private boolean isPagin;
    private String categoryId = "", searchTitle = "All Categories";
    private boolean isClear;
    private HashMap<String, Object> parms, tagFilterParms;
    private boolean isFilter;
    private PopupWindow popup;
    private GPSTracker gpsTracker;
    private Double lat = 0.0, lng = 0.0;
    private ViewPagerAdapter viewPagerAdapter;
    private String sortedBy = "", sordedOrder = "";
    private Address location;
    private Address tagLocation;
    private PopupWindow tagPopup;
    private String tagSearch = "", itemSort, tagSort;
    private String type = "2";
    private HomeCategoryListAdapter adapter1;
    private ArrayList<CategoryListResponse> mCategoryList;
    private CategoryListViewModel mViewModel;

    public static HomeFragment getInstance() {
        return new HomeFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }


        if (context instanceof IHomeHost) {
            mHomeHost = (IHomeHost) context;
        } else
            throw new IllegalStateException("Host must implement IHomeHost");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        mActivity = getActivity();
        context = mBinding.getRoot().getContext();
        activity = ((BaseActivity) mActivity);

//            buttonListener = (OnButtonPressListener) getActivity();


        setupView();
        initView();
        setupViewPager("1");
//        setmyListener((myListener) this);
        return mBinding.getRoot();
    }

    public void setmyListener(myListener listener) {
        this.listener = listener;
    }

    // setup view pager
    private void setupViewPager(String type1) {
        Bundle bundle = new Bundle();
        bundle.putString("type12", type1);
        ProductListFragment fragment = new ProductListFragment();
        fragment.setArguments(bundle);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(fragment, "Product List");
        viewPagerAdapter.addFragment(new TagListingFragment(), "TAG List");
        mBinding.vp.setAdapter(viewPagerAdapter);
        mBinding.vp.addOnPageChangeListener(this);
        viewPagerAdapter.notifyDataSetChanged();

        // mBinding.vp.getAdapter().notifyDataSetChanged();
    }


    // init views and listener
    private void initView() {
        ((HomeActivity) mActivity).setCatid(categoryId);
        gpsTracker = new GPSTracker(mActivity);
        getArgumentsData();
        itemSort = getString(R.string.newest);
        //AKM
        //tagSort = getString(R.string.all_member);
        tagSort = getString(R.string.all_tag);
        originalBackgroundTranslationX = mBinding.viewBackground.getTranslationX();
        setupLocation();
        mBinding.includeHeader.ivCategory.setOnClickListener(this);
        mBinding.includeHeader.tvSearch.setHint(getString(R.string.search_all_cat));
        mBinding.flItems.setOnClickListener(this);
        mBinding.ivView.setOnClickListener(this);
        mBinding.swipe.setOnRefreshListener(this);
        mBinding.llMenu.setVisibility(View.VISIBLE);
        mBinding.cardcatall.setOnClickListener(this);
        mBinding.flTags.setOnClickListener(this);
        mBinding.includeHeader.tvSearch.setOnClickListener(this);
        mBinding.ivFilter.setOnClickListener(this);
        mBinding.tvSort.setOnClickListener(this);
        mBinding.ivCart.setOnClickListener(this);
        mBinding.ivMap.setOnClickListener(this);
        mBinding.ivDown.setOnClickListener(this);
        mBinding.ivblank.setOnClickListener(this);
        mBinding.ivAdd.setOnClickListener(this);
        mBinding.includeHeader.ivCart.setOnClickListener(this);
        mBinding.includeHeader.ivGift.setOnClickListener(this);
        argbEvaluator = new ArgbEvaluator();

        String a = PreferenceManager.getInstance(getActivity()).getfargtype();
        if (a.equals("1")) {
            mBinding.ivView.setImageResource(R.drawable.ic_list);
        } else {
            mBinding.ivView.setImageResource(R.drawable.ic_squares);
        }
        String b = PreferenceManager.getInstance(getActivity()).getfargmaptype();
        if (b.equals("1")) {
            mBinding.ivMap.setImageResource(R.drawable.map);
        } else {
            mBinding.ivMap.setImageResource(R.drawable.ic_list);
        }

    }

    //get arguments if any and open detail of deeplink product id
    private void getArgumentsData() {
        if (getArguments() != null && getArguments().getString(AppConstants.NOTIFICATION_ACTION.ENTITY_ID) != null) {
            String productId = getArguments().getString(AppConstants.NOTIFICATION_ACTION.ENTITY_ID);
            String type = getArguments().getString(AppConstants.DEEP_INK_CONSTENT.TYPE);
            if (type.length() > 0 && productId != null && productId.length() > 0) {
                if (type.equals("3")) {
                    Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                    intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, productId);
                    startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.PRODUCT_DETAILS);
                } else if (type.equals("4")) {
                    Intent intent = new Intent(mActivity, TagDetailsActivity.class);
                    intent.putExtra("TAG_ID", productId);
                    startActivity(intent);
                }
            }
        }
    }

    private void setupView() {
//        mBinding.llMenu.setVisibility(View.VISIBLE);
        mCategoryList = new ArrayList<>();
        adapter1 = new HomeCategoryListAdapter(mActivity, mCategoryList);

        mBinding.rvCategoryList.setAdapter(adapter1);
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        getLoadingStateObserver().onChanged(false);
        hideProgressDialog();
        showToastLong(failureResponse.getErrorMessage());
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        super.onErrorOccurred(throwable);
        hideProgressDialog();
        getLoadingStateObserver().onChanged(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing view model

        mViewModel = ViewModelProviders.of(this).get(CategoryListViewModel.class);
        mViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mViewModel.getCategoryListViewModel().observe(this, new Observer<CategoryResponse>() {
            @Override
            public void onChanged(@Nullable CategoryResponse categoryResponse) {
                hideProgressDialog();
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

                    adapter1.notifyDataSetChanged();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter1.notifyDataSetChanged();
                        }
                    }, 1000);
                }
            }
        });
        if (AppUtils.isInternetAvailable(mActivity))
            mViewModel.hitGetCategory();
        else showNoNetworkError();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_category:

                openCategory();
            case R.id.ivblank:
                mBinding.appbarContainer.setExpanded(true, true);
//                openCategory();
                break;
            case R.id.iv_down:
                mBinding.appbarContainer.setExpanded(true, true);
//                openCategory();
                break;
            case R.id.fl_tags:
                if (mBinding.vp != null) {
                    mBinding.llMenu.setVisibility(View.GONE);
                }
                mBinding.ivView.setVisibility(View.GONE);
                mBinding.ivAdd.setVisibility(View.GONE);
                mBinding.ivMap.setVisibility(View.GONE);
                mBinding.vp.setCurrentItem(1);
                break;
            case R.id.fl_items:
                if (mBinding.vp != null) {
                    mBinding.llMenu.setVisibility(View.VISIBLE);

                }
                mBinding.ivAdd.setVisibility(View.GONE);
                mBinding.ivMap.setVisibility(View.GONE);
                mBinding.ivView.setVisibility(View.VISIBLE);
                mBinding.vp.setCurrentItem(0);
                break;
            case R.id.iv_cart:
                openCartActivity();
                break;
            case R.id.iv_add:
                if (!(DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    openAddTAgScreen();

                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, activity);
                }
                break;

            case R.id.cardcatall:
/*
                mBinding.tvSort.setText(getString(R.string.newest));
                sortedBy = "created";
                sordedOrder = "-1";*/
                isClear = true;
                categoryId = "";
                searchTitle = "All";

                mBinding.includeHeader.tvSearch.setText("Search in All Categories");
                ((HomeActivity) mActivity).setCatid("");
                /*  setupViewPager("2");*/
                currentFragmentMethodAllData();
                if(adapter1 != null) {
                    adapter1.posClicked = -2; // to make all cat rings
                    adapter1.notifyDataSetChanged();
                }
                break;
            case R.id.iv_filter:
                openFilterScreen();
                break;
            case R.id.iv_view:
                String a = PreferenceManager.getInstance(getActivity()).getfargtype();
                if (a.equals("1")) {
                    mBinding.ivView.setImageResource(R.drawable.ic_squares);
                    PreferenceManager.getInstance(getActivity()).putfargtype("2");
                    switchview("1");
                } else {
                    mBinding.ivView.setImageResource(R.drawable.ic_list);
                    PreferenceManager.getInstance(getActivity()).putfargtype("1");
                    switchview("1");
                }
                break;

            case R.id.iv_map:
                mBinding.vp.setCurrentItem(1);
                String b = PreferenceManager.getInstance(getActivity()).getfargtype();
                if (b.equals("1")) {
                    mBinding.ivView.setImageResource(R.drawable.ic_list);
//                    PreferenceManager.getInstance(getActivity()).putfargmaptype("2");

                } else {
                    mBinding.ivView.setImageResource(R.drawable.map);
//                    PreferenceManager.getInstance(getActivity()).putfargmaptype("1");
//                    setupViewPager("2");
                }
             /*   if (buttonListener != null) {
                    buttonListener.onButtonPressed("Message From First Fragment");
                }*/

                break;
            case R.id.tv_sort:
                showSortpopUP();
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
                itemSort = getString(R.string.newest);

                popup.dismiss();
                currentFragmentMethod("created", "-1", lat, lng);


                break;
            case R.id.tv_low_to_high:
                itemSort = getString(R.string.price_low_to_high);
                mBinding.tvSort.setText(getString(R.string.price_low_to_high));

                popup.dismiss();

                currentFragmentMethod("firmPrice", "1", lat, lng);

                break;
            case R.id.tv_price_high_low:
                itemSort = getString(R.string.price_high_to_low);
                mBinding.tvSort.setText(getString(R.string.price_high_to_low));

                popup.dismiss();
                currentFragmentMethod("firmPrice", "-1", lat, lng);
                break;

            /*case R.id.tv_above_fifty_members:
                tagSort = getString(R.string.above_50_members);
                mBinding.tvSort.setText(getString(R.string.above_50_members));
                hideTagCloseButton();
                tagPopup.dismiss();
                currentFragmentTagMethod(50);
                break;
            case R.id.tv_above_five_hundered_members:
                tagSort = getString(R.string.above_500_members);
                mBinding.tvSort.setText(getString(R.string.above_500_members));
                tagPopup.dismiss();
                hideTagCloseButton();
                currentFragmentTagMethod(500);
                break;
            case R.id.tv_above_hundered_members:
                tagSort = getString(R.string.above_100_members);
                mBinding.tvSort.setText(getString(R.string.above_100_members));
                tagPopup.dismiss();
                currentFragmentTagMethod(100);
                hideTagCloseButton();
                break;
            case R.id.tv_above_one_thousand_members:
                tagSort = getString(R.string.above_1000_members);
                mBinding.tvSort.setText(getString(R.string.above_1000_members));
                tagPopup.dismiss();
                hideTagCloseButton();
                currentFragmentTagMethod(1000);
                break;
            case R.id.tv_above_ten_members:
                tagSort = getString(R.string.above_10_members);
                mBinding.tvSort.setText(getString(R.string.above_10_members));
                tagPopup.dismiss();
                hideTagCloseButton();
                currentFragmentTagMethod(10);
                break;
            case R.id.tv_all_member:
                hideTagCloseButton();
                tagSort = getString(R.string.all_member);
                mBinding.tvSort.setText(getString(R.string.all_member));
                tagPopup.dismiss();
                currentFragmentTagMethod(0);
                break;*/
            case R.id.tv_all:
                hideTagCloseButton();
                tagSort = getString(R.string.all_tag);
                mBinding.tvSort.setText(getString(R.string.all_tag));

                tagPopup.dismiss();
                currentFragmentTagMethod("");
                break;
            case R.id.tv_Apartment:
                hideTagCloseButton();
                tagSort = getString(R.string.apartments);
                mBinding.tvSort.setText(getString(R.string.apartments));

                tagPopup.dismiss();
                currentFragmentTagMethod("1");
                break;
            case R.id.tv_Universities:
                hideTagCloseButton();
                tagSort = getString(R.string.universities);
                mBinding.tvSort.setText(getString(R.string.universities));

                tagPopup.dismiss();
                currentFragmentTagMethod("2");
                break;
            case R.id.tv_Organization:
                hideTagCloseButton();
                tagSort = getString(R.string.organizations);
                mBinding.tvSort.setText(getString(R.string.organizations));

                tagPopup.dismiss();
                currentFragmentTagMethod("3");
                break;
            case R.id.tv_Club:
                hideTagCloseButton();
                tagSort = getString(R.string.clubs);
                mBinding.tvSort.setText(getString(R.string.clubs));

                tagPopup.dismiss();
                currentFragmentTagMethod("4");
                break;

            case R.id.tv_Other:
                hideTagCloseButton();
                tagSort = getString(R.string.other);
                mBinding.tvSort.setText(getString(R.string.other));

                tagPopup.dismiss();
                currentFragmentTagMethod("5");
                break;
            case R.id.tv_my_tag:
                hideTagCloseButton();
                tagSort = getString(R.string.my_tag);
                mBinding.tvSort.setText(getString(R.string.my_tag));

                tagPopup.dismiss();
                currentFragmentTagMethod(DataManager.getInstance().getUserDetails().getUserId());
                break;
            case R.id.iv_close:
                if (mBinding.includeHeader.tvSearch.getText().toString().length() > 0) {
                    hideTagCloseButton();
                    mBinding.includeHeader.tvSearch.setText("");
                    mBinding.includeHeader.tvSearch.setHint("");
                    currentFragmentTagMethod("");
                }
                break;
            case R.id.iv_gift:
                openGiftActivity();
                break;

        }
    }

    private void openAddTAgScreen() {
        Intent intent = new Intent(mActivity, AddTagActivity.class);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.TAG_CREATED);
    }

    private void openGiftActivity() {
        Intent intent = new Intent(mActivity, GiftRewardPromotionActivity.class);
        startActivity(intent);
    }

    private void openCartActivity() {
        Intent intent = new Intent(mActivity, CartActivity.class);
        startActivity(intent);
    }

    private void hideTagCloseButton() {
        mBinding.includeHeader.ivClose.setVisibility(View.VISIBLE);
        mBinding.includeHeader.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_search));
        tagSearch = "";
    }

    private void showSortpopUP() {
        if (mBinding.vp != null) {
            Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
            if (presentFragment != null && mBinding.vp != null && mBinding.vp.getCurrentItem() == 0) {
                showsortingPopUp();
            } else {
                showTagsortingPopUp();
            }
        }
    }

    private void checkGps() {
        if (gpsTracker == null)
            gpsTracker = new GPSTracker(mActivity);
        if (gpsTracker.isGPSEnable()) {
            if (gpsTracker.getLocation() != null) {
                itemSort = getString(R.string.closest);
                mBinding.tvSort.setText(getString(R.string.closest));
                currentFragmentMethod("", "", gpsTracker.getLocation().getLatitude(), gpsTracker.getLongitude());
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    /*
     * This funciton is use for open Category Screen
     * */
    private void openCategory() {
        Intent intent = new Intent(mActivity, CategoryActivity.class);
        startActivityForResult(intent, 100);
    }

    /*
     * This function is use for open Search Screen for Tag and Products*/
    private void openSearchScreen() {
        if (mBinding.vp != null) {
            Intent intent = new Intent(mActivity, SearchAcivity.class);
            if (mBinding.vp.getCurrentItem() == 0) {
                intent.putExtra("IS_FROM", 0);
                intent.putExtra("CATEGORY", categoryId);
                intent.putExtra("SEARCH_TITTLE", searchTitle);
                intent.putExtra("FILTER_DATA", parms);

            } else {
                intent.putExtra("IS_FROM", 1);
                intent.putExtra("CATEGORY", categoryId);
                intent.putExtra("SEARCH_TITTLE", searchTitle);
                intent.putExtra("FILTER_DATA", tagFilterParms);
            }
            startActivityForResult(intent, AppConstants.REQUEST_CODE.TAG_SEACHING_RESULT);
        }
    }

    /*
     * This function is use to open*/
    private void openSearchResult() {
        Intent intent = new Intent(mActivity, SearchAcivity.class);
        intent.putExtra("IS_FROM", 3);
        intent.putExtra("CATEGORY", categoryId);
        intent.putExtra("SEARCH_TITTLE", searchTitle);
        intent.putExtra("FILTER_DATA", tagFilterParms);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.CLEAR_CATEGORY);
    }

    private void openFilterScreen() {
        if (mBinding.vp != null) {
            Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
            if (presentFragment != null && mBinding.vp.getCurrentItem() == 0) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mActivity, FilterActivity.class);
                        intent.putExtra("FILTER_DATA", parms);
                        intent.putExtra("LOCATION", location);
                        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.FILTER);
                    }
                });
            } else {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(mActivity, TagFilterActivity.class);
                        intent.putExtra("FILTER_DATA", tagFilterParms);
                        intent.putExtra("LOCATION", tagLocation);
                        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.TAG_FILTER);
                    }
                });
            }
        }
    }

    @Override
    public void onRefresh() {
        String search = "";
        Log.d("vihsalsklfdh", categoryId + "qwerty" + searchTitle);
        HomeViewModel homeViewModel = new HomeViewModel();
        if ((sortedBy != null && sortedBy.length() > 0) || (sordedOrder != null && sordedOrder.length() > 0) || (lat > 0 && lng > 0) || (parms != null && parms.size() > 0)) {
            homeViewModel.getProductFilterList(parms, lat, lng, sortedBy, sordedOrder, 1, limit, true, true, categoryId);
        } else {
            if (gpsTracker != null)
                homeViewModel.getProductList(search, 1, limit, true, true, categoryId, false, gpsTracker.getLatitude(), gpsTracker.getLongitude());
            else
                homeViewModel.getProductList(search, 1, limit, true, true, categoryId, false, 0.0, 0.0);

        }
    }

    private void updteList() {
        String search = "";
        HomeViewModel homeViewModel = new HomeViewModel();
        if ((sortedBy != null && sortedBy.length() > 0) || (sordedOrder != null && sordedOrder.length() > 0) || (lat > 0 && lng > 0) || (parms != null && parms.size() > 0)) {
            homeViewModel.getProductFilterList(parms, lat, lng, sortedBy, sordedOrder, 1, limit, true, true, categoryId);
        } else {
            if (gpsTracker != null)
                homeViewModel.getProductList(search, 1, limit, true, true, categoryId, false, gpsTracker.getLatitude(), gpsTracker.getLongitude());
            else
                homeViewModel.getProductList(search, 1, limit, true, true, categoryId, false, 0.0, 0.0);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    isClear = true;
                    categoryId = data.getExtras().getString("CATEGORY_ID");
                    searchTitle = data.getExtras().getString("TITLE");
                    openSearchResult();

                }
                break;
            case AppConstants.ACTIVITY_RESULT.FILTER:
                if (resultCode == Activity.RESULT_OK) {
                    isFilter = true;
                    if (data != null && data.getExtras() != null && data.getExtras().containsKey("FILTER_DATA")) {
                        parms = (HashMap<String, Object>) data.getExtras().get("FILTER_DATA");
                        location = (Address) data.getExtras().get("LOCATION");
                        ((HomeActivity) mActivity).setProductFilterParms(parms);

                    } else {
                        if (parms != null) {
                            parms.clear();
                            FilterManager.getInstance().setmFilterMap(parms);
                            ((HomeActivity) mActivity).setProductFilterParms(parms);
                            mBinding.tvSort.setText(getString(R.string.newest));
                            location = null;
                            categoryId = null;

                        }
                    }
                }
                break;
            case AppConstants.ACTIVITY_RESULT.TAG_FILTER:

                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.getExtras() != null && data.getExtras().containsKey("FILTER_DATA")) {
                        try {
                            tagFilterParms = (HashMap<String, Object>) data.getExtras().get("FILTER_DATA");
                            tagLocation = (Address) data.getExtras().get("TAG_LOCATION");
                            ((HomeActivity) mActivity).setTagFilterParms(tagFilterParms);
                            hideTagCloseButton();
                        } catch (Exception e) {
                        }
                    } else {
                        if (tagFilterParms != null) {
                            tagFilterParms.clear();
                            ((HomeActivity) mActivity).setTagFilterParms(tagFilterParms);
                            tagLocation = null;
                        }
                    }
                }
                break;
            case AppConstants.ACTIVITY_RESULT.CLEAR_CATEGORY:
                if (resultCode == Activity.RESULT_OK) {
                    categoryId = "";
                    searchTitle = "";
                    setSortingTitle();
                }
                break;
            case AppConstants.REQUEST_CODE.TAG_SEACHING_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
                    if (presentFragment != null && mBinding.vp != null && mBinding.vp.getCurrentItem() == 1) {
                        if (data != null && data.getExtras() != null && data.getExtras().containsKey("TAG")) {
                            TagData tagData = (TagData) data.getExtras().get("TAG");
                            tagSearch = tagData.getTagName();
                            mBinding.includeHeader.tvSearch.setText(tagSearch);
                            mBinding.includeHeader.ivClose.setVisibility(View.VISIBLE);
                            if (tagSearch != null && tagSearch.length() > 0)
                                mBinding.includeHeader.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_gallery_close));
                            else
                                mBinding.includeHeader.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_search));

                        } else if (data != null && data.getExtras() != null && data.getExtras().containsKey("SEARCH_KEY")) {
                            String search = data.getExtras().getString("SEARCH_KEY");
                            mBinding.includeHeader.tvSearch.setText(search);
                            if (search != null && search.length() > 0)
                                mBinding.includeHeader.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_gallery_close));
                            else
                                mBinding.includeHeader.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_search));

                        }
                    } else if (presentFragment != null && mBinding.vp != null && mBinding.vp.getCurrentItem() == 0) {
                        if (data != null && data.getExtras() != null && data.getExtras().containsKey("FILTER_DATA")) {
                            parms = (HashMap<String, Object>) data.getExtras().get("FILTER_DATA");
                            setSortData();
                        }
                    }
                }
                break;
            case AppConstants.ACTIVITY_RESULT.PRODUCT_DETAILS:
                if (resultCode == Activity.RESULT_OK) {
                    callProductListApi();
                }
                break;
        }
        Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
        if (mBinding.vp != null) {
            if (presentFragment != null && mBinding.vp.getCurrentItem() == 0) {
                presentFragment.onActivityResult(requestCode, resultCode, data);
            } else {
                presentFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void setSortingTitle() {

        switch (DataManager.getInstance().getSortBy()) {
            case "":
                mBinding.tvSort.setText(getString(R.string.closest));
                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {
                    checkGps();
                }
                break;
            case "created":
                mBinding.tvSort.setText(getString(R.string.newest));
                currentFragmentMethod("created", "-1", lat, lng);

                break;
            case "firmPrice":
                if (DataManager.getInstance().getSortorder().equalsIgnoreCase("-1")) {
                    mBinding.tvSort.setText(getString(R.string.price_high_to_low));
                    currentFragmentMethod("firmPrice", "-1", lat, lng);
                } else {
                    mBinding.tvSort.setText(getString(R.string.price_low_to_high));
                    currentFragmentMethod("firmPrice", "1", lat, lng);
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d("offset", "" + positionOffset);

//        if (position == 1) {
//            if (mBinding.ivBackground.getVisibility() == View.VISIBLE) {
//                TranslateAnimation animate = new TranslateAnimation(
//                        -100,                 // fromXDelta
//                        0,                 // toXDelta
//                        0,  // fromYDelta
//                        0);                // toYDelta
//                animate.setDuration(00);
//                mBinding.ivBackground.startAnimation(animate);
//                mBinding.ivTagBackground.setVisibility(View.VISIBLE);
//                mBinding.ivBackground.setAlpha(positionOffset);
//                mBinding.ivTagBackground.setAlpha(1 - positionOffset);
//                if (tagSearch.length() > 0) {
//                    mBinding.includeHeader.ivClose.setVisibility(View.VISIBLE);
//                } else {
//                    mBinding.includeHeader.ivClose.setVisibility(View.GONE);
//                }
//            }
//        } else {
//
//            mBinding.ivBackground.setAlpha(1 - positionOffset);
//            mBinding.ivTagBackground.setAlpha(positionOffset);
//        }
        switch (position) {
            case 0:
                mBinding.llMenu.setVisibility(View.VISIBLE);
                mBinding.viewBackground.setTranslationX(originalBackgroundTranslationX + positionOffsetPixels / 2.25f);
               /* mBinding.tvItems.setTextColor((Integer) argbEvaluator.evaluate(positionOffset,
                        getResources().getColor(R.color.Black),
                        getResources().getColor(R.color.Black)));
                mBinding.tvTags.setTextColor((Integer) argbEvaluator.evaluate(positionOffset,
                        getResources().getColor(R.color.White),
                        getResources().getColor(R.color.White)));*/
//                mBinding.includeHeader.ivClose.setVisibility(View.GONE);
                mBinding.ivView.setVisibility(View.VISIBLE);
                mBinding.ivDown.setVisibility(View.GONE);

                if (FilterManager.getInstance().getmFilterMap() != null && FilterManager.getInstance().getmFilterMap().size() > 0)
                    setSortData();
                else
                    mBinding.tvSort.setText(itemSort);
                mBinding.includeHeader.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_search));
                break;
            case 1:
                mBinding.llMenu.setVisibility(View.GONE);
                mBinding.ivView.setVisibility(View.GONE);
                mBinding.appbarContainer.setExpanded(true, true);
                mBinding.tvSort.setText(tagSort);
                mBinding.ivDown.setVisibility(View.VISIBLE);
                if (tagSearch.length() > 0) {
                    mBinding.includeHeader.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_gallery_close));
                    mBinding.includeHeader.ivClose.setVisibility(View.VISIBLE);
                } else {
                    mBinding.includeHeader.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_search));
                }
        }
    }

    private void setToggle(int gone, int visible, int p, int p2, int p3, int p4, String
            sortText, String tagSearch) {
        Drawable img = getContext().getResources().getDrawable(p2);
        Drawable img1 = getContext().getResources().getDrawable(p4);

        mBinding.tvSort.setText(sortText);
        mBinding.includeHeader.ivCategory.setVisibility(View.GONE);
        mBinding.tvItems.setTextColor(mActivity.getResources().getColor(p));

        mBinding.tvTags.setTextColor(mActivity.getResources().getColor(p3));
        mBinding.tvItems.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
        mBinding.tvTags.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        mBinding.includeHeader.tvSearch.setText(tagSearch);
        mBinding.includeHeader.ivClose.setOnClickListener(this);
        mBinding.includeHeader.tvSearch.setText(tagSearch);

    }


    private void hideview() {
        if (mBinding != null) {
            mBinding.appbarContainer.setExpanded(true, true);
        }
    }

    @Override
    public void onPageSelected(int position) {

        if (position == 1) {
            {

//                mBinding.appbarContainer.setExpanded(true, true);
//                mBinding.appbarContainer.setExpanded(false);
                setToggle(View.VISIBLE, View.VISIBLE, R.color.White, R.drawable.placeholder, R.color.Black, R.drawable.ic_items_active, getString(R.string.all_member), "Search For Tags");
            }
        } else {

            setToggle(View.VISIBLE, View.VISIBLE, R.color.Black, R.drawable.ic_tags_active, R.color.White, R.drawable.squares, getString(R.string.newest), "Search in " + searchTitle);
        }

    }

    @Override
    public void onPageScrollStateChanged(int i) {


        if (i == 1) {

        } else {

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

    private void showTagsortingPopUp() {

        LayoutTagTypeSortBinding popBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_tag_type_sort, null, false);
        tagPopup = new PopupWindow(mActivity);
        tagPopup.setContentView(popBinding.getRoot());
        tagPopup.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        tagPopup.setFocusable(true);
        tagPopup.setOutsideTouchable(true);
        popBinding.tvAll.setVisibility(View.VISIBLE);
        /*popBinding.tvAllMember.setOnClickListener(this);
        popBinding.tvAboveTenMembers.setOnClickListener(this);
        popBinding.tvAboveFiftyMembers.setOnClickListener(this);
        popBinding.tvAboveHunderedMembers.setOnClickListener(this);
        popBinding.tvAboveFiveHunderedMembers.setOnClickListener(this);
        popBinding.tvAboveOneThousandMembers.setOnClickListener(this);*/

        popBinding.tvAll.setOnClickListener(this);
        popBinding.tvApartment.setOnClickListener(this);
        popBinding.tvUniversities.setOnClickListener(this);
        popBinding.tvOrganization.setOnClickListener(this);
        popBinding.tvClub.setOnClickListener(this);
        popBinding.tvOther.setOnClickListener(this);
        popBinding.tvMyTag.setOnClickListener(this);
        //popBinding.tvOther.setVisibility(View.GONE);


        mBinding.includeHeader.ivClose.setOnClickListener(this);
        tagPopup.showAsDropDown(mBinding.tvSort);
    }

    private void currentFragmentMethod(String sortedBy, String sordedOrder, double latitude,
                                       double longitude) {
        if (mBinding.vp != null) {
            Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
            if (presentFragment != null && mBinding.vp.getCurrentItem() == 0) {
                if (parms == null) {
                    parms = new HashMap<>();
                }
                parms.put(AppConstants.KEY_CONSTENT.SORD_BY, sortedBy);
                parms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, sordedOrder);
                ((ProductListFragment) presentFragment).hitSortApi(sortedBy, sordedOrder, latitude, longitude, categoryId);

            }
        }
    }

    private void switchview(String sortedBy) {
        if (mBinding.vp != null) {
            Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
            if (presentFragment != null && mBinding.vp.getCurrentItem() == 0) {

                ((ProductListFragment) presentFragment).chnageview(sortedBy);

            }
        }
    }


    private void currentFragmentMethodAllData() {
        if (mBinding.vp != null) {
            Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
            if (presentFragment != null && mBinding.vp.getCurrentItem() == 0) {
                if (parms == null) {
                    parms = new HashMap<>();
                }
                parms.put(AppConstants.KEY_CONSTENT.SORD_BY, sortedBy);
                parms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, sordedOrder);
                ((ProductListFragment) presentFragment).hitallApi();

            }
        }
    }


    private void currentFragmentMethod2(String sortedBy, String id, String sordedOrder, double latitude,
                                        double longitude) {
        if (mBinding.vp != null) {
            Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
            if (presentFragment != null && mBinding.vp.getCurrentItem() == 0) {
                if (parms == null) {
                    parms = new HashMap<>();
                }
                parms.put(AppConstants.KEY_CONSTENT.SORD_BY, sortedBy);
                parms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, sordedOrder);
                ((ProductListFragment) presentFragment).hitSortApi1(sortedBy, id, sordedOrder, latitude, longitude);

            }
        }
    }

    private void currentFragmentTagMethod(String tagType) {
        if (mBinding.vp != null) {
            Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
            if (presentFragment != null && mBinding.vp.getCurrentItem() == 1) {
                ((TagListingFragment) presentFragment).sortTagData(tagType);
            }
        }
    }

    public void updateDeviceToken(RatingData data) {
        Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
        if (presentFragment != null && mBinding.vp != null && mBinding.vp.getCurrentItem() == 0) {
            ((ProductListFragment) presentFragment).showRatingDialog(data);
        }
    }

    public void callProductListApi() {
        try {
            Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
            if (presentFragment != null && mBinding.vp != null && mBinding.vp.getCurrentItem() == 0) {
                ((ProductListFragment) presentFragment).hitSortApi();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callProductListApi1() {
        try {
            Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vp.getCurrentItem());
            if (presentFragment != null && mBinding.vp != null && mBinding.vp.getCurrentItem() == 0) {
                searchTitle = "All";

                mBinding.includeHeader.tvSearch.setText("Search in All Categories");
                ((ProductListFragment) presentFragment).hitSortApi2();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void setSortData() {
        if (parms != null) {
            if (parms.containsKey(AppConstants.KEY_CONSTENT.SORD_BY)) {
                if (parms.get(AppConstants.KEY_CONSTENT.SORD_BY).toString().equalsIgnoreCase("created")) {
                    mBinding.tvSort.setText(getString(R.string.newest));
                } else if (parms.get(AppConstants.KEY_CONSTENT.SORD_BY).toString().equalsIgnoreCase("firmPrice") && parms.get(AppConstants.KEY_CONSTENT.SORT_ORDER).equals("1")) {
                    mBinding.tvSort.setText(getString(R.string.price_low_to_high));
                } else if (parms.get(AppConstants.KEY_CONSTENT.SORD_BY).toString().equalsIgnoreCase("firmPrice") && parms.get(AppConstants.KEY_CONSTENT.SORT_ORDER).equals("-1")) {
                    mBinding.tvSort.setText(getString(R.string.price_high_to_low));
                } else
                    mBinding.tvSort.setText(getString(R.string.closest));
            }
        }

    }

    public void updateProductList() {
        Fragment presentFragment = viewPagerAdapter.getItem(0);
        if (presentFragment != null) {
            ((ProductListFragment) presentFragment).updateListByFilter();
            if (FilterManager.getInstance().getmFilterMap() != null && FilterManager.getInstance().getmFilterMap().containsKey(AppConstants.KEY_CONSTENT.SORD_BY)) {
                parms = FilterManager.getInstance().getmFilterMap();
            }
        }
    }

    public interface SendMessage {
        void sendData(String message);
    }

    /**
     * This interface is used to interact with the host {@link HomeActivity}
     */
    public interface IHomeHost {

        void openChangePasswordFragment();

        void logOutSuccess();

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
            return new CategoryListViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
            CategoryListViewHolder holder = null;
            holder = (CategoryListViewHolder) viewHolder;
            final Bitmap[] bitmapImage = {null};
            try {
                Picasso.get()
                        .load(mCategoryList.get(position).getImageUrl())
                        .error(R.mipmap.ic_launcher)
                        .into(new Target() {

                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                ((CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setImageBitmap(bitmap);
                                bitmapImage[0] = bitmap;

                                if(posClicked == position || posClicked == -2 ){
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

                                                        ((CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setBackgroundDrawable(gradient);
                                                    }
                                                });

                                    //((HomeCategoryListAdapter.CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setSelected(false);
                                    ((CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setAlpha(1f);
                                }else{
                                    ((CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setBackgroundDrawable(null);
                                    ((CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setAlpha(.6f);
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

            ((CategoryListViewHolder) viewHolder).viewBinding.tvCategoryName.setText(mCategoryList.get(position).getName());

            if (position == mCategoryList.size() - 1) {
                ((CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setImageResource(R.drawable.moreicn);
                ((CategoryListViewHolder) viewHolder).viewBinding.ivCategoryImage.setBackgroundDrawable(null);
            }


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
                        /*Intent intent = new Intent();
                        intent.putExtra("CATEGORY_ID", mCategoryList.get(getAdapterPosition()).getId());
                        intent.putExtra("TITLE", mCategoryList.get(getAdapterPosition()).getName());
                        ((HomeActivity) context).setResult(Activity.RESULT_OK, intent);*/
                        if (mCategoryList.get(getAdapterPosition()).getName().equals("more")) {
                            posClicked = -1;
                            openCategory();
                        } else {
                            isClear = true;
                            categoryId = mCategoryList.get(getAdapterPosition()).getId();
                            searchTitle = mCategoryList.get(getAdapterPosition()).getName();
                            ((HomeActivity) mActivity).setCatid(categoryId);
//                        openSearchResult();
                            mBinding.includeHeader.tvSearch.setText("Search in " + searchTitle);
                            Log.d("vihsalsklfdh", categoryId + "qwerty" + searchTitle);
//                            setupViewPager("2");

                            currentFragmentMethod2("created", categoryId, "-1", lat, lng);
                      /*  String search = "";
                        HomeViewModel homeViewModel = new HomeViewModel();
                        if ((sortedBy != null && sortedBy.length() > 0) || (sordedOrder != null && sordedOrder.length() > 0) || (lat > 0 && lng > 0) || (parms != null && parms.size() > 0)) {
                            homeViewModel.getProductFilterList(parms, lat, lng, sortedBy, sordedOrder, 1, limit, true, true, categoryId);
                            viewPagerAdapter.notifyDataSetChanged();
                        } else {
                            if (gpsTracker != null) {
                                homeViewModel.getProductList(search, 1, limit, true, true, categoryId, false, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                                viewPagerAdapter.notifyDataSetChanged();
                            } else {
                                homeViewModel.getProductList(search, 1, limit, true, true, categoryId, false, 0.0, 0.0);
                                viewPagerAdapter.notifyDataSetChanged();
                            }
*/
                            posClicked = getAdapterPosition();
                        }
                        notifyDataSetChanged();

                    }
                });
            }

        }
    }
}
