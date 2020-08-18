package com.taghawk.model;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

public class PaymentRefundModel extends CommonResponse {
    @SerializedName("data")
    private PaymentHistoryData mData;

    public PaymentHistoryData getmData() {
        return mData;
    }

    public void setmData(PaymentHistoryData mData) {
        this.mData = mData;
    }
}
