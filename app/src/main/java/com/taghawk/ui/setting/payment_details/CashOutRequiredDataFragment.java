package com.taghawk.ui.setting.payment_details;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dnitinverma.amazons3library.AmazonS3;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.taghawk.R;
import com.taghawk.adapters.AddDocumentAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.camera2basic.RecyclerListener;
import com.taghawk.constants.AppConstants;
import com.taghawk.countrypicker.CountryCodeSelectionActivity;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.BottomSheetDialogJoinByDocumentBinding;
import com.taghawk.databinding.BottomSheetDialogOtpVerifyBinding;
import com.taghawk.databinding.BottomSheetDialogVerifyPhoneBinding;
import com.taghawk.databinding.CashOutRequiredDataFragmentBinding;
import com.taghawk.gallery_picker.ImagesGallery;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.cashout.MerchantDetailBeans;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.profileresponse.AddressData;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.profile.AddUpdateAddressActivity;
import com.taghawk.ui.profile.ProfileEditViewModel;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.PermissionUtility;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CashOutRequiredDataFragment extends BaseFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private CashOutRequiredDataFragmentBinding mBinding;
    private String ssn;
    private BottomSheetDialog mDialog;
    private Activity mActivity;
    private ProfileEditViewModel profileViewModel;
    private String dob;
    private DatePickerDialog calDialog;
    private AddDocumentAdapter addDocumentAdapter;
    private ArrayList<String> mFileDocumentArrayList;
    private AmazonS3 mAmazonS3;
    private ArrayList<ImageList> mImageList = new ArrayList<>();
    private int imageUploadCount;
    private HomeViewModel mHomeViewModel;
    private File documentFile1, backDocument;
    private BottomSheetDialogVerifyPhoneBinding bottomSheetDialogVerifyPhoneBinding;
    private String phoneNumber;
    private double currentAmount;
    private MerchantDetailBeans beans;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = CashOutRequiredDataFragmentBinding.inflate(inflater);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mActivity = getActivity();
        mFileDocumentArrayList = new ArrayList<>();
        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.includeHeader.tvTitle.setVisibility(View.GONE);
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.tvCashOut.setOnClickListener(this);
        mBinding.tvSsnVerify.setOnClickListener(this);
        mBinding.tvDobVerify.setOnClickListener(this);
        mBinding.ivUpload.setOnClickListener(this);
        mBinding.tvPhoneVerify.setOnClickListener(this);
        mBinding.tvDocumentUpload.setOnClickListener(this);
        mBinding.tvAddAddress.setOnClickListener(this);
        setUpViewModel();
        setSpannableText();
        setupView();
        setData();
    }

    private void setupView() {
        mBinding.rvDocuments.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayout.HORIZONTAL, false));
        addDocumentAdapter = new AddDocumentAdapter(mActivity, mFileDocumentArrayList, new RecyclerListener() {
            @Override
            public void onItemClick(View v, int position, String number, boolean flag) {
                mFileDocumentArrayList.remove(position);
                addDocumentAdapter.notifyDataSetChanged();
            }
        });
        mBinding.rvDocuments.setAdapter(addDocumentAdapter);
    }

    // make bold stripe text bold
    private void setSpannableText() {
        String str = getString(R.string.to_be_sure_the_funds_will_be_safely_transferred_to_your_bank_account_stripe_needs_the_following_information_to_confirm_your_identity_at_your_first_cash_out);
        Spannable spannable = new SpannableStringBuilder(str);
        Typeface font = Typeface.createFromAsset(mActivity.getAssets(), "galano_grotesque_bold.otf");
        spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 70, 77, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#6772e4")), 70, 77, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(1.2f), 70, 77, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
        spannable.setSpan(font, 70, 77, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
        mBinding.tvMsgTitle.setText(spannable);


        String str1 = getString(R.string.we_won_t_store_your_personal_information_all_data_are_processed_by_stripe);
        Spannable spannable1 = new SpannableStringBuilder(str1);
        spannable1.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), str1.length() - 7, str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable1.setSpan(new ForegroundColorSpan(Color.parseColor("#6772e4")), str1.length() - 7, str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable1.setSpan(new RelativeSizeSpan(1.2f), str1.length() - 7, str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
        spannable1.setSpan(font, str1.length() - 7, str1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
        mBinding.tvMsgSecond.setText(spannable1);

    }

    private void setData() {
        if (DataManager.getInstance().getDob() != null && DataManager.getInstance().getDob().length() > 0) {
            mBinding.tvDobInfo.setText(DataManager.getInstance().getDob());
            mBinding.tvDobVerify.setText(getString(R.string.edit));
        }
        if (DataManager.getInstance().getSSNnumber() != null && DataManager.getInstance().getSSNnumber().length() > 0) {
//            mBinding.tvSsnInfo.setText(DataManager.getInstance().getDob());
            mBinding.tvSsnInfo.setText("xxx-xx-" + DataManager.getInstance().getSSNnumber());
            mBinding.tvSsnVerify.setText(getString(R.string.edit));

        }
        if (DataManager.getInstance().getPhoneNumber() != null && DataManager.getInstance().getPhoneNumber().length() > 0) {
            mBinding.tvPhoneInfo.setText(DataManager.getInstance().getPhoneNumber());
        }

        if (DataManager.getInstance().isPhoneVerified()) {
            phoneVerified(mBinding.tvPhoneVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.verfied, false);
        } else {
            if (DataManager.getInstance().getPhoneNumber() != null && DataManager.getInstance().getPhoneNumber().length() > 0) {
                phoneVerified(mBinding.tvPhoneVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.verify, true);
            } else {
                phoneVerified(mBinding.tvPhoneVerify, R.drawable.shape_rectangle_theme_fill, R.color.White, R.string.add, true);
            }
        }
        if (DataManager.getInstance().getAddressLineOne() != null && DataManager.getInstance().getAddressLineOne().length() > 0) {
            AddressData addressData = new AddressData();
            addressData.setState(DataManager.getInstance().getAddressState());
            addressData.setPostalCode(DataManager.getInstance().getAddressPostalCode());
            addressData.setCity(DataManager.getInstance().getAddressCity());
            addressData.setAddressLineOne(DataManager.getInstance().getAddressLineOne());
            addressData.setAddressLineTwo(DataManager.getInstance().getAddressLineTwo());
            setAddress(addressData);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_cross:
                mActivity.finish();
                break;
            case R.id.tv_cash_out:
                if (!(DataManager.getInstance().getDob() != null && DataManager.getInstance().getDob().length() > 0)) {
                    showToastShort(getString(R.string.please_select_dob));
                } else if (!((DataManager.getInstance().getSSNnumber() != null && DataManager.getInstance().getSSNnumber().length() > 0) || DataManager.getInstance().getIsPassport())) {
                    showToastShort(getString(R.string.please_upload_ssn_and_upload_your_passport));
                } else if (!DataManager.getInstance().isPhoneVerified()) {
                    showToastShort(getString(R.string.please_add_phone_number));
                } else if (DataManager.getInstance().getAddressLineOne() == null || DataManager.getInstance().getAddressLineOne().length()== 0) {
                    showToastShort(getString(R.string.please_add_address));
                } else {
                    mHomeViewModel.merchantDetails();
                }
                break;
            case R.id.tv_dob_verify:
                openCalender();
                break;
            case R.id.tv_ssn_verify:
                addSSNBottomSheetDialog();
                break;
            case R.id.iv_upload:
                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 301)) {
                    Intent intent = new Intent(mActivity, ImagesGallery.class);
                    intent.putExtra("selectedList", (Serializable) mFileDocumentArrayList);
                    intent.putExtra("title", "Select Image");
                    intent.putExtra("maxSelection", 2); // Optional
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.MULTIPLE_IMAGE_INTENT);
                }
                break;
            case R.id.tv_phone_verify:
                phoneVerifyBottomSheetDialog();
                break;
            case R.id.tv_document_upload:
                if (AppUtils.isInternetAvailable(mActivity)) {
                    if (mFileDocumentArrayList != null && mFileDocumentArrayList.size() > 0) {
                        MultipartBody.Part document = null, document1;
                        document = MultipartBody.Part.createFormData("front", documentFile1.getName(), RequestBody.create(MediaType.parse("image/*"), documentFile1));
                        document1 = MultipartBody.Part.createFormData("back", backDocument.getName(), RequestBody.create(MediaType.parse("image/*"), backDocument));
                        profileViewModel.uploadDocument(document, document1);
                    } else {
                        showToastShort(getString(R.string.please_upload_both_side_of_pic_of_your_passport));
                    }
                } else {
                    showNoNetworkError();
                }
                break;
            case R.id.tv_add_address:
                openAddUpdateAddress();
                break;
        }

    }

    private void openAddUpdateAddress() {
        Intent intent = new Intent(mActivity, AddUpdateAddressActivity.class);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.ADD_UPDATE_ADDRESS);
    }

    private void openCalender() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(calendar.get(Calendar.YEAR) - 13, calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        calDialog = new DatePickerDialog(mActivity, this,
                calendar.get(Calendar.YEAR) - 13, calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        calDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        calDialog.show();
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


                        case AppConstants.REQUEST_CODE.ADD_DOB:
                            mBinding.tvDobInfo.setText(dob);
                            DataManager.getInstance().saveDob(dob);
                            mBinding.tvDobVerify.setText(getString(R.string.edit));
                            break;
                        case AppConstants.REQUEST_CODE.ADD_SSN:
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            DataManager.getInstance().saveSSNNumber(ssn);
                            mBinding.tvSsnInfo.setText("xxx-xx-" + ssn);
                            mBinding.tvSsnVerify.setText(getString(R.string.edit));

                            break;
                        case AppConstants.DOCUMENT_UPLOAD:
                            DataManager.getInstance().saveIsPassport(true);
                            mBinding.tvDocumentUpload.setText("Uploaded");
                            mBinding.tvDocumentUpload.setEnabled(false);
                            break;
                        case AppConstants.REQUEST_CODE.MOBILE_VERIFY:
                            mBinding.tvPhoneInfo.setText(phoneNumber);
                            otpVerifyBottomSheetDialog();
                            break;
                        case AppConstants.REQUEST_CODE.EMAIL_VERIFY:
                            getCustomBottomDialog(getString(R.string.successfully), commonResponse.getMessage(), new OnDialogItemClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                }

                                @Override
                                public void onNegativeBtnClick() {
                                }
                            });
                            break;
                        case AppConstants.REQUEST_CODE.OTP_VERIFY:
                            mBinding.tvPhoneInfo.setText(phoneNumber);
                            DataManager.getInstance().savePhoneVerified(true);
                            DataManager.getInstance().savePhonenNumber(phoneNumber);
                            phoneVerified(mBinding.tvPhoneVerify, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.verfied, false);
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            break;
                    }


                }

            }
        });
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mHomeViewModel.cashOutLiveDAta().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                showToastShort(commonResponse.getMessage());
                getLoadingStateObserver().onChanged(false);
                currentAmount = 0.0;
                DataManager.getInstance().saveCashOutBalance("" + currentAmount);
                mActivity.setResult(Activity.RESULT_OK);
                mActivity.finish();
            }
        });
        mHomeViewModel.merchantDetailLiveData().observe(this, new Observer<MerchantDetailBeans>() {

            @Override
            public void onChanged(@Nullable MerchantDetailBeans merchantDetailBeans) {
                getLoadingStateObserver().onChanged(false);
                beans = merchantDetailBeans;
                if (beans != null && beans.getMerchantDetailData() != null && beans.getMerchantDetailData().getExternalAccountData() != null) {
                    if (beans.getMerchantDetailData().isPayoutsEnabled()) {
                        performAction();
                    } else {
                        getCustomBottomDialog(getString(R.string.accout_pending), getString(R.string.stripe), new OnDialogItemClickListener() {
                            @Override
                            public void onPositiveBtnClick() {
                                mActivity.setResult(Activity.RESULT_OK);
                                mActivity.finish();
                            }

                            @Override
                            public void onNegativeBtnClick() {

                            }
                        });
//                        showToastShort("Your account is in under process. It might take sometime before you start with your first cashout.");
                    }

                }
            }
        });
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dob = (month + 1) + "/" + dayOfMonth + "/" + year;

        profileViewModel.updateProfile(0, "", "", "", "", null, "", "", dob, "", "", "", "", "", "", AppConstants.REQUEST_CODE.ADD_DOB);

    }

    @SuppressLint("WrongConstant")
    public void uploadDocument() {
        mDialog = new BottomSheetDialog(mActivity);
        final BottomSheetDialogJoinByDocumentBinding binding = BottomSheetDialogJoinByDocumentBinding.inflate(LayoutInflater.from(mActivity));
        mDialog.setContentView(binding.getRoot());
        mDialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(mActivity.getResources().getColor(android.R.color.transparent));
        binding.rvAddDocumentImages.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayout.HORIZONTAL, false));
        binding.tvTitle.setText(getString(R.string.upload_first_last_photo_of_passport));
        binding.tvMessage.setVisibility(View.GONE);
        addDocumentAdapter = new AddDocumentAdapter(mActivity, mFileDocumentArrayList, new RecyclerListener() {
            @Override
            public void onItemClick(View v, int position, String number, boolean flag) {
                mFileDocumentArrayList.remove(position);
                addDocumentAdapter.notifyDataSetChanged();
            }
        });
        binding.rvAddDocumentImages.setAdapter(addDocumentAdapter);
        binding.ivAddDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 301)) {
                    Intent intent = new Intent(mActivity, ImagesGallery.class);
                    intent.putExtra("selectedList", (Serializable) mFileDocumentArrayList);
                    intent.putExtra("title", "Select Image");
                    intent.putExtra("maxSelection", 2); // Optional
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.MULTIPLE_IMAGE_INTENT);
                }
            }
        });
        binding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppUtils.isInternetAvailable(mActivity)) {
                    if (mFileDocumentArrayList != null && mFileDocumentArrayList.size() > 0) {
                        MultipartBody.Part document = null, document1;
                        document = MultipartBody.Part.createFormData("front", documentFile1.getName(), RequestBody.create(MediaType.parse("image/*"), documentFile1));
                        document1 = MultipartBody.Part.createFormData("back", backDocument.getName(), RequestBody.create(MediaType.parse("image/*"), backDocument));
                        profileViewModel.uploadDocument(document, document1);
                    } else {
                        showToastShort(getString(R.string.please_upload_both_side_of_pic_of_your_passport));
                    }
                } else {
                    showNoNetworkError();
                }
                mDialog.dismiss();
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


    private void performAction() {
        DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.cash_out), getString(R.string.cash_out_msg), getString(R.string.cash_out), getString(R.string.cencel), new DialogCallback() {
            @Override
            public void submit(String data) {
                if (AppUtils.isInternetAvailable(mActivity)) {
                    if (DataManager.getInstance().getCashOutBalance().length() > 0)
                        mHomeViewModel.cashOutBalance(Double.valueOf(DataManager.getInstance().getCashOutBalance()));
                } else showNoNetworkError();
            }

            @Override
            public void cancel() {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.REQUEST_CODE.MULTIPLE_IMAGE_INTENT:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getStringArrayListExtra("result") != null) {
                        mFileDocumentArrayList.clear();
                        ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                        if (selectionResult != null && selectionResult.size() > 0) {
                            if (selectionResult.size() == 2) {
                                documentFile1 = new File(selectionResult.get(0));
                                backDocument = new File(selectionResult.get(1));
                                mFileDocumentArrayList.addAll(selectionResult);
                                addDocumentAdapter.notifyDataSetChanged();
                            } else {
                                showToastShort(getString(R.string.please_upload_both_side_of_pic_of_your_passport));
                            }
                        }
                    }
                }
                break;
            case AppConstants.ACTIVITY_RESULT.ADD_UPDATE_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {
                    AddressData addressData = (AddressData) data.getExtras().getSerializable(AppConstants.BUNDLE_DATA);
                    if (addressData != null) {
                        setAddress(addressData);
                    }
                }
                break;
        }
    }


    private void setAddress(AddressData addressData) {
        mBinding.tvAddress.setText(addressData.getAddressLineOne() + ", " + addressData.getAddressLineTwo() + "," + addressData.getCity() + " " + addressData.getState() + ", " + addressData.getPostalCode());
        mBinding.tvAddAddress.setText(getString(R.string.edit));
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


        bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setEnabled(false);
        bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setEnabled(true);
//        mBinding.tvPhoneVerify.setText(getString(R.string.add));

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
        return true;
    }


    private void phonVerifyHit() {
        if (bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().length() > 0) {
            phoneNumber = bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().toString().trim();
            profileViewModel.updateProfile(3, "", "", "+1", bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().toString().trim(), null, "", "", "", "", "", "", "", "", "", AppConstants.REQUEST_CODE.MOBILE_VERIFY);
//            mBinding.tvPhoneInfo.setText(bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().toString());
            mDialog.dismiss();
        } else {
            showToastLong(getString(R.string.please_enter_phone_number));
        }
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

    private void phoneVerified(AppCompatButton tvPhoneVerify, int p, int p2, int p3, boolean b) {
        tvPhoneVerify.setBackgroundResource(p);
        tvPhoneVerify.setTextColor(getResources().getColor(p2));
        tvPhoneVerify.setText(getString(p3));
        tvPhoneVerify.setEnabled(b);
    }

}
