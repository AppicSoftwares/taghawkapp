package com.taghawk.model.refreshtoken;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by appinventiv on 21/8/18.
 */

public class RefreshTokenResponse {
    @SerializedName("CODE")
    @Expose
    private int cODE;
    @SerializedName("MESSAGE")
    @Expose
    private String mESSAGE;
    @SerializedName("RESULT")
    @Expose
    private RefreshTokenResult rESULT;

    public int getCODE() {
        return cODE;
    }

    public void setCODE(int cODE) {
        this.cODE = cODE;
    }

    public String getMESSAGE() {
        return mESSAGE;
    }

    public void setMESSAGE(String mESSAGE) {
        this.mESSAGE = mESSAGE;
    }

    public RefreshTokenResult getRefreshTokenResult() {
        return rESULT;
    }

    public void setRefreshTokenResult(RefreshTokenResult rESULT) {
        this.rESULT = rESULT;
    }
}
