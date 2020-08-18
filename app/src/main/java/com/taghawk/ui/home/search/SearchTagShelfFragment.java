package com.taghawk.ui.home.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.taghawk.R;
import com.taghawk.Repository.HomeRepo;
import com.taghawk.adapters.ShelfSearchListAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentSearchBinding;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.SearchModel;
import com.taghawk.model.SearchSuggestionData;
import com.taghawk.util.AppUtils;
import com.taghawk.util.FilterManager;
import com.taghawk.util.GPSTracker;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchTagShelfFragment extends BaseFragment implements View.OnClickListener {


    private ArrayList<SearchSuggestionData> mSearchList;
    private ShelfSearchListAdapter adapter;
    private HomeRepo repo = new HomeRepo();
    private SearchViewModel mSearchSuggestionViewModel;
    private FragmentSearchBinding mSearchBinding;
    private ISearchHost mSearchHost;
    private boolean isFirst = false;
    private String categoryId;
    private HashMap<String, Object> filterParms;
    private Activity mActivity;
    private GPSTracker gpsTracker;
    private String tagId = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mSearchBinding = FragmentSearchBinding.inflate(inflater, container, false);
        initView(mSearchBinding);
        setUpList(mSearchBinding);
        showKeyboard();
        return mSearchBinding.getRoot();
    }

    private void showKeyboard() {
        mSearchBinding.etSearch.requestFocus();
        mSearchBinding.etSearch.postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyBaord(mSearchBinding.etSearch);
            }
        }, 1000);
    }

    private void showKeyBaord(AppCompatEditText etSearch) {
        if (etSearch.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void callApi() {
        mSearchSuggestionViewModel.getShelfSearchSuggestion(FilterManager.getInstance().getmFilterMap(), "", categoryId, gpsTracker.getLatitude(), gpsTracker.getLongitude(), tagId);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ISearchHost) {
            mSearchHost = (ISearchHost) context;
        } else
            throw new IllegalStateException("Host must implement IHomeHost");
    }

    private void initView(final FragmentSearchBinding mSearchBinding) {
        mActivity = getActivity();
        gpsTracker = new GPSTracker(mActivity);
        if (getArguments() != null) {
            String searchTitle = getArguments().getString("SEARCH_TITTLE");
            if (searchTitle != null && searchTitle.length() > 0) {
                mSearchBinding.etSearch.setMaxLines(1);
                mSearchBinding.etSearch.setHint(getString(R.string.search_in) + " " + searchTitle);
                categoryId = getArguments().getString("CATEGORY");
                filterParms = (HashMap<String, Object>) getArguments().getSerializable("FILTER_DATA");
                tagId = getArguments().getString(AppConstants.BUNDLE_DATA);
            }
        }

        mSearchBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mSearchBinding.ivCross.setVisibility(View.VISIBLE);
                    mSearchSuggestionViewModel.getShelfSearchSuggestion(FilterManager.getInstance().getmFilterMap(), s.toString(), categoryId, 0.0, 0.0, tagId);
                } else {
                    mSearchBinding.ivCross.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchBinding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    AppUtils.hideKeyboard(mActivity);
                    mSearchHost.openSearchResultData(mSearchBinding.etSearch.getText().toString());
                }
                return false;
            }
        });
    }

    private void setUpList(FragmentSearchBinding mSearchBinding) {
        mSearchList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mSearchBinding.getRoot().getContext());
        adapter = new ShelfSearchListAdapter(mSearchList, mSearchHost);
        mSearchBinding.rvSuggestionSearchList.setLayoutManager(layoutManager);
        mSearchBinding.rvSuggestionSearchList.setAdapter(adapter);
        mSearchBinding.ivBack.setOnClickListener(this);
        mSearchBinding.ivCross.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchSuggestionViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        mSearchSuggestionViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        //observing login live data
        mSearchSuggestionViewModel.getmSeacrhViewModel().observe(this, new Observer<SearchModel>() {
            @Override
            public void onChanged(@Nullable SearchModel searchModel) {
                getLoadingStateObserver().onChanged(false);
                if (searchModel != null) {
                    if (searchModel.getCode() == 201 || searchModel.getCode() == 200) {
                        mSearchList.clear();
                        mSearchList.addAll(searchModel.getSearchSuggestionList());
                        if (mSearchList.size() > 0)
                            showNoDataFound(View.GONE, View.VISIBLE);
                        else {
                            if (!isFirst) {
                                showNoDataFound(View.VISIBLE, View.GONE);
                            }
                        }
                        isFirst = false;
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        isFirst = true;
//        mSearchSuggestionViewModel.getSearchSuggestion(filterParms, "", categoryId, gpsTracker.getLatitude(), gpsTracker.getLongitude());
    }

    private void showNoDataFound(int gone, int visible) {
        mSearchBinding.tvNoData.setVisibility(gone);
        mSearchBinding.includeEmpty.tvTitle.setText(getString(R.string.no_data_found));
        mSearchBinding.includeEmpty.tvEmptyMsg.setText(getString(R.string.no_data_found_));
        mSearchBinding.rvSuggestionSearchList.setVisibility(visible);
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        showNoDataFound(View.VISIBLE, View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                mSearchHost.backPressed();
                break;
            case R.id.iv_cross:
                mSearchBinding.etSearch.setText("");
//                mSearchSuggestionViewModel.getSearchSuggestion(filterParms, "", categoryId, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                break;
        }
    }

    /**
     * This interface is used to interact with the host {@link SearchAcivity}
     */
    public interface ISearchHost {

        void openSearchResultData(String seach);

        void backPressed();
    }

}