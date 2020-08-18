package com.taghawk.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;

public class ChatProductsSpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ChatProductModel> productList;

    public ChatProductsSpinnerAdapter(Context context, ArrayList<ChatProductModel> productList) {
        mContext = context;
        this.productList = productList;
    }

    public int getCount() {
        return productList.size();
    }

    public ChatProductModel getItem(int i) {
        return productList.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }


    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.row_chat_product_spinner, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvProductName = (TextView) view.findViewById(R.id.tv_product_name);
            viewHolder.tvProductPrice = (TextView) view.findViewById(R.id.tv_product_price);
            viewHolder.ivProduct = (ImageView) view.findViewById(R.id.iv_product);
            viewHolder.llMain = (LinearLayout) view.findViewById(R.id.ll_product);
            viewHolder.tvPending = (TextView) view.findViewById(R.id.tv_pending);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();
        ChatProductModel chatProductModel = productList.get(position);
        viewHolder.tvProductName.setText(chatProductModel.getProductName());
        viewHolder.tvProductPrice.setText(TextUtils.concat("$ " + chatProductModel.getProductPrice()));
        AppUtils.loadCircularImage(mContext, chatProductModel.getProductImage(), 20, R.drawable.ic_home_placeholder, viewHolder.ivProduct, true);
        switch (chatProductModel.getProductStatus())
        {
            case 1:
                viewHolder.tvPending.setVisibility(View.GONE);
                viewHolder.llMain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.White));
                break;
            case 2:
                viewHolder.tvPending.setVisibility(View.VISIBLE);
                viewHolder.tvPending.setText(mContext.getString(R.string.sold));
                viewHolder.tvPending.setTextColor(ContextCompat.getColor(mContext, R.color.txt_black));
                viewHolder.llMain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.message_detail_product_header_bg));
                break;
            case 5:
                viewHolder.tvPending.setVisibility(View.VISIBLE);
                viewHolder.tvPending.setText(mContext.getString(R.string.pending));
                viewHolder.tvPending.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                viewHolder.llMain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.White));
                break;
        }
        return view;
    }

    @Override
    public View getView(int pos, View view, final ViewGroup viewgroup) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        int position = ((Spinner) viewgroup).getSelectedItemPosition();
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.row_chat_product_spinner, viewgroup, false);
            viewHolder = new ViewHolder();
            viewHolder.tvProductName = (TextView) view.findViewById(R.id.tv_product_name);
            viewHolder.tvProductPrice = (TextView) view.findViewById(R.id.tv_product_price);
            viewHolder.ivProduct = (ImageView) view.findViewById(R.id.iv_product);
            viewHolder.llMain = (LinearLayout) view.findViewById(R.id.ll_product);
            viewHolder.tvPending = (TextView) view.findViewById(R.id.tv_pending);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();
        final ChatProductModel chatProductModel = productList.get(position);
        viewHolder.tvProductName.setText(chatProductModel.getProductName());
        viewHolder.tvProductPrice.setText(TextUtils.concat("$ " + chatProductModel.getProductPrice()));
        AppUtils.loadCircularImage(mContext, chatProductModel.getProductImage(), 20, R.drawable.ic_home_placeholder, viewHolder.ivProduct, true);
        viewHolder.ivProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(viewgroup.getContext(), ProductDetailsActivity.class);
                    intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, chatProductModel.getProductId());
                    viewgroup.getContext().startActivity(intent);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });
        switch (chatProductModel.getProductStatus())
        {
            case 1:
                viewHolder.tvPending.setVisibility(View.GONE);
                break;
            case 2:
                viewHolder.tvPending.setVisibility(View.VISIBLE);
                viewHolder.tvPending.setText(mContext.getString(R.string.sold));
                viewHolder.tvPending.setTextColor(ContextCompat.getColor(mContext, R.color.txt_black));
                break;
            case 5:
                viewHolder.tvPending.setVisibility(View.VISIBLE);
                viewHolder.tvPending.setText(mContext.getString(R.string.pending));
                viewHolder.tvPending.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                break;
        }
        return view;
    }

    private class ViewHolder {
        private TextView tvProductName;
        private TextView tvProductPrice;
        private ImageView ivProduct;
        private LinearLayout llMain;
        private TextView tvPending;

    }
}