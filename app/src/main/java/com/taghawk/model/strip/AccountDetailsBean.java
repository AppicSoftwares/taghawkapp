package com.taghawk.model.strip;

import com.google.gson.annotations.SerializedName;

public class AccountDetailsBean {

    @SerializedName("routing_number")
    private String routingNumber;
    @SerializedName("_id")
    private String id;
    @SerializedName("account_holder_name")
    private String accountHolderName;
    @SerializedName("account_holder_type")
    private String accountHolderType;
    @SerializedName("account_number")
    private String accountNumber;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountHolderType() {
        return accountHolderType;
    }

    public void setAccountHolderType(String accountHolderType) {
        this.accountHolderType = accountHolderType;
    }
}
