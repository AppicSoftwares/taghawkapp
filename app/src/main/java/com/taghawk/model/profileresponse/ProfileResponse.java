
package com.taghawk.model.profileresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

import java.io.Serializable;

public class ProfileResponse extends CommonResponse implements Serializable {

    @SerializedName("data")
    @Expose
    private UserDetail userDetail;


    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }
}
