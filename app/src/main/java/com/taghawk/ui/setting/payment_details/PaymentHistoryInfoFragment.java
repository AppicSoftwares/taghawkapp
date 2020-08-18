package com.taghawk.ui.setting.payment_details;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.adapters.AddDocumentAdapter;
import com.taghawk.adapters.PaymentHistoryAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.camera2basic.RecyclerListener;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.CustomReasonToDeclineDialog;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.data.DataManager;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.BottomSheetDisputeBinding;
import com.taghawk.databinding.FragmentPaymentHistoryInfoBinding;
import com.taghawk.firebase.FirebaseManager;
import com.taghawk.gallery_picker.ImagesGallery;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.PaymentHistoryData;
import com.taghawk.model.PaymentHistoryModel;
import com.taghawk.model.PaymentRefundModel;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.request.User;
import com.taghawk.ui.chat.MessagesDetailActivity;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.PermissionUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class PaymentHistoryInfoFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AmazonCallback {

    private FragmentPaymentHistoryInfoBinding mBinding;
    private AddBankAccountViewModel mViewModel;
    private Activity mActivity;
    private ArrayList<PaymentHistoryData> mList;
    private PaymentHistoryAdapter mAdapter;
    private int position;
    private BottomSheetDialog mDialog;
    private AddDocumentAdapter addDocumentAdapter;
    private AmazonS3 mAmazonS3;
    private ArrayList<String> mFileDocumentArrayList;
    private int imageUploadCount = 0;
    private ArrayList<ImageList> mImageList = new ArrayList<>();
    private String reasonTxt;
    private boolean isChatClicked;

    private String submitResponse = "false";
    private int requestCode = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentPaymentHistoryInfoBinding.inflate(inflater);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mActivity = getActivity();
        mBinding.swipe.setOnRefreshListener(this);
        setUpAmazon();
        setUpView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        registerBroadCast();
    }

    private void registerBroadCast() {
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(localReceiver,
                new IntentFilter(AppConstants.BROAD_CAST_PAYMENT_ACTION));
    }

    private void setUpView() {
        mList = new ArrayList<>();
        mFileDocumentArrayList = new ArrayList<>();

        LinearLayoutManager linearLayout = new LinearLayoutManager(mActivity);
        mAdapter = new PaymentHistoryAdapter(mList, this);
        mBinding.rvPaymentHistroy.setLayoutManager(linearLayout);
        mBinding.rvPaymentHistroy.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
    }

    // setup Live Data
    private void setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get(AddBankAccountViewModel.class);
        mViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mViewModel.paymentHistoryLiveData().observe(this, new Observer<PaymentHistoryModel>() {
            @Override
            public void onChanged(@Nullable PaymentHistoryModel paymentHistoryModel) {
                if (paymentHistoryModel != null) {
                    if (mBinding.swipe != null && mBinding.swipe.isRefreshing()) {
                        mBinding.swipe.setRefreshing(false);
                        mList.clear();
                    }
                    mList.addAll(paymentHistoryModel.getPaymentHistoryList());
                    if (mList.size() > 0) {
                        showEmptyPlaceHolder(View.GONE, View.VISIBLE);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        showEmptyPlaceHolder(View.VISIBLE, View.GONE);
                    }
                }
            }
        });
        mViewModel.paymentRefundLiveData().observe(this, new Observer<PaymentRefundModel>() {
            @Override
            public void onChanged(@Nullable PaymentRefundModel paymentRefundModel) {
                getLoadingStateObserver().onChanged(false);
                if (paymentRefundModel != null) {
                    mList.set(position, paymentRefundModel.getmData());
                    mAdapter.notifyDataSetChanged();
//                    switch (paymentRefundModel.getmData().getDeliveryStatus()) {
//                        case AppConstants.PAYMENT_REFUND_STATUS.REFUND_SUCCESS:
//                            sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_RELEASE);
//                            break;
//                        case AppConstants.PAYMENT_REFUND_STATUS.ITEM_DELEVER:
//                        case AppConstants.PAYMENT_REFUND_STATUS.COMPLETED:
//                            sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RELEASE_PAYMENT);
//                            break;
//                        case AppConstants.PAYMENT_REFUND_STATUS.REQUEST_FOR_REFUND:
//                            sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND);
//                            break;
//                        case AppConstants.PAYMENT_REFUND_STATUS.DECLINED:
//                            sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName() + getString(R.string.due_to_the_following_reason) + paymentRefundModel.getmData().getReason(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_DECLINE);
//                            break;
//                        case AppConstants.PAYMENT_REFUND_STATUS.REFUND_ACCEPTED:
//                            sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_ACCEPT);
//                            break;
//                        case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_DONE:
//                            if (!TextUtils.isEmpty(paymentRefundModel.getmData().getRefundAcceptedDate()) && !paymentRefundModel.getmData().getRefundAcceptedDate().equalsIgnoreCase("0"))
//                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE);
//                            else
//                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE);
//                            break;
//                        case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_RESPONSE:
//                            if (!TextUtils.isEmpty(paymentRefundModel.getmData().getRefundAcceptedDate()) && !paymentRefundModel.getmData().getRefundAcceptedDate().equalsIgnoreCase("0"))
//                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName() + getString(R.string.you_will_receive_decision_from_the_admin_within_7_days), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE_RESPONSE);
//                            else
//                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName() + getString(R.string.you_will_receive_decision_from_the_admin_within_7_days), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE_RESPONSE);
//                            break;
//                        case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_STARTED:
//                            sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE);
//                            break;
//                        case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_RESPONSE:
//                            sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName() + getString(R.string.you_will_receive_decision_from_the_admin_within_7_days), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE_RESPONSE);
//                            break;
//                    }
                }
            }
        });

        mViewModel.getCancelDisputeLiveData().observe(this, new Observer<PaymentRefundModel>() {
            @Override
            public void onChanged(@Nullable PaymentRefundModel paymentRefundModel) {
                getLoadingStateObserver().onChanged(false);
                if (paymentRefundModel != null && paymentRefundModel.getCode() == 200) {
//                   switch (paymentRefundModel.getRequestCode()) {
//                        case AppConstants.REQUEST_CODE.CANCEL_OPEN_DISPUTE:
//                            sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_OPEN_DISPUTE);
//                             break;
//                        case AppConstants.REQUEST_CODE.CANCEL_REFUND_REQUEST:
//                            sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_REQUEST);
//                                break;
//                        case AppConstants.REQUEST_CODE.CANCEL_REFUND_DISPUTE:
//                            sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_DISPUTE);
//                            break;
//                        case AppConstants.REQUEST_CODE.CANCEL_REFUND_ACCEPT_DISPUTE:
//                            sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_ACCEPT_DISPUTE);
//                          break;
//                    }
                }
            }
        });

        //get payment history service
        getPaymentHistory();
    }

//    private void sendMessage(String messageId, String text, String messageType) {
//        ChatModel chatModel = getDefaultChatModel();
//        chatModel.setLastMessage(getMessageModel(messageId, text, messageType));
//        messagesDetailViewModel.sendMessageToUser(user, isNewChat, isOtherUserCreated, user.getFullName() + " " + getString(R.string.send_a_message), chatModel.getLastMessage().getMessageText(), chatModel, null);
//        if (messageType.equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT))
//            fragmentMessagesDetailBinding.etMessage.setText("");
//    }
    /**
     * used to get the default chat model for sending message
     */
//    private ChatModel getDefaultChatModel() {
//        ChatModel chatModel = new ChatModel();
//        chatModel.setChatMute(false);
//        chatModel.setPinned(false);
//        chatModel.setCreatedTimeStamp(ServerValue.TIMESTAMP);
//        chatModel.setChatType(currentChatModel.getChatType());
//        chatModel.setOtherUserId(currentChatModel.getOtherUserId());
//        chatModel.setRoomName(currentChatModel.getRoomName());
//        chatModel.setRoomImage(currentChatModel.getRoomImage());
//        chatModel.setUserType(currentChatModel.getUserType());
//        ChatProductModel chatProductModel = new ChatProductModel();
//        chatProductModel.setProductId(currentChatModel.getProductInfo().getProductId());
//        chatProductModel.setProductImage(currentChatModel.getProductInfo().getProductImage());
//        chatProductModel.setProductName(currentChatModel.getProductInfo().getProductName());
//        chatProductModel.setProductPrice(currentChatModel.getProductInfo().getProductPrice());
//        chatModel.setProductInfo(chatProductModel);
//        chatModel.setRoomId(roomId);
//        return chatModel;
//    }


    private void getPaymentHistory() {
        if (AppUtils.isInternetAvailable(mActivity)) {
            mViewModel.payementHistory();
        } else
            showNoNetworkError();
    }

    private void showEmptyPlaceHolder(int gone, int visible) {
        mBinding.llEmptyPlaceHolder.setVisibility(gone);
        mBinding.rvPaymentHistroy.setVisibility(visible);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_main:
                if (mList != null && mList.size() > 0) {
                    openProductDetails(v);
                }
                break;
            case R.id.tv_release_payment:
                position = (int) v.getTag();
                if (mList != null && mList.size() > 0) {
                    if (mList.get(position).getSellerId().equalsIgnoreCase(DataManager.getInstance().getUserDetails().getUserId())) {
                        if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.REQUEST_FOR_REFUND)) {
                            acceptReturnRequest();//accept refund
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.REFUND_ACCEPTED)) {
                            confirmProductReceivedBySeller();//release refund
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_STARTED)
                                || mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_RESPONSE)) {
                            acceptReturnRequest();//accept refund
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.REFUND_DISPUTE_CAN_START)) {
                            submitResponse = "false";
                            requestCode = AppConstants.REQUEST_CODE.OPEN_A_DISPUTE;
                            submitDispute();
                        }
                    } else {
                        if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.PENDING)
                        || mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_CAN_START)
                        || mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.REFUND_DISPUTE_CAN_START)) {
                            confirmItemAndReleasePayment(getString(R.string.confirm), getString(R.string.release_payment_msg), getString(R.string.confirm), getString(R.string.cancel));//release payment
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.DECLINED)) {
                            confirmItemAndReleasePayment(getString(R.string.cancel_refund_request), getString(R.string.do_you_want_to_cancel_the_refund_request), getString(R.string.confirm), getString(R.string.cancel));
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.REQUEST_FOR_REFUND)) {
                            cancelDispute(getString(R.string.cancel_dispute), getString(R.string.do_you_want_accept_the_refund_request), getString(R.string.confirm), getString(R.string.cancel), mList.get(position).getId(), "actionCancelRefundRequest", AppConstants.REQUEST_CODE.CANCEL_REFUND_REQUEST);//cancel dispute api
                            // cancel dispute api
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_RESPONSE)) {
                            cancelDispute(getString(R.string.cancel_dispute), getString(R.string.do_you_want_to_cancel_the_dispute_request), getString(R.string.confirm), getString(R.string.cancel), mList.get(position).getId(), "actionCancelRefundDispute", AppConstants.REQUEST_CODE.CANCEL_REFUND_DISPUTE);//cancel dispute api
                            // cancel dispute api
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_DONE)
                        || mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_RESPONSE)) {
                            confirmItemAndReleasePayment(getString(R.string.cancel_refund_request), getString(R.string.do_you_want_to_cancel_the_refund_request), getString(R.string.confirm), getString(R.string.cancel));
                        }
                    }
                }
                break;
            case R.id.tv_refund:
                position = (int) v.getTag();
                if (mList != null && mList.size() > 0) {
                    if (mList.get(position).getSellerId().equalsIgnoreCase(DataManager.getInstance().getUserDetails().getUserId())) {
                        if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.REQUEST_FOR_REFUND)) {
                            declineRetrunRequestSeller();
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_STARTED)) {
                            submitResponse = "true";
                            requestCode = AppConstants.REQUEST_CODE.REFUND_DECLINE_DISPUTE_RESPONSE;
                            submitDispute();//refund case dispute response
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.REFUND_ACCEPTED)) {
                            declineRetrunRequestSeller();
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_DONE)
                        || mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_RESPONSE)) {

                            if (!TextUtils.isEmpty(mList.get(position).getRefundAcceptedDate()) && !mList.get(position).getRefundAcceptedDate().equalsIgnoreCase("0"))
                                cancelDispute(getString(R.string.cancel_dispute), getString(R.string.do_you_want_to_cancel_the_dispute_request), getString(R.string.confirm), getString(R.string.cancel), mList.get(position).getId(), "actionCancelRefundAcceptDispute", AppConstants.REQUEST_CODE.CANCEL_REFUND_ACCEPT_DISPUTE);//cancel dispute api
                            else if (!TextUtils.isEmpty(mList.get(position).getDeclineDate()) && !mList.get(position).getDeclineDate().equalsIgnoreCase("0"))
                                cancelDispute(getString(R.string.cancel_dispute), getString(R.string.do_you_want_to_cancel_the_dispute_request), getString(R.string.confirm), getString(R.string.cancel), mList.get(position).getId(), "actionCancelNoResponseDispute", AppConstants.REQUEST_CODE.CANCEL_OPEN_DISPUTE);//cancel dispute api
                            else
                                cancelDispute(getString(R.string.cancel_dispute), getString(R.string.do_you_want_to_cancel_the_dispute_request), getString(R.string.confirm), getString(R.string.cancel), mList.get(position).getId(), "actionCancelOpenDispute", AppConstants.REQUEST_CODE.CANCEL_OPEN_DISPUTE);//cancel dispute api

                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_CAN_START)) {
                            submitResponse = "false";
                            requestCode = AppConstants.REQUEST_CODE.OPEN_A_DISPUTE;
                            submitDispute();
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.REFUND_DISPUTE_CAN_START)) {
                            submitResponse = "false";
                            requestCode = AppConstants.REQUEST_CODE.REFUND_DECLINE_DISPUTE;
                            submitDispute();
                        }

                    } else {
                        if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.PENDING)
                        || mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_CAN_START))
                            initiateRefundRequest();
                        else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.DECLINED)) {
                            submitResponse = "false";
                            requestCode = AppConstants.REQUEST_CODE.REFUND_DECLINE_DISPUTE;
                            submitDispute();
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_STARTED)) {
                            cancelDispute(getString(R.string.cancel_dispute), getString(R.string.do_you_want_to_cancel_the_dispute_request), getString(R.string.confirm), getString(R.string.cancel), mList.get(position).getId(), "actionCancelRefundDispute", AppConstants.REQUEST_CODE.CANCEL_REFUND_DISPUTE);
                        } else if (mList.get(position).getDeliveryStatus().equalsIgnoreCase(AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_DONE)) {
                            submitResponse = "true";
                            requestCode = AppConstants.REQUEST_CODE.OPEN_DISPUTE_RESPONSE;
                            submitDispute();
                        }
                    }
                }
                break;
            case R.id.iv_chat:
                position = (int) v.getTag();
                openChat();
                break;
        }

    }

    private void openChat() {
        if (!isChatClicked) {
            isChatClicked = true;
            final PaymentHistoryData paymentHistoryData = mList.get(position);
            final String sellerId, profilePic, name;
            final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            if (DataManager.getInstance().getUserDetails().getUserId().equalsIgnoreCase(paymentHistoryData.getSellerId())) {
                sellerId = paymentHistoryData.getUserId();
                profilePic = paymentHistoryData.getBuyerPic();
                name = paymentHistoryData.getBuyerName();
            } else {
                sellerId = paymentHistoryData.getSellerId();
                profilePic = paymentHistoryData.getSellerpic();
                name = paymentHistoryData.getSellerName();
            }
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(FirebaseManager.getFirebaseRoomId(user.getUserId(), sellerId)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        chatModel.setRoomName(name);
                        chatModel.setRoomImage(profilePic);
                        chatModel.setChatType(AppConstants.FIREBASE.FIREBASE_SINGLE_CHAT);
                        chatModel.setOtherUserId(sellerId);
                        chatModel.setRoomId(FirebaseManager.getFirebaseRoomId(user.getUserId(), paymentHistoryData.getSellerId()));
                    }
                    ChatProductModel chatProductModel = new ChatProductModel();
                    chatProductModel.setProductId(paymentHistoryData.getProductId());
                    chatProductModel.setProductName(paymentHistoryData.getProductName());
                    chatProductModel.setProductPrice(Double.parseDouble(paymentHistoryData.getPrice()));
                    chatProductModel.setProductImage((paymentHistoryData.getImageLists() != null && paymentHistoryData.getImageLists().size() > 0) ? paymentHistoryData.getImageLists().get(0).getThumbUrl() : "");
                    chatModel.setProductInfo(chatProductModel);
                    startActivity(new Intent(mActivity, MessagesDetailActivity.class).putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel).putExtra(AppConstants.FIREBASE.TIMESTAMP, chatModel.getCreatedTimeStampLong()));
                    isChatClicked = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    isChatClicked = false;
                }
            });
        }
    }

    private void declineRetrunRequestSeller() {
        new CustomReasonToDeclineDialog(mActivity, new OnDialogViewClickListener() {
            @Override
            public void onSubmit(String txt, int id) {
                if (AppUtils.isInternetAvailable(mActivity)) {
                    mViewModel.declineRetrunRequest(mList.get(position).getId(), txt);
                } else {
                    showNoNetworkError();
                }

            }
        }).show();
    }

    private void cancelDispute(String title, String msg, String positiveButton, String negativeButton, String orderId, String action, int requestCode) {
        DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, title, msg, positiveButton, negativeButton, new DialogCallback() {
            @Override
            public void submit(String data) {
                if (AppUtils.isInternetAvailable(mActivity)) {
                    mViewModel.cancelDisputeApi(orderId, action, requestCode);
                } else {
                    showNoNetworkError();
                }

            }

            @Override
            public void cancel() {

            }
        });
    }

    private void openProductDetails(View v) {
        try {
            position = (int) v.getTag();
            Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
            intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, mList.get(position).getProductId());
            startActivity(intent);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    // Buyer release Payment to seller
    private void confirmItemAndReleasePayment(String title, String msg, String positiveButton, String negativeButton) {
        DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, title, msg, positiveButton, negativeButton, new DialogCallback() {
            @Override
            public void submit(String data) {
                if (AppUtils.isInternetAvailable(mActivity)) {
                    mViewModel.confirmItemReceived(mList.get(position).getId());
                } else {
                    showNoNetworkError();
                }

            }

            @Override
            public void cancel() {

            }
        });
    }

    private void initiateRefundRequest() {
        DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.refund_request), getString(R.string.refund_msg), getString(R.string.refund), getString(R.string.cancel), new DialogCallback() {
            @Override
            public void submit(String data) {

                if (AppUtils.isInternetAvailable(mActivity)) {
                    mViewModel.initiateRefundRequest(mList.get(position).getId());
                } else {
                    showNoNetworkError();
                }

            }

            @Override
            public void cancel() {

            }
        });
    }

    //Function is for accept return raise by buyer
    private void acceptReturnRequest() {
        DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.refund_request), getString(R.string.refund_accept_msg), getString(R.string.accept), getString(R.string.cancel), new DialogCallback() {
            @Override
            public void submit(String data) {

                if (AppUtils.isInternetAvailable(mActivity)) {
                    mViewModel.returnRequestAccept(mList.get(position).getId());
                } else {
                    showNoNetworkError();
                }

            }

            @Override
            public void cancel() {

            }
        });
    }

    //Function is for confirm  return product received by seller
    private void confirmProductReceivedBySeller() {
        DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.refund_request), getString(R.string.confirm_return_seller_msg), getString(R.string.confirm), getString(R.string.cancel), new DialogCallback() {
            @Override
            public void submit(String data) {

                if (AppUtils.isInternetAvailable(mActivity)) {
                    mViewModel.confirmReturnItemReceivedSeller(mList.get(position).getId());
                } else {
                    showNoNetworkError();
                }

            }

            @Override
            public void cancel() {

            }
        });
    }

    @SuppressLint("WrongConstant")
    public void submitDispute() {
        mDialog = new BottomSheetDialog(mActivity);
        final BottomSheetDisputeBinding binding = BottomSheetDisputeBinding.inflate(LayoutInflater.from(mActivity));
        mDialog.setContentView(binding.getRoot());
        mDialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(mActivity.getResources().getColor(android.R.color.transparent));
        binding.rvAddDocumentImages.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayout.HORIZONTAL, false));
        addDocumentAdapter = new AddDocumentAdapter(mActivity, mFileDocumentArrayList, new RecyclerListener() {
            @Override
            public void onItemClick(View v, int position, String number, boolean flag) {
                mFileDocumentArrayList.remove(position);
                addDocumentAdapter.notifyDataSetChanged();
            }
        });
        binding.rvAddDocumentImages.setAdapter(addDocumentAdapter);
        binding.ivAddProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 301)) {
                    Intent intent = new Intent(mActivity, ImagesGallery.class);
                    intent.putExtra("selectedList", (Serializable) mFileDocumentArrayList);
                    intent.putExtra("title", "Select Image");
                    intent.putExtra("maxSelection", 5); // Optional
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.MULTIPLE_IMAGE_INTENT);
                }
            }
        });
        binding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reasonTxt = binding.etReason.getText().toString();
                if (reasonTxt != null && reasonTxt.trim().length() > 0) {

                    if (AppUtils.isInternetAvailable(mActivity)) {
                        if (mFileDocumentArrayList != null && mFileDocumentArrayList.size() > 0) {
                            for (int i = 0; i < mFileDocumentArrayList.size(); i++) {
                                startUpload(mFileDocumentArrayList.get(i));
                            }
                        } else {
                            mViewModel.initiatDispute(mList.get(position).getId(), reasonTxt, mImageList, submitResponse, requestCode);
                        }
                    } else {
                        showNoNetworkError();
                    }
                    mDialog.dismiss();

                } else {
                    showToastShort(getString(R.string.please_enter_reason_for_dispute));
                }
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    @Override
    public void onRefresh() {
        getPaymentHistory();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case AppConstants.REQUEST_CODE.MULTIPLE_IMAGE_INTENT:
                if (data.getStringArrayListExtra("result") != null) {
                    mFileDocumentArrayList.clear();
                    ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                    mFileDocumentArrayList.addAll(selectionResult);
                    addDocumentAdapter.notifyDataSetChanged();
                }
                break;

        }

    }

    private ImageBean addDataInBean(String path, boolean isMultiple) {
        ImageBean bean = new ImageBean();
        bean.setId("1");
        bean.setName("sample");
        bean.setImagePath(path);
        bean.setMultiple(isMultiple);
        return bean;
    }

    // Start Uploading image to Amazon Server
    private void startUpload(String path) {
        getLoadingStateObserver().onChanged(true);
        ImageBean bean = addDataInBean(path, false);
        mAmazonS3.uploadImage(bean);
    }

    // Initilize the Amazon S3
    public void setUpAmazon() {
        mAmazonS3 = mAmazonS3.getInstance(mActivity, this, AppConstants.AMAZON_S3.AMAZON_POOLID, AppConstants.AMAZON_S3.BUCKET, AppConstants.AMAZON_S3.AMAZON_SERVER_URL, AppConstants.AMAZON_S3.END_POINT);
    }

    @Override
    public void uploadSuccess(ImageBean bean) {
        getLoadingStateObserver().onChanged(false);
        imageUploadCount++;
        ImageList imageList = new ImageList();
        imageList.setUrl(bean.getServerUrl());
        imageList.setThumbUrl(bean.getServerUrl());
        imageList.setType(bean.getType());
        mImageList.add(imageList);
        if (imageUploadCount == this.mFileDocumentArrayList.size()) {
            imageUploadCount = 0;
            mViewModel.initiatDispute(mList.get(position).getId(), reasonTxt, mImageList, submitResponse, requestCode);
            if (mDialog != null)
                mDialog.dismiss();
            mDialog.dismiss();
            mFileDocumentArrayList.clear();
            reasonTxt = "";
        }

    }

    @Override
    public void uploadFailed(ImageBean bean) {
        imageUploadCount++;
        if (imageUploadCount == this.mFileDocumentArrayList.size()) {
            imageUploadCount = 0;
            mViewModel.initiatDispute(mList.get(position).getId(), reasonTxt, mImageList, submitResponse, requestCode);
            if (mDialog != null)
                mDialog.dismiss();
            mFileDocumentArrayList.clear();
        }
    }

    @Override
    public void uploadProgress(ImageBean bean) {

    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {

    }

    BroadcastReceiver localReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(AppConstants.KEY_CONSTENT.PRODUCT_ID)) {
                String producId = intent.getExtras().getString(AppConstants.KEY_CONSTENT.PRODUCT_ID);
                mViewModel.checkProductStatus(producId);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(localReceiver);
    }
}
