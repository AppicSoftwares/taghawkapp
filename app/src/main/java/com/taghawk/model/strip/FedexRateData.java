package com.taghawk.model.strip;

import com.google.gson.annotations.SerializedName;

public class FedexRateData {

    @SerializedName("total_charge")
    private RateBean rateBean;

    public RateBean getRateBean() {
        return rateBean;
    }

    public void setRateBean(RateBean rateBean) {
        this.rateBean = rateBean;
    }
}
