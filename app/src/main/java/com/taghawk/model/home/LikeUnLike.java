package com.taghawk.model.home;

import com.taghawk.model.commonresponse.CommonResponse;
import com.google.gson.annotations.SerializedName;

public class LikeUnLike extends CommonResponse {
    @SerializedName("data")
    LikeUnLikeModel likeUnLikeModel;

    public LikeUnLikeModel getLikeUnLikeModel() {
        return likeUnLikeModel;
    }

    public void setLikeUnLikeModel(LikeUnLikeModel likeUnLikeModel) {
        this.likeUnLikeModel = likeUnLikeModel;
    }

}
