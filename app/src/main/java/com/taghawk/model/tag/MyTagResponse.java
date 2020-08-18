
package com.taghawk.model.tag;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.pendingRequests.PendingRequestResult;

public class MyTagResponse extends CommonResponse {

    @SerializedName("data")
    @Expose
    private MyTagResult myTagResult;

    public MyTagResult getMyTagResult() {
        return myTagResult;
    }

    public void setMyTagResult(MyTagResult myTagResult) {
        this.myTagResult = myTagResult;
    }
}
