package com.taghawk.model.login;

import com.taghawk.constants.AppConstants;
import com.google.gson.annotations.SerializedName;

/**
 * Created by appinventiv on 21/1/19.
 */

public class LoginModel {


    @SerializedName(AppConstants.KEY_CONSTENT.EMAIL)
    private String email;
    @SerializedName(AppConstants.KEY_CONSTENT.PASSWORD)
    private String password;
    @SerializedName(AppConstants.KEY_CONSTENT.USER_TYPE)
    private String userType;
    @SerializedName(AppConstants.KEY_CONSTENT.DEVICE_ID)
    private String device_Id;
    @SerializedName(AppConstants.KEY_CONSTENT.DEVICETOKEN)
    private String deviceToken;

    public LoginModel(String email, String password, String userType, String device_Id, String deviceToken) {
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.device_Id = device_Id;
        this.deviceToken = deviceToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDevice_Id() {
        return device_Id;
    }

    public void setDevice_Id(String device_Id) {
        this.device_Id = device_Id;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
