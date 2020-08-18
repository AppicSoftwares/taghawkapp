package com.taghawk.ui.setting.payment_details;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class PaymentDetailsActivity extends BaseActivity {

    private boolean isOpenAddBankAccount = false;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        addInitialFragment();
    }

    private void addInitialFragment() {
        AccountPaymentInfoFragment fragment = new AccountPaymentInfoFragment();
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.PAYMENT)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(AppConstants.PAYMENT, getIntent().getExtras().getBoolean(AppConstants.PAYMENT));
            fragment.setArguments(bundle);
        }
        addFragmentWithBackstack(R.id.home_container, fragment, AccountPaymentInfoFragment.class.getSimpleName());

    }

//    private void addInitialFragment() {
//        PaymentDetailFragment fragment = new PaymentDetailFragment();
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(AppConstants.BUNDLE_DATA, isOpenAddBankAccount);
//        fragment.setArguments(bundle);
//        addFragmentWithBackstack(R.id.home_container, fragment, PaymentDetailFragment.class.getSimpleName());
//    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.BUNDLE_DATA))
            isOpenAddBankAccount = getIntent().getExtras().getBoolean(AppConstants.BUNDLE_DATA, false);
    }

    public void addBankDetailFragment() {
        addFragmentWithBackstack(R.id.home_container, new AddBankDetailFragment(), AddBankDetailFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (getCurrentFragment() instanceof AccountPaymentInfoFragment) {
            finish();
        }
//        else {
//            popFragment();
//            if (getCurrentFragment() instanceof AccountPaymentInfoFragment) {
//                ((AccountPaymentInfoFragment) getCurrentFragment()).setData();
//            }
//
//        }
    }
}
