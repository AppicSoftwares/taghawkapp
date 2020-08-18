package com.taghawk.ui.home.search;

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
import com.taghawk.adapters.TagSearchListAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.databinding.FragmentSearchBinding;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tag.TagSearchBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchTagFragment extends BaseFragment implements View.OnClickListener {


    private ArrayList<TagData> mSearchList;
    private TagSearchListAdapter adapter;
    private HomeRepo repo = new HomeRepo();
    private SearchViewModel mSearchSuggestionViewModel;
    private FragmentSearchBinding mSearchBinding;
    private ISearchHost mSearchHost;
    private boolean isFirst = false;
    private String categoryId;
    private HashMap<String, Object> filterParms;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mSearchBinding = FragmentSearchBinding.inflate(inflater, container, false);
        initView(mSearchBinding);
        showKeyboard();
        setUpList(mSearchBinding);
        return mSearchBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ISearchHost) {
            mSearchHost = (ISearchHost) context;
        } else
            throw new IllegalStateException("Host must implement IHomeHost");
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
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    private void initView(final FragmentSearchBinding mSearchBinding) {
        if (getArguments() != null) {
            filterParms = (HashMap<String, Object>) getArguments().getSerializable("FILTER_DATA");

        }
        mSearchBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mSearchBinding.ivCross.setVisibility(View.VISIBLE);
                    mSearchSuggestionViewModel.getTagSearch(filterParms, s.toString());
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
                    if (mSearchBinding.etSearch.length() > 0)
                        mSearchHost.sendSearchKeyForResult(mSearchBinding.etSearch.getText().toString());
                }
                return false;
            }
        });
    }

    private void setUpList(FragmentSearchBinding mSearchBinding) {
        mSearchList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mSearchBinding.getRoot().getContext());
        adapter = new TagSearchListAdapter(mSearchList);
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
        mSearchSuggestionViewModel.getTagSeachViewModel().observe(this, new Observer<TagSearchBean>() {
            @Override
            public void onChanged(@Nullable TagSearchBean searchModel) {
                getLoadingStateObserver().onChanged(false);
                if (searchModel != null) {
                    if (searchModel.getCode() == 201 || searchModel.getCode() == 200) {
                        mSearchList.clear();
                        mSearchList.addAll(searchModel.getmTagSearchList());
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
        mSearchSuggestionViewModel.getTagSearch(filterParms, "");
    }

    private void showNoDataFound(int gone, int visible) {
        mSearchBinding.tvNoData.setVisibility(gone);
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
                ((SearchAcivity) getActivity()).finish();
                break;
            case R.id.iv_cross:
                mSearchBinding.etSearch.setText("");
                mSearchSuggestionViewModel.getTagSearch(filterParms, "");
                break;
        }
    }

    /**
     * This interface is used to interact with the host {@link SearchAcivity}
     */
    public interface ISearchHost {

        void sendSearchKeyForResult(String seach);

        void backPressed();
    }

}
