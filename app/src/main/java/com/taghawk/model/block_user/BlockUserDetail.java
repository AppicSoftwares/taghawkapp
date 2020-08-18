package com.taghawk.model.block_user;

import com.google.gson.annotations.SerializedName;

public class BlockUserDetail {
    @SerializedName("_id")
    private String id;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("email")
    private String email;
    @SerializedName("profilePicture")
    private String profilePicture;

    private boolean isUnBlock;

    public boolean isUnBlock() {
        return isUnBlock;
    }

    public void setUnBlock(boolean unBlock) {
        isUnBlock = unBlock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
