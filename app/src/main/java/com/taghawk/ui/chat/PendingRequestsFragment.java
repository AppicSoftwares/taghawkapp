package com.taghawk.ui.chat;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.adapters.PendingRequestsAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.FragmentPendingRequestsBinding;
import com.taghawk.firebase.FirebaseManager;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.pendingRequests.PendingRequest;
import com.taghawk.model.pendingRequests.PendingRequestResponse;
import com.taghawk.model.request.User;
import com.taghawk.model.tag.TagData;
import com.taghawk.ui.home.ZoomImageActivity;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class PendingRequestsFragment extends BaseFragment implements View.OnClickListener {

    private GroupDetailViewModel groupDetailViewModel;
    private FragmentPendingRequestsBinding fragmentPendingRequestsBinding;
    private Activity mActivity;
    private String tagId, tagImage, tagName;
    private ArrayList<PendingRequest> pendingRequestsList;
    private PendingRequestsAdapter pendingRequestsAdapter;
    private boolean isChatClicked, isLoading;

    /**
     * This method is used to return the instance of this fragment
     *
     * @return new instance of {@link PendingRequestsFragment}
     */
    public static PendingRequestsFragment getInstance() {
        return new PendingRequestsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentPendingRequestsBinding = FragmentPendingRequestsBinding.inflate(inflater, container, false);
        initView();
        return fragmentPendingRequestsBinding.getRoot();
    }

    // init views and listener
    private void initView() {
        mActivity = getActivity();
        pendingRequestsList = new ArrayList<>();
        groupDetailViewModel = ViewModelProviders.of(this).get(GroupDetailViewModel.class);
        groupDetailViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        fragmentPendingRequestsBinding.tvToolbarTitle.setText(R.string.pending_requests);
        fragmentPendingRequestsBinding.ibBack.setOnClickListener(this);
        if (getArguments() == null)
            return;
        tagId = getArguments().getString(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID);
        tagName = getArguments().getString(AppConstants.TAG_KEY_CONSTENT.NAME);
        tagImage = getArguments().getString(AppConstants.TAG_KEY_CONSTENT.IMAGE_URL);
        fragmentPendingRequestsBinding.rvPendingRequests.setLayoutManager(new LinearLayoutManager(mActivity));
        pendingRequestsAdapter = new PendingRequestsAdapter(pendingRequestsList, new RecyclerViewCallback() {
            @Override
            public void onClick(int position, View view) {
                final PendingRequest pendingRequest = pendingRequestsList.get(position);
                switch (view.getId()) {
                    case R.id.tv_view_document:
                        ArrayList<ImageList> imageLists = new ArrayList<>();
                        for (String imageUrl : pendingRequest.getDocumentUrl()) {
                            ImageList image = new ImageList();
                            image.setThumbUrl(imageUrl);
                            image.setUrl(imageUrl);
                            imageLists.add(image);
                        }
                        Intent intent = new Intent(mActivity, ZoomImageActivity.class);
                        intent.putExtra("ImageUrl", imageLists);
                        startActivity(intent);
                        break;
                    case R.id.ib_accept:
//                        view.setEnabled(false);
                        if (!isLoading) {
                            if (AppUtils.isConnection(mActivity)) {
                                isLoading = true;
                                getLoadingStateObserver().onChanged(true);
                                groupDetailViewModel.acceptRejectTagRequest(pendingRequest.getSenderId(), tagId, 1);
//                                view.setEnabled(true);
                            } else
                                showToastShort(getString(R.string.no_internet));
                        }
                        break;
                    case R.id.ib_reject:
                        if (!isLoading) {
                            if (AppUtils.isConnection(mActivity)) {
                                isLoading = true;
                                getLoadingStateObserver().onChanged(true);
                                groupDetailViewModel.acceptRejectTagRequest(pendingRequest.getSenderId(), tagId, 2);
                            } else
                                showToastShort(getString(R.string.no_internet));
                        }
                        break;
                    case R.id.ib_chat:
                        if (!isChatClicked) {
                            isChatClicked = true;
                            final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(FirebaseManager.getFirebaseRoomId(user.getUserId(), pendingRequest.getSenderId())).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ChatModel chatModel = null;
                                    if (dataSnapshot.exists())
                                        chatModel = dataSnapshot.getValue(ChatModel.class);
                                    else {
                                        chatModel = new ChatModel();
                                        chatModel.setPinned(false);
                                        chatModel.setChatMute(false);
                                        chatModel.setCreatedTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                                        chatModel.setRoomName(pendingRequest.getSenderName());
                                        chatModel.setRoomImage(pendingRequest.getSenderProfilePic());
                                        chatModel.setChatType(AppConstants.FIREBASE.FIREBASE_SINGLE_CHAT);
                                        chatModel.setOtherUserId(pendingRequest.getSenderId());
                                        chatModel.setRoomId(FirebaseManager.getFirebaseRoomId(user.getUserId(), pendingRequest.getSenderId()));
                                        ChatProductModel chatProductModel = new ChatProductModel();
                                        chatProductModel.setProductId("");
                                        chatProductModel.setProductName("");
                                        chatProductModel.setProductPrice(Double.parseDouble("0"));
                                        chatProductModel.setProductImage("");
                                        chatModel.setProductInfo(chatProductModel);
                                    }

                                    startActivity(new Intent(mActivity, MessagesDetailActivity.class).putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel).putExtra(AppConstants.FIREBASE.TIMESTAMP, chatModel.getCreatedTimeStampLong()));
                                    isChatClicked = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    isChatClicked = false;
                                }
                            });
                        }
                        break;

                }
            }
        });
        fragmentPendingRequestsBinding.rvPendingRequests.setAdapter(pendingRequestsAdapter);
        if (AppUtils.isConnection(mActivity)) {
            isLoading = true;
            getLoadingStateObserver().onChanged(true);
            groupDetailViewModel.getPendingRequests(tagId);
        } else {
            showToastShort(getString(R.string.no_internet));
            fragmentPendingRequestsBinding.tvNoResults.setVisibility(View.VISIBLE);
        }
        groupDetailViewModel.getPendingRequestsLiveData().observe(this, new Observer<PendingRequestResponse>() {
            @Override
            public void onChanged(@Nullable PendingRequestResponse pendingRequestResponse) {
                isLoading = false;
                getLoadingStateObserver().onChanged(false);
                if (pendingRequestResponse != null) {
                    TransitionManager.beginDelayedTransition(fragmentPendingRequestsBinding.llMain, new ChangeBounds());
                    pendingRequestsList.addAll(pendingRequestResponse.getPendingRequestResult().getPendingRequests());
                    pendingRequestsAdapter.notifyDataSetChanged();
                }
            }
        });
        groupDetailViewModel.getAcceptRejectLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                isLoading = false;
                if (commonResponse == null)
                    getLoadingStateObserver().onChanged(false);
                if (commonResponse != null) {
                    groupDetailViewModel.updatePendingCount(tagId, true);
                    updateList((String) commonResponse.getExtraLocalData().get(AppConstants.KEY_CONSTENT.USER_ID), (int) commonResponse.getExtraLocalData().get(AppConstants.KEY_CONSTENT.STATUS));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                mActivity.onBackPressed();
                break;
        }
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        isLoading = false;
        getLoadingStateObserver().onChanged(false);
        if (pendingRequestsList.size() == 0)
            fragmentPendingRequestsBinding.tvNoResults.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        isLoading = false;
        getLoadingStateObserver().onChanged(false);
        if (pendingRequestsList.size() == 0)
            fragmentPendingRequestsBinding.tvNoResults.setVisibility(View.VISIBLE);
    }

    /**
     * used to update the list if accepts or reject the request
     *
     * @param memberId member for which request accepted or rejected
     * @param status   accept(1) or reject(2)
     */
    private void updateList(String memberId, int status) {
        int position = -1;
        TransitionManager.beginDelayedTransition(fragmentPendingRequestsBinding.llMain, new ChangeBounds());
        for (PendingRequest pendingRequest : pendingRequestsList) {
            position += 1;
            if (pendingRequest.getSenderId().equalsIgnoreCase(memberId)) {
                if (status == 1) {
                    User user = new User();
                    user.setUserId(memberId);
                    user.setFullName(pendingRequest.getSenderName());
                    user.setProfilePicture(pendingRequest.getSenderProfilePic());
                    user.setUserType(1);
                    TagData tagData = new TagData();
                    tagData.setTagName(tagName);
                    tagData.setTagId(tagId);
                    tagData.setTagImageUrl(tagImage);
                    groupDetailViewModel.joinTagOnFirebase(user, tagData);
                }
                pendingRequestsList.remove(pendingRequest);
                pendingRequestsAdapter.notifyItemRemoved(position);
                if (pendingRequestsList.size() == 0)
                    fragmentPendingRequestsBinding.tvNoResults.setVisibility(View.VISIBLE);
                getLoadingStateObserver().onChanged(false);
                break;
            }
        }
    }
}
