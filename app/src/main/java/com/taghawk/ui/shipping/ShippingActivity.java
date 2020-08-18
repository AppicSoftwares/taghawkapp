package com.taghawk.ui.shipping;


import android.os.Bundle;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.ActivityShippingBinding;
import com.taghawk.model.cart.CartDataBean;

import java.util.HashMap;

public class ShippingActivity extends BaseActivity implements View.OnClickListener {

    private ActivityShippingBinding mBinding;
    private CartDataBean cartData;

    @Override
    protected int getResourceId() {
        return R.layout.activity_shipping;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_shipping);
        initView();
        getIntentData();
        addInitialFragment();
    }

    private void initView() {
        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.includeHeader.tvTitle.setText(getString(R.string.shipping));
        mBinding.includeHeader.ivCross.setOnClickListener(this);
    }

    private void getIntentData() {
        if (getIntent() != null) {
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.BUNDLE_DATA)) {
                cartData = (CartDataBean) getIntent().getExtras().get(AppConstants.BUNDLE_DATA);
            }
        }
    }

    private void addInitialFragment() {
        addFragmentWithBackstack(R.id.shipping_container, new ShippingFrgamentStepOne(), ShippingFrgamentStepOne.class.getSimpleName());
    }

    public void addReviewFragmentStepTwo(HashMap<String, Object> parms) {
        setSetupCircle(2);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.BUNDLE_DATA, cartData);
        bundle.putSerializable("DATA", parms);
        ShippingFragmentStepTwo fragmentStepTwo = new ShippingFragmentStepTwo();
        fragmentStepTwo.setArguments(bundle);
        addFragmentWithBackstack(R.id.shipping_container, fragmentStepTwo, ShippingFragmentStepTwo.class.getSimpleName());

    }

    private void setSetupCircle(int step) {
        if (step == 2) {
            setCircleColor(R.drawable.circle_color_primary, R.drawable.circle_white, R.color.colorPrimary);
            mBinding.tvStepOneCircle.setTextColor(getResources().getColor(R.color.txt_black));
            mBinding.tvStepTwoCircle.setTextColor(getResources().getColor(R.color.White));
        } else {
            setCircleColor(R.drawable.circle_white, R.drawable.circle_color_primary, R.color.txt_light_gray);
            mBinding.tvStepOneCircle.setTextColor(getResources().getColor(R.color.White));
            mBinding.tvStepTwoCircle.setTextColor(getResources().getColor(R.color.txt_black));
        }
    }

    private void setCircleColor(int stepTwo, int stepOne, int line) {
        mBinding.tvStepTwoCircle.setBackgroundDrawable(getResources().getDrawable(stepTwo));
        mBinding.tvStepOneCircle.setBackgroundDrawable(getResources().getDrawable(stepOne));
        mBinding.tvLine.setTextColor(getResources().getColor(line));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFragment() instanceof ShippingFragmentStepTwo) {
            popFragment();
            setSetupCircle(1);
        } else {
            finish();
        }
    }
}
