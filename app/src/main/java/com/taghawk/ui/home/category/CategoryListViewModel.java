package com.taghawk.ui.home.category;



import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.Repository.CategoryRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.category.CategoryResponse;

/**
 * Created by Appinventiv on 24-01-2019.
 */

public class CategoryListViewModel extends ViewModel {
    CategoryRepo repo = new CategoryRepo();
    private RichMediatorLiveData<CategoryResponse> mCategoryLiveModel;
    private Observer<FailureResponse> mFailureResponseObserver;
    private Observer<Throwable> mErrorObserver;
    private Observer<Boolean> progressLoading;

    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureObserver, Observer<Boolean> isLoading) {
        mFailureResponseObserver = failureObserver;
        mErrorObserver = errorObserver;
        progressLoading = isLoading;
        initLiveData();
    }

    private void initLiveData() {
        mCategoryLiveModel = new RichMediatorLiveData<CategoryResponse>() {
            @Override
            protected Observer<FailureResponse> getFailureObserver() {
                return mFailureResponseObserver;
            }

            @Override
            protected Observer<Throwable> getErrorObserver() {
                return mErrorObserver;
            }
        };
    }

    public void hitGetCategory() {
        progressLoading.onChanged(true);
        repo.getCategoryList(mCategoryLiveModel, false);
    }

    public RichMediatorLiveData<CategoryResponse> getCategoryListViewModel() {
        return mCategoryLiveModel;
    }

}
