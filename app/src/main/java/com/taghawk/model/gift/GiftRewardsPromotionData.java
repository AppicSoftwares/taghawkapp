package com.taghawk.model.gift;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class GiftRewardsPromotionData implements Parcelable {


    @SerializedName("_id")
    private String id;
    @SerializedName("isActive")
    private boolean isActive;
    @SerializedName("days")
    private int days;
    @SerializedName("rewardPoint")
    private int rewardPoints;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeByte(this.isActive ? (byte) 1 : (byte) 0);
        dest.writeInt(this.days);
        dest.writeInt(this.rewardPoints);
    }

    public GiftRewardsPromotionData() {
    }

    protected GiftRewardsPromotionData(Parcel in) {
        this.id = in.readString();
        this.isActive = in.readByte() != 0;
        this.days = in.readInt();
        this.rewardPoints = in.readInt();
    }

    public static final Parcelable.Creator<GiftRewardsPromotionData> CREATOR = new Parcelable.Creator<GiftRewardsPromotionData>() {
        @Override
        public GiftRewardsPromotionData createFromParcel(Parcel source) {
            return new GiftRewardsPromotionData(source);
        }

        @Override
        public GiftRewardsPromotionData[] newArray(int size) {
            return new GiftRewardsPromotionData[size];
        }
    };
}
