package com.taghawk.Repository;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.NotificationModel;
import com.taghawk.model.commonresponse.CommonResponse;

/**
 * Created by Appinventiv on 24-01-2019.
 */

public class NotificationRepo {

    //Get Notification List
    public void hitGetNotification(final RichMediatorLiveData<NotificationModel> liveData, int page, int limit, final int requestCode) {

        DataManager.getInstance().getNotification(page, limit).enqueue(new NetworkCallback<NotificationModel>() {
            @Override
            public void onSuccess(NotificationModel userResponse) {
                userResponse.setRequestCode(requestCode);
                liveData.setValue(userResponse);
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

    //This Api is use for mark notification read
    public void markNotificationRead(final RichMediatorLiveData<CommonResponse> liveData, String notificationId, final int requestCode) {

        DataManager.getInstance().markNotificationRead(notificationId).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse userResponse) {
                userResponse.setRequestCode(requestCode);
                liveData.setValue(userResponse);
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

    // This Api is use for ON/OFF notification
    public void notificationOnOff(final RichMediatorLiveData<CommonResponse> mLiveData, int type) {
        DataManager.getInstance().notificationOnOff(type).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    mLiveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                mLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                mLiveData.setError(t);
            }
        });
    }

}
