package com.taghawk.Repository;

import android.util.Log;

import com.google.gson.Gson;
import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.AddressUpdateResponse;
import com.taghawk.model.DeleteAddressRequest;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentStatusRequest;
import com.taghawk.model.ShippingAddressesResponse;
import com.taghawk.model.chat.DeleteTagRequest;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.strip.FedexRateResponse;

import java.util.HashMap;

/**
 * Created by Appinventiv on 24-01-2019.
 */

public class ShippingRepo {


    // This Service is use for caculate Fedex Shipping Charges
    public void getFedexRate(final RichMediatorLiveData<FedexRateResponse> shippingLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().getFexdexShippingRate(parms).enqueue(new NetworkCallback<FedexRateResponse>() {
            @Override
            public void onSuccess(FedexRateResponse categoryResponse) {
                shippingLiveData.setValue(categoryResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                shippingLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                shippingLiveData.setError(t);
            }
        });
    }

    // This Service is use for caculate Fedex Shipping Charges
    public void updateShippingAddress(final RichMediatorLiveData<AddressUpdateResponse> updateAddressLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().updateShippingAddress(parms).enqueue(new NetworkCallback<AddressUpdateResponse>() {
            @Override
            public void onSuccess(AddressUpdateResponse addressUpdateResponse) {
                updateAddressLiveData.setValue(addressUpdateResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                updateAddressLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                updateAddressLiveData.setError(t);
            }
        });
    }

    // This Service is use for calculate Fedex Shipping Charges
    public void getShippingAddresses(final RichMediatorLiveData<ShippingAddressesResponse> addressesLiveData, String userId) {
        DataManager.getInstance().getShippingAddressesResponse(userId).enqueue(new NetworkCallback<ShippingAddressesResponse>() {
            @Override
            public void onSuccess(ShippingAddressesResponse shippingAddressesResponse) {
                addressesLiveData.setValue(shippingAddressesResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                addressesLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                addressesLiveData.setError(t);
            }
        });
    }

    // This Service is use for calculate Fedex Shipping Charges
    public void getBillingAddress(final RichMediatorLiveData<ShippingAddressesResponse> addressesLiveData, String userId) {
        DataManager.getInstance().getBillingAddress(userId).enqueue(new NetworkCallback<ShippingAddressesResponse>() {
            @Override
            public void onSuccess(ShippingAddressesResponse shippingAddressesResponse) {
                addressesLiveData.setValue(shippingAddressesResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                addressesLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                addressesLiveData.setError(t);
            }
        });
    }

    // This Service is use for calculate Fedex Shipping Charges
    public void deleteShippingAddress(final RichMediatorLiveData<CommonResponse> deleteAddressLiveData, DeleteAddressRequest deleteAddressRequest) {
        DataManager.getInstance().deleteShippingAddress(deleteAddressRequest).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse commonResponse) {
                deleteAddressLiveData.setValue(commonResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                deleteAddressLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                deleteAddressLiveData.setError(t);
            }
        });
    }

    public void createLable(final RichMediatorLiveData<FedexRateResponse> shippingLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().createLable(parms).enqueue(new NetworkCallback<FedexRateResponse>() {
            @Override
            public void onSuccess(FedexRateResponse categoryResponse) {
                shippingLiveData.setValue(categoryResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                shippingLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                shippingLiveData.setError(t);
            }
        });
    }

    // This Service is use for payment for Fedex
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
}
