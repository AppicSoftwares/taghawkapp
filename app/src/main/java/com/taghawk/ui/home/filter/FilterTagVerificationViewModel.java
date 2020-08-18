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

import java.util.HashMap;

public class FilterTagVerificationViewModel extends ViewModel {

    private Observer<FailureResponse> mFailureObserver;

    private RichMediatorLiveData<ProductListingModel> mProductListing;
    private RichMediatorLiveData<ProductDetailsModel> mProductDetails;
    private Observer<HashMap<String, Object>> filterData;
    //Initializing repository class
    private HomeRepo mHomeRepo = new HomeRepo();
    private MutableLiveData<HashMap<String, Object>> mLiveData;


    //saving error & failure observers instance
    public void setGenericListeners(MutableLiveData<HashMap<String, Object>> mLiveData, Observer<FailureResponse> failureResponseObserver) {
        this.mFailureObserver = failureResponseObserver;
        this.mLiveData = mLiveData;
        initView();
    }

    private void initView() {
        mLiveData = new MutableLiveData<>();
    }


    public void validateFilter(HashMap<String, Object> parms, Address location, int tagType, int distance) {
        if (parms == null)
            parms = new HashMap<>();
        if (location != null) {
            parms.put(AppConstants.KEY_CONSTENT.LAT, location.getLatitude());
            parms.put(AppConstants.KEY_CONSTENT.LONGI, location.getLongitude());
//            parms.put(AppConstants.KEY_CONSTENT.LOCATION, location.getAddressLine(0));
        }
        if (tagType > 0) {
            parms.put(AppConstants.KEY_CONSTENT.TAG_TYPE, tagType);
        }else{
            parms.put(AppConstants.KEY_CONSTENT.TAG_TYPE, tagType);
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
            } else {
                mFailureObserver.onChanged(new FailureResponse(
                        AppConstants.UIVALIDATIONS.LOCATION_SELECT_LOCATION, ResourceUtils.getInstance().getString(R.string.select_location)));
            }

        }
        mLiveData.setValue(parms);
    }


    public MutableLiveData<HashMap<String, Object>> getmLiveData() {
        return mLiveData;
    }

}



