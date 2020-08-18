package com.taghawk.bluesnap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.ChosenPaymentMethod;
import com.bluesnap.androidapi.models.CreditCard;
import com.bluesnap.androidapi.models.CreditCardInfo;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.PurchaseDetails;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.Shopper;
import com.bluesnap.androidapi.models.ShopperConfiguration;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapLocalBroadcastManager;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapAlertDialog;
import com.bluesnap.androidapi.services.BluesnapServiceCallback;
import com.bluesnap.androidapi.services.CardinalManager;
import com.bluesnap.androidapi.services.KountService;
import com.bluesnap.androidapi.services.TaxCalculator;
import com.bluesnap.androidapi.services.TokenProvider;
import com.bluesnap.androidapi.services.TokenServiceCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.TagHawkApplication;
import com.taghawk.base.BaseActivity;
import com.taghawk.base.NetworkCallback;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.firebase.FirebaseManager;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.BillingAddressDataItem;
import com.taghawk.model.BlueSnapCardListResponse;
import com.taghawk.model.CreateSiftOrderRequest;
import com.taghawk.model.CreateSiftOrderResponse;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentStatusFailureModel;
import com.taghawk.model.PaymentStatusRequest;
import com.taghawk.model.ShippingAddressesResponse;
import com.taghawk.model.VendorCommissionModel;
import com.taghawk.model.cart.CartDataBean;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.request.User;
import com.taghawk.stripe.GooglePayPayment;
import com.taghawk.ui.chat.MessagesDetailActivity;
import com.taghawk.ui.chat.MessagesDetailViewModel;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.ui.profile.AddUpdateAddressActivity;
import com.taghawk.ui.profile.ProfileEditViewModel;
import com.taghawk.util.DialogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_CREATE_TRANSACTION;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_PASS;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_TOKEN_CREATION;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_URL;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_USER;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_VAULTED_SHOPPER;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

public class BlueSnapPaymentCardActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatImageView imageViewClose;
    private AppCompatTextView tvTitle;
    private AppCompatTextView tvReset;
    private AppCompatImageView ivShare;
    private AppCompatEditText etCardNumber;
    private AppCompatEditText etExpiry;
    private AppCompatEditText etCvv;
    private AppCompatEditText etCardHolderName;
    private AppCompatEditText etZipCode;
    private CheckBox checkBoxSaveCard;
    private AppCompatTextView textViewPay;
    private RelativeLayout tvCheckout;
    private ProgressBar progressBarPay;
    private LinearLayout llNewCard;
    private RecyclerView rvSavedCards;
    private AppCompatTextView textViewAddCard;
    private LinearLayout llSavedCards;
    private LinearLayout llBillingAddress;
    private AppCompatTextView textViewConfirmPurchase;
    private LinearLayout llSavedCardPayOption;
    private AppCompatTextView mTvBillingFullName;
    private AppCompatTextView mTvBillingAddress;
    private AppCompatTextView mTvBillingMobile;
    private ImageView mIvBillingEdit;
    private CheckBox mCheckBoxSameAsShipping;
    private LinearLayout mLlSameAsShipping;
    private LinearLayout mLlGooglePay;

    private static final String TAG = BlueSnapPaymentCardActivity.class.getSimpleName();
    private Activity mActivity;
    private double totalAmount = 0d;

    private BlueSnapService blueSnapService;
    private TokenProvider tokenProvider;
    private String merchantToken;
    private SdkRequestBase sdkRequest;

    private String SHOPPER_ID = "SHOPPER_ID";
    private String message;
    private String title;
    private Context context;
    private String transactionId;
    private String cardLastFourDigits = "";
    private String tokenSuffix = "";

    ArrayList<CartDataBean> mCartList = new ArrayList<>();
    private int cartItemCount = -1;
    private MessagesDetailViewModel messagesDetailViewModel;
    private ProfileEditViewModel profileEditViewModel;
    private HashMap<String, Object> parms;
    private String screen = "";
    private Observer<FailureResponse> mFailureObserver;
    private Observer<Throwable> mErrorObserver;

    private String getShopperResponse;

    BlueSnapCardListResponse blueSnapResponse;

    private int selectedCardPosition = 0;
    private ArrayList<BlueSnapCardListResponse.CreditCardInfo> cardInfoArrayList = new ArrayList<>();

    private boolean transactionStatus = false;

    private ArrayList<VendorCommissionModel> vendorsCommissionsList = new ArrayList<>();

    private String vendorIdString = "";

    private BillingAddressDataItem billingAddress = new BillingAddressDataItem();
    private boolean isNewCard = false;
    private boolean isTransaction = false;
    private boolean isGooglePay = false;

    @Override
    protected int getResourceId() {
        return R.layout.activity_bluesnap_payment_card;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bluesnap_payment_card);

        mActivity = BlueSnapPaymentCardActivity.this;

        if (getIntent() != null) {
            totalAmount = Double.parseDouble(getIntent().getStringExtra("totalAmount"));
            screen = getIntent().getStringExtra("screen");
            mCartList = getIntent().getParcelableArrayListExtra("cartList");
            if (getIntent().hasExtra("shipToParams")) {
                parms = (HashMap<String, Object>) getIntent().getSerializableExtra("shipToParams");
            }
        }


        initView();
    }

    private void initView() {

        imageViewClose = findViewById(R.id.image_view_close);
        tvTitle = findViewById(R.id.tv_title);
        tvReset = findViewById(R.id.tv_reset);
        ivShare = findViewById(R.id.iv_share);
        etCardNumber = findViewById(R.id.et_card_number);
        etExpiry = findViewById(R.id.et_expiry);
        etCvv = findViewById(R.id.et_cvv);
        etCardHolderName = findViewById(R.id.et_card_holder_name);
        etZipCode = findViewById(R.id.et_zip_code);
        checkBoxSaveCard = findViewById(R.id.check_box_save_card);
        textViewPay = findViewById(R.id.text_view_pay);
        tvCheckout = findViewById(R.id.tv_checkout);
        progressBarPay = findViewById(R.id.progress_bar_pay);
        llNewCard = findViewById(R.id.ll_new_card);
        rvSavedCards = findViewById(R.id.rv_saved_cards);
        textViewAddCard = findViewById(R.id.text_view_add_card);
        llSavedCards = findViewById(R.id.ll_saved_cards);
        textViewConfirmPurchase = findViewById(R.id.text_view_confirm_purchase);
        llSavedCardPayOption = findViewById(R.id.ll_saved_card_pay_option);
        llBillingAddress = findViewById(R.id.ll_billing_address);
        mTvBillingFullName = findViewById(R.id.tv_billing_full_name);
        mTvBillingAddress = findViewById(R.id.tv_billing_address);
        mTvBillingMobile = findViewById(R.id.tv_billing_mobile);
        mIvBillingEdit = findViewById(R.id.iv_billing_edit);
        mCheckBoxSameAsShipping = findViewById(R.id.check_box_same_as_shipping);
        mLlSameAsShipping = findViewById(R.id.ll_same_as_shipping);
        mLlGooglePay = findViewById(R.id.ll_google_pay);

        messagesDetailViewModel = ViewModelProviders.of(this).get(MessagesDetailViewModel.class);
        messagesDetailViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        profileEditViewModel = ViewModelProviders.of(this).get(ProfileEditViewModel.class);
        profileEditViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());

        blueSnapService = BlueSnapService.getInstance();
        sdkRequest = blueSnapService.getSdkRequest();

        if (!TextUtils.isEmpty(PreferenceManager.getInstance(mActivity).getString(SHOPPER_ID)))
            getSavedCardsListing();
        else {
            llNewCard.setVisibility(View.VISIBLE);
            llSavedCards.setVisibility(View.GONE);
            llSavedCardPayOption.setVisibility(View.GONE);
            generateMerchantToken();
        }

        if (mCartList != null && mCartList.size() > 0) {
            for (int i = 0; i < mCartList.size(); i++) {
                if (vendorsCommissionsList != null && vendorsCommissionsList.size() > 0) {
                    boolean isSellerVendorAdd = false;
                    float comissin = 0f;
                    int commissionPosition = 0;
                    for (int c = 0; c < vendorsCommissionsList.size(); c++) {
                        if (mCartList.get(i).getSellerVendorId().equalsIgnoreCase(vendorsCommissionsList.get(c).getVendorId())) {
                            isSellerVendorAdd = true;
                            commissionPosition = c;
                            comissin = Float.parseFloat(vendorsCommissionsList.get(c).getCommissionAmount()) + Float.parseFloat(mCartList.get(i).getSellerCommissionAmount());
                            break;
                        } else {
                            isSellerVendorAdd = false;
                        }
                    }
                    if (isSellerVendorAdd) {
                        vendorsCommissionsList.get(commissionPosition).setCommissionAmount(Float.toString(comissin));
                    } else {
                        VendorCommissionModel vendor = new VendorCommissionModel();
                        vendor.setVendorId(mCartList.get(i).getSellerVendorId());
                        vendor.setCommissionAmount(mCartList.get(i).getSellerCommissionAmount());
                        vendorsCommissionsList.add(vendor);
                    }
                    boolean isSellerVendorAdd1 = false;
                    float comissin1 = 0f;
                    int commissionPosition1 = 0;
                    for (int c = 0; c < vendorsCommissionsList.size(); c++) {
                        if (mCartList.get(i).getOwnerVendorId() != null && !TextUtils.isEmpty(mCartList.get(i).getOwnerVendorId())
                                && mCartList.get(i).getOwnerCommissionAmount() != null && !TextUtils.isEmpty(mCartList.get(i).getOwnerCommissionAmount())) {
                            if (mCartList.get(i).getOwnerVendorId().equalsIgnoreCase(vendorsCommissionsList.get(c).getVendorId())) {
                                isSellerVendorAdd1 = true;
                                commissionPosition1 = c;
                                comissin1 = Float.parseFloat(vendorsCommissionsList.get(c).getCommissionAmount()) + Float.parseFloat(mCartList.get(i).getOwnerCommissionAmount());
                                break;
                            } else {
                                isSellerVendorAdd1 = false;
                            }
                        }
                    }
                    if (isSellerVendorAdd1) {
                        vendorsCommissionsList.get(commissionPosition1).setCommissionAmount(Float.toString(comissin1));
                    } else {
                        VendorCommissionModel vendor = new VendorCommissionModel();
                        vendor.setVendorId(mCartList.get(i).getOwnerVendorId());
                        vendor.setCommissionAmount(mCartList.get(i).getOwnerCommissionAmount());
                        vendorsCommissionsList.add(vendor);
                    }

                } else {
                    VendorCommissionModel vendor = new VendorCommissionModel();
                    vendor.setVendorId(mCartList.get(i).getSellerVendorId());
                    vendor.setCommissionAmount(mCartList.get(i).getSellerCommissionAmount());
                    vendorsCommissionsList.add(vendor);

                    if (vendorsCommissionsList != null && vendorsCommissionsList.size() > 0) {
                        if (mCartList.get(i).getOwnerVendorId() != null && !TextUtils.isEmpty(mCartList.get(i).getOwnerVendorId())
                                && mCartList.get(i).getOwnerCommissionAmount() != null && !TextUtils.isEmpty(mCartList.get(i).getOwnerCommissionAmount())) {
                            for (int c = 0; c < vendorsCommissionsList.size(); c++) {
                                if (mCartList.get(i).getOwnerVendorId().equalsIgnoreCase(vendorsCommissionsList.get(c).getVendorId())) {
                                    float comissin = Float.parseFloat(vendorsCommissionsList.get(c).getCommissionAmount()) + Float.parseFloat(mCartList.get(i).getOwnerCommissionAmount());
                                    vendorsCommissionsList.get(c).setCommissionAmount(Float.toString(comissin));
                                } else {
                                    VendorCommissionModel vendor1 = new VendorCommissionModel();
                                    vendor1.setVendorId(mCartList.get(i).getOwnerVendorId());
                                    vendor1.setCommissionAmount(mCartList.get(i).getOwnerCommissionAmount());
                                    vendorsCommissionsList.add(vendor1);
                                }
                                break;
                            }
                        }
                    } else {
                        if (mCartList.get(i).getOwnerVendorId() != null && !TextUtils.isEmpty(mCartList.get(i).getOwnerVendorId())
                                && mCartList.get(i).getOwnerCommissionAmount() != null && !TextUtils.isEmpty(mCartList.get(i).getOwnerCommissionAmount())) {
                            VendorCommissionModel vendor1 = new VendorCommissionModel();
                            vendor1.setVendorId(mCartList.get(i).getOwnerVendorId());
                            vendor1.setCommissionAmount(mCartList.get(i).getOwnerCommissionAmount());
                            vendorsCommissionsList.add(vendor1);
                        }
                    }

                }
            }
        }

        Log.e("vendorsCommissionsList", "size is: " + vendorsCommissionsList.size());

        if (vendorsCommissionsList != null && vendorsCommissionsList.size() > 0) {
            vendorIdString = vendorIdString + "<vendors-info>";
            for (int i = 0; i < vendorsCommissionsList.size(); i++) {
                if (vendorsCommissionsList.get(i).getVendorId() != null && !TextUtils.isEmpty(vendorsCommissionsList.get(i).getVendorId())
                        && vendorsCommissionsList.get(i).getCommissionAmount() != null && !TextUtils.isEmpty(vendorsCommissionsList.get(i).getCommissionAmount())) {
                    vendorIdString = vendorIdString + "<vendor-info>"
                            + "<vendor-id>" + vendorsCommissionsList.get(i).getVendorId() + "</vendor-id>"
                            + "<commission-amount>" + vendorsCommissionsList.get(i).getCommissionAmount() + "</commission-amount>"
                            + "</vendor-info>";
                }
            }
            vendorIdString = vendorIdString + "</vendors-info>";
            Log.e("Vendor_xml", "xml is:\n " + vendorIdString);
        }

        textViewPay.setOnClickListener(this);
        textViewAddCard.setOnClickListener(this);
        textViewConfirmPurchase.setOnClickListener(this);
        mIvBillingEdit.setOnClickListener(this);

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCheckBoxSameAsShipping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    isTransaction = false;
                    openAddUpdateBillingAddress();
                }
            }
        });

//        etCardNumber.addTextChangedListener(new TextWatcher() {
//            private static final char space = ' ';
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // Remove spacing char
//                if (s.length() > 0 && (s.length() % 5) == 0) {
//                    final char c = s.charAt(s.length() - 1);
//                    if (space == c) {
//                        s.delete(s.length() - 1, s.length());
//                    }
//                }
//                // Insert char where needed.
//                if (s.length() > 0 && (s.length() % 5) == 0) {
//                    char c = s.charAt(s.length() - 1);
//                    // Only if its a digit where there should be a space we insert a space
//                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
//                        s.insert(s.length() - 1, String.valueOf(space));
//                    }
//                }
//            }
//        });

        profileEditViewModel.getAddBillingAddressLivedata().observe(this, new Observer<ShippingAddressesResponse>() {
            @Override
            public void onChanged(@Nullable ShippingAddressesResponse commonResponse) {
//                showToastShort(commonResponse.getMessage());

                if (commonResponse.getStatusCode() == 200) {
                    billingAddress = commonResponse.getData().get(0);

                    getLoadingStateObserver().onChanged(true);

                    if (isNewCard) {
                        isTransaction = true;
                        siftCreateOrder(etCardHolderName.getText().toString().trim().split(" ")[0],
                                etCardHolderName.getText().toString().trim().split(" ")[1],
                                etZipCode.getText().toString(),
//                            AppUtils.getCityName(mActivity, etZipCode.getText().toString()),
                                etCardNumber.getText().toString().trim().substring(etCardNumber.getText().toString().trim().length() - 4));
                    } else if (isGooglePay) {
//                        isTransaction = true;
                        siftCreateOrderGooglePay();
                    } else {
                        isTransaction = true;
                        siftCreateOrder(blueSnapResponse.getFirstName(),
                                blueSnapResponse.getLastName(),
                                blueSnapResponse.getZip(),
//                        AppUtils.getCityName(mActivity, blueSnapResponse.getZip()),
                                cardInfoArrayList.get(selectedCardPosition).getCreditCard().getCardLastFourDigits());
                    }

                } else {
                    getLoadingStateObserver().onChanged(false);
                    showToastShort(commonResponse.getMessage());
                }
            }
        });

        etExpiry.addTextChangedListener(new TextWatcher() {
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
                    if (!editable.toString().contains("/")) {
                        if (Character.isDigit(c) && TextUtils.split(editable.toString(), String.valueOf("/")).length <= 2) {
                            editable.insert(editable.length() - 1, String.valueOf("/"));
                        }
                    }
                }
            }
        });

        mLlGooglePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewPay.setEnabled(false);
                textViewConfirmPurchase.setEnabled(false);

                isNewCard = false;
                isTransaction = false;
                isGooglePay = true;
                if(mCheckBoxSameAsShipping.isChecked()) {
                    profileEditViewModel.addBillingAddress(mActivity,
                            parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.STEET1).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.STEET2).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.CITY).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.STATE).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.PHONE).toString(),
                            "",
                            parms.get(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE).toString());

                } else if (billingAddress.getStreet1() != null && !TextUtils.isEmpty(billingAddress.getStreet1())) {
                    getLoadingStateObserver().onChanged(true);
                    siftCreateOrderGooglePay();
                } else {
                    openAddUpdateBillingAddress();
                }

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_view_pay:
                if (etCardNumber.getText().toString().trim().replace(" ", "").length() < 16) {
                    showCardDetailsError("Please enter a valid card number.");
                } else if (etExpiry.getText().toString().replace("/", "").length() < 6) {
                    showCardDetailsError("Please enter a valid expiry date with year in 4 digits format.");
                } else if (etCvv.getText().toString().trim().length() < 3) {
                    showCardDetailsError("Please enter a valid CVV code.");
                } else if (etCardHolderName.getText().toString().trim().split(" ").length < 2) {
                    showCardDetailsError("Please enter your full name.");
                } else if (etZipCode.getText().toString().trim().length() < 5) {
                    showCardDetailsError("Please enter a valid zip code of 5 digits.");
                } else {
                    textViewPay.setEnabled(false);
                    textViewConfirmPurchase.setEnabled(false);

                    isNewCard = true;
                    if(mCheckBoxSameAsShipping.isChecked()) {
                        profileEditViewModel.addBillingAddress(mActivity,
                                parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString(),
                                parms.get(AppConstants.KEY_CONSTENT.STEET1).toString(),
                                parms.get(AppConstants.KEY_CONSTENT.STEET2).toString(),
                                parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString(),
                                parms.get(AppConstants.KEY_CONSTENT.CITY).toString(),
                                parms.get(AppConstants.KEY_CONSTENT.STATE).toString(),
                                parms.get(AppConstants.KEY_CONSTENT.PHONE).toString(),
                                "",
                                parms.get(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE).toString());

                    } else if (billingAddress.getStreet1() != null && !TextUtils.isEmpty(billingAddress.getStreet1())) {
                        isTransaction = true;
                        getLoadingStateObserver().onChanged(true);
                        siftCreateOrder(etCardHolderName.getText().toString().trim().split(" ")[0],
                                etCardHolderName.getText().toString().trim().split(" ")[1],
                                etZipCode.getText().toString(),
//                            AppUtils.getCityName(mActivity, etZipCode.getText().toString()),
                                etCardNumber.getText().toString().trim().substring(etCardNumber.getText().toString().trim().length() - 4));
                    } else {
                        isTransaction = true;
                        openAddUpdateBillingAddress();
                    }

                }
                break;
            case R.id.text_view_add_card:
                llNewCard.setVisibility(View.VISIBLE);
                llSavedCards.setVisibility(View.GONE);
                llSavedCardPayOption.setVisibility(View.GONE);
                break;
            case R.id.text_view_confirm_purchase:
                textViewPay.setEnabled(false);
                textViewConfirmPurchase.setEnabled(false);

                isNewCard = false;
                if(mCheckBoxSameAsShipping.isChecked()) {
                    profileEditViewModel.addBillingAddress(mActivity,
                            parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.STEET1).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.STEET2).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.CITY).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.STATE).toString(),
                            parms.get(AppConstants.KEY_CONSTENT.PHONE).toString(),
                            "",
                            parms.get(AppConstants.KEY_CONSTENT.RESIDENCE_TYPE).toString());

                } else if (billingAddress.getStreet1() != null && !TextUtils.isEmpty(billingAddress.getStreet1())) {
                    isTransaction = true;
                    getLoadingStateObserver().onChanged(true);
                    siftCreateOrder(blueSnapResponse.getFirstName(),
                            blueSnapResponse.getLastName(),
                            blueSnapResponse.getZip(),
//                        AppUtils.getCityName(mActivity, blueSnapResponse.getZip()),
                            cardInfoArrayList.get(selectedCardPosition).getCreditCard().getCardLastFourDigits());
                } else {
                    isTransaction = true;
                    openAddUpdateBillingAddress();
                }

                break;
            case R.id.iv_billing_edit:
                isTransaction = false;
                openAddUpdateBillingAddress();
                break;
        }
    }

    public void onPaySubmit(double totalAmount) {

//        getLoadingStateObserver().onChanged(true);
        Log.e("totalAmount", "amount is " + totalAmount);
//        String productPriceStr = AndroidUtil.stringify(totalAmount);
//        if (TextUtils.isEmpty(productPriceStr)) {
//            Toast.makeText(getActivity().getApplicationContext(), "null payment", Toast.LENGTH_LONG).show();
//            return;
//        }

//        Double productPrice = Double.valueOf(productPriceStr);
//        if (productPrice <= 0D) {
//            Toast.makeText(getActivity().getApplicationContext(), "0 payment", Toast.LENGTH_LONG).show();
//            return;
//        }

//        readCurencyFromSpinner(ratesSpinner.getSelectedItem().toString());
        Double taxAmount = 0D;
        // You can set the Amouut solely
        SdkRequest sdkRequest = new SdkRequest(totalAmount, "USD", false, false, false);

//        // Or you can set the Amount with tax, this will override setAmount()
//        // The total purchase amount will be the sum of both numbers
//        if (taxAmountPrecentage > 0D) {
//            sdkRequest.setAmountWithTax(productPrice, productPrice * (taxAmountPrecentage / 100));
//        } else {
//            sdkRequest.setAmountNoTax(productPrice);
//        }

//        Switch googlePayTestModeSwitch = findViewById(R.id.googlePayTestModeSwitch);
//        sdkRequest.setGooglePayTestMode(googlePayTestModeSwitch.isChecked());

//        sdkRequest.setAllowCurrencyChange(allowCurrencyChangeSwitch.isChecked());
//        sdkRequest.setHideStoreCardSwitch(hideStoreCardSwitch.isChecked());
//        sdkRequest.setGooglePayActive(!disableGooglePaySwitch.isChecked());
//        sdkRequest.setActivate3DS(activate3DSSwitch.isChecked());

        sdkRequest.setGooglePayTestMode(false);

        sdkRequest.setAllowCurrencyChange(false);
        sdkRequest.setHideStoreCardSwitch(false);
        sdkRequest.setGooglePayActive(false);
        sdkRequest.setActivate3DS(false);

        try {
            sdkRequest.verify();
        } catch (BSPaymentRequestException e) {

//            new AlertDialog.Builder(BlueSnapPaymentCardActivity.this)
//                    .setTitle("SdkRequest error")
//                    .setMessage("" + e.getMessage())
//                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    })
//                    .create()
//                    .show();

            DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.card_error_info), new OnDialogItemClickListener() {
                @Override
                public void onPositiveBtnClick() {

                }

                @Override
                public void onNegativeBtnClick() {

                }
            });
            textViewPay.setEnabled(true);
            textViewConfirmPurchase.setEnabled(true);

            Log.d("TAG", sdkRequest.toString());
            finish();
        }

        // Set special tax policy: non-US pay no tax; MA pays 10%, other US states pay 5%
        sdkRequest.setTaxCalculator(new TaxCalculator() {
            @Override
            public void updateTax(String shippingCountry, String shippingState, PriceDetails priceDetails) {
                if ("us".equalsIgnoreCase(shippingCountry)) {
                    Double taxRate = 0.05;
                    if ("ma".equalsIgnoreCase(shippingState)) {
                        taxRate = 0.1;
                    }
                    priceDetails.setTaxAmount(priceDetails.getSubtotalAmount() * taxRate);
                } else {
                    priceDetails.setTaxAmount(0D);
                }
            }
        });

        try {
            blueSnapService.setSdkRequest(sdkRequest);
//            Intent intent = new Intent(getActivity().getApplicationContext(), BluesnapCheckoutActivity.class);
//            startActivityForResult(intent, BluesnapCheckoutActivity.REQUEST_CODE_DEFAULT);

            Integer cardExpMonth = Integer.parseInt(etExpiry.getText().toString().trim().split("/")[0]);
            Integer cardExpYear = Integer.parseInt(etExpiry.getText().toString().trim().split("/")[1]);

            CreditCard creditCard = new CreditCard();
            creditCard.setNumber("" + etCardNumber.getText().toString().trim());
            creditCard.setExpirationMonth(cardExpMonth);
            creditCard.setExpirationYear(cardExpYear);
            creditCard.setCvc("" + etCvv.getText().toString().trim());
            creditCard.setCardType("");
            creditCard.setCardSubType("");
//            creditCard.toJson();

            CreditCardInfo creditCardInfo = new CreditCardInfo();
            creditCardInfo.setCreditCard(creditCard);

            BillingContactInfo billingContactInfo = new BillingContactInfo();
            billingContactInfo.setEmail("");
            billingContactInfo.setAddress("");
            billingContactInfo.setZip("" + etZipCode.getText().toString());
            billingContactInfo.setCity("");
            billingContactInfo.setCountry("");
            billingContactInfo.setFullName("" + etCardHolderName.getText().toString().trim());
            billingContactInfo.setFirstName("" + etCardHolderName.getText().toString().trim().split(" ")[0]);
            if (etCardHolderName.getText().toString().trim().split(" ").length > 1)
                billingContactInfo.setLastName("" + etCardHolderName.getText().toString().trim().split(" ")[1]);

            Shopper shopper = new Shopper();
            shopper.setNewCreditCardInfo(creditCardInfo);
            shopper.getNewCreditCardInfo().setBillingContactInfo(billingContactInfo);
            shopper.setStoreCard(checkBoxSaveCard.isChecked());

            tokenizeCardOnServer(shopper, new Intent());

        } catch (BSPaymentRequestException e) {
            Log.e("TAG", "payment request not validated: ", e);
            finish();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showCardDetailsError(String message) {

        DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), "" + message, new OnDialogItemClickListener() {
            @Override
            public void onPositiveBtnClick() {

            }

            @Override
            public void onNegativeBtnClick() {

            }
        });

    }

    private void generateMerchantToken() {

        // create the interface for activating the token creation from server
        tokenProvider = new TokenProvider() {
            @Override
            public void getNewToken(final TokenServiceCallback tokenServiceCallback) {

                getLoadingStateObserver().onChanged(true);
                merchantTokenService(new TokenServiceInterface() {
                    @Override
                    public void onServiceSuccess() {
                        //change the expired token
                        tokenServiceCallback.complete(merchantToken);
                    }

                    @Override
                    public void onServiceFailure() {

                    }
                });
            }
        };


        getLoadingStateObserver().onChanged(true);
        merchantTokenService(new TokenServiceInterface() {
            @Override
            public void onServiceSuccess() {
                initControlsAfterToken();
            }

            @Override
            public void onServiceFailure() {

            }
        });
    }

    private void merchantTokenService(final TokenServiceInterface tokenServiceInterface) {

        String returningOrNewShopper = "";
//        String returningShopperId = returningShopperEditText.getText().toString();
//        String returningShopperId = "";
//        if (returningShopperSwitch.isChecked() && returningShopperId.length() >= 4) {
//            returningOrNewShopper = "?shopperId=" + returningShopperId;
//        }

        getLoadingStateObserver().onChanged(true);
        if (!TextUtils.isEmpty(PreferenceManager.getInstance(mActivity).getString(SHOPPER_ID))) {
            returningOrNewShopper = "?shopperId=" + PreferenceManager.getInstance(mActivity).getString(SHOPPER_ID);
        }

        final String finalReturningOrNewShopper = returningOrNewShopper;
        final Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
                    ArrayList<CustomHTTPParams> headerParams = new ArrayList<>();
                    headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
                    BlueSnapHTTPResponse post = HTTPOperationController.post(SANDBOX_URL + SANDBOX_TOKEN_CREATION + finalReturningOrNewShopper, null, "application/json", "application/json", headerParams);
                    Log.e("API", SANDBOX_URL + SANDBOX_TOKEN_CREATION + finalReturningOrNewShopper);
                    Log.e("API_HEADER", "Authorization : " + basicAuth);
                    String responseString = post.getResponseString();
                    Log.e("API_RESPONSE", "" + responseString);
                    if (post.getResponseCode() == HTTP_CREATED && post.getHeaders() != null) {
                        String location = post.getHeaders().get("Location").get(0);
                        merchantToken = location.substring(location.lastIndexOf('/') + 1);
                        tokenServiceInterface.onServiceSuccess();
//                        getLoadingStateObserver().onChanged(false);
                        Log.e("PF_TOKEN", "token is : " + BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken());
                        getBillingAddress();

                    } else {

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("*****Done*****");
                                getLoadingStateObserver().onChanged(false);
                                getBillingAddress();
                                DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.card_error_info), new OnDialogItemClickListener() {
                                    @Override
                                    public void onPositiveBtnClick() {

                                    }

                                    @Override
                                    public void onNegativeBtnClick() {

                                    }
                                });
                                textViewPay.setEnabled(true);
                                textViewConfirmPurchase.setEnabled(true);
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
                            getBillingAddress();
                            DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.card_error_info), new OnDialogItemClickListener() {
                                @Override
                                public void onPositiveBtnClick() {

                                }

                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                            textViewPay.setEnabled(true);
                            textViewConfirmPurchase.setEnabled(true);
                        }
                    }, 10);
                }

            }
        };

        TagHawkApplication.mainHandler.post(myRunnable);

    }

    private void initControlsAfterToken() {
        final String merchantStoreCurrency = /*null != merchantStoreCurrencySpinner && null != merchantStoreCurrencySpinner.getSelectedItem() ? merchantStoreCurrencySpinner.getSelectedItem().toString() :*/ "USD";
        //final String merchantStoreCurrency = (null == currency || null == currency.getCurrencyCode()) ? "USD" : currency.getCurrencyCode();
        blueSnapService.setup(merchantToken, tokenProvider, merchantStoreCurrency, getApplicationContext(), new BluesnapServiceCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        if (null == currency || null == currency.getCurrencyCode()) {
//                            Set<String> supportedRates = bluesnapService.getSupportedRates();
//                            if (supportedRates != null) {
//                                updateSpinnerAdapterFromRates(demoSupportedRates(supportedRates));
//                            }
//                        }
//                        progressBar.setVisibility(View.INVISIBLE);
//                        linearLayoutForProgressBar.setVisibility(View.VISIBLE);
//                        productPriceEditText.setVisibility(View.VISIBLE);
//                        productPriceEditText.requestFocus();
                        updateReturningShopperDetails();

                    }
                });


            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        new AlertDialog.Builder(BlueSnapPaymentCardActivity.this)
//                                .setMessage("Failed to setup sdk")
//                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                    }
//                                })
//                                .create()
//                                .show();

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
                                textViewPay.setEnabled(true);
                                textViewConfirmPurchase.setEnabled(true);
                            }
                        }, 10);


                    }
                });
            }
        });
    }

    /**
     * This method gets called after we create a token, and we have a returning shopper, we show how
     * to call bluesnapService.getShopperConfiguration() and get the shopper details.
     * In this case, we display the name and chosen payment method on the screen.
     */
    private void updateReturningShopperDetails() {
        if (false) {
            ShopperConfiguration shopperInfo = blueSnapService.getShopperConfiguration();
            String shopperInfoText = "";
            if (shopperInfo != null) {
                BillingContactInfo billingInfo = shopperInfo.getBillingContactInfo();
                shopperInfoText = billingInfo.getFullName();
                ChosenPaymentMethod chosenPaymentMethod = shopperInfo.getChosenPaymentMethod();
                if (chosenPaymentMethod != null) {
                    shopperInfoText += "; Payment method: " + chosenPaymentMethod.getChosenPaymentMethodType();
                    CreditCard creditCard = chosenPaymentMethod.getCreditCard();
                    if (creditCard != null) {
                        shopperInfoText += " " + creditCard.getCardLastFourDigits();
                    }
                }
            }

            Log.e("shopper_id", "" + shopperInfoText);
//            shopperDetailsTextView.setText(shopperInfoText);
//            shopperDetailsTextView.setVisibility(View.VISIBLE);
//            shopperConfigSwitch.setVisibility(View.VISIBLE);
        } else {
//            shopperDetailsTextView.setVisibility(View.INVISIBLE);
//            shopperConfigSwitch.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * tokenize Card On Server,
     * receive shopper and activate api tokenization to the server according to SDK Request {@link com.bluesnap.androidapi.models.SdkRequest} spec
     *
     * @param shopper      - {@link Shopper}
     * @param resultIntent - {@link Intent}
     * @throws UnsupportedEncodingException - UnsupportedEncodingException
     * @throws JSONException                - JSONException
     */
    private void tokenizeCardOnServer(final Shopper shopper, final Intent resultIntent) throws UnsupportedEncodingException, JSONException {
        final PurchaseDetails purchaseDetails = new PurchaseDetails(
                shopper.getNewCreditCardInfo().getCreditCard(),
                shopper.getNewCreditCardInfo().getBillingContactInfo(),
                shopper.getShippingContactInfo(),
                shopper.isStoreCard());

        sdkRequest = blueSnapService.getSdkRequest();
        blueSnapService.getAppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BlueSnapHTTPResponse response = blueSnapService.submitTokenizedDetails(purchaseDetails);
                    if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        if (sdkRequest.isActivate3DS()) {
                            cardinal3DS(purchaseDetails, shopper, resultIntent, response);
                        } else {
                            finishFromActivity(shopper, resultIntent, response);
                        }

                    } else if (response.getResponseCode() == 400 && null != blueSnapService.getTokenProvider() && !"".equals(response.getResponseString())) {
                        try {
                            JSONObject errorResponse = new JSONObject(response.getResponseString());
                            JSONArray rs2 = (JSONArray) errorResponse.get("message");
                            JSONObject rs3 = (JSONObject) rs2.get(0);
                            if ("EXPIRED_TOKEN".equals(rs3.get("errorName"))) {
                                blueSnapService.getTokenProvider().getNewToken(new TokenServiceCallback() {
                                    @Override
                                    public void complete(String newToken) {
                                        blueSnapService.setNewToken(newToken);
                                        try {
                                            tokenizeCardOnServer(shopper, resultIntent);
                                        } catch (UnsupportedEncodingException e) {
                                            Log.e(TAG, "Unsupported Encoding Exception", e);
                                        } catch (JSONException e) {
                                            Log.e(TAG, "json parsing exception", e);
                                        }
                                    }
                                });
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
                                        textViewPay.setEnabled(true);
                                        textViewConfirmPurchase.setEnabled(true);
                                    }
                                }, 10);
//                                finishFromActivityWithFailure(response.toString());
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "json parsing exception", e);
                        }
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
                                textViewPay.setEnabled(true);
                                textViewConfirmPurchase.setEnabled(true);
                            }
                        }, 10);
//                        finishFromActivityWithFailure(response.toString());
                    }

                } catch (JSONException ex) {
                    Log.e(TAG, "JsonException");

                }
            }
        });

    }

    private void cardinal3DS(PurchaseDetails purchaseDetails, Shopper shopper, final Intent resultIntent, BlueSnapHTTPResponse response) {

        // Request auth with 3DS
        CardinalManager cardinalManager = CardinalManager.getInstance();

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Got broadcastReceiver intent");

                if (cardinalManager.getThreeDSAuthResult().equals(CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_CANCELED.name())) {
                    Log.d(TAG, "Cardinal challenge canceled");
                    getLoadingStateObserver().onChanged(false);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BluesnapAlertDialog.setDialog(mActivity, "3DS Authentication is required", "");
                        }
                    });

                } else if (cardinalManager.getThreeDSAuthResult().equals(CardinalManager.ThreeDSManagerResponse.AUTHENTICATION_FAILED.name())
                        || cardinalManager.getThreeDSAuthResult().equals(CardinalManager.ThreeDSManagerResponse.THREE_DS_ERROR.name())) { //cardinal internal error or authentication failure

                    // TODO: Change this after receiving "proceed with/without 3DS" from server in init API call
                    String error = intent.getStringExtra(CardinalManager.THREE_DS_AUTH_DONE_EVENT_NAME);
//                    finishFromActivityWithFailure(error);
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
                            textViewPay.setEnabled(true);
                            textViewConfirmPurchase.setEnabled(true);
                        }
                    }, 10);

                } else { //cardinal success (success/bypass/unavailable/unsupported)
                    Log.d(TAG, "3DS Flow ended properly");
                    finishFromActivity(shopper, resultIntent, response);
                }
            }
        };

        BlueSnapLocalBroadcastManager.registerReceiver(mActivity, CardinalManager.THREE_DS_AUTH_DONE_EVENT, broadcastReceiver);

        try {

            cardinalManager.authWith3DS(blueSnapService.getSdkResult().getCurrencyNameCode(), blueSnapService.getSdkResult().getAmount(), mActivity, purchaseDetails.getCreditCard());

        } catch (JSONException e) {
            Log.d(TAG, "Error in parsing authWith3DS API response");
//            finishFromActivityWithFailure(null);
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
                    textViewPay.setEnabled(true);
                    textViewConfirmPurchase.setEnabled(true);
                }
            }, 10);
        }


    }

    /**
     * 3DS flow
     */
    private void finishFromActivity(Shopper shopper, final Intent resultIntent, BlueSnapHTTPResponse response) {
        try {
            String Last4;
            String ccType;
            SdkResult sdkResult = BlueSnapService.getInstance().getSdkResult();

            if (shopper.getNewCreditCardInfo().getCreditCard().getIsNewCreditCard()) {
                // New Card
                JSONObject jsonObject = new JSONObject(response.getResponseString());
                Last4 = jsonObject.getString("last4Digits");
                ccType = jsonObject.getString("ccType");
                Log.d(TAG, "tokenization of new credit card");
            } else {
                // Reused Card
                Last4 = shopper.getNewCreditCardInfo().getCreditCard().getCardLastFourDigits();
                ccType = shopper.getNewCreditCardInfo().getCreditCard().getCardType();
                Log.d(TAG, "tokenization of previous used credit card");
            }

            sdkResult.setBillingContactInfo(shopper.getNewCreditCardInfo().getBillingContactInfo());
            if (sdkRequest.getShopperCheckoutRequirements().isShippingRequired())
                sdkResult.setShippingContactInfo(shopper.getShippingContactInfo());
            sdkResult.setKountSessionId(KountService.getInstance().getKountSessionId());
            sdkResult.setToken(BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken());
            // update last4 from server result
            sdkResult.setLast4Digits(Last4);
            // update card type from server result
            sdkResult.setCardType(ccType);
            sdkResult.setChosenPaymentMethodType(SupportedPaymentMethods.CC);
            sdkResult.setThreeDSAuthenticationResult(CardinalManager.getInstance().getThreeDSAuthResult());

            TagHawkApplication.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    checkMerchantTX(sdkResult);
                }
            });

            //Only set the remember shopper here since failure can lead to missing tokenization on the server
            shopper.getNewCreditCardInfo().getCreditCard().setTokenizationSuccess();
            Log.d(TAG, "tokenization finished");
//            finish();
        } catch (NullPointerException | JSONException e) {
//            finishFromActivityWithFailure(null);
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
                    textViewPay.setEnabled(true);
                    textViewConfirmPurchase.setEnabled(true);
                }
            }, 10);
        }

    }

    private void finishFromActivityWithFailure(String response) {
        String errorMsg;

        if (response != null) {
            errorMsg = "Service Error: " + response;
        } else {
            errorMsg = "SDK Error";
        }

        getLoadingStateObserver().onChanged(false);

//        Log.e(TAG, errorMsg);
//        setResult(BluesnapCheckoutActivity.RESULT_SDK_FAILED, new Intent().putExtra(BluesnapCheckoutActivity.SDK_ERROR_MSG, errorMsg));
//        finish();
    }

    private void checkMerchantTX(SdkResult sdkResult) {
        createCreditCardTransaction(sdkResult);
    }

    public void createCreditCardTransaction(final SdkResult sdkResult) {

        //TODO: I'm just a string but please don't make me look that bad..Use String.format
        String body = "<card-transaction xmlns=\"http://ws.plimus.com\">" +
                "<card-transaction-type>AUTH_CAPTURE</card-transaction-type>" +
                "<recurring-transaction>ECOMMERCE</recurring-transaction>" +
                "<soft-descriptor>MobileSDKtest</soft-descriptor>" +
                "<amount>" + sdkResult.getAmount() + "</amount>" +
                "<currency>" + sdkResult.getCurrencyNameCode() + "</currency>" +
//                "<transaction-fraud-info>" +
//                "<fraud-session-id>" + sdkResult.getKountSessionId() + "</fraud-session-id>" +
//                "</transaction-fraud-info>" +
                "<pf-token>" + sdkResult.getToken() + "</pf-token>" +
                vendorIdString +
                "</card-transaction>";

//        String body = " [\"amount\": \"" + sdkResult.getAmount() + "\", \n" +
//                "\"recurringTransaction\": \"ECOMMERCE\", \n" +
//                "\"softDescriptor\": \"MobileSDKtest\", \n" +
//                "\"cardTransactionType\": \"AUTH_CAPTURE\", \n" +
//                "\"storeCard\": \"" + checkBoxSaveCard.isChecked() + "\", \n" +
//                "\"currency\": \"" + "USD" + "\", \n" +
//                "\"pfToken\": \"" + sdkResult.getToken() + "\"]";


        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        List<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
        BlueSnapHTTPResponse httpResponse = HTTPOperationController.post(SANDBOX_URL + SANDBOX_CREATE_TRANSACTION, body, "application/xml", "application/xml", headerParams);
        Log.e("API", SANDBOX_URL + SANDBOX_CREATE_TRANSACTION);
        Log.e("API_HEADER", "Authorization : " + basicAuth);
        Log.e("API_PARAMS", "" + body);
        String responseString = httpResponse.getResponseString();
        Log.e("API_RESPONSE", "" + responseString);
        if (httpResponse.getResponseCode() == HTTP_OK && httpResponse.getHeaders() != null) {
            setShopperId(responseString.substring(responseString.indexOf("<vaulted-shopper-id>") +
                    "<vaulted-shopper-id>".length(), responseString.indexOf("</vaulted-shopper-id>")));
            setTransactionId(responseString.substring(responseString.indexOf("<transaction-id>") +
                    "<transaction-id>".length(), responseString.indexOf("</transaction-id>")));
            setCardLastFourDigits(responseString.substring(responseString.indexOf("<card-last-four-digits>") +
                    "<card-last-four-digits>".length(), responseString.indexOf("</card-last-four-digits>")));

            String merchantToken = BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken();
            setTokenSuffix(merchantToken.substring(merchantToken.length() - 6));
            Log.d(TAG, responseString);
//            setMessage("Transaction Success " + getTransactionId() + "\nShopper Id " + getShopperId());
//            setTitle("Merchant Server");
//            callback.onSuccess();

//            getLoadingStateObserver().onChanged(false);

            PaymentStatusRequest paymentStatusRequest = new PaymentStatusRequest();

            paymentStatusRequest.setAmount(sdkResult.getAmount());
            paymentStatusRequest.setCurrency(sdkResult.getCurrencyNameCode());
            if (!TextUtils.isEmpty(PreferenceManager.getInstance(mActivity).getString(SHOPPER_ID))) {
                paymentStatusRequest.setVaultedShopperId(responseString.substring(responseString.indexOf("<vaulted-shopper-id>") + "<vaulted-shopper-id>".length(), responseString.indexOf("</vaulted-shopper-id>")));
            }

            PaymentStatusRequest.Payment payment = new PaymentStatusRequest.Payment();
            payment.setId(getTransactionId());
            payment.setPaid(true);

            PaymentStatusRequest.Address address = new PaymentStatusRequest.Address();
            address.setPostal_code(responseString.substring(responseString.indexOf("<zip>") + "<zip>".length(), responseString.indexOf("</zip>")));
//            paymentStatusRequest.getPayment().getBilling_details().getAddress().setPostal_code(responseString.substring(responseString.indexOf("<zip>") + "<zip>".length(), responseString.indexOf("</zip>")));
            PaymentStatusRequest.Billing_details billing_details = new PaymentStatusRequest.Billing_details();
            billing_details.setAddress(address);
            payment.setBilling_details(billing_details);

            PaymentStatusRequest.Payment_method_details payment_method_details = new PaymentStatusRequest.Payment_method_details();
            payment_method_details.setType("card");

            PaymentStatusRequest.Card card = new PaymentStatusRequest.Card();
            card.setExp_month(0);
            card.setExp_year(0);
            card.setFunding(responseString.substring(responseString.indexOf("<card-type>") + "<card-type>".length(), responseString.indexOf("</card-type>")));
            card.setNetwork(responseString.substring(responseString.indexOf("<card-type>") + "<card-type>".length(), responseString.indexOf("</card-type>")));
            card.setLast4(getCardLastFourDigits());
            card.setWallet(new PaymentStatusRequest.Wallet());

            PaymentStatusRequest.Checks checks = new PaymentStatusRequest.Checks();
            card.setChecks(checks);

            payment_method_details.setCard(card);

            payment.setPayment_method_details(payment_method_details);
            paymentStatusRequest.setPayment(payment);

            ArrayList<PaymentStatusRequest.Products> products = new ArrayList<>();
            for (int i = 0; i < mCartList.size(); i++) {
                PaymentStatusRequest.Products product = new PaymentStatusRequest.Products();
                product.setPrice(mCartList.get(i).getProductPrice());
                product.setProductId(mCartList.get(i).getProductId());
                product.setSellerId(mCartList.get(i).getSellerId());
                if (mCartList.get(i).getOwnerId() != null && !TextUtils.isEmpty(mCartList.get(i).getOwnerId()))
                    product.setOwnerId(mCartList.get(i).getOwnerId());
                products.add(product);
            }
            paymentStatusRequest.setProducts(products);

            if (screen.equalsIgnoreCase("shipping")) {

                PaymentStatusRequest.Ship_to ship_to = new PaymentStatusRequest.Ship_to();

                ship_to.setContact_name(parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString());
                if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getEmail() != null)
                    ship_to.setEmail(DataManager.getInstance().getUserDetails().getEmail());
                ship_to.setCity(parms.get(AppConstants.KEY_CONSTENT.CITY).toString());
                ship_to.setState(parms.get(AppConstants.KEY_CONSTENT.STATE).toString());
                ship_to.setPostal_code(parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
                ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
                ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
                ship_to.setPhone(parms.get(AppConstants.KEY_CONSTENT.PHONE).toString());
                ship_to.setType("FEDEX");

                paymentStatusRequest.setShip_to(ship_to);
            }

            PaymentStatusRequest.Transaction transaction = new PaymentStatusRequest.Transaction();
            transaction.setTransactionid("" + getTransactionId());
            transaction.setAmount(sdkResult.getAmount().toString());
            transaction.setCurrency(sdkResult.getCurrencyNameCode());
            transaction.setFirstname(responseString.substring(responseString.indexOf("<first-name>") + "<first-name>".length(), responseString.indexOf("</first-name>")));
            transaction.setLastname(responseString.substring(responseString.indexOf("<last-name>") + "<last-name>".length(), responseString.indexOf("</last-name>")));
            transaction.setZip(responseString.substring(responseString.indexOf("<zip>") + "<zip>".length(), responseString.indexOf("</zip>")));
            transaction.setCardlastfourdigits(getCardLastFourDigits());

            paymentStatusRequest.setTransaction(transaction);

//            getLoadingStateObserver().onChanged(true);
            doPayment(paymentStatusRequest);

//            Intent intent = new Intent();
//            intent.putExtra(AppConstants.BUNDLE_DATA, paymentStatusRequest);
//            setResult(RESULT_OK, intent);
//            finish();

        } else {
//            getLoadingStateObserver().onChanged(false);
            Log.e(TAG, responseString);
            //Disabled until server will return a reasonable error
            String errorName = "Transaction Failed";
            try {
                if (responseString != null)
                    errorName = responseString.substring(responseString.indexOf("<error-name>") + "<error-name>".length(), responseString.indexOf("</error-name>"));
                Log.e(TAG, "Failed TX Response:  " + responseString);
            } catch (Exception e) {
                Log.e(TAG, "failed to get error name from response string");
                Log.e(TAG, "Failed TX Response:  " + responseString);
            }
//            setMessage(errorName);
//            setTitle("Merchant Server");
//            callback.onFailure();
            getPaymentFailureApi(Double.toString(sdkResult.getAmount()), sdkResult.getCurrencyNameCode(), sdkResult.getBillingContactInfo().getFirstName(), sdkResult.getBillingContactInfo().getLastName(), sdkResult.getBillingContactInfo().getZip(), sdkResult.getLast4Digits(), errorName);

        }

    }

    //This Api is use for payment
    public void doPayment(PaymentStatusRequest paymentStatusRequest) {

//        getLoadingStateObserver().onChanged(true);
        Gson gson = new Gson();
        String json = gson.toJson(paymentStatusRequest);
        Log.e("REQUEST_JSON", "" + json);
        DataManager.getInstance().doPayment(paymentStatusRequest).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse successResponse) {
                if (successResponse.getCode() == 200) {
                    transactionStatus = true;
                    if (screen.equalsIgnoreCase("shipping")) {
                        CartDataBean mCartData = new CartDataBean();
                        mCartData = mCartList.get(0);
                        final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
                        final String roomId = FirebaseManager.getFirebaseRoomId(user.getUserId(), mCartData.getSellerId());
                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        CartDataBean finalMCartData = mCartData;
                        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(finalMCartData.getSellerId()).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshotOtherUser) {
                                        ChatProductModel chatProductModel = new ChatProductModel();
                                        chatProductModel.setProductId(finalMCartData.getProductId());
                                        chatProductModel.setProductName(finalMCartData.getProductName());
                                        chatProductModel.setProductPrice(Double.parseDouble(finalMCartData.getProductPrice()));
                                        if (finalMCartData.getProductPicList() != null && finalMCartData.getProductPicList().size() > 0)
                                            chatProductModel.setProductImage(finalMCartData.getProductPicList().get(0));
                                        else
                                            chatProductModel.setProductImage("");
                                        boolean isOtherUserCreated, isNewChat;
                                        final ChatModel chatModel;
                                        if (dataSnapshotOtherUser.exists())
                                            isOtherUserCreated = true;
                                        else
                                            isOtherUserCreated = false;
                                        if (dataSnapshot.exists()) {
                                            chatModel = dataSnapshot.getValue(ChatModel.class);
                                            isNewChat = false;
                                        } else {
                                            isNewChat = true;
                                            chatModel = new ChatModel();
                                            chatModel.setPinned(false);
                                            chatModel.setChatMute(false);
                                            chatModel.setCreatedTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                                            chatModel.setRoomName(finalMCartData.getSellerName());
                                            chatModel.setRoomImage("");
                                            chatModel.setRoomId(FirebaseManager.getFirebaseRoomId(user.getUserId(), finalMCartData.getSellerId()));
                                            chatModel.setChatType(AppConstants.FIREBASE.FIREBASE_SINGLE_CHAT);
                                            chatModel.setOtherUserId(finalMCartData.getSellerId());
                                        }
                                        if (isOtherUserCreated && !isNewChat)
                                            messagesDetailViewModel.updateProductInfo(user.getUserId(), finalMCartData.getSellerId(), roomId, chatProductModel);
                                        chatModel.setProductInfo(chatProductModel);
                                        MessageModel lastMessage = new MessageModel();
                                        lastMessage.setMessageId(databaseReference.push().getKey());
                                        lastMessage.setMessageText(/*getString(R.string.reserved_the_item) + */" " + finalMCartData.getProductName());
                                        lastMessage.setMessageStatus(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_DELIVERED);
                                        lastMessage.setMessageType(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RESERVE_ITEM);
                                        lastMessage.setSenderId(user.getUserId());
                                        try {
                                            lastMessage.setTimeStamp(ServerValue.TIMESTAMP);
                                        } catch (Exception e) {
                                            lastMessage.setTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                                            e.printStackTrace();
                                        }
                                        lastMessage.setSenderImage(user.getProfilePicture());
                                        lastMessage.setSenderName(user.getFullName());
                                        lastMessage.setRoomId(roomId);
                                        chatModel.setLastMessage(lastMessage);
                                        messagesDetailViewModel.sendMessageToUser(user, isNewChat, isOtherUserCreated, user.getFullName() + " " + getString(R.string.send_a_message), chatModel.getLastMessage().getMessageText(), chatModel, null);

                                        new Handler(Looper.getMainLooper())
                                                .postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, mActivity.getString(R.string.congratulations_title), successResponse.getMessage(), new OnDialogItemClickListener() {
                                                            @Override
                                                            public void onPositiveBtnClick() {
                                                                try {
                                                                    startActivity(new Intent(mActivity, MessagesDetailActivity.class).putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel).putExtra(AppConstants.FIREBASE.TIMESTAMP, chatModel.getCreatedTimeStampLong()));
//                                                                    mActivity.finish();
                                                                } catch (Exception e) {
                                                                    Intent intent = new Intent(mActivity, HomeActivity.class);
                                                                    startActivity(intent);
                                                                    mActivity.finish();
                                                                    e.printStackTrace();
                                                                }
                                                            }

                                                            @Override
                                                            public void onNegativeBtnClick() {

                                                            }
                                                        });
                                                        getLoadingStateObserver().onChanged(false);
                                                        textViewPay.setEnabled(true);
                                                        textViewConfirmPurchase.setEnabled(true);
                                                    }
                                                }, 10);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("Firebase_Error", "" + databaseError.getMessage());
                                        DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, mActivity.getString(R.string.congratulations_title), successResponse.getMessage(), new OnDialogItemClickListener() {
                                            @Override
                                            public void onPositiveBtnClick() {
                                                Intent intent = new Intent(mActivity, HomeActivity.class);
                                                startActivity(intent);
                                                mActivity.finish();
                                            }

                                            @Override
                                            public void onNegativeBtnClick() {

                                            }
                                        });
                                        getLoadingStateObserver().onChanged(false);
                                        textViewPay.setEnabled(true);
                                        textViewConfirmPurchase.setEnabled(true);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("Firebase_Error", "" + databaseError.getMessage());
                                DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, mActivity.getString(R.string.congratulations_title), successResponse.getMessage(), new OnDialogItemClickListener() {
                                    @Override
                                    public void onPositiveBtnClick() {
                                        Intent intent = new Intent(mActivity, HomeActivity.class);
                                        startActivity(intent);
                                        mActivity.finish();
                                    }

                                    @Override
                                    public void onNegativeBtnClick() {

                                    }
                                });
                                getLoadingStateObserver().onChanged(false);
                                textViewPay.setEnabled(true);
                                textViewConfirmPurchase.setEnabled(true);
                            }
                        });

                    } else {
                        cartItemCount++;
                        final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
                        updateChatAfterPayment(successResponse.getMessage(), mCartList.get(cartItemCount), user);

//                        switch (successResponse.getRequestCode()) {
//                            case AppConstants.REQUEST_CODE.DELETE_PRODUCT_CART:
////                            mCartList.remove(position);
////                            adapter.notifyDataSetChanged();
////                            if (mCartList.size() == 0) {
////                                emptyPlaceHolder(View.GONE, View.VISIBLE, View.GONE);
////                            } else {
////                                calculateTotalPrize(mCartList);
////                            }
//                                break;
//                            case AppConstants.REQUEST_CODE.PAYMENT:
//                                cartItemCount++;
//                                final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
//                                updateChatAfterPayment(successResponse.getMessage(), mCartList.get(cartItemCount), user);
//                                break;
//                            case AppConstants.REQUEST_CODE.ZERO_PAYMENT:
//                       /*     getCustomBottomDialog(mActivity.getString(R.string.congratulations_title), commonResponse.getMessage(), new OnDialogItemClickListener() {
//                                @Override
//                                public void onPositiveBtnClick() {*/
//                                mActivity.setResult(Activity.RESULT_OK);
//                                mActivity.finish();
//                                   /* final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
//                                    updateChatAfterPayment(commonResponse.getMessage(), mCartList.get(cartItemCount), user);*/
//                           /*     }
//                                @Override
//                                public void onNegativeBtnClick() {
//                                }
//                            });*/
//                                break;
//                        }
                    }

                } else if (successResponse.getCode() == 415) {
                    showRemoveItemFromCartDialog(successResponse);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                Log.e("Firebase_Error", "" + failureResponse.getMessage());
                getLoadingStateObserver().onChanged(false);
            }

            @Override
            public void onError(Throwable t) {
                Log.e("Firebase_Error", "" + t.getMessage());
                getLoadingStateObserver().onChanged(false);
            }
        });
    }


    // THis function is use for showing dialog for item sold which is added in cart
    private void showRemoveItemFromCartDialog(CommonResponse commonResponse) {
        DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, getString(R.string.sold), commonResponse.getMessage(), new OnDialogItemClickListener() {
            @Override
            public void onPositiveBtnClick() {
//                if (AppUtils.isInternetAvailable(mActivity))
//                    mCartViewModel.getCardList();
//                else showNoNetworkError();
            }

            @Override
            public void onNegativeBtnClick() {

            }
        });
    }

    // This Function is use for update chat screen after payment
    private void updateChatAfterPayment(final String responseMessage, final CartDataBean mCartData, final User user) {

        final String roomId = FirebaseManager.getFirebaseRoomId(user.getUserId(), mCartData.getSellerId());
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(mCartData.getSellerId()).child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshotOtherUser) {

                        System.currentTimeMillis();
                        ChatProductModel chatProductModel = new ChatProductModel();
                        chatProductModel.setProductId(mCartData.getProductId());
                        chatProductModel.setProductName(mCartData.getProductName());
                        chatProductModel.setProductPrice(Double.parseDouble(mCartData.getProductPrice()));
                        if (mCartData.getProductPicList() != null && mCartData.getProductPicList().size() > 0)
                            chatProductModel.setProductImage(mCartData.getProductPicList().get(0));
                        else
                            chatProductModel.setProductImage("");
                        boolean isOtherUserCreated, isNewChat;
                        final ChatModel chatModel, newChatModel;
                        if (dataSnapshotOtherUser.exists())
                            isOtherUserCreated = true;
                        else
                            isOtherUserCreated = false;
                        if (dataSnapshot.exists()) {
                            chatModel = dataSnapshot.getValue(ChatModel.class);
                            isNewChat = false;
                        } else {
                            isNewChat = true;
                            chatModel = new ChatModel();
                            chatModel.setPinned(false);
                            chatModel.setChatMute(false);
                            chatModel.setCreatedTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                            chatModel.setRoomName(mCartData.getSellerName());
                            chatModel.setRoomImage("");
                            chatModel.setRoomId(FirebaseManager.getFirebaseRoomId(user.getUserId(), mCartData.getSellerId()));
                            chatModel.setChatType(AppConstants.FIREBASE.FIREBASE_SINGLE_CHAT);
                            chatModel.setOtherUserId(mCartData.getSellerId());
                        }
                        if (isOtherUserCreated && !isNewChat) {
//                            if (mCartList.size()==1&&chatModel.getProductInfo().getProductId()!=null&&chatModel.getProductInfo().getProductId().equalsIgnoreCase(chatProductModel.getProductId()))
//                            {
//                                ChatProductModel chatProductModel1=new ChatProductModel();
//                                messagesDetailViewModel.updateProductInfo(user.getUserId(), mCartData.getSellerId(), roomId, chatProductModel1);
//                            }
                            messagesDetailViewModel.updateProductInfo(user.getUserId(), mCartData.getSellerId(), roomId, chatProductModel);
                        }
                        chatModel.setProductInfo(chatProductModel);
                        MessageModel lastMessage = new MessageModel();
                        lastMessage.setMessageId(databaseReference.push().getKey());
                        lastMessage.setMessageText(/*getString(R.string.reserved_the_item) + */" " + mCartData.getProductName());
                        lastMessage.setMessageStatus(AppConstants.FIREBASE.FIREBASE_MESSAGE_STATUS_DELIVERED);
                        lastMessage.setMessageType(AppConstants.FIREBASE.FIREBASE_MESSAGE_TYPE_ACTION_RESERVE_ITEM);
                        lastMessage.setSenderId(user.getUserId());
                        try {
                            lastMessage.setTimeStamp(ServerValue.TIMESTAMP);
                        } catch (Exception e) {
                            lastMessage.setTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                            e.printStackTrace();
                        }
                        lastMessage.setSenderImage(user.getProfilePicture());
                        lastMessage.setSenderName(user.getFullName());
                        lastMessage.setRoomId(roomId);
                        chatModel.setLastMessage(lastMessage);
//                        Toast.makeText(mActivity, "time is" + chatModel.getCreatedTimeStamp() + " \nother is " + Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() + "\n and last is " + System.currentTimeMillis(), Toast.LENGTH_LONG).show();
                        messagesDetailViewModel.sendMessageToUser(user, isNewChat, isOtherUserCreated, user.getFullName() + " " + getString(R.string.send_a_message), chatModel.getLastMessage().getMessageText(), chatModel, new FirebaseManager.CountUpdateListener() {
                            @Override
                            public void isCountUpdated(boolean isUpdated) {
                                if (cartItemCount < mCartList.size() - 1) {

                                    cartItemCount++;
                                    updateChatAfterPayment(responseMessage, mCartList.get(cartItemCount), user);
                                }
                            }
                        });
                        if (cartItemCount == 0) {

                            getLoadingStateObserver().onChanged(false);
                            textViewPay.setEnabled(true);
                            textViewConfirmPurchase.setEnabled(true);
                            DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, getString(R.string.congratulations_title), responseMessage, new OnDialogItemClickListener() {
                                @Override
                                public void onPositiveBtnClick() {
                                    try {
                                        startActivity(new Intent(mActivity, MessagesDetailActivity.class).putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel).putExtra(AppConstants.FIREBASE.TIMESTAMP, chatModel.getCreatedTimeStampLong()).putExtra(AppConstants.FIREBASE.FIREBASE_OTHER_USER_ID, mCartData.getSellerId()));
//                                    mActivity.setResult(Activity.RESULT_OK);
//                                    mActivity.finish();
                                    } catch (Exception e) {
                                        Intent intent = new Intent(mActivity, HomeActivity.class);
                                        startActivity(intent);
                                        mActivity.finish();
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase_Error", "" + databaseError.getMessage());
                        if (cartItemCount == 0) {
                            try {
                                getLoadingStateObserver().onChanged(false);
                                textViewPay.setEnabled(true);
                                textViewConfirmPurchase.setEnabled(true);
                                DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, getString(R.string.congratulations_title), responseMessage, new OnDialogItemClickListener() {
                                    @Override
                                    public void onPositiveBtnClick() {
                                        Intent intent = new Intent(mActivity, HomeActivity.class);
                                        startActivity(intent);
                                        mActivity.finish();
                                    }

                                    @Override
                                    public void onNegativeBtnClick() {

                                    }
                                });
                            } catch (Exception e) {
                                Intent intent = new Intent(mActivity, HomeActivity.class);
                                startActivity(intent);
                                mActivity.finish();
                                e.printStackTrace();
                            }
                        }
                        cartItemCount++;
                        getLoadingStateObserver().onChanged(false);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase_Error", "" + databaseError.getMessage());
                if (cartItemCount == 0) {
                    getLoadingStateObserver().onChanged(false);
                    textViewPay.setEnabled(true);
                    textViewConfirmPurchase.setEnabled(true);
                    DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, getString(R.string.congratulations_title), responseMessage, new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {
                            Intent intent = new Intent(mActivity, HomeActivity.class);
                            startActivity(intent);
                            mActivity.finish();
                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });
                }
                cartItemCount++;
                getLoadingStateObserver().onChanged(false);
            }
        });
    }

    public String getShopperId() {
        return PreferenceManager.getInstance(context).getString(SHOPPER_ID);
    }

    private void setShopperId(String shopperId) {
        PreferenceManager.getInstance(context).putString(SHOPPER_ID, "" + shopperId);
    }

    public String getTransactionId() {
        return transactionId;
    }

    private void setTransactionId(String id) {
        this.transactionId = id;
    }

    public String getCardLastFourDigits() {
        return cardLastFourDigits;
    }

    public void setCardLastFourDigits(String cardLastFourDigits) {
        this.cardLastFourDigits = cardLastFourDigits;
    }

    public String getTokenSuffix() {
        return tokenSuffix;
    }

    private void setTokenSuffix(String token) {
        this.tokenSuffix = token;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private void getSavedCardsListing() {
        getLoadingStateObserver().onChanged(true);
        get_shopper_service(new TokenServiceInterface() {
            @Override
            public void onServiceSuccess() {


            }

            @Override
            public void onServiceFailure() {

            }
        });
    }

    // Make a retrieve vaulted shopper API call
    private void get_shopper_service(final TokenServiceInterface tokenServiceInterface) {
        getLoadingStateObserver().onChanged(true);

        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        List<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));

        List<CustomHTTPParams> sahdboxHttpHeaders = headerParams;

        final String vaultedShopperId = "" + PreferenceManager.getInstance(mActivity).getString(SHOPPER_ID);

        final Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("API", "" + SANDBOX_URL + SANDBOX_VAULTED_SHOPPER + "/" + vaultedShopperId);
                    BlueSnapHTTPResponse response = HTTPOperationController.get(SANDBOX_URL + SANDBOX_VAULTED_SHOPPER + "/" + vaultedShopperId, "application/json", "application/json", sahdboxHttpHeaders);
                    if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                        getShopperResponse = response.getResponseString();
                        Log.e("CARDS_RESPONSE", "" + getShopperResponse);

                        blueSnapResponse = new Gson().fromJson(getShopperResponse, BlueSnapCardListResponse.class);

//                        getLoadingStateObserver().onChanged(false);

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (blueSnapResponse.getPaymentSources().getCreditCardInfo().size() > 0) {
                                    llNewCard.setVisibility(View.GONE);
                                    llSavedCards.setVisibility(View.VISIBLE);
                                    llSavedCardPayOption.setVisibility(View.VISIBLE);

                                    for (int i = 0; i < blueSnapResponse.getPaymentSources().getCreditCardInfo().size(); i++) {
                                        if (blueSnapResponse.getLastPaymentInfo().getCreditCard() != null && blueSnapResponse.getLastPaymentInfo().getCreditCard().getCardLastFourDigits().equalsIgnoreCase(blueSnapResponse.getPaymentSources().getCreditCardInfo().get(i).getCreditCard().getCardLastFourDigits())) {
                                            selectedCardPosition = i;
                                        }
                                    }
                                    cardInfoArrayList.clear();
                                    rvSavedCards.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
                                    rvSavedCards.setAdapter(new SavedCardsAdapter(mActivity, (ArrayList<BlueSnapCardListResponse.CreditCardInfo>) blueSnapResponse.getPaymentSources().getCreditCardInfo()));
                                } else {
                                    llNewCard.setVisibility(View.VISIBLE);
                                    llSavedCards.setVisibility(View.GONE);
                                    llSavedCardPayOption.setVisibility(View.GONE);
                                }
                                generateMerchantToken();

                            }
                        }, 10);
                        tokenServiceInterface.onServiceSuccess();
                    } else {
                        Log.e(TAG, response.getResponseCode() + " " + response.getErrorResponseString());
                        generateMerchantToken();
                        tokenServiceInterface.onServiceFailure();
                    }

                } catch (Exception e) {
                    generateMerchantToken();
                    tokenServiceInterface.onServiceFailure();
                    getLoadingStateObserver().onChanged(false);
                }

            }
        };

        TagHawkApplication.mainHandler.post(myRunnable);

    }

    private void getPayFromSavedCards(BlueSnapCardListResponse.CreditCardInfo creditCardInfo) {

//        getLoadingStateObserver().onChanged(true);

        String body = "<card-transaction xmlns=\"http://ws.plimus.com\">" +
                "<card-transaction-type>AUTH_CAPTURE</card-transaction-type>" +
                "<soft-descriptor>MobileSDKtest</soft-descriptor>" +
                "<recurring-transaction>ECOMMERCE</recurring-transaction>" +
                "<amount>" + totalAmount + "</amount>" +
                "<currency>USD</currency>" +
                "<vaulted-shopper-id>" + PreferenceManager.getInstance(mActivity).getString(SHOPPER_ID) + "</vaulted-shopper-id>" +
                "<credit-card>" +
                "<card-last-four-digits>" + creditCardInfo.getCreditCard().getCardLastFourDigits() + "</card-last-four-digits>" +
                "<card-type>" + creditCardInfo.getCreditCard().getCardType() + "</card-type>" +
                "</credit-card>" +
                "<pf-token>" + BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken() + "</pf-token>" +
                vendorIdString +
                "</card-transaction>";

//        String body = " [\"amount\": \"" + totalAmount + "\", \n" +
//                "\"recurringTransaction\": \"ECOMMERCE\", \n" +
//                "\"softDescriptor\": \"MobileSDKtest\", \n" +
//                "\"cardTransactionType\": \"AUTH_CAPTURE\", \n" +
//                "\"vaultedShopperId\": \"" + PreferenceManager.getInstance(mActivity).getString(SHOPPER_ID) + "\", \n" +
//                "\"creditCard\": [\n" +
//                "\"cardLastFourDigits\": \"" + creditCardInfo.getCreditCard().getCardLastFourDigits() + "\", \n" +
//                "\"cardType\": \"" + creditCardInfo.getCreditCard().getCardType() + "\"\n" +
//                "], \n" +
//                "\"currency\": \"" + "USD" + "\", \n" +
//                "\"pfToken\": \"" + BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken() + "\"]";

        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
        List<CustomHTTPParams> headerParams = new ArrayList<>();
        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
        BlueSnapHTTPResponse httpResponse = HTTPOperationController.post(SANDBOX_URL + SANDBOX_CREATE_TRANSACTION, body, "application/xml", "application/xml", headerParams);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getLoadingStateObserver().onChanged(true);

            }
        }, 10);
        Log.e("API", SANDBOX_URL + SANDBOX_CREATE_TRANSACTION);
        Log.e("API_HEADER", "Authorization : " + basicAuth);
        Log.e("API_PARAMS", "" + body);
        String responseString = httpResponse.getResponseString();
        Log.e("API_RESPONSE", "" + responseString);
        if (httpResponse.getResponseCode() == HTTP_OK && httpResponse.getHeaders() != null) {
            setShopperId(responseString.substring(responseString.indexOf("<vaulted-shopper-id>") +
                    "<vaulted-shopper-id>".length(), responseString.indexOf("</vaulted-shopper-id>")));
            setTransactionId(responseString.substring(responseString.indexOf("<transaction-id>") +
                    "<transaction-id>".length(), responseString.indexOf("</transaction-id>")));
            setCardLastFourDigits(responseString.substring(responseString.indexOf("<card-last-four-digits>") +
                    "<card-last-four-digits>".length(), responseString.indexOf("</card-last-four-digits>")));

            String merchantToken = BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken();
            setTokenSuffix(merchantToken.substring(merchantToken.length() - 6));
            Log.d(TAG, responseString);
//            setMessage("Transaction Success " + getTransactionId() + "\nShopper Id " + getShopperId());
//            setTitle("Merchant Server");
//            callback.onSuccess();

            PaymentStatusRequest paymentStatusRequest = new PaymentStatusRequest();

            paymentStatusRequest.setAmount(totalAmount);
            paymentStatusRequest.setCurrency("USD");
            if (!TextUtils.isEmpty(PreferenceManager.getInstance(mActivity).getString(SHOPPER_ID))) {
                paymentStatusRequest.setVaultedShopperId(responseString.substring(responseString.indexOf("<vaulted-shopper-id>") + "<vaulted-shopper-id>".length(), responseString.indexOf("</vaulted-shopper-id>")));
            }

            PaymentStatusRequest.Payment payment = new PaymentStatusRequest.Payment();
            payment.setId(getTransactionId());
            payment.setPaid(true);

            PaymentStatusRequest.Address address = new PaymentStatusRequest.Address();
            address.setPostal_code("");
//            paymentStatusRequest.getPayment().getBilling_details().getAddress().setPostal_code(responseString.substring(responseString.indexOf("<zip>") + "<zip>".length(), responseString.indexOf("</zip>")));
            PaymentStatusRequest.Billing_details billing_details = new PaymentStatusRequest.Billing_details();
            billing_details.setAddress(address);
            payment.setBilling_details(billing_details);

            PaymentStatusRequest.Payment_method_details payment_method_details = new PaymentStatusRequest.Payment_method_details();
            payment_method_details.setType("card");

            PaymentStatusRequest.Card card = new PaymentStatusRequest.Card();
            card.setExp_month(0);
            card.setExp_year(0);
            card.setFunding(responseString.substring(responseString.indexOf("<card-type>") + "<card-type>".length(), responseString.indexOf("</card-type>")));
            card.setNetwork(responseString.substring(responseString.indexOf("<card-type>") + "<card-type>".length(), responseString.indexOf("</card-type>")));
            card.setLast4(getCardLastFourDigits());
            card.setWallet(new PaymentStatusRequest.Wallet());

            PaymentStatusRequest.Checks checks = new PaymentStatusRequest.Checks();
            card.setChecks(checks);

            payment_method_details.setCard(card);

            payment.setPayment_method_details(payment_method_details);
            paymentStatusRequest.setPayment(payment);

            ArrayList<PaymentStatusRequest.Products> products = new ArrayList<>();
            for (int i = 0; i < mCartList.size(); i++) {
                PaymentStatusRequest.Products product = new PaymentStatusRequest.Products();
                product.setPrice(mCartList.get(i).getProductPrice());
                product.setProductId(mCartList.get(i).getProductId());
                product.setSellerId(mCartList.get(i).getSellerId());
                if (mCartList.get(i).getOwnerId() != null && !TextUtils.isEmpty(mCartList.get(i).getOwnerId()))
                    product.setOwnerId(mCartList.get(i).getOwnerId());
                products.add(product);
            }
            paymentStatusRequest.setProducts(products);

            if (screen.equalsIgnoreCase("shipping")) {

                PaymentStatusRequest.Ship_to ship_to = new PaymentStatusRequest.Ship_to();

                ship_to.setContact_name(parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString());
                if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getEmail() != null)
                    ship_to.setEmail(DataManager.getInstance().getUserDetails().getEmail());
                ship_to.setCity(parms.get(AppConstants.KEY_CONSTENT.CITY).toString());
                ship_to.setState(parms.get(AppConstants.KEY_CONSTENT.STATE).toString());
                ship_to.setPostal_code(parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
                ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
                ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
                ship_to.setPhone(parms.get(AppConstants.KEY_CONSTENT.PHONE).toString());
                ship_to.setType("FEDEX");

                paymentStatusRequest.setShip_to(ship_to);
            }

            PaymentStatusRequest.Transaction transaction = new PaymentStatusRequest.Transaction();
            transaction.setTransactionid("" + getTransactionId());
            transaction.setAmount(Double.toString(totalAmount));
            transaction.setCurrency("USD");
            transaction.setFirstname("" + creditCardInfo.getBillingContactInfo().getFirstName());
            transaction.setLastname("" + creditCardInfo.getBillingContactInfo().getLastName());
            transaction.setZip("" + creditCardInfo.getBillingContactInfo().getZip());
            transaction.setCardlastfourdigits(getCardLastFourDigits());

            paymentStatusRequest.setTransaction(transaction);

//            getLoadingStateObserver().onChanged(true);
            doPayment(paymentStatusRequest);

//            Intent intent = new Intent();
//            intent.putExtra(AppConstants.BUNDLE_DATA, paymentStatusRequest);
//            setResult(RESULT_OK, intent);
//            finish();

        } else {
            getLoadingStateObserver().onChanged(false);
            Log.e(TAG, responseString);
            //Disabled until server will return a reasonable error
            String errorName = "Transaction Failed";
            try {
                if (responseString != null)
                    errorName = responseString.substring(responseString.indexOf("<error-name>") + "<error-name>".length(), responseString.indexOf("</error-name>"));
                Log.e(TAG, "Failed TX Response:  " + responseString);
            } catch (Exception e) {
                Log.e(TAG, "failed to get error name from response string");
                Log.e(TAG, "Failed TX Response:  " + responseString);
            }
//            setMessage(errorName);
//            setTitle("Merchant Server");
//            callback.onFailure();
            getPaymentFailureApi(Double.toString(totalAmount), "USD", creditCardInfo.getBillingContactInfo().getFirstName(), creditCardInfo.getBillingContactInfo().getLastName(), creditCardInfo.getBillingContactInfo().getZip(), creditCardInfo.getCreditCard().getCardLastFourDigits(), errorName);

        }

    }

    private class SavedCardsAdapter extends RecyclerView.Adapter<SavedCardsAdapter.MyViewHolder> {

        private Context context;
        private LayoutInflater inflater;

        public SavedCardsAdapter(Context context, ArrayList<BlueSnapCardListResponse.CreditCardInfo> cardInfoList) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            cardInfoArrayList = cardInfoList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.layout_bluessnap_saved_cards, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            if (cardInfoArrayList.get(position).getCreditCard().getCardType().equalsIgnoreCase("VISA")) {
                holder.ivCardType.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_visa));
            } else if (cardInfoArrayList.get(position).getCreditCard().getCardType().equalsIgnoreCase("DISCOVER")) {
                holder.ivCardType.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_discover));
            } else if (cardInfoArrayList.get(position).getCreditCard().getCardType().equalsIgnoreCase("AMEX")) {
                holder.ivCardType.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_amex));
            } else if (cardInfoArrayList.get(position).getCreditCard().getCardType().equalsIgnoreCase("MASTERCARD")) {
                holder.ivCardType.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_mastercard));
            } else if (cardInfoArrayList.get(position).getCreditCard().getCardType().equalsIgnoreCase("JCB")) {
                holder.ivCardType.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_jcb_icon));
            }

            holder.tvCardSubType.setText("" + cardInfoArrayList.get(position).getCreditCard().getCardSubType());
            holder.tvCardLastDigits.setText("XXXX" + cardInfoArrayList.get(position).getCreditCard().getCardLastFourDigits());

            if (selectedCardPosition == position) {
                holder.llMain.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.round_border_color_primary_2dp));
            } else {
                holder.llMain.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_border_color_white));
            }

            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedCardPosition = holder.getAdapterPosition();
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return cardInfoArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private ImageView ivCardType;
            private AppCompatTextView tvCardSubType;
            private AppCompatTextView tvCardLastDigits;
            private LinearLayout llMain;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                ivCardType = itemView.findViewById(R.id.iv_card_type);
                tvCardSubType = itemView.findViewById(R.id.tv_card_sub_type);
                tvCardLastDigits = itemView.findViewById(R.id.tv_card_last_digits);
                llMain = itemView.findViewById(R.id.ll_main);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (transactionStatus) {
            startActivity(new Intent(mActivity, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finishAffinity();
        }
    }

    private void siftCreateOrder(String firstName, String lastName, String zipCode, String cardLastFourDigits) {


        CreateSiftOrderRequest createSiftOrderRequest = new CreateSiftOrderRequest();

        if (screen.equalsIgnoreCase("shipping")) {

            PaymentStatusRequest.Ship_to ship_to = new PaymentStatusRequest.Ship_to();

            ship_to.setContact_name(parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString());
            if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getEmail() != null)
                ship_to.setEmail(DataManager.getInstance().getUserDetails().getEmail());
            ship_to.setCity(parms.get(AppConstants.KEY_CONSTENT.CITY).toString());
            ship_to.setState(parms.get(AppConstants.KEY_CONSTENT.STATE).toString());
            ship_to.setPostal_code(parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
            ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
            ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
            ship_to.setPhone(parms.get(AppConstants.KEY_CONSTENT.PHONE).toString());
            ship_to.setType("FEDEX");

            createSiftOrderRequest.setShip_to(ship_to);
        }

        PaymentStatusRequest.Transaction transaction = new PaymentStatusRequest.Transaction();
//        transaction.setTransactionid("" + getTransactionId());
        transaction.setAmount(Double.toString(totalAmount));
        transaction.setCurrency("USD");
        transaction.setFirstname(firstName);
        transaction.setLastname(lastName);
        transaction.setZip(zipCode);
        transaction.setCardlastfourdigits(cardLastFourDigits);
        if (screen.equalsIgnoreCase("shipping"))
            transaction.setShipping_choice("fedex");
        else
            transaction.setShipping_choice("pickup_deliver");

//        transaction.setFirstname("" + etCardHolderName.getText().toString().trim().split(" ")[0]);
//        if (etCardHolderName.getText().toString().trim().split(" ").length > 1)
//            transaction.setLastname("" + etCardHolderName.getText().toString().trim().split(" ")[1]);
//        transaction.setZip("" + etZipCode.getText().toString());
//        transaction.setCity(AppUtils.getCityName(mActivity, etZipCode.getText().toString()));
//        transaction.setCardlastfourdigits(getCardLastFourDigits());

        createSiftOrderRequest.setTransaction(transaction);

        DataManager.getInstance().createSiftOrder(createSiftOrderRequest).enqueue(new NetworkCallback<CreateSiftOrderResponse>() {
            @Override
            public void onSuccess(CreateSiftOrderResponse response) {
                if (response.getCode() == 200) {
                    if (response.getData()) {
                        if (isNewCard) {
//                            getLoadingStateObserver().onChanged(true);
                            onPaySubmit(totalAmount);
                        } else {

                            TagHawkApplication.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    getLoadingStateObserver().onChanged(true);
                                    getPayFromSavedCards(cardInfoArrayList.get(selectedCardPosition));
                                }
                            });
                        }
                    } else {
                        DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.payment_declined), mActivity.getString(R.string.payment_declined_message), new OnDialogItemClickListener() {
                            @Override
                            public void onPositiveBtnClick() {

                            }

                            @Override
                            public void onNegativeBtnClick() {

                            }
                        });
                        getLoadingStateObserver().onChanged(false);
                        textViewPay.setEnabled(true);
                        textViewConfirmPurchase.setEnabled(true);
                    }
                } else {
                    // when api response != 200
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {

            }

            @Override
            public void onError(Throwable t) {

            }
        });

    }

    private void openAddUpdateBillingAddress() {
        Intent intent = new Intent(mActivity, AddUpdateAddressActivity.class);
        intent.putExtra("type", "billing_address");
        intent.putExtra("billingAddress", billingAddress);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.ADD_BILLING_ADDRESS);
    }

    private void getBillingAddress() {

        getLoadingStateObserver().onChanged(true);
        DataManager.getInstance().getBillingAddress(DataManager.getInstance().getUserDetails().getUserId()).enqueue(new NetworkCallback<ShippingAddressesResponse>() {
            @Override
            public void onSuccess(ShippingAddressesResponse response) {
                if (response.getStatusCode() == 200) {
                    getLoadingStateObserver().onChanged(false);
                    if (response.getData().size() > 0 && response.getData().get(0).getStreet1() != null && !TextUtils.isEmpty(response.getData().get(0).getStreet1().trim())) {

                        Log.e("hiiiii", "entered");
                        billingAddress = response.getData().get(0);
                        mCheckBoxSameAsShipping.setChecked(false);
                        mLlSameAsShipping.setVisibility(View.GONE);
                        llBillingAddress.setVisibility(View.VISIBLE);
                        mTvBillingFullName.setText("" + billingAddress.getContact_name());
                        if (billingAddress.getStreet2() != null && !TextUtils.isEmpty(billingAddress.getStreet2()))
                            mTvBillingAddress.setText("" + billingAddress.getStreet1() + ", #" + billingAddress.getStreet2() + ", " + billingAddress.getCity() + ", " + billingAddress.getState() + "\n" + "Zip code: " + billingAddress.getPostal_code());
                        else
                            mTvBillingAddress.setText("" + billingAddress.getStreet1() + ", " + billingAddress.getCity() + ", " + billingAddress.getState() + "\n" + "Zip code: " + billingAddress.getPostal_code());

                        mTvBillingMobile.setText("Mobile: " + billingAddress.getPhone());

                    } else if (screen.equalsIgnoreCase("shipping")) {

                        mCheckBoxSameAsShipping.setChecked(true);
                        mLlSameAsShipping.setVisibility(View.VISIBLE);
                        llBillingAddress.setVisibility(View.GONE);

                    } else {
                        mCheckBoxSameAsShipping.setChecked(false);
                        mLlSameAsShipping.setVisibility(View.GONE);
                        llBillingAddress.setVisibility(View.GONE);
                    }
                } else {
                    // when api response != 200
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {

            }

            @Override
            public void onError(Throwable t) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.ADD_BILLING_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {
                    billingAddress = data.getExtras().getParcelable(AppConstants.BUNDLE_DATA);
                    if (billingAddress.getStreet1() != null && !TextUtils.isEmpty(billingAddress.getStreet1().trim())) {
                        mLlSameAsShipping.setVisibility(View.GONE);
                        llBillingAddress.setVisibility(View.VISIBLE);
                        mTvBillingFullName.setText("" + billingAddress.getContact_name());
                        if (billingAddress.getStreet2() != null && !TextUtils.isEmpty(billingAddress.getStreet2()))
                            mTvBillingAddress.setText("" + billingAddress.getStreet1() + ", #" + billingAddress.getStreet2() + ", " + billingAddress.getCity() + ", " + billingAddress.getState() + "\n" + "Zip code: " + billingAddress.getPostal_code());
                        else
                            mTvBillingAddress.setText("" + billingAddress.getStreet1() + ", " + billingAddress.getCity() + ", " + billingAddress.getState() + "\n" + "Zip code: " + billingAddress.getPostal_code());

                        mTvBillingMobile.setText("Mobile: " + billingAddress.getPhone());

                        if(isTransaction) {
                            isTransaction = false;
                            getLoadingStateObserver().onChanged(true);
                            if (isNewCard) {
                                siftCreateOrder(etCardHolderName.getText().toString().trim().split(" ")[0],
                                        etCardHolderName.getText().toString().trim().split(" ")[1],
                                        etZipCode.getText().toString(),
//                            AppUtils.getCityName(mActivity, etZipCode.getText().toString()),
                                        etCardNumber.getText().toString().trim().substring(etCardNumber.getText().toString().trim().length() - 4));
                            } else {
                                siftCreateOrder(blueSnapResponse.getFirstName(),
                                        blueSnapResponse.getLastName(),
                                        blueSnapResponse.getZip(),
//                        AppUtils.getCityName(mActivity, blueSnapResponse.getZip()),
                                        cardInfoArrayList.get(selectedCardPosition).getCreditCard().getCardLastFourDigits());
                            }
                        } else if (isGooglePay) {
                            siftCreateOrderGooglePay();
                        }

                    } else {
                        if(screen.equalsIgnoreCase("shipping")) {
//                            mCheckBoxSameAsShipping.setChecked(true);
                            mLlSameAsShipping.setVisibility(View.VISIBLE);
                            llBillingAddress.setVisibility(View.GONE);
                        } else {
                            mCheckBoxSameAsShipping.setChecked(false);
                            mLlSameAsShipping.setVisibility(View.GONE);
                            llBillingAddress.setVisibility(View.GONE);
                        }
                    }
//                    profileViewModel.getBillingAddress(DataManager.getInstance().getUserDetails().getUserId());
                }
                break;
            case AppConstants.ACTIVITY_RESULT.G_PAY_STRIPE:
                if (resultCode == Activity.RESULT_OK) {
                    if(data.getExtras().getInt("paymentStatus") == 1) {
                        String apiGooglePayResponse = data.getExtras().getString("responseString");
                        setGooglePaySuccessRequestCharges(apiGooglePayResponse);
                    } else if(data.getExtras().getInt("paymentStatus") == 2) {
                        String errorMessage = data.getExtras().getString("errorString");
                        getPaymentFailureApiGooglePay(Double.toString(totalAmount), "USD", errorMessage);
                    } else {
                        String errorMessage = data.getExtras().getString("errorString");
                        DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, "Google Pay Error", errorMessage, new OnDialogItemClickListener() {
                            @Override
                            public void onPositiveBtnClick() {

                            }

                            @Override
                            public void onNegativeBtnClick() {

                            }
                        });
                    }
                }
                break;
        }
    }

    private void getPaymentFailureApi(String amount, String currency, String firstName, String lastName, String zipCode, String cardLastFourDigits, String errorMessage) {

        PaymentStatusFailureModel paymentStatusRequest = new PaymentStatusFailureModel();

        if (screen.equalsIgnoreCase("shipping")) {

            PaymentStatusFailureModel.Ship_to ship_to = new PaymentStatusFailureModel.Ship_to();

            ship_to.setContact_name(parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString());
            if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getEmail() != null)
                ship_to.setEmail(DataManager.getInstance().getUserDetails().getEmail());
            ship_to.setCity(parms.get(AppConstants.KEY_CONSTENT.CITY).toString());
            ship_to.setState(parms.get(AppConstants.KEY_CONSTENT.STATE).toString());
            ship_to.setPostal_code(parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
            ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
            ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
            ship_to.setPhone(parms.get(AppConstants.KEY_CONSTENT.PHONE).toString());
            ship_to.setType("FEDEX");

            paymentStatusRequest.setShip_to(ship_to);
        }

        PaymentStatusFailureModel.Transaction transaction = new PaymentStatusFailureModel.Transaction();
//        transaction.setTransactionid("" + getTransactionId());
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setFirstname(firstName);
        transaction.setLastname(lastName);
        transaction.setZip(zipCode);
        transaction.setCardlastfourdigits(cardLastFourDigits);
        if (screen.equalsIgnoreCase("shipping"))
            transaction.setShipping_choice("fedex");
        else
            transaction.setShipping_choice("pickup_deliver");

        paymentStatusRequest.setTransaction(transaction);

        Gson gson = new Gson();
        String json = gson.toJson(paymentStatusRequest);
        Log.e("FAILURE_REQUEST_JSON", "" + json);
        DataManager.getInstance().paymentFailure(paymentStatusRequest).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                if (response.getCode() == 200) {

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("*****Done*****");
                            getLoadingStateObserver().onChanged(false);
                            DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, mActivity.getString(R.string.transaction_failed), "" + errorMessage, new OnDialogItemClickListener() {
                                @Override
                                public void onPositiveBtnClick() {

                                }

                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                            textViewPay.setEnabled(true);
                            textViewConfirmPurchase.setEnabled(true);
                        }
                    }, 10);

                } else {
                    // when api response != 200
                    getLoadingStateObserver().onChanged(false);
                    DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.user_details_error_info), new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {

                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });

                    textViewPay.setEnabled(true);
                    textViewConfirmPurchase.setEnabled(true);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                getLoadingStateObserver().onChanged(false);
                DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.user_details_error_info), new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {

                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });

                textViewPay.setEnabled(true);
                textViewConfirmPurchase.setEnabled(true);
            }

            @Override
            public void onError(Throwable t) {
                getLoadingStateObserver().onChanged(false);
                DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.user_details_error_info), new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {

                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });

                textViewPay.setEnabled(true);
                textViewConfirmPurchase.setEnabled(true);
            }
        });

    }

    private void setGooglePaySuccessRequestCharges(String responseString) {

        setShopperId(responseString.substring(responseString.indexOf("<vaulted-shopper-id>") +
                "<vaulted-shopper-id>".length(), responseString.indexOf("</vaulted-shopper-id>")));
        setTransactionId(responseString.substring(responseString.indexOf("<transaction-id>") +
                "<transaction-id>".length(), responseString.indexOf("</transaction-id>")));
        setCardLastFourDigits(responseString.substring(responseString.indexOf("<card-last-four-digits>") +
                "<card-last-four-digits>".length(), responseString.indexOf("</card-last-four-digits>")));

        String merchantToken = BlueSnapService.getInstance().getBlueSnapToken().getMerchantToken();
        setTokenSuffix(merchantToken.substring(merchantToken.length() - 6));
        Log.d(TAG, responseString);
//            setMessage("Transaction Success " + getTransactionId() + "\nShopper Id " + getShopperId());
//            setTitle("Merchant Server");
//            callback.onSuccess();

//            getLoadingStateObserver().onChanged(false);

        PaymentStatusRequest paymentStatusRequest = new PaymentStatusRequest();

        paymentStatusRequest.setAmount(totalAmount);
        paymentStatusRequest.setCurrency("USD");
        if (!TextUtils.isEmpty(PreferenceManager.getInstance(mActivity).getString(SHOPPER_ID))) {
            paymentStatusRequest.setVaultedShopperId(responseString.substring(responseString.indexOf("<vaulted-shopper-id>") + "<vaulted-shopper-id>".length(), responseString.indexOf("</vaulted-shopper-id>")));
        }

        PaymentStatusRequest.Payment payment = new PaymentStatusRequest.Payment();
        payment.setId(getTransactionId());
        payment.setPaid(true);

        PaymentStatusRequest.Address address = new PaymentStatusRequest.Address();
        address.setPostal_code("0");
//            paymentStatusRequest.getPayment().getBilling_details().getAddress().setPostal_code(responseString.substring(responseString.indexOf("<zip>") + "<zip>".length(), responseString.indexOf("</zip>")));
        PaymentStatusRequest.Billing_details billing_details = new PaymentStatusRequest.Billing_details();
        billing_details.setAddress(address);
        payment.setBilling_details(billing_details);

        PaymentStatusRequest.Payment_method_details payment_method_details = new PaymentStatusRequest.Payment_method_details();
        payment_method_details.setType("card");

        PaymentStatusRequest.Card card = new PaymentStatusRequest.Card();
        card.setExp_month(0);
        card.setExp_year(0);
        card.setFunding(responseString.substring(responseString.indexOf("<card-type>") + "<card-type>".length(), responseString.indexOf("</card-type>")));
        card.setNetwork(responseString.substring(responseString.indexOf("<card-type>") + "<card-type>".length(), responseString.indexOf("</card-type>")));
        card.setLast4(getCardLastFourDigits());
        card.setWallet(new PaymentStatusRequest.Wallet());

        PaymentStatusRequest.Checks checks = new PaymentStatusRequest.Checks();
        card.setChecks(checks);

        payment_method_details.setCard(card);

        payment.setPayment_method_details(payment_method_details);
        paymentStatusRequest.setPayment(payment);

        ArrayList<PaymentStatusRequest.Products> products = new ArrayList<>();
        for (int i = 0; i < mCartList.size(); i++) {
            PaymentStatusRequest.Products product = new PaymentStatusRequest.Products();
            product.setPrice(mCartList.get(i).getProductPrice());
            product.setProductId(mCartList.get(i).getProductId());
            product.setSellerId(mCartList.get(i).getSellerId());
            if (mCartList.get(i).getOwnerId() != null && !TextUtils.isEmpty(mCartList.get(i).getOwnerId()))
                product.setOwnerId(mCartList.get(i).getOwnerId());
            products.add(product);
        }
        paymentStatusRequest.setProducts(products);

        if (screen.equalsIgnoreCase("shipping")) {

            PaymentStatusRequest.Ship_to ship_to = new PaymentStatusRequest.Ship_to();

            ship_to.setContact_name(parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString());
            if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getEmail() != null)
                ship_to.setEmail(DataManager.getInstance().getUserDetails().getEmail());
            ship_to.setCity(parms.get(AppConstants.KEY_CONSTENT.CITY).toString());
            ship_to.setState(parms.get(AppConstants.KEY_CONSTENT.STATE).toString());
            ship_to.setPostal_code(parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
            ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
            ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
            ship_to.setPhone(parms.get(AppConstants.KEY_CONSTENT.PHONE).toString());
            ship_to.setType("FEDEX");

            paymentStatusRequest.setShip_to(ship_to);
        }

        PaymentStatusRequest.Transaction transaction = new PaymentStatusRequest.Transaction();
        transaction.setTransactionid("" + getTransactionId());
        transaction.setAmount(String.valueOf(totalAmount));
        transaction.setCurrency("USD");
//        transaction.setFirstname(responseString.substring(responseString.indexOf("<first-name>") + "<first-name>".length(), responseString.indexOf("</first-name>")));
//        transaction.setLastname(responseString.substring(responseString.indexOf("<last-name>") + "<last-name>".length(), responseString.indexOf("</last-name>")));
//        transaction.setZip(responseString.substring(responseString.indexOf("<zip>") + "<zip>".length(), responseString.indexOf("</zip>")));
//        transaction.setCardlastfourdigits(getCardLastFourDigits());
        transaction.setWallet("google_wallet");

        paymentStatusRequest.setTransaction(transaction);

//            getLoadingStateObserver().onChanged(true);
        doPayment(paymentStatusRequest);

    }

    private void siftCreateOrderGooglePay() {


        CreateSiftOrderRequest createSiftOrderRequest = new CreateSiftOrderRequest();

        if (screen.equalsIgnoreCase("shipping")) {

            PaymentStatusRequest.Ship_to ship_to = new PaymentStatusRequest.Ship_to();

            ship_to.setContact_name(parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString());
            if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getEmail() != null)
                ship_to.setEmail(DataManager.getInstance().getUserDetails().getEmail());
            ship_to.setCity(parms.get(AppConstants.KEY_CONSTENT.CITY).toString());
            ship_to.setState(parms.get(AppConstants.KEY_CONSTENT.STATE).toString());
            ship_to.setPostal_code(parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
            ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
            ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
            ship_to.setPhone(parms.get(AppConstants.KEY_CONSTENT.PHONE).toString());
            ship_to.setType("FEDEX");

            createSiftOrderRequest.setShip_to(ship_to);
        }

        PaymentStatusRequest.Transaction transaction = new PaymentStatusRequest.Transaction();
//        transaction.setTransactionid("" + getTransactionId());
        transaction.setAmount(Double.toString(totalAmount));
        transaction.setCurrency("USD");
//        transaction.setFirstname("");
//        transaction.setLastname("");
//        transaction.setZip("");
//        transaction.setCardlastfourdigits(cardLastFourDigits);
        transaction.setWallet("google_wallet");
        if (screen.equalsIgnoreCase("shipping"))
            transaction.setShipping_choice("fedex");
        else
            transaction.setShipping_choice("pickup_deliver");

//        transaction.setFirstname("" + etCardHolderName.getText().toString().trim().split(" ")[0]);
//        if (etCardHolderName.getText().toString().trim().split(" ").length > 1)
//            transaction.setLastname("" + etCardHolderName.getText().toString().trim().split(" ")[1]);
//        transaction.setZip("" + etZipCode.getText().toString());
//        transaction.setCity(AppUtils.getCityName(mActivity, etZipCode.getText().toString()));
//        transaction.setCardlastfourdigits(getCardLastFourDigits());

        createSiftOrderRequest.setTransaction(transaction);

        DataManager.getInstance().createSiftOrder(createSiftOrderRequest).enqueue(new NetworkCallback<CreateSiftOrderResponse>() {
            @Override
            public void onSuccess(CreateSiftOrderResponse response) {
                if (response.getCode() == 200) {
                    if (response.getData()) {
                        getLoadingStateObserver().onChanged(false);
                        Intent intent = new Intent(mActivity, GooglePayPayment.class);
                        intent.putExtra(AppConstants.KEY_CONSTENT.PRICE, totalAmount);
                        intent.putExtra("vendorIdString", vendorIdString);
                        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.G_PAY_STRIPE);
                    } else {
                        DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.payment_declined), mActivity.getString(R.string.payment_declined_message), new OnDialogItemClickListener() {
                            @Override
                            public void onPositiveBtnClick() {

                            }

                            @Override
                            public void onNegativeBtnClick() {

                            }
                        });
                        getLoadingStateObserver().onChanged(false);
                        textViewPay.setEnabled(true);
                        textViewConfirmPurchase.setEnabled(true);
                    }
                } else {
                    // when api response != 200
                    getLoadingStateObserver().onChanged(false);
                    Toast.makeText(mActivity, "Something went wrong try again later", Toast.LENGTH_SHORT).show();
                    Log.e("create_order_error", "error case");
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                getLoadingStateObserver().onChanged(false);
                DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.user_details_error_info), new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {

                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });
                Log.e("create_order_error", "error case");
            }

            @Override
            public void onError(Throwable t) {
                getLoadingStateObserver().onChanged(false);
                DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.user_details_error_info), new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {

                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });
                Log.e("create_order_error", "error case");
            }
        });

    }

    private void getPaymentFailureApiGooglePay(String amount, String currency, String errorMessage) {

        PaymentStatusFailureModel paymentStatusRequest = new PaymentStatusFailureModel();

        if (screen.equalsIgnoreCase("shipping")) {

            PaymentStatusFailureModel.Ship_to ship_to = new PaymentStatusFailureModel.Ship_to();

            ship_to.setContact_name(parms.get(AppConstants.KEY_CONSTENT.CONTACT_NAME).toString());
            if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getEmail() != null)
                ship_to.setEmail(DataManager.getInstance().getUserDetails().getEmail());
            ship_to.setCity(parms.get(AppConstants.KEY_CONSTENT.CITY).toString());
            ship_to.setState(parms.get(AppConstants.KEY_CONSTENT.STATE).toString());
            ship_to.setPostal_code(parms.get(AppConstants.KEY_CONSTENT.ZIP_CODE).toString());
            ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
            ship_to.setStreet1(parms.get(AppConstants.KEY_CONSTENT.STEET1).toString());
            ship_to.setPhone(parms.get(AppConstants.KEY_CONSTENT.PHONE).toString());
            ship_to.setType("FEDEX");

            paymentStatusRequest.setShip_to(ship_to);
        }

        PaymentStatusFailureModel.Transaction transaction = new PaymentStatusFailureModel.Transaction();
//        transaction.setTransactionid("" + getTransactionId());
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
//        transaction.setFirstname("");
//        transaction.setLastname("");
//        transaction.setZip("");
//        transaction.setCardlastfourdigits(cardLastFourDigits);
        transaction.setWallet("google_wallet");
        if (screen.equalsIgnoreCase("shipping"))
            transaction.setShipping_choice("fedex");
        else
            transaction.setShipping_choice("pickup_deliver");

        paymentStatusRequest.setTransaction(transaction);

        Gson gson = new Gson();
        String json = gson.toJson(paymentStatusRequest);
        Log.e("FAILURE_REQUEST_JSON", "" + json);
        DataManager.getInstance().paymentFailure(paymentStatusRequest).enqueue(new NetworkCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                if (response.getCode() == 200) {

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("*****Done*****");
                            getLoadingStateObserver().onChanged(false);
                            DialogUtil.getInstance().CustomBottomSheetDialog(mActivity, mActivity.getString(R.string.transaction_failed), "" + errorMessage, new OnDialogItemClickListener() {
                                @Override
                                public void onPositiveBtnClick() {

                                }

                                @Override
                                public void onNegativeBtnClick() {

                                }
                            });
                            textViewPay.setEnabled(true);
                            textViewConfirmPurchase.setEnabled(true);
                        }
                    }, 10);

                } else {
                    // when api response != 200
                    getLoadingStateObserver().onChanged(false);
                    DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.user_details_error_info), new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {

                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });

                    textViewPay.setEnabled(true);
                    textViewConfirmPurchase.setEnabled(true);
                }
            }

            @Override
            public void onFailure(FailureResponse failureResponse) {
                getLoadingStateObserver().onChanged(false);
                DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.user_details_error_info), new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {

                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });

                textViewPay.setEnabled(true);
                textViewConfirmPurchase.setEnabled(true);
                Log.e("create_order_error", "error case");
            }

            @Override
            public void onError(Throwable t) {
                getLoadingStateObserver().onChanged(false);
                DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(mActivity, mActivity.getString(R.string.card_error), mActivity.getString(R.string.user_details_error_info), new OnDialogItemClickListener() {
                    @Override
                    public void onPositiveBtnClick() {

                    }

                    @Override
                    public void onNegativeBtnClick() {

                    }
                });

                textViewPay.setEnabled(true);
                textViewConfirmPurchase.setEnabled(true);
                Log.e("create_order_error", "error case");
            }
        });

    }

}
