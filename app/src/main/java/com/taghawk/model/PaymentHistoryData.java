package com.taghawk.model;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.home.ImageList;

import java.util.ArrayList;

public class PaymentHistoryData {

    @SerializedName("_id")
    private String id;
    @SerializedName("price")
    private String price;
    @SerializedName("deliveryStatus")
    private String deliveryStatus = "";
    @SerializedName("refundId")
    private String refundId;
    @SerializedName("netPrice")
    private String netPrice;
    @SerializedName("disputeId")
    private String disputeId;
    @SerializedName("refundDate")
    private String refundDate;
    @SerializedName("declineDate")
    private String declineDate;
    @SerializedName("refundAcceptedDate")
    private String refundAcceptedDate;
    @SerializedName("disputeDate")
    private String disputeDate;
    @SerializedName("disputeCompleteDate")
    private String disputeCompleteDate;
    @SerializedName("refundRequestDate")
    private String refundRequestDate;
    @SerializedName("sellerId")
    private String sellerId;
    @SerializedName("userId")
    private String userId;
    @SerializedName("productId")
    private String productId;
    @SerializedName("title")
    private String productName;
    @SerializedName("images")
    private ArrayList<ImageList> imageLists;
    @SerializedName("sellerName")
    private String sellerName;
    @SerializedName("sellerProfilePicture")
    private String sellerpic;
    @SerializedName("productStatus")
    private int productStatus;
    @SerializedName("reason")
    private String reason = "";
    @SerializedName("userName")
    private String buyerName;
    @SerializedName("profilePicture")
    private String buyerPic;
    @SerializedName("sellerDisputeStatus")
    private int sellerDisputeStatus;
    @SerializedName("chargeId")
    private String chargeId;

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public String getDeclineDate() {
        return declineDate;
    }

    public void setDeclineDate(String declineDate) {
        this.declineDate = declineDate;
    }

    public String getRefundAcceptedDate() {
        return refundAcceptedDate;
    }

    public void setRefundAcceptedDate(String refundAcceptedDate) {
        this.refundAcceptedDate = refundAcceptedDate;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPic() {
        return buyerPic;
    }

    public void setBuyerPic(String buyerPic) {
        this.buyerPic = buyerPic;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(int productStatus) {
        this.productStatus = productStatus;
    }

    public String getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(String netPrice) {
        this.netPrice = netPrice;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerpic() {
        return sellerpic;
    }

    public void setSellerpic(String sellerpic) {
        this.sellerpic = sellerpic;
    }

    public long getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(long purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    @SerializedName("purchasedDate")
    private long purchasedDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(String disputeId) {
        this.disputeId = disputeId;
    }

    public String getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(String refundDate) {
        this.refundDate = refundDate;
    }

    public String getDisputeDate() {
        return disputeDate;
    }

    public void setDisputeDate(String disputeDate) {
        this.disputeDate = disputeDate;
    }

    public String getDisputeCompleteDate() {
        return disputeCompleteDate;
    }

    public void setDisputeCompleteDate(String disputeCompleteDate) {
        this.disputeCompleteDate = disputeCompleteDate;
    }

    public String getRefundRequestDate() {
        return refundRequestDate;
    }

    public void setRefundRequestDate(String refundRequestDate) {
        this.refundRequestDate = refundRequestDate;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ArrayList<ImageList> getImageLists() {
        return imageLists;
    }

    public void setImageLists(ArrayList<ImageList> imageLists) {
        this.imageLists = imageLists;
    }

    public int getSellerDisputeStatus() {
        return sellerDisputeStatus;
    }

    public void setSellerDisputeStatus(int sellerDisputeStatus) {
        this.sellerDisputeStatus = sellerDisputeStatus;
    }
}
