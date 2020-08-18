package com.taghawk.Repository;


import androidx.lifecycle.Observer;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.login.CheckSocialLoginmodel;
import com.taghawk.model.login.LoginFirebaseModel;
import com.taghawk.model.login.LoginModel;
import com.taghawk.model.request.User;
import com.taghawk.model.request.UserResponse;

import java.util.HashMap;

import siftscience.android.Sift;

public class LoginRepo {

    private Observer<Boolean> lodingObserver;

    // This Api is use for login
    public void hitLoginApi(final RichMediatorLiveData<UserResponse> liveData, final LoginModel user) {

        DataManager.getInstance().hitLoginApi(user).enqueue(new NetworkCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse userResponse) {
                User user1 = userResponse.getRESULT();
                LoginFirebaseModel loginFirebaseModel = new LoginFirebaseModel();
                loginFirebaseModel.setEmail(user1.getEmail());
                loginFirebaseModel.setFullName(user1.getFullName());
                loginFirebaseModel.setTotalUnreadCount(0);
                loginFirebaseModel.setUserId(user1.getUserId());
                loginFirebaseModel.setMyTags("");
                loginFirebaseModel.setDeviceType("1");
                loginFirebaseModel.setDeviceToken(DataManager.getInstance().getDeviceToken());
                loginFirebaseModel.setProfilePicture(user1.getProfilePicture() == null ? "" : user1.getProfilePicture());
                DataManager.getInstance().createFirebaseUser(loginFirebaseModel);
                if (userResponse.getRESULT().getBalance() != null)
                    DataManager.getInstance().saveCashOutBalance(String.valueOf(userResponse.getRESULT().getBalance()));
                saveUserToPreference(userResponse.getRESULT());
                Sift.setUserId(user1.getUserId());
                liveData.setValue(userResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });

    }

    // This Api is use for socialLogin
    public void hitSocialLoginApi(final RichMediatorLiveData<UserResponse> liveData, HashMap<String, Object> user) {

        DataManager.getInstance().hitSocialLoginApi(user).enqueue(new NetworkCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse userResponse) {
                User user1 = userResponse.getRESULT();
                LoginFirebaseModel loginFirebaseModel = new LoginFirebaseModel();
                loginFirebaseModel.setEmail(user1.getEmail());
                loginFirebaseModel.setFullName(user1.getFullName());
                loginFirebaseModel.setTotalUnreadCount(0);
                loginFirebaseModel.setUserId(user1.getUserId());
                loginFirebaseModel.setMyTags("");
                loginFirebaseModel.setDeviceType("1");
                loginFirebaseModel.setDeviceToken(DataManager.getInstance().getDeviceToken());
                loginFirebaseModel.setProfilePicture(user1.getProfilePicture() == null ? "" : user1.getProfilePicture());
                DataManager.getInstance().createFirebaseUser(loginFirebaseModel);
                saveUserToPreference(userResponse.getRESULT());
                Sift.setUserId(user1.getUserId());
                liveData.setValue(userResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });

    }


    private void saveUserToPreference(User user) {
        if (user != null) {
            DataManager.getInstance().saveAccessToken(user.getAccessToken());
            DataManager.getInstance().saveRefreshToken(user.getRefreshToken());

            DataManager.getInstance().saveDob(user.getDob());
            if (user.getGetBankDetailsModel() != null) {
                DataManager.getInstance().saveAccountHolderName(user.getGetBankDetailsModel().getAccountDetailsBean().getAccountHolderName());
                DataManager.getInstance().saveAccountNumber(user.getGetBankDetailsModel().getAccountDetailsBean().getAccountNumber());
                DataManager.getInstance().saveRoutingNumber(user.getGetBankDetailsModel().getAccountDetailsBean().getRoutingNumber());
            }

            if (user.getMerchantId() != null)
                DataManager.getInstance().saveMerchentId(user.getMerchantId());

            if (user != null && user.getSsnNumber() != null)
                DataManager.getInstance().saveSSNNumber(user.getSsnNumber());
            DataManager.getInstance().saveUserDetails(user);
            if (user != null)
                DataManager.getInstance().saveLoginType(user.getUserType());
            DataManager.getInstance().saveIsPassport(user.isPassport());
            DataManager.getInstance().savePhoneVerified(user.isPhoneVerified());
            DataManager.getInstance().saveIsMuteStatus(user.isMute());
            if (user.getPhoneNumber() != null && user.getPhoneNumber().length() > 0)
                DataManager.getInstance().savePhonenNumber(user.getPhoneNumber());
            if (user.getAddressData() != null) {
                if (user.getAddressData().getAddressLineOne() != null && user.getAddressData().getAddressLineOne().length() > 0)
                    DataManager.getInstance().saveAddressLineOne(user.getAddressData().getAddressLineOne());
                if (user.getAddressData().getAddressLineTwo() != null && user.getAddressData().getAddressLineTwo().length() > 0)
                    DataManager.getInstance().saveAddressLineTwo(user.getAddressData().getAddressLineTwo());
                if (user.getAddressData().getCity() != null && user.getAddressData().getCity().length() > 0)
                    DataManager.getInstance().saveAddressCity(user.getAddressData().getCity());
                if (user.getAddressData().getPostalCode() != null && user.getAddressData().getPostalCode().length() > 0)
                    DataManager.getInstance().saveAddressPostalCode(user.getAddressData().getPostalCode());
                if (user.getAddressData().getState() != null && user.getAddressData().getState().length() > 0)
                    DataManager.getInstance().saveAddressstate(user.getAddressData().getState());
            }
        }
    }

    public void saveDeviceToken(String deviceToken) {
        //save device token to shared preference using data manager
        DataManager.getInstance().saveDeviceToken(deviceToken);
    }

    public void setLoadingObserver(Observer<Boolean> lodingObserver) {
        this.lodingObserver = lodingObserver;

    }

    // This Api is use for Guest Login
    public void hitGuestLoginApi(final RichMediatorLiveData<UserResponse> liveData, HashMap<String, String> parms) {

        DataManager.getInstance().hitGuestLoginApi(parms).enqueue(new NetworkCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse userResponse) {
                saveUserToPreference(userResponse.getRESULT());
                liveData.setValue(userResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });

    }

    public void checkSocialLogin(final RichMediatorLiveData<CheckSocialLoginmodel> liveData, HashMap<String, String> parms) {

        DataManager.getInstance().checkSocialLogin(parms).enqueue(new NetworkCallback<CheckSocialLoginmodel>() {
            @Override
            public void onSuccess(CheckSocialLoginmodel userResponse) {
                liveData.setValue(userResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });

    }
}
