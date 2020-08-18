package com.taghawk.ui.setting.change_password;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.R;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.request.ChangePassword;
import com.taghawk.ui.onboard.reset.ResetPasswordRepo;
import com.taghawk.util.ResourceUtils;

public class ChangePasswordViewModel extends ViewModel {

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
    public void onSubmitClicked(ChangePassword reset, String confirmPassword) {
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
    private boolean checkValidation(ChangePassword reset, String confirmPassword) {
        if (reset.getOldPassword().isEmpty()) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.OLD_PASSWORD_EMPTY, ResourceUtils.getInstance().getString(R.string.please_enter_old_pass)
            ));
            return false;
        } else if (reset.getOldPassword().length() < 6) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.OLD_PASSWORD_EMPTY, ResourceUtils.getInstance()
                    .getString(R.string.enter_valid_password)
            ));
            return false;
        } else if (reset.getPassword().isEmpty()) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.PASSWORD_EMPTY, ResourceUtils.getInstance().getString(R.string.enter_password)
            ));
            return false;
        } else if (reset.getPassword().length() < 6) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.PASSWORD_EMPTY, ResourceUtils.getInstance()
                    .getString(R.string.enter_valid_password)
            ));
            return false;
        } else if (confirmPassword.isEmpty()) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.CONFIRM_PASSWORD_EMPTY, ResourceUtils.getInstance()
                    .getString(R.string.confirm_password_empty)
            ));
            return false;
        } else if (!reset.getPassword().equals(confirmPassword)) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.CONFIRM_PASSWORD_EMPTY, ResourceUtils.getInstance()
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
