package com.taghawk.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.nv.NetverifyDocumentData;
import com.jumio.nv.NetverifyMrzData;
import com.jumio.nv.NetverifySDK;
import com.taghawk.R;
import com.taghawk.adapters.AddDocumentAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.camera2basic.CameraTwoActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.countrypicker.CountryCodeSelectionActivity;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.BottomSheetDialogJoinByDocumentBinding;
import com.taghawk.databinding.BottomSheetDialogJoinByEmailBinding;
import com.taghawk.databinding.BottomSheetDialogOtpVerifyBinding;
import com.taghawk.databinding.BottomSheetDialogVerifyPhoneBinding;
import com.taghawk.databinding.FragmentProfileEditBinding;
import com.taghawk.fb.FBSignCallback;
import com.taghawk.fb.FBSignInAI;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.AddressDataItem;
import com.taghawk.model.BillingAddressDataItem;
import com.taghawk.model.ShippingAddressesResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.profileresponse.AddressData;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.model.profileresponse.UserDetail;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.util.AppUtils;
import com.taghawk.util.PermissionUtility;
import com.taghawk.util.ResourceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class ProfileEditFragment extends BaseFragment implements View.OnClickListener, AmazonCallback, FBSignCallback, DatePickerDialog.OnDateSetListener {

    private ProfileEditViewModel profileViewModel;
    //    private ProfileEditFragment.IProfileEditHost mIProfileHost;
    private FragmentProfileEditBinding mBinding;
    private AppCompatActivity mActivity;
    private HomeViewModel mHomeViewModel;
    private UserDetail mUserDetail;
    private ArrayList<String> mFileArrayList;
    private ArrayList<String> mFileDocumentArrayList;
    private ArrayList<ImageBean> mMergedArrayList;
    private ArrayList<ImageList> mImageList = new ArrayList<>();
    private AmazonS3 mAmazonS3;
    private FBSignInAI mFBSignInAI;
    HashMap<String, Object> params = new HashMap<>();
    private final int FB_LOGIN_REQUEST_CODE = 64206;  //Fb Default request code
    BottomSheetDialog mDialog;
    AddDocumentAdapter addDocumentAdapter;
    private int imageUploadCount;
    BottomSheetDialogVerifyPhoneBinding bottomSheetDialogVerifyPhoneBinding;
    private String id = "+1";
    DatePickerDialog calDialog;
    private String dob;
    private String ssn;
    private NetverifySDK netverifySDK;
    private final static String TAG = "JumioSDK_DV_Profile";
    private boolean isFromCashOut;
    private String phonNumber;

    private ArrayList<BillingAddressDataItem> addressList = new ArrayList<>();

    private boolean addressPageStatus = false;

    /**
     * This method is used to return the instance of this fragment
     *
     * @return new instance of {@link ProfileFragment}
     */
    public static ProfileFragment getInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        setUpAmazon();
        setUpViewModel();
    }

    /**
     * Method to initVariables
     */
    private void initVariables() {
        mMergedArrayList = new ArrayList<>();
        mFileArrayList = new ArrayList<>();
        mFileDocumentArrayList = new ArrayList<>();
        mActivity = (AppCompatActivity) getActivity();
        if (getArguments() != null) {
            if (getArguments().containsKey(AppConstants.PROFILE_INFO)) {
                mUserDetail = (UserDetail) getArguments().getSerializable(AppConstants.PROFILE_INFO);
            }
            if (getArguments().containsKey(AppConstants.BUNDLE_DATA)) {
                isFromCashOut = getArguments().getBoolean(AppConstants.BUNDLE_DATA);
            }
        }
    }

    public void getProfileService() {
        try {
            getLoadingStateObserver().onChanged(true);
            HashMap<String, Object> params = new HashMap<>();
            profileViewModel.getProfile(params, AppConstants.REQUEST_CODE.PROFILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Initilize the Amazon S3
    public void setUpAmazon() {
        mAmazonS3 = mAmazonS3.getInstance(mActivity, this, AppConstants.AMAZON_S3.AMAZON_POOLID, AppConstants.AMAZON_S3.BUCKET, AppConstants.AMAZON_S3.AMAZON_SERVER_URL, AppConstants.AMAZON_S3.END_POINT);
    }
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof ProfileEditFragment.IProfileEditHost) {
//            mIProfileHost = (ProfileEditFragment.IProfileEditHost) context;
//        } else
//            throw new IllegalStateException("Host must implement ProfileEditFragment.IProfileEditHost");
//    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentProfileEditBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing view model
        initView();
        initFacebook();
//        if (mUserDetail != null)
//            setUpViews();
//        else {
        getProfileService();
//        }

    }

    private void setUpViews() {

        if (mUserDetail != null) {
            if (mUserDetail.getPhoneNumber() != null && mUserDetail.getPhoneNumber().length() > 0) {
                mBinding.tvPhoneVerify.setText(getString(R.string.verify));
            } else {
                mBinding.tvPhoneVerify.setText(getString(R.string.edit));
            }
            if (mUserDetail.getEmail() != null && mUserDetail.getEmail().length() > 0) {
                mBinding.tvEmailVerify.setText(getString(R.string.verify));
            } else {
                mBinding.tvEmailVerify.setText(getString(R.string.add));
            }
            if (!TextUtils.isEmpty(mUserDetail.getFirstName()))
                mBinding.etFirstName.setText(mUserDetail.getFirstName());
            if (!TextUtils.isEmpty(mUserDetail.getLastName()))
                mBinding.etLastName.setText(mUserDetail.getLastName());

            if (!TextUtils.isEmpty(mUserDetail.getEmail()))
                mBinding.tvEmailInfo.setText(mUserDetail.getEmail());

            if (!TextUtils.isEmpty(mUserDetail.getPhoneNumber())) {
                mBinding.tvPhoneInfo.setText(mUserDetail.getPhoneNumber());
            }

            if (!TextUtils.isEmpty(mUserDetail.getGovtId()))
                mBinding.tvDocumentInfo.setText(mUserDetail.getGovtId());

            if (!TextUtils.isEmpty(mUserDetail.getProfilePicture()))
                Glide.with(mActivity).load(mUserDetail.getProfilePicture()).apply(RequestOptions.placeholderOf(R.drawable.ic_detail_user_placeholder)).into(mBinding.ivProfile);

            if (mUserDetail.getIsEmailVerified() != null && mUserDetail.getIsEmailVerified()) {
                emailVerified(mBinding.tvEmailVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.verfied, false);
            } else {
                if (mUserDetail.getEmail() != null && mUserDetail.getEmail().length() > 0) {
                    emailVerified(mBinding.tvEmailVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.verify, true);
                } else {
                    emailVerified(mBinding.tvEmailVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.add, true);
                }
            }

            if (mUserDetail.getIsPhoneVerified() != null && mUserDetail.getIsPhoneVerified()) {
                phoneVerified(mBinding.tvPhoneVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.verfied, false);
            } else {
                if (mUserDetail.getPhoneNumber() != null && mUserDetail.getPhoneNumber().length() > 0) {
                    phoneVerified(mBinding.tvPhoneVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.verify, true);
                } else {
                    phoneVerified(mBinding.tvPhoneVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.edit, true);
                }
            }

            if (mUserDetail.getIsFacebookLogin() != null && mUserDetail.getIsFacebookLogin()) {
                fbVerified(mBinding.tvFbVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.verfied, false);
            } else {
                fbVerified(mBinding.tvFbVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.verify, true);
            }

            if (mUserDetail.getOfficialIdVerified() != null && mUserDetail.getOfficialIdVerified()) {
                documentVerified(mBinding.tvDocumentVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.verfied, false);
            } else {
                documentVerified(mBinding.tvDocumentVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.verify, true);
            }
            if (mUserDetail.getDob() != null && mUserDetail.getDob().length() > 0) {
                mBinding.tvDobInfo.setText(mUserDetail.getDob());
                documentVerified(mBinding.tvDobVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.edit, true);
            } else {
                documentVerified(mBinding.tvDobVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.add, true);

            }
            if (mUserDetail.getSsnNumber() != null && mUserDetail.getSsnNumber().length() > 0) {
                mBinding.tvSsnInfo.setText("xxx-xx-" + mUserDetail.getSsnNumber());
                documentVerified(mBinding.tvSsnVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.edit, true);
            } else {
                documentVerified(mBinding.tvSsnVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.add, true);
            }
//            if (mUserDetail.getAddressData() != null) {
//                setAddress();
//            }

        }
    }

    private void setAddress() {
        if(mUserDetail.getAddressData().getAddressLineTwo() != null && !TextUtils.isEmpty(mUserDetail.getAddressData().getAddressLineTwo()))
            mBinding.tvAddress.setText(mUserDetail.getAddressData().getAddressLineOne() + ", #" + mUserDetail.getAddressData().getAddressLineTwo() + "," + mUserDetail.getAddressData().getCity() + " " + mUserDetail.getAddressData().getState() + ", " + mUserDetail.getAddressData().getPostalCode());
        else
            mBinding.tvAddress.setText(mUserDetail.getAddressData().getAddressLineOne() + "," + mUserDetail.getAddressData().getCity() + " " + mUserDetail.getAddressData().getState() + ", " + mUserDetail.getAddressData().getPostalCode());
        mBinding.tvAddAddress.setText(getString(R.string.edit));
    }

    private void documentVerified(AppCompatButton tvDocumentVerify, int p, int p2, int p3, boolean b) {
        tvDocumentVerify.setBackgroundResource(p);
        tvDocumentVerify.setTextColor(getResources().getColor(p2));
        tvDocumentVerify.setText(getString(p3));
        tvDocumentVerify.setEnabled(b);
    }


    private void fbVerified(AppCompatButton tvFbVerify, int p, int p2, int p3, boolean b) {
        tvFbVerify.setBackgroundResource(p);
        tvFbVerify.setTextColor(getResources().getColor(p2));
        tvFbVerify.setText(getString(p3));
        tvFbVerify.setEnabled(b);
    }

    private void phoneVerified(AppCompatButton tvPhoneVerify, int p, int p2, int p3, boolean b) {
        tvPhoneVerify.setBackgroundResource(p);
        tvPhoneVerify.setTextColor(getResources().getColor(p2));
        tvPhoneVerify.setText(getString(p3));
        tvPhoneVerify.setEnabled(b);
    }

    private void emailVerified(AppCompatButton tvEmailVerify, int p, int p2, int p3, boolean b) {
        tvEmailVerify.setBackgroundResource(p);
        tvEmailVerify.setTextColor(getResources().getColor(p2));
        tvEmailVerify.setText(getString(p3));
        tvEmailVerify.setEnabled(b);
    }


    /**
     * Method to set Up View Model
     */
    private void setUpViewModel() {
        profileViewModel = ViewModelProviders.of(this).get(ProfileEditViewModel.class);
        profileViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        profileViewModel.profileViewModel().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse.getCode() == 200) {
                    showToastShort(commonResponse.getMessage());
                    switch (commonResponse.getRequestCode()) {
                        case AppConstants.REQUEST_CODE.FACEBOOK_VERIFY:
                            fbVerified(mBinding.tvFbVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.verfied, false);
                            break;
                        case AppConstants.REQUEST_CODE.PROFILE_PICTURE:
                            mHomeViewModel.setActionData(AppConstants.ActionConstants.BACK_ACTION);
//                            mUserDetail.setProfilePicture(String.valueOf(params.get(AppConstants.REQUEST_PARAMS.PROFILE_PICTURE)));
                            break;
                        case AppConstants.REQUEST_CODE.DOCUMENT_VERIFY:
                            documentVerified(mBinding.tvDocumentVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.verify, true);
                            break;
                        case AppConstants.REQUEST_CODE.MOBILE_VERIFY:
                            phoneVerified(mBinding.tvPhoneVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.verify, true);
                            otpVerifyBottomSheetDialog();
                            break;
                        case AppConstants.REQUEST_CODE.EMAIL_VERIFY:
                            emailVerified(mBinding.tvEmailVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.verify, true);
                            getCustomBottomDialog(getString(R.string.successfully), commonResponse.getMessage(), new OnDialogItemClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                }

                                @Override
                                public void onNegativeBtnClick() {
                                }
                            });
                            break;
                        case AppConstants.REQUEST_CODE.PROFILE_NAME:
                            mHomeViewModel.setActionData(AppConstants.ActionConstants.BACK_ACTION);
                            mBinding.etFirstName.clearFocus();
                            break;
                        case AppConstants.REQUEST_CODE.OTP_VERIFY:
                            DataManager.getInstance().savePhoneVerified(true);
                            DataManager.getInstance().savePhonenNumber(phonNumber);
                            phoneVerified(mBinding.tvPhoneVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.verfied, false);
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            break;
                        case AppConstants.REQUEST_CODE.ADD_DOB:
                            mBinding.tvDobInfo.setText(dob);
                            DataManager.getInstance().saveDob(dob);
                            documentVerified(mBinding.tvDobVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.edit, true);
                            break;
                        case AppConstants.REQUEST_CODE.ADD_SSN:
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            DataManager.getInstance().saveSSNNumber(ssn);
                            mBinding.tvSsnInfo.setText("xxx-xx-" + ssn);
                            documentVerified(mBinding.tvSsnVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.edit, true);
                            break;
                    }
//                    mHomeViewModel.setUpdatedProfileData(mUserDetail);
                }

            }
        });
        profileViewModel.getmProfileEditLiveData().observe(mActivity, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(@Nullable ProfileResponse profileResponse) {
                getLoadingStateObserver().onChanged(false);
                if (profileResponse.getCode() == 200) {
                    mUserDetail = profileResponse.getUserDetail();
                    if (mUserDetail != null) {
                        profileViewModel.updateUserNode(mUserDetail.getId(), mUserDetail.getProfilePicture(), mUserDetail.getFullName(), mUserDetail.getEmail());
                        if (AppUtils.isInternetAvailable(mActivity))
                            profileViewModel.getShippingAddresses(DataManager.getInstance().getUserDetails().getUserId());
                        else
                            showNoNetworkError();
                        setUpViews();
                    }
                }
            }
        });
        profileViewModel.getAddressesLiveData().observe(this, new Observer<ShippingAddressesResponse>() {
            @Override
            public void onChanged(@Nullable ShippingAddressesResponse response) {
                getLoadingStateObserver().onChanged(false);
                if (response.getStatusCode() == 200) {
                    if (response.getData() != null && response.getData().size() > 0) {
                        addressList = (ArrayList<BillingAddressDataItem>) response.getData();
                        for(int i=0; i < addressList.size(); i++){
                            if(addressList.get(i).getSelectedStatus() == 1) {
                                if(addressList.get(i).getStreet2() != null && !TextUtils.isEmpty(addressList.get(i).getStreet2()))
                                    mBinding.tvAddress.setText("" + addressList.get(i).getStreet1() + ", #" + addressList.get(i).getStreet2() + ", " + addressList.get(i).getCity() + ", " + addressList.get(i).getState() + ", " + addressList.get(i).getPostal_code());
                                else
                                    mBinding.tvAddress.setText("" + addressList.get(i).getStreet1() + ", " + addressList.get(i).getCity() + ", " + addressList.get(i).getState() + ", " + addressList.get(i).getPostal_code());

                                mBinding.tvAddAddress.setText(getString(R.string.edit));
                                break;
                            } else {
                                if (mUserDetail.getAddressData() != null) {
                                    setAddress();
                                }
                            }
                        }
                    } else {
                        if (mUserDetail.getAddressData() != null) {
                            setAddress();
                        }
                    }
                } else {
                    showToastShort(response.getMessage());
                }
            }
        });
//        profileViewModel
        mHomeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        mHomeViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());

    }

    private void setUpUserDetails() {
        if (!TextUtils.isEmpty(mUserDetail.getProfilePicture()))
            Glide.with(mActivity).load(mUserDetail.getProfilePicture()).apply(RequestOptions.placeholderOf(R.drawable.ic_detail_user_placeholder)).into(mBinding.ivProfile);

    }

    // init views and listener
    private void initView() {
        mActivity = (AppCompatActivity) getActivity();
        mBinding.ivClose.setOnClickListener(this);
        mBinding.ivCamera.setOnClickListener(this);
        mBinding.tvFbVerify.setOnClickListener(this);
        mBinding.tvEmailVerify.setOnClickListener(this);
        mBinding.tvPhoneVerify.setOnClickListener(this);
        mBinding.tvDocumentVerify.setOnClickListener(this);
        mBinding.tvEditProfile.setOnClickListener(this);
        mBinding.etFirstName.setOnClickListener(this);
        mBinding.etLastName.setOnClickListener(this);
        mBinding.tvDobVerify.setOnClickListener(this);
        mBinding.tvSsnVerify.setOnClickListener(this);
        mBinding.tvAddAddress.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getActivity(), CameraTwoActivity.class);
                    intent.putExtra(AppConstants.CAMERA_CONSTANTS.IMAGE_LIMIT_ONESHOT, 1);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.CAMERA_ACTIVITY);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                if (isFromCashOut) {
                    mActivity.finish();
                } else
                    mHomeViewModel.setActionData(AppConstants.ActionConstants.BACK_ACTION);
                break;
            case R.id.iv_camera:
                if (PermissionUtility.isPermissionGranted(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, AppConstants.ACTIVITY_RESULT.CAMERA_PERMISSION)) {
                    Intent intent = new Intent(getActivity(), CameraTwoActivity.class);
                    intent.putExtra(AppConstants.CAMERA_CONSTANTS.IMAGE_LIMIT_ONESHOT, 1);
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.CAMERA_ACTIVITY);
                }
                break;
            case R.id.tv_fb_verify:
                fbSignIn();
                break;
            case R.id.tv_document_verify:
                if (mUserDetail != null) {
                    if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101))
                        documentVerifyBottomSheetDialog();
                }
                break;
            case R.id.tv_phone_verify:
                if (mUserDetail != null) {
                    if (mUserDetail.getPhoneNumber() != null && mUserDetail.getPhoneNumber().length() > 0 && mUserDetail.getCountryCode() != null && mUserDetail.getCountryCode().length() > 0) {
                        profileViewModel.updateProfile(3, "", "", mUserDetail.getCountryCode(), mUserDetail.getPhoneNumber(), null, "", "", "", "", "", "", "", "", "", AppConstants.REQUEST_CODE.MOBILE_VERIFY);
                    } else {
                        phoneVerifyBottomSheetDialog();
                    }
                }

                break;
            case R.id.tv_email_verify:
                if (mUserDetail != null) {
                    if (mUserDetail.getEmail() != null && mUserDetail.getEmail().length() > 0) {
                        profileViewModel.updateProfile(2, "", mUserDetail.getEmail(), "", "", null, "", "", "", "", "", "", "", "", "", AppConstants.REQUEST_CODE.EMAIL_VERIFY);
                    } else {
                        emailVerifyBottomSheetDialog();
                    }
                }
                break;
            case R.id.tv_edit_profile:

                if (TextUtils.isEmpty(mBinding.etFirstName.getText().toString().trim())) {
                    mBinding.textInputFirstName.setErrorEnabled(true);
                    mBinding.textInputFirstName.setError(ResourceUtils.getInstance().getString(R.string.enter_first_name));
                } else if (!mBinding.etFirstName.getText().toString().matches("[a-z A-Z]*")) {
                    mBinding.textInputFirstName.setErrorEnabled(true);
                    mBinding.textInputFirstName.setError(ResourceUtils.getInstance().getString(R.string.enter_valid_name));
                } else if (TextUtils.isEmpty(mBinding.etLastName.getText().toString().trim())) {
                    mBinding.textInputLastName.setErrorEnabled(true);
                    mBinding.textInputLastName.setError(ResourceUtils.getInstance().getString(R.string.enter_last_name));
                } else if (!mBinding.etLastName.getText().toString().matches("[a-z A-Z]*")) {
                    mBinding.textInputLastName.setErrorEnabled(true);
                    mBinding.textInputLastName.setError(ResourceUtils.getInstance().getString(R.string.enter_valid_last_name));
                } else {
                    if (mFileArrayList.size() == 1) {
                        startUpload(mFileArrayList.get(0), false);
                    } else {
                        profileViewModel.updateProfile(0, "", "", "", "", null, mBinding.etFirstName.getText().toString().trim() + " " + mBinding.etLastName.getText().toString().trim(), mUserDetail.getProfilePicture(), "", mBinding.etFirstName.getText().toString().trim(), mBinding.etLastName.getText().toString().trim(), "", "", "", "", AppConstants.REQUEST_CODE.PROFILE_NAME);
                    }
                }
                break;
            case R.id.et_first_name:
                mBinding.etFirstName.setEnabled(true);
                break;
            case R.id.et_last_name:
                mBinding.etFirstName.setEnabled(true);
                break;
            case R.id.tv_dob_verify:
                openCalender();
                break;
            case R.id.tv_ssn_verify:
                addSSNBottomSheetDialog();
                break;
            case R.id.tv_add_address:
                openAddUpdateAddress();
                break;
        }

    }

    private void openAddUpdateAddress() {
        Intent intent = new Intent(mActivity, AddUpdateAddressActivity.class);
        addressPageStatus = true;
        intent.putExtra("type", "shipping_address");
        intent.putExtra("addressList", addressList);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.ADD_UPDATE_ADDRESS);
    }

    private void openCalender() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calDialog = new DatePickerDialog(mActivity, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        calDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.REQUEST_CODE.CAMERA_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    mFileArrayList.clear();
                    mFileArrayList.addAll(data.getExtras().getStringArrayList("images"));
                    if (mFileArrayList.size() > 0) {
                        Glide.with(this).load(Uri.parse(mFileArrayList.get(0)).toString()).into(mBinding.ivProfile);
//                        startUpload(mFileArrayList.get(0));
                    }
                }
                break;

            case FB_LOGIN_REQUEST_CODE:
                mFBSignInAI.setActivityResult(requestCode, resultCode, data);
                break;
            case AppConstants.REQUEST_CODE.MULTIPLE_IMAGE_INTENT:
                if (data.getStringArrayListExtra("result") != null) {
                    mFileDocumentArrayList.clear();
                    imageUploadCount = 0;
                    ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                    mFileDocumentArrayList.addAll(selectionResult);
                    addDocumentAdapter.notifyDataSetChanged();
                }
                break;
            case AppConstants.COUNTRY_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String name = data.getStringExtra("Name");
                    id = data.getStringExtra("Id");
                    if (bottomSheetDialogVerifyPhoneBinding != null) {
                        bottomSheetDialogVerifyPhoneBinding.tvCountryCode.setText(id);
                    }
                }
                break;
            case AppConstants.ACTIVITY_RESULT.ADD_UPDATE_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {
                    addressPageStatus = false;
                    addressList.clear();
                    addressList = data.getExtras().getParcelableArrayList(AppConstants.BUNDLE_DATA);
                    BillingAddressDataItem addressDataResponse = new BillingAddressDataItem();

                    if(addressList.size() > 0) {
                        for(int i=0; i<addressList.size(); i++){
                            if(addressList.get(i).getSelectedStatus() == 1) {
                                addressDataResponse = addressList.get(i);
                                break;
                            }
                        }
                    }

                    AddressData addressData = new AddressData();
                    addressData.setAddressLineOne(addressDataResponse.getStreet1());
                    addressData.setAddressLineTwo(addressDataResponse.getStreet2());
                    addressData.setCity(addressDataResponse.getCity());
                    addressData.setState(addressDataResponse.getState());
                    addressData.setPostalCode(addressDataResponse.getPostal_code());
                    if (addressData != null && addressData.getAddressLineOne() != null && !TextUtils.isEmpty(addressData.getAddressLineOne())) {
                        mBinding.tvAddAddress.setText(getString(R.string.edit));
                        mUserDetail.setAddressData(addressData);
                        if(mUserDetail.getAddressData().getAddressLineTwo() != null && !TextUtils.isEmpty(mUserDetail.getAddressData().getAddressLineTwo()))
                            mBinding.tvAddress.setText(mUserDetail.getAddressData().getAddressLineOne() + ", #" + mUserDetail.getAddressData().getAddressLineTwo() + "," + mUserDetail.getAddressData().getCity() + " " + mUserDetail.getAddressData().getState() + ", " + mUserDetail.getAddressData().getPostalCode());
                        else
                            mBinding.tvAddress.setText(mUserDetail.getAddressData().getAddressLineOne() + "," + mUserDetail.getAddressData().getCity() + " " + mUserDetail.getAddressData().getState() + ", " + mUserDetail.getAddressData().getPostalCode());

                    } else {
                        mBinding.tvAddAddress.setText(getString(R.string.edit));
                        if (mUserDetail.getAddressData() != null) {
                            setAddress();
                        }
                    }
                }
                break;
        }
        if (requestCode == NetverifySDK.REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    return;
                } else {
                    String scanReference = (data == null) ? "" : data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE);
                    NetverifyDocumentData documentData = (data == null) ? null : (NetverifyDocumentData) data.getParcelableExtra(NetverifySDK.EXTRA_SCAN_DATA);
                    NetverifyMrzData mrzData = documentData != null ? documentData.getMrzData() : null;
                    getLoadingStateObserver().onChanged(true);
                    profileViewModel.updateProfile(4, scanReference, AppConstants.REQUEST_CODE.DOCUMENT_VERIFY);

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                String errorMessage = data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE);
                String errorCode = data.getStringExtra(NetverifySDK.EXTRA_ERROR_CODE);
            }
        }
    }

    /*
     *  Sign In Method
     */
    public void fbSignIn() {
        if (mFBSignInAI != null)
            mFBSignInAI.doSignIn();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(addressPageStatus) {
            if (AppUtils.isInternetAvailable(mActivity))
                profileViewModel.getShippingAddresses(DataManager.getInstance().getUserDetails().getUserId());
            else
                showNoNetworkError();
        }
    }

    private void initFacebook() {
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getActivity());
        initializeFB();
    }

    private void initializeFB() {
        mFBSignInAI = new FBSignInAI();
        mFBSignInAI.setActivity(mActivity);
        mFBSignInAI.setCallback(this);

    }

    private ImageBean addDataInBean(String path, boolean isMultiple) {
        ImageBean bean = new ImageBean();
        bean.setId("1");
        bean.setName("sample");
        bean.setImagePath(path);
        bean.setMultiple(isMultiple);
        return bean;
    }

    // Start Uploading image to Amazon Server
    private void startUpload(String path, int type) {
        getLoadingStateObserver().onChanged(true);
        ImageBean bean = addDataInBean(path, false);
        bean.setType(type);
        mAmazonS3.uploadImage(bean);
    }

    // Start Uploading image to Amazon Server
    private void startUpload(String path, boolean isMultiple) {
        getLoadingStateObserver().onChanged(true);
        ImageBean bean = addDataInBean(path, isMultiple);
        mAmazonS3.uploadImage(bean);
    }

    @Override
    public void uploadSuccess(ImageBean bean) {
        getLoadingStateObserver().onChanged(false);

        if (bean.isMultiple()) {
            if (mImageList != null && imageUploadCount == 0) {
                mImageList.clear();
            }
            imageUploadCount++;
            ImageList imageList = new ImageList();
            imageList.setUrl(bean.getServerUrl());
            imageList.setThumbUrl(bean.getServerUrl());
            imageList.setType(bean.getType());
            mImageList.add(imageList);
            if (imageUploadCount == this.mFileDocumentArrayList.size()) {
                profileViewModel.updateProfile(4, "", "", "", "", getDocumentString(mImageList), "", "", "", "", "", "", "", "", "", AppConstants.REQUEST_CODE.DOCUMENT_VERIFY);
                if (mDialog != null)
                    mDialog.dismiss();
                mFileDocumentArrayList.clear();
            }
        } else {
            profileViewModel.updateProfile(0, "", "", "", "", null, mBinding.etFirstName.getText().toString().trim() + " " + mBinding.etFirstName.getText().toString().trim(), bean.getServerUrl(), "", mBinding.etFirstName.getText().toString().trim(), mBinding.etLastName.getText().toString().trim(), "", "", "", "", AppConstants.REQUEST_CODE.PROFILE_NAME);
        }
    }

    private void updateUserDetails() {
        getLoadingStateObserver().onChanged(true);
        params.clear();
        for (int i = 0; i < mImageList.size(); i++) {
            if (mImageList.get(i).getType() == AppConstants.UPLOAD_IMAGE_TYPE.PROFILE_PIC) {
                params.put(AppConstants.REQUEST_PARAMS.PROFILE_PICTURE, mImageList.get(i).getUrl());
                break;
            }
        }
        for (int i = 0; i < mImageList.size(); i++) {
            if (mImageList.get(i).getType() == AppConstants.UPLOAD_IMAGE_TYPE.DOCUMENT) {
                params.put(AppConstants.REQUEST_PARAMS.OFFICIAL_ID, mImageList.get(i).getUrl());
                break;
            }
        }
        params.put(AppConstants.REQUEST_PARAMS.FULL_NAME, mBinding.etFirstName.getText().toString().trim());
        params.put(AppConstants.REQUEST_PARAMS.PHONE_NUMBER, mBinding.tvPhoneInfo.getText().toString());
//        profileViewModel.updateProfile();
    }

    @Override
    public void uploadFailed(ImageBean bean) {
        getLoadingStateObserver().onChanged(false);
        if (imageUploadCount == this.mFileDocumentArrayList.size()) {
            profileViewModel.updateProfile(4, "", "", "", "", getDocumentString(mImageList), "", "", "", "", "", "", "", "", "", AppConstants.REQUEST_CODE.DOCUMENT_VERIFY);
            if (mDialog != null)
                mDialog.dismiss();
        }

    }

    @Override
    public void uploadProgress(ImageBean bean) {
    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {

    }

    @Override
    public void fbSignInSuccessResult(JSONObject jsonObject) {
        String social_id = null;
        try {
            social_id = jsonObject.getString("id");
            profileViewModel.updateProfile(1, social_id, "", "", "", null, "", "", "", "", "", "", "", "", "", AppConstants.REQUEST_CODE.FACEBOOK_VERIFY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fbSignOutSuccessResult() {
        Log.d("Fb", "fbSignOutSuccessResult");
    }

    @Override
    public void fbSignInFailure(FacebookException exception) {
        Log.d("Fb", "fbSignInFailure");

    }

    @Override
    public void fbSignInCancel() {
        Log.d("Fb", "fbSignInCancel");

    }

    @Override
    public void fbFriendsList(JSONArray data) {
//
    }

    public void documentVerifyBottomSheetDialog() {
        mDialog = new BottomSheetDialog(mActivity);
        final BottomSheetDialogJoinByDocumentBinding binding = BottomSheetDialogJoinByDocumentBinding.inflate(LayoutInflater.from(mActivity));
        mDialog.setContentView(binding.getRoot());
        mDialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(mActivity.getResources().getColor(android.R.color.transparent));
        binding.tvMessage.setText(mActivity.getResources().getString(R.string.select_your_document_to_verify_your_identity));
        binding.tvTitle.setText(mActivity.getResources().getString(R.string.become_verify_seller));
        binding.tvTitleDocument.setVisibility(View.GONE);
        binding.llUploadDocuments.setVisibility(View.GONE);

        binding.tvApply.setText(getString(R.string.scan));
        binding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                initializeNetverifySDK();

                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.CAMERA}, 301)) {
                    try {
                        if (netverifySDK != null) {
                            startActivityForResult(netverifySDK.getIntent(), netverifySDK.REQUEST_CODE);
                        }
                    } catch (MissingPermissionException e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

//    public void documentVerifyBottomSheetDialog() {
//        mDialog = new BottomSheetDialog(mActivity);
//        final BottomSheetDialogJoinByDocumentBinding binding = BottomSheetDialogJoinByDocumentBinding.inflate(LayoutInflater.from(mActivity));
//        mDialog.setContentView(binding.getRoot());
//        mDialog.setCancelable(false);
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
//                .getLayoutParams();
//        CoordinatorLayout.Behavior behavior = params.getBehavior();
//        ((View) binding.main.getParent()).setBackgroundColor(mActivity.getResources().getColor(android.R.color.transparent));
//        binding.rvAddDocumentImages.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayout.HORIZONTAL, false));
//        binding.tvMessage.setText(getString(R.string.edit_profile_document));
//        binding.tvTitleDocument.setText(getString(R.string.edit_profile_id));
//        binding.tvApply.setText(getString(R.string.done_lowercase));
//        addDocumentAdapter = new AddDocumentAdapter(mActivity, mFileDocumentArrayList, new RecyclerListener() {
//            @Override
//            public void onItemClick(View v, int position, String number, boolean flag) {
//                mFileDocumentArrayList.remove(position);
//                addDocumentAdapter.notifyDataSetChanged();
//            }
//        });
//        binding.rvAddDocumentImages.setAdapter(addDocumentAdapter);
//        binding.ivAddDocument.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mActivity, ImagesGallery.class);
//                intent.putExtra("selectedList", (Serializable) mFileDocumentArrayList);
//                intent.putExtra("title", "Select Image");
//                intent.putExtra("maxSelection", 5); // Optional
//                startActivityForResult(intent, AppConstants.REQUEST_CODE.MULTIPLE_IMAGE_INTENT);
//            }
//        });
//
//        binding.tvApply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mFileDocumentArrayList == null || mFileDocumentArrayList.size() == 0) {
//                    showToastShort(ResourceUtils.getInstance().getString(R.string.please_upload_document_image));
//                } else {
//                    for (int i = 0; i < mFileDocumentArrayList.size(); i++) {
//                        startUpload(mFileDocumentArrayList.get(i), true);
//                    }
//
//                }
//
//            }
//        });
//        binding.tvApply.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    if (mFileDocumentArrayList == null || mFileDocumentArrayList.size() == 0) {
//                        showToastShort(ResourceUtils.getInstance().getString(R.string.please_upload_document_image));
//                    } else {
//                        for (int i = 0; i < mFileDocumentArrayList.size(); i++) {
//                            startUpload(mFileDocumentArrayList.get(i), true);
//                        }
//
//                    }
//                }
//                return false;
//            }
//        });
//        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDialog.dismiss();
//            }
//        });
//        mDialog.show();
//    }

    private String getDocumentString(ArrayList<ImageList> mFileDocumentArrayList) {
        String str = "";
        for (int i = 0; i < mFileDocumentArrayList.size(); i++) {
            if (i == 0) {
                str = mFileDocumentArrayList.get(i).getUrl();
            } else {
                str = str + "," + mFileDocumentArrayList.get(i).getUrl();
            }
        }

        return str;
    }

    private String[] getDocumentArray(ArrayList<String> mFileDocumentArrayList) {

        return new String[0];
    }

    public void phoneVerifyBottomSheetDialog() {
        mDialog = new BottomSheetDialog(mActivity);
        bottomSheetDialogVerifyPhoneBinding = BottomSheetDialogVerifyPhoneBinding.inflate(LayoutInflater.from(mActivity));
        mDialog.setContentView(bottomSheetDialogVerifyPhoneBinding.getRoot());
        mDialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) bottomSheetDialogVerifyPhoneBinding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) bottomSheetDialogVerifyPhoneBinding.main.getParent()).setBackgroundColor(mActivity.getResources().getColor(android.R.color.transparent));
        bottomSheetDialogVerifyPhoneBinding.tvMessage.setText(getString(R.string.verify_phone_number));
        bottomSheetDialogVerifyPhoneBinding.tvApply.setText(getString(R.string.done_lowercase));

        if (mUserDetail != null && mUserDetail.getPhoneNumber().length() > 0) {
            bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setText(mUserDetail.getPhoneNumber());
            bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setEnabled(true);
            bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setText(mUserDetail.getPhoneNumber());
            mBinding.tvEmailVerify.setText(getString(R.string.verify));
        } else {
            bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setEnabled(false);
            bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setEnabled(true);
            mBinding.tvPhoneVerify.setText(getString(R.string.edit));
        }
        bottomSheetDialogVerifyPhoneBinding.tvCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent countryCodeSelection = new Intent(getActivity(), CountryCodeSelectionActivity.class);
                startActivityForResult(countryCodeSelection, AppConstants.COUNTRY_CODE);
            }
        });
        bottomSheetDialogVerifyPhoneBinding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePhoneNumber())
                    phonVerifyHit();
            }
        });
        bottomSheetDialogVerifyPhoneBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (validatePhoneNumber())
                        phonVerifyHit();
                }
                return false;
            }
        });
        mDialog.show();
    }

    private boolean validatePhoneNumber() {
        if (bottomSheetDialogVerifyPhoneBinding.etVerificationPhone == null || bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().toString().trim().length() == 0) {
            showToastShort(getString(R.string.please_enter_phone_number));
            return false;
        } else if (bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().toString().trim().length() < 7) {
            showToastShort(getString(R.string.phone_invalid_val));
            return false;
        }
        phonNumber = bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().toString().trim();
        return true;
    }

    private void phonVerifyHit() {
        if (bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().length() > 0) {
            profileViewModel.updateProfile(3, "", "", id, bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().toString().trim(), null, "", "", "", "", "", "", "", "", "", AppConstants.REQUEST_CODE.MOBILE_VERIFY);
            mBinding.tvPhoneInfo.setText(bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().toString());
            mDialog.dismiss();
        } else {
            showToastLong(getString(R.string.please_enter_phone_number));
        }
    }

    public void emailVerifyBottomSheetDialog() {
        mDialog = new BottomSheetDialog(mActivity);
        final BottomSheetDialogJoinByEmailBinding binding = BottomSheetDialogJoinByEmailBinding.inflate(LayoutInflater.from(mActivity));
        mDialog.setContentView(binding.getRoot());
        mDialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(mActivity.getResources().getColor(android.R.color.transparent));
        binding.tvMessage.setText(getString(R.string.email_verification_edit));
        binding.tvTitle.setText(getString(R.string.verify_email));
        binding.tvDomainText.setVisibility(View.GONE);
        binding.tvApply.setText(getString(R.string.done_lowercase));
        if (mUserDetail != null && mUserDetail.getEmail().length() > 0) {
            binding.etVerificationEmail.setEnabled(false);
            binding.etVerificationEmail.setText(mUserDetail.getEmail());
            mBinding.tvEmailVerify.setText(getString(R.string.verify));
        } else {
            binding.etVerificationEmail.setEnabled(false);
            binding.etVerificationEmail.setEnabled(true);
            mBinding.tvEmailVerify.setText(getString(R.string.add));
        }
        binding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etVerificationEmail.getText().length() > 0) {
                    profileViewModel.updateProfile(2, "", binding.etVerificationEmail.getText().toString().trim(), "", "", null, "", "", "", "", "", "", "", "", "", AppConstants.REQUEST_CODE.EMAIL_VERIFY);
                    mBinding.tvEmailInfo.setText(binding.etVerificationEmail.getText().toString().trim());
                    mDialog.dismiss();
                }
            }
        });
        binding.etVerificationEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (binding.etVerificationEmail.getText().length() > 0) {
                        profileViewModel.updateProfile(2, "", binding.etVerificationEmail.getText().toString().trim(), "", "", null, "", "", "", "", "", "", "", "", "", AppConstants.REQUEST_CODE.EMAIL_VERIFY);
                        mBinding.tvEmailInfo.setText(binding.etVerificationEmail.getText().toString().trim());
                        mDialog.dismiss();
                    }
                }
                return false;
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public void otpVerifyBottomSheetDialog() {
        mDialog = new BottomSheetDialog(mActivity);
        final BottomSheetDialogOtpVerifyBinding binding = BottomSheetDialogOtpVerifyBinding.inflate(LayoutInflater.from(mActivity));
        mDialog.setContentView(binding.getRoot());
        mDialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(mActivity.getResources().getColor(android.R.color.transparent));
        binding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etVerificationOtp.getText().length() > 0) {
                    profileViewModel.verifyOtp(binding.etVerificationOtp.getText().toString().trim(), AppConstants.REQUEST_CODE.OTP_VERIFY);
                }
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        binding.etVerificationOtp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (binding.etVerificationOtp.getText().length() == 4) {
                        profileViewModel.verifyOtp(binding.etVerificationOtp.getText().toString().trim(), AppConstants.REQUEST_CODE.OTP_VERIFY);
                    }
                }
                return false;
            }
        });
        binding.etVerificationOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.etVerificationOtp.getText().toString().trim().length() == 4) {
                    profileViewModel.verifyOtp(binding.etVerificationOtp.getText().toString().trim(), AppConstants.REQUEST_CODE.OTP_VERIFY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDialog.show();
    }

    public void addSSNBottomSheetDialog() {
        mDialog = new BottomSheetDialog(mActivity);
        final BottomSheetDialogOtpVerifyBinding binding = BottomSheetDialogOtpVerifyBinding.inflate(LayoutInflater.from(mActivity));
        mDialog.setContentView(binding.getRoot());
        mDialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(mActivity.getResources().getColor(android.R.color.transparent));
        binding.etVerificationOtp.setHint(getString(R.string.enter_ssn));
        binding.tvTitle.setText(getString(R.string.ssn_number));
        binding.tvMessage.setText(getString(R.string.ssn_msg));
        binding.tvApply.setText(getString(R.string.add));
        binding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etVerificationOtp.getText().length() == 4) {
                    ssn = binding.etVerificationOtp.getText().toString().trim();
                    profileViewModel.updateProfile(0, "", "", "", "", null, "", "", "", "", "", "", binding.etVerificationOtp.getText().toString(), "", "", AppConstants.REQUEST_CODE.ADD_SSN);
                }
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        binding.etVerificationOtp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (binding.etVerificationOtp.getText().length() == 4) {
                        ssn = binding.etVerificationOtp.getText().toString().trim();
                        profileViewModel.updateProfile(0, "", "", "", "", null, "", "", "", "", "", "", binding.etVerificationOtp.getText().toString(), "", "", AppConstants.REQUEST_CODE.ADD_SSN);
                    }
                }
                return false;
            }
        });
        binding.etVerificationOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.etVerificationOtp.getText().toString().trim().length() == 4) {
                    ssn = binding.etVerificationOtp.getText().toString().trim();
                    profileViewModel.updateProfile(0, "", "", "", "", null, "", "", "", "", "", "", binding.etVerificationOtp.getText().toString(), "", "", AppConstants.REQUEST_CODE.ADD_SSN);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dob = (month + 1) + "/" + dayOfMonth + "/" + year;
        profileViewModel.updateProfile(0, "", "", "", "", getDocumentString(mImageList), "", "", dob, "", "", "", "", "", "", AppConstants.REQUEST_CODE.ADD_DOB);
    }

    private void initializeNetverifySDK() {
        try {

            // Call the method isSupportedPlatform to check if the device is supported.
            if (!NetverifySDK.isSupportedPlatform(getActivity()))
                Log.w(TAG, "Device not supported");

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
            if (NetverifySDK.isRooted(getActivity()))
                Log.w(TAG, "Device is rooted");
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            netverifySDK = NetverifySDK.create(getActivity(), AppConstants.JUMIO_CONSTENT.API_TOKEN, AppConstants.JUMIO_CONSTENT.API_SECERT_KEY, JumioDataCenter.US);

            // Enable ID verification to receive a verification status and verified data positions (see Callback chapter).
            // Note: Not possible for accounts configured as Fastfill only.
            netverifySDK.setRequireVerification(true);
//            netverifySDK.setPreselectedCountry("USA");
            // You can disable face match during the ID verification for a specific transaction.
            netverifySDK.setRequireFaceMatch(true);

        } catch (PlatformNotSupportedException | NullPointerException e) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e);
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            netverifySDK = null;
        }
    }
}