package com.taghawk.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;


public class TagDetailFirebaseModel implements Parcelable {
    private String tagId;
    private String description;
    private int tagType;
    private int tagStatus;
    private String tagName;
    private String tagAddress;
    private String tagImageUrl;
    private HashMap<String,MemberModel> members;
    private double tagLongitude;
    private double tagLatitude;
    private String shareCode;
    private String shareLink;
    private int verificationType;
    private String verificationData;
    private MessageModel lastMessage;
    private int pendingRequestCount;
    private String ownerId;
    private String announcement;

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getPendingRequestCount() {
        return pendingRequestCount;
    }

    public void setPendingRequestCount(int pendingRequestCount) {
        this.pendingRequestCount = pendingRequestCount;
    }

    public MessageModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(int verificationType) {
        this.verificationType = verificationType;
    }

    public String getVerificationData() {
        return verificationData;
    }

    public void setVerificationData(String verificationData) {
        this.verificationData = verificationData;
    }

    public HashMap<String, MemberModel> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, MemberModel> members) {
        this.members = members;
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

    public double getTagLongitude() {
        return tagLongitude;
    }

    public void setTagLongitude(double tagLongitude) {
        this.tagLongitude = tagLongitude;
    }

    public double getTagLatitude() {
        return tagLatitude;
    }

    public void setTagLatitude(double tagLatitude) {
        this.tagLatitude = tagLatitude;
    }

    public TagDetailFirebaseModel() {
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
        dest.writeString(this.tagAddress);
        dest.writeString(this.tagImageUrl);
        dest.writeDouble(this.tagLongitude);
        dest.writeDouble(this.tagLatitude);
        dest.writeString(this.shareCode);
        dest.writeString(this.shareLink);
        dest.writeInt(this.verificationType);
        dest.writeInt(this.pendingRequestCount);
        dest.writeString(this.verificationData);
        dest.writeString(this.ownerId);
        dest.writeString(this.announcement);
        dest.writeParcelable(this.lastMessage, flags);
    }

    protected TagDetailFirebaseModel(Parcel in) {
        this.tagId = in.readString();
        this.description = in.readString();
        this.tagType = in.readInt();
        this.tagStatus = in.readInt();
        this.tagName = in.readString();
        this.tagAddress = in.readString();
        this.tagImageUrl = in.readString();
        this.tagLongitude = in.readDouble();
        this.tagLatitude = in.readDouble();
        this.shareCode = in.readString();
        this.shareLink = in.readString();
        this.verificationType = in.readInt();
        this.pendingRequestCount = in.readInt();
        this.verificationData = in.readString();
        this.ownerId = in.readString();
        this.announcement = in.readString();
        this.lastMessage = in.readParcelable(MessageModel.class.getClassLoader());
    }

    public static final Creator<TagDetailFirebaseModel> CREATOR = new Creator<TagDetailFirebaseModel>() {
        @Override
        public TagDetailFirebaseModel createFromParcel(Parcel source) {
            return new TagDetailFirebaseModel(source);
        }

        @Override
        public TagDetailFirebaseModel[] newArray(int size) {
            return new TagDetailFirebaseModel[size];
        }
    };
}
