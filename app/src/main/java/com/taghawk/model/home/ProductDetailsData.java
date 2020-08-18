package com.taghawk.model.home;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.taghawk.model.tag.TagData;

import java.util.ArrayList;

public class ProductDetailsData implements Parcelable {
    public static final int VIEW_TYPE_PRODUCT = 1;
    public static final int VIEW_TYPE_PRODUCT_COUNT = 2;
    public static final int VIEW_TYPE_PRODUCT_SELECT = 3;
    @SerializedName("_id")
    private String productId;
    @SerializedName("firmPrice")
    private String firmPrice;
    @SerializedName("isNegotiable")
    private Boolean isNegotiable;
    @SerializedName("isTransactionCost")
    private boolean isTransactionCost;
    @SerializedName("condition")
    private int condition;
    @SerializedName("shippingAvailibility")
    private int[] shippingType;
    @SerializedName("title")
    private String title;
    @SerializedName("images")
    private ArrayList<ImageList> imageList;
    @SerializedName("productCategoryId")
    private String productCategoryId;
    @SerializedName("description")
    private String description;
    @SerializedName("created")
    private long createdDateTime;
    @SerializedName("isLiked")
    private Boolean isLiked;
    @SerializedName("productAddress")
    private String productAddress;
    @SerializedName("productLongitude")
    private Double productLongitude;
    @SerializedName("productLatitude")
    private Double productLatitude;
    @SerializedName("userLongitude")
    private Double userLongitude;
    @SerializedName("userLatitude")
    private Double userLatitude;
    @SerializedName("fullName")
    private String userFullName;
    @SerializedName("email")
    private String userEmail;
    @SerializedName("userCreated")
    private long userCreatedSince;
    @SerializedName("followers")
    private int followers;
    @SerializedName("followings")
    private int followings;
    @SerializedName("rating")
    private Double rating;
    @SerializedName("similarProducts")
    private ArrayList<ProductListModel> mSimilarProductList;
    @SerializedName("link")
    private String shareLink;
    @SerializedName("isCreatedByMe")
    private boolean isMyProduct;
    @SerializedName("categoryName")
    private String categoryName;
    @SerializedName("userId")
    private String selletId;
    @SerializedName("isPromoted")
    private boolean isPromoted;
    @SerializedName("profilePicture")
    private String profilePicture;
    @SerializedName("shippingType")
    private String shippingModeType;
    @SerializedName("weight")
    private String weight;
    @SerializedName("shippingPrice")
    private String shippingPrice;
    @SerializedName("sharedCommunities")
    private ArrayList<TagData> mSharedTagList;
    @SerializedName("productStatus")
    private int productStatus;// 1 for normal and 2 for sold product
    @SerializedName("isFacebookLogin")
    @Expose
    private Boolean isFacebookLogin = false;
    @SerializedName("isEmailVerified")
    @Expose
    private Boolean isEmailVerified = false;
    @SerializedName("isDocumentsVerified")
    @Expose
    private Boolean officialIdVerified = false;
    @SerializedName("isPhoneVerified")
    @Expose
    private Boolean isPhoneVerified = false;
    @SerializedName("sellerVerified")
    @Expose
    private Boolean sellerVerified;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;

    public Boolean getSellerVerified() {
        return sellerVerified;
    }

    public void setSellerVerified(Boolean sellerVerified) {
        this.sellerVerified = sellerVerified;
    }

    public Boolean getFacebookLogin() {
        return isFacebookLogin;
    }

    public void setFacebookLogin(Boolean facebookLogin) {
        isFacebookLogin = facebookLogin;
    }

    public Boolean getEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public Boolean getOfficialIdVerified() {
        return officialIdVerified;
    }

    public void setOfficialIdVerified(Boolean officialIdVerified) {
        this.officialIdVerified = officialIdVerified;
    }

    public Boolean getPhoneVerified() {
        return isPhoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        isPhoneVerified = phoneVerified;
    }

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ArrayList<TagData> getmSharedTagList() {
        return mSharedTagList;
    }

    public void setmSharedTagList(ArrayList<TagData> mSharedTagList) {
        this.mSharedTagList = mSharedTagList;
    }

    public String getShippingModeType() {
        return shippingModeType;
    }

    public void setShippingModeType(String shippingModeType) {
        this.shippingModeType = shippingModeType;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(String shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    private int viewType = VIEW_TYPE_PRODUCT;
    private int moreProductCount;

    public int getMoreProductCount() {
        return moreProductCount;
    }

    public void setMoreProductCount(int moreProductCount) {
        this.moreProductCount = moreProductCount;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public boolean isTransactionCost() {
        return isTransactionCost;
    }

    public void setTransactionCost(boolean transactionCost) {
        isTransactionCost = transactionCost;
    }

    public int getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(int productStatus) {
        this.productStatus = productStatus;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public boolean isPromoted() {
        return isPromoted;
    }

    public void setPromoted(boolean promoted) {
        isPromoted = promoted;
    }

    private boolean loading;

    public static Creator<ProductDetailsData> getCREATOR() {
        return CREATOR;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public String getSelletId() {
        return selletId;
    }

    public void setSelletId(String selletId) {
        this.selletId = selletId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isMyProduct() {
        return isMyProduct;
    }

    public void setMyProduct(boolean myProduct) {
        isMyProduct = myProduct;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public int[] getShippingType() {
        return shippingType;
    }

    public void setShippingType(int[] shippingType) {
        this.shippingType = shippingType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getFirmPrice() {
        return firmPrice;
    }

    public void setFirmPrice(String firmPrice) {
        this.firmPrice = firmPrice;
    }

    public Boolean getNegotiable() {
        return isNegotiable;
    }

    public void setNegotiable(Boolean negotiable) {
        isNegotiable = negotiable;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }
//
//    public int getShippingType() {
//        return shippingType;
//    }
//
//    public void setShippingType(int shippingType) {
//        this.shippingType = shippingType;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ImageList> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageList> imageList) {
        this.imageList = imageList;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(long createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public String getProductAddress() {
        return productAddress;
    }

    public void setProductAddress(String productAddress) {
        this.productAddress = productAddress;
    }

    public Double getProductLongitude() {
        return productLongitude;
    }

    public void setProductLongitude(Double productLongitude) {
        this.productLongitude = productLongitude;
    }

    public Double getProductLatitude() {
        return productLatitude;
    }

    public void setProductLatitude(Double productLatitude) {
        this.productLatitude = productLatitude;
    }

    public Double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(Double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public Double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(Double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getUserCreatedSince() {
        return userCreatedSince;
    }

    public void setUserCreatedSince(long userCreatedSince) {
        this.userCreatedSince = userCreatedSince;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowings() {
        return followings;
    }

    public void setFollowings(int followings) {
        this.followings = followings;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public ArrayList<ProductListModel> getmSimilarProductList() {
        return mSimilarProductList;
    }

    public void setmSimilarProductList(ArrayList<ProductListModel> mSimilarProductList) {
        this.mSimilarProductList = mSimilarProductList;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ProductDetailsData() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productId);
        dest.writeString(this.firmPrice);
        dest.writeValue(this.isNegotiable);
        dest.writeByte(this.isTransactionCost ? (byte) 1 : (byte) 0);
        dest.writeInt(this.condition);
        dest.writeIntArray(this.shippingType);
        dest.writeString(this.title);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeTypedList(this.imageList);
        dest.writeString(this.productCategoryId);
        dest.writeString(this.description);
        dest.writeLong(this.createdDateTime);
        dest.writeValue(this.isLiked);
        dest.writeString(this.productAddress);
        dest.writeValue(this.productLongitude);
        dest.writeValue(this.productLatitude);
        dest.writeValue(this.userLongitude);
        dest.writeValue(this.userLatitude);
        dest.writeString(this.userFullName);
        dest.writeString(this.userEmail);
        dest.writeLong(this.userCreatedSince);
        dest.writeInt(this.followers);
        dest.writeInt(this.followings);
        dest.writeValue(this.rating);
        dest.writeTypedList(this.mSimilarProductList);
        dest.writeString(this.shareLink);
        dest.writeByte(this.isMyProduct ? (byte) 1 : (byte) 0);
        dest.writeString(this.categoryName);
        dest.writeString(this.selletId);
        dest.writeByte(this.isPromoted ? (byte) 1 : (byte) 0);
        dest.writeString(this.profilePicture);
        dest.writeString(this.shippingModeType);
        dest.writeString(this.weight);
        dest.writeString(this.shippingPrice);
        dest.writeTypedList(this.mSharedTagList);
        dest.writeInt(this.productStatus);
        dest.writeValue(this.isFacebookLogin);
        dest.writeValue(this.isEmailVerified);
        dest.writeValue(this.officialIdVerified);
        dest.writeValue(this.isPhoneVerified);
        dest.writeValue(this.sellerVerified);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.viewType);
        dest.writeInt(this.moreProductCount);
        dest.writeByte(this.loading ? (byte) 1 : (byte) 0);
    }

    protected ProductDetailsData(Parcel in) {
        this.productId = in.readString();
        this.firmPrice = in.readString();
        this.isNegotiable = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isTransactionCost = in.readByte() != 0;
        this.condition = in.readInt();
        this.shippingType = in.createIntArray();
        this.title = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.imageList = in.createTypedArrayList(ImageList.CREATOR);
        this.productCategoryId = in.readString();
        this.description = in.readString();
        this.createdDateTime = in.readLong();
        this.isLiked = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.productAddress = in.readString();
        this.productLongitude = (Double) in.readValue(Double.class.getClassLoader());
        this.productLatitude = (Double) in.readValue(Double.class.getClassLoader());
        this.userLongitude = (Double) in.readValue(Double.class.getClassLoader());
        this.userLatitude = (Double) in.readValue(Double.class.getClassLoader());
        this.userFullName = in.readString();
        this.userEmail = in.readString();
        this.userCreatedSince = in.readLong();
        this.followers = in.readInt();
        this.followings = in.readInt();
        this.rating = (Double) in.readValue(Double.class.getClassLoader());
        this.mSimilarProductList = in.createTypedArrayList(ProductListModel.CREATOR);
        this.shareLink = in.readString();
        this.isMyProduct = in.readByte() != 0;
        this.categoryName = in.readString();
        this.selletId = in.readString();
        this.isPromoted = in.readByte() != 0;
        this.profilePicture = in.readString();
        this.shippingModeType = in.readString();
        this.weight = in.readString();
        this.shippingPrice = in.readString();
        this.mSharedTagList = in.createTypedArrayList(TagData.CREATOR);
        this.productStatus = in.readInt();
        this.isFacebookLogin = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isEmailVerified = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.officialIdVerified = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isPhoneVerified = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.sellerVerified = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isSelected = in.readByte() != 0;
        this.viewType = in.readInt();
        this.moreProductCount = in.readInt();
        this.loading = in.readByte() != 0;
    }

    public static final Creator<ProductDetailsData> CREATOR = new Creator<ProductDetailsData>() {
        @Override
        public ProductDetailsData createFromParcel(Parcel source) {
            return new ProductDetailsData(source);
        }

        @Override
        public ProductDetailsData[] newArray(int size) {
            return new ProductDetailsData[size];
        }
    };
}
