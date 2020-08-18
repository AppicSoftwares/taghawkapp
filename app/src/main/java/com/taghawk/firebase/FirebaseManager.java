package com.taghawk.firebase;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.chat.ChatPushModel;
import com.taghawk.model.chat.MemberModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.chat.TagDetailFirebaseModel;
import com.taghawk.model.login.LoginFirebaseModel;
import com.taghawk.model.request.User;
import com.taghawk.model.tag.TagData;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class FirebaseManager {

    private static volatile FirebaseManager instance;
    private DatabaseReference databaseReference;

    private FirebaseManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            synchronized (FirebaseManager.class) {
                if (instance == null)
                    instance = new FirebaseManager();
            }
        }
        return instance;
    }

    protected FirebaseManager readResolve() {
        return getInstance();
    }

    public String getFirebaseGeneratedId() {
        return databaseReference.push().getKey();
    }

    public static String getFirebaseRoomId(String userId, String otherUserId) {
        if (userId == null || otherUserId == null)
            return "";
        int value = userId.compareTo(otherUserId);
        String key;
        if (value > 0)
            key = otherUserId + "-" + userId;
        else
            key = userId + "-" + otherUserId;
        return key;
    }

    public void createFirebaseUser(final LoginFirebaseModel loginFirebaseModel) {
        if (!TextUtils.isEmpty(loginFirebaseModel.getUserId())) {
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(loginFirebaseModel.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        LoginFirebaseModel loginFirebaseModel1 = dataSnapshot.getValue(LoginFirebaseModel.class);
                        loginFirebaseModel.setTotalUnreadCount(loginFirebaseModel1.getTotalUnreadCount());
                        loginFirebaseModel.setMyTags(loginFirebaseModel1.getMyTags() != null ? loginFirebaseModel1.getMyTags() : "");
                    }
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(loginFirebaseModel.getUserId()).setValue(loginFirebaseModel);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public Query getUserNodeQuery(String userId) {
        return databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId);
    }


    public void blockUserOnFirebase(final String userId, final String otherUserId) {
        String singleRoomId = getFirebaseRoomId(userId, otherUserId);
        updateTotalUnreadCountForBlock(userId, singleRoomId);
        updateTotalUnreadCountForBlock(otherUserId, singleRoomId);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                    if (loginFirebaseModel.getMyTags() != null && !loginFirebaseModel.getMyTags().equalsIgnoreCase("")) {
                        String[] myTags = loginFirebaseModel.getMyTags().split(",");
                        if (myTags.length > 0) {
                            for (String tagId : myTags) {
                                if (!tagId.equalsIgnoreCase("")) {
                                    updateTotalUnreadCountForBlock(otherUserId, tagId);
                                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).child(AppConstants.FIREBASE.FIREBASE_KEY_MEMBERS).child(otherUserId).removeValue();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                    if (loginFirebaseModel.getMyTags() != null && !loginFirebaseModel.getMyTags().equalsIgnoreCase("")) {
                        String[] myTags = loginFirebaseModel.getMyTags().split(",");
                        if (myTags.length > 0) {
                            for (String tagId : myTags) {
                                if (!tagId.equalsIgnoreCase("")) {
                                    updateTotalUnreadCountForBlock(userId, tagId);
                                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).child(AppConstants.FIREBASE.FIREBASE_KEY_MEMBERS).child(userId).removeValue();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendMessageToUser(User user, boolean isNewUser, boolean isOtherUserCreated, final String title, final String text, final ChatModel chatModel,final CountUpdateListener countUpdateListener) {
        final String otherUserId = chatModel.getOtherUserId();
        final String userId = user.getUserId();
        final String key = getFirebaseRoomId(userId, otherUserId);
        final MessageModel lastMessage = chatModel.getLastMessage();
        lastMessage.setRoomId(key);
        if (isNewUser)
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(key).setValue(chatModel);
        if (!isOtherUserCreated) {
            chatModel.setOtherUserId(userId);
            chatModel.setRoomImage(user.getProfilePicture());
            chatModel.setRoomName(user.getFullName());
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(otherUserId).child(key).setValue(chatModel);
        }
        if (lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RESERVE_ITEM)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RELEASE_PAYMENT)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_DECLINE)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_ACCEPT)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_RELEASE)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE_RESPONSE)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE_RESPONSE)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE_RESPONSE)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_OPEN_DISPUTE)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_REQUEST)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_DISPUTE)
                || lastMessage.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_ACCEPT_DISPUTE)) {
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(key).child(AppConstants.FIREBASE.FIREBASE_KEY_LAST_MESSAGE).setValue(lastMessage);
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(otherUserId).child(key).child(AppConstants.FIREBASE.FIREBASE_KEY_LAST_MESSAGE).setValue(lastMessage);
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(otherUserId).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        final ChatModel chatModel1 = dataSnapshot.getValue(ChatModel.class);
                        int unreadCount = chatModel1.getUnreadMessageCount();
                        unreadCount += 1;
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_UNREAD_MESSAGE_COUNT, unreadCount);
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(otherUserId).child(key).updateChildren(hashMap);
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                    int count = loginFirebaseModel.getTotalUnreadCount();
                                    count = count + 1;
                                    loginFirebaseModel.setTotalUnreadCount(count);
                                    if (!chatModel.getLastMessage().getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_PRODUCT_CHANGE_HEADER))
                                        sendPushForChat(title, text, chatModel1, loginFirebaseModel);
                                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(otherUserId).setValue(loginFirebaseModel);
                                    if (countUpdateListener!=null)
                                        countUpdateListener.isCountUpdated(true);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                if (countUpdateListener!=null)
                                    countUpdateListener.isCountUpdated(false);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(key).child(lastMessage.getMessageId()).setValue(chatModel.getLastMessage());
    }

    public void sendMessageToGroup(final String userId, final String roomId, final String title, final String text, final MessageModel messageModel) {
        if (!messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_DATE_HEADER) && !messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TIME_HEADER)) {
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        TagDetailFirebaseModel tagDetailFirebaseModel = dataSnapshot.getValue(TagDetailFirebaseModel.class);
                        HashMap<String, MemberModel> membersMap = tagDetailFirebaseModel.getMembers();
                        int memberCount = membersMap.size();
                        for (final Map.Entry<String, MemberModel> entry : membersMap.entrySet()) {
                            if (!entry.getKey().equalsIgnoreCase(userId))
                                databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(entry.getKey()).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            final ChatModel chatModel1 = dataSnapshot.getValue(ChatModel.class);
                                            int unreadCount = chatModel1.getUnreadMessageCount();
                                            unreadCount += 1;
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_UNREAD_MESSAGE_COUNT, unreadCount);
                                            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(entry.getKey()).child(roomId).updateChildren(hashMap);
                                            databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(entry.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getValue() != null) {
                                                        LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                                        int count = loginFirebaseModel.getTotalUnreadCount();
                                                        count = count + 1;
                                                        loginFirebaseModel.setTotalUnreadCount(count);
                                                        if (messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TEXT) || messageModel.getMessageType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_IMAGE))
                                                            sendPushForChat(title, text, chatModel1, loginFirebaseModel);
                                                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(entry.getKey()).setValue(loginFirebaseModel);
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
                                });
                        }
                        messageModel.setMemberCount(memberCount);
                        messageModel.setReadCount(1);
                        messageModel.setRoomId(roomId);
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(roomId).child(AppConstants.FIREBASE.FIREBASE_KEY_LAST_MESSAGE).setValue(messageModel);
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(roomId).child(messageModel.getMessageId()).setValue(messageModel);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    messageModel.setMemberCount(0);
                    messageModel.setReadCount(0);
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(roomId).child(AppConstants.FIREBASE.FIREBASE_KEY_LAST_MESSAGE).setValue(messageModel);
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(roomId).child(messageModel.getMessageId()).setValue(messageModel);
                }
            });
        } else {
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(roomId).child(messageModel.getMessageId()).setValue(messageModel);
        }
    }


    private void sendPushForChat(final String title, final String text, final ChatModel chatModel, final LoginFirebaseModel loginFirebaseModel) {
        try {
            final JSONObject finalMap = new JSONObject();
            final JSONObject jsonData = new JSONObject();
            final JSONObject jsonNotification = new JSONObject();
            final ChatPushModel notificationBean = new ChatPushModel();
            notificationBean.setEntityId(chatModel.getRoomId());
            notificationBean.setTitle(title);
            notificationBean.setType(AppConstants.FIREBASE.PUSH_TYPE_MESSAGE);
            if (!chatModel.isChatMute()) {
                JSONObject jsonObjectPayload = new JSONObject();
                jsonObjectPayload.put("type", AppConstants.FIREBASE.PUSH_TYPE_MESSAGE);
                jsonObjectPayload.put("entityId", chatModel.getRoomId());
                jsonObjectPayload.put("title", title);
                jsonObjectPayload.put("userId", loginFirebaseModel.getUserId());
                jsonObjectPayload.put("text", text);
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

        }
    }

    public void addTagOnFirebase(final User user, final TagData tagData) {
        final ChatModel chatModel = getDefaultChatModelForTag(user, tagData);
        final TagDetailFirebaseModel tagDetailFirebaseModel = getTagDetailData(user, tagData);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                    String myTags = loginFirebaseModel.getMyTags();
                    if (myTags.equalsIgnoreCase(""))
                        myTags = tagData.getTagId();
                    else
                        myTags = myTags + "," + tagData.getTagId();
                    loginFirebaseModel.setMyTags(myTags);
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(user.getUserId()).setValue(loginFirebaseModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(tagData.getTagId()).setValue(chatModel);
        MessageModel dateHeaderMessageModel = getMessageModelForTag(user, tagData.getTagId(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_DATE_HEADER);
        dateHeaderMessageModel.setMessageText(user.getUserId());
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(tagData.getTagId()).child(dateHeaderMessageModel.getMessageId()).setValue(dateHeaderMessageModel);
        tagDetailFirebaseModel.getLastMessage().setMessageText(user.getUserId());
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(tagData.getTagId()).child(tagDetailFirebaseModel.getLastMessage().getMessageId()).setValue(tagDetailFirebaseModel.getLastMessage());
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagData.getTagId()).setValue(tagDetailFirebaseModel);
    }

    public void editTagOnFirebase(TagData tagData) {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (tagData.getTagName() != null)
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_NAME, tagData.getTagName());
        if (tagData.getAnnouncement() != null)
            hashMap.put(AppConstants.TAG_KEY_CONSTENT.ANNOUNCEMENT, tagData.getAnnouncement());
        if (tagData.getDescription() != null)
            hashMap.put(AppConstants.TAG_KEY_CONSTENT.DESCRIPTION, tagData.getDescription());
        if (tagData.getTagAddress() != null)
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_ADDRESS, tagData.getTagAddress());
        if (tagData.getTagImageUrl() != null)
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_IMAGE_URL, tagData.getTagImageUrl());
        if (tagData.getTagLatitude() != null)
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_LATITUDE, Double.parseDouble(tagData.getTagLatitude()));
        if (tagData.getTagLongitude() != null)
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_LONGITUDE, Double.parseDouble(tagData.getTagLongitude()));
        if (tagData.getTagType() != 0)
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_TYPE, tagData.getTagType());
        if (tagData.getJoinTagData() != null)
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_VERIFICATION_DATA, tagData.getJoinTagData());
        if (tagData.getJoinTagBy() != 0)
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_VERIFICATION_TYPE, tagData.getJoinTagBy());
        if (hashMap.size() > 0)
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagData.getTagId()).updateChildren(hashMap);
    }

    public void joinTag(User user, TagData tagData) {
        final ChatModel chatModel = getDefaultChatModelForTag(user, tagData);
        MessageModel messageModel = getMessageModelForTag(user, tagData.getTagId(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_USER_JOIN_HEADER);
        messageModel.setMessageText(user.getUserId());
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(tagData.getTagId()).setValue(chatModel);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(tagData.getTagId()).child(messageModel.getMessageId()).setValue(messageModel);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagData.getTagId()).child(AppConstants.FIREBASE.FIREBASE_KEY_LAST_MESSAGE).setValue(messageModel);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagData.getTagId()).child(AppConstants.FIREBASE.FIREBASE_KEY_MEMBERS).child(user.getUserId()).setValue(getMemberModel(user));
    }

    public void updatePendingRequestcount(final String tagId, final boolean isDecrease) {
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    TagDetailFirebaseModel tagDetailFirebaseModel = dataSnapshot.getValue(TagDetailFirebaseModel.class);
                    int pendingReqCount = tagDetailFirebaseModel.getPendingRequestCount();
                    if (isDecrease)
                        pendingReqCount -= 1;
                    else
                        pendingReqCount += 1;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_PENDING_REQUEST_COUNT, pendingReqCount);
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private MemberModel getMemberModel(User user) {
        MemberModel memberModel = new MemberModel();
        memberModel.setMemberType(AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_MEMBER);
        memberModel.setBlocked(false);
        memberModel.setMemberId(user.getUserId());
        memberModel.setMemberImage(user.getProfilePicture());
        memberModel.setMemberName(user.getFullName());
        memberModel.setMute(false);
        return memberModel;
    }

    private TagDetailFirebaseModel getTagDetailData(User user, TagData tagData) {
        TagDetailFirebaseModel tagDetailFirebaseModel = new TagDetailFirebaseModel();
        tagDetailFirebaseModel.setTagId(tagData.getTagId());
        tagDetailFirebaseModel.setDescription(tagData.getDescription());
        tagDetailFirebaseModel.setMembers(getMembers(user));
        tagDetailFirebaseModel.setShareCode(tagData.getShareCode());
        tagDetailFirebaseModel.setShareLink(tagData.getShareLink());
        tagDetailFirebaseModel.setTagAddress(tagData.getTagAddress());
        tagDetailFirebaseModel.setTagImageUrl(tagData.getTagImageUrl());
        tagDetailFirebaseModel.setTagLatitude(Double.parseDouble(tagData.getTagLatitude()));
        tagDetailFirebaseModel.setTagName(tagData.getTagName());
        tagDetailFirebaseModel.setTagStatus(tagData.getTagStatus());
        tagDetailFirebaseModel.setTagLongitude(Double.parseDouble(tagData.getTagLongitude()));
        tagDetailFirebaseModel.setTagType(tagData.getTagType());
        tagDetailFirebaseModel.setAnnouncement(tagData.getAnnouncement());
        tagDetailFirebaseModel.setLastMessage(getMessageModelForTag(user, tagData.getTagId(), AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_TAG_CREATED_HEADER));
        tagDetailFirebaseModel.setOwnerId(user.getUserId());
        if (tagData.getTagType() == 1) {
            tagDetailFirebaseModel.setVerificationType(tagData.getJoinTagBy());
            tagDetailFirebaseModel.setVerificationData(tagData.getJoinTagData());
        } else {
            tagDetailFirebaseModel.setVerificationType(0);
            tagDetailFirebaseModel.setVerificationData("");
        }
        return tagDetailFirebaseModel;
    }

    private ChatModel getDefaultChatModelForTag(User user, TagData tagData) {
        ChatModel chatModel = new ChatModel();
        chatModel.setChatMute(false);
        chatModel.setPinned(false);
        chatModel.setMute(false);
        try {
            chatModel.setCreatedTimeStamp(ServerValue.TIMESTAMP);
        } catch (Exception e) {
            chatModel.setCreatedTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
            e.printStackTrace();
        }
        chatModel.setChatType(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT);
        chatModel.setRoomName(tagData.getTagName());
        chatModel.setRoomImage(tagData.getTagImageUrl());
        chatModel.setUserType(String.valueOf(user.getUserType()));
        chatModel.setRoomId(tagData.getTagId());
        return chatModel;
    }

    private MessageModel getMessageModelForTag(User user, String roomId, String messageType) {
        MessageModel lastMessage = new MessageModel();
        lastMessage.setMessageId(getFirebaseGeneratedId());
        lastMessage.setMessageText(user.getFullName());
        lastMessage.setMessageStatus(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_DELIVERED);
        lastMessage.setMessageType(messageType);
        lastMessage.setSenderId(user.getUserId());
        try {
            lastMessage.setTimeStamp(ServerValue.TIMESTAMP);
        } catch (Exception e) {
            lastMessage.setTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
            e.printStackTrace();
        }
        lastMessage.setRoomId(roomId);
        lastMessage.setSenderImage(user.getProfilePicture());
        lastMessage.setSenderName(user.getFullName());
        lastMessage.setMemberCount(0);
        lastMessage.setReadCount(0);
        return lastMessage;
    }

    private HashMap<String, MemberModel> getMembers(User user) {
        HashMap<String, MemberModel> members = new HashMap<>();
        MemberModel mineMemberModel = new MemberModel();
        mineMemberModel.setMemberId(user.getUserId());
        mineMemberModel.setMemberImage(user.getProfilePicture() == null ? "" : user.getProfilePicture());
        mineMemberModel.setMemberName(user.getFullName());
        mineMemberModel.setMemberType(AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_OWNER);
        mineMemberModel.setBlocked(false);
        mineMemberModel.setMute(false);
        members.put(user.getUserId(), mineMemberModel);
        return members;
    }

    public void updateTotalUnreadCountForBlock(final String userId, final String roomId) {
        FirebaseDatabase.getInstance().getReference().child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ChatModel chatModel1 = dataSnapshot.getValue(ChatModel.class);
                    final int unreadCount = chatModel1.getUnreadMessageCount();
                    FirebaseDatabase.getInstance().getReference().child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).removeValue();
                    if (unreadCount > 0)
                        FirebaseDatabase.getInstance().getReference().child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                    int count = loginFirebaseModel.getTotalUnreadCount();
                                    count = count - unreadCount;
                                    if (count < 0)
                                        count = 0;
                                    loginFirebaseModel.setTotalUnreadCount(count);
                                    FirebaseDatabase.getInstance().getReference().child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).setValue(loginFirebaseModel);
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
        });
    }

    public void updateUnreadCount(final String userId, final String roomId) {
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getValue() != null) {
                        ChatModel chatModel1 = dataSnapshot.getValue(ChatModel.class);
                        final int unreadCount = chatModel1.getUnreadMessageCount();
                        if (unreadCount > 0) {
                            final HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_UNREAD_MESSAGE_COUNT, 0);
                            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).updateChildren(hashMap);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                        int count = loginFirebaseModel.getTotalUnreadCount();
                                        count = count - unreadCount;
                                        if (count < 0)
                                            count = 0;
                                        loginFirebaseModel.setTotalUnreadCount(count);
                                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).setValue(loginFirebaseModel);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void deleteUserChat(final String userId, final String roomId) {
        String otherUserId = "";
        String[] ids = roomId.split("-");
        if (ids.length == 2) {
            if (ids[0].equalsIgnoreCase(userId))
                otherUserId = ids[1];
            else
                otherUserId = ids[0];
        }
        if (!otherUserId.equalsIgnoreCase(""))
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(otherUserId).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists())
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(roomId).removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ChatModel chatModel1 = dataSnapshot.getValue(ChatModel.class);
                    final int unreadCount = chatModel1.getUnreadMessageCount();
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).removeValue();
                    if (unreadCount > 0)
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                    int count = loginFirebaseModel.getTotalUnreadCount();
                                    count = count - unreadCount;
                                    if (count < 0)
                                        count = 0;
                                    loginFirebaseModel.setTotalUnreadCount(count);
                                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).setValue(loginFirebaseModel);
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
        });
    }

    public void updateMesssageStatus(final String messageId, final String roomId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_MESSAGE_STATUS, AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_READ);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(roomId).child(messageId).updateChildren(hashMap);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(roomId).child(messageId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    int readCount = messageModel.getReadCount();
                    readCount += 1;
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_READ_COUNT, readCount);
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(roomId).child(messageId).updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void muteUnmuteChat(boolean isMute, String userId, String roomId) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_CHAT_MUTE, isMute);
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeTagType(boolean isPrivate, String tagId) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_TYPE, isPrivate ? 1 : 2);
            if (isPrivate) {
                hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_VERIFICATION_TYPE, AppConstants.TAG_VERIFICATION_METHOD.DOCUMENT);
                hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_VERIFICATION_DATA, "");
            } else {
                hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_VERIFICATION_TYPE, 0);
                hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_VERIFICATION_DATA, "");
            }
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeVerificationType(int type, String tagId) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_VERIFICATION_TYPE, type);
            if (type == AppConstants.TAG_VERIFICATION_METHOD.DOCUMENT)
                hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_VERIFICATION_DATA, "");
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeVerifcationData(String data, String tagId) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("verificationData", data);
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeTagAddress(String address, double lat, double lon, String tagId) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_ADDRESS, address);
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_LONGITUDE, lon);
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_LATITUDE, lat);
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeTagImage(String data, String tagId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_IMAGE_URL, data);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).updateChildren(hashMap);
    }

    public void changeTagName(String data, String tagId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_TAG_NAME, data);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).updateChildren(hashMap);
    }

    public void changeAnnouncement(String data, String tagId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(AppConstants.TAG_KEY_CONSTENT.ANNOUNCEMENT, data);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).updateChildren(hashMap);
    }

    public void changeDescription(String data, String tagId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(AppConstants.TAG_KEY_CONSTENT.DESCRIPTION, data);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).updateChildren(hashMap);
    }


    public void deleteTag(final String userId, HashMap<String, MemberModel> membersMap, final String tagId) {
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                    String[] myTags = loginFirebaseModel.getMyTags().split(",");
                    if (myTags.length > 0) {
                        String newTags = "";
                        for (String tag : myTags) {
                            if (!tagId.equalsIgnoreCase(tag)) {
                                if (newTags.equalsIgnoreCase(""))
                                    newTags = tag;
                                else
                                    newTags = newTags + "," + tag;
                            }
                        }
                        loginFirebaseModel.setMyTags(newTags);
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).setValue(loginFirebaseModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        for (final Map.Entry<String, MemberModel> entry : membersMap.entrySet()) {
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(entry.getKey()).child(tagId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        ChatModel chatModel1 = dataSnapshot.getValue(ChatModel.class);
                        final int unreadCount = chatModel1.getUnreadMessageCount();
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(entry.getKey()).child(tagId).removeValue();
                        if (unreadCount > 0)
                            databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(entry.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                        int count = loginFirebaseModel.getTotalUnreadCount();
                                        count = count - unreadCount;
                                        if (count < 0)
                                            count = 0;
                                        loginFirebaseModel.setTotalUnreadCount(count);
                                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(entry.getKey()).setValue(loginFirebaseModel);
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
            });
        }
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(tagId).removeValue();
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).removeValue();
    }

    public void exitTag(final String userId, String userName, final String tagId) {
        MessageModel lastMessage = new MessageModel();
        lastMessage.setMessageId(getFirebaseGeneratedId());
        lastMessage.setMessageText(userId);
        lastMessage.setMessageStatus(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_DELIVERED);
        lastMessage.setMessageType(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_USER_LEFT_HEADER);
        lastMessage.setSenderId(userId);
        try {
            lastMessage.setTimeStamp(ServerValue.TIMESTAMP);
        } catch (Exception e) {
            lastMessage.setTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
            e.printStackTrace();
        }
        lastMessage.setRoomId(tagId);
        lastMessage.setSenderImage("");
        lastMessage.setSenderName(userName);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(tagId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ChatModel chatModel1 = dataSnapshot.getValue(ChatModel.class);
                    final int unreadCount = chatModel1.getUnreadMessageCount();
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(tagId).removeValue();
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                int count = loginFirebaseModel.getTotalUnreadCount();
                                count = count - unreadCount;
                                if (count < 0)
                                    count = 0;
                                loginFirebaseModel.setTotalUnreadCount(count);
                                databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).setValue(loginFirebaseModel);
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
        });
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(tagId).child(lastMessage.getMessageId()).setValue(lastMessage);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).child(AppConstants.FIREBASE.FIREBASE_KEY_LAST_MESSAGE).setValue(lastMessage);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).child(AppConstants.FIREBASE.FIREBASE_KEY_MEMBERS).child(userId).removeValue();
    }

    public void removeMember(final String memberId, String memberName, final String tagId) {
        User user = DataManager.getInstance().getUserDetails();
        MessageModel lastMessage = new MessageModel();
        lastMessage.setMessageId(getFirebaseGeneratedId());
        lastMessage.setMessageText(memberId);
        lastMessage.setSenderId(user.getUserId());
        lastMessage.setSenderImage(user.getProfilePicture());
        lastMessage.setSenderName(user.getFullName());
        lastMessage.setMessageStatus(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_DELIVERED);
        lastMessage.setMessageType(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_USER_REMOVED_HEADER);
        try {
            lastMessage.setTimeStamp(ServerValue.TIMESTAMP);
        } catch (Exception e) {
            lastMessage.setTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
            e.printStackTrace();
        }
        lastMessage.setRoomId(tagId);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(tagId).child(lastMessage.getMessageId()).setValue(lastMessage);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).child(AppConstants.FIREBASE.FIREBASE_KEY_LAST_MESSAGE).setValue(lastMessage);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).child(AppConstants.FIREBASE.FIREBASE_KEY_MEMBERS).child(memberId).removeValue();
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(memberId).child(tagId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ChatModel chatModel1 = dataSnapshot.getValue(ChatModel.class);
                    final int unreadCount = chatModel1.getUnreadMessageCount();
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(memberId).child(tagId).removeValue();
                    if (unreadCount > 0)
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                                    int count = loginFirebaseModel.getTotalUnreadCount();
                                    count = count - unreadCount;
                                    if (count < 0)
                                        count = 0;
                                    loginFirebaseModel.setTotalUnreadCount(count);
                                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(memberId).setValue(loginFirebaseModel);
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
        });
    }

    public void makeGroupAdmin(String tagId, String memberId, int memberType) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_MEMBER_TYPE, memberType);
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).child(AppConstants.FIREBASE.FIREBASE_KEY_MEMBERS).child(memberId).updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void transferOwnership(final User user, final String memberId, String memberName, final String tagId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_MEMBER_TYPE, AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_OWNER);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).child(AppConstants.FIREBASE.FIREBASE_KEY_MEMBERS).child(memberId).updateChildren(hashMap);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(memberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                    String myTags = loginFirebaseModel.getMyTags();
                    if (myTags.equalsIgnoreCase(""))
                        myTags = tagId;
                    else
                        myTags = myTags + "," + tagId;
                    loginFirebaseModel.setMyTags(myTags);
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(memberId).setValue(loginFirebaseModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_MEMBER_TYPE, AppConstants.FIREBASE.FIREBASE_MEMBER_TYPE_MEMBER);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).child(AppConstants.FIREBASE.FIREBASE_KEY_MEMBERS).child(user.getUserId()).updateChildren(hashMap);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                    String[] myTags = loginFirebaseModel.getMyTags().split(",");
                    if (myTags.length > 0) {
                        String newTags = "";
                        for (String tag : myTags) {
                            if (!tagId.equalsIgnoreCase(tag)) {
                                if (newTags.equalsIgnoreCase(""))
                                    newTags = tag;
                                else
                                    newTags = newTags + "," + tag;
                            }
                        }
                        loginFirebaseModel.setMyTags(newTags);
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(user.getUserId()).setValue(loginFirebaseModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        hashMap.clear();
        hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_OWNER_ID, memberId);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).updateChildren(hashMap);
        MessageModel messageModel = getMessageModelForTag(user, tagId, AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_OWNERSHIP_TRANSFER_HEADER);
        messageModel.setMessageText(memberId);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).child(AppConstants.FIREBASE.FIREBASE_KEY_LAST_MESSAGE).setValue(messageModel);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(tagId).child(messageModel.getMessageId()).setValue(messageModel);
    }

    public void pinnedChat(boolean isPinned, String userId, String roomId) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_PINNED, isPinned);
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void muteUnmuteUser(String tagId, String memberId, boolean isMute) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_MUTE, isMute);
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(tagId).child(AppConstants.FIREBASE.FIREBASE_KEY_MEMBERS).child(memberId).updateChildren(hashMap);
            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(memberId).child(tagId).updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateGroupDataOnRoomNode(final String userId, final String roomId, final String roomName, final String roomImage) {
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_ROOM_NAME, roomName);
                    hashMap.put(AppConstants.FIREBASE.FIREBASE_KEY_ROOM_IMAGE, roomImage);
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateDeviceToken(final String userId, final String token) {
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                    if (loginFirebaseModel != null) {
                        loginFirebaseModel.setDeviceToken(token);
                        loginFirebaseModel.setDeviceType("1");
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).setValue(loginFirebaseModel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateUserNodeOnEditProfile(final String userId, final String profilePicture, final String fullName, final String email) {
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                    loginFirebaseModel.setFullName(fullName);
                    loginFirebaseModel.setProfilePicture(profilePicture);
                    loginFirebaseModel.setEmail(email);
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_USER_NODE).child(userId).setValue(loginFirebaseModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateProductInfo(String userId, String otherUserId, String roomId, ChatProductModel chatProductModel) {
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(otherUserId).child(roomId).child(AppConstants.FIREBASE.FIREBASE_KEY_PRODUCT_INFO).setValue(chatProductModel);
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).child(roomId).child(AppConstants.FIREBASE.FIREBASE_KEY_PRODUCT_INFO).setValue(chatProductModel);
    }

    public DatabaseReference getUserChatsQuery(String userId) {
        return databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId);
    }

    public Query getUserChatsQueryForNewlyAddedInbox(String userId) {
        return databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(userId).orderByChild(AppConstants.FIREBASE.FIREBASE_KEY_CREATED_TIMESTAMP).startAt(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
    }

    public Query getUserMessagesQuery(String roomId, long endIndex, long createdTimeStamp) {
        return databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(roomId).orderByChild(AppConstants.FIREBASE.TIMESTAMP).startAt(createdTimeStamp, AppConstants.FIREBASE.TIMESTAMP).endAt(endIndex, AppConstants.FIREBASE.TIMESTAMP).limitToLast(100);
    }

    public Query getNewMessageQuery(String roomId, long startIndex) {
        return databaseReference.child(AppConstants.FIREBASE.FIREBASE_MESSAGES_NODE).child(roomId).orderByChild(AppConstants.FIREBASE.TIMESTAMP).startAt(startIndex, AppConstants.FIREBASE.TIMESTAMP);
    }

    public Query getOtherUserNodeQuery(String otherUserId, String roomId) {
        return databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(otherUserId).child(roomId);
    }

    public Query getGroupNodeQuery(String roomId) {
        return databaseReference.child(AppConstants.FIREBASE.FIREBASE_TAGS_DETAIL_NODE).child(roomId);
    }

    public interface CountUpdateListener {
        void isCountUpdated(boolean isUpdated);
    }
}
