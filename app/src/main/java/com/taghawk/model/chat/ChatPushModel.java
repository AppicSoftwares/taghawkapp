package com.taghawk.model.chat;

import com.google.gson.annotations.SerializedName;

public class ChatPushModel {
    @SerializedName("type")
    private String type;
    @SerializedName("title")
    private String title;
    @SerializedName("entityId")
    private String entityId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
