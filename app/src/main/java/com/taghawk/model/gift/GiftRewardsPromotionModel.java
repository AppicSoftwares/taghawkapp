package com.taghawk.model.gift;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GiftRewardsPromotionModel implements Parcelable {

    @SerializedName("statusCode")
    private int statusCode;
    @SerializedName("message")
    private String message;
    @SerializedName("rewardPoint")
    private int rewardPoint;
    @SerializedName("data")
    private ArrayList<GiftRewardsPromotionData> mPromotionList;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRewardPoint() {
        return rewardPoint;
    }

    public void setRewardPoint(int rewardPoint) {
        this.rewardPoint = rewardPoint;
    }

    public ArrayList<GiftRewardsPromotionData> getmPromotionList() {
        return mPromotionList;
    }

    public void setmPromotionList(ArrayList<GiftRewardsPromotionData> mPromotionList) {
        this.mPromotionList = mPromotionList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.statusCode);
        dest.writeString(this.message);
        dest.writeInt(this.rewardPoint);
        dest.writeTypedList(this.mPromotionList);
    }

    public GiftRewardsPromotionModel() {
    }

    protected GiftRewardsPromotionModel(Parcel in) {
        this.statusCode = in.readInt();
        this.message = in.readString();
        this.rewardPoint = in.readInt();
        this.mPromotionList = in.createTypedArrayList(GiftRewardsPromotionData.CREATOR);
    }

    public static final Parcelable.Creator<GiftRewardsPromotionModel> CREATOR = new Parcelable.Creator<GiftRewardsPromotionModel>() {
        @Override
        public GiftRewardsPromotionModel createFromParcel(Parcel source) {
            return new GiftRewardsPromotionModel(source);
        }

        @Override
        public GiftRewardsPromotionModel[] newArray(int size) {
            return new GiftRewardsPromotionModel[size];
        }
    };
}
