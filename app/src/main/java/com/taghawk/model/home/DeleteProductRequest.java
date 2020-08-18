package com.taghawk.model.home;

import com.google.gson.annotations.SerializedName;

public class DeleteProductRequest {

    @SerializedName("productId")
    private String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
