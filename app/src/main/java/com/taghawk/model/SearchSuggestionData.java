package com.taghawk.model;

import com.google.gson.annotations.SerializedName;

public class SearchSuggestionData {
    @SerializedName("count")
    private String totalitems;
    @SerializedName("title")
    private String title;

    public String getTotalitems() {
        return totalitems;
    }

    public void setTotalitems(String totalitems) {
        this.totalitems = totalitems;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
