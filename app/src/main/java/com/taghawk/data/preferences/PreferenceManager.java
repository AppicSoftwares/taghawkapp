package com.taghawk.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.taghawk.constants.AppConstants;
import com.taghawk.model.request.User;
import com.google.gson.Gson;


public class PreferenceManager {

    private static PreferenceManager mAppSharedPreferenceInstance;
    private SharedPreferences sharedPref;

    private PreferenceManager(Context context) {
        sharedPref = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceManager getInstance(Context context) {
        if (mAppSharedPreferenceInstance == null) {
            synchronized (PreferenceManager.class) {
                if (mAppSharedPreferenceInstance == null)
                    mAppSharedPreferenceInstance = new PreferenceManager(context);
            }
        }
        return mAppSharedPreferenceInstance;
    }


    public int getInt(String key) {
        return sharedPref.getInt(key, 0);
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public String getFilterLongitude(String key) {
        return sharedPref.getString(key, "0");
    }

    public void putFilterLongitude(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);         // Commit the edits!
        editor.apply();
    }

    public String getFilterLatitude(String key) {
        return sharedPref.getString(key, "0");
    }

    public void putFilterLatitude(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);         // Commit the edits!
        editor.apply();
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);         // Commit the edits!
        editor.apply();
    }

    public String getString(String key) {
        return sharedPref.getString(key, null);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void putfargtype(String key) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("gargtype", key);
        editor.apply();
    }
    public String getfargtype() {
        return sharedPref.getString("gargtype", "1");
    }
 public void putfargmaptype(String key) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("gargmaptype", key);
        editor.apply();
    }
    public String getfargmaptype() {
        return sharedPref.getString("gargmaptype", "1");
    }

    /**
     * Method used to get the user details from the SharedPreference
     *
     * @return object of {@link User} which contains all the info about the user
     */
    public User getDetails() {
        User userDetail;
        String response = mAppSharedPreferenceInstance.getString(AppConstants.USER_DETAILS);
        userDetail = new Gson().fromJson(response, User.class);
        return userDetail;
    }

    public boolean getBoolean(String key) {
        return sharedPref.getBoolean(key, false);
    }

    public void clearAllPrefs() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}
