package com.taghawk.model.tag;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TagData implements Parcelable {
    @SerializedName("_id")
    private String tagId;
    @SerializedName("description")
    private String description;
    @SerializedName("type")
    private int tagType;
    @SerializedName("status")
    private int tagStatus;
    @SerializedName("name")
    private String tagName;
    @SerializedName("created")
    private String created;
    @SerializedName("tagAddress")
    private String tagAddress;
    @SerializedName("tagImageUrl")
    private String tagImageUrl;
    @SerializedName("members")
    private int totalMembers;
    @SerializedName("isMember")
    private boolean isTagMember;
    @SerializedName("tagLongitude")
    private String tagLongitude;
    @SerializedName("tagLatitude")
    private String tagLatitude;
    @SerializedName("adminName")
    private String adminName;
    @SerializedName("adminEmail")
    private String adminEmail;
    @SerializedName("shareCode")
    @Expose
    private String shareCode;

    @SerializedName("link")
    private String shareLink;
    @SerializedName("joinTagBy")
    private int joinTagBy;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("document_type")
    private String documentType;
    @SerializedName("tagJoinData")
    private String joinTagData;
    @SerializedName("announcement")
    private String announcement;

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getJoinTagData() {
        return joinTagData;
    }

    public void setJoinTagData(String joinTagData) {
        this.joinTagData = joinTagData;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public int getJoinTagBy() {
        return joinTagBy;
    }

    public void setJoinTagBy(int joinTagBy) {
        this.joinTagBy = joinTagBy;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    private boolean isSelected = true;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public int getTagStatus() {
        return tagStatus;
    }

    public void setTagStatus(int tagStatus) {
        this.tagStatus = tagStatus;
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

    public String getTagAddress() {
        return tagAddress;
    }

    public void setTagAddress(String tagAddress) {
        this.tagAddress = tagAddress;
    }

    public String getTagImageUrl() {
        return tagImageUrl;
    }

    public void setTagImageUrl(String tagImageUrl) {
        this.tagImageUrl = tagImageUrl;
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }

    public boolean isTagMember() {
        return isTagMember;
    }

    public void setTagMember(boolean tagMember) {
        isTagMember = tagMember;
    }

    public String getTagLongitude() {
        return tagLongitude;
    }

    public void setTagLongitude(String tagLongitude) {
        this.tagLongitude = tagLongitude;
    }

    public String getTagLatitude() {
        return tagLatitude;
    }

    public void setTagLatitude(String tagLatitude) {
        this.tagLatitude = tagLatitude;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public TagData() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tagId);
        dest.writeString(this.description);
        dest.writeInt(this.tagType);
        dest.writeInt(this.tagStatus);
        dest.writeString(this.tagName);
        dest.writeString(this.created);
        dest.writeString(this.tagAddress);
        dest.writeString(this.tagImageUrl);
        dest.writeInt(this.totalMembers);
        dest.writeByte(this.isTagMember ? (byte) 1 : (byte) 0);
        dest.writeString(this.tagLongitude);
        dest.writeString(this.tagLatitude);
        dest.writeString(this.adminName);
        dest.writeString(this.adminEmail);
        dest.writeString(this.shareCode);
        dest.writeString(this.shareLink);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.documentType);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.joinTagBy);
        dest.writeString(this.joinTagData);
    }

    protected TagData(Parcel in) {
        this.tagId = in.readString();
        this.description = in.readString();
        this.tagType = in.readInt();
        this.tagStatus = in.readInt();
        this.tagName = in.readString();
        this.created = in.readString();
        this.tagAddress = in.readString();
        this.tagImageUrl = in.readString();
        this.totalMembers = in.readInt();
        this.isTagMember = in.readByte() != 0;
        this.tagLongitude = in.readString();
        this.tagLatitude = in.readString();
        this.adminName = in.readString();
        this.adminEmail = in.readString();
        this.shareCode = in.readString();
        this.shareLink = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.documentType = in.readString();
        this.isSelected = in.readByte() != 0;
        this.joinTagBy = in.readInt();
        this.joinTagData = in.readString();
    }

    public static final Creator<TagData> CREATOR = new Creator<TagData>() {
        @Override
        public TagData createFromParcel(Parcel source) {
            return new TagData(source);
        }

        @Override
        public TagData[] newArray(int size) {
            return new TagData[size];
        }
    };
}
