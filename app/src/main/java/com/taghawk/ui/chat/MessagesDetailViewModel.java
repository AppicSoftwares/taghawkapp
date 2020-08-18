package com.taghawk.ui.chat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.firebase.FirebaseManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentRefundModel;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.chat.ChatPushModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.model.login.LoginFirebaseModel;
import com.taghawk.model.profileresponse.ProfileProductsResponse;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.model.request.User;

import org.json.JSONObject;

import java.util.HashMap;

public class MessagesDetailViewModel extends ViewModel {

    private Observer<Throwable> mErrorObserver;
    private MutableLiveData<Integer> mActionLiveData;
    private Observer<FailureResponse> mFailureObserver;
    private DataManager dataManager;

    private MediatorLiveData<MessageModel> sendMessageViewModel;
    private RichMediatorLiveData<ProfileProductsResponse> mProfileProductsLiveData;
    private RichMediatorLiveData<ProductListingModel> mTagProductsLiveData;
    private RichMediatorLiveData<PaymentRefundModel> productStatusLiveData, newProductStatusLive;
    private ChatMessagesRepo chatMessagesRepo;
    private RichMediatorLiveData<ProfileResponse> blockUserLiveData;


    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        dataManager = DataManager.getInstance();
        initLiveData();
    }

    private void initLiveData() {
        chatMessagesRepo = new ChatMessagesRepo();
        if (sendMessageViewModel == null) {
            sendMessageViewModel = new RichMediatorLiveData<MessageModel>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mProfileProductsLiveData == null) {
            mProfileProductsLiveData = new RichMediatorLiveData<ProfileProductsResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (blockUserLiveData == null) {
            blockUserLiveData = new RichMediatorLiveData<ProfileResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mTagProductsLiveData == null) {
            mTagProductsLiveData = new RichMediatorLiveData<ProductListingModel>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (productStatusLiveData == null) {
            productStatusLiveData = new RichMediatorLiveData<PaymentRefundModel>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (newProductStatusLive == null) {
            newProductStatusLive = new RichMediatorLiveData<PaymentRefundModel>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
    }

    public RichMediatorLiveData<PaymentRefundModel> getProductStatusLiveData() {
        return productStatusLiveData;
    }

    public RichMediatorLiveData<PaymentRefundModel> getNewProductStatusLiveData() {
        return newProductStatusLive;
    }

    public void checkProductStatus(String productId) {
        chatMessagesRepo.getProductStatusApi(newProductStatusLive, productId);
    }

    public void sendMessageToUser(User user, boolean isNewChat, boolean isOtherUserCreated, String title, String text, ChatModel chatModel, FirebaseManager.CountUpdateListener countUpdateListener) {
        if (dataManager == null)
            dataManager = DataManager.getInstance();
        dataManager.sendMessageToUser(user, isNewChat, isOtherUserCreated, title, text, chatModel, countUpdateListener);
    }

    public void sendMessageToGroup(String userId, String roomId, String title, String text, MessageModel messageModel) {
        dataManager.sendMessageToGroup(userId, roomId, title, text, messageModel);
    }

    void muteUnmuteChat(boolean isMute, String userId, String roomId) {
        dataManager.muteUnmuteChat(isMute, userId, roomId);
    }

    void pinnedChat(boolean isPinned, String userId, String roomId) {
        dataManager.pinnedChat(isPinned, userId, roomId);
    }

    Query getRoomMessagesQuery(String roomId, long endIndex, long createdTimeStamp) {
        return dataManager.getUserMessagesQuery(roomId, endIndex, createdTimeStamp);
    }

    Query getNewMessageQuery(String roomId, long startIndex) {
        return dataManager.getNewMessageQuery(roomId, startIndex);
    }

    Query getOtherUserNodeQuery(String otherUserId, String roomId) {
        return dataManager.getOtherUserNodeQuery(otherUserId, roomId);
    }

    public void updateProductInfo(String userId, String otherUserId, String roomId, ChatProductModel chatProductModel) {
        dataManager.updateProductInfo(userId, otherUserId, roomId, chatProductModel);
    }

    public void deleteUserChat(String userId, String roomId) {
        dataManager.deleteUserChat(userId, roomId);
    }

    void updateUnreadCount(String userId, String roomId) {
        dataManager.updateUnreadCount(userId, roomId);
    }

    void updateMessageStatus(String messageId, String roomId) {
        dataManager.updateMessageStatus(messageId, roomId);
    }

    public void getProfileProductsList(String otherUserId) {
        chatMessagesRepo.hitGetProductsAPI(mProfileProductsLiveData, otherUserId);
    }

    public RichMediatorLiveData<ProfileProductsResponse> profileProductsLiveData() {
        return mProfileProductsLiveData;
    }

    void getTagProducts(String tagId) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagId);
        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, 1);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, 100);
        chatMessagesRepo.getTagProducts(mTagProductsLiveData, parms);
    }

    RichMediatorLiveData<ProductListingModel> getmTagProductsLiveData() {
        return mTagProductsLiveData;
    }

    DatabaseReference getUserChatsQuery(String userId) {
        return DataManager.getInstance().getUserChatsQuery(userId);
    }

    Query getGroupDetailQuery(String roomId) {
        return dataManager.getGroupNodeQuery(roomId);
    }

    void blockUser(HashMap<String, Object> hashMap, String membername, int type) {
        chatMessagesRepo.blockUser(blockUserLiveData, hashMap, membername, type);
    }

    RichMediatorLiveData<ProfileResponse> getBlockUserLiveData() {
        return blockUserLiveData;
    }

    void blockUserOnFirebase(String userId, String otherUserId) {
        dataManager.blockUserOnFirebase(userId, otherUserId);
    }

    public void sendPushOnFirebase(final String memberId, final String roomId, final String title, final String text, final String userId) {
        if (dataManager == null)
            dataManager = DataManager.getInstance();
        final JSONObject finalMap = new JSONObject();
        final JSONObject jsonData = new JSONObject();
        final JSONObject jsonNotification = new JSONObject();
        final ChatPushModel notificationBean = new ChatPushModel();
        notificationBean.setEntityId(roomId);
        notificationBean.setTitle(title);
        notificationBean.setType(AppConstants.FIREBASE.PUSH_TYPE_MESSAGE);
        dataManager.getUserChatsQuery(userId).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    final ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    if (!chatModel.isChatMute()) {
                        dataManager.getUserNodeQuery(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.getValue() != null) {
                                        JSONObject jsonObjectPayload = new JSONObject();
                                        jsonObjectPayload.put("type", AppConstants.FIREBASE.PUSH_TYPE_MESSAGE);
                                        jsonObjectPayload.put("entityId", roomId);
                                        jsonObjectPayload.put("title", title);
                                        jsonObjectPayload.put("userId", memberId);
                                        jsonObjectPayload.put("text", text);
                                        LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                        jsonObjectPayload.put("badge", loginFirebaseModel.getTotalUnreadCount());
                                        if (loginFirebaseModel != null && loginFirebaseModel.getDeviceToken() != null && !loginFirebaseModel.getDeviceToken().equalsIgnoreCase("")) {
                                            finalMap.put("to", loginFirebaseModel.getDeviceToken());
                                            if (loginFirebaseModel.getDeviceType().equals("1")) {
                                                jsonData.put("alert", jsonObjectPayload);
                                                finalMap.put("data", jsonObjectPayload);
                                                finalMap.put("sound", "default");
                                                finalMap.put("priority", "High");
                                                finalMap.put("forceStart", "1");
                                                finalMap.put("forceShow", true);
                                                finalMap.put("collapse_key", "Updates Available");
                                                finalMap.put("content_available", true);
                                            } else {
                                                jsonNotification.put("aps", jsonObjectPayload);
                                                jsonNotification.put("sound", "default");
                                                jsonNotification.put("priority", "High");
                                                jsonNotification.put("forceStart", "1");
                                                jsonNotification.put("forceShow", true);
                                                jsonNotification.put("collapse_key", "Updates Available");
                                                jsonNotification.put("content_available", true);
                                                jsonNotification.put("title", notificationBean.getTitle());
                                                jsonNotification.put("body", text);
                                                jsonNotification.put("badge", loginFirebaseModel.getTotalUnreadCount());
                                                finalMap.put("notification", jsonNotification);
                                            }
                                            HashMap<String, String> headerParams = new HashMap<>();
                                            headerParams.put("Content-Type", "application/json");
                                            headerParams.put("Authorization", AppConstants.FIREBASE.FIREBASE_SERVER_KEY);
                                            //chatMessagesRepo.sendPushOnFirebase(finalMap);
                                            AndroidNetworking.post("https://fcm.googleapis.com/fcm/send")
                                                    .addHeaders(headerParams)
                                                    .addJSONObjectBody(finalMap)
                                                    .setPriority(Priority.HIGH)
                                                    .build()
                                                    .getAsString(new StringRequestListener() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            Log.e("AFN Stop Time", System.currentTimeMillis() + "");
                                                        }

                                                        @Override
                                                        public void onError(ANError anError) {
                                                            Log.e("error", anError.getErrorDetail());
                                                        }
                                                    });

                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
