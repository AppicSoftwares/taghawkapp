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
import com.taghawk.databinding.FragmentAddUpdateAddressBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.AddressDataItem;
import com.taghawk.model.AddressUpdateResponse;
import com.taghawk.model.BillingAddressDataItem;
import com.taghawk.model.ShippingAddressesResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.ui.profile.ProfileEditViewModel;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentAddUpdateAddress extends BaseFragment implements View.OnClickListener {

    public static FragmentAddUpdateAddressBinding mBinding;
    private Activity mActivity;
    private ProfileEditViewModel profileEditViewModel;
    private HashMap<String, Object> parms;
    private ArrayList<BillingAddressDataItem> addressList = new ArrayList<>();
    private String addressType;
    private String type = "";
    private BillingAddressDataItem billingAddress = new BillingAddressDataItem();

    public static String shipId = "";
    public static int selectedAddressStatus = 0;
    private String id = "+1";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentAddUpdateAddressBinding.inflate(inflater, container, false);
        initView();
        setListener();
        return mBinding.getRoot();
    }

    private void setListener() {
        mBinding.tvAdd.setOnClickListener(this);
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.etAddressType.setOnClickListener(this);
        mBinding.imageViewAddAddress.setOnClickListener(this);
        mBinding.tvCountryCode.setOnClickListener(this);
    }

    private void initView() {
        mActivity = getActivity();
//        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));

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

    private void setData() {
        mBinding.etStreetAddress.setText(DataManager.getInstance().getAddressLineOne());
        mBinding.etApartment.setText(DataManager.getInstance().getAddressLineTwo());
        mBinding.etState.setText(DataManager.getInstance().getAddressState());
        mBinding.etZip.setText(DataManager.getInstance().getAddressPostalCode());
        mBinding.etCity.setText(DataManager.getInstance().getAddressCity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewModel();
    }

    private void setUpViewModel() {
        profileEditViewModel = ViewModelProviders.of(this).get(ProfileEditViewModel.class);
        profileEditViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        profileEditViewModel.profileViewModel().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                performActionForShipping();
                saveToPrefrence();
            }
        });
        profileEditViewModel.getDeleteAddressLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
//                showToastShort(commonResponse.getMessage());
                getLoadingStateObserver().onChanged(false);
                if (commonResponse.getCode() == 200) {
                    if (AppUtils.isInternetAvailable(mActivity))
                        profileEditViewModel.getShippingAddresses(DataManager.getInstance().getUserDetails().getUserId());
                    else
                        showNoNetworkError();
                } else {
                    showToastShort(commonResponse.getMessage());
                }
            }
        });
        profileEditViewModel.getUpdateAddressLiveData().observe(this, new Observer<AddressUpdateResponse>() {
            @Override
            public void onChanged(@Nullable AddressUpdateResponse addressUpdateResponse) {
//                showToastShort(commonResponse.getMessage());
                getLoadingStateObserver().onChanged(false);
                if (addressUpdateResponse.getStatusCode() == 200) {
                    if (addressUpdateResponse.getData() != null && addressUpdateResponse.getData().size() > 0) {
                        addressList.clear();
                        mBinding.includeHeader.tvTitle.setText(getString(R.string.shipping_address));
                        mBinding.linearLayoutAddressForm.setVisibility(View.GONE);
                        mBinding.linearLayoutAddressList.setVisibility(View.VISIBLE);
                        mBinding.recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
                        addressList = (ArrayList<BillingAddressDataItem>) addressUpdateResponse.getData();
                        mBinding.recyclerViewAddresses.setAdapter(new ShippingAddressesAdapter(mActivity, addressList, profileEditViewModel, "profile"));
                    } else {
                        addressList.clear();
                        mBinding.includeHeader.tvTitle.setText(getString(R.string.add_address));
                        mBinding.tvAdd.setText(getString(R.string.add));
                        mBinding.linearLayoutAddressForm.setVisibility(View.VISIBLE);
                        mBinding.linearLayoutAddressList.setVisibility(View.GONE);
                        mBinding.checkBoxSaveAddress.setVisibility(View.VISIBLE);
                        mBinding.checkBoxSaveAddress.setChecked(true);
                    }
                } else {
                    showToastShort(addressUpdateResponse.getMessage());
                }

            }
        });
        profileEditViewModel.getAddAddressLivedata().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
//                showToastShort(commonResponse.getMessage());
                getLoadingStateObserver().onChanged(false);
                if (commonResponse.getCode() == 200) {
                    profileEditViewModel.getShippingAddresses(DataManager.getInstance().getUserDetails().getUserId());
                } else {
                    showToastShort(commonResponse.getMessage());
                }
            }
        });
        profileEditViewModel.getAddBillingAddressLivedata().observe(this, new Observer<ShippingAddressesResponse>() {
            @Override
            public void onChanged(@Nullable ShippingAddressesResponse commonResponse) {
//                showToastShort(commonResponse.getMessage());
                getLoadingStateObserver().onChanged(false);
                if (commonResponse.getStatusCode() == 200) {
                    billingAddress = commonResponse.getData().get(0);
                    performActionForBilling();
                } else {
                    showToastShort(commonResponse.getMessage());
                }
            }
        });
        profileEditViewModel.getAddressesLiveData().observe(this, new Observer<ShippingAddressesResponse>() {
            @Override
            public void onChanged(@Nullable ShippingAddressesResponse response) {
                getLoadingStateObserver().onChanged(false);
                if (response.getStatusCode() == 200) {
                    if (response.getData() != null && response.getData().size() > 0) {
                        addressList.clear();
                        mBinding.includeHeader.tvTitle.setText(getString(R.string.shipping_address));
                        mBinding.linearLayoutAddressForm.setVisibility(View.GONE);
                        mBinding.linearLayoutAddressList.setVisibility(View.VISIBLE);
                        mBinding.recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
                        addressList = (ArrayList<BillingAddressDataItem>) response.getData();
                        mBinding.recyclerViewAddresses.setAdapter(new ShippingAddressesAdapter(mActivity, addressList, profileEditViewModel, "profile"));
                    } else {
                        addressList.clear();
                        mBinding.includeHeader.tvTitle.setText(getString(R.string.add_address));
                        mBinding.tvAdd.setText(getString(R.string.add));
                        mBinding.linearLayoutAddressForm.setVisibility(View.VISIBLE);
                        mBinding.linearLayoutAddressList.setVisibility(View.GONE);
                        mBinding.checkBoxSaveAddress.setVisibility(View.VISIBLE);
                        mBinding.checkBoxSaveAddress.setChecked(true);
                    }
                    setAllFieldsBlank();
                } else {
                    showToastShort(response.getMessage());
                }
            }
        });
        if(getArguments() !=  null) {
            type = getArguments().getString("type");
            if(type.equalsIgnoreCase("billing_address"))
                billingAddress = getArguments().getParcelable("billingAddress");
            else
                addressList = getArguments().getParcelableArrayList("addressList");
        }
        if(type.equalsIgnoreCase("billing_address")) {
            mBinding.includeHeader.tvTitle.setText(getString(R.string.billing_address));
            mBinding.llAddAddressBtn.setVisibility(View.GONE);
            mBinding.tvAdd.setText(getString(R.string.add));
            mBinding.linearLayoutAddressForm.setVisibility(View.VISIBLE);
            mBinding.linearLayoutAddressList.setVisibility(View.GONE);
            mBinding.checkBoxSaveAddress.setVisibility(View.GONE);
            if(billingAddress != null && billingAddress.getStreet1() != null && !TextUtils.isEmpty(billingAddress.getStreet1())) {
                mBinding.etFullName.setText("" + billingAddress.getContact_name());
                mBinding.etStreetAddress.setText("" + billingAddress.getStreet1());
                mBinding.etApartment.setText("" +  (!TextUtils.isEmpty(billingAddress.getStreet2())?billingAddress.getStreet2():""));
                mBinding.etZip.setText("" + billingAddress.getPostal_code());
                mBinding.etCity.setText("" + billingAddress.getCity());
                mBinding.etState.setText("" + billingAddress.getState());
                if(billingAddress.getType().equalsIgnoreCase("business"))
                    mBinding.etAddressType.setText("Office");
                else
                    mBinding.etAddressType.setText("Residential");
                mBinding.etVerificationPhone.setText("" + billingAddress.getPhone().substring((billingAddress.getPhone().length()-10)));
                mBinding.tvCountryCode.setText(billingAddress.getPhone().substring(0, billingAddress.getPhone().length()-10));

                mBinding.checkBoxSaveAddress.setVisibility(View.GONE);
            }
        }
        else {
            mBinding.llAddAddressBtn.setVisibility(View.VISIBLE);
            if(addressList != null && addressList.size() > 0) {
                mBinding.includeHeader.tvTitle.setText(getString(R.string.shipping_address));
                mBinding.recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
                mBinding.recyclerViewAddresses.setAdapter(new ShippingAddressesAdapter(mActivity, addressList, profileEditViewModel, "profile"));
            } else {
                if (AppUtils.isInternetAvailable(mActivity))
                    profileEditViewModel.getShippingAddresses(DataManager.getInstance().getUserDetails().getUserId());
                else
                    showNoNetworkError();
            }
        }

    }

    private void saveToPrefrence() {
        DataManager.getInstance().saveAddressLineOne(mBinding.etStreetAddress.getText().toString().trim());
        DataManager.getInstance().saveAddressLineTwo(mBinding.etApartment.getText().toString().trim());
        DataManager.getInstance().saveAddressCity(mBinding.etCity.getText().toString().trim());
        DataManager.getInstance().saveAddressPostalCode(mBinding.etZip.getText().toString().trim());
        DataManager.getInstance().saveAddressstate(mBinding.etState.getText().toString().trim());
    }

    private void performActionForShipping() {
        Intent intent = new Intent();
        intent.putExtra(AppConstants.BUNDLE_DATA, addressList);
        mActivity.setResult(Activity.RESULT_OK, intent);
        mActivity.finish();
    }

    private void performActionForBilling() {
        Intent intent = new Intent();
        intent.putExtra(AppConstants.BUNDLE_DATA, billingAddress);
        mActivity.setResult(Activity.RESULT_OK, intent);
        mActivity.finish();
    }

    @NonNull
    private BillingAddressDataItem createAddressModel() {

        BillingAddressDataItem addressData = new BillingAddressDataItem();
        if(addressList.size() > 0) {
            for(int i=0; i<addressList.size(); i++){
                if(addressList.get(i).getSelectedStatus() == 1) {
                    addressData = addressList.get(i);
                    break;
                }
            }
        }
        return addressData;

//        AddressData addressData = new AddressData();
//        addressData.setAddressLineOne(mBinding.etStreetAddress.getText().toString().trim());
//        if (mBinding.etApartment.getText() != null && mBinding.etApartment.getText().length() > 0)
//            addressData.setAddressLineTwo(mBinding.etApartment.getText().toString().trim());
//        addressData.setCity(mBinding.etCity.getText().toString().trim());
//        addressData.setPostalCode(mBinding.etZip.getText().toString().trim());
//        addressData.setState(mBinding.etState.getText().toString().trim());
//        return addressData;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                if(type.equalsIgnoreCase("billing_address")) {
                    profileEditViewModel.addBillingAddress(getActivity(), mBinding.etFullName.getText().toString().trim(), mBinding.etStreetAddress.getText().toString().trim(), mBinding.etApartment.getText().toString().trim(), mBinding.etZip.getText().toString().trim(), mBinding.etCity.getText().toString().trim(), mBinding.etState.getText().toString().trim(), mBinding.etVerificationPhone.getText().toString().trim(), mBinding.tvCountryCode.getText().toString().trim(), mBinding.etAddressType.getText().toString().trim());
                }
                else {
                    if (!TextUtils.isEmpty(shipId)) {
                        //update address api
                        profileEditViewModel.updateAddress(getActivity(), mBinding.etFullName.getText().toString().trim(), mBinding.etStreetAddress.getText().toString().trim(), mBinding.etApartment.getText().toString().trim(), mBinding.etZip.getText().toString().trim(), mBinding.etCity.getText().toString().trim(), mBinding.etState.getText().toString().trim(), "" + mBinding.tvCountryCode.getText().toString().trim() + mBinding.etVerificationPhone.getText().toString().trim(), mBinding.tvCountryCode.getText().toString().trim(), mBinding.etAddressType.getText().toString().trim(), shipId, selectedAddressStatus);
                    } else {
                        if (addressList.size() > 0)
                            selectedAddressStatus = 0;
                        else
                            selectedAddressStatus = 1;
                        //add address api here
                        profileEditViewModel.addProfileAddresses(getActivity(), mBinding.etFullName.getText().toString().trim(), mBinding.etStreetAddress.getText().toString().trim(), mBinding.etApartment.getText().toString().trim(), mBinding.etZip.getText().toString().trim(), mBinding.etCity.getText().toString().trim(), mBinding.etState.getText().toString().trim(), mBinding.etVerificationPhone.getText().toString().trim(), mBinding.tvCountryCode.getText().toString().trim(), mBinding.etAddressType.getText().toString().trim(), selectedAddressStatus);

                    }
                }

                break;
            case R.id.iv_cross:
                if(type.equalsIgnoreCase("billing_address")) {
                    performActionForBilling();
                } else {
                    performActionForShipping();
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
                mBinding.includeHeader.tvTitle.setText(getString(R.string.add_address));
                mBinding.tvAdd.setText(getString(R.string.add));
                mBinding.linearLayoutAddressList.setVisibility(View.GONE);
                mBinding.linearLayoutAddressForm.setVisibility(View.VISIBLE);
                mBinding.checkBoxSaveAddress.setVisibility(View.VISIBLE);
                mBinding.checkBoxSaveAddress.setChecked(true);
                break;
            case R.id.tv_country_code:
//                if(type.equalsIgnoreCase("billing_address")) {
                    Intent countryCodeSelection = new Intent(getActivity(), CountryCodeSelectionActivity.class);
                    startActivityForResult(countryCodeSelection, AppConstants.COUNTRY_CODE);
//                }
                break;

        }
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

    public static void updateAddress(BillingAddressDataItem addressData) {

        mBinding.includeHeader.tvTitle.setText("Shipping Address");
        mBinding.tvAdd.setText("Update");

        shipId = addressData.get_id();
        selectedAddressStatus = addressData.getSelectedStatus();
        mBinding.etFullName.setText("" + addressData.getContact_name());
        mBinding.etStreetAddress.setText("" + addressData.getStreet1());
        mBinding.etApartment.setText("" +  (!TextUtils.isEmpty(addressData.getStreet2())?addressData.getStreet2():""));
        mBinding.etZip.setText("" + addressData.getPostal_code());
        mBinding.etCity.setText("" + addressData.getCity());
        mBinding.etState.setText("" + addressData.getState());
        if(addressData.getType().equalsIgnoreCase("business"))
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

}
