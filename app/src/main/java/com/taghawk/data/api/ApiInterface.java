package com.taghawk.data.api;


import com.taghawk.constants.AppConstants;
import com.taghawk.model.AddProduct.AddProductModel;
import com.taghawk.model.AddressUpdateResponse;
import com.taghawk.model.CommonDataModel;
import com.taghawk.model.ContentViewModel;
import com.taghawk.model.CreateSiftOrderRequest;
import com.taghawk.model.CreateSiftOrderResponse;
import com.taghawk.model.DeleteAddressRequest;
import com.taghawk.model.NotificationModel;
import com.taghawk.model.PaymentHistoryModel;
import com.taghawk.model.PaymentRefundModel;
import com.taghawk.model.PaymentStatusFailureModel;
import com.taghawk.model.PaymentStatusRequest;
import com.taghawk.model.SearchModel;
import com.taghawk.model.ShippingAddressesResponse;
import com.taghawk.model.ShopperIdResponse;
import com.taghawk.model.VendorIdResponse;
import com.taghawk.model.block_user.BlockUserModel;
import com.taghawk.model.cart.CartModel;
import com.taghawk.model.cashout.MerchantDetailBeans;
import com.taghawk.model.category.CategoryResponse;
import com.taghawk.model.chat.DeleteTagRequest;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.follow_following.FollowFollowingBean;
import com.taghawk.model.gift.GiftRewardsPromotionModel;
import com.taghawk.model.home.DeleteProductRequest;
import com.taghawk.model.home.LikeUnLike;
import com.taghawk.model.home.ProductDetailsModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.model.login.CheckSocialLoginmodel;
import com.taghawk.model.login.LoginModel;
import com.taghawk.model.pendingRequests.PendingRequestResponse;
import com.taghawk.model.profileresponse.BalanceResponse;
import com.taghawk.model.profileresponse.ProfileProductsResponse;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.model.refreshtoken.RefreshTokenResponse;
import com.taghawk.model.request.ChangePassword;
import com.taghawk.model.request.Reset;
import com.taghawk.model.request.UserResponse;
import com.taghawk.model.review_rating.ReviewRatingModel;
import com.taghawk.model.strip.CreateMercentResponse;
import com.taghawk.model.strip.FedexRateResponse;
import com.taghawk.model.strip.GetBankDetail;
import com.taghawk.model.tag.MyTagResponse;
import com.taghawk.model.tag.TagDetailsModel;
import com.taghawk.model.tag.TagModel;
import com.taghawk.model.tag.TagSearchBean;
import com.taghawk.model.tag.UserSpecificTagsModel;
import com.taghawk.model.tagaddresponse.AddTagResponse;
import com.taghawk.model.update_rating_notification.UpdateRatingNotificationBean;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by appinventiv on 27/3/18.
 */

public interface ApiInterface {

    //    String BASE_URL = "https://taghawkdev.appskeeper.com/api/v1/";
    //    String BASE_URL = "http://taghawkdev.appskeeper.com:7307/api/v1/";
//    String BASE_URL = "http://10.10.8.99:3000/api/v1/";
//    String BASE_URL = "https://taghawkdev.appskeeper.com/api/v1/";
//            String BASE_URL = "http://taghawkdev.appskeeper.com:7307/api/v1/";
//    String BASE_URL = "http://10.10.7.253:3000/api/v1/";
    String FIREBASE_PUSH_API = "https://fcm.googleapis.com/fcm/send";

    //    String BASE_URL = "http://taghawkstg.appskeeper.com:7308/api/v1/";
    //    Live
//    String BASE_URL = "http://13.125.247.137:2000/api/v1/";
//    String BASE_URL = "http://13.209.26.65:2000/api/v2/";

//    String BASE_URL = "http://54.180.152.39:2000/api/v2/";
//    String BASE_URL = "https://api.taghawk.app/api/v1/";
//    String BASE_URL = "http://13.125.247.137:2000/api/v1/";

    //Live
//    String BASE_URL = "https://api.taghawk.app/api/v2/";

    //Development
    String BASE_URL = "http://34.201.100.155:3000/api/v2/";

    @POST("user/login")
    Call<UserResponse> login(@Body LoginModel user);

    @FormUrlEncoded
    @POST("user")
    Call<UserResponse> signUp(@FieldMap HashMap<String, String> user);

    //    @POST("change-password")
    //    Call<CommonResponse> changePassword(@Body User user);

    @POST("common/change-forgot-password")
    Call<CommonResponse> resetPassword(@Body Reset reset);

    @FormUrlEncoded
    @POST("refresh")
    Call<RefreshTokenResponse> refreshToken(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("user/forgot-password")
    Call<CommonResponse> forgotPassword(@Field("email") String email, @Field("userType") String userType);

    @FormUrlEncoded
    @POST("user/social-login")
    Call<UserResponse> socialLogin(@FieldMap HashMap<String, Object> user);

    @PUT("logout")
    Call<CommonResponse> logOut();

    @GET("category")
    Call<CategoryResponse> getCategoryList();

    @GET("product")
    Call<ProductListingModel> getProductListing(@QueryMap HashMap<String, Object> parms);

    @GET("product/suggestion")
    Call<SearchModel> getSearchSuggestion(@QueryMap HashMap<String, Object> parms);

    @GET("tag/suggestion")
    Call<TagSearchBean> getTagSearchList(@QueryMap HashMap<String, Object> parms);

    @GET("product/info")
    Call<ProductDetailsModel> getProductDetails(@Query("id") String parms);

    @FormUrlEncoded
    @POST("user-guest")
    Call<UserResponse> guestUserLogin(@FieldMap HashMap<String, String> parms);

    @FormUrlEncoded
    @PUT("product-like-unlike")
    Call<LikeUnLike> getProductLikeUnLike(@FieldMap HashMap<String, Object> parms);

    @FormUrlEncoded
    @PUT("user-report-product")
    Call<LikeUnLike> reportProduct(@FieldMap HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("product/share")
    Call<LikeUnLike> shareProduct(@FieldMap HashMap<String, Object> parms);

    @GET("tag")
    Call<TagModel> tagListing(@QueryMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("user/logout")
    Call<CommonResponse> logout(@FieldMap HashMap<String, Object> parms);

    @GET("tag/info")
    Call<TagDetailsModel> getTagDetails(@QueryMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("product/visitedtag")
    Call<TagDetailsModel> getTagVisited(@FieldMap() HashMap<String, Object> parms);

    @GET("user/profile")
    Call<ProfileResponse> getProfileDetails(@QueryMap() HashMap<String, Object> parms);

    @GET("tag/user")
    Call<UserSpecificTagsModel> getUserSpecificTag(@QueryMap() HashMap<String, Object> parms);

    @GET("user/product")
    Call<ProfileProductsResponse> getProfileProducts(@QueryMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("product")
    Call<AddProductModel> addProduct(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("tag")
    Call<AddTagResponse> addTag(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("tag/request-member")
    Call<CommonResponse> joinTag(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("product-promotion")
    Call<CommonResponse> markProductFeatured(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @PUT("product")
    Call<AddProductModel> editProduct(@FieldMap() HashMap<String, Object> parms);


    @GET("cart")
    Call<CartModel> getCartList();

    @GET("user/getvaultedShopperId")
    Call<ShopperIdResponse> getVaultedShopperId(@QueryMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("cart")
    Call<CommonResponse> addProductToCart(@FieldMap() HashMap<String, Object> parms);

    @HTTP(method = "DELETE", path = "product", hasBody = true)
    Call<CommonResponse> deleteProduct(@Body DeleteProductRequest myBodyRequest);

    @FormUrlEncoded
    @PUT("user/update-profile")
    Call<CommonResponse> editProfile(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @PUT("user/verify-phone")
    Call<CommonResponse> otpVerification(@FieldMap() HashMap<String, Object> parms);

    @GET("notification/list")
    Call<NotificationModel> notificationList(@Query("pageNo") int pageNumber, @Query("limit") int limit);

    @FormUrlEncoded
    @PUT("notification/mark-read")
    Call<CommonResponse> markNotificationRead(@Field("notificationId") String notificationId);

    @FormUrlEncoded
    @POST("user/friend-follow")
    Call<ProfileResponse> followFriend(@Field("userId") String userId);

    @FormUrlEncoded
    @PUT("tag")
    Call<AddTagResponse> editTag(@FieldMap() HashMap<String, Object> parms);

    @GET("user/friends")
    Call<FollowFollowingBean> getFollowFollowingList(@QueryMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @PUT("user/friend-remove")
    Call<ProfileResponse> removeUnfriend(@FieldMap() HashMap<String, Object> parms);

    @GET("rating")
    Call<ReviewRatingModel> getReviewRating(@QueryMap() HashMap<String, Object> parms);


    @FormUrlEncoded
    @POST("rating/reply")
    Call<CommonResponse> replyEditComment(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("rating")
    Call<CommonResponse> giveFeedback(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @PUT("user/update-device-token")
    Call<UpdateRatingNotificationBean> updateDeviceToken(@Field("deviceToken") String deviceToken);

    @Headers("Content-Type: application/json")
    @POST("payment/create/charges")
    Call<CommonResponse> doPayment(@Body PaymentStatusRequest paymentStatusRequest);

    @Headers("Content-Type: application/json")
    @POST("payment/failure")
    Call<CommonResponse> paymentFailure(@Body PaymentStatusFailureModel paymentStatusRequest);

    @Headers("Content-Type: application/json")
    @POST("payment/siftCreateOrder")
    Call<CreateSiftOrderResponse> createSiftOrder(@Body CreateSiftOrderRequest paymentStatusRequest);

    @FormUrlEncoded
    @POST("payment/checkSoldOutProduct")
    Call<CommonResponse> checkSoldOutProduct(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("product/buy/zeroPrize")
    Call<CommonResponse> doZeroPayment(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @PUT("rating")
    Call<CommonResponse> denyFeedback(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("payment/create/Merchant")
    Call<CreateMercentResponse> createMercent(@FieldMap() HashMap<String, Object> parms);

    @GET("user/getvendorsId")
    Call<VendorIdResponse> getVendorId(@QueryMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @PUT("payment/update/Merchant")
    Call<CommonResponse> saveBankDetails(@FieldMap() HashMap<String, Object> parms);

    @GET("payment/bankData")
    Call<GetBankDetail> getBankDetails();

    @POST("user/change-password")
    Call<CommonResponse> changePassword(@Body ChangePassword reset);

    @FormUrlEncoded
    @POST("tag/transfer-ownership")
    Call<CommonResponse> transferOwnership(@FieldMap() HashMap<String, Object> parms);

    @GET("tag/user")
    Call<MyTagResponse> getMyTags(@QueryMap() HashMap<String, Object> parms);

    @GET("tag/pending-request")
    Call<PendingRequestResponse> getTagPendingRequests(@QueryMap() HashMap<String, Object> parms);

    @HTTP(method = "DELETE", path = "tag/delete/owner", hasBody = true)
    Call<CommonResponse> deleteTag(@Body DeleteTagRequest deleteTagRequest);

    @FormUrlEncoded
    @PUT("tag/report")
    Call<CommonResponse> reportTag(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @PUT("tag/exit")
    Call<CommonResponse> exitTag(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @PUT("tag/remove-member")
    Call<CommonResponse> removeMember(@FieldMap() HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("tag/block-unblock-user")
    Call<CommonResponse> blockUserFromTag(@FieldMap() HashMap<String, Object> parms);

    @GET("user/blocked-list")
    Call<BlockUserModel> getBlockUserList(@QueryMap() HashMap<String, Object> parms);

    @GET("content/view")
    Call<ContentViewModel> getContentView(@Query("type") String type);

    @FormUrlEncoded
    @POST("shipping-rates")
    Call<FedexRateResponse> getFedexShippingRate(@FieldMap HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("add/saveaddress")
    Call<CommonResponse> addProfileAddresses(@FieldMap HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("add/saveBillingAddress")
    Call<ShippingAddressesResponse> addBillingAddress(@FieldMap HashMap<String, Object> parms);

    @GET("shipping-address")
    Call<ShippingAddressesResponse> getShippingAddressesResponse(@Query("userId") String userId);

    @GET("billing-address")
    Call<ShippingAddressesResponse> getBillingAddress(@Query("userId") String userId);

    @FormUrlEncoded
    @PUT("update/shipping-address")
    Call<AddressUpdateResponse> updateShippingAddress(@FieldMap HashMap<String, Object> parms);

    @HTTP(method = "DELETE", path = "delete/shipping-address", hasBody = true)
    Call<CommonResponse> deleteShippingAddress(@Body DeleteAddressRequest deleteAddressRequest);

    @FormUrlEncoded
    @PUT("user/notification-setting")
    Call<CommonResponse> notificationOnOff(@Field("isMute") int isMute);

    @FormUrlEncoded
    @PUT("tag/accept-reject-request")
    Call<TagDetailsModel> acceptRejectTagRequestFromLink(@FieldMap HashMap<String, Object> parms);

    @FormUrlEncoded
    @PUT("tag/accept-reject-request")
    Call<CommonResponse> acceptRejectTagRequest(@FieldMap HashMap<String, Object> parms);

//    @GET("tag/products")
//    Call<ProductListingModel> getTagProducts(@QueryMap() HashMap<String, Object> parms);

    @GET("tag/products_new")
    Call<ProductListingModel> getTagProducts(@QueryMap() HashMap<String, Object> parms);

    @GET("user/verifyEmail")
    Call<CommonResponse> emailVerify(@Query("token") String token);

    @GET("user/balance")
    Call<BalanceResponse> getBalance();

    @POST("fcm/send")
    Call<ResponseBody> sendPiushNotificationOnFirebase(@Body JSONObject jsonObject);

    @FormUrlEncoded
    @POST("payment/payouts")
    Call<BalanceResponse> cashoutBalance(@FieldMap HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("payment/cashout")
    Call<CommonResponse> cashoutBalance1(@FieldMap HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("product-promotion-selective")
    Call<CommonResponse> productPromotionSelection(@FieldMap HashMap<String, Object> parms);


    @GET("promotion-packages")
    Call<GiftRewardsPromotionModel> getRewardsPromotions();

    @FormUrlEncoded
    @POST("shipping-labels")
    Call<FedexRateResponse> createLable(@FieldMap HashMap<String, Object> parms);

    @FormUrlEncoded
    @PUT("order/refund")
    Call<PaymentRefundModel> initiateRefund(@Field("orderId") String orderId);

    @FormUrlEncoded
    @PUT("payment/release")
    Call<PaymentRefundModel> confirmItemReceived(@Field("orderId") String orderId);

    @FormUrlEncoded
    @PUT("payment/refund/release")
    Call<PaymentRefundModel> confirmReturnItemReceivedSeller(@Field("orderId") String orderId);

    @FormUrlEncoded
    @PUT("payment/refund/accept")
    Call<PaymentRefundModel> returnRequestAccept(@Field("orderId") String orderId);

    @FormUrlEncoded
    @PUT("order/refund/cancel")
    Call<PaymentRefundModel> cancelDispute(@Field("orderId") String orderId, @Field("action") String action);

    @FormUrlEncoded
    @PUT("payment/refund/decline")
    Call<PaymentRefundModel> declineReturnRequest(@FieldMap HashMap<String, Object> parms);

    @FormUrlEncoded
    @POST("payment/dispute")
    Call<PaymentRefundModel> initiateDispute(@FieldMap HashMap<String, Object> parms);

//    @FormUrlEncoded
//    @PUT("payment/refund/accept")
//    Call<PaymentRefundModel> returnRequestAccept(@Field("orderId") String orderId);

    @GET("payment/history")
    Call<PaymentHistoryModel> getPaymentHistroy();

    @GET("product/order/details")
    Call<PaymentRefundModel> getProductStatusApi(@Query(AppConstants.KEY_CONSTENT.PRODUCT_ID) String productId);

    @Multipart
    @POST("payment/upload/identity")
    Call<CommonResponse> uploadDocument(@Part MultipartBody.Part documentImage, @Part MultipartBody.Part backDocumentImage);

    @FormUrlEncoded
    @POST("payment/addDebitCard")
    Call<CommonResponse> addDebitCard(@Field("token") String cardToken, @Field("name") String name);

    @FormUrlEncoded
    @POST("user/checkSocialLogin")
    Call<CheckSocialLoginmodel> checkSocialLogin(@FieldMap() HashMap<String, String> parms);

    @GET("payment/merchant")
    Call<MerchantDetailBeans> getmerchantDetails(@Query("userId") String userId);

    @GET("user/common/info")
    Call<CommonDataModel> getCommonData();


}

