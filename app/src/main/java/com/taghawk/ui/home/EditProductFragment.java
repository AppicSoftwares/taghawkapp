package com.taghawk.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.camera2basic.AddProductAdapter;
import com.taghawk.camera2basic.CameraTwoActivity;
import com.taghawk.camera2basic.RecyclerListener;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.CustomChooseWeightDialog;
import com.taghawk.custom_dialog.CustomProductCategoryDialog;
import com.taghawk.custom_dialog.CustomProductCondition;
import com.taghawk.databinding.LayoutAddProductBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.interfaces.OnDialogItemObjectClickListener;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.AddProduct.AddProductModel;
import com.taghawk.model.AddProduct.ChooseWeightModel;
import com.taghawk.model.AddProduct.ImageBean;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.category.CategoryListResponse;
import com.taghawk.model.category.CategoryResponse;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.ui.create.AddProductViewModel;
import com.taghawk.ui.home.filter.ChoosseLocationActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.ImageSorting;
import com.taghawk.util.PermissionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class EditProductFragment extends BaseFragment implements View.OnClickListener, AmazonCallback {

    private LayoutAddProductBinding mBinding;
    private AddProductViewModel mAddProductViewModel;
    private int shippingType, tvPickup, tvDelivery, tvShipping;
    private Address location;
    private Activity mActiviy;
    private int productConditionId;
    private String productCategoryId;
    private ArrayList<CategoryListResponse> mCategoryList;
    private GPSTracker gpsTracker;
    private ImageBean imageBean;
    private ArrayList<String> mFileArrayList, mTempList;
    private AddProductAdapter addProductAdapter;
    private ProductDetailsData mData;
    private AmazonS3 mAmazonS3;
    private int imageUploadCount;
    private ArrayList<ImageList> mImageList;
    private HashMap<String, Object> mDataMap;
    private ChooseWeightModel model;
    private ArrayList<ChooseWeightModel> weightList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = LayoutAddProductBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mActiviy = getActivity();
        mDataMap = new HashMap<>();
        mFileArrayList = new ArrayList<>();
        mCategoryList = new ArrayList<>();
        mTempList = new ArrayList<>();
        mImageList = new ArrayList<>();
        weightList = new ArrayList<>();
        mBinding.header.tvTitle.setText(getString(R.string.edit_product));
        mBinding.tvPreview.setText(getString(R.string.update));
        mBinding.rvSharedTag.setVisibility(View.GONE);
        mBinding.tvSharedTag.setVisibility(View.GONE);
        mBinding.tvShipping.setSelected(true);
        mBinding.tvPickup.setSelected(true);
        mBinding.tvDeliver.setSelected(true);
        weightList.addAll(loadJSONFromAsset());
        doUpload();
        setLisener();
        setCameraList();
        getBundleData();
        mBinding.etProductPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBinding.etProductPrice.getText().toString().trim().length() > 0)
                    calculateServiceCharge(mBinding.etProductPrice.getText().toString().trim());
                else {
                    calculateServiceCharge("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getBundleData() {
        if (getArguments() != null) {
            mData = (ProductDetailsData) getArguments().get(AppConstants.BUNDLE_DATA);
            if (mData != null) {
                setData(mData);
            }
        }
    }

    private void setData(ProductDetailsData data) {
        productConditionId = data.getCondition();
        productCategoryId = data.getProductCategoryId();
        location = AppUtils.getAddressByLatLng(mActiviy, data.getProductLatitude(), data.getProductLongitude());
        mBinding.etTitle.setText(data.getTitle());
        mBinding.tvProductCategory.setText(data.getCategoryName());
        mBinding.etProductPrice.setText(data.getFirmPrice());
        calculateServiceCharge(data.getFirmPrice());
        mBinding.tbFirmPrice.setChecked(data.getNegotiable());
        mBinding.etProductCondition.setText(AppUtils.getProductCondition(mActiviy, data.getCondition()));
        mBinding.etProductDescription.setText(data.getDescription());
        mBinding.etProductLocation.setText(data.getProductAddress());
//        mBinding.tbTransactionFee.setChecked(data.isTransactionCost());
        getImageUrl(data.getImageList());
        setShippingData(data, data.getShippingType());
    }

    private void setShippingData(ProductDetailsData data, int[] shippingType) {
        for (int i = 0; i < shippingType.length; i++) {
            if (shippingType[i] == 1) {
                tvPickup = 1;
                mBinding.tvPickup.setSelected(false);
                mBinding.tvPickup.setTextColor(mActiviy.getResources().getColor(R.color.White));
                mBinding.tvPickup.setBackgroundDrawable(mActiviy.getResources().getDrawable(R.drawable.edit_field_filled_drawable));
            } else if (shippingType[i] == 2) {
                tvDelivery = 2;
                mBinding.tvDeliver.setSelected(false);

                mBinding.tvDeliver.setTextColor(mActiviy.getResources().getColor(R.color.White));
                mBinding.tvDeliver.setBackgroundDrawable(mActiviy.getResources().getDrawable(R.drawable.edit_field_filled_drawable));

            } else if (shippingType[i] == 3) {
                tvShipping = 3;
                mBinding.tvShipping.setSelected(false);
                mBinding.tvShipping.setTextColor(mActiviy.getResources().getColor(R.color.White));
                mBinding.tvShipping.setBackgroundDrawable(mActiviy.getResources().getDrawable(R.drawable.edit_field_filled_drawable));
                mBinding.llAddProductWeight.setVisibility(View.VISIBLE);
                getitemFromList(data.getWeight());
                if (data.getShippingModeType().equalsIgnoreCase(AppConstants.FEDEX)) {
                    mBinding.rbFedex.setChecked(true);
                } else {
                    mBinding.rbUsps.setChecked(true);

                }
            }
        }
    }

    private void getitemFromList(String weight) {
        String str = weight;
        for (int i = 0; i < weightList.size(); i++) {
            if (weightList.get(i).getWeight().equalsIgnoreCase(weight)) {
                model = weightList.get(i);
                setWeightDetails(model);
                return;
            }
        }
    }

    private void getImageUrl(ArrayList<ImageList> imageList) {
        for (int i = 0; i < imageList.size(); i++) {
            mFileArrayList.add(imageList.get(i).getUrl());
            mTempList.add(imageList.get(i).getUrl());
        }
        addProductAdapter.notifyDataSetChanged();
    }


    private String getCategoryText(String productCategoryId) {
        for (int i = 0; i < mCategoryList.size(); i++) {
            if (mCategoryList.get(i).equals(productCategoryId)) {
                return mCategoryList.get(i).getName();
            }
        }
        return "";
    }

    private void setLisener() {
        mBinding.ivAddProduct.setOnClickListener(this);
        mBinding.header.ivCross.setOnClickListener(this);
        mBinding.tvProductCategory.setOnClickListener(this);
        mBinding.etProductCondition.setOnClickListener(this);
        mBinding.etProductLocation.setOnClickListener(this);
        mBinding.tvPickup.setOnClickListener(this);
        mBinding.tvDeliver.setOnClickListener(this);
        mBinding.tvShipping.setOnClickListener(this);
        mBinding.ivCurrentLocation.setOnClickListener(this);
        mBinding.ivAddProduct.setOnClickListener(this);
        mBinding.tvPreview.setOnClickListener(this);
        mBinding.tvChooseWeight.setOnClickListener(this);
        mBinding.ivTransactionInfo.setOnClickListener(this);

    }

    @SuppressLint("WrongConstant")
    private void setCameraList() {
        mBinding.rvAddProductImages.setLayoutManager(new LinearLayoutManager(mActiviy, LinearLayout.HORIZONTAL, false));
        addProductAdapter = new AddProductAdapter(mActiviy, mFileArrayList, new RecyclerListener() {
            @Override
            public void onItemClick(View v, int position, String number, boolean flag) {
                mFileArrayList.remove(position);
                addProductAdapter.notifyItemRemoved(position);
                if (mTempList.size() > 0 && mTempList.size() >= position) {
                    mTempList.remove(position);
                }
            }
        });
        mBinding.rvAddProductImages.setAdapter(addProductAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCategoryList = new ArrayList<>();
        mAddProductViewModel = ViewModelProviders.of(this).get(AddProductViewModel.class);
        mAddProductViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mAddProductViewModel.getEditLiveData().observe(this, new Observer<AddProductModel>() {
            @Override
            public void onChanged(@Nullable AddProductModel addProductModel) {
                getLoadingStateObserver().onChanged(false);
                if (addProductModel.getCode() == 200) {
                    showToastShort(addProductModel.getMessage());
                    mActiviy.setResult(Activity.RESULT_OK);
                    mActiviy.finish();
                }
            }
        });
        mAddProductViewModel.getmValidateData().observe(this, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(@Nullable HashMap<String, Object> parms) {
                mDataMap = parms;
                if (mTempList.size() == mFileArrayList.size()) {
                    mAddProductViewModel.updateProduct(parms);
                } else {
                    for (int i = mTempList.size(); i < mFileArrayList.size(); i++)
                        startUpload(mFileArrayList.get(i), i);
                }
            }
        });
        mAddProductViewModel.getCategoryListViewModel().observe(this, new Observer<CategoryResponse>() {
            @Override
            public void onChanged(@Nullable CategoryResponse categoryResponse) {
                getLoadingStateObserver().onChanged(false);
                if (categoryResponse.getCode() == 200) {
                    if (categoryResponse.getmCategory() != null)
                        mCategoryList.addAll(categoryResponse.getmCategory());
                    if (categoryResponse.isShowDialog()) {
                        showCategoryDialog();
                    }
                }
            }
        });
        if (AppUtils.isInternetAvailable(mActiviy)) {
            mAddProductViewModel.hitGetCategory(false);
        } else showNoNetworkError();
    }


    private void convertImageInImageBean() {
        imageBean = new ImageBean();
        ArrayList<ImageList> imList = new ArrayList<>();
        for (int i = 0; i < mFileArrayList.size(); i++) {
            ImageList image = new ImageList();
            image.setThumbUrl(mFileArrayList.get(i));
            image.setUrl(mFileArrayList.get(i));
            imList.add(image);
        }
        imageBean.setImageList(imList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_product:
                if (PermissionUtility.isPermissionGranted(mActiviy, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, AppConstants.ACTIVITY_RESULT.CAMERA_PERMISSION)) {
                    Intent intent = new Intent(mActiviy, CameraTwoActivity.class);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.CAMERA_ACTIVITY);
                }
                break;
            case R.id.iv_cross:
                mActiviy.finish();
                break;

            case R.id.tv_preview:
                if (AppUtils.isInternetAvailable(mActiviy)) {
                    getLoadingStateObserver().onChanged(true);

                    mAddProductViewModel.editProduct(mBinding.etTitle.getText().toString().trim(), mBinding.tvProductCategory.getText().toString().trim(), productCategoryId, mBinding.etProductPrice.getText().toString().trim(), productConditionId, mBinding.etProductDescription.getText().toString().trim(), location, mBinding.tbFirmPrice.isChecked(), mTempList, mData.getProductId(), tvPickup, tvDelivery, tvShipping, null, true, mBinding.rbFedex.isChecked(), mBinding.rbUsps.isChecked(), model);
                } else showNoNetworkError();
                break;
            case R.id.et_product_condition:
                new CustomProductCondition(mActiviy, new OnDialogViewClickListener() {
                    @Override
                    public void onSubmit(String txt, int id) {
                        mBinding.etProductCondition.setText(txt);
                        productConditionId = id;
                    }
                }).show();
                break;
            case R.id.tv_product_category:
                if (mCategoryList != null && mCategoryList.size() > 0) {
                    showCategoryDialog();
                } else {
                    if (AppUtils.isInternetAvailable(mActiviy)) {
                        mAddProductViewModel.hitGetCategory(true);
                    } else showNoNetworkError();
                }
                break;
            case R.id.tv_pickup:
                if (!mBinding.tvPickup.isSelected()) {
                    setPickupData(0, true, R.color.txt_black, R.drawable.edit_field_drawable);
                } else {
                    setPickupData(1, false, R.color.White, R.drawable.edit_field_filled_drawable);
                }
                break;
            case R.id.tv_deliver:
                if (!mBinding.tvDeliver.isSelected()) {
                    setDeliveryData(0, true, R.color.txt_black, R.drawable.edit_field_drawable);
                } else {
                    setDeliveryData(2, false, R.color.White, R.drawable.edit_field_filled_drawable);
                }
                break;
            case R.id.tv_shipping:
                if (!mBinding.tvShipping.isSelected()) {
                    setShippingData(0, true, R.color.txt_black, R.drawable.edit_field_drawable);
                } else {
                    setShippingData(3, false, R.color.White, R.drawable.edit_field_filled_drawable);
                }
                break;
            case R.id.et_product_location:
                Intent in = new Intent(mActiviy, ChoosseLocationActivity.class);
                startActivityForResult(in, AppConstants.ACTIVITY_RESULT.SEACH_LOACTION);
                break;
            case R.id.iv_current_location:
                if (PermissionUtility.isPermissionGranted(mActiviy, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {
                    fetchCurrentLocation();
                }
                break;
            case R.id.tv_choose_weight:
                new CustomChooseWeightDialog(mActiviy, new OnDialogItemObjectClickListener() {
                    @Override
                    public void onPositiveBtnClick(Object object) {
                        setWeightDetails((ChooseWeightModel) object);
                    }
                }).show();
                break;
            case R.id.iv_transaction_info:
                getCustomBottomDialog(mActiviy.getString(R.string.info), getString(R.string.transaction_cost_msg), new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {
                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });
                break;
        }
    }

    private void setShippingData(int i, boolean b, int p, int p2) {
        tvShipping = i;
        mBinding.tvShipping.setSelected(b);
        mBinding.tvShipping.setTextColor(mActiviy.getResources().getColor(p));
        mBinding.tvShipping.setBackgroundDrawable(mActiviy.getResources().getDrawable(p2));
        if (!b) {
            mBinding.llAddProductWeight.setVisibility(View.VISIBLE);
        } else {
            mBinding.llAddProductWeight.setVisibility(View.GONE);

        }
    }

    private void setDeliveryData(int i, boolean b, int p, int p2) {
        tvDelivery = i;
        mBinding.tvDeliver.setSelected(b);
        mBinding.tvDeliver.setTextColor(mActiviy.getResources().getColor(p));
        mBinding.tvDeliver.setBackgroundDrawable(mActiviy.getResources().getDrawable(p2));
    }

    private void setPickupData(int i, boolean b, int p, int p2) {
        tvPickup = i;
        mBinding.tvPickup.setSelected(b);
        mBinding.tvPickup.setTextColor(mActiviy.getResources().getColor(p));
        mBinding.tvPickup.setBackgroundDrawable(mActiviy.getResources().getDrawable(p2));
    }


    private void showCategoryDialog() {
        new CustomProductCategoryDialog(mActiviy, mCategoryList, new OnDialogItemObjectClickListener() {
            @Override
            public void onPositiveBtnClick(Object object) {
                CategoryListResponse response = (CategoryListResponse) object;
                if (response != null) {
                    mBinding.tvProductCategory.setText(response.getName());
                    productCategoryId = response.getId();
                }
            }
        }).show();
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        showToastShort(failureResponse.getErrorMessage());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.SEACH_LOACTION:
                if (data != null) {
                    String locAdd = data.getExtras().getString("Location");
                    location = AppUtils.getLocationFromAddress(mActiviy, locAdd);
                    if (location != null) {
                        mBinding.etProductLocation.setText(location.getAddressLine(0));
                    }
                }
                break;
            case AppConstants.ACTIVITY_RESULT.GPS_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        new Thread().sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (gpsTracker.getLocation() != null) {
                        setCurrentLocationText();
                    }
                }
                break;
            case AppConstants.REQUEST_CODE.CAMERA_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    mFileArrayList.addAll(data.getExtras().getStringArrayList("images"));
                    if (mFileArrayList != null && mFileArrayList.size() > 0) {
                        addProductAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchCurrentLocation();
                }
                break;
            case AppConstants.ACTIVITY_RESULT.CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(mActiviy, CameraTwoActivity.class);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.CAMERA_ACTIVITY);
                }
                break;
        }
    }

    private void fetchCurrentLocation() {
        if (gpsTracker == null)
            gpsTracker = new GPSTracker(mActiviy);
        if (gpsTracker.isGPSEnable()) {
            setCurrentLocationText();
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void setCurrentLocationText() {
        try {
            if (AppUtils.getAddressByLatLng(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0) != null) {
                location = AppUtils.getAddressByLatLng(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                mBinding.etProductLocation.setText(AppUtils.getAddressByLatLng(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0));
            } else {
                showToastLong(getString(R.string.unable_to_fatch_your_location));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Initilize the Amazon S3
    public void doUpload() {

        mAmazonS3 = AmazonS3.getInstance(mActiviy, this, AppConstants.AMAZON_S3.AMAZON_POOLID, AppConstants.AMAZON_S3.BUCKET, AppConstants.AMAZON_S3.AMAZON_SERVER_URL, AppConstants.AMAZON_S3.END_POINT);
    }

    private com.dnitinverma.amazons3library.model.ImageBean addDataInBean(String path, int position) {
        com.dnitinverma.amazons3library.model.ImageBean bean = new com.dnitinverma.amazons3library.model.ImageBean();
        bean.setId("1");
        bean.setName("sample");
        bean.setPosition(position);
        bean.setImagePath(path);
        return bean;
    }

    // Start Uploading image to Amazon Server
    private void startUpload(String path, int position) {

        com.dnitinverma.amazons3library.model.ImageBean bean = addDataInBean(path, position);
        mAmazonS3.uploadImage(bean);
    }

    @Override
    public void uploadSuccess(com.dnitinverma.amazons3library.model.ImageBean bean) {
        imageUploadCount++;
        ImageList imageList = new ImageList();
        imageList.setUrl(bean.getServerUrl());
        imageList.setThumbUrl(bean.getServerUrl());
        mImageList.add(imageList);
        if (imageUploadCount == mFileArrayList.size() - mTempList.size()) {
            sortImageList();
            ImageList[] imageLists = new ImageList[imageUploadCount];
            for (int i = 0; i < mImageList.size(); i++) {
                imageLists[i] = mImageList.get(i);
            }
            mAddProductViewModel.editProduct(mBinding.etTitle.getText().toString(), mBinding.tvProductCategory.getText().toString(), productCategoryId, mBinding.etProductPrice.getText().toString(), productConditionId, mBinding.etProductDescription.getText().toString(), location, mBinding.tbFirmPrice.isChecked(), mTempList, mData.getProductId(), tvPickup, tvDelivery, tvShipping, imageLists, true, mBinding.rbFedex.isChecked(), mBinding.rbUsps.isChecked(), model);
        }
    }

    private void sortImageList() {
        Collections.sort(mImageList, new ImageSorting());
    }

    @Override
    public void uploadFailed(com.dnitinverma.amazons3library.model.ImageBean bean) {
        imageUploadCount++;
        if (imageUploadCount == mFileArrayList.size() - mTempList.size()) {
            getLoadingStateObserver().onChanged(false);
        }
    }

    @Override
    public void uploadProgress(com.dnitinverma.amazons3library.model.ImageBean bean) {
    }

    @Override
    public void uploadError(Exception e, com.dnitinverma.amazons3library.model.ImageBean imageBean) {

    }

    private void setWeightDetails(ChooseWeightModel object) {
        model = object;
        mBinding.tvChooseWeight.setText(model.getWeight());
        mBinding.rbFedex.setText(getString(R.string.fedex) + "                    " + "$" + model.getFedexPrice());
//        mBinding.rbUsps.setText(getString(R.string.usps) + "                    " + "$" + model.getUspsPrice());
        mBinding.rbFedex.setEnabled(true);
//        mBinding.rbUsps.setEnabled(true);
        mBinding.rbFedex.setTextColor(getResources().getColor(R.color.txt_black));
//        mBinding.rbUsps.setTextColor(getResources().getColor(R.color.txt_black));

//        if (!model.isAvailableInFedex()) {
//            mBinding.rbFedex.setEnabled(false);
//            mBinding.rbFedex.setTextColor(getResources().getColor(R.color.txt_light_gray));
//        }
//        if (!model.isAvailableInUsps()) {
//            mBinding.rbUsps.setEnabled(false);
//            mBinding.rbUsps.setTextColor(getResources().getColor(R.color.txt_light_gray));
//
//        }
    }

    private ArrayList<ChooseWeightModel> loadJSONFromAsset() {
        ArrayList<ChooseWeightModel> weightList = new ArrayList<>();
        String json = null;
        try {
            InputStream is = mActiviy.getAssets().open("ShippingPrice.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        if (json != null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray m_jArry = jsonObject.getJSONArray("shippingPrirceDetails");
                for (int i = 0; i < m_jArry.length(); i++) {
                    JSONObject jo_inside = m_jArry.getJSONObject(i);
                    ChooseWeightModel weightListBean = new ChooseWeightModel();
                    String weight = jo_inside.getString("weight");
                    Double fedexPrice = jo_inside.getDouble("fedexPrice");
//                    Double uspsPrice = jo_inside.getDouble("uspsPrice");
//                    boolean isAvailableInFedex = jo_inside.getBoolean("isAvailableInFedex");
//                    boolean isAvailableInUsps = jo_inside.getBoolean("isAvailableInUsps");

                    weightListBean.setWeight(weight);
//                    weightListBean.setUspsPrice(uspsPrice);
                    weightListBean.setFedexPrice(fedexPrice);
//                    weightListBean.setAvailableInFedex(isAvailableInFedex);
//                    weightListBean.setAvailableInUsps(isAvailableInUsps);
                    weightList.add(weightListBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return weightList;
    }

    private void calculateServiceCharge(String price) {
        double totalAmount = 0.0;
        if (price != null && price.length() == 1 && price.equalsIgnoreCase(".")) {

        } else
            totalAmount = Double.valueOf(price);
        double serviceFee = 0, earnedAmount = 0;
        if (totalAmount == 0) {
            serviceFee = 0;
            earnedAmount = 0;
        } else if (totalAmount >= 0.8 && totalAmount <= 10) {
            serviceFee = 0.8;
            earnedAmount = totalAmount - serviceFee;
        } else if (totalAmount > 10) {
            serviceFee = (totalAmount * 8) / 100;
            earnedAmount = totalAmount - serviceFee;
        }
//        double roundOff =;
        mBinding.tvServiceFee.setText("$" + Math.round(serviceFee * 100.0) / 100.0);
        mBinding.tvYouEarn.setText("$" + Math.round(earnedAmount * 100.0) / 100.0);
    }

}
