package com.taghawk.ui.shipping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bluesnap.androidapi.services.BlueSnapService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.bluesnap.BlueSnapPaymentCardActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.FragamentShippingStepTwoBinding;
import com.taghawk.firebase.FirebaseManager;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentStatusRequest;
import com.taghawk.model.cart.CartDataBean;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.request.User;
import com.taghawk.model.strip.FedexRateResponse;
import com.taghawk.ui.chat.MessagesDetailActivity;
import com.taghawk.ui.chat.MessagesDetailViewModel;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class ShippingFragmentStepTwo extends BaseFragment implements View.OnClickListener {

    private FragamentShippingStepTwoBinding mBinding;
    private Activity mActivity;
    private HashMap<String, Object> parms;
    private CartDataBean mCartData;
    private ShippingViewModel mShippingViewModel;
    private MessagesDetailViewModel messagesDetailViewModel;
    private double totalAmount;

    private BlueSnapService bluesnapService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragamentShippingStepTwoBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        getArgumentsData();
        setViewModel();

    }

    private void setViewModel() {
        messagesDetailViewModel = ViewModelProviders.of(this).get(MessagesDetailViewModel.class);
        mShippingViewModel = ViewModelProviders.of(this).get(ShippingViewModel.class);
        mShippingViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mShippingViewModel.getmShippingRateCalucalateLiveData().observe(this, new Observer<FedexRateResponse>() {
            @Override
            public void onChanged(@Nullable FedexRateResponse commonResponse) {
//                showToastShort(commonResponse.getMessage());
                getLoadingStateObserver().onChanged(false);
                mBinding.tvShippingCharges.setText("$" + commonResponse.getFedexRateData().getRateBean().getAmount());
                mBinding.tvTotal.setText("$" + setTotalProce(commonResponse.getFedexRateData().getRateBean().getAmount(), mCartData.getProductPrice()));
            }
        });
        mShippingViewModel.getmPaymentLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable final CommonResponse commonResponse) {
                if (commonResponse != null && commonResponse.getCode() == 200) {
                    final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
                    final String roomId = FirebaseManager.getFirebaseRoomId(user.getUserId(), mCartData.getSellerId());
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(mCartData.getSellerId()).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshotOtherUser) {
                                    ChatProductModel chatProductModel = new ChatProductModel();
                                    chatProductModel.setProductId(mCartData.getProductId());
                                    chatProductModel.setProductName(mCartData.getProductName());
                                    chatProductModel.setProductPrice(Double.parseDouble(mCartData.getProductPrice()));
                                    if (mCartData.getProductPicList() != null && mCartData.getProductPicList().size() > 0)
                                        chatProductModel.setProductImage(mCartData.getProductPicList().get(0));
                                    else
                                        chatProductModel.setProductImage("");
                                    boolean isOtherUserCreated, isNewChat;
                                    final ChatModel chatModel;
                                    if (dataSnapshotOtherUser.exists())
                                        isOtherUserCreated = true;
                                    else
                                        isOtherUserCreated = false;
                                    if (dataSnapshot.exists()) {
                                        chatModel = dataSnapshot.getValue(ChatModel.class);
                                        isNewChat = false;
                                    } else {
                                        isNewChat = true;
                                        chatModel = new ChatModel();
                                        chatModel.setPinned(false);
                                        chatModel.setChatMute(false);
                                        chatModel.setCreatedTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                                        chatModel.setRoomImage("");
                                        chatModel.setRoomId(FirebaseManager.getFirebaseRoomId(user.getUserId(), mCartData.getSellerId()));
                                        chatModel.setChatType(AppConstants.FIREBASE.FIREBASE_SINGLE_CHAT);
                                        chatModel.setOtherUserId(mCartData.getSellerId());
                                    }
                                    if (isOtherUserCreated && !isNewChat)
                                        messagesDetailViewModel.updateProductInfo(user.getUserId(), mCartData.getSellerId(), roomId, chatProductModel);
                                    chatModel.setProductInfo(chatProductModel);
                                    MessageModel lastMessage = new MessageModel();
                                    lastMessage.setMessageId(databaseReference.push().getKey());
                                    lastMessage.setMessageText(/*getString(R.string.reserved_the_item) + */" " + mCartData.getProductName());
                                    lastMessage.setMessageStatus(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_DELIVERED);
                                    lastMessage.setMessageType(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT);
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
                                    chatModel.setLastMessage(lastMessage);
                                    messagesDetailViewModel.sendMessageToUser(user, isNewChat, isOtherUserCreated, user.getFullName() + " " + getString(R.string.send_a_message), chatModel.getLastMessage().getMessageText(), chatModel, null);
                                    getLoadingStateObserver().onChanged(false);
                                    getCustomBottomDialog(mActivity.getString(R.string.congratulations_title), commonResponse.getMessage(), new OnDialogItemClickListener() {
                                        @Override
                                        public void onPositiveBtnClick() {
                                            startActivity(new Intent(mActivity, MessagesDetailActivity.class).putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel).putExtra(AppConstants.FIREBASE.TIMESTAMP, chatModel.getCreatedTimeStampLong()));
                                            mActivity.finish();
                                        }

                                        @Override
                                        public void onNegativeBtnClick() {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    getLoadingStateObserver().onChanged(false);
                                    getCustomBottomDialog(mActivity.getString(R.string.congratulations_title), commonResponse.getMessage(), new OnDialogItemClickListener() {
                                        @Override
                                        public void onPositiveBtnClick() {
                                            Intent intent = new Intent(mActivity, HomeActivity.class);
                                            startActivity(intent);
                                            mActivity.finish();
                                        }

                                        @Override
                                        public void onNegativeBtnClick() {

                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            getLoadingStateObserver().onChanged(false);
                            getCustomBottomDialog(mActivity.getString(R.string.congratulations_title), commonResponse.getMessage(), new OnDialogItemClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    Intent intent = new Intent(mActivity, HomeActivity.class);
                                    startActivity(intent);
                                    mActivity.finish();
                                }

                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                        }
                    });
                }
            }
        });
        if (AppUtils.isInternetAvailable(mActivity))
            mShippingViewModel.calculateFedexRate(getActivity(), mCartData.getProductId(), parms);
        else
            showNoNetworkError();
    }

    private String setTotalProce(double amount, String productPrice) {

        totalAmount = amount + Double.valueOf(productPrice);
        return "" + String.format("%.2f", totalAmount);

    }

    private void getArgumentsData() {
        if (getArguments() != null) {
            parms = (HashMap<String, Object>) getArguments().get("DATA");
            mCartData = (CartDataBean) getArguments().get(AppConstants.BUNDLE_DATA);
            setData(parms, mCartData);
        }
    }

    private void setData(HashMap<String, Object> parms, CartDataBean mCartData) {
        mBinding.includeCart.setCartViewModel(mCartData);
        if (parms.containsKey(AppConstants.KEY_CONSTENT.CONTACT_NAME)) {
            mBinding.tvFullName.setText(parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString());
        }
        if (parms.containsKey(AppConstants.KEY_CONSTENT.STEET1) && parms.containsKey(AppConstants.KEY_CONSTENT.STEET2) && parms.containsKey(AppConstants.KEY_CONSTENT.CITY) && parms.containsKey(AppConstants.KEY_CONSTENT.STATE) && parms.containsKey(AppConstants.KEY_CONSTENT.ZIP_CODE)) {
           if(parms.get(AppConstants.KEY_CONSTENT.STEET2) != null && !TextUtils.isEmpty(parms.get(AppConstants.KEY_CONSTENT.STEET2).toString())) {
               mBinding.tvAddress.setText("" + parms.get(AppConstants.KEY_CONSTENT.STEET1).toString() + ", #" + parms.get(AppConstants.KEY_CONSTENT.STEET2).toString() + ", " + parms.get(AppConstants.KEY_CONSTENT.CITY).toString() + ", " + parms.get(AppConstants.KEY_CONSTENT.STATE).toString() + "\n" + "Zip code: " + parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
           } else {
               mBinding.tvAddress.setText("" + parms.get(AppConstants.KEY_CONSTENT.STEET1).toString() + ", " + parms.get(AppConstants.KEY_CONSTENT.CITY).toString() + ", " + parms.get(AppConstants.KEY_CONSTENT.STATE).toString() + "\n" + "Zip code: " + parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
           }
        }
        if (parms.containsKey(AppConstants.KEY_CONSTENT.PHONE)) {
            mBinding.tvMobile.setText("Mobile : " + parms.get(AppConstants.KEY_CONSTENT.PHONE));
        }
        mBinding.tvTotalMrp.setText("$" + mCartData.getProductPrice());
    }

    private void initView() {
        mActivity = getActivity();
        bluesnapService = BlueSnapService.getInstance();
        parms = new HashMap<>();
        mCartData = new CartDataBean();
        mBinding.tvEdit.setOnClickListener(this);
        mBinding.tvCheckout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit:
                mActivity.onBackPressed();
                break;
            case R.id.tv_checkout:
                if (AppUtils.isInternetAvailable(mActivity)) {
//                    Intent intent = new Intent(mActivity, GooglePayPayment.class);
//                    Log.d("PRICE", "" + totalAmount);
//                    intent.putExtra(AppConstants.KEY_CONSTENT.PRICE, "" + totalAmount);
//                    startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.G_PAY_STRIPE);

                    ArrayList<CartDataBean> mCartList = new ArrayList<>();
                    mCartList.add(mCartData);
                    Intent intent = new Intent(mActivity, BlueSnapPaymentCardActivity.class);
                    intent.putExtra("screen", "shipping");
                    intent.putExtra("totalAmount", setTotalProce(totalAmount, mCartData.getProductPrice()));
                    intent.putExtra("shipToParams", parms);
                    intent.putExtra("cartList", mCartList);
                    startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.BLUE_SNAP_PAY);


                } else
                    showNoNetworkError();
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.BLUE_SNAP_PAY:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        PaymentStatusRequest response = (PaymentStatusRequest) data.getExtras().getSerializable(AppConstants.BUNDLE_DATA);
                        Log.e("amount_get", "" + response.getAmount());


//                        Token token = new Gson().fromJson(rawToken, Token.class);
//                        if (!TextUtils.isEmpty(transactionRequest)) {
                        chargeToken(response);
//                        }
                    }

                }
                break;
        }

    }

    private void chargeToken(PaymentStatusRequest paymentStatusRequest) {
        mShippingViewModel.createLable(mCartData, parms, paymentStatusRequest, totalAmount, AppUtils.getStateSortName(mActivity, parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString()));
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        if (failureResponse != null && failureResponse.getErrorCode() == 400) {
            if (getLoadingStateObserver() != null) {
                getLoadingStateObserver().onChanged(false);
            }
            getCustomBottomDialog(mActivity.getString(R.string.opps), failureResponse.getErrorMessage().toString(), new OnDialogItemClickListener() {
                @Override
                public void onPositiveBtnClick() {
                    if (mActivity != null)
                        mActivity.onBackPressed();
                }

                @Override
                public void onNegativeBtnClick() {

                }
            });
        } else {
            super.onFailure(failureResponse);
        }

    }

}
