package com.taghawk.model.request;

public class ChangePassword {

    private String oldPassword;
    private String password;

    public ChangePassword( String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.password = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
