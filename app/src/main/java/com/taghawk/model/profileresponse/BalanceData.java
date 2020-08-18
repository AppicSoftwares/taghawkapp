package com.taghawk.model.profileresponse;

import com.google.gson.annotations.SerializedName;

public class BalanceData {

    @SerializedName("availableBalance")
    private Double currentBalance;
    @SerializedName("pendingBalance")
    private Double pendingBalance;
    @SerializedName("availableSoonBalance")
    private Double availableSoonBalance;
    @SerializedName("cashOutBalance")
    private Double cashOutBalance;
    @SerializedName("pendingCashOutBalance")
    private Double pendingCashOutBalance;
    @SerializedName("totalEarningBalance")
    private Double totalEarningBalance;
    @SerializedName("vendorsId")
    private String vendorsId;
    @SerializedName("cashOutDate")
    private String cashOutDate;
    @SerializedName("cashOutTime")
    private int cashOutTime;

    public int getCashOutTime() {
        return cashOutTime;
    }

    public void setCashOutTime(int cashOutTime) {
        this.cashOutTime = cashOutTime;
    }

    public String getCashOutDate() {
        return cashOutDate;
    }

    public void setCashOutDate(String cashOutDate) {
        this.cashOutDate = cashOutDate;
    }

    public Double getPendingCashOutBalance() {
        return pendingCashOutBalance;
    }

    public void setPendingCashOutBalance(Double pendingCashOutBalance) {
        this.pendingCashOutBalance = pendingCashOutBalance;
    }

    public Double getTotalEarningBalance() {
        return totalEarningBalance;
    }

    public void setTotalEarningBalance(Double totalEarningBalance) {
        this.totalEarningBalance = totalEarningBalance;
    }

    public String getVendorsId() {
        return vendorsId;
    }

    public void setVendorsId(String vendorsId) {
        this.vendorsId = vendorsId;
    }

    public Double getAvailableSoonBalance() {
        return availableSoonBalance;
    }

    public void setAvailableSoonBalance(Double availableSoonBalance) {
        this.availableSoonBalance = availableSoonBalance;
    }

    public Double getPendingBalance() {
        return pendingBalance;
    }

    public void setPendingBalance(Double pendingBalance) {
        this.pendingBalance = pendingBalance;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Double getCashOutBalance() {
        return cashOutBalance;
    }

    public void setCashOutBalance(Double cashOutBalance) {
        this.cashOutBalance = cashOutBalance;
    }
}
