package com.taghawk.Repository;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.login.LoginFirebaseModel;
import com.taghawk.model.request.User;
import com.taghawk.model.request.UserResponse;

import java.util.HashMap;

import siftscience.android.Sift;

public class SignUpRepo {

    // This Api is use for signup
    public void hitSignUpApi(final RichMediatorLiveData<UserResponse> signUpLiveData, HashMap<String, String> user) {
        DataManager.getInstance().hitSignUpApi(user).enqueue(new NetworkCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse userResponse) {
                //save data in preference
                User user1=userResponse.getRESULT();
                LoginFirebaseModel loginFirebaseModel=new LoginFirebaseModel();
                loginFirebaseModel.setEmail(user1.getEmail());
                loginFirebaseModel.setFullName(user1.getFullName());
                loginFirebaseModel.setTotalUnreadCount(0);
                loginFirebaseModel.setUserId(user1.getUserId());
                loginFirebaseModel.setMyTags("");
                loginFirebaseModel.setDeviceType("1");
                loginFirebaseModel.setDeviceToken(DataManager.getInstance().getDeviceToken());
                loginFirebaseModel.setProfilePicture(user1.getProfilePicture()==null?"":user1.getProfilePicture());
                DataManager.getInstance().createFirebaseUser(loginFirebaseModel);
                saveUserToPreference(userResponse.getRESULT());
                Sift.setUserId(user1.getUserId());
                signUpLiveData.setValue(userResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                signUpLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                signUpLiveData.setError(t);
            }
        });

    }


    public void hitSocialLoginApi(final RichMediatorLiveData<UserResponse> liveData, HashMap<String,Object> user) {

        DataManager.getInstance().hitSocialLoginApi(user).enqueue(new NetworkCallback<UserResponse>() {
            @Override
            public void onSuccess(UserResponse userResponse) {
                User user1=userResponse.getRESULT();
                LoginFirebaseModel loginFirebaseModel=new LoginFirebaseModel();
                loginFirebaseModel.setEmail(user1.getEmail());
                loginFirebaseModel.setFullName(user1.getFullName());
                loginFirebaseModel.setTotalUnreadCount(0);
                loginFirebaseModel.setUserId(user1.getUserId());
                loginFirebaseModel.setMyTags("");
                loginFirebaseModel.setDeviceType("1");
                loginFirebaseModel.setDeviceToken(DataManager.getInstance().getDeviceToken());
                loginFirebaseModel.setProfilePicture(user1.getProfilePicture()==null?"":user1.getProfilePicture());
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
            DataManager.getInstance().saveUserDetails(user);
        }
    }
}
