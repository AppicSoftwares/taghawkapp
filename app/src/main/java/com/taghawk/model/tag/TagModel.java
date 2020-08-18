package com.taghawk.model.tag;

import com.taghawk.model.commonresponse.CommonResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TagModel extends CommonResponse {
    @SerializedName("data")
    ArrayList<TagData> mTagListData;
//    private int requestCode;
//
//    public int getRequestCode() {
//        return requestCode;
//    }
//
//    public void setRequestCode(int requestCode) {
//        this.requestCode = requestCode;
//    }

    public ArrayList<TagData> getmTagListData() {
        return mTagListData;
    }

    public void setmTagListData(ArrayList<TagData> mTagListData) {
        this.mTagListData = mTagListData;
    }
}
