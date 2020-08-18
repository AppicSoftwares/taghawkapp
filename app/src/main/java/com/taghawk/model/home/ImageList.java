package com.taghawk.model.home;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ImageList implements Parcelable {

    @SerializedName("thumbUrl")
    private String thumbUrl;
    @SerializedName("url")
    private String url;
    int type;
    int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ImageList() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.thumbUrl);
        dest.writeString(this.url);
        dest.writeInt(this.type);
        dest.writeInt(this.position);
    }

    protected ImageList(Parcel in) {
        this.thumbUrl = in.readString();
        this.url = in.readString();
        this.type = in.readInt();
        this.position = in.readInt();
    }

    public static final Creator<ImageList> CREATOR = new Creator<ImageList>() {
        @Override
        public ImageList createFromParcel(Parcel source) {
            return new ImageList(source);
        }

        @Override
        public ImageList[] newArray(int size) {
            return new ImageList[size];
        }
    };
}
