
package com.taghawk.model.profileresponse;

import java.util.List;

import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.home.ProductListModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileProductsResponse {

    private boolean loading;

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<ProductDetailsData> data = null;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("total_page")
    @Expose
    private Integer totalPage;
    @SerializedName("next_hit")
    @Expose
    private Integer nextHit;
    @SerializedName("limit")
    @Expose
    private Integer limit;
    @SerializedName("time")
    @Expose
    private long time;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ProductDetailsData> getData() {
        return data;
    }

    public void setData(List<ProductDetailsData> data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
