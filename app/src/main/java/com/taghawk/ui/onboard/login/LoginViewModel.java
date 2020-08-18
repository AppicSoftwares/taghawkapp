package com.taghawk.ui.onboard.login;


import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.R;
import com.taghawk.Repository.LoginRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.login.CheckSocialLoginmodel;
import com.taghawk.model.login.LoginModel;
import com.taghawk.model.request.UserResponse;
import com.taghawk.util.ResourceUtils;

import java.util.HashMap;

public class LoginViewModel extends ViewModel {

    private RichMediatorLiveData<UserResponse> mLoginLiveData;
    private RichMediatorLiveData<CheckSocialLoginmodel> mCheckSocialLoginLiveData;
    private Observer<FailureResponse> mFailureObserver;
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> lodingObserver;
    private MutableLiveData<FailureResponse> mValidateLiveData;
//    private MutableLiveData<Boolean> isLodingProgress;

    private LoginRepo mLoginRepo = new LoginRepo();

    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureObserver, Observer<Boolean> lodingObserver) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureObserver;
        this.lodingObserver = lodingObserver;
        initLiveData();
    }

    private void initLiveData() {
        if (mLoginLiveData == null) {
            mLoginLiveData = new RichMediatorLiveData<UserResponse>() {
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

        if (mCheckSocialLoginLiveData == null) {
            mCheckSocialLoginLiveData = new RichMediatorLiveData<CheckSocialLoginmodel>() {
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
        if (mValidateLiveData == null) {
            mValidateLiveData = new MutableLiveData<>();
        }

//        if (isLodingProgress == null)
//            isLodingProgress = new MutableLiveData<>();

    }

    /**
     * This method gives the login live data object to {@link LoginFragment}
     *
     * @return {@link #mLoginLiveData}
     */
    public RichMediatorLiveData<UserResponse> getLoginLiveData() {
        return mLoginLiveData;
    }

    /**
     * Method used to hit login api after checking validations
     *
     * @param user contains all the params of the request
     */
    public void loginButtonClicked(LoginModel user) {

        if (checkValidation(user)) {
            //showProgress
            lodingObserver.onChanged(true);
            mLoginRepo.hitLoginApi(mLoginLiveData, user);
        }
    }

    public void socialLogin(String email, HashMap<String, Object> user, Boolean isFromSocialEmptyEmail) {
        //showProgress
        if (isFromSocialEmptyEmail) {
            user.put(AppConstants.KEY_CONSTENT.EMAIL, email);
        }
        lodingObserver.onChanged(true);
        mLoginRepo.hitSocialLoginApi(mLoginLiveData, user);
    }

    public void checkSocialLogin(HashMap<String, String> parms) {
        lodingObserver.onChanged(true);
        mLoginRepo.checkSocialLogin(mCheckSocialLoginLiveData, parms);
    }

    /**
     * Method to check validation
     *
     * @param user
     * @return
     */
    private boolean checkValidation(LoginModel user) {
        if (user.getEmail().isEmpty()) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.EMAIL_EMPTY, ResourceUtils.getInstance().getString(R.string.enter_email)));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(user.getEmail()).matches()) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.INVALID_EMAIL, ResourceUtils.getInstance().getString(R.string.enter_valid_email)
            ));
            return false;
        } else if (user.getPassword().isEmpty()) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.PASSWORD_EMPTY, ResourceUtils.getInstance().getString(R.string.enter_password)
            ));
            return false;
        } else if (user.getPassword().length() < 6) {
            mValidateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.INVALID_PASSWORD, ResourceUtils.getInstance().getString(R.string.enter_valid_password)
            ));
            return false;
        }
        return true;
    }

    public void guestUserLogin(String deviceId, String deviceToken) {
        lodingObserver.onChanged(true);
        HashMap<String, String> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.DEVICE_ID, deviceId);
        parms.put(AppConstants.KEY_CONSTENT.DEVICETOKEN, deviceToken);
        mLoginRepo.hitGuestLoginApi(mLoginLiveData, parms);
    }

    /**
     * This method gives the validation live data object to {@link LoginFragment}
     *
     * @return {@link #mValidateLiveData}
     */
    public MutableLiveData<FailureResponse> getValidationLiveData() {
        return mValidateLiveData;
    }

    public RichMediatorLiveData<CheckSocialLoginmodel> getmCheckSocialLoginLiveData() {
        return mCheckSocialLoginLiveData;
    }
//    public MutableLiveData<Boolean> isLoading() {
//        return isLodingProgress;
//    }

    public void saveDeviceToken(String deviceToken) {
        mLoginRepo.saveDeviceToken(deviceToken);
    }
}
