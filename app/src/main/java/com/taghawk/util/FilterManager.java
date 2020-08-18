package com.taghawk.util;

import java.util.HashMap;

public class FilterManager {

    private static final String TAG = "FilterManager";
    private static FilterManager INSTANCE;
    private HashMap<String, Object> mFilterMap;

    public HashMap<String, Object> getmFilterMap() {
        return mFilterMap;
    }

    public void setmFilterMap(HashMap<String, Object> mFilterMap) {
        this.mFilterMap = mFilterMap;
    }

    public static FilterManager getInstance() {
        if (INSTANCE == null) {
            synchronized (FilterManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new FilterManager();
            }
        }
        return INSTANCE;
    }

}
