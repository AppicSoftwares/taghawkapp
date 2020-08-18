package com.taghawk.ui.tag;


import android.location.Address;
import android.util.Patterns;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.R;
import com.taghawk.Repository.TagRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.AddTagStepOneBinding;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.request.User;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tagaddresponse.AddTagResponse;
import com.taghawk.util.ResourceUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddTagViewModel extends ViewModel {

    private TagRepo mTagRepo = new TagRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> loading;
    private Observer<FailureResponse> mFailureObserver;
    private RichMediatorLiveData<AddTagResponse> mAddTagLiveData;

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    private void initLiveData() {
        if (mAddTagLiveData == null) {
            mAddTagLiveData = new RichMediatorLiveData<AddTagResponse>() {
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

    public void proceedTagRequest(AddTagStepOneBinding mBinding, ImageList imageList, String type2, int tagverificationtype, int tagTypeId, Address location, boolean isEdit, String tagId, boolean isRewards, String gPayId) {
        if (validate(mBinding, imageList, tagverificationtype, location)) {
            HashMap<String, Object> parms = new HashMap<>();
            parms.put(AppConstants.TAG_KEY_CONSTENT.NAME, mBinding.etTagName.getText().toString());
            if (mBinding.llTypeContainer.getVisibility() == View.VISIBLE) {
                switch (tagverificationtype) {
                    case 1:
                        parms.put(AppConstants.TAG_KEY_CONSTENT.EMAIL, mBinding.etVerificationEmail.getText().toString());
                        break;
                    case 2:
                        parms.put(AppConstants.TAG_KEY_CONSTENT.PASSWORD, mBinding.etVerificationPassword.getText().toString());
                        break;
                    case 3:
                        parms.put(AppConstants.TAG_KEY_CONSTENT.DOCUMENT_TYPE, mBinding.etVerificationDocument.getText().toString());
                        break;
                }
                parms.put(AppConstants.TAG_KEY_CONSTENT.JOIN_TAG_BY, tagverificationtype);
            }
            if (imageList != null) {
                HashMap<String, String> imageParams = new HashMap<>();
                imageParams.put(AppConstants.TAG_KEY_CONSTENT.URL, imageList.getUrl());
                imageParams.put(AppConstants.TAG_KEY_CONSTENT.THUMB_URL, imageList.getThumbUrl());
                parms.put(AppConstants.TAG_KEY_CONSTENT.IMAGE_URL, new JSONObject(imageParams));
            }
            parms.put(AppConstants.TAG_KEY_CONSTENT.TYPE, tagTypeId);

            parms.put(AppConstants.TAG_KEY_CONSTENT.POINTS_CHARGED, 3000);
            parms.put(AppConstants.TAG_KEY_CONSTENT.DESCRIPTION, mBinding.etTagDescription.getText().toString());
            parms.put(AppConstants.TAG_KEY_CONSTENT.ADDRESS, mBinding.etTagLocation.getText().toString());
            parms.put(AppConstants.TAG_KEY_CONSTENT.CITY, location.getAdminArea());
            parms.put(AppConstants.TAG_KEY_CONSTENT.LAT, location.getLatitude());
            parms.put(AppConstants.TAG_KEY_CONSTENT.LONG, location.getLongitude());
            parms.put(AppConstants.TAG_KEY_CONSTENT.ANNOUNCEMENT, mBinding.etTagAnnouncement.getText().toString().trim());


            parms.put("subType", type2);


            loading.onChanged(true);
            if (isEdit) {
                parms.put(AppConstants.KEY_CONSTENT.COMMUNITY_ID, tagId);
                mTagRepo.editTag(mAddTagLiveData, parms);
            } else {
                parms.put(AppConstants.KEY_CONSTENT.CURRENCY, AppConstants.CURRENCY_USD);
                if (!isRewards) {
                    parms.put(AppConstants.KEY_CONSTENT.SOURCE, gPayId);
                    parms.put(AppConstants.KEY_CONSTENT.PRICE, AppConstants.TAG_CREATE_AMOUNT);
                    parms.put(AppConstants.KEY_CONSTENT.CREATED_USING, "PAYMENT");
                } else {
                    parms.put(AppConstants.KEY_CONSTENT.CREATED_USING, "POINT");

                }
                mTagRepo.addTag(mAddTagLiveData, parms);
            }
        }

    }


    private boolean validate(AddTagStepOneBinding mBinding, ImageList imageList, int tagverificationtype, Address location) {

        if (imageList == null) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_upload_tag_image)));
            return false;
        } else if (mBinding.etTagName.getText().toString().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_title)));
            return false;
        } else if (mBinding.etTagType.getText().toString().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_type)));
            return false;
        } /*else if (mBinding.etTagType2.getText().toString().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_type)));
            return false;
        }*/ else if (mBinding.llTypeContainer.getVisibility() == View.VISIBLE && !checkVerificationMethods(mBinding, tagverificationtype)) {
            return false;
        } else if (mBinding.etTagDescription.getText().toString().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_description)));
            return false;
        } else if (mBinding.etTagDescription.getText().toString().length() < 3) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_description_min_text)));
            return false;
        } else if (mBinding.etTagLocation.getText().toString().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_select_location)));
            return false;
        } else if (location == null) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_select_location)));
            return false;
        }
//        else if (location.getLocality() == null) {
//            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.city_name_not_available)));
//            return false;
//        }
        return true;
    }

    private boolean checkVerificationMethods(AddTagStepOneBinding mBinding, int tagverificationtype) {
        switch (tagverificationtype) {
            case 1:
                if (mBinding.etVerificationEmail.getText().toString().length() == 0) {
                    mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_email_domain)));
                    return false;
                } else if (!Patterns.EMAIL_ADDRESS.matcher("abc@" + mBinding.etVerificationEmail.getText().toString()).matches()) {
                    mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_email_domain_valid)));
                    return false;
                }
                break;
            case 2:
                if (mBinding.etVerificationPassword.getText().toString().length() == 0) {
                    mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_password)));
                    return false;
                } else if (mBinding.etVerificationPassword.getText().toString().length() < 6) {
                    mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.minimum_6_character)));
                    return false;
                }
                break;
            case 3:
                if (mBinding.etVerificationDocument.getText().toString().length() == 0) {
                    mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_document_info)));
                    return false;
                }
                break;
            default:
                mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_select_tag_verfication_method)));
                return false;
        }
        return true;
    }


    public boolean validate(AddTagStepOneBinding mBinding, ArrayList<String> mImageList) {
        if (mImageList == null || mImageList.size() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_select_tag_imahe)));
            return false;
        } else if (mBinding.etTagName.getText().toString().trim().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_name)));
            return false;
        } else if (mBinding.etTagType.getText().toString().trim().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_type_)));
            return false;
        } else if (mBinding.etTagDescription.getText().toString().trim().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_description_)));
            return false;
        } else if (mBinding.etTagAnnouncement.getText().toString().trim().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_announcment)));
            return false;
        } else if (mBinding.etTagLocation.getText().toString().trim().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_location)));
            return false;
        } else if (mBinding.etPaymentMethod.getText().toString().trim().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_tag_payment_method)));
            return false;
        }

        return true;
    }

    public void addTagOnFirebase(User user, TagData tagData) {
        DataManager.getInstance().addTagOnFirebase(user, tagData);
    }

    public void editTagOnFirebase(TagData tagData) {
        DataManager.getInstance().editTagOnFirebase(tagData);
    }

    public RichMediatorLiveData<AddTagResponse> mAddTagViewModel() {
        return mAddTagLiveData;
    }
}
