package com.taghawk.model.request;

public class Reset {

    private String accessToken ;
    private String password;

    public Reset(String token, String password, String confirmPassword) {
        this.accessToken = token;
        this.password = password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
