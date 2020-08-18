package com.taghawk.Repository;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.CommonDataModel;
import com.taghawk.model.ContentViewModel;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentHistoryModel;
import com.taghawk.model.PaymentRefundModel;
import com.taghawk.model.SearchModel;
import com.taghawk.model.VendorIdResponse;
import com.taghawk.model.cashout.MerchantDetailBeans;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.DeleteProductRequest;
import com.taghawk.model.home.LikeUnLike;
import com.taghawk.model.home.ProductDetailsModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.model.profileresponse.BalanceResponse;
import com.taghawk.model.strip.CreateMercentResponse;
import com.taghawk.model.strip.GetBankDetail;
import com.taghawk.model.tag.TagModel;
import com.taghawk.model.tag.TagSearchBean;
import com.taghawk.model.update_rating_notification.UpdateRatingNotificationBean;
import com.taghawk.ui.home.HomeFragment;

import java.util.HashMap;

public class HomeRepo {

    /**
     * This method is used to hit log out api
     *
     * @param logOutLiveData live data object
     */
    public void userLogOut(final RichMediatorLiveData<CommonResponse> logOutLiveData) {
        DataManager.getInstance().hitLogOutApi().enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    logOutLiveData.setValue(successResponse);
                    DataManager.getInstance().clearPreferences();
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                logOutLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                logOutLiveData.setError(t);
            }
        });
    }

    // Api is used for get product list
    public void getProductList(final RichMediatorLiveData<ProductListingModel> productLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().getProductListing(parms).enqueue(new NetworkCallback<ProductListingModel>() {
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

    //Api is used for get Tag Products
    public void getTagProducts(final RichMediatorLiveData<ProductListingModel> productLiveData, HashMap<String, Object> parms) {
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

    public void getSearchSuggestion(final RichMediatorLiveData<SearchModel> searcViewModel, HashMap<String, Object> parms) {
        DataManager.getInstance().getSearchSuggestion(parms).enqueue(new NetworkCallback<SearchModel>() {
            @Override
            public void onSuccess(SearchModel successResponse) {
                if (successResponse != null) {
                    searcViewModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                searcViewModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                searcViewModel.setError(t);
            }
        });
    }

    // Api is used for get details of product
    public void getProductDetails(final RichMediatorLiveData<ProductDetailsModel> productDetailsModel, String id) {
        DataManager.getInstance().getProductDetails(id).enqueue(new NetworkCallback<ProductDetailsModel>() {
            @Override
            public void onSuccess(ProductDetailsModel successResponse) {
                if (successResponse != null) {
                    productDetailsModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                productDetailsModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                productDetailsModel.setError(t);
            }
        });
    }

    // Api is used for like/unlike product
    public void getLikeUnLike(final RichMediatorLiveData<LikeUnLike> productDetailsModel, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().getHitLikeUnLike(parms).enqueue(new NetworkCallback<LikeUnLike>() {
            @Override
            public void onSuccess(LikeUnLike successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    HomeFragment homeFragment = new HomeFragment();
                    homeFragment.onRefresh();
                    productDetailsModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                productDetailsModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                productDetailsModel.setError(t);
            }
        });
    }

    // Api is used for Report product
    public void reportProduct(final RichMediatorLiveData<LikeUnLike> productDetailsModel, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().reportProduct(parms).enqueue(new NetworkCallback<LikeUnLike>() {
            @Override
            public void onSuccess(LikeUnLike successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    productDetailsModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                productDetailsModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                productDetailsModel.setError(t);
            }
        });
    }

    // APi is used for share product
    public void shareProduct(final RichMediatorLiveData<LikeUnLike> productDetailsModel, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().shareProduct(parms).enqueue(new NetworkCallback<LikeUnLike>() {
            @Override
            public void onSuccess(LikeUnLike successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    productDetailsModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                productDetailsModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                productDetailsModel.setError(t);
            }
        });
    }


    // Api is used for User email verification
    public void verifyEmail(final RichMediatorLiveData<CommonResponse> productDetailsModel, String token, final int requestCode) {
        DataManager.getInstance().verifyEmail(token).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    productDetailsModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                productDetailsModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                productDetailsModel.setError(t);
            }
        });
    }

    // Api is use for Tag Listing
    public void getTagListing(final RichMediatorLiveData<TagModel> tagLiveData, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().getTagList(parms).enqueue(new NetworkCallback<TagModel>() {
            @Override
            public void onSuccess(TagModel successResponse) {
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


    // Api is use for search TAG
    public void getTagSearch(final RichMediatorLiveData<TagSearchBean> searcViewModel, HashMap<String, Object> parms) {
        DataManager.getInstance().getTagSearchData(parms).enqueue(new NetworkCallback<TagSearchBean>() {
            @Override
            public void onSuccess(TagSearchBean successResponse) {
                if (successResponse != null) {
                    searcViewModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                searcViewModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                searcViewModel.setError(t);
            }
        });
    }


    // This Api is use for logout
    public void logout(final RichMediatorLiveData<CommonResponse> tagLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().hitLogOut(parms).enqueue(new NetworkCallback<CommonResponse>() {
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

    // Api is used for delete Product
    public void deleteProduct(final RichMediatorLiveData<CommonResponse> tagLiveData, DeleteProductRequest deleteProductRequest, final int request) {
        DataManager.getInstance().deleteProduct(deleteProductRequest).enqueue(new NetworkCallback<CommonResponse>() {
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

    // Api is used for give feedback after purchasing product
    public void giveFeedback(final RichMediatorLiveData<CommonResponse> productDetailsModel, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().givenFeedback(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    productDetailsModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                productDetailsModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                productDetailsModel.setError(t);
            }
        });
    }

    // This APi is use to deny feedback for purchased product
    public void denyFeedback(final RichMediatorLiveData<CommonResponse> productDetailsModel, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().denyFeedback(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    productDetailsModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                productDetailsModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                productDetailsModel.setError(t);
            }
        });
    }

    // This Api is used for Update Token
    public void updateDeviceToken(final RichMediatorLiveData<UpdateRatingNotificationBean> productDetailsModel, String parms, final int requestCode) {
        DataManager.getInstance().updateDeviceToken(parms).enqueue(new NetworkCallback<UpdateRatingNotificationBean>() {
            @Override
            public void onSuccess(UpdateRatingNotificationBean successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    productDetailsModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                productDetailsModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                productDetailsModel.setError(t);
            }
        });
    }

    // This api is used for create merchant for cashout
    public void createMerchent(final RichMediatorLiveData<CreateMercentResponse> productDetailsModel, HashMap<String, Object> parms) {
        DataManager.getInstance().createMerchent(parms).enqueue(new NetworkCallback<CreateMercentResponse>() {
            @Override
            public void onSuccess(CreateMercentResponse successResponse) {
                if (successResponse != null) {
                    productDetailsModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                productDetailsModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                productDetailsModel.setError(t);
            }
        });
    }

    // This api is used for create merchant for cashout
    public void getVendorId(final RichMediatorLiveData<VendorIdResponse> productDetailsModel, HashMap<String, Object> parms) {
        DataManager.getInstance().getVendorId(parms).enqueue(new NetworkCallback<VendorIdResponse>() {
            @Override
            public void onSuccess(VendorIdResponse successResponse) {
                if (successResponse != null) {
                    productDetailsModel.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                productDetailsModel.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                productDetailsModel.setError(t);
            }
        });
    }

    public void saveBankDetails(final RichMediatorLiveData<CommonResponse> mLiveData, HashMap<String, Object> parms) {
        DataManager.getInstance().saveBankDetails(parms).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
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


    public void getBankDetails(final RichMediatorLiveData<GetBankDetail> mLiveData) {
        DataManager.getInstance().getBankDetails().enqueue(new NetworkCallback<GetBankDetail>() {
            @Override
            public void onSuccess(GetBankDetail successResponse) {
                if (successResponse != null) {
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

    // Get HTML content for Terms & Condition , Privacy and FAQ
    public void getHtmlContent(final RichMediatorLiveData<ContentViewModel> mLiveData, String type) {
        DataManager.getInstance().getHtmlContent(type).enqueue(new NetworkCallback<ContentViewModel>() {
            @Override
            public void onSuccess(ContentViewModel successResponse) {
                if (successResponse != null) {
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


    public void cashOutBalance(final RichMediatorLiveData<BalanceResponse> liveData, HashMap<String, Object> parms) {
        DataManager.getInstance().cashOutBalance(parms).enqueue(new NetworkCallback<BalanceResponse>() {
            @Override
            public void onSuccess(BalanceResponse successResponse) {
                if (successResponse != null) {
                    liveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    // This Api is use for get Balance  of user
    public void getBalance(final RichMediatorLiveData<BalanceResponse> liveData) {
        DataManager.getInstance().getBalance().enqueue(new NetworkCallback<BalanceResponse>() {
            @Override
            public void onSuccess(BalanceResponse successResponse) {
                if (successResponse != null) {
                    liveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    // This Api is use for get Payment History List
    public void getPaymentHistory(final RichMediatorLiveData<PaymentHistoryModel> liveData) {
        DataManager.getInstance().getPaymentHistroy().enqueue(new NetworkCallback<PaymentHistoryModel>() {
            @Override
            public void onSuccess(PaymentHistoryModel successResponse) {
                if (successResponse != null) {
                    liveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    // This is Api is use for initiate Refund
    public void initiateRefund(final RichMediatorLiveData<PaymentRefundModel> liveData, String orderId, final int requestCode) {
        DataManager.getInstance().initiateRefund(orderId).enqueue(new NetworkCallback<PaymentRefundModel>() {
            @Override
            public void onSuccess(PaymentRefundModel successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    liveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    // This Api is use for mark item received by Buyer
    public void confirmItemReceived(final RichMediatorLiveData<PaymentRefundModel> liveData, String orderId, final int requestCode) {
        DataManager.getInstance().confirmItemReceived(orderId).enqueue(new NetworkCallback<PaymentRefundModel>() {
            @Override
            public void onSuccess(PaymentRefundModel successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    liveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    // This Api is use for mark return item received by seller
    public void confirmReturnItemReceivedSeller(final RichMediatorLiveData<PaymentRefundModel> liveData, String orderId, final int requestCode) {
        DataManager.getInstance().confirmReturnItemReceivedSeller(orderId).enqueue(new NetworkCallback<PaymentRefundModel>() {
            @Override
            public void onSuccess(PaymentRefundModel successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    liveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    // This Api is use for accept the return request by seller
    public void returnRequestAccept(final RichMediatorLiveData<PaymentRefundModel> liveData, String orderId, final int requestCode) {
        DataManager.getInstance().returnRequestAccept(orderId).enqueue(new NetworkCallback<PaymentRefundModel>() {
            @Override
            public void onSuccess(PaymentRefundModel successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    liveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    // This Api is use for accept the return request by seller
    public void cancelDispute(final RichMediatorLiveData<PaymentRefundModel> liveData, String orderId, String action, final int requestCode) {
        DataManager.getInstance().cancelDispute(orderId, action).enqueue(new NetworkCallback<PaymentRefundModel>() {
            @Override
            public void onSuccess(PaymentRefundModel successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    liveData.setValue(successResponse);
            }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    public void declineRetrunRequest(final RichMediatorLiveData<PaymentRefundModel> liveData, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().declineReturnRequest(parms).enqueue(new NetworkCallback<PaymentRefundModel>() {
            @Override
            public void onSuccess(PaymentRefundModel successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    liveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    // This Api is use for initate Dispute  by buyer and submit proof by sellet
    public void initiateDispute(final RichMediatorLiveData<PaymentRefundModel> liveData, HashMap<String, Object> parms, final int requestCode) {
        DataManager.getInstance().initiateDispute(parms).enqueue(new NetworkCallback<PaymentRefundModel>() {
            @Override
            public void onSuccess(PaymentRefundModel successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    liveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    // This Api is use for add Debit card to stripe
    public void addDebitCard(final RichMediatorLiveData<CommonResponse> liveData, String token, String name, final int requestCode) {
        DataManager.getInstance().addDebitCard(token, name).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    liveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    // This Api is use for get Merchant Details
    public void merchantDetail(final RichMediatorLiveData<MerchantDetailBeans> liveData, final int requestCode) {
        DataManager.getInstance().getMerchantDetails(DataManager.getInstance().getUserDetails().getUserId()).enqueue(new NetworkCallback<MerchantDetailBeans>() {
            @Override
            public void onSuccess(MerchantDetailBeans successResponse) {
                if (successResponse != null) {
                    successResponse.setRequestCode(requestCode);
                    liveData.setValue(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                liveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                liveData.setError(t);
            }
        });
    }

    // This Api is use for get product Status
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

    // This Api is use for get product Status
    public void getCommonResponseData(final RichMediatorLiveData<CommonDataModel> tagLiveData) {
        DataManager.getInstance().getCommonResponseData().enqueue(new NetworkCallback<CommonDataModel>() {
            @Override
            public void onSuccess(CommonDataModel commonResponse) {
                if (commonResponse != null) {
                    tagLiveData.setValue(commonResponse);
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
