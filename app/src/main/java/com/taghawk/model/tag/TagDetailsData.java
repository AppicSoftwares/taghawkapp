package com.taghawk.model.tag;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.home.ProductListModel;

import java.util.ArrayList;

public class TagDetailsData implements Parcelable {

    @SerializedName("_id")
    private String tagId;
    @SerializedName("description")
    private String tagDescription;
    @SerializedName("type")
    private int tagType;
    @SerializedName("subType")
    private int subType;
    @SerializedName("name")
    private String tagName;
    //     @SerializedName("users")
    @SerializedName("created")
    private String created;
    @SerializedName("tagImageUrl")
    private String tagImageUrl;
    @SerializedName("tagAddress")
    private String tagAddress;
    @SerializedName("members")
    private int tagTotalMembers;
    @SerializedName("isMember")
    private boolean isMember;
    @SerializedName("tagLongitude")
    private Double tagLongitude;
    @SerializedName("tagLatitude")
    private double tagLatitude;
    @SerializedName("productInfo")
    private ArrayList<ProductListModel> mTagProducts;
    @SerializedName("adminName")
    private String tagFounderName;
    @SerializedName("link")
    private String shareLink;
    @SerializedName("ownerName")
    private String ownerName;
    @SerializedName("joinTagBy")
    private int joinTagBy;

    @SerializedName("email")
    private String email;
    @SerializedName("document_type")
    private String document_type;
    @SerializedName("isCreatedByMe")
    private boolean isCreatedByMe;
    @SerializedName("requestStatus")
    private int requestStatus;
    @SerializedName("announcement")
    private String announcement;
    @SerializedName("tagJoinData")
    private String tagJoinData;

    public String getTagJoinData() {
        return tagJoinData;
    }

    public void setTagJoinData(String tagJoinData) {
        this.tagJoinData = tagJoinData;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public int isRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public static Creator<TagDetailsData> getCREATOR() {
        return CREATOR;
    }

    public boolean isCreatedByMe() {
        return isCreatedByMe;
    }

    public void setCreatedByMe(boolean createdByMe) {
        isCreatedByMe = createdByMe;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getJoinTagBy() {
        return joinTagBy;
    }

    public void setJoinTagBy(int joinTagBy) {
        this.joinTagBy = joinTagBy;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public String getTagFounderName() {
        return tagFounderName;
    }

    public void setTagFounderName(String tagFounderName) {
        this.tagFounderName = tagFounderName;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagDescription() {
        return tagDescription;
    }

    public void setTagDescription(String tagDescription) {
        this.tagDescription = tagDescription;
    }

    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getTagImageUrl() {
        return tagImageUrl;
    }

    public void setTagImageUrl(String tagImageUrl) {
        this.tagImageUrl = tagImageUrl;
    }

    public String getTagAddress() {
        return tagAddress;
    }

    public void setTagAddress(String tagAddress) {
        this.tagAddress = tagAddress;
    }

    public int getTagTotalMembers() {
        return tagTotalMembers;
    }

    public void setTagTotalMembers(int tagTotalMembers) {
        this.tagTotalMembers = tagTotalMembers;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public Double getTagLongitude() {
        return tagLongitude;
    }

    public void setTagLongitude(Double tagLongitude) {
        this.tagLongitude = tagLongitude;
    }

    public double getTagLatitude() {
        return tagLatitude;
    }

    public void setTagLatitude(double tagLatitude) {
        this.tagLatitude = tagLatitude;
    }

    public ArrayList<ProductListModel> getmTagProducts() {
        return mTagProducts;
    }

    public void setmTagProducts(ArrayList<ProductListModel> mTagProducts) {
        this.mTagProducts = mTagProducts;
    }

    public TagDetailsData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tagId);
        dest.writeString(this.tagDescription);
        dest.writeInt(this.tagType);
        dest.writeInt(this.subType);
        dest.writeString(this.tagName);
        dest.writeString(this.created);
        dest.writeString(this.tagImageUrl);
        dest.writeString(this.tagAddress);
        dest.writeInt(this.tagTotalMembers);
        dest.writeByte(this.isMember ? (byte) 1 : (byte) 0);
        dest.writeValue(this.tagLongitude);
        dest.writeDouble(this.tagLatitude);
        dest.writeTypedList(this.mTagProducts);
        dest.writeString(this.tagFounderName);
        dest.writeString(this.shareLink);
        dest.writeString(this.ownerName);
        dest.writeInt(this.joinTagBy);
        dest.writeString(this.email);
        dest.writeString(this.document_type);
        dest.writeByte(this.isCreatedByMe ? (byte) 1 : (byte) 0);
        dest.writeInt(this.requestStatus);
        dest.writeString(this.announcement);
        dest.writeString(this.tagJoinData);
    }

    protected TagDetailsData(Parcel in) {
        this.tagId = in.readString();
        this.tagDescription = in.readString();
        this.tagType = in.readInt();
        this.subType = in.readInt();
        this.tagName = in.readString();
        this.created = in.readString();
        this.tagImageUrl = in.readString();
        this.tagAddress = in.readString();
        this.tagTotalMembers = in.readInt();
        this.isMember = in.readByte() != 0;
        this.tagLongitude = (Double) in.readValue(Double.class.getClassLoader());
        this.tagLatitude = in.readDouble();
        this.mTagProducts = in.createTypedArrayList(ProductListModel.CREATOR);
        this.tagFounderName = in.readString();
        this.shareLink = in.readString();
        this.ownerName = in.readString();
        this.joinTagBy = in.readInt();
        this.email = in.readString();
        this.document_type = in.readString();
        this.isCreatedByMe = in.readByte() != 0;
        this.requestStatus = in.readInt();
        this.announcement = in.readString();
        this.tagJoinData = in.readString();
    }

    public static final Creator<TagDetailsData> CREATOR = new Creator<TagDetailsData>() {
        @Override
        public TagDetailsData createFromParcel(Parcel source) {
            return new TagDetailsData(source);
        }

        @Override
        public TagDetailsData[] newArray(int size) {
            return new TagDetailsData[size];
        }
    };
}
