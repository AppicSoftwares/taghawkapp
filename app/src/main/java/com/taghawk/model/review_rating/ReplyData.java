package com.taghawk.model.review_rating;

import com.google.gson.annotations.SerializedName;

public class ReplyData {

    @SerializedName("replyDate")
    private long replyDate;
    @SerializedName("replyComment")
    private String replyComment;
    @SerializedName("editedStatus")
    private boolean editedStatus;
    @SerializedName("editedDate")
    private long editedDate;

    public long getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(long replyDate) {
        this.replyDate = replyDate;
    }

    public String getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(String replyComment) {
        this.replyComment = replyComment;
    }

    public boolean isEditedStatus() {
        return editedStatus;
    }

    public void setEditedStatus(boolean editedStatus) {
        this.editedStatus = editedStatus;
    }

    public long getEditedDate() {
        return editedDate;
    }

    public void setEditedDate(long editedDate) {
        this.editedDate = editedDate;
    }
}

