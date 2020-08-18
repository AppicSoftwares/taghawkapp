package com.taghawk.ui.onboard.reset;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.R;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.request.Reset;
import com.taghawk.util.ResourceUtils;

public class ResetPasswordViewModel extends ViewModel {

    private RichMediatorLiveData<CommonResponse> mResetPasswordLiveData;
    private Observer<FailureResponse> mFailureObserver;
    private Observer<Throwable> mErrorObserver;
    private MutableLiveData<FailureResponse> mValidateLiveData;
    private Observer<Boolean> loading;
    private ResetPasswordRepo mResetPasswordRepo = new ResetPasswordRepo();

    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();

    }

    private void initLiveData() {
        if (mResetPasswordLiveData == null) {
            mResetPasswordLiveData = new RichMediatorLiveData<CommonResponse>() {
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

        if (mValidateLiveData == null)
            mValidateLiveData = new MutableLiveData<>();
    }

    public RichMediatorLiveData<CommonResponse> getmResetPasswordLiveData() {
        return mResetPasswordLiveData;
    }

    /**
     * This method is used to check the validations and pass the data to the
     * {@link ResetPasswordRepo} to get the response
     *
     * @param reset contains the params of the request
     */
    public void onSubmitClicked(Reset reset, String confirmPassword) {
        if (checkValidation(reset, confirmPassword)) {
            loading.onChanged(true);
            mResetPasswordRepo.resetPassword(mResetPasswordLiveData, reset);
        }
    }

    /**
     * This method is used to check the validations
     *
     * @return false if any validation fails otherwise true
     */
    private boolean checkValidation(Reset reset, String confirmPassword) {
        if (reset.getPassword().isEmpty()) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.NEW_PASSWORD_EMPTY, ResourceUtils.getInstance()
                    .getString(R.string.new_password_empty)
            ));
            return false;
        } else if (reset.getPassword().length() < 6) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.INVALID_PASSWORD, ResourceUtils.getInstance()
                    .getString(R.string.enter_valid_password)
            ));
            return false;
        } else if (confirmPassword.isEmpty()) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.CONFIRM_PASSWORD_EMPTY, ResourceUtils.getInstance()
                    .getString(R.string.confirm_password_empty)
            ));
            return false;
        } else if (confirmPassword.length() < 6) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.INVALID_PASSWORD, ResourceUtils.getInstance()
                    .getString(R.string.enter_valid_password)
            ));
            return false;

        } else if (!reset.getPassword().equals(confirmPassword)) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.PASSWORD_NOT_MATCHED, ResourceUtils.getInstance()
                    .getString(R.string.password_not_matched)
            ));
            return false;
        }
        return true;
    }

    public MutableLiveData<FailureResponse> getValidationLiveData() {
        return mValidateLiveData;

    }
}
