package com.taghawk.ui.setting.payment_details;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentAddBankDetailsBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.strip.CreateMercentResponse;
import com.taghawk.util.DialogUtil;

public class AddBankDetailFragment extends BaseFragment implements View.OnClickListener {
    private FragmentAddBankDetailsBinding mBinding;
    private AddBankAccountViewModel viewModel;
    private Activity mActivity;
    private boolean isFromCashOut;

    private String accountNumber = "";
    private String routingNumber = "";
    private String accountHolderName = "";
    private String bankName = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentAddBankDetailsBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(AddBankAccountViewModel.class);
        viewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        viewModel.getmMerchentLiveData().observe(this, new Observer<CreateMercentResponse>() {
            @Override
            public void onChanged(@Nullable CreateMercentResponse addTagResponse) {
                getLoadingStateObserver().onChanged(false);


            }
        });
        viewModel.getmSaveBankLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                showToastShort(commonResponse.getMessage());
                if (commonResponse.getCode() == 200 || commonResponse.getCode() == 201) {

                    DataManager.getInstance().saveRoutingNumber(mBinding.etRoutingNumber.getText().toString().trim());
                    DataManager.getInstance().saveAccountNumber(mBinding.etAccountNumber.getText().toString().trim());
                    DataManager.getInstance().saveAccountHolderName(mBinding.etFirstName.getText().toString().trim() + " " + mBinding.etLastName.getText().toString().trim());
                    if (isFromCashOut)
                        ((AddAccountActivity) mActivity).openCashOutRequiredData();
                    else {
                        mActivity.setResult(Activity.RESULT_OK);
                        mActivity.finish();
                    }
                }
            }
        });

    }

    private void initView() {
        mActivity = getActivity();

        if (getArguments() != null) {
            accountNumber = getArguments().getString("accountNumber");
            routingNumber = getArguments().getString("routingNumber");
            accountHolderName = getArguments().getString("accountHolderName");
            bankName = getArguments().getString("bankName");
        }

        if(!TextUtils.isEmpty(accountNumber)) {
            mBinding.etAccountNumber.setText("" + accountNumber);
            mBinding.etVerifiyAccount.setText("" + accountNumber);
        }
        if(!TextUtils.isEmpty(routingNumber)) {
            mBinding.etRoutingNumber.setText("" + routingNumber);
            mBinding.etVerifiyRoutingNumber.setText("" + routingNumber);
        }
        if(!TextUtils.isEmpty(accountHolderName)) {
            String[] name = accountHolderName.split(" ");
            if(name.length > 1) {
                mBinding.etFirstName.setText("" + name[0]);
                mBinding.etLastName.setText("" + name[1]);
            } else {
                mBinding.etFirstName.setText("" + name[0]);
            }
        }
        if(!TextUtils.isEmpty(bankName)) {
            mBinding.etBankName.setText("" + bankName);
        }

        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.includeHeader.tvTitle.setText(getResources().getString(R.string.add_bank_details));
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.tvAdd.setOnClickListener(this);
        if (getArguments() != null && getArguments().containsKey(AppConstants.IS_FROM_CASH_OUT)) {
            isFromCashOut = getArguments().getBoolean(AppConstants.IS_FROM_CASH_OUT);
        }
        setPreviousData();
    }

    private void setPreviousData() {

//        if (DataManager.getInstance().getRoutingNumber() != null && DataManager.getInstance().getRoutingNumber().length() > 0) {
//            mBinding.etVerifiyRoutingNumber.setText(DataManager.getInstance().getRoutingNumber());
//            mBinding.etRoutingNumber.setText(DataManager.getInstance().getRoutingNumber());
//        }
//        if (DataManager.getInstance().getAccountNumber() != null && DataManager.getInstance().getAccountNumber().length() > 0) {
//            mBinding.etAccountNumber.setText(DataManager.getInstance().getAccountNumber());
//            mBinding.etVerifiyAccount.setText(DataManager.getInstance().getAccountNumber());
//        }
//        if (DataManager.getInstance().getAccountHolderName() != null && DataManager.getInstance().getAccountHolderName().length() > 1) {
//            mBinding.etFirstName.setText(DataManager.getInstance().getAccountHolderName().split(" ")[0]);
//            if (DataManager.getInstance().getAccountHolderName().split(" ").length == 2)
//                mBinding.etLastName.setText(DataManager.getInstance().getAccountHolderName().split(" ")[1]);
//        }
//        if (!isFromCashOut) {
//            mBinding.tvAdd.setText(getString(R.string.update));
//        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                boolean isValid = viewModel.validateBankDetails(mBinding.etAccountNumber.getText().toString().trim(), mBinding.etVerifiyAccount.getText().toString().trim(), mBinding.etRoutingNumber.getText().toString().trim(), mBinding.etVerifiyRoutingNumber.getText().toString().trim(), mBinding.etFirstName.getText().toString().trim(), mBinding.etLastName.getText().toString().trim(), mBinding.etBankName.getText().toString().trim());
                if(isValid) {
                    Intent intent = new Intent(mActivity, PayoutDetailsActivity.class);
                    intent.putExtra("accountNumber", mBinding.etAccountNumber.getText().toString().trim());
                    intent.putExtra("routingNumber",  mBinding.etRoutingNumber.getText().toString().trim());
                    intent.putExtra("accountHolderName",  mBinding.etFirstName.getText().toString().trim() + " " + mBinding.etLastName.getText().toString().trim());
                    intent.putExtra("bankName",  mBinding.etBankName.getText().toString().trim());
                    startActivity(intent);
                    mActivity.finish();
                } else {
                    DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, getString(R.string.invalid_info), "Please enter valid bank details", new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {

                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });
                }
                break;
            case R.id.iv_cross:
                mActivity.finish();
                break;
        }
    }

}
