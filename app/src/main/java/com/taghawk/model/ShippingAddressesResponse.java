package com.taghawk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShippingAddressesResponse implements Parcelable {

    @Expose
    @SerializedName("data")
    private List<BillingAddressDataItem> data;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("statusCode")
    private int statusCode;

    protected ShippingAddressesResponse(Parcel in) {
        message = in.readString();
        statusCode = in.readInt();
    }

    public static final Creator<ShippingAddressesResponse> CREATOR = new Creator<ShippingAddressesResponse>() {
        @Override
        public ShippingAddressesResponse createFromParcel(Parcel in) {
            return new ShippingAddressesResponse(in);
        }

        @Override
        public ShippingAddressesResponse[] newArray(int size) {
            return new ShippingAddressesResponse[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeInt(statusCode);
    }


}
