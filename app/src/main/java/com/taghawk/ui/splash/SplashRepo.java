package com.taghawk.ui.splash;


import com.taghawk.data.DataManager;

public class SplashRepo {


    public String getAccessToken() {
        return DataManager.getInstance().getAccessToken();
    }
}
