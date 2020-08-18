package com.taghawk.ui.notification;



import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.Repository.NotificationRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.NotificationModel;
import com.taghawk.model.commonresponse.CommonResponse;

/**
 * Created by Appinventiv on 24-01-2019.
 */

public class NotificationViewModel extends ViewModel {
    NotificationRepo repo = new NotificationRepo();
    private Observer<FailureResponse> mFailureResponseObserver;
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> progressLoading;
    private RichMediatorLiveData<NotificationModel> mNotificationLiveModel;
    private RichMediatorLiveData<CommonResponse> notificationReadLiveData;

    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureObserver, Observer<Boolean> isLoading) {
        mFailureResponseObserver = failureObserver;
        mErrorObserver = errorObserver;
        progressLoading = isLoading;
        initLiveData();
    }

    // initialize Live Data
    private void initLiveData() {
        if (mNotificationLiveModel == null) {
            mNotificationLiveModel = new RichMediatorLiveData<NotificationModel>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureResponseObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (notificationReadLiveData == null) {
            notificationReadLiveData = new RichMediatorLiveData<CommonResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureResponseObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
    }

    // Notification mark as read APi
    public void markNotificationRead(String notificationId) {
//        loading.onChanged(true);
        repo.markNotificationRead(notificationReadLiveData, notificationId, AppConstants.REQUEST_CODE.NOTIFICATION_LIST);
    }


    public RichMediatorLiveData<NotificationModel> getNotificationLiveModel() {
        return mNotificationLiveModel;
    }
    public RichMediatorLiveData<CommonResponse> getNotificationReadLiveData() {
        return notificationReadLiveData;
    }

    public void hitGetAllNotification(int limit, int page, boolean isRefresing) {
        if (!isRefresing)
            progressLoading.onChanged(true);
        repo.hitGetNotification(mNotificationLiveModel, limit, page, AppConstants.REQUEST_CODE.NOTIFICATION_LIST);
    }


}
