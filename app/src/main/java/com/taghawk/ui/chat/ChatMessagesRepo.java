package com.taghawk.ui.chat;

import android.util.Log;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentRefundModel;
import com.taghawk.model.chat.ChatPushModel;
import com.taghawk.model.chat.DeleteTagRequest;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.model.pendingRequests.PendingRequestResponse;
import com.taghawk.model.profileresponse.ProfileProductsResponse;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.model.tagaddresponse.AddTagResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatMessagesRepo {

    void hitGetProductsAPI(final RichMediatorLiveData<ProfileProductsResponse> mArticleListLiveData, String otherUserId) {
        DataManager.getInstance().getProfileProducts(generateSingleChatProductsParams(otherUserId)).enqueue(new NetworkCallback<ProfileProductsResponse>() {
            @Override
            public void onSuccess(ProfileProductsResponse response) {
                if (response.getStatusCode() == 200) {
                    mArticleListLiveData.setValue(response);
                } else
                    mArticleListLiveData.setFailure(getFailureData(response.getStatusCode(), response.getMessage()));
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                mArticleListLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                mArticleListLiveData.setError(t);
            }
        });
    }

    public void hitMyProductsAPI(final RichMediatorLiveData<ProfileProductsResponse> mArticleListLiveData, HashMap<String, Object> hashMap) {
        DataManager.getInstance().getProfileProducts(hashMap).enqueue(new NetworkCallback<ProfileProductsResponse>() {
            @Override
            public void onSuccess(ProfileProductsResponse response) {
                if (response.getStatusCode() == 200) {
                    mArticleListLiveData.setValue(response);
                } else
                    mArticleListLiveData.setFailure(getFailureData(response.getStatusCode(), response.getMessage()));
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                mArticleListLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                mArticleListLiveData.setError(t);
            }
        });
    }

    void getTagProducts(final RichMediatorLiveData<ProductListingModel> productLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().getTagProduct(parms).enqueue(new NetworkCallback<ProductListingModel>() {
            @Override
            public void onSuccess(ProductListingModel successResponse) {
                if (successResponse != null) {
                    productLiveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                productLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                productLiveData.setError(t);
            }
        });
    }

    void editTag(final RichMediatorLiveData<AddTagResponse> tagLiveData, HashMap<String, Object> parms, final String type) {
        DataManager.getInstance().editTag(parms).enqueue(new NetworkCallback<AddTagResponse>() {
            @Override
            public void onSuccess(AddTagResponse successResponse) {
                if (successResponse != null) {
                    successResponse.setEditType(type);
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

    void reportTag(final RichMediatorLiveData<CommonResponse> tagLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().reportTag(parms).enqueue(new NetworkCallback<CommonResponse>() {
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

    void removeMember(final RichMediatorLiveData<CommonResponse> tagLiveData, final HashMap<String, Object> parms, final String memberName, final int type) {
        DataManager.getInstance().removeMember(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(AppConstants.KEY_CONSTENT.USER_ID, parms.get(AppConstants.KEY_CONSTENT.USER_ID));
                    hashMap.put(AppConstants.KEY_CONSTENT.MEMBER_NAME, memberName);
                    hashMap.put(AppConstants.KEY_CONSTENT.ACTION_TYPE, type);
                    successResponse.setExtraLocalData(hashMap);
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

    void transferOwnership(final RichMediatorLiveData<CommonResponse> tagLiveData, final HashMap<String, Object> parms, final String memberName, final int type) {
        DataManager.getInstance().transferOwnership(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(AppConstants.KEY_CONSTENT.USER_ID, parms.get(AppConstants.KEY_CONSTENT.USER_ID));
                    hashMap.put(AppConstants.KEY_CONSTENT.MEMBER_NAME, memberName);
                    hashMap.put(AppConstants.KEY_CONSTENT.ACTION_TYPE, type);
                    successResponse.setExtraLocalData(hashMap);
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

    void blockUser(final RichMediatorLiveData<ProfileResponse> tagLiveData, final HashMap<String, Object> parms, final String memberName, final int type) {
        DataManager.getInstance().removeUnfriend(parms).enqueue(new NetworkCallback<ProfileResponse>() {
            @Override
            public void onSuccess(ProfileResponse successResponse) {
                if (successResponse != null) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(AppConstants.KEY_CONSTENT.USER_ID, parms.get(AppConstants.KEY_CONSTENT.USER_ID));
                    hashMap.put(AppConstants.KEY_CONSTENT.MEMBER_NAME, memberName);
                    hashMap.put(AppConstants.KEY_CONSTENT.ACTION_TYPE, type);
                    successResponse.setExtraLocalData(hashMap);
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

    void getPendingRequests(final RichMediatorLiveData<PendingRequestResponse> tagLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().getPendingRequests(parms).enqueue(new NetworkCallback<PendingRequestResponse>() {
            @Override
            public void onSuccess(PendingRequestResponse successResponse) {
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

    void acceptRejectTagRequest(final RichMediatorLiveData<CommonResponse> mLiveData, final HashMap<String, Object> parms) {
        DataManager.getInstance().acceptRejectTagRequest(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    successResponse.setExtraLocalData(parms);
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

    public void deleteTag(final RichMediatorLiveData<CommonResponse> tagLiveData, final HashMap<String, Object> parms, final int type) {
        DeleteTagRequest deleteTagRequest = new DeleteTagRequest();
        deleteTagRequest.setCommunityId((String) parms.get(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID));
        DataManager.getInstance().deleteTagApi(deleteTagRequest).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(AppConstants.KEY_CONSTENT.ACTION_TYPE, type);
                    hashMap.putAll(parms);
                    successResponse.setExtraLocalData(hashMap);
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

    public void exitTag(final RichMediatorLiveData<CommonResponse> tagLiveData, final HashMap<String, Object> parms, final int type) {
        DataManager.getInstance().exitTagApi(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(AppConstants.KEY_CONSTENT.ACTION_TYPE, type);
                    hashMap.putAll(parms);
                    successResponse.setExtraLocalData(hashMap);
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

    public void getProductStatusApi(final RichMediatorLiveData<PaymentRefundModel> tagLiveData, final String productId) {
        DataManager.getInstance().productStatusApi(productId).enqueue(new NetworkCallback<PaymentRefundModel>() {
            @Override
            public void onSuccess(PaymentRefundModel paymentRefundModel) {
                if (paymentRefundModel != null) {
                    tagLiveData.setValue(paymentRefundModel);
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

    private FailureResponse getFailureData(Integer statusCode, String message) {
        FailureResponse failureResponse = new FailureResponse();
        failureResponse.setErrorCode(statusCode);
        failureResponse.setErrorMessage(message);
        return failureResponse;
    }

    private HashMap<String, Object> generateSingleChatProductsParams(String otherUserId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(AppConstants.KEY_CONSTENT.PRODUCT_STATUS, 4);
        if (!otherUserId.equalsIgnoreCase(""))
            params.put(AppConstants.KEY_CONSTENT.OTHER_USER_ID, otherUserId);
        params.put(AppConstants.KEY_CONSTENT.PAGE_NO, String.valueOf(1));
        params.put(AppConstants.KEY_CONSTENT.LIMIT, String.valueOf(100));
        return params;
    }

    private HashMap<String, Object> generateGroupChatProductsParams(String communityId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(AppConstants.KEY_CONSTENT.COMMUNITY_ID, communityId);
        params.put(AppConstants.KEY_CONSTENT.PAGE_NO, String.valueOf(1));
        params.put(AppConstants.KEY_CONSTENT.LIMIT, String.valueOf(100));
        return params;
    }

}
