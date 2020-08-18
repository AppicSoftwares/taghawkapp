package com.taghawk.ui.setting.payment_details;


import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.taghawk.R;
import com.taghawk.adapters.ViewPagerAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentAccountPaymentInfoBinding;
import com.taghawk.ui.home.HomeViewModel;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class AccountPaymentInfoFragment extends BaseFragment implements View.OnClickListener {

    /**
     * A {@link HomeViewModel} object to handle all the actions and business logic
     */
    private FragmentAccountPaymentInfoBinding mBinding;
    private Activity mActivity;
    private ViewPagerAdapter viewPagerAdapter;
    private float originalBackgroundTranslationX;
    private ArgbEvaluator argbEvaluator;

    /**
     * This method is used to return the instance of this fragment
     *
     * @return new instance of {@link AccountPaymentInfoFragment}
     */
    public static AccountPaymentInfoFragment getInstance() {
        return new AccountPaymentInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentAccountPaymentInfoBinding.inflate(inflater, container, false);
        initView();
//        TagHawkApplication.getInstance().setMessageTabVisible(true);
        return mBinding.getRoot();
    }

    // init views and listener
    private void initView() {
        mActivity = getActivity();
        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.includeHeader.tvTitle.setText(getString(R.string.wallet));
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.flMessages.setOnClickListener(this);
        mBinding.flNotifications.setOnClickListener(this);
        originalBackgroundTranslationX = mBinding.viewBackground.getTranslationX();
        argbEvaluator = new ArgbEvaluator();
        if (mActivity instanceof PaymentDetailsActivity) {
            mBinding.includeHeader.ivCross.setVisibility(View.VISIBLE);
        } else {
            mBinding.includeHeader.ivCross.setVisibility(View.GONE);
        }
        setupViewPager();
        setCurrentPage();

    }

    /*Set Current Page to  Payemnt History*/
    private void setCurrentPage() {
        if (getArguments() != null && getArguments().containsKey(AppConstants.PAYMENT)) {
            boolean isPaymentHistory = getArguments().getBoolean(AppConstants.PAYMENT);
            if (isPaymentHistory) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.vpPayment.setCurrentItem(1, true);
                    }
                }, 2000);
            }
        }

    }

    // setup view pager
    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new PaymentDetailFragment(), getString(R.string.account_info));
        viewPagerAdapter.addFragment(new PaymentHistoryInfoFragment(), getString(R.string.payment_history));

        mBinding.vpPayment.setAdapter(viewPagerAdapter);

        mBinding.vpPayment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        mBinding.viewBackground.setTranslationX(originalBackgroundTranslationX + positionOffsetPixels / 2.25f);
                        mBinding.tvMessages.setTextColor((Integer) argbEvaluator.evaluate(positionOffset,
                                getResources().getColor(R.color.Black),
                                getResources().getColor(R.color.White)));
                        mBinding.tvNotifications.setTextColor((Integer) argbEvaluator.evaluate(positionOffset,
                                getResources().getColor(R.color.White),
                                getResources().getColor(R.color.Black)));
                        break;
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    public void setData() {
        if (mBinding != null && mBinding.vpPayment != null) {
            if (mBinding.vpPayment.getCurrentItem() == 0) {
                Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vpPayment.getCurrentItem());
                if (presentFragment != null && presentFragment instanceof PaymentDetailFragment) {
//                    ((PaymentDetailFragment) presentFragment).setData();
                }
            } else {

            }
        }
    }

    public void passOnActivityData(int requestCode, int resultCode, @Nullable Intent data) {
        if (mBinding != null && mBinding.vpPayment != null) {
            if (mBinding.vpPayment.getCurrentItem() == 0) {
                Fragment presentFragment = viewPagerAdapter.getItem(mBinding.vpPayment.getCurrentItem());
                if (presentFragment != null && presentFragment instanceof PaymentDetailFragment) {
                    ((PaymentDetailFragment) presentFragment).onActivityResult(requestCode, resultCode, data);
                }
            } else {

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_messages:
                mBinding.vpPayment.setCurrentItem(0, true);
                break;
            case R.id.fl_notifications:
                mBinding.vpPayment.setCurrentItem(1, true);
                break;
            case R.id.iv_cross:
                mActivity.onBackPressed();
                break;

        }
    }

}
