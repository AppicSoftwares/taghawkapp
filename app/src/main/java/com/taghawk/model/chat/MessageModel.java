package com.taghawk.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class MessageModel implements Parcelable {
    private String messageId;
    private String messageStatus;
    private String messageText;
    private String messageType;
    private String roomId;
    private String senderId;
    private long timeStamp;
    private String loadingImageOnAmazon;
    private String senderName;
    private String senderImage;
    private int readCount;
    private int memberCount;
    private String shareId;
    private String shareImage;

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getShareImage() {
        return shareImage;
    }

    public void setShareImage(String shareImage) {
        this.shareImage = shareImage;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getLoadingImageOnAmazon() {
        return loadingImageOnAmazon;
    }

    public void setLoadingImageOnAmazon(String loadingImageOnAmazon) {
        this.loadingImageOnAmazon = loadingImageOnAmazon;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    @Exclude
    public long getTimeStampLong() {
        return (long)timeStamp;
    }

    public void setTimeStamp(Object object) {
        this.timeStamp = (long) object;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.messageId);
        dest.writeString(this.messageStatus);
        dest.writeString(this.messageText);
        dest.writeString(this.messageType);
        dest.writeString(this.roomId);
        dest.writeString(this.senderId);
        dest.writeString(this.senderName);
        dest.writeString(this.senderImage);
        dest.writeInt(this.readCount);
        dest.writeInt(this.memberCount);
        dest.writeString(this.shareId);
        dest.writeString(this.shareImage);
    }

    public MessageModel() {
    }

    protected MessageModel(Parcel in) {
        this.messageId = in.readString();
        this.messageStatus = in.readString();
        this.messageText = in.readString();
        this.messageType = in.readString();
        this.roomId = in.readString();
        this.senderId = in.readString();
        this.senderName = in.readString();
        this.senderImage = in.readString();
        this.memberCount = in.readInt();
        this.readCount = in.readInt();
        this.shareId=in.readString();
        this.shareImage=in.readString();
    }

    public static final Parcelable.Creator<MessageModel> CREATOR = new Parcelable.Creator<MessageModel>() {
        @Override
        public MessageModel createFromParcel(Parcel source) {
            return new MessageModel(source);
        }

        @Override
        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };
}
