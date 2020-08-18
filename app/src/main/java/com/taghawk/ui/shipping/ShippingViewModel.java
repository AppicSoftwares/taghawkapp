package com.taghawk.ui.shipping;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.Repository.ShippingRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.AddressUpdateResponse;
import com.taghawk.model.DeleteAddressRequest;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentStatusRequest;
import com.taghawk.model.ShippingAddressesResponse;
import com.taghawk.model.cart.CartDataBean;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.strip.FedexRateResponse;
import com.taghawk.util.AppUtils;
import com.taghawk.util.ResourceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ShippingViewModel extends ViewModel {

    private Observer<Throwable> mErrorObserver;
    private Observer<FailureResponse> mFailureObserver;
    private Observer<Boolean> loading;
    private MutableLiveData<FailureResponse> mValidateLiveData;
    private RichMediatorLiveData<HashMap<String, Object>> mSendDataToNextStepLiveModel;
    private RichMediatorLiveData<FedexRateResponse> mShippingRateCalucalateLiveData;
    private RichMediatorLiveData<CommonResponse> mPaymentLiveData;
    private RichMediatorLiveData<ShippingAddressesResponse> mAddressesLiveData;
    private RichMediatorLiveData<CommonResponse> deleteAddressLiveData;
    private RichMediatorLiveData<AddressUpdateResponse> updateAddressLiveData;
    private ShippingRepo shippingRepo = new ShippingRepo();

    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    private void initLiveData() {
        if (mValidateLiveData == null) {
            mValidateLiveData = new MutableLiveData<>();
        }
        if (mSendDataToNextStepLiveModel == null) {
            mSendDataToNextStepLiveModel = new RichMediatorLiveData<HashMap<String, Object>>() {
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
        if (mShippingRateCalucalateLiveData == null) {
            mShippingRateCalucalateLiveData = new RichMediatorLiveData<FedexRateResponse>() {
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
        if (mPaymentLiveData == null) {
            mPaymentLiveData = new RichMediatorLiveData<CommonResponse>() {
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
        if (mAddressesLiveData == null) {
            mAddressesLiveData = new RichMediatorLiveData<ShippingAddressesResponse>() {
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
        if (deleteAddressLiveData == null) {
            deleteAddressLiveData = new RichMediatorLiveData<CommonResponse>() {
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
        if (updateAddressLiveData == null) {
            updateAddressLiveData = new RichMediatorLiveData<AddressUpdateResponse>() {
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

    public void getShippingAddresses(String userId) {
        loading.onChanged(true);
        shippingRepo.getShippingAddresses(mAddressesLiveData, userId);
    }

    public void deleteShippingAddress(DeleteAddressRequest deleteAddressRequest) {
        loading.onChanged(true);
        shippingRepo.deleteShippingAddress(deleteAddressLiveData, deleteAddressRequest);
    }

    public void calculateFedexRate(Context context, String productId, HashMap<String, Object> parms) {
        loading.onChanged(true);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
        int shipStatus = (int) parms.get(AppConstants.KEY_CONSTENT.SHIP_STATUS);
        hashMap.put(AppConstants.KEY_CONSTENT.SHIP_STATUS, shipStatus);
        parms.remove(AppConstants.KEY_CONSTENT.SHIP_STATUS);
        hashMap.put(AppConstants.KEY_CONSTENT.SHIP_TO, getJson(context, parms, true, ""));
        Log.e("SHIPPING_JSON", "" + getJson(context, parms, true, ""));
        shippingRepo.getFedexRate(mShippingRateCalucalateLiveData, hashMap);
    }

    public void updateAddressLiveData(Context context, String shipId, int selectedStatus, String fullName, String streetAddress, String apartment, String zipCode, String city, String state, String phoneNumber, String countryCode, String addressType) {
        loading.onChanged(true);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(AppConstants.KEY_CONSTENT.CONTACT_NAME, fullName);
        hashMap.put(AppConstants.KEY_CONSTENT.EMAIL, DataManager.getInstance().getUserDetails().getEmail());
        hashMap.put(AppConstants.KEY_CONSTENT.CITY, city);
        hashMap.put(AppConstants.KEY_CONSTENT.STATE, state);
        hashMap.put(AppConstants.KEY_CONSTENT.ZIP_CODE, zipCode);
        hashMap.put(AppConstants.KEY_CONSTENT.STEET1, streetAddress);
        if(!TextUtils.isEmpty(apartment))
            hashMap.put(AppConstants.KEY_CONSTENT.STEET2, apartment);
        hashMap.put(AppConstants.KEY_CONSTENT.PHONE, phoneNumber);
        if(addressType.equalsIgnoreCase("office"))
            hashMap.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "business");
        else
            hashMap.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "residential");
        String cCode = AppUtils.getCountryCode(context, zipCode);
        hashMap.put(AppConstants.KEY_CONSTENT.COUNTRY, cCode);
        hashMap.put(AppConstants.KEY_CONSTENT.SHIP_ID, shipId);
        hashMap.put(AppConstants.KEY_CONSTENT.SELECTED_STATUS, selectedStatus);
        shippingRepo.updateShippingAddress(updateAddressLiveData, hashMap);
    }

    public void createLable(CartDataBean cartBean, HashMap<String, Object> parms, PaymentStatusRequest paymentStatusRequest, double totalAmount, String stateSortName) {
        loading.onChanged(true);
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put(AppConstants.KEY_CONSTENT.PRODUCTS, getProductIdJson(productId));
//        hashMap.put(AppConstants.KEY_CONSTENT.SOURCE, source);
//        hashMap.put(AppConstants.KEY_CONSTENT.CURRENCY, "usd");
//        hashMap.put(AppConstants.KEY_CONSTENT.AMOUNT, totalAmount);
//        hashMap.put(AppConstants.KEY_CONSTENT.SHIP_TO, getJson(parms, false, stateSortName));

        ArrayList<PaymentStatusRequest.Products> products = new ArrayList<>();
        Gson gson = new Gson();
        PaymentStatusRequest.Products product = new PaymentStatusRequest.Products();
        product.setPrice(cartBean.getProductPrice());
        product.setProductId(cartBean.getProductId());
        product.setSellerId(cartBean.getSellerId());
        products.add(product);
        paymentStatusRequest.setProducts(products);

        PaymentStatusRequest.Ship_to ship_to = new PaymentStatusRequest.Ship_to();

        ship_to.setContact_name(parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString());
        if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getEmail() != null)
            ship_to.setEmail(DataManager.getInstance().getUserDetails().getEmail());
        ship_to.setCity(parms.get(AppConstants.KEY_CONSTENT.CITY).toString());
        ship_to.setState(stateSortName);
        ship_to.setPostal_code(parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
//            object.put(AppConstants.KEY_CONSTENT.STEET1, parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
        ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
        ship_to.setPhone(parms.get(AppConstants.KEY_CONSTENT.PHONE).toString());
//            object.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, parms.get(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE).toString());
        ship_to.setType("FEDEX");

        paymentStatusRequest.setShip_to(ship_to);
//        paymentStatusRequest.setShipStatus(saveAddressStatus);

        shippingRepo.doPayment(mPaymentLiveData, paymentStatusRequest, AppConstants.REQUEST_CODE.PAYMENT);
    }

    private String getJson(Context context, HashMap<String, Object> parms, boolean isCountry, String stateSortName) {
        JSONObject object = new JSONObject();
        try {
            object.put(AppConstants.KEY_CONSTENT.CONTACT_NAME, parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString());
            if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getEmail() != null)
                object.put(AppConstants.KEY_CONSTENT.EMAIL, DataManager.getInstance().getUserDetails().getEmail());
            object.put(AppConstants.KEY_CONSTENT.CITY, parms.get(AppConstants.KEY_CONSTENT.CITY).toString());
            if (isCountry)
                object.put(AppConstants.KEY_CONSTENT.STATE, parms.get(AppConstants.KEY_CONSTENT.STATE).toString());
            else
                object.put(AppConstants.KEY_CONSTENT.STATE, stateSortName);
            object.put(AppConstants.KEY_CONSTENT.ZIP_CODE, parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
            object.put(AppConstants.KEY_CONSTENT.STEET1, parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
            if(!TextUtils.isEmpty(parms.get(AppConstants.KEY_CONSTENT.STEET2).toString()))
                object.put(AppConstants.KEY_CONSTENT.STEET2, parms.get(AppConstants.KEY_CONSTENT.STEET2).toString());
            object.put(AppConstants.KEY_CONSTENT.PHONE,  parms.get(AppConstants.KEY_CONSTENT.PHONE).toString());
//            object.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, parms.get(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE).toString());
            if (isCountry) {
                if(parms.get(AppConstants.KEY_CONSTENT.TYPE).toString().equalsIgnoreCase("office")) {
                    object.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "business");
                } else {
                    object.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "residential");
                }
            } else {
                object.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "FEDEX");
            }
            String cCode = AppUtils.getCountryCode(context, parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
            object.put(AppConstants.KEY_CONSTENT.COUNTRY, parms.get(AppConstants.KEY_CONSTENT.COUNTRY));
            object.put(AppConstants.KEY_CONSTENT.SELECTED_STATUS, parms.get(AppConstants.KEY_CONSTENT.SELECTED_STATUS));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public void moveToStepTwo(Context context, String fullName, String streetAdrress, String apartment, String zipCode, String city, String state, String phoneNumber, String countryCode, String addressType, int shipStatus, int selectedStatus, String country) {
        if (validate(fullName, streetAdrress, zipCode, city, state, phoneNumber)) {
            HashMap<String, Object> parms = new HashMap<>();
            parms.put(AppConstants.KEY_CONSTENT.CONTACT_NAME, fullName);
            parms.put(AppConstants.KEY_CONSTENT.STEET1, streetAdrress);
            if(apartment != null && !TextUtils.isEmpty(apartment))
                parms.put(AppConstants.KEY_CONSTENT.STEET2, apartment);
            else
                parms.put(AppConstants.KEY_CONSTENT.STEET2, "");
            parms.put(AppConstants.KEY_CONSTENT.ZIP_CODE, zipCode);
            parms.put(AppConstants.KEY_CONSTENT.CITY, city);
            parms.put(AppConstants.KEY_CONSTENT.STATE, state);
            parms.put(AppConstants.KEY_CONSTENT.PHONE, countryCode + "" + phoneNumber);
            String cCode = AppUtils.getCountryCode(context, zipCode);
            if(!TextUtils.isEmpty(country))
                parms.put(AppConstants.KEY_CONSTENT.COUNTRY, country);
            else if(!TextUtils.isEmpty(cCode))
                parms.put(AppConstants.KEY_CONSTENT.COUNTRY, cCode);
            else
                parms.put(AppConstants.KEY_CONSTENT.COUNTRY, "US");
            parms.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, addressType);
            parms.put(AppConstants.KEY_CONSTENT.SHIP_STATUS, shipStatus);
            parms.put(AppConstants.KEY_CONSTENT.SELECTED_STATUS, selectedStatus);
            Log.e("STEP_TWO", "" + parms.toString());
            mSendDataToNextStepLiveModel.setValue(parms);
        }
    }

    private boolean validate(String fullName, String streetAdrress, String zipCode, String city, String state, String phoneNumber) {
        if (fullName == null || fullName.length() == 0) {
            mValidateLiveData.setValue(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.please_Enter_full_name)));
            return false;
        } else if (streetAdrress == null || streetAdrress.length() == 0) {
            mValidateLiveData.setValue(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.please_Enter_street_address)));
            return false;
        } /*else if (streetAdrress.length() < 6) {
            mValidateLiveData.setValue(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.street_address_must_be_six_character)));
            return false;
        }*/ else if (zipCode == null || zipCode.length() == 0) {
            mValidateLiveData.setValue(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.please_Enter_zip_code)));
            return false;
        } else if (zipCode.length() < 5) {
            mValidateLiveData.setValue(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.zip_code_must_be_5_digit)));
            return false;
        } else if (city == null || city.length() == 0) {
            mValidateLiveData.setValue(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.city_can_not_be_empty)));
            return false;
        } else if (state == null || state.length() == 0) {
            mValidateLiveData.setValue(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.state_can_not_be_empty)));

            return false;
        } else if (phoneNumber == null || phoneNumber.length() == 0) {
            mValidateLiveData.setValue(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.please_enter_phone_number)));
            return false;
        } else if (phoneNumber.length() < 7) {
            mValidateLiveData.setValue(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.enter_valid_phone)));
            return false;
        }

        return true;
    }


    public MutableLiveData<FailureResponse> getValidateLiveData() {
        return mValidateLiveData;
    }

    public RichMediatorLiveData<HashMap<String, Object>> getmSendDataToNextStepLiveModel() {
        return mSendDataToNextStepLiveModel;
    }

    public RichMediatorLiveData<FedexRateResponse> getmShippingRateCalucalateLiveData() {
        return mShippingRateCalucalateLiveData;
    }

    public RichMediatorLiveData<CommonResponse> getmPaymentLiveData() {
        return mPaymentLiveData;
    }

    public RichMediatorLiveData<ShippingAddressesResponse> getAddressesLiveData() {
        return mAddressesLiveData;
    }

    public RichMediatorLiveData<CommonResponse> getDeleteAddressLiveData() {
        return deleteAddressLiveData;
    }

    public RichMediatorLiveData<AddressUpdateResponse> getUpdateAddressLiveData() {
        return updateAddressLiveData;
    }

    private String getProductIdJson(CartDataBean mCartData) {
        JSONArray productArray = new JSONArray();
        JSONObject productObject = new JSONObject();
        try {
            productObject.put(AppConstants.KEY_CONSTENT.PRICE, mCartData.getProductPrice());
            productObject.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, mCartData.getProductId());
            productObject.put(AppConstants.KEY_CONSTENT.SELLER_ID, mCartData.getSellerId());
            productArray.put(productObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return productArray.toString();
    }
}
