package com.taghawk.model;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

public class ContentViewModel extends CommonResponse {

    @SerializedName("data")
    private ContenViewData mcontentView;

    public ContenViewData getMcontentView() {
        return mcontentView;
    }

    public void setMcontentView(ContenViewData mcontentView) {
        this.mcontentView = mcontentView;
    }
}
