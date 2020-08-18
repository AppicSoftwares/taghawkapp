package com.taghawk.ui.tag;

import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.camera2basic.CameraTwoActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.CustomTagTypeDialog;
import com.taghawk.custom_dialog.CustomTagType_2Dialog;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.AddTagStepOneBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tagaddresponse.AddTagResponse;
import com.taghawk.stripe.GooglePayPayment;
import com.taghawk.stripe.Token;
import com.taghawk.ui.home.filter.ChoosseLocationActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.PermissionUtility;

import java.util.ArrayList;

public class AddTagStepOne extends BaseFragment implements View.OnClickListener, AmazonCallback {

    private AddTagViewModel addTagViewModel;
    private AddTagStepOneBinding mBinding;
    private Activity mActivity;
    private int tagTypeId, getTagTypeIdNew;
    private int tagTypeJionedId;
    private GPSTracker gpsTracker;
    private Address location;
    private ArrayList<String> mFileArrayList;
    private AmazonS3 mAmazonS3;
    ImageList mImageList;
    private AddTagStepOne.IAddTagHost listener;
    private TagData mAddedTagDetail;
    private TagData tagData;
    private boolean isRewards;
    private String gPayId;
    private int id = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = AddTagStepOneBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewModel();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddTagStepOne.IAddTagHost)
            listener = (AddTagStepOne.IAddTagHost) context;
        else
            throw new IllegalStateException("Your Activity should implement AddTagStepOne.IAddTagHost");
    }

    // Initilize the Amazon S3
    public void doUpload() {
        mAmazonS3 = mAmazonS3.getInstance(mActivity, this, AppConstants.AMAZON_S3.AMAZON_POOLID, AppConstants.AMAZON_S3.BUCKET, AppConstants.AMAZON_S3.AMAZON_SERVER_URL, AppConstants.AMAZON_S3.END_POINT);
    }

    private void setUpViewModel() {
        addTagViewModel = ViewModelProviders.of(this).get(AddTagViewModel.class);
        addTagViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        addTagViewModel.mAddTagViewModel().observe(this, new Observer<AddTagResponse>() {
            @Override
            public void onChanged(@Nullable AddTagResponse addTagResponse) {
                getLoadingStateObserver().onChanged(false);
                showToastShort(addTagResponse.getMessage());
                if (addTagResponse.getStatusCode() == 200) {
                    Bundle bundle = new Bundle();
                    mAddedTagDetail = addTagResponse.getData();
                    addTagViewModel.addTagOnFirebase(DataManager.getInstance().getUserDetails(), mAddedTagDetail);
                    ((AddTagActivity) mActivity).setTagData(mAddedTagDetail);
                    bundle.putParcelable(AppConstants.TAG_KEY_CONSTENT.TAG_REFER_INFO, mAddedTagDetail);
                    listener.showStepTwoFragment(bundle);
                }
            }
        });
    }


    private void initView() {
        mFileArrayList = new ArrayList<>();
        mActivity = getActivity();
        mBinding.etTagType.setOnClickListener(this);
        mBinding.etTagType2.setOnClickListener(this);
        mBinding.etTagLocation.setOnClickListener(this);
        mBinding.ivCurrentLocation.setOnClickListener(this);
        mBinding.ivTagPhoto.setOnClickListener(this);
        mBinding.tvEmail.setOnClickListener(this);
        mBinding.tvPassword.setOnClickListener(this);
        mBinding.tvDocument.setOnClickListener(this);
        mBinding.tvNext.setOnClickListener(this);
        mBinding.etPaymentMethod.setOnClickListener(this);
        getArgumentsData();
        doUpload();
    }

    private void getArgumentsData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_tag_photo:
                if (PermissionUtility.isPermissionGranted(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, AppConstants.ACTIVITY_RESULT.CAMERA_PERMISSION)) {
                    Intent intent = new Intent(getActivity(), CameraTwoActivity.class);
                    intent.putExtra(AppConstants.CAMERA_CONSTANTS.IMAGE_LIMIT_ONESHOT, 1);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.CAMERA_ACTIVITY);
                }
                break;
            case R.id.et_tag_location:
                Intent in = new Intent(mActivity, ChoosseLocationActivity.class);
                startActivityForResult(in, AppConstants.ACTIVITY_RESULT.SEACH_LOACTION);
                break;
            case R.id.iv_current_location:
                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {
                    fetchCurrentLocation();
                }
                break;
            case R.id.et_tag_type:
                new CustomTagTypeDialog(mActivity, getString(R.string.private_txt), getString(R.string.public_txt), false, new OnDialogViewClickListener() {
                    @Override
                    public void onSubmit(String txt, int id) {
                        tagTypeId = id;
                        mBinding.etTagType.setText(txt);
                        if (tagTypeId == 1) {
                            mBinding.llTypeContainer.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.llTypeContainer.setVisibility(View.GONE);
                        }
                    }
                }).show();
                break;
                case R.id.et_tag_type_2:
                new CustomTagType_2Dialog(mActivity, getString(R.string.apartment),
                        getString(R.string.university),getString(R.string.organization)
                        ,getString(R.string.club),getString(R.string.other), false, new OnDialogViewClickListener() {
                    @Override
                    public void onSubmit(String txt, int id) {
                        mBinding.etTagType2.setText(txt);
                        getTagTypeIdNew = id;
                        /*if (tagTypeId == 1) {
                            mBinding.llTypeContainer.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.llTypeContainer.setVisibility(View.GONE);
                        }*/
                    }
                }).show();
                break;
            case R.id.tv_email:
                makeOptionSelected(R.id.tv_email);
                break;
            case R.id.tv_password:
                makeOptionSelected(R.id.tv_password);
                break;
            case R.id.tv_document:
                makeOptionSelected(R.id.tv_document);
                break;
            case R.id.tv_next:
//                DialogUtil.getInstance().customRewardsOrPaymentDialog(mActivity, new OnDialogViewClickListener() {
//                    @Override
//                    public void onSubmit(String txt, int id) {
//                                            }
//                });
                if (addTagViewModel.validate(mBinding, mFileArrayList)) {
                    if (id == 0) {
                        showToastShort(getString(R.string.please_select_payment_method));
                    } else {
                        if (id == 1) {
                            performAction(true, null);
                        } else if (id == 2) {
                            Intent intent = new Intent(mActivity, GooglePayPayment.class);
                            intent.putExtra(AppConstants.KEY_CONSTENT.PRICE, AppConstants.TAG_CREATE_AMOUNT);
                            startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.G_PAY_STRIPE);
                        }
                    }
                }

                break;
            case R.id.et_payment_method:
                new CustomTagTypeDialog(mActivity, getString(R.string.rewards_points), getString(R.string.google_pay), false, new OnDialogViewClickListener() {
                    @Override
                    public void onSubmit(String txt, int payemntId) {
                        id = payemntId;
                        mBinding.etPaymentMethod.setText(txt);

                    }
                }).show();
                break;
        }
    }

    private void performAction(boolean isRewards, String id) {
        this.isRewards = isRewards;
        gPayId = id;
        if (AppUtils.isInternetAvailable(mActivity)) {
            if (mFileArrayList != null && mFileArrayList.size() > 0) {
                getLoadingStateObserver().onChanged(true);
                startUpload(mFileArrayList.get(0));
            }
        } else {
            showNoNetworkError();
        }
    }

    private void prepareDataForRequest() {
        addTagViewModel.proceedTagRequest(mBinding, mImageList, "" + getTagTypeIdNew, tagTypeJionedId, tagTypeId, location, false, "", isRewards, gPayId);
    }

    private void makeOptionSelected(int id) {
        mBinding.tvEmail.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.edit_field_drawable));
        mBinding.tvEmail.setTextColor(getActivity().getResources().getColor(R.color.txt_black));
        mBinding.tvPassword.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.edit_field_drawable));
        mBinding.tvPassword.setTextColor(getActivity().getResources().getColor(R.color.txt_black));
        mBinding.tvDocument.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.edit_field_drawable));
        mBinding.tvDocument.setTextColor(getActivity().getResources().getColor(R.color.txt_black));
        mBinding.rlEmail.setVisibility(View.GONE);
        mBinding.etVerificationPassword.setVisibility(View.GONE);
        mBinding.etVerificationDocument.setVisibility(View.GONE);
        switch (id) {
            case R.id.tv_email:
                tagTypeJionedId = 1;
                mBinding.tvEmail.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.edit_field_filled_drawable));
                mBinding.tvEmail.setTextColor(getActivity().getResources().getColor(R.color.White));
                mBinding.rlEmail.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_password:
                tagTypeJionedId = 2;
                mBinding.tvPassword.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.edit_field_filled_drawable));
                mBinding.tvPassword.setTextColor(getActivity().getResources().getColor(R.color.White));
                mBinding.etVerificationPassword.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_document:
                tagTypeJionedId = 3;
                mBinding.tvDocument.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.edit_field_filled_drawable));
                mBinding.tvDocument.setTextColor(getActivity().getResources().getColor(R.color.White));
                mBinding.etVerificationDocument.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void fetchCurrentLocation() {
        if (gpsTracker == null)
            gpsTracker = new GPSTracker(mActivity);
        if (gpsTracker.isGPSEnable()) {
            setCurrentLocationText();
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void setCurrentLocationText() {
        try {
            if (AppUtils.getAddressByLatLng(mActivity, gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0) != null) {
                location = AppUtils.getAddressByLatLng(mActivity, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                mBinding.etTagLocation.setText(AppUtils.getAddressByLatLng(mActivity, gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0));
            } else {
                showToastLong(getString(R.string.unable_to_fatch_your_location));
            }
        } catch (Exception E) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.SEACH_LOACTION:
                if (data != null) {
                    String locAdd = data.getExtras().getString("Location");
                    location = AppUtils.getLocationFromAddress(mActivity, locAdd);
                    if (location != null) {
                        mBinding.etTagLocation.setText(location.getAddressLine(0));
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
                    mFileArrayList.clear();
                    mFileArrayList.addAll(data.getExtras().getStringArrayList("images"));
                    if (mFileArrayList.size() > 0) {
                        Glide.with(this).load(Uri.parse(mFileArrayList.get(0)).toString()).into(mBinding.ivTagPhoto);
                    }
                }
                break;
            case AppConstants.ACTIVITY_RESULT.G_PAY_STRIPE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        String rawToken = data.getExtras().getString(AppConstants.BUNDLE_DATA);
                        Token token = new Gson().fromJson(rawToken, Token.class);
                        if (token.getId() != null) {
                            performAction(false, token.getId());
                        }
                    }

                }

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
                    Intent intent = new Intent(getActivity(), CameraTwoActivity.class);
                    intent.putExtra(AppConstants.CAMERA_CONSTANTS.IMAGE_LIMIT_ONESHOT, 1);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.CAMERA_ACTIVITY);
                }
                break;
        }
    }

    private ImageBean addDataInBean(String path) {
        ImageBean bean = new ImageBean();
        bean.setId("1");
        bean.setName("sample");
        bean.setImagePath(path);
        return bean;
    }

    // Start Uploading image to Amazon Server
    private void startUpload(String path) {
        ImageBean bean = addDataInBean(path);
        mAmazonS3.uploadImage(bean);
    }

    @Override
    public void uploadSuccess(ImageBean bean) {
        mImageList = new ImageList();
        mImageList.setUrl(bean.getServerUrl());
        mImageList.setThumbUrl(bean.getServerUrl());
        prepareDataForRequest();
    }

    @Override
    public void uploadFailed(ImageBean bean) {
        getLoadingStateObserver().onChanged(false);

    }

    @Override
    public void uploadProgress(ImageBean bean) {
    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {

    }

    public interface IAddTagHost {
        void showStepTwoFragment(Bundle bundle);
    }


}
