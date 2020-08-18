package com.taghawk.model.cashout;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

public class MerchantDetailBeans extends CommonResponse {
    @SerializedName("data")
    private MerchantDetailData merchantDetailData;

    public MerchantDetailData getMerchantDetailData() {
        return merchantDetailData;
    }

    public void setMerchantDetailData(MerchantDetailData merchantDetailData) {
        this.merchantDetailData = merchantDetailData;
    }
}
