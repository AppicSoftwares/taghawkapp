package com.taghawk.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

@IgnoreExtraProperties
public class ChatModel implements Parcelable {

    private String chatType;
    private boolean chatMute;
    private boolean pinned;
    private MessageModel lastMessage;
    private ChatProductModel productInfo;
    private String roomId;
    private String roomImage;
    private String roomName;
    private String userType;
    @Exclude
    private HashMap<String,MemberModel> members;
    private String otherUserId;
    private int unreadMessageCount;
    private Object createdTimeStamp;
    private boolean blocked;
    private boolean mute;

    public boolean getMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public ChatProductModel getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(ChatProductModel productInfo) {
        this.productInfo = productInfo;
    }

    public Object getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    @Exclude
    public long getCreatedTimeStampLong() {
        return (long) createdTimeStamp;
    }

    public void setCreatedTimeStamp(Object object) {
        this.createdTimeStamp = object;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public boolean isChatMute() {
        return chatMute;
    }

    public void setChatMute(boolean chatMute) {
        this.chatMute = chatMute;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public MessageModel getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Exclude
    public HashMap<String, MemberModel> getMembers() {
        return members;
    }

    @Exclude
    public void setMembers(HashMap<String, MemberModel> members) {
        this.members = members;
    }

    public ChatModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.chatType);
        dest.writeByte(this.chatMute ? (byte) 1 : (byte) 0);
        dest.writeByte(this.pinned ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.lastMessage, flags);
        dest.writeParcelable(this.productInfo, flags);
        dest.writeString(this.roomId);
        dest.writeString(this.roomImage);
        dest.writeString(this.roomName);
        dest.writeString(this.userType);
        dest.writeString(this.otherUserId);
        dest.writeInt(this.unreadMessageCount);
        dest.writeByte(this.blocked?(byte)1:(byte)0);
        dest.writeByte(this.mute?(byte)1:(byte)0);
    }

    protected ChatModel(Parcel in) {
        this.chatType = in.readString();
        this.chatMute = in.readByte() != 0;
        this.pinned = in.readByte() != 0;
        this.lastMessage = in.readParcelable(MessageModel.class.getClassLoader());
        this.productInfo = in.readParcelable(ChatProductModel.class.getClassLoader());
        this.roomId = in.readString();
        this.roomImage = in.readString();
        this.roomName = in.readString();
        this.userType = in.readString();
        this.otherUserId = in.readString();
        this.unreadMessageCount = in.readInt();
        this.blocked = in.readByte() != 0;
        this.mute = in.readByte() != 0;
    }

    public static final Creator<ChatModel> CREATOR = new Creator<ChatModel>() {
        @Override
        public ChatModel createFromParcel(Parcel source) {
            return new ChatModel(source);
        }

        @Override
        public ChatModel[] newArray(int size) {
            return new ChatModel[size];
        }
    };
}
