package com.taghawk.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_view.PositionedLinkedHashmap;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.RowChatMessagesListBinding;
import com.taghawk.databinding.RowMessagesHeaderBinding;
import com.taghawk.model.RemoveFirebaseListenerModel;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.login.LoginFirebaseModel;
import com.taghawk.model.request.User;
import com.taghawk.util.AppUtils;
import com.taghawk.util.TimeAgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMessagesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ADAPTER_TYPE_INBOX = 1;
    public static final int ADAPTER_TYPE_SEARCH = 2;
    private Context context;
    private OnClickListener onClickListener;
    private int adapterType;
    private HashMap<String, LoginFirebaseModel> membersHashMap;
    public HashMap<String, RemoveFirebaseListenerModel> listenerModelHashMap;
    private PositionedLinkedHashmap<String, ChatModel> chatInboxHashmap;

    public ChatMessagesListAdapter(PositionedLinkedHashmap<String, ChatModel> chatHasmap, int adapterType, OnClickListener onClickListener) {
        this.chatInboxHashmap = chatHasmap;
        this.onClickListener = onClickListener;
        this.adapterType = adapterType;
        membersHashMap = new HashMap<>();
        listenerModelHashMap = new HashMap<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        RowChatMessagesListBinding mBinding = RowChatMessagesListBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ChatMessageViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ChatMessageViewHolder holder = null;
        holder = (ChatMessageViewHolder) viewHolder;
        holder.bind(chatInboxHashmap.get(chatInboxHashmap.getKeyValue(position)));
    }

    @Override
    public int getItemCount() {
        return chatInboxHashmap.size();
    }

    public interface OnClickListener {
        void onClick(ChatModel chatModel, View view);
    }

    private class ChatMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RowChatMessagesListBinding viewBinding;

        public ChatMessageViewHolder(RowChatMessagesListBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
        }

        public void bind(final ChatModel chatModel) {
            final User user = DataManager.getInstance().getUserDetails();
            if (getAdapterPosition() == chatInboxHashmap.size() - 1)
                viewBinding.viewDivider.setVisibility(View.INVISIBLE);
            else
                viewBinding.viewDivider.setVisibility(View.VISIBLE);
            viewBinding.llMain.setOnClickListener(this);
            viewBinding.llSwipe.setOnClickListener(this);
            viewBinding.ivMute.setVisibility(chatModel.isChatMute() ? View.VISIBLE : View.GONE);
            if (chatModel.getChatType() != null && chatModel.getChatType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_SINGLE_CHAT) && chatModel.getProductInfo() != null && chatModel.getProductInfo().getProductId() != null && !chatModel.getProductInfo().getProductId().equalsIgnoreCase("")) {
                viewBinding.ivProduct.setVisibility(View.VISIBLE);
                AppUtils.loadCircularImage(context, chatModel.getProductInfo().getProductImage(), 20, R.drawable.ic_home_placeholder, viewBinding.ivProduct, true);
            } else
            viewBinding.ivProduct.setVisibility(View.GONE);
            AppUtils.loadCircularImage(context, chatModel.getRoomImage(), 300, R.drawable.ic_detail_user_placeholder, viewBinding.ivUser, true);
            viewBinding.tvUserName.setText(chatModel.getRoomName());
            TimeAgo timeAgo = new TimeAgo();
            if (chatModel.getLastMessage() != null && chatModel.getLastMessage().getTimeStampLong() > 0) {
                viewBinding.tvTime.setText(timeAgo.getFormattedTimeInDaysAgo(chatModel.getLastMessage().getTimeStampLong(), context));
//                Log.e("timestamp", "time is" + chatModel.getLastMessage().getTimeStampLong());
            }

            if (adapterType == ADAPTER_TYPE_INBOX) {
                if (chatModel.isChatMute()) {
                    if (chatModel.getUnreadMessageCount() > 0) {
                        viewBinding.tvUnreadCount.setVisibility(View.VISIBLE);
                        viewBinding.tvUnreadCount.setText("");
                        viewBinding.tvUnreadCount.setBackgroundResource(R.drawable.circular_white_border_color_accent_bg);
                        if (chatModel.getChatType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT)) {
                            String text = viewBinding.tvMessage.getText().toString();
                            viewBinding.tvMessage.setText(AppUtils.getSpannableString(context, "(" + chatModel.getUnreadMessageCount() + " " + context.getString(R.string.news) + ")", R.color.colorAccent, 1f, false, true, false, null));
                            viewBinding.tvMessage.append(" ");
                            viewBinding.tvMessage.append(text);
                        }
                    } else
                        viewBinding.tvUnreadCount.setVisibility(View.GONE);
                } else {
                    if (chatModel.getUnreadMessageCount() > 0) {
                        viewBinding.tvUnreadCount.setVisibility(View.VISIBLE);
//                        viewBinding.tvUnreadCount.setText(chatModel.getUnreadMessageCount() > 99 ? 99 + "+" : String.valueOf(chatModel.getUnreadMessageCount()));
                        viewBinding.tvUnreadCount.setText(chatModel.getUnreadMessageCount() > 99 ? " " + 99 : String.valueOf(chatModel.getUnreadMessageCount()));
                        viewBinding.tvUnreadCount.setBackgroundResource(R.drawable.circular_color_accent_border_bg);
                    } else
                        viewBinding.tvUnreadCount.setVisibility(View.GONE);
                }
            } else
                viewBinding.tvUnreadCount.setVisibility(View.GONE);

            if (chatModel.getLastMessage() != null && chatModel.getLastMessage().getMessageId() != null)
                switch (chatModel.getLastMessage().getMessageType()) {
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT:
                        viewBinding.tvMessage.setText(chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE:
                        viewBinding.tvMessage.setText(context.getString(R.string.sent_an_image));
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_COMMUNITY:
                        viewBinding.tvMessage.setText(context.getString(R.string.shared_a_tag));
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_PRODUCT:
                        viewBinding.tvMessage.setText(context.getString(R.string.shared_a_product));
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RESERVE_ITEM:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.reserved_the_item) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RELEASE_PAYMENT:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.released_payment_for_the_item) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.requested_a_refund_for_the_item) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_DECLINE:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.declined_the_refund_request_for_the_item) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_ACCEPT:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.accepted_the_refund_request_for_the_item) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_RELEASE:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.released_the_refund_for_the_item) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.opened_a_dispute_for_the_lack_of_payment) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE_RESPONSE:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.submitted_a_response_for_the_lack_payment_dispute) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.opened_a_dispute_for_the_declined_refund_request) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE_RESPONSE:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.submitted_a_return_dispute_for_the_declined_refund_request) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.opened_a_dispute_for_the_refund_request) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE_RESPONSE:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.submitted_a_response_to_the_refund_request_dispute) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_OPEN_DISPUTE:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.cancelled_the_payment_dispute) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_REQUEST:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.cancelled_the_refund_request) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_DISPUTE:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.cancelled_the_declined_refund_request_dispute) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                    case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_ACCEPT_DISPUTE:
                        viewBinding.tvMessage.setText("" + chatModel.getLastMessage().getSenderName() + " " + context.getString(R.string.cancelled_the_refund_request_dispute) + " " + chatModel.getLastMessage().getMessageText());
                        break;
                }

            if (chatModel.getChatType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT)) {
                if (chatModel.getLastMessage() != null && chatModel.getLastMessage().getMessageId() != null)
                    switch (chatModel.getLastMessage().getMessageType()) {
                        case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT:
                        case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE:
                        case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_COMMUNITY:
                        case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_PRODUCT:
                            break;
                        default:
                            if (membersHashMap.containsKey(chatModel.getLastMessage().getMessageText())) {
                                updateHeaders(viewBinding, chatModel.getLastMessage());
                            } else if (!listenerModelHashMap.containsKey(chatModel.getLastMessage().getMessageText())) {
                                Query query = DataManager.getInstance().getUserNodeQuery(chatModel.getLastMessage().getMessageText());
                                ValueEventListener valueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                            if (loginFirebaseModel != null) {
                                                if (membersHashMap.containsKey(chatModel.getLastMessage().getMessageText())) {
                                                    LoginFirebaseModel oldModel = membersHashMap.get(chatModel.getLastMessage().getMessageText());
                                                    if (!oldModel.getFullName().equalsIgnoreCase(loginFirebaseModel.getFullName()) || !oldModel.getProfilePicture().equalsIgnoreCase(loginFirebaseModel.getProfilePicture())) {
                                                        membersHashMap.put(chatModel.getLastMessage().getMessageText(), loginFirebaseModel);
                                                        updateHeaders(viewBinding, chatModel.getLastMessage());
                                                        notifyDataSetChanged();
                                                    }
                                                } else {
                                                    membersHashMap.put(chatModel.getLastMessage().getMessageText(), loginFirebaseModel);
                                                    updateHeaders(viewBinding, chatModel.getLastMessage());
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };
                                query.addValueEventListener(valueEventListener);
                                RemoveFirebaseListenerModel removeFirebaseListenerModel = new RemoveFirebaseListenerModel();
                                removeFirebaseListenerModel.setValueEventListener(valueEventListener);
                                removeFirebaseListenerModel.setQuery(query);
                                listenerModelHashMap.put(chatModel.getLastMessage().getMessageText(), removeFirebaseListenerModel);
                            }
                            break;
                    }
            } else {
                if (membersHashMap.containsKey(chatModel.getOtherUserId())) {
                    updateUserData(viewBinding, chatModel);
                } else if (!listenerModelHashMap.containsKey(chatModel.getOtherUserId())) {
                    Query query = DataManager.getInstance().getUserNodeQuery(chatModel.getOtherUserId());
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                if (loginFirebaseModel != null) {
                                    if (membersHashMap.containsKey(chatModel.getOtherUserId())) {
                                        LoginFirebaseModel oldModel = membersHashMap.get(chatModel.getOtherUserId());
                                        if (!oldModel.getFullName().equalsIgnoreCase(loginFirebaseModel.getFullName()) || !oldModel.getProfilePicture().equalsIgnoreCase(loginFirebaseModel.getProfilePicture())) {
                                            membersHashMap.put(chatModel.getOtherUserId(), loginFirebaseModel);
                                            updateUserData(viewBinding, chatModel);
                                            notifyDataSetChanged();
                                        }
                                    } else {
                                        membersHashMap.put(chatModel.getOtherUserId(), loginFirebaseModel);
                                        updateUserData(viewBinding, chatModel);
                                    }
                                    DataManager.getInstance().updateGroupDataOnRoomNode(user.getUserId(), chatModel.getRoomId(), loginFirebaseModel.getFullName(), loginFirebaseModel.getProfilePicture());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    query.addValueEventListener(valueEventListener);
                    RemoveFirebaseListenerModel removeFirebaseListenerModel = new RemoveFirebaseListenerModel();
                    removeFirebaseListenerModel.setValueEventListener(valueEventListener);
                    removeFirebaseListenerModel.setQuery(query);
                    listenerModelHashMap.put(chatModel.getOtherUserId(), removeFirebaseListenerModel);
                }
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_swipe:
                case R.id.ll_main:
                    onClickListener.onClick(chatInboxHashmap.get(chatInboxHashmap.getKeyValue(getAdapterPosition())), view);
                    break;
            }
        }
    }

    /**
     * used to update the header type views in the adapter
     *
     * @param rowMessagesHeaderBinding view binding for that row
     * @param messageModel             message data for the header
     */
    private void updateHeaders(RowChatMessagesListBinding rowMessagesHeaderBinding, final MessageModel messageModel) {
        String userId = DataManager.getInstance().getUserDetails().getUserId();
        final LoginFirebaseModel loginFirebaseModel = membersHashMap.get(messageModel.getMessageText());
        switch (messageModel.getMessageType()) {
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TAG_CREATED_HEADER:
                rowMessagesHeaderBinding.tvMessage.setText(AppUtils.getSpannableString(context, userId.equalsIgnoreCase(messageModel.getMessageText()) ? context.getString(R.string.you) : loginFirebaseModel.getFullName(), R.color.colorAccent, 1f, false, true, false, null));
                rowMessagesHeaderBinding.tvMessage.append(" ");
                rowMessagesHeaderBinding.tvMessage.append(context.getString(R.string.created_the_tag));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_USER_JOIN_HEADER:
                rowMessagesHeaderBinding.tvMessage.setText(AppUtils.getSpannableString(context, userId.equalsIgnoreCase(messageModel.getMessageText()) ? context.getString(R.string.you) : loginFirebaseModel.getFullName(), R.color.colorAccent, 1f, false, true, false, null));
                rowMessagesHeaderBinding.tvMessage.append(" ");
                rowMessagesHeaderBinding.tvMessage.append(context.getString(R.string.joined_the_tag));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_USER_REMOVED_HEADER:
                rowMessagesHeaderBinding.tvMessage.setText(AppUtils.getSpannableString(context, userId.equalsIgnoreCase(messageModel.getMessageText()) ? context.getString(R.string.you) : loginFirebaseModel.getFullName(), R.color.colorAccent, 1f, false, true, false, null));
                rowMessagesHeaderBinding.tvMessage.append(" ");
                rowMessagesHeaderBinding.tvMessage.append(context.getString(R.string.removed_from_the_tag));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_USER_LEFT_HEADER:
                rowMessagesHeaderBinding.tvMessage.setText(AppUtils.getSpannableString(context, userId.equalsIgnoreCase(messageModel.getMessageText()) ? context.getString(R.string.you) : loginFirebaseModel.getFullName(), R.color.colorAccent, 1f, false, true, false, null));
                rowMessagesHeaderBinding.tvMessage.append(" ");
                rowMessagesHeaderBinding.tvMessage.append(context.getString(R.string.left_the_tag));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_OWNERSHIP_TRANSFER_HEADER:
                rowMessagesHeaderBinding.tvMessage.setText(context.getResources().getString(R.string.ownership_transfer_to));
                rowMessagesHeaderBinding.tvMessage.append(" ");
                rowMessagesHeaderBinding.tvMessage.append(AppUtils.getSpannableString(context, userId.equalsIgnoreCase(messageModel.getMessageText()) ? context.getString(R.string.you) : loginFirebaseModel.getFullName(), R.color.colorAccent, 1.2f, false, true, false, null));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RESERVE_ITEM:
                rowMessagesHeaderBinding.tvMessage.setText("" + messageModel.getSenderName() + " " + context.getString(R.string.reserved_the_item) + " " + messageModel.getMessageText());
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RELEASE_PAYMENT:
                rowMessagesHeaderBinding.tvMessage.setText("" + messageModel.getSenderName() + " " + context.getString(R.string.released_payment_for_the_item) + " " + messageModel.getMessageText());
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND:
                rowMessagesHeaderBinding.tvMessage.setText("" + messageModel.getSenderName() + " " + context.getString(R.string.requested_a_refund_for_the_item) + " " + messageModel.getMessageText());
                break;
        }
    }

    /**
     * used to update the header type views in the adapter
     *
     * @param viewBinding view binding for that row
     * @param chatModel   message data for the user
     */
    private void updateUserData(RowChatMessagesListBinding viewBinding, final ChatModel chatModel) {
        LoginFirebaseModel loginFirebaseModel = membersHashMap.get(chatModel.getOtherUserId());
        chatModel.setRoomImage(loginFirebaseModel.getProfilePicture());
        chatModel.setRoomName(loginFirebaseModel.getFullName());
        AppUtils.loadCircularImage(context, chatModel.getRoomImage(), 300, R.drawable.ic_detail_user_placeholder, viewBinding.ivUser, true);
        viewBinding.tvUserName.setText(chatModel.getRoomName());
    }

    /**
     * used to update and sort the list of inbox data
     */
    public void updateList() {
        List<Map.Entry<String, ChatModel>> entries = new ArrayList<>(chatInboxHashmap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, ChatModel>>() {
            @Override
            public int compare(Map.Entry<String, ChatModel> lhs, Map.Entry<String, ChatModel> rhs) {
                if (rhs.getValue().getLastMessage() == null) {
                    MessageModel messageModel = new MessageModel();
                    messageModel.setTimeStamp(0L);
                    rhs.getValue().setLastMessage(messageModel);
                }
                if (lhs.getValue().getLastMessage() == null) {
                    MessageModel messageModel = new MessageModel();
                    messageModel.setTimeStamp(0L);
                    lhs.getValue().setLastMessage(messageModel);
                }
                return Long.compare((Long) rhs.getValue().getLastMessage().getTimeStampLong()
                        , (Long) lhs.getValue().getLastMessage().getTimeStampLong());
            }
        });
        chatInboxHashmap.clear();
        for (Map.Entry<String, ChatModel> entry : entries) {
            chatInboxHashmap.put(entry.getKey(), entry.getValue());
        }
        chatInboxHashmap.updateIndexes();
        notifyDataSetChanged();
    }

}
