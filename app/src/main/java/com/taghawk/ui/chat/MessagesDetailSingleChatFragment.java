package com.taghawk.ui.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.TagHawkApplication;
import com.taghawk.adapters.AddDocumentAdapter;
import com.taghawk.adapters.ChatProductsSpinnerAdapter;
import com.taghawk.adapters.MessagesDetailListAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.base.NetworkCallback;
import com.taghawk.camera2basic.RecyclerListener;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.CustomReasonToDeclineDialog;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.custom_view.PositionedLinkedHashmap;
import com.taghawk.data.DataManager;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.BottomSheetDisputeBinding;
import com.taghawk.databinding.FragmentMessagesDetailSingleChatBinding;
import com.taghawk.databinding.LayoutSingleChatMessagePopupWindowBinding;
import com.taghawk.firebase.FirebaseChildEventListener;
import com.taghawk.gallery_picker.ImagesGallery;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.interfaces.OnDialogItemObjectClickListener;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentHistoryData;
import com.taghawk.model.PaymentRefundModel;
import com.taghawk.model.RemoveFirebaseListenerModel;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.login.LoginFirebaseModel;
import com.taghawk.model.profileresponse.ProfileProductsResponse;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.model.request.User;
import com.taghawk.model.tag.TagData;
import com.taghawk.ui.home.product_details.MyProductsActivity;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.ui.profile.OtherProfileActivity;
import com.taghawk.ui.setting.payment_details.AddBankAccountViewModel;
import com.taghawk.ui.tag.MyTagsActivity;
import com.taghawk.ui.tag.TagDetailsActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.PermissionUtility;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_CREATE_TRANSACTION;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_PASS;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_REFUND_TRANSACTION;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_URL;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_USER;
import static java.net.HttpURLConnection.HTTP_OK;

public class MessagesDetailSingleChatFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, AmazonCallback, OnDialogItemClickListener {

    private final int CAMERA_REQUEST_CODE = 141;
    private final int GALLERY_REQUEST_CODE = 142;
    private final int SHARE_PRODUCT_REQ_CODE = 143;
    private final int SHARE_TAG_REQ_CODE = 144;

    private MessagesDetailViewModel messagesDetailViewModel;
    private FragmentMessagesDetailSingleChatBinding fragmentMessagesDetailBinding;
    private Activity mActivity;
    private MessagesDetailListAdapter messagesDetailListAdapter;
    private PositionedLinkedHashmap<String, MessageModel> messagesHashmap;
    private PopupWindow popup;
    private ChatModel currentChatModel;
    private User user;
    private FirebaseChildEventListener newMessageEventListener, otherUserChildNodeListener;
    private ValueEventListener roomNodeListener, productInfoListener;
    private String roomId;
    private boolean isNewChat = true, isOtherUserCreated = false;
    private AmazonS3 mAmazonS3;
    private long endIndexTimeStamp, createdTimeStamp;
    private Query messagesQuery, newMessageQuery, otherUserNodeQuery, roomNodeQuery, productInfoQuery;
    private boolean hasMoreData = true, isLoading;
    private ArrayList<ChatProductModel> productList;
    private ChatProductsSpinnerAdapter chatProductsSpinnerAdapter;
    private Uri photoUri;
    private String mCurrentPhotoPath;
    private AddBankAccountViewModel mViewModel;
    private PaymentHistoryData productStatusData;
    BroadcastReceiver localReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(AppConstants.KEY_CONSTENT.PRODUCT_ID) && intent.getExtras().containsKey(AppConstants.KEY_CONSTENT.USER_ID) && intent.getExtras().containsKey(AppConstants.KEY_CONSTENT.SELLER_ID)) {

                String sellerId = null, buyerId = null, producId = null;
                sellerId = intent.getExtras().getString(AppConstants.KEY_CONSTENT.SELLER_ID);
                buyerId = intent.getExtras().getString(AppConstants.KEY_CONSTENT.USER_ID);
                producId = intent.getExtras().getString(AppConstants.KEY_CONSTENT.PRODUCT_ID);
                if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                    ChatProductModel model = productList.get(currentProductSpinnerPosition);
                    if (model.getProductId().equalsIgnoreCase(producId)) {
                        if (user.getUserId().equalsIgnoreCase(sellerId) || currentChatModel.getOtherUserId().equalsIgnoreCase(sellerId)) {
                            if (user.getUserId().equalsIgnoreCase(buyerId) || currentChatModel.getOtherUserId().equalsIgnoreCase(buyerId)) {
                                if (AppUtils.isConnection(mActivity))
                                    getProductStatusApi(producId);
                            }
                        }
                    }

                }
            }

        }
    };
    private BottomSheetDialog mDialog;
    private AddDocumentAdapter addDocumentAdapter;
    private ArrayList<String> mFileDocumentArrayList;
    private String reasonTxt;
    private int lastClickedProductStatusButtonId = 0, currentProductSpinnerPosition = -1;
    private int imageUploadCount;
    private ArrayList<ImageList> mImageList;
    private ChatProductModel roomProductModel;
    private String otherUserId = "";
    private boolean isFirstTimeLoaded = true;
    private LinearLayoutManager linearLayoutManager;

    private String submitResponse = "false";
    private int requestCode = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentMessagesDetailBinding = FragmentMessagesDetailSingleChatBinding.inflate(inflater, container, false);
        initView();
        setMorePopupWindow();
        setMessagesData();
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(localReceiver,
                new IntentFilter(AppConstants.BROAD_CAST_PAYMENT_ACTION));
        return fragmentMessagesDetailBinding.getRoot();
    }

    /**
     * intialize views and variables
     */
    private void initView() {
        mActivity = getActivity();
        mAmazonS3 = AmazonS3.getInstance(mActivity, this, AppConstants.AMAZON_S3.AMAZON_POOLID, AppConstants.AMAZON_S3.BUCKET, AppConstants.AMAZON_S3.AMAZON_SERVER_URL, AppConstants.AMAZON_S3.END_POINT);
//        messagesList = new ArrayList<>();
        messagesHashmap = new PositionedLinkedHashmap<String, MessageModel>();
        productList = new ArrayList<>();
        mImageList = new ArrayList<>();
        mFileDocumentArrayList = new ArrayList<>();
        user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
        currentChatModel = getArguments().getParcelable(AppConstants.FIREBASE.FIREBASE_CHAT_DATA);
        createdTimeStamp = getArguments().getLong(AppConstants.FIREBASE.TIMESTAMP);
        if (getArguments() != null && getArguments().containsKey(AppConstants.FIREBASE.FIREBASE_OTHER_USER_ID)) {
            otherUserId = getArguments().getString(AppConstants.FIREBASE.FIREBASE_OTHER_USER_ID);
            currentChatModel.setOtherUserId(otherUserId);
        }
//        messagesHashmap.put(currentChatModel.getLastMessage().getMessageId(), currentChatModel.getLastMessage());
//        messagesHashmap.addIndex(currentChatModel.getLastMessage().getMessageId());
        endIndexTimeStamp = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
        roomId = currentChatModel.getRoomId();
        chatProductsSpinnerAdapter = new ChatProductsSpinnerAdapter(mActivity, productList);
        fragmentMessagesDetailBinding.spinnerProducts.setAdapter(chatProductsSpinnerAdapter);
        fragmentMessagesDetailBinding.tvToolbarTitle.setText(currentChatModel.getRoomName());
        linearLayoutManager = new LinearLayoutManager(mActivity);
        fragmentMessagesDetailBinding.rvMessages.setLayoutManager(linearLayoutManager);
        ((SimpleItemAnimator) fragmentMessagesDetailBinding.rvMessages.getItemAnimator()).setSupportsChangeAnimations(false);
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
                        Intent i = new Intent(mActivity, OtherProfileActivity.class);
                        i.putExtra(AppConstants.BUNDLE_DATA, messageModel.getSenderId());
                        mActivity.startActivity(i);
                        break;
                    case R.id.iv_media:
                        switch (messageModel.getMessageType()) {
                            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_PRODUCT:
                                Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                                intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, messageModel.getShareId());
                                mActivity.startActivity(intent);
                                break;
                            case AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_COMMUNITY:
                                Intent detailsIntent = new Intent(mActivity, TagDetailsActivity.class);
                                detailsIntent.putExtra("TAG_ID", messageModel.getShareId());
                                mActivity.startActivity(detailsIntent);
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
        messagesDetailListAdapter.setUserNodeUpdateListener(new MessagesDetailListAdapter.OnUserNodeUpdate() {
            @Override
            public void onUpdate(LoginFirebaseModel loginFirebaseModel) {
                currentChatModel.setRoomImage(loginFirebaseModel.getProfilePicture());
                currentChatModel.setRoomName(loginFirebaseModel.getFullName());
                fragmentMessagesDetailBinding.tvToolbarTitle.setText(currentChatModel.getRoomName());
            }
        });
        fragmentMessagesDetailBinding.rvMessages.setAdapter(messagesDetailListAdapter);
        fragmentMessagesDetailBinding.rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) fragmentMessagesDetailBinding.rvMessages.getLayoutManager();
                if (linearLayoutManager != null) {
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int totalCount = linearLayoutManager.getChildCount();
                    if (hasMoreData && !isLoading && firstVisibleItemPosition == 2) {
                        isLoading = true;
                        checkMessages(false);
                    }
                }
            }
        });
        fragmentMessagesDetailBinding.ibSend.setOnClickListener(this);
        fragmentMessagesDetailBinding.ibMore.setOnClickListener(this);
        fragmentMessagesDetailBinding.ibAttachment.setOnClickListener(this);
        fragmentMessagesDetailBinding.ibBack.setOnClickListener(this);
        fragmentMessagesDetailBinding.llMain.setOnClickListener(this);
        fragmentMessagesDetailBinding.btnPositive.setOnClickListener(this);
        fragmentMessagesDetailBinding.btnNegative.setOnClickListener(this);
        fragmentMessagesDetailBinding.tvInfo.setOnClickListener(this);
        KeyboardVisibilityEvent.setEventListener(
                mActivity,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen && (((LinearLayoutManager) fragmentMessagesDetailBinding.rvMessages.getLayoutManager()).findLastVisibleItemPosition() >= messagesHashmap.size() - 4))
                            fragmentMessagesDetailBinding.rvMessages.scrollToPosition(messagesHashmap.size() - 1);
                    }
                });
       /* if (getArguments().getString("tag").equals("1")){
            sendMessage(messagesQuery.getRef().push().getKey(), "this product is reserved", AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT);

        }*/
    }

    /**
     * used to set up the data for the popup window to show on more button click
     */
    private void setMorePopupWindow() {
        final LayoutSingleChatMessagePopupWindowBinding popBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_single_chat_message_popup_window, null, false);
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
        popBinding.tvBlockUser.setOnClickListener(this);
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
        popBinding.tvBlockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.getInstance().CustomUnFollowRemoveBottomSheetDialog(mActivity, getString(R.string.block_msg), currentChatModel.getRoomName(), getString(R.string.block), currentChatModel.getRoomImage(), false, true, new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {
                        if (AppUtils.isConnection(mActivity)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(AppConstants.KEY_CONSTENT.USER_ID, currentChatModel.getOtherUserId());
                            hashMap.put(AppConstants.KEY_CONSTENT.ACTION, 3);
                            getLoadingStateObserver().onChanged(true);
                            messagesDetailViewModel.blockUser(hashMap, currentChatModel.getRoomName(), 1);
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
    }

    /**
     * used to set up all the messages data, observers, live data etc
     */
    private void setMessagesData() {
        //initializing view model
        messagesDetailViewModel = ViewModelProviders.of(this).get(MessagesDetailViewModel.class);
        mViewModel = ViewModelProviders.of(this).get(AddBankAccountViewModel.class);
        mViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        messagesDetailViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        checkIfRoomExists();
        if (AppUtils.isConnection(mActivity)) {
            if (otherUserId != null && otherUserId.length() > 0)
                messagesDetailViewModel.getProfileProductsList(otherUserId);
            else
                messagesDetailViewModel.getProfileProductsList(currentChatModel.getOtherUserId());

        } else if (currentChatModel.getProductInfo().getProductId() != null && !currentChatModel.getProductInfo().getProductId().equalsIgnoreCase("")) {
            productList.add(currentChatModel.getProductInfo());
            chatProductsSpinnerAdapter.notifyDataSetChanged();
            currentProductSpinnerPosition = 0;
            if (AppUtils.isConnection(mActivity))
                getProductStatusApi(currentChatModel.getProductInfo().getProductId());
        } else
            fragmentMessagesDetailBinding.spinnerProducts.setVisibility(View.GONE);
        messagesDetailViewModel.getBlockUserLiveData().observe(this, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(@Nullable ProfileResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse != null && commonResponse.getCode() == 200) {
                    showToastShort(commonResponse.getMessage());
                    newMessageQuery.removeEventListener(newMessageEventListener);
                    otherUserNodeQuery.removeEventListener(otherUserChildNodeListener);
                    messagesDetailViewModel.blockUserOnFirebase(user.getUserId(), currentChatModel.getOtherUserId());
                    Intent intent = new Intent();
                    intent.putExtra(AppConstants.BUNDLE_DATA, true);
                    mActivity.setResult(Activity.RESULT_OK, intent);
                    mActivity.finish();
                }
            }
        });
        messagesDetailViewModel.getNewProductStatusLiveData().observe(this, new Observer<PaymentRefundModel>() {
            @Override
            public void onChanged(@Nullable PaymentRefundModel paymentRefundModel) {
                getLoadingStateObserver().onChanged(false);
                if (paymentRefundModel != null && paymentRefundModel.getCode() == 200) {
                    productStatusData = paymentRefundModel.getmData();
                    if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition >= productList.size() - 1) {
                        ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                        if (chatProductModel.getProductId().equalsIgnoreCase(productStatusData.getProductId()) && productStatusData.getProductStatus() != chatProductModel.getProductStatus()) {
                            switch (productStatusData.getProductStatus()) {
                                case 1:
                                case 5:
                                    productList.get(currentProductSpinnerPosition).setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                    chatProductsSpinnerAdapter.notifyDataSetChanged();
                                    break;
                                case 2:
                                    fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                    chatProductModel.setProductStatus(productStatusData.getProductStatus());
                                    productList.remove(currentProductSpinnerPosition);
                                    productList.add(chatProductModel);
                                    currentProductSpinnerPosition = productList.size() - 1;
                                    fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                    fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                    chatProductsSpinnerAdapter.notifyDataSetChanged();
                                    break;
                            }
                        }
                    }
                    checkProductStatusData();
                }
            }
        });

        mViewModel.paymentRefundLiveData().observe(this, new Observer<PaymentRefundModel>() {
            @Override
            public void onChanged(@Nullable PaymentRefundModel paymentRefundModel) {
                getLoadingStateObserver().onChanged(false);
                if (paymentRefundModel != null && paymentRefundModel.getCode() == 200) {
                    if (currentChatModel.getProductInfo() != null && currentChatModel.getProductInfo().getProductId() != null && currentChatModel.getProductInfo().getProductId().equalsIgnoreCase(paymentRefundModel.getmData().getProductId()))
                        currentChatModel.getProductInfo().setProductStatus(paymentRefundModel.getmData().getProductStatus());
                    switch (paymentRefundModel.getmData().getDeliveryStatus()) {
                        case AppConstants.PAYMENT_REFUND_STATUS.REFUND_SUCCESS://refund released
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_RELEASE);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();

                            }
                            break;
                        case AppConstants.PAYMENT_REFUND_STATUS.ITEM_DELEVER:
                        case AppConstants.PAYMENT_REFUND_STATUS.COMPLETED:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RELEASE_PAYMENT);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                        case AppConstants.PAYMENT_REFUND_STATUS.REQUEST_FOR_REFUND:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                        case AppConstants.PAYMENT_REFUND_STATUS.DECLINED:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName() + " " + getString(R.string.due_to_the_following_reason) + paymentRefundModel.getmData().getReason(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_DECLINE);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                        case AppConstants.PAYMENT_REFUND_STATUS.REFUND_ACCEPTED:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName() + ". Please release refund to " + paymentRefundModel.getmData().getBuyerName() + " when the correct item has been returned.", AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_ACCEPT);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                        case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_DONE:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                if (!TextUtils.isEmpty(paymentRefundModel.getmData().getRefundAcceptedDate()) && !paymentRefundModel.getmData().getRefundAcceptedDate().equalsIgnoreCase("0"))
                                    sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE);
                                else
                                    sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                        case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_RESPONSE:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                if (!TextUtils.isEmpty(paymentRefundModel.getmData().getRefundAcceptedDate()) && !paymentRefundModel.getmData().getRefundAcceptedDate().equalsIgnoreCase("0"))
                                    sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName() + getString(R.string.you_will_receive_decision_from_the_admin_within_7_days), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE_RESPONSE);
                                else
                                    sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName() + getString(R.string.you_will_receive_decision_from_the_admin_within_7_days), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE_RESPONSE);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                        case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_STARTED:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                        case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_RESPONSE:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName() + getString(R.string.you_will_receive_decision_from_the_admin_within_7_days), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE_RESPONSE);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                    }
                    productStatusData = paymentRefundModel.getmData();
                    checkProductStatusData();
                }
            }
        });

        mViewModel.getCancelDisputeLiveData().observe(this, new Observer<PaymentRefundModel>() {
            @Override
            public void onChanged(@Nullable PaymentRefundModel paymentRefundModel) {
                getLoadingStateObserver().onChanged(false);
                if (paymentRefundModel != null && paymentRefundModel.getCode() == 200) {
                    if (currentChatModel.getProductInfo() != null && currentChatModel.getProductInfo().getProductId() != null && currentChatModel.getProductInfo().getProductId().equalsIgnoreCase(paymentRefundModel.getmData().getProductId()))
                        currentChatModel.getProductInfo().setProductStatus(paymentRefundModel.getmData().getProductStatus());

                    switch (paymentRefundModel.getRequestCode()) {
                        case AppConstants.REQUEST_CODE.CANCEL_OPEN_DISPUTE:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_OPEN_DISPUTE);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                        case AppConstants.REQUEST_CODE.CANCEL_REFUND_REQUEST:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_REQUEST);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                        case AppConstants.REQUEST_CODE.CANCEL_REFUND_DISPUTE:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_DISPUTE);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                        case AppConstants.REQUEST_CODE.CANCEL_REFUND_ACCEPT_DISPUTE:
                            if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                                chatProductModel.setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                productList.remove(currentProductSpinnerPosition);
                                productList.add(chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), "" + chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_ACCEPT_DISPUTE);
                                currentProductSpinnerPosition = productList.size() - 1;
                                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                chatProductsSpinnerAdapter.notifyDataSetChanged();
                            }
                            break;
                    }
                    productStatusData = paymentRefundModel.getmData();
                    checkProductStatusData();
                }
            }
        });

        messagesDetailViewModel.profileProductsLiveData().observe(this, new Observer<ProfileProductsResponse>() {
            @Override
            public void onChanged(@Nullable ProfileProductsResponse profileProductsResponse) {
                if (profileProductsResponse != null && profileProductsResponse.getData() != null && profileProductsResponse.getData().size() > 0) {
                    setProductsListingData(profileProductsResponse.getData());
                } else if (currentChatModel.getProductInfo().getProductId() != null && !currentChatModel.getProductInfo().getProductId().equalsIgnoreCase("")) {
                    productList.clear();
                    productList.add(currentChatModel.getProductInfo());
                    currentProductSpinnerPosition = 0;
                    chatProductsSpinnerAdapter.notifyDataSetChanged();
                    if (AppUtils.isConnection(mActivity))
                        getProductStatusApi(currentChatModel.getProductInfo().getProductId());
                } else {
                    currentProductSpinnerPosition = -1;
                    productList.clear();
                    fragmentMessagesDetailBinding.spinnerProducts.setVisibility(View.GONE);
                    chatProductsSpinnerAdapter.notifyDataSetChanged();
                }
            }
        });
        getNewlyAddedMessage();
        checkOtherUserNode();
        checkMessages(true);
    }

    /**
     * used to set the product listing data
     *
     * @param productsListingData list of products
     */
    private void setProductsListingData(List<ProductDetailsData> productsListingData) {
        fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
        productList.clear();
        int position = -1;
        currentProductSpinnerPosition = 0;
        for (ProductDetailsData productDetailsData : productsListingData) {
            position += 1;
            ChatProductModel chatProductModel = new ChatProductModel();
            chatProductModel.setProductPrice(Double.parseDouble(productDetailsData.getFirmPrice()));
            chatProductModel.setProductName(productDetailsData.getTitle());
            chatProductModel.setProductId(productDetailsData.getProductId());
            chatProductModel.setProductStatus(productDetailsData.getProductStatus());
            chatProductModel.setProductImage((productDetailsData.getImageList() != null && productDetailsData.getImageList().size() > 0) ? productDetailsData.getImageList().get(0).getThumbUrl() : "");
            productList.add(chatProductModel);
            if (currentChatModel.getProductInfo() != null && currentChatModel.getProductInfo().getProductId() != null && (chatProductModel.getProductId().equalsIgnoreCase(currentChatModel.getProductInfo().getProductId())))
                currentProductSpinnerPosition = position;
        }
        fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
        if (AppUtils.isConnection(mActivity))
            getProductStatusApi(productsListingData.get(currentProductSpinnerPosition).getProductId());
        chatProductsSpinnerAdapter.notifyDataSetChanged();
        fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
    }

    /**
     * used to check the current product status whether payment done or pending etc
     */
    private void checkProductStatusData() {
        if (productStatusData != null) {
            boolean isSeller;
            if (productStatusData.getProductStatus() == 1 && productStatusData.getDeliveryStatus().equalsIgnoreCase("")) {
                updateProductStatusUI(View.GONE, View.GONE, View.GONE, "", "", "");
                return;
            }
            if (productStatusData.getSellerId().equalsIgnoreCase(user.getUserId())) {
                if (productStatusData.getUserId().equalsIgnoreCase(currentChatModel.getOtherUserId()))
                    isSeller = true;
                else {
                    updateProductStatusUI(View.GONE, View.GONE, View.GONE, "", "", "");
                    return;
                }
            } else if (productStatusData.getUserId().equalsIgnoreCase(user.getUserId())) {
                isSeller = false;
//                if (productStatusData.getSellerId().equalsIgnoreCase(currentChatModel.getOtherUserId()))
//                    isSeller = false;
//                else {
//                    updateProductStatusUI(View.GONE, View.GONE, View.GONE, "", "", "");
//                    return;
//                }
            } else {
                updateProductStatusUI(View.GONE, View.GONE, View.GONE, "", "", "");
                return;
            }
            switch (productStatusData.getDeliveryStatus()) {
                case AppConstants.PAYMENT_REFUND_STATUS.PENDING://reserve item
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.VISIBLE);
                    if (isSeller) {
                        updateProductStatusUI(View.VISIBLE, View.GONE, View.GONE, "", "", getString(R.string.inspect_and_confirm_product_to_release_the_payment_seller));
                    } else
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.VISIBLE, getString(R.string.refund), getString(R.string.release_payment), getString(R.string.inspect_and_confirm_product_to_release_the_payment));
                    break;//done

                case AppConstants.PAYMENT_REFUND_STATUS.ITEM_DELEVER:
                case AppConstants.PAYMENT_REFUND_STATUS.COMPLETED:// after release payment
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.GONE);
                    if (isSeller)
                        updateProductStatusUI(View.VISIBLE, View.GONE, View.GONE, "", "", getString(R.string.order_complete) + "\n" + currentChatModel.getRoomName() + " " + getString(R.string.has_released_the_payment_check_your_wallet));
                    else
                        updateProductStatusUI(View.VISIBLE, View.GONE, View.GONE, "", "", getString(R.string.order_complete_you_have_release_the_payment));
                    break;//done

                case AppConstants.PAYMENT_REFUND_STATUS.REQUEST_FOR_REFUND://after buyer refund request
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.VISIBLE);
                    fragmentMessagesDetailBinding.tvInfo.setText(R.string.refund_policy);
                    if (isSeller)
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.VISIBLE, getString(R.string.decline), getString(R.string.accept), currentChatModel.getRoomName() + " " + getString(R.string.initiates_a_refund_request));
                    else
                        updateProductStatusUI(View.VISIBLE, View.GONE, View.VISIBLE, "", getString(R.string.cancel_refund_request), getString(R.string.you_have_requested_a_refund_you_will_receive_a_response_within_5_days));
                    break;//done

                case AppConstants.PAYMENT_REFUND_STATUS.REFUND_ACCEPTED://refund accepted from seller
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.VISIBLE);
                    fragmentMessagesDetailBinding.tvInfo.setText(R.string.refund_payment);
                    if (isSeller)
                            updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.VISIBLE, getString(R.string.dispute), getString(R.string.release_refund), getString(R.string.you_accepted_the_refund_request_if_correct_release_payment));
                    else
                        updateProductStatusUI(View.VISIBLE, View.GONE, View.VISIBLE, "", getString(R.string.cancel_refund_request), currentChatModel.getRoomName() + " " + getString(R.string.return_the_item_if_agree_to_refund));
                    break;//done

                case AppConstants.PAYMENT_REFUND_STATUS.REFUND_SUCCESS://when seller accepted refund and item received - release payment
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.GONE);
                    if (isSeller)
                        updateProductStatusUI(View.VISIBLE, View.GONE, View.GONE, "", "", getString(R.string.order_complete_you_have_release_the_refund));
                    else
                        updateProductStatusUI(View.VISIBLE, View.GONE, View.GONE, "", "", getString(R.string.refund_complete) + "" + currentChatModel.getRoomName() + " " + getString(R.string.has_released_the_refund));
                    break;//done

                case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_STARTED://buyer add dispute - after refund decline case
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.GONE);
                    if (isSeller)
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.VISIBLE, getString(R.string.submit_response), getString(R.string.accept_refund), currentChatModel.getRoomName() + " " + getString(R.string.has_initiated_a_dispute_on_this_order_you_have_3_days_to_submit));
                    else
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.GONE, getString(R.string.cancel_dispute), "", getString(R.string.you_have_opened_a_dispute_you_will_receive_admin_decision));
                    break;//done

                case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_RESPONSE://after refund decline case buyer dispute - seller submit response dispute
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.GONE);
                    if (isSeller)
                        updateProductStatusUI(View.VISIBLE, View.GONE, View.VISIBLE, "", getString(R.string.accept_refund), getString(R.string.you_have_submitted_your_response));
                    else
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.GONE, getString(R.string.cancel_dispute), "", getString(R.string.you_have_opened_a_dispute_you_will_receive_admin_decision));
                    break;//done

                case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_COMPLETED:
                    updateProductStatusUI(View.GONE, View.GONE, View.GONE, "", "", "");
                    break;

                case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_DONE://5 days of response - seller open a dispute
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.GONE);
                    if (isSeller)
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.GONE, getString(R.string.cancel_dispute), "", getString(R.string.you_have_opened_a_dispute_you_will_receive_admin_decision));
                    else {
                        if (!TextUtils.isEmpty(productStatusData.getRefundAcceptedDate()) && !productStatusData.getRefundAcceptedDate().equalsIgnoreCase("0"))
                            updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.VISIBLE, getString(R.string.submit_response), getString(R.string.release_payment), currentChatModel.getRoomName() + " " + getString(R.string.has_accept_refund_dispute_dispute_on_this_order_you_have_3_days_to_submit));
                        else
                            updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.VISIBLE, getString(R.string.submit_response), getString(R.string.release_payment), currentChatModel.getRoomName() + " " + getString(R.string.has_initiated_a_dispute_on_this_order_you_have_3_days_to_submit));
                    }
                    break;//done

                case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_RESPONSE://after 5 days when seller dispute - buyer submit response dispute
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.GONE);
                    if (isSeller)
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.GONE, getString(R.string.cancel_dispute), "", getString(R.string.you_have_opened_a_dispute_you_will_receive_admin_decision));
                    else {
                        if (!TextUtils.isEmpty(productStatusData.getRefundAcceptedDate()) && !productStatusData.getRefundAcceptedDate().equalsIgnoreCase("0"))
                            updateProductStatusUI(View.VISIBLE, View.GONE, View.VISIBLE, "", getString(R.string.release_payment), currentChatModel.getRoomName() + " " + getString(R.string.has_accept_refund_dispute_dispute_on_this_order_you_have_3_days_to_submit));
                        else
                            updateProductStatusUI(View.VISIBLE, View.GONE, View.VISIBLE, "", getString(R.string.release_payment), currentChatModel.getRoomName() + " " + getString(R.string.has_initiated_a_dispute_on_this_order_you_have_3_days_to_submit));
                    }
                    break;//done

                case AppConstants.PAYMENT_REFUND_STATUS.DECLINED:// when seller decline refund request
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.VISIBLE);
                    fragmentMessagesDetailBinding.tvInfo.setText(R.string.refund_policy);
                    if (isSeller) {
                        updateProductStatusUI(View.VISIBLE, View.GONE, View.GONE, "", "", getString(R.string.you_decline_the_refund_due_to_following_reason));
                    } else {
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.VISIBLE, getString(R.string.dispute), getString(R.string.release_payment), currentChatModel.getRoomName() + " " + getString(R.string.decline_your_refund_request_due_to_following_reason) + "\n" + productStatusData.getReason());
                    }
                    break;//done

                case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_CAN_START://if buyer not respond till 5 days, seller will have open a dispute option
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.VISIBLE);
                    if (isSeller) {// open a dispute
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.GONE, getString(R.string.open_a_dispute), "", getString(R.string.inspect_and_confirm_product_to_release_the_payment_seller));
                    } else {//refund, release payment
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.VISIBLE, getString(R.string.refund), getString(R.string.release_payment), getString(R.string.inspect_and_confirm_product_to_release_the_payment));
                    }
                    break;//done

                case AppConstants.PAYMENT_REFUND_STATUS.REFUND_DISPUTE_CAN_START://if buyer not respond till 3 days after refund request declined by seller, seller can dispute
                    fragmentMessagesDetailBinding.tvInfo.setVisibility(View.VISIBLE);
                    if (isSeller) {// open a dispute
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.GONE, getString(R.string.dispute), "", getString(R.string.you_decline_the_refund_due_to_following_reason));
                    } else {//refund, release payment
                        updateProductStatusUI(View.VISIBLE, View.VISIBLE, View.VISIBLE, getString(R.string.dispute), getString(R.string.release_payment), currentChatModel.getRoomName() + " " + getString(R.string.decline_your_refund_request_due_to_following_reason) + "\n" + productStatusData.getReason());
                    }
                    break;//done

                default:
                    updateProductStatusUI(View.GONE, View.GONE, View.GONE, "", "", "");
                    break;
            }

        } else
            updateProductStatusUI(View.GONE, View.GONE, View.GONE, "", "", "");

        fragmentMessagesDetailBinding.rvMessages.scrollToPosition(messagesHashmap.size() - 1);

    }

    /**
     * \
     * used to update the product status UI
     *
     * @param negativeBtnVisibility whether negative or left button is visible or not
     * @param positiveBtnVisibility whether positive or right button is visible or not
     * @param negativeBtnText       text for negative button
     * @param positiveBtnText       text for positive button
     * @param titleText             main title text
     */
    private void updateProductStatusUI(int mainLayoutVisibility, int negativeBtnVisibility, int positiveBtnVisibility, String negativeBtnText, String positiveBtnText, String titleText) {
        fragmentMessagesDetailBinding.llProductStatus.setVisibility(mainLayoutVisibility);
        fragmentMessagesDetailBinding.btnNegative.setVisibility(negativeBtnVisibility);
        fragmentMessagesDetailBinding.btnNegative.setText(negativeBtnText);
        fragmentMessagesDetailBinding.btnPositive.setVisibility(positiveBtnVisibility);
        fragmentMessagesDetailBinding.btnPositive.setText(positiveBtnText);
        fragmentMessagesDetailBinding.tvProductStatus.setText(titleText);
    }

    /**
     * used to check whether the room exists or not
     */
    private void checkIfRoomExists() {
        roomNodeQuery = messagesDetailViewModel.getUserChatsQuery(user.getUserId()).child(currentChatModel.getRoomId());
        roomNodeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists() || dataSnapshot.getValue() == null) {
                    if (!isNewChat)
                        mActivity.finish();
                } else if (dataSnapshot.getValue() != null) {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    fragmentMessagesDetailBinding.tvToolbarTitle.setText(chatModel.getRoomName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        productInfoQuery = messagesDetailViewModel.getUserChatsQuery(user.getUserId()).child(currentChatModel.getRoomId()).child("productInfo");
        productInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                    roomProductModel = dataSnapshot.getValue(ChatProductModel.class);
                    if (!isFirstTimeLoaded && currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {

                        if (!productList.get(currentProductSpinnerPosition).getProductId().equalsIgnoreCase(roomProductModel.getProductId())) {
                            int position = -1;
                            for (ChatProductModel productModel : productList) {
                                position += 1;
                                if (roomProductModel.getProductId().equalsIgnoreCase(productModel.getProductId())) {
                                    fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                    currentProductSpinnerPosition = position;
                                    currentChatModel.setProductInfo(roomProductModel);
                                    fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                    if (AppUtils.isConnection(mActivity)) {
                                        updateProductStatusUI(View.GONE, View.GONE, View.GONE, "", "", "");
                                    }
                                    break;
                                }
                            }
                        }
                        getProductStatusApi(roomProductModel.getProductId());
                    }
                    isFirstTimeLoaded = false;
                    fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        productInfoQuery.addValueEventListener(productInfoListener);
        roomNodeQuery.addValueEventListener(roomNodeListener);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i != currentProductSpinnerPosition) {
            if (AppUtils.isConnection(mActivity)) {
                currentProductSpinnerPosition = i;
                ChatProductModel chatProductModel = productList.get(i);

                if (messagesHashmap.size() > 0) {
                    if (!currentChatModel.getProductInfo().getProductId().equalsIgnoreCase(chatProductModel.getProductId())) {
                        updateProductStatusUI(View.GONE, View.GONE, View.GONE, "", "", "");
//                        messagesDetailViewModel.checkProductStatus(chatProductModel.getProductId());
                        getProductStatusApi(chatProductModel.getProductId());
//                        messagesDetailViewModel.checkProductStatus("5d15b457f5acdf70cf8f862b");
                        if (isOtherUserCreated && !isNewChat)
                            messagesDetailViewModel.updateProductInfo(user.getUserId(), currentChatModel.getOtherUserId(), roomId, chatProductModel);
                        sendMessage(messagesQuery.getRef().push().getKey(), chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_PRODUCT_CHANGE_HEADER);
                        currentChatModel.getProductInfo().setProductId(chatProductModel.getProductId());
                        currentChatModel.getProductInfo().setProductImage(chatProductModel.getProductImage());
                        currentChatModel.getProductInfo().setProductName(chatProductModel.getProductName());
                        currentChatModel.getProductInfo().setProductPrice(chatProductModel.getProductPrice());
                    }
                } else if (!currentChatModel.getProductInfo().getProductId().equalsIgnoreCase(chatProductModel.getProductId())) {
                    currentChatModel.getProductInfo().setProductId(chatProductModel.getProductId());
                    currentChatModel.getProductInfo().setProductImage(chatProductModel.getProductImage());
                    currentChatModel.getProductInfo().setProductName(chatProductModel.getProductName());
                    currentChatModel.getProductInfo().setProductPrice(chatProductModel.getProductPrice());
                    getProductStatusApi(chatProductModel.getProductId());
                }
            } else {
                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(this);
                showToastShort(getString(R.string.no_internet));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * used to get new messages from the firebase
     */
    private void getNewlyAddedMessage() {
        newMessageQuery = messagesDetailViewModel.getNewMessageQuery(roomId, endIndexTimeStamp);
        newMessageEventListener = new FirebaseChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    if (isAdded()) {
                        messagesDetailViewModel.updateUnreadCount(user.getUserId(), roomId);
                        isNewChat = false;
                        if (!isOtherUserCreated)
                            otherUserNodeQuery.addChildEventListener(otherUserChildNodeListener);
                        isOtherUserCreated = true;
                        noData(View.VISIBLE, "", "");
                        MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                        if (messagesHashmap.containsKey(messageModel.getMessageId())) {
                            MessageModel oldMessage = messagesHashmap.get(messageModel.getMessageId());
                            if (oldMessage != null) {
                                oldMessage.setLoadingImageOnAmazon(null);
                                oldMessage.setTimeStamp(messageModel.getTimeStamp());
                                oldMessage.setMessageStatus(messageModel.getMessageStatus());
                                messagesDetailListAdapter.notifyItemChanged(messagesHashmap.getKeyIndex(messageModel.getMessageId()));
                            }
                        } else {
                            messagesHashmap.put(messageModel.getMessageId(), messageModel);
                            messagesHashmap.addIndex(messageModel.getMessageId());
                            messagesDetailListAdapter.notifyItemInserted(messagesHashmap.size() - 1);
                            if (messageModel.getSenderId().equalsIgnoreCase(user.getUserId()) || ((LinearLayoutManager) fragmentMessagesDetailBinding.rvMessages.getLayoutManager()).findLastVisibleItemPosition() >= messagesHashmap.size() - 3)
                                fragmentMessagesDetailBinding.rvMessages.smoothScrollToPosition(messagesHashmap.size() - 1);
                            if (!messageModel.getSenderId().equalsIgnoreCase(user.getUserId()))
                                messagesDetailViewModel.updateMessageStatus(messageModel.getMessageId(), roomId);
                        }

                    }
                    fragmentMessagesDetailBinding.rvMessages.smoothScrollToPosition(messagesHashmap.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    isNewChat = false;
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    if (messageModel.getSenderId().equalsIgnoreCase(user.getUserId()))
                        updateMessage(messageModel);
                }
            }
        };
        newMessageQuery.addChildEventListener(newMessageEventListener);
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
                    try {
                        if (getArguments().getString("tag").equals("1")) {
                            sendMessage(messagesQuery.getRef().push().getKey(), "I have reserved the item " + productList.get(0).getProductName()
                                    , AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!dataSnapshot.exists() && messagesHashmap.size() == 0) {

                    } else {
                        isNewChat = false;
                        messagesDetailViewModel.updateUnreadCount(user.getUserId(), roomId);
                        int position = -1;
                        PositionedLinkedHashmap<String, MessageModel> positionedLinkedHashmap = new PositionedLinkedHashmap<>();
                        for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                            position += 1;
                            MessageModel messageModel = dataSnapshots.getValue(MessageModel.class);
                            if (!messageModel.getSenderId().equalsIgnoreCase(user.getUserId())) {
                                messageModel.setSenderName(currentChatModel.getRoomName());
                                messageModel.setSenderImage(currentChatModel.getRoomImage());
                            }
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
                        if (messagesHashmap.size() > 0)
                            endIndexTimeStamp = messagesHashmap.get(messagesHashmap.getKeyValue(0)).getTimeStampLong();
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
                Log.e("firebase error", "error : " + databaseError.getMessage());
            }
        });
    }

    /**
     * used to check the user node on firebase for other user
     */
    private void checkOtherUserNode() {
        otherUserNodeQuery = messagesDetailViewModel.getOtherUserNodeQuery(currentChatModel.getOtherUserId(), roomId);
        otherUserChildNodeListener = new FirebaseChildEventListener() {
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                isOtherUserCreated = false;
                otherUserNodeQuery.removeEventListener(otherUserChildNodeListener);
            }
        };
        otherUserNodeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    isOtherUserCreated = false;
                } else {
                    isOtherUserCreated = true;
                    otherUserNodeQuery.addChildEventListener(otherUserChildNodeListener);
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
            messagesDetailListAdapter.notifyItemChanged(messagesHashmap.getKeyIndex(messageModel.getMessageId()));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED)) {
                    boolean showRationale = mActivity.shouldShowRequestPermissionRationale(permissions[0]);
                    if (!showRationale) {
                        DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.permission_req_msg), getString(R.string.please_enable_the_permissions_from_settings_to_access_this_feature), getString(R.string.cancel), getString(R.string.setting), new DialogCallback() {
                            @Override
                            public void submit(String data) {
                                AppUtils.openAppSettings(mActivity);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        break;
                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])) {

                        // user did NOT check "never ask again"
                        // this is a good place to explain the user
                        // why you need the permission and ask if he wants
                        // to accept it (the rationale)
                    }
                } else {
                    openImageGallery();
                }
                break;
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED)) {
                    boolean showRationale = mActivity.shouldShowRequestPermissionRationale(permissions[0]);
                    if (!showRationale) {
                        DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.permission_req_msg), getString(R.string.please_enable_the_permissions_from_settings_to_access_this_feature), getString(R.string.cancel), getString(R.string.setting), new DialogCallback() {
                            @Override
                            public void submit(String data) {
                                AppUtils.openAppSettings(mActivity);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        break;
                    } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])) {

                    }
                } else {
                    try {
                        dispatchTakePictureIntent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    Uri imageUri;
                    if (photoUri != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            imageUri = Uri.parse(mCurrentPhotoPath);
                        } else {
                            imageUri = photoUri;
                        }
                        if (new File(imageUri.getPath()).length() > 0) {
                            MediaScannerConnection.scanFile(mActivity, new String[]{imageUri.getPath()}, new String[]{"image/*"}, null);
                            MessageModel messageModel = new MessageModel();
                            try {
                                messageModel.setTimeStamp(ServerValue.TIMESTAMP);
                            } catch (Exception e) {
                                messageModel.setTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                                e.printStackTrace();
                            }
                            messageModel.setSenderId(user.getUserId());
                            messageModel.setMessageType(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE);
                            messageModel.setMessageStatus(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_PENDING);
                            messageModel.setMessageText(imageUri.getPath());
                            messageModel.setMessageId(messagesQuery.getRef().push().getKey());
                            messageModel.setLoadingImageOnAmazon(ImageBean.IMAGE_UPLOAD_LOADING);
                            messageModel.setRoomId(roomId);
                            messagesHashmap.put(messageModel.getMessageId(), messageModel);
                            messagesHashmap.addIndex(messageModel.getMessageId());
                            messagesDetailListAdapter.notifyItemInserted(messagesHashmap.size() - 1);
                            fragmentMessagesDetailBinding.rvMessages.smoothScrollToPosition(messagesHashmap.size() - 1);
                            startUpload(imageUri.getPath(), messageModel.getMessageId());
                        } else
                            showToastShort(getString(R.string.the_file_you_picked_was_not_a_photo));
                    }
                    break;
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    // Get the cursor
                    assert selectedImage != null;
                    Cursor cursor = mActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    assert cursor != null;
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    MessageModel messageModel = new MessageModel();
                    messageModel.setTimeStamp(ServerValue.TIMESTAMP);
                    try {
                        messageModel.setTimeStamp(ServerValue.TIMESTAMP);
                    } catch (Exception e) {
                        messageModel.setTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                        e.printStackTrace();
                    }
                    messageModel.setSenderId(user.getUserId());
                    messageModel.setMessageType(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE);
                    messageModel.setMessageStatus(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_PENDING);
                    messageModel.setMessageText(imgDecodableString);
                    messageModel.setMessageId(messagesQuery.getRef().push().getKey());
                    messageModel.setLoadingImageOnAmazon(ImageBean.IMAGE_UPLOAD_LOADING);
                    messageModel.setRoomId(roomId);
                    messagesHashmap.put(messageModel.getMessageId(), messageModel);
                    messagesHashmap.addIndex(messageModel.getMessageId());
                    messagesDetailListAdapter.notifyItemInserted(messagesHashmap.size() - 1);
                    fragmentMessagesDetailBinding.rvMessages.smoothScrollToPosition(messagesHashmap.size() - 1);
                    startUpload(imgDecodableString, messageModel.getMessageId());
                    break;
                case SHARE_PRODUCT_REQ_CODE:
                    ProductDetailsData productDetailsData = data.getParcelableExtra(AppConstants.BUNDLE_DATA);
                    if (AppUtils.isConnection(mActivity)) {
                        if (productDetailsData != null) {
                            shareProduct(productDetailsData);
                        } else
                            showToastShort(getString(R.string.product_unavailable));
                    } else
                        showToastShort(getString(R.string.no_internet));
                    break;
                case SHARE_TAG_REQ_CODE:
                    TagData tagData = data.getParcelableExtra(AppConstants.BUNDLE_DATA);
                    if (AppUtils.isConnection(mActivity)) {
                        if (tagData != null) {
                            shareTag(tagData);
                        } else
                            showToastShort(getString(R.string.tag_unavailable));
                    } else
                        showToastShort(getString(R.string.no_internet));
                    break;
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                mActivity.onBackPressed();
                break;
            case R.id.ll_main:
                AppUtils.hideKeyboard(mActivity);
                break;
            case R.id.btn_negative:
            case R.id.btn_positive:
                lastClickedProductStatusButtonId = view.getId();
                openConfirmationDialog(view.getId());
                break;
            case R.id.tv_info:
                if (productStatusData != null)
                    openInfoPopup();
                break;
            case R.id.ib_send:
                if (AppUtils.isConnection(mActivity)) {
                    if (!TextUtils.isEmpty(fragmentMessagesDetailBinding.etMessage.getText().toString().trim())) {
                        if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                            ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                            if (roomProductModel != null && roomProductModel.getProductId() != null && !chatProductModel.getProductId().equalsIgnoreCase(roomProductModel.getProductId())) {
                                if (isOtherUserCreated && !isNewChat)
                                    messagesDetailViewModel.updateProductInfo(user.getUserId(), currentChatModel.getOtherUserId(), roomId, chatProductModel);
                                sendMessage(messagesQuery.getRef().push().getKey(), chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_PRODUCT_CHANGE_HEADER);
                            }
                        }
                        sendMessage(messagesQuery.getRef().push().getKey(), fragmentMessagesDetailBinding.etMessage.getText().toString(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT);
                    } else
                        showToastShort(getString(R.string.enter_some_message));
                } else
                    showToastShort(getString(R.string.no_internet));
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
            case R.id.ib_attachment:
            case R.id.iv_camera:
                DialogUtil.getInstance().CustomBottomSheetDialogShareInChat(mActivity, new OnDialogItemObjectClickListener() {

                    @Override
                    public void onPositiveBtnClick(Object object) {
                        switch (((View) object).getId()) {
                            case R.id.tv_take_photo:
                                if (Build.VERSION.SDK_INT >= 23) {
                                    //do your check here
                                    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                                        return;
                                    } else {
                                        try {
                                            dispatchTakePictureIntent();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    try {
                                        dispatchTakePictureIntent();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case R.id.tv_gallery:
                                if (Build.VERSION.SDK_INT >= 23) {
                                    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
                                    } else {
                                        openImageGallery();
                                    }
                                } else
                                    openImageGallery();
                                break;
                            case R.id.tv_share_community:
                                Intent intent = new Intent(mActivity, MyTagsActivity.class);
                                startActivityForResult(intent, SHARE_TAG_REQ_CODE);
                                break;
                            case R.id.tv_share_product:
                                Intent intent2 = new Intent(mActivity, MyProductsActivity.class);
                                startActivityForResult(intent2, SHARE_PRODUCT_REQ_CODE);
                                break;
                        }
                    }
                });
                break;
        }

    }

    /**
     * used to open the info popup for different payment statuses
     */
    private void openInfoPopup() {
        boolean isSeller;
        if (productStatusData.getSellerId().equalsIgnoreCase(user.getUserId())) {
            isSeller = true;
        } else if (productStatusData.getUserId().equalsIgnoreCase(user.getUserId())) {
            isSeller = false;
        } else
            return;
        DialogUtil dialogUtil = DialogUtil.getInstance();
        switch (productStatusData.getDeliveryStatus()) {
            case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_CAN_START:
            case AppConstants.PAYMENT_REFUND_STATUS.PENDING:
                if (isSeller)
                    dialogUtil.customBottomSheetRefundDialog(mActivity, "<p><strong>Transaction Policy Highlights:</strong></p>" +
                            "<ul>" +
                            "<li> Payment will be released to you after the buyer clicks “Release Payment” button.</li>" +
                            "<li> You can dispute the payment after 5 days of no action from the buyer.</li>" +
                            "</ul>" +
                            "<p><br /><strong>Refund Policy Highlights:</strong></p>" +
                            "<ul>" +
                            "<li> Items are non-refundable after the payment is released.</li>" +
                            "<li> Refunds can be requested before the item is delivered.</li>" +
                            "<li> Upon delivery, item can not be refunded unless the item is different from description.</li>" +
                            "</ul>");
                else
                    dialogUtil.customBottomSheetRefundDialog(mActivity, "<p><strong>Transaction Policy Highlights:</strong></p>" +
                            "<ul>" +
                            "<li> Payment will be released to the seller after you click the \"Release Payment\" button.</li>" +
                            "<li> The seller has the option to dispute after 5 days if no action is received from you.</li>" +
                            "</ul>" +
                            "<p><br /><strong>Refund Policy Highlights:</strong></p>" +
                            "<ul>" +
                            "<li> Items are non-refundable after the payment is released.</li>" +
                            "<li> Refunds can be requested before the item is delivered.</li>" +
                            "<li> Upon delivery, item can not be refunded unless the item is different from description.</li>" +
                            "</ul>");
                break;
            case AppConstants.PAYMENT_REFUND_STATUS.REQUEST_FOR_REFUND:
            case AppConstants.PAYMENT_REFUND_STATUS.DECLINED:
                if (isSeller)
                    dialogUtil.customBottomSheetRefundDialog(mActivity, "<p><strong>Transaction Policy Highlights:</strong></p>" +
                            "<ul>" +
                            "<li> The buyer has the option to dispute if you decline the refund request or after 3 days of no response from you.</li>" +
                            "<li> You may request the return of your item before releasing refund to the buyer.</li>" +
                            "</ul>" +
                            "<p><br /><strong>Refund Policy Highlights:</strong></p>" +
                            "<ul>" +
                            "<li> Refunds should  be accepted if the item has not been delivered.</li>" +
                            "<li> Items can not be refunded after the buyer accepts the item upon pickup/delivery.</li>" +
                            "<li> Item can not be refunded after the buyer receive a shipped item unless the item is different from the description.</li>" +
                            "</ul>");
                else
                    dialogUtil.customBottomSheetRefundDialog(mActivity, "<p><strong>Transaction Policy Highlights:</strong></p>" +
                            "<ul>" +
                            "<li> You may open a dispute if the seller declines your refund request or after 3 days of no response from the seller.</li>" +
                            "<li> You may need to return the item if requested by the seller.</li>" +
                            "</ul>" +
                            "<p><br /><strong>Refund Policy Highlights:</strong></p>" +
                            "<ul>" +
                            "<li> Refunds should  be accepted if the item has not been delivered.</li>" +
                            "<li> Items can not be refunded after the buyer accepts the item upon pickup/delivery.</li>" +
                            "<li> Item can not be refunded after the buyer receive a shipped item unless the item is different from the description.</li>" +
                            "</ul>");
                break;
            case AppConstants.PAYMENT_REFUND_STATUS.REFUND_ACCEPTED:
                if (isSeller)
                    dialogUtil.customBottomSheetRefundDialog(mActivity, "<p><strong>Transaction Policy Highlights:</strong></p>" +
                            "<ul>" +
                            "<li> The refund will be released to the buyer once you click \"Release Refund\".</li>" +
                            "<li> You have the option to dispute if the returned item is different than the original item.</li>" +
                            "<li> The buyer has the option to dispute after 5 days of no action from you.</li>" +
                            "</ul>");
                else
                    dialogUtil.customBottomSheetRefundDialog(mActivity, "<p><strong>Transaction Policy Highlights:</strong></p>" +
                            "<ul>" +
                            "<li> The refund will be released to you after the seller clicks \"Release Refund\".</li>" +
                            "<li> The seller has the option to dispute if the returned item is different than the original item.</li>" +
                            "<li> You have the option to dispute after 5 days of no action from the seller.</li>" +
                            "</ul>");
                break;
        }
    }

    /**
     * used to confirm the action taken by the user
     */
    private void openConfirmationDialog(int viewId) {
        boolean isSeller = productStatusData.getSellerId().equalsIgnoreCase(user.getUserId());
        switch (productStatusData.getDeliveryStatus()) {
            case AppConstants.PAYMENT_REFUND_STATUS.PENDING:
                if (!isSeller) {
                    if (viewId == R.id.btn_negative)
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.yes_lowercase), getString(R.string.refund), getString(R.string.do_you_want_to_request_a_refund), this);
                    else
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.confirm_payment), getString(R.string.i_confirm_that_the_item_is_same_as_description), this);
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.REQUEST_FOR_REFUND:
                if (isSeller) {
                    if (viewId == R.id.btn_negative) {
                        new CustomReasonToDeclineDialog(mActivity, new OnDialogViewClickListener() {
                            @Override
                            public void onSubmit(String txt, int id) {
                                if (AppUtils.isInternetAvailable(mActivity)) {
                                    mViewModel.declineRetrunRequest(productStatusData.getId(), txt);
                                } else {
                                    showNoNetworkError();
                                }
                            }
                        }).show();
                    } else
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.accept), getString(R.string.accept_refund), getString(R.string.do_you_want_accept_the_refund_request), this);
                } else if (viewId == R.id.btn_positive)
                    AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.cancel_refund), getString(R.string.do_you_want_to_cancel_the_refund_request), this);
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.REFUND_ACCEPTED:
                if (isSeller) {
                    if (viewId == R.id.btn_negative) {
                        submitResponse = "false";
                        requestCode = AppConstants.REQUEST_CODE.OPEN_A_DISPUTE;
                        submitDispute();
                    } else
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.confirm_refund), getString(R.string.do_you_want_to_release_the_refund), this);
                } else if (viewId == R.id.btn_positive)
                    AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.cancel_refund), getString(R.string.do_you_want_to_cancel_the_refund_request), this);
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.DECLINED:
                if (!isSeller) {
                    if (viewId == R.id.btn_negative) {
                        submitResponse = "false";
                        requestCode = AppConstants.REQUEST_CODE.REFUND_DECLINE_DISPUTE;
                        submitDispute();
                    } else
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.confirm_payment), getString(R.string.i_confirm_that_the_item_is_same_as_description), this);
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_STARTED:
                if (isSeller) {
                    if (viewId == R.id.btn_negative) {
                        submitResponse = "true";
                        requestCode = AppConstants.REQUEST_CODE.REFUND_DECLINE_DISPUTE_RESPONSE;
                        submitDispute();
                    } else
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.accept), getString(R.string.accept_refund), getString(R.string.do_you_want_accept_the_refund_request), this);
                } else if (viewId == R.id.btn_negative)
                    AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.cancel_dispute), getString(R.string.do_you_want_to_cancel_the_dispute_request), this);
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_RESPONSE:
                if (isSeller)
                    AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.accept), getString(R.string.accept_refund), getString(R.string.do_you_want_accept_the_refund_request), this);
                else
                    AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.cancel_dispute), getString(R.string.do_you_want_to_cancel_the_dispute_request), this);
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_DONE:
                if (isSeller)
                    AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.cancel_dispute), getString(R.string.do_you_want_to_cancel_the_dispute_request), this);
                else {
                    if (viewId == R.id.btn_positive)
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.confirm_payment), getString(R.string.i_confirm_that_the_item_is_same_as_description), this);
                    else {
                        submitResponse = "true";
                        requestCode = AppConstants.REQUEST_CODE.OPEN_DISPUTE_RESPONSE;
                        submitDispute();
                    }
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_RESPONSE:
                if (isSeller)
                    AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.cancel_dispute), getString(R.string.do_you_want_to_cancel_the_dispute_request), this);
                else {
                    if (viewId == R.id.btn_positive)
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.confirm_payment), getString(R.string.i_confirm_that_the_item_is_same_as_description), this);

                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_CAN_START:
                if (!isSeller) {
                    if (viewId == R.id.btn_negative)
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.yes_lowercase), getString(R.string.refund), getString(R.string.do_you_want_to_request_a_refund), this);
                    else
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.confirm_payment), getString(R.string.i_confirm_that_the_item_is_same_as_description), this);
                } else {
                    if (viewId == R.id.btn_negative) {
                        submitResponse = "false";
                        requestCode = AppConstants.REQUEST_CODE.OPEN_A_DISPUTE;
                        submitDispute();
                    }
                }
                break;

            case AppConstants.PAYMENT_REFUND_STATUS.REFUND_DISPUTE_CAN_START:
                if (!isSeller) {
                    if (viewId == R.id.btn_negative) {
                        submitResponse = "false";
                        requestCode = AppConstants.REQUEST_CODE.REFUND_DECLINE_DISPUTE;
                        submitDispute();
                    } else
                        AppUtils.openBottomSheetDialog(mActivity, getString(R.string.cancel_lowercase), getString(R.string.confirm), getString(R.string.confirm_payment), getString(R.string.i_confirm_that_the_item_is_same_as_description), this);
                } else {
                    if (viewId == R.id.btn_negative) {
                        submitResponse = "false";
                        requestCode = AppConstants.REQUEST_CODE.OPEN_A_DISPUTE;
                        submitDispute();
                    }
                }
                break;//done
        }
    }

    @SuppressLint("WrongConstant")
    public void submitDispute() {
        if (mDialog != null)
            mDialog.dismiss();
        mDialog = new BottomSheetDialog(mActivity);
        final BottomSheetDisputeBinding binding = BottomSheetDisputeBinding.inflate(LayoutInflater.from(mActivity));
        mDialog.setContentView(binding.getRoot());
        mDialog.setCancelable(false);
        imageUploadCount = 0;
        mImageList.clear();
        mFileDocumentArrayList.clear();
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
                    intent.putExtra("selectedList", mFileDocumentArrayList);
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
                if (reasonTxt != null && reasonTxt.length() > 0) {


                    if (AppUtils.isInternetAvailable(mActivity)) {
                        if (mFileDocumentArrayList != null && mFileDocumentArrayList.size() > 0) {
                            for (int i = 0; i < mFileDocumentArrayList.size(); i++) {
                                getLoadingStateObserver().onChanged(true);
                                startUpload(mFileDocumentArrayList.get(i), "0");
                            }
                        } else {
                            mViewModel.initiatDispute(productStatusData.getId(), reasonTxt, mImageList, submitResponse, requestCode);
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

    /**
     * this method is used to open the gallery
     */
    private void openImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);

    }

    //this method pass intent to open camera
    private void dispatchTakePictureIntent() throws IOException {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                File file = AppUtils.createImageFile();
                photoUri = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", file);
                mCurrentPhotoPath = "file:" + file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            photoUri = Uri.fromFile(new File(AppUtils.getFilePath()));
        }
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        try {
            intentCamera.putExtra("return-data", true);
            startActivityForResult(intentCamera, CAMERA_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
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
        if (!bean.getId().equalsIgnoreCase("0"))
            sendMessage(bean.getId(), bean.getServerUrl(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE);
        else {
            imageUploadCount++;
            ImageList imageList = new ImageList();
            imageList.setUrl(bean.getServerUrl());
            imageList.setThumbUrl(bean.getServerUrl());
            imageList.setType(bean.getType());
            mImageList.add(imageList);
            if (imageUploadCount >= this.mFileDocumentArrayList.size()) {
                imageUploadCount = 0;
                mViewModel.initiatDispute(productStatusData.getId(), reasonTxt, mImageList, submitResponse, requestCode);
                if (mDialog != null)
                    mDialog.dismiss();
                mDialog.dismiss();
                mFileDocumentArrayList.clear();
                reasonTxt = "";
            }

        }
    }

    @Override
    public void uploadFailed(ImageBean bean) {
        if (!bean.getId().equalsIgnoreCase("0"))
            setImageUploadingFailed(bean);
        else {
            imageUploadCount++;
            if (imageUploadCount == this.mFileDocumentArrayList.size()) {
                imageUploadCount = 0;
                mViewModel.initiatDispute(productStatusData.getId(), reasonTxt, mImageList, submitResponse, requestCode);
                if (mDialog != null)
                    mDialog.dismiss();
                mFileDocumentArrayList.clear();
            }
        }
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
     * used to get the default chat model for sending message
     */
    private ChatModel getDefaultChatModel() {
        ChatModel chatModel = new ChatModel();
        chatModel.setChatMute(false);
        chatModel.setPinned(false);
        try {
            chatModel.setCreatedTimeStamp(ServerValue.TIMESTAMP);
        } catch (Exception e) {
            chatModel.setCreatedTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
            e.printStackTrace();
        }
        chatModel.setChatType(currentChatModel.getChatType());
        chatModel.setOtherUserId(currentChatModel.getOtherUserId());
        chatModel.setRoomName(currentChatModel.getRoomName());
        chatModel.setRoomImage(currentChatModel.getRoomImage());
        chatModel.setUserType(currentChatModel.getUserType());
        ChatProductModel chatProductModel = new ChatProductModel();
        chatProductModel.setProductId(currentChatModel.getProductInfo().getProductId());
        chatProductModel.setProductImage(currentChatModel.getProductInfo().getProductImage());
        chatProductModel.setProductName(currentChatModel.getProductInfo().getProductName());
        chatProductModel.setProductPrice(currentChatModel.getProductInfo().getProductPrice());
        chatModel.setProductInfo(chatProductModel);
        chatModel.setRoomId(roomId);
        return chatModel;
    }

    /**
     * used to get the message model for sending message
     *
     * @param messageId   firebase generated id for message
     * @param text        text for the message
     * @param messageType type for the message
     * @return
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
        ChatModel chatModel = getDefaultChatModel();
        chatModel.setLastMessage(getMessageModel(messageId, text, messageType));
        messagesDetailViewModel.sendMessageToUser(user, isNewChat, isOtherUserCreated, user.getFullName() + " " + getString(R.string.send_a_message), chatModel.getLastMessage().getMessageText(), chatModel, null);
        if (messageType.equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT))
            fragmentMessagesDetailBinding.etMessage.setText("");
    }

    /**
     * used to share the product in the chat
     *
     * @param productDetailsData the product used to share
     */
    private void shareProduct(ProductDetailsData productDetailsData) {
        ChatModel chatModel = getDefaultChatModel();
        MessageModel messageModel = getMessageModel(messagesQuery.getRef().push().getKey(), productDetailsData.getTitle(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_PRODUCT);
        messageModel.setShareImage((productDetailsData.getImageList() != null && productDetailsData.getImageList().size() > 0) ? productDetailsData.getImageList().get(0).getUrl() : "");
        messageModel.setShareId(productDetailsData.getProductId());
        chatModel.setLastMessage(messageModel);
        if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
            ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
            if (roomProductModel != null && roomProductModel.getProductId() != null && !chatProductModel.getProductId().equalsIgnoreCase(roomProductModel.getProductId())) {
                if (isOtherUserCreated && !isNewChat)
                    messagesDetailViewModel.updateProductInfo(user.getUserId(), currentChatModel.getOtherUserId(), roomId, chatProductModel);
                sendMessage(messagesQuery.getRef().push().getKey(), chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_PRODUCT_CHANGE_HEADER);
            }
        }
        messagesDetailViewModel.sendMessageToUser(user, isNewChat, isOtherUserCreated, user.getFullName() + " " + getString(R.string.send_a_message), messageModel.getMessageText() + " " + getString(R.string.is_shared_with_you), chatModel, null);
    }

    /**
     * used to share the tag in the chat
     *
     * @param tagData the tag used to share
     */
    private void shareTag(TagData tagData) {
        ChatModel chatModel = getDefaultChatModel();
        MessageModel messageModel = getMessageModel(messagesQuery.getRef().push().getKey(), tagData.getTagName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_SHARE_COMMUNITY);
        messageModel.setShareImage(tagData.getTagImageUrl());
        messageModel.setShareId(tagData.getTagId());
        chatModel.setLastMessage(messageModel);
        if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
            ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
            if (roomProductModel != null && roomProductModel.getProductId() != null && !chatProductModel.getProductId().equalsIgnoreCase(roomProductModel.getProductId())) {
                if (isOtherUserCreated && !isNewChat)
                    messagesDetailViewModel.updateProductInfo(user.getUserId(), currentChatModel.getOtherUserId(), roomId, chatProductModel);
                sendMessage(messagesQuery.getRef().push().getKey(), chatProductModel.getProductName(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_PRODUCT_CHANGE_HEADER);
            }
        }
        messagesDetailViewModel.sendMessageToUser(user, isNewChat, isOtherUserCreated, user.getFullName() + " " + getString(R.string.send_a_message), messageModel.getMessageText() + " " + getString(R.string.is_shared_with_you), chatModel, null);
    }

    /**
     * used to show the layout if no data available
     *
     * @param VISIBILITY visibility for data views like recyclerview etc
     * @param errorTitle text for the title of the no data view
     * @param errorMsg   description for no data view
     */
    private void noData(int VISIBILITY, String errorTitle, String errorMsg) {
        fragmentMessagesDetailBinding.ibMore.setVisibility(VISIBILITY);
        fragmentMessagesDetailBinding.rvMessages.setVisibility(VISIBILITY);
        fragmentMessagesDetailBinding.includeHeaderEmpty.tvTitle.setText(errorTitle);
        fragmentMessagesDetailBinding.includeHeaderEmpty.tvEmptyMsg.setText(errorMsg);
        fragmentMessagesDetailBinding.tvNoData.setVisibility(VISIBILITY == View.VISIBLE ? View.GONE : View.VISIBLE);
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
        if (messagesDetailListAdapter != null && messagesDetailListAdapter.listenerModelHashMap != null) {
            for (final Map.Entry<String, RemoveFirebaseListenerModel> entry : messagesDetailListAdapter.listenerModelHashMap.entrySet()) {
                RemoveFirebaseListenerModel removeFirebaseListenerModel = entry.getValue();
                removeFirebaseListenerModel.getQuery().removeEventListener(removeFirebaseListenerModel.getValueEventListener());
            }
            messagesDetailListAdapter.listenerModelHashMap.clear();
        }
        newMessageQuery.removeEventListener(newMessageEventListener);
        roomNodeQuery.removeEventListener(roomNodeListener);
        otherUserNodeQuery.removeEventListener(otherUserChildNodeListener);
        productInfoQuery.removeEventListener(productInfoListener);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(localReceiver);
    }

    @Override
    public void onPositiveBtnClick() {
        if (AppUtils.isConnection(mActivity)) {
            boolean isSeller = productStatusData.getSellerId().equalsIgnoreCase(user.getUserId());
            switch (productStatusData.getDeliveryStatus()) {
                case AppConstants.PAYMENT_REFUND_STATUS.PENDING:
                    if (!isSeller) {
                        if (lastClickedProductStatusButtonId == R.id.btn_negative)
                            mViewModel.initiateRefundRequest(productStatusData.getId());//refund api
                        else
                            mViewModel.confirmItemReceived(productStatusData.getId());//release payment api
                    }
                    break;
                case AppConstants.PAYMENT_REFUND_STATUS.REQUEST_FOR_REFUND:
                    if (isSeller) {
                        if (lastClickedProductStatusButtonId == R.id.btn_positive) {
                            mViewModel.returnRequestAccept(productStatusData.getId());//refund accept api
                        } /*else{
                            //refund decline api - donex
                        }*/
                    } else {
                        mViewModel.cancelDisputeApi(productStatusData.getId(), "actionCancelRefundRequest", AppConstants.REQUEST_CODE.CANCEL_REFUND_REQUEST);//cancel refund request
                    }
                    break;
                case AppConstants.PAYMENT_REFUND_STATUS.REFUND_ACCEPTED:
                    if (isSeller)
                        if (lastClickedProductStatusButtonId == R.id.btn_negative) {
                            //refund dispute api - done
                        } else {
//                            mViewModel.confirmReturnItemReceivedSeller(productStatusData.getPrice());//release refund

                            TagHawkApplication.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    blueSnapReleaseRefund(productStatusData.getChargeId(), productStatusData.getPrice(), productStatusData.getId());
                                }
                            });

                        }
                    else
                        mViewModel.cancelDisputeApi(productStatusData.getId(), "actionCancelRefundRequest", AppConstants.REQUEST_CODE.CANCEL_REFUND_REQUEST);//cancel refund request
                    break;
                case AppConstants.PAYMENT_REFUND_STATUS.REFUND_SUCCESS:
                    break;//after releasing refund when accepted
                case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_STARTED:
                    if (isSeller)
                        if (lastClickedProductStatusButtonId == R.id.btn_negative) {
                            //refund dispute case submit response api - done
                        } else
                            mViewModel.returnRequestAccept(productStatusData.getId());//refund request accept
                    else {
                        mViewModel.cancelDisputeApi(productStatusData.getId(), "actionCancelRefundDispute", AppConstants.REQUEST_CODE.CANCEL_REFUND_DISPUTE);//cancel dispute api
                    }
                    break;
                case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_RESPONSE:
                    if (isSeller)
                        mViewModel.returnRequestAccept(productStatusData.getId());//refund request accept
                    else {
                        mViewModel.cancelDisputeApi(productStatusData.getId(), "actionCancelRefundDispute", AppConstants.REQUEST_CODE.CANCEL_REFUND_DISPUTE);//cancel dispute api
                    }
                    break;
                case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_DONE:
                    if (isSeller) {
                        //add 3 days of no response
                        if (!TextUtils.isEmpty(productStatusData.getRefundAcceptedDate()) && !productStatusData.getRefundAcceptedDate().equalsIgnoreCase("0"))
                            mViewModel.cancelDisputeApi(productStatusData.getId(), "actionCancelRefundAcceptDispute", AppConstants.REQUEST_CODE.CANCEL_REFUND_ACCEPT_DISPUTE);//cancel dispute api
                        else if (!TextUtils.isEmpty(productStatusData.getDeclineDate()) && !productStatusData.getDeclineDate().equalsIgnoreCase("0"))
                            mViewModel.cancelDisputeApi(productStatusData.getId(), "actionCancelNoResponseDispute", AppConstants.REQUEST_CODE.CANCEL_OPEN_DISPUTE);//cancel dispute api
                        else
                            mViewModel.cancelDisputeApi(productStatusData.getId(), "actionCancelOpenDispute", AppConstants.REQUEST_CODE.CANCEL_OPEN_DISPUTE);//cancel dispute api
                    } else {
                        if (lastClickedProductStatusButtonId == R.id.btn_negative) {
                            //submit dispute response api - done
                        } else
                            mViewModel.confirmItemReceived(productStatusData.getId());//release payment api
                    }
                    break;
                case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_RESPONSE:
                    if (isSeller) {
                        //3 days of no response
                        if (!TextUtils.isEmpty(productStatusData.getRefundAcceptedDate()) && !productStatusData.getRefundAcceptedDate().equalsIgnoreCase("0"))
                            mViewModel.cancelDisputeApi(productStatusData.getId(), "actionCancelRefundAcceptDispute", AppConstants.REQUEST_CODE.CANCEL_REFUND_ACCEPT_DISPUTE);//cancel dispute api
                        else if (!TextUtils.isEmpty(productStatusData.getDeclineDate()) && !productStatusData.getDeclineDate().equalsIgnoreCase("0"))
                            mViewModel.cancelDisputeApi(productStatusData.getId(), "actionCancelNoResponseDispute", AppConstants.REQUEST_CODE.CANCEL_OPEN_DISPUTE);//cancel dispute api
                        else
                            mViewModel.cancelDisputeApi(productStatusData.getId(), "actionCancelOpenDispute", AppConstants.REQUEST_CODE.CANCEL_OPEN_DISPUTE);//cancel dispute api
                    } else {
                        mViewModel.confirmItemReceived(productStatusData.getId());//release payment api
                    }
                    break;
                case AppConstants.PAYMENT_REFUND_STATUS.DECLINED:
                    if (!isSeller) {
                        if (lastClickedProductStatusButtonId == R.id.btn_negative) {
                            //refund decline time dispute start - done
                        } else {
                            mViewModel.confirmItemReceived(productStatusData.getId());//release payment api
                        }
                    }
                    break;
                case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_CAN_START:
                    if (!isSeller) {
                        if (lastClickedProductStatusButtonId == R.id.btn_negative)
                            mViewModel.initiateRefundRequest(productStatusData.getId());//refund request api
                        else
                            mViewModel.confirmItemReceived(productStatusData.getId());//release payment
                    } else if (lastClickedProductStatusButtonId == R.id.btn_negative) {
                        //open a dispute api - done
                    }
                    break;
                case AppConstants.PAYMENT_REFUND_STATUS.REFUND_DISPUTE_CAN_START:
                    if (!isSeller) {
                        if (lastClickedProductStatusButtonId == R.id.btn_negative) {
                            //refund decline time dispute start - done
                        } else
                            mViewModel.confirmItemReceived(productStatusData.getId());//release payment
                    } else if (lastClickedProductStatusButtonId == R.id.btn_negative) {
                        //open a dispute api - done
                    }
                    break;
            }
        } else
            showToastShort(getString(R.string.no_internet));
    }

    @Override
    public void onNegativeBtnClick() {

    }

    //I am calling this because LiveData call is nor coming in observer
    public void getProductStatusApi(final String productId) {
        DataManager.getInstance().productStatusApi(productId).enqueue(new NetworkCallback<PaymentRefundModel>() {
            @Override
            public void onSuccess(PaymentRefundModel paymentRefundModel) {
                if (paymentRefundModel != null && paymentRefundModel.getCode() == 200) {
                    productStatusData = paymentRefundModel.getmData();
                    if (currentProductSpinnerPosition != -1 && currentProductSpinnerPosition <= productList.size() - 1) {
                        ChatProductModel chatProductModel = productList.get(currentProductSpinnerPosition);
                        if (chatProductModel.getProductId().equalsIgnoreCase(productStatusData.getProductId()) && productStatusData.getProductStatus() != chatProductModel.getProductStatus()) {
                            switch (productStatusData.getProductStatus()) {
                                case 1:
                                case 5:
                                    productList.get(currentProductSpinnerPosition).setProductStatus(paymentRefundModel.getmData().getProductStatus());
                                    chatProductsSpinnerAdapter.notifyDataSetChanged();
                                    break;
                                case 2:
                                    fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(null);
                                    chatProductModel.setProductStatus(productStatusData.getProductStatus());
                                    productList.remove(currentProductSpinnerPosition);
                                    productList.add(chatProductModel);
                                    currentProductSpinnerPosition = productList.size() - 1;
                                    fragmentMessagesDetailBinding.spinnerProducts.setSelection(currentProductSpinnerPosition);
                                    fragmentMessagesDetailBinding.spinnerProducts.setOnItemSelectedListener(MessagesDetailSingleChatFragment.this);
                                    chatProductsSpinnerAdapter.notifyDataSetChanged();
                                    break;
                            }
                        }
                    }
                    checkProductStatusData();

                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {

            }

            @Override
            public void onError(Throwable t) {

            }
        });

    }

    private void blueSnapReleaseRefund(String transactionId, String amount, String productId) {

        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        List<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
        BlueSnapHTTPResponse httpResponse = HTTPOperationController.put(SANDBOX_URL + SANDBOX_REFUND_TRANSACTION + transactionId + "/refund?amount=" + amount, "", "application/json", "application/json", headerParams);
        Log.e("API", SANDBOX_URL + SANDBOX_REFUND_TRANSACTION + transactionId + "/refund?amount=" + amount);
        Log.e("API_HEADER", "Authorization : " + basicAuth);
//        Log.e("API_PARAMS", "" + body);
        String responseString = httpResponse.getResponseString();
        Log.e("API_RESPONSE", "response is : " + responseString);
        String responseErrorString = httpResponse.getErrorResponseString();
        Log.e("API_ERROR_RESPONSE", "response is : " + responseErrorString);

        if (responseString == null  || TextUtils.isEmpty(responseString)) {
            System.out.println("*****Done*****");
            Log.e("BLUESNAP_REFUND", "api hit properly");

            mViewModel.confirmReturnItemReceivedSeller(productId);//release refund

        } else {
            Toast.makeText(mActivity, "Product refund occur with an error", Toast.LENGTH_SHORT).show();
        }

    }

}
