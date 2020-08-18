package com.taghawk.model.cashout;

import com.google.gson.annotations.SerializedName;

public class ExternalAccountCardData {
    @SerializedName("id")
    private String cardId;
    @SerializedName("object")
    private String objectType;
    @SerializedName("account")
    private String account;
    @SerializedName("last4")
    private String last4;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }
}
