package com.taghawk.model.tag;

import com.taghawk.model.commonresponse.CommonResponse;
import com.google.gson.annotations.SerializedName;

public class TagDetailsModel extends CommonResponse {

    @SerializedName("data")
    private TagDetailsData tagDetailsData;

    public TagDetailsData getTagDetailsData() {
        return tagDetailsData;
    }

    public void setTagDetailsData(TagDetailsData tagDetailsData) {
        this.tagDetailsData = tagDetailsData;
    }
}
