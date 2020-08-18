
package com.taghawk.model.tag;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.taghawk.model.pendingRequests.PendingRequest;

import java.util.ArrayList;

public class MyTagResult {

    @SerializedName("data")
    @Expose
    private ArrayList<TagData> tagData = null;

    public ArrayList<TagData> getTagData() {
        return tagData;
    }

    public void setTagData(ArrayList<TagData> tagData) {
        this.tagData = tagData;
    }
}
