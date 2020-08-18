package com.taghawk.model;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

import java.util.ArrayList;

public class NotificationModel extends CommonResponse {

    @SerializedName("data")
    ArrayList<NotificationData> mList;
    @SerializedName("total")
    private int total;
    @SerializedName("page")
    private int currentPage;
    @SerializedName("total_page")
    private int totalPage;
    @SerializedName("next_hit")
    private int nextHit;

    public ArrayList<NotificationData> getmList() {
        return mList;
    }

    public void setmList(ArrayList<NotificationData> mList) {
        this.mList = mList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getNextHit() {
        return nextHit;
    }

    public void setNextHit(int nextHit) {
        this.nextHit = nextHit;
    }
}
