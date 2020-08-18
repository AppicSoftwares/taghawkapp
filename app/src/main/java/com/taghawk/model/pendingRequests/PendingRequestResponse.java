
package com.taghawk.model.pendingRequests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

public class PendingRequestResponse extends CommonResponse {

    @SerializedName("data")
    @Expose
    private PendingRequestResult pendingRequestResult;

    public PendingRequestResult getPendingRequestResult() {
        return pendingRequestResult;
    }

    public void setPendingRequestResult(PendingRequestResult pendingRequestResult) {
        this.pendingRequestResult = pendingRequestResult;
    }
}
