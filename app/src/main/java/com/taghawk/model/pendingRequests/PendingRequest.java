
package com.taghawk.model.pendingRequests;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PendingRequest {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("joinTagBy")
    @Expose
    private Integer joinTagBy;
    @SerializedName("requestParameter")
    @Expose
    private String requestParameter;
    @SerializedName("documentUrl")
    @Expose
    private List<String> documentUrl = null;
    @SerializedName("requestStatus")
    @Expose
    private Integer requestStatus;
    @SerializedName("isSenderVerified")
    @Expose
    private Boolean isSenderVerified;
    @SerializedName("senderId")
    @Expose
    private String senderId;
    @SerializedName("created")
    @Expose
    private long created;
    @SerializedName("senderName")
    @Expose
    private String senderName;
    @SerializedName("senderProfilePic")
    @Expose
    private String senderProfilePic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getJoinTagBy() {
        return joinTagBy;
    }

    public void setJoinTagBy(Integer joinTagBy) {
        this.joinTagBy = joinTagBy;
    }

    public String getRequestParameter() {
        return requestParameter;
    }

    public void setRequestParameter(String requestParameter) {
        this.requestParameter = requestParameter;
    }

    public List<String> getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(List<String> documentUrl) {
        this.documentUrl = documentUrl;
    }

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Boolean getIsSenderVerified() {
        return isSenderVerified;
    }

    public void setIsSenderVerified(Boolean isSenderVerified) {
        this.isSenderVerified = isSenderVerified;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderProfilePic() {
        return senderProfilePic;
    }

    public void setSenderProfilePic(String senderProfilePic) {
        this.senderProfilePic = senderProfilePic;
    }

}
