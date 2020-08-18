package com.taghawk.model.login;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

public class CheckSocialLoginmodel extends CommonResponse {
    @SerializedName("data")
    private CheckSocialLoginData checkSocialLoginData;

    public CheckSocialLoginData getCheckSocialLoginData() {
        return checkSocialLoginData;
    }

    public void setCheckSocialLoginData(CheckSocialLoginData checkSocialLoginData) {
        this.checkSocialLoginData = checkSocialLoginData;
    }
}
