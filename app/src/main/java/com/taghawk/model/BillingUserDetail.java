package com.taghawk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.taghawk.model.profileresponse.AddressData;

import java.io.Serializable;
import java.util.List;

public class BillingUserDetail {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("isFacebookLogin")
    @Expose
    private Boolean isFacebookLogin;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("isEmailVerified")
    @Expose
    private Boolean isEmailVerified;
    @SerializedName("userType")
    @Expose
    private List<Integer> userType = null;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("invitationCode")
    @Expose
    private String invitationCode;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("created")
    @Expose
    private long created;
    @SerializedName("followers")
    @Expose
    private Integer followers;
    @SerializedName("following")
    @Expose
    private Integer following;
    @SerializedName("profileCompleted")
    @Expose
    private Integer profileCompleted;
    @SerializedName("profilePicture")
    @Expose
    private String profilePicture;
    @SerializedName("sellerVerified")
    @Expose
    private Boolean sellerVerified;
    //    @SerializedName("address")
//    @Expose
//    private String address;
    @SerializedName("long")
    @Expose
    private Integer _long;
    @SerializedName("lat")
    @Expose
    private Integer lat;
    @SerializedName("isPhoneVerified")
    @Expose
    private Boolean isPhoneVerified;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("isDocumentsVerified")
    @Expose
    private Boolean officialIdVerified;
    @SerializedName("govtId")
    @Expose
    private String govtId;
    @SerializedName("sellerRating")
    @Expose
    private Double sellerRating;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;

    @SerializedName("shareUrl")
    private String shareUrl;

    @SerializedName("isFollowing")
    private boolean isFollowing;
    @SerializedName("isFollower")
    private boolean isFollower;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("dob")
    private String dob;

    @SerializedName("ssnNumber")
    private String ssnNumber;

    @SerializedName("drivingLicense")
    private String drivingLicense;

    @SerializedName("passportNumber")
    private String passportNumber;

    @SerializedName("passportCountry")
    private String passportCountry;

    @SerializedName("balance")
    private Double balance;

    @SerializedName("address")
    private AddressData addressData;

    @SerializedName("billingaddress")
    private BillingAddressDataItem billingAddress;

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getPassportCountry() {
        return passportCountry;
    }

    public void setPassportCountry(String passportCountry) {
        this.passportCountry = passportCountry;
    }

    public BillingAddressDataItem getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(BillingAddressDataItem billingAddress) {
        this.billingAddress = billingAddress;
    }

    public AddressData getAddressData() {
        return addressData;
    }

    public void setAddressData(AddressData addressData) {
        this.addressData = addressData;
    }

    public Double getCashOutBalance() {
        return cashOutBalance;
    }

    public void setCashOutBalance(Double cashOutBalance) {
        this.cashOutBalance = cashOutBalance;
    }

    @SerializedName("cashOutBalance")
    private Double cashOutBalance;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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

    public Boolean getFacebookLogin() {
        return isFacebookLogin;
    }

    public void setFacebookLogin(Boolean facebookLogin) {
        isFacebookLogin = facebookLogin;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Double getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(Double sellerRating) {
        this.sellerRating = sellerRating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsFacebookLogin() {
        return isFacebookLogin;
    }

    public void setIsFacebookLogin(Boolean isFacebookLogin) {
        this.isFacebookLogin = isFacebookLogin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public List<Integer> getUserType() {
        return userType;
    }

    public void setUserType(List<Integer> userType) {
        this.userType = userType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getFollowing() {
        return following;
    }

    public void setFollowing(Integer following) {
        this.following = following;
    }

    public Integer getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(Integer profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Boolean getSellerVerified() {
        return sellerVerified;
    }

    public void setSellerVerified(Boolean sellerVerified) {
        this.sellerVerified = sellerVerified;
    }

//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }

    public Integer getLong() {
        return _long;
    }

    public void setLong(Integer _long) {
        this._long = _long;
    }

    public Integer getLat() {
        return lat;
    }

    public void setLat(Integer lat) {
        this.lat = lat;
    }

    public Boolean getIsPhoneVerified() {
        return isPhoneVerified;
    }

    public void setIsPhoneVerified(Boolean isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getOfficialIdVerified() {
        return officialIdVerified;
    }

    public void setOfficialIdVerified(Boolean officialIdVerified) {
        this.officialIdVerified = officialIdVerified;
    }

    public String getGovtId() {
        return govtId;
    }

    public void setGovtId(String govtId) {
        this.govtId = govtId;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public Integer get_long() {
        return _long;
    }

    public void set_long(Integer _long) {
        this._long = _long;
    }

    public Boolean getPhoneVerified() {
        return isPhoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        isPhoneVerified = phoneVerified;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}