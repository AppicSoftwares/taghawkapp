package com.taghawk.ui.home;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.Query;
import com.taghawk.R;
import com.taghawk.Repository.CartRepo;
import com.taghawk.Repository.HomeRepo;
import com.taghawk.Repository.NotificationRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.CommonDataModel;
import com.taghawk.model.ContentViewModel;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.VendorIdResponse;
import com.taghawk.model.cashout.MerchantDetailBeans;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.DeleteProductRequest;
import com.taghawk.model.home.LikeUnLike;
import com.taghawk.model.home.ProductDetailsModel;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.model.profileresponse.BalanceResponse;
import com.taghawk.model.profileresponse.ProfileProductsResponse;
import com.taghawk.model.profileresponse.UserDetail;
import com.taghawk.model.request.User;
import com.taghawk.model.strip.CreateMercentResponse;
import com.taghawk.model.strip.GetBankDetail;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tag.TagDetailsModel;
import com.taghawk.model.tag.TagModel;
import com.taghawk.model.tag.TagSearchBean;
import com.taghawk.model.update_rating_notification.UpdateRatingNotificationBean;
import com.taghawk.ui.chat.ChatMessagesRepo;
import com.taghawk.util.AppUtils;
import com.taghawk.util.FilterManager;
import com.taghawk.util.ResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;

import siftscience.android.Sift;

public class HomeViewModel extends ViewModel {

    private Observer<Throwable> mErrorObserver;
    private MutableLiveData<Integer> mActionLiveData;
    private Observer<FailureResponse> mFailureObserver;

    private RichMediatorLiveData<ProductListingModel> mProductListing;
    private RichMediatorLiveData<ProductDetailsModel> mProductDetails;
    private RichMediatorLiveData<UserDetail> mUpdatedUserDetail;
    private RichMediatorLiveData<CommonResponse> mlogOut, mFeedBackLiveData;
    private RichMediatorLiveData<TagModel> mTagList;
    private RichMediatorLiveData<LikeUnLike> mLikeUnLike;
    private RichMediatorLiveData<CommonResponse> mCartViewModel;
    private RichMediatorLiveData<TagDetailsModel> mAccpetRejectTagRequest;
    private Observer<Boolean> loading;
    private RichMediatorLiveData<TagSearchBean> mTagSearchViewModel;
    private RichMediatorLiveData<UpdateRatingNotificationBean> mUpdateDeviceTokenLiveData;
    private RichMediatorLiveData<CreateMercentResponse> mCreateMerchentLiveData;
    private RichMediatorLiveData<GetBankDetail> mgetBankDetailsViewModel;
    private RichMediatorLiveData<ContentViewModel> mHtmlContenLiveData;
    private RichMediatorLiveData<ProfileProductsResponse> mProfileProductsLiveData;
    private RichMediatorLiveData<BalanceResponse> mCashOutLiveData;
    private RichMediatorLiveData<BalanceResponse> mBalanceLiveData;
    private RichMediatorLiveData<MerchantDetailBeans> merchantDetailsLiveData;
    private RichMediatorLiveData<MerchantDetailBeans> merchantDetailsLiveDataCopy;
    private RichMediatorLiveData<CommonDataModel> mCommonLiveData;
    private RichMediatorLiveData<VendorIdResponse> mGetVendorIdLivedata;
    //Initializing repository class
    private HomeRepo mHomeRepo = new HomeRepo();
    private CartRepo cartRepo = new CartRepo();
    private NotificationRepo notificationRepo = new NotificationRepo();

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    public MutableLiveData<Integer> getActionLiveData() {
        return mActionLiveData;
    }

    public void setActionData(int actionData) {
        mActionLiveData.setValue(actionData);
    }

    public MutableLiveData<UserDetail> getUpdatedProfileLiveData() {
        return mUpdatedUserDetail;
    }

    public void setUpdatedProfileData(UserDetail actionData) {
        mUpdatedUserDetail.setValue(actionData);
    }

    /**
     * Method is used to initialize live data objects
     */
    private void initLiveData() {
        if (mActionLiveData == null) {
            mActionLiveData = new MutableLiveData<>();
        }


        if (mUpdatedUserDetail == null) {
            mUpdatedUserDetail = new RichMediatorLiveData<UserDetail>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mProductListing == null) {
            mProductListing = new RichMediatorLiveData<ProductListingModel>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mProductDetails == null) {
            mProductDetails = new RichMediatorLiveData<ProductDetailsModel>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mLikeUnLike == null) {
            mLikeUnLike = new RichMediatorLiveData<LikeUnLike>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mTagList == null) {
            mTagList = new RichMediatorLiveData<TagModel>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mlogOut == null) {
            mlogOut = new RichMediatorLiveData<CommonResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mCartViewModel == null) {
            mCartViewModel = new RichMediatorLiveData<CommonResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mTagSearchViewModel == null) {
            mTagSearchViewModel = new RichMediatorLiveData<TagSearchBean>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }

        if (mUpdateDeviceTokenLiveData == null) {
            mUpdateDeviceTokenLiveData = new RichMediatorLiveData<UpdateRatingNotificationBean>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mFeedBackLiveData == null) {
            mFeedBackLiveData = new RichMediatorLiveData<CommonResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mCreateMerchentLiveData == null) {
            mCreateMerchentLiveData = new RichMediatorLiveData<CreateMercentResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }

            };
        }
        if (mgetBankDetailsViewModel == null) {
            mgetBankDetailsViewModel = new RichMediatorLiveData<GetBankDetail>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mHtmlContenLiveData == null) {
            mHtmlContenLiveData = new RichMediatorLiveData<ContentViewModel>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mAccpetRejectTagRequest == null) {
            mAccpetRejectTagRequest = new RichMediatorLiveData<TagDetailsModel>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }

        if (mProfileProductsLiveData == null) {
            mProfileProductsLiveData = new RichMediatorLiveData<ProfileProductsResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mCashOutLiveData == null) {
            mCashOutLiveData = new RichMediatorLiveData<BalanceResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mBalanceLiveData == null) {
            mBalanceLiveData = new RichMediatorLiveData<BalanceResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (merchantDetailsLiveData == null) {
            merchantDetailsLiveData = new RichMediatorLiveData<MerchantDetailBeans>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (merchantDetailsLiveDataCopy == null) {
            merchantDetailsLiveDataCopy = new RichMediatorLiveData<MerchantDetailBeans>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mCommonLiveData == null) {
            mCommonLiveData = new RichMediatorLiveData<CommonDataModel>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mGetVendorIdLivedata == null) {
            mGetVendorIdLivedata = new RichMediatorLiveData<VendorIdResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
    }

    public void getProductList(String search, int pageNumber, int limit, Boolean isRefresing, Boolean isProductSCreen, String category, boolean isReset, double lat, double lng) {

        HashMap<String, Object> parms = new HashMap();
        if (!isRefresing && isProductSCreen)
            loading.onChanged(true);
        if (FilterManager.getInstance().getmFilterMap() != null) {
            parms = FilterManager.getInstance().getmFilterMap();
        }
        if (search.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.SEARCH_KEY, search);
        }
        if (category != null && category.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, category);
        }
        if (!isReset) {
            parms.put(AppConstants.KEY_CONSTENT.SORD_BY, "created");
            parms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, -1);
        }
        if (lat != 0 && lng != 0) {
            parms.put(AppConstants.KEY_CONSTENT.LAT, lat);
            parms.put(AppConstants.KEY_CONSTENT.LONGI, lng);
        }
        if (pageNumber == 1) {
            parms.remove("promotedProductIds");
        }
        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageNumber);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mHomeRepo.getProductList(mProductListing, parms);
    }

    public void getProductListPagination(ArrayList<ProductListModel> mList, String search, int pageNumber, int limit, Boolean isRefresing, Boolean isProductSCreen, String category, boolean isReset, double lat, double lng) {

        HashMap<String, Object> parms = new HashMap();
        if (!isRefresing && isProductSCreen)
            loading.onChanged(true);
        if (FilterManager.getInstance().getmFilterMap() != null) {
            parms = FilterManager.getInstance().getmFilterMap();
        }
        if (search.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.SEARCH_KEY, search);
        }
        if (category != null && category.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, category);
        }
        if (!isReset) {
            parms.put(AppConstants.KEY_CONSTENT.SORD_BY, "created");
            parms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, -1);
        }
        if (lat != 0 && lng != 0) {
            parms.put(AppConstants.KEY_CONSTENT.LAT, lat);
            parms.put(AppConstants.KEY_CONSTENT.LONGI, lng);

        }
        if (pageNumber <= 4) {
            if (mList != null && mList.size() > 0) {
                if (getPromotedProductIds(mList).trim().length() > 0)
                    parms.put("promotedProductIds", getPromotedProductIds(mList));
            }
        }
        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageNumber);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mHomeRepo.getProductList(mProductListing, parms);
    }

    public void getProductFilterListPagination(ArrayList<ProductListModel> mList, HashMap<String, Object> parms, Double lat, Double lng, String sortedBy, String sortedOrder, int pageNumber, int limit, boolean isRefreshing, boolean isProductScreen, String categoryId) {
//        loading.onChanged(true);
        if (!isRefreshing && isProductScreen)
            loading.onChanged(true);
        if (parms == null) {
            parms = new HashMap<>();
        }
        if (FilterManager.getInstance().getmFilterMap() != null) {
            parms = FilterManager.getInstance().getmFilterMap();
        }
        if (categoryId != null && categoryId.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, categoryId);
        }
        if (categoryId != null && categoryId.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, categoryId);
        }
        if (lat != 0 && lng != 0) {
            parms.put(AppConstants.KEY_CONSTENT.LAT, lat);
            parms.put(AppConstants.KEY_CONSTENT.LONGI, lng);
            if (sortedBy.length() == 0) {
                parms.remove(AppConstants.KEY_CONSTENT.SORD_BY);
                parms.remove(AppConstants.KEY_CONSTENT.SORT_ORDER);
            }
        }
        if (sortedBy != null && sortedBy.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.SORD_BY, sortedBy);
            parms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, sortedOrder);
        }
        if (pageNumber <= 4) {
            if (mList != null && mList.size() > 0) {
                if (getPromotedProductIds(mList).trim().length() > 0)
                    parms.put("promotedProductIds", getPromotedProductIds(mList));
            }
        }
        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageNumber);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mHomeRepo.getProductList(mProductListing, parms);
    }


    private String getPromotedProductIds(ArrayList<ProductListModel> mList) {
        String str = "";
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getPromoted()) {
                if (str.trim().length() == 0)
                    str = mList.get(i).get_id();
                else {
                    str = str + "," + mList.get(i).get_id();
                }
            }
        }

        return str;
    }

    public void getProductFilterList(HashMap<String, Object> parms, Double lat, Double lng, String sortedBy, String sortedOrder, int pageNumber, int limit, boolean isRefreshing, boolean isProductScreen, String categoryId) {
//        loading.onChanged(true);
        if (!isRefreshing && isProductScreen)
            loading.onChanged(true);
        if (parms == null) {
            parms = new HashMap<>();
        }

        if (FilterManager.getInstance().getmFilterMap() != null) {
            parms = FilterManager.getInstance().getmFilterMap();
        } else {
            parms.clear();
        }
        if (parms.containsKey(AppConstants.KEY_CONSTENT.SEARCH_KEY)) {
            parms.remove((AppConstants.KEY_CONSTENT.SEARCH_KEY));
        }
        if (categoryId == null && categoryId.length() < 0) {
            parms.remove(AppConstants.KEY_CONSTENT.CATEGORY_ID);
        }
        if (categoryId != null && categoryId.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, categoryId);
        }
        if (lat != 0 && lng != 0) {
            parms.put(AppConstants.KEY_CONSTENT.LAT, lat);
            parms.put(AppConstants.KEY_CONSTENT.LONGI, lng);
            if (sortedBy.length() == 0) {
                parms.remove(AppConstants.KEY_CONSTENT.SORD_BY);
                parms.remove(AppConstants.KEY_CONSTENT.SORT_ORDER);
            }
        }
        if (sortedBy != null && sortedBy.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.SORD_BY, sortedBy);
            parms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, sortedOrder);
        }
        if (pageNumber == 1) {
            parms.remove("promotedProductIds");
        }
        if (categoryId.isEmpty()){
            parms.remove(AppConstants.KEY_CONSTENT.CATEGORY_ID);
        }else {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, categoryId);
        }

        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageNumber);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mHomeRepo.getProductList(mProductListing, parms);
    }


    public void getProductSearchFilterList(String search, HashMap<String, Object> parms, Double lat, Double lng, String sortedBy, String sortedOrder, int pageNumber, int limit, boolean isRefreshing, boolean isProductScreen, String categoryId) {
//        loading.onChanged(true);
        if (!isRefreshing && isProductScreen)
            loading.onChanged(true);
        if (parms == null) {
            parms = new HashMap<>();
        }
        if (FilterManager.getInstance().getmFilterMap() != null) {
            parms = FilterManager.getInstance().getmFilterMap();
        } else {
            parms.clear();
        }
        if (search.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.SEARCH_KEY, search);
        }
        if (categoryId != null && categoryId.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, categoryId);
        }
        if (categoryId != null && categoryId.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, categoryId);
        }
        if (lat != 0 && lng != 0) {
            parms.put(AppConstants.KEY_CONSTENT.LAT, lat);
            parms.put(AppConstants.KEY_CONSTENT.LONGI, lng);
        }

        if (sortedBy != null && sortedBy.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.SORD_BY, sortedBy);
            parms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, sortedOrder);
        }
        if (sortedBy != null && sortedBy.equalsIgnoreCase("closest")) {
            if (parms.containsKey(AppConstants.KEY_CONSTENT.SORD_BY)) {
                parms.remove(AppConstants.KEY_CONSTENT.SORD_BY);
                parms.remove(AppConstants.KEY_CONSTENT.SORT_ORDER);
            }
        }

        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageNumber);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mHomeRepo.getProductList(mProductListing, parms);
    }


    public void getTagProductList(String search, HashMap<String, Object> parms, Double lat, Double lng, String sortedBy, String sortedOrder, int pageNumber, int limit, boolean isRefreshing, boolean isProductScreen, String categoryId, String tagId) {
//        loading.onChanged(true);
        if (!isRefreshing && isProductScreen)
            loading.onChanged(true);
        if (parms == null) {
            parms = new HashMap<>();
        }

        if (search != null && search.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.SEARCH_KEY, search);
        }

        if (categoryId != null && categoryId.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, categoryId);
        }else{
            parms.remove(AppConstants.KEY_CONSTENT.CATEGORY_ID);
        }

        if (lat != 0 && lng != 0) {
            parms.put(AppConstants.KEY_CONSTENT.LAT, lat);
            parms.put(AppConstants.KEY_CONSTENT.LONGI, lng);
        }
        if (sortedBy != null && sortedBy.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.SORD_BY, sortedBy);
            parms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, sortedOrder);
        }
        parms.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagId);

        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageNumber);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mHomeRepo.getTagProducts(mProductListing, parms);
    }


    public void getTagProductListPagination(ArrayList<ProductListModel> mList, String search, HashMap<String, Object> parms, Double lat, Double lng, String sortedBy, String sortedOrder, int pageNumber, int limit, boolean isRefreshing, boolean isProductScreen, String categoryId, String tagId) {
//        loading.onChanged(true);
        if (!isRefreshing && isProductScreen)
            loading.onChanged(true);
        if (parms == null) {
            parms = new HashMap<>();
        }
        if (search != null && search.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.SEARCH_KEY, search);
        }
        if (categoryId != null && categoryId.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, categoryId);
        }
        if (categoryId != null && categoryId.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, categoryId);
        }
        if (lat != 0 && lng != 0) {
            parms.put(AppConstants.KEY_CONSTENT.LAT, lat);
            parms.put(AppConstants.KEY_CONSTENT.LONGI, lng);
        }
        if (sortedBy != null && sortedBy.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.SORD_BY, sortedBy);
            parms.put(AppConstants.KEY_CONSTENT.SORT_ORDER, sortedOrder);
        }
        if (pageNumber <= 4) {
            if (mList != null && mList.size() > 0) {
                if (getPromotedProductIds(mList).trim().length() > 0)
                    parms.put("promotedProductIds", getPromotedProductIds(mList));
            }
        }
        parms.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagId);

        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageNumber);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mHomeRepo.getTagProducts(mProductListing, parms);
    }


    public void getTagProducts(String tagId, int pageNumber, int limit, boolean isRefreshing) {

        if (!isRefreshing)
            loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagId);
        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageNumber);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mHomeRepo.getTagProducts(mProductListing, parms);
    }


    /**
     * This method gives the log out live data object to {@link HomeFragment}
     *
     * @return {@link #mProductListing}
     */
    public RichMediatorLiveData<ProductListingModel> getProductListing() {
        return mProductListing;
    }

    public RichMediatorLiveData<TagModel> getTagListing() {
        return mTagList;
    }

    public RichMediatorLiveData<ProductDetailsModel> getProductDetails() {
        return mProductDetails;
    }

    public RichMediatorLiveData<LikeUnLike> getLikeViewModel() {
        return mLikeUnLike;
    }

    public void getProductDetailData(String productId) {
        loading.onChanged(true);
        mHomeRepo.getProductDetails(mProductDetails, productId);
    }

    public void getLikeUnLike(String productId, int status, int requestCode) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
        parms.put(AppConstants.KEY_CONSTENT.STATUS, status);
        mHomeRepo.getLikeUnLike(mLikeUnLike, parms, requestCode);
    }

    public void reportProduct(String productId, String reason, int requestCode) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
        parms.put(AppConstants.KEY_CONSTENT.REASON, reason);
        mHomeRepo.reportProduct(mLikeUnLike, parms, requestCode);
    }

    public void shareProduct(String productId, int requestCode) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
        mHomeRepo.shareProduct(mLikeUnLike, parms, requestCode);

    }

    public void verifyEmail(String linkId) {

        mHomeRepo.verifyEmail(mFeedBackLiveData, linkId, 900);
    }

    public void getTagList(int requestCode, HashMap<String, Object> parms, Object tagTypeNew, Location location) {
        loading.onChanged(true);
        if (parms == null)
            parms = new HashMap<>();
        if (tagTypeNew instanceof String) {
            //AKM
            if(((String) tagTypeNew).length() == 1)
                parms.put(AppConstants.KEY_CONSTENT.TAG_TYPE_NEW, tagTypeNew);
            else if (((String) tagTypeNew).length() > 1)
                parms.put(AppConstants.KEY_CONSTENT.USER_ID, tagTypeNew);
            //parms.put(AppConstants.KEY_CONSTENT.MEMBER_SIZE, members);
        }
        if (tagTypeNew instanceof Integer) {
            //AKM
            //parms.put(AppConstants.KEY_CONSTENT.TAG_TYPE_NEW, tagTypeNew);
            //parms.put(AppConstants.KEY_CONSTENT.MEMBER_SIZE, members);
        }

        if (location != null) {
            parms.put(AppConstants.KEY_CONSTENT.LAT, location.getLatitude());
            parms.put(AppConstants.KEY_CONSTENT.LONGI, location.getLongitude());
        }
        mHomeRepo.getTagListing(mTagList, parms, requestCode);
    }

    public void hitLogOut(String deviceId) {
        if (mlogOut != null) {
            loading.onChanged(true);
            HashMap<String, Object> parms = new HashMap<>();
            parms.put(AppConstants.KEY_CONSTENT.DEVICE_ID, deviceId);
            Sift.unsetUserId();
            mHomeRepo.logout(mlogOut, parms);
        }
    }

    public void addCart(String productId, int action, int shippingAvailibity, String sellerId, int requestCode) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
        parms.put(AppConstants.KEY_CONSTENT.ACTION, action);
        parms.put(AppConstants.KEY_CONSTENT.SHIPPING_AVAILIBILITY, shippingAvailibity);
        parms.put(AppConstants.KEY_CONSTENT.SELLER_ID, sellerId);
        mHomeRepo.addProductToCart(mCartViewModel, parms, requestCode);
    }

    public void deleteProduct(String productId) {
        loading.onChanged(true);
        DeleteProductRequest deleteProductRequest = new DeleteProductRequest();
        deleteProductRequest.setProductId(productId);
        mHomeRepo.deleteProduct(mCartViewModel, deleteProductRequest, AppConstants.REQUEST_CODE.DELETE_PRODUCT);

    }

    public void deletAllCart(String productId, int action, int requestCode) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        if (productId != null && productId.length() > 0)
            parms.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
        parms.put(AppConstants.KEY_CONSTENT.ACTION, action);
        cartRepo.addProductToCart(mCartViewModel, parms, requestCode);
    }

    public void getTagSearch(HashMap<String, Object> parms, String search) {
        loading.onChanged(true);
        if (parms == null) {
            parms = new HashMap<>();
        }
        if (search.length() > 0)
            parms.put(AppConstants.KEY_CONSTENT.NAME, search);
        mHomeRepo.getTagSearch(mTagSearchViewModel, parms);
    }

    public void markNotificationRead(String notificationId) {
//        loading.onChanged(true);
        notificationRepo.markNotificationRead(mCartViewModel, notificationId, AppConstants.REQUEST_CODE.NOTIFICATION_LIST);
    }

    public void notificationOnOff(int type) {
        loading.onChanged(true);
        notificationRepo.notificationOnOff(mFeedBackLiveData, type);
    }

    public void getCommonData() {
        mHomeRepo.getCommonResponseData(mCommonLiveData);
    }

    public void giveRating(String sellerId, String productId, int rating, String comment) {
        if (validateFeedBackMsg(rating, comment)) {
            loading.onChanged(true);
            HashMap<String, Object> parms = new HashMap<>();
            parms.put(AppConstants.KEY_CONSTENT.SELLER_ID, sellerId);
            parms.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
            parms.put(AppConstants.KEY_CONSTENT.RATING, rating);
            parms.put(AppConstants.KEY_CONSTENT.COMMENT, comment);
            mHomeRepo.giveFeedback(mFeedBackLiveData, parms, AppConstants.REQUEST_CODE.SUBMIT_FEEDBACK);
        }
    }

    public void acceptRejectTagRequest(String userId, String communityId, int status) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.USER_ID, userId);
        parms.put(AppConstants.KEY_CONSTENT.COMMUNITY_ID, communityId);
        parms.put(AppConstants.KEY_CONSTENT.STATUS, status);
        cartRepo.acceptRejectTagRequest(mAccpetRejectTagRequest, parms, AppConstants.REQUEST_CODE.ACCEPT_REJECT_TAG_REQUEST);
    }

    public void denyRating(String sellerId, String productId) {

        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.SELLER_ID, sellerId);
        parms.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
        mHomeRepo.denyFeedback(mFeedBackLiveData, parms, AppConstants.REQUEST_CODE.DENY_RATING);

    }

    private boolean validateFeedBackMsg(int rating, String comment) {
        if (rating == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_select_rating)));
            return false;
        }
        return true;
    }

    public void updateDeviceToken(String token) {
        mHomeRepo.updateDeviceToken(mUpdateDeviceTokenLiveData, token, 10);
    }

    public void getBankDetails() {
        mHomeRepo.getBankDetails(mgetBankDetailsViewModel);
    }

    public void createMerchent() {
        String ip = "";
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.IP, AppUtils.getLocalIpAddress());
//        parms.put(AppConstants.KEY_CONSTENT.ACCOUNT_NUMBER, DataManager.getInstance().getAccountNumber());
//        parms.put(AppConstants.KEY_CONSTENT.ACCOUNT_HOLDER, DataManager.getInstance().getAccountHolderName());
//        parms.put(AppConstants.KEY_CONSTENT.ROUTING_NUMBER, DataManager.getInstance().getRoutingNumber());
        mHomeRepo.createMerchent(mCreateMerchentLiveData, parms);

    }

    public void getVendor() {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.USER_ID, DataManager.getInstance().getUserDetails().getUserId());
        mHomeRepo.getVendorId(mGetVendorIdLivedata, parms);
    }

    public void getHtmlContent(String type) {
        loading.onChanged(true);
        mHomeRepo.getHtmlContent(mHtmlContenLiveData, type);
    }

    public RichMediatorLiveData<CommonResponse> logout() {
        return mlogOut;
    }

    public RichMediatorLiveData<CommonResponse> getCartViewModel() {
        return mCartViewModel;
    }

    public RichMediatorLiveData<TagSearchBean> getTagSeachViewModel() {
        return mTagSearchViewModel;
    }

    public RichMediatorLiveData<UpdateRatingNotificationBean> getmUpdateDeviceTokenLiveData() {
        return mUpdateDeviceTokenLiveData;
    }

    public RichMediatorLiveData<CommonResponse> getmFeedBackLiveData() {
        return mFeedBackLiveData;
    }

    public RichMediatorLiveData<CreateMercentResponse> getmCreateMerchentLiveData() {
        return mCreateMerchentLiveData;
    }

    public RichMediatorLiveData<VendorIdResponse> getVendorIdLiveData() {
        return mGetVendorIdLivedata;
    }

    public RichMediatorLiveData<GetBankDetail> getMgetBankDetailsViewModel() {
        return mgetBankDetailsViewModel;
    }

    public RichMediatorLiveData<ContentViewModel> getHtmlContentLiveData() {
        return mHtmlContenLiveData;
    }

    public Query getUserNodeQuery(String userId) {
        return DataManager.getInstance().getUserNodeQuery(userId);
    }

    public void cashOutBalance(double amount) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.AMOUNT, amount);
//        parms.put(AppConstants.KEY_CONSTENT.CURRENCY, AppConstants.CURRENCY_USD);
//        parms.put("method", "standard");
//        parms.put("source_type", "card");
//        parms.put("description", "bank_account");
        mHomeRepo.cashOutBalance(mCashOutLiveData, parms);
    }

    public void getBalance() {
//        loading.onChanged(true);
        mHomeRepo.getBalance(mBalanceLiveData);
    }

    public void getProfileProductsList(int pageNumber, int limit) {
        ChatMessagesRepo chatMessagesRepo = new ChatMessagesRepo();
        HashMap<String, Object> params = new HashMap<>();
        params.put("productStatus", AppConstants.PROFILE_PRODUCTS_TYPE.SELLING);
        params.put("pageNo", String.valueOf(pageNumber));
        params.put("limit", String.valueOf(limit));
        chatMessagesRepo.hitMyProductsAPI(mProfileProductsLiveData, params);
    }

    public void merchantDetails() {
        loading.onChanged(true);
        mHomeRepo.merchantDetail(merchantDetailsLiveData, 111);
    }

    public void merchantDetailsClicked() {
        loading.onChanged(true);
        mHomeRepo.merchantDetail(merchantDetailsLiveDataCopy, 111);
    }

    public RichMediatorLiveData<ProfileProductsResponse> profileProductsLiveData() {
        return mProfileProductsLiveData;
    }

    public void joinTagOnFirebase(User user, TagData tagData) {
        DataManager.getInstance().joinTag(user, tagData);
    }


    public RichMediatorLiveData<TagDetailsModel> getmAccpetRejectTagRequest() {
        return mAccpetRejectTagRequest;
    }

    public RichMediatorLiveData<CommonDataModel> getmCommonLiveData() {
        return mCommonLiveData;
    }

    public RichMediatorLiveData<BalanceResponse> cashOutLiveDAta() {
        return mCashOutLiveData;
    }

    public RichMediatorLiveData<BalanceResponse> getBalanceLiveData() {
        return mBalanceLiveData;
    }

    public RichMediatorLiveData<MerchantDetailBeans> merchantDetailLiveData() {
        return merchantDetailsLiveData;
    }

    public RichMediatorLiveData<MerchantDetailBeans> merchantDetailRefreshLiveData() {
        return merchantDetailsLiveDataCopy;
    }

    public void updateDeviceTokenOnFirebase(String userId, String token) {
        DataManager.getInstance().updateDeviceTokenOnFirebase(userId, token);
    }

}
