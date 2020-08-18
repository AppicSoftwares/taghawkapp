package com.taghawk.ui.tag;

import android.Manifest;
import android.app.Activity;
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
import com.bumptech.glide.request.RequestOptions;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.camera2basic.CameraTwoActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.CustomTagTypeDialog;
import com.taghawk.custom_dialog.CustomTagType_2Dialog;
import com.taghawk.databinding.AddTagStepOneBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.tag.TagDetailsData;
import com.taghawk.model.tagaddresponse.AddTagResponse;
import com.taghawk.model.tagaddresponse.Data;
import com.taghawk.ui.home.filter.ChoosseLocationActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.PermissionUtility;

import java.util.ArrayList;


public class EditTagFragment extends BaseFragment implements View.OnClickListener, AmazonCallback {

    ImageList mImageList;
    TagDetailsData mData;
    private AddTagViewModel addTagViewModel;
    private AddTagStepOneBinding mBinding;
    private Activity mActivity;
    private int tagTypeId, getTagTypeIdNew;
    private int tagTypeJionedId;
    private GPSTracker gpsTracker;
    private Address location;
    private ArrayList<String> mFileArrayList;
    private AmazonS3 mAmazonS3;
    private EditTagFragment.IAddTagHost listener;
    private Data mAddedTagDetail;

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


    // Initilize the Amazon S3
    public void doUpload() {
        mAmazonS3 = AmazonS3.getInstance(mActivity, this, AppConstants.AMAZON_S3.AMAZON_POOLID, AppConstants.AMAZON_S3.BUCKET, AppConstants.AMAZON_S3.AMAZON_SERVER_URL, AppConstants.AMAZON_S3.END_POINT);
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
                    addTagViewModel.editTagOnFirebase(addTagResponse.getData());
                    mActivity.setResult(Activity.RESULT_OK);
                    mActivity.finish();
                }
            }
        });
    }

    private void initView() {
        mFileArrayList = new ArrayList<>();
        mActivity = getActivity();
        mBinding.etTagType.setOnClickListener(this);
        mBinding.etTagLocation.setOnClickListener(this);
        mBinding.ivCurrentLocation.setOnClickListener(this);
        mBinding.ivTagPhoto.setOnClickListener(this);
        mBinding.tvEmail.setOnClickListener(this);
        mBinding.tvPassword.setOnClickListener(this);
        mBinding.tvDocument.setOnClickListener(this);
        mBinding.etTagType2.setOnClickListener(this);
        mBinding.tvNext.setOnClickListener(this);
        mBinding.etPaymentMethod.setVisibility(View.GONE);
        mBinding.tvPaymentUsing.setVisibility(View.GONE);
        getArgumentsData();
        doUpload();
    }

    private void getArgumentsData() {
        if (getArguments() != null) {
            mData = getArguments().getParcelable(AppConstants.BUNDLE_DATA);
            setPreviousData(mData);
        }
    }

    private void setPreviousData(TagDetailsData mData) {
        Glide.with(mActivity).asBitmap().load(mData.getTagImageUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(mBinding.ivTagPhoto);
        mBinding.etTagName.setText(mData.getTagName());
        mBinding.etTagType.setText(getTagType(mData.getTagType()));
        mBinding.etTagType2.setText(getSubType(mData.getSubType()));
        mBinding.etTagDescription.setText(mData.getTagDescription());
        if (mData.getAnnouncement() != null && mData.getAnnouncement().length() > 0) {
            mBinding.etTagAnnouncement.setText(mData.getAnnouncement());

        }
        location = AppUtils.getAddressByLatLng(mActivity, mData.getTagLatitude(), mData.getTagLongitude());
        mBinding.etTagLocation.setText(mData.getTagAddress());
        if (tagTypeId == 1) {
            mBinding.llTypeContainer.setVisibility(View.VISIBLE);
            joinByTagAction(mData.getJoinTagBy());
        } else {
            mBinding.llTypeContainer.setVisibility(View.GONE);
        }
    }

    private void joinByTagAction(int joinTagBy) {
        switch (joinTagBy) {
            case 1:
                makeOptionSelected(R.id.tv_email);
                mBinding.etVerificationEmail.setText(mData.getTagJoinData());
                break;
            case 2:
                makeOptionSelected(R.id.tv_password);
                mBinding.etVerificationPassword.setText(mData.getTagJoinData());

                break;
            case 3:
                mBinding.etVerificationDocument.setText(mData.getTagJoinData());

                makeOptionSelected(R.id.tv_document);
                break;
        }
    }

    private String getTagType(int type) {
        if (type == 1) {
            tagTypeId = 1;
            return getString(R.string.private_txt);
        } else {
            tagTypeId = 2;
            return getString(R.string.public_txt);
        }
    }

    private String getSubType(int subType) {
        if (subType == 1) {
            getTagTypeIdNew = 1;
            return getString(R.string.apartment);
        } else if (subType == 2) {
            getTagTypeIdNew = 2;
            return getString(R.string.university);
        }else if (subType == 3) {
            getTagTypeIdNew = 3;
            return getString(R.string.organization);
        }else if (subType == 4) {
            getTagTypeIdNew = 4;
            return getString(R.string.club);
        }else if (subType == 5) {
            getTagTypeIdNew = 5;
            return getString(R.string.other);
        } else{
            return getString(R.string.other);
        }
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
                if (mData != null) {
                    if (mFileArrayList != null && mFileArrayList.size() > 0) {
                        getLoadingStateObserver().onChanged(true);
                        startUpload(mFileArrayList.get(0));
                    } else {
                        mImageList = new ImageList();
                        mImageList.setUrl(mData.getTagImageUrl());
                        mImageList.setThumbUrl(mData.getTagImageUrl());
                        prepareDataForRequest();
                    }
                }
                break;
        }
    }

    private void prepareDataForRequest() {
        addTagViewModel.proceedTagRequest(mBinding, mImageList, "" + getTagTypeIdNew, tagTypeJionedId, tagTypeId, location, true, mData.getTagId(), false, null);

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
