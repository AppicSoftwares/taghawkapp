package com.taghawk.model.singup;

import com.taghawk.constants.AppConstants;
import com.google.gson.annotations.SerializedName;

/**
 * Created by appinventiv on 21/1/19.
 */

public class SignupModel {

    @SerializedName("fullName")
    private String fullName;
    @SerializedName(AppConstants.KEY_CONSTENT.EMAIL)
    private String email;
    @SerializedName(AppConstants.KEY_CONSTENT.PASSWORD)
    private String password;
    @SerializedName(AppConstants.KEY_CONSTENT.DEVICE_ID)
    private String deviceId;
    @SerializedName(AppConstants.KEY_CONSTENT.USER_TYPE)
    private String userType;
    @SerializedName(AppConstants.KEY_CONSTENT.DEVICETOKEN)
    private String deviceToken;
    @SerializedName("invitationCode")
    private String invitationCode;

    public SignupModel(String fullName, String email, String password, String deviceId, String userType, String deviceToken, String invitationCode) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.invitationCode = invitationCode;
        this.deviceId = deviceId;
        this.userType = userType;
        this.deviceToken = deviceToken;

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
}
