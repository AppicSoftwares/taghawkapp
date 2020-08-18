package com.taghawk.ui.onboard.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.taghawk.Jointag.Jointag;
import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.ActivityLoginTypesBinding;
import com.taghawk.fb.FBSignCallback;
import com.taghawk.fb.FBSignInAI;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.ui.onboard.forgotpassword.ForgotPasswordFragment;
import com.taghawk.ui.onboard.reset.ResetPasswordFragment;
import com.taghawk.ui.onboard.signup.SignUpFragment;
import com.taghawk.util.AppUtils;
import com.taghawk.util.PermissionUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by appinventiv on 21/1/19.
 */

public class LoginActivity extends BaseActivity implements LoginFragment.ILoginHost, SignUpFragment.ISignUpHost, FBSignCallback, ForgotPasswordFragment.IForgotPasswordHost, ResetPasswordFragment.IResetPasswordHost {

    private ActivityLoginTypesBinding mLoginTypeBinding;
    private int FB_LOGIN_REQUEST_CODE = 64206;  //Fb Default request code
    private FBSignInAI mFBSignInAI;
    private String type;
    private String linkId, deepLinkType;
    private boolean isSignup = false, isLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionUtility.isPermissionGranted(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {

        }

        String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DataManager.getInstance().saveDeviceId(androidId);
        Log.e("androidId", "" + androidId);

        getIntentData();
        showLoginTypeFragment();
//        showSignUpFragment();
        initFacebook();
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.DEEP_INK_CONSTENT.LINK_ID)) {
            linkId = getIntent().getExtras().getString(AppConstants.DEEP_INK_CONSTENT.LINK_ID);
            deepLinkType = getIntent().getExtras().getString(AppConstants.DEEP_INK_CONSTENT.RESET_PASSWORD);
        } else if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.IS_SIGN_UP)) {
            isSignup = getIntent().getExtras().getBoolean(AppConstants.IS_SIGN_UP);
        } else if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.IS_LOGIN)) {
            isLogin = getIntent().getExtras().getBoolean(AppConstants.IS_LOGIN);
        }
    }

    private void showLoginTypeFragment() {
        if (linkId != null && linkId.length() > 0 && deepLinkType != null && deepLinkType.equals(AppConstants.DEEP_INK_CONSTENT.RESET_PASSWORD)) {
            ResetPasswordFragment fragment = new ResetPasswordFragment();
            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.TOKEN, linkId);
            fragment.setArguments(bundle);
            replaceFragmentWithBackstack(R.id.onboard_container, fragment, ResetPasswordFragment.class.getSimpleName());
        } else {
            if (isSignup) {
                showSignUpFragment();
            } else if (isLogin) {
                moveToLogin(1);
            } else {
                showSignUpFragment();
//                replaceFragmentWithBackstack(R.id.onboard_container, new LoginTypeFagment(), LoginTypeFagment.class.getSimpleName());

            }

        }
    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_onboard;
    }

    @Override
    public void showSignUpFragment() {
        replaceFragmentWithBackstack(R.id.onboard_container, new SignUpFragment(), SignUpFragment.class.getSimpleName());
    }

    @Override
    public void steerToHomeActivity() {

        finishAffinity();
//        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(new Intent(this, HomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

//        startActivity(intent);

    }

    @Override
    public void steerToJoinActivity() {

        finishAffinity();
//        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(new Intent(this, Jointag.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//        startActivity(intent);

    }

    @Override
    public void showForgotPasswordFragment() {
        replaceFragmentWithBackstack(R.id.onboard_container, new ForgotPasswordFragment(), ForgotPasswordFragment.class.getSimpleName());
    }


    @Override
    public void onBackPressed() {
        backPressedAction();
    }

    private void backPressedAction() {
        try {
            if (getCurrentFragment() instanceof SignUpFragment || getCurrentFragment() instanceof ResetPasswordFragment) {
//                replaceFragmentWithBackstack(R.id.onboard_container, new LoginTypeFagment(), LoginTypeFagment.class.getSimpleName());
                moveToLogin(1);
            } else if (getCurrentFragment() instanceof ForgotPasswordFragment) {
                popFragment();
            } else {
                finish();
            }
        } catch (Exception e) {
            finish();
        }
    }

    @Override
    public void fbSignInSuccessResult(JSONObject jsonObject) {

        String name, email = null, gender = null, social_id, userProfilePicUrl = null, ageRange = "", firstName = "", lastName = "", birthday = "";
        try {
            name = jsonObject.getString("name");
            if (jsonObject.has("email")) {
                email = jsonObject.getString("email");
            }
            social_id = jsonObject.getString("id");
            if (jsonObject.has("gender")) {
                gender = jsonObject.getString("gender");
            }
            if (jsonObject.has("picture")) {
                userProfilePicUrl = "https://graph.facebook.com/" + jsonObject.getString("id") + "/picture?width=2000";
            }
            if (jsonObject.has("first_name")) {
                firstName = jsonObject.getString("first_name");
            }
            if (jsonObject.has("last_name")) {
                lastName = jsonObject.getString("last_name");
            }
            if (jsonObject.has("birthday"))
                birthday = jsonObject.getString("birthday");
            if (jsonObject.has("age_range"))
                ageRange = jsonObject.getString("age_range");
            callLoginApi(social_id, name, userProfilePicUrl, email, firstName, lastName, birthday, ageRange);
            doLogout();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void callLoginApi(String social_id, String name, String userProfilePicUrl, String email, String firstName, String lastName, String birthday, String ageRange) {
        int socialType = 1;
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("socialLoginType", socialType);
        parms.put("socialId", social_id);
        parms.put("fullName", name);
        parms.put("firstName", firstName);
        if (lastName != null && lastName.length() > 0)
            parms.put("lastName", lastName);
        else {
            if (firstName.length() > 0)
                parms.put("lastName", firstName.substring(0, 1));
        }

        if (email != null && email.length() > 0)
            parms.put(AppConstants.KEY_CONSTENT.EMAIL, email);
        parms.put("profilePicture", userProfilePicUrl);
        parms.put(AppConstants.KEY_CONSTENT.USER_TYPE, "2");
        parms.put(AppConstants.KEY_CONSTENT.DEVICE_ID, getDeviceId());
        parms.put(AppConstants.KEY_CONSTENT.DEVICETOKEN, DataManager.getInstance().getDeviceToken());
        if (getCurrentFragment() instanceof LoginFragment) {
            if (AppUtils.isInternetAvailable(this)) {
                LoginFragment fragment = (LoginFragment) getCurrentFragment();
                fragment.socialLogin(parms);
            }
        } else if (getCurrentFragment() instanceof SignUpFragment) {
            SignUpFragment fragment = (SignUpFragment) getCurrentFragment();
            fragment.socialSignup(parms);
        }
    }


    @Override
    public void fbSignOutSuccessResult() {
    }

    @Override
    public void fbSignInFailure(FacebookException exception) {

    }

    @Override
    public void fbSignInCancel() {

    }

    @Override
    public void fbFriendsList(JSONArray data) {

    }

    private void initFacebook() {
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(this);
        initializeFB();
    }

    private void initializeFB() {
        mFBSignInAI = new FBSignInAI();
        mFBSignInAI.setActivity(this);
        mFBSignInAI.setCallback(this);

    }

    /*
     *  Sign In Method
     */
    public void fbSignIn() {
        if (mFBSignInAI != null)
            mFBSignInAI.doSignIn();

    }

    @Override
    public void moveToLogin() {
        popFragment();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            moveToLogin(1);
        }
    }

    /*
     *  Sign out Method
     */
    public void doLogout() {
        if (mFBSignInAI != null)
            mFBSignInAI.doSignOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (FB_LOGIN_REQUEST_CODE == requestCode) {
            mFBSignInAI.setActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void navigateToLoginFragment() {
        backPressedAction();
    }

    @Override
    public void backPressed() {
        backPressedAction();
    }

    private void moveToLogin(int customer) {
        LoginFragment fragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.KEY_CONSTENT.USER_TYPE, customer);
        fragment.setArguments(bundle);
        replaceFragmentWithBackstack(R.id.onboard_container, fragment, LoginFragment.class.getSimpleName());
    }
}
