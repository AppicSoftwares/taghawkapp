package com.taghawk.ui.onboard.signup;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.taghawk.databinding.FragmentSignUpBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.request.UserResponse;
import com.taghawk.ui.onboard.login.LoginFragment;

import java.util.HashMap;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class SignUpFragment extends BaseFragment implements View.OnClickListener {
    private SignUpViewModel mSignUpViewModel;
    private ISignUpHost mSignUpHost;
    private FragmentSignUpBinding mSignUpBinding;
    private Observer<Boolean> isLoding;
    private Activity mActivity;
    private HashMap<String, Object> signupSocialDataMap;

    public static SignUpFragment getInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ISignUpHost) {
            mSignUpHost = (ISignUpHost) context;
        } else
            throw new IllegalStateException("Host must implement ISignUpHost");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mSignUpBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        mActivity = getActivity();
        initView();
        String androidId = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DataManager.getInstance().saveDeviceId(androidId);
        Log.e("androidId", "" + androidId);
        return mSignUpBinding.getRoot();
    }

    private void initView() {
        mSignUpBinding.cardSocialSignUp.setOnClickListener(this);
        mSignUpBinding.cardSignUp.setOnClickListener(this);
        mSignUpBinding.tvExploreAsGuest.setOnClickListener(this);
        mSignUpBinding.tvLogin.setOnClickListener(this);
        String str = getString(R.string.i_m_already_a_member);
        Spannable spannable = new SpannableString(str);
        spannable.setSpan(new UnderlineSpan(), str.length() - 5, str.length(), 0);
        mSignUpBinding.tvLogin.setText(spannable);
        mSignUpBinding.tvLogin.setHighlightColor(Color.TRANSPARENT);
        mSignUpBinding.tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
        setAddTextChanged();
    }

    private void setAddTextChanged() {
        mSignUpBinding.etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSignUpBinding.textInputFirstName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mSignUpBinding.etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSignUpBinding.textInputLastName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mSignUpBinding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSignUpBinding.textInputEmail.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mSignUpBinding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSignUpBinding.textInputPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSignUpViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        mSignUpViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mSignUpViewModel.getSignUpLiveData().observe(this, new Observer<UserResponse>() {
            @Override
            public void onChanged(@Nullable UserResponse userResponse) {
                hideProgressDialog();
                //on success
                getCustomBottomDialog(view.getContext().getString(R.string.success), userResponse.getMESSAGE(), new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {
                        mSignUpHost.steerToJoinActivity();
                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });
            }
        });
        mSignUpViewModel.getValidationLiveData().observe(this, new Observer<FailureResponse>() {
            @Override
            public void onChanged(@Nullable FailureResponse failureResponse) {
                hideProgressDialog();
                switch (failureResponse.getErrorCode()) {
                    case AppConstants.UIVALIDATIONS.NAME_EMPTY:
                        mSignUpBinding.textInputFirstName.setErrorEnabled(true);
                        mSignUpBinding.textInputFirstName.setError(failureResponse.getErrorMessage());
                        break;
                    case AppConstants.UIVALIDATIONS.INVALID_NAME:
                        mSignUpBinding.textInputFirstName.setErrorEnabled(true);
                        mSignUpBinding.textInputFirstName.setError(failureResponse.getErrorMessage());
                        break;
                    case AppConstants.UIVALIDATIONS.LAST_NAME_EMPTY:
                        mSignUpBinding.textInputLastName.setErrorEnabled(true);
                        mSignUpBinding.textInputLastName.setError(failureResponse.getErrorMessage());
                        break;
                    case AppConstants.UIVALIDATIONS.LAST_INVALID_NAME:
                        mSignUpBinding.textInputLastName.setErrorEnabled(true);
                        mSignUpBinding.textInputLastName.setError(failureResponse.getErrorMessage());
                        break;
                    case AppConstants.UIVALIDATIONS.EMAIL_EMPTY:
                        mSignUpBinding.textInputEmail.setErrorEnabled(true);
                        mSignUpBinding.textInputEmail.setError(failureResponse.getErrorMessage());
                        break;
                    case AppConstants.UIVALIDATIONS.INVALID_EMAIL:
                        mSignUpBinding.textInputEmail.setErrorEnabled(true);
                        mSignUpBinding.textInputEmail.setError(failureResponse.getErrorMessage());
                        break;
                    case AppConstants.UIVALIDATIONS.PASSWORD_EMPTY:
                        mSignUpBinding.textInputPassword.setErrorEnabled(true);
                        mSignUpBinding.textInputPassword.setError(failureResponse.getErrorMessage());
                        break;
                    case AppConstants.UIVALIDATIONS.INVALID_PASSWORD:
                        mSignUpBinding.textInputPassword.setErrorEnabled(true);
                        mSignUpBinding.textInputPassword.setError(failureResponse.getErrorMessage());
                        break;
                    case AppConstants.UIVALIDATIONS.FACEBOOK_EMAIL_REQUIRED:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_social_sign_up:
                mSignUpHost.fbSignIn();
                break;
            case R.id.card_sign_up:
                HashMap<String, String> parms = new HashMap<>();
                parms.put("fullName", mSignUpBinding.etFirstName.getText().toString().trim() + " " + mSignUpBinding.etLastName.getText().toString().trim());
                parms.put("firstName", mSignUpBinding.etFirstName.getText().toString().trim());
                parms.put("lastName", mSignUpBinding.etLastName.getText().toString().trim());
                parms.put(AppConstants.KEY_CONSTENT.EMAIL, mSignUpBinding.etEmail.getText().toString().trim());
                parms.put(AppConstants.KEY_CONSTENT.PASSWORD, mSignUpBinding.etPassword.getText().toString().trim());
                parms.put(AppConstants.KEY_CONSTENT.DEVICE_ID, getDeviceId());
                parms.put(AppConstants.KEY_CONSTENT.USER_TYPE, "2");
                parms.put(AppConstants.KEY_CONSTENT.DEVICETOKEN, DataManager.getInstance().getDeviceToken());
                if (mSignUpBinding.etInvitation.getText().toString().length() > 0)
                    parms.put("invitationCode", mSignUpBinding.etInvitation.getText().toString().trim());
                mSignUpViewModel.userSignUp(parms);
                break;
            case R.id.tv_explore_as_guest:
                guestLoginRequest();
                break;
            case R.id.tv_login:
                mSignUpHost.moveToLogin();
                break;
        }
    }


    private void guestLoginRequest() {
        if (DataManager.getInstance().getDeviceToken() == null || DataManager.getInstance().getDeviceToken().length() == 0) {
            getDeviceToken();
        }
        mSignUpViewModel.guestUserLogin(getDeviceId(), DataManager.getInstance().getDeviceToken());
    }

    private void getDeviceToken() {
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (task != null && task.getResult().getToken() != null) {
                        String token = task.getResult().getToken();
                        mSignUpViewModel.saveDeviceToken(token);
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        if (failureResponse.getErrorCode() == 411) {
            new CustomEmailRequiredDialog(mActivity, new DialogCallback() {
                @Override
                public void submit(String data) {
                    mSignUpViewModel.socialLogin(data, signupSocialDataMap, true);
                }

                @Override
                public void cancel() {

                }
            }).show();

        } else if (failureResponse != null && failureResponse.getErrorCode() == 400) {
            if (getLoadingStateObserver() != null) {
                getLoadingStateObserver().onChanged(false);
            }
            getCustomBottomDialog(mActivity.getString(R.string.opps), failureResponse.getErrorMessage().toString(), new OnDialogItemClickListener() {
                @Override
                public void onPositiveBtnClick() {
//                    if (mActivity != null)
//                        mActivity.onBackPressed();
                }

                @Override
                public void onNegativeBtnClick() {

                }
            });
        }

    }

    public void socialSignup(HashMap<String, Object> parms) {
        signupSocialDataMap = parms;
        mSignUpViewModel.socialLogin("", parms, false);
    }

    public interface ISignUpHost {

        void steerToHomeActivity();
        void steerToJoinActivity();

        void fbSignIn();

        void moveToLogin();
    }
}
