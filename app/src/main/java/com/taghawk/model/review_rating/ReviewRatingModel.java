package com.taghawk.model.review_rating;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

import java.util.ArrayList;

public class ReviewRatingModel extends CommonResponse {

    @SerializedName("data")
    private ArrayList<ReviewRatingData> mReviewRatingData;
    @SerializedName("total")
    private int totalItems;
    @SerializedName("page")
    private int currentPage;
    @SerializedName("total_page")
    private int totalPage;
    @SerializedName("next_hit")
    private int nextHit;
    @SerializedName("limit")
    private int limit;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public ArrayList<ReviewRatingData> getmReviewRatingData() {
        return mReviewRatingData;
    }

    public void setmReviewRatingData(ArrayList<ReviewRatingData> mReviewRatingData) {
        this.mReviewRatingData = mReviewRatingData;
    }
}
