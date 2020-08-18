package com.taghawk.stripe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bluesnap.androidapi.http.BlueSnapHTTPResponse;
import com.bluesnap.androidapi.http.CustomHTTPParams;
import com.bluesnap.androidapi.http.HTTPOperationController;
import com.bluesnap.androidapi.models.BillingContactInfo;
import com.bluesnap.androidapi.models.CreditCardTypeResolver;
import com.bluesnap.androidapi.models.PriceDetails;
import com.bluesnap.androidapi.models.SDKConfiguration;
import com.bluesnap.androidapi.models.SdkRequest;
import com.bluesnap.androidapi.models.SdkRequestBase;
import com.bluesnap.androidapi.models.SdkResult;
import com.bluesnap.androidapi.models.ShippingContactInfo;
import com.bluesnap.androidapi.models.ShopperCheckoutRequirements;
import com.bluesnap.androidapi.models.SupportedPaymentMethods;
import com.bluesnap.androidapi.services.BSPaymentRequestException;
import com.bluesnap.androidapi.services.BlueSnapService;
import com.bluesnap.androidapi.services.BluesnapAlertDialog;
import com.bluesnap.androidapi.utils.JsonParser;
import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;
import com.bluesnap.androidapi.views.activities.CreditCardActivity;
import com.bluesnap.androidapi.views.activities.WebViewActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.CardInfo;
import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodToken;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.bluesnap.BlueSnapDetails;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.util.DialogUtil;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_CREATE_TRANSACTION;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_PASS;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_URL;
import static com.taghawk.bluesnap.BlueSnapDetails.SANDBOX_USER;

//import com.stripe.model.Token;

public class GooglePayPayment extends BaseActivity {

    private double totalAmount = 0d;
    private String vaultedShopperId = "";
    private String vendorIdString = "";
    private String SHOPPER_ID = "SHOPPER_ID";
    public final List<Integer> SUPPORTED_METHODS = Arrays.asList(WalletConstants.PAYMENT_METHOD_CARD, WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD);
    public final String GATEWAY_TOKENIZATION_NAME = "bluesnap";
    protected SdkRequestBase sdkRequest;
    protected SDKConfiguration sdkConfiguration;
    protected PaymentsClient googlePayClient;
    private boolean showGooglePay;
    private static final int GOOGLE_PAY_PAYMENT_DATA_REQUEST_CODE = 991;
    protected final BlueSnapService blueSnapService = BlueSnapService.getInstance();


    @Override
    protected int getResourceId() {
        return R.layout.activity_pay_with_google;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pay_with_google);

        if (getIntent() != null) {
            totalAmount = getIntent().getExtras().getDouble(AppConstants.KEY_CONSTENT.PRICE);
            vendorIdString = getIntent().getExtras().getString("vendorIdString");
            Log.e("vendorIdString", "" + vendorIdString);
        }

        if(PreferenceManager.getInstance(this).getString(SHOPPER_ID) != null && !TextUtils.isEmpty(PreferenceManager.getInstance(this).getString(SHOPPER_ID)))
            vaultedShopperId = PreferenceManager.getInstance(this).getString(SHOPPER_ID);

        Log.e("vaultedShopperId", "" + vaultedShopperId);

        sdkRequest = blueSnapService.getSdkRequest();
        sdkConfiguration = blueSnapService.getsDKConfiguration();

        checkIsGooglePayAvailable();


    }

    private void checkIsGooglePayAvailable() {


        // It's recommended to create the PaymentsClient object inside of the onCreate method.
        googlePayClient = createPaymentsClient(this);
        if (googlePayClient == null) {
//            setGooglePayAvailable(false);
//            Toast.makeText(GooglePayPayment.this, "Google Pay not available", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("paymentStatus", 0);
            resultIntent.putExtra("errorString", "Google Pay not available, please try to use a different payment method");
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
            // OnCompleteListener to be triggered when the result of the call is known.
            isReadyToPay(googlePayClient).addOnCompleteListener(
                    new OnCompleteListener<Boolean>() {
                        public void onComplete(Task<Boolean> task) {
                            try {
                                boolean result = task.getResult(ApiException.class);
                                if(result)
                                    startGooglePayActivityForResult();
                                else{
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("paymentStatus", 0);
                                    resultIntent.putExtra("errorString", "Google Pay not available, please try to use a different payment method");
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                }
                            } catch (ApiException exception) {
                                // Process error
                                Log.w("TAG", "isReadyToPay failed", exception);
//                                setGooglePayAvailable(false);
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("paymentStatus", 0);
                                resultIntent.putExtra("errorString", "Google Pay not available, please try to use a different payment method");
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                            }
                        }
                    });
        }
    }

    /**
     * start GooglePay Activity For Result
     */
    protected void startGooglePayActivityForResult() {

        Log.d("TAG", "start GooglePay flow");

        // Disables the button to prevent multiple clicks.
//        LinearLayout googlePayButton = findViewById(R.id.googlePayButton);
//        if (googlePayButton != null) {
//            googlePayButton.setClickable(false);
//        }

        Task<PaymentData> futurePaymentData = createPaymentDataRequest(googlePayClient);

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        AutoResolveHelper.resolveTask(futurePaymentData, this, GOOGLE_PAY_PAYMENT_DATA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "got result " + resultCode);
        Log.d("TAG", "got request " + requestCode);
        switch (requestCode) {
            case GOOGLE_PAY_PAYMENT_DATA_REQUEST_CODE: {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getLoadingStateObserver().onChanged(true);
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handleGooglePaySuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e("ACTIVITY_RESULT", "activity result cancelled case");
                        // Nothing to here normally - the user simply cancelled without selecting a
                        // payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        Log.e("ACTIVITY_RESULT", "activity result time error case");
                        handleGooglePayError(status.getStatusCode());
                        break;
                }

                // Re-enables the Pay with Google button.
//                LinearLayout googlePayButton = findViewById(R.id.googlePayButton);
//                if (googlePayButton != null) {
//                    googlePayButton.setClickable(true);
//                }
                break;
            }
            case CreditCardActivity.CREDIT_CARD_ACTIVITY_REQUEST_CODE: {
                if (resultCode != Activity.RESULT_CANCELED) {
                    setResult(resultCode, data);
                    finish();
                }
                break;
            }
            case CreditCardActivity.CREDIT_CARD_ACTIVITY_DEFAULT_REQUEST_CODE: {
                setResult(resultCode, data);
                finish();
                break;
            }
            case WebViewActivity.PAYPAL_REQUEST_CODE: {
                if (resultCode != Activity.RESULT_CANCELED) {
                    setResult(resultCode, data);
                    finish();
                }
                break;
            }
            default: {
            }

        }

    }


    /**
     * In case of success from the Google-Pay button, we create the token we will need
     * to send to BlueSnap to actually create the payment transaction
     *
     * @param paymentData
     */
    private void handleGooglePaySuccess(PaymentData paymentData) {

        SdkResult sdkResult = createSDKResult(paymentData);
        String encodedToken = sdkResult == null ? null : sdkResult.getGooglePayToken();
        if (encodedToken == null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("paymentStatus", 2);
            resultIntent.putExtra("errorString", "Error handling Google Pay, please try again or use a different payment method");
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            blueSnapService.getAppExecutors().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    // post the token
                    try {
//                        BlueSnapHTTPResponse response = BlueSnapService.getInstance().submitTokenenizedPayment(encodedToken, SupportedPaymentMethods.GOOGLE_PAY);

                        //TODO: I'm just a string but please don't make me look that bad..Use String.format
//                        String body = "{\n" +
//                                "   \"cardTransactionType\": \"AUTH_CAPTURE\",\n" +
//                                "    \"softDescriptor\": \"DescTest\",\n" +
//                                "    \"amount\": " + totalAmount + ",\n" +
//                                "    \"currency\": \"USD\",\n";
//                        if(vaultedShopperId != null && !TextUtils.isEmpty(vaultedShopperId))
//                            body = body + "    \"vaultedShopperId\": " + vaultedShopperId + ",\n";
//                        body = body + "    \"wallet\": {\n" +
//                                "      \"walletType\": \"GOOGLE_PAY\",\n" +
//                                "      \"encodedPaymentToken\": \"" + encodedToken + "\"\n" +
//                                "    }\n" +
//                                "}";

                        String body = "<card-transaction xmlns=\"http://ws.plimus.com\">\n" +
                                "  <card-transaction-type>AUTH_CAPTURE</card-transaction-type>\n" +
                                "  <soft-descriptor>DescTest</soft-descriptor>\n" +
                                "  <amount>" + totalAmount + "</amount>\n" +
                                "  <currency>USD</currency>\n";
                        if(vaultedShopperId != null && !TextUtils.isEmpty(vaultedShopperId))
                            body = body + "  <vaulted-shopper-id>"+ vaultedShopperId +"</vaulted-shopper-id>\n";
                        body = body + "  <wallet>\n" +
                                "    <wallet-type>GOOGLE_PAY</wallet-type>\n" +
                                "    <encoded-payment-token>" + encodedToken +"</encoded-payment-token>\n" +
                                "  </wallet>\n" +
                                vendorIdString +
                                "</card-transaction>";

                        Log.e("REQUEST", "" + body);

                        String basicAuth = "Basic " + Base64.encodeToString((SANDBOX_USER + ":" + SANDBOX_PASS).getBytes(StandardCharsets.UTF_8), 0);
                        List<CustomHTTPParams> headerParams = new ArrayList<>();
                        headerParams.add(new CustomHTTPParams("Authorization", basicAuth));
                        BlueSnapHTTPResponse response = HTTPOperationController.post(SANDBOX_URL + SANDBOX_CREATE_TRANSACTION, body, "application/xml", "application/xml", headerParams);
                        Log.e("API", SANDBOX_URL + SANDBOX_CREATE_TRANSACTION);
                        Log.e("API_HEADER", "Authorization : " + basicAuth);
                        Log.e("API_PARAMS", "" + body);
                        String responseString = response.getResponseString();
                        Log.e("API_RESPONSE", "" + responseString);

                        if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            Log.d("TAG", "GPay token submitted successfully");

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("responseString", responseString);
                            resultIntent.putExtra("paymentStatus", 1);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();

                        } else {
                            String errorMsg = String.format("Service Error %s, %s", response.getResponseCode(), response.getResponseString());
                            Log.e("TAG", errorMsg);
//                            showDialogInUIThread("Error handling GPay, please try again or use a different payment method", "Error");

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("paymentStatus", 2);
                            resultIntent.putExtra("errorString", "Error handling Google Pay, please try again or use a different payment method");
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();

                        }
                    } catch (Exception e) {
                        Log.e("TAG", "Error submitting GPay details", e);
//                        showDialogInUIThread("Error handling GPay, please try again or use a different payment method", "Error");

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("paymentStatus", 2);
                        resultIntent.putExtra("errorString", "Error handling Google Pay, please try again or use a different payment method");
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                }
            });
        }
    }

    private void handleGooglePayError(int statusCode) {
        // At this stage, the user has already seen a popup informing them an error occurred.
        // Normally, only logging is required.
        // statusCode will hold the value of any constant from CommonStatusCode or one of the
        // WalletConstants.ERROR_CODE_* constants.
        Log.w("TAG", "loadPaymentData failed; " + String.format("Error code: %d", statusCode));
//        showDialogInUIThread("GPay service error", "Error");

        Intent resultIntent = new Intent();
        resultIntent.putExtra("paymentStatus", 0);
        resultIntent.putExtra("errorString", "Google Pay service error, please try again or use a different payment method");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }

    private void showDialogInUIThread(String message, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BluesnapAlertDialog.setDialog(GooglePayPayment.this, message, title);
            }
        });
    }

    public PaymentsClient createPaymentsClient(Activity activity) {

        // check that Google Play is available
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        final int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity.getBaseContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            boolean isUserResolvableError = googleApiAvailability.isUserResolvableError(resultCode);
            Log.i("TAG", "Google Play not available; resultCode=" + resultCode + ", isUserResolvableError=" + isUserResolvableError);
            return null;
        }

//        BlueSnapService blueSnapService = BlueSnapService.getInstance();
        SdkRequest sdkRequest = new SdkRequest(totalAmount, "USD", false, false, false);
//        sdkRequest.setGooglePayTestMode(true);
        sdkRequest.setGooglePayActive(true);
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

            DialogUtil.getInstance().CustomBottomSheetJustifiedDialog(this, getString(R.string.card_error), getString(R.string.card_error_info), new OnDialogItemClickListener() {
                @Override
                public void onPositiveBtnClick() {
                    finish();
                }

                @Override
                public void onNegativeBtnClick() {

                }
            });

        }
        int googlePayMode = WalletConstants.ENVIRONMENT_PRODUCTION;
        if (sdkRequest.isGooglePayTestMode()) {
            googlePayMode = WalletConstants.ENVIRONMENT_TEST;
        }
        // Create the client
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(googlePayMode)
                .build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    public Task<Boolean> isReadyToPay(PaymentsClient client) {

        IsReadyToPayRequest.Builder request = IsReadyToPayRequest.newBuilder();
        for (Integer allowedMethod : SUPPORTED_METHODS) {
            request.addAllowedPaymentMethod(allowedMethod);
        }
        Task<Boolean> readyToPay = client.isReadyToPay(request.build());
        return readyToPay;
    }

    public Task<PaymentData> createPaymentDataRequest(PaymentsClient googlePayClient) {

        BlueSnapService blueSnapService = BlueSnapService.getInstance();
        SdkRequestBase sdkRequest = blueSnapService.getSdkRequest();
        String merchantId = BlueSnapDetails.SANDBOX_MERCHANT_ID;
        if (merchantId == null) {
            Log.e("TAG", "Missing merchantId from SDK init data");
            return null;
        }

        List<Pair<String, String>> GATEWAY_TOKENIZATION_PARAMETERS = Arrays.asList(
                Pair.create("gatewayMerchantId", merchantId)
        );

        PaymentMethodTokenizationParameters.Builder paramsBuilder =
                PaymentMethodTokenizationParameters.newBuilder()
                        .setPaymentMethodTokenizationType(
                                WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                        .addParameter("gateway", GATEWAY_TOKENIZATION_NAME);
        for (Pair<String, String> param : GATEWAY_TOKENIZATION_PARAMETERS) {
            paramsBuilder.addParameter(param.first, param.second);
        }

        PaymentDataRequest request = createPaymentDataRequest(paramsBuilder.build(), sdkRequest);
        Task<PaymentData> futurePaymentData = googlePayClient.loadPaymentData(request);
        return futurePaymentData;
    }

    private PaymentDataRequest createPaymentDataRequest(PaymentMethodTokenizationParameters params, SdkRequestBase sdkRequest) {

//        final PriceDetails priceDetails = sdkRequest.getPriceDetails();
        // AS-149: Google Pay price does not allow more than 2 digits after the decimal point
        String price = String.format("%.2f", totalAmount);
        TransactionInfo transactionInfo = createTransaction(price, "USD");

        final List merchantPaymentMethods = getMerchantPaymentMethods();

        ShopperCheckoutRequirements shopperCheckoutRequirements = new ShopperCheckoutRequirements(false, false, false);
        PaymentDataRequest request =
                PaymentDataRequest.newBuilder()
                        .setPhoneNumberRequired(shopperCheckoutRequirements.isShippingRequired())
                        .setEmailRequired(shopperCheckoutRequirements.isEmailRequired())
                        .setShippingAddressRequired(shopperCheckoutRequirements.isShippingRequired())

                        .setTransactionInfo(transactionInfo)
                        .addAllowedPaymentMethods(SUPPORTED_METHODS)
                        .setCardRequirements(
                                CardRequirements.newBuilder()
                                        .addAllowedCardNetworks(merchantPaymentMethods)
                                        .setAllowPrepaidCards(false) // todo: need to find out wehat this means
                                        .setBillingAddressRequired(true)

                                        // Omitting this parameter will result in the API returning
                                        // only a "minimal" billing address (post code only).
                                        .setBillingAddressFormat(shopperCheckoutRequirements.isBillingRequired() ? WalletConstants.BILLING_ADDRESS_FORMAT_FULL : WalletConstants.BILLING_ADDRESS_FORMAT_MIN)
                                        .build())
                        .setPaymentMethodTokenizationParameters(params)
                        .setUiRequired(true)
                        .build();

        return request;
    }

    public TransactionInfo createTransaction(String price, String currency) {
        return TransactionInfo.newBuilder()
                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_ESTIMATED)
                .setTotalPrice(price)
                .setCurrencyCode(currency)
                .build();
    }

    private List<Integer> getMerchantPaymentMethods() {

        ArrayList<String> creditCardBrands = BlueSnapService.getInstance().getsDKConfiguration().getSupportedPaymentMethods().getCreditCardBrands();

        List<Integer> supportedNetworks = new java.util.ArrayList<>();

        for (String ccBrand : creditCardBrands) {
            if (ccBrand.equalsIgnoreCase(CreditCardTypeResolver.VISA)) {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_VISA);
            } else if (ccBrand.equalsIgnoreCase(CreditCardTypeResolver.AMEX)) {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_AMEX);
            } else if (ccBrand.equalsIgnoreCase(CreditCardTypeResolver.DISCOVER)) {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_DISCOVER);
            } else if (ccBrand.equalsIgnoreCase(CreditCardTypeResolver.JCB)) {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_JCB);
            } else if (ccBrand.equalsIgnoreCase(CreditCardTypeResolver.MASTERCARD)) {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_MASTERCARD);
            } else {
                supportedNetworks.add(WalletConstants.CARD_NETWORK_OTHER);
            }
        }

        return supportedNetworks;
    }

    public SdkResult createSDKResult(PaymentData paymentData) {

        // PaymentMethodToken contains the payment information, as well as any additional
        // requested information, such as billing and shipping address.
        // Refer to your processor's documentation on how to proceed from here.
        PaymentMethodToken token = paymentData.getPaymentMethodToken();

        SdkResult sdkResult = null;
        try {
            String encodedToken = createBlsTokenFromGooglePayPaymentData(paymentData);
            Log.d("TAG", "paymentData encoded as Token for BlueSnap");
            Log.d("TAG", encodedToken);

            sdkResult = BlueSnapService.getInstance().getSdkResult();
            sdkResult.setChosenPaymentMethodType(SupportedPaymentMethods.GOOGLE_PAY);
            sdkResult.setGooglePayToken(encodedToken);

            final UserAddress billingAddress = paymentData.getCardInfo().getBillingAddress();
            if (billingAddress != null) {
                BillingContactInfo billingContactInfo = new BillingContactInfo();
                billingContactInfo.setEmail(billingAddress.getEmailAddress());
                billingContactInfo.setAddress(billingAddress.getAddress1());
                billingContactInfo.setAddress2(billingAddress.getAddress2());
                billingContactInfo.setCity(billingAddress.getLocality());
                billingContactInfo.setCountry(billingAddress.getCountryCode());
                billingContactInfo.setFullName(billingAddress.getName());
                billingContactInfo.setState(billingAddress.getAdministrativeArea());
                billingContactInfo.setZip(billingAddress.getPostalCode());
                sdkResult.setBillingContactInfo(billingContactInfo);
            }

            final UserAddress shippingAddress = paymentData.getShippingAddress();
            if (shippingAddress != null) {
                ShippingContactInfo shippingContactInfo = new ShippingContactInfo();
                shippingContactInfo.setPhone(shippingAddress.getPhoneNumber());
                shippingContactInfo.setAddress(shippingAddress.getAddress1());
                shippingContactInfo.setAddress2(shippingAddress.getAddress2());
                shippingContactInfo.setCity(shippingAddress.getLocality());
                shippingContactInfo.setCountry(shippingAddress.getCountryCode());
                shippingContactInfo.setFullName(shippingAddress.getName());
                shippingContactInfo.setState(shippingAddress.getAdministrativeArea());
                shippingContactInfo.setZip(shippingAddress.getPostalCode());
                sdkResult.setShippingContactInfo(shippingContactInfo);
            }

        } catch (Exception e) {
            Log.e("TAG", "Error encoding payment data into BlueSnap token", e);
        }
        return sdkResult;
    }

    /**
     * Creates a base64 encoded token with the PaymentData
     */
    public String createBlsTokenFromGooglePayPaymentData(PaymentData paymentData) throws Exception {

        final CardInfo cardInfo = paymentData.getCardInfo();

        JSONObject result = new JSONObject();

        // paymentMethodData
        JSONObject paymentMethodData = new JSONObject();

        // paymentMethodData -> description: A payment method and method identifier suitable for communication to a shopper in a confirmation screen or purchase receipt.
        final String description = cardInfo.getCardDescription();
        if (description != null) {
            paymentMethodData.put("description", description);
        }

        // paymentMethodData -> tokenizationData
        final PaymentMethodToken paymentMethodToken = paymentData.getPaymentMethodToken();
        JSONObject tokenizationData = new JSONObject();
        tokenizationData.put("type", paymentMethodToken.getPaymentMethodTokenizationType());
        tokenizationData.put("token", paymentMethodToken.getToken());
        paymentMethodData.put("tokenizationData", tokenizationData);

        // paymentMethodData -> info
        JSONObject info = new JSONObject();
        paymentMethodData.put("info", info);

        // paymentMethodData -> info -> cardNetwork
        final String cardNetwork = cardInfo.getCardNetwork();
        if (cardNetwork != null) {
            info.put("cardNetwork", cardNetwork);
        }

        // paymentMethodData -> info -> cardDetails
        final String cardDetails = cardInfo.getCardDetails();
        if (cardDetails != null) {
            info.put("cardDetails", cardDetails);
        }

        // paymentMethodData -> info -> cardClass (1-3 or 0, should somehow translate to DEBIT/CREDIT)
        final int cardClassCode = cardInfo.getCardClass();
        String cardClass = null;
        if (cardClassCode == WalletConstants.CARD_CLASS_CREDIT) {
            cardClass = "CREDIT";
        } else if (cardClassCode == WalletConstants.CARD_CLASS_DEBIT) {
            cardClass = "DEBIT";
        } else if (cardClassCode == WalletConstants.CARD_CLASS_PREPAID) {
            cardClass = "PREPAID";
        }
        if (cardClass != null) {
            info.put("cardClass", cardClass);
        }
        // paymentMethodData -> info -> billingAddress
        final JSONObject billingAddressJson = getUserAddressAsJson(cardInfo.getBillingAddress());
        if (billingAddressJson != null) {
            info.put("billingAddress", billingAddressJson);
        }

        result.put("paymentMethodData", paymentMethodData);

        // email
        final String email = paymentData.getEmail();
        if (email != null) {
            result.put("email", email);
        }

        // googleTransactionId - not sure this is the right place in the json for it
        final String googleTransactionId = paymentData.getGoogleTransactionId();
        if (googleTransactionId != null) {
            result.put("googleTransactionId", googleTransactionId);
        }
        // shippingAddress
        final JSONObject shippingAddressJson = getUserAddressAsJson(paymentData.getShippingAddress());
        if (shippingAddressJson != null) {
            result.put("shippingAddress", shippingAddressJson);
        }

        String tokenForBls = result.toString();
        String encodedToken = Base64.encodeToString(tokenForBls.getBytes(), Base64.NO_WRAP | Base64.URL_SAFE);

        return encodedToken;
    }

    private JSONObject getUserAddressAsJson(UserAddress userAddress) throws Exception {
        JSONObject res = null;
        if (userAddress != null) {
            res = new JSONObject();
            JsonParser.putJSONifNotNull(res, "name", userAddress.getName());
            JsonParser.putJSONifNotNull(res,"postalCode", userAddress.getPostalCode());
            JsonParser.putJSONifNotNull(res,"countryCode", userAddress.getCountryCode());
            JsonParser.putJSONifNotNull(res,"phoneNumber", userAddress.getPhoneNumber());
            JsonParser.putJSONifNotNull(res,"companyName", userAddress.getCompanyName());
            JsonParser.putJSONifNotNull(res,"emailAddress", userAddress.getEmailAddress());
            JsonParser.putJSONifNotNull(res,"address1", userAddress.getAddress1());
            JsonParser.putJSONifNotNull(res,"address2", userAddress.getAddress2());
            JsonParser.putJSONifNotNull(res,"address3", userAddress.getAddress3());
            JsonParser.putJSONifNotNull(res,"address4", userAddress.getAddress4());
            JsonParser.putJSONifNotNull(res,"address5", userAddress.getAddress5());
            // A country subdivision (e.g. state or province)
            JsonParser.putJSONifNotNull(res,"administrativeArea", userAddress.getAdministrativeArea());
            // City, town, neighborhood, or suburb.
            JsonParser.putJSONifNotNull(res,"locality", userAddress.getLocality());
            JsonParser.putJSONifNotNull(res,"sortingCode", userAddress.getSortingCode());
        }
        return res;
    }

}
