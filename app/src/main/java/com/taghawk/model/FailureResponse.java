package com.taghawk.model;

import com.google.gson.annotations.SerializedName;

public class FailureResponse {

    private int errorCode;
    private CharSequence errorMessage;
    private String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FailureResponse() {
    }

    public FailureResponse(int errorCode, CharSequence errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public CharSequence getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
