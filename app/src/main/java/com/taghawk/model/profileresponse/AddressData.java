package com.taghawk.model.profileresponse;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressData implements Serializable {
    @SerializedName("state")
    private String state;
    @SerializedName("city")
    private String city;
    @SerializedName("postal_code")
    private String postalCode;
    @SerializedName("line1")
    private String addressLineOne;
    @SerializedName("line2")
    private String addressLineTwo = "";

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddressLineOne() {
        return addressLineOne;
    }

    public void setAddressLineOne(String addressLineOne) {
        this.addressLineOne = addressLineOne;
    }

    public String getAddressLineTwo() {
        return addressLineTwo;
    }

    public void setAddressLineTwo(String addressLineTwo) {
        this.addressLineTwo = addressLineTwo;
    }

    public AddressData() {
    }
}
