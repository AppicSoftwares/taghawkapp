package com.taghawk.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.AdapterPaymentHistoryBinding;
import com.taghawk.model.PaymentHistoryData;
import com.taghawk.util.AppUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<PaymentHistoryData> mList;
    private View.OnClickListener listener;

    public PaymentHistoryAdapter(ArrayList<PaymentHistoryData> mList, View.OnClickListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterPaymentHistoryBinding mBinding = AdapterPaymentHistoryBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new PaymentHistoryViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        PaymentHistoryViewHolder holder = null;
        holder = (PaymentHistoryViewHolder) viewHolder;
        holder.bind(mList.get(i));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void setDeliveryStatusAction(AdapterPaymentHistoryBinding mBinding, PaymentHistoryData bean, boolean isSeller) {
        mBinding.tvAction.setVisibility(View.GONE);
        mBinding.llActionButton.setVisibility(View.GONE);

        switch (bean.getDeliveryStatus()) {
            case AppConstants.PAYMENT_REFUND_STATUS.PENDING:
                if (isSeller) {
//                    mBinding.llActionButton.setVisibility(View.GONE);
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getContext().getString(R.string.payment_pending));
                    mBinding.tvCurrentStatus.setText(R.string.pending);
                } else {
//                    mBinding.llActionButton.setVisibility(View.VISIBLE);
                    mBinding.tvCurrentStatus.setText(R.string.pending);
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.buyer_confirm_item));
                    mBinding.tvRefund.setText(mBinding.tvReleasePayment.getResources().getString(R.string.refund));
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.release_payment));
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.ITEM_DELEVER:
            case AppConstants.PAYMENT_REFUND_STATUS.COMPLETED:
//                mBinding.llActionButton.setVisibility(View.GONE);

                if (isSeller) {
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.sold));
                } else {
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.sold));
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.REQUEST_FOR_REFUND:
//                mBinding.llActionButton.setVisibility(View.VISIBLE);
                if (isSeller) {
                    mBinding.tvRefund.setVisibility(View.VISIBLE);
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.refund_requested));
                    mBinding.tvRefund.setText(mBinding.tvReleasePayment.getResources().getString(R.string.decline));
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.accept));
                } else {
//                    mBinding.llActionButton.setVisibility(View.VISIBLE);
                    mBinding.tvRefund.setVisibility(View.INVISIBLE);
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.refund_requested));
                    mBinding.tvReleasePayment.setVisibility(View.VISIBLE);
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.cancel_refund));
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.REFUND_ACCEPTED:

                if (isSeller) {
//                    mBinding.llActionButton.setVisibility(View.VISIBLE);
                    mBinding.tvCurrentStatus.setText("Release refund to the buyer after you received the return item.");
                    mBinding.tvRefund.setText(mBinding.tvReleasePayment.getResources().getString(R.string.dispute));
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.release_refund));

                } else {
//                    mBinding.llActionButton.setVisibility(View.GONE);
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.return_accepted_payment_pending));
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.REFUND_SUCCESS:
//                mBinding.llActionButton.setVisibility(View.GONE);
                if (isSeller) {
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.payment_released));
                } else {
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.refund_success));
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_STARTED:

                if (isSeller) {
//                    mBinding.llActionButton.setVisibility(View.VISIBLE);
                    mBinding.tvRefund.setText(mBinding.tvRefund.getResources().getString(R.string.submit_response));
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.accept_refund));
                    mBinding.tvCurrentStatus.setText("Submit your response to the dispute or accept the refund.");
                } else {
//                    mBinding.llActionButton.setVisibility(View.VISIBLE);
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.dispute_started));
                    mBinding.tvRefund.setVisibility(View.VISIBLE);
                    mBinding.tvRefund.setText(mBinding.tvRefund.getResources().getString(R.string.cancel_dispute));
//                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.dispute_started));
                    mBinding.tvReleasePayment.setVisibility(View.INVISIBLE);
//                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.cancel_refund));
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_RESPONSE:
//                mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.dispute_started));
                if (isSeller) {
//                    mBinding.llActionButton.setVisibility(View.VISIBLE);
//                    mBinding.tvRefund.setText(mBinding.tvRefund.getResources().getString(R.string.submit_response));
                    mBinding.tvRefund.setVisibility(View.INVISIBLE);
                    mBinding.tvReleasePayment.setVisibility(View.VISIBLE);
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.accept_refund));
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.seller_submit_statement));
                } else {
//                    mBinding.llActionButton.setVisibility(View.GONE);
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.seller_submit_statement));
                    mBinding.tvRefund.setVisibility(View.INVISIBLE);
                    mBinding.tvReleasePayment.setVisibility(View.VISIBLE);
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.cancel_dispute));
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_COMPLETED:
//                mBinding.llActionButton.setVisibility(View.GONE);
                mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.dispute_closed));
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.SELLER_STATEMENT_DONE:
//                mBinding.llActionButton.setVisibility(View.VISIBLE);
                if (isSeller) {
                    mBinding.tvRefund.setVisibility(View.VISIBLE);
                    mBinding.tvRefund.setText(mBinding.tvRefund.getResources().getString(R.string.cancel_dispute));
//                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.seller_submit_statement));
                    mBinding.tvReleasePayment.setVisibility(View.INVISIBLE);
//                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.release_refund));
                } else {
//                    mBinding.llActionButton.setVisibility(View.VISIBLE);
                    mBinding.tvCurrentStatus.setText("Submit your response to the dispute or release the payment.");
                    mBinding.tvRefund.setText(mBinding.tvRefund.getResources().getString(R.string.submit_response));
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.release_payment));
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_RESPONSE:
//                mBinding.llActionButton.setVisibility(View.VISIBLE);
                if (isSeller) {
//                    mBinding.tvRefund.setVisibility(View.INVISIBLE);
                    mBinding.tvRefund.setVisibility(View.VISIBLE);
                    mBinding.tvRefund.setText(mBinding.tvRefund.getResources().getString(R.string.cancel_dispute));
                    mBinding.tvReleasePayment.setVisibility(View.INVISIBLE);
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.seller_submit_statement));
//                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.release_refund));
                } else {
//                    mBinding.llActionButton.setVisibility(View.VISIBLE);
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.seller_submit_statement));
                    mBinding.tvRefund.setVisibility(View.INVISIBLE);
                    mBinding.tvRefund.setVisibility(View.VISIBLE);
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.release_payment));
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.DECLINED:
                if (isSeller) {
//                    mBinding.llActionButton.setVisibility(View.GONE);
//                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.release_refund));
//                    mBinding.tvAction.setVisibility(View.VISIBLE);
//                    mBinding.tvAction.setText(mBinding.tvReleasePayment.getResources().getString(R.string.dispute));
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.decline_refund_for_this_product));
                } else {
//                    mBinding.llActionButton.setVisibility(View.VISIBLE);
//                    mBinding.tvRefund.setVisibility(View.VISIBLE);
                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.decline_refund_decline));
                    mBinding.tvRefund.setText(mBinding.tvRefund.getResources().getString(R.string.dispute));
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.release_payment));
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.DISPUTE_CAN_START:
                if (isSeller) {
//                    mBinding.llActionButton.setVisibility(View.GONE);
//                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.decline_refund_for_this_product));
                    mBinding.tvRefund.setVisibility(View.VISIBLE);
                    mBinding.tvRefund.setText(mBinding.tvRefund.getResources().getString(R.string.open_a_dispute));
                    mBinding.tvReleasePayment.setVisibility(View.INVISIBLE);
//                    mBinding.tvAction.setVisibility(View.VISIBLE);
//                    mBinding.tvAction.setText(mBinding.tvReleasePayment.getResources().getString(R.string.dispute));
                } else {
//                    mBinding.llActionButton.setVisibility(View.VISIBLE);
//                    mBinding.tvRefund.setVisibility(View.VISIBLE);
//                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.decline_refund_decline));
                    mBinding.tvRefund.setText(mBinding.tvRefund.getResources().getString(R.string.refund));
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.release_payment));
                }
                break;//done

            case AppConstants.PAYMENT_REFUND_STATUS.REFUND_DISPUTE_CAN_START:
                if (isSeller) {
//                    mBinding.llActionButton.setVisibility(View.GONE);
                    mBinding.tvRefund.setVisibility(View.INVISIBLE);
                    mBinding.tvReleasePayment.setVisibility(View.VISIBLE);
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.dispute));
//                    mBinding.tvAction.setVisibility(View.VISIBLE);
//                    mBinding.tvAction.setText(mBinding.tvReleasePayment.getResources().getString(R.string.dispute));
//                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.decline_refund_for_this_product));
                } else {
//                    mBinding.llActionButton.setVisibility(View.VISIBLE);
//                    mBinding.tvRefund.setVisibility(View.VISIBLE);
//                    mBinding.tvCurrentStatus.setText(mBinding.tvCurrentStatus.getResources().getString(R.string.decline_refund_decline));
                    mBinding.tvRefund.setText(mBinding.tvRefund.getResources().getString(R.string.dispute));
                    mBinding.tvReleasePayment.setText(mBinding.tvReleasePayment.getResources().getString(R.string.release_payment));
                }
                break;//done

        }
    }

    private class PaymentHistoryViewHolder extends RecyclerView.ViewHolder {
        private AdapterPaymentHistoryBinding mBinding;

        public PaymentHistoryViewHolder(AdapterPaymentHistoryBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            mBinding.tvRefund.setOnClickListener(listener);
            mBinding.tvReleasePayment.setOnClickListener(listener);
            mBinding.llMain.setOnClickListener(listener);
            mBinding.ivChat.setOnClickListener(listener);
        }

        public void bind(PaymentHistoryData bean) {
            mBinding.ivChat.setTag(getAdapterPosition());
            mBinding.tvRefund.setTag(getAdapterPosition());
            mBinding.tvReleasePayment.setTag(getAdapterPosition());
            mBinding.llMain.setTag(getAdapterPosition());
            mBinding.tvProductName.setText(bean.getProductName());
            mBinding.tvDate.setText(AppUtils.timestampToStringDate(bean.getPurchasedDate()));

            if (bean.getImageLists() != null && bean.getImageLists().size() > 0)
                Glide.with(mBinding.ivProduct.getContext()).asBitmap().load(bean.getImageLists().get(0).getUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(mBinding.ivProduct);
            else
                mBinding.ivProduct.setImageDrawable(mBinding.ivProduct.getResources().getDrawable(R.drawable.ic_home_placeholder));

            if (bean.getSellerId().equalsIgnoreCase(DataManager.getInstance().getUserDetails().getUserId())) {
                try {
                    mBinding.tvPrice.setText("$ " + new DecimalFormat("##.##").format(Double.valueOf(bean.getNetPrice())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setDeliveryStatusAction(mBinding, bean, true);
            } else {
                try {
                    mBinding.tvPrice.setText("$ " + new DecimalFormat("##.##").format(Double.valueOf(bean.getPrice())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setDeliveryStatusAction(mBinding, bean, false);
            }
        }


    }
}
