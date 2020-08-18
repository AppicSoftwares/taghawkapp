package com.taghawk.model.home;

import com.taghawk.model.commonresponse.CommonResponse;
import com.google.gson.annotations.SerializedName;

public class ProductDetailsModel extends CommonResponse {

    @SerializedName("data")
    private ProductDetailsData mProductList;

    public ProductDetailsData getmProductList() {
        return mProductList;
    }

    public void setmProductList(ProductDetailsData mProductList) {
        this.mProductList = mProductList;
    }
}
