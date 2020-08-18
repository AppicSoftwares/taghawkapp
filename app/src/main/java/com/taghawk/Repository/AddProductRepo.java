package com.taghawk.Repository;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.AddProduct.AddProductModel;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.tag.UserSpecificTagsModel;

import java.util.HashMap;

public class AddProductRepo {


    // Function is use for get Specific Tag of User
    public void getUserSpecificTags(final RichMediatorLiveData<UserSpecificTagsModel> tagLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().getUserSpecificTags(parms).enqueue(new NetworkCallback<UserSpecificTagsModel>() {
            @Override
            public void onSuccess(UserSpecificTagsModel successResponse) {
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

    // Api is use for create product
    public void addProduct(final RichMediatorLiveData<AddProductModel> tagLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().addProduct(parms).enqueue(new NetworkCallback<AddProductModel>() {
            @Override
            public void onSuccess(AddProductModel successResponse) {
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

    // Api is use for create Product/Add Product
    public void markProductFeatured(final RichMediatorLiveData<CommonResponse> tagLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().markProductFeatured(parms).enqueue(new NetworkCallback<CommonResponse>() {
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

    // This Api is use for edit product
    public void editProduct(final RichMediatorLiveData<AddProductModel> editLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().editProduct(parms).enqueue(new NetworkCallback<AddProductModel>() {
            @Override
            public void onSuccess(AddProductModel successResponse) {
                if (successResponse != null) {
                    editLiveData.setValue(successResponse);
                }
            }
            @Override
            public void onFailure(FailureResponse failureResponse) {
                editLiveData.setFailure(failureResponse);
            }
            @Override
            public void onError(Throwable t) {
                editLiveData.setError(t);
            }
        });
    }
}
