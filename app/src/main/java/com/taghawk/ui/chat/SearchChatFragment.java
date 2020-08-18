package com.taghawk.ui.chat;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.TagHawkApplication;
import com.taghawk.adapters.ChatMessagesListAdapter;
import com.taghawk.adapters.PendingRequestsAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_view.PositionedLinkedHashmap;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.FragmentPendingRequestsBinding;
import com.taghawk.databinding.FragmentSearchBinding;
import com.taghawk.firebase.FirebaseManager;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.pendingRequests.PendingRequest;
import com.taghawk.model.pendingRequests.PendingRequestResponse;
import com.taghawk.model.request.User;
import com.taghawk.model.tag.TagData;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.home.ZoomImageActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class SearchChatFragment extends BaseFragment {

    private FragmentSearchBinding fragmentSearchBinding;
    private Activity mActivity;
    private PositionedLinkedHashmap<String, ChatModel> chatMapOriginal, chatMapFiltered;
    private ChatMessagesListAdapter chatMessagesListAdapter;
    private MessagesViewModel messagesViewModel;
    private User user;

    /**
     * This method is used to return the instance of this fragment
     *
     * @return new instance of {@link SearchChatFragment}
     */
    public static SearchChatFragment getInstance() {
        return new SearchChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false);
        initView();
        return fragmentSearchBinding.getRoot();
    }

    // init views and listener
    private void initView() {
        mActivity = getActivity();
        chatMapOriginal = new PositionedLinkedHashmap<>();
        chatMapFiltered = new PositionedLinkedHashmap<>();
        messagesViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        messagesViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        chatMapOriginal = TagHawkApplication.getInstance().getChatInboxMap();
        user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
        ((SimpleItemAnimator) fragmentSearchBinding.rvSuggestionSearchList.getItemAnimator()).setSupportsChangeAnimations(false);
        fragmentSearchBinding.rvSuggestionSearchList.setLayoutManager(new LinearLayoutManager(mActivity));
        messagesViewModel.getMembersActionLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse != null) {
                    String tagId = (String) commonResponse.getExtraLocalData().get(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID);
                    ChatModel chatModel = chatMapOriginal.get(tagId);
                    if (chatModel != null) {
                        switch ((int) commonResponse.getExtraLocalData().get(AppConstants.KEY_CONSTENT.ACTION_TYPE)) {
                            case GroupDetailFragment.MEMBER_ACTION_DELETE_TAG:
                                messagesViewModel.deleteTag(user.getUserId(),chatModel.getMembers(), tagId);
                                chatMapOriginal.remove(chatModel.getRoomId());
                                chatMapFiltered.remove(chatModel.getRoomId());
                                chatMessagesListAdapter.updateList();
                                fragmentSearchBinding.tvNoData.setVisibility(chatMapFiltered.size() > 0 ? View.GONE : View.VISIBLE);
                                break;
                            case GroupDetailFragment.MEMBER_ACTION_EXIT_TAG:
                                messagesViewModel.exitTag(user.getUserId(), user.getFullName(), tagId);
                                chatMapOriginal.remove(chatModel.getRoomId());
                                chatMapFiltered.remove(chatModel.getRoomId());
                                chatMessagesListAdapter.updateList();
                                fragmentSearchBinding.tvNoData.setVisibility(chatMapFiltered.size() > 0 ? View.GONE : View.VISIBLE);
                                break;
                        }
                    }
                }
            }
        });
        chatMessagesListAdapter = new ChatMessagesListAdapter(chatMapFiltered, ChatMessagesListAdapter.ADAPTER_TYPE_SEARCH, new ChatMessagesListAdapter.OnClickListener() {
            @Override
            public void onClick(final ChatModel chatModel, View view) {
                switch (view.getId()) {
                    case R.id.ll_swipe:
                        if (chatModel.getChatType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT)) {
                            if (chatModel.getOtherUserId().equalsIgnoreCase(user.getUserId())) {
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
                            AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.delete), getString(R.string.delete_chat) + "!", getString(R.string.are_you_sure_you_want_to_delete_chat_with) + " " + chatModel.getRoomName() + "?", new OnDialogItemClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    if (AppUtils.isConnection(mActivity)) {
                                        messagesViewModel.deleteUserChat(user.getUserId(), chatModel.getRoomId());
                                        chatMapOriginal.remove(chatModel.getRoomId());
                                        chatMapFiltered.remove(chatModel.getRoomId());
                                        chatMessagesListAdapter.updateList();
                                        fragmentSearchBinding.tvNoData.setVisibility(chatMapFiltered.size() > 0 ? View.GONE : View.VISIBLE);
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
                        startActivity(new Intent(mActivity, MessagesDetailActivity.class).putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel).putExtra(AppConstants.FIREBASE.TIMESTAMP, chatModel.getCreatedTimeStampLong()));
                        mActivity.finish();
                        break;
                }
            }
        });
        fragmentSearchBinding.rvSuggestionSearchList.setAdapter(chatMessagesListAdapter);
        fragmentSearchBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterSearchData(charSequence.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        fragmentSearchBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });
    }

    /**
     * Used to filter out the searched results from the inbox chat data
     * @param s
     */
    private void filterSearchData(String s) {
        chatMapFiltered.clear();
        if (!s.equalsIgnoreCase(""))
            for (final Map.Entry<String, ChatModel> entry : chatMapOriginal.entrySet()) {
                if (entry.getValue().getRoomName().trim().toLowerCase().contains(s.toLowerCase()))
                    chatMapFiltered.put(entry.getKey(), entry.getValue());
            }
        if (chatMapFiltered.size() > 0)
            sortData();
        else {
            fragmentSearchBinding.rvSuggestionSearchList.setVisibility(View.GONE);
            fragmentSearchBinding.tvNoData.setVisibility(View.VISIBLE);
        }
        chatMessagesListAdapter.notifyDataSetChanged();
    }

    /**
     * used to sort the data alphabetically
     */
    public void sortData() {
        fragmentSearchBinding.tvNoData.setVisibility(View.GONE);
        fragmentSearchBinding.rvSuggestionSearchList.setVisibility(View.VISIBLE);
        List<Map.Entry<String, ChatModel>> entries = new ArrayList<>(chatMapFiltered.entrySet());
        chatMapFiltered.clear();
        Collections.sort(entries, new Comparator<Map.Entry<String, ChatModel>>() {
            @Override
            public int compare(Map.Entry<String, ChatModel> lhs, Map.Entry<String, ChatModel> rhs) {
                return rhs.getValue().getRoomName().compareTo(lhs.getValue().getRoomName());
            }
        });
        for (Map.Entry<String, ChatModel> entry : entries) {
            chatMapFiltered.put(entry.getKey(), entry.getValue());
        }
        chatMessagesListAdapter.updateList();
    }
}
