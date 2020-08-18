package com.taghawk.model;

import com.google.gson.annotations.SerializedName;

public class DeleteAddressRequest {

    @SerializedName("shipId")
    private String shipId;

    public String getShipId() {
        return shipId;
    }

    public void setShipId(String shipId) {
        this.shipId = shipId;
    }
}
