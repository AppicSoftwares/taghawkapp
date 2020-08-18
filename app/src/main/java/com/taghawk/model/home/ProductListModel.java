package com.taghawk.model.home;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductListModel implements Parcelable {

    @SerializedName("_id")
    private String _id;
    @SerializedName("firmPrice")
    private String firmPrice;
    @SerializedName("title")
    private String productTitle;
    @SerializedName("images")
    private ArrayList<ImageList> imageLists;
    @SerializedName("created")
    private String createdDateTime;
    @SerializedName("sellerRating")
    private String sellerRating;
    @SerializedName("sellerVerified")
    private Boolean isSellerVerified;
    @SerializedName("isLiked")
    private Boolean isLiked;
    @SerializedName("viewCount")
    private int viewCount;
    @SerializedName("shareCount")
    private int shareCount;
    @SerializedName("isNegotiable")
    private boolean isNegotiable;
    @SerializedName("condition")
    private int condition;
    @SerializedName("shippingAvailibility")
    private int[] shippingType;
    @SerializedName("status")
    private int status;
    @SerializedName("userId")
    private String userId;
    @SerializedName("productCategoryId")
    private String productCategoryId;
    @SerializedName("description")
    private String description;
/*    @SerializedName("location")
    private String location;*/
 @SerializedName("city")
    private String city;
@SerializedName("state")
    private String state;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

/*    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }*/

    public static Creator<ProductListModel> getCREATOR() {
        return CREATOR;
    }

    @SerializedName("link")
    private String shareLink;

    @SerializedName("isPromoted")
    @Expose
    private Boolean isPromoted;
    @SerializedName("isCreatedByMe")
    private boolean isMyProduct;

    private boolean isSelected = true;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private int viewType=ProductDetailsData.VIEW_TYPE_PRODUCT;
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

    public boolean isMyProduct() {
        return isMyProduct;
    }

    public void setMyProduct(boolean myProduct) {
        isMyProduct = myProduct;
    }

    private boolean loading;

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public boolean isNegotiable() {
        return isNegotiable;
    }

    public void setNegotiable(boolean negotiable) {
        isNegotiable = negotiable;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int[] getShippingType() {
        return shippingType;
    }

    public void setShippingType(int[] shippingType) {
        this.shippingType = shippingType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public Boolean getPromoted() {
        return isPromoted;
    }

    public void setPromoted(Boolean promoted) {
        isPromoted = promoted;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFirmPrice() {
        //return new DecimalFormat("0.#").format(firmPrice);
        return firmPrice;
    }

    public void setFirmPrice(String firmPrice) {
        this.firmPrice = firmPrice;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public ArrayList<ImageList> getImageLists() {
        return imageLists;
    }

    public void setImageLists(ArrayList<ImageList> imageLists) {
        this.imageLists = imageLists;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(String sellerRating) {
        this.sellerRating = sellerRating;
    }

    public Boolean getSellerVerified() {
        return isSellerVerified;
    }

    public void setSellerVerified(Boolean sellerVerified) {
        isSellerVerified = sellerVerified;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static class Location {
        @Expose
        @SerializedName("coordinates")
        private List<Double> coordinates;
        @Expose
        @SerializedName("type")
        private String type;
        @Expose
        @SerializedName("location")
        private String location;

        public List<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Double> coordinates) {
            this.coordinates = coordinates;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLocation() {
            return location;

        }

        public void setLocation(String location) {
            this.location = location;
        }
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.firmPrice);
        dest.writeString(this.productTitle);
        dest.writeTypedList(this.imageLists);
        dest.writeString(this.createdDateTime);
        dest.writeString(this.sellerRating);
        dest.writeValue(this.isSellerVerified);
        dest.writeValue(this.isLiked);
    }


    public ProductListModel() {
    }

    protected ProductListModel(Parcel in) {
        this._id = in.readString();
        this.firmPrice = in.readString();
        this.productTitle = in.readString();
        this.imageLists = in.createTypedArrayList(ImageList.CREATOR);
        this.createdDateTime = in.readString();
        this.sellerRating = in.readString();
        this.isSellerVerified = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isLiked = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<ProductListModel> CREATOR = new Parcelable.Creator<ProductListModel>() {
        @Override
        public ProductListModel createFromParcel(Parcel source) {
            return new ProductListModel(source);
        }

        @Override
        public ProductListModel[] newArray(int size) {
            return new ProductListModel[size];
        }
    };



}
