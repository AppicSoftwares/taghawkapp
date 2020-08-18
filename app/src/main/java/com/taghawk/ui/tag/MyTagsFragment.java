package com.taghawk.ui.tag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.taghawk.R;
import com.taghawk.adapters.MyTagsAdapter;
import com.taghawk.adapters.ProductListAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentViewAllProductBinding;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.model.tag.MyTagResponse;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tag.TagSearchBean;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;

public class MyTagsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentViewAllProductBinding mBinding;
    private ArrayList<TagData> mTagsList;
    private Activity mActivity;
    private MyTagsAdapter adapter;
    private TagViewModel tagViewModel;
    private int previousSelectedPosition = -1;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentViewAllProductBinding.inflate(inflater, container, false);
        initView();
        setUpList();
        return mBinding.getRoot();
    }

    private void initView() {
        mActivity = getActivity();
        mBinding.swipe.setOnRefreshListener(this);
        mBinding.includeHeader.tvTitle.setText(getString(R.string.share_community));
        mBinding.includeHeader.tvReset.setText(getString(R.string.done_lowercase));
        mBinding.includeHeader.tvReset.setVisibility(View.VISIBLE);
        mBinding.includeHeader.ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        mBinding.includeHeader.tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (previousSelectedPosition != -1) {
                    Intent intent = new Intent();
                    intent.putExtra(AppConstants.BUNDLE_DATA, mTagsList.get(previousSelectedPosition));
                    mActivity.setResult(Activity.RESULT_OK, intent);
                    mActivity.finish();
                } else
                    showToastShort(getString(R.string.select_any_product_to_share));
            }
        });
        mBinding.includeHeader.ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
    }

    private void setUpList() {
        mTagsList = new ArrayList<>();
        adapter = new MyTagsAdapter(mTagsList, new RecyclerViewCallback() {
            @Override
            public void onClick(int position, View view) {
                if (previousSelectedPosition != position) {
                    if (previousSelectedPosition != -1) {
                        mTagsList.get(previousSelectedPosition).setSelected(false);
                        adapter.notifyItemChanged(previousSelectedPosition);
                    }
                    previousSelectedPosition = position;
                    mTagsList.get(previousSelectedPosition).setSelected(true);
                    adapter.notifyItemChanged(previousSelectedPosition);
                }
            }
        });
        mBinding.rvProductListing.setLayoutManager(new LinearLayoutManager(mActivity));
        mBinding.rvProductListing.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing view model
        tagViewModel = ViewModelProviders.of(this).get(TagViewModel.class);
        tagViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        tagViewModel.getMyTagsLiveData().observe(this, new Observer<MyTagResponse>() {
            @Override
            public void onChanged(@Nullable MyTagResponse myTagResponse) {
                getLoadingStateObserver().onChanged(false);
                if (myTagResponse != null && myTagResponse.getCode() == 200) {
                    if (mBinding.swipe.isRefreshing()) {
                        mTagsList.clear();
                    }
                    mTagsList.addAll(myTagResponse.getMyTagResult().getTagData());
                    adapter.notifyDataSetChanged();
                    if (mTagsList.size() > 0) {
                        noData(View.VISIBLE, View.GONE, "", "");
                    } else {
                        noData(View.GONE, View.VISIBLE, getString(R.string.oops_it_s_empty), getString(R.string.no_results));
                    }
                } else if (mTagsList.size() == 0)
                    noData(View.GONE, View.VISIBLE, getString(R.string.oops_it_s_empty), getString(R.string.no_results));
                if (mBinding.swipe != null && mBinding.swipe.isRefreshing())
                    mBinding.swipe.setRefreshing(false);
            }
        });
        if (AppUtils.isConnection(mActivity))
            tagViewModel.getMyTags();
        else {
            noData(View.GONE, View.VISIBLE, getString(R.string.oops_it_s_empty), getString(R.string.no_results));
            showToastShort(getString(R.string.no_internet));
        }
    }

    private void noData(int visible, int gone, String errorTitle, String errorMsg) {
        mBinding.rvProductListing.setVisibility(visible);
        mBinding.includeEmpty.tvTitle.setText(errorTitle);
        mBinding.includeEmpty.tvEmptyMsg.setText(errorMsg);
        mBinding.tvNoData.setVisibility(gone);
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        if (mBinding.swipe != null && mBinding.swipe.isRefreshing())
            mBinding.swipe.setRefreshing(false);
        getLoadingStateObserver().onChanged(false);
        if (mTagsList != null && mTagsList.size() > 0) {
            noData(View.VISIBLE, View.GONE, "", "");
        } else {
            noData(View.GONE, View.VISIBLE, getString(R.string.oops_it_s_empty), getString(R.string.no_results));
            showToastShort(getString(R.string.something_went_wrong));
        }
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        super.onErrorOccurred(throwable);
        if (mBinding.swipe != null && mBinding.swipe.isRefreshing())
            mBinding.swipe.setRefreshing(false);
        if (mTagsList.size()==0)
        {
            noData(View.GONE, View.VISIBLE, getString(R.string.oops_it_s_empty), getString(R.string.no_results));
            showToastShort(getString(R.string.something_went_wrong));
        }
        getLoadingStateObserver().onChanged(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onRefresh() {
        if (AppUtils.isConnection(mActivity))
            tagViewModel.getMyTags();
        else {
            noData(View.GONE, View.VISIBLE, getString(R.string.oops_it_s_empty), getString(R.string.no_results));
            showToastShort(getString(R.string.no_internet));
        }
        mBinding.swipe.setRefreshing(false);
    }

}
