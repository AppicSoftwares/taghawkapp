package com.taghawk.util;



import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.Repository.HomeRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;

import java.util.HashMap;

public class CommonViewModel extends ViewModel {
    private HomeRepo mHomeRepo = new HomeRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> loading;
    private Observer<FailureResponse> mFailureObserver;
    private RichMediatorLiveData<CommonResponse> mlogOut;

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    private void initLiveData() {
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
    }

    public RichMediatorLiveData<CommonResponse> logout() {
        return mlogOut;
    }

    public void hitLogOut(String deviceId) {
        if (mlogOut != null) {
            loading.onChanged(true);
            HashMap<String, Object> parms = new HashMap<>();
            parms.put(AppConstants.KEY_CONSTENT.DEVICE_ID, deviceId);
            mHomeRepo.logout(mlogOut, parms);
        }
    }
}
