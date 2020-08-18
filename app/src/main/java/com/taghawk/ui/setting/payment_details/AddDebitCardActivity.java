package com.taghawk.ui.setting.payment_details;

import android.os.Bundle;


import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class AddDebitCardActivity extends BaseActivity {


    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView() {
        AddDebitCardFragment fragment = new AddDebitCardFragment();
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.IS_FROM_CASH_OUT)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(AppConstants.IS_FROM_CASH_OUT, getIntent().getBooleanExtra(AppConstants.IS_FROM_CASH_OUT, false));
            fragment.setArguments(bundle);
        }
        replaceFragment(R.id.home_container, fragment, AddDebitCardFragment.class.getSimpleName());
    }
}
