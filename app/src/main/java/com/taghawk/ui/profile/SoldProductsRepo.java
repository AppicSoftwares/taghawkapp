package com.taghawk.ui.profile;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.profileresponse.ProfileProductsResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class SoldProductsRepo {
    private int mPage;
    private Integer mTotal;

    public SoldProductsRepo() {
        initPage(0);
    }

    public void initPage(int mPage) {
        this.mPage = mPage;
        mTotal = null;
    }

    public void getProductsList(int size, RichMediatorLiveData<ProfileProductsResponse> mProductsLiveData, String sellerId) {
        if (mTotal == null || mTotal > size) {
            mPage++;
            if (mPage > 1) {
                mProductsLiveData.setValue(getPageLoaderData());
            }
            hitGetProductsAPI(mProductsLiveData, sellerId);
        }
    }

    private void hitGetProductsAPI(final RichMediatorLiveData<ProfileProductsResponse> mArticleListLiveData, String sellerId) {
        DataManager.getInstance().getProfileProducts(generateFaqsParams(sellerId)).enqueue(new NetworkCallback<ProfileProductsResponse>() {
            @Override
            public void onSuccess(ProfileProductsResponse response) {
                if (response.getStatusCode() == 200) {
                    mArticleListLiveData.setValue(response);
                    mTotal = response.getTotal();
                } else
                    mArticleListLiveData.setFailure(getFailureData(response.getStatusCode(), response.getMessage()));
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                mArticleListLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                mArticleListLiveData.setError(t);
            }
        });
    }

    private FailureResponse getFailureData(Integer statusCode, String message) {
        FailureResponse failureResponse = new FailureResponse();
        failureResponse.setErrorCode(statusCode);
        failureResponse.setErrorMessage(message);
        return failureResponse;
    }

    private HashMap<String, Object> generateFaqsParams(String sellerId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("productStatus", AppConstants.PROFILE_PRODUCTS_TYPE.SOLD);
        if (sellerId != null && sellerId.length() > 0)
            params.put(AppConstants.KEY_CONSTENT.USER_ID, sellerId);
        params.put("pageNo", String.valueOf(mPage));
        params.put("limit", String.valueOf(10));
        return params;
    }

    private ProfileProductsResponse getPageLoaderData() {
        ProfileProductsResponse data = new ProfileProductsResponse();
        data.setLoading(true);
        ProductDetailsData addProductData = new ProductDetailsData();
        addProductData.setLoading(true);
        ArrayList<ProductDetailsData> addProductDataList = new ArrayList<>();
        addProductDataList.add(addProductData);
        data.setData(addProductDataList);
        return data;
    }

}
