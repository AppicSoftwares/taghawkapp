package com.taghawk.fb;


import com.facebook.FacebookException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Rajat on 21-02-2017.
 */

public interface FBSignCallback {
    void fbSignInSuccessResult(JSONObject jsonObject);
    void fbSignOutSuccessResult();
    void fbSignInFailure(FacebookException exception);
    void fbSignInCancel();

    void fbFriendsList(JSONArray data);
}
