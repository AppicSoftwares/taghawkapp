package com.taghawk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BillingAddressDataItem implements Parcelable {

    @Expose
    @SerializedName("selectedStatus")
    private int selectedStatus;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("phone")
    private String phone;
    @Expose
    @SerializedName("country")
    private String country;
    @Expose
    @SerializedName("state")
    private String state;
    @Expose
    @SerializedName("street1")
    private String street1;
    @Expose
    @SerializedName("street2")
    private String street2;
    @Expose
    @SerializedName("postal_code")
    private String postal_code;
    @Expose
    @SerializedName("city")
    private String city;
    @Expose
    @SerializedName("email")
    private String email;
    @Expose
    @SerializedName("contact_name")
    private String contact_name;
    @Expose
    @SerializedName("_id")
    private String _id;

    public BillingAddressDataItem() {
    }

    BillingAddressDataItem(Parcel in) {
        selectedStatus = in.readInt();
        type = in.readString();
        phone = in.readString();
        country = in.readString();
        state = in.readString();
        street1 = in.readString();
        street2 = in.readString();
        postal_code = in.readString();
        city = in.readString();
        email = in.readString();
        contact_name = in.readString();
        _id = in.readString();
    }

    public static final Creator<BillingAddressDataItem> CREATOR = new Creator<BillingAddressDataItem>() {
        @Override
        public BillingAddressDataItem createFromParcel(Parcel in) {
            return new BillingAddressDataItem(in);
        }

        @Override
        public BillingAddressDataItem[] newArray(int size) {
            return new BillingAddressDataItem[size];
        }
    };

    public int getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(int selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(selectedStatus);
        dest.writeString(type);
        dest.writeString(phone);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeString(street1);
        dest.writeString(street2);
        dest.writeString(postal_code);
        dest.writeString(city);
        dest.writeString(email);
        dest.writeString(contact_name);
        dest.writeString(_id);
    }
}
