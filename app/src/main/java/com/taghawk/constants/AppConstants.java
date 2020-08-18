package com.taghawk.constants;

import android.os.Environment;

public class AppConstants {

    public static final String USER_DETAILS = "user_details";
    public static final String TOKEN = "user_id";
    public static final Integer SUCCESS_CODE = 200;
    public static final String TAG_CREATE_AMOUNT = "300";
    public static final int LIST_VIEW_TYPE = 1;
    public static final int LOADER_VIEW_TYPE = 0;
    public static final String BUNDLE_DATA = "bundle_data";
    public static final String IS_SIGN_UP = "isSignUp";
    public static final String IS_LOGIN = "isLogin";
    public static final int MEDIA_ON_CLICK = 1;
    public static final int GALLERY_REQ_CODE = 988;
    public static final int CAMERA_PERMISSION = 989;
    public static final int TYPE_IMAGE = 1;
    public static final int SELECTED_VIDEOS = 2;
    public static final String CROP_IMAGES = "crop_images";
    public static final String PROFILE_INFO = "PROFILE_INFO";
    public static final int COUNTRY_CODE = 2977;
    public static final int GUEST_USER = 1;
    public static final int NORMAL_USER = 2;
    public static final String CURRENCY_USD = "USD";
    public static final String FEDEX = "FEDEX";
    public static final String USPS = "USPS";
    public static final String NOTIFICATION_TYPE = "push_notification";
    public static final int DOCUMENT_UPLOAD = 112;
    public static final String IS_FROM_CASH_OUT = "IS_FROM_CASH_OUT";
    public static final String BROAD_CAST_PAYMENT_ACTION = "broad_cast_payment_action";
    public static final String BROAD_CAST_TAG_JOINED_ACTION = "broad_cast_tag_joined_action";
    public static final String PAYMENT = "ORDER_STATUS";
    public static final String TAG_JOINED_ACCEPTED = "TAG_JOIN_ACCEPTED";


    //    public static final String STRIPE_KEY = "pk_test_blkWlguds94EeKloWW9S4PUf";
    //    client stripe account
//    public static final String STRIPE_KEY = "pk_test_FOXoFBKrNB90C9zlCQU1aAQF";
    //    Live Stripe Key
    public static final String STRIPE_KEY = "pk_live_Kax3WilXTomLpwkQB6mYwe26";

    public static String APP_IMAGE_FOLDER = Environment.getExternalStorageDirectory().toString() + "/TagHawk";

    // Api Key Constant
    public static interface KEY_CONSTENT {
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String USER_TYPE = "userType";
        public static final String DEVICE_ID = "deviceId";
        public static final String DEVICETOKEN = "deviceToken";
        public static final String PLATEFORM = "platform";
        public static final String OS_VERSION = "os_version";
        public static final String DEVICE_MENUFECTURER = "device_manufacturer";
        public static final String DEVICE_MODEL = "device_model";
        public static final String APP_VERSION = "app_version";
        public static final String ACCESS_TOKEN = "accessToken";
        public static final String FULL_NAME = "fullName";
        public static final String INVITATION_CODE = "invitationCode";
        public static final String REFRESH_TOKEN = "refreshToken";
        public static final String PAGE_NO = "pageNo";
        public static final String LIMIT = "limit";
        public static final String SEARCH_KEY = "searchKey";
        public static final String CATEGORY_ID = "productCategoryId";
        public static final String LAT = "lat";
        public static final String LONGI = "long";
        public static final String SELLER_RATING = "sellerRating";
        public static final String CONDITION = "condition";
        public static final String PRICE_FROM = "priceFrom";
        public static final String PRICE_TO = "priceTo";
        public static final String SELLER_VERIFIED = "sellerVerified";
        public static final String PRODUCT_ID = "productId";
        public static final String SHIP_STATUS = "shipStatus";
        public static final String SHIP_TO = "ship_to";
        public static final String PRODUCTS = "products";
        public static final String STATUS = "status";
        public static final String REASON = "reason";
        public static final String SORD_BY = "sortBy";
        public static final String SORT_ORDER = "sortOrder";
        public static final String POSTED_WITH_IN = "postedWithIn";
        public static final String TAG_TYPE = "tagType";
        public static final String DISTANCE = "distance";
        public static final String NAME = "name";
        public static final String TAG_ID = "communityId";
        public static final String TAGID = "tagId";
        public static final String TITLE = "title";
        public static final String IMAGES = "images";
        public static final String PRODUCT_CATEGORY_ID = "productCategoryId";
        public static final String FIRM_PRICE = "firmPrice";
        public static final String IS_NEGOTIABLE = "isNegotiable";
        public static final String DESCRIPTION = "description";
        public static final String SHIPPING_AVAILIBILITY = "shippingAvailibility";
        public static final String LOCATION = "location";
        public static final String SHARED_COMMUNITIES = "sharedCommunities";
        public static final String WEIGHT = "weight";
        public static final String SHIPPING_PRICE = "shippingPrice";
        public static final String SHIPPING_TYPE = "shippingType";
        public static final String MEMBER_SIZE = "memberSize";
        public static final String TAG_TYPE_NEW = "subType";
        public static final String PAYMENT_ID = "paymentId";
        public static final String DAYS = "days";
        public static final String PRICE = "price";
        public static final String CREATED_USING = "createdUsing";
        public static final String ACTION = "action";
        public static final String PROFILE_PICTURE = "profilePicture";
        public static final String FACEBOOK_ID = "facebookId";
        public static final String COUNTRY_CODE = "countryCode";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String DOCUMENTS = "documents";
        public static final String OTP = "otp";
        public static final String SELLER_ID = "sellerId";
        public static final String COMMUNITY_ID = "communityId";
        public static final String USER_ID = "userId";
        public static final String TYPE = "type";
        public static final String JOIN_FROM = "join_from";
        public static final String RATING_ID = "ratingId";
        public static final String RATING = "rating";
        public static final String REPLY_COMMENT = "replyComment";
        public static final String COMMENT = "comment";
        public static final String SOURCE = "source";
        public static final String CURRENCY = "currency";
        public static final String AMOUNT = "amount";
        String OTHER_USER_ID = "otherUserId";
        public static final String IS_TRANSACTION_COST = "isTransactionCost";
        public static final String DOB = "dob";
        public static final String SSN = "ssnNumber";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String IP = "ip";
        public static final String ACCOUNT_NUMBER = "account_number";
        public static final String ACCOUNT_HOLDER = "account_holder_name";
        public static final String ROUTING_NUMBER = "routing_number";
        String MEMBER_NAME = "memberName";
        String ACTION_TYPE = "actionT";
        public static final String CONTACT_NAME = "contact_name";
        public static final String SHIP_ID = "shipId";
        public static final String SELECTED_STATUS = "selectedStatus";
        public static final String CITY = "city";
        public static final String ZIP_CODE = "postal_code";
        public static final String STEET1 = "street1";
        public static final String COUNTRY = "country";
        public static final String STEET2 = "street2";
        public static final String STATE = "state";
        public static final String ADDRESS_LINE = "line1";
        public static final String ADDRESS_LINE_TWO = "line2";
        public static final String PHONE = "phone";
        public static final String RESIDENCE_TYPE = "type";
        public static final String DOCUMENT_REFERENCE_ID = "documentReferenceId";
        public static final String ORDER_ID = "orderId";
        public static final String DECLINE_MESSAGE = "declineMessage";
        public static final String STATEMENT = "statement";
        public static final String SUBMIT_RESPONSE = "submitResponse";
        public static final String PROOF = "proof";
        public static final String DRIVING_LICENSE = "drivingLicense";
        public static final String PASSPORT_NUMBER = "passportNumber";
        public static final String PASSPORT_COUNTRY = "passportCountry";
        String PRODUCT_STATUS = "productStatus";
    }

    public interface ActionConstants {

        int BACK_ACTION = 1;
    }

    //Jumio document type verification Constant
    public interface JUMIO_CONSTENT {
        public static final String API_TOKEN = "ed000985-93d5-4837-91c8-3a266c2c83b5";
        public static final String API_SECERT_KEY = "yIMeTuvMkeNCUCffNeT2sf904Gr2v6qT";
    }

    //Upload Image Type Constant
    public interface UPLOAD_IMAGE_TYPE {
        int PROFILE_PIC = 1;
        int DOCUMENT = 2;
    }

    // Tag Contants
    public static interface TAG_KEY_CONSTENT {
        String NAME = "name";
        String EMAIL = "email";
        String PASSWORD = "password";
        String IMAGE_URL = "imageUrl";
        String URL = "url";
        String THUMB_URL = "thumbUrl";

        String JOIN_TAG_BY = "joinTagBy";
        String TYPE = "type";
        String DOCUMENT_TYPE = "document_type";
        String POINTS_CHARGED = "pointsCharged";
        String DESCRIPTION = "description";
        String ADDRESS = "address";
        String CITY = "city";
        String LAT = "lat";
        String LONG = "long";

        String TAG_REFER_INFO = "TAG_REFER_INFO ";
        String COMMUNITY_ID = "communityId";

        String REQUEST_PARAMETER = "requestParameter";
        String DOCUMENT_URL = "documentUrl";
        String JUMIO_SCAN_REFERNCE = "jumioIdScanReference";
        String REASON = "reason";
        String ANNOUNCEMENT = "announcement";
    }

    public static interface REQUEST_PARAMS {
        String PROFILE_PICTURE = "profilePicture";
        String FULL_NAME = "fullName";
        String PHONE_NUMBER = "phoneNumber";
        String OFFICIAL_ID = "officialId";
    }

    // Tag Verification Contan
    public static interface TAG_VERIFICATION_METHOD {
        int EMAIL = 1;
        int PASSWORD = 2;
        int DOCUMENT = 3;
    }

    public static interface SSN_VERIFICATION_STATUS {
        String PENDING = "pending";
        String UNVERIFIED = "unverified";
        String VERIFIED = "verified";
    }


    // Profile Product type Contants
    public static interface PROFILE_PRODUCTS_TYPE {
        int SELLING = 1;
        int SOLD = 2;
        int FAVOURITE = 3;
    }


    // UI Validation Constant
    public static class UIVALIDATIONS {
        public static final int LAST_NAME_EMPTY = 99;
        public static final int LAST_INVALID_NAME = 98;
        public static final int EMAIL_EMPTY = 101;
        public static final int PASSWORD_EMPTY = 102;
        public static final int INVALID_PASSWORD = 104;
        public static final int INVALID_EMAIL = 103;
        public static final int NAME_EMPTY = 105;
        public static final int NEW_PASSWORD_EMPTY = 106;
        public static final int CONFIRM_PASSWORD_EMPTY = 107;
        public static final int PASSWORD_NOT_MATCHED = 108;
        public static final int INVALID_NAME = 109;
        public static final int TO_PRICE = 110;
        public static final int FROM_PRICE = 111;
        public static final int FROM_GREATER_TO = 112;
        public static final int FACEBOOK_EMAIL_REQUIRED = 411;
        public static final int LOCATION_SELECT_LOCATION = 113;
        public static final int OLD_PASSWORD_EMPTY = 114;
        public static final int COMMEN_ERROR = 115;
        public static final int FIRST_NAME_EMPTY = 116;
        public static final int LINK_EXPIRED = 400;


    }

    // Preference Contants

    public static class PreferenceConstants {

        public static final String REFRESH_TOKEN = "refresh_token";
        public static final String ACCESS_TOKEN = "access_token";
        public static final String LOGIN_TYPE = "loginType";
        public static final String IS_PASSPORT = "IS_PASSPORT";
        public static final String IS_PHONE_VERIFIED = "isVerified";
        public static final String PHONE_NUMBER = "PHONE_NUMBER";
        public static final String USER_NAME = "user_name";
        public static final String USER_DETAILS = "user_detail";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String DEVICE_ID = "device_id";
        public static final String FILTER_LOCATION = "filter_location";
        public static final String FILTER_LATITUDE = "filter_latitude";
        public static final String FILTER_LONGITUDE = "filter_longitude";
        public static final String ACCOUNT_NUMBER = "account_number";
        public static final String ACCOUNT_HOLDER = "account_holder_name";
        public static final String ROUTING_NUMBER = "routing_number";
        public static final String IS_MUTE = "IS_MUTE";
        public static final String MERCHENT_ID = "merchentId";
        public static final String SSN_NUMBER = "ssnNumber";
        public static final String DOB = "dob";
        public static final String BALANCE = "balance";
        public static final String SORT_BY = "sortBy";
        public static final String SORT_ORDER = "sortOrder";
        public static final String VENDOR_ID = "vendorId";
        public static final String TAG_PRODUCT_ID = "tagProductId";
    }

    //Login Type Actions
    public class LoginTypes {

        public static final int CUSTOMER = 2;
        public static final int HAWK_DRIVER = 3;
        public static final int MOVING_COMPANY = 4;
    }

    //UPDATE TYPE
    public class UpdateType {

        public static final String FORCE = "FORCE";
        public static final String NORMAL = "NORMAL";
        public static final String SKIP = "SKIP";
    }

    //Flow unflow contants
    public class UNFOLLOW_REMOVE_ACTION {

        public static final int UNFOLLOW = 1;
        public static final int REMOVE = 2;
        public static final int BLOCK = 3;
        public static final int UN_BLOCK = 4;
    }

    //Deeplink Notification Contants

    public static interface DEEP_INK_CONSTENT {
        public static final String RESET_PASSWORD = "ResetPassword";
        public static final String LINK_ID = "token";
        public static final String PRODUCT_ID = "productId";
        public static final String USER_ID = "user_id";

        public static final String TYPE = "type";
        public static final int PRODUCT_SHARE = 3;
        public static final int TAG_SHARE = 4;
    }

    // Camera Actions Ccntants
    public static interface CAMERA_CONSTANTS {
        public static final String IMAGE_LIMIT_ONESHOT = "IMAGE_LIMIT_ONESHOT";
    }

    // Activity Contants
    public static interface ACTIVITY_RESULT {
        public static final int SEACH_LOACTION = 1001;
        public static final int GPS_ENABLE = 202;
        public static final int LOCATION_PERMISSION = 203;
        public static final int FILTER = 204;
        public static final int TAG_FILTER = 205;
        public static final int CLEAR_CATEGORY = 206;
        public static final int CAMERA_PERMISSION = 207;
        public static final int EDIT_PRODUCT = 208;
        public static final int PRODUCT_DETAILS = 209;

        public static final int GALLERY_IMAGES = 210;
        public static final int EDIT_TAG = 211;
        public static final int FOLLOW = 124;
        public static final int FOLLOWFOLLOWING = 212;
        public static final int G_PAY_STRIPE = 213;
        public static final int PRODUCT_SOLD = 214;
        public static final int TAG_CREATED = 215;
        public static final int SHIPPING_PAYMENT = 216;
        int SHARE_PRODUCT = 217;
        int VIEW_PRODUCT = 218;
        public static final int SINGLE_CHAT_USER_BLOCK = 217;
        public static final int TAG_DETAILS = 218;
        public static final int CHAT_SHELF_DETAILS = 219;
        public static final int ADD_UPDATE_ADDRESS = 220;
        public static final int BLUE_SNAP_PAY = 221;
        public static final int ADD_BILLING_ADDRESS = 222;

    }

    // Payment Flow Constant (Wallet Journey and Holding Payment)
    public static interface PAYMENT_REFUND_STATUS {
        public static final String PENDING = "PENDING";
        public static final String ITEM_DELEVER = "ITEM_DELEVER";
        public static final String COMPLETED = "COMPLETED";
        public static final String REQUEST_FOR_REFUND = "REQUEST_FOR_REFUND";
        public static final String REFUND_ACCEPTED = "REFUND_ACCEPTED";
        public static final String REFUND_SUCCESS = "REFUND_SUCCESS";
        public static final String DISPUTE_STARTED = "DISPUTE_STARTED";
        public static final String DISPUTE_RESPONSE = "DISPUTE_RESPONSE";
        public static final String DISPUTE_COMPLETED = "DISPUTE_COMPLETED";
        public static final String DECLINED = "DECLINED";
        public static final String SELLER_STATEMENT_DONE = "SELLER_STATEMENT_DONE";
        public static final String SELLER_STATEMENT_RESPONSE = "SELLER_STATEMENT_RESPONSE";
        public static final String REFUND_DISPUTE_CAN_START = "REFUND_DISPUTE_CAN_START";
        String DISPUTE_CAN_START = "DISPUTE_CAN_START";

    }

    // Network Request Code
    public static interface REQUEST_CODE {
        public static final int LIKE_PRODUCT = 100;
        public static final int LIKE_SIMILAR_PRODUCT = 101;
        public static final int REPORT_PRODUCT = 102;
        public static final int SHARE_PRODUCT = 103;
        public static final int TAG_LISTING = 104;
        public static final int TAG_DEtAILS = 105;
        public static final int CAMERA_ACTIVITY = 106;
        public static final int TAG_SEACHING_RESULT = 107;
        public final int MULTIPLE_IMAGE_INTENT = 2456;
        public static final int ADD_TO_CART = 108;
        public static final int DELETE_PRODUCT = 109;
        public static final int DELETE_PRODUCT_CART = 110;
        public static final int DELETE_ALL_PRODUCT_CART = 111;
        public static final int SHIPPING_TYPE_IN_CART = 112;
        public static final int FACEBOOK_VERIFY = 113;
        public static final int MOBILE_VERIFY = 114;
        public static final int EMAIL_VERIFY = 115;
        public static final int DOCUMENT_VERIFY = 116;
        public static final int PROFILE_UPDATE = 117;
        public static final int PROFILE_PICTURE = 118;
        public static final int PROFILE_NAME = 119;
        public static final int OTP_VERIFY = 120;
        public static final int NOTIFICATION_LIST = 121;
        public static final int MARK_NOTIFICATION_READ = 122;
        public static final int PROFILE = 123;
        public static final int FOLLOW = 124;
        public static final int FOLLOW_FOLLOWING = 124;
        public static final int UNFOLLOW = 125;
        public static final int REMOVE_FRIEND = 126;
        public static final int REVIEW_RATING_LIST = 127;
        public static final int REVIEW_EDIT = 128;
        public static final int REVIEW_REPLY = 129;
        public static final int SUBMIT_FEEDBACK = 130;
        public static final int DENY_RATING = 131;
        public static final int PAYMENT = 132;
        public static final int BLOCK = 133;
        public static final int ADD_DOB = 134;
        public static final int ADD_SSN = 135;
        public static final int ADD_TO_CART_SINGLE_PRODUCT = 136;
        public static final int ACCEPT_REJECT_TAG_REQUEST = 137;
        public static final int UNBLOCK = 138;
        public static final int ZERO_PAYMENT = 139;
        public static final int REFUND_REQUEST = 140;
        public static final int CONFIRM_ITEM_RECEIVED = 141;
        public static final int REFUND_REQUEST_ACCEPT = 142;
        public static final int CONFIRM_ITEM_RECEIVED_SELLER = 143;
        public static final int REFUND_REQUEST_DECLINE = 144;
        public static final int OPEN_A_DISPUTE = 145;
        public static final int REFUND_DECLINE_DISPUTE = 146;
        public static final int REFUND_DECLINE_DISPUTE_RESPONSE = 147;
        public static final int OPEN_DISPUTE_RESPONSE = 148;
        public static final int REFUND_RELEASE = 149;
        public static final int CANCEL_OPEN_DISPUTE = 150;
        public static final int CANCEL_REFUND_DISPUTE = 151;
        public static final int CANCEL_REFUND_REQUEST = 152;
        public static final int CANCEL_REFUND_ACCEPT_DISPUTE = 153;
        public static final int ADD_DRIVING_PASSPORT = 153;
//        public static final int ZERO_PAYMENT = 139;
//        public static final int ZERO_PAYMENT = 139;
    }


    // Notification Constant
    public static interface NOTIFICATION_ACTION {
        public static final String PRODUCT_LIKE = "PRODUCT_LIKE";
        public static final String PRODUCT_UNLIKE = "PRODUCT_UNLIKE";
        public static final String FOLLOWED = "FOLLOWED";
        public static final String TAG_JOINED = "TAG_JOINED";
        public static final String TAG_JOINED_ACCEPTED = "TAG_JOIN_ACCEPTED";
        public static final String PRODUCT_ADDED = "PRODUCT_ADDED";
        public static final String INVITE_CODE_USED = "INVITE_CODE_USED";
        public static final String TAG_UPDATED = "TAG_UPDATED";
        public static final String MYSELF_REMOVE_FROM_GROUP = "MYSELF_REMOVE_FROM_GROUP";
        public static final String MYSELF_MADE_ADMIN = "MYSELF_MADE_ADMIN";
        public static final String NOTIFICATION_ID = "notificationId";
        public static final String ENTITY_ID = "entityId";
        public static final String PRODUCT_ADDED_IN_CART = "PRODUCT_ADDED_IN_CART";
        public static final String ANNOUNCEMENT = "ANNOUNCEMENT";
        public static final String TAG_DETAILS = "4";
        public static final String PRODUCT_DETAILS = "5";
        public static final String PRODUCT_DETAILS_LINK = "3";
        public static final String VERIFY_EMAIL = "VerifyEmail";
        public static final String JUMIO_APPROVAL = "JUMIO_APPROVAL";
        public static final String TAG_REQUEST = "TAG_REQUEST";
        public static final String PRODUCT_SOLD = "PRODUCT_SOLD";
        public static final String STRIPE_UPDATE = "STRIPE_UPDATE";

    }

    // AMazon S3 Constants
    public static interface AMAZON_S3 {
        //Testing
//        public static final String AMAZON_POOLID = "us-east-1:b1f250f2-66a7-4d07-96e9-01817149a439";
        //Live
        public static final String AMAZON_POOLID = "us-east-1:2761e492-b863-41a7-a8eb-373a1d73375b";
        //testing
//        public static final String BUCKET = "appinventiv-development";
        //Live
        public static final String BUCKET = "taghawk";
        //testing
//        public static final String AMAZON_SERVER_URL = "https://appinventiv-development.s3.amazonaws.com/";
        //Live
        public static final String AMAZON_SERVER_URL = "https://taghawk.s3.amazonaws.com/";
        public static final String END_POINT = "s3.amazonaws.com";
    }

    // Firebase Actions constent
    public interface FIREBASE {
        String FIREBASE_USER_NODE = "users";
        String FIREBASE_ROOMS_NODE = "roomGroupIds";
        String FIREBASE_OTHER_USER_ID = "otherUserId";
        String FIREBASE_MESSAGES_NODE = "messages";
        String FIREBASE_TAGS_DETAIL_NODE = "tagsDetail";
        String FIREBASE_SINGLE_CHAT = "single";
        String FIREBASE_GROUP_CHAT = "group";
        String FIREBASE_MESSAGE_STATUS_DELIVERED = "delivered";
        String FIREBASE_MESSAGE_STATUS_READ = "read";
        String FIREBASE_MESSAGE_STATUS_PENDING = "pending";
        String FIREBASE_MESSAGE_TYPE_IMAGE = "image";
        String FIREBASE_MESSAGE_TYPE_TEXT = "text";
        String FIREBASE_MESSAGE_TYPE_SHARE_PRODUCT = "shareProduct";
        String FIREBASE_MESSAGE_TYPE_SHARE_COMMUNITY = "shareCommunity";
        String FIREBASE_MESSAGE_TYPE_PRODUCT_CHANGE_HEADER = "productChangeHeader";
        String FIREBASE_MESSAGE_TYPE_TAG_CREATED_HEADER = "tagCreatedHeader";
        String FIREBASE_MESSAGE_TYPE_USER_JOIN_HEADER = "userJoin";
        String FIREBASE_MESSAGE_TYPE_USER_REMOVED_HEADER = "userRemove";
        String FIREBASE_MESSAGE_TYPE_USER_LEFT_HEADER = "userLeft";
        String FIREBASE_MESSAGE_TYPE_TIME_HEADER = "timeHeader";
        String FIREBASE_MESSAGE_TYPE_DATE_HEADER = "dateHeader";
        String FIREBASE_MESSAGE_TYPE_OWNERSHIP_TRANSFER_HEADER = "ownershipTransfer";
        String FIREBASE_CHAT_DATA = "chatData";
        String TIMESTAMP = "timeStamp";
        String FIREBASE_KEY_CREATED_TIMESTAMP = "createdTimeStamp";
        String FIREBASE_KEY_PRODUCT_INFO = "productInfo";
        String FIREBASE_KEY_ROOM_NAME = "roomName";
        String FIREBASE_KEY_ROOM_IMAGE = "roomImage";
        String FIREBASE_KEY_MEMBERS = "members";
        String FIREBASE_KEY_MUTE = "mute";
        String FIREBASE_KEY_PINNED = "pinned";
        String FIREBASE_KEY_LAST_MESSAGE = "lastMessage";
        String FIREBASE_KEY_OWNER_ID = "ownerId";
        String FIREBASE_KEY_MEMBER_TYPE = "memberType";
        String FIREBASE_KEY_TAG_NAME = "tagName";
        String FIREBASE_KEY_TAG_IMAGE_URL = "tagImageUrl";
        String FIREBASE_KEY_TAG_LATITUDE = "tagLatitude";
        String FIREBASE_KEY_TAG_LONGITUDE = "tagLongitude";
        String FIREBASE_KEY_TAG_ADDRESS = "tagAddress";
        String FIREBASE_KEY_VERIFICATION_DATA = "verificationData";
        String FIREBASE_KEY_VERIFICATION_TYPE = "verificationType";
        String FIREBASE_KEY_TAG_TYPE = "tagType";
        String FIREBASE_KEY_CHAT_MUTE = "chatMute";
        String FIREBASE_KEY_READ_COUNT = "readCount";
        String FIREBASE_KEY_MESSAGE_STATUS = "messageStatus";
        String FIREBASE_KEY_UNREAD_MESSAGE_COUNT = "unreadMessageCount";
        String FIREBASE_KEY_PENDING_REQUEST_COUNT = "pendingRequestCount";
        String FIREBASE_MESSAGE_TYPE_SHELF_PRODUCT = "shelfProduct";


        String FIREBASE_MESSAGE_TYPE_ACTION_RESERVE_ITEM = "actionReserveItem";
        String FIREBASE_MESSAGE_TYPE_ACTION_RELEASE_PAYMENT = "actionReleasePayment";
        String FIREBASE_MESSAGE_TYPE_ACTION_REFUND = "actionRefund";
        String FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_DECLINE = "actionRefundSellerDecline";
        String FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_ACCEPT = "actionRefundSellerAccept";
        String FIREBASE_MESSAGE_TYPE_ACTION_REFUND_SELLER_RELEASE = "actionRefundSellerRelease";
        String FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE = "actionSellerOpenDispute";
        String FIREBASE_MESSAGE_TYPE_ACTION_SELLER_OPEN_DISPUTE_RESPONSE = "actionSellerOpenDisputeResponse";
        String FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE = "actionRefundDispute";
        String FIREBASE_MESSAGE_TYPE_ACTION_REFUND_DISPUTE_RESPONSE = "actionRefundDisputeResponse";
        String FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE = "actionRefundAcceptDispute";
        String FIREBASE_MESSAGE_TYPE_ACTION_REFUND_ACCEPT_DISPUTE_RESPONSE = "actionRefundAcceptDisputeResponse";
        String FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_OPEN_DISPUTE = "actionCancelOpenDispute";
        String FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_REQUEST = "actionCancelRefundRequest";
        String FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_DISPUTE = "actionCancelRefundDispute";
        String FIREBASE_MESSAGE_TYPE_ACTION_CANCEL_REFUND_ACCEPT_DISPUTE = "actionCancelRefundAcceptDispute";


        int FIREBASE_MEMBER_TYPE_SUPER_ADMIN = 0;
        int FIREBASE_MEMBER_TYPE_OWNER = 1;
        int FIREBASE_MEMBER_TYPE_ADMIN = 2;
        int FIREBASE_MEMBER_TYPE_MEMBER = 3;
        String PUSH_TYPE_MESSAGE = "GET_MESSAGE";
        String NOTIFICATION = "notification";
        String NOTIFICATION_CHANNEL_GROUP = "notification_channel_group";
        //        String FIREBASE_SERVER_KEY = "key=" + "AIzaSyBnlUDGLM8jin1L4wXSfJC8Fzk9sGUTQXw";
        String FIREBASE_SERVER_KEY = "key=" + "AAAAZpnw4Bk:APA91bHfgm6-TFqNdKmZ6agBci5hbCMLmg-oj6m7EVCVWsP3FIcxK5imcquFuP3EDsb3F0ubOcyymQEYYsL0PFLSZfq4m8WzpcB5HQH5HH-6E7vgTyJU8z7q0QDHjt7cR3FgoQ85r_20";

    }
}
