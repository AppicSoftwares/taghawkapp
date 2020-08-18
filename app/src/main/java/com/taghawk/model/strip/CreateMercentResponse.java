package com.taghawk.model.strip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateMercentResponse {

    @SerializedName("statusCode")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    private String merchentId;

    public String getMerchentId() {
        return merchentId;
    }

    public void setMerchentId(String merchentId) {
        this.merchentId = merchentId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
