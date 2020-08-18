package com.taghawk.model;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

import java.util.ArrayList;

public class PaymentHistoryModel extends CommonResponse {

    @SerializedName("data")
    private ArrayList<PaymentHistoryData> paymentHistoryList;

    public ArrayList<PaymentHistoryData> getPaymentHistoryList() {
        return paymentHistoryList;
    }

    public void setPaymentHistoryList(ArrayList<PaymentHistoryData> paymentHistoryList) {
        this.paymentHistoryList = paymentHistoryList;
    }
}
