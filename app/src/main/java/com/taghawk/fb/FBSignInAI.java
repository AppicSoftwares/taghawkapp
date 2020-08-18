package com.taghawk.fb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by appinventiv on 7/9/17.
 */

public class FBSignInAI {
    private Activity mActivity;
    private CallbackManager callbackManager;
    private FBSignCallback fbSignCallback;
    private AccessToken accessToken;


    /*
     *  Initialize activity instance
     */
    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    /*
     *  Initialize FB callback
     */
    public void setCallback(FBSignCallback fbSignCallback) {
        this.fbSignCallback = fbSignCallback;
    }

    /*
     *  Sign In Method
     */
    public void doSignIn() {
        callbackManager = CallbackManager.Factory.create();
        LoginViaFacebook();
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "email", "user_friends"));
    }


    /*
     *  Sign out Method
     */
    public void doSignOut() {
        LoginManager.getInstance().logOut();
        fbSignCallback.fbSignOutSuccessResult();
    }

    /*
     * To get user profile information from facebook
     */
    public void LoginViaFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject jsonObject,
                                    GraphResponse response) {
                                if (jsonObject != null) {
                                    try {
                                        fbSignCallback.fbSignInSuccessResult(jsonObject);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, picture, age_range, birthday,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                fbSignCallback.fbSignInCancel();
            }

            @Override
            public void onError(FacebookException exception) {
                fbSignCallback.fbSignInFailure(exception);
            }
        });
    }

    /*
     *  return callback to facebook using callbackmanager
     */
    public void setActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Method to get Fb Friends
     */
    public void getFBFriends() {
        new GraphRequest(accessToken,
                "/me/friends",
                null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                        try {
                            if (response.getJSONObject() != null)
                                if (response.getJSONObject().has("data"))
                                    fbSignCallback.fbFriendsList(response.getJSONObject().getJSONArray("data"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).executeAsync();

    }

}
