package com.taghawk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.profileresponse.UserDetail;

import java.io.Serializable;

public class BillingProfileResponse extends CommonResponse {

    @SerializedName("data")
    @Expose
    private BillingUserDetail userDetail;


    public BillingUserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(BillingUserDetail userDetail) {
        this.userDetail = userDetail;
    }
}
