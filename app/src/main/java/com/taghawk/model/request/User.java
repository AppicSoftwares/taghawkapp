package com.taghawk.model.request;

import com.google.gson.annotations.SerializedName;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.profileresponse.AddressData;
import com.taghawk.model.strip.GetBankDetailsModel;

public class User {

    @SerializedName(AppConstants.KEY_CONSTENT.ACCESS_TOKEN)
    private String accessToken;
    @SerializedName(AppConstants.KEY_CONSTENT.FULL_NAME)
    private String fullName;
    @SerializedName(AppConstants.KEY_CONSTENT.EMAIL)
    private String email;
    @SerializedName(AppConstants.KEY_CONSTENT.INVITATION_CODE)
    private String invitationCode;
    @SerializedName(AppConstants.KEY_CONSTENT.REFRESH_TOKEN)
    private String refreshToken;
    @SerializedName("userType")
    private int userType;
    @SerializedName(AppConstants.KEY_CONSTENT.USER_ID)
    private String userId;
    @SerializedName("ssnNumber")
    private String ssnNumber;
    @SerializedName("dob")
    private String dob;
    @SerializedName("bankDetail")
    private GetBankDetailsModel getBankDetailsModel;
    @SerializedName("stripeMerchantId")
    private String merchantId;
    @SerializedName("profilePicture")
    private String profilePicture;

    @SerializedName("isMute")
    private boolean isMute;

    @SerializedName("balance")
    private Double balance;

    @SerializedName("passport")
    private boolean isPassport;
    @SerializedName("isPhoneVerified")
    private boolean isPhoneVerified;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("loginType")
    private String loginType;

    @SerializedName("address")
    private AddressData addressData;

    public AddressData getAddressData() {
        return addressData;
    }

    public void setAddressData(AddressData addressData) {
        this.addressData = addressData;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isPhoneVerified() {
        return isPhoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        isPhoneVerified = phoneVerified;
    }

    public boolean isPassport() {
        return isPassport;
    }

    public void setPassport(boolean passport) {
        isPassport = passport;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getSsnNumber() {
        return ssnNumber;
    }

    public void setSsnNumber(String ssnNumber) {
        this.ssnNumber = ssnNumber;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public GetBankDetailsModel getGetBankDetailsModel() {
        return getBankDetailsModel;
    }

    public void setGetBankDetailsModel(GetBankDetailsModel getBankDetailsModel) {
        this.getBankDetailsModel = getBankDetailsModel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
