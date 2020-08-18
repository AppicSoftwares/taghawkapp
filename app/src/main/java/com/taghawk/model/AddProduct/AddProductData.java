package com.taghawk.model.AddProduct;
import android.os.Parcel;
import android.os.Parcelable;

import com.taghawk.model.home.ImageList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AddProductData implements Parcelable {

    @SerializedName("_id")
    private String id;
    @SerializedName("viewCount")
    private int viewCount;
    @SerializedName("shareCount")
    private int shareCount;
    @SerializedName("firmPrice")
    private double firmPrice;
    @SerializedName("isNegotiable")
    private boolean isNegotiable;
    @SerializedName("condition")
    private int condition;
    @SerializedName("shippingAvailibility")
    private int[] shippingType;
    @SerializedName("status")
    private int status;
    @SerializedName("title")
    private String title;
    @SerializedName("userId")
    private String userId;
    @SerializedName("images")
    private ArrayList<ImageList> images;
    @SerializedName("productCategoryId")
    private String productCategoryId;
    @SerializedName("description")
    private String description;
    @SerializedName("created")
    private String created;
    @SerializedName("link")
    private String shareLink;
    @SerializedName("isPromoted")
    @Expose
    private Boolean isPromoted;
    @SerializedName("sellerVerified")
    @Expose
    private Boolean sellerVerified;

    private boolean loading;

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public Boolean getPromoted() {
        return isPromoted;
    }

    public void setPromoted(Boolean promoted) {
        isPromoted = promoted;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public double getFirmPrice() {
        return firmPrice;
    }

    public void setFirmPrice(double firmPrice) {
        this.firmPrice = firmPrice;
    }

    public boolean isNegotiable() {
        return isNegotiable;
    }

    public void setNegotiable(boolean negotiable) {
        isNegotiable = negotiable;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int[] getShippingType() {
        return shippingType;
    }

    public void setShippingType(int[] shippingType) {
        this.shippingType = shippingType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<ImageList> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageList> images) {
        this.images = images;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public AddProductData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.viewCount);
        dest.writeInt(this.shareCount);
        dest.writeDouble(this.firmPrice);
        dest.writeByte(this.isNegotiable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.condition);
        dest.writeIntArray(this.shippingType);
        dest.writeInt(this.status);
        dest.writeString(this.title);
        dest.writeString(this.userId);
        dest.writeTypedList(this.images);
        dest.writeString(this.productCategoryId);
        dest.writeString(this.description);
        dest.writeString(this.created);
        dest.writeString(this.shareLink);
        dest.writeValue(this.isPromoted);
        dest.writeValue(this.sellerVerified);
        dest.writeByte(this.loading ? (byte) 1 : (byte) 0);
    }

    protected AddProductData(Parcel in) {
        this.id = in.readString();
        this.viewCount = in.readInt();
        this.shareCount = in.readInt();
        this.firmPrice = in.readDouble();
        this.isNegotiable = in.readByte() != 0;
        this.condition = in.readInt();
        this.shippingType = in.createIntArray();
        this.status = in.readInt();
        this.title = in.readString();
        this.userId = in.readString();
        this.images = in.createTypedArrayList(ImageList.CREATOR);
        this.productCategoryId = in.readString();
        this.description = in.readString();
        this.created = in.readString();
        this.shareLink = in.readString();
        this.isPromoted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.sellerVerified = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.loading = in.readByte() != 0;
    }

    public static final Creator<AddProductData> CREATOR = new Creator<AddProductData>() {
        @Override
        public AddProductData createFromParcel(Parcel source) {
            return new AddProductData(source);
        }

        @Override
        public AddProductData[] newArray(int size) {
            return new AddProductData[size];
        }
    };
}
