package com.taghawk.model.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CartDataBean implements Parcelable {
    @SerializedName("_id")
    private String id;
    @SerializedName("totalCount")
    private int totalCount;
    @SerializedName("productId")
    private String productId;
    @SerializedName("productName")
    private String productName;
    @SerializedName("productPicUrl")
    private ArrayList<String> productPicList;
    @SerializedName("productDescription")
    private String productDescription;
    @SerializedName("sellerName")
    private String sellerName;
    @SerializedName("sellerId")
    private String sellerId;
    @SerializedName("productPrice")
    private String productPrice;
    @SerializedName("shippingAvailibility")
    private int shippingAvailibility;
    @SerializedName("productStatus")
    private int productStatus;
    @SerializedName("ownerId")
    private String ownerId;
    @SerializedName("sellerVendorId")
    private String sellerVendorId;
    @SerializedName("ownerVendorId")
    private String ownerVendorId;
    @SerializedName("sellerCommissionAmount")
    private String sellerCommissionAmount;
    @SerializedName("ownerCommissionAmount")
    private String ownerCommissionAmount;

    public int getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(int productStatus) {
        this.productStatus = productStatus;
    }

    public int getShippingAvailibility() {
        return shippingAvailibility;
    }

    public void setShippingAvailibility(int shippingAvailibility) {
        this.shippingAvailibility = shippingAvailibility;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ArrayList<String> getProductPicList() {
        return productPicList;
    }

    public void setProductPicList(ArrayList<String> productPicList) {
        this.productPicList = productPicList;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSellerVendorId() {
        return sellerVendorId;
    }

    public void setSellerVendorId(String sellerVendorId) {
        this.sellerVendorId = sellerVendorId;
    }

    public String getOwnerVendorId() {
        return ownerVendorId;
    }

    public void setOwnerVendorId(String ownerVendorId) {
        this.ownerVendorId = ownerVendorId;
    }

    public String getSellerCommissionAmount() {
        return sellerCommissionAmount;
    }

    public void setSellerCommissionAmount(String sellerCommissionAmount) {
        this.sellerCommissionAmount = sellerCommissionAmount;
    }

    public String getOwnerCommissionAmount() {
        return ownerCommissionAmount;
    }

    public void setOwnerCommissionAmount(String ownerCommissionAmount) {
        this.ownerCommissionAmount = ownerCommissionAmount;
    }

    public CartDataBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.totalCount);
        dest.writeString(this.productId);
        dest.writeString(this.productName);
        dest.writeStringList(this.productPicList);
        dest.writeString(this.productDescription);
        dest.writeString(this.sellerName);
        dest.writeString(this.sellerId);
        dest.writeString(this.productPrice);
        dest.writeInt(this.shippingAvailibility);
        dest.writeInt(this.productStatus);
        dest.writeString(this.ownerId);
        dest.writeString(this.sellerVendorId);
        dest.writeString(this.ownerVendorId);
        dest.writeString(this.sellerCommissionAmount);
        dest.writeString(this.ownerCommissionAmount);
    }

    protected CartDataBean(Parcel in) {
        this.id = in.readString();
        this.totalCount = in.readInt();
        this.productId = in.readString();
        this.productName = in.readString();
        this.productPicList = in.createStringArrayList();
        this.productDescription = in.readString();
        this.sellerName = in.readString();
        this.sellerId = in.readString();
        this.productPrice = in.readString();
        this.shippingAvailibility = in.readInt();
        this.productStatus = in.readInt();
        this.ownerId = in.readString();
        this.sellerVendorId = in.readString();
        this.ownerVendorId = in.readString();
        this.sellerCommissionAmount = in.readString();
        this.ownerCommissionAmount = in.readString();
    }

    public static final Creator<CartDataBean> CREATOR = new Creator<CartDataBean>() {
        @Override
        public CartDataBean createFromParcel(Parcel source) {
            return new CartDataBean(source);
        }

        @Override
        public CartDataBean[] newArray(int size) {
            return new CartDataBean[size];
        }
    };
}
