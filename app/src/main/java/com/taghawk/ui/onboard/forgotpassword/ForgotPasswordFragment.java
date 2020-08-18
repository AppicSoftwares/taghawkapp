package com.taghawk.ui.onboard.forgotpassword;


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
import com.taghawk.databinding.FragmentForgotPasswordBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.ui.onboard.OnBoardActivity;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ForgotPasswordFragment extends BaseFragment implements TextWatcher, View.OnClickListener {

    /**
     * A {@link ForgotPasswordViewModel} object to handle all the actions and business logic
     */
    private ForgotPasswordViewModel mForgotPasswordViewModel;
    private IForgotPasswordHost mForgotPasswordHost;
    private FragmentForgotPasswordBinding mBinding;
    private Activity mActivity;

    public static ForgotPasswordFragment getInstance() {
        return new ForgotPasswordFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IForgotPasswordHost) {
            mForgotPasswordHost = (IForgotPasswordHost) context;
        } else
            throw new IllegalStateException("host must implement ILoginHost");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentForgotPasswordBinding.inflate(inflater);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mActivity = getActivity();
        mBinding.etEmail.addTextChangedListener(this);
        mBinding.cardForget.setOnClickListener(this);
        mBinding.ivBack.setOnClickListener(this);
        mBinding.etEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mForgotPasswordViewModel.onSubmitClicked(mBinding.etEmail.getText().toString().trim(), "2");
                }
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mForgotPasswordViewModel = ViewModelProviders.of(this).get(ForgotPasswordViewModel.class);
        mForgotPasswordViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mForgotPasswordViewModel.getForgotPasswordLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse successResponse) {
                if (successResponse != null) {
                    hideProgressDialog();
                    getCustomBottomDialog("Success!", successResponse.getMessage(), new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {
                            mForgotPasswordHost.moveToLogin();
                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });
                }
            }
        });
        mForgotPasswordViewModel.getValidationLiveData().observe(this, new Observer<FailureResponse>() {
            @Override
            public void onChanged(@Nullable FailureResponse failureResponse) {
                if (failureResponse != null) {
                    mBinding.textInputEmail.setErrorEnabled(true);
                    mBinding.textInputEmail.setError(failureResponse.getErrorMessage());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_forget:
                mForgotPasswordViewModel.onSubmitClicked(mBinding.etEmail.getText().toString().trim(), "2");
                break;
            case R.id.iv_back:
                mForgotPasswordHost.backPressed();
                break;
        }
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

    /**
     * This interface is used to interact with the host {@link OnBoardActivity}
     */
    public interface IForgotPasswordHost {

        void navigateToLoginFragment();

        void backPressed();

        void moveToLogin();
    }
}
