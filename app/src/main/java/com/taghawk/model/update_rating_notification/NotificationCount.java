package com.taghawk.model.update_rating_notification;

import com.google.gson.annotations.SerializedName;

public class NotificationCount {


    @SerializedName("unreadCount")
    private int unreadCount;
    @SerializedName("readCount")
    private int readCount;

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
