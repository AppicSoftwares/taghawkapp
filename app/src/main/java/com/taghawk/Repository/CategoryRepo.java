package com.taghawk.Repository;

import com.taghawk.base.NetworkCallback;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.data.DataManager;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.category.CategoryResponse;

/**
 * Created by Appinventiv on 24-01-2019.
 */

public class CategoryRepo {

    // This Api is used for getting list of category
    public void getCategoryList(final RichMediatorLiveData<CategoryResponse> categoryLiveData, final boolean showDialog) {
        DataManager.getInstance().getCategoryList().enqueue(new NetworkCallback<CategoryResponse>() {
            @Override
            public void onSuccess(CategoryResponse categoryResponse) {
                categoryResponse.setShowDialog(showDialog);
                categoryLiveData.setValue(categoryResponse);
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                categoryLiveData.setFailure(failureResponse);
            }

            @Override
            public void onError(Throwable t) {
                categoryLiveData.setError(t);
            }
        });
    }
}
