package com.taghawk.ui.create;

import android.location.Address;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.R;
import com.taghawk.Repository.AddProductRepo;
import com.taghawk.Repository.CategoryRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.AddProduct.AddProductModel;
import com.taghawk.model.AddProduct.ChooseWeightModel;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.category.CategoryResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tag.UserSpecificTagsModel;
import com.taghawk.util.ResourceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddProductViewModel extends ViewModel {
    CategoryRepo repo = new CategoryRepo();
    private Observer<Throwable> mErrorObserver;
    private Observer<FailureResponse> mFailureObserver;

    private RichMediatorLiveData<AddProductModel> mAddProductViewModel;
    private RichMediatorLiveData<AddProductModel> mEditProductViewModel;
    private RichMediatorLiveData<UserSpecificTagsModel> mUSerSpecificTags;
    private RichMediatorLiveData<HashMap<String, Object>> mValidateData;
    private RichMediatorLiveData<CommonResponse> mFetureLiveData;
    private Observer<Boolean> loading;
    //Initializing repository class
    private RichMediatorLiveData<CategoryResponse> mCategoryLiveModel;
    AddProductRepo addProductRepo = new AddProductRepo();

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    private void initLiveData() {
        if (mAddProductViewModel == null) {
            mAddProductViewModel = new RichMediatorLiveData<AddProductModel>() {
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
        if (mValidateData == null) {
            mValidateData = new RichMediatorLiveData<HashMap<String, Object>>() {
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
        if (mCategoryLiveModel == null) {
            mCategoryLiveModel = new RichMediatorLiveData<CategoryResponse>() {
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
        if (mUSerSpecificTags == null) {
            mUSerSpecificTags = new RichMediatorLiveData<UserSpecificTagsModel>() {
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
        if (mFetureLiveData == null) {
            mFetureLiveData = new RichMediatorLiveData<CommonResponse>() {
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
        if (mEditProductViewModel == null) {
            mEditProductViewModel = new RichMediatorLiveData<AddProductModel>() {
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

    public void addProductRequest(String title, String category, String categoryId, String etProductPrice, int condition, String discrption, int shippingType, Address location, boolean isFirm, HashMap<Integer, TagData> mSharedTagData, ArrayList<String> imageLists, int tvDelivery, int tvPickup, int tvShipping, boolean isTransactionFee, boolean rbFedex, boolean rbUsps, ChooseWeightModel model,String state,String city) {

        if (validate(title, category, etProductPrice, tvPickup, tvDelivery, tvShipping, location, imageLists, model, rbFedex, rbUsps, discrption)) {
            HashMap<String, Object> parms = new HashMap<>();
            parms.put(AppConstants.KEY_CONSTENT.TITLE, title);
            parms.put(AppConstants.KEY_CONSTENT.PRODUCT_CATEGORY_ID, categoryId);
            parms.put(AppConstants.KEY_CONSTENT.FIRM_PRICE, etProductPrice);
            if (isFirm) {
                parms.put(AppConstants.KEY_CONSTENT.IS_NEGOTIABLE, true);
            } else
                parms.put(AppConstants.KEY_CONSTENT.IS_NEGOTIABLE, false);
            parms.put(AppConstants.KEY_CONSTENT.IS_TRANSACTION_COST, isTransactionFee);
            if (discrption.length() > 0) {
                parms.put(AppConstants.KEY_CONSTENT.DESCRIPTION, discrption);
            }
            parms.put(AppConstants.KEY_CONSTENT.SHIPPING_AVAILIBILITY, getShippingString(tvPickup, tvDelivery, tvShipping));
            parms.put(AppConstants.KEY_CONSTENT.LOCATION, location.getAddressLine(0));
            parms.put("state", state);

            parms.put("city", city);


            Log.d("kjdgfa",city+""+state);
            parms.put(AppConstants.KEY_CONSTENT.LAT, location.getLatitude());
            parms.put(AppConstants.KEY_CONSTENT.LONGI, location.getLongitude());
            if (condition > 0) {
                parms.put(AppConstants.KEY_CONSTENT.CONDITION, condition);
            }
            if (mSharedTagData.size() > 0) {
                parms.put(AppConstants.KEY_CONSTENT.SHARED_COMMUNITIES, mSharedTagData);
            }
            if (tvShipping == 3) {
                if (model != null) {
                    parms.put(AppConstants.KEY_CONSTENT.WEIGHT, model.getWeight());
                    if (rbFedex) {
                        parms.put(AppConstants.KEY_CONSTENT.SHIPPING_PRICE, model.getFedexPrice());
                        parms.put(AppConstants.KEY_CONSTENT.SHIPPING_TYPE, AppConstants.FEDEX);
                    } else {
                        parms.put(AppConstants.KEY_CONSTENT.SHIPPING_TYPE, AppConstants.USPS);
                    }
                }

            }
            mValidateData.setValue(parms);
        }
    }


    public void editProduct(String title, String category, String categoryId, String etProductPrice, int condition, String discrption, Address location, boolean isFirm, ArrayList<String> imageLists, String productId, int pickup, int deliver, int shipping, ImageList[] Images, boolean isTransactionFee, boolean rbFedex, boolean rbUsps, ChooseWeightModel model) {

        if (validate(title, category, etProductPrice, pickup, deliver, shipping, location, imageLists, model, rbFedex, rbUsps, discrption)) {
            HashMap<String, Object> parms = new HashMap<>();
            parms.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
            parms.put(AppConstants.KEY_CONSTENT.TITLE, title);
            parms.put(AppConstants.KEY_CONSTENT.PRODUCT_CATEGORY_ID, categoryId);
            parms.put(AppConstants.KEY_CONSTENT.FIRM_PRICE, etProductPrice);
            parms.put(AppConstants.KEY_CONSTENT.IS_TRANSACTION_COST, isTransactionFee);
            if (isFirm) {
                parms.put(AppConstants.KEY_CONSTENT.IS_NEGOTIABLE, true);
            } else
                parms.put(AppConstants.KEY_CONSTENT.IS_NEGOTIABLE, false);
            if (discrption.length() > 0) {
                parms.put(AppConstants.KEY_CONSTENT.DESCRIPTION, discrption);
            }
//            Integer[] arr = new Integer[]{Integer.valueOf(shippingType)};
            parms.put(AppConstants.KEY_CONSTENT.SHIPPING_AVAILIBILITY, getShippingString(pickup, deliver, shipping));
            parms.put(AppConstants.KEY_CONSTENT.IMAGES, getJson(imageLists, Images));
            parms.put(AppConstants.KEY_CONSTENT.LOCATION, location.getAddressLine(0));
            parms.put(AppConstants.KEY_CONSTENT.LAT, location.getLatitude());
            parms.put(AppConstants.KEY_CONSTENT.LONGI, location.getLongitude());
            if (shipping == 3) {
                if (model != null) {
                    parms.put(AppConstants.KEY_CONSTENT.WEIGHT, model.getWeight());
                    if (rbFedex) {
                        parms.put(AppConstants.KEY_CONSTENT.SHIPPING_PRICE, model.getFedexPrice());
                        parms.put(AppConstants.KEY_CONSTENT.SHIPPING_TYPE, AppConstants.FEDEX);
                    } else {
                        parms.put(AppConstants.KEY_CONSTENT.SHIPPING_TYPE, AppConstants.USPS);
                    }
                }

            }
            if (condition > 0) {
                parms.put(AppConstants.KEY_CONSTENT.CONDITION, condition);
            }

            if (Images != null && Images.length > 0) {
                updateProduct(parms);
            } else
                mValidateData.setValue(parms);
        }
    }

    public void updateProduct(HashMap<String, Object> parms) {
        addProductRepo.editProduct(mEditProductViewModel, parms);

    }

    private String getJson(ArrayList<String> imageLists, ImageList[] image) {
        if (imageLists == null) {
            imageLists = new ArrayList<>();
        }
        if (image != null && image.length > 0) {
            for (int i = 0; i < image.length; i++) {
                imageLists.add(image[i].getUrl());
            }
        }
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < imageLists.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("thumbUrl", imageLists.get(i));
                object.put("url", imageLists.get(i));
                jsonArray.put(object);
            }
            return jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getShippingString(int pickup, int deliver, int shipping) {
        ArrayList<String> mList = new ArrayList<>();
        if (pickup > 0) {
            mList.add(String.valueOf(pickup));
        }
        if (deliver > 0) {
            mList.add(String.valueOf(deliver));
        }
        if (shipping > 0) {
            mList.add(String.valueOf(shipping));
        }
        String str = "";
        for (int i = 0; i < mList.size(); i++) {
            if (i == 0) {
                str = mList.get(i);
            } else {
                str = str + "," + mList.get(i);
            }
        }
        return str;
    }


    public String getJson(ArrayList<ImageList> imageLists) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < imageLists.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("thumbUrl", imageLists.get(i).getThumbUrl());
                object.put("url", imageLists.get(i).getUrl());
                jsonArray.put(object);
            }
            return jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean validate(String title, String category, String etProductPrice, int tvPickup, int tvDelivery, int shipping, Address location, ArrayList<String> mImageList, ChooseWeightModel model, boolean rbFedex, boolean rbUsps, String description) {

        if (mImageList == null || mImageList.size() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_upload_product_image)));
            return false;
        } else if (title.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_product_title)));
            return false;
        } else if (category.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_product_category)));
            return false;
        } else if (etProductPrice.length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_product_price)));
            return false;
        } else if (Double.valueOf(etProductPrice) != 0 && Double.valueOf(etProductPrice) < 0.8) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.price_should_grater_then_zeo)));
            return false;
        } else if (tvPickup == 0 && tvDelivery == 0 && shipping == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_select_shipping_type)));
            return false;
        } else if (description.trim().length() == 0) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_enter_description)));
            return false;
        } else if (Double.valueOf(etProductPrice) == 0 && shipping == 3) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.shipping_is_not_avaliable_for_price_0_product)));
            return false;
        } else if (shipping == 3 && model == null) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_select_weight)));
            return false;
        } else if (shipping == 3 && !rbFedex && !rbUsps) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_select_shipping_mode)));
            return false;
        }
//        else if (shipping == 3 && rbFedex && !model.isAvailableInFedex()) {
//            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.selected_weight_is_not_available)));
//            return false;
//        } else if (shipping == 3 && rbUsps && !model.isAvailableInUsps()) {
//            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.selected_weight_is_not_available)));
//            return false;
//        }
        else if (location == null) {
            mFailureObserver.onChanged(new FailureResponse(1, ResourceUtils.getInstance().getString(R.string.please_select_location)));
            return false;
        }
        return true;
    }

    public void addProductRequest(HashMap<String, Object> parms) {
        loading.onChanged(true);
        addProductRepo.addProduct(mAddProductViewModel, parms);
    }

    public RichMediatorLiveData<AddProductModel> getmAddProductViewModel() {
        return mAddProductViewModel;
    }

    public RichMediatorLiveData<HashMap<String, Object>> getmValidateData() {
        return mValidateData;
    }

    public RichMediatorLiveData<CategoryResponse> getCategoryListViewModel() {
        return mCategoryLiveModel;
    }

    public RichMediatorLiveData<UserSpecificTagsModel> getmUSerSpecificTags() {
        return mUSerSpecificTags;
    }

    public void hitGetCategory(boolean showDialog) {
        if (showDialog)
            loading.onChanged(true);
        repo.getCategoryList(mCategoryLiveModel, showDialog);
    }

    public void getUserSpecificTags(int limit, int pageno) {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.LIMIT, limit);
        parms.put(AppConstants.KEY_CONSTENT.PAGE_NO, pageno);
        addProductRepo.getUserSpecificTags(mUSerSpecificTags, parms);
    }

    public void markProductFeatured(String productId, String sourceId, int days, double price, HashMap<Integer, TagData> mSharedTagMap, boolean isShareTag) {
        loading.onChanged(true);
        HashMap<String, Object> parms = new HashMap<>();
        parms.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, productId);
        parms.put(AppConstants.KEY_CONSTENT.SOURCE, sourceId);
        parms.put(AppConstants.KEY_CONSTENT.CURRENCY, AppConstants.CURRENCY_USD);
        parms.put(AppConstants.KEY_CONSTENT.DAYS, days);
        if (isShareTag && mSharedTagMap != null && mSharedTagMap.size() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.PRICE, price + mSharedTagMap.size());
            parms.put(AppConstants.KEY_CONSTENT.SHARED_COMMUNITIES, getSharedTagId(mSharedTagMap));
        } else
            parms.put(AppConstants.KEY_CONSTENT.PRICE, price);
        addProductRepo.markProductFeatured(mFetureLiveData, parms);
    }

    private String getSharedTagId(HashMap<Integer, TagData> mSharedTagMap) {
        String sharedTag = "";
        ArrayList<TagData> listOfValues = new ArrayList<TagData>();
        listOfValues.addAll(mSharedTagMap.values());
        for (int i = 0; i < listOfValues.size(); i++) {
            if (i == 0)
                sharedTag = listOfValues.get(i).getTagId();
            else
                sharedTag = sharedTag + "," + listOfValues.get(i).getTagId();
        }
        return sharedTag;
    }

    public RichMediatorLiveData<CommonResponse> getFeaturedLiveData() {
        return mFetureLiveData;
    }

    public RichMediatorLiveData<AddProductModel> getEditLiveData() {
        return mEditProductViewModel;
    }
}
