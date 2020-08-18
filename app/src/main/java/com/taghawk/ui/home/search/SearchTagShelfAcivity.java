package com.taghawk.ui.home.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.ui.home.shelf.SearchTagProductResultFragment;
import com.taghawk.util.AppUtils;
import com.taghawk.util.FilterManager;

import java.util.HashMap;

public class SearchTagShelfAcivity extends BaseActivity implements SearchTagShelfFragment.ISearchHost, SearchTagProductResultFragment.ISearchHost {

    private String categoryId = "";
    private String searchTitle = "";
    private HashMap<String, Object> filterData;
    private int IsFRom;
    private String communityId = "";
    private String tagName = "";
    private String sortedBy = "created";
    private String sortedOrder = "-1";


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
        else if (IsFRom == 3)
            openSearchResult("");

    }

    private void getIntentData() {
        filterData = new HashMap<>();
        if (getIntent() != null && getIntent().getExtras() != null) {
            IsFRom = getIntent().getExtras().getInt("IS_FROM");
            categoryId = getIntent().getExtras().getString("CATEGORY");
            searchTitle = getIntent().getExtras().getString("SEARCH_TITTLE");
            filterData = (HashMap<String, Object>) getIntent().getExtras().getSerializable("FILTER_DATA");
            communityId = getIntent().getExtras().getString(AppConstants.BUNDLE_DATA);
            tagName = getIntent().getExtras().getString(AppConstants.TAG_KEY_CONSTENT.NAME);
        }
    }

    private void addInitialFragment() {
//        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("SEARCH_TITTLE")) {
//            searchTitle = getIntent().getExtras().getString("SEARCH_TITTLE");
//        }
        Bundle bundle = new Bundle();
        bundle.putString("SEARCH_TITTLE", searchTitle);
        bundle.putString("CATEGORY", categoryId);
        bundle.putString("SORTEDBY", sortedBy);
        bundle.putString("SORTEDORDER", sortedOrder);
        bundle.putSerializable("FILTER_DATA", FilterManager.getInstance().getmFilterMap());
        bundle.putString(AppConstants.BUNDLE_DATA, communityId);
//        bundle.putString("SEARCH_TITTLE", tagName);
        SearchTagShelfFragment fragment = new SearchTagShelfFragment();
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, SearchTagFragment.class.getSimpleName());
    }


    private void initView() {
        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);

    }

    public void openSearchResult(String search) {
        popFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY_CONSTENT.SEARCH_KEY, search);
        bundle.putString("CATEGORY", categoryId);
        bundle.putString("SEARCH_TITTLE", searchTitle);
        bundle.putSerializable("FILTER_DATA", FilterManager.getInstance().getmFilterMap());
        bundle.putString(AppConstants.BUNDLE_DATA, communityId);
        bundle.putString("SEARCH_TITTLE", tagName);
        SearchTagProductResultFragment fragment = new SearchTagProductResultFragment();
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, SearchTagProductResultFragment.class.getSimpleName());
    }

    public void sendSearchKeyForResult(String search) {
        Intent intent = new Intent();
        intent.putExtra("SEARCH_KEY", search);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void openSearchResultData(String search) {
        popFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.KEY_CONSTENT.SEARCH_KEY, search);
        bundle.putString("CATEGORY", categoryId);
        bundle.putString("SORTEDBY", sortedBy);
        bundle.putString("SORTEDORDER", sortedOrder);
        bundle.putSerializable("FILTER_DATA", FilterManager.getInstance().getmFilterMap());
        bundle.putString("SEARCH_TITTLE", searchTitle);
        bundle.putString(AppConstants.BUNDLE_DATA, communityId);
//        bundle.putString("SEARCH_TITTLE", tagName);
        SearchTagProductResultFragment fragment = new SearchTagProductResultFragment();
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, SearchResultFragment.class.getSimpleName());
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
    public void openSearchSuggestion(String searchTitle, String categoryId, String sortedBy, String sortedOrder) {
        this.searchTitle = searchTitle;
        this.categoryId = categoryId;
        this.sortedBy = sortedBy;
        this.sortedOrder = sortedOrder;
        addInitialFragment();
    }

    @Override
    public void onBackPressed() {
        backPressedHanding();
    }

    private void backPressedHanding() {
        if (getCurrentFragment() != null && getCurrentFragment() instanceof SearchTagProductResultFragment) {
            ((SearchTagProductResultFragment) getCurrentFragment()).backPressedAction();
        } else {
            finish();
        }

    }
}
