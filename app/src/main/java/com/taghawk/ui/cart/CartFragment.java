package com.taghawk.ui.cart;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bluesnap.androidapi.views.activities.BluesnapCheckoutActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.adapters.CartListAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.bluesnap.BlueSnapPaymentCardActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.FragmentCartBinding;
import com.taghawk.firebase.FirebaseManager;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.PaymentStatusRequest;
import com.taghawk.model.ShopperIdResponse;
import com.taghawk.model.cart.CartDataBean;
import com.taghawk.model.cart.CartModel;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.chat.MessageModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.request.User;
import com.taghawk.stripe.Token;
import com.taghawk.ui.chat.MessagesDetailActivity;
import com.taghawk.ui.chat.MessagesDetailViewModel;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.ui.shipping.ShippingActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class CartFragment extends BaseFragment implements View.OnClickListener {

    private FragmentCartBinding mBinding;
    private CartViewModel mCartViewModel;
    private ArrayList<CartDataBean> mCartList = new ArrayList<>();
    private Activity mActivity;
    private CartListAdapter adapter;
    private int position;
    private MessagesDetailViewModel messagesDetailViewModel;
    private int cartItemCount = -1;
    private String shippingStatus = "";

    private String SHOPPER_ID = "SHOPPER_ID";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentCartBinding.inflate(inflater, container, false);
        initView();
        setUpCartList();
        return mBinding.getRoot();
    }

    // This function is use for Reyclerview initialization
    private void setUpCartList() {
        mBinding.rvCart.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = new CartListAdapter(mActivity, mCartList, this);
        mBinding.rvCart.setAdapter(adapter);
    }

    // Initialize views
    private void initView() {
        mActivity = getActivity();
        mCartList = new ArrayList<>();
        mBinding.header.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.header.tvTitle.setText(getString(R.string.cart));
        mBinding.header.ivCross.setOnClickListener(this);
        mBinding.tvCheckout.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messagesDetailViewModel = ViewModelProviders.of(this).get(MessagesDetailViewModel.class);
        messagesDetailViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mCartViewModel = ViewModelProviders.of(this).get(CartViewModel.class);
        mCartViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mCartViewModel.getCartListViewModel().observe(this, new Observer<CartModel>() {
            @Override
            public void onChanged(@Nullable CartModel cartModel) {
                getLoadingStateObserver().onChanged(false);
                if (cartModel.getCode() == 200) {
//                    if (mCartList != null) {
//                        mCartList.clear();
//                    }
                    mCartList.clear();
                    mCartList.addAll(cartModel.getmCartList());
                    if (mCartList.size() > 0) {
                        emptyPlaceHolder(View.VISIBLE, View.GONE, View.VISIBLE);
                        adapter.notifyDataSetChanged();
                        calculateTotalPrize(mCartList);
                    } else {
                        emptyPlaceHolder(View.GONE, View.VISIBLE, View.GONE);
                    }

                }

            }
        });

        mCartViewModel.getDeleteCartItemViewModel().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable final CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse.getCode() == 200) {
                    switch (commonResponse.getRequestCode()) {
                        case AppConstants.REQUEST_CODE.DELETE_PRODUCT_CART:
                            mCartList.remove(position);
                            adapter.notifyDataSetChanged();
                            if (mCartList.size() == 0) {
                                emptyPlaceHolder(View.GONE, View.VISIBLE, View.GONE);
                            } else {
                                calculateTotalPrize(mCartList);
                            }
                            break;
                        case AppConstants.REQUEST_CODE.PAYMENT:
//                            cartItemCount++;
//                            final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
//                            updateChatAfterPayment(commonResponse.getMessage(), mCartList.get(cartItemCount), user);
                            break;
                        case AppConstants.REQUEST_CODE.ZERO_PAYMENT:
                       /*     getCustomBottomDialog(mActivity.getString(R.string.congratulations_title), commonResponse.getMessage(), new OnDialogItemClickListener() {
                                @Override
                                public void onPositiveBtnClick() {*/
                            mActivity.setResult(Activity.RESULT_OK);
                            mActivity.finish();
                                   /* final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
                                    updateChatAfterPayment(commonResponse.getMessage(), mCartList.get(cartItemCount), user);*/
                           /*     }
                                @Override
                                public void onNegativeBtnClick() {
                                }
                            });*/
                            break;
                    }

                } else if (commonResponse.getCode() == 415) {
                    showRemoveItemFromCartDialog(commonResponse);
                }
            }
        });
        mCartViewModel.getSoldOutProductViewModel().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable final CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse.getCode() == 200) {

                    if(shippingStatus.equalsIgnoreCase("pay")) {
                        Intent intent = new Intent(mActivity, BlueSnapPaymentCardActivity.class);
                        intent.putExtra("screen", "cart");
                        intent.putExtra("totalAmount", getTotalAmount(mCartList));
                        intent.putExtra("cartList", mCartList);
                        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.BLUE_SNAP_PAY);
                    } else {
                        OpenShippingScreen();
                    }

                } else if (commonResponse.getCode() == 415) {
                    showRemoveItemFromCartDialog(commonResponse);
                }
            }
        });
        mCartViewModel.getVaultedShopperIdViewModel().observe(this, new Observer<ShopperIdResponse>() {
            @Override
            public void onChanged(@Nullable final ShopperIdResponse shopperIdResponse) {
                getLoadingStateObserver().onChanged(false);
                if (shopperIdResponse.getData() != null && shopperIdResponse.getData().size() > 0) {
                    PreferenceManager.getInstance(mActivity).putString(SHOPPER_ID, shopperIdResponse.getData().get(0).toString());
                } else {
//                    showRemoveItemFromCartDialog(commonResponse);
                }
            }
        });
        if (AppUtils.isInternetAvailable(mActivity))
            mCartViewModel.getCardList();
        else showNoNetworkError();

        if (AppUtils.isInternetAvailable(mActivity))
            mCartViewModel.getVaultedShopperId();
        else showNoNetworkError();
    }

    //This function is use for calculate total prize of all items added in cart
    private void calculateTotalPrize(ArrayList<CartDataBean> mCartList) {
        double totalPrize = 0.0;

        for (int i = 0; i < mCartList.size(); i++) {
            totalPrize = totalPrize + Double.valueOf(mCartList.get(i).getProductPrice());
        }
        mBinding.tvTotal.setText("$" + totalPrize);
        mBinding.tvCharges.setText("$" + totalPrize);
        if(mCartList.get(0).getShippingAvailibility() == 3) {
            mBinding.tvCheckout.setText("Add Shipping Info");
        } else{
            mBinding.tvCheckout.setText("Checkout");
        }
    }

    // THis function is use for showing dialog for item sold which is added in cart
    private void showRemoveItemFromCartDialog(CommonResponse commonResponse) {
        getCustomBottomDialog(getString(R.string.sold), commonResponse.getMessage(), new OnDialogItemClickListener() {
            @Override
            public void onPositiveBtnClick() {
                if (AppUtils.isInternetAvailable(mActivity))
                    mCartViewModel.getCardList();
                else showNoNetworkError();
            }

            @Override
            public void onNegativeBtnClick() {

            }
        });
    }

    //This function is use for showing empty message
    private void emptyPlaceHolder(int visible, int gone, int visibilty) {
        mBinding.nestedScrol.setVisibility(visible);
        mBinding.llEmptyPlaceHolder.setVisibility(gone);
        mBinding.includeEmptyPlaceHolder.tvEmptyMsg.setText(getString(R.string.cart_msg));
        mBinding.tvCheckout.setVisibility(visibilty);
        mBinding.tvPaymentNote.setVisibility(visibilty);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                ((CartActivity) mActivity).finish();
                break;
            case R.id.iv_bin:

                DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, getString(R.string.remove_item), getString(R.string.cart_delete_msg), getString(R.string.delete), getString(R.string.cencel), new DialogCallback() {
                    @Override
                    public void submit(String data) {
                        if (AppUtils.isInternetAvailable(mActivity)) {
                            position = (int) v.getTag();
                            mCartViewModel.addCart(mCartList.get(position).getProductId(), 0, AppConstants.REQUEST_CODE.DELETE_PRODUCT_CART);
                        } else showNoNetworkError();
                    }

                    @Override
                    public void cancel() {

                    }
                });

                break;
            case R.id.tv_checkout:
                if (AppUtils.isInternetAvailable(mActivity)) {
                    if (mCartList != null) {

                        if (Double.valueOf(mCartList.get(0).getProductPrice()) == 0) {
                            doZeroPayment();
                        } else if (mCartList.size() == 1) {
                            if (mCartList.get(0).getShippingAvailibility() == 3) {

                                shippingStatus = "shipping";
                                checkProductSoldOut();

                            } else {

                                shippingStatus = "pay";
                                checkProductSoldOut();

//                                Intent intent = new Intent(mActivity, GooglePayPayment.class);
//                                intent.putExtra(AppConstants.KEY_CONSTENT.PRICE, getTotalAmount(mCartList));
//                                new AlertDialog.Builder(getActivity())
//                                        .setTitle("Security Exception")
//                                        .setMessage("Our Online Payment system is unavailable while we add additional security  please contact the seller to discuss other payment solutions for now")
//                                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
////                                                chargeToken("5dd54505aaac1d1102faf52c");
//                                                doZeroPayment();
//                                            }
//                                        })
//                                        .create()
//                                        .show();
//                                startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.G_PAY_STRIPE);

                            }
                        } else {

                            shippingStatus = "pay";
                            checkProductSoldOut();

//                            Intent intent = new Intent(mActivity, GooglePayPayment.class);
//                            intent.putExtra(AppConstants.KEY_CONSTENT.PRICE, getTotalAmount(mCartList));
//                            startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.G_PAY_STRIPE);
//                            new AlertDialog.Builder(getActivity())
//                                    .setTitle("Security Exception")
//                                    .setMessage("Our Online Payment system is unavailable while we add additional security  please contact the seller to discuss other payment solutions for now")
//                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            chargeToken("5dd54505aaac1d1102faf52c");
//                                        }
//                                    })
//                                    .create()
//                                    .show();

                        }
//                        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
//                                .setTitleText("Payment Notice")
//                                .setContentText("Our Online Payment system is unavailable while we add additional security. Please contact the seller to discuss other payment solutions for now")
//                                .setConfirmText("CHAT")
//                                .showCancelButton(false)
//                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sDialog) {
//                                        doZeroPayment();
//                                        sDialog.cancel();
//
//                                    }
//                                })
//                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                    @Override
//                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                        sweetAlertDialog.cancel();
//
//                                    }
//                                })
//                                .show();
                    }

                } else showNoNetworkError();
                break;
            case R.id.ll_main:
                openProductDetails(v);
                break;
        }
    }

    // This function is use for open Add shipping Details Screen
    private void OpenShippingScreen() {
        Intent intent = new Intent(mActivity, ShippingActivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, mCartList.get(0));
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.SHIPPING_PAYMENT);
    }

    // This function is use for open Product Details
    private void openProductDetails(View v) {
        try {
            position = (int) v.getTag();
            Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
            intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, mCartList.get(position).getProductId());
            v.getContext().startActivity(intent);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.BLUE_SNAP_PAY:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        PaymentStatusRequest response = (PaymentStatusRequest) data.getExtras().getSerializable(AppConstants.BUNDLE_DATA);
                        Log.e("amount_get", "" + response.getAmount());

                        ArrayList<PaymentStatusRequest.Products> products = new ArrayList<>();
                        for(int i = 0; i < mCartList.size(); i++) {
                            PaymentStatusRequest.Products product = new PaymentStatusRequest.Products();
                            product.setPrice(mCartList.get(i).getProductPrice());
                            product.setProductId(mCartList.get(i).getProductId());
                            product.setSellerId(mCartList.get(i).getSellerId());
                            products.add(product);
                        }
                        response.setProducts(products);

//                        Token token = new Gson().fromJson(rawToken, Token.class);
//                        if (!TextUtils.isEmpty(transactionRequest)) {
                            chargeToken(response);
//                        }
                    }

                }
                break;
            case AppConstants.ACTIVITY_RESULT.SHIPPING_PAYMENT:
                if (resultCode == Activity.RESULT_OK) {
//                    Intent intent = new Intent(mActivity, GooglePayPayment.class);
//                    intent.putExtra(AppConstants.KEY_CONSTENT.PRICE, getTotalAmount(mCartList));
//                    startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.G_PAY_STRIPE);

                    shippingStatus = "pay";
                    checkProductSoldOut();

                }
                break;
        }

    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        if (failureResponse.getErrorCode() == 404) {
            if (mCartList == null || mCartList.size() == 0) {
                emptyPlaceHolder(View.GONE, View.VISIBLE, View.GONE);
            }
        }
    }

    // THis function is use for complete the payment
    private void chargeToken(PaymentStatusRequest transactionRequestJson) {

        if (AppUtils.isInternetAvailable(mActivity)) {

//            mCartViewModel.doPayment(mCartList.get(0).getProductPrice(), AppConstants.CURRENCY_USD, id, mCartList.get(0).getProductId(), mCartList.get(0).getSellerId());
            try {
                mCartViewModel.doPayment(transactionRequestJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else showNoNetworkError();
    }

    /*
     * THis function is use for payment of 0$ product
     * **/
    private void doZeroPayment() {
        if (AppUtils.isInternetAvailable(mActivity)) {
            mCartViewModel.doZeroPayment(getProductZeroJson(mCartList));
        } else showNoNetworkError();

    }


    private String getProductJson(ArrayList<CartDataBean> mCartList) {
        JSONArray productArray = new JSONArray();
        for (int i = 0; i < mCartList.size(); i++) {
            JSONObject productObject = new JSONObject();
            try {
                productObject.put(AppConstants.KEY_CONSTENT.PRICE, mCartList.get(i).getProductPrice());
                productObject.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, mCartList.get(i).getProductId());
                productObject.put(AppConstants.KEY_CONSTENT.SELLER_ID, mCartList.get(i).getSellerId());
                productArray.put(productObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return productArray.toString();
    }


    private JSONArray getProductJsonArray(ArrayList<CartDataBean> mCartList) {
        JSONArray productArray = new JSONArray();
        for (int i = 0; i < mCartList.size(); i++) {
            JSONObject productObject = new JSONObject();
            try {
                productObject.put(AppConstants.KEY_CONSTENT.PRICE, mCartList.get(i).getProductPrice());
                productObject.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, mCartList.get(i).getProductId());
                productObject.put(AppConstants.KEY_CONSTENT.SELLER_ID, mCartList.get(i).getSellerId());
                productArray.put(productObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return productArray;
    }

    private String getProductZeroJson(ArrayList<CartDataBean> mCartList) {
        JSONArray productArray = new JSONArray();
        for (int i = 0; i < mCartList.size(); i++) {
            JSONObject productObject = new JSONObject();
            try {
                productObject.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, mCartList.get(i).getProductId());
                productObject.put(AppConstants.KEY_CONSTENT.SELLER_ID, mCartList.get(i).getSellerId());
                productArray.put(productObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return productArray.toString();
    }

    private String getTotalAmount(ArrayList<CartDataBean> mCartList) {
        Double totalAmount = 0.0;
        if (mCartList != null) {
            for (int i = 0; i < mCartList.size(); i++) {
                totalAmount = totalAmount + Double.parseDouble(mCartList.get(i).getProductPrice());
            }
        }
        return String.valueOf(totalAmount);
    }

    private String getProductIdJson(ArrayList<CartDataBean> mCartList) {
        JSONArray productArray = new JSONArray();
        for (int i = 0; i < mCartList.size(); i++) {
            JSONObject productObject = new JSONObject();
            try {
//                productObject.put(AppConstants.KEY_CONSTENT.PRICE, mCartList.get(i).getProductPrice());
                productObject.put(AppConstants.KEY_CONSTENT.PRODUCT_ID, mCartList.get(i).getProductId());
//                productObject.put(AppConstants.KEY_CONSTENT.SELLER_ID, mCartList.get(i).getSellerId());
                productArray.put(productObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return productArray.toString();
    }

    // THis function is use to check products are sold out or ready for payment
    private void checkProductSoldOut() {

        if (AppUtils.isInternetAvailable(mActivity)) {

//            mCartViewModel.doPayment(mCartList.get(0).getProductPrice(), AppConstants.CURRENCY_USD, id, mCartList.get(0).getProductId(), mCartList.get(0).getSellerId());
            mCartViewModel.checkSoldOutProduct(getProductIdJson(mCartList));
        } else showNoNetworkError();
    }

}


