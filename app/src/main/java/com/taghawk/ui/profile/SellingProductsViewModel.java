package com.taghawk.ui.profile;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.DeleteProductRequest;
import com.taghawk.model.profileresponse.ProfileProductsResponse;

public class SellingProductsViewModel extends ViewModel {

    private SellingProductsRepo mSellingProductsRepo = new SellingProductsRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> loading;
    private Observer<FailureResponse> mFailureObserver;
    private RichMediatorLiveData<ProfileProductsResponse> mProfileProductsLiveData;
    private RichMediatorLiveData<CommonResponse> mDeleteProductLiveData;

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
        if (mDeleteProductLiveData == null) {
            mDeleteProductLiveData = new RichMediatorLiveData<CommonResponse>() {
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

    public void deleteProduct(String productId) {
        loading.onChanged(true);
        DeleteProductRequest deleteProductRequest = new DeleteProductRequest();
        deleteProductRequest.setProductId(productId);
        mSellingProductsRepo.deleteProduct(mDeleteProductLiveData, deleteProductRequest, AppConstants.REQUEST_CODE.DELETE_PRODUCT);

    }

    public void getProfileProductsList(boolean b, int size,String sellerId) {
        if (b)
            mSellingProductsRepo.initPage(0);
        mSellingProductsRepo.getProductsList(size, mProfileProductsLiveData,sellerId);
    }

    public RichMediatorLiveData<ProfileProductsResponse> profileProductsLiveData() {
        return mProfileProductsLiveData;
    }

    public RichMediatorLiveData<CommonResponse> getDeleteLiveData() {
        return mDeleteProductLiveData;
    }
}
