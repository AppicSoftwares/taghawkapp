package com.taghawk.model.update_rating_notification;

import com.google.gson.annotations.SerializedName;

public class UpdateRatingNotificationData {

    @SerializedName("notification")
    private NotificationCount notificationCount;

    @SerializedName("ratingData")
    private RatingData ratingData;

    public RatingData getRatingData() {
        return ratingData;
    }

    public void setRatingData(RatingData ratingData) {
        this.ratingData = ratingData;
    }

    public NotificationCount getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(NotificationCount notificationCount) {
        this.notificationCount = notificationCount;
    }
}
