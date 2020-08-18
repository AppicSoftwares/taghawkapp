package com.taghawk.ui.setting.payment_details;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentDepositMoneyBinding;
import com.taghawk.model.strip.CreateMercentResponse;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.profile.ProfileFragment;

public class DepositMoneyFragment extends BaseFragment implements View.OnClickListener {


    private Activity mActivity;
    private FragmentDepositMoneyBinding mBinding;
    private boolean isFromCashOut;
    private HomeViewModel mHomeViewModel;

    /**
     * This method is used to return the instance of this fragment
     *
     * @return new instance of {@link ProfileFragment}
     */
    public static ProfileFragment getInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();

    }

    /**
     * Method to initVariables
     */
    private void initVariables() {

        mActivity = (AppCompatActivity) getActivity();
        if (getArguments() != null && getArguments().containsKey(AppConstants.IS_FROM_CASH_OUT)) {
            isFromCashOut = getArguments().getBoolean(AppConstants.IS_FROM_CASH_OUT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentDepositMoneyBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing view model
        initView();
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mHomeViewModel.getmCreateMerchentLiveData().observe(this, new Observer<CreateMercentResponse>() {
            @Override
            public void onChanged(@Nullable CreateMercentResponse createMercentResponse) {
                getLoadingStateObserver().onChanged(false);
                if (createMercentResponse.getCode() == 200) {
                    DataManager.getInstance().saveMerchentId(createMercentResponse.getMerchentId());
                }
            }
        });
        if (DataManager.getInstance().getMerchentId() == null || DataManager.getInstance().getMerchentId().length() == 0) {
            mHomeViewModel.createMerchent();
        }
    }

    private void initView() {
        openAddDebitCard();
        mBinding.tvDepositeAccount.setOnClickListener(this);
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.includeHeader.tvTitle.setText(getString(R.string.deposit_money));
        mBinding.tvDepositeAccount.setOnClickListener(this);
        mBinding.tvDebitCard.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_debit_card:
                openAddDebitCard();
                break;
            case R.id.tv_deposite_account:
                openBankAccountScreen();
                break;
            case R.id.iv_cross:
                mActivity.setResult(Activity.RESULT_CANCELED);
                mActivity.finish();
                break;
        }
    }

    private void openBankAccountScreen() {
        Intent intent = new Intent(mActivity, AddAccountActivity.class);
        intent.putExtra(AppConstants.IS_FROM_CASH_OUT, isFromCashOut);
        startActivityForResult(intent, 2003);
    }

    private void openAddDebitCard() {
        Intent intent = new Intent(mActivity, AddDebitCardActivity.class);
        intent.putExtra(AppConstants.IS_FROM_CASH_OUT, isFromCashOut);
        startActivityForResult(intent, 2004);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2003:
                if (resultCode == Activity.RESULT_OK) {
                    mActivity.setResult(Activity.RESULT_OK);
                    mActivity.finish();
                }
                break;
            case 2004:
                if (resultCode == Activity.RESULT_CANCELED) {
                    mActivity.setResult(Activity.RESULT_CANCELED);
                    mActivity.finish();
                }
                break;
        }
    }
}