package com.taghawk.ui.profile;



import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.profileresponse.ProfileProductsResponse;

public class SoldProductsViewModel extends ViewModel {

    private SoldProductsRepo mSellingProductsRepo = new SoldProductsRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> loading;
    private Observer<FailureResponse> mFailureObserver;
    private RichMediatorLiveData<ProfileProductsResponse> mProfileProductsLiveData;

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    private void initLiveData() {
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
    }

    public void getProfileProductsList(boolean b, int size,String sellerId) {
        if (b)
            mSellingProductsRepo.initPage(0);
        mSellingProductsRepo.getProductsList(size, mProfileProductsLiveData,sellerId);
    }

    public RichMediatorLiveData<ProfileProductsResponse> profileProductsLiveData() {
        return mProfileProductsLiveData;
    }
}
