
package com.taghawk.model.profileresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

public class BalanceResponse extends CommonResponse {

    @SerializedName("data")
    @Expose
    private BalanceData balanceData;

    public BalanceData getBalanceData() {
        return balanceData;
    }

    public void setBalanceData(BalanceData balanceData) {
        this.balanceData = balanceData;
    }
}
