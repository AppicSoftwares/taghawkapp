package com.taghawk.ui.setting.payment_details;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.TagHawkApplication;
import com.taghawk.base.BaseActivity;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentPaymentDetailsBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.PayoutVendorRequestModel;
import com.taghawk.model.VendorRetrieveModel;
import com.taghawk.model.cashout.MerchantDetailBeans;
import com.taghawk.model.profileresponse.BalanceResponse;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.profile.ProfileEditActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_PASS;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_URL;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_USER;
import static com.taghawk.bluesnap.BlueSnapDetails.VENDORS;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;

public class PaymentDetailFragment extends BaseFragment implements View.OnClickListener {
    private static ProgressDialog pgdialog;
    private FragmentPaymentDetailsBinding mBinding;
    private Activity mActivity;
    private HomeViewModel mHomeViewModel;
    private double currentAmount;
    private MerchantDetailBeans beans;
    private String payOutType;
    private VendorRetrieveModel vendorRetrieveModel;
    private String vendorPayoutStatus = "Incomplete";
    private JSONObject retrieveVendorJson;
    private boolean isDuplicatePayoutRequest = false;
    private String payoutRequestTime = "12AM (PST)";
    private boolean isResumed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentPaymentDetailsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
//        mHomeViewModel.getmCreateMerchentLiveData().observe(this, new Observer<CreateMercentResponse>() {
//            @Override
//            public void onChanged(@Nullable CreateMercentResponse createMercentResponse) {
//                getLoadingStateObserver().onChanged(false);
//                if (createMercentResponse.getCode() == 200 && createMercentResponse.getMerchentId() != null) {
//                    DataManager.getInstance().saveMerchentId(createMercentResponse.getMerchentId());
//                    if (DataManager.getInstance().getMerchentId() != null && DataManager.getInstance().getMerchentId().length() > 0) {
//                        if (!(beans != null && beans.getMerchantDetailData() != null && beans.getMerchantDetailData().getExternalAccountData() != null && beans.getMerchantDetailData().getExternalAccountData().getId() != null)) {
//                            openDepositeAccountActivity(true);
//                        } else if (!((DataManager.getInstance().getSSNnumber() != null && DataManager.getInstance().getSSNnumber().length() > 0) || DataManager.getInstance().getIsPassport()) || !(DataManager.getInstance().getDob() != null && DataManager.getInstance().getDob().length() > 0) || !DataManager.getInstance().isPhoneVerified() || DataManager.getInstance().getAddressLineOne().length() == 0) {
//                            openAddAccountACtivity(true);
//                        }
//                    }
////                    openDepositeAccountActivity(true);
//                } else if (createMercentResponse.getCode() == 400) {
//                    getCustomBottomDialog(getString(R.string.verify), createMercentResponse.getMessage(), new OnDialogItemClickListener() {
//                        @Override
//                        public void onPositiveBtnClick() {
//                        }
//
//                        @Override
//                        public void onNegativeBtnClick() {
//
//                        }
//                    });
//                }
//            }
//        });
//        mHomeViewModel.getVendorIdLiveData().observe(this, new Observer<VendorIdResponse>() {
//            @Override
//            public void onChanged(@Nullable VendorIdResponse  vendorIdResponse) {
//                getLoadingStateObserver().onChanged(false);
//                if (vendorIdResponse.getStatusCode() == 200/* && createMercentResponse.getMerchentId() != null*/) {
//
////                    DataManager.getInstance().saveMerchentId(createMercentResponse.getMerchentId());
////                    if (DataManager.getInstance().getMerchentId() != null && DataManager.getInstance().getMerchentId().length() > 0) {
////                        if (!(beans != null && beans.getMerchantDetailData() != null && beans.getMerchantDetailData().getExternalAccountData() != null && beans.getMerchantDetailData().getExternalAccountData().getId() != null)) {
////                            openDepositeAccountActivity(true);
////                        } else if (!((DataManager.getInstance().getSSNnumber() != null && DataManager.getInstance().getSSNnumber().length() > 0) || DataManager.getInstance().getIsPassport()) || !(DataManager.getInstance().getDob() != null && DataManager.getInstance().getDob().length() > 0) || !DataManager.getInstance().isPhoneVerified() || DataManager.getInstance().getAddressLineOne().length() == 0) {
////                            openAddAccountACtivity(true);
////                        }
////                    }
////                    openDepositeAccountActivity(true);
//                } else if (vendorIdResponse.getStatusCode() == 400) {
////                    getCustomBottomDialog("Create Vendor", vendorIdResponse.getMessage(), new OnDialogItemClickListener() {
////                        @Override
////                        public void onPositiveBtnClick() {
////                        }
////
////                        @Override
////                        public void onNegativeBtnClick() {
////
////                        }
////                    });
//                }
//            }
//        });
        mHomeViewModel.cashOutLiveDAta().observe(this, new Observer<BalanceResponse>() {
            @Override
            public void onChanged(@Nullable BalanceResponse balanceResponse) {
//                showToastShort(balanceResponse.getMessage());
//                getLoadingStateObserver().onChanged(false);
                mBinding.tvCashOut.setEnabled(true);
                if (balanceResponse.getBalanceData() != null && balanceResponse.getBalanceData().getCashOutBalance() != null) {
                    currentAmount = balanceResponse.getBalanceData().getCashOutBalance();
                    try {
                        DataManager.getInstance().saveCashOutBalance("" + currentAmount);

                        mBinding.tvAmount.setText(getString(R.string.available_now) + ": " + "$ " + new DecimalFormat("##.##").format(balanceResponse.getBalanceData().getCashOutBalance()));
                        mBinding.tvPendingAmount.setText(getString(R.string.awaiting_buyers_confirmation) + ": " + "$ " + new DecimalFormat("##.##").format(balanceResponse.getBalanceData().getPendingBalance()));
                        mBinding.tvAvaliableSoon.setText(getString(R.string.available_soon) + ": " + "$ " + new DecimalFormat("##.##").format(balanceResponse.getBalanceData().getAvailableSoonBalance()));
                        mBinding.tvPendingCashout.setText("Pending Cash-out: $" + new DecimalFormat("##.##").format(balanceResponse.getBalanceData().getPendingCashOutBalance()));
                        mBinding.tvTotalEarnings.setText("Total Earnings To Date: $" + new DecimalFormat("##.##").format(balanceResponse.getBalanceData().getTotalEarningBalance()));

                    } catch (Exception e) {
                        mBinding.tvAmount.setText(getString(R.string.available_now) + ": " + "$ " + balanceResponse.getBalanceData().getCashOutBalance());
                        mBinding.tvPendingAmount.setText(getString(R.string.awaiting_buyers_confirmation) + ": " + "$ " + balanceResponse.getBalanceData().getPendingBalance());
                        mBinding.tvAvaliableSoon.setText(getString(R.string.available_soon) + ": " + "$ " + balanceResponse.getBalanceData().getAvailableSoonBalance());
                        mBinding.tvPendingCashout.setText("Pending Cash-out: $" + balanceResponse.getBalanceData().getPendingCashOutBalance());
                        mBinding.tvTotalEarnings.setText("Total Earnings To Date: $" + balanceResponse.getBalanceData().getTotalEarningBalance());

                    }

                    getBlueSnapPayout(1000000);

                }

                Log.e("isduplicate", "" + balanceResponse.getBalanceData().getCashOutTime());
                if (balanceResponse.getBalanceData().getCashOutTime() != 1) {
                    isDuplicatePayoutRequest = true;
                } else {
                    isDuplicatePayoutRequest = false;
                }

                getLoadingStateObserver().onChanged(false);
                String title = "Congratulations!";
                String message = "Your cash-out is scheduled to be processed today at 12AM (PST). \n" +
                        "\n" +
                        "Note: weekend cash-outs are processed at 12AM (PST) on next Monday.\n";
                DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, title, message, new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {

                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });

//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//                Date now = new Date();
//                String sDateTimeNow = simpleDateFormat.format(now);
//                Date dateTimeNow = new Date();
//                Date datePayoutRequest = new Date();
//                try {
//                    dateTimeNow = simpleDateFormat.parse(sDateTimeNow);
//                    datePayoutRequest = simpleDateFormat.parse(balanceResponse.getBalanceData().getCashOutDate());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                Log.e("current_time", "" + dateTimeNow);
//                long diffMilliSecs = dateTimeNow.getTime() - datePayoutRequest.getTime();
//                payoutRequestHours = (int) diffMilliSecs / (1000 * 60 * 60);
//                Log.e("payoutRequestHours", "" + payoutRequestHours);

            }
        });
        mHomeViewModel.getBalanceLiveData().observe(this, new Observer<BalanceResponse>() {
            @Override
            public void onChanged(@Nullable BalanceResponse balanceResponse) {
                if (balanceResponse.getBalanceData() != null && balanceResponse.getBalanceData().getCashOutBalance() != null) {
                    currentAmount = balanceResponse.getBalanceData().getCashOutBalance();
                    try {
                        DataManager.getInstance().saveCashOutBalance("" + currentAmount);

                        mBinding.tvAmount.setText(getString(R.string.available_now) + ": " + "$ " + new DecimalFormat("##.##").format(balanceResponse.getBalanceData().getCashOutBalance()));
                        mBinding.tvPendingAmount.setText(getString(R.string.awaiting_buyers_confirmation) + ": " + "$ " + new DecimalFormat("##.##").format(balanceResponse.getBalanceData().getPendingBalance()));
                        mBinding.tvAvaliableSoon.setText(getString(R.string.available_soon) + ": " + "$ " + new DecimalFormat("##.##").format(balanceResponse.getBalanceData().getAvailableSoonBalance()));
                        mBinding.tvPendingCashout.setText("Pending Cash-out: $" + new DecimalFormat("##.##").format(balanceResponse.getBalanceData().getPendingCashOutBalance()));
                        mBinding.tvTotalEarnings.setText("Total Earnings To Date: $" + new DecimalFormat("##.##").format(balanceResponse.getBalanceData().getTotalEarningBalance()));

                    } catch (Exception e) {
                        mBinding.tvAmount.setText(getString(R.string.available_now) + ": " + "$ " + balanceResponse.getBalanceData().getCashOutBalance());
                        mBinding.tvPendingAmount.setText(getString(R.string.awaiting_buyers_confirmation) + ": " + "$ " + balanceResponse.getBalanceData().getPendingBalance());
                        mBinding.tvAvaliableSoon.setText(getString(R.string.available_soon) + ": " + "$ " + balanceResponse.getBalanceData().getAvailableSoonBalance());
                        mBinding.tvPendingCashout.setText("Pending Cash-out: $" + balanceResponse.getBalanceData().getPendingCashOutBalance());
                        mBinding.tvTotalEarnings.setText("Total Earnings To Date: $" + balanceResponse.getBalanceData().getTotalEarningBalance());

                    }

                    if (balanceResponse.getBalanceData().getVendorsId() != null && !TextUtils.isEmpty(balanceResponse.getBalanceData().getVendorsId())) {
                        DataManager.getInstance().saveVendorId(balanceResponse.getBalanceData().getVendorsId());
                        getLoadingStateObserver().onChanged(true);
                        TagHawkApplication.mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                retrieveBlueSnapVendor();
                            }
                        });
                    } else {
                        //open create vendor id screen

                    }

                    Log.e("isduplicate", "" + balanceResponse.getBalanceData().getCashOutTime());
                    if (balanceResponse.getBalanceData().getCashOutTime() != 1) {
                        isDuplicatePayoutRequest = true;
                    } else {
                        isDuplicatePayoutRequest = false;
                    }

//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//                    Date now = new Date();
//                    String sDateTimeNow = simpleDateFormat.format(now);
//                    Date dateTimeNow = new Date();
//                    Date datePayoutRequest = new Date();
//                    try {
//                        dateTimeNow = simpleDateFormat.parse(sDateTimeNow);
//                        datePayoutRequest = simpleDateFormat.parse(balanceResponse.getBalanceData().getCashOutDate());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    Log.e("current_time", "" + dateTimeNow);
//                    long diffMilliSecs = dateTimeNow.getTime() - datePayoutRequest.getTime();
//                    payoutRequestHours = (int) diffMilliSecs / (1000 * 60 * 60);
//                    Log.e("payoutRequestHours", "" + payoutRequestHours);

                }
            }
        });
        mHomeViewModel.merchantDetailLiveData().observe(this, new Observer<MerchantDetailBeans>() {

            @Override
            public void onChanged(@Nullable MerchantDetailBeans merchantDetailBeans) {
                getLoadingStateObserver().onChanged(false);
                beans = merchantDetailBeans;
                if (beans != null && beans.getMerchantDetailData() != null && beans.getMerchantDetailData().getExternalAccountData() != null && beans.getMerchantDetailData().getExternalAccountData().getLast4() != null) {
//                    mBinding.tvAccountNumber.setText("xxxxxxx" + beans.getMerchantDetailData().getExternalAccountData().getLast4());
                    if (beans.getMerchantDetailData().getExternalAccountData().getAccountType().equalsIgnoreCase("card")) {
                        mBinding.tvAccountTxt.setText(getString(R.string.debit_card_number));
//                        payOutType = "card";
                    } else {
//                        payOutType = "card";
                        mBinding.tvAccountTxt.setText(getString(R.string.account_number));
                    }

                }
            }
        });

        if (AppUtils.isInternetAvailable(mActivity)) {
            mHomeViewModel.getBalance();

        } else
            showNoNetworkError();
//        mHomeViewModel.merchantDetailRefreshLiveData().observe(this, new Observer<MerchantDetailBeans>() {
//            @Override
//            public void onChanged(@Nullable MerchantDetailBeans merchantDetailBeans) {
//                getLoadingStateObserver().onChanged(false);
//                beans = merchantDetailBeans;
//                performPayoutDisableAction();
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if(isResumed) {
            if (DataManager.getInstance().getVendorId() != null && !TextUtils.isEmpty(DataManager.getInstance().getVendorId())) {
                if (AppUtils.isInternetAvailable(mActivity)) {
                    mHomeViewModel.getBalance();

                } else
                    showNoNetworkError();
            }
        }
    }

    private void initView() {
        mActivity = getActivity();
//        if (getArguments() != null) {
//            boolean isopenAddBank = getArguments().getBoolean(AppConstants.BUNDLE_DATA, false);
//            if (isopenAddBank) {
//                openAddAccountACtivity();
//            }
//        }
//        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.includeHeader.tvTitle.setText(getString(R.string.payment_details));
        mBinding.includeHeader.ivCross.setOnClickListener(this);
//        mBinding.tvAccountNumber.setOnClickListener(this);
        mBinding.llIncludeHeader.setVisibility(View.GONE);
        mBinding.tvCashOut.setOnClickListener(this);
        mBinding.tvMoreInfo.setOnClickListener(this);
        mBinding.tvPayoutProfileUpdateBtn.setOnClickListener(this);

    }

    public void setData() {
//        currentAmount = Double.valueOf(DataManager.getInstance().getCashOutBalance());
//        mBinding.tvAmount.setText("$ " + Double.valueOf(DataManager.getInstance().getCashOutBalance()));
        if (vendorRetrieveModel != null && vendorRetrieveModel.getPayoutInfo() != null &&
                vendorRetrieveModel.getPayoutInfo().size() > 0 && vendorRetrieveModel.getPayoutInfo().get(0).getBankAccountId() != null &&
                vendorRetrieveModel.getPayoutInfo().get(0).getBankAccountId().length() > 0) {

            mBinding.tvAccountNumber.setText("xxxxxxx" + vendorRetrieveModel.getPayoutInfo().get(0).getBankAccountId().substring(vendorRetrieveModel.getPayoutInfo().get(0).getBankAccountId().length() - 4, vendorRetrieveModel.getPayoutInfo().get(0).getBankAccountId().length()));
            mBinding.tvAccountNumber.setTextColor(getResources().getColor(R.color.txt_black));
        } else {
            mBinding.tvAccountNumber.setText("xxxxxxx");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tv_account_number:
//                if (currentAmount > 0)
//                    openDepositeAccountActivity(false);
//                else {
//                    showToastShort(getString(R.string.dont_have_balance_for_cashout));
//                }
//                break;
            case R.id.iv_cross:
                mActivity.onBackPressed();
                break;
            case R.id.tv_cash_out:
                if (!(DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    performCashOutAction();
                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, (BaseActivity) mActivity);
                }

                break;
            case R.id.tv_more_info:
                DialogUtil dialogUtil = DialogUtil.getInstance();
                String content = "• The minimum cashout amount is $10.\n" +
                        "• if you opt to cash out, your entire available balance will be processed at 12AM(PST).\n" +
                        "• Cash out that occurs on the weekend aggregates to the next Monday.\n" +
                        "• Cash out fee is 0.25% of the transfer amount + $0.5\n" +
                        "• You will receive the money within 3 business days.\n" +
                        "• If you have any question, please contact Support@taghawk.app\n" +
                        "";
                dialogUtil.customBottomSheetWalletDialog(mActivity, content);
                break;
            case R.id.tv_payout_profile_update_btn:
                if (!TextUtils.isEmpty(DataManager.getInstance().getVendorId()))
                    openAddAccountACtivity(false);
                else
                    DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, "Update Account Info", "You haven't sold any of your products yet. Please sell product first to update your cash out profile", new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {

                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });
                break;
        }

    }

//    private void performPayoutDisableAction() {
//        if (beans != null && ((DataManager.getInstance().getIsPassport() && !beans.getMerchantDetailData().isPassportVerified()) || (beans.getMerchantDetailData().isSSNLast4Provided() && !beans.getMerchantDetailData().isSsnVerified()))) {
//            String str = "";
//            if ((beans.getMerchantDetailData().isSSNLast4Provided()) && !beans.getMerchantDetailData().isSsnVerified()) {
//                if (str.length() > 0) {
//                    str = str + "," + "SSN";
//                } else
//                    str = "SSN";
//            }
//            if (DataManager.getInstance().getIsPassport() && !beans.getMerchantDetailData().isPassportVerified()) {
//                if (str.length() > 0) {
//                    str = str + "," + "Passport";
//                } else
//                    str = "Passport";
//            }
//
//            if (beans.getMerchantDetailData().getVerificationSSNData().getStatus().equalsIgnoreCase(AppConstants.SSN_VERIFICATION_STATUS.PENDING)) {
//                getCustomBottomDialog(getString(R.string.accout_pending), getString(R.string.stripe), new OnDialogItemClickListener() {
//                    @Override
//                    public void onPositiveBtnClick() {
//                    }
//
//                    @Override
//                    public void onNegativeBtnClick() {
//
//                    }
//                });
//
//            } else {
//                if (str.length() > 0) {
//                    getCustomBottomDialog(getString(R.string.verification_status), str + " " + getString(R.string.rejected_msg), new OnDialogItemClickListener() {
//                        @Override
//                        public void onPositiveBtnClick() {
//                            openEmail("support@taghawk.app", "");
//                        }
//
//                        @Override
//                        public void onNegativeBtnClick() {
//
//                        }
//                    });
//                } else {
//                    mHomeViewModel.merchantDetails();
//                }
//            }
//
//        } else if (beans != null && !beans.getMerchantDetailData().isAddressVerified()) {
//            getCustomBottomDialog(getString(R.string.verification_status), getString(R.string.address_rejected), new OnDialogItemClickListener() {
//                @Override
//                public void onPositiveBtnClick() {
//                }
//
//                @Override
//                public void onNegativeBtnClick() {
//
//                }
//            });
//
//        } else {
//            getCustomBottomDialog(getString(R.string.accout_pending), getString(R.string.stripe), new OnDialogItemClickListener() {
//                @Override
//                public void onPositiveBtnClick() {
//                }
//
//                @Override
//                public void onNegativeBtnClick() {
//
//                }
//            });
//        }
//    }

    private void performCashOutAction() {

        if (!TextUtils.isEmpty(DataManager.getInstance().getVendorId()) && currentAmount >= 0) {

            if (vendorPayoutStatus.equalsIgnoreCase("Approved")) {

                //24 hours stop duplicate request code here
                if (!isDuplicatePayoutRequest) {
                    performAction();
//                    String message = "Do you want to cash out the entire balance?";
//                    DialogUtil.getInstance().showCancelableAlertDialog(mActivity, "", message, "Yes", "Cancel", new OnDialogItemClickListener() {
//                        @Override
//                        public void onPositiveBtnClick() {
//
//                        }
//
//                        @Override
//                        public void onNegativeBtnClick() {
//
//                        }
//                    });

                } else {
                    String title = "Duplicate Cash-out Request";
                    String message = "You have a cash-out scheduled for " + payoutRequestTime + " today. Your entire available balance will be processed.\n" +
                            "\n" +
                            "You can only cash out once a day. Please try again after 24 hours.\n";
                    DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, title, message, new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {

                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });
                }

            } else {
                openAddAccountACtivity(false);
//                DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, "Account Verification", "Please complete profile with valid bank details first to start cash-out.", new OnDialogItemClickListener() {
//                    @Override
//                    public void onPositiveBtnClick() {
//
//                    }
//
//                    @Override
//                    public void onNegativeBtnClick() {
//
//                    }
//                });
            }

        } else {
//                showToastShort(getString(R.string.dont_have_balance_for_cashout));
            DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, "Cash Out", "The minimum cash out amount is $10.", new OnDialogItemClickListener() {
                @Override
                public void onPositiveBtnClick() {

                }

                @Override
                public void onNegativeBtnClick() {

                }
            });
        }


    }

    private String getSpannableText() {
        String str = getString(R.string.account_pending_status_msg);
        Spannable spannable = new SpannableStringBuilder(str);
        Typeface font = Typeface.createFromAsset(mActivity.getAssets(), "galano_grotesque_bold.otf");
        spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 45, 52, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#6772e4")), 45, 52, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(1.2f), 45, 52, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size
        spannable.setSpan(font, 45, 52, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // set size

        return spannable.toString();
    }

    private void openDepositeAccountActivity(boolean type) {
        Intent intent = new Intent(mActivity, FragmentDepositMoneyActivity.class);
        intent.putExtra("IS_FROM_CASH_OUT", type);
        startActivityForResult(intent, 2002);
    }

    private void openEditProfileFragment() {
        Intent intent = new Intent(mActivity, ProfileEditActivity.class);
        startActivity(intent);
    }

    private void openAddAccountACtivity(boolean type) {
        isResumed = true;
        Intent intent = new Intent(mActivity, AddAccountActivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, type);
        try {
            String bankAccountId = ((JSONObject) retrieveVendorJson.getJSONArray("payoutInfo").get(0)).getString("bankAccountId");
            if (!TextUtils.isEmpty(bankAccountId))
                intent.putExtra("accountNumber", bankAccountId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String bankId = ((JSONObject) retrieveVendorJson.getJSONArray("payoutInfo").get(0)).getString("bankId");
            if (!TextUtils.isEmpty(bankId))
                intent.putExtra("routingNumber", bankId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String nameOnAccount = ((JSONObject) retrieveVendorJson.getJSONArray("payoutInfo").get(0)).getString("nameOnAccount");
            if (!TextUtils.isEmpty(nameOnAccount))
                intent.putExtra("accountHolderName", nameOnAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String bankName = ((JSONObject) retrieveVendorJson.getJSONArray("payoutInfo").get(0)).getString("bankName");
            if (!TextUtils.isEmpty(bankName))
                intent.putExtra("bankName", bankName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mActivity.startActivityForResult(intent, 1001);
    }

    private void openAddBankDetailActivity() {
        Intent intent = new Intent(mActivity, PaymentDetailsActivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, true);
        mActivity.startActivity(intent);
    }

    private void performAction() {
        DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.cash_out), getString(R.string.cash_out_msg), getString(R.string.cash_out), getString(R.string.cencel), new DialogCallback() {
            @Override
            public void submit(String data) {
                mBinding.tvCashOut.setEnabled(false);
                getLoadingStateObserver().onChanged(true);
                getBlueSnapPayout(((int) currentAmount) - 1);
            }

            @Override
            public void cancel() {

            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 1001:
//                onActivityResultAction(resultCode);
//                break;
//            case 2002:
//                onActivityResultAction(resultCode);
//                break;
//        }
//    }

//    private void onActivityResultAction(int resultCode) {
//        if (resultCode == Activity.RESULT_OK) {
////                    if (DataManager.getInstance().getAccountNumber() != null && DataManager.getInstance().getAccountNumber().length() > 0) {
////                        String accountNumber = DataManager.getInstance().getAccountNumber();
////                        mBinding.tvAccountNumber.setText("xxxxxxx" + accountNumber.substring(accountNumber.length() - 4, accountNumber.length()));
////                        mBinding.tvAccountNumber.setTextColor(getResources().getColor(R.color.txt_black));
////                    }
//
//            showDialogDataProcessing(mActivity);
//            new CountDownTimer(10000, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                }
//
//                @Override
//                public void onFinish() {
//                    if (pgdialog != null && pgdialog.isShowing())
//                        pgdialog.dismiss();
//                    if (AppUtils.isInternetAvailable(mActivity)) {
//                        mHomeViewModel.getBalance();
//                        mHomeViewModel.getVendor();
//                    } else
//                        showNoNetworkError();
//                }
//            }.start();
//
//        } else {
//            if (AppUtils.isInternetAvailable(mActivity)) {
//                mHomeViewModel.getBalance();
//                mHomeViewModel.getVendor();
//            } else
//                showNoNetworkError();
//        }
//    }

    public void openEmail(String emailAddress, String subject) {
        String mailto = "mailto:" + emailAddress +
                "?subject=" + Uri.encode(subject);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));
        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            //TODO: Handle case where no email app is available
        }
    }

    public static void showDialogDataProcessing(Context context) {
        try {
            if (pgdialog != null)
                if (pgdialog.isShowing())
                    pgdialog.dismiss();
            pgdialog = ProgressDialog.show(context, null, null, true);
            pgdialog.setContentView(R.layout.progress_data_processing);
            pgdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pgdialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveBlueSnapVendor() {

        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        List<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));

        List<CustomHTTPParams> sandboxHttpHeaders = headerParams;

        String vendorId = DataManager.getInstance().getVendorId();

        final Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
                    ArrayList<CustomHTTPParams> headerParams = new ArrayList<>();
                    headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
                    BlueSnapHTTPResponse response = HTTPOperationController.get(SANDBOX_URL + VENDORS + "/" + vendorId, "application/json", "application/json", sandboxHttpHeaders);
                    Log.e("API", SANDBOX_URL + VENDORS + "/" + vendorId);
                    Log.e("API_HEADER", "Authorization : " + basicAuth);
                    String responseString = response.getResponseString();

                    if (response.getResponseCode() == HTTP_OK) {
//                        String location = response.getHeaders().get("Location").get(0);

                        Log.e("API_RESPONSE", "" + responseString);
                        retrieveVendorJson = new JSONObject(responseString);

                        vendorRetrieveModel = new Gson().fromJson(responseString, VendorRetrieveModel.class);
                        if (vendorRetrieveModel.getVerification().getPayoutStatus().equalsIgnoreCase("Approved")) {
                            vendorPayoutStatus = vendorRetrieveModel.getVerification().getPayoutStatus();
                        }

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("*****Done*****");
                                getLoadingStateObserver().onChanged(false);
                                setData();

                            }
                        }, 10);


                    } else {

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("*****Done*****");
                                getLoadingStateObserver().onChanged(false);
                                DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.card_error_info), new OnDialogItemClickListener() {
                                    @Override
                                    public void onPositiveBtnClick() {

                                    }

                                    @Override
                                    public void onNegativeBtnClick() {

                                    }
                                });
                            }
                        }, 10);
                    }

                } catch (Exception e) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("*****Done*****");
                            getLoadingStateObserver().onChanged(false);
//                            DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.card_error_info), new OnDialogItemClickListener() {
//                                @Override
//                                public void onPositiveBtnClick() {
//
//                                }
//
//                                @Override
//                                public void onNegativeBtnClick() {
//
//                                }
//                            });
                        }
                    }, 10);
                }

            }
        };

        TagHawkApplication.mainHandler.post(myRunnable);

    }

//    private void getBlueSnapPayout() {
//
//        showToastLong("Payout process functionality is pending");
//
////        mHomeViewModel.cashOutBalance(currentAmount);
//
//    }

    private void getBlueSnapPayout(int minPayoutAmount) {

        PayoutVendorRequestModel requestModel = new PayoutVendorRequestModel();
        requestModel.setEmail(vendorRetrieveModel.getEmail());
        requestModel.setName(vendorRetrieveModel.getName());
        requestModel.setFirstName(vendorRetrieveModel.getFirstName());
        requestModel.setLastName(vendorRetrieveModel.getLastName());
        requestModel.setPhone(vendorRetrieveModel.getPhone());
        requestModel.setAddress(vendorRetrieveModel.getAddress());
        requestModel.setCity(vendorRetrieveModel.getCity());
        requestModel.setCountry(vendorRetrieveModel.getCountry());
        requestModel.setZip(vendorRetrieveModel.getZip());
        requestModel.setState(vendorRetrieveModel.getState());
        requestModel.setDefaultPayoutCurrency(vendorRetrieveModel.getDefaultPayoutCurrency());

        PayoutVendorRequestModel.VendorPrincipal vendorPrincipal = new PayoutVendorRequestModel.VendorPrincipal();
        vendorPrincipal.setFirstName(vendorRetrieveModel.getVendorPrincipal().getFirstName());
        vendorPrincipal.setLastName(vendorRetrieveModel.getVendorPrincipal().getLastName());
        vendorPrincipal.setAddress(vendorRetrieveModel.getVendorPrincipal().getAddress());
        vendorPrincipal.setCity(vendorRetrieveModel.getVendorPrincipal().getCity());
        vendorPrincipal.setZip(vendorRetrieveModel.getVendorPrincipal().getZip());
        vendorPrincipal.setCountry(vendorRetrieveModel.getVendorPrincipal().getCountry());
        vendorPrincipal.setDob(vendorRetrieveModel.getVendorPrincipal().getDob());
        vendorPrincipal.setPersonalIdentificationNumber(vendorRetrieveModel.getVendorPrincipal().getPersonalIdentificationNumber());
        vendorPrincipal.setDriverLicenseNumber(vendorRetrieveModel.getVendorPrincipal().getDriverLicenseNumber());
        vendorPrincipal.setPassportNumber(vendorRetrieveModel.getVendorPrincipal().getPassportNumber());
        vendorPrincipal.setEmail(vendorRetrieveModel.getVendorPrincipal().getEmail());
        requestModel.setVendorPrincipal(vendorPrincipal);

        PayoutVendorRequestModel.VendorAgreement vendorAgreement = new PayoutVendorRequestModel.VendorAgreement();
        vendorAgreement.setCommissionPercent(vendorRetrieveModel.getVendorAgreement().getCommissionPercent());

        ArrayList<PayoutVendorRequestModel.PayoutInfo> payoutInfoList = new ArrayList<>();
        PayoutVendorRequestModel.PayoutInfo payoutInfo = new PayoutVendorRequestModel.PayoutInfo();
        payoutInfo.setPayoutType(vendorRetrieveModel.getPayoutInfo().get(0).getPayoutType());
        payoutInfo.setBaseCurrency(vendorRetrieveModel.getPayoutInfo().get(0).getBaseCurrency());
        payoutInfo.setNameOnAccount(vendorRetrieveModel.getPayoutInfo().get(0).getNameOnAccount());
        payoutInfo.setBankAccountType(vendorRetrieveModel.getPayoutInfo().get(0).getBankAccountType());
        payoutInfo.setBankAccountClass(vendorRetrieveModel.getPayoutInfo().get(0).getBankAccountClass());
        payoutInfo.setBankName(vendorRetrieveModel.getPayoutInfo().get(0).getBankName());
        payoutInfo.setBankId(vendorRetrieveModel.getPayoutInfo().get(0).getBankId());
        payoutInfo.setCountry(vendorRetrieveModel.getPayoutInfo().get(0).getCountry());
        payoutInfo.setState(vendorRetrieveModel.getPayoutInfo().get(0).getState());
        payoutInfo.setCity(vendorRetrieveModel.getPayoutInfo().get(0).getCity());
        payoutInfo.setAddress(vendorRetrieveModel.getPayoutInfo().get(0).getAddress());
        payoutInfo.setBankAccountId(vendorRetrieveModel.getPayoutInfo().get(0).getBankAccountId());
        payoutInfo.setMinimalPayoutAmount(minPayoutAmount);
        payoutInfoList.add(payoutInfo);
        requestModel.setPayoutInfo(payoutInfoList);

        String updateVendorJson = new Gson().toJson(requestModel);
        Log.e("updateVendorJson", "" + updateVendorJson);

        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        List<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));

        List<CustomHTTPParams> sandboxHttpHeaders = headerParams;

        String vendorId = DataManager.getInstance().getVendorId();

        final Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
                    ArrayList<CustomHTTPParams> headerParams = new ArrayList<>();
                    headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
                    BlueSnapHTTPResponse response = HTTPOperationController.put(SANDBOX_URL + VENDORS + "/" + vendorId, updateVendorJson, "application/json", "application/json", sandboxHttpHeaders);
                    Log.e("API", SANDBOX_URL + VENDORS + "/" + vendorId);
                    Log.e("API_HEADER", "Authorization : " + basicAuth);
                    String responseString = response.getResponseString();

                    if (response.getResponseCode() == HTTP_NO_CONTENT) {
//                        String location = response.getHeaders().get("Location").get(0);

                        Log.e("API_RESPONSE", "successfully payout requested");
//                        retrieveVendorJson = new JSONObject(responseString);

                        Handler handler = new Handler(Looper.getMainLooper());

                        if (AppUtils.isInternetAvailable(mActivity)) {
                            if (minPayoutAmount != 1000000) {
                                mHomeViewModel.cashOutBalance(minPayoutAmount);
                            }
                        } else {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("*****Done*****");
                                    getLoadingStateObserver().onChanged(false);

                                }
                            }, 10);
                            showNoNetworkError();
                        }

                    } else {

                        Log.e("RESPONSE", responseString);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("*****Done*****");
                                getLoadingStateObserver().onChanged(false);
                                String title = "Cash-out Request Error";
                                String message = "You are getting some error in requesting payout. Please try again later.\n";
                                DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, title, message, new OnDialogItemClickListener() {
                                    @Override
                                    public void onPositiveBtnClick() {

                                    }

                                    @Override
                                    public void onNegativeBtnClick() {

                                    }
                                });
                            }
                        }, 10);
                    }

                } catch (Exception e) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("*****Done*****");
                            getLoadingStateObserver().onChanged(false);
//                            DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.card_error_info), new OnDialogItemClickListener() {
//                                @Override
//                                public void onPositiveBtnClick() {
//
//                                }
//
//                                @Override
//                                public void onNegativeBtnClick() {
//
//                                }
//                            });
                        }
                    }, 10);
                }

            }
        };

        TagHawkApplication.mainHandler.post(myRunnable);

    }

}
