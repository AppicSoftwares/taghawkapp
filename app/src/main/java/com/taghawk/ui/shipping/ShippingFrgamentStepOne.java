package com.taghawk.ui.shipping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taghawk.R;
import com.taghawk.adapters.ShippingAddressesAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.countrypicker.CountryCodeSelectionActivity;
import com.taghawk.custom_dialog.CustomAddressTypeDialog;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentShippingStepOneBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.AddressDataItem;
import com.taghawk.model.AddressUpdateResponse;
import com.taghawk.model.BillingAddressDataItem;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.ShippingAddressesResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ShippingFrgamentStepOne extends BaseFragment implements View.OnClickListener {

    public static FragmentShippingStepOneBinding mBinding;
    private Activity mActivity;
    private ShippingViewModel mShippingViewModel;
    private HashMap<String, Object> parms;
    private String addressType;

    private int shipStatus = 0;
    public static String shipId = "";
    public static int selectedAddressStatus = 0;
    private ArrayList<BillingAddressDataItem> addressList = new ArrayList<>();
    private String id = "+1";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentShippingStepOneBinding.inflate(inflater, container, false);
        initView();
        setListener();
        return mBinding.getRoot();
    }

    private void setListener() {
        mBinding.tvContinueToReview.setOnClickListener(this);
        mBinding.etAddressType.setOnClickListener(this);
        mBinding.imageViewAddAddress.setOnClickListener(this);
        mBinding.tvCountryCode.setOnClickListener(this);
    }

    private void initView() {
        mActivity = getActivity();
        mBinding.etZip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBinding.etZip.getText().toString().trim().length() >= 5) {
                    mBinding.etCity.setText(AppUtils.getCityName(mActivity, mBinding.etZip.getText().toString().trim()));
                    mBinding.etState.setText(AppUtils.getStateName(mActivity, mBinding.etZip.getText().toString().trim()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewModel();
    }

    private void setUpViewModel() {
        mShippingViewModel = ViewModelProviders.of(this).get(ShippingViewModel.class);
        mShippingViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mShippingViewModel.getValidateLiveData().observe(this, new Observer<FailureResponse>() {
            @Override
            public void onChanged(@Nullable FailureResponse failureResponse) {
                showToastShort(failureResponse.getErrorMessage());
            }
        });
        mShippingViewModel.getmSendDataToNextStepLiveModel().observe(this, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(@Nullable HashMap<String, Object> hashMap) {
                if (parms == null) {
                    parms = new HashMap<>();
                }
                parms = hashMap;
                moveToNextFragment(parms);
            }
        });
        mShippingViewModel.getAddressesLiveData().observe(this, new Observer<ShippingAddressesResponse>() {
            @Override
            public void onChanged(@Nullable ShippingAddressesResponse response) {
                getLoadingStateObserver().onChanged(false);
                if (response.getStatusCode() == 200) {
                    shipId = "";
                    if (response.getData() != null && response.getData().size() > 0) {
                        addressList.clear();
                        mBinding.linearLayoutAddressForm.setVisibility(View.GONE);
                        mBinding.linearLayoutAddressList.setVisibility(View.VISIBLE);
                        mBinding.recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
                        addressList = (ArrayList<BillingAddressDataItem>) response.getData();
                        mBinding.recyclerViewAddresses.setAdapter(new ShippingAddressesAdapter(mActivity, addressList, mShippingViewModel, "shipping"));
                    } else {
                        addressList.clear();
                        mBinding.linearLayoutAddressForm.setVisibility(View.VISIBLE);
                        mBinding.linearLayoutAddressList.setVisibility(View.GONE);
                        mBinding.checkBoxSaveAddress.setVisibility(View.VISIBLE);
                        mBinding.checkBoxSaveAddress.setChecked(true);

                    }
                } else {
                    showToastShort(response.getMessage());
                }
                setAllFieldsBlank();
            }
        });

        mShippingViewModel.getDeleteAddressLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
//                showToastShort(commonResponse.getMessage());
                getLoadingStateObserver().onChanged(false);
                if (commonResponse.getCode() == 200) {
                    if (AppUtils.isInternetAvailable(mActivity))
                        mShippingViewModel.getShippingAddresses(DataManager.getInstance().getUserDetails().getUserId());
                    else
                        showNoNetworkError();
                } else {
                    showToastShort(commonResponse.getMessage());
                }
            }
        });
        mShippingViewModel.getUpdateAddressLiveData().observe(this, new Observer<AddressUpdateResponse>() {
            @Override
            public void onChanged(@Nullable AddressUpdateResponse addressUpdateResponse) {
//                showToastShort(commonResponse.getMessage());
                getLoadingStateObserver().onChanged(false);
                if (addressUpdateResponse.getStatusCode() == 200) {
                    mShippingViewModel.moveToStepTwo(getActivity(), mBinding.etFullName.getText().toString().trim(), mBinding.etStreetAddress.getText().toString().trim(), mBinding.etApartment.getText().toString().trim(), mBinding.etZip.getText().toString().trim(), mBinding.etCity.getText().toString().trim(), mBinding.etState.getText().toString().trim(), mBinding.etVerificationPhone.getText().toString().trim(), mBinding.tvCountryCode.getText().toString().trim(), mBinding.etAddressType.getText().toString().trim(), 0, selectedAddressStatus, "");
                } else {
                    showToastShort(addressUpdateResponse.getMessage());
                }
            }
        });
        if (AppUtils.isInternetAvailable(mActivity))
            mShippingViewModel.getShippingAddresses(DataManager.getInstance().getUserDetails().getUserId());
        else
            showNoNetworkError();

    }

    private void moveToNextFragment(HashMap<String, Object> parms) {
        ((ShippingActivity) mActivity).addReviewFragmentStepTwo(parms);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_continue_to_review:
                if (!TextUtils.isEmpty(shipId)) {
                    mShippingViewModel.updateAddressLiveData(getActivity(), shipId, selectedAddressStatus, mBinding.etFullName.getText().toString().trim(), mBinding.etStreetAddress.getText().toString().trim(), mBinding.etApartment.getText().toString().trim(), mBinding.etZip.getText().toString().trim(), mBinding.etCity.getText().toString().trim(), mBinding.etState.getText().toString().trim(), "" + mBinding.tvCountryCode.getText().toString().trim() + mBinding.etVerificationPhone.getText().toString().trim(), mBinding.tvCountryCode.getText().toString().trim(), mBinding.etAddressType.getText().toString().trim());
                } else {
                    if (mBinding.checkBoxSaveAddress.isChecked())
                        shipStatus = 1;
                    else
                        shipStatus = 0;

                    if (addressList.size() > 0)
                        selectedAddressStatus = 0;
                    else
                        selectedAddressStatus = 1;
                    mShippingViewModel.moveToStepTwo(getActivity(), mBinding.etFullName.getText().toString().trim(), mBinding.etStreetAddress.getText().toString().trim(), mBinding.etApartment.getText().toString().trim(), mBinding.etZip.getText().toString().trim(), mBinding.etCity.getText().toString().trim(), mBinding.etState.getText().toString().trim(), mBinding.etVerificationPhone.getText().toString().trim(), mBinding.tvCountryCode.getText().toString().trim(), mBinding.etAddressType.getText().toString().trim(), shipStatus, selectedAddressStatus, "");
//                    mShippingViewModel.getShippingAddresses(DataManager.getInstance().getUserDetails().getUserId());
                }
                break;
            case R.id.et_address_type:
                new CustomAddressTypeDialog(mActivity, false, new OnDialogViewClickListener() {
                    @Override
                    public void onSubmit(String txt, int id) {
                        addressType = txt;
                        mBinding.etAddressType.setText(txt);
                    }
                }).show();
                break;
            case R.id.image_view_add_address:
                setAllFieldsBlank();
                shipId = "";
                mBinding.linearLayoutAddressForm.setVisibility(View.VISIBLE);
                mBinding.linearLayoutAddressList.setVisibility(View.GONE);
                mBinding.checkBoxSaveAddress.setVisibility(View.VISIBLE);
                mBinding.checkBoxSaveAddress.setChecked(true);
                break;
            case R.id.tv_country_code:
                Intent countryCodeSelection = new Intent(getActivity(), CountryCodeSelectionActivity.class);
                startActivityForResult(countryCodeSelection, AppConstants.COUNTRY_CODE);
                break;
        }
    }

    public static void updateAddress(BillingAddressDataItem addressData) {

        shipId = addressData.get_id();
        selectedAddressStatus = addressData.getSelectedStatus();
        mBinding.etFullName.setText("" + addressData.getContact_name());
        mBinding.etStreetAddress.setText("" + addressData.getStreet1());
        mBinding.etApartment.setText("" +  (!TextUtils.isEmpty(addressData.getStreet2())?addressData.getStreet2():""));
        mBinding.etZip.setText("" + addressData.getPostal_code());
        mBinding.etCity.setText("" + addressData.getCity());
        mBinding.etState.setText("" + addressData.getState());
        if (addressData.getType().equalsIgnoreCase("business"))
            mBinding.etAddressType.setText("Office");
        else
            mBinding.etAddressType.setText("Residential");
        mBinding.etVerificationPhone.setText("" + addressData.getPhone().substring((addressData.getPhone().length()-10)));
        mBinding.tvCountryCode.setText(addressData.getPhone().substring(0, addressData.getPhone().length()-10));

        mBinding.checkBoxSaveAddress.setVisibility(View.GONE);
    }

    private void setAllFieldsBlank() {
        mBinding.etFullName.setText("");
        mBinding.etStreetAddress.setText("");
        mBinding.etApartment.setText("");
        mBinding.etZip.setText("");
        mBinding.etCity.setText("");
        mBinding.etState.setText("");
        mBinding.etAddressType.setText("");
        mBinding.etVerificationPhone.setText("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.COUNTRY_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String name = data.getStringExtra("Name");
                    id = data.getStringExtra("Id");
                    if (mBinding != null) {
                        mBinding.tvCountryCode.setText(id);
                    }
                }
                break;
        }
    }

}
