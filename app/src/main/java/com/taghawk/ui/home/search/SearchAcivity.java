package com.taghawk.ui.home.search;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.util.AppUtils;

import java.util.HashMap;

public class SearchAcivity extends BaseActivity implements SearchFragment.ISearchHost, SearchResultFragment.ISearchHost, SearchTagFragment.ISearchHost {

    private String categoryId = "";
    private String searchTitle = "";
    private HashMap<String, Object> filterData;
    private int IsFRom;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getIntentData();
        if (IsFRom == 0)
            addInitialFragment();
        else if (IsFRom == 1)
            addSearchTagFragment();
        else if (IsFRom == 3)
            openSearchResult("");

    }

    private void addSearchTagFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("SEARCH_TITTLE", searchTitle);
        bundle.putString("CATEGORY", categoryId);
        bundle.putSerializable("FILTER_DATA", filterData);
        SearchTagFragment fragment = new SearchTagFragment();
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, SearchTagFragment.class.getSimpleName());
    }

    private void getIntentData() {
        filterData = new HashMap<>();
        if (getIntent() != null && getIntent().getExtras() != null) {
            IsFRom = getIntent().getExtras().getInt("IS_FROM");
            categoryId = getIntent().getExtras().getString("CATEGORY");
            searchTitle = getIntent().getExtras().getString("SEARCH_TITTLE");
            filterData = (HashMap<String, Object>) getIntent().getExtras().getSerializable("FILTER_DATA");
        }
    }

    private void addInitialFragment() {
//        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("SEARCH_TITTLE")) {
//            searchTitle = getIntent().getExtras().getString("SEARCH_TITTLE");
//        }
        Bundle bundle = new Bundle();
        bundle.putString("SEARCH_TITTLE", searchTitle);
        bundle.putString("CATEGORY", categoryId);
        bundle.putSerializable("FILTER_DATA", filterData);
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, SearchFragment.class.getSimpleName());
    }


    private void initView() {
        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);

    }


    @Override
    public void openSearchResultData(String search) {
        popFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY_CONSTENT.SEARCH_KEY, search);
        bundle.putString("CATEGORY", categoryId);
        bundle.putSerializable("FILTER_DATA", filterData);
        bundle.putString("SEARCH_TITTLE", searchTitle);

        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, SearchResultFragment.class.getSimpleName());
    }

    public void openSearchResult(String search) {
        popFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY_CONSTENT.SEARCH_KEY, search);
        bundle.putString("CATEGORY", categoryId);
        bundle.putString("SEARCH_TITTLE", searchTitle);
        bundle.putSerializable("FILTER_DATA", filterData);
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, SearchResultFragment.class.getSimpleName());
    }

    public void sendSearchKeyForResult(String search) {
        Intent intent = new Intent();
        intent.putExtra("SEARCH_KEY", search);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void backPressed() {
        backPressedHanding();
    }

    @Override
    public void setCategoryId(String id) {
        this.categoryId = id;
    }

    @Override
    public void setCategoryName(String name) {
        this.searchTitle = name;
    }

    @Override
    public void openSearchSuggestion(String searchTitle) {
        this.searchTitle = searchTitle;
        addInitialFragment();
    }

    @Override
    public void onBackPressed() {
        backPressedHanding();
    }

    private void backPressedHanding() {
//        if (getCurrentFragment() instanceof SearchResultFragment) {
//            popFragment();
//        } else {

//        setResult(RESULT_OK);
        finish()  ;
//        }
    }
}
