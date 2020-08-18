
package com.taghawk.model.tagaddresponse;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.taghawk.model.tag.TagData;

public class AddTagResponse implements Parcelable {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private TagData data;

    private String editType;

    public String getEditType() {
        return editType;
    }

    public void setEditType(String editType) {
        this.editType = editType;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TagData getData() {
        return data;
    }

    public void setData(TagData data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.statusCode);
        dest.writeString(this.message);
        dest.writeString(this.editType);
        dest.writeParcelable(this.data, flags);
    }

    public AddTagResponse() {
    }

    protected AddTagResponse(Parcel in) {
        this.statusCode = (Integer) in.readValue(Integer.class.getClassLoader());
        this.message = in.readString();
        this.data = in.readParcelable(TagData.class.getClassLoader());
        this.editType=in.readString();
    }

    public static final Parcelable.Creator<AddTagResponse> CREATOR = new Parcelable.Creator<AddTagResponse>() {
        @Override
        public AddTagResponse createFromParcel(Parcel source) {
            return new AddTagResponse(source);
        }

        @Override
        public AddTagResponse[] newArray(int size) {
            return new AddTagResponse[size];
        }
    };
}
