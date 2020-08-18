package com.taghawk.ui.setting.payment_details;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class FragmentDepositMoneyActivity extends BaseActivity {
    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addinitialFragment();
    }

    private void addinitialFragment() {
        DepositMoneyFragment fragment = new DepositMoneyFragment();

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.IS_FROM_CASH_OUT)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(AppConstants.IS_FROM_CASH_OUT, getIntent().getBooleanExtra(AppConstants.IS_FROM_CASH_OUT, false));
            fragment.setArguments(bundle);
        }

        replaceFragment(R.id.home_container, fragment, DepositMoneyFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
