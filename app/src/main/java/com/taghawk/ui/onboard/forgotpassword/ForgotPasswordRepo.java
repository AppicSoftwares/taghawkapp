package com.taghawk.ui.onboard.forgotpassword;


import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;

public class ForgotPasswordRepo {

    public void hitForgotPassword(final RichMediatorLiveData<CommonResponse> forgotLiveData, String email, String userType) {
        DataManager.getInstance().hitForgotPasswordApi(email, userType).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                forgotLiveData.setValue(successResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                forgotLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                forgotLiveData.setError(t);
            }
        });
    }
}
