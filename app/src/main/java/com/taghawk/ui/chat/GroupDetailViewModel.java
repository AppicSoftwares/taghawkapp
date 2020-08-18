package com.taghawk.ui.chat;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.Query;
import com.taghawk.Repository.CartRepo;
import com.taghawk.Repository.HomeRepo;
import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.chat.MemberModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.chat.TagDetailFirebaseModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.pendingRequests.PendingRequestResponse;
import com.taghawk.model.profileresponse.ProfileProductsResponse;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.model.request.User;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tag.TagSearchBean;
import com.taghawk.model.tagaddresponse.AddTagResponse;

import java.util.HashMap;

public class GroupDetailViewModel extends ViewModel {

    private Observer<Throwable> mErrorObserver;
    private Observer<FailureResponse> mFailureObserver;
    private DataManager dataManager;

    private Observer<Boolean> loading;
    private ChatMessagesRepo chatMessagesRepo;
    private RichMediatorLiveData<AddTagResponse> mAddTagLiveData;
    private RichMediatorLiveData<CommonResponse> reportTagLiveData,membersActionLiveData,acceptRejectLiveData;
    private RichMediatorLiveData<ProfileResponse> blockUserLiveData;
    private RichMediatorLiveData<PendingRequestResponse> pendingRequestsLiveData;

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        dataManager=DataManager.getInstance();
        chatMessagesRepo=new ChatMessagesRepo();
        initLiveData();
    }

    private void initLiveData()
    {
        if (mAddTagLiveData == null) {
            mAddTagLiveData = new RichMediatorLiveData<AddTagResponse>() {
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

        if (reportTagLiveData == null) {
            reportTagLiveData = new RichMediatorLiveData<CommonResponse>() {
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
        if (membersActionLiveData == null) {
            membersActionLiveData = new RichMediatorLiveData<CommonResponse>() {
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
        if (acceptRejectLiveData == null) {
            acceptRejectLiveData = new RichMediatorLiveData<CommonResponse>() {
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
        if (pendingRequestsLiveData == null) {
            pendingRequestsLiveData = new RichMediatorLiveData<PendingRequestResponse>() {
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

    Query getGroupDetailQuery(String roomId)
    {
        return dataManager.getGroupNodeQuery(roomId);
    }

    void muteUnmuteChat(boolean isMute,String userId,String roomId)
    {
        dataManager.muteUnmuteChat(isMute,userId,roomId);
    }

    void changeTagType(boolean isPrivate,String tagId)
    {
        dataManager.changeTagType(isPrivate,tagId);
    }

    void changeVerificationType(int type,String tagId)
    {
        dataManager.changeVerificationType(type,tagId);
    }

    void changeVerificationData(String data,String tagId)
    {
        dataManager.changeVerificationData(data,tagId);
    }

    void changeTagName(String data,String tagId)
    {
        dataManager.changeTagName(data,tagId);
    }

    void changeTagImage(String data,String tagId)
    {
        dataManager.changeTagImage(data,tagId);
    }

    void changeAnnouncement(String data,String tagId)
    {
        dataManager.changeAnnouncement(data,tagId);
    }

    void changeDescription(String data,String tagId)
    {
        dataManager.changeDescription(data,tagId);
    }

    void changeTagAddress(String address,double lat,double lon,String tagId)
    {
        dataManager.changeTagAddress(address,lat,lon,tagId);
    }

    public void deleteTag(String userId,HashMap<String,MemberModel> hashMap, String tagId)
    {
        dataManager.deleteTag(userId,hashMap,tagId);
    }

    public void exitTag(String userId,String userName,String tagId)
    {
        dataManager.exitTag(userId,userName,tagId);
    }

    void removeMember(String memberId,String memberName,String tagId)
    {
        dataManager.removeMember(memberId,memberName,tagId);
    }

    void muteUnmuteUser(String tagId, String memberId, boolean isMute)
    {
        dataManager.muteUnmuteUser(tagId,memberId,isMute);
    }

    void transferOwnership(User user, String memberId,String memberName,String tagId)
    {
        dataManager.transferOwnership(user,memberId,memberName,tagId);
    }

    void makeAdmin(String tagId,String memberId,int memberType)
    {
        dataManager.makeAdmin(tagId,memberId,memberType);
    }

    void pinnedChat(boolean isPinned,String userId,String roomId)
    {
        dataManager.pinnedChat(isPinned,userId,roomId);
    }

    void updatePendingCount(String tagId,boolean isDecrease)
    {
        dataManager.updatePendingRequestCount(tagId,isDecrease);
    }

    void joinTagOnFirebase(User user, TagData tagData)
    {
        dataManager.joinTag(user,tagData);
    }

    void editTag(HashMap<String,Object> hashMap,String type)
    {
        chatMessagesRepo.editTag(mAddTagLiveData,hashMap,type);
    }

    void reportTag(HashMap<String,Object> hashMap)
    {
        chatMessagesRepo.reportTag(reportTagLiveData,hashMap);
    }

    void removeMember(HashMap<String,Object> hashMap,String membername,int type)
    {
        chatMessagesRepo.removeMember(membersActionLiveData,hashMap,membername,type);
    }

    void transferOwnership(HashMap<String,Object> hashMap,String membername,int type)
    {
        chatMessagesRepo.transferOwnership(membersActionLiveData,hashMap,membername,type);
    }

    void blockUser(HashMap<String,Object> hashMap,String membername,int type)
    {
        chatMessagesRepo.blockUser(blockUserLiveData,hashMap,membername,type);
    }

    void blockUserOnFirebase(String userId,String otherUserId)
    {
        dataManager.blockUserOnFirebase(userId,otherUserId);
    }

    RichMediatorLiveData<ProfileResponse> getBlockUserLiveData() {
        return blockUserLiveData;
    }

    void deleteTagApi(HashMap<String,Object> hashMap, int type)
    {
        chatMessagesRepo.deleteTag(membersActionLiveData,hashMap,type);
    }

    void exitTagApi(HashMap<String,Object> hashMap,int type)
    {
        chatMessagesRepo.exitTag(membersActionLiveData,hashMap,type);
    }

    void acceptRejectTagRequest(String userId, String communityId, int status) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.USER_ID, userId);
        parms.put(AppConstants.KEY_CONSTENT.COMMUNITY_ID, communityId);
        parms.put(AppConstants.KEY_CONSTENT.STATUS, status);
        chatMessagesRepo.acceptRejectTagRequest(acceptRejectLiveData, parms);
    }

    void getPendingRequests(String tagId)
    {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, 1);
        parms.put(AppConstants.KEY_CONSTENT.COMMUNITY_ID, tagId);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, 200);
        chatMessagesRepo.getPendingRequests(pendingRequestsLiveData,parms);
    }

    RichMediatorLiveData<CommonResponse> getAcceptRejectLiveData() {
        return acceptRejectLiveData;
    }

    RichMediatorLiveData<PendingRequestResponse> getPendingRequestsLiveData() {
        return pendingRequestsLiveData;
    }

    RichMediatorLiveData<AddTagResponse> editTagLiveData() {
        return mAddTagLiveData;
    }

    RichMediatorLiveData<CommonResponse> getReportTagLiveData() {
        return reportTagLiveData;
    }

    RichMediatorLiveData<CommonResponse> getMembersActionLiveData() {
        return membersActionLiveData;
    }

}
