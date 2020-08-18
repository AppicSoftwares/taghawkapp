package com.taghawk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressUpdateResponse {

    @Expose
    @SerializedName("data")
    private List<BillingAddressDataItem> data;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("statusCode")
    private int statusCode;

    public List<BillingAddressDataItem> getData() {
        return data;
    }

    public void setData(List<BillingAddressDataItem> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}
