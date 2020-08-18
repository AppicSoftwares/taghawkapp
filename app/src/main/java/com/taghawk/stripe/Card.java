
package com.taghawk.stripe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Card implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("object")
    @Expose
    private String object;
    @SerializedName("address_city")
    @Expose
    private String addressCity;
    @SerializedName("address_country")
    @Expose
    private String addressCountry;
    @SerializedName("address_line1")
    @Expose
    private String addressLine1;
    @SerializedName("address_line1_check")
    @Expose
    private String addressLine1Check;
    @SerializedName("address_line2")
    @Expose
    private String addressLine2;
    @SerializedName("address_state")
    @Expose
    private String addressState;
    @SerializedName("address_zip")
    @Expose
    private String addressZip;
    @SerializedName("address_zip_check")
    @Expose
    private String addressZipCheck;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("cvc_check")
    @Expose
    private String cvcCheck;
    @SerializedName("dynamic_last4")
    @Expose
    private String dynamicLast4;
    @SerializedName("exp_month")
    @Expose
    private Integer expMonth;
    @SerializedName("exp_year")
    @Expose
    private Integer expYear;
    @SerializedName("funding")
    @Expose
    private String funding;
    @SerializedName("last4")
    @Expose
    private String last4;
    //    @SerializedName("metadata")
//    @Expose
//    private Metadata metadata;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("tokenization_method")
    @Expose
    private String tokenizationMethod;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Object getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine1Check() {
        return addressLine1Check;
    }

    public void setAddressLine1Check(String addressLine1Check) {
        this.addressLine1Check = addressLine1Check;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getAddressZip() {
        return addressZip;
    }

    public void setAddressZip(String addressZip) {
        this.addressZip = addressZip;
    }

    public String getAddressZipCheck() {
        return addressZipCheck;
    }

    public void setAddressZipCheck(String addressZipCheck) {
        this.addressZipCheck = addressZipCheck;
    }

    public static Creator<Card> getCREATOR() {
        return CREATOR;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCvcCheck() {
        return cvcCheck;
    }

    public void setCvcCheck(String cvcCheck) {
        this.cvcCheck = cvcCheck;
    }

    public String getDynamicLast4() {
        return dynamicLast4;
    }

    public void setDynamicLast4(String dynamicLast4) {
        this.dynamicLast4 = dynamicLast4;
    }

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

//    public Metadata getMetadata() {
//        return metadata;
//    }
//
//    public void setMetadata(Metadata metadata) {
//        this.metadata = metadata;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTokenizationMethod() {
        return tokenizationMethod;
    }

    public void setTokenizationMethod(String tokenizationMethod) {
        this.tokenizationMethod = tokenizationMethod;
    }

    public Card() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.object);
        dest.writeString(this.addressCity);
        dest.writeString(this.addressCountry);
        dest.writeString(this.addressLine1);
        dest.writeString(this.addressLine1Check);
        dest.writeString(this.addressLine2);
        dest.writeString(this.addressState);
        dest.writeString(this.addressZip);
        dest.writeString(this.addressZipCheck);
        dest.writeString(this.brand);
        dest.writeString(this.country);
        dest.writeString(this.cvcCheck);
        dest.writeString(this.dynamicLast4);
        dest.writeValue(this.expMonth);
        dest.writeValue(this.expYear);
        dest.writeString(this.funding);
        dest.writeString(this.last4);
        dest.writeString(this.name);
        dest.writeString(this.tokenizationMethod);
    }

    protected Card(Parcel in) {
        this.id = in.readString();
        this.object = in.readString();
        this.addressCity = in.readString();
        this.addressCountry = in.readString();
        this.addressLine1 = in.readString();
        this.addressLine1Check = in.readString();
        this.addressLine2 = in.readString();
        this.addressState = in.readString();
        this.addressZip = in.readString();
        this.addressZipCheck = in.readString();
        this.brand = in.readString();
        this.country = in.readString();
        this.cvcCheck = in.readParcelable(Object.class.getClassLoader());
        this.dynamicLast4 = in.readString();
        this.expMonth = (Integer) in.readValue(Integer.class.getClassLoader());
        this.expYear = (Integer) in.readValue(Integer.class.getClassLoader());
        this.funding = in.readString();
        this.last4 = in.readString();
        this.name = in.readString();
        this.tokenizationMethod = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}
