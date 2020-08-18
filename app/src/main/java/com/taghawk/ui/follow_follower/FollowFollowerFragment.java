package com.taghawk.ui.follow_follower;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taghawk.R;
import com.taghawk.adapters.FollowFollowingAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentFollowFollowerBinding;
import com.taghawk.databinding.LayoutRemoveBlockBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.follow_following.FollowFollowingBean;
import com.taghawk.model.follow_following.FollowFollowingData;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.ui.profile.OtherProfileActivity;
import com.taghawk.ui.profile.ProfileViewModel;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;

import java.util.ArrayList;

public class FollowFollowerFragment extends BaseFragment implements View.OnClickListener {

    private FragmentFollowFollowerBinding mBinding;
    private String userId;
    private int type;
    private Activity mActivity;
    private ProfileViewModel profileViewModel;
    private FollowFollowingAdapter adapter;
    private ArrayList<FollowFollowingData> mList;
    private boolean isFromOtherProfile;
    private int position;
    private LinearLayoutManager linearLayoutManager;
    private int currentPage = 1, limit = 15;
    private boolean isLoading;
    private PopupWindow popup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentFollowFollowerBinding.inflate(inflater, container, false);
        return mBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArgumentData();
        initView();
        setLisener();
        setUpList();
        setupViewModel();

    }

    private void setupViewModel() {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        profileViewModel.getmFollowFollowingLiveData().observe(this, new Observer<FollowFollowingBean>() {
            @Override
            public void onChanged(@Nullable FollowFollowingBean bean) {
                getLoadingStateObserver().onChanged(false);
                if (bean.getCode() == 200) {
                    if (bean.getNextHit() > 0) {
                        isLoading = true;
                    } else {
                        isLoading = false;
                    }
                    currentPage = bean.getCurrentPage();
                    mList.addAll(bean.getFollowFollowingData());
                    if (mList.size() > 0) {
                        if (type == 1)
                            setTitle(getString(R.string.followers), mList.size());
                        else
                            setTitle(getString(R.string.following), mList.size());
                        adapter.notifyDataSetChanged();
                    } else {
                        if (type == 1)
                            mBinding.includeHeader.tvTitle.setText(getString(R.string.followers));
                        else
                            mBinding.includeHeader.tvTitle.setText(getString(R.string.following));
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
                        case AppConstants.REQUEST_CODE.FOLLOW:
                            mList.get(position).setFollowing(true);
                            adapter.notifyItemChanged(position);
                            break;
                        case AppConstants.REQUEST_CODE.UNFOLLOW:
                            mList.get(position).setFollowing(false);
                            adapter.notifyItemChanged(position);
                            break;
                        case AppConstants.REQUEST_CODE.REMOVE_FRIEND:
                            mList.remove(position);
                            adapter.notifyItemRemoved(position);
                            break;
                        case AppConstants.REQUEST_CODE.BLOCK:
                            mList.remove(position);
                            adapter.notifyItemRemoved(position);
                            break;

                    }
                }
            }
        });
        if (AppUtils.isInternetAvailable(mActivity))
            profileViewModel.getFollowFollowingList(userId, type, currentPage, limit, AppConstants.REQUEST_CODE.FOLLOW_FOLLOWING);
        else showNoNetworkError();
    }


    private void setTitle(String string, int size) {
        mBinding.includeHeader.tvTitle.setText(string + " (" + size + ")");
    }

    private void setUpList() {
        adapter = new FollowFollowingAdapter(mList, type, isFromOtherProfile, this);
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
                            if (AppUtils.isInternetAvailable(mActivity))
                                profileViewModel.getFollowFollowingList(userId, type, currentPage++, limit, AppConstants.REQUEST_CODE.FOLLOW_FOLLOWING);
                            else showNoNetworkError();
                        }
                    }
                }

            }
        });
    }


    private void getArgumentData() {

        if (getArguments() != null) {
            type = getArguments().getInt(AppConstants.BUNDLE_DATA);
            userId = getArguments().getString(AppConstants.KEY_CONSTENT.USER_ID);
            isFromOtherProfile = getArguments().getBoolean("IS_OTHER_PROFILE", true);
        }

    }

    private void setLisener() {

        mBinding.includeHeader.ivCross.setOnClickListener(this);

    }

    private void initView() {
        mActivity = getActivity();
        mList = new ArrayList<>();
        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));

        if (type == 1)
            mBinding.includeHeader.tvTitle.setText(getString(R.string.followers));
        else if (type == 2)
            mBinding.includeHeader.tvTitle.setText(getString(R.string.following));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                backPressedAcction();
                break;
            case R.id.ll_main:
                position = (int) v.getTag();
                openProfileActivity(mList.get(position).getUserId());
                break;
            case R.id.tv_follow:
                position = (int) v.getTag();
                if (!mList.get(position).isFollowing()) {
                    profileViewModel.followFriend(mList.get(position).getUserId(), AppConstants.REQUEST_CODE.FOLLOW);
                } else {
                    DialogUtil.getInstance().CustomUnFollowRemoveBottomSheetDialog(mActivity, getString(R.string.unfollow_meg), mList.get(position).getFullName(), getString(R.string.unfollow), mList.get(position).getProfilePicture(), false, false, new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {
                            if (AppUtils.isInternetAvailable(mActivity))
                                profileViewModel.removeUnfollow(mList.get(position).getUserId(), AppConstants.UNFOLLOW_REMOVE_ACTION.UNFOLLOW, AppConstants.REQUEST_CODE.UNFOLLOW);
                            else showNoNetworkError();
                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });
                }

                break;
            case R.id.iv_more:
                position = (int) v.getTag();
                showsortingPopUp(v);
                break;
            case R.id.tv_block:
                popup.dismiss();
                DialogUtil.getInstance().CustomUnFollowRemoveBottomSheetDialog(mActivity, getString(R.string.block_msg), mList.get(position).getFullName(), getString(R.string.block), mList.get(position).getProfilePicture(), true, true, new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {
                        profileViewModel.removeUnfollow(mList.get(position).getUserId(), AppConstants.UNFOLLOW_REMOVE_ACTION.BLOCK, AppConstants.REQUEST_CODE.BLOCK);
                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });
                break;
            case R.id.tv_remove:
                popup.dismiss();
                DialogUtil.getInstance().CustomUnFollowRemoveBottomSheetDialog(mActivity, getString(R.string.follower_remove_msg), mList.get(position).getFullName(), getString(R.string.remove), mList.get(position).getProfilePicture(), true, false, new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {
                        profileViewModel.removeUnfollow(mList.get(position).getUserId(), AppConstants.UNFOLLOW_REMOVE_ACTION.REMOVE, AppConstants.REQUEST_CODE.REMOVE_FRIEND);
                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });
                break;
        }

    }

    private void backPressedAcction() {
        ((FollowFollowerActivity) mActivity).setResult(Activity.RESULT_OK);
        ((FollowFollowerActivity) mActivity).finish();
    }

    private void openProfileActivity(String userId) {
        if (!(userId.equalsIgnoreCase(DataManager.getInstance().getUserDetails().getUserId()))) {
            Intent intent = new Intent(mActivity, OtherProfileActivity.class);
            intent.putExtra(AppConstants.BUNDLE_DATA, userId);
            mActivity.startActivity(intent);
        }
    }

    private void showsortingPopUp(View view) {

        LayoutRemoveBlockBinding popBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_remove_block, null, false);
        popup = new PopupWindow(mActivity);
        popup.setContentView(popBinding.getRoot());
        popup.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        popBinding.tvBlock.setOnClickListener(this);
        popBinding.tvRemove.setOnClickListener(this);
        popup.showAsDropDown(view);

    }

}
