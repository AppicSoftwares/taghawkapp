package com.taghawk.model.AddProduct;

import android.os.Parcel;
import android.os.Parcelable;

import com.taghawk.model.commonresponse.CommonResponse;
import com.google.gson.annotations.SerializedName;

public class AddProductModel extends CommonResponse implements Parcelable {
    @SerializedName("data")
    private AddProductData mAddProductData;

    public AddProductData getmAddProductData() {
        return mAddProductData;
    }

    public void setmAddProductData(AddProductData mAddProductData) {
        this.mAddProductData = mAddProductData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mAddProductData, flags);
    }

    public AddProductModel() {
    }

    protected AddProductModel(Parcel in) {
        this.mAddProductData = in.readParcelable(AddProductData.class.getClassLoader());
    }

    public static final Parcelable.Creator<AddProductModel> CREATOR = new Parcelable.Creator<AddProductModel>() {
        @Override
        public AddProductModel createFromParcel(Parcel source) {
            return new AddProductModel(source);
        }

        @Override
        public AddProductModel[] newArray(int size) {
            return new AddProductModel[size];
        }
    };
}
