package com.taghawk.model.strip;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

public class GetBankDetail extends CommonResponse {
    @SerializedName("data")
    private GetBankDetailsModel getBankDetailsModel;

    public GetBankDetailsModel getGetBankDetailsModel() {
        return getBankDetailsModel;
    }

    public void setGetBankDetailsModel(GetBankDetailsModel getBankDetailsModel) {
        this.getBankDetailsModel = getBankDetailsModel;
    }
}
