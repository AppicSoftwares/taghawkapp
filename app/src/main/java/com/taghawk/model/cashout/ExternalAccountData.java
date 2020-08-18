package com.taghawk.model.cashout;

import com.google.gson.annotations.SerializedName;

public class ExternalAccountData {

    @SerializedName("id")
    private String id;
    @SerializedName("object")
    private String accountType;
    @SerializedName("account")
    private String account;
    @SerializedName("account_holder_name")
    private String accountHolderName;
    @SerializedName("bank_name")
    private String bankName;
    @SerializedName("last4")
    private String last4;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }
}
