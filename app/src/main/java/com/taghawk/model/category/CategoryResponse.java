package com.taghawk.model.category;

import com.taghawk.model.commonresponse.CommonResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */

public class CategoryResponse extends CommonResponse {
    @SerializedName("data")
    @Expose
    private ArrayList<CategoryListResponse> mCategory;
    private boolean showDialog;

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public ArrayList<CategoryListResponse> getmCategory() {
        return mCategory;
    }

    public void setmCategory(ArrayList<CategoryListResponse> mCategory) {
        this.mCategory = mCategory;
    }
}
