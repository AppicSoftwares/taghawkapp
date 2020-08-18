package com.taghawk.ui.setting.payment_details;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentAddDebitCardBinding;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.util.AppUtils;

public class AddDebitCardFragment extends BaseFragment implements View.OnClickListener {

    private FragmentAddDebitCardBinding mBinding;
    private Activity mActivity;
    private AddBankAccountViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentAddDebitCardBinding.inflate(inflater);
        initView();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(AddBankAccountViewModel.class);
        viewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        viewModel.getAddDebitCardLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                showToastShort(commonResponse.getMessage());
                mActivity.setResult(Activity.RESULT_CANCELED);
                mActivity.finish();
            }
        });
    }

    private void initView() {
        mActivity = getActivity();
        mBinding.tvAdd.setOnClickListener(this);
        mBinding.includeHeader.tvTitle.setText(getString(R.string.add_debit_card));
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.etExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (mBinding.etExpiry.getText().toString().trim().length() == 2) {
//                    mBinding.etExpiry.setText(mBinding.etExpiry.getText().toString().trim() + "/");
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 && (editable.length() % 3) == 0) {
                    final char c = editable.charAt(editable.length() - 1);
                    if ('/' == c) {
                        editable.delete(editable.length() - 1, editable.length());
                    }
                }
                if (editable.length() > 0 && (editable.length() % 3) == 0) {
                    char c = editable.charAt(editable.length() - 1);
                    if (Character.isDigit(c) && TextUtils.split(editable.toString(), String.valueOf("/")).length <= 2) {
                        editable.insert(editable.length() - 1, String.valueOf("/"));
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                if (validate(mBinding.etCardNumber.getText().toString().trim(), mBinding.etExpiry.getText().toString().trim(), mBinding.etCvv.getText().toString().trim())) {
                    getLoadingStateObserver().onChanged(true);
                    onAddCard(mActivity, mBinding.etCardNumber.getText().toString().trim(), mBinding.etExpiry.getText().toString().trim(), mBinding.etCvv.getText().toString().trim(), mBinding.etCardHolderName.getText().toString().trim());
                }
                break;
            case R.id.iv_cross:
                mActivity.finish();
                break;
        }
    }

    private boolean validate(String cardNumber, String expiry, String cvv) {
        if (cardNumber.length() == 0) {
            showToastLong(getString(R.string.please_enter_card_number));
            return false;
        } else if (cardNumber.length() < 16) {
            showToastLong(getString(R.string.invalid_card_number));
            return false;
        } else if (expiry.length() == 0) {
            showToastLong(getString(R.string.please_enter_expiry_of_card));
            return false;
        } else if (expiry.length() < 5) {
            showToastLong(getString(R.string.invalid_expiry_number));
            return false;
        } else if (cvv.length() == 0) {
            showToastLong(getString(R.string.please_enter_cvv));
            return false;
        }

        return true;
    }

    public void onAddCard(final Context context, String cardNumber, String expiry, String cardCVC, final String name) {

        Integer cardExpMonth = Integer.parseInt(expiry.split("/")[0]);
        Integer cardExpYear = Integer.parseInt(expiry.split("/")[1]);

        String token1 = "";
//        create(cardNumber,
//                cardExpMonth,
//                cardExpYear,
//                cardCVC)
        final Card card = new Card.Builder(cardNumber,
                cardExpMonth,
                cardExpYear,
                cardCVC)
                .currency("usd").name(name)
                .build();
        card.validateNumber();
        card.validateCVC();
        Stripe stripe = new Stripe(context, AppConstants.STRIPE_KEY);
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        // Send token to your own web service
                        String token1 = token.getId();
                        if (AppUtils.isInternetAvailable(mActivity)) {
                            viewModel.addDebitCard(token1, name);
                        } else {
                            getLoadingStateObserver().onChanged(false);
                            showNoNetworkError();
                        }
                    }

                    public void onError(Exception error) {
                        getLoadingStateObserver().onChanged(false);
                        Toast.makeText(context,
                                error.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

}
