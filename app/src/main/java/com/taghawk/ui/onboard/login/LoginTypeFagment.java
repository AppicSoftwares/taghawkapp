package com.taghawk.ui.onboard.login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.ActivityLoginTypesBinding;

/**
 * Created by appinventiv on 21/1/19.
 */

public class LoginTypeFagment extends BaseFragment implements View.OnClickListener {

    private ActivityLoginTypesBinding mBinding;
    private Activity mActivity;
    private LoginFragment.ILoginHost mLoginHost;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragment.ILoginHost) {
            mLoginHost = (LoginFragment.ILoginHost) context;
        } else
            throw new IllegalStateException("host must implement ILoginHost");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_login_types, container, false);
        mActivity = getActivity();
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mBinding.cardCustomer.setOnClickListener(this);
        mBinding.cardDriver.setOnClickListener(this);
        mBinding.cardMovingCompany.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_customer:
                moveToLogin(AppConstants.LoginTypes.CUSTOMER);
                break;
            case R.id.card_driver:
                showToastLong(getString(R.string.under_development));
//                moveToLogin(AppConstants.LoginTypes.HAWK_DRIVER);
                break;
            case R.id.card_moving_company:
//                moveToLogin(AppConstants.LoginTypes.MOVING_COMPANY);
                break;

        }

    }

    private void moveToLogin(int customer) {
        LoginFragment fragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.KEY_CONSTENT.USER_TYPE, customer);
        fragment.setArguments(bundle);
        ((LoginActivity) mActivity).replaceFragmentWithBackstack(R.id.onboard_container, fragment, LoginFragment.class.getSimpleName());
    }


}
