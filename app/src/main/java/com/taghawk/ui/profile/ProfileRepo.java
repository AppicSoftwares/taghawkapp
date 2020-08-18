package com.taghawk.ui.profile;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.block_user.BlockUserModel;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.follow_following.FollowFollowingBean;
import com.taghawk.model.profileresponse.ProfileResponse;

import java.util.HashMap;

public class ProfileRepo {

    public void getProfile(final RichMediatorLiveData<ProfileResponse> changePasswordLiveData, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().getProfileDetails(parms).enqueue(new NetworkCallback<ProfileResponse>() {
            @Override
            public void onSuccess(ProfileResponse successResponse) {
                successResponse.setRequestCode(requestCode);
                changePasswordLiveData.setValue(successResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                changePasswordLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                changePasswordLiveData.setError(t);
            }
        });
    }

    public void followFriend(final RichMediatorLiveData<ProfileResponse> changePasswordLiveData, String userId, final int requestCode) {
        DataManager.getInstance().followFriend(userId).enqueue(new NetworkCallback<ProfileResponse>() {
            @Override
            public void onSuccess(ProfileResponse successResponse) {
                successResponse.setRequestCode(requestCode);
                changePasswordLiveData.setValue(successResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                changePasswordLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                changePasswordLiveData.setError(t);
            }
        });
    }

    public void getFollowFollowing(final RichMediatorLiveData<FollowFollowingBean> liveData, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().getFollowFollowingList(parms).enqueue(new NetworkCallback<FollowFollowingBean>() {
            @Override
            public void onSuccess(FollowFollowingBean successResponse) {
                successResponse.setRequestCode(requestCode);
                liveData.setValue(successResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    public void removeUnfollow(final RichMediatorLiveData<ProfileResponse> liveData, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().removeUnfriend(parms).enqueue(new NetworkCallback<ProfileResponse>() {
            @Override
            public void onSuccess(ProfileResponse successResponse) {
                successResponse.setRequestCode(requestCode);
                liveData.setValue(successResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }


    public void getBlockList(final RichMediatorLiveData<BlockUserModel> blockUserModelRichMediatorLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().getBlockUserList(parms).enqueue(new NetworkCallback<BlockUserModel>() {
            @Override
            public void onSuccess(BlockUserModel successResponse) {
                blockUserModelRichMediatorLiveData.setValue(successResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                blockUserModelRichMediatorLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                blockUserModelRichMediatorLiveData.setError(t);
            }
        });
    }
}
