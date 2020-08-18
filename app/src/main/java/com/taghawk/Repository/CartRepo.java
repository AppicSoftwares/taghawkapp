package com.taghawk.Repository;

import android.util.Log;

import com.google.gson.Gson;
import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentStatusRequest;
import com.taghawk.model.ShopperIdResponse;
import com.taghawk.model.cart.CartModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.tag.TagDetailsModel;

import java.util.HashMap;

import retrofit2.http.QueryMap;

/**
 * Created by Appinventiv on 24-01-2019.
 */

public class CartRepo {

    // This Api is used for getting list of cart items
    public void getCartList(final RichMediatorLiveData<CartModel> categoryLiveData) {
        DataManager.getInstance().getCartList().enqueue(new NetworkCallback<CartModel>() {
            @Override
            public void onSuccess(CartModel categoryResponse) {
                categoryLiveData.setValue(categoryResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                categoryLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                categoryLiveData.setError(t);
            }
        });
    }

    // This Api is used for add product in Cart
    public void addProductToCart(final RichMediatorLiveData<CommonResponse> tagLiveData, HashMap<String, Object> parms, final int request) {
        DataManager.getInstance().addProductCArt(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(request);
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

    //This Api is use for payment
    public void doPayment(final RichMediatorLiveData<CommonResponse> tagLiveData, PaymentStatusRequest paymentStatusRequest, final int request) {
        Gson gson = new Gson();
        String json = gson.toJson(paymentStatusRequest);
        Log.e("REQUEST_JSON", "" + json);
        DataManager.getInstance().doPayment(paymentStatusRequest).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(request);
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

    //This Api is use to check cart products are sold out or not just before payment
    public void checkSoldOutProduct(final RichMediatorLiveData<CommonResponse> tagLiveData, HashMap<String, Object> parms, final int request) {
        DataManager.getInstance().checkSoldOutProduct(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(request);
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

    // This Api is used for 0$ product
    public void doZeroPayment(final RichMediatorLiveData<CommonResponse> tagLiveData, HashMap<String, Object> parms, final int request) {
        DataManager.getInstance().doZeroPayment(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(request);
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
    public void acceptRejectTagRequest(final RichMediatorLiveData<TagDetailsModel> mLiveData, HashMap<String, Object> parms, final int request) {
        DataManager.getInstance().acceptRejectTagRequestFromLink(parms).enqueue(new NetworkCallback<TagDetailsModel>() {
            @Override
            public void onSuccess(TagDetailsModel successResponse) {
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

    // This Api is used to get transaction shopper id if created and saved
    public void getVaultedShopperId(final RichMediatorLiveData<ShopperIdResponse> categoryLiveData, @QueryMap() HashMap<String, Object> parms) {
        DataManager.getInstance().getVaultedShopperId(parms).enqueue(new NetworkCallback<ShopperIdResponse>() {
            @Override
            public void onSuccess(ShopperIdResponse categoryResponse) {
                categoryLiveData.setValue(categoryResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                categoryLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                categoryLiveData.setError(t);
            }
        });
    }

}
