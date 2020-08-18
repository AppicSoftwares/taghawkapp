package com.taghawk.ui.setting.block_user;

import android.app.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taghawk.R;
import com.taghawk.adapters.BlockUserAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentFollowFollowerBinding;
import com.taghawk.model.block_user.BlockUserDetail;
import com.taghawk.model.block_user.BlockUserModel;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.ui.profile.ProfileViewModel;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;

public class BlockUserFragment extends BaseFragment implements View.OnClickListener {

    private FragmentFollowFollowerBinding mBinding;
    private Activity mActivity;
    private ArrayList<BlockUserDetail> mList;
    private ProfileViewModel profileViewModel;
    private boolean isLoading;
    private int currentPage = 1, limit = 15;
    private BlockUserAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int poistion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentFollowFollowerBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setLisener();
        setUpList();
        setupViewModel();
    }

    private void setupViewModel() {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        profileViewModel.getmBlockUSerLiveData().observe(this, new Observer<BlockUserModel>() {
            @Override
            public void onChanged(@Nullable BlockUserModel bean) {
                getLoadingStateObserver().onChanged(false);
                if (bean.getCode() == 200) {
                    if (bean.getBlockUserData().getNextHit() > 0) {
                        isLoading = true;
                    } else {
                        isLoading = false;
                    }
                    currentPage = bean.getBlockUserData().getCurrentPage();
                    mList.addAll(bean.getBlockUserData().getBlockUserDetail());
                    if (mList.size() > 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        mBinding.llEmptyPlaceHolder.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        profileViewModel.profileViewModel().observe(this, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(@Nullable ProfileResponse profileResponse) {
                getLoadingStateObserver().onChanged(false);
                if (profileResponse.getCode() == 200) {
                    switch (profileResponse.getRequestCode()) {
                        case AppConstants.REQUEST_CODE.UNBLOCK:
                            mList.get(poistion).setUnBlock(true);
                            adapter.notifyItemChanged(poistion);
                            break;
                    }
                }
            }
        });
        if (AppUtils.isInternetAvailable(mActivity)) {
            profileViewModel.getBlockUserList(currentPage, limit);
        } else {
            showToastShort(getString(R.string.no_internet));
        }
    }

    public void initView() {
        mActivity = getActivity();
        mList = new ArrayList<>();
        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.includeHeader.tvTitle.setText(getString(R.string.block_users));
    }

    private void setUpList() {
        adapter = new BlockUserAdapter(mList, this);
        linearLayoutManager = new LinearLayoutManager(mActivity);
        mBinding.rvFollowFollower.setLayoutManager(linearLayoutManager);
        mBinding.rvFollowFollower.setAdapter(adapter);
        pagination();
    }

    private void pagination() {
        mBinding.rvFollowFollower.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int visibleItemCount = linearLayoutManager.getChildCount();
                    int totalItems = linearLayoutManager.getItemCount();
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                    if (isLoading) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItems
                                && firstVisibleItemPosition >= 0) {
                            isLoading = false;
                            if (AppUtils.isInternetAvailable(mActivity)) {
                                profileViewModel.getBlockUserList(currentPage++, limit++);
                            } else {
                                showToastShort(getString(R.string.no_internet));
                            }
                        }
                    }
                }

            }
        });
    }


    private void setLisener() {

        mBinding.includeHeader.ivCross.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                ((BlockUserActivity) mActivity).finish();
                break;
            case R.id.tv_un_block:
                poistion = (int) v.getTag();
                profileViewModel.removeUnfollow(mList.get(poistion).getId(), AppConstants.UNFOLLOW_REMOVE_ACTION.UN_BLOCK, AppConstants.REQUEST_CODE.UNBLOCK);
                break;
        }
    }
}
