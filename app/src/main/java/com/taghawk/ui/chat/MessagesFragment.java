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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.adapters.ChatMessagesListAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_view.PositionedLinkedHashmap;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.FragmentChatMessagesBinding;
import com.taghawk.firebase.FirebaseChildEventListener;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.RemoveFirebaseListenerModel;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.TagDetailFirebaseModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.request.User;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;

import java.util.HashMap;
import java.util.Map;

public class MessagesFragment extends BaseFragment {

    public PositionedLinkedHashmap<String, ChatModel> openMessagesHashmap, pinnedMessagesHashmap;
    /**
     * A {@link HomeViewModel} object to handle all the actions and business logic
     */
    private MessagesViewModel messagesViewModel;
    private FragmentChatMessagesBinding fragmentChatMessagesBinding;
    private Activity mActivity;
    private ChatMessagesListAdapter openMessagesListAdapter;
    private ChatMessagesListAdapter pinnedMessagesListAdapter;
    private User user;
    private FirebaseChildEventListener firebaseNewlyAddedChildEventListener, firebaseChangedChildEventListener;
    private Query newlyAddedInboxQuery, anyInboxChangeQuery;
    private int totalGroupcount;
    private HashMap<String, RemoveFirebaseListenerModel> listenerMapGroupChat;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentChatMessagesBinding = FragmentChatMessagesBinding.inflate(inflater, container, false);
        initView();
        setUpList();
        return fragmentChatMessagesBinding.getRoot();
    }

    /**
     * this method is used to initialize the views or variables
     */
    private void initView() {
        mActivity = getActivity();
        pinnedMessagesHashmap = new PositionedLinkedHashmap<>();
        openMessagesHashmap = new PositionedLinkedHashmap<>();
        listenerMapGroupChat = new HashMap<>();
        user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
        fragmentChatMessagesBinding.rvPinnedChats.setLayoutManager(new LinearLayoutManager(mActivity));
        fragmentChatMessagesBinding.rvOpenChats.setLayoutManager(new LinearLayoutManager(mActivity));
        ((SimpleItemAnimator) fragmentChatMessagesBinding.rvOpenChats.getItemAnimator()).setSupportsChangeAnimations(false);
        ((SimpleItemAnimator) fragmentChatMessagesBinding.rvPinnedChats.getItemAnimator()).setSupportsChangeAnimations(false);
        openMessagesListAdapter = new ChatMessagesListAdapter(openMessagesHashmap, ChatMessagesListAdapter.ADAPTER_TYPE_INBOX, new ChatMessagesListAdapter.OnClickListener() {
            @Override
            public void onClick(final ChatModel chatModel, View view) {
                performListClickAction(chatModel, view);
            }
        });
        fragmentChatMessagesBinding.rvOpenChats.setAdapter(openMessagesListAdapter);
        pinnedMessagesListAdapter = new ChatMessagesListAdapter(pinnedMessagesHashmap, ChatMessagesListAdapter.ADAPTER_TYPE_INBOX, new ChatMessagesListAdapter.OnClickListener() {
            @Override
            public void onClick(final ChatModel chatModel, View view) {
                performListClickAction(chatModel, view);
            }
        });
        fragmentChatMessagesBinding.rvPinnedChats.setAdapter(pinnedMessagesListAdapter);
        fragmentChatMessagesBinding.rvOpenChats.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    ((HomeActivity) mActivity).handleDirection(View.GONE);
                } else if (dy <= 0) {
                    ((HomeActivity) mActivity).handleDirection(View.VISIBLE);
                }
            }
        });
    }

    /**
     * this method is used to set up all the inbox related data
     */
    private void setUpList() {
        messagesViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        messagesViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        messagesViewModel.getMembersActionLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse != null) {
                    String tagId = (String) commonResponse.getExtraLocalData().get(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID);
                    ChatModel chatModel = pinnedMessagesHashmap.get(tagId);
                    if (chatModel == null)
                        chatModel = openMessagesHashmap.get(tagId);
                    if (chatModel != null) {
                        switch ((int) commonResponse.getExtraLocalData().get(AppConstants.KEY_CONSTENT.ACTION_TYPE)) {
                            case GroupDetailFragment.MEMBER_ACTION_DELETE_TAG:
                                messagesViewModel.deleteTag(user.getUserId(), chatModel.getMembers(), tagId);
                                break;
                            case GroupDetailFragment.MEMBER_ACTION_EXIT_TAG:
                                messagesViewModel.exitTag(user.getUserId(), user.getFullName(), tagId);
                                break;
                        }
                    }
                }
            }
        });
        getPastInboxData();
        getNewlyAddedInbox();
        getChangedInbox();
    }

    /**
     * this method is used to handle the click events of adapter class
     * for pinned or open chat inbox items
     *
     * @param chatModel model for which click happened
     * @param view      on which click happened
     */
    private void performListClickAction(final ChatModel chatModel, View view) {
        switch (view.getId()) {
            case R.id.ll_swipe:
                if (chatModel.getChatType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT)) {
                    if (chatModel.getOtherUserId().equalsIgnoreCase(user.getUserId())) {

                        // delete the tag if current user is owner of the tag
                        DialogUtil.getInstance().CustomBottomSheetDialogForDeleteGroup(mActivity, getString(R.string.are_you_sure_you_want_to_delete) + " " + chatModel.getRoomName() + " " + getString(R.string.tag) + "?", new OnDialogItemClickListener() {
                            @Override
                            public void onPositiveBtnClick() {
                                if (AppUtils.isConnection(mActivity)) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, chatModel.getRoomId());
                                    getLoadingStateObserver().onChanged(true);
                                    messagesViewModel.deleteTagApi(hashMap, GroupDetailFragment.MEMBER_ACTION_DELETE_TAG);
                                } else
                                    showToastShort(getString(R.string.no_internet));
                            }

                            @Override
                            public void onNegativeBtnClick() {

                            }
                        });
                    } else {

                        // exit the tag if current user is member of the tag
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.exit), getString(R.string.exit_tag) + "!", getString(R.string.are_you_sure_you_want_to_exit) + " " + chatModel.getRoomName() + " " + getString(R.string.tag) + "?", new OnDialogItemClickListener() {
                            @Override
                            public void onPositiveBtnClick() {
                                if (AppUtils.isConnection(mActivity)) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, chatModel.getRoomId());
                                    getLoadingStateObserver().onChanged(true);
                                    messagesViewModel.exitTagApi(hashMap, GroupDetailFragment.MEMBER_ACTION_EXIT_TAG);
                                } else
                                    showToastShort(getString(R.string.no_internet));
                            }

                            @Override
                            public void onNegativeBtnClick() {

                            }
                        });
                    }
                } else {

                    // delete the chat for the current user if one to one chat
                    AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.delete), getString(R.string.delete_chat) + "!", getString(R.string.are_you_sure_you_want_to_delete_chat_with) + " " + chatModel.getRoomName() + "?", new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {
                            if (AppUtils.isConnection(mActivity)) {
                                messagesViewModel.deleteUserChat(user.getUserId(), chatModel.getRoomId());
                            } else
                                showToastShort(getString(R.string.no_internet));
                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });
                }
                break;
            default:
                mActivity.startActivityForResult(new Intent(mActivity, MessagesDetailActivity.class).putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel).putExtra(AppConstants.FIREBASE.TIMESTAMP, chatModel.getCreatedTimeStampLong()), AppConstants.ACTIVITY_RESULT.TAG_DETAILS);
                break;
        }
    }

    /**
     * this method is used to get the changed or removed inbox item from the firebase
     */
    private void getChangedInbox() {
        anyInboxChangeQuery = messagesViewModel.getUserChatsQuery(user.getUserId());
        firebaseChangedChildEventListener = new FirebaseChildEventListener() {
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    // if room node is removed from the firebase
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    RemoveFirebaseListenerModel removeFirebaseListenerModel = listenerMapGroupChat.get(chatModel.getRoomId());
                    if (removeFirebaseListenerModel != null) {
                        removeFirebaseListenerModel.getQuery().removeEventListener(removeFirebaseListenerModel.getValueEventListener());
                        listenerMapGroupChat.remove(chatModel.getRoomId());
                    }
                    removeInboxData(chatModel);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (isAdded())
                    if (dataSnapshot.getValue() != null) {

                        // if room node is changed on firebase
                        ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                        if (chatModel != null && chatModel.getRoomId() != null) {
                            if (openMessagesHashmap.containsKey(chatModel.getRoomId())) {
                                if (chatModel.getChatType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT))
                                    chatModel.setLastMessage(openMessagesHashmap.get(chatModel.getRoomId()).getLastMessage());
                                chatModel.setRoomName(openMessagesHashmap.get(chatModel.getRoomId()).getRoomName());
                                chatModel.setRoomImage(openMessagesHashmap.get(chatModel.getRoomId()).getRoomImage());
                                openMessagesHashmap.remove(chatModel.getRoomId());
                                openMessagesHashmap.removeIndex(chatModel.getRoomId());
                            } else if (pinnedMessagesHashmap.containsKey(chatModel.getRoomId())) {
                                if (chatModel.getChatType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT))
                                    chatModel.setLastMessage(pinnedMessagesHashmap.get(chatModel.getRoomId()).getLastMessage());
                                chatModel.setRoomName(pinnedMessagesHashmap.get(chatModel.getRoomId()).getRoomName());
                                chatModel.setRoomImage(pinnedMessagesHashmap.get(chatModel.getRoomId()).getRoomImage());
                                pinnedMessagesHashmap.removeIndex(chatModel.getRoomId());
                                pinnedMessagesHashmap.remove(chatModel.getRoomId());
                            }
                            if (chatModel.isPinned())
                                pinnedMessagesHashmap.put(chatModel.getRoomId(), chatModel);
                            else
                                openMessagesHashmap.put(chatModel.getRoomId(), chatModel);
                            pinnedMessagesListAdapter.updateList();
                            openMessagesListAdapter.updateList();
                            updateNoDataUIs();
                        }
                    }
            }
        };
        anyInboxChangeQuery.addChildEventListener(firebaseChangedChildEventListener);
    }

    /**
     * this method is used to remove the item if anyone deletes or remove chat or tag from firebase
     *
     * @param chatModel the removed chat
     */
    private void removeInboxData(ChatModel chatModel) {
        if (isAdded()) {
            if (chatModel.isPinned()) {
                int position = pinnedMessagesHashmap.getKeyIndex(chatModel.getRoomId());
                pinnedMessagesHashmap.remove(chatModel.getRoomId());
                pinnedMessagesHashmap.removeIndex(chatModel.getRoomId());
                pinnedMessagesListAdapter.notifyItemRemoved(position);
            } else {
                int position = openMessagesHashmap.getKeyIndex(chatModel.getRoomId());
                openMessagesHashmap.remove(chatModel.getRoomId());
                openMessagesHashmap.removeIndex(chatModel.getRoomId());
                openMessagesListAdapter.notifyItemRemoved(position);
            }
            updateNoDataUIs();
        }
    }

    /**
     * this method is used to get the new inbox chat added from new user or tag/group
     */
    private void getNewlyAddedInbox() {
        newlyAddedInboxQuery = messagesViewModel.getUserChatsQueryForNewlyAddedInbox(user.getUserId());
        firebaseNewlyAddedChildEventListener = new FirebaseChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (isAdded())
                    if (dataSnapshot.getValue() != null) {
                        final ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                        if (chatModel != null) {
                            PositionedLinkedHashmap<String, ChatModel> positionedLinkedHashmap = new PositionedLinkedHashmap<>();
                            positionedLinkedHashmap.put(chatModel.getRoomId(), chatModel);
                            if (chatModel.isPinned()) {
                                positionedLinkedHashmap.putAll(pinnedMessagesHashmap);
                                pinnedMessagesHashmap.clear();
                                pinnedMessagesHashmap.addIndexOnPosition(chatModel.getRoomId(), 0);
                                pinnedMessagesHashmap.putAll(positionedLinkedHashmap);
                            } else {
                                positionedLinkedHashmap.putAll(openMessagesHashmap);
                                openMessagesHashmap.clear();
                                openMessagesHashmap.addIndexOnPosition(chatModel.getRoomId(), 0);
                                openMessagesHashmap.putAll(positionedLinkedHashmap);
                            }
                            TransitionManager.beginDelayedTransition(fragmentChatMessagesBinding.nslMain, new ChangeBounds());
                            if (chatModel.getChatType() != null && chatModel.getChatType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT)) {

                                //only minimum data is availble on roomGroups node so rest of the data is fetched from the tag detail node
                                Query query = messagesViewModel.getGroupNodeQuery(chatModel.getRoomId());
                                ValueEventListener valueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            totalGroupcount += 1; // used to calculate whether we get the callbacks for all the tags or groups
                                            final TagDetailFirebaseModel tagDetailFirebaseModel = dataSnapshot.getValue(TagDetailFirebaseModel.class);
                                            messagesViewModel.getUserChatsQuery(user.getUserId()).child(tagDetailFirebaseModel.getTagId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        if (tagDetailFirebaseModel.getMembers().containsKey(user.getUserId())) {
                                                            messagesViewModel.updateGroupDataOnRoomNode(user.getUserId(), tagDetailFirebaseModel.getTagId(), tagDetailFirebaseModel.getTagName(), tagDetailFirebaseModel.getTagImageUrl());
                                                            updateGroupNode(tagDetailFirebaseModel, false);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };
                                RemoveFirebaseListenerModel removeFirebaseListenerModel = new RemoveFirebaseListenerModel();
                                removeFirebaseListenerModel.setQuery(query);
                                removeFirebaseListenerModel.setValueEventListener(valueEventListener);
                                listenerMapGroupChat.put(chatModel.getRoomId(), removeFirebaseListenerModel);
                                query.addValueEventListener(valueEventListener);
                            } else if (chatModel.isPinned())
                                pinnedMessagesListAdapter.notifyItemInserted(0);
                            else
                                openMessagesListAdapter.notifyItemInserted(0);
                        }
                    }
            }
        };
        newlyAddedInboxQuery.addChildEventListener(firebaseNewlyAddedChildEventListener);
    }

    /**
     * this method is used to check whether there is any data in pinned or open chats
     */
    private void updateNoDataUIs() {
        if (openMessagesHashmap.size() > 0) {
            fragmentChatMessagesBinding.tvOpenChats.setVisibility(View.VISIBLE);
            fragmentChatMessagesBinding.rvOpenChats.setVisibility(View.VISIBLE);
        } else {
            fragmentChatMessagesBinding.tvOpenChats.setVisibility(View.GONE);
            fragmentChatMessagesBinding.rvOpenChats.setVisibility(View.GONE);
        }
        if (pinnedMessagesHashmap.size() > 0) {
            fragmentChatMessagesBinding.tvPinnedChats.setVisibility(View.VISIBLE);
            fragmentChatMessagesBinding.rvPinnedChats.setVisibility(View.VISIBLE);
        } else {
            fragmentChatMessagesBinding.tvPinnedChats.setVisibility(View.GONE);
            fragmentChatMessagesBinding.rvPinnedChats.setVisibility(View.GONE);
        }
        if (openMessagesHashmap.size() == 0 && pinnedMessagesHashmap.size() == 0)
            noData(View.GONE, getString(R.string.oops_it_s_empty), getString(R.string.no_chats_available_in_the_inbox));
    }

    /**
     * this method is used to get all the past inbox chat data from firebase
     */
    private void getPastInboxData() {
        DatabaseReference databaseReference = messagesViewModel.getUserChatsQuery(user.getUserId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (isAdded())
                        noData(View.GONE, getString(R.string.oops_it_s_empty), getString(R.string.no_chats_available_in_the_inbox));
                } else {
                    totalGroupcount = 0;
                    if (isAdded())
                        TransitionManager.beginDelayedTransition(fragmentChatMessagesBinding.nslMain, new ChangeBounds());
                    for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                        final ChatModel chatModel = dataSnapshots.getValue(ChatModel.class);
                        if (chatModel.isPinned()) {
                            pinnedMessagesHashmap.put(chatModel.getRoomId(), chatModel);
                            pinnedMessagesHashmap.addIndex(chatModel.getRoomId());
                        } else {
                            openMessagesHashmap.put(chatModel.getRoomId(), chatModel);
                            openMessagesHashmap.addIndex(chatModel.getRoomId());
                        }
                        if (chatModel.getChatType() != null && chatModel.getChatType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT)) {

                            //only minimum data is availble on roomGroups node so rest of the data is fetched from the tag detail node
                            Query query = messagesViewModel.getGroupNodeQuery(chatModel.getRoomId());
                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        totalGroupcount += 1; // used to calculate whether we get the callbacks for all the tags or groups
                                        final TagDetailFirebaseModel tagDetailFirebaseModel = dataSnapshot.getValue(TagDetailFirebaseModel.class);

                                        //to check whether current user is a member of this tag/group or has been removed
                                        messagesViewModel.getUserChatsQuery(user.getUserId()).child(tagDetailFirebaseModel.getTagId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    try {
                                                        if (tagDetailFirebaseModel.getMembers().containsKey(user.getUserId())) {
                                                            messagesViewModel.updateGroupDataOnRoomNode(user.getUserId(), tagDetailFirebaseModel.getTagId(), tagDetailFirebaseModel.getTagName(), tagDetailFirebaseModel.getTagImageUrl());
                                                            updateGroupNode(tagDetailFirebaseModel, true);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };
                            RemoveFirebaseListenerModel removeFirebaseListenerModel = new RemoveFirebaseListenerModel();
                            removeFirebaseListenerModel.setQuery(query);
                            removeFirebaseListenerModel.setValueEventListener(valueEventListener);
                            listenerMapGroupChat.put(chatModel.getRoomId(), removeFirebaseListenerModel);
                            query.addValueEventListener(valueEventListener);
                        }
                    }

                    //if no chat related to any tag or group
                    if (isAdded() && listenerMapGroupChat.size() == 0) {
                        if (pinnedMessagesHashmap.size() > 0) {
                            pinnedMessagesListAdapter.updateList();
                            fragmentChatMessagesBinding.tvPinnedChats.setVisibility(View.VISIBLE);
                            fragmentChatMessagesBinding.rvPinnedChats.setVisibility(View.VISIBLE);
                        }
                        if (openMessagesHashmap.size() > 0) {
                            openMessagesListAdapter.updateList();
                            fragmentChatMessagesBinding.tvOpenChats.setVisibility(View.VISIBLE);
                            fragmentChatMessagesBinding.rvOpenChats.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * this method is used to update the group item when some change in tag detail on firebase
     *
     * @param tagDetailFirebaseModel changed tag detail
     * @param isSort                 whether we want to sort the adapter items on basis of timestamp
     */
    private void updateGroupNode(TagDetailFirebaseModel tagDetailFirebaseModel, boolean isSort) {
        if (pinnedMessagesHashmap.containsKey(tagDetailFirebaseModel.getTagId())) {
            pinnedMessagesHashmap.get(tagDetailFirebaseModel.getTagId()).setLastMessage(tagDetailFirebaseModel.getLastMessage());
            pinnedMessagesHashmap.get(tagDetailFirebaseModel.getTagId()).setRoomName(tagDetailFirebaseModel.getTagName());
            pinnedMessagesHashmap.get(tagDetailFirebaseModel.getTagId()).setRoomImage(tagDetailFirebaseModel.getTagImageUrl());
            pinnedMessagesHashmap.get(tagDetailFirebaseModel.getTagId()).setOtherUserId(tagDetailFirebaseModel.getOwnerId());
            pinnedMessagesHashmap.get(tagDetailFirebaseModel.getTagId()).setMembers(tagDetailFirebaseModel.getMembers());
            //this condition is used so that we will notify the adapter only once we got listener callback for all the queries
            if (isAdded() && totalGroupcount >= listenerMapGroupChat.size()) {
                if (isSort)
                    pinnedMessagesListAdapter.updateList();
                else
                    pinnedMessagesListAdapter.notifyItemChanged(pinnedMessagesHashmap.getKeyIndex(tagDetailFirebaseModel.getTagId()));
            }
        } else if (openMessagesHashmap.containsKey(tagDetailFirebaseModel.getTagId())) {
            openMessagesHashmap.get(tagDetailFirebaseModel.getTagId()).setLastMessage(tagDetailFirebaseModel.getLastMessage());
            openMessagesHashmap.get(tagDetailFirebaseModel.getTagId()).setRoomName(tagDetailFirebaseModel.getTagName());
            openMessagesHashmap.get(tagDetailFirebaseModel.getTagId()).setRoomImage(tagDetailFirebaseModel.getTagImageUrl());
            openMessagesHashmap.get(tagDetailFirebaseModel.getTagId()).setOtherUserId(tagDetailFirebaseModel.getOwnerId());
            openMessagesHashmap.get(tagDetailFirebaseModel.getTagId()).setMembers(tagDetailFirebaseModel.getMembers());
            //this condition is used so that we will notify the adapter only once we got listener callback for all the queries
            if (isAdded() && totalGroupcount >= listenerMapGroupChat.size()) {
                if (isSort)
                    openMessagesListAdapter.updateList();
                else
                    openMessagesListAdapter.notifyItemChanged(openMessagesHashmap.getKeyIndex(tagDetailFirebaseModel.getTagId()));
            }
        }
        updateNoDataUIs();
    }

    /**
     * this method is used to show the view for not data found
     *
     * @param VISIBILITY whether data views are visible or not
     * @param errorTitle title for no data view
     * @param errorMsg   description for no data view
     */
    private void noData(int VISIBILITY, String errorTitle, String errorMsg) {
        fragmentChatMessagesBinding.tvPinnedChats.setVisibility(VISIBILITY);
        fragmentChatMessagesBinding.tvOpenChats.setVisibility(VISIBILITY);
        fragmentChatMessagesBinding.includeHeaderEmpty.tvTitle.setText(errorTitle);
        fragmentChatMessagesBinding.includeHeaderEmpty.tvEmptyMsg.setText(errorMsg);
        fragmentChatMessagesBinding.tvNoData.setVisibility(VISIBILITY == View.VISIBLE ? View.GONE : View.VISIBLE);
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //remove all the attached firebase listeners
        newlyAddedInboxQuery.removeEventListener(firebaseNewlyAddedChildEventListener);
        anyInboxChangeQuery.removeEventListener(firebaseChangedChildEventListener);
        for (final Map.Entry<String, RemoveFirebaseListenerModel> entry : listenerMapGroupChat.entrySet()) {
            RemoveFirebaseListenerModel removeFirebaseListenerModel = entry.getValue();
            removeFirebaseListenerModel.getQuery().removeEventListener(removeFirebaseListenerModel.getValueEventListener());
        }
        listenerMapGroupChat.clear();
        if (openMessagesListAdapter != null && openMessagesListAdapter.listenerModelHashMap != null) {
            for (final Map.Entry<String, RemoveFirebaseListenerModel> entry : openMessagesListAdapter.listenerModelHashMap.entrySet()) {
                RemoveFirebaseListenerModel removeFirebaseListenerModel = entry.getValue();
                removeFirebaseListenerModel.getQuery().removeEventListener(removeFirebaseListenerModel.getValueEventListener());
            }
            openMessagesListAdapter.listenerModelHashMap.clear();
        }
        if (pinnedMessagesListAdapter != null && pinnedMessagesListAdapter.listenerModelHashMap != null) {
            for (final Map.Entry<String, RemoveFirebaseListenerModel> entry : pinnedMessagesListAdapter.listenerModelHashMap.entrySet()) {
                RemoveFirebaseListenerModel removeFirebaseListenerModel = entry.getValue();
                removeFirebaseListenerModel.getQuery().removeEventListener(removeFirebaseListenerModel.getValueEventListener());
            }
            pinnedMessagesListAdapter.listenerModelHashMap.clear();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.TAG_DETAILS:
                if (resultCode == Activity.RESULT_OK) {
                    ((HomeActivity) mActivity).updateProductList();
                }
                break;
        }
    }
}
