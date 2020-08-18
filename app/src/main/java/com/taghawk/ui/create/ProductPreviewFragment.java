package com.taghawk.ui.create;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taghawk.R;
import com.taghawk.adapters.ProductResultAdapter;
import com.taghawk.adapters.SlidingImage_Adapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentProductPreciewBinding;
import com.taghawk.model.AddProduct.AddProductModel;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.tag.TagData;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.ImageSorting;
import com.taghawk.util.PermissionUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ProductPreviewFragment extends BaseFragment implements View.OnClickListener, OnMapReadyCallback, AmazonCallback {

    ProductDetailsData productDetailsModelData;
    GPSTracker gpsTracker;
    private FragmentProductPreciewBinding mBinding;
    private AddProductViewModel mProductDetailsViewModel;
    private String productId;
    private Activity mActivity;
    private ArrayList<ProductListModel> mProductList;
    private ProductResultAdapter adapter;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private View mapView;
    private int similarProductPosition;
    private PopupWindow popup;
    private HashMap<String, Object> dataHashMap;
    private ArrayList<ImageList> imageList;
    ArrayList<TagData> mSharedTagsList = new ArrayList<>();
    private AmazonS3 mAmazonS3;
    private int imageUploadCount;
    private ArrayList<ImageList> mImageList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentProductPreciewBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing view model
        mProductDetailsViewModel = ViewModelProviders.of(this).get(AddProductViewModel.class);
        mProductDetailsViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mProductDetailsViewModel.getmAddProductViewModel().observe(this, new Observer<AddProductModel>() {
            @Override
            public void onChanged(@Nullable AddProductModel addProductModel) {
                getLoadingStateObserver().onChanged(false);
                if (addProductModel.getCode() == 200) {
//                    showToastShort(addProductModel.getMessage());
                    OpenFeaturedActivity(addProductModel);
//                    openHomeActivity();
                }
            }
        });
    }

    private void OpenFeaturedActivity(AddProductModel addProductModel) {
        Intent intent = new Intent(mActivity, FeturedPostActivity.class);
        intent.putExtra("DATA", addProductModel);
        intent.putExtra("SHARED_TAG_DATA", mSharedTagsList);
        startActivity(intent);
        mActivity.finish();
    }

    private void openHomeActivity() {
        Intent intent = new Intent(mActivity, HomeActivity.class);
        startActivity(intent);
        mActivity.finish();
    }

    @SuppressLint("SetTextI18n")
    private void setData(HashMap<String, Object> parms) {
        PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 201);
        mBinding.tvTitleName.setText(parms.get(AppConstants.KEY_CONSTENT.TITLE).toString());
        if (parms.containsKey(AppConstants.KEY_CONSTENT.FIRM_PRICE))
            mBinding.tvProductPrice.setText("$ " + parms.get(AppConstants.KEY_CONSTENT.FIRM_PRICE).toString());
        if (parms.containsKey(AppConstants.KEY_CONSTENT.LOCATION))
            mBinding.tvSellerProductLocation.setText(parms.get(AppConstants.KEY_CONSTENT.LOCATION).toString());
        if (parms.containsKey(AppConstants.KEY_CONSTENT.CONDITION))
            mBinding.tvProductCondition.setText(getString(R.string.condition) + ": " + getProductCondition(Integer.valueOf(parms.get(AppConstants.KEY_CONSTENT.CONDITION).toString())));
        if (parms.containsKey(AppConstants.KEY_CONSTENT.DESCRIPTION))
            mBinding.tvProductDescription.setText(parms.get(AppConstants.KEY_CONSTENT.DESCRIPTION).toString());
        if (parms.containsKey(AppConstants.KEY_CONSTENT.IS_NEGOTIABLE)) {
            boolean isNegotiable = (boolean) parms.get(AppConstants.KEY_CONSTENT.IS_NEGOTIABLE);
            if (isNegotiable) {
                mBinding.tvNegotiable.setVisibility(View.VISIBLE);
            } else {
                mBinding.tvNegotiable.setVisibility(View.GONE);

            }
        }
//        mBinding.tvUserLocation.setText(parms.get(AppConstants.KEY_CONSTENT.LOCATION).toString());
        mBinding.includeHeader.tvTitle.setText(parms.get(AppConstants.KEY_CONSTENT.TITLE).toString());
        String arr = (String) parms.get(AppConstants.KEY_CONSTENT.SHIPPING_AVAILIBILITY);
        String tempStr[] = arr.split(",");
        mBinding.tvDeliveryType.setText(setDevliveryType(tempStr));
        setPager(imageList);
        if (parms.containsKey(AppConstants.KEY_CONSTENT.SHARED_COMMUNITIES)) {
            HashMap<String, TagData> mList = (HashMap<String, TagData>) parms.get(AppConstants.KEY_CONSTENT.SHARED_COMMUNITIES);
            if (mList != null && mList.size() > 0) {
                mSharedTagsList.addAll(mList.values());
//                String[] sharedTagId = new String[mSharedTagsList.size()];
                String sharedTagId = "";
                String str = "";
                for (int i = 0; i < mSharedTagsList.size(); i++) {
//                    sharedTagId[i] = mSharedTagsList.get(i).getTagId().toString();
                    if (i == 0) {
                        sharedTagId = mSharedTagsList.get(i).getTagId().toString();
                        str = "- " + mSharedTagsList.get(i).getTagName();
                    } else {
                        sharedTagId = sharedTagId + "," + mSharedTagsList.get(i).getTagId().toString();
                        str = str + "\n- " + mSharedTagsList.get(i).getTagName().toString();
                    }
                }
                mBinding.tvShareTagsName.setText(str);
                dataHashMap.put(AppConstants.KEY_CONSTENT.SHARED_COMMUNITIES, sharedTagId);
            } else {
                mBinding.tvTxtSharedTag.setVisibility(View.GONE);
            }
        } else {
            mBinding.tvTxtSharedTag.setVisibility(View.GONE);
        }
    }


    private String setDevliveryType(String[] shippingType) {
        String shipingType = "";
        int count = shippingType.length;
        for (int i = 0; i < shippingType.length; i++) {
            if (i == 0) {
                shipingType = getShippingType(shippingType[i], count);
            } else {
                shipingType = shipingType + " , " + getShippingType(shippingType[i], count);
            }
        }
        return getString(R.string.availabilty) + " " + shipingType;
    }

    private String getShippingType(String shipingType, int shippingTypeCount) {
        String shhipingType = "";
        switch (shipingType) {
            case "1":
                if (shippingTypeCount > 1) {
                    shhipingType = "Pick-Up";

                } else {
                    shhipingType = "Pick-Up Only";
                }
                break;
            case "2":
                shhipingType = getString(R.string.deliver);
                break;
            case "3":
                shhipingType = getString(R.string.shipping);
                break;
        }
        return shhipingType;
    }


    private void setMarker(HashMap<String, Object> productDetailsModel) {
        Double lat = (Double) productDetailsModel.get(AppConstants.KEY_CONSTENT.LAT);
        Double longi = (Double) productDetailsModel.get(AppConstants.KEY_CONSTENT.LONGI);
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(lat, longi));
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin));
        if (map != null) {
            map.getUiSettings().setAllGesturesEnabled(false);
            map.addCircle(drawCircle(new LatLng(lat, longi)));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), 12));
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


    private void setPager(ArrayList<ImageList> imageList) {
        mBinding.vpImages.setAdapter(new SlidingImage_Adapter(mActivity, imageList, true));
        mBinding.circleIndicator.setViewPager(mBinding.vpImages);
        if (imageList.size() == 1) {
            mBinding.circleIndicator.setVisibility(View.GONE);
        } else if (imageList.size() > 1) {
            mBinding.circleIndicator.setVisibility(View.VISIBLE);
        }
    }

    private String getProductCondition(int condition) {
        String productCondition = "";
        switch (condition) {
            case 1:
                productCondition = getString(R.string.new_never_used);
                break;
            case 2:
                productCondition = getString(R.string.like_new_rarely_used);
                break;
            case 3:
                productCondition = getString(R.string.good_gently_used);
                break;
            case 4:
                productCondition = getString(R.string.normal_normal_wear);
                break;
            case 5:
                productCondition = getString(R.string.flawed_with_flaw);
                break;
        }

        return productCondition;
    }

    private void initView() {
        mActivity = getActivity();
        getArgumentsData();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        gpsTracker = new GPSTracker(mActivity);
        mBinding.includeHeader.ivBack.setOnClickListener(this);
        mBinding.tvPost.setOnClickListener(this);
        mBinding.includeHeader.ivReport.setVisibility(View.GONE);
        mBinding.includeHeader.ivShare.setVisibility(View.GONE);
        doUpload();
        mBinding.nestedScrol.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
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


    private void setTintHeader(int p) {
        mBinding.includeHeader.ivBack.setColorFilter(ContextCompat.getColor(mActivity, p), android.graphics.PorterDuff.Mode.SRC_IN);
        mBinding.includeHeader.ivShare.setColorFilter(ContextCompat.getColor(mActivity, p), android.graphics.PorterDuff.Mode.SRC_IN);
        mBinding.includeHeader.ivReport.setColorFilter(ContextCompat.getColor(mActivity, p), android.graphics.PorterDuff.Mode.SRC_IN);
        mBinding.includeHeader.tvTitle.setTextColor(ContextCompat.getColor(mActivity, p));
    }

    private void getArgumentsData() {
        imageList = new ArrayList<>();
        mImageList = new ArrayList<>();
        if (getArguments() != null) {
            dataHashMap = (HashMap<String, Object>) getArguments().getSerializable("ADD_PRODUCT_DATA");
            imageList = getArguments().getParcelableArrayList("IMAGES");
            if (dataHashMap != null && dataHashMap.size() > 0) {
                setData(dataHashMap);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                mActivity.finish();
                break;
            case R.id.tv_post:
                getLoadingStateObserver().onChanged(true);
                for (int i = 0; i < imageList.size(); i++)
                    startUpload(imageList.get(i).getUrl(), i);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (dataHashMap != null) {
            setMarker(dataHashMap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 201) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (map != null && dataHashMap != null) {
                    setMarker(dataHashMap);
                }

            }
        }
    }

    // Initilize the Amazon S3
    public void doUpload() {

        mAmazonS3 = mAmazonS3.getInstance(mActivity, this, AppConstants.AMAZON_S3.AMAZON_POOLID, AppConstants.AMAZON_S3.BUCKET, AppConstants.AMAZON_S3.AMAZON_SERVER_URL, AppConstants.AMAZON_S3.END_POINT);
    }

    private ImageBean addDataInBean(String path, int i) {
        ImageBean bean = new ImageBean();
        bean.setId("1");
        bean.setPosition(i);
        bean.setName("sample");
        bean.setImagePath(path);
        return bean;
    }

    // Start Uploading image to Amazon Server
    private void startUpload(String path, int position) {

        ImageBean bean = addDataInBean(path, position);

        mAmazonS3.uploadImage(bean);
    }

    @Override
    public void uploadSuccess(ImageBean bean) {
        imageUploadCount++;
        ImageList imageList = new ImageList();
        imageList.setUrl(bean.getServerUrl());
        imageList.setThumbUrl(bean.getServerUrl());
        imageList.setPosition(bean.getPosition());
        mImageList.add(imageList);
        if (imageUploadCount == this.imageList.size()) {
            sortImageList();
            ImageList[] imageLists = new ImageList[imageUploadCount];
            for (int i = 0; i < mImageList.size(); i++) {
                imageLists[i] = mImageList.get(i);
            }
            dataHashMap.put(AppConstants.KEY_CONSTENT.IMAGES, mProductDetailsViewModel.getJson(mImageList));
            if (AppUtils.isInternetAvailable(mActivity)) {
                mProductDetailsViewModel.addProductRequest(dataHashMap);
            } else showNoNetworkError();
        }

    }


    @Override
    public void uploadFailed(ImageBean bean) {
        imageUploadCount++;
        if (imageUploadCount == this.imageList.size()) {
            getLoadingStateObserver().onChanged(false);
        }
    }

    @Override
    public void uploadProgress(ImageBean bean) {
    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {

    }

    private void sortImageList() {
        Collections.sort(mImageList, new ImageSorting());
    }

}
