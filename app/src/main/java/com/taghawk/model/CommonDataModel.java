package com.taghawk.model;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

public class CommonDataModel extends CommonResponse {

    @SerializedName("data")
    private CommonDataResponse commonDataResponse;

    public CommonDataResponse getCommonDataResponse() {
        return commonDataResponse;
    }

    public void setCommonDataResponse(CommonDataResponse commonDataResponse) {
        this.commonDataResponse = commonDataResponse;
    }
}
