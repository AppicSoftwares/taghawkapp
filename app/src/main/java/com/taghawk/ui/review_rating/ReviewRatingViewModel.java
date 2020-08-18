package com.taghawk.ui.review_rating;



import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.Repository.ReviewRatingRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.review_rating.ReviewRatingModel;

import java.util.HashMap;

public class ReviewRatingViewModel extends ViewModel {

    private ReviewRatingRepo mReviewRatingRepo = new ReviewRatingRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> loading;
    private Observer<FailureResponse> mFailureObserver;
    private RichMediatorLiveData<ReviewRatingModel> mReviewLiveData;
    private RichMediatorLiveData<CommonResponse> mReviewReplyLiveData;

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    private void initLiveData() {
        if (mReviewLiveData == null) {
            mReviewLiveData = new RichMediatorLiveData<ReviewRatingModel>() {
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
        if (mReviewReplyLiveData == null) {
            mReviewReplyLiveData = new RichMediatorLiveData<CommonResponse>() {
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

    public void getReviewRatingList(String userId, int pageNo, int limit, boolean referesing) {

        if (!referesing)
            loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.SELLER_ID, userId);
        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageNo);
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        mReviewRatingRepo.getReviewRating(mReviewLiveData, parms, AppConstants.REQUEST_CODE.REVIEW_RATING_LIST);

    }

    public void replyAndEditReply(String ratingId, String replyComment, int action, int requestCode) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.RATING_ID, ratingId);
        parms.put(AppConstants.KEY_CONSTENT.REPLY_COMMENT, replyComment);
        parms.put(AppConstants.KEY_CONSTENT.ACTION, action);
        mReviewRatingRepo.replyEditComment(mReviewReplyLiveData, parms, requestCode);

    }

    public RichMediatorLiveData<ReviewRatingModel> getReviewLiveData() {
        return mReviewLiveData;
    }

    public RichMediatorLiveData<CommonResponse> getReplyLiveData() {
        return mReviewReplyLiveData;
    }

}
