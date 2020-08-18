package com.taghawk.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class ChatProductModel implements Parcelable {
    private String productImage="";
    private String productName="";
    private double productPrice=0;
    private String productId="";
    @Exclude
    private int productStatus;

    @Exclude
    public int getProductStatus() {
        return productStatus;
    }

    @Exclude
    public void setProductStatus(int productStatus) {
        this.productStatus = productStatus;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productImage);
        dest.writeString(this.productName);
        dest.writeDouble(this.productPrice);
        dest.writeString(this.productId);
        dest.writeInt(this.productStatus);
    }

    public ChatProductModel() {
    }

    protected ChatProductModel(Parcel in) {
        this.productImage = in.readString();
        this.productName = in.readString();
        this.productPrice = in.readDouble();
        this.productId = in.readString();
        this.productStatus = in.readInt();
    }

    public static final Parcelable.Creator<ChatProductModel> CREATOR = new Parcelable.Creator<ChatProductModel>() {
        @Override
        public ChatProductModel createFromParcel(Parcel source) {
            return new ChatProductModel(source);
        }

        @Override
        public ChatProductModel[] newArray(int size) {
            return new ChatProductModel[size];
        }
    };
}
