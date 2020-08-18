package com.taghawk.model.update_rating_notification;

import com.google.gson.annotations.SerializedName;

public class RatingData {

    @SerializedName("_id")
    private String id;
    @SerializedName("sellerId")
    private String sellerId;
    @SerializedName("productId")
    private String productId;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("profilePicture")
    private String profilePicture;
    @SerializedName("title")
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
