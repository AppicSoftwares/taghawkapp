package com.taghawk.model.cart;

import com.taghawk.model.commonresponse.CommonResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CartModel extends CommonResponse {
    @SerializedName("data")
    private ArrayList<CartDataBean> mCartList;

    public ArrayList<CartDataBean> getmCartList() {
        return mCartList;
    }

    public void setmCartList(ArrayList<CartDataBean> mCartList) {
        this.mCartList = mCartList;
    }
}
