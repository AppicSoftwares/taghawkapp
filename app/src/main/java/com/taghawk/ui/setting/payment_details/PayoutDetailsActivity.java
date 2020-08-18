package com.taghawk.ui.setting.payment_details;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;

public class PayoutDetailsActivity extends BaseActivity {

    private String accountNumber = "";
    private String routingNumber = "";
    private String accountHolderName = "";
    private String bankName = "";

    @Override
    protected int getResourceId() {
        return R.layout.activity_payout_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
    }

    private void getIntentData() {

        if(getIntent() != null) {
            accountNumber = getIntent().getStringExtra("accountNumber");
            routingNumber = getIntent().getStringExtra("routingNumber");
            accountHolderName = getIntent().getStringExtra("accountHolderName");
            bankName = getIntent().getStringExtra("bankName");
        }

        Bundle bundle = new Bundle();
        PayoutDetailFragment fragment = new PayoutDetailFragment();
        bundle.putString("accountNumber", accountNumber);
        bundle.putString("routingNumber", routingNumber);
        bundle.putString("accountHolderName", accountHolderName);
        bundle.putString("bankName", bankName);
        fragment.setArguments(bundle);
        replaceFragment(R.id.home_container, fragment, PayoutDetailFragment.class.getSimpleName());
    }

}
