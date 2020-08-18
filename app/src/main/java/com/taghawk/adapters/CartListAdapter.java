package com.taghawk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taghawk.databinding.AdapterCartViewBinding;
import com.taghawk.model.cart.CartDataBean;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class CartListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<CartDataBean> mCartList;
    private View.OnClickListener listener;

    public CartListAdapter(Context context, ArrayList<CartDataBean> mCartList, View.OnClickListener listener) {
        this.context = context;
        this.mCartList = mCartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterCartViewBinding mBinding = AdapterCartViewBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new CartListViewModel(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        CartListViewModel holder = null;
        holder = (CartListViewModel) viewHolder;
        holder.bind(mCartList.get(position));
    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }

    private class CartListViewModel extends RecyclerView.ViewHolder {
        AdapterCartViewBinding viewBinding;

        public CartListViewModel(AdapterCartViewBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.ivBin.setVisibility(View.VISIBLE);
            viewBinding.llMain.setOnClickListener(listener);
            viewBinding.ivBin.setOnClickListener(listener);
        }

        public void bind(CartDataBean bean) {
            viewBinding.llMain.setTag(getAdapterPosition());
            viewBinding.ivBin.setTag(getAdapterPosition());
            viewBinding.setCartViewModel(bean);
            if (bean.getProductStatus() == 2 || bean.getProductStatus() == 5) {
                viewBinding.ivSold.setVisibility(View.VISIBLE);
            } else {
                viewBinding.ivSold.setVisibility(View.GONE);

            }
            viewBinding.tvSellerName.setText("Seller Name: " + bean.getSellerName());
        }
    }
}
