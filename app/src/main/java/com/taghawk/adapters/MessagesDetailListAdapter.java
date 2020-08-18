package com.taghawk.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_view.PositionedLinkedHashmap;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.RowMessagesHeaderBinding;
import com.taghawk.databinding.RowMyMessageBinding;
import com.taghawk.databinding.RowOtherUserMessageBinding;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.RemoveFirebaseListenerModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.login.LoginFirebaseModel;
import com.taghawk.model.request.User;
import com.taghawk.util.AppUtils;
import com.taghawk.util.TimeAgo;

import java.util.HashMap;

/**
 * Created by Appinventiv on 23-01-2019.
 */

public class MessagesDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private PositionedLinkedHashmap<String, MessageModel> messagesHashmap;
    private RecyclerViewCallback recyclerViewCallback;
    private String userId;
    private String chatType;
    private HashMap<String, LoginFirebaseModel> membersHashMap;
    public HashMap<String, RemoveFirebaseListenerModel> listenerModelHashMap;
    private OnUserNodeUpdate onUserNodeUpdate;
    private User user;
    private String roomName;

    public MessagesDetailListAdapter(String chatType, String roomName, PositionedLinkedHashmap<String, MessageModel> messagesHashmap, String userId, RecyclerViewCallback recyclerViewCallback) {
        this.messagesHashmap = messagesHashmap;
        this.recyclerViewCallback = recyclerViewCallback;
        this.userId = userId;
        this.roomName = roomName;
        user=DataManager.getInstance().getUserDetails();
        this.chatType = chatType;
        membersHashMap = new HashMap<>();
        listenerModelHashMap = new HashMap<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        switch (viewType) {
            case 1:
                RowOtherUserMessageBinding mBinding = RowOtherUserMessageBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
                return new OtherUserMessageViewHolder(mBinding);
            case 2:
                RowMyMessageBinding rowMyMessageBinding = RowMyMessageBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
                return new MyMessageViewHolder(rowMyMessageBinding);
            case 3:
                RowMessagesHeaderBinding rowMessagesHeaderBinding = RowMessagesHeaderBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
                return new HeaderViewHolder(rowMessagesHeaderBinding);
            default:
                RowMyMessageBinding messageBinding = RowMyMessageBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
                return new MyMessageViewHolder(messageBinding);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String messageId = messagesHashmap.getKeyValue(position);
        MessageModel messageModel = messagesHashmap.get(messageId);
        if (messageModel.getSenderId() == null)
            messageModel.setSenderId(userId);
        return (messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT)
                || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE)
                || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_COMMUNITY)
                || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_PRODUCT)
                || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHELF_PRODUCT))
                ? ((messageModel.getSenderId().equalsIgnoreCase(userId) ? 2 : 1)) : 3;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        String messageId = messagesHashmap.getKeyValue(position);
        final MessageModel messageModel = messagesHashmap.get(messageId);
        if (viewHolder instanceof OtherUserMessageViewHolder)
            ((OtherUserMessageViewHolder) viewHolder).bind(messageModel);
        else if (viewHolder instanceof MyMessageViewHolder)
            ((MyMessageViewHolder) viewHolder).bind(messageModel);
        else if (viewHolder instanceof HeaderViewHolder)
            ((HeaderViewHolder) viewHolder).bind(messageModel);
    }

    @Override
    public int getItemCount() {
        return messagesHashmap.size();
    }

    public void setUserNodeUpdateListener(OnUserNodeUpdate onUserNodeUpdate) {
        this.onUserNodeUpdate = onUserNodeUpdate;
    }

    public interface OnUserNodeUpdate {
        void onUpdate(LoginFirebaseModel loginFirebaseModel);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        RowMessagesHeaderBinding rowMessagesHeaderBinding;

        HeaderViewHolder(RowMessagesHeaderBinding rowMessagesHeaderBinding) {
            super(rowMessagesHeaderBinding.getRoot());
            this.rowMessagesHeaderBinding = rowMessagesHeaderBinding;
            rowMessagesHeaderBinding.tvHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewCallback.onClick(getAdapterPosition(), view);
                }
            });
        }

        public void bind(final MessageModel messageModel) {
            final String id;
            if (messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_PRODUCT_CHANGE_HEADER)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RESERVE_ITEM)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RELEASE_PAYMENT)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_DECLINE)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_ACCEPT)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_RELEASE)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE_RESPONSE)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE_RESPONSE)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE_RESPONSE)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_OPEN_DISPUTE)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_REQUEST)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_DISPUTE)
                    || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_ACCEPT_DISPUTE))
                id = messageModel.getSenderId();
            else
                id = messageModel.getMessageText();
            if (id.equalsIgnoreCase(userId))
            {
                if (!membersHashMap.containsKey(id))
                {
                    LoginFirebaseModel loginFirebaseModel=new LoginFirebaseModel();
                    loginFirebaseModel.setUserId(user.getUserId());
                    loginFirebaseModel.setFullName(user.getFullName());
                    loginFirebaseModel.setProfilePicture(user.getProfilePicture());
                    membersHashMap.put(user.getUserId(),loginFirebaseModel);
                }
                updateHeaders(rowMessagesHeaderBinding, messageModel);
            }
            else if ((messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_DATE_HEADER) || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TIME_HEADER)) || membersHashMap.containsKey(id)) {
                updateHeaders(rowMessagesHeaderBinding, messageModel);
            } else {
                if (!listenerModelHashMap.containsKey(id)) {
                    Query query = DataManager.getInstance().getUserNodeQuery(id);
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                if (loginFirebaseModel != null) {
                                    membersHashMap.put(id, loginFirebaseModel);
                                    updateHeaders(rowMessagesHeaderBinding, messageModel);
                                    if (onUserNodeUpdate != null)
                                        onUserNodeUpdate.onUpdate(loginFirebaseModel);
                                    notifyDataSetChanged();
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
                    listenerModelHashMap.put(id, removeFirebaseListenerModel);
                }
            }
        }
    }

    private void updateHeaders(RowMessagesHeaderBinding rowMessagesHeaderBinding, final MessageModel messageModel) {
        TimeAgo timeAgo = new TimeAgo();
        final LoginFirebaseModel loginFirebaseModel;
        if (messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_PRODUCT_CHANGE_HEADER))
            loginFirebaseModel = membersHashMap.get(messageModel.getSenderId());
        else
            loginFirebaseModel = membersHashMap.get(messageModel.getMessageText());
        rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.GONE);
        switch (messageModel.getMessageType()) {
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_PRODUCT_CHANGE_HEADER:
                rowMessagesHeaderBinding.tvHeader.setText(loginFirebaseModel.getFullName());
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(context.getResources().getString(R.string.switches_the_topic_to_item));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.colorAccent, 1.2f, false, true, false, null));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TAG_CREATED_HEADER:
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, userId.equalsIgnoreCase(messageModel.getMessageText()) ? context.getString(R.string.you) : loginFirebaseModel.getFullName(), R.color.colorAccent, 1f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(context.getString(R.string.created_the_tag));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_USER_JOIN_HEADER:
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, userId.equalsIgnoreCase(messageModel.getMessageText()) ? context.getString(R.string.you) : loginFirebaseModel.getFullName(), R.color.colorAccent, 1f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(context.getString(R.string.joined_the_tag));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_USER_REMOVED_HEADER:
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, userId.equalsIgnoreCase(messageModel.getMessageText()) ? context.getString(R.string.you) : loginFirebaseModel.getFullName(), R.color.colorAccent, 1f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(context.getString(R.string.removed_from_the_tag));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_USER_LEFT_HEADER:
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, userId.equalsIgnoreCase(messageModel.getMessageText()) ? context.getString(R.string.you) : loginFirebaseModel.getFullName(), R.color.colorAccent, 1f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(context.getString(R.string.left_the_tag));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_DATE_HEADER:
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()), R.color.colorAccent, 1f, false, true, false, null));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TIME_HEADER:
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()), R.color.gray_dark, 1.2f, false, true, false, null));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_OWNERSHIP_TRANSFER_HEADER:
                rowMessagesHeaderBinding.tvHeader.setText(context.getResources().getString(R.string.ownership_transfer_to));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, userId.equalsIgnoreCase(messageModel.getMessageText()) ? context.getString(R.string.you) : loginFirebaseModel.getFullName(), R.color.colorAccent, 1.2f, false, true, false, null));
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RESERVE_ITEM:
                //reserve item
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.append(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "Item Reserved", R.color.colorAccent, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.reserved_the_item), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                if(messageModel.getSenderId().equalsIgnoreCase(userId))
                    rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, ". Please release payment to " + roomName + " when the item has been delivered.", R.color.colorAccent, 1.3f, false, false, false, null));
                else
                    rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, ". Please ask " + messageModel.getSenderName() + " to release payment when the item has been delivered.", R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RELEASE_PAYMENT:
                //release payment
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "Payment Released", R.color.colorAccent, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.released_payment_for_the_item), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND:
                //requested for refund
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "[Action Required] Refund Request", R.color.RedDark, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.requested_a_refund_for_the_item), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_DECLINE:
                //refund request decline
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "[Action Required] Refund Request Declined", R.color.RedDark, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.declined_the_refund_request_for_the_item), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_ACCEPT:
                //refund request accept message
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "Refund Request Accepted", R.color.colorAccent, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.accepted_the_refund_request_for_the_item), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;

            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_RELEASE:
                //refund released message
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "Refund Released", R.color.colorAccent, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.released_the_refund_for_the_item), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE:
                //open a dispute
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "[Action Required] Payment Dispute", R.color.RedDark, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.opened_a_dispute_for_the_lack_of_payment), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE_RESPONSE:
                //open dispute response
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "[Action Required] Payment Dispute Response", R.color.RedDark, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.submitted_a_response_for_the_lack_payment_dispute), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE:
                //refund decline time dispute
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "[Action Required] Declined Refund Request Dispute", R.color.RedDark, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.opened_a_dispute_for_the_declined_refund_request), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE_RESPONSE:
                //refund decline time dispute response
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "Declined Refund Request Dispute Response", R.color.RedDark, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.submitted_a_return_dispute_for_the_declined_refund_request), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE:
                //refund accept time dispute, when item not received after wait
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "[Action Required] Refund Request Dispute", R.color.RedDark, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.opened_a_dispute_for_the_refund_request), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE_RESPONSE:
                //refund accept time dispute and add submit response
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "Refund Request Dispute Response", R.color.RedDark, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.submitted_a_response_to_the_refund_request_dispute), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.RedDark, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_OPEN_DISPUTE:
                //cancel open dispute request
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "Payment Dispute Cancelled", R.color.colorAccent, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.cancelled_the_payment_dispute), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_REQUEST:
                //cancel applied refund request
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "Refund Request Cancelled", R.color.colorAccent, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.cancelled_the_refund_request), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_DISPUTE:
                //cancel refund decline time added dispute request
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "Declined Refund Request Dispute Cancelled", R.color.colorAccent, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.cancelled_the_declined_refund_request_dispute), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_ACCEPT_DISPUTE:
                //cancel refund decline time added dispute request
                rowMessagesHeaderBinding.tvHeaderTime.setVisibility(View.VISIBLE);
                rowMessagesHeaderBinding.tvHeaderTime.setText("\n");
                rowMessagesHeaderBinding.tvHeaderTime.setText(timeAgo.getFormattedDate(context, messageModel.getTimeStampLong()));
                rowMessagesHeaderBinding.tvHeader.setText(AppUtils.getSpannableString(context, "Refund Request Dispute Cancelled", R.color.colorAccent, 1.3f, false, true, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getSenderName(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, context.getString(R.string.cancelled_the_refund_request_dispute), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append(" ");
                rowMessagesHeaderBinding.tvHeader.append(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.colorAccent, 1.3f, false, false, false, null));
                rowMessagesHeaderBinding.tvHeader.append("\n");
                break;
        }
    }

    private class OtherUserMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RowOtherUserMessageBinding viewBinding;

        public OtherUserMessageViewHolder(RowOtherUserMessageBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.ibRetry.setOnClickListener(this);
            viewBinding.ivMedia.setOnClickListener(this);
            viewBinding.ivUser.setOnClickListener(this);
            viewBinding.llMain.setOnClickListener(this);
            viewBinding.tvMessage.setOnClickListener(this);
        }

        public void bind(final MessageModel messageModel) {
            viewBinding.tvMessage.setMovementMethod(LinkMovementMethod.getInstance());
            viewBinding.ibRetry.setOnClickListener(this);
            if (membersHashMap.containsKey(messageModel.getSenderId())) {
                LoginFirebaseModel loginFirebaseModel = membersHashMap.get(messageModel.getSenderId());
                if (chatType.equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT)) {
                    viewBinding.tvUserName.setText(loginFirebaseModel.getFullName());
                    viewBinding.tvUserName.setVisibility(View.VISIBLE);
                } else
                    viewBinding.tvUserName.setVisibility(View.GONE);
                AppUtils.loadCircularImage(itemView.getContext(), loginFirebaseModel.getProfilePicture(), 300, R.drawable.ic_detail_user_placeholder, viewBinding.ivUser, true);
            } else {
                Query query = DataManager.getInstance().getUserNodeQuery(messageModel.getSenderId());
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                            if (membersHashMap.containsKey(messageModel.getSenderId())) {
                                LoginFirebaseModel oldModel = membersHashMap.get(messageModel.getSenderId());
                                if (oldModel.getFullName() == null)
                                    oldModel.setFullName("");
                                if (oldModel.getProfilePicture() == null)
                                    oldModel.setProfilePicture("");
                                if (loginFirebaseModel.getFullName() == null)
                                    loginFirebaseModel.setFullName("");
                                if (loginFirebaseModel.getProfilePicture() == null)
                                    loginFirebaseModel.setProfilePicture("");
                                if (!oldModel.getFullName().equalsIgnoreCase(loginFirebaseModel.getFullName()) || !oldModel.getProfilePicture().equalsIgnoreCase(loginFirebaseModel.getProfilePicture())) {
                                    if (onUserNodeUpdate != null)
                                        onUserNodeUpdate.onUpdate(loginFirebaseModel);
                                    membersHashMap.put(messageModel.getSenderId(), loginFirebaseModel);
                                    if (chatType.equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT)) {
                                        viewBinding.tvUserName.setText(loginFirebaseModel.getFullName());
                                        viewBinding.tvUserName.setVisibility(View.VISIBLE);
                                    } else
                                        viewBinding.tvUserName.setVisibility(View.GONE);
                                    AppUtils.loadCircularImage(itemView.getContext(), loginFirebaseModel.getProfilePicture(), 300, R.drawable.ic_detail_user_placeholder, viewBinding.ivUser, true);
                                    notifyDataSetChanged();
                                }
                            } else {
                                membersHashMap.put(messageModel.getSenderId(), loginFirebaseModel);
                                if (chatType.equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT)) {
                                    viewBinding.tvUserName.setText(loginFirebaseModel.getFullName());
                                    viewBinding.tvUserName.setVisibility(View.VISIBLE);
                                } else
                                    viewBinding.tvUserName.setVisibility(View.GONE);
                                AppUtils.loadCircularImage(itemView.getContext(), loginFirebaseModel.getProfilePicture(), 300, R.drawable.ic_detail_user_placeholder, viewBinding.ivUser, true);
                                notifyDataSetChanged();
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
                listenerModelHashMap.put(messageModel.getSenderId(), removeFirebaseListenerModel);
            }

            switch (messageModel.getMessageType()) {
                case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE:
                    viewBinding.flMedia.setVisibility(View.VISIBLE);
                    viewBinding.tvShareIcon.setVisibility(View.GONE);
                    viewBinding.tvShareName.setVisibility(View.GONE);
                    viewBinding.cvMessage.setVisibility(View.GONE);
                    AppUtils.loadCircularImage(context, messageModel.getMessageText(), 20, R.drawable.ic_home_placeholder, viewBinding.ivMedia, true);
                    if (messageModel.getLoadingImageOnAmazon() == null) {
                        viewBinding.viewOverlay.setVisibility(View.GONE);
                        viewBinding.progressBar.setVisibility(View.GONE);
                        viewBinding.ibRetry.setVisibility(View.GONE);
                    } else if (messageModel.getLoadingImageOnAmazon().equalsIgnoreCase("loading")) {
                        viewBinding.viewOverlay.setVisibility(View.VISIBLE);
                        viewBinding.progressBar.setVisibility(View.VISIBLE);
                        viewBinding.ibRetry.setVisibility(View.GONE);
                    } else {
                        viewBinding.viewOverlay.setVisibility(View.VISIBLE);
                        viewBinding.progressBar.setVisibility(View.GONE);
                        viewBinding.ibRetry.setVisibility(View.VISIBLE);
                    }
                    break;
                case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT:
                    viewBinding.flMedia.setVisibility(View.GONE);
                    viewBinding.cvMessage.setVisibility(View.VISIBLE);
                    viewBinding.tvMessage.setText(messageModel.getMessageText());
                    break;
                case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_COMMUNITY:
                    viewBinding.flMedia.setVisibility(View.VISIBLE);
                    viewBinding.cvMessage.setVisibility(View.GONE);
                    AppUtils.loadCircularImage(context, messageModel.getShareImage(), 20, R.drawable.ic_home_placeholder, viewBinding.ivMedia, true);
                    viewBinding.viewOverlay.setVisibility(View.VISIBLE);
                    viewBinding.progressBar.setVisibility(View.GONE);
                    viewBinding.ibRetry.setVisibility(View.GONE);
                    viewBinding.tvShareName.setVisibility(View.VISIBLE);
                    viewBinding.tvShareIcon.setVisibility(View.VISIBLE);
                    viewBinding.tvShareName.setText(messageModel.getMessageText());
                    viewBinding.tvShareIcon.setImageResource(R.drawable.ic_tab_home_active);
                    break;
                case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_PRODUCT:
                    viewBinding.flMedia.setVisibility(View.VISIBLE);
                    viewBinding.cvMessage.setVisibility(View.GONE);
                    AppUtils.loadCircularImage(context, messageModel.getShareImage(), 20, R.drawable.ic_home_placeholder, viewBinding.ivMedia, true);
                    viewBinding.viewOverlay.setVisibility(View.VISIBLE);
                    viewBinding.progressBar.setVisibility(View.GONE);
                    viewBinding.ibRetry.setVisibility(View.GONE);
                    viewBinding.tvShareName.setVisibility(View.VISIBLE);
                    viewBinding.tvShareIcon.setVisibility(View.VISIBLE);
                    viewBinding.tvShareName.setText(messageModel.getMessageText());
                    viewBinding.tvShareIcon.setImageResource(R.drawable.ic_tab_driver_active);
                    break;
                case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHELF_PRODUCT:
                    viewBinding.flMedia.setVisibility(View.GONE);
                    viewBinding.cvMessage.setVisibility(View.VISIBLE);
                    viewBinding.tvMessage.setText(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.colorAccent, 1.2f, false, true, false, null));
                    viewBinding.tvMessage.append(" ");
                    viewBinding.tvMessage.append(context.getString(R.string.is_added_to_the_shelf));
                    break;
            }
            if (messageModel.getMessageStatus().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_PENDING) || chatType.equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT))
                viewBinding.tvTime.setVisibility(View.INVISIBLE);
            else {
                TimeAgo timeAgo = new TimeAgo();
                viewBinding.tvTime.setVisibility(View.VISIBLE);
                viewBinding.tvTime.setText(timeAgo.getFormattedTime(messageModel.getTimeStampLong()));
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_user:
                case R.id.ll_main:
                case R.id.tv_message:
                case R.id.ib_retry:
                case R.id.iv_media:
                    recyclerViewCallback.onClick(getAdapterPosition(), view);
                    break;
            }
        }
    }

    private class MyMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RowMyMessageBinding viewBinding;

        public MyMessageViewHolder(RowMyMessageBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.ibRetry.setOnClickListener(this);
            viewBinding.ivMedia.setOnClickListener(this);
            viewBinding.llMain.setOnClickListener(this);
            viewBinding.tvMessage.setOnClickListener(this);
        }

        public void bind(final MessageModel messageModel) {
            viewBinding.tvMessage.setMovementMethod(LinkMovementMethod.getInstance());
            viewBinding.ibRetry.setOnClickListener(this);
            switch (messageModel.getMessageType()) {
                case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE:
                    viewBinding.flMedia.setVisibility(View.VISIBLE);
                    viewBinding.tvShareIcon.setVisibility(View.GONE);
                    viewBinding.tvShareName.setVisibility(View.GONE);
                    viewBinding.cvMessage.setVisibility(View.GONE);
                    AppUtils.loadCircularImage(context, messageModel.getMessageText(), 20, R.drawable.ic_home_placeholder, viewBinding.ivMedia, true);
                    if (messageModel.getLoadingImageOnAmazon() == null) {
                        viewBinding.viewOverlay.setVisibility(View.GONE);
                        viewBinding.progressBar.setVisibility(View.GONE);
                        viewBinding.ibRetry.setVisibility(View.GONE);
                    } else if (messageModel.getLoadingImageOnAmazon().equalsIgnoreCase("loading")) {
                        viewBinding.viewOverlay.setVisibility(View.VISIBLE);
                        viewBinding.progressBar.setVisibility(View.VISIBLE);
                        viewBinding.ibRetry.setVisibility(View.GONE);
                    } else {
                        viewBinding.viewOverlay.setVisibility(View.VISIBLE);
                        viewBinding.progressBar.setVisibility(View.GONE);
                        viewBinding.ibRetry.setVisibility(View.VISIBLE);
                    }
                    break;
                case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT:
                    viewBinding.flMedia.setVisibility(View.GONE);
                    viewBinding.cvMessage.setVisibility(View.VISIBLE);
                    viewBinding.tvMessage.setText(messageModel.getMessageText());
                    break;
                case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_COMMUNITY:
                    viewBinding.flMedia.setVisibility(View.VISIBLE);
                    viewBinding.cvMessage.setVisibility(View.GONE);
                    AppUtils.loadCircularImage(context, messageModel.getShareImage(), 20, R.drawable.ic_home_placeholder, viewBinding.ivMedia, true);
                    viewBinding.viewOverlay.setVisibility(View.VISIBLE);
                    viewBinding.progressBar.setVisibility(View.GONE);
                    viewBinding.ibRetry.setVisibility(View.GONE);
                    viewBinding.tvShareName.setVisibility(View.VISIBLE);
                    viewBinding.tvShareIcon.setVisibility(View.VISIBLE);
                    viewBinding.tvShareName.setText(messageModel.getMessageText());
                    viewBinding.tvShareIcon.setImageResource(R.drawable.ic_tab_home_active);
                    break;
                case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_PRODUCT:
                    viewBinding.flMedia.setVisibility(View.VISIBLE);
                    viewBinding.cvMessage.setVisibility(View.GONE);
                    AppUtils.loadCircularImage(context, messageModel.getShareImage(), 20, R.drawable.ic_home_placeholder, viewBinding.ivMedia, true);
                    viewBinding.viewOverlay.setVisibility(View.VISIBLE);
                    viewBinding.progressBar.setVisibility(View.GONE);
                    viewBinding.ibRetry.setVisibility(View.GONE);
                    viewBinding.tvShareName.setVisibility(View.VISIBLE);
                    viewBinding.tvShareIcon.setVisibility(View.VISIBLE);
                    viewBinding.tvShareName.setText(messageModel.getMessageText());
                    viewBinding.tvShareIcon.setImageResource(R.drawable.ic_tab_driver_active);
                    break;
                case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHELF_PRODUCT:
                    viewBinding.flMedia.setVisibility(View.GONE);
                    viewBinding.cvMessage.setVisibility(View.VISIBLE);
                    viewBinding.tvMessage.setText(AppUtils.getSpannableString(context, messageModel.getMessageText(), R.color.White, 1.2f, false, true, false, null));
                    viewBinding.tvMessage.append(" ");
                    viewBinding.tvMessage.append(context.getString(R.string.is_added_to_the_shelf));
                    break;
            }
            if (messageModel.getMessageStatus().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_PENDING) || chatType.equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT)) {
                viewBinding.tvTime.setVisibility(View.INVISIBLE);
                viewBinding.ivStatus.setVisibility(View.INVISIBLE);
            } else {
                TimeAgo timeAgo = new TimeAgo();
                viewBinding.tvTime.setVisibility(View.VISIBLE);
                viewBinding.tvTime.setText(timeAgo.getFormattedTime(messageModel.getTimeStampLong()));
                viewBinding.ivStatus.setVisibility(View.VISIBLE);
                if (messageModel.getMessageStatus().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_READ))
                    viewBinding.ivStatus.setImageResource(R.drawable.ic_receive_tick);
                else
                    viewBinding.ivStatus.setImageResource(R.drawable.ic_sending_tick);
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_main:
                case R.id.ib_retry:
                case R.id.tv_message:
                case R.id.iv_media:
                    recyclerViewCallback.onClick(getAdapterPosition(), view);
                    break;
            }
        }
    }


}
