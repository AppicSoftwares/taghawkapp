package com.taghawk.ui.gift;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.Repository.GiftPromotionRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.gift.GiftRewardsPromotionModel;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.profileresponse.ProfileProductsResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class GiftRewardsPromotionViewModel extends ViewModel {

    private GiftPromotionRepo repo = new GiftPromotionRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> loading;
    private Observer<FailureResponse> mFailureObserver;
    private RichMediatorLiveData<GiftRewardsPromotionModel> mGiftPromotionLiveData;
    private RichMediatorLiveData<ProfileProductsResponse> mProductLiveData;
    private RichMediatorLiveData<CommonResponse> mRedeemLiveData;

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    private void initLiveData() {
        if (mGiftPromotionLiveData == null) {
            mGiftPromotionLiveData = new RichMediatorLiveData<GiftRewardsPromotionModel>() {
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
        if (mProductLiveData == null) {
            mProductLiveData = new RichMediatorLiveData<ProfileProductsResponse>() {
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
        if (mRedeemLiveData == null) {
            mRedeemLiveData = new RichMediatorLiveData<CommonResponse>() {
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

    public void getProfileProductsList(boolean b, int size, String sellerId) {
        if (b) {
//            loading.onChanged(true);
            repo.initPage(0);
        }
        repo.getProductsList(size, mProductLiveData, sellerId);
    }


    public void getGiftPromotions() {
        loading.onChanged(true);
        repo.getRewardsPromtions(mGiftPromotionLiveData);
    }

    public void promoteMultiple(HashMap<Integer, ProductDetailsData> selectiveProductIds, int days, int rewardsPoints, int avaliableRewards) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("products", getProductIds(selectiveProductIds));
        parms.put("price", (rewardsPoints * selectiveProductIds.size()));
        parms.put(AppConstants.KEY_CONSTENT.DAYS, days);
        repo.markProductFeatured(mRedeemLiveData, parms);
    }

    private String getProductIds(HashMap<Integer, ProductDetailsData> selectiveProductIds) {
        String productIds = "";
        ArrayList<ProductDetailsData> listOfValues = new ArrayList<ProductDetailsData>();
        listOfValues.addAll(selectiveProductIds.values());
        for (int i = 0; i < listOfValues.size(); i++) {
            if (i == 0)
                productIds = listOfValues.get(i).getProductId();
            else
                productIds = productIds + "," + listOfValues.get(i).getProductId();
        }
        return productIds;
    }

    public RichMediatorLiveData<GiftRewardsPromotionModel> getGiftPromotionLiveData() {
        return mGiftPromotionLiveData;
    }

    public RichMediatorLiveData<ProfileProductsResponse> productsLiveData() {
        return mProductLiveData;
    }

    public RichMediatorLiveData<CommonResponse> redeemLiveData() {
        return mRedeemLiveData;
    }
}
