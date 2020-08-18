package com.taghawk.model;

import com.taghawk.model.commonresponse.CommonResponse;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchModel extends CommonResponse {
    @SerializedName("data")
    private ArrayList<SearchSuggestionData> SearchSuggestionList;

    public ArrayList<SearchSuggestionData> getSearchSuggestionList() {
        return SearchSuggestionList;
    }

    public void setSearchSuggestionList(ArrayList<SearchSuggestionData> searchSuggestionList) {
        SearchSuggestionList = searchSuggestionList;
    }
}
