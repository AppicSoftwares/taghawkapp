package com.taghawk.ui.onboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.ui.onboard.forgotpassword.ForgotPasswordFragment;
import com.taghawk.ui.onboard.login.LoginFragment;
import com.taghawk.ui.onboard.reset.ResetPasswordFragment;

import java.net.URL;

public class OnBoardActivity extends BaseActivity implements LoginFragment.ILoginHost,
        ForgotPasswordFragment.IForgotPasswordHost,
        ResetPasswordFragment.IResetPasswordHost {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showInitialFragment();
    }

    /**
     * deep linking to reset password and sending the token from that link to
     * {@link ResetPasswordFragment} reset the password.
     *
     * @param intent contains the deep link url so that we can take token from that link
     */
    private void checkIntent(Intent intent) {
        String action = intent.getAction();
        Uri data = intent.getData();
        try {
            if (Intent.ACTION_VIEW.equals(action) && data != null) {
                URL url = new URL(data.toString());
                if (url.getAuthority().contains("appinventive")) {
                    String token = url.getQuery().split("=")[1];
                    if (token != null) {
                        //adding reset password fragment with user id to reset the password
                        replaceFragment(R.id.onboard_container, ResetPasswordFragment.getInstance(token), ResetPasswordFragment.class.getSimpleName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_onboard;
    }

    /**
     * Method to show initial fragment
     */
    private void showInitialFragment() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            // This function is called here because if the user selects forgot password, then a link to reset his/her
            // password is sent to their email address.Through deep linking, this OnBoardActivity gets the link in
            // intent object and we have to maintain that flow also. So, in this case intent will not be null and we
            // will add ResetPasswordFragment
            checkIntent(intent);
        } else
            //if intent data is null , then it will add the LoginFragment
            addFragment(R.id.onboard_container, LoginFragment.getInstance(), LoginFragment.class.getSimpleName());
    }

    @Override
    public void showSignUpFragment() {
//        addFragmentWithBackstack(R.id.onboard_container, SignUpFragment.getInstance(), SignUpFragment.class.getSimpleName());
    }

    @Override
    public void steerToHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else
            finish();
    }

    @Override
    public void showForgotPasswordFragment() {
        addFragmentWithBackstack(R.id.onboard_container, ForgotPasswordFragment.getInstance(), ForgotPasswordFragment.class.getSimpleName());
    }

    @Override
    public void fbSignIn() {

    }

    @Override
    public void navigateToLoginFragment() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void backPressed() {

    }

    @Override
    public void moveToLogin() {
        popFragment();
    }
}
