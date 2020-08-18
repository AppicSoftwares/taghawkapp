package com.taghawk.ui.onboard.reset;


import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.request.ChangePassword;
import com.taghawk.model.request.Reset;

public class ResetPasswordRepo {


    public void resetPassword(final RichMediatorLiveData<CommonResponse> resetPasswordLiveData, Reset reset) {
        DataManager.getInstance().hitResetPasswordApi(reset).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                resetPasswordLiveData.setValue(successResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                resetPasswordLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                resetPasswordLiveData.setError(t);
            }
        });

    }


    public void resetPassword(final RichMediatorLiveData<CommonResponse> resetPasswordLiveData, ChangePassword reset) {
        DataManager.getInstance().hitChangePasswordApi(reset).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                resetPasswordLiveData.setValue(successResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                resetPasswordLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                resetPasswordLiveData.setError(t);
            }
        });

    }
}
