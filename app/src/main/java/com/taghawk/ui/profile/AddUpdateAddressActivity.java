package com.taghawk.ui.profile;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.ShippingAddressesResponse;
import com.taghawk.model.profileresponse.AddressData;
import com.taghawk.ui.shipping.FragmentAddUpdateAddress;

import java.util.ArrayList;

public class AddUpdateAddressActivity extends BaseActivity {

    private FragmentAddUpdateAddress fragment;
    private AddressData addressData;

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFilterFragment();
    }

    private void addFilterFragment() {
        fragment = new FragmentAddUpdateAddress();

        String type = getIntent().getStringExtra("type");
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        if(type.equalsIgnoreCase("billing_address"))
            bundle.putParcelable("billingAddress", getIntent().getParcelableExtra("billingAddress"));
        else
            bundle.putParcelableArrayList("addressList", getIntent().getParcelableArrayListExtra("addressList"));
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, FragmentAddUpdateAddress.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
