package com.taghawk.data.api;



import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

import com.taghawk.BuildConfig;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.QueryMap;

/**
 * Created by appinventiv on 27/3/18.
 */

public class ApiManager {

    private static final ApiManager instance = new ApiManager();
    //    public static String mAuth = "Basic " + Base64.encodeToString("taghawk:taghawk@123", Base64.DEFAULT);
    private static String mAuth = Credentials.basic("taghawk", "taghawk@123");
    private static OkHttpClient sOkHttpClient;
    private ApiInterface apiClient, authenticatedApiClient;
    private OkHttpClient.Builder httpClient;

    private ApiManager() {
        apiClient = getRetrofitService();
        httpClient = getHttpClient();
        authenticatedApiClient = getAuthenticatedRetrofitService();
    }

    public static ApiManager getInstance() {
        return instance;
    }

    private static ApiInterface getRetrofitService() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ApiInterface.BASE_URL)
                .build();

        return retrofit.create(ApiInterface.class);
    }

    private ApiInterface getAuthenticatedRetrofitService() {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(ApiInterface.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = retrofitBuilder.client(httpClient.build()).build();
        return retrofit.create(ApiInterface.class);
    }

    private ApiInterface getFirebaseAuthenticatedRetrofitService() {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(ApiInterface.FIREBASE_PUSH_API)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = retrofitBuilder.client(getHttpClientForFirebasePush().build()).build();
        return retrofit.create(ApiInterface.class);
    }

    /**
     * Method to create {@link OkHttpClient} builder by adding required headers in the {@link Request}
     *
     * @return OkHttpClient object
     */
    private OkHttpClient.Builder getHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();
                        if (DataManager.getInstance().getAccessToken() != null && DataManager.getInstance().getAccessToken().length() > 0) {
                            mAuth = "Bearer " + DataManager.getInstance().getAccessToken();
                        } else {
                            mAuth = Credentials.basic("taghawk", "taghawk@123");
                        }
                        Request request = original.newBuilder()
                                .addHeader("api_key", "1234")
                                .addHeader("Authorization", mAuth)
                                .addHeader(AppConstants.KEY_CONSTENT.PLATEFORM, "1")
                                .addHeader(AppConstants.KEY_CONSTENT.OS_VERSION, "" + android.os.Build.VERSION.RELEASE)
                                .addHeader(AppConstants.KEY_CONSTENT.DEVICE_MENUFECTURER, "" + android.os.Build.MANUFACTURER)
                                .addHeader(AppConstants.KEY_CONSTENT.DEVICE_MODEL, "" + Build.MODEL)
                                .addHeader(AppConstants.KEY_CONSTENT.APP_VERSION, "" + BuildConfig.VERSION_NAME)
                                .addHeader(AppConstants.KEY_CONSTENT.DEVICE_ID, "" + DataManager.getInstance().getDeviceId())
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                });

//        if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(logging);
//        }
//        sOkHttpClient = okHttpClientBuilder.build();
        return okHttpClientBuilder;
    }

    /**
     * Method to create {@link OkHttpClient} builder by adding required headers in the {@link Request}
     *
     * @return OkHttpClient object
     */
    private OkHttpClient.Builder getHttpClientForFirebasePush() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", AppConstants.FIREBASE.FIREBASE_SERVER_KEY)
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                });

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(logging);
        }
        return okHttpClientBuilder;
    }

    public Call<ResponseBody> hitFirebasePushApi(JSONObject jsonObject) {
        return getFirebaseAuthenticatedRetrofitService().sendPiushNotificationOnFirebase(jsonObject);
    }

    public Call<BalanceResponse> cashOutBalance(HashMap<String, Object> parms) {
        return authenticatedApiClient.cashoutBalance(parms);
    }

    public Call<CommonResponse> cashoutBalance1(HashMap<String, Object> parms) {
        return authenticatedApiClient.cashoutBalance1(parms);
    }

    public Call<UserResponse> hitLoginApi(LoginModel user) {
        return authenticatedApiClient.login(user);
    }

    public Call<UserResponse> hitGuestLoginApi(HashMap<String, String> user) {
        return authenticatedApiClient.guestUserLogin(user);
    }

    public Call<RefreshTokenResponse> refreshToken(HashMap<String, String> params) {
        return authenticatedApiClient.refreshToken(params);
    }

    public Call<UserResponse> hitSignUpApi(HashMap<String, String> user) {
        return authenticatedApiClient.signUp(user);
    }

    public Call<CommonResponse> hitPasswordApi(String email, String userType) {
        return authenticatedApiClient.forgotPassword(email, userType);
    }


    public Call<UserResponse> hitSocialLoginApi(HashMap<String, Object> parms) {
        return authenticatedApiClient.socialLogin(parms);
    }

    public Call<CommonResponse> hitResetPasswordApi(Reset reset) {
        return authenticatedApiClient.resetPassword(reset);
    }

    public Call<CommonResponse> hitLogOutPassword() {
        return authenticatedApiClient.logOut();
    }

    public Call<CategoryResponse> hitGetCategoryList() {
        return authenticatedApiClient.getCategoryList();
    }

//    public Call<CommonResponse> hitChangePasswordApi(User user) {
//        return authenticatedApiClient.changePassword(user);
//    }

    public Call<ProductListingModel> getProductList(HashMap<String, Object> parms) {
        return authenticatedApiClient.getProductListing(parms);
    }

    public Call<SearchModel> getSearchSuggestion(String accessToken, HashMap<String, Object> parms) {
        return authenticatedApiClient.getSearchSuggestion(parms);
    }


    public Call<TagSearchBean> getTagSearch(String accessToken, HashMap<String, Object> parms) {
        return authenticatedApiClient.getTagSearchList(parms);
    }

    public Call<ProductDetailsModel> getProductDetail(String id) {
        return authenticatedApiClient.getProductDetails(id);
    }

    public Call<LikeUnLike> getLikeUnLike(HashMap<String, Object> parms) {

        return authenticatedApiClient.getProductLikeUnLike(parms);
    }

    public Call<LikeUnLike> hitReportProduct(HashMap<String, Object> parms) {

        return authenticatedApiClient.reportProduct(parms);
    }

    public Call<LikeUnLike> hitshareProduct(HashMap<String, Object> parms) {

        return authenticatedApiClient.shareProduct(parms);
    }

    public Call<TagModel> hitTagList(HashMap<String, Object> parms) {

        return authenticatedApiClient.tagListing(parms);
    }

    public Call<CommonResponse> logout(HashMap<String, Object> parms) {

        return authenticatedApiClient.logout(parms);
    }

    public Call<TagDetailsModel> hitTagDetails(HashMap<String, Object> parms) {

        return authenticatedApiClient.getTagDetails(parms);
    }

    public Call<TagDetailsModel> getTagVisited(HashMap<String, Object> parms) {

        return authenticatedApiClient.getTagVisited(parms);
    }

    public Call<ProfileResponse> hitProfileDetails(HashMap<String, Object> parms) {

        return authenticatedApiClient.getProfileDetails(parms);
    }

    public Call<UserSpecificTagsModel> getTagsUserSpecific(HashMap<String, Object> parms) {

        return authenticatedApiClient.getUserSpecificTag(parms);
    }

    public Call<ProfileProductsResponse> getProfileProducts(HashMap<String, Object> parms) {

        return authenticatedApiClient.getProfileProducts(parms);
    }

    public Call<AddProductModel> addProduct(HashMap<String, Object> parms) {

        return authenticatedApiClient.addProduct(parms);
    }

    public Call<AddProductModel> editProduct(HashMap<String, Object> parms) {

        return authenticatedApiClient.editProduct(parms);
    }

    public Call<CommonResponse> editProfile(HashMap<String, Object> parms) {

        return authenticatedApiClient.editProfile(parms);
    }

    public Call<AddTagResponse> addTag(HashMap<String, Object> parms) {
        return authenticatedApiClient.addTag(parms);
    }

    public Call<CommonResponse> joinTag(HashMap<String, Object> parms) {
        return authenticatedApiClient.joinTag(parms);
    }

    public Call<CommonResponse> transferOwnership(HashMap<String, Object> parms) {
        return authenticatedApiClient.transferOwnership(parms);
    }

    public Call<MyTagResponse> getMyTags(HashMap<String, Object> parms) {
        return authenticatedApiClient.getMyTags(parms);
    }

    public Call<CommonResponse> blockUser(HashMap<String, Object> parms) {
        return authenticatedApiClient.blockUserFromTag(parms);
    }

    public Call<PendingRequestResponse> pendingRequests(HashMap<String, Object> parms) {
        return authenticatedApiClient.getTagPendingRequests(parms);
    }

    public Call<CommonResponse> markFeatured(HashMap<String, Object> parms) {

        return authenticatedApiClient.markProductFeatured(parms);
    }

    public Call<CommonResponse> addProductToCart(HashMap<String, Object> parms) {
        return authenticatedApiClient.addProductToCart(parms);
    }

    public Call<CommonResponse> deleteProduct(DeleteProductRequest request) {
        return authenticatedApiClient.deleteProduct(request);
    }

    public Call<CartModel> getCartList() {
        return authenticatedApiClient.getCartList();
    }

    public Call<ShopperIdResponse> getVaultedShopperId(@QueryMap() HashMap<String, Object> parms) {
        return authenticatedApiClient.getVaultedShopperId(parms);
    }

    public Call<CommonResponse> otpVerify(HashMap<String, Object> parms) {
        return authenticatedApiClient.otpVerification(parms);
    }

    public Call<NotificationModel> getNotification(int page, int limit) {
        return authenticatedApiClient.notificationList(page, limit);
    }

    public Call<CommonResponse> markNotificationRead(String markNotification) {
        return authenticatedApiClient.markNotificationRead(markNotification);
    }

    public Call<AddTagResponse> editTag(HashMap<String, Object> parms) {
        return authenticatedApiClient.editTag(parms);
    }

    public Call<CommonResponse> reportTag(HashMap<String, Object> parms) {
        return authenticatedApiClient.reportTag(parms);
    }

    public Call<CommonResponse> deleteTag(DeleteTagRequest deleteTagRequest) {
        return authenticatedApiClient.deleteTag(deleteTagRequest);
    }

    public Call<CommonResponse> exitTag(HashMap<String, Object> parms) {
        return authenticatedApiClient.exitTag(parms);
    }

    public Call<PaymentRefundModel> productStatus(String productId) {
        return authenticatedApiClient.getProductStatusApi(productId);
    }

    public Call<CommonResponse> removeMember(HashMap<String, Object> parms) {
        return authenticatedApiClient.removeMember(parms);
    }

    public Call<ProfileResponse> followFriend(String parms) {
        return authenticatedApiClient.followFriend(parms);
    }

    public Call<FollowFollowingBean> getFollowFollowingList(HashMap<String, Object> parms) {
        return authenticatedApiClient.getFollowFollowingList(parms);
    }

    public Call<ProfileResponse> removeUnfriend(HashMap<String, Object> parms) {
        return authenticatedApiClient.removeUnfriend(parms);
    }

    public Call<ReviewRatingModel> getReviewRating(HashMap<String, Object> parms) {
        return authenticatedApiClient.getReviewRating(parms);
    }

    public Call<CommonResponse> replyEditComment(HashMap<String, Object> parms) {
        return authenticatedApiClient.replyEditComment(parms);
    }

    public Call<CommonResponse> giveFeedback(HashMap<String, Object> parms) {
        return authenticatedApiClient.giveFeedback(parms);
    }

    public Call<UpdateRatingNotificationBean> updateDeviceToken(String parms) {
        return authenticatedApiClient.updateDeviceToken(parms);
    }

    public Call<CommonResponse> denyFeedback(HashMap<String, Object> parms) {
        return authenticatedApiClient.denyFeedback(parms);
    }

    public Call<CommonResponse> doPayment(PaymentStatusRequest paymentStatusRequest) {
        return authenticatedApiClient.doPayment(paymentStatusRequest);
    }

    public Call<CommonResponse> paymentFailure(PaymentStatusFailureModel paymentStatusRequest) {
        return authenticatedApiClient.paymentFailure(paymentStatusRequest);
    }

    public Call<CreateSiftOrderResponse> createSiftOrder(CreateSiftOrderRequest createSiftOrderRequest) {
        return authenticatedApiClient.createSiftOrder(createSiftOrderRequest);
    }

    public Call<CommonResponse> checkSoldOutProduct(HashMap<String, Object> parms) {
        return authenticatedApiClient.checkSoldOutProduct(parms);
    }


    public Call<CommonResponse> doZeroPayment(HashMap<String, Object> parms) {
        return authenticatedApiClient.doZeroPayment(parms);
    }

    public Call<CreateMercentResponse> createMerchent(HashMap<String, Object> parms) {
        return authenticatedApiClient.createMercent(parms);
    }

    public Call<VendorIdResponse> getVendorId(HashMap<String, Object> parms) {
        return authenticatedApiClient.getVendorId(parms);
    }

    public Call<CommonResponse> saveBankDetails(HashMap<String, Object> parms) {
        return authenticatedApiClient.saveBankDetails(parms);
    }

    public Call<GetBankDetail> getBankDetails() {
        return authenticatedApiClient.getBankDetails();
    }

    public Call<CommonResponse> changePassword(ChangePassword changePassword) {
        return authenticatedApiClient.changePassword(changePassword);
    }

    public Call<BlockUserModel> getBlockUserList(HashMap<String, Object> parms) {
        return authenticatedApiClient.getBlockUserList(parms);
    }

    public Call<ContentViewModel> getHtmlContent(String type) {
        return authenticatedApiClient.getContentView(type);
    }

    public Call<FedexRateResponse> getFexdexShippingRate(HashMap<String, Object> parms) {
        return authenticatedApiClient.getFedexShippingRate(parms);
    }

    public Call<CommonResponse> addProfileAddresses(HashMap<String, Object> parms) {
        return authenticatedApiClient.addProfileAddresses(parms);
    }

    public Call<ShippingAddressesResponse> addBillingAddress(HashMap<String, Object> parms) {
        return authenticatedApiClient.addBillingAddress(parms);
    }

    public Call<AddressUpdateResponse> updateShippingAddress(HashMap<String, Object> parms) {
        return authenticatedApiClient.updateShippingAddress(parms);
    }

    public Call<ShippingAddressesResponse> getShippingAddressesResponse(String userId) {
        return authenticatedApiClient.getShippingAddressesResponse(userId);
    }

    public Call<ShippingAddressesResponse> getBillingAddress(String userId) {
        return authenticatedApiClient.getBillingAddress(userId);
    }

    public Call<CommonResponse> deleteShippingAddress(DeleteAddressRequest deleteAddressRequest) {
        return authenticatedApiClient.deleteShippingAddress(deleteAddressRequest);
    }

    public Call<CommonResponse> notificationOnOff(int status) {
        return authenticatedApiClient.notificationOnOff(status);
    }

    public Call<TagDetailsModel> acceptRejectTagRequestFromLink(HashMap<String, Object> parms) {
        return authenticatedApiClient.acceptRejectTagRequestFromLink(parms);
    }

    public Call<CommonResponse> acceptRejectTagRequest(HashMap<String, Object> parms) {
        return authenticatedApiClient.acceptRejectTagRequest(parms);
    }

    public Call<CommonResponse> verifyEmail(String token) {
        return authenticatedApiClient.emailVerify(token);
    }

    public Call<BalanceResponse> getBalance() {
        return authenticatedApiClient.getBalance();
    }


    public Call<ProductListingModel> getTagProduct(HashMap<String, Object> parms) {
        return authenticatedApiClient.getTagProducts(parms);
    }

    public Call<GiftRewardsPromotionModel> getRewardsPromotions() {
        return authenticatedApiClient.getRewardsPromotions();
    }

    public Call<CommonResponse> productPromotionSelection(HashMap<String, Object> parms) {
        return authenticatedApiClient.productPromotionSelection(parms);
    }

    public Call<FedexRateResponse> createLable(HashMap<String, Object> parms) {
        return authenticatedApiClient.createLable(parms);
    }

    public Call<PaymentHistoryModel> getPaymentHistroy() {
        return authenticatedApiClient.getPaymentHistroy();
    }

    public Call<PaymentRefundModel> initiateRefund(String orderId) {
        return authenticatedApiClient.initiateRefund(orderId);
    }

    public Call<PaymentRefundModel> confirmItemReceived(String orderId) {
        return authenticatedApiClient.confirmItemReceived(orderId);
    }

    public Call<PaymentRefundModel> confirmReturnItemReceivedSeller(String orderId) {
        return authenticatedApiClient.confirmReturnItemReceivedSeller(orderId);
    }

    public Call<PaymentRefundModel> returnRequestAccept(String orderId) {
        return authenticatedApiClient.returnRequestAccept(orderId);
    }

    public Call<PaymentRefundModel> cancelDispute(String orderId,String action) {
        return authenticatedApiClient.cancelDispute(orderId, action);
    }

    public Call<PaymentRefundModel> declineReturnRequest(HashMap<String, Object> parms) {
        return authenticatedApiClient.declineReturnRequest(parms);
    }

    public Call<PaymentRefundModel> initiateDispute(HashMap<String, Object> parms) {
        return authenticatedApiClient.initiateDispute(parms);
    }

    public Call<CommonResponse> uploadDocument(MultipartBody.Part documentImage, MultipartBody.Part backDocument) {
        return authenticatedApiClient.uploadDocument(documentImage, backDocument);
    }

    public Call<CommonResponse> addDebitCard(String cardToken, String name) {
        return authenticatedApiClient.addDebitCard(cardToken, name);
    }

    public Call<MerchantDetailBeans> getMerchantDetails(String userId) {
        return authenticatedApiClient.getmerchantDetails(userId);
    }

    public Call<CommonDataModel> getCommonResponseData() {
        return authenticatedApiClient.getCommonData();
    }

    public Call<CheckSocialLoginmodel> checkSocialLoginmodel(HashMap<String, String> parms) {
        return authenticatedApiClient.checkSocialLogin(parms);
    }


}

