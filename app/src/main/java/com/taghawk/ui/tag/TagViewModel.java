package com.taghawk.ui.tag;



import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.Repository.NotificationRepo;
import com.taghawk.Repository.TagRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.request.User;
import com.taghawk.model.tag.MyTagResponse;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tag.TagDetailsModel;

import java.util.HashMap;

public class TagViewModel extends ViewModel {

    private TagRepo mTagRepo = new TagRepo();
    NotificationRepo notificationRepo = new NotificationRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> loading;
    private Observer<FailureResponse> mFailureObserver;
    private RichMediatorLiveData<TagDetailsModel> mTagDetails;
    private RichMediatorLiveData<TagDetailsModel> mTagVisitModel;
    private RichMediatorLiveData<CommonResponse> mJoinMemberLiveData;
    private RichMediatorLiveData<MyTagResponse> myTagsLiveData;
    private RichMediatorLiveData<CommonResponse> cancelPendingRequestLiveData;

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    private void initLiveData() {
        if (mTagDetails == null) {
            mTagDetails = new RichMediatorLiveData<TagDetailsModel>() {
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

        if (mJoinMemberLiveData == null) {
            mJoinMemberLiveData = new RichMediatorLiveData<CommonResponse>() {
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
        if (myTagsLiveData == null) {
            myTagsLiveData = new RichMediatorLiveData<MyTagResponse>() {
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
        if (cancelPendingRequestLiveData == null) {
            cancelPendingRequestLiveData = new RichMediatorLiveData<CommonResponse>() {
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
        if (mTagVisitModel == null) {
            mTagVisitModel = new RichMediatorLiveData<TagDetailsModel>() {
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


    public void hitTagDetails(String tagId, int request, int limit) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.TAG_ID, tagId);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mTagRepo.getTagDetails(mTagDetails, parms, request);
    }


    public void visitTag(String tagId, String productId) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.TAGID, tagId);
        parms.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
        mTagRepo.getTagVisit(mTagVisitModel, parms);
    }

    public void joinTag(HashMap<String, Object> params) {
        mTagRepo.joinTag(mJoinMemberLiveData, params);

    }

    public void joinTagOnFirebase(User user, TagData tagData) {
        DataManager.getInstance().joinTag(user, tagData);
    }

    public void updatePendingRequestCount(String tagId, boolean isDecrease) {
        DataManager.getInstance().updatePendingRequestCount(tagId, isDecrease);
    }

    public void markNotificationRead(String notificationId) {
//        loading.onChanged(true);
        notificationRepo.markNotificationRead(mJoinMemberLiveData, notificationId, AppConstants.REQUEST_CODE.NOTIFICATION_LIST);
    }

    public void acceptRejectTagRequest(String userId, String communityId, int status) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.USER_ID, userId);
        parms.put(AppConstants.KEY_CONSTENT.COMMUNITY_ID, communityId);
        parms.put(AppConstants.KEY_CONSTENT.STATUS, status);
        mTagRepo.acceptRejectTagRequest(cancelPendingRequestLiveData, parms, AppConstants.REQUEST_CODE.ACCEPT_REJECT_TAG_REQUEST);
    }

    public RichMediatorLiveData<TagDetailsModel> mGetTagDetailsViewModel() {
        return mTagDetails;
    }

    public RichMediatorLiveData<TagDetailsModel> mGetVisitedTagViewModel() {
        return mTagVisitModel;
    }

    public RichMediatorLiveData<CommonResponse> getmJoinMemberLiveData() {
        return mJoinMemberLiveData;
    }

    public void getMyTags() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(AppConstants.KEY_CONSTENT.PAGE_NO, 1);
        params.put(AppConstants.KEY_CONSTENT.LIMIT, 200);
        mTagRepo.myTags(myTagsLiveData, params);
    }

    public RichMediatorLiveData<MyTagResponse> getMyTagsLiveData() {
        return myTagsLiveData;
    }

    public RichMediatorLiveData<CommonResponse> cancelPendingRequest() {
        return cancelPendingRequestLiveData;
    }
}
