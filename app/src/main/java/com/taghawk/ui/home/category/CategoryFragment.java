package com.taghawk.ui.home.category;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.taghawk.R;
import com.taghawk.adapters.CategoryListAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.databinding.FragmentCategoryListBinding;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.category.CategoryListResponse;
import com.taghawk.model.category.CategoryResponse;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */

public class CategoryFragment extends BaseFragment {

    private Activity mActivity;
    private FragmentCategoryListBinding mCategoryBinding;
    private CategoryListAdapter adapter;
    private ArrayList<CategoryListResponse> mCategoryList;
    private CategoryListViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCategoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_list, container, false);
        mActivity = getActivity();
        setupView();
        return mCategoryBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CategoryListViewModel.class);
        mViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mViewModel.getCategoryListViewModel().observe(this, new Observer<CategoryResponse>() {
            @Override
            public void onChanged(@Nullable CategoryResponse categoryResponse) {
                hideProgressDialog();
                if (categoryResponse != null) {
                    getLoadingStateObserver().onChanged(false);
                    mCategoryList.addAll(categoryResponse.getmCategory());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        if (AppUtils.isInternetAvailable(mActivity))
            mViewModel.hitGetCategory();
        else showNoNetworkError();
    }

    private void setupView() {
        mCategoryList = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        adapter = new CategoryListAdapter(mActivity, mCategoryList);
        mCategoryBinding.rvCategoryList.setLayoutManager(layoutManager);
        mCategoryBinding.rvCategoryList.setAdapter(adapter);
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        getLoadingStateObserver().onChanged(false);
        hideProgressDialog();
        showToastLong(failureResponse.getErrorMessage());
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        super.onErrorOccurred(throwable);
        hideProgressDialog();
        getLoadingStateObserver().onChanged(false);
    }


}
