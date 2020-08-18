package com.taghawk.model.cashout;

import com.google.gson.annotations.SerializedName;

public class MerchantDetailData {


    @SerializedName("id")
    private String id;
    @SerializedName("object")
    private String type;
    @SerializedName("charges_enabled")
    private String chargesEnabled;
    @SerializedName("external_accounts")
    private ExternalAccountData externalAccountData;
    @SerializedName("payouts_enabled")
    private boolean payoutsEnabled;
    @SerializedName("isAddressVerified")
    private boolean isAddressVerified;
    @SerializedName("isInfo")
    private boolean isInfo;
    @SerializedName("name")
    private boolean isNameVerified;
    @SerializedName("passport")
    private boolean isPassportVerified;
    @SerializedName("ssn_last_4_provided")
    private boolean isSSNLast4Provided;
    @SerializedName("verified")
    private boolean ssnVerified;
    @SerializedName("verification")
    private VerificationSSNData verificationSSNData;

    public VerificationSSNData getVerificationSSNData() {
        return verificationSSNData;
    }

    public void setVerificationSSNData(VerificationSSNData verificationSSNData) {
        this.verificationSSNData = verificationSSNData;
    }

    public boolean isSsnVerified() {
        return ssnVerified;
    }

    public void setSsnVerified(boolean ssnVerified) {
        this.ssnVerified = ssnVerified;
    }

    public boolean isNameVerified() {
        return isNameVerified;

    }


    public void setNameVerified(boolean nameVerified) {
        isNameVerified = nameVerified;
    }

    public boolean isPassportVerified() {
        return isPassportVerified;
    }

    public void setPassportVerified(boolean passportVerified) {
        isPassportVerified = passportVerified;
    }

    public boolean isSSNLast4Provided() {
        return isSSNLast4Provided;
    }

    public void setSSNLast4Provided(boolean SSNLast4Provided) {
        isSSNLast4Provided = SSNLast4Provided;
    }

    public boolean isInfo() {
        return isInfo;
    }

    public void setInfo(boolean info) {
        isInfo = info;
    }

    public boolean isAddressVerified() {
        return isAddressVerified;
    }

    public void setAddressVerified(boolean addressVerified) {
        isAddressVerified = addressVerified;
    }

    public boolean isPayoutsEnabled() {
        return payoutsEnabled;
    }

    public void setPayoutsEnabled(boolean payoutsEnabled) {
        this.payoutsEnabled = payoutsEnabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChargesEnabled() {
        return chargesEnabled;
    }

    public void setChargesEnabled(String chargesEnabled) {
        this.chargesEnabled = chargesEnabled;
    }

    public ExternalAccountData getExternalAccountData() {
        return externalAccountData;
    }

    public void setExternalAccountData(ExternalAccountData externalAccountData) {
        this.externalAccountData = externalAccountData;
    }
}
