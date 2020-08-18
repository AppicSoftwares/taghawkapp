package com.taghawk.ui.setting.payment_details;

import android.app.Activity;
import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class AddAccountActivity extends BaseActivity {

    private boolean isOpenCashOutRequired;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        if (isOpenCashOutRequired) {
            openCashOutRequiredData();
        } else
            addInitialFragment();
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getBoolean(AppConstants.BUNDLE_DATA)) {
            isOpenCashOutRequired = getIntent().getBooleanExtra(AppConstants.BUNDLE_DATA, false);
        }
    }

    private void addInitialFragment() {
        AddBankDetailFragment fragment = new AddBankDetailFragment();
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = new Bundle();
            if(getIntent().getExtras().getBoolean(AppConstants.IS_FROM_CASH_OUT))
                bundle.putBoolean(AppConstants.IS_FROM_CASH_OUT, getIntent().getExtras().getBoolean(AppConstants.IS_FROM_CASH_OUT));
            bundle.putString("accountNumber", getIntent().getStringExtra("accountNumber"));
            bundle.putString("routingNumber", getIntent().getStringExtra("routingNumber"));
            bundle.putString("accountHolderName", getIntent().getStringExtra("accountHolderName"));
            bundle.putString("bankName", getIntent().getStringExtra("bankName"));
            fragment.setArguments(bundle);
        }
        replaceFragment(R.id.home_container, fragment, AddBankDetailFragment.class.getSimpleName());

    }

    public void openCashOutRequiredData() {
        CashOutRequiredDataFragment fragment = new CashOutRequiredDataFragment();
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getBoolean(AppConstants.IS_FROM_CASH_OUT)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(AppConstants.IS_FROM_CASH_OUT, getIntent().getExtras().getBoolean(AppConstants.IS_FROM_CASH_OUT));
            fragment.setArguments(bundle);
        }
        replaceFragment(R.id.home_container, fragment, CashOutRequiredDataFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
