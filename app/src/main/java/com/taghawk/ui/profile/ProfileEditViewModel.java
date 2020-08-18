package com.taghawk.ui.profile;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.R;
import com.taghawk.Repository.HomeRepo;
import com.taghawk.Repository.ShippingRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.AddressUpdateResponse;
import com.taghawk.model.DeleteAddressRequest;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.ShippingAddressesResponse;
import com.taghawk.model.cashout.MerchantDetailBeans;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.util.AppUtils;
import com.taghawk.util.ResourceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MultipartBody;

public class ProfileEditViewModel extends ViewModel {

    private ProfileEditRepo mProfileRepo = new ProfileEditRepo();
    private ShippingRepo shippingRepo = new ShippingRepo();
    private RichMediatorLiveData<ProfileResponse> mProfileEditLiveData;
    private HomeRepo mHomeRepo = new HomeRepo();
    private ProfileRepo mRepo = new ProfileRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> loading;
    private Observer<FailureResponse> mFailureObserver;
    private RichMediatorLiveData<CommonResponse> mProfileLiveData;
    private RichMediatorLiveData<MerchantDetailBeans> merchantDetailsLiveData;
    private RichMediatorLiveData<AddressUpdateResponse> updateAddressLiveData;
    private RichMediatorLiveData<CommonResponse> deleteAddressLiveData;
    private RichMediatorLiveData<ShippingAddressesResponse> mAddressesLiveData;
    private RichMediatorLiveData<CommonResponse> addAddressLiveData;
    private RichMediatorLiveData<ShippingAddressesResponse> addBillingAddressLiveData;
    private RichMediatorLiveData<ShippingAddressesResponse> mBillingAddressesLiveData;

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    private void initLiveData() {
        if (mProfileLiveData == null) {
            mProfileLiveData = new RichMediatorLiveData<CommonResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mProfileEditLiveData == null) {
            mProfileEditLiveData = new RichMediatorLiveData<ProfileResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (merchantDetailsLiveData == null) {
            merchantDetailsLiveData = new RichMediatorLiveData<MerchantDetailBeans>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (updateAddressLiveData == null) {
            updateAddressLiveData = new RichMediatorLiveData<AddressUpdateResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (deleteAddressLiveData == null) {
            deleteAddressLiveData = new RichMediatorLiveData<CommonResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mAddressesLiveData == null) {
            mAddressesLiveData = new RichMediatorLiveData<ShippingAddressesResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (addAddressLiveData == null) {
            addAddressLiveData = new RichMediatorLiveData<CommonResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (addBillingAddressLiveData == null) {
            addBillingAddressLiveData = new RichMediatorLiveData<ShippingAddressesResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }
        if (mBillingAddressesLiveData == null) {
            mBillingAddressesLiveData = new RichMediatorLiveData<ShippingAddressesResponse>() {
                @Override
                protected Observer<FailureResponse> getFailureObserver() {
                    return mFailureObserver;
                }

                @Override
                protected Observer<Throwable> getErrorObserver() {
                    return mErrorObserver;
                }
            };
        }

    }

    public void updateAddress(String streetAdrress, String apartment, String zipCode, String city, String state) {
        if (validate(streetAdrress, zipCode, city, state)) {
            loading.onChanged(true);
            HashMap<String, Object> parms = new HashMap<>();
            parms.put(AppConstants.KEY_CONSTENT.ADDRESS_LINE, streetAdrress);
            parms.put(AppConstants.KEY_CONSTENT.ADDRESS_LINE_TWO, apartment);
            parms.put(AppConstants.KEY_CONSTENT.ZIP_CODE, zipCode);
            parms.put(AppConstants.KEY_CONSTENT.CITY, city);
            parms.put(AppConstants.KEY_CONSTENT.STATE, state);
            parms.put(AppConstants.KEY_CONSTENT.STATUS, 5);
            mProfileRepo.updateProfile(mProfileLiveData, parms, 333);
        }
    }

    public void updateAddress(Context context, String fullName, String streetAddress, String apartment, String zipCode, String city, String state, String phoneNumber, String countryCode, String addressType, String shipId, int selectedStatus) {
        if (validate(fullName, streetAddress, zipCode, city, state, phoneNumber)) {
            loading.onChanged(true);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(AppConstants.KEY_CONSTENT.CONTACT_NAME, fullName);
            hashMap.put(AppConstants.KEY_CONSTENT.EMAIL, DataManager.getInstance().getUserDetails().getEmail());
            hashMap.put(AppConstants.KEY_CONSTENT.CITY, city);
            hashMap.put(AppConstants.KEY_CONSTENT.STATE, state);
            hashMap.put(AppConstants.KEY_CONSTENT.ZIP_CODE, zipCode);
            hashMap.put(AppConstants.KEY_CONSTENT.STEET1, streetAddress);
            if(!TextUtils.isEmpty(apartment))
                hashMap.put(AppConstants.KEY_CONSTENT.STEET2, apartment);
            hashMap.put(AppConstants.KEY_CONSTENT.PHONE, phoneNumber);
            if(addressType.equalsIgnoreCase("office"))
                hashMap.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "business");
            else
                hashMap.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "residential");
            String cCode = AppUtils.getCountryCode(context, zipCode);
            hashMap.put(AppConstants.KEY_CONSTENT.COUNTRY, cCode);
            hashMap.put(AppConstants.KEY_CONSTENT.SHIP_ID, shipId);
            hashMap.put(AppConstants.KEY_CONSTENT.SELECTED_STATUS, selectedStatus);
            Log.e("JSON", "" + hashMap.toString());
            shippingRepo.updateShippingAddress(updateAddressLiveData, hashMap);
        }
    }

    public void addProfileAddresses(Context context, String fullName, String streetAdrress, String apartment, String zipCode, String city, String state, String phoneNumber, String countryCode, String type, int selectedStatus) {
        if (validate(fullName, streetAdrress, zipCode, city, state, phoneNumber)) {
            loading.onChanged(true);
            JSONObject object = new JSONObject();
            try {
                object.put(AppConstants.KEY_CONSTENT.CONTACT_NAME, fullName);
                if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getEmail() != null)
                    object.put(AppConstants.KEY_CONSTENT.EMAIL, DataManager.getInstance().getUserDetails().getEmail());
                object.put(AppConstants.KEY_CONSTENT.CITY, city);
                object.put(AppConstants.KEY_CONSTENT.STATE, state);
                object.put(AppConstants.KEY_CONSTENT.ZIP_CODE, zipCode);
                object.put(AppConstants.KEY_CONSTENT.STEET1, streetAdrress);
                if(!TextUtils.isEmpty(apartment))
                    object.put(AppConstants.KEY_CONSTENT.STEET2, apartment);
                object.put(AppConstants.KEY_CONSTENT.PHONE, countryCode + "" + phoneNumber);
                if (type.equalsIgnoreCase("office")) {
                    object.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "business");
                } else {
                    object.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "residential");
                }
                String cCode = AppUtils.getCountryCode(context, zipCode);
                object.put(AppConstants.KEY_CONSTENT.COUNTRY, cCode);
                object.put(AppConstants.KEY_CONSTENT.SELECTED_STATUS, selectedStatus);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HashMap<String, Object> parms = new HashMap<>();
            parms.put(AppConstants.KEY_CONSTENT.SHIP_TO, object);
            Log.e("JSON", "" + object.toString());
            mProfileRepo.addProfileAddresses(addAddressLiveData, parms);
        }
    }

    public void addBillingAddress(Context context, String fullName, String streetAdrress, String apartment, String zipCode, String city, String state, String phoneNumber, String countryCode, String type) {
        if (validate(fullName, streetAdrress, zipCode, city, state, phoneNumber)) {
            loading.onChanged(true);
            HashMap<String, Object> parms = new HashMap<>();

            parms.put(AppConstants.KEY_CONSTENT.CONTACT_NAME, fullName);
            if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getEmail() != null)
                parms.put(AppConstants.KEY_CONSTENT.EMAIL, DataManager.getInstance().getUserDetails().getEmail());
            parms.put(AppConstants.KEY_CONSTENT.CITY, city);
            parms.put(AppConstants.KEY_CONSTENT.STATE, state);
            parms.put(AppConstants.KEY_CONSTENT.ZIP_CODE, zipCode);
            parms.put(AppConstants.KEY_CONSTENT.STEET1, streetAdrress);
            if(!TextUtils.isEmpty(apartment))
                parms.put(AppConstants.KEY_CONSTENT.STEET2, apartment);
            parms.put(AppConstants.KEY_CONSTENT.PHONE, countryCode + "" + phoneNumber);
            if (type.equalsIgnoreCase("office")) {
                parms.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "business");
            } else {
                parms.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "residential");
            }
            String cCode = AppUtils.getCountryCode(context, zipCode);
            if(!TextUtils.isEmpty(cCode))
                parms.put(AppConstants.KEY_CONSTENT.COUNTRY, cCode);
            else
                parms.put(AppConstants.KEY_CONSTENT.COUNTRY, "US");

            Log.e("JSON", "" + parms.toString());
            mProfileRepo.addBillingAddress(addBillingAddressLiveData, parms);
        }
    }

    public void deleteShippingAddress(DeleteAddressRequest deleteAddressRequest) {
        loading.onChanged(true);
        shippingRepo.deleteShippingAddress(deleteAddressLiveData, deleteAddressRequest);
    }

    public void updateAddressLiveData(String shipId, int selectedStatus, String fullName, String streetAddress, String apartment, String zipCode, String city, String state, String phoneNumber, String countryCode, String addressType) {
        loading.onChanged(true);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(AppConstants.KEY_CONSTENT.CONTACT_NAME, fullName);
        hashMap.put(AppConstants.KEY_CONSTENT.EMAIL, DataManager.getInstance().getUserDetails().getEmail());
        hashMap.put(AppConstants.KEY_CONSTENT.CITY, city);
        hashMap.put(AppConstants.KEY_CONSTENT.STATE, state);
        hashMap.put(AppConstants.KEY_CONSTENT.ZIP_CODE, zipCode);
        hashMap.put(AppConstants.KEY_CONSTENT.STEET1, streetAddress);
        hashMap.put(AppConstants.KEY_CONSTENT.STEET2, apartment);
        hashMap.put(AppConstants.KEY_CONSTENT.PHONE, phoneNumber);
        if (addressType.equalsIgnoreCase("office"))
            hashMap.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "business");
        else
            hashMap.put(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE, "residential");
        hashMap.put(AppConstants.KEY_CONSTENT.COUNTRY, countryCode);
        hashMap.put(AppConstants.KEY_CONSTENT.SHIP_ID, shipId);
        hashMap.put(AppConstants.KEY_CONSTENT.SELECTED_STATUS, selectedStatus);
        shippingRepo.updateShippingAddress(updateAddressLiveData, hashMap);
    }

    public void updateProfile(int status, String socialId, String email, String countryCode, String mobile, String documents, String name, String profileImage, String dob, String firstName, String lastName, String drivingLicense, String ssn,  String passportCountry, String passportNumber, int requestCode) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.STATUS, status);
        if (socialId.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.FACEBOOK_ID, socialId);
        } else if (documents != null && documents.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.DOCUMENTS, documents);
        } else if (email.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.EMAIL, email);
        } else if (mobile.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.PHONE_NUMBER, mobile);
            parms.put(AppConstants.KEY_CONSTENT.COUNTRY_CODE, countryCode);
        }
        if (name.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.FULL_NAME, name);
        }
        if (profileImage != null && profileImage.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.PROFILE_PICTURE, profileImage);
        }
        if (firstName != null && firstName.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.FIRST_NAME, firstName);
        }
        if (lastName != null && lastName.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.LAST_NAME, lastName);
        }
        if (dob != null && dob.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.DOB, dob);
        }
        if (ssn != null && ssn.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.SSN, ssn);
        }
        if (ssn != null && drivingLicense.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.DRIVING_LICENSE, drivingLicense);
            parms.put(AppConstants.KEY_CONSTENT.PASSPORT_COUNTRY, " ");
            parms.put(AppConstants.KEY_CONSTENT.PASSPORT_NUMBER, " ");
        }
        if (ssn != null && passportNumber.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.DRIVING_LICENSE, " ");
            parms.put(AppConstants.KEY_CONSTENT.PASSPORT_COUNTRY, passportCountry);
            parms.put(AppConstants.KEY_CONSTENT.PASSPORT_NUMBER, passportNumber);
        }
        mProfileRepo.updateProfile(mProfileLiveData, parms, requestCode);

    }

    public void uploadDocument(MultipartBody.Part documentImage, MultipartBody.Part backDocument) {
        loading.onChanged(true);
        mProfileRepo.uploadDocument(mProfileLiveData, documentImage, backDocument, AppConstants.DOCUMENT_UPLOAD);
    }

    public void updateProfile(int status, String scanReference, int requestCode) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.STATUS, status);
        parms.put(AppConstants.KEY_CONSTENT.DOCUMENT_REFERENCE_ID, scanReference);
        mProfileRepo.updateProfile(mProfileLiveData, parms, requestCode);
    }

    public void getShippingAddresses(String userId) {
        loading.onChanged(true);
        shippingRepo.getShippingAddresses(mAddressesLiveData, userId);
    }

    public void getBillingAddress(String userId) {
        loading.onChanged(true);
        shippingRepo.getBillingAddress(mBillingAddressesLiveData, userId);
    }

    public void verifyOtp(String otp, int requestCode) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.OTP, otp);
        mProfileRepo.otpVerification(mProfileLiveData, parms, requestCode);
    }

    private boolean validate(String streetAdrress, String zipCode, String city, String state) {
        if (streetAdrress == null || streetAdrress.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.please_Enter_address_line)));
            return false;
        } else if (streetAdrress.length() < 6) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.street_address_must_be_six_character)));
            return false;
        } else if (zipCode == null || zipCode.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.please_Enter_zip_code)));
            return false;
        } else if (zipCode.length() < 5) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.zip_code_must_be_5_digit)));
            return false;
        } else if (city == null || city.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.city_can_not_be_empty)));
            return false;
        } else if (state == null || state.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.state_can_not_be_empty)));

            return false;
        }
        return true;
    }

    private boolean validate(String fullName, String streetAdrress, String zipCode, String city, String state, String phoneNumber) {
        if (fullName == null || fullName.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.please_Enter_full_name)));
            return false;
        } else if (streetAdrress == null || streetAdrress.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.please_Enter_street_address)));
            return false;
        } /*else if (streetAdrress.length() < 6) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.street_address_must_be_six_character)));
            return false;
        }*/ else if (zipCode == null || zipCode.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.please_Enter_zip_code)));
            return false;
        } else if (zipCode.length() < 5) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.zip_code_must_be_5_digit)));
            return false;
        } else if (city == null || city.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.city_can_not_be_empty)));
            return false;
        } else if (state == null || state.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.state_can_not_be_empty)));
            return false;
        } else if (phoneNumber == null || phoneNumber.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.please_enter_phone_number)));
            return false;
        } else if (phoneNumber.length() < 7) {
            mFailureObserver.onChanged(new FailureResponse(AppConstants.UIVALIDATIONS.COMMEN_ERROR, ResourceUtils.getInstance().getString(R.string.enter_valid_phone)));
            return false;
        }
        return true;
    }

    public void merchantDetails() {
        loading.onChanged(true);
        mHomeRepo.merchantDetail(merchantDetailsLiveData, 111);
    }

    public void getProfile(HashMap<String, Object> params, int requestCode) {

        mRepo.getProfile(mProfileEditLiveData, params, requestCode);
    }

    public void updateUserNode(String userId, String profilePicture, String fullName, String email) {
        DataManager.getInstance().updateUserNodeOnEditProfile(userId, profilePicture, fullName, email);
    }

    public RichMediatorLiveData<ProfileResponse> getmProfileEditLiveData() {
        return mProfileEditLiveData;
    }

    public RichMediatorLiveData<MerchantDetailBeans> merchantDetailLiveData() {
        return merchantDetailsLiveData;
    }

    public RichMediatorLiveData<CommonResponse> profileViewModel() {
        return mProfileLiveData;
    }

    public RichMediatorLiveData<AddressUpdateResponse> getUpdateAddressLiveData() {
        return updateAddressLiveData;
    }

    public RichMediatorLiveData<CommonResponse> getDeleteAddressLiveData() {
        return deleteAddressLiveData;
    }

    public RichMediatorLiveData<ShippingAddressesResponse> getAddressesLiveData() {
        return mAddressesLiveData;
    }

    public RichMediatorLiveData<ShippingAddressesResponse> getBillingAddressesLiveData() {
        return mBillingAddressesLiveData;
    }

    public RichMediatorLiveData<CommonResponse> getAddAddressLivedata() {
        return addAddressLiveData;
    }

    public RichMediatorLiveData<ShippingAddressesResponse> getAddBillingAddressLivedata() {
        return addBillingAddressLiveData;
    }

}
