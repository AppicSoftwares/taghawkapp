package com.taghawk.ui.setting.payment_details;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.TagHawkApplication;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.countrypicker.CountryCodeSelectionActivity;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.BottomSheetDialogJoinByEmailBinding;
import com.taghawk.databinding.BottomSheetDialogOtpVerifyBinding;
import com.taghawk.databinding.BottomSheetDialogVerifyPhoneBinding;
import com.taghawk.databinding.FragmentPayoutDetailBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.AddressDataItem;
import com.taghawk.model.BillingAddressDataItem;
import com.taghawk.model.PayoutVendorRequestModel;
import com.taghawk.model.ShippingAddressesResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.model.profileresponse.UserDetail;
import com.taghawk.ui.profile.AddUpdateAddressActivity;
import com.taghawk.ui.profile.ProfileEditViewModel;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_PASS;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_URL;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_USER;
import static com.taghawk.bluesnap.BlueSnapDetails.VENDORS;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;

public class PayoutDetailFragment extends BaseFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private FragmentPayoutDetailBinding mBinding;
    private Activity mActivity;
    private UserDetail mUserDetail;
    private DatePickerDialog calDialog;
    private String dob;
    private Dialog mDialog;
    BottomSheetDialogVerifyPhoneBinding bottomSheetDialogVerifyPhoneBinding;
    ArrayAdapter<CharSequence> countryAdapter;
    private String phoneNumber;
    private String ssn;
    private ProfileEditViewModel profileViewModel;

    private String accountNumber = "";
    private String routingNumber = "";
    private String accountHolderName = "";
    private String bankName = "";
    private String id = "+1";

    private BillingAddressDataItem billingAddress = new BillingAddressDataItem();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPayoutDetailBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {

        if (getArguments() != null) {
            accountNumber = getArguments().getString("accountNumber");
            routingNumber = getArguments().getString("routingNumber");
            accountHolderName = getArguments().getString("accountHolderName");
            bankName = getArguments().getString("bankName");
            Log.e("bankName", "" + bankName);
        }

        mActivity = getActivity();

        profileViewModel = ViewModelProviders.of(this).get(ProfileEditViewModel.class);
        profileViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());

        countryAdapter = ArrayAdapter.createFromResource(mActivity, R.array.country_value_array, android.R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.includeHeader.tvTitle.setText("Account Holder's Information");
        mBinding.includeHeader.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_12dp));
        mBinding.tvEmailInfo.setText(DataManager.getInstance().getUserDetails().getEmail());
        if (!TextUtils.isEmpty(DataManager.getInstance().getUserDetails().getEmail()))
            mBinding.tvEmailAdd.setText("Edit");
        else
            mBinding.tvEmailAdd.setText("Add");

        if (!TextUtils.isEmpty(DataManager.getInstance().getUserDetails().getPhoneNumber())) {
            mBinding.tvPhoneAdd.setText("Edit");
            mBinding.tvPhoneInfo.setText(DataManager.getInstance().getUserDetails().getPhoneNumber());
        } else
            mBinding.tvPhoneAdd.setText("Add");

        getProfileService();

        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.tvEmailAdd.setOnClickListener(this);
        mBinding.tvPhoneAdd.setOnClickListener(this);
        mBinding.tvBillingAddressAdd.setOnClickListener(this);
        mBinding.tvDateOfBirthAdd.setOnClickListener(this);
        mBinding.tvCashOut.setOnClickListener(this);
        mBinding.tvSsnVerify.setOnClickListener(this);

        mBinding.spinnerVerificationId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (parent.getSelectedItemPosition()) {
                    case 0:
                        mBinding.etDrivingLicenseNo.setVisibility(View.VISIBLE);
                        mBinding.llPassportCountryIssue.setVisibility(View.GONE);
                        mBinding.etPassportNumber.setVisibility(View.GONE);
                        break;
                    case 1:
                        mBinding.etDrivingLicenseNo.setVisibility(View.GONE);
                        mBinding.llPassportCountryIssue.setVisibility(View.VISIBLE);
                        mBinding.etPassportNumber.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        profileViewModel.getmProfileEditLiveData().observe((AppCompatActivity) mActivity, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(@Nullable ProfileResponse profileResponse) {
                getLoadingStateObserver().onChanged(false);
//                profileViewModel.getBillingAddress(DataManager.getInstance().getUserDetails().getUserId());
                if (profileResponse.getCode() == 200) {
                    mUserDetail = profileResponse.getUserDetail();
//                    billingAddress = profileResponse.getUserDetail().getBillingAddress();
                    billingAddress.setSelectedStatus(profileResponse.getUserDetail().getBillingAddress().getSelectedStatus());
                    billingAddress.setType(profileResponse.getUserDetail().getBillingAddress().getType());
                    billingAddress.setPhone(profileResponse.getUserDetail().getBillingAddress().getPhone());
                    billingAddress.setCountry(profileResponse.getUserDetail().getBillingAddress().getCountry());
                    billingAddress.setState(profileResponse.getUserDetail().getBillingAddress().getState());
                    billingAddress.setStreet1(profileResponse.getUserDetail().getBillingAddress().getStreet1());
                    billingAddress.setStreet2(profileResponse.getUserDetail().getBillingAddress().getStreet2());
                    billingAddress.setPostal_code(profileResponse.getUserDetail().getBillingAddress().getPostal_code());
                    billingAddress.setCity(profileResponse.getUserDetail().getBillingAddress().getCity());
                    billingAddress.setEmail(profileResponse.getUserDetail().getBillingAddress().getEmail());
                    billingAddress.setContact_name(profileResponse.getUserDetail().getBillingAddress().getContact_name());
                    billingAddress.set_id(profileResponse.getUserDetail().getBillingAddress().get_id());

                    if (mUserDetail != null) {
                        profileViewModel.updateUserNode(mUserDetail.getId(), mUserDetail.getProfilePicture(), mUserDetail.getFullName(), mUserDetail.getEmail());
                        if (!TextUtils.isEmpty(mUserDetail.getEmail()))
                            mBinding.tvEmailAdd.setText("Edit");
                        else
                            mBinding.tvEmailAdd.setText("Add");

                        if (!TextUtils.isEmpty(mUserDetail.getPhoneNumber())) {
                            mBinding.tvPhoneAdd.setText("Edit");
                            mBinding.tvPhoneInfo.setText(mUserDetail.getPhoneNumber());
                        } else
                            mBinding.tvPhoneAdd.setText("Add");

                        if (!TextUtils.isEmpty(mUserDetail.getDob())) {
                            mBinding.tvDateOfBirthAdd.setText("Edit");
                            mBinding.tvDateOfBirth.setText(mUserDetail.getDob());
                        } else
                            mBinding.tvDateOfBirthAdd.setText("Add");

                        if (!TextUtils.isEmpty(mUserDetail.getSsnNumber())) {
                            mBinding.tvSsnVerify.setText("Edit");
                            ssn = mUserDetail.getSsnNumber();
                            mBinding.tvSsnInfo.setText(mUserDetail.getSsnNumber());
                        } else
                            mBinding.tvSsnVerify.setText("Add");

                        if (profileResponse.getUserDetail().getDrivingLicense() != null && !TextUtils.isEmpty(profileResponse.getUserDetail().getDrivingLicense().trim())) {

                            mBinding.etDrivingLicenseNo.setVisibility(View.VISIBLE);
                            mBinding.llPassportCountryIssue.setVisibility(View.GONE);
                            mBinding.etPassportNumber.setVisibility(View.GONE);
                            mBinding.spinnerVerificationId.setSelection(0);
                            mBinding.etDrivingLicenseNo.setText("" + profileResponse.getUserDetail().getDrivingLicense());
                        } else if(profileResponse.getUserDetail().getPassportNumber() != null && !TextUtils.isEmpty(profileResponse.getUserDetail().getPassportNumber().trim())) {

                            mBinding.etDrivingLicenseNo.setVisibility(View.GONE);
                            mBinding.llPassportCountryIssue.setVisibility(View.VISIBLE);
                            mBinding.etPassportNumber.setVisibility(View.VISIBLE);
                            mBinding.spinnerVerificationId.setSelection(1);
                            int position = countryAdapter.getPosition(profileResponse.getUserDetail().getPassportCountry());
                            mBinding.spinnerPassportCountry.setSelection(position);
                            mBinding.etPassportNumber.setText("" + profileResponse.getUserDetail().getPassportNumber());
                        }

                    }

                    if (billingAddress.getStreet1() != null && !TextUtils.isEmpty(billingAddress.getStreet1())) {
                        if (billingAddress.getStreet2() != null && !TextUtils.isEmpty(billingAddress.getStreet2()))
                            mBinding.tvAddress.setText("" + billingAddress.getStreet1() + ", #" + billingAddress.getStreet2() + ", " + billingAddress.getCity() + ", " + billingAddress.getState() + "\n" + "Zip code: " + billingAddress.getPostal_code());
                        else
                            mBinding.tvAddress.setText("" + billingAddress.getStreet1() + ", " + billingAddress.getCity() + ", " + billingAddress.getState() + "\n" + "Zip code: " + billingAddress.getPostal_code());

                        mBinding.tvBillingAddressAdd.setText(getString(R.string.edit));
                    } else {
                        mBinding.tvBillingAddressAdd.setText(getString(R.string.add));
                    }

                }
            }
        });

        profileViewModel.profileViewModel().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {

                if (commonResponse.getCode() == 200) {

                    switch (commonResponse.getRequestCode()) {

                        case AppConstants.REQUEST_CODE.ADD_DOB:
                            getLoadingStateObserver().onChanged(false);
//                            showToastShort(commonResponse.getMessage());
                            mBinding.tvDateOfBirth.setText(dob);
                            DataManager.getInstance().saveDob(dob);
                            mBinding.tvDateOfBirthAdd.setText(getString(R.string.edit));
                            break;
                        case AppConstants.REQUEST_CODE.ADD_SSN:
                            getLoadingStateObserver().onChanged(false);
//                            showToastShort(commonResponse.getMessage());
                            mBinding.tvSsnInfo.setText("" + ssn);

                            break;
                        case AppConstants.REQUEST_CODE.ADD_DRIVING_PASSPORT:

                            if (mBinding.spinnerVerificationId.getSelectedItemPosition() == 0) {

                                getLoadingStateObserver().onChanged(true);
                                getUpdateVendorPayoutApi(
                                        mBinding.tvEmailInfo.getText().toString().trim(),
                                        mUserDetail.getFirstName(),
                                        mUserDetail.getLastName(),
                                        mBinding.tvPhoneInfo.getText().toString().trim(),
                                        billingAddress,
                                        mBinding.tvDateOfBirth.getText().toString().trim(),
                                        mBinding.etDrivingLicenseNo.getText().toString().trim(),
                                        ssn,
                                        "",
                                        "",
                                        accountHolderName,
                                        bankName,
                                        routingNumber,
                                        accountNumber,
                                        0);

                            } else {

                                int pos = mBinding.spinnerPassportCountry.getSelectedItemPosition();
                                String[] countryCodeArray = getResources().getStringArray(R.array.country_key_array);
                                String country = countryCodeArray[pos];
                                getLoadingStateObserver().onChanged(true);
                                getUpdateVendorPayoutApi(
                                        mBinding.tvEmailInfo.getText().toString().trim(),
                                        mUserDetail.getFirstName(),
                                        mUserDetail.getLastName(),
                                        mBinding.tvPhoneInfo.getText().toString().trim(),
                                        billingAddress,
                                        mBinding.tvDateOfBirth.getText().toString().trim(),
                                        "",
                                        ssn,
                                        country,
                                        mBinding.etPassportNumber.getText().toString().trim(),
                                        accountHolderName,
                                        bankName,
                                        routingNumber,
                                        accountNumber,
                                        1);

                            }
                            break;

//                        case AppConstants.DOCUMENT_UPLOAD:
//                            DataManager.getInstance().saveIsPassport(true);
//                            mBinding.tvDocumentUpload.setText("Uploaded");
//                            mBinding.tvDocumentUpload.setEnabled(false);
//                            break;
                        case AppConstants.REQUEST_CODE.MOBILE_VERIFY:
                            getLoadingStateObserver().onChanged(false);
                            showToastShort(commonResponse.getMessage());
//                            mBinding.tvPhoneInfo.setText(phoneNumber);
                            otpVerifyBottomSheetDialog();
                            break;
                        case AppConstants.REQUEST_CODE.EMAIL_VERIFY:
                            getLoadingStateObserver().onChanged(false);
                            showToastShort(commonResponse.getMessage());
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
                            getLoadingStateObserver().onChanged(false);
                            showToastShort(commonResponse.getMessage());
                            mBinding.tvPhoneInfo.setText(phoneNumber);
                            DataManager.getInstance().savePhoneVerified(true);
                            DataManager.getInstance().savePhonenNumber(phoneNumber);
                            phoneVerified(mBinding.tvPhoneAdd, R.drawable.shape_rectangle_snow_fill, R.color.White, R.string.verfied, false);
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                            break;
                    }


                }

            }
        });

        profileViewModel.getBillingAddressesLiveData().observe(this, new Observer<ShippingAddressesResponse>() {
            @Override
            public void onChanged(@Nullable ShippingAddressesResponse response) {
                getLoadingStateObserver().onChanged(false);
                if (response.getStatusCode() == 200) {
                    if (response.getData() != null && response.getData().size() > 0) {
                        billingAddress = response.getData().get(0);
                        if (billingAddress.getStreet2() != null && !TextUtils.isEmpty(billingAddress.getStreet2()))
                            mBinding.tvAddress.setText("" + billingAddress.getStreet1() + ", #" + billingAddress.getStreet2() + ", " + billingAddress.getCity() + ", " + billingAddress.getState() + "\n" + "Zip code: " + billingAddress.getPostal_code());
                        else
                            mBinding.tvAddress.setText("" + billingAddress.getStreet1() + ", " + billingAddress.getCity() + ", " + billingAddress.getState() + "\n" + "Zip code: " + billingAddress.getPostal_code());

                        mBinding.tvBillingAddressAdd.setText(getString(R.string.edit));
                    } else {
                        if (mUserDetail.getAddressData() != null) {
                            mBinding.tvBillingAddressAdd.setText(getString(R.string.add));
                        }
                    }
                } else {
                    showToastShort(response.getMessage());
                }
            }
        });

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                mActivity.finish();
                break;
            case R.id.tv_email_add:
                if (mBinding.tvEmailAdd.getText().toString().length() > 0) {
                    profileViewModel.updateProfile(2, "", mBinding.tvEmailAdd.getText().toString(), "", "", null, "", "", "", "", "", "", "", "", "", AppConstants.REQUEST_CODE.EMAIL_VERIFY);
                } else {
                    emailVerifyBottomSheetDialog();
                }
                break;
            case R.id.tv_phone_add:
                phoneVerifyBottomSheetDialog();
                break;
            case R.id.tv_billing_address_add:
                openAddUpdateBillingAddress();
                break;
            case R.id.tv_date_of_birth_add:
                openCalender();
                break;
            case R.id.tv_ssn_verify:
                addSSNBottomSheetDialog();
                break;
            case R.id.tv_cash_out:

                if (TextUtils.isEmpty(mBinding.tvEmailInfo.getText().toString().trim())) {
                    showToastLong("Please add your email address");
                } else if (TextUtils.isEmpty(mBinding.tvPhoneInfo.getText().toString().trim())) {
                    showToastLong("Please provide your phone number");
                } else if (/*billingAddress != null && */billingAddress.getStreet1() == null || TextUtils.isEmpty(billingAddress.getStreet1())) {
                    showToastLong("Please add your billing address");
                } else if (TextUtils.isEmpty(mBinding.tvDateOfBirth.getText().toString().trim())) {
                    showToastLong("Please provide your date of birth");
                } else if(mBinding.tvSsnInfo.getText().toString().trim().length() <4) {
                    showToastLong("Please provide your last 4 digits of SSN");
                } else if (TextUtils.isEmpty(mBinding.etDrivingLicenseNo.getText().toString().trim())
                        && TextUtils.isEmpty(mBinding.etPassportNumber.getText().toString().trim())) {
                    showToastLong("Please provide any one verified Id");
                } else {
                    getLoadingStateObserver().onChanged(true);
                    mBinding.tvCashOut.setEnabled(false);
                    if (mBinding.spinnerVerificationId.getSelectedItemPosition() == 0) {
                        profileViewModel.updateProfile(2, "", "", "", "", null, "", "", "", "", "", mBinding.etDrivingLicenseNo.getText().toString().trim(), "", "", "", AppConstants.REQUEST_CODE.ADD_DRIVING_PASSPORT);
                    } else {
                        profileViewModel.updateProfile(2, "", "", "", "", null, "", "", "", "", "", "", "", mBinding.spinnerPassportCountry.getSelectedItem().toString(), mBinding.etPassportNumber.getText().toString().trim(), AppConstants.REQUEST_CODE.ADD_DRIVING_PASSPORT);
                    }
                }

//                DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, "BlueSnap Process", "The BlueSnap Cash Out process is currently under development. You will get facility very soon.", new OnDialogItemClickListener() {
//                    @Override
//                    public void onPositiveBtnClick() {
//
//                    }
//
//                    @Override
//                    public void onNegativeBtnClick() {
//
//                    }
//                });
                break;
        }
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

        if (mUserDetail != null && mBinding.tvPhoneInfo.getText().toString().trim().length() > 0) {
            bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setText(mUserDetail.getPhoneNumber());
            bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setEnabled(true);
            bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setText(mBinding.tvPhoneInfo.getText().toString().trim());
            mBinding.tvPhoneAdd.setText("Edit");
        } else {
            bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.setEnabled(true);
            mBinding.tvPhoneAdd.setText("Add");
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


//                mBinding.tvPhoneInfo.setText("" + bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().toString().trim());
//                mDialog.dismiss();
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
            profileViewModel.updateProfile(3, "", "", bottomSheetDialogVerifyPhoneBinding.tvCountryCode.getText().toString().trim(), bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().toString().trim(), null, "", "", "", "", "", "", "", "", "", AppConstants.REQUEST_CODE.MOBILE_VERIFY);
//            mBinding.tvPhoneInfo.setText(bottomSheetDialogVerifyPhoneBinding.etVerificationPhone.getText().toString());
            mDialog.dismiss();
        } else {
            showToastLong(getString(R.string.please_enter_phone_number));
        }
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if ((month + 1) < 10) {
            if(dayOfMonth < 10)
                dob = "0" + dayOfMonth + "-0" + (month + 1) + "-" + year;
            else
                dob = dayOfMonth + "-0" + (month + 1) + "-" + year;
        } else {
            if(dayOfMonth < 10)
                dob = "0" + dayOfMonth + "-" + (month + 1) + "-" + year;
            else
                dob = dayOfMonth + "-" + (month + 1) + "-" + year;
        }
        mBinding.tvDateOfBirth.setText(dob);
        profileViewModel.updateProfile(3, "", "", "", "", null, "", "", dob, "", "", "", "", "", "", AppConstants.REQUEST_CODE.ADD_DOB);
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
                    mDialog.dismiss();
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
                        mDialog.dismiss();
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
                    mDialog.dismiss();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDialog.show();
    }

    private void openAddUpdateBillingAddress() {
        Intent intent = new Intent(mActivity, AddUpdateAddressActivity.class);
        intent.putExtra("type", "billing_address");
        intent.putExtra("billingAddress", billingAddress);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.ADD_BILLING_ADDRESS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.ADD_BILLING_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {
                    billingAddress = data.getExtras().getParcelable(AppConstants.BUNDLE_DATA);
                    if (billingAddress.getStreet1() != null && !TextUtils.isEmpty(billingAddress.getStreet1())) {
                        if (billingAddress.getStreet2() != null && !TextUtils.isEmpty(billingAddress.getStreet2()))
                            mBinding.tvAddress.setText("" + billingAddress.getStreet1() + ", #" + billingAddress.getStreet2() + ", " + billingAddress.getCity() + ", " + billingAddress.getState() + "\n" + "Zip code: " + billingAddress.getPostal_code());
                        else
                            mBinding.tvAddress.setText("" + billingAddress.getStreet1() + ", " + billingAddress.getCity() + ", " + billingAddress.getState() + "\n" + "Zip code: " + billingAddress.getPostal_code());

                        mBinding.tvBillingAddressAdd.setText(getString(R.string.edit));
                    } else {
                        mBinding.tvBillingAddressAdd.setText(getString(R.string.add));
                    }
//                    profileViewModel.getBillingAddress(DataManager.getInstance().getUserDetails().getUserId());
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
            mBinding.tvEmailAdd.setText(getString(R.string.verify));
        } else {
            binding.etVerificationEmail.setEnabled(false);
            binding.etVerificationEmail.setEnabled(true);
            mBinding.tvEmailAdd.setText(getString(R.string.add));
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

    private void getUpdateVendorPayoutApi(String email, String firstName, String lastName, String phoneNumber, BillingAddressDataItem billingAddress, String dob, String drivingLicense, String ssnNumber, String passportCountry, String passportNumber, String nameOnAccount, String bankName, String bankId, String bankAccountId, int verificationType) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getLoadingStateObserver().onChanged(true);

            }
        }, 10);
        PayoutVendorRequestModel requestModel = new PayoutVendorRequestModel();
        requestModel.setEmail(email);
        requestModel.setName("" + firstName + " " + lastName);
        requestModel.setFirstName(firstName);
        requestModel.setLastName(lastName);
        requestModel.setPhone(phoneNumber);
        requestModel.setAddress("" + billingAddress.getStreet2() + ", " + billingAddress.getStreet1());
        requestModel.setCity(billingAddress.getCity());
        requestModel.setCountry("US");
        requestModel.setZip(billingAddress.getPostal_code());
        requestModel.setState(AppUtils.getStateShortName(billingAddress.getState()));
        requestModel.setDefaultPayoutCurrency("USD");

        PayoutVendorRequestModel.VendorPrincipal vendorPrincipal = new PayoutVendorRequestModel.VendorPrincipal();
        vendorPrincipal.setFirstName(firstName);
        vendorPrincipal.setLastName(lastName);
        vendorPrincipal.setAddress("" + billingAddress.getStreet2() + ", " + billingAddress.getStreet1());
        vendorPrincipal.setCity(billingAddress.getCity());
        vendorPrincipal.setZip(billingAddress.getPostal_code());
        vendorPrincipal.setCountry("US");
        vendorPrincipal.setDob(dob);
        vendorPrincipal.setPersonalIdentificationNumber(Integer.parseInt(ssnNumber));
        if(verificationType == 0) {
            vendorPrincipal.setDriverLicenseNumber(drivingLicense);
        } else {
            vendorPrincipal.setCountry(passportCountry);
            vendorPrincipal.setPassportNumber(passportNumber);
        }
        vendorPrincipal.setEmail(email);
        requestModel.setVendorPrincipal(vendorPrincipal);

        PayoutVendorRequestModel.VendorAgreement vendorAgreement = new PayoutVendorRequestModel.VendorAgreement();
        vendorAgreement.setCommissionPercent(92);

        ArrayList<PayoutVendorRequestModel.PayoutInfo> payoutInfoList = new ArrayList<>();
        PayoutVendorRequestModel.PayoutInfo payoutInfo = new PayoutVendorRequestModel.PayoutInfo();
        payoutInfo.setPayoutType("ACH");
        payoutInfo.setBaseCurrency("USD");
        payoutInfo.setNameOnAccount(nameOnAccount);
        payoutInfo.setBankAccountType("CHECKING");
        payoutInfo.setBankAccountClass("PERSONAL");
        payoutInfo.setBankName(bankName);
        payoutInfo.setBankId(bankId);
        payoutInfo.setCountry("US");
        payoutInfo.setState(AppUtils.getStateShortName(billingAddress.getState()));
        payoutInfo.setCity(billingAddress.getCity());
        payoutInfo.setAddress("" + billingAddress.getStreet2() + ", " + billingAddress.getStreet1());
        payoutInfo.setBankAccountId(bankAccountId);
        payoutInfo.setMinimalPayoutAmount(1000000);
        payoutInfoList.add(payoutInfo);
        requestModel.setPayoutInfo(payoutInfoList);

        String updateVendorJson = new Gson().toJson(requestModel);
        Log.e("updateVendorJson", "" + updateVendorJson);

        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        List<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));

        List<CustomHTTPParams> sandboxHttpHeaders = headerParams;

        String vendorId = DataManager.getInstance().getVendorId();

        final Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
                    ArrayList<CustomHTTPParams> headerParams = new ArrayList<>();
                    headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
                    BlueSnapHTTPResponse response = HTTPOperationController.put(SANDBOX_URL + VENDORS + "/" + vendorId, updateVendorJson, "application/json", "application/json", sandboxHttpHeaders);
                    Log.e("API", SANDBOX_URL + VENDORS + "/" + vendorId);
                    Log.e("API_HEADER", "Authorization : " + basicAuth);
                    String responseString = response.getResponseString();

                    if (response.getResponseCode() == HTTP_NO_CONTENT) {
//                        String location = response.getHeaders().get("Location").get(0);

                        Log.e("API_RESPONSE", "successfully updated");
//                        retrieveVendorJson = new JSONObject(responseString);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("*****Done*****");

                                String message = "You have successfully updated your account verification details.";
                                DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, "Profile Updated!", message, new OnDialogItemClickListener() {
                                    @Override
                                    public void onPositiveBtnClick() {
                                        mActivity.finish();
                                    }

                                    @Override
                                    public void onNegativeBtnClick() {

                                    }
                                });
                                getLoadingStateObserver().onChanged(false);
                                mBinding.tvCashOut.setEnabled(true);

                            }
                        }, 10);

                    } else {

                        Log.e("RESPONSE", responseString);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("*****Done*****");
                                DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, "Profile Update Error", "Some details are not valid, please try again with proper details.", new OnDialogItemClickListener() {
                                    @Override
                                    public void onPositiveBtnClick() {

                                    }

                                    @Override
                                    public void onNegativeBtnClick() {

                                    }
                                });
                                getLoadingStateObserver().onChanged(false);
                                mBinding.tvCashOut.setEnabled(true);
                            }
                        }, 10);
                    }

                } catch (Exception e) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("*****Done*****");
                            getLoadingStateObserver().onChanged(false);
                            mBinding.tvCashOut.setEnabled(true);
//                            DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.card_error_info), new OnDialogItemClickListener() {
//                                @Override
//                                public void onPositiveBtnClick() {
//
//                                }
//
//                                @Override
//                                public void onNegativeBtnClick() {
//
//                                }
//                            });
                        }
                    }, 10);
                }

            }
        };

        TagHawkApplication.mainHandler.post(myRunnable);


    }

}
