package com.taghawk.ui.chat;

import android.location.Location;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.taghawk.Repository.CartRepo;
import com.taghawk.Repository.HomeRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.MemberModel;
import com.taghawk.model.chat.TagDetailFirebaseModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.DeleteProductRequest;
import com.taghawk.model.home.LikeUnLike;
import com.taghawk.model.home.ProductDetailsModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.model.profileresponse.UserDetail;
import com.taghawk.model.request.User;
import com.taghawk.model.tag.TagModel;
import com.taghawk.model.tag.TagSearchBean;
import com.taghawk.ui.home.HomeFragment;

import java.util.HashMap;

public class MessagesViewModel extends ViewModel {

    private Observer<Throwable> mErrorObserver;
    private MutableLiveData<Integer> mActionLiveData;
    private Observer<FailureResponse> mFailureObserver;

    private MediatorLiveData<ChatModel> mediatorChatLiveData;
    private Observer<Boolean> loading;
    private RichMediatorLiveData<CommonResponse> membersActionLiveData;
    private ChatMessagesRepo chatMessagesRepo;
    private DataManager dataManager;


    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        chatMessagesRepo=new ChatMessagesRepo();
        dataManager=DataManager.getInstance();
        initLiveData();
    }

    public MutableLiveData<Integer> getActionLiveData() {
        return mActionLiveData;
    }

    public void setActionData(int actionData) {
        mActionLiveData.setValue(actionData);
    }

    /**
     * Method is used to initialize live data objects
     */
    private void initLiveData() {
        if (mActionLiveData == null) {
            mActionLiveData = new MutableLiveData<>();
        }
        if (mediatorChatLiveData == null) {
            mediatorChatLiveData = new RichMediatorLiveData<ChatModel>() {
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
    }

    public MediatorLiveData<ChatModel> getUserChatsLiveData()
    {
        return mediatorChatLiveData;
    }

    DatabaseReference getUserChatsQuery(String userId)
    {
        return dataManager.getUserChatsQuery(userId);
    }

    Query getUserChatsQueryForNewlyAddedInbox(String userId)
    {
        return dataManager.getUserChatsQueryForNewlyAddedInbox(userId);
    }

    Query getGroupNodeQuery(String roomId)
    {
        return dataManager.getGroupNodeQuery(roomId);
    }

    public Query getUserNodeQuery(String userId)
    {
        return dataManager.getUserNodeQuery(userId);
    }

    void updateGroupDataOnRoomNode(String userId,String roomId,String roomName,String roomImage)
    {
        dataManager.updateGroupDataOnRoomNode(userId,roomId,roomName,roomImage);
    }

    void deleteUserChat(String userId, String roomId)
    {
        dataManager.deleteUserChat(userId,roomId);
    }

    public void deleteTag(String userId,HashMap<String, MemberModel> hashMap, String tagId)
    {
        dataManager.deleteTag(userId,hashMap,tagId);
    }

    public void exitTag(String userId,String userName,String tagId)
    {
        dataManager.exitTag(userId,userName,tagId);
    }

    void deleteTagApi(HashMap<String,Object> hashMap,int type)
    {
        chatMessagesRepo.deleteTag(membersActionLiveData,hashMap,type);
    }

    void exitTagApi(HashMap<String,Object> hashMap,int type)
    {
        chatMessagesRepo.exitTag(membersActionLiveData,hashMap,type);
    }

    RichMediatorLiveData<CommonResponse> getMembersActionLiveData() {
        return membersActionLiveData;
    }

}
