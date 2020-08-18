package com.taghawk.ui.setting.change_password;


import android.app.Activity;
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
import com.taghawk.databinding.FragmentChangePasswordBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.request.ChangePassword;
import com.taghawk.ui.onboard.reset.ResetPasswordViewModel;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ChangePasswordFragment extends BaseFragment implements View.OnClickListener {

    private FragmentChangePasswordBinding mBinding;
    /**
     * A {@link ResetPasswordViewModel} object to handle all the actions and business logic of reset password
     */
    private ChangePasswordViewModel mChangePasswordViewModel;

    private String mUserId;
    private Activity mActivity;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mActivity = getActivity();
        mBinding.tvUpdate.setOnClickListener(this);
        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.includeHeader.tvTitle.setText(getString(R.string.change_password));
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        editFieldAction();
    }

    private void editFieldAction() {
        mBinding.etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBinding.textInputNewPassword.setErrorEnabled(false);
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
                mBinding.textConfirmPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBinding.textInputOldPassword.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.etConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ChangePassword reset = new ChangePassword(mBinding.etOldPassword.getText().toString().trim(), mBinding.etNewPassword.getText().toString().trim());
                    mChangePasswordViewModel.onSubmitClicked(reset, mBinding.etConfirmPassword.getText().toString().trim());
                }
                return false;
            }
        });
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        getLoadingStateObserver().onChanged(false);
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        super.onErrorOccurred(throwable);
        getLoadingStateObserver().onChanged(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing view model & setting listeners
        mChangePasswordViewModel = ViewModelProviders.of(this).get(ChangePasswordViewModel.class);
        mChangePasswordViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mChangePasswordViewModel.getmResetPasswordLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse successResponse) {
                //password is reset successfully
                if (successResponse != null) {
                    hideProgressDialog();
                    mBinding.etNewPassword.setText("");
                    mBinding.etOldPassword.setText("");
                    mBinding.etConfirmPassword.setText("");
                    getCustomBottomDialog(getString(R.string.congratulations_title), getString(R.string.changepassword_msg), new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {
                            ((ChangePasswordActivity) mActivity).finish();
                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });//                    showToastLong(successResponse.getMessage());
                }
            }
        });
        mChangePasswordViewModel.getValidationLiveData().observe(this, new Observer<FailureResponse>() {
            @Override
            public void onChanged(@Nullable FailureResponse failureResponse) {
                hideProgressDialog();
                switch (failureResponse.getErrorCode()) {
                    case AppConstants.UIVALIDATIONS.OLD_PASSWORD_EMPTY:
                        mBinding.textInputOldPassword.setErrorEnabled(true);
                        mBinding.textInputOldPassword.setError(failureResponse.getErrorMessage());
                        break;
                    case AppConstants.UIVALIDATIONS.PASSWORD_EMPTY:
                        mBinding.textInputNewPassword.setErrorEnabled(true);
                        mBinding.textInputNewPassword.setError(failureResponse.getErrorMessage());
                        break;

                    case AppConstants.UIVALIDATIONS.CONFIRM_PASSWORD_EMPTY:
                        mBinding.textConfirmPassword.setErrorEnabled(true);
                        mBinding.textConfirmPassword.setError(failureResponse.getErrorMessage());
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_update:
                ChangePassword reset = new ChangePassword(mBinding.etOldPassword.getText().toString().trim(), mBinding.etNewPassword.getText().toString().trim());
                mChangePasswordViewModel.onSubmitClicked(reset, mBinding.etConfirmPassword.getText().toString().trim());
                break;
            case R.id.iv_cross:
                ((ChangePasswordActivity) mActivity).finish();
                break;
        }
    }
}
