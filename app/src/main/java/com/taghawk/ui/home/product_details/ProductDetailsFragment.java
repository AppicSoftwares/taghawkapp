package com.taghawk.ui.home.product_details;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.adapters.ProductResultAdapter;
import com.taghawk.adapters.SlidingImage_Adapter;
import com.taghawk.base.BaseActivity;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.CustomReportDialog;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.data.DataManager;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.ActivityProductDetailsBinding;
import com.taghawk.firebase.FirebaseManager;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.AddProduct.AddProductData;
import com.taghawk.model.AddProduct.AddProductModel;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.cashout.MerchantDetailBeans;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.home.LikeUnLike;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.home.ProductDetailsModel;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.request.User;
import com.taghawk.ui.cart.CartActivity;
import com.taghawk.ui.chat.MessagesDetailActivity;
import com.taghawk.ui.create.FeturedPostActivity;
import com.taghawk.ui.home.EditProductActivity;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.profile.OtherProfileActivity;
import com.taghawk.ui.review_rating.ReviewRatingActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.PermissionUtility;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class ProductDetailsFragment extends BaseFragment implements View.OnClickListener, OnMapReadyCallback {

    private ProductDetailsData productDetailsModelData;
    private GPSTracker gpsTracker;
    private ActivityProductDetailsBinding mBinding;
    private HomeViewModel mProductDetailsViewModel;
    private String productId;
    private Activity mActivity;
    private ArrayList<ProductListModel> mProductList;
    private ProductResultAdapter adapter;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private View mapView;
    private int similarProductPosition;
    private PopupWindow popup;
    private int shipingId;
    private String notificationId;
    private BaseActivity activity;
    private boolean isChatClicked;
    private MerchantDetailBeans beans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = ActivityProductDetailsBinding.inflate(inflater, container, false);
        initView();
        setLisener();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing view model
        mProductDetailsViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mProductDetailsViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mProductDetailsViewModel.getProductDetails().observe(this, new Observer<ProductDetailsModel>() {
            @Override
            public void onChanged(@Nullable ProductDetailsModel productDetailsModel) {
                getLoadingStateObserver().onChanged(false);
                productDetailsModelData = productDetailsModel.getmProductList();
                setData(productDetailsModel.getmProductList());
            }
        });
        mProductDetailsViewModel.getLikeViewModel().observe(this, new Observer<LikeUnLike>() {
            @Override
            public void onChanged(@Nullable LikeUnLike likeUnLike) {
                switch (likeUnLike.getRequestCode()) {
                    case AppConstants.REQUEST_CODE.LIKE_PRODUCT:
                        if (likeUnLike != null && likeUnLike.getLikeUnLikeModel().isLiked()) {
                            setLikeUnLike(R.drawable.ic_like_fill);
                        } else
                            setLikeUnLike(R.drawable.ic_like_unfill);
                        if (productDetailsModelData != null) {
                            productDetailsModelData.setLiked(likeUnLike.getLikeUnLikeModel().isLiked());
                        }
                        break;
                    case AppConstants.REQUEST_CODE.LIKE_SIMILAR_PRODUCT:
                        if (productDetailsModelData != null) {
                            productDetailsModelData.getmSimilarProductList().get(similarProductPosition).setLiked(likeUnLike.getLikeUnLikeModel().isLiked());
                            adapter.notifyItemChanged(similarProductPosition);
                        }
                        break;
                    case AppConstants.REQUEST_CODE.REPORT_PRODUCT:
                        getLoadingStateObserver().onChanged(false);
                        showToastShort(likeUnLike.getMessage());
                        break;
                    case AppConstants.REQUEST_CODE.SHARE_PRODUCT:
                        getLoadingStateObserver().onChanged(false);
                        AppUtils.share(mActivity, productDetailsModelData.getShareLink(), "TagHawk Share Product", getString(R.string.share));
                        break;
                }
            }
        });
        mProductDetailsViewModel.getCartViewModel().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse.getCode() == 200) {
                    if (commonResponse.getRequestCode() == AppConstants.REQUEST_CODE.ADD_TO_CART) {
//                        showToastShort(commonResponse.getMessage());
                        showCheckoutShippingDialog();
//                        openCartActicity();
                    } else if (commonResponse.getRequestCode() == AppConstants.REQUEST_CODE.DELETE_PRODUCT) {
                        showToastShort(commonResponse.getMessage());
                        mActivity.setResult(RESULT_OK);
                        mActivity.finish();
                    } else if (commonResponse.getRequestCode() == AppConstants.REQUEST_CODE.DELETE_ALL_PRODUCT_CART) {
                        mProductDetailsViewModel.addCart(productDetailsModelData.getProductId(), 1, shipingId, productDetailsModelData.getSelletId(), AppConstants.REQUEST_CODE.ADD_TO_CART);
                    }
                } else if (commonResponse.getCode() == 211) {
                    DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.replace_cart_item), commonResponse.getMessage(), getString(R.string.replace), getString(R.string.cencel), new DialogCallback() {
                        @Override
                        public void submit(String data) {
                            mProductDetailsViewModel.deletAllCart("", 2, AppConstants.REQUEST_CODE.DELETE_ALL_PRODUCT_CART);
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                } else if (commonResponse.getCode() == 400) {
//                    showToastShort(commonResponse.getMessage());
                    DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.already_exist), commonResponse.getMessage(), getString(R.string.checkout), getString(R.string.shop_more), new DialogCallback() {
                        @Override
                        public void submit(String data) {
                            openCartActicity();
                        }

                        @Override
                        public void cancel() {
                            mActivity.finish();
                        }
                    });
                }
            }
        });

        if (AppUtils.isInternetAvailable(mActivity)) {
            mProductDetailsViewModel.getProductDetailData(productId);
            if (notificationId != null && notificationId.length() > 0) {
                mProductDetailsViewModel.markNotificationRead(notificationId);
            }
        }

    }

    private void showCheckoutShippingDialog() {
        DialogUtil.getInstance().CustomBottomSheetCheckoutDialog(mActivity, new OnDialogItemClickListener() {
            @Override
            public void onPositiveBtnClick() {
                mActivity.finish();
            }

            @Override
            public void onNegativeBtnClick() {
                openCartActicity();
            }
        });
    }

    private void openCartActicity() {
        Intent intent = new Intent(mActivity, CartActivity.class);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.PRODUCT_SOLD);
    }

    @SuppressLint("SetTextI18n")
    private void setData(ProductDetailsData productDetailsModel) {
        PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 201);
        mBinding.includeHeader.tvTitle.setText(productDetailsModel.getTitle());
        mBinding.tvTitleName.setText(productDetailsModel.getTitle());
        mBinding.tvProductPrice.setText("$ " + productDetailsModel.getFirmPrice());
        //AKM
        //mBinding.tvSellerProductLocation.setText(AppUtils.getState(mActivity, new LatLng(productDetailsModelData.getProductLatitude(), productDetailsModelData.getProductLongitude())));
        mBinding.tvSellerProductLocation.setText(productDetailsModel.getCity() + ", " + productDetailsModel.getState());
        mBinding.tvUserLocation.setText(productDetailsModel.getCity() + ", " + productDetailsModel.getState());

        mBinding.tvProductCondition.setText(getString(R.string.condition) + ": " + AppUtils.getProductCondition(mActivity, productDetailsModel.getCondition()));
        mBinding.tvProductPostedDate.setText(getString(R.string.posted) + " " + new PrettyTime().format(AppUtils.timeStampToDate(productDetailsModel.getCreatedDateTime())));
        mBinding.tvProductDescription.setText(productDetailsModel.getDescription());
        mBinding.tvSellerName.setText(productDetailsModel.getUserFullName());
        mBinding.tvDeliveryType.setText(setDevliveryType(productDetailsModel.getShippingType()));
        if (productDetailsModel.getNegotiable()) {
            mBinding.tvNegotiable.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvNegotiable.setVisibility(View.GONE);
        }
        //if (productDetailsModel.getProductLatitude() != null && productDetailsModel.getProductLongitude() != null)
          //  mBinding.tvUserLocation.setText(AppUtils.getState(mActivity, new LatLng(productDetailsModel.getProductLatitude(), productDetailsModel.getProductLongitude())));
        mBinding.tvUserCreateDate.setText(getString(R.string.member_since) + AppUtils.timeStampStringDate(productDetailsModel.getUserCreatedSince()));
        mBinding.tvFollowFollowers.setText(getString(R.string.follower) + productDetailsModel.getFollowers() + " Following: " + productDetailsModel.getFollowings());
        sellProfilePic(productDetailsModel.getProfilePicture());
        setDistance(productDetailsModel);
        mBinding.tvRating.setText("" + productDetailsModel.getRating());
        setLikeUnLike(productDetailsModel);
        setPager(productDetailsModel.getImageList());
        setSimilarProduct(productDetailsModel);
        setMarker(productDetailsModel);
        isMyProduct(productDetailsModel.isMyProduct());
        if (productDetailsModelData.isPromoted() && productDetailsModel.isMyProduct()) {
            mBinding.tvBuyNow.setText(getString(R.string.promoted));
        }
        if (productDetailsModel.getProductStatus() == 2 || productDetailsModel.getProductStatus() == 5) {
            soldOutProduct();
        }
        if (productDetailsModel.getEmailVerified()) {
            mBinding.ivInfoEmail.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivInfoEmail.setVisibility(View.GONE);

        }
        if (productDetailsModel.getPhoneVerified()) {
            mBinding.ivInfoPhone.setVisibility(View.VISIBLE);
        } else
            mBinding.ivInfoPhone.setVisibility(View.GONE);
        if (productDetailsModel.getFacebookLogin()) {
            mBinding.ivInfoFacebook.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivInfoFacebook.setVisibility(View.GONE);
        }

        if (productDetailsModel.getOfficialIdVerified()) {
            mBinding.ivInfoDocument.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivInfoDocument.setVisibility(View.GONE);

        }
        if (!productDetailsModel.getFacebookLogin() && !productDetailsModel.getPhoneVerified() && !productDetailsModel.getEmailVerified() && !productDetailsModel.getOfficialIdVerified())
            mBinding.llVerifyContainer.setVisibility(View.GONE);
        if (productDetailsModelData.getSellerVerified()) {
            mBinding.ivShield.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivShield.setVisibility(View.GONE);
        }
    }


    private void soldOutProduct() {
        mBinding.tvBuyNow.setEnabled(false);
        mBinding.tvBuyNow.setText(getString(R.string.sold));
        mBinding.tvChat.setVisibility(View.VISIBLE);
        mBinding.tvBuyNow.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_sold_out));
        if (productDetailsModelData != null) {
            if (!(DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                if (productDetailsModelData != null) {
                    if (productDetailsModelData.isMyProduct()) {
//                        DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.delete_product), getString(R.string.product_delete_msg), getString(R.string.delete), getString(R.string.cencel), new DialogCallback() {
//                            @Override
//                            public void submit(String data) {
//                                mProductDetailsViewModel.deleteProduct(productDetailsModelData.getProductId());
//                            }
//
//                            @Override
//                            public void cancel() {
//
//                            }
//                        });
                    } else {
                        if (!isChatClicked) {
                            isChatClicked = true;
                            final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(FirebaseManager.getFirebaseRoomId(user.getUserId(), productDetailsModelData.getSelletId())).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ChatModel chatModel = null;
                                    ChatProductModel chatProductModel = new ChatProductModel();
                                    chatProductModel.setProductId(productDetailsModelData.getProductId());
                                    chatProductModel.setProductName(productDetailsModelData.getTitle());
                                    chatProductModel.setProductPrice(Double.parseDouble(productDetailsModelData.getFirmPrice()));
                                    if (productDetailsModelData.getImageList() != null && productDetailsModelData.getImageList().size() > 0)
                                        chatProductModel.setProductImage(productDetailsModelData.getImageList().get(0).getThumbUrl());
                                    else
                                        chatProductModel.setProductImage("");
                                    if (dataSnapshot.exists())
                                        chatModel = dataSnapshot.getValue(ChatModel.class);
                                    else {
                                        chatModel = new ChatModel();
                                        chatModel.setPinned(false);
                                        chatModel.setChatMute(false);
                                        chatModel.setCreatedTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                                        chatModel.setRoomName(productDetailsModelData.getUserFullName());
                                        chatModel.setRoomImage(productDetailsModelData.getProfilePicture());
                                        chatModel.setRoomId(FirebaseManager.getFirebaseRoomId(user.getUserId(), productDetailsModelData.getSelletId()));
                                        chatModel.setChatType(AppConstants.FIREBASE.FIREBASE_SINGLE_CHAT);
                                        chatModel.setOtherUserId(productDetailsModelData.getSelletId());
                                    }
                                    chatModel.setProductInfo(chatProductModel);

                                    startActivityForResult(new Intent(mActivity, MessagesDetailActivity.class).putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel).putExtra("tag", "1").putExtra(AppConstants.FIREBASE.TIMESTAMP, chatModel.getCreatedTimeStampLong()), AppConstants.ACTIVITY_RESULT.SINGLE_CHAT_USER_BLOCK);
                                    isChatClicked = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    isChatClicked = false;

                                }
                            });
                        }
                    }
                }
            } else {
                DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, activity);
            }
        }
    }



    private void setDistance(ProductDetailsData productDetailsModel) {
        if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 201)) {
            if (DataManager.getInstance() != null && DataManager.getInstance().getFilterLatitude() != null && DataManager.getInstance().getFilterLongitude() != null && DataManager.getInstance().getFilterLongitude().trim().length() > 0 && Double.valueOf(DataManager.getInstance().getFilterLatitude()) > 0) {
                mBinding.tvUserLocationWithDistance.setText(getString(R.string.your_location_distance) + " " + AppUtils.calculateDistance(productDetailsModel.getProductLatitude(), productDetailsModel.getProductLongitude(), Double.valueOf(DataManager.getInstance().getFilterLatitude()), Double.valueOf(DataManager.getInstance().getFilterLongitude())) + " Miles");
            } else if (gpsTracker != null && gpsTracker.getLocation() != null && productDetailsModel.getProductLatitude() != null && productDetailsModel.getProductLongitude() != null) {
                mBinding.tvUserLocationWithDistance.setText(getString(R.string.your_location_distance) + " " + AppUtils.calculateDistance(productDetailsModel.getProductLatitude(), productDetailsModel.getProductLongitude(), gpsTracker.getLocation()) + " Miles");
            }
        } else
            mBinding.tvUserLocationWithDistance.setText(getString(R.string.your_location));
    }

    private void setSimilarProduct(ProductDetailsData productDetailsModel) {
        if (productDetailsModel.getmSimilarProductList().size() > 0) {
            setUpSimilarList(productDetailsModel.getmSimilarProductList());
            mBinding.tvSimilatItems.setVisibility(View.VISIBLE);

        } else {
            mBinding.tvSimilatItems.setVisibility(View.GONE);
        }
    }

    private void setLikeUnLike(ProductDetailsData productDetailsModel) {
        if (productDetailsModel.getLiked()) {
            setLikeUnLike(R.drawable.ic_like_fill);
        } else
            setLikeUnLike(R.drawable.ic_like_unfill);
    }

    private void sellProfilePic(String profilePicture) {
        if (profilePicture != null && profilePicture.length() > 0) {
            Glide.with(mActivity).asBitmap().load(profilePicture).apply(RequestOptions.placeholderOf(R.drawable.ic_tab_profile_unactive)).into(mBinding.ivSellerImage);
            mBinding.tvFirstCharater.setVisibility(View.GONE);
        } else {
            if (productDetailsModelData != null && (productDetailsModelData.getUserFullName() != null && productDetailsModelData.getUserFullName().length() > 0)) {
                mBinding.tvFirstCharater.setVisibility(View.VISIBLE);
                mBinding.tvFirstCharater.setText(productDetailsModelData.getUserFullName().trim().toUpperCase().substring(0, 1));
            }
        }

    }

    private void isMyProduct(boolean myProduct) {
        if (myProduct) {
            setIsMyText(R.string.promote, R.string.delete, View.GONE);
            mBinding.ivLike.setVisibility(View.GONE);
            mBinding.ivEdit.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivEdit.setVisibility(View.GONE);
            mBinding.ivLike.setVisibility(View.VISIBLE);
            setIsMyText(R.string.reserve_item, R.string.chat, View.VISIBLE);
        }
    }

    private void setIsMyText(int p, int p2, int reportVisibility) {
        mBinding.tvBuyNow.setText(getString(p));
        mBinding.tvChat.setText(getString(p2));
        mBinding.includeHeader.ivReport.setVisibility(reportVisibility);
    }

    private void setLikeUnLike(int imageResource) {
        mBinding.ivLike.setImageDrawable(mActivity.getResources().getDrawable(imageResource));
    }

    private String setDevliveryType(int[] shippingType) {
        String shipingType = "";

        for (int i = 0; i < shippingType.length; i++) {
            if (i == 0) {
                shipingType = getShippingType(shippingType[i]);
            } else {
                shipingType = shipingType + " , " + getShippingType(shippingType[i]);
            }
        }
        return getString(R.string.availabilty) + " " + shipingType;
    }

    private String getShippingType(int shipingType) {
        String shhipingType = "";
        switch (shipingType) {
            case 1:
                if (productDetailsModelData.getShippingType().length > 1) {
                    shhipingType = "Pick-Up";
                } else
                    shhipingType = "Pick-Up Only";
                break;
            case 2:
                shhipingType = getString(R.string.deliver);
                break;
            case 3:
                shhipingType = getString(R.string.shipping);
                break;
        }
        return shhipingType;
    }

    private void setMarker(ProductDetailsData productDetailsModel) {
        if (map != null) {
            map.getUiSettings().setAllGesturesEnabled(false);
            map.addCircle(drawCircle(new LatLng(productDetailsModel.getProductLatitude(), productDetailsModel.getProductLongitude())));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(productDetailsModel.getProductLatitude(), productDetailsModel.getProductLongitude()), 12));
        }
    }

    private CircleOptions drawCircle(LatLng point) {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(2500);

        // Border color of the circle
        circleOptions.strokeColor(Color.CYAN);

        // Fill color of the circle
        circleOptions.fillColor(Color.parseColor("#802bcefd"));

        // Border width of the circle
        circleOptions.strokeWidth(2);

        return circleOptions;
    }

    private void setUpSimilarList(final ArrayList<ProductListModel> getmSimilarProductList) {
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        adapter = new ProductResultAdapter(getmSimilarProductList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.card_main:
                        int position = (int) v.getTag();
                        Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
                        intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, getmSimilarProductList.get(position).get_id());
                        v.getContext().startActivity(intent);
                        break;
                    case R.id.is_liked:
                        if (!(DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                            int status;
                            similarProductPosition = (int) v.getTag();
                            if (getmSimilarProductList.get(similarProductPosition).getLiked()) {
                                status = 0;
                                setLikeUnLike(R.drawable.ic_like_unfill, v);
                            } else {
                                status = 1;
                                setLikeUnLike(R.drawable.ic_like_fill, v);

                            }
                            hitLikeUnLike(getmSimilarProductList.get(similarProductPosition).get_id(), status, AppConstants.REQUEST_CODE.LIKE_SIMILAR_PRODUCT);
                        } else {
                            DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, activity);
                        }
                        break;
                }
            }
        });
        mBinding.rvSimilerItems.setLayoutManager(layoutManager);
        mBinding.rvSimilerItems.setAdapter(adapter);
    }

    private void openRatingScreen(ProductDetailsData bean) {
        Intent intent = new Intent(mActivity, ReviewRatingActivity.class);
        intent.putExtra(AppConstants.KEY_CONSTENT.SELLER_ID, bean.getSelletId());
        intent.putExtra(AppConstants.KEY_CONSTENT.FULL_NAME, bean.getUserFullName());
        intent.putExtra(AppConstants.KEY_CONSTENT.JOIN_FROM, getString(R.string.member_since) + AppUtils.timeStampStringDate(bean.getUserCreatedSince()));
        intent.putExtra(AppConstants.KEY_CONSTENT.SELLER_RATING, bean.getRating());
        intent.putExtra(AppConstants.KEY_CONSTENT.IMAGES, bean.getProfilePicture());
        startActivity(intent);
    }

    private void setPager(ArrayList<ImageList> imageList) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        mBinding.vpImages.setAdapter(new SlidingImage_Adapter(mActivity, imageList, true));
        if (imageList.size() == 1) {
            mBinding.circleIndicator.setVisibility(View.GONE);
        } else if (imageList.size() > 1) {
            mBinding.circleIndicator.setVisibility(View.VISIBLE);
        }
        mBinding.circleIndicator.setViewPager(mBinding.vpImages);

    }


    private void initView() {
        mActivity = getActivity();
        activity = ((BaseActivity) mActivity);
        productDetailsModelData = new ProductDetailsData();
        if (getArguments() != null) {
            productId = getArguments().getString(AppConstants.NOTIFICATION_ACTION.ENTITY_ID);
            notificationId = getArguments().getString(AppConstants.NOTIFICATION_ACTION.NOTIFICATION_ID, "");
        }
        gpsTracker = new GPSTracker(mActivity);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mBinding.nestedScrol.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                int color = Color.parseColor("#FFFFFFFF"); // ideally a global variable
                int color = Color.parseColor("#2bcefd"); // ideally a global variable
                if (scrollY < 954) {
                    int alpha = (scrollY << 54) | (-1 >>> 8);
                    color &= (alpha);
                    setTintHeader(R.color.White);
                    mBinding.includeHeader.tvTitle.setVisibility(View.GONE);
                } else {
                    setTintHeader(R.color.White);
                    mBinding.includeHeader.tvTitle.setVisibility(View.VISIBLE);
                }
                mBinding.includeHeader.main.setBackgroundColor(color);
            }
        });
    }

    private void setLisener() {
        mBinding.includeHeader.ivBack.setOnClickListener(this);
        mBinding.includeHeader.ivReport.setOnClickListener(this);
        mBinding.includeHeader.ivShare.setOnClickListener(this);
        mBinding.tvBuyNow.setOnClickListener(this);
        mBinding.tvChat.setOnClickListener(this);
        mBinding.ivLike.setOnClickListener(this);
        mBinding.ivEdit.setOnClickListener(this);
        mBinding.llSellerData.setOnClickListener(this);
        mBinding.ivSellerImage.setOnClickListener(this);
        mBinding.llProfile.setOnClickListener(this);
        mBinding.tvRating.setOnClickListener(this);
        mBinding.cardMap.setOnClickListener(this);

    }

    private void setTintHeader(int p) {
        mBinding.includeHeader.ivBack.setColorFilter(ContextCompat.getColor(mActivity, p), android.graphics.PorterDuff.Mode.SRC_IN);
        mBinding.includeHeader.ivShare.setColorFilter(ContextCompat.getColor(mActivity, p), android.graphics.PorterDuff.Mode.SRC_IN);
        mBinding.includeHeader.ivReport.setColorFilter(ContextCompat.getColor(mActivity, p), android.graphics.PorterDuff.Mode.SRC_IN);
        mBinding.includeHeader.tvTitle.setTextColor(ContextCompat.getColor(mActivity, p));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                mActivity.finish();
                break;
            case R.id.iv_like:
                int isLike;
                if (!(DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    if (productDetailsModelData != null) {
                        if (!productDetailsModelData.getLiked()) {
                            setLikeUnLike(R.drawable.ic_like_unfill);
                            isLike = 1;
                        } else {
                            setLikeUnLike(R.drawable.ic_like_fill);
                            isLike = 0;
                        }
                        hitLikeUnLike(productDetailsModelData.getProductId(), isLike, AppConstants.REQUEST_CODE.LIKE_PRODUCT);
                    }
                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, activity);
                }
                break;
            case R.id.iv_report:
                if (!(DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    new CustomReportDialog(mActivity, new DialogCallback() {
                        @Override
                        public void submit(String reason) {
                            mProductDetailsViewModel.reportProduct(productDetailsModelData.getProductId(), reason, AppConstants.REQUEST_CODE.REPORT_PRODUCT);
                        }

                        @Override
                        public void cancel() {

                        }
                    }).show();
                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, activity);
                }
                break;
            case R.id.iv_share:
                mProductDetailsViewModel.shareProduct(productDetailsModelData.getProductId(), AppConstants.REQUEST_CODE.SHARE_PRODUCT);
                break;
            case R.id.tv_buy_now:
                if (!(DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    if (productDetailsModelData != null) {
                        if (productDetailsModelData.isMyProduct()) {
                            if (!productDetailsModelData.isPromoted())
                                openFeatureActivity();
                        } else {
                            buyProductAction();
                        }
                    }
                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, activity);
                }

                break;
            case R.id.iv_edit:
                if (!(DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    openEditActivity();
                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, activity);
                }
                break;
            case R.id.tv_chat:
                if (productDetailsModelData != null) {
                    if (!(DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                        if (productDetailsModelData != null) {
                            if (productDetailsModelData.isMyProduct()) {
                                DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.delete_product), getString(R.string.product_delete_msg), getString(R.string.delete), getString(R.string.cencel), new DialogCallback() {
                                    @Override
                                    public void submit(String data) {
                                        mProductDetailsViewModel.deleteProduct(productDetailsModelData.getProductId());
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });
                            } else {
                                if (!isChatClicked) {
                                    isChatClicked = true;
                                    final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(FirebaseManager.getFirebaseRoomId(user.getUserId(), productDetailsModelData.getSelletId())).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            ChatModel chatModel = null;
                                            ChatProductModel chatProductModel = new ChatProductModel();
                                            chatProductModel.setProductId(productDetailsModelData.getProductId());
                                            chatProductModel.setProductName(productDetailsModelData.getTitle());
                                            chatProductModel.setProductPrice(Double.parseDouble(productDetailsModelData.getFirmPrice()));
                                            if (productDetailsModelData.getImageList() != null && productDetailsModelData.getImageList().size() > 0)
                                                chatProductModel.setProductImage(productDetailsModelData.getImageList().get(0).getThumbUrl());
                                            else
                                                chatProductModel.setProductImage("");
                                            if (dataSnapshot.exists())
                                                chatModel = dataSnapshot.getValue(ChatModel.class);
                                            else {
                                                chatModel = new ChatModel();
                                                chatModel.setPinned(false);
                                                chatModel.setChatMute(false);
                                                chatModel.setCreatedTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                                                chatModel.setRoomName(productDetailsModelData.getUserFullName());
                                                chatModel.setRoomImage(productDetailsModelData.getProfilePicture());
                                                chatModel.setRoomId(FirebaseManager.getFirebaseRoomId(user.getUserId(), productDetailsModelData.getSelletId()));
                                                chatModel.setChatType(AppConstants.FIREBASE.FIREBASE_SINGLE_CHAT);
                                                chatModel.setOtherUserId(productDetailsModelData.getSelletId());
                                            }
                                            chatModel.setProductInfo(chatProductModel);
                                            startActivityForResult(new Intent(mActivity, MessagesDetailActivity.class).putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel).putExtra(AppConstants.FIREBASE.TIMESTAMP, chatModel.getCreatedTimeStampLong()), AppConstants.ACTIVITY_RESULT.SINGLE_CHAT_USER_BLOCK);
                                            isChatClicked = false;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            isChatClicked = false;

                                        }
                                    });
                                }
                            }
                        }
                    } else {
                        DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, activity);
                    }
                }
                break;
            case R.id.ll_profile:
            case R.id.ll_seller_data:
            case R.id.iv_seller_image:
                if (productDetailsModelData != null && productDetailsModelData.getUserFullName() != null && productDetailsModelData.getUserFullName().trim().length() > 0) {
                    if (!(productDetailsModelData.getSelletId().equalsIgnoreCase(DataManager.getInstance().getUserDetails().getUserId())))
                        openSellerProfile(productDetailsModelData.getSelletId());
                }
                break;
            case R.id.tv_rating:
                if (productDetailsModelData != null) {
                    openRatingScreen(productDetailsModelData);
                }
                break;
            case R.id.card_map:
                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 400))
                    openMapFullScreenActivity();
                break;

        }
    }


    private void openMapFullScreenActivity() {
        Intent intent = new Intent(mActivity, MapFullScreenActivity.class);
        intent.putExtra(AppConstants.KEY_CONSTENT.LAT, productDetailsModelData.getProductLatitude());
        intent.putExtra(AppConstants.KEY_CONSTENT.LONGI, productDetailsModelData.getProductLongitude());
        startActivity(intent);
    }

    private void openSellerProfile(String selletId) {
        Intent intent = new Intent(mActivity, OtherProfileActivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, selletId);
        startActivity(intent);

    }

    private void openFeatureActivity() {
        Intent intent = new Intent(mActivity, FeturedPostActivity.class);
        AddProductModel model = new AddProductModel();
        AddProductData data = new AddProductData();
        data.setId(productDetailsModelData.getProductId());
        data.setImages(productDetailsModelData.getImageList());
        data.setFirmPrice(Double.valueOf(productDetailsModelData.getFirmPrice()));
        model.setmAddProductData(data);
        intent.putExtra("DATA", model);
        intent.putExtra("SHARED_TAG_DATA", productDetailsModelData.getmSharedTagList());
        startActivity(intent);
    }

    private void buyProductAction() {
        if (productDetailsModelData != null) {
            final Integer[] shipping = getIntegerFromInt(productDetailsModelData.getShippingType());
            if (productDetailsModelData.getShippingType().length > 1 && Arrays.asList(shipping).contains(3)) {
                getCustomShippingBottomDialog(shipping, new OnDialogViewClickListener() {
                    @Override
                    public void onSubmit(String txt, int id) {
                        shipingId = id;
                        hitAddToCart(productDetailsModelData.getProductId(), id, productDetailsModelData.getSelletId(), AppConstants.REQUEST_CODE.ADD_TO_CART);
                    }
                });
            } else {
                shipingId = productDetailsModelData.getShippingType()[0];
                hitAddToCart(productDetailsModelData.getProductId(), productDetailsModelData.getShippingType()[0], productDetailsModelData.getSelletId(), AppConstants.REQUEST_CODE.ADD_TO_CART);
            }
        }
    }

    private Integer[] getIntegerFromInt(int[] shippingType) {
        Integer[] integers = new Integer[shippingType.length];
        for (int i = 0; i < shippingType.length; i++) {
            integers[i] = Integer.valueOf(shippingType[i]);
        }
        return integers;
    }

    private void hitAddToCart(String productId, int i, String sellerId, int requestCode) {
        mProductDetailsViewModel.addCart(productId, 1, i, sellerId, requestCode);
    }

    private void openEditActivity() {
        Intent intent = new Intent(mActivity, EditProductActivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, productDetailsModelData);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.EDIT_PRODUCT);
    }

    private void hitLikeUnLike(String productId, int isLike, int requestCode) {
        mProductDetailsViewModel.getLikeUnLike(productId, isLike, requestCode);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 400))
                    openMapFullScreenActivity();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 201) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (gpsTracker != null && gpsTracker.getLocation() != null) {
                    if (productDetailsModelData != null) {
                        mBinding.tvUserLocationWithDistance.setText(getString(R.string.your_location_distance) + AppUtils.calculateDistance(productDetailsModelData.getProductLatitude(), productDetailsModelData.getProductLongitude(), gpsTracker.getLocation()) + " Miles");
                    }
                }
            } else
                mBinding.tvUserLocationWithDistance.setText(getString(R.string.your_location_distance));

        }
    }


    private void setLikeUnLike(int imageResource, View mBinding) {
        ((AppCompatImageView) mBinding).setImageDrawable(mBinding.getContext().getResources().getDrawable(imageResource));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.EDIT_PRODUCT:
                if (resultCode == RESULT_OK) {
                    mProductDetailsViewModel.getProductDetailData(productId);
                }
                break;
            case AppConstants.ACTIVITY_RESULT.PRODUCT_SOLD:
                if (resultCode == RESULT_OK) {
                    soldOutProduct();
                }
                break;
            case AppConstants.ACTIVITY_RESULT.SINGLE_CHAT_USER_BLOCK:
                if (resultCode == RESULT_OK) {
                    boolean isBlock = data.getBooleanExtra(AppConstants.BUNDLE_DATA, false);
                    if (isBlock) {
                        mActivity.finish();
                    }
                }
                break;
        }
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
//        super.onFailure(failureResponse);
        if (productDetailsModelData == null || productDetailsModelData.getProductId() == null) {
            getCustomBottomDialog(getString(R.string.oops), failureResponse.getErrorMessage().toString(), new OnDialogItemClickListener() {
                @Override
                public void onPositiveBtnClick() {
                    mActivity.finish();
                }

                @Override
                public void onNegativeBtnClick() {

                }
            });
        } else {
            showToastLong(failureResponse.getErrorMessage());
        }
    }
}
