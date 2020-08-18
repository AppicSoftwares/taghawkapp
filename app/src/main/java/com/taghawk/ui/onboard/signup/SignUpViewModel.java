package com.taghawk.ui.onboard.signup;

import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.R;
import com.taghawk.Repository.LoginRepo;
import com.taghawk.Repository.SignUpRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.request.UserResponse;
import com.taghawk.util.ResourceUtils;

import java.util.HashMap;

public class SignUpViewModel extends ViewModel {

    private RichMediatorLiveData<UserResponse> signUpLiveData;
    private Observer<FailureResponse> failureResponseObserver;
    private Observer<Throwable> errorObserver;
    private MutableLiveData<FailureResponse> validateLiveData;
    private Observer<Boolean> isLoading;

    private SignUpRepo signUpRepo = new SignUpRepo();


    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureObserver, Observer<Boolean> isLoading) {
        this.errorObserver = errorObserver;
        this.failureResponseObserver = failureObserver;
        this.isLoading = isLoading;
        initLiveData();
    }

    private void initLiveData() {
        if (signUpLiveData == null) {
            signUpLiveData = new RichMediatorLiveData<UserResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    isLoading.onChanged(false);
                    return failureResponseObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    isLoading.onChanged(false);
                    return errorObserver;
                }
            };
        }

        if (validateLiveData == null)
            validateLiveData = new MutableLiveData<>();
    }


    public RichMediatorLiveData<UserResponse> getSignUpLiveData() {
        return signUpLiveData;
    }

    public void socialLogin(String email, HashMap<String, Object> user, Boolean isFromSocialEmptyEmail) {
        //showProgress
        if (isFromSocialEmptyEmail) {
            user.put(AppConstants.KEY_CONSTENT.EMAIL, email);
        }
        isLoading.onChanged(true);
        signUpRepo.hitSocialLoginApi(signUpLiveData, user);
    }


    /**
     * Method used to hit sign up api after checking validations
     *
     * @param user contains all the params of the request
     */
    public void userSignUp(HashMap<String, String> user) {
        if (checkValidation(user)) {
            isLoading.onChanged(true);
            signUpRepo.hitSignUpApi(signUpLiveData, user);
        }
    }

    private boolean checkValidation(HashMap<String, String> user) {
        if (user.get("firstName").isEmpty()) {
            validateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.NAME_EMPTY, ResourceUtils.getInstance().getString(R.string.enter_first_name)
            ));
            return false;
        } else if (!user.get("firstName").matches("[a-z A-Z]*")) {
            validateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.NAME_EMPTY, ResourceUtils.getInstance().getString(R.string.enter_valid_first_name)
            ));
            return false;
        }
        if (user.get("lastName").isEmpty()) {
            validateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.LAST_NAME_EMPTY, ResourceUtils.getInstance().getString(R.string.enter_last_name)
            ));
            return false;
        } else if (!user.get("lastName").matches("[a-z A-Z]*")) {
            validateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.LAST_NAME_EMPTY, ResourceUtils.getInstance().getString(R.string.enter_valid_last_name)
            ));
            return false;
        } else if (user.get(AppConstants.KEY_CONSTENT.EMAIL).isEmpty()) {
            validateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.EMAIL_EMPTY, ResourceUtils.getInstance().getString(R.string.enter_email)
            ));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(user.get(AppConstants.KEY_CONSTENT.EMAIL)).matches()) {
            validateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.INVALID_EMAIL, ResourceUtils.getInstance().getString(R.string.enter_valid_email)
            ));
            return false;
        } else if (user.get(AppConstants.KEY_CONSTENT.PASSWORD).isEmpty()) {
            validateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.PASSWORD_EMPTY, ResourceUtils.getInstance().getString(R.string.enter_password)
            ));
            return false;
        } else if (user.get(AppConstants.KEY_CONSTENT.PASSWORD).length() < 6) {
            validateLiveData.setValue(new FailureResponse(
                    AppConstants.UIVALIDATIONS.INVALID_PASSWORD, ResourceUtils.getInstance().getString(R.string.enter_valid_password)
            ));
            return false;
        }
        return true;
    }
    private LoginRepo mLoginRepo = new LoginRepo();
    public void saveDeviceToken(String deviceToken) {
        mLoginRepo.saveDeviceToken(deviceToken);
    }

    public void guestUserLogin(String deviceId, String deviceToken) {
        isLoading.onChanged(true);
        HashMap<String, String> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.DEVICE_ID, deviceId);
        parms.put(AppConstants.KEY_CONSTENT.DEVICETOKEN, deviceToken);
        mLoginRepo.hitGuestLoginApi(signUpLiveData, parms);
    }

    public MutableLiveData<FailureResponse> getValidationLiveData() {
        return validateLiveData;
    }

}
