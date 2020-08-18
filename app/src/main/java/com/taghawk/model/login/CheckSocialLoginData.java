package com.taghawk.model.login;

import com.google.gson.annotations.SerializedName;

public class CheckSocialLoginData {

    @SerializedName("isExist")
    private boolean isExist;

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }
}
