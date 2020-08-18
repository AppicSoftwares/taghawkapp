package com.taghawk.model.cashout;

import com.google.gson.annotations.SerializedName;

public class VerificationSSNData {
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
