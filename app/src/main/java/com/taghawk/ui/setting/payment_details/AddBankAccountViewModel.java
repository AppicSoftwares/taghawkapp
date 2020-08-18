package com.taghawk.ui.setting.payment_details;



import android.text.TextUtils;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.R;
import com.taghawk.Repository.HomeRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentHistoryModel;
import com.taghawk.model.PaymentRefundModel;
import com.taghawk.model.cart.CartModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.profileresponse.BalanceResponse;
import com.taghawk.model.strip.CreateMercentResponse;
import com.taghawk.util.ResourceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddBankAccountViewModel extends ViewModel {

    HomeRepo homeRepo = new HomeRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> loading;
    private Observer<FailureResponse> mFailureObserver;
    private RichMediatorLiveData<CartModel> mCartViewModel;
    private RichMediatorLiveData<CreateMercentResponse> mMerchentLiveData;
    private RichMediatorLiveData<CommonResponse> mSaveBankLiveData;
    private RichMediatorLiveData<BalanceResponse> mCashOutLiveData;
    private RichMediatorLiveData<PaymentHistoryModel> paymentHistroyLiveData;
    private RichMediatorLiveData<PaymentRefundModel> paymentRefundLiveData;
    private RichMediatorLiveData<PaymentRefundModel> cancelDisputeLiveData;
    private RichMediatorLiveData<CommonResponse> mAddDebitCard;


    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    private void initLiveData() {
        if (mMerchentLiveData == null) {
            mMerchentLiveData = new RichMediatorLiveData<CreateMercentResponse>() {
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
        if (mSaveBankLiveData == null) {
            mSaveBankLiveData = new RichMediatorLiveData<CommonResponse>() {
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
        if (mCashOutLiveData == null) {
            mCashOutLiveData = new RichMediatorLiveData<BalanceResponse>() {
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
        if (paymentHistroyLiveData == null) {
            paymentHistroyLiveData = new RichMediatorLiveData<PaymentHistoryModel>() {
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
        if (paymentRefundLiveData == null) {
            paymentRefundLiveData = new RichMediatorLiveData<PaymentRefundModel>() {
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
        if (cancelDisputeLiveData == null) {
            cancelDisputeLiveData = new RichMediatorLiveData<PaymentRefundModel>() {
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
        if (mAddDebitCard == null) {
            mAddDebitCard = new RichMediatorLiveData<CommonResponse>() {
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

    public void saveBankDetails(String accountNumber, String verifyAccountNumber, String routingNumber, String verifyRoutingNumber, String firstName, String lastName) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        if (validate(accountNumber, verifyAccountNumber, routingNumber, verifyRoutingNumber, firstName, lastName, "")) {
            parms.put(AppConstants.KEY_CONSTENT.ACCOUNT_NUMBER, accountNumber);
            parms.put(AppConstants.KEY_CONSTENT.ROUTING_NUMBER, routingNumber);
            parms.put(AppConstants.KEY_CONSTENT.ACCOUNT_HOLDER, firstName + " " + lastName);
            homeRepo.saveBankDetails(mSaveBankLiveData, parms);
        }
    }

    public boolean validateBankDetails(String accountNumber, String verifyAccountNumber, String routingNumber, String verifyRoutingNumber, String firstName, String lastName, String bankName) {
        if (validate(accountNumber, verifyAccountNumber, routingNumber, verifyRoutingNumber, firstName, lastName, bankName)) {
            return true;
        } else {
            return false;
        }
    }

    public void cashOutBalance(double amount) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.AMOUNT, amount);
        parms.put(AppConstants.KEY_CONSTENT.CURRENCY, AppConstants.CURRENCY_USD);
        homeRepo.cashOutBalance(mCashOutLiveData, parms);
    }

    public void addDebitCard(String token,String name) {

        homeRepo.addDebitCard(mAddDebitCard, token,name, 109);
    }

    //getPayement History of buyer
    public void payementHistory() {
        homeRepo.getPaymentHistory(paymentHistroyLiveData);
    }

    //initiate Refund Request
    public void initiateRefundRequest(String orderId) {
        loading.onChanged(true);
        homeRepo.initiateRefund(paymentRefundLiveData, orderId, AppConstants.REQUEST_CODE.REFUND_REQUEST);
    }

    //confirm product received by buyer and release payment
    public void confirmItemReceived(String orderId) {
        loading.onChanged(true);
        homeRepo.confirmItemReceived(paymentRefundLiveData, orderId, AppConstants.REQUEST_CODE.CONFIRM_ITEM_RECEIVED);
    }

    //confirm product received by seller and release payment
    public void confirmReturnItemReceivedSeller(String orderId) {
        loading.onChanged(true);
        homeRepo.confirmReturnItemReceivedSeller(paymentRefundLiveData, orderId, AppConstants.REQUEST_CODE.CONFIRM_ITEM_RECEIVED_SELLER);
    }

    //Seller accept request of seller
    public void returnRequestAccept(String orderId) {
        loading.onChanged(true);
        homeRepo.returnRequestAccept(paymentRefundLiveData, orderId, AppConstants.REQUEST_CODE.REFUND_REQUEST_ACCEPT);
    }

    //Seller accept request of seller
    public void cancelDisputeApi(String orderId, String action, int requestCode) {
        loading.onChanged(true);
        homeRepo.cancelDispute(cancelDisputeLiveData, orderId, action, requestCode);
    }

    //Seller decline retrun request
    public void declineRetrunRequest(String orderId, String reason) {

        loading.onChanged(true);
        HashMap parms = new HashMap();
        parms.put(AppConstants.KEY_CONSTENT.ORDER_ID, orderId);
        parms.put(AppConstants.KEY_CONSTENT.DECLINE_MESSAGE, reason);
        homeRepo.declineRetrunRequest(paymentRefundLiveData, parms, AppConstants.REQUEST_CODE.REFUND_REQUEST_DECLINE);
    }

    //Initiate Dispute
    public void initiatDispute(String orderId, String reason, ArrayList<ImageList> mImageList, String submitResponse, int requestCode) {

        loading.onChanged(true);
        HashMap parms = new HashMap();
        parms.put(AppConstants.KEY_CONSTENT.ORDER_ID, orderId);
        parms.put(AppConstants.KEY_CONSTENT.STATEMENT, reason);
        if (mImageList != null && mImageList.size() > 0)
            parms.put(AppConstants.KEY_CONSTENT.PROOF, getJson(mImageList));
        parms.put(AppConstants.KEY_CONSTENT.SUBMIT_RESPONSE, submitResponse);
        homeRepo.initiateDispute(paymentRefundLiveData, parms, requestCode);
    }

    //This Function is use for get Product Status
    public void checkProductStatus(String productId) {
        homeRepo.getProductStatusApi(paymentRefundLiveData, productId);
    }

    public JSONArray getJson(ArrayList<ImageList> mImageList) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < mImageList.size(); i++) {
            JSONObject object = new JSONObject();
            try {
                object.put("url", mImageList.get(i).getUrl());
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    private boolean validate(String accountNumber, String verifyAccountNumber, String routingNumber, String verifyRoutingNumber, String firstName, String lastName, String bankName) {

        if (accountNumber == null || accountNumber.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.Please_Enter_Account_Number)));
            return false;
        } else if (verifyAccountNumber == null || verifyAccountNumber.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.Please_Verify_Your_Account_Number)));

            return false;
        } else if (verifyAccountNumber.length() < 12 || verifyAccountNumber.length() > 20) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.Please_Enter_Valid_Account_Number)));

            return false;
        } else if (routingNumber == null || routingNumber.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.Please_Enter_Routing_Number)));

            return false;
        } else if (verifyRoutingNumber == null || verifyRoutingNumber.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.Please_Verify_Your_Routing_Number)));

            return false;
        } else if (verifyRoutingNumber.length() < 9) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.Please_Enter_Valid_Routing_Number)));

            return false;
        } else if (!accountNumber.equalsIgnoreCase(verifyAccountNumber)) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.Verify_Account_Same_As_Account)));

            return false;
        } else if (!routingNumber.equalsIgnoreCase(verifyRoutingNumber)) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.Verify_Routing_Same_As_Routing)));
            return false;
        } else if (TextUtils.isEmpty(firstName)) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_first_name)));
            return false;
        } else if (TextUtils.isEmpty(lastName)) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_last_name)));
            return false;
        } else if (TextUtils.isEmpty(bankName)) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_bank_name)));
            return false;
        }

        return true;
    }

    public RichMediatorLiveData<CreateMercentResponse> getmMerchentLiveData() {
        return mMerchentLiveData;
    }

    public RichMediatorLiveData<CommonResponse> getmSaveBankLiveData() {
        return mSaveBankLiveData;
    }

    public RichMediatorLiveData<CommonResponse> getAddDebitCardLiveData() {
        return mAddDebitCard;
    }

    public RichMediatorLiveData<PaymentHistoryModel> paymentHistoryLiveData() {
        return paymentHistroyLiveData;
    }

    public RichMediatorLiveData<PaymentRefundModel> paymentRefundLiveData() {
        return paymentRefundLiveData;
    }

    public RichMediatorLiveData<PaymentRefundModel> getCancelDisputeLiveData() {
        return cancelDisputeLiveData;
    }
}
