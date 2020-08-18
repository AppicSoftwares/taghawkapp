package com.taghawk.model.refreshtoken;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by appinventiv on 21/8/18.
 */

public class RefreshTokenResult {
    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;
    @SerializedName("token")
    @Expose
    private String token;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
