package com.taghawk.data;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.gson.Gson;
import com.taghawk.TagHawkApplication;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.api.ApiManager;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.firebase.FirebaseManager;
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
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.chat.DeleteTagRequest;
import com.taghawk.model.chat.MemberModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.follow_following.FollowFollowingBean;
import com.taghawk.model.gift.GiftRewardsPromotionModel;
import com.taghawk.model.home.DeleteProductRequest;
import com.taghawk.model.home.LikeUnLike;
import com.taghawk.model.home.ProductDetailsModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.model.login.CheckSocialLoginmodel;
import com.taghawk.model.login.LoginFirebaseModel;
import com.taghawk.model.login.LoginModel;
import com.taghawk.model.pendingRequests.PendingRequestResponse;
import com.taghawk.model.profileresponse.BalanceResponse;
import com.taghawk.model.profileresponse.ProfileProductsResponse;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.model.request.ChangePassword;
import com.taghawk.model.request.Reset;
import com.taghawk.model.request.User;
import com.taghawk.model.request.UserResponse;
import com.taghawk.model.review_rating.ReviewRatingModel;
import com.taghawk.model.strip.CreateMercentResponse;
import com.taghawk.model.strip.FedexRateResponse;
import com.taghawk.model.strip.GetBankDetail;
import com.taghawk.model.tag.MyTagResponse;
import com.taghawk.model.tag.TagData;
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
import retrofit2.http.QueryMap;

public class DataManager {

    private static DataManager instance;
    private ApiManager apiManager;
    private PreferenceManager mPrefManager;
    private FirebaseManager firebaseManager;


    private DataManager(Context context) {
        //Initializing SharedPreference object
        mPrefManager = PreferenceManager.getInstance(context);
        firebaseManager = FirebaseManager.getInstance();
    }

    /**
     * Returns the single instance of {@link DataManager} if
     * {@link #init(Context)} is called first
     *
     * @return instance
     */
    public static DataManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Call init() before getInstance()");
        }
        return instance;
    }

    /**
     * Method used to create an instance of {@link DataManager}
     *
     * @param context of the application passed from the {@link TagHawkApplication}
     * @return instance if it is null
     */
    public synchronized static DataManager init(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    /**
     * Method to initialize {@link ApiManager} class
     */
    public void initApiManager() {
        apiManager = ApiManager.getInstance();
    }


    public Call<UserResponse> hitLoginApi(LoginModel user) {
        return apiManager.hitLoginApi(user);
    }

    public Call<UserResponse> hitGuestLoginApi(HashMap<String, String> parms) {
        return apiManager.hitGuestLoginApi(parms);
    }

    public String getRefreshToken() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.REFRESH_TOKEN);
    }

    public String getUserName() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.USER_NAME);
    }

    public String getSortBy() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.SORT_BY);
    }

    public String getSortorder() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.SORT_ORDER);
    }

    public void saveSortBy(String sortBy) {
        mPrefManager.putString(AppConstants.PreferenceConstants.SORT_BY, sortBy);
    }

    public void saveSortOrder(String sortOrder) {
        mPrefManager.putString(AppConstants.PreferenceConstants.SORT_ORDER, sortOrder);
    }

    public void saveAccessToken(String accessToken) {
        mPrefManager.putString(AppConstants.PreferenceConstants.ACCESS_TOKEN, accessToken);
    }

    public void saveAccountNumber(String accountNumber) {
        mPrefManager.putString(AppConstants.PreferenceConstants.ACCOUNT_NUMBER, accountNumber);
    }

    public void saveAccountHolderName(String accountHolderName) {
        mPrefManager.putString(AppConstants.PreferenceConstants.ACCOUNT_HOLDER, accountHolderName);
    }

    public void saveIsMuteStatus(boolean isMute) {
        mPrefManager.putBoolean(AppConstants.PreferenceConstants.IS_MUTE, isMute);
    }

    public boolean getIsMuteStatus() {
        return mPrefManager.getBoolean(AppConstants.PreferenceConstants.IS_MUTE);
    }


    public void saveRoutingNumber(String routingNumber) {
        mPrefManager.putString(AppConstants.PreferenceConstants.ROUTING_NUMBER, routingNumber);
    }

    public String getAccountNumber() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.ACCOUNT_NUMBER);
    }

    public String getAccountHolderName() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.ACCOUNT_HOLDER);
    }

    public String getRoutingNumber() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.ROUTING_NUMBER);
    }

    public String getMerchentId() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.MERCHENT_ID);
    }

    public void saveMerchentId(String merchentId) {
        mPrefManager.putString(AppConstants.PreferenceConstants.MERCHENT_ID, merchentId);
    }

    public String getVendorId() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.MERCHENT_ID);
    }

    public void saveVendorId(String merchentId) {
        mPrefManager.putString(AppConstants.PreferenceConstants.MERCHENT_ID, merchentId);
    }

    public void saveRefreshToken(String refreshToken) {
        mPrefManager.putString(AppConstants.PreferenceConstants.REFRESH_TOKEN, refreshToken);
    }

    public void saveDeviceToken(String deviceToken) {
        mPrefManager.putString(AppConstants.PreferenceConstants.DEVICE_TOKEN, deviceToken);
    }

    public void saveDeviceId(String deviceToken) {
        mPrefManager.putString(AppConstants.PreferenceConstants.DEVICE_ID, deviceToken);
    }

    public void saveLocation(String location) {
        mPrefManager.putString(AppConstants.PreferenceConstants.FILTER_LOCATION, location);
    }

    public String getFilterLocation() {
        if (mPrefManager.getString(AppConstants.PreferenceConstants.FILTER_LOCATION) != null)
            return mPrefManager.getString(AppConstants.PreferenceConstants.FILTER_LOCATION);
        else
            return "";
    }

    public void saveFilterLatitude(String latitude) {
        mPrefManager.putString(AppConstants.PreferenceConstants.FILTER_LATITUDE, latitude);
    }


    public String getCashOutBalance() {
        if (mPrefManager.getString(AppConstants.PreferenceConstants.BALANCE) != null)
            return mPrefManager.getString(AppConstants.PreferenceConstants.BALANCE);
        else
            return "";
    }

    public void saveCashOutBalance(String cashOutBalance) {
        mPrefManager.putString(AppConstants.PreferenceConstants.BALANCE, cashOutBalance);
    }

    public void saveSSNNumber(String ssnNumber) {
        mPrefManager.putString(AppConstants.PreferenceConstants.SSN_NUMBER, ssnNumber);
    }

    public void saveDob(String dob) {
        mPrefManager.putString(AppConstants.PreferenceConstants.DOB, dob);
    }

    public String getSSNnumber() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.SSN_NUMBER);
    }

    public String getDob() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.DOB);
    }

    public String getFilterLatitude() {
        if (mPrefManager.getString(AppConstants.PreferenceConstants.FILTER_LATITUDE) != null)
            return mPrefManager.getString(AppConstants.PreferenceConstants.FILTER_LATITUDE);
        else
            return "";
    }

    public void saveAddressLineOne(String address) {
        mPrefManager.putString(AppConstants.KEY_CONSTENT.ADDRESS_LINE, address);
    }

    public void saveAddressLineTwo(String address) {
        mPrefManager.putString(AppConstants.KEY_CONSTENT.ADDRESS_LINE_TWO, address);
    }

    public void saveAddressCity(String city) {
        mPrefManager.putString(AppConstants.KEY_CONSTENT.CITY, city);
    }

    public void saveAddressstate(String state) {
        mPrefManager.putString(AppConstants.KEY_CONSTENT.STATE, state);
    }

    public void saveAddressPostalCode(String postalCode) {
        mPrefManager.putString(AppConstants.KEY_CONSTENT.ZIP_CODE, postalCode);
    }

    public String getAddressLineOne() {
        if (mPrefManager.getString(AppConstants.KEY_CONSTENT.ADDRESS_LINE) != null)
            return mPrefManager.getString(AppConstants.KEY_CONSTENT.ADDRESS_LINE);
        else
            return "";
    }

    public String getAddressLineTwo() {
        if (mPrefManager.getString(AppConstants.KEY_CONSTENT.ADDRESS_LINE_TWO) != null)
            return mPrefManager.getString(AppConstants.KEY_CONSTENT.ADDRESS_LINE_TWO);
        else
            return "";
    }

    public String getAddressCity() {
        if (mPrefManager.getString(AppConstants.KEY_CONSTENT.CITY) != null)
            return mPrefManager.getString(AppConstants.KEY_CONSTENT.CITY);
        else
            return "";
    }

    public String getAddressState() {
        if (mPrefManager.getString(AppConstants.KEY_CONSTENT.STATE) != null)
            return mPrefManager.getString(AppConstants.KEY_CONSTENT.STATE);
        else
            return "";
    }

    public String getAddressPostalCode() {
        if (mPrefManager.getString(AppConstants.KEY_CONSTENT.ZIP_CODE) != null)
            return mPrefManager.getString(AppConstants.KEY_CONSTENT.ZIP_CODE);
        else
            return "";
    }


    public void saveFilterLongitude(String longitude) {
        mPrefManager.putString(AppConstants.PreferenceConstants.FILTER_LONGITUDE, longitude);
    }

    public String getFilterLongitude() {
        if (mPrefManager.getString(AppConstants.PreferenceConstants.FILTER_LONGITUDE) != null)
            return mPrefManager.getString(AppConstants.PreferenceConstants.FILTER_LONGITUDE);
        else
            return "";
    }

    public String getDeviceToken() {
        if (mPrefManager.getString(AppConstants.PreferenceConstants.DEVICE_TOKEN) != null)
            return mPrefManager.getString(AppConstants.PreferenceConstants.DEVICE_TOKEN);
        else
            return "";
    }


    public String getDeviceId() {
        if (mPrefManager.getString(AppConstants.PreferenceConstants.DEVICE_ID) != null)
            return mPrefManager.getString(AppConstants.PreferenceConstants.DEVICE_ID);
        else
            return "";
    }


    public String getAccessToken() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.ACCESS_TOKEN);
    }

    public Call<UserResponse> hitSignUpApi(HashMap<String, String> user) {
        return apiManager.hitSignUpApi(user);
    }

    public void saveLoginType(int loginType) {
        mPrefManager.putInt(AppConstants.PreferenceConstants.LOGIN_TYPE, loginType);
    }

    public void saveIsPassport(boolean isPassport) {
        mPrefManager.putBoolean(AppConstants.PreferenceConstants.IS_PASSPORT, isPassport);
    }

    public boolean getIsPassport() {
        return mPrefManager.getBoolean(AppConstants.PreferenceConstants.IS_PASSPORT);
    }

    public void savePhoneVerified(boolean isVerified) {
        mPrefManager.putBoolean(AppConstants.PreferenceConstants.IS_PHONE_VERIFIED, isVerified);
    }

    public boolean isPhoneVerified() {
        return mPrefManager.getBoolean(AppConstants.PreferenceConstants.IS_PHONE_VERIFIED);
    }

    public void savePhonenNumber(String num) {
        mPrefManager.putString(AppConstants.PreferenceConstants.PHONE_NUMBER, num);
    }

    public String getPhoneNumber() {
        return mPrefManager.getString(AppConstants.PreferenceConstants.PHONE_NUMBER);
    }

    public User getUserDetails() {
        User userDetail;
        String response = mPrefManager.getString(AppConstants.PreferenceConstants.USER_DETAILS);
        userDetail = new Gson().fromJson(response, User.class);
        return userDetail;

    }


    public void saveUserDetails(User user) {
        //save user name differently
//        mPrefManager.putString(AppConstants.PreferenceConstants.USER_NAME, user.getFirstName());
        String userDetail = new Gson().toJson(user);
        mPrefManager.putString(AppConstants.PreferenceConstants.USER_DETAILS, userDetail);
    }

    public Call<CommonResponse> hitForgotPasswordApi(String email, String userType) {
        return apiManager.hitPasswordApi(email, userType);
    }

    public Call<UserResponse> hitSocialLoginApi(HashMap<String, Object> parms) {
        return apiManager.hitSocialLoginApi(parms);
    }


    public Call<CommonResponse> hitResetPasswordApi(Reset reset) {
        return apiManager.hitResetPasswordApi(reset);
    }

    public Call<CommonResponse> hitLogOutApi() {
        return apiManager.hitLogOutPassword();
    }


    public Call<CategoryResponse> getCategoryList() {
        return apiManager.hitGetCategoryList();
    }

    public void clearPreferences() {
        firebaseManager.updateDeviceToken(getUserDetails().getUserId(), "");
        mPrefManager.clearAllPrefs();
    }

//    public Call<CommonResponse> hitChangePasswordApi(User user) {
//        return apiManager.hitChangePasswordApi(user);
//    }

    public Call<ProductListingModel> getProductListing(HashMap<String, Object> parms) {
        return apiManager.getProductList(parms);
    }

    public Call<SearchModel> getSearchSuggestion(HashMap<String, Object> parms) {
        return apiManager.getSearchSuggestion(getAccessToken(), parms);
    }

    public Call<TagSearchBean> getTagSearchData(HashMap<String, Object> parms) {
        return apiManager.getTagSearch(getAccessToken(), parms);
    }

    public Call<ProductDetailsModel> getProductDetails(String id) {
        return apiManager.getProductDetail(id);
    }

    public Call<LikeUnLike> getHitLikeUnLike(HashMap<String, Object> parms) {
        return apiManager.getLikeUnLike(parms);
    }


    public Call<LikeUnLike> reportProduct(HashMap<String, Object> parms) {
        return apiManager.hitReportProduct(parms);
    }


    public Call<LikeUnLike> shareProduct(HashMap<String, Object> parms) {
        return apiManager.hitshareProduct(parms);
    }


    public Call<TagModel> getTagList(HashMap<String, Object> parms) {
        return apiManager.hitTagList(parms);
    }

    public Call<CommonResponse> hitLogOut(HashMap<String, Object> parms) {
        return apiManager.logout(parms);
    }

    public Call<TagDetailsModel> getTagDetails(HashMap<String, Object> parms) {
        return apiManager.hitTagDetails(parms);
    }

    public Call<TagDetailsModel> getTagVisited(HashMap<String, Object> parms) {
        return apiManager.getTagVisited(parms);
    }

    public Call<ProfileResponse> getProfileDetails(HashMap<String, Object> parms) {
        return apiManager.hitProfileDetails(parms);
    }

    public Call<UserSpecificTagsModel> getUserSpecificTags(HashMap<String, Object> parms) {
        return apiManager.getTagsUserSpecific(parms);
    }

    public Call<ProfileProductsResponse> getProfileProducts(HashMap<String, Object> parms) {
        return apiManager.getProfileProducts(parms);
    }

    public Call<AddProductModel> addProduct(HashMap<String, Object> parms) {
        return apiManager.addProduct(parms);
    }

    public Call<AddTagResponse> addTag(HashMap<String, Object> parms) {
        return apiManager.addTag(parms);
    }

    public Call<CommonResponse> joinTag(HashMap<String, Object> parms) {
        return apiManager.joinTag(parms);
    }

    public Call<CommonResponse> transferOwnership(HashMap<String, Object> parms) {
        return apiManager.transferOwnership(parms);
    }

    public Call<MyTagResponse> getMyTags(HashMap<String, Object> parms) {
        return apiManager.getMyTags(parms);
    }

    public Call<PendingRequestResponse> getPendingRequests(HashMap<String, Object> parms) {
        return apiManager.pendingRequests(parms);
    }

    public void blockUserOnFirebase(String userId, String otherUserId) {
        firebaseManager.blockUserOnFirebase(userId, otherUserId);
    }

    public Call<AddProductModel> editProduct(HashMap<String, Object> parms) {
        return apiManager.editProduct(parms);
    }

    public Call<CommonResponse> editProfile(HashMap<String, Object> parms) {
        return apiManager.editProfile(parms);
    }

    public Call<CommonResponse> markProductFeatured(HashMap<String, Object> parms) {
        return apiManager.markFeatured(parms);
    }

    public Call<CommonResponse> addProductCArt(HashMap<String, Object> parms) {
        return apiManager.addProductToCart(parms);
    }

    public Call<CommonResponse> deleteProduct(DeleteProductRequest request) {
        return apiManager.deleteProduct(request);
    }

    public Call<CartModel> getCartList() {
        return apiManager.getCartList();
    }

    public Call<ShopperIdResponse> getVaultedShopperId(@QueryMap() HashMap<String, Object> parms) {
        return apiManager.getVaultedShopperId(parms);
    }

    public void createFirebaseUser(LoginFirebaseModel loginFirebaseModel) {
        firebaseManager.createFirebaseUser(loginFirebaseModel);
    }

    public void updateUserNodeOnEditProfile(String userId, String profilePicture, String fullName, String email) {
        firebaseManager.updateUserNodeOnEditProfile(userId, profilePicture, fullName, email);
    }

    public Query getUserNodeQuery(String userId) {
        return firebaseManager.getUserNodeQuery(userId);
    }

    public DatabaseReference getUserChatsQuery(String userId) {
        return firebaseManager.getUserChatsQuery(userId);
    }

    public Query getUserChatsQueryForNewlyAddedInbox(String userId) {
        return firebaseManager.getUserChatsQueryForNewlyAddedInbox(userId);
    }

    public void deleteUserChat(String userId, String roomId) {
        firebaseManager.deleteUserChat(userId, roomId);
    }

    public void updateUnreadCount(String userId, String roomId) {
        firebaseManager.updateUnreadCount(userId, roomId);
    }

    public void updateMessageStatus(String messageId, String roomId) {
        firebaseManager.updateMesssageStatus(messageId, roomId);
    }

    public void sendMessageToUser(User user, boolean isNewUser, boolean isOtherUserCreated, String title, String text, ChatModel chatModel, FirebaseManager.CountUpdateListener countUpdateListener) {
        firebaseManager.sendMessageToUser(user, isNewUser, isOtherUserCreated, title, text, chatModel, countUpdateListener);
    }

    public void sendMessageToGroup(String userId, String roomId, String title, String text, MessageModel messageModel) {
        firebaseManager.sendMessageToGroup(userId, roomId, title, text, messageModel);
    }

    public void addTagOnFirebase(User user, TagData tagData) {
        firebaseManager.addTagOnFirebase(user, tagData);
    }

    public void editTagOnFirebase(TagData tagData) {
        firebaseManager.editTagOnFirebase(tagData);
    }

    public void muteUnmuteChat(boolean isMute, String userId, String roomId) {
        firebaseManager.muteUnmuteChat(isMute, userId, roomId);
    }

    public void changeTagType(boolean isPrivate, String tagId) {
        firebaseManager.changeTagType(isPrivate, tagId);
    }

    public void changeVerificationType(int type, String tagId) {
        firebaseManager.changeVerificationType(type, tagId);
    }

    public void changeVerificationData(String data, String tagId) {
        firebaseManager.changeVerifcationData(data, tagId);
    }

    public void changeTagImage(String data, String tagId) {
        firebaseManager.changeTagImage(data, tagId);
    }

    public void changeTagName(String data, String tagId) {
        firebaseManager.changeTagName(data, tagId);
    }

    public void changeAnnouncement(String data, String tagId) {
        firebaseManager.changeAnnouncement(data, tagId);
    }

    public void changeDescription(String data, String tagId) {
        firebaseManager.changeDescription(data, tagId);
    }

    public void changeTagAddress(String address, double lat, double lon, String tagId) {
        firebaseManager.changeTagAddress(address, lat, lon, tagId);
    }

    public void deleteTag(String userId, HashMap<String, MemberModel> hashMap, String tagId) {
        firebaseManager.deleteTag(userId, hashMap, tagId);
    }

    public void exitTag(String userId, String userName, String tagId) {
        firebaseManager.exitTag(userId, userName, tagId);
    }

    public void removeMember(String memberId, String memberName, String tagId) {
        firebaseManager.removeMember(memberId, memberName, tagId);
    }

    public void muteUnmuteUser(String tagId, String memberId, boolean isMute) {
        firebaseManager.muteUnmuteUser(tagId, memberId, isMute);
    }

    public void joinTag(User user, TagData tagData) {
        firebaseManager.joinTag(user, tagData);
    }

    public void updatePendingRequestCount(String tagId, boolean isDecrease) {
        firebaseManager.updatePendingRequestcount(tagId, isDecrease);
    }

    public void makeAdmin(String tagId, String memberId, int memberType) {
        firebaseManager.makeGroupAdmin(tagId, memberId, memberType);
    }

    public void transferOwnership(User user, String memberId, String memberName, String tagId) {
        firebaseManager.transferOwnership(user, memberId, memberName, tagId);
    }

    public void pinnedChat(boolean isPinned, String userId, String roomId) {
        firebaseManager.pinnedChat(isPinned, userId, roomId);
    }

    public void updateProductInfo(String userId, String otherUserId, String roomId, ChatProductModel chatProductModel) {
        firebaseManager.updateProductInfo(userId, otherUserId, roomId, chatProductModel);
    }

    public void updateGroupDataOnRoomNode(String userId, String roomId, String roomName, String roomImage) {
        firebaseManager.updateGroupDataOnRoomNode(userId, roomId, roomName, roomImage);
    }

    public Query getUserMessagesQuery(String roomId, long endIndex, long createdTimeStamp) {
        return firebaseManager.getUserMessagesQuery(roomId, endIndex, createdTimeStamp);
    }

    public Query getNewMessageQuery(String roomId, long startIndex) {
        return firebaseManager.getNewMessageQuery(roomId, startIndex);
    }

    public Query getOtherUserNodeQuery(String otherUserId, String roomId) {
        return firebaseManager.getOtherUserNodeQuery(otherUserId, roomId);
    }

    public Query getGroupNodeQuery(String roomId) {
        return firebaseManager.getGroupNodeQuery(roomId);
    }

    public Call<CommonResponse> otpVerify(HashMap<String, Object> request) {
        return apiManager.otpVerify(request);
    }

    public Call<NotificationModel> getNotification(int page, int limit) {
        return apiManager.getNotification(page, limit);
    }

    public Call<CommonResponse> markNotificationRead(String notificationRead) {
        return apiManager.markNotificationRead(notificationRead);
    }

    public Call<AddTagResponse> editTag(HashMap<String, Object> parms) {
        return apiManager.editTag(parms);
    }

    public Call<CommonResponse> reportTag(HashMap<String, Object> parms) {
        return apiManager.reportTag(parms);
    }

    public Call<CommonResponse> deleteTagApi(DeleteTagRequest deleteTagRequest) {
        return apiManager.deleteTag(deleteTagRequest);
    }

    public Call<CommonResponse> exitTagApi(HashMap<String, Object> parms) {
        return apiManager.exitTag(parms);
    }

    public Call<PaymentRefundModel> productStatusApi(String productId) {
        return apiManager.productStatus(productId);
    }

    public Call<CommonResponse> removeMember(HashMap<String, Object> parms) {
        return apiManager.removeMember(parms);
    }

    public Call<ProfileResponse> followFriend(String parms) {
        return apiManager.followFriend(parms);
    }

    public Call<FollowFollowingBean> getFollowFollowingList(HashMap<String, Object> parms) {
        return apiManager.getFollowFollowingList(parms);
    }

    public Call<ProfileResponse> removeUnfriend(HashMap<String, Object> parms) {
        return apiManager.removeUnfriend(parms);
    }

    public Call<ReviewRatingModel> getReviewRating(HashMap<String, Object> parms) {
        return apiManager.getReviewRating(parms);
    }

    public Call<CommonResponse> replyEditComment(HashMap<String, Object> parms) {
        return apiManager.replyEditComment(parms);
    }


    public Call<CommonResponse> givenFeedback(HashMap<String, Object> parms) {
        return apiManager.giveFeedback(parms);
    }

    public Call<UpdateRatingNotificationBean> updateDeviceToken(String parms) {
        return apiManager.updateDeviceToken(parms);
    }

    public Call<CommonResponse> denyFeedback(HashMap<String, Object> parms) {
        return apiManager.denyFeedback(parms);
    }

    public Call<CommonResponse> doPayment(PaymentStatusRequest paymentStatusRequest) {
        return apiManager.doPayment(paymentStatusRequest);
    }

    public Call<CommonResponse> paymentFailure(PaymentStatusFailureModel paymentStatusRequest) {
        return apiManager.paymentFailure(paymentStatusRequest);
    }

    public Call<CreateSiftOrderResponse> createSiftOrder(CreateSiftOrderRequest createSiftOrderRequest) {
        return apiManager.createSiftOrder(createSiftOrderRequest);
    }

    public Call<CommonResponse> checkSoldOutProduct(HashMap<String, Object> parms) {
        return apiManager.checkSoldOutProduct(parms);
    }

    public Call<CommonResponse> doZeroPayment(HashMap<String, Object> parms) {
        return apiManager.doZeroPayment(parms);
    }


    public Call<CreateMercentResponse> createMerchent(HashMap<String, Object> parms) {
        return apiManager.createMerchent(parms);
    }

    public Call<VendorIdResponse> getVendorId(HashMap<String, Object> parms) {
        return apiManager.getVendorId(parms);
    }

    public Call<CommonResponse> saveBankDetails(HashMap<String, Object> parms) {
        return apiManager.saveBankDetails(parms);
    }

    public Call<GetBankDetail> getBankDetails() {
        return apiManager.getBankDetails();
    }

    public Call<CommonResponse> hitChangePasswordApi(ChangePassword changePassword) {
        return apiManager.changePassword(changePassword);
    }

    public Call<BlockUserModel> getBlockUserList(HashMap<String, Object> parms) {
        return apiManager.getBlockUserList(parms);
    }

    public Call<ContentViewModel> getHtmlContent(String type) {
        return apiManager.getHtmlContent(type);
    }

    public Call<FedexRateResponse> getFexdexShippingRate(HashMap<String, Object> parms) {
        return apiManager.getFexdexShippingRate(parms);
    }

    public Call<CommonResponse> addProfileAddresses(HashMap<String, Object> parms) {
        return apiManager.addProfileAddresses(parms);
    }

    public Call<ShippingAddressesResponse> addBillingAddress(HashMap<String, Object> parms) {
        return apiManager.addBillingAddress(parms);
    }

    public Call<AddressUpdateResponse> updateShippingAddress(HashMap<String, Object> parms) {
        return apiManager.updateShippingAddress(parms);
    }

    public Call<ShippingAddressesResponse> getShippingAddressesResponse(String userId) {
        return apiManager.getShippingAddressesResponse(userId);
    }

    public Call<ShippingAddressesResponse> getBillingAddress(String userId) {
        return apiManager.getBillingAddress(userId);
    }

    public Call<CommonResponse> deleteShippingAddress(DeleteAddressRequest deleteAddressRequest) {
        return apiManager.deleteShippingAddress(deleteAddressRequest);
    }

    public Call<CommonResponse> notificationOnOff(int status) {
        return apiManager.notificationOnOff(status);
    }


    public Call<TagDetailsModel> acceptRejectTagRequestFromLink(HashMap<String, Object> parms) {
        return apiManager.acceptRejectTagRequestFromLink(parms);
    }

    public Call<CommonResponse> acceptRejectTagRequest(HashMap<String, Object> parms) {
        return apiManager.acceptRejectTagRequest(parms);
    }

    public Call<CommonResponse> verifyEmail(String token) {
        return apiManager.verifyEmail(token);
    }

    public Call<ProductListingModel> getTagProduct(HashMap<String, Object> parms) {
        return apiManager.getTagProduct(parms);
    }

    public Call<ResponseBody> hitFirebasePushApi(JSONObject jsonObject) {
        return apiManager.hitFirebasePushApi(jsonObject);
    }

    public Call<BalanceResponse> cashOutBalance(HashMap<String, Object> parms) {
        return apiManager.cashOutBalance(parms);
    }

    public void updateDeviceTokenOnFirebase(String userId, String deviceToken) {
        firebaseManager.updateDeviceToken(userId, deviceToken);
    }

    public void sendPushOnFirebase(String userId, String deviceToken) {
        firebaseManager.updateDeviceToken(userId, deviceToken);
    }

    public Call<BalanceResponse> getBalance() {
        return apiManager.getBalance();
    }

    public Call<GiftRewardsPromotionModel> getRewardsPromotions() {
        return apiManager.getRewardsPromotions();
    }


    public Call<CommonResponse> productPromotionSelection(HashMap<String, Object> parms) {
        return apiManager.productPromotionSelection(parms);
    }

    public Call<FedexRateResponse> createLable(HashMap<String, Object> parms) {
        return apiManager.createLable(parms);
    }

    public Call<PaymentHistoryModel> getPaymentHistroy() {
        return apiManager.getPaymentHistroy();
    }

    public Call<PaymentRefundModel> initiateRefund(String orderId) {
        return apiManager.initiateRefund(orderId);
    }

    public Call<PaymentRefundModel> confirmItemReceived(String orderId) {
        return apiManager.confirmItemReceived(orderId);
    }


    public Call<PaymentRefundModel> confirmReturnItemReceivedSeller(String orderId) {
        return apiManager.confirmReturnItemReceivedSeller(orderId);
    }

    public Call<PaymentRefundModel> returnRequestAccept(String orderId) {
        return apiManager.returnRequestAccept(orderId);
    }

    public Call<PaymentRefundModel> cancelDispute(String orderId, String action) {
        return apiManager.cancelDispute(orderId, action);
    }

    public Call<PaymentRefundModel> declineReturnRequest(HashMap<String, Object> parms) {
        return apiManager.declineReturnRequest(parms);
    }

    public Call<PaymentRefundModel> initiateDispute(HashMap<String, Object> parms) {
        return apiManager.initiateDispute(parms);
    }

    public Call<CommonResponse> uploadDocument(MultipartBody.Part documentImage, MultipartBody.Part backDocumentImage) {
        return apiManager.uploadDocument(documentImage, backDocumentImage);
    }

    public Call<CommonResponse> addDebitCard(String cardToken, String name) {
        return apiManager.addDebitCard(cardToken, name);
    }

    public Call<CheckSocialLoginmodel> checkSocialLogin(HashMap<String, String> parms) {
        return apiManager.checkSocialLoginmodel(parms);
    }

    public Call<MerchantDetailBeans> getMerchantDetails(String userId) {
        return apiManager.getMerchantDetails(userId);
    }

    public Call<CommonDataModel> getCommonResponseData() {
        return apiManager.getCommonResponseData();
    }

}
