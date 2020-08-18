package com.taghawk.model.review_rating;

import com.google.gson.annotations.SerializedName;

public class ReviewRatingData {

    @SerializedName("_id")
    private String commentId;
    @SerializedName("userId")
    private String buyerUserId;
    @SerializedName("profilePicture")
    private String buyerPicture;
    @SerializedName("created")
    private long commentTime;
    @SerializedName("rating")
    private float commentRating;
    @SerializedName("comment")
    private String commentMsg;
    @SerializedName("replied")
    private ReplyData replyComment;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("title")
    private String productName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    private boolean isReplyShowing;

    public boolean isReplyShowing() {
        return isReplyShowing;
    }

    public void setReplyShowing(boolean replyShowing) {
        isReplyShowing = replyShowing;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public String getBuyerPicture() {
        return buyerPicture;
    }

    public void setBuyerPicture(String buyerPicture) {
        this.buyerPicture = buyerPicture;
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(long commentTime) {
        this.commentTime = commentTime;
    }

    public float getCommentRating() {
        return commentRating;
    }

    public void setCommentRating(float commentRating) {
        this.commentRating = commentRating;
    }

    public String getCommentMsg() {
        return commentMsg;
    }

    public void setCommentMsg(String commentMsg) {
        this.commentMsg = commentMsg;
    }

    public ReplyData getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(ReplyData replyComment) {
        this.replyComment = replyComment;
    }
}
