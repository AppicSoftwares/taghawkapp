package com.taghawk.ui.home.filter;

import android.location.Address;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.R;
import com.taghawk.Repository.HomeRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.home.ProductDetailsModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.util.ResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class FilterVerificationViewModel extends ViewModel {

    private Observer<FailureResponse> mFailureObserver;

    private RichMediatorLiveData<ProductListingModel> mProductListing;
    private RichMediatorLiveData<ProductDetailsModel> mProductDetails;
    private Observer<HashMap<String, Object>> filterData;
    //Initializing repository class
    private HomeRepo mHomeRepo = new HomeRepo();
    private MutableLiveData<HashMap<String, Object>> mLiveData;

    private String[] sellerRating = new String[]{"1", "2", "3", "4", "5"};

    //saving error & failure observers instance
    public void setGenericListeners(MutableLiveData<HashMap<String, Object>> mLiveData, Observer<FailureResponse> failureResponseObserver) {
        this.mFailureObserver = failureResponseObserver;
        this.mLiveData = mLiveData;
        initView();
    }

    private void initView() {
        mLiveData = new MutableLiveData<>();
    }


    public void validateFilter(HashMap<String, Object> parms, Address location, String fromPrizeRange, String toPrizeRange, boolean newConditon, boolean newLikeConditon, boolean goodCondition, boolean normalCondition, boolean flawedCondition, int sellerRationgPosition, boolean isSellerVerified, int postedWithin, int distance) {
        Integer[] condition;
        int count = 0;
        ArrayList<String> mConditionList = new ArrayList<>();
        if (parms == null)
            parms = new HashMap<>();
        if (valiadate(fromPrizeRange, toPrizeRange, distance, location)) {
            if (location != null) {
                parms.put(AppConstants.KEY_CONSTENT.LAT, location.getLatitude());
                parms.put(AppConstants.KEY_CONSTENT.LONGI, location.getLongitude());
            }
            if (fromPrizeRange != null && toPrizeRange != null && fromPrizeRange.length() > 0 && toPrizeRange.length() > 0 && Integer.valueOf(toPrizeRange) >= Integer.valueOf(fromPrizeRange)) {
                parms.put(AppConstants.KEY_CONSTENT.PRICE_FROM, fromPrizeRange);
                parms.put(AppConstants.KEY_CONSTENT.PRICE_TO, toPrizeRange);
            } else {
                if (parms.containsKey(AppConstants.KEY_CONSTENT.PRICE_TO)) {
                    parms.remove(AppConstants.KEY_CONSTENT.PRICE_TO);
                }
                if (parms.containsKey(AppConstants.KEY_CONSTENT.PRICE_FROM))
                    parms.remove(AppConstants.KEY_CONSTENT.PRICE_FROM);
            }
            if (newConditon) {
                mConditionList.add("1");
            }
            if (newLikeConditon) {
                mConditionList.add("2");
            }
            if (goodCondition) {
                mConditionList.add("3");
            }
            if (normalCondition) {
                mConditionList.add("4");
            }
            if (flawedCondition) {
                mConditionList.add("5");
            }
            String condition1 = "";
            for (int i = 0; i < mConditionList.size(); i++) {
//                arr[i] = mConditionList.get(i);
                if (i == 0) {
                    condition1 = mConditionList.get(i);
                } else {
                    condition1 = condition1 + "," + mConditionList.get(i);
                }
            }
            if (mConditionList.size() > 0) {
                parms.put(AppConstants.KEY_CONSTENT.CONDITION, condition1);
            } else {
                if (parms.containsKey(AppConstants.KEY_CONSTENT.CONDITION))
                    parms.remove(AppConstants.KEY_CONSTENT.CONDITION);
            }
            if (sellerRationgPosition != -1) {
                parms.put(AppConstants.KEY_CONSTENT.SELLER_RATING, sellerRating[sellerRationgPosition]);
            } else {
                if (parms.containsKey(AppConstants.KEY_CONSTENT.SELLER_RATING))
                    parms.remove(AppConstants.KEY_CONSTENT.SELLER_RATING);
            }
            if (isSellerVerified) {
                parms.put(AppConstants.KEY_CONSTENT.SELLER_VERIFIED, true);
            } else {
                if (parms.containsKey(AppConstants.KEY_CONSTENT.SELLER_VERIFIED))
                    parms.remove(AppConstants.KEY_CONSTENT.SELLER_VERIFIED);
            }
            if (postedWithin > 0) {
                parms.put(AppConstants.KEY_CONSTENT.POSTED_WITH_IN, postedWithin);
            }
            if (distance != 100) {
                if (location != null) {
                    if (distance == 0) {
                        parms.put(AppConstants.KEY_CONSTENT.DISTANCE, 1);
                    } else if (distance == 25) {
                        parms.put(AppConstants.KEY_CONSTENT.DISTANCE, 5);
                    } else if (distance == 50) {
                        parms.put(AppConstants.KEY_CONSTENT.DISTANCE, 10);
                    } else if (distance == 75) {
                        parms.put(AppConstants.KEY_CONSTENT.DISTANCE, 50);
                    }
                }
            } else {
                parms.remove(AppConstants.KEY_CONSTENT.DISTANCE);
            }
            mLiveData.setValue(parms);
        }
    }

    private boolean valiadate(String fromPrizeRange, String toPrizeRange, int distance, Address location) {
        if ((toPrizeRange == null || toPrizeRange.length() == 0) && (fromPrizeRange != null && fromPrizeRange.length() > 0)) {
            mFailureObserver.onChanged(new FailureResponse(
                    AppConstants.UIVALIDATIONS.FROM_PRICE, ResourceUtils.getInstance().getString(R.string.enter_to_price)));
            return false;

        } else if ((fromPrizeRange == null || fromPrizeRange.length() == 0) && (toPrizeRange != null && toPrizeRange.length() > 0)) {
            mFailureObserver.onChanged(new FailureResponse(
                    AppConstants.UIVALIDATIONS.TO_PRICE, ResourceUtils.getInstance().getString(R.string.enter_from_price)));
            return false;
        } else if (toPrizeRange.length() > 0 && fromPrizeRange.length() > 0 && Integer.valueOf(toPrizeRange) < Integer.valueOf(fromPrizeRange)) {
            mFailureObserver.onChanged(new FailureResponse(
                    AppConstants.UIVALIDATIONS.FROM_GREATER_TO, ResourceUtils.getInstance().getString(R.string.enter_from_greater_price)));
            return false;
        }
        if (distance > 0) {
            if (location != null) {
                return true;
            } else {
                mFailureObserver.onChanged(new FailureResponse(
                        AppConstants.UIVALIDATIONS.LOCATION_SELECT_LOCATION, ResourceUtils.getInstance().getString(R.string.select_location)));
                return false;
            }
        }
        return true;
    }

    public MutableLiveData<HashMap<String, Object>> getmLiveData() {
        return mLiveData;
    }

}



