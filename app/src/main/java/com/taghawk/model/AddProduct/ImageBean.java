package com.taghawk.model.AddProduct;

import android.os.Parcel;
import android.os.Parcelable;

import com.taghawk.model.home.ImageList;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ImageBean extends ArrayList<String> implements Parcelable {
    @SerializedName("images")
    private ArrayList<ImageList> imageList;

    public ArrayList<ImageList> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageList> imageList) {
        this.imageList = imageList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.imageList);
    }

    public ImageBean() {
    }

    protected ImageBean(Parcel in) {
        this.imageList = in.createTypedArrayList(ImageList.CREATOR);
    }

    public static final Parcelable.Creator<ImageBean> CREATOR = new Parcelable.Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(Parcel source) {
            return new ImageBean(source);
        }

        @Override
        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };
}
