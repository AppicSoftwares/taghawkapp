package com.taghawk.model.home;

import com.google.gson.annotations.SerializedName;

public class LikeUnLikeModel {

    @SerializedName("isLiked")
    private boolean isLiked;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
