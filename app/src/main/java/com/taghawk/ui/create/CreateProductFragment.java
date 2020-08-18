package com.taghawk.ui.create;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taghawk.R;
import com.taghawk.adapters.SharedTagAdapter;
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
import com.taghawk.model.AddProduct.ChooseWeightModel;
import com.taghawk.model.AddProduct.ImageBean;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.category.CategoryListResponse;
import com.taghawk.model.category.CategoryResponse;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tag.UserSpecificTagsModel;
import com.taghawk.ui.home.filter.ChoosseLocationActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.PermissionUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateProductFragment extends BaseFragment implements View.OnClickListener {

    LayoutAddProductBinding mBinding;
    AddProductAdapter addProductAdapter;
    private AddProductViewModel mAddProductViewModel;
    private int shippingType;
    private Address location;
    private Activity mActiviy;
    private HashMap<String, Object> dataHashMap;
    private int productConditionId;
    private String productCategoryId;
    private ArrayList<CategoryListResponse> mCategoryList;
    private ArrayList<TagData> mTagsList;
    private SharedTagAdapter adapter;
    private HashMap<Integer, TagData> sharedTagsMap;
    private GPSTracker gpsTracker;
    private ImageBean imageBean;
    private ArrayList<String> mFileArrayList;
    private int tvShipping, tvDelivery, tvPickup;
    private ChooseWeightModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = LayoutAddProductBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mFileArrayList = new ArrayList<>();
//        mBinding.header.ivCross.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.txt_black));
        mBinding.ivAddProduct.setOnClickListener(this);
        mActiviy = getActivity();
        sharedTagsMap = new HashMap<>();
        mBinding.header.ivCross.setOnClickListener(this);
        mBinding.header.tvTitle.setText(getString(R.string.add_product));
        mBinding.tvProductCategory.setOnClickListener(this);
        mBinding.etProductCondition.setOnClickListener(this);
        mBinding.etProductLocation.setOnClickListener(this);
        mBinding.ivTransactionInfo.setOnClickListener(this);
        mBinding.tvChooseWeight.setOnClickListener(this);
        mBinding.tvPickup.setOnClickListener(this);
        mBinding.tvDeliver.setOnClickListener(this);
        mBinding.tvShipping.setOnClickListener(this);
        mBinding.ivCurrentLocation.setOnClickListener(this);
        mBinding.ivAddProduct.setOnClickListener(this);
        mBinding.tvPreview.setOnClickListener(this);
//        mBinding.tvViewAll.setOnClickListener(this);
        mBinding.tvShipping.setSelected(true);
        mBinding.tvPickup.setSelected(true);
        mBinding.tvDeliver.setSelected(true);
        if (PermissionUtility.isPermissionGranted(mActiviy, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {
            fetchCurrentLocation();
        }

        setupSharedTag();
        setCameraList();
        mBinding.etProductPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBinding.etProductPrice.getText().toString().trim().length() > 0) {
                    calculateServiceCharge(mBinding.etProductPrice.getText().toString().trim());

                    String priceString = mBinding.etProductPrice.getText().toString().trim();
                    if(priceString.length() == 1){
                       priceString =  priceString.replace(".", "0");
                    }


                    double price = Double.parseDouble(priceString);
                    if(price < .80 && price != 0){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(R.string.price_should_grater_then_zeo)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mBinding.etProductPrice.setText("");
                                    }
                                });
                        // Create the AlertDialog object and return it
                        builder.create();
                        builder.show();
                    }
                }
                else {
                    calculateServiceCharge("0");
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//        mBinding.etProductDescription.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.d("before", s.toString());
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!s.toString().matches("^[a-zA-Z0-9]*$")) {
//                    mBinding.etProductDescription.setText("");
//                }
//            }
//        });
    }

    //This function is use for calculate service charge amount
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

    //initialize Recycler view of camera
    private void setCameraList() {
        mBinding.rvAddProductImages.setLayoutManager(new LinearLayoutManager(mActiviy, RecyclerView.HORIZONTAL, false));
        addProductAdapter = new AddProductAdapter(mActiviy, mFileArrayList, new RecyclerListener() {
            @Override
            public void onItemClick(View v, int position, String number, boolean flag) {
                try {
                    mFileArrayList.remove(position);
                    addProductAdapter.notifyItemRemoved(position);
                } catch (Exception e) {
                }

            }
        });
        mBinding.rvAddProductImages.setAdapter(addProductAdapter);
        getArgumentsdata();
    }

    //get data from argument
    private void getArgumentsdata() {
        if (getArguments() != null) {
            ArrayList<String> mTemList = (ArrayList<String>) getArguments().getSerializable(AppConstants.BUNDLE_DATA);
            if (mTemList != null && mTemList.size() > 0) {
                mFileArrayList.addAll(mTemList);
                addProductAdapter.notifyDataSetChanged();
            }
        }
    }

    // initialize recyclerview list for shared Tags
    private void setupSharedTag() {
        mTagsList = new ArrayList<>();
        final GridLayoutManager layoutManager = new GridLayoutManager(mActiviy, 3);
        mBinding.rvSharedTag.setLayoutManager(layoutManager);
        adapter = new SharedTagAdapter(mActiviy, mTagsList, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView != null) {
                    final int position = Integer.valueOf(buttonView.getTag().toString());
                    if (isChecked) {
                        mTagsList.get(position).setSelected(true);
                        sharedTagsMap.put(position, mTagsList.get(position));
                    } else {
                        mTagsList.get(position).setSelected(false);
                        if (sharedTagsMap.size() > 0)
                            sharedTagsMap.remove(position);
                    }
                    mBinding.rvSharedTag.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(position);
                        }
                    });
                }
            }
        });
        mBinding.rvSharedTag.setAdapter(adapter);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCategoryList = new ArrayList<>();
        mAddProductViewModel = ViewModelProviders.of(this).get(AddProductViewModel.class);
        mAddProductViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mAddProductViewModel.getmValidateData().observe(this, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(@Nullable HashMap<String, Object> dataMap) {
                openPreviewScreen(dataMap);
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
        mAddProductViewModel.getmUSerSpecificTags().observe(this, new Observer<UserSpecificTagsModel>() {
            @Override
            public void onChanged(@Nullable UserSpecificTagsModel userSpecificTagsModel) {
                mTagsList.addAll(userSpecificTagsModel.getUserTagInfo().getmTagListData());
                if (mTagsList != null && mTagsList.size() > 0) {
                    adapter.notifyDataSetChanged();
                    mBinding.rvSharedTag.setVisibility(View.VISIBLE);
                    mBinding.tvSharedTag.setVisibility(View.VISIBLE);
                } else {
                    mBinding.rvSharedTag.setVisibility(View.GONE);
                    mBinding.tvSharedTag.setVisibility(View.GONE);
                }
            }
        });
        if (AppUtils.isInternetAvailable(mActiviy)) {
            mAddProductViewModel.hitGetCategory(false);
            mAddProductViewModel.getUserSpecificTags(100, 1);
        } else showNoNetworkError();
    }

    private void openPreviewScreen(HashMap<String, Object> dataMap) {
        Intent intent = new Intent(mActiviy, ProductPreviewActivity.class);
        convertImageInImageBean();
        intent.putExtra("ADD_PRODUCT_DATA", dataMap);
        intent.putParcelableArrayListExtra("IMAGES", imageBean.getImageList());
        startActivity(intent);
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
                    if (mFileArrayList != null && mFileArrayList.size() < 10) {
                        Intent intent = new Intent(mActiviy, CameraTwoActivity.class);
//                        intent.putExtra("MAX_IMAGES", 10 - mFileArrayList.size());
                        if (mFileArrayList != null && mFileArrayList.size() > 0)
                            intent.putExtra("PREVIOUS_LIST", mFileArrayList);
                        startActivityForResult(intent, AppConstants.REQUEST_CODE.CAMERA_ACTIVITY);
                    } else {
                        Toast.makeText(mActiviy, getString(R.string.max_camera_image_picking), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.iv_cross:
                ((AddProductActivity) getContext()).finish();
                break;
            case R.id.tv_preview:
                if (AppUtils.isInternetAvailable(mActiviy)) {
                    mAddProductViewModel.addProductRequest(mBinding.etTitle.getText().toString().trim(), mBinding.tvProductCategory.getText().toString().trim(), productCategoryId, mBinding.etProductPrice.getText().toString().trim(), productConditionId, mBinding.etProductDescription.getText().toString().trim(), shippingType, location, mBinding.tbFirmPrice.isChecked(), sharedTagsMap, mFileArrayList, tvDelivery, tvPickup, tvShipping, true, mBinding.rbFedex.isChecked(), mBinding.rbUsps.isChecked(), model,state,city);
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
                    mAddProductViewModel.hitGetCategory(true);
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
            case R.id.tv_choose_weight:
                new CustomChooseWeightDialog(mActiviy, new OnDialogItemObjectClickListener() {
                    @Override
                    public void onPositiveBtnClick(Object object) {
                        setWeightDetails((ChooseWeightModel) object);
                    }
                }).show();
                break;
        }
    }

    private void setWeightDetails(ChooseWeightModel object) {
        model = object;
        mBinding.tvChooseWeight.setText(model.getWeight());
        mBinding.rbFedex.setText(getString(R.string.fedex) + "                    " + "$" + model.getFedexPrice());
        mBinding.rbFedex.setEnabled(true);
        mBinding.rbUsps.setEnabled(true);
        mBinding.rbFedex.setTextColor(getResources().getColor(R.color.txt_black));
        mBinding.rbUsps.setTextColor(getResources().getColor(R.color.txt_black));

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
    String state = "";
    String city = "";
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
                        state = location.getAdminArea();
                        city = location.getLocality();

                        Log.d("hdgfa",location.getAdminArea()+","+location.getCountryName()+
                                ","+location.getFeatureName()+","+location.getLocality()+","+location.getLocale()
                        );
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
                    if (mFileArrayList != null && mFileArrayList.size() > 0) {
                        mFileArrayList.clear();
                    }
                    mFileArrayList.addAll(data.getExtras().getStringArrayList("images"));
                    if (mFileArrayList.size() > 0) {
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

    /*
        private void setCurrentLocationText() {
            try {
                if (AppUtils.getAddressByLatLng(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0) != null) {
                    location = AppUtils.getAddressByLatLng(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                    Address address = AppUtils.getAddressByLatLng1(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0);
                    mBinding.etProductLocation.setText(AppUtils.getAddressByLatLng1(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude()));
                } else {
                    showToastLong(getString(R.string.unable_to_fatch_your_location));
                }
            } catch (Exception E) {
            }
        }*/
    private void setCurrentLocationText() {
        try {
            if (AppUtils.getAddressByLatLng(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0) != null) {
                location = AppUtils.getAddressByLatLng(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                String address = AppUtils.getAddressByLatLng(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0);
                mBinding.etProductLocation.setText(address);
                city = AppUtils.getAddressByLatLng1(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                state = AppUtils.getAddressByLatLng2(mActiviy, gpsTracker.getLatitude(), gpsTracker.getLongitude());
            } else {
                showToastLong(getString(R.string.unable_to_fatch_your_location));
            }
        } catch (Exception E) {
        }
    }

}
