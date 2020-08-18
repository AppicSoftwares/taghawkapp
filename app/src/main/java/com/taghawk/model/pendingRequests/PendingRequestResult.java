
package com.taghawk.model.pendingRequests;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PendingRequestResult {

    @SerializedName("data")
    @Expose
    private ArrayList<PendingRequest> pendingRequests = null;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("total_page")
    @Expose
    private Integer totalPage;
    @SerializedName("next_hit")
    @Expose
    private Integer nextHit;
    @SerializedName("limit")
    @Expose
    private Integer limit;

    public ArrayList<PendingRequest> getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(ArrayList<PendingRequest> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getNextHit() {
        return nextHit;
    }

    public void setNextHit(Integer nextHit) {
        this.nextHit = nextHit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

}
