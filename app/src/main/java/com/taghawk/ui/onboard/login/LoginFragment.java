package com.taghawk.ui.onboard.login;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.CustomEmailRequiredDialog;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentLoginBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.login.CheckSocialLoginmodel;
import com.taghawk.model.login.LoginModel;
import com.taghawk.model.request.UserResponse;
import com.taghawk.ui.onboard.OnBoardActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;

import java.util.HashMap;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {


    private ILoginHost mLoginHost;
    private FragmentLoginBinding mLoginBinding;
    private LoginViewModel mLoginViewModel;
    private Activity mActivity;
    private HashMap<String, Object> mHashMapSocialData;
    private int UserType;

    public static LoginFragment getInstance() {
        return new LoginFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ILoginHost) {
            mLoginHost = (ILoginHost) context;
        } else
            throw new IllegalStateException("host must implement ILoginHost");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        mActivity = getActivity();
        initView();
        return mLoginBinding.getRoot();
    }

    private void initView() {
        getDeviceToken();
        mLoginBinding.cardLogin.setOnClickListener(this);
        mLoginBinding.cardSocialLogin.setOnClickListener(this);
        mLoginBinding.tvSignup.setOnClickListener(this);
        mLoginBinding.tvExploreAsGuest.setOnClickListener(this);
        mLoginBinding.ivBack.setOnClickListener(this);
        mLoginBinding.tvForgetPassword.setOnClickListener(this);
        mLoginBinding.tvExploreAsGuest.setOnClickListener(this);
        String str = getString(R.string.i_m_a_new_user_sign_up);
        Spannable spannable = new SpannableString(str);
        spannable.setSpan(new UnderlineSpan(), str.length() - 7, str.length(), 0);
        mLoginBinding.tvSignup.setText(spannable);
        mLoginBinding.tvSignup.setHighlightColor(Color.TRANSPARENT);
        mLoginBinding.tvSignup.setMovementMethod(LinkMovementMethod.getInstance());
        mLoginBinding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mLoginBinding.textInputEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mLoginBinding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mLoginBinding.textInputPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mLoginBinding.etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginRequest();
                }
                return false;
            }
        });
        if (getArguments() != null)
            UserType = getArguments().getInt(AppConstants.KEY_CONSTENT.USER_TYPE);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        mLoginViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        //observing login live data
        mLoginViewModel.getLoginLiveData().observe(this, new Observer<UserResponse>() {
            @Override
            public void onChanged(@Nullable UserResponse userResponse) {
                if (userResponse != null) {
                    hideProgressDialog();
                    if (userResponse.getCODE() == 201 || userResponse.getCODE() == 200) {
//                        showToastLong(getString(R.string.login_success));
                        mLoginHost.steerToHomeActivity();
                    }
                }
            }
        });

        //observing validation live data
        mLoginViewModel.getValidationLiveData().observe(this, new Observer<FailureResponse>() {
            @Override
            public void onChanged(@Nullable FailureResponse failureResponse) {
                hideProgressDialog();
                if (failureResponse != null) {
                    switch (failureResponse.getErrorCode()) {
                        case AppConstants.UIVALIDATIONS.EMAIL_EMPTY:
                            mLoginBinding.textInputEmail.setErrorEnabled(true);
                            mLoginBinding.textInputEmail.setError(failureResponse.getErrorMessage());
                            break;
                        case AppConstants.UIVALIDATIONS.INVALID_EMAIL:
                            mLoginBinding.textInputEmail.setErrorEnabled(true);
                            mLoginBinding.textInputEmail.setError(failureResponse.getErrorMessage());
                            break;
                        case AppConstants.UIVALIDATIONS.PASSWORD_EMPTY:
                            mLoginBinding.textInputPassword.setErrorEnabled(true);
                            mLoginBinding.textInputPassword.setError(failureResponse.getErrorMessage());
                            break;
                        case AppConstants.UIVALIDATIONS.INVALID_PASSWORD:
                            mLoginBinding.textInputPassword.setErrorEnabled(true);
                            mLoginBinding.textInputPassword.setError(failureResponse.getErrorMessage());
                            break;
                    }
                }
            }
        });
        mLoginViewModel.getmCheckSocialLoginLiveData().observe(this, new Observer<CheckSocialLoginmodel>() {
            @Override
            public void onChanged(@Nullable CheckSocialLoginmodel checkSocialLoginmodel) {
                getLoadingStateObserver().onChanged(false);
                if (checkSocialLoginmodel != null && checkSocialLoginmodel.getCheckSocialLoginData() != null && !checkSocialLoginmodel.getCheckSocialLoginData().isExist()) {
                    DialogUtil.getInstance().customInviteCodeDialog(mActivity, "", new OnDialogViewClickListener() {
                        @Override
                        public void onSubmit(String txt, int id) {
                            switch (id) {
                                case 1:
                                    mHashMapSocialData.put("invitationCode", txt);
                                    mLoginViewModel.socialLogin("", mHashMapSocialData, false);
                                    break;
                                case 2:
                                    showToastShort("Please enter invitation code");
                                    break;
                                case 3:
                                    if (mHashMapSocialData.containsKey("invitationCode")) {
                                        mHashMapSocialData.remove("invitationCode");
                                    }
                                    mLoginViewModel.socialLogin("", mHashMapSocialData, false);
                                    break;
                            }
                        }
                    });
                } else {
                    mLoginViewModel.socialLogin("", mHashMapSocialData, false);

                }

            }
        });
    }

    /**
     * This method is used to get device token from fire base
     */
    private void getDeviceToken() {
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (task != null && task.getResult().getToken() != null) {
                        String token = task.getResult().getToken();
                        mLoginViewModel.saveDeviceToken(token);
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        super.onErrorOccurred(throwable);
        getLoadingStateObserver().onChanged(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.card_login:
                loginRequest();
                break;
            case R.id.card_social_login:
                mLoginHost.fbSignIn();
                break;
            case R.id.tv_signup:
                mLoginHost.showSignUpFragment();
                break;
            case R.id.tv_explore_as_guest:
                guestLoginRequest();
                break;
            case R.id.tv_forget_password:
                mLoginBinding.etEmail.setText("");
                mLoginBinding.etPassword.setText("");
                mLoginHost.showForgotPasswordFragment();
                break;
            case R.id.iv_back:
//                mLoginHost.backPressed();
                break;

        }

    }

    private void loginRequest() {
        if (AppUtils.isInternetAvailable(mActivity)) {
            if (DataManager.getInstance().getDeviceToken() == null || DataManager.getInstance().getDeviceToken().length() == 0) {
                getDeviceToken();
            }
            mLoginViewModel.loginButtonClicked(new LoginModel(mLoginBinding.etEmail.getText().toString().trim(),
                    mLoginBinding.etPassword.getText().toString().trim(), "2", getDeviceId(), DataManager.getInstance().getDeviceToken()));
        } else {
            showNoNetworkError();
        }
    }

    private void guestLoginRequest() {
        if (DataManager.getInstance().getDeviceToken() == null || DataManager.getInstance().getDeviceToken().length() == 0) {
            getDeviceToken();
        }
        mLoginViewModel.guestUserLogin(getDeviceId(), DataManager.getInstance().getDeviceToken());
    }

    public void socialLogin(HashMap<String, Object> parms) {
        if (AppUtils.isInternetAvailable(mActivity)) {
            mHashMapSocialData = parms;
            HashMap<String, String> map = new HashMap<>();
            if (parms.containsKey(AppConstants.KEY_CONSTENT.EMAIL))
                map.put(AppConstants.KEY_CONSTENT.EMAIL, parms.get(AppConstants.KEY_CONSTENT.EMAIL).toString());
            map.put("socialId", parms.get("socialId").toString());
            mLoginViewModel.checkSocialLogin(map);

        } else {
            showNoNetworkError();
        }
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        if (failureResponse.getErrorCode() == 411) {
            new CustomEmailRequiredDialog(mActivity, new DialogCallback() {
                @Override
                public void submit(String data) {
                    mLoginViewModel.socialLogin(data, mHashMapSocialData, true);
                }

                @Override
                public void cancel() {

                }
            }).show();
        }else if(failureResponse.getErrorCode() == 400){
            showToastShort(failureResponse.getErrorMessage());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }


    /**
     * This interface is used to interact with the host {@link OnBoardActivity}
     */
    public interface ILoginHost {
        void showSignUpFragment();

        void steerToHomeActivity();

        void showForgotPasswordFragment();

        void fbSignIn();

        void backPressed();
    }
}
