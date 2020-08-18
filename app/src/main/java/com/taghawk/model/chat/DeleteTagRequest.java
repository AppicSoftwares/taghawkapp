package com.taghawk.model.chat;

import com.google.gson.annotations.SerializedName;

public class DeleteTagRequest {

    @SerializedName("communityId")
    private String communityId;

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }
}
