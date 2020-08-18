package com.taghawk.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

public class MemberModel implements Parcelable {
    private String memberId;
    private String memberName;
    private String memberImage;
    private int memberType;
    private boolean blocked;
    private boolean mute;

    public boolean getMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberImage() {
        return memberImage;
    }

    public void setMemberImage(String memberImage) {
        this.memberImage = memberImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.memberId);
        dest.writeString(this.memberName);
        dest.writeString(this.memberImage);
        dest.writeInt(this.memberType);
        dest.writeByte(this.blocked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mute ? (byte) 1 : (byte) 0);
    }

    public MemberModel() {
    }

    protected MemberModel(Parcel in) {
        this.memberId = in.readString();
        this.memberName = in.readString();
        this.memberImage = in.readString();
        this.memberType = in.readInt();
        this.blocked = in.readByte() != 0;
        this.mute = in.readByte() != 0;
    }

    public static final Parcelable.Creator<MemberModel> CREATOR = new Parcelable.Creator<MemberModel>() {
        @Override
        public MemberModel createFromParcel(Parcel source) {
            return new MemberModel(source);
        }

        @Override
        public MemberModel[] newArray(int size) {
            return new MemberModel[size];
        }
    };
}
