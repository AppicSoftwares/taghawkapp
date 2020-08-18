package com.taghawk.model.strip;

import com.google.gson.annotations.SerializedName;

public class RateBean {
    @SerializedName("amount")
    private double amount;
    @SerializedName("currency")
    private String currency;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
