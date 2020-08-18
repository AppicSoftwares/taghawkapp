package com.taghawk.model.update_rating_notification;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

public class UpdateRatingNotificationBean extends CommonResponse {

    @SerializedName("data")
    private UpdateRatingNotificationData updateRatingNotificationData;

    public UpdateRatingNotificationData getUpdateRatingNotificationData() {
        return updateRatingNotificationData;
    }

    public void setUpdateRatingNotificationData(UpdateRatingNotificationData updateRatingNotificationData) {
        this.updateRatingNotificationData = updateRatingNotificationData;
    }
}
