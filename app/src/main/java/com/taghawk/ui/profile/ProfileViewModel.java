package com.taghawk.ui.profile;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.block_user.BlockUserModel;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.follow_following.FollowFollowingBean;
import com.taghawk.model.profileresponse.ProfileResponse;

import java.util.HashMap;

public class ProfileViewModel extends ViewModel {

    private ProfileRepo mProfileRepo = new ProfileRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> loading;
    private Observer<FailureResponse> mFailureObserver;
    private RichMediatorLiveData<ProfileResponse> mProfileLiveData;
    private RichMediatorLiveData<FollowFollowingBean> mFollowFollowingLiveData;
    private RichMediatorLiveData<BlockUserModel> mBlockUSerLiveData;

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    private void initLiveData() {
        if (mProfileLiveData == null) {
            mProfileLiveData = new RichMediatorLiveData<ProfileResponse>() {
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
        if (mFollowFollowingLiveData == null) {
            mFollowFollowingLiveData = new RichMediatorLiveData<FollowFollowingBean>() {
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
        if (mBlockUSerLiveData == null) {
            mBlockUSerLiveData = new RichMediatorLiveData<BlockUserModel>() {
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

    public void getProfile(HashMap<String, Object> params, int requestCode) {

        mProfileRepo.getProfile(mProfileLiveData, params, requestCode);
    }

    public void getFollowFollowingList(String userId, int type, int pageNo, int limit, int requestCode) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.USER_ID, userId);
        parms.put(AppConstants.KEY_CONSTENT.TYPE, type);
        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageNo);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mProfileRepo.getFollowFollowing(mFollowFollowingLiveData, parms, requestCode);
    }

    public void followFriend(String userId, int requestCode) {
        loading.onChanged(true);
        mProfileRepo.followFriend(mProfileLiveData, userId, requestCode);
    }

    public void getBlockUserList(int pageNo, int limit) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageNo);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mProfileRepo.getBlockList(mBlockUSerLiveData, parms);
    }


    public void removeUnfollow(String userId, int action, int requestCode) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.USER_ID, userId);
        parms.put(AppConstants.KEY_CONSTENT.ACTION, action);
        mProfileRepo.removeUnfollow(mProfileLiveData, parms, requestCode);
    }

    public void updateUserNode(String userId,String profilePicture,String fullName,String email)
    {
        DataManager.getInstance().updateUserNodeOnEditProfile(userId,profilePicture,fullName,email);
    }

    public RichMediatorLiveData<ProfileResponse> profileViewModel() {
        return mProfileLiveData;
    }

    public RichMediatorLiveData<FollowFollowingBean> getmFollowFollowingLiveData() {
        return mFollowFollowingLiveData;
    }

    public RichMediatorLiveData<BlockUserModel> getmBlockUSerLiveData() {
        return mBlockUSerLiveData;
    }
}
