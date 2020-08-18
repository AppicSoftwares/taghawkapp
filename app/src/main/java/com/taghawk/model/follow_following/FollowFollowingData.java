package com.taghawk.model.follow_following;

import com.google.gson.annotations.SerializedName;

public class FollowFollowingData {
    @SerializedName("_id")
    private String id;
    @SerializedName("requestStatus")
    private int requestStatus;
    @SerializedName("senderId")
    private String senderId;
    @SerializedName("receiverId")
    private String receiverId;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("sellerVerified")
    private boolean sellerVerified;
    @SerializedName("isFollowing")
    private boolean isFollowing;
    @SerializedName("isFollower")
    private boolean isFollower;
    @SerializedName("profilePicture")
    private String profilePicture;
    @SerializedName("userId")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isSellerVerified() {
        return sellerVerified;
    }

    public void setSellerVerified(boolean sellerVerified) {
        this.sellerVerified = sellerVerified;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
