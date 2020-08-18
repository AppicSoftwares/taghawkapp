package com.taghawk.ui.notification;

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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.taghawk.R;
import com.taghawk.adapters.NotificationAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentNotificationBinding;
import com.taghawk.model.NotificationData;
import com.taghawk.model.NotificationModel;
import com.taghawk.ui.cart.CartActivity;
import com.taghawk.ui.chat.PendingRequestsActivity;
import com.taghawk.ui.follow_follower.FollowFollowerActivity;
import com.taghawk.ui.gift.GiftRewardPromotionActivity;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.ui.setting.payment_details.PaymentDetailsActivity;
import com.taghawk.ui.tag.TagDetailsActivity;

import java.util.ArrayList;

public class NotificationFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    FragmentNotificationBinding mBinding;
    private NotificationViewModel notificationViewModel;
    private Activity mActivity;
    private ArrayList<NotificationData> mList;
    private NotificationAdapter adapter;
    private boolean isLoading;
    private int currentPage = 1, limit = 15;
    private int position;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentNotificationBinding.inflate(inflater, container, false);
        initView();
        setUpList();
        return mBinding.getRoot();
    }

    private void setUpList() {
        mList = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mBinding.rvNotification.setLayoutManager(layoutManager);
        adapter = new NotificationAdapter(mActivity, mList, this);
        mBinding.rvNotification.setAdapter(adapter);
        mBinding.rvNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItems = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (isLoading) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItems
                                && firstVisibleItemPosition >= 0) {
                            isLoading = false;
                            notificationViewModel.hitGetAllNotification(limit, currentPage++, true);
                        }
                    }
                }

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        notificationViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        notificationViewModel.getNotificationLiveModel().observe(this, new Observer<NotificationModel>() {
            @Override
            public void onChanged(@Nullable NotificationModel notificationModel) {
                getLoadingStateObserver().onChanged(false);
                if (notificationModel.getCode() == 200) {
                    if (notificationModel.getRequestCode() == AppConstants.REQUEST_CODE.NOTIFICATION_LIST) {
                        notificationListUpdate(notificationModel);
                    }
                }
            }
        });
        notificationViewModel.hitGetAllNotification(currentPage, limit, false);
    }

    private void notificationListUpdate(@NonNull NotificationModel notificationModel) {
        if (mBinding.swipe.isRefreshing()) {
            mBinding.swipe.setRefreshing(false);
            mList.clear();
        }
        if (notificationModel.getNextHit() > 0) {
            isLoading = true;
        } else {
            isLoading = false;
        }
        currentPage = notificationModel.getCurrentPage();
        mList.addAll(notificationModel.getmList());
        if (mList.size() > 0) {
            noData(View.VISIBLE, View.GONE, "", "");
        } else {
            noData(View.GONE, View.VISIBLE, getString(R.string.no_data_found), getString(R.string.no_notification));
        }
        adapter.notifyDataSetChanged();
    }

    private void noData(int visible, int gone, String errorTitle, String errorMsg) {
        mBinding.rvNotification.setVisibility(visible);
        mBinding.includeEmpty.tvTitle.setText(errorTitle);
        mBinding.includeEmpty.tvEmptyMsg.setText(errorMsg);
        mBinding.llEmpty.setVisibility(gone);
    }

    private void initView() {
        mActivity = getActivity();
//        mBinding.header.tvTitle.setText(getString(R.string.notification));
        mBinding.header.tvTitle.setVisibility(View.GONE);
        mBinding.header.ivCross.setVisibility(View.GONE);
        mBinding.llHeader.setVisibility(View.GONE);
        mBinding.header.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black));
        mBinding.header.ivCross.setOnClickListener(this);
        mBinding.swipe.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                mActivity.finish();
                break;
            case R.id.ll_notification:
                position = (int) v.getTag();
//                notificationViewModel.markNotificationRead(mList.get(position).getId());
                notificationActionOnType(mList.get(position));
                break;
        }
    }

    private void notificationActionOnType(NotificationData notificationData) {
        Intent intent = null;
        markAsRead(notificationData);
        switch (notificationData.getType()) {
            case AppConstants.NOTIFICATION_ACTION.PRODUCT_LIKE:
                intent = perfromAction(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, notificationData, ProductDetailsActivity.class);
                mActivity.startActivity(intent);
                break;
            case AppConstants.NOTIFICATION_ACTION.PRODUCT_ADDED:
                intent = perfromAction(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, notificationData, ProductDetailsActivity.class);
                mActivity.startActivity(intent);
                break;
            case AppConstants.NOTIFICATION_ACTION.FOLLOWED:
//                intent = perfromAction(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, notificationData, FollowFollowerActivity.class);
                openFollowFollowingActivity();
                return;
            case AppConstants.NOTIFICATION_ACTION.ANNOUNCEMENT:
            case AppConstants.NOTIFICATION_ACTION.TAG_JOINED_ACCEPTED:
            case AppConstants.NOTIFICATION_ACTION.TAG_UPDATED:
            case AppConstants.NOTIFICATION_ACTION.MYSELF_REMOVE_FROM_GROUP:
            case AppConstants.NOTIFICATION_ACTION.MYSELF_MADE_ADMIN:
            case AppConstants.NOTIFICATION_ACTION.TAG_DETAILS:
            case AppConstants.NOTIFICATION_ACTION.JUMIO_APPROVAL:
            case AppConstants.NOTIFICATION_ACTION.TAG_JOINED:
                intent = perfromAction("TAG_ID", notificationData, TagDetailsActivity.class);
                mActivity.startActivity(intent);
                break;
//            case AppConstants.NOTIFICATION_ACTION.TAG_UPDATED:
//                intent = perfromAction("TAG_ID", notificationData, TagDetailsActivity.class);
//                mActivity.startActivity(intent);
//                break;

            case AppConstants.NOTIFICATION_ACTION.INVITE_CODE_USED:
                openGiftActivity();
                break;
            case AppConstants.NOTIFICATION_ACTION.PRODUCT_ADDED_IN_CART:
                openCartActivity();
                break;
            case AppConstants.NOTIFICATION_ACTION.TAG_REQUEST:
                Intent intent1 = new Intent(mActivity, PendingRequestsActivity.class);
//                intent1.putExtra(AppConstants.TAG_KEY_CONSTENT.NAME, tagDetailFirebaseModel.getTagName());
                intent1.putExtra(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, notificationData.getId());
//                intent1.putExtra(AppConstants.TAG_KEY_CONSTENT.IMAGE_URL, tagDetailFirebaseModel.getTagImageUrl());
                startActivity(intent1);
                break;
            case AppConstants.NOTIFICATION_ACTION.PRODUCT_SOLD:
            case AppConstants.PAYMENT:
                openPaymentHistory();
                break;
        }


    }

    //Function is use for mark notification as read
    private void markAsRead(NotificationData notificationData) {
        if (notificationData != null && notificationData.getId() != null) {
            notificationViewModel.markNotificationRead(notificationData.getId());
        }
    }

    /*
     * open PaymentHistory Screen
     * */
    private void openPaymentHistory() {
        Intent intent = new Intent(mActivity, PaymentDetailsActivity.class);
        intent.putExtra(AppConstants.PAYMENT, true);
        startActivity(intent);
    }


    private void openGiftActivity() {
        Intent intent = new Intent(mActivity, GiftRewardPromotionActivity.class);
        startActivity(intent);
    }

    private void openCartActivity() {
        Intent intent = new Intent(mActivity, CartActivity.class);
        startActivity(intent);
    }

    private Intent perfromAction(String key, NotificationData notificationData, Class actionClass) {
        Intent intent = new Intent(mActivity, actionClass);
        if (notificationData.getReadStatus() == 1)
            intent.putExtra(AppConstants.NOTIFICATION_ACTION.NOTIFICATION_ID, notificationData.getId());
        intent.putExtra(key, notificationData.getEntityId());
        return intent;
    }

    private void openFollowFollowingActivity() {
        Intent intent = new Intent(mActivity, FollowFollowerActivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, 1);
        intent.putExtra(AppConstants.KEY_CONSTENT.USER_ID, DataManager.getInstance().getUserDetails().getUserId());
        intent.putExtra("IS_OTHER_PROFILE", false);
        mActivity.startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.FOLLOWFOLLOWING);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        notificationViewModel.hitGetAllNotification(currentPage, limit, true);
    }
}
