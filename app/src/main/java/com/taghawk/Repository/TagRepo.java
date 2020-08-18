package com.taghawk.Repository;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.tag.MyTagResponse;
import com.taghawk.model.tag.TagDetailsModel;
import com.taghawk.model.tagaddresponse.AddTagResponse;

import java.util.HashMap;

public class TagRepo {

    // This Service is use to get Tag Details
    public void getTagDetails(final RichMediatorLiveData<TagDetailsModel> tagLiveData, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().getTagDetails(parms).enqueue(new NetworkCallback<TagDetailsModel>() {
            @Override
            public void onSuccess(TagDetailsModel successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    tagLiveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                tagLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                tagLiveData.setError(t);
            }
        });
    }

    // This Service is use to get Tag Details
    public void getTagVisit(final RichMediatorLiveData<TagDetailsModel> tagLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().getTagVisited(parms).enqueue(new NetworkCallback<TagDetailsModel>() {
            @Override
            public void onSuccess(TagDetailsModel successResponse) {
                if (successResponse != null) {
//                    successResponse.setRequestCode(requestCode);
                    tagLiveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                tagLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                tagLiveData.setError(t);
            }
        });
    }


    // This Api is use for create TAG

    public void addTag(final RichMediatorLiveData<AddTagResponse> tagLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().addTag(parms).enqueue(new NetworkCallback<AddTagResponse>() {
            @Override
            public void onSuccess(AddTagResponse successResponse) {
                if (successResponse != null) {
                    tagLiveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                tagLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                tagLiveData.setError(t);
            }
        });
    }


    public void editTag(final RichMediatorLiveData<AddTagResponse> tagLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().editTag(parms).enqueue(new NetworkCallback<AddTagResponse>() {
            @Override
            public void onSuccess(AddTagResponse successResponse) {
                if (successResponse != null) {
                    tagLiveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                tagLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                tagLiveData.setError(t);
            }
        });
    }

    // This Service is use for create TAG join request
    public void joinTag(final RichMediatorLiveData<CommonResponse> tagLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().joinTag(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    tagLiveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                tagLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                tagLiveData.setError(t);
            }
        });

    }


    //This service is use for my Tags
    public void myTags(final RichMediatorLiveData<MyTagResponse> tagLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().getMyTags(parms).enqueue(new NetworkCallback<MyTagResponse>() {
            @Override
            public void onSuccess(MyTagResponse successResponse) {
                if (successResponse != null) {
                    tagLiveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                tagLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                tagLiveData.setError(t);
            }
        });

    }

    // This Api is used for accept and Reject Tag Join Request
    public void acceptRejectTagRequest(final RichMediatorLiveData<CommonResponse> mLiveData, HashMap<String, Object> parms, final int request) {
        DataManager.getInstance().acceptRejectTagRequest(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
//                    successResponse.setRequestCode(request);
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
