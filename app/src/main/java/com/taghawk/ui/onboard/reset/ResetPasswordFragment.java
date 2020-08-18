package com.taghawk.ui.onboard.reset;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentResetPasswordBinding;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.request.Reset;
import com.taghawk.ui.onboard.OnBoardActivity;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ResetPasswordFragment extends BaseFragment {

    private IResetPasswordHost mResetPasswordHost;
    private FragmentResetPasswordBinding mBinding;
    /**
     * A {@link ResetPasswordViewModel} object to handle all the actions and business logic of reset password
     */
    private ResetPasswordViewModel mResetPasswordViewModel;

    private String mUserId;
    private Activity mActivity;

    /**
     * This method gives the instance of this fragment
     *
     * @param userId coming from the host {@link OnBoardActivity}
     * @return new instance of {@link ResetPasswordFragment}
     */
    public static ResetPasswordFragment getInstance(String userId) {
        ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.TOKEN, userId);
        resetPasswordFragment.setArguments(bundle);
        return resetPasswordFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IResetPasswordHost) {
            mResetPasswordHost = (IResetPasswordHost) context;
        } else
            throw new IllegalStateException("Host must implement IResetPasswordHost");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mActivity = getActivity();
        mBinding.cardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reset reset = new Reset(mUserId, mBinding.etNewPassword.getText().toString().trim(), mBinding.etConfirmPassword.getText().toString().trim());
                mResetPasswordViewModel.onSubmitClicked(reset, mBinding.etConfirmPassword.getText().toString().trim());
            }
        });
        mBinding.etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBinding.textInputEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBinding.textInputPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.etConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Reset reset = new Reset(mUserId, mBinding.etNewPassword.getText().toString().trim(), mBinding.etConfirmPassword.getText().toString().trim());
                    mResetPasswordViewModel.onSubmitClicked(reset, mBinding.etConfirmPassword.getText().toString().trim());
                }
                return false;
            }
        });
        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResetPasswordHost.backPressed();
            }
        });
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        getLoadingStateObserver().onChanged(false);
        showToastLong(failureResponse.getErrorMessage());
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        super.onErrorOccurred(throwable);
        getLoadingStateObserver().onChanged(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getting user id from the arguments
        if (getArguments() != null) {
            mUserId = getArguments().getString(AppConstants.TOKEN);
        }
        //initializing view model & setting listeners
        mResetPasswordViewModel = ViewModelProviders.of(this).get(ResetPasswordViewModel.class);
        mResetPasswordViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mResetPasswordViewModel.getmResetPasswordLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse successResponse) {
                //password is reset successfully
                if (successResponse != null) {
                    hideProgressDialog();
                    showToastLong(getString(R.string.password_reset_success));
                    mResetPasswordHost.navigateToLoginFragment();
                }
            }
        });
        mResetPasswordViewModel.getValidationLiveData().observe(this, new Observer<FailureResponse>() {
            @Override
            public void onChanged(@Nullable FailureResponse failureResponse) {
                hideProgressDialog();
                switch (failureResponse.getErrorCode()) {
                    case AppConstants.UIVALIDATIONS.INVALID_PASSWORD:
                    case AppConstants.UIVALIDATIONS.NEW_PASSWORD_EMPTY:
                        mBinding.textInputEmail.setErrorEnabled(true);
                        mBinding.textInputEmail.setError(failureResponse.getErrorMessage());
                        break;
                    case AppConstants.UIVALIDATIONS.PASSWORD_NOT_MATCHED:
                    case AppConstants.UIVALIDATIONS.CONFIRM_PASSWORD_EMPTY:
                        mBinding.textInputPassword.setErrorEnabled(true);
                        mBinding.textInputPassword.setError(failureResponse.getErrorMessage());
                        break;

                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * This interface is used to interact with the host {@link OnBoardActivity}
     */
    public interface IResetPasswordHost {

        void navigateToLoginFragment();

        void backPressed();
    }
}
