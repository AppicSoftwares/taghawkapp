package com.taghawk.ui.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.adapters.GroupMembersAdapter;
import com.taghawk.base.BaseActivity;
import com.taghawk.base.BaseFragment;
import com.taghawk.camera2basic.CameraTwoActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_view.PositionedLinkedHashmap;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.FragmentGroupDetailBinding;
import com.taghawk.databinding.LayoutGroupMemberProfilePopupWindowBinding;
import com.taghawk.firebase.FirebaseManager;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.interfaces.OnDialogItemObjectClickListener;
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
import com.taghawk.ui.home.filter.ChoosseLocationActivity;
import com.taghawk.ui.profile.OtherProfileActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.PermissionUtility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class GroupDetailFragment extends BaseFragment implements View.OnClickListener, AmazonCallback, CompoundButton.OnCheckedChangeListener {

    public static final int MEMBER_ACTION_TRANSFER_OWNERSHIP = 1;
    public static final int MEMBER_ACTION_REMOVE = 2;
    public static final int MEMBER_ACTION_BLOCK = 3;
    public static final int MEMBER_ACTION_DELETE_TAG = 4;
    public static final int MEMBER_ACTION_EXIT_TAG = 5;
    private FragmentGroupDetailBinding fragmentGroupDetailBinding;
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
    private AmazonS3 mAmazonS3;
    private boolean isChatClicked;
    public static PositionedLinkedHashmap<String, MemberModel> membersHashmap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentGroupDetailBinding = FragmentGroupDetailBinding.inflate(inflater, container, false);
        initView();
        return fragmentGroupDetailBinding.getRoot();
    }

    /**
     * used to initialize the views and variables
     */
    private void initView() {
        mActivity = getActivity();
        membersHashmap = new PositionedLinkedHashmap<>();
        mAmazonS3 = AmazonS3.getInstance(mActivity, this, AppConstants.AMAZON_S3.AMAZON_POOLID, AppConstants.AMAZON_S3.BUCKET, AppConstants.AMAZON_S3.AMAZON_SERVER_URL, AppConstants.AMAZON_S3.END_POINT);
        groupDetailViewModel = ViewModelProviders.of(this).get(GroupDetailViewModel.class);
        groupDetailViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
        chatModel = getArguments().getParcelable(AppConstants.FIREBASE.FIREBASE_CHAT_DATA);
        fragmentGroupDetailBinding.tvTagName.setText(chatModel.getRoomName());
        AppUtils.loadCircularImage(mActivity, chatModel.getRoomImage(), 300, R.drawable.ic_home_placeholder, fragmentGroupDetailBinding.ivTag, true);
        fragmentGroupDetailBinding.rvGroupMembers.setLayoutManager(new LinearLayoutManager(mActivity));
        groupMembersAdapter = new GroupMembersAdapter(membersHashmap, new GroupMembersAdapter.SearchListener() {
            @Override
            public void onSearch(int searchCount) {
                if (searchCount > 0)
                    fragmentGroupDetailBinding.tvNoResults.setVisibility(View.GONE);
                else
                    fragmentGroupDetailBinding.tvNoResults.setVisibility(View.VISIBLE);
                fragmentGroupDetailBinding.nsvDetail.scrollTo(0, fragmentGroupDetailBinding.nsvDetail.getBottom());
            }
        },
                new GroupMembersAdapter.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(MemberModel memberModel, View view) {
                        if (memberModel.getMemberType()!=AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_SUPER_ADMIN&&!memberModel.getMemberId().equalsIgnoreCase(user.getUserId()))
                            showGroupMembersPopupWindow(memberModel, view);
                    }
                });
        fragmentGroupDetailBinding.btnDelete.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fragmentGroupDetailBinding.nsvDetail.getLayoutParams();
                layoutParams.bottomMargin = fragmentGroupDetailBinding.btnDelete.getHeight();
                fragmentGroupDetailBinding.nsvDetail.setLayoutParams(layoutParams);
                fragmentGroupDetailBinding.btnDelete.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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
//        fragmentGroupDetailBinding.etSearchGroupMember.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                groupMembersAdapter.getFilter().filter(fragmentGroupDetailBinding.etSearchGroupMember.getText().toString().trim());
//                if (charSequence.toString().trim().equalsIgnoreCase("")) {
//                    fragmentGroupDetailBinding.llProfile.setVisibility(View.VISIBLE);
//                    fragmentGroupDetailBinding.llDetail.setVisibility(View.VISIBLE);
//                } else {
//                    fragmentGroupDetailBinding.llProfile.setVisibility(View.GONE);
//                    fragmentGroupDetailBinding.llDetail.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
        fragmentGroupDetailBinding.rvGroupMembers.setAdapter(groupMembersAdapter);
        fragmentGroupDetailBinding.ibBack.setOnClickListener(this);
        fragmentGroupDetailBinding.ibAlert.setOnClickListener(this);
        fragmentGroupDetailBinding.ibShare.setOnClickListener(this);
        fragmentGroupDetailBinding.tvTagName.setOnClickListener(this);
        fragmentGroupDetailBinding.tvLocation.setOnClickListener(this);
        fragmentGroupDetailBinding.llEmail.setOnClickListener(this);
        fragmentGroupDetailBinding.ibEmailCheck.setOnClickListener(this);
        fragmentGroupDetailBinding.ibEmailEdit.setOnClickListener(this);
        fragmentGroupDetailBinding.llPassword.setOnClickListener(this);
        fragmentGroupDetailBinding.ibPasswordCheck.setOnClickListener(this);
        fragmentGroupDetailBinding.ibPasswordEdit.setOnClickListener(this);
        fragmentGroupDetailBinding.llDocument.setOnClickListener(this);
        fragmentGroupDetailBinding.ibCheckDocument.setOnClickListener(this);
        fragmentGroupDetailBinding.btnDelete.setOnClickListener(this);
        fragmentGroupDetailBinding.ibEditTagImage.setOnClickListener(this);
        fragmentGroupDetailBinding.ibAnnouncementEdit.setOnClickListener(this);
        fragmentGroupDetailBinding.ibDescriptionEdit.setOnClickListener(this);
        fragmentGroupDetailBinding.tvPendingRequests.setOnClickListener(this);
        fragmentGroupDetailBinding.etSearchGroupMember.setOnClickListener(this);
        getGroupDetail();
    }

    /**
     * used to get the tag/group detail from the firebase server
     */
    private void getGroupDetail() {
        groupDetailQuery = groupDetailViewModel.getGroupDetailQuery(chatModel.getRoomId());
        fragmentGroupDetailBinding.progressBar.setVisibility(View.VISIBLE);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fragmentGroupDetailBinding.progressBar.setVisibility(View.GONE);
                if (dataSnapshot.getValue() != null) {
                    tagDetailFirebaseModel = dataSnapshot.getValue(TagDetailFirebaseModel.class);
                    setGroupData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                fragmentGroupDetailBinding.progressBar.setVisibility(View.GONE);
            }
        };
        groupDetailQuery.addValueEventListener(valueEventListener);
    }

    /**
     * used to set the tag/group data
     */
    private void setGroupData() {
        fragmentGroupDetailBinding.switchPrivate.setOnCheckedChangeListener(null);
        fragmentGroupDetailBinding.switchMuteChat.setOnCheckedChangeListener(null);
        fragmentGroupDetailBinding.switchPinOnTop.setOnCheckedChangeListener(null);
        if (tagDetailFirebaseModel != null && tagDetailFirebaseModel.getMembers().containsKey(user.getUserId())) {
            myMembertype = tagDetailFirebaseModel.getMembers().get(user.getUserId()).getMemberType();
            TransitionManager.beginDelayedTransition(fragmentGroupDetailBinding.clMain, new ChangeBounds());
            updateGroupMembers();
            if (myMembertype == AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_OWNER) {
                fragmentGroupDetailBinding.ibAlert.setVisibility(View.GONE);
                fragmentGroupDetailBinding.tvTagName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_group_edit, 0);
                fragmentGroupDetailBinding.ibEditTagImage.setVisibility(View.VISIBLE);
                fragmentGroupDetailBinding.ibAnnouncementEdit.setVisibility(View.VISIBLE);
                fragmentGroupDetailBinding.ibDescriptionEdit.setVisibility(View.VISIBLE);
                fragmentGroupDetailBinding.btnDelete.setText(getString(R.string.delete_tag));
            } else if (myMembertype == AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_ADMIN){
                fragmentGroupDetailBinding.ibAlert.setVisibility(View.VISIBLE);
                fragmentGroupDetailBinding.tvTagName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                fragmentGroupDetailBinding.ibEditTagImage.setVisibility(View.GONE);
                fragmentGroupDetailBinding.ibAnnouncementEdit.setVisibility(View.VISIBLE);
                fragmentGroupDetailBinding.ibDescriptionEdit.setVisibility(View.VISIBLE);
                fragmentGroupDetailBinding.btnDelete.setText(getString(R.string.exit_tag));
            }
            else
            {
                fragmentGroupDetailBinding.ibAlert.setVisibility(View.VISIBLE);
                fragmentGroupDetailBinding.tvTagName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                fragmentGroupDetailBinding.ibEditTagImage.setVisibility(View.GONE);
                fragmentGroupDetailBinding.ibAnnouncementEdit.setVisibility(View.GONE);
                fragmentGroupDetailBinding.ibDescriptionEdit.setVisibility(View.GONE);
                fragmentGroupDetailBinding.btnDelete.setText(getString(R.string.exit_tag));
            }
            AppUtils.loadCircularImage(mActivity, tagDetailFirebaseModel.getTagImageUrl(), 300, R.drawable.ic_home_placeholder, fragmentGroupDetailBinding.ivTag, true);
            fragmentGroupDetailBinding.tvTagName.setText(tagDetailFirebaseModel.getTagName());
            if (tagDetailFirebaseModel.getAnnouncement() != null)
                fragmentGroupDetailBinding.tvAnnouncement.setText(tagDetailFirebaseModel.getAnnouncement().trim());
            if (tagDetailFirebaseModel.getDescription() != null)
                fragmentGroupDetailBinding.tvDescription.setText(tagDetailFirebaseModel.getDescription().trim());
            fragmentGroupDetailBinding.switchPinOnTop.setChecked(chatModel.isPinned());
            fragmentGroupDetailBinding.switchMuteChat.setChecked(chatModel.isChatMute());
            fragmentGroupDetailBinding.tvLocation.setText(tagDetailFirebaseModel.getTagAddress());
            fragmentGroupDetailBinding.switchPrivate.setChecked(tagDetailFirebaseModel.getTagType() == 1);
            if (fragmentGroupDetailBinding.switchPrivate.isChecked()) {
                fragmentGroupDetailBinding.switchPrivate.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_light_gray));
                fragmentGroupDetailBinding.tvPrivate.setTextColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
            } else {
                fragmentGroupDetailBinding.switchPrivate.setTextColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                fragmentGroupDetailBinding.tvPrivate.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_light_gray));
            }
            setVerificationData();
            if (tagDetailFirebaseModel.getOwnerId().equalsIgnoreCase(user.getUserId())) {
                if (tagDetailFirebaseModel.getPendingRequestCount() > 0) {
                    fragmentGroupDetailBinding.tvPendingRequests.setVisibility(View.VISIBLE);
                    fragmentGroupDetailBinding.tvPendingRequests.setText(getString(R.string.pending_requests));
                    fragmentGroupDetailBinding.tvPendingRequests.append(" ");
                    fragmentGroupDetailBinding.tvPendingRequests.append(AppUtils.getSpannableString(mActivity, "(" + tagDetailFirebaseModel.getPendingRequestCount() + ")", R.color.colorAccent, 1f, false, true, false, null));
                } else
                    fragmentGroupDetailBinding.tvPendingRequests.setVisibility(View.GONE);
            } else
                fragmentGroupDetailBinding.tvPendingRequests.setVisibility(View.GONE);
            fragmentGroupDetailBinding.switchPrivate.setOnCheckedChangeListener(this);
            fragmentGroupDetailBinding.switchMuteChat.setOnCheckedChangeListener(this);
            fragmentGroupDetailBinding.switchPinOnTop.setOnCheckedChangeListener(this);
        } else {
            Intent intent = new Intent();
            intent.putExtra(AppConstants.DEEP_INK_CONSTENT.TYPE, AppConstants.UNFOLLOW_REMOVE_ACTION.REMOVE);
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        }
    }

    /**
     * used to set the data regarding tag type or verification type
     */
    private void setVerificationData() {
        switch (myMembertype) {
            case AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_OWNER:
                fragmentGroupDetailBinding.llTagType.setVisibility(View.VISIBLE);
                fragmentGroupDetailBinding.btnDelete.setText(getString(R.string.delete_tag));
                if (tagDetailFirebaseModel.getTagType() == 1) {
                    fragmentGroupDetailBinding.tvVerificationType.setVisibility(View.VISIBLE);
                    fragmentGroupDetailBinding.cvVerificationType.setVisibility(View.VISIBLE);
                    fragmentGroupDetailBinding.llEmail.setVisibility(View.VISIBLE);
                    fragmentGroupDetailBinding.llPassword.setVisibility(View.VISIBLE);
                    fragmentGroupDetailBinding.llDocument.setVisibility(View.VISIBLE);
                    fragmentGroupDetailBinding.viewDividerEmail.setVisibility(View.VISIBLE);
                    fragmentGroupDetailBinding.viewDividerPassword.setVisibility(View.VISIBLE);
                    fragmentGroupDetailBinding.ibEmailCheck.setVisibility(View.VISIBLE);
                    fragmentGroupDetailBinding.ibPasswordCheck.setVisibility(View.VISIBLE);
                    fragmentGroupDetailBinding.ibCheckDocument.setVisibility(View.VISIBLE);
                    switch (tagDetailFirebaseModel.getVerificationType()) {
                        case AppConstants.TAG_VERIFICATION_METHOD.EMAIL:
                            fragmentGroupDetailBinding.ibEmailCheck.setImageResource(R.drawable.ic_radio_tick);
                            fragmentGroupDetailBinding.ibPasswordCheck.setImageResource(R.drawable.ic_radio_box);
                            fragmentGroupDetailBinding.ibCheckDocument.setImageResource(R.drawable.ic_radio_box);
                            fragmentGroupDetailBinding.tvPassword.setVisibility(View.GONE);
                            fragmentGroupDetailBinding.ibPasswordEdit.setVisibility(View.GONE);
                            fragmentGroupDetailBinding.tvEmail.setText(tagDetailFirebaseModel.getVerificationData());
                            fragmentGroupDetailBinding.tvEmail.setVisibility(View.VISIBLE);
                            fragmentGroupDetailBinding.ibEmailEdit.setVisibility(View.VISIBLE);
                            break;
                        case AppConstants.TAG_VERIFICATION_METHOD.PASSWORD:
                            fragmentGroupDetailBinding.ibPasswordCheck.setImageResource(R.drawable.ic_radio_tick);
                            fragmentGroupDetailBinding.ibEmailCheck.setImageResource(R.drawable.ic_radio_box);
                            fragmentGroupDetailBinding.ibCheckDocument.setImageResource(R.drawable.ic_radio_box);
                            fragmentGroupDetailBinding.tvEmail.setVisibility(View.GONE);
                            fragmentGroupDetailBinding.ibEmailEdit.setVisibility(View.GONE);
                            fragmentGroupDetailBinding.tvPassword.setText(tagDetailFirebaseModel.getVerificationData());
                            fragmentGroupDetailBinding.tvPassword.setVisibility(View.VISIBLE);
                            fragmentGroupDetailBinding.ibPasswordEdit.setVisibility(View.VISIBLE);
                            break;
                        case AppConstants.TAG_VERIFICATION_METHOD.DOCUMENT:
                            fragmentGroupDetailBinding.ibPasswordCheck.setImageResource(R.drawable.ic_radio_box);
                            fragmentGroupDetailBinding.ibEmailCheck.setImageResource(R.drawable.ic_radio_box);
                            fragmentGroupDetailBinding.tvEmail.setVisibility(View.GONE);
                            fragmentGroupDetailBinding.tvPassword.setVisibility(View.GONE);
                            fragmentGroupDetailBinding.ibEmailEdit.setVisibility(View.GONE);
                            fragmentGroupDetailBinding.ibPasswordEdit.setVisibility(View.GONE);
                            fragmentGroupDetailBinding.ibCheckDocument.setImageResource(R.drawable.ic_radio_tick);
                            break;
                    }
                } else {
                    fragmentGroupDetailBinding.tvVerificationType.setVisibility(View.GONE);
                    fragmentGroupDetailBinding.cvVerificationType.setVisibility(View.GONE);
                }
                fragmentGroupDetailBinding.switchPrivate.setClickable(true);
                break;
            case AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_ADMIN:
                fragmentGroupDetailBinding.llTagType.setVisibility(View.VISIBLE);
                fragmentGroupDetailBinding.btnDelete.setText(getString(R.string.exit_tag));
                fragmentGroupDetailBinding.switchPrivate.setClickable(false);
                fragmentGroupDetailBinding.llEmail.setVisibility(View.GONE);
                fragmentGroupDetailBinding.llPassword.setVisibility(View.GONE);
                fragmentGroupDetailBinding.llDocument.setVisibility(View.GONE);
                fragmentGroupDetailBinding.viewDividerEmail.setVisibility(View.GONE);
                fragmentGroupDetailBinding.viewDividerPassword.setVisibility(View.GONE);
                fragmentGroupDetailBinding.ibEmailCheck.setVisibility(View.GONE);
                fragmentGroupDetailBinding.ibCheckDocument.setVisibility(View.GONE);
                fragmentGroupDetailBinding.ibPasswordCheck.setVisibility(View.GONE);
                fragmentGroupDetailBinding.ibEmailEdit.setVisibility(View.GONE);
                fragmentGroupDetailBinding.ibPasswordEdit.setVisibility(View.GONE);
                if (tagDetailFirebaseModel.getTagType() == 1) {
                    fragmentGroupDetailBinding.tvVerificationType.setVisibility(View.VISIBLE);
                    fragmentGroupDetailBinding.cvVerificationType.setVisibility(View.VISIBLE);
                    switch (tagDetailFirebaseModel.getVerificationType()) {
                        case AppConstants.TAG_VERIFICATION_METHOD.EMAIL:
                            fragmentGroupDetailBinding.llEmail.setVisibility(View.VISIBLE);
                            fragmentGroupDetailBinding.ibEmailCheck.setImageResource(R.drawable.ic_radio_tick);
                            fragmentGroupDetailBinding.tvEmail.setText(tagDetailFirebaseModel.getVerificationData());
                            break;
                        case AppConstants.TAG_VERIFICATION_METHOD.PASSWORD:
                            fragmentGroupDetailBinding.llPassword.setVisibility(View.VISIBLE);
                            fragmentGroupDetailBinding.ibPasswordCheck.setImageResource(R.drawable.ic_radio_tick);
                            fragmentGroupDetailBinding.tvPassword.setText(tagDetailFirebaseModel.getVerificationData());
                            break;
                        case AppConstants.TAG_VERIFICATION_METHOD.DOCUMENT:
                            fragmentGroupDetailBinding.llDocument.setVisibility(View.VISIBLE);
                            fragmentGroupDetailBinding.ibCheckDocument.setImageResource(R.drawable.ic_radio_tick);
                            break;
                    }
                } else {
                    fragmentGroupDetailBinding.tvVerificationType.setVisibility(View.GONE);
                    fragmentGroupDetailBinding.cvVerificationType.setVisibility(View.GONE);
                }
                break;
            case AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_MEMBER:
                fragmentGroupDetailBinding.llTagType.setVisibility(View.GONE);
                fragmentGroupDetailBinding.tvVerificationType.setVisibility(View.GONE);
                fragmentGroupDetailBinding.cvVerificationType.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * used to update the group members when group data is fetched or updated
     */
    private void updateGroupMembers() {
        fragmentGroupDetailBinding.tvGroupMembers.setText(TextUtils.concat(getString(R.string.group_members) + " (" + tagDetailFirebaseModel.getMembers().size() + ")"));
        membersHashmap.clear();
        membersHashmap.putAll(tagDetailFirebaseModel.getMembers());
        membersHashmap.updateIndexes();
        groupMembersAdapter.notifyDataSetChanged();
        //groupMembersAdapter.getFilter().filter(fragmentGroupDetailBinding.etSearchGroupMember.getText().toString().trim());
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.switch_pin_on_top:
                if (AppUtils.isConnection(mActivity)) {
                    chatModel.setPinned(b);
                    groupDetailViewModel.pinnedChat(b, user.getUserId(), chatModel.getRoomId());
                }
                break;
            case R.id.switch_mute_chat:
                if (AppUtils.isConnection(mActivity)) {
                    chatModel.setChatMute(b);
                    groupDetailViewModel.muteUnmuteChat(b, user.getUserId(), chatModel.getRoomId());
                }
                break;
            case R.id.switch_private:
                if (b) {
                    fragmentGroupDetailBinding.switchPrivate.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_light_gray));
                    fragmentGroupDetailBinding.tvPrivate.setTextColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                } else {
                    fragmentGroupDetailBinding.switchPrivate.setTextColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                    fragmentGroupDetailBinding.tvPrivate.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_light_gray));
                }
                if (AppUtils.isConnection(mActivity)) {
                    HashMap<String, Object> objectHashMap = new HashMap<>();
                    objectHashMap.put(AppConstants.KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                    objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.JOIN_TAG_BY, AppConstants.TAG_VERIFICATION_METHOD.DOCUMENT);
                    objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.TYPE, b ? 1 : 2);
                    getLoadingStateObserver().onChanged(true);
                    groupDetailViewModel.editTag(objectHashMap, AppConstants.TAG_KEY_CONSTENT.TYPE);
                } else
                    showToastShort(getString(R.string.no_internet));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                ((Activity) mActivity).onBackPressed();
                break;
            case R.id.ib_alert:
                AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel), getString(R.string.report), getString(R.string.report_tag) + "!", getString(R.string.are_you_sure_you_want_to_report) + " " + tagDetailFirebaseModel.getTagName() + " " + getString(R.string.tag) + "?", new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {
                        if (AppUtils.isConnection(mActivity)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                            // hashMap.put(AppConstants.TAG_KEY_CONSTENT.REASON, "");
                            getLoadingStateObserver().onChanged(true);
                            groupDetailViewModel.reportTag(hashMap);
                        } else
                            showToastShort(getString(R.string.no_internet));
                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });
                break;
            case R.id.ib_share:
                ((BaseActivity) Objects.requireNonNull(getActivity())).performShareAction(tagDetailFirebaseModel.getShareLink());
                break;
            case R.id.ib_email_check:
            case R.id.ll_email:
                if (tagDetailFirebaseModel.getOwnerId().equalsIgnoreCase(user.getUserId()) && tagDetailFirebaseModel.getVerificationType() != AppConstants.TAG_VERIFICATION_METHOD.EMAIL) {
                    DialogUtil.getInstance().CustomBottomSheetDialogForVerificationType(mActivity, AppConstants.TAG_VERIFICATION_METHOD.EMAIL, tagDetailFirebaseModel.getVerificationData(), new OnDialogItemObjectClickListener() {
                        @Override
                        public void onPositiveBtnClick(Object o) {
                            if (AppUtils.isConnection(mActivity)) {
                                HashMap<String, Object> objectHashMap = new HashMap<>();
                                objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.JOIN_TAG_BY, AppConstants.TAG_VERIFICATION_METHOD.EMAIL);
                                objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.EMAIL, (String) o);
                                objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                                getLoadingStateObserver().onChanged(true);
                                groupDetailViewModel.editTag(objectHashMap, AppConstants.TAG_KEY_CONSTENT.EMAIL);
                            } else
                                showToastShort(getString(R.string.no_internet));
                        }
                    });
                }
                break;
            case R.id.ib_password_check:
            case R.id.ll_password:
                if (tagDetailFirebaseModel.getOwnerId().equalsIgnoreCase(user.getUserId()) && tagDetailFirebaseModel.getVerificationType() != AppConstants.TAG_VERIFICATION_METHOD.PASSWORD) {
                    DialogUtil.getInstance().CustomBottomSheetDialogForVerificationType(mActivity, AppConstants.TAG_VERIFICATION_METHOD.PASSWORD, tagDetailFirebaseModel.getVerificationData(), new OnDialogItemObjectClickListener() {
                        @Override
                        public void onPositiveBtnClick(Object o) {
                            if (AppUtils.isConnection(mActivity)) {
                                HashMap<String, Object> objectHashMap = new HashMap<>();
                                objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.JOIN_TAG_BY, AppConstants.TAG_VERIFICATION_METHOD.PASSWORD);
                                objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.PASSWORD, (String) o);
                                objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                                getLoadingStateObserver().onChanged(true);
                                groupDetailViewModel.editTag(objectHashMap, AppConstants.TAG_KEY_CONSTENT.PASSWORD);
                            } else
                                showToastShort(getString(R.string.no_internet));
                        }
                    });
                }
                break;
            case R.id.ll_document:
            case R.id.ib_check_document:
                if (tagDetailFirebaseModel.getOwnerId().equalsIgnoreCase(user.getUserId()) && tagDetailFirebaseModel.getVerificationType() != AppConstants.TAG_VERIFICATION_METHOD.DOCUMENT) {
                    if (AppUtils.isConnection(mActivity)) {
                        HashMap<String, Object> objectHashMap = new HashMap<>();
                        objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.JOIN_TAG_BY, AppConstants.TAG_VERIFICATION_METHOD.DOCUMENT);
                        objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.DOCUMENT_TYPE, "");
                        objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                        getLoadingStateObserver().onChanged(true);
                        groupDetailViewModel.editTag(objectHashMap, AppConstants.TAG_KEY_CONSTENT.DOCUMENT_TYPE);
                    } else
                        showToastShort(getString(R.string.no_internet));
                }
                break;
            case R.id.ib_email_edit:
                DialogUtil.getInstance().CustomBottomSheetDialogForVerificationType(mActivity, AppConstants.TAG_VERIFICATION_METHOD.EMAIL, tagDetailFirebaseModel.getVerificationData(), new OnDialogItemObjectClickListener() {
                    @Override
                    public void onPositiveBtnClick(Object o) {
                        if (AppUtils.isConnection(mActivity)) {
                            HashMap<String, Object> objectHashMap = new HashMap<>();
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.JOIN_TAG_BY, AppConstants.TAG_VERIFICATION_METHOD.EMAIL);
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.EMAIL, (String) o);
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                            getLoadingStateObserver().onChanged(true);
                            groupDetailViewModel.editTag(objectHashMap, AppConstants.TAG_KEY_CONSTENT.EMAIL);
                        } else
                            showToastShort(getString(R.string.no_internet));
                    }
                });
                break;
            case R.id.ib_password_edit:
                DialogUtil.getInstance().CustomBottomSheetDialogForVerificationType(mActivity, AppConstants.TAG_VERIFICATION_METHOD.PASSWORD, tagDetailFirebaseModel.getVerificationData(), new OnDialogItemObjectClickListener() {
                    @Override
                    public void onPositiveBtnClick(Object o) {
                        if (AppUtils.isConnection(mActivity)) {
                            HashMap<String, Object> objectHashMap = new HashMap<>();
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.JOIN_TAG_BY, AppConstants.TAG_VERIFICATION_METHOD.PASSWORD);
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.PASSWORD, (String) o);
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                            getLoadingStateObserver().onChanged(true);
                            groupDetailViewModel.editTag(objectHashMap, AppConstants.TAG_KEY_CONSTENT.PASSWORD);
                        } else
                            showToastShort(getString(R.string.no_internet));
                    }
                });
                break;
            case R.id.tv_location:
                if (tagDetailFirebaseModel.getOwnerId().equalsIgnoreCase(user.getUserId())) {
                    final Intent in = new Intent(mActivity, ChoosseLocationActivity.class);
                    startActivityForResult(in, AppConstants.ACTIVITY_RESULT.SEACH_LOACTION);
                }
                break;
            case R.id.btn_delete:
                if (AppUtils.isConnection(mActivity)) {
                    final boolean isDelete = myMembertype == AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_OWNER;
                    if (isDelete) {
                        DialogUtil.getInstance().CustomBottomSheetDialogForDeleteGroup(mActivity, getString(R.string.are_you_sure_you_want_to_delete) + " " + tagDetailFirebaseModel.getTagName() + " " + getString(R.string.tag) + "?", new OnDialogItemClickListener() {
                            @Override
                            public void onPositiveBtnClick() {
                                if (AppUtils.isConnection(mActivity)) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                                    getLoadingStateObserver().onChanged(true);
                                    groupDetailViewModel.deleteTagApi(hashMap, MEMBER_ACTION_DELETE_TAG);
                                } else
                                    showToastShort(getString(R.string.no_internet));
                            }

                            @Override
                            public void onNegativeBtnClick() {

                            }
                        });
                    } else {
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel), getString(R.string.exit), getString(R.string.exit_tag), getString(R.string.are_you_sure_you_want_to_exit) + " " + tagDetailFirebaseModel.getTagName() + " " + getString(R.string.tag) + "?", new OnDialogItemClickListener() {
                            @Override
                            public void onPositiveBtnClick() {
                                if (AppUtils.isConnection(mActivity)) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                                    getLoadingStateObserver().onChanged(true);
                                    groupDetailViewModel.exitTagApi(hashMap, MEMBER_ACTION_EXIT_TAG);
                                } else
                                    showToastShort(getString(R.string.no_internet));
                            }

                            @Override
                            public void onNegativeBtnClick() {

                            }
                        });
                    }
                }
                break;
            case R.id.ib_edit_tag_image:
                if (PermissionUtility.isPermissionGranted(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, AppConstants.ACTIVITY_RESULT.CAMERA_PERMISSION)) {
                    Intent intent = new Intent(getActivity(), CameraTwoActivity.class);
                    intent.putExtra(AppConstants.CAMERA_CONSTANTS.IMAGE_LIMIT_ONESHOT, 1);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.CAMERA_ACTIVITY);
                }
                break;
            case R.id.ib_announcement_edit:
                DialogUtil.getInstance().CustomBottomSheetDialogForVerificationType(mActivity, 4, tagDetailFirebaseModel.getAnnouncement(), new OnDialogItemObjectClickListener() {
                    @Override
                    public void onPositiveBtnClick(Object o) {
                        if (AppUtils.isConnection(mActivity)) {
                            HashMap<String, Object> objectHashMap = new HashMap<>();
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.ANNOUNCEMENT, (String) o);
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                            getLoadingStateObserver().onChanged(true);
                            groupDetailViewModel.editTag(objectHashMap, AppConstants.TAG_KEY_CONSTENT.ANNOUNCEMENT);
                        } else
                            showToastShort(getString(R.string.no_internet));
                    }
                });
                break;
            case R.id.ib_description_edit:
                DialogUtil.getInstance().CustomBottomSheetDialogForVerificationType(mActivity, 5, tagDetailFirebaseModel.getDescription(), new OnDialogItemObjectClickListener() {
                    @Override
                    public void onPositiveBtnClick(Object o) {
                        if (AppUtils.isConnection(mActivity)) {
                            HashMap<String, Object> objectHashMap = new HashMap<>();
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.DESCRIPTION, (String) o);
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                            getLoadingStateObserver().onChanged(true);
                            groupDetailViewModel.editTag(objectHashMap, AppConstants.TAG_KEY_CONSTENT.DESCRIPTION);
                        } else
                            showToastShort(getString(R.string.no_internet));
                    }
                });
                break;
            case R.id.tv_tag_name:
                DialogUtil.getInstance().CustomBottomSheetDialogForVerificationType(mActivity, 0, tagDetailFirebaseModel.getTagName(), new OnDialogItemObjectClickListener() {
                    @Override
                    public void onPositiveBtnClick(Object o) {
                        if (AppUtils.isConnection(mActivity)) {
                            HashMap<String, Object> objectHashMap = new HashMap<>();
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.NAME, (String) o);
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                            getLoadingStateObserver().onChanged(true);
                            groupDetailViewModel.editTag(objectHashMap, AppConstants.TAG_KEY_CONSTENT.NAME);
                        } else
                            showToastShort(getString(R.string.no_internet));
                    }
                });
                break;
            case R.id.tv_pending_requests:
                Intent intent = new Intent(mActivity, PendingRequestsActivity.class);
                intent.putExtra(AppConstants.TAG_KEY_CONSTENT.NAME, tagDetailFirebaseModel.getTagName());
                intent.putExtra(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                intent.putExtra(AppConstants.TAG_KEY_CONSTENT.IMAGE_URL, tagDetailFirebaseModel.getTagImageUrl());
                startActivity(intent);
                break;
            case R.id.et_search_group_member:
                if (membersHashmap != null && membersHashmap.size() > 0) {
                    Intent intent1 = new Intent(mActivity, GroupMembersActivity.class);
                    intent1.putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel);
                    intent1.putExtra(AppConstants.BUNDLE_DATA, tagDetailFirebaseModel);
                    startActivity(intent1);
                }
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.SEACH_LOACTION:
                if (data != null) {
                    String locAdd = data.getExtras().getString("Location");
                    Address location = AppUtils.getLocationFromAddress(mActivity, locAdd);
                    if (AppUtils.isConnection(mActivity)) {
                        if (location != null) {
                            HashMap<String, Object> objectHashMap = new HashMap<>();
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.ADDRESS, location.getAddressLine(0));
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.CITY, location.getAdminArea());
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.LAT, location.getLatitude());
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.LONG, location.getLongitude());
                            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
                            getLoadingStateObserver().onChanged(true);
                            groupDetailViewModel.editTag(objectHashMap, AppConstants.TAG_KEY_CONSTENT.ADDRESS);
                        }
                    } else
                        showToastShort(getString(R.string.no_internet));
                }
                break;

            case AppConstants.REQUEST_CODE.CAMERA_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> images = data.getExtras().getStringArrayList("images");
                    if (images != null && images.size() > 0) {
                        if (AppUtils.isConnection(mActivity)) {
                            fragmentGroupDetailBinding.ivTag.setImageURI(Uri.parse(images.get(0)));
                            fragmentGroupDetailBinding.ibEditTagImage.setVisibility(View.GONE);
                            fragmentGroupDetailBinding.viewOverlay.setVisibility(View.VISIBLE);
                            fragmentGroupDetailBinding.progressBarTagImage.setVisibility(View.VISIBLE);
                            startUpload(images.get(0), "1");
                        } else
                            showToastShort(getString(R.string.no_internet));
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getActivity(), CameraTwoActivity.class);
                    intent.putExtra(AppConstants.CAMERA_CONSTANTS.IMAGE_LIMIT_ONESHOT, 1);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.CAMERA_ACTIVITY);
                }
                break;
        }
    }

    /**
     * used to start uploading image on amazon
     *
     * @param path image path used to upload
     * @param id   self used image id
     */
    private void startUpload(String path, String id) {
        getLoadingStateObserver().onChanged(false);
        ImageBean bean = addDataInBean(path, id);
        mAmazonS3.uploadImage(bean);
    }

    /**
     * used to get the image uplaoding bean
     *
     * @param path image path used to upload
     * @param id   self used image id
     */
    private ImageBean addDataInBean(String path, String id) {
        ImageBean bean = new ImageBean();
        bean.setId(id);
        bean.setName("sample");
        bean.setImagePath(path);
        return bean;
    }

    @Override
    public void uploadSuccess(ImageBean bean) {
        fragmentGroupDetailBinding.progressBarTagImage.setVisibility(View.GONE);
        fragmentGroupDetailBinding.viewOverlay.setVisibility(View.GONE);
        fragmentGroupDetailBinding.ibEditTagImage.setVisibility(View.VISIBLE);
        if (AppUtils.isConnection(mActivity)) {
            HashMap<String, Object> objectHashMap = new HashMap<>();
            HashMap<String, String> imageParams = new HashMap<>();
            imageParams.put(AppConstants.TAG_KEY_CONSTENT.URL, bean.getServerUrl());
            imageParams.put(AppConstants.TAG_KEY_CONSTENT.THUMB_URL, bean.getServerUrl());
            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.IMAGE_URL, new JSONObject(imageParams));
            objectHashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagDetailFirebaseModel.getTagId());
            getLoadingStateObserver().onChanged(true);
            groupDetailViewModel.editTag(objectHashMap, AppConstants.TAG_KEY_CONSTENT.IMAGE_URL);
        } else {
            AppUtils.loadCircularImage(mActivity, tagDetailFirebaseModel.getTagImageUrl(), 300, R.drawable.ic_home_placeholder, fragmentGroupDetailBinding.ivTag, true);
            showToastShort(getString(R.string.no_internet));
        }
    }

    @Override
    public void uploadFailed(ImageBean bean) {
        imageUploadFailedOnAmazon();
    }

    @Override
    public void uploadProgress(ImageBean bean) {

    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {
        imageUploadFailedOnAmazon();
    }

    /**
     * used to update the UI when image uploading failed on amazon
     */
    private void imageUploadFailedOnAmazon() {
        fragmentGroupDetailBinding.progressBarTagImage.setVisibility(View.GONE);
        fragmentGroupDetailBinding.viewOverlay.setVisibility(View.GONE);
        fragmentGroupDetailBinding.ibEditTagImage.setVisibility(View.VISIBLE);
        AppUtils.loadCircularImage(mActivity, tagDetailFirebaseModel.getTagImageUrl(), 300, R.drawable.ic_home_placeholder, fragmentGroupDetailBinding.ivTag, true);
        showToastShort(getString(R.string.uploading_failed));
    }

    /**
     * used to show the popup window for the group member
     *
     * @param memberModel member model clicked
     * @param view        anchor view for popup window
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
}
