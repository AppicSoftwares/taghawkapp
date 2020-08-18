package com.taghawk.ui.chat;

import android.Manifest;
import android.app.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.adapters.ChatProductListAdapter;
import com.taghawk.adapters.MessagesDetailListAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.camera2basic.CameraTwoActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_view.PositionedLinkedHashmap;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.FragmentMessagesDetailGroupChatBinding;
import com.taghawk.databinding.LayoutGroupChatMessagePopupWindowBinding;
import com.taghawk.firebase.FirebaseChildEventListener;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.RemoveFirebaseListenerModel;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.MemberModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.chat.TagDetailFirebaseModel;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.model.request.User;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.home.search.SearchTagShelfAcivity;
import com.taghawk.ui.profile.OtherProfileActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.PermissionUtility;
import com.taghawk.util.TimeAgo;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

public class MessagesDetailGroupChatFragment extends BaseFragment implements View.OnClickListener, AmazonCallback {

    /**
     * A {@link HomeViewModel} object to handle all the actions and business logic
     */
    public static final int GROUP_DETAIL_RESULT_CODE = 209;
    private MessagesDetailViewModel messagesDetailViewModel;
    private FragmentMessagesDetailGroupChatBinding fragmentMessagesDetailBinding;
    private Activity mActivity;
    private MessagesDetailListAdapter messagesDetailListAdapter;
    private PositionedLinkedHashmap<String, MessageModel> messagesHashmap;
    private ChatModel currentChatModel;
    private User user;
    private FirebaseChildEventListener newMessageAddedListener;
    private String roomId;
    private AmazonS3 mAmazonS3;
    private long endIndexTimeStamp, createdTimeStamp;
    private Query messagesQuery, newMessageQuery;
    private boolean hasMoreData = true, isLoading;
    private ArrayList<ProductListModel> productList;
    private ChatProductListAdapter chatProductListAdapter;
    private Query roomNodeQuery, tagAnnouncementQuery;
    private ValueEventListener roomNodeListener, announcementValueEventListener;
    private TagDetailFirebaseModel tagDetailFirebaseModel;
    private PopupWindow popup;
    private long lastMessageTimeStamp = 0;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentMessagesDetailBinding = FragmentMessagesDetailGroupChatBinding.inflate(inflater, container, false);
        initView();
        setMorePopupWindow();
        setMessagesData();
        return fragmentMessagesDetailBinding.getRoot();
    }

    /**
     * intialize views and variables
     */
    private void initView() {
        mActivity = getActivity();
        mAmazonS3 = AmazonS3.getInstance(mActivity, this, AppConstants.AMAZON_S3.AMAZON_POOLID, AppConstants.AMAZON_S3.BUCKET, AppConstants.AMAZON_S3.AMAZON_SERVER_URL, AppConstants.AMAZON_S3.END_POINT);
        productList = new ArrayList<>();
        messagesHashmap = new PositionedLinkedHashmap<>();
        user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
        currentChatModel = getArguments().getParcelable(AppConstants.FIREBASE.FIREBASE_CHAT_DATA);
        createdTimeStamp = getArguments().getLong(AppConstants.FIREBASE.TIMESTAMP);
        endIndexTimeStamp = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
        roomId = currentChatModel.getRoomId();
        fragmentMessagesDetailBinding.tvToolbarTitle.setText(currentChatModel.getRoomName());
        fragmentMessagesDetailBinding.rvMessages.setLayoutManager(new LinearLayoutManager(mActivity));
        ((SimpleItemAnimator) fragmentMessagesDetailBinding.rvMessages.getItemAnimator()).setSupportsChangeAnimations(false);
        if (currentChatModel.isBlocked() || currentChatModel.getMute())
            fragmentMessagesDetailBinding.llMessageSend.setVisibility(View.GONE);
        else
            fragmentMessagesDetailBinding.llMessageSend.setVisibility(View.VISIBLE);
        messagesDetailListAdapter = new MessagesDetailListAdapter(currentChatModel.getChatType(), currentChatModel.getRoomName(), messagesHashmap, user.getUserId(), new RecyclerViewCallback() {
            @Override
            public void onClick(int position, View view) {
                final MessageModel messageModel = messagesHashmap.get(messagesHashmap.getKeyValue(position));
                AppUtils.hideKeyboard(mActivity);
                switch (view.getId()) {
                    case R.id.ll_main:
                    case R.id.ib_retry:
                        break;
                    case R.id.iv_user:
                        Intent intent = new Intent(mActivity, OtherProfileActivity.class);
                        intent.putExtra(AppConstants.BUNDLE_DATA, messageModel.getSenderId());
                        mActivity.startActivity(intent);
                        break;
                    case R.id.iv_media:
                        switch (messageModel.getMessageType()) {
                            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_PRODUCT:
                                break;
                            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_COMMUNITY:
                                break;
                            default:
                                if (messageModel.getLoadingImageOnAmazon() == null)
                                    AppUtils.openFullViewImage(mActivity, messageModel.getMessageText());
                                break;
                        }
                        break;
                }
            }
        });
        fragmentMessagesDetailBinding.rvMessages.setAdapter(messagesDetailListAdapter);
        fragmentMessagesDetailBinding.rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) fragmentMessagesDetailBinding.rvMessages.getLayoutManager();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (hasMoreData && !isLoading && firstVisibleItemPosition == 2) {
                    isLoading = true;
                    checkMessages(false);
                }
            }
        });
        fragmentMessagesDetailBinding.ibSend.setOnClickListener(this);
        fragmentMessagesDetailBinding.ibMore.setOnClickListener(this);
        fragmentMessagesDetailBinding.tvToolbarTitle.setOnClickListener(this);
        fragmentMessagesDetailBinding.ibAttachment.setOnClickListener(this);
        fragmentMessagesDetailBinding.ibBack.setOnClickListener(this);
        fragmentMessagesDetailBinding.tvShelf.setOnClickListener(this);
        fragmentMessagesDetailBinding.llMain.setOnClickListener(this);
        fragmentMessagesDetailBinding.rvProducts.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        chatProductListAdapter = new ChatProductListAdapter(productList, new RecyclerViewCallback() {
            @Override
            public void onClick(int position, View view) {
                AppUtils.hideKeyboard(mActivity);
                Intent intent = new Intent(mActivity, SearchTagShelfAcivity.class);
                intent.putExtra(AppConstants.BUNDLE_DATA, currentChatModel.getRoomId());
                intent.putExtra("IS_FROM", 3);
                intent.putExtra(AppConstants.TAG_KEY_CONSTENT.NAME, currentChatModel.getRoomName());
                intent.putExtra(AppConstants.KEY_CONSTENT.TYPE, AppConstants.ACTIVITY_RESULT.VIEW_PRODUCT);
                mActivity.startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.TAG_DETAILS);

            }
        });
        fragmentMessagesDetailBinding.rvProducts.setAdapter(chatProductListAdapter);
        KeyboardVisibilityEvent.setEventListener(
                mActivity,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen && (((LinearLayoutManager) fragmentMessagesDetailBinding.rvMessages.getLayoutManager()).findLastVisibleItemPosition() >= messagesHashmap.size() - 4))
                            fragmentMessagesDetailBinding.rvMessages.scrollToPosition(messagesHashmap.size() - 1);
                    }
                });
    }

    /**
     * used to set up the data for the popup window to show on more button click
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setMorePopupWindow() {
        final LayoutGroupChatMessagePopupWindowBinding popBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_group_chat_message_popup_window, null, false);
        popup = new PopupWindow(mActivity);
        popup.setContentView(popBinding.getRoot());
        popup.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        popup.setElevation(2f);
        popup.setOverlapAnchor(true);
        if (currentChatModel.isChatMute())
            popBinding.switchMuteChat.setChecked(true);
        else
            popBinding.switchMuteChat.setChecked(false);
        popBinding.switchPinOnTop.setChecked(currentChatModel.isPinned());
        popBinding.switchPinOnTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (AppUtils.isConnection(mActivity)) {
                    currentChatModel.setPinned(b);
                    messagesDetailViewModel.pinnedChat(b, user.getUserId(), roomId);
                }
            }
        });
        popBinding.switchMuteChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (AppUtils.isConnection(mActivity)) {
                    currentChatModel.setChatMute(b);
                    messagesDetailViewModel.muteUnmuteChat(b, user.getUserId(), roomId);
                }
            }
        });
        popBinding.tvViewTag.setOnClickListener(this);
    }


    /**
     * used to set up all the messages data, observers, live data etc
     */
    private void setMessagesData() {
        //initializing view model
        messagesDetailViewModel = ViewModelProviders.of(this).get(MessagesDetailViewModel.class);
        messagesDetailViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        if (AppUtils.isConnection(mActivity))
            messagesDetailViewModel.getTagProducts(currentChatModel.getRoomId());
        messagesDetailViewModel.getmTagProductsLiveData().observe(this, new Observer<ProductListingModel>() {
            @Override
            public void onChanged(@Nullable ProductListingModel profileProductsResponse) {
                if (profileProductsResponse != null) {
                    int position = -1;
                    for (ProductListModel productListModel : profileProductsResponse.getmProductList()) {
                        position += 1;
                        if (position == 4) {
                            if (profileProductsResponse.getTotalItems() > 5) {
                                productListModel.setMoreProductCount(profileProductsResponse.getTotalItems());
                                productListModel.setViewType(ProductDetailsData.VIEW_TYPE_PRODUCT_COUNT);
                            }
                            productList.add(productListModel);
                            break;
                        } else
                            productList.add(productListModel);
                    }
                    chatProductListAdapter.notifyDataSetChanged();
                }
            }
        });
        getNewlyAddedMessage();
        checkMessages(true);
        checkRoomData();
        checkTagAnnouncement();
    }

    /**
     * used to check the latest announcement of the tag
     */
    private void checkTagAnnouncement() {
        tagAnnouncementQuery = messagesDetailViewModel.getGroupDetailQuery(currentChatModel.getRoomId()).getRef().child(AppConstants.TAG_KEY_CONSTENT.ANNOUNCEMENT);
        announcementValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String announcement = (String) dataSnapshot.getValue();
                    fragmentMessagesDetailBinding.tvAnnouncement.setText(announcement);
                    fragmentMessagesDetailBinding.tvAnnouncement.setSelected(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        tagAnnouncementQuery.addValueEventListener(announcementValueEventListener);
    }

    /**
     * Used to check the updated room data
     */
    private void checkRoomData() {
        roomNodeQuery = messagesDetailViewModel.getUserChatsQuery(user.getUserId()).child(currentChatModel.getRoomId());
        roomNodeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getValue() != null) {
                        if (currentChatModel != null) {
                            ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                            currentChatModel.setRoomImage(chatModel.getRoomImage());
                            currentChatModel.setRoomName(chatModel.getRoomName());
                            fragmentMessagesDetailBinding.tvToolbarTitle.setText(currentChatModel.getRoomName());
                            currentChatModel.setMute(chatModel.getMute());
                            currentChatModel.setBlocked(chatModel.isBlocked());
                            TransitionManager.beginDelayedTransition(fragmentMessagesDetailBinding.llMain, new ChangeBounds());
                            if (currentChatModel.isBlocked() || currentChatModel.getMute()) {
                                fragmentMessagesDetailBinding.llMessageSend.setVisibility(View.GONE);
                                fragmentMessagesDetailBinding.tvAlert.setVisibility(View.VISIBLE);
                            } else {
                                fragmentMessagesDetailBinding.tvAlert.setVisibility(View.GONE);
                                fragmentMessagesDetailBinding.llMessageSend.setVisibility(View.VISIBLE);
                            }
                            messagesDetailViewModel.getGroupDetailQuery(currentChatModel.getRoomId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        tagDetailFirebaseModel = dataSnapshot.getValue(TagDetailFirebaseModel.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    } else {
                        mActivity.setResult(Activity.RESULT_OK);
                        mActivity.finish();
                    }
                } else {
                    mActivity.setResult(Activity.RESULT_OK);
                    mActivity.finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        roomNodeQuery.addValueEventListener(roomNodeListener);
    }

    /**
     * used to get new messages from the firebase
     */
    private void getNewlyAddedMessage() {
        newMessageQuery = messagesDetailViewModel.getNewMessageQuery(roomId, endIndexTimeStamp);
        newMessageAddedListener = new FirebaseChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    messagesDetailViewModel.updateUnreadCount(user.getUserId(), roomId);
                    noData(View.VISIBLE, "", "");
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    if (messageModel != null) {
                        lastMessageTimeStamp = messageModel.getTimeStampLong();
                        if (messagesHashmap.containsKey(messageModel.getMessageId())) {
                            MessageModel oldMessage = messagesHashmap.get(messageModel.getMessageId());
                            if (oldMessage != null) {
                                oldMessage.setLoadingImageOnAmazon(null);
                                oldMessage.setTimeStamp(messageModel.getTimeStamp());
                                oldMessage.setMessageStatus(messageModel.getMessageStatus());
                                messagesDetailListAdapter.notifyItemChanged(messagesHashmap.getKeyIndex(messageModel.getMessageId()));
                            }
                        } else {
                            messagesHashmap.addIndex(messageModel.getMessageId());
                            messagesHashmap.put(messageModel.getMessageId(), messageModel);
                            messagesDetailListAdapter.notifyItemInserted(messagesHashmap.size() - 1);
                            if (messageModel.getSenderId().equalsIgnoreCase(user.getUserId()) || ((LinearLayoutManager) fragmentMessagesDetailBinding.rvMessages.getLayoutManager()).findLastVisibleItemPosition() >= messagesHashmap.size() - 3)
                                fragmentMessagesDetailBinding.rvMessages.smoothScrollToPosition(messagesHashmap.size() - 1);
                            if (!messageModel.getSenderId().equalsIgnoreCase(user.getUserId()))
                                messagesDetailViewModel.updateMessageStatus(messageModel.getMessageId(), roomId);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    if (messageModel.getSenderId().equalsIgnoreCase(user.getUserId()))
                        updateMessage(messageModel);
                }
            }
        };
        newMessageQuery.addChildEventListener(newMessageAddedListener);
    }

    /**
     * used to check the messages from the firebase node with pagination
     *
     * @param isScrollToBottom whether you want to scroll the list to bottom or not
     */
    private void checkMessages(final boolean isScrollToBottom) {
        messagesQuery = messagesDetailViewModel.getRoomMessagesQuery(roomId, endIndexTimeStamp, createdTimeStamp - 1);
        messagesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    if (!dataSnapshot.exists() && messagesHashmap.size() == 0) {
                        noData(View.GONE, getString(R.string.oops_it_s_empty), getString(R.string.send_a_message_to_start_chatting));
                    } else {
                        messagesDetailViewModel.updateUnreadCount(user.getUserId(), roomId);
                        int position = -1;
                        PositionedLinkedHashmap<String, MessageModel> positionedLinkedHashmap = new PositionedLinkedHashmap<>();
                        for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                            position += 1;
                            MessageModel messageModel = dataSnapshots.getValue(MessageModel.class);
                            positionedLinkedHashmap.put(messageModel.getMessageId(), messageModel);
                            messagesHashmap.addIndexOnPosition(messageModel.getMessageId(), position);
                            if (!messageModel.getSenderId().equalsIgnoreCase(user.getUserId()) && messageModel.getMessageStatus().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_DELIVERED))
                                messagesDetailViewModel.updateMessageStatus(messageModel.getMessageId(), roomId);
                        }
                        int size = positionedLinkedHashmap.size();
                        positionedLinkedHashmap.putAll(messagesHashmap);
                        messagesHashmap.clear();
                        messagesHashmap.putAll(positionedLinkedHashmap);
                        if (messagesHashmap.size() > size)
                            messagesDetailListAdapter.notifyItemRangeInserted(0, size);
                        else
                            messagesDetailListAdapter.notifyDataSetChanged();
                        if (messagesHashmap.size() > 0) {
                            endIndexTimeStamp = messagesHashmap.get(messagesHashmap.getKeyValue(0)).getTimeStampLong();
                            lastMessageTimeStamp = messagesHashmap.get(messagesHashmap.getKeyValue(messagesHashmap.size() - 1)).getTimeStampLong();
                        }
                        if (position < 99)
                            hasMoreData = false;
                        if (isScrollToBottom)
                            fragmentMessagesDetailBinding.rvMessages.scrollToPosition(messagesHashmap.size() - 1);
                        isLoading = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * used to update the message status in the list
     *
     * @param messageModel updated message model
     */
    private void updateMessage(MessageModel messageModel) {
        MessageModel oldMessage = messagesHashmap.get(messageModel.getMessageId());
        if (oldMessage != null) {
            oldMessage.setMessageStatus(messageModel.getMessageStatus());
            oldMessage.setReadCount(messageModel.getReadCount());
            oldMessage.setMemberCount(messageModel.getMemberCount());
            messagesDetailListAdapter.notifyItemChanged(messagesHashmap.getKeyIndex(messageModel.getMessageId()));
        }
    }

    /**
     * used to show the no data layout if there is no message yet
     *
     * @param VISIBILITY visibility of the messages data
     * @param errorTitle title of the no data layout
     * @param errorMsg   description of the no data layout
     */
    private void noData(int VISIBILITY, String errorTitle, String errorMsg) {
        fragmentMessagesDetailBinding.includeHeaderEmpty.tvTitle.setText(errorTitle);
        fragmentMessagesDetailBinding.includeHeaderEmpty.tvEmptyMsg.setText(errorMsg);
        fragmentMessagesDetailBinding.tvNoData.setVisibility(VISIBILITY == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        super.onErrorOccurred(throwable);
        getLoadingStateObserver().onChanged(false);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.REQUEST_CODE.CAMERA_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> images = data.getExtras().getStringArrayList("images");
                    if (images != null && images.size() > 0) {
                        MessageModel messageModel = new MessageModel();
                        messageModel.setTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                        messageModel.setSenderId(user.getUserId());
                        messageModel.setMessageType(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE);
                        messageModel.setMessageStatus(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_PENDING);
                        messageModel.setMessageText(images.get(0));
                        messageModel.setMessageId(messagesQuery.getRef().push().getKey());
                        messageModel.setLoadingImageOnAmazon(ImageBean.IMAGE_UPLOAD_LOADING);
                        messageModel.setRoomId(roomId);
                        messagesHashmap.put(messageModel.getMessageId(), messageModel);
                        messagesHashmap.addIndex(messageModel.getMessageId());
                        messagesDetailListAdapter.notifyItemInserted(messagesHashmap.size() - 1);
                        fragmentMessagesDetailBinding.rvMessages.smoothScrollToPosition(messagesHashmap.size() - 1);
                        startUpload(images.get(0), messageModel.getMessageId());
                    }
                }
                break;
            case GROUP_DETAIL_RESULT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getIntExtra(AppConstants.DEEP_INK_CONSTENT.TYPE, 0) == AppConstants.UNFOLLOW_REMOVE_ACTION.REMOVE) {
                        newMessageQuery.removeEventListener(newMessageAddedListener);
                        roomNodeQuery.removeEventListener(roomNodeListener);
                        tagAnnouncementQuery.removeEventListener(announcementValueEventListener);
                        mActivity.setResult(Activity.RESULT_OK);
                        mActivity.finish();
                    } else
                        currentChatModel = data.getParcelableExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA);
                }
                break;
            case AppConstants.ACTIVITY_RESULT.TAG_DETAILS:
                if (resultCode == Activity.RESULT_OK) {
                    mActivity.setResult(Activity.RESULT_OK);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                ((Activity) mActivity).onBackPressed();
                break;
            case R.id.ll_main:
                AppUtils.hideKeyboard(mActivity);
                break;
            case R.id.ib_more:
                AppUtils.hideKeyboard(mActivity);
                if (popup != null) {
                    if (popup.isShowing())
                        popup.dismiss();
                    else
                        popup.showAsDropDown(fragmentMessagesDetailBinding.ibMore);
                }
                break;
            case R.id.ib_send:
                if (AppUtils.isConnection(mActivity)) {
                    if (!TextUtils.isEmpty(fragmentMessagesDetailBinding.etMessage.getText().toString().trim()))
                        sendMessage(messagesQuery.getRef().push().getKey(), fragmentMessagesDetailBinding.etMessage.getText().toString(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT);
                    else
                        showToastShort(getString(R.string.enter_some_message));
                } else
                    showToastShort(getString(R.string.no_internet));
                break;
            case R.id.ib_attachment:
                if (PermissionUtility.isPermissionGranted(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, AppConstants.ACTIVITY_RESULT.CAMERA_PERMISSION)) {
                    Intent intent = new Intent(getActivity(), CameraTwoActivity.class);
                    intent.putExtra(AppConstants.CAMERA_CONSTANTS.IMAGE_LIMIT_ONESHOT, 1);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.CAMERA_ACTIVITY);
                }
                break;
            case R.id.tv_view_tag:
            case R.id.tv_toolbar_title:
                AppUtils.hideKeyboard(mActivity);
                popup.dismiss();
                Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
                intent.putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, currentChatModel);
                startActivityForResult(intent, GROUP_DETAIL_RESULT_CODE);
                break;
            case R.id.tv_shelf:
                AppUtils.hideKeyboard(mActivity);
                if (productList.size() > 0) {
                    Intent i = new Intent(mActivity, SearchTagShelfAcivity.class);
                    i.putExtra(AppConstants.BUNDLE_DATA, currentChatModel.getRoomId());
                    i.putExtra("IS_FROM", 3);
                    i.putExtra(AppConstants.TAG_KEY_CONSTENT.NAME, currentChatModel.getRoomName());
                    i.putExtra(AppConstants.KEY_CONSTENT.TYPE, AppConstants.ACTIVITY_RESULT.VIEW_PRODUCT);
                    mActivity.startActivityForResult(i, AppConstants.ACTIVITY_RESULT.CHAT_SHELF_DETAILS);

                }
                break;
        }

    }

    /**
     * used to start image uploading on Amazon
     *
     * @param path image path to upload
     * @param id   self generated image id
     */
    private void startUpload(String path, String id) {
        getLoadingStateObserver().onChanged(false);
        ImageBean bean = addDataInBean(path, id);
        mAmazonS3.uploadImage(bean);
    }

    /**
     * used to get the image model for sending over Amazon
     *
     * @param path image path
     * @param id   self generated image id to track the exact image
     * @return required image model will be returned
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
        sendMessage(bean.getId(), bean.getServerUrl(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE);
    }

    @Override
    public void uploadFailed(ImageBean bean) {
        setImageUploadingFailed(bean);
    }

    @Override
    public void uploadProgress(ImageBean bean) {

    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {
        setImageUploadingFailed(imageBean);
    }

    /**
     * used to update the image message item if image uploading failed on Amazon
     *
     * @param imageBean the failed image
     */
    private void setImageUploadingFailed(ImageBean imageBean) {
        MessageModel messageModel = messagesHashmap.get(imageBean.getId());
        if (messageModel != null) {
            messageModel.setLoadingImageOnAmazon(ImageBean.IMAGE_UPLOAD_FAILED);
            messagesDetailListAdapter.notifyItemChanged(messagesHashmap.getKeyIndex(messageModel.getMessageId()));
        }
    }

    /**
     * used to get the message model for sending message
     *
     * @param messageId   firebase generated id for message
     * @param text        text for the message
     * @param messageType type for the message
     * @return return final messageModel
     */
    private MessageModel getMessageModel(String messageId, String text, String messageType) {
        MessageModel lastMessage = new MessageModel();
        lastMessage.setMessageId(messageId);
        lastMessage.setMessageText(text);
        lastMessage.setMessageStatus(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_DELIVERED);
        lastMessage.setMessageType(messageType);
        lastMessage.setSenderId(user.getUserId());
        try {
            lastMessage.setTimeStamp(ServerValue.TIMESTAMP);
        } catch (Exception e) {
            lastMessage.setTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
            e.printStackTrace();
        }
        lastMessage.setSenderImage(user.getProfilePicture());
        lastMessage.setSenderName(user.getFullName());
        lastMessage.setRoomId(roomId);
        return lastMessage;
    }

    /**
     * used to send the typed message in the chat
     *
     * @param messageId   the firebase generated id for the message
     * @param text        the text for the message
     * @param messageType the type for the message like text,image etc
     */
    private void sendMessage(String messageId, String text, String messageType) {
        TimeAgo timeAgo = new TimeAgo();
        if (lastMessageTimeStamp > 0) {
            if (timeAgo.checkCurrentDate(lastMessageTimeStamp)) {
                if (!timeAgo.checkTimeDifference(lastMessageTimeStamp))
                    messagesDetailViewModel.sendMessageToGroup(user.getUserId(), roomId, "","",getMessageModel(messagesQuery.getRef().push().getKey(), "", AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TIME_HEADER));
            } else
                messagesDetailViewModel.sendMessageToGroup(user.getUserId(), roomId, "","",getMessageModel(messagesQuery.getRef().push().getKey(), "", AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_DATE_HEADER));
        } else
            messagesDetailViewModel.sendMessageToGroup(user.getUserId(), roomId,"","", getMessageModel(messagesQuery.getRef().push().getKey(), "", AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_DATE_HEADER));
        MessageModel messageModel= getMessageModel(messageId, text, messageType);
        messagesDetailViewModel.sendMessageToGroup(user.getUserId(), roomId,user.getFullName() + " " + getString(R.string.send_a_message) + " " + getString(R.string.in) + " " + currentChatModel.getRoomName(),messageModel.getMessageText(),messageModel);
        if (messageType.equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT))
            fragmentMessagesDetailBinding.etMessage.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messagesDetailListAdapter != null && messagesDetailListAdapter.listenerModelHashMap != null) {
            for (final Map.Entry<String, RemoveFirebaseListenerModel> entry : messagesDetailListAdapter.listenerModelHashMap.entrySet()) {
                RemoveFirebaseListenerModel removeFirebaseListenerModel = entry.getValue();
                removeFirebaseListenerModel.getQuery().removeEventListener(removeFirebaseListenerModel.getValueEventListener());
            }
            messagesDetailListAdapter.listenerModelHashMap.clear();
        }
        newMessageQuery.removeEventListener(newMessageAddedListener);
        roomNodeQuery.removeEventListener(roomNodeListener);
        tagAnnouncementQuery.removeEventListener(announcementValueEventListener);
    }

}
