package com.taghawk.ui.cart;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.Repository.CartRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentStatusRequest;
import com.taghawk.model.ShopperIdResponse;
import com.taghawk.model.cart.CartModel;
import com.taghawk.model.commonresponse.CommonResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CartViewModel extends ViewModel {

    private CartRepo mCartRepo = new CartRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> loading;
    private Observer<FailureResponse> mFailureObserver;
    private RichMediatorLiveData<CartModel> mCartViewModel;
    private RichMediatorLiveData<CommonResponse> mDeleteCart;
    private RichMediatorLiveData<CommonResponse> mSoldOutProduct;
    private RichMediatorLiveData<ShopperIdResponse> vaultedShopperIdLiveData;

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    /**
     * Initilization of live data
     */
    private void initLiveData() {
        if (mCartViewModel == null) {
            mCartViewModel = new RichMediatorLiveData<CartModel>() {
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
        if (mDeleteCart == null) {
            mDeleteCart = new RichMediatorLiveData<CommonResponse>() {
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
        if (mSoldOutProduct == null) {
            mSoldOutProduct = new RichMediatorLiveData<CommonResponse>() {
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
        if (vaultedShopperIdLiveData == null) {
            vaultedShopperIdLiveData = new RichMediatorLiveData<ShopperIdResponse>() {
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

    // THis Api is use to get the cart list items
    public void getCardList() {
        loading.onChanged(true);
        mCartRepo.getCartList(mCartViewModel);
    }

    // THis Api is use to get the cart list items
    public void getVaultedShopperId() {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.USER_ID, DataManager.getInstance().getUserDetails().getUserId());
        mCartRepo.getVaultedShopperId(vaultedShopperIdLiveData, parms);
    }

    //THis Api is use for add product in cart

    public void addCart(String productId, int action, int requestCode) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        if (productId != null && productId.length() > 0)
            parms.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
        parms.put(AppConstants.KEY_CONSTENT.ACTION, action);
        mCartRepo.addProductToCart(mDeleteCart, parms, requestCode);
    }

    //tHis Api is use for payment
    public void doPayment(PaymentStatusRequest transactionRequest) throws JSONException {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
//        parms.put(AppConstants.KEY_CONSTENT.SELLER_ID, sellerId);
//        parms.put(AppConstants.KEY_CONSTENT.PRODUCTS, productIsJson);
//        parms.put(AppConstants.KEY_CONSTENT.SOURCE, source);
//        parms.put(AppConstants.KEY_CONSTENT.CURRENCY, currency);
//        parms.put(AppConstants.KEY_CONSTENT.AMOUNT, totalAmount);
        mCartRepo.doPayment(mDeleteCart, transactionRequest, AppConstants.REQUEST_CODE.PAYMENT);

    }

    //This Api is use to check cart products are sold out or not just before payment
    public void checkSoldOutProduct(String productIsJson) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
//        parms.put(AppConstants.KEY_CONSTENT.SELLER_ID, sellerId);
        parms.put(AppConstants.KEY_CONSTENT.PRODUCTS, productIsJson);
//        parms.put(AppConstants.KEY_CONSTENT.SOURCE, source);
//        parms.put(AppConstants.KEY_CONSTENT.CURRENCY, currency);
//        parms.put(AppConstants.KEY_CONSTENT.AMOUNT, totalAmount);
        mCartRepo.checkSoldOutProduct(mSoldOutProduct, parms, AppConstants.REQUEST_CODE.PAYMENT);

    }

    //This is api is use to payment of 0$ product
    public void doZeroPayment(String productIsJson) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.PRODUCTS, productIsJson);
        mCartRepo.doZeroPayment(mDeleteCart, parms, AppConstants.REQUEST_CODE.ZERO_PAYMENT);

    }

    public RichMediatorLiveData<CartModel> getCartListViewModel() {
        return mCartViewModel;
    }


    public RichMediatorLiveData<ShopperIdResponse> getVaultedShopperIdViewModel() {
        return vaultedShopperIdLiveData;
    }

    public RichMediatorLiveData<CommonResponse> getDeleteCartItemViewModel() {
        return mDeleteCart;
    }

    public RichMediatorLiveData<CommonResponse> getSoldOutProductViewModel() {
        return mSoldOutProduct;
    }
}
