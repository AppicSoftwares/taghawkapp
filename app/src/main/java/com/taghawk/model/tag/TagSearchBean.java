package com.taghawk.model.tag;

import com.taghawk.model.commonresponse.CommonResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TagSearchBean extends CommonResponse {

    @SerializedName("data")
    ArrayList<TagData> mTagSearchList;

    public ArrayList<TagData> getmTagSearchList() {
        return mTagSearchList;
    }

    public void setmTagSearchList(ArrayList<TagData> mTagSearchList) {
        this.mTagSearchList = mTagSearchList;
    }
}
