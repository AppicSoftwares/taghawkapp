package com.taghawk.ui.home.search;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.taghawk.Repository.HomeRepo;
import com.taghawk.base.RichMediatorLiveData;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.SearchModel;
import com.taghawk.model.tag.TagSearchBean;
import com.taghawk.ui.home.HomeFragment;
import com.taghawk.util.FilterManager;

import java.util.HashMap;

public class SearchViewModel extends ViewModel {

    private Observer<Throwable> mErrorObserver;

    private Observer<FailureResponse> mFailureObserver;

    private RichMediatorLiveData<SearchModel> mSeacrhViewModel;
    private RichMediatorLiveData<TagSearchBean> mTagSearchViewModel;

    private Observer<Boolean> loading;
    //Initializing repository class
    private HomeRepo mHomeRepo = new HomeRepo();

    //saving error & failure observers instance
    public void setGenericListeners(Observer<Throwable> errorObserver,
                                    Observer<FailureResponse> failureResponseObserver, Observer<Boolean> loading) {
        this.mErrorObserver = errorObserver;
        this.mFailureObserver = failureResponseObserver;
        this.loading = loading;
        initLiveData();
    }

    /**
     * Method is used to initialize live data objects
     */
    private void initLiveData() {
        if (mSeacrhViewModel == null) {
            mSeacrhViewModel = new RichMediatorLiveData<SearchModel>() {
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
        if (mTagSearchViewModel == null) {
            mTagSearchViewModel = new RichMediatorLiveData<TagSearchBean>() {
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

    public void getSearchSuggestion(HashMap<String, Object> parms, String search, String categoryId, double lat, double lng) {

        if (parms == null) {
            parms = new HashMap<>();
        }
        if (categoryId != null && categoryId.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, categoryId);
        }
        if (lat > 0 && !(parms.containsKey(AppConstants.KEY_CONSTENT.LAT))) {
            parms.put(AppConstants.KEY_CONSTENT.LAT, lat);
            parms.put(AppConstants.KEY_CONSTENT.LONGI, lng);

        }
        parms.put(AppConstants.KEY_CONSTENT.SEARCH_KEY, search);
        mHomeRepo.getSearchSuggestion(mSeacrhViewModel, parms);
    }

    public void getShelfSearchSuggestion(HashMap<String, Object> parms, String search, String categoryId, double lat, double lng, String tagId) {

        if (parms == null) {
            parms = new HashMap<>();
        }
        if (FilterManager.getInstance().getmFilterMap() != null) {
            parms = FilterManager.getInstance().getmFilterMap();
        } else {
            parms.clear();
        }
        if (categoryId != null && categoryId.length() > 0) {
            parms.put(AppConstants.KEY_CONSTENT.CATEGORY_ID, categoryId);
        }
        if (lat > 0 && !(parms.containsKey(AppConstants.KEY_CONSTENT.LAT))) {
            parms.put(AppConstants.KEY_CONSTENT.LAT, lat);
            parms.put(AppConstants.KEY_CONSTENT.LONGI, lng);

        }
        parms.put(AppConstants.KEY_CONSTENT.SEARCH_KEY, search);
        parms.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, tagId);
        mHomeRepo.getSearchSuggestion(mSeacrhViewModel, parms);
    }


    public void getTagSearch(HashMap<String, Object> parms, String search) {

        if (parms == null) {
            parms = new HashMap<>();
        }
        if (search.length() > 0)
            parms.put(AppConstants.KEY_CONSTENT.NAME, search);
        mHomeRepo.getTagSearch(mTagSearchViewModel, parms);
    }

    /**
     * This method gives the log out live data object to {@link HomeFragment}
     *
     * @return {@link #mSeacrhViewModel}
     */
    public RichMediatorLiveData<SearchModel> getmSeacrhViewModel() {
        return mSeacrhViewModel;
    }

    public RichMediatorLiveData<TagSearchBean> getTagSeachViewModel() {
        return mTagSearchViewModel;
    }
}
