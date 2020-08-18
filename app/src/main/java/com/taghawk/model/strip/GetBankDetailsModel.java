package com.taghawk.model.strip;

import com.google.gson.annotations.SerializedName;

public class GetBankDetailsModel {

    @SerializedName("_id")
    private String id;
    @SerializedName("userId")
    private String userId;
    @SerializedName("accountDetails")
    private AccountDetailsBean accountDetailsBean;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AccountDetailsBean getAccountDetailsBean() {
        return accountDetailsBean;
    }

    public void setAccountDetailsBean(AccountDetailsBean accountDetailsBean) {
        this.accountDetailsBean = accountDetailsBean;
    }
}
