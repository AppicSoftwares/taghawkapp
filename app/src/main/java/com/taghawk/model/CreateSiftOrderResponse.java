package com.taghawk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class CreateSiftOrderResponse {

    @SerializedName("statusCode")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private boolean data;
    private int requestCode = 0;

    public boolean getData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    private HashMap<String,Object> extraLocalData;

    public HashMap<String, Object> getExtraLocalData() {
        return extraLocalData;
    }

    public void setExtraLocalData(HashMap<String, Object> extraLocalData) {
        this.extraLocalData = extraLocalData;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
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
