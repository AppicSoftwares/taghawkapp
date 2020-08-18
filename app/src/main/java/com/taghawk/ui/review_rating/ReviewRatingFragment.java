package com.taghawk.ui.review_rating;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.taghawk.R;
import com.taghawk.adapters.RatingReviewAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentReviewRatingBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.review_rating.ReplyData;
import com.taghawk.model.review_rating.ReviewRatingData;
import com.taghawk.model.review_rating.ReviewRatingModel;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;

public class ReviewRatingFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, OnDialogViewClickListener {

    private String sellerUserId, sellerUserName, sellerJoinFrom, sellerImage;
    private Double sellerRating;
    private FragmentReviewRatingBinding mBinding;
    private Activity mActivity;
    private ReviewRatingViewModel mReviewRatingViewModel;
    private ArrayList<ReviewRatingData> mList;
    private RatingReviewAdapter adapter;
    private int limit = 10, currentPage = 1, position;
    private String reply;
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentReviewRatingBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAragumentData();
        initView();
    }

    private void initView() {

        mActivity = getActivity();
        mList = new ArrayList<>();
        mBinding.includeHeader.tvTitle.setText(getString(R.string.reviews));
        mBinding.includeHeader.ivCross.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.tvSellerName.setText(sellerUserName);
        mBinding.tvRating.setText("" + sellerRating);
        mBinding.tvUserCreateDate.setText(sellerJoinFrom);
        mBinding.swipe.setOnRefreshListener(this);
        sellProfilePic(sellerImage);
        setupList();
        setupViewModel();
        mBinding.includeHeader.ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReviewRatingActivity) mActivity).finish();
            }
        });

    }

    private void setupList() {

        adapter = new RatingReviewAdapter(mList, sellerUserName, sellerUserId, this);
        linearLayoutManager = new LinearLayoutManager(mActivity);
        mBinding.rvCommentList.setLayoutManager(linearLayoutManager);
        mBinding.rvCommentList.setAdapter(adapter);
        pagination();

    }

    private void pagination() {
        mBinding.rvCommentList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            mReviewRatingViewModel.getReviewRatingList(sellerUserId, currentPage++, limit, true);

                        }
                    }
                }

            }
        });
    }

    private void setupViewModel() {

        mReviewRatingViewModel = ViewModelProviders.of(this).get(ReviewRatingViewModel.class);
        mReviewRatingViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mReviewRatingViewModel.getReviewLiveData().observe(this, new Observer<ReviewRatingModel>() {
            @Override
            public void onChanged(@Nullable ReviewRatingModel reviewRatingModel) {
                getLoadingStateObserver().onChanged(false);
                if (reviewRatingModel.getCode() == 200) {
                    switch (reviewRatingModel.getRequestCode()) {
                        case AppConstants.REQUEST_CODE.REVIEW_RATING_LIST:
                            if (mBinding.swipe != null && mBinding.swipe.isRefreshing()) {
                                mList.clear();
                                mBinding.swipe.setRefreshing(false);
                            }
                            if (reviewRatingModel.getNextHit() > 0) {
                                isLoading = true;
                            } else {
                                isLoading = false;
                            }
                            mBinding.tvReviewCount.setText("Reviews (" + reviewRatingModel.getTotalItems() + ")");
                            currentPage = reviewRatingModel.getCurrentPage();
                            mList.addAll(reviewRatingModel.getmReviewRatingData());
                            adapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });
        mReviewRatingViewModel.getReplyLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse.getCode() == 200) {
                    switch (commonResponse.getRequestCode()) {
                        case AppConstants.REQUEST_CODE.REVIEW_REPLY:
                            ReplyData data = new ReplyData();
                            data.setReplyComment(reply);
                            mList.get(position).setReplyComment(data);
                            adapter.notifyItemChanged(position);
                            break;
                        case AppConstants.REQUEST_CODE.REVIEW_EDIT:
                            ReplyData data1 = new ReplyData();
                            data1.setReplyComment(reply);
                            data1.setEditedStatus(true);
                            data1.setEditedDate(AppUtils.getTimeStamp());
                            mList.get(position).getReplyComment().setReplyComment(reply);
                            adapter.notifyItemChanged(position);
                            break;
                    }
                }
            }
        });
        mReviewRatingViewModel.getReviewRatingList(sellerUserId, currentPage, limit, false);
    }


    private void sellProfilePic(String profilePicture) {

        if (profilePicture != null && profilePicture.length() > 0) {
            Glide.with(mActivity).asBitmap().load(profilePicture).apply(RequestOptions.placeholderOf(R.drawable.ic_tab_profile_unactive)).into(mBinding.ivSellerImage);
            mBinding.tvFirstCharater.setVisibility(View.GONE);
        } else {
            if (sellerUserName.length() > 0) {
                mBinding.tvFirstCharater.setVisibility(View.VISIBLE);
                mBinding.tvFirstCharater.setText(sellerUserName.toUpperCase().toString().substring(0, 1));
            }
        }
    }

    private void getAragumentData() {

        sellerRating = getArguments().getDouble(AppConstants.KEY_CONSTENT.SELLER_RATING);
        sellerUserId = getArguments().getString(AppConstants.KEY_CONSTENT.SELLER_ID);
        sellerUserName = getArguments().getString(AppConstants.KEY_CONSTENT.FULL_NAME);
        sellerJoinFrom = getArguments().getString(AppConstants.KEY_CONSTENT.JOIN_FROM);
        sellerImage = getArguments().getString(AppConstants.KEY_CONSTENT.IMAGES);

    }

    @Override
    public void onRefresh() {

        currentPage = 1;
        mReviewRatingViewModel.getReviewRatingList(sellerUserId, currentPage, limit, true);

    }

    @Override
    public void onSubmit(String txt, int position) {
        this.position = position;
        if (mList != null && !mList.get(position).isReplyShowing() && mList.get(position).getReplyComment() == null) {
            mList.get(position).setReplyShowing(true);
            adapter.notifyItemChanged(position);
        } else if (mList != null && mList.get(position).getReplyComment() == null) {
            reply = txt;
            mList.get(position).setReplyShowing(true);
            mReviewRatingViewModel.replyAndEditReply(mList.get(position).getCommentId(), txt, 1, AppConstants.REQUEST_CODE.REVIEW_REPLY);
        } else if (mList != null && mList.get(position).getReplyComment() != null) {
            reply = txt;
            mList.get(position).setReplyShowing(true);
            mReviewRatingViewModel.replyAndEditReply(mList.get(position).getCommentId(), txt, 2, AppConstants.REQUEST_CODE.REVIEW_EDIT);
        }
    }
}
