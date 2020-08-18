package com.taghawk.ui.chat;

import android.app.Activity;

import android.content.Intent;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.adapters.GroupMembersAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_view.PositionedLinkedHashmap;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.FragmentGroupMembersBinding;
import com.taghawk.databinding.LayoutGroupMemberProfilePopupWindowBinding;
import com.taghawk.firebase.FirebaseManager;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.RemoveFirebaseListenerModel;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.chat.MemberModel;
import com.taghawk.model.chat.TagDetailFirebaseModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.model.request.User;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tagaddresponse.AddTagResponse;
import com.taghawk.ui.profile.OtherProfileActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class GroupMemberFragment extends BaseFragment implements View.OnClickListener {

    public static final int MEMBER_ACTION_TRANSFER_OWNERSHIP = 1;
    public static final int MEMBER_ACTION_REMOVE = 2;
    public static final int MEMBER_ACTION_BLOCK = 3;
    public static final int MEMBER_ACTION_DELETE_TAG = 4;
    public static final int MEMBER_ACTION_EXIT_TAG = 5;
    private FragmentGroupMembersBinding fragmentGroupDetailBinding;
    private GroupDetailViewModel groupDetailViewModel;
    private Activity mActivity;
    private User user;
    public ChatModel chatModel;
    private Query groupDetailQuery;
    private ValueEventListener valueEventListener;
    private TagDetailFirebaseModel tagDetailFirebaseModel;
    private GroupMembersAdapter groupMembersAdapter;
    private PopupWindow popup;
    private int myMembertype;
    private boolean isChatClicked;
    private PositionedLinkedHashmap<String, MemberModel> membersHashmap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentGroupDetailBinding = FragmentGroupMembersBinding.inflate(inflater, container, false);
        initView();
        return fragmentGroupDetailBinding.getRoot();
    }

    /**
     * used to initialize the views and variables
     */
    private void initView() {
        mActivity = getActivity();
        membersHashmap = new PositionedLinkedHashmap<>();
        fragmentGroupDetailBinding.ivBack.setOnClickListener(this);
        groupDetailViewModel = ViewModelProviders.of(this).get(GroupDetailViewModel.class);
        groupDetailViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
        chatModel = getArguments().getParcelable(AppConstants.FIREBASE.FIREBASE_CHAT_DATA);
        fragmentGroupDetailBinding.rvGroupMembers.setLayoutManager(new LinearLayoutManager(mActivity));
        groupMembersAdapter = new GroupMembersAdapter(membersHashmap, new GroupMembersAdapter.SearchListener() {
            @Override
            public void onSearch(int searchCount) {

            }
        },
                new GroupMembersAdapter.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(MemberModel memberModel, View view) {
                        if (memberModel.getMemberType()!= AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_SUPER_ADMIN&& !memberModel.getMemberId().equalsIgnoreCase(user.getUserId()))
                            showGroupMembersPopupWindow(memberModel, view);
                    }
                });

        groupDetailViewModel.getReportTagLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse != null) {
                    showToastShort(commonResponse.getMessage());
                    Intent intent = new Intent();
                    intent.putExtra(AppConstants.DEEP_INK_CONSTENT.TYPE, AppConstants.UNFOLLOW_REMOVE_ACTION.REMOVE);
                    mActivity.setResult(Activity.RESULT_OK, intent);
                    mActivity.finish();
                    groupDetailQuery.removeEventListener(valueEventListener);
                    groupDetailViewModel.exitTag(user.getUserId(), user.getFullName(), tagDetailFirebaseModel.getTagId());
                }
            }
        });
        groupDetailViewModel.getBlockUserLiveData().observe(this, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(@Nullable ProfileResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse != null && commonResponse.getCode() == 200) {
                    showToastShort(commonResponse.getMessage());
                    groupDetailViewModel.blockUserOnFirebase(user.getUserId(), (String) commonResponse.getExtraLocalData().get(AppConstants.KEY_CONSTENT.USER_ID));
                }
            }
        });
        groupDetailViewModel.getMembersActionLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse != null) {
                    switch ((int) commonResponse.getExtraLocalData().get(AppConstants.KEY_CONSTENT.ACTION_TYPE)) {
                        case MEMBER_ACTION_REMOVE:
                            groupDetailViewModel.removeMember((String) commonResponse.getExtraLocalData().get(AppConstants.KEY_CONSTENT.USER_ID), (String) commonResponse.getExtraLocalData().get(AppConstants.KEY_CONSTENT.MEMBER_NAME), chatModel.getRoomId());
                            break;
                        case MEMBER_ACTION_TRANSFER_OWNERSHIP:
                            groupDetailViewModel.transferOwnership(user, (String) commonResponse.getExtraLocalData().get(AppConstants.KEY_CONSTENT.USER_ID), (String) commonResponse.getExtraLocalData().get(AppConstants.KEY_CONSTENT.MEMBER_NAME), chatModel.getRoomId());
                            break;
                        case MEMBER_ACTION_DELETE_TAG:
                            Intent intent = new Intent();
                            intent.putExtra(AppConstants.DEEP_INK_CONSTENT.TYPE, AppConstants.UNFOLLOW_REMOVE_ACTION.REMOVE);
                            mActivity.setResult(Activity.RESULT_OK, intent);
                            mActivity.finish();
                            groupDetailQuery.removeEventListener(valueEventListener);
                            groupDetailViewModel.deleteTag(user.getUserId(), tagDetailFirebaseModel.getMembers(), tagDetailFirebaseModel.getTagId());
                            break;
                        case MEMBER_ACTION_EXIT_TAG:
                            Intent intent2 = new Intent();
                            intent2.putExtra(AppConstants.DEEP_INK_CONSTENT.TYPE, AppConstants.UNFOLLOW_REMOVE_ACTION.REMOVE);
                            mActivity.setResult(Activity.RESULT_OK, intent2);
                            mActivity.finish();
                            groupDetailQuery.removeEventListener(valueEventListener);
                            groupDetailViewModel.exitTag(user.getUserId(), user.getFullName(), tagDetailFirebaseModel.getTagId());
                            break;
                    }
                }
            }
        });
        groupDetailViewModel.editTagLiveData().observe(this, new Observer<AddTagResponse>() {
            @Override
            public void onChanged(@Nullable AddTagResponse addTagResponse) {
                getLoadingStateObserver().onChanged(false);
                if (addTagResponse != null) {
                    TagData tagData = addTagResponse.getData();
                    switch (addTagResponse.getEditType()) {
                        case AppConstants.KEY_CONSTENT.NAME:
                            tagDetailFirebaseModel.setTagName(tagData.getTagName());
                            groupDetailViewModel.changeTagName(tagDetailFirebaseModel.getTagName(), chatModel.getRoomId());
                            break;
                        case AppConstants.TAG_KEY_CONSTENT.ANNOUNCEMENT:
                            tagDetailFirebaseModel.setAnnouncement(tagData.getAnnouncement());
                            groupDetailViewModel.changeAnnouncement(tagDetailFirebaseModel.getAnnouncement(), chatModel.getRoomId());
                            break;
                        case AppConstants.TAG_KEY_CONSTENT.DESCRIPTION:
                            tagDetailFirebaseModel.setDescription(tagData.getDescription());
                            groupDetailViewModel.changeDescription(tagDetailFirebaseModel.getDescription(), chatModel.getRoomId());
                            break;
                        case AppConstants.TAG_KEY_CONSTENT.IMAGE_URL:
                            tagDetailFirebaseModel.setTagImageUrl(tagData.getTagImageUrl());
                            groupDetailViewModel.changeTagImage(tagDetailFirebaseModel.getTagImageUrl(), chatModel.getRoomId());
                            break;
                        case AppConstants.TAG_KEY_CONSTENT.ADDRESS:
                            tagDetailFirebaseModel.setTagAddress(tagData.getTagAddress());
                            tagDetailFirebaseModel.setTagLatitude(Double.parseDouble(tagData.getTagLatitude()));
                            tagDetailFirebaseModel.setTagLongitude(Double.parseDouble(tagData.getTagLongitude()));
                            groupDetailViewModel.changeTagAddress(tagDetailFirebaseModel.getTagAddress(), tagDetailFirebaseModel.getTagLatitude(), tagDetailFirebaseModel.getTagLongitude(), chatModel.getRoomId());
                            break;
                        case AppConstants.TAG_KEY_CONSTENT.DOCUMENT_TYPE:
                            tagDetailFirebaseModel.setVerificationType(AppConstants.TAG_VERIFICATION_METHOD.DOCUMENT);
                            groupDetailViewModel.changeVerificationType(tagDetailFirebaseModel.getVerificationType(), chatModel.getRoomId());
                            break;
                        case AppConstants.TAG_KEY_CONSTENT.PASSWORD:
                            tagDetailFirebaseModel.setVerificationData(tagData.getJoinTagData());
                            groupDetailViewModel.changeVerificationData(tagDetailFirebaseModel.getVerificationData(), chatModel.getRoomId());
                            tagDetailFirebaseModel.setVerificationType(AppConstants.TAG_VERIFICATION_METHOD.PASSWORD);
                            groupDetailViewModel.changeVerificationType(tagDetailFirebaseModel.getVerificationType(), chatModel.getRoomId());
                            break;
                        case AppConstants.TAG_KEY_CONSTENT.EMAIL:
                            tagDetailFirebaseModel.setVerificationData(tagData.getJoinTagData());
                            groupDetailViewModel.changeVerificationData(tagDetailFirebaseModel.getVerificationData(), chatModel.getRoomId());
                            tagDetailFirebaseModel.setVerificationType(AppConstants.TAG_VERIFICATION_METHOD.EMAIL);
                            groupDetailViewModel.changeVerificationType(tagDetailFirebaseModel.getVerificationType(), chatModel.getRoomId());
                            break;
                        case AppConstants.TAG_KEY_CONSTENT.TYPE:
                            tagDetailFirebaseModel.setTagType(tagData.getTagType());
                            tagDetailFirebaseModel.setVerificationData("");
                            tagDetailFirebaseModel.setVerificationType(tagData.getJoinTagBy());
                            groupDetailViewModel.changeTagType(tagData.getTagType() == 1, chatModel.getRoomId());
                            break;
                    }
                }
            }
        });
        fragmentGroupDetailBinding.etSearchGroupMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                groupMembersAdapter.getFilter().filter(fragmentGroupDetailBinding.etSearchGroupMember.getText().toString().trim());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        fragmentGroupDetailBinding.rvGroupMembers.setAdapter(groupMembersAdapter);
        getArgumentsData();
        getGroupDetail();
    }

    private void getArgumentsData() {
        if (GroupDetailFragment.membersHashmap != null) {
            membersHashmap.putAll(GroupDetailFragment.membersHashmap);
            membersHashmap.updateIndexes();
            groupMembersAdapter.notifyDataSetChanged();
        }
        tagDetailFirebaseModel = getArguments().getParcelable(AppConstants.BUNDLE_DATA);
    }


    /**
     * used to set the tag/group data
     */

    /**
     * used to set the data regarding tag type or verification type
     */

    /**
     * used to update the group members when group data is fetched or updated
     */
    private void updateGroupMembers() {
        membersHashmap.clear();
        membersHashmap.putAll(tagDetailFirebaseModel.getMembers());
        membersHashmap.updateIndexes();
        groupMembersAdapter.getFilter().filter(fragmentGroupDetailBinding.etSearchGroupMember.getText().toString().trim());
    }

    private void getGroupDetail() {
        groupDetailQuery = groupDetailViewModel.getGroupDetailQuery(chatModel.getRoomId());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    tagDetailFirebaseModel = dataSnapshot.getValue(TagDetailFirebaseModel.class);
                    updateGroupMembers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        groupDetailQuery.addValueEventListener(valueEventListener);
    }

    /**
     * used to show the popup window for the group member
     *
     * @param memberModel member model clicked
     * @param view        anchor view for popup window
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showGroupMembersPopupWindow(final MemberModel memberModel, View view) {
        LayoutGroupMemberProfilePopupWindowBinding popBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_group_member_profile_popup_window, null, false);
        popup = new PopupWindow(mActivity);
        popup.setContentView(popBinding.getRoot());
        popup.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        popup.setElevation(2f);
        popup.setOverlapAnchor(true);
        if (user.getUserId().equalsIgnoreCase(tagDetailFirebaseModel.getOwnerId())) {
            if (memberModel.getBlocked())
                popBinding.tvBlock.setText(getString(R.string.unblock));
            else
                popBinding.tvBlock.setText(getString(R.string.block));
            popBinding.tvBlock.append(" ");
            popBinding.tvBlock.append(memberModel.getMemberName());
            popBinding.tvBlock.setVisibility(View.VISIBLE);
            if (memberModel.getMemberType() == AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_ADMIN)
                popBinding.tvMakeAdmin.setText(getString(R.string.remove_as_group_admin));
            else
                popBinding.tvMakeAdmin.setText(getString(R.string.make_group_admin));
            popBinding.tvMute.setText(memberModel.getMute() ? getString(R.string.unmute) + " " + memberModel.getMemberName() : getString(R.string.mute) + " " + memberModel.getMemberName());
            popBinding.tvMakeAdmin.setVisibility(View.VISIBLE);
            popBinding.tvRemove.setText(getString(R.string.remove) + " " + memberModel.getMemberName());
        } else {
            popBinding.tvBlock.setVisibility(View.GONE);
            popBinding.tvMakeAdmin.setVisibility(View.GONE);
            popBinding.tvTransferOwnership.setVisibility(View.GONE);
            popBinding.tvMute.setVisibility(View.GONE);
            popBinding.tvRemove.setVisibility(View.GONE);
        }
        popBinding.tvMessageUser.setText(getString(R.string.message) + " " + memberModel.getMemberName());
        popBinding.tvMessageUser.setVisibility(View.VISIBLE);
        popBinding.tvViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, OtherProfileActivity.class);
                intent.putExtra(AppConstants.BUNDLE_DATA, memberModel.getMemberId());
                mActivity.startActivity(intent);
                popup.dismiss();
            }
        });
        popBinding.tvMakeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtils.isConnection(mActivity)) {
                    int type;
                    if (memberModel.getMemberType() == AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_ADMIN)
                        type = AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_MEMBER;
                    else
                        type = AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_ADMIN;
                    groupDetailViewModel.makeAdmin(chatModel.getRoomId(), memberModel.getMemberId(), type);
                    memberModel.setMemberType(type);
                } else
                    showToastShort(getString(R.string.no_internet));
                popup.dismiss();
            }
        });
        popBinding.tvBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.getInstance().CustomUnFollowRemoveBottomSheetDialog(mActivity, getString(R.string.block_msg), memberModel.getMemberName(), getString(R.string.block), memberModel.getMemberImage(), false, true, new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {
                        if (AppUtils.isConnection(mActivity)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(AppConstants.KEY_CONSTENT.USER_ID, memberModel.getMemberId());
                            hashMap.put(AppConstants.KEY_CONSTENT.ACTION, 3);
                            getLoadingStateObserver().onChanged(true);
                            groupDetailViewModel.blockUser(hashMap, memberModel.getMemberName(), MEMBER_ACTION_BLOCK);
                        } else
                            showToastShort(getString(R.string.no_internet));
                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });
                popup.dismiss();
            }
        });
        popBinding.tvTransferOwnership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.done_lowercase), getString(R.string.transfer_ownership), getString(R.string.are_you_sure_you_want_to_transfer_the_ownership_to) + " " + memberModel.getMemberName() + "?", new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {
                        if (AppUtils.isConnection(mActivity)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                            hashMap.put(AppConstants.KEY_CONSTENT.USER_ID, memberModel.getMemberId());
                            getLoadingStateObserver().onChanged(true);
                            groupDetailViewModel.transferOwnership(hashMap, memberModel.getMemberName(), MEMBER_ACTION_TRANSFER_OWNERSHIP);
                        } else
                            showToastShort(getString(R.string.no_internet));
                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });
                popup.dismiss();
            }
        });
        popBinding.tvMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AppUtils.isConnection(mActivity)) {
                    groupDetailViewModel.muteUnmuteUser(chatModel.getRoomId(), memberModel.getMemberId(), !memberModel.getMute());
                    memberModel.setMute(!memberModel.getMute());
                }
                popup.dismiss();
            }
        });
        popBinding.tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.done_lowercase), getString(R.string.remove_member) + "!", getString(R.string.are_you_sure_you_want_to_remove) + " " + memberModel.getMemberName() + "?", new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {
                        if (AppUtils.isConnection(mActivity)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                            hashMap.put(AppConstants.KEY_CONSTENT.USER_ID, memberModel.getMemberId());
                            getLoadingStateObserver().onChanged(true);
                            groupDetailViewModel.removeMember(hashMap, memberModel.getMemberName(), MEMBER_ACTION_REMOVE);
                        } else
                            showToastShort(getString(R.string.no_internet));
                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });
                popup.dismiss();
            }
        });
        popBinding.tvMessageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isChatClicked) {
                    isChatClicked = true;
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(FirebaseManager.getFirebaseRoomId(user.getUserId(), memberModel.getMemberId())).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                chatModel.setRoomName(memberModel.getMemberName());
                                chatModel.setRoomImage(memberModel.getMemberImage());
                                chatModel.setChatType(AppConstants.FIREBASE.FIREBASE_SINGLE_CHAT);
                                chatModel.setOtherUserId(memberModel.getMemberId());
                                chatModel.setRoomId(FirebaseManager.getFirebaseRoomId(user.getUserId(), memberModel.getMemberId()));
                                ChatProductModel chatProductModel = new ChatProductModel();
                                chatProductModel.setProductId("");
                                chatProductModel.setProductName("");
                                chatProductModel.setProductPrice(0);
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
                    popup.dismiss();
                }
            }
        });
        int[] values = new int[2];
        view.getLocationInWindow(values);
        int positionOfIcon = values[1];
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int height = (displayMetrics.heightPixels * 2) / 3;
        if (positionOfIcon > height) {
            if (myMembertype == AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_OWNER)
                popup.showAsDropDown(view, 0, -500, Gravity.END);
            else
                popup.showAsDropDown(view, 0, -200, Gravity.END);
        } else {
            popup.showAsDropDown(view, 0, 0, Gravity.END);
        }
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        getLoadingStateObserver().onChanged(false);
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        super.onErrorOccurred(throwable);
        getLoadingStateObserver().onChanged(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (groupMembersAdapter != null && groupMembersAdapter.listenerModelHashMap != null) {
            for (final Map.Entry<String, RemoveFirebaseListenerModel> entry : groupMembersAdapter.listenerModelHashMap.entrySet()) {
                RemoveFirebaseListenerModel removeFirebaseListenerModel = entry.getValue();
                removeFirebaseListenerModel.getQuery().removeEventListener(removeFirebaseListenerModel.getValueEventListener());
            }
            groupMembersAdapter.listenerModelHashMap.clear();
        }
        groupDetailQuery.removeEventListener(valueEventListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                mActivity.finish();
                break;
        }
    }
}
