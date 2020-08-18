package com.taghawk.model;

import com.google.gson.annotations.SerializedName;

public class CommonDataResponse{
    @SerializedName("versionInfo")
    private CommonResponseData commonResponseData;

    public CommonResponseData getCommonResponseData() {
        return commonResponseData;
    }

    public void setCommonResponseData(CommonResponseData commonResponseData) {
        this.commonResponseData = commonResponseData;
    }
}
