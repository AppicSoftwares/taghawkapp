package com.taghawk.model.login;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginFirebaseModel implements Parcelable {
    private String email;
    private String fullName;
    private int totalUnreadCount;
    private String userId;
    private String myTags;
    private String deviceToken;
    private String deviceType;
    private String profilePicture;

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getMyTags() {
        return myTags;
    }

    public void setMyTags(String myTags) {
        this.myTags = myTags;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getTotalUnreadCount() {
        return totalUnreadCount;
    }

    public void setTotalUnreadCount(int totalUnreadCount) {
        this.totalUnreadCount = totalUnreadCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LoginFirebaseModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.fullName);
        dest.writeInt(this.totalUnreadCount);
        dest.writeString(this.userId);
        dest.writeString(this.myTags);
        dest.writeString(this.deviceToken);
        dest.writeString(this.deviceType);
        dest.writeString(this.profilePicture);
    }

    protected LoginFirebaseModel(Parcel in) {
        this.email = in.readString();
        this.fullName = in.readString();
        this.totalUnreadCount = in.readInt();
        this.userId = in.readString();
        this.myTags = in.readString();
        this.deviceToken = in.readString();
        this.deviceType = in.readString();
        this.profilePicture = in.readString();
    }

    public static final Creator<LoginFirebaseModel> CREATOR = new Creator<LoginFirebaseModel>() {
        @Override
        public LoginFirebaseModel createFromParcel(Parcel source) {
            return new LoginFirebaseModel(source);
        }

        @Override
        public LoginFirebaseModel[] newArray(int size) {
            return new LoginFirebaseModel[size];
        }
    };
}
