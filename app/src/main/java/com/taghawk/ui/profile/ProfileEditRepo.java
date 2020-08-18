package com.taghawk.ui.profile;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.ShippingAddressesResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.strip.FedexRateResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;

public class ProfileEditRepo {

    public void updateProfile(final RichMediatorLiveData<CommonResponse> editProfileLiveData, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().editProfile(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse commonResponse) {
                commonResponse.setRequestCode(requestCode);
                editProfileLiveData.setValue(commonResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                editProfileLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                editProfileLiveData.setError(t);
            }
        });
    }

    public void otpVerification(final RichMediatorLiveData<CommonResponse> otpLiveData, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().otpVerify(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse commonResponse) {
                commonResponse.setRequestCode(requestCode);
                otpLiveData.setValue(commonResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                otpLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                otpLiveData.setError(t);
            }
        });
    }

    public void uploadDocument(final RichMediatorLiveData<CommonResponse> otpLiveData, MultipartBody.Part documentImage, MultipartBody.Part backDocument, final int requestCode) {
        DataManager.getInstance().uploadDocument(documentImage,backDocument).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse commonResponse) {
                commonResponse.setRequestCode(requestCode);
                otpLiveData.setValue(commonResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                otpLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                otpLiveData.setError(t);
            }
        });
    }

    // This Service is use for caculate Fedex Shipping Charges
    public void addProfileAddresses(final RichMediatorLiveData<CommonResponse> addAddressLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().addProfileAddresses(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse commonResponse) {
                addAddressLiveData.setValue(commonResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                addAddressLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                addAddressLiveData.setError(t);
            }
        });
    }

    // This Service is use for caculate Fedex Shipping Charges
    public void addBillingAddress(final RichMediatorLiveData<ShippingAddressesResponse> addAddressLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().addBillingAddress(parms).enqueue(new NetworkCallback<ShippingAddressesResponse>() {
            @Override
            public void onSuccess(ShippingAddressesResponse commonResponse) {
                addAddressLiveData.setValue(commonResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                addAddressLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                addAddressLiveData.setError(t);
            }
        });
    }

}
