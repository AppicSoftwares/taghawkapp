package com.taghawk.ui.onboard.forgotpassword;

import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.R;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.util.ResourceUtils;

public class ForgotPasswordViewModel extends ViewModel {

    private RichMediatorLiveData<CommonResponse> forgotLiveData;
    private Observer<FailureResponse> failureObserver;
    private Observer<Throwable> errorObserver;
    private MutableLiveData<FailureResponse> validateLiveData;
    private Observer<Boolean> processingLoading;

    private ForgotPasswordRepo forgotPasswordRepo = new ForgotPasswordRepo();

    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureObserver, Observer<Boolean> processingLoading) {
        this.errorObserver = errorObserver;
        this.failureObserver = failureObserver;
        this.processingLoading = processingLoading;
        initLiveData();
    }

    private void initLiveData() {
        if (forgotLiveData == null) {
            forgotLiveData = new RichMediatorLiveData<CommonResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return failureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return errorObserver;
                }
            };

            if (validateLiveData == null)
                validateLiveData = new MutableLiveData<>();
        }
    }


    public RichMediatorLiveData<CommonResponse> getForgotPasswordLiveData() {
        return forgotLiveData;
    }

    public void onSubmitClicked(String email, String userType) {
        if (checkValidation(email)) {
            processingLoading.onChanged(true);
            forgotPasswordRepo.hitForgotPassword(forgotLiveData, email, userType);
        }

    }


    private boolean checkValidation(String email) {
        if (email.isEmpty()) {
            validateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.EMAIL_EMPTY, ResourceUtils.getInstance().getString(R.string.enter_email)
            ));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            validateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.INVALID_EMAIL, ResourceUtils.getInstance().getString(R.string.enter_valid_email)
            ));
            return false;
        }
        return true;
    }

    public MutableLiveData<FailureResponse> getValidationLiveData() {
        return validateLiveData;
    }
}
