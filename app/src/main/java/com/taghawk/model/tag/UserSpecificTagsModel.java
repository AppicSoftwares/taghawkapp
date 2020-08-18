package com.taghawk.model.tag;

import com.taghawk.model.commonresponse.CommonResponse;
import com.google.gson.annotations.SerializedName;

public class UserSpecificTagsModel extends CommonResponse {
    @SerializedName("data")
    private TagModel userTagInfo;

    public TagModel getUserTagInfo() {
        return userTagInfo;
    }

    public void setUserTagInfo(TagModel userTagInfo) {
        this.userTagInfo = userTagInfo;
    }
}
