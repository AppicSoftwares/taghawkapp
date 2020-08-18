package com.taghawk.Repository;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.review_rating.ReviewRatingModel;

import java.util.HashMap;

/**
 * Created by Appinventiv on 24-01-2019.
 */

public class ReviewRatingRepo {


    // This Api is use for get review Rating List
    public void getReviewRating(final RichMediatorLiveData<ReviewRatingModel> reviewLivemodel, HashMap<String, Object> parms, final int request) {
        DataManager.getInstance().getReviewRating(parms).enqueue(new NetworkCallback<ReviewRatingModel>() {
            @Override
            public void onSuccess(ReviewRatingModel successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(request);
                    reviewLivemodel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                reviewLivemodel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                reviewLivemodel.setError(t);
            }
        });
    }

    // This APi is use for reply on review
    public void replyEditComment(final RichMediatorLiveData<CommonResponse> reviewLivemodel, HashMap<String, Object> parms, final int request) {
        DataManager.getInstance().replyEditComment(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(request);
                    reviewLivemodel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                reviewLivemodel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {

                reviewLivemodel.setError(t);
            }
        });
    }

}
