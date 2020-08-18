package com.taghawk.model.strip;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

public class FedexRateResponse extends CommonResponse {

    @SerializedName("data")
    private FedexRateData fedexRateData;

    public FedexRateData getFedexRateData() {
        return fedexRateData;
    }

    public void setFedexRateData(FedexRateData fedexRateData) {
        this.fedexRateData = fedexRateData;
    }
}
