package com.taghawk.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.taghawk.R;
import com.taghawk.databinding.AdapterProductListingBinding;
import com.taghawk.model.home.ProductListModel;

import java.util.ArrayList;

public class PromoteMultipleProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ProductListModel> mList;

    public PromoteMultipleProductAdapter(ArrayList<ProductListModel> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterProductListingBinding mBinding = AdapterProductListingBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new PromoteMultipleViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        PromoteMultipleViewHolder holder = null;
        holder = (PromoteMultipleViewHolder) viewHolder;
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class PromoteMultipleViewHolder extends RecyclerView.ViewHolder {
        AdapterProductListingBinding viewBinding;

        PromoteMultipleViewHolder(@NonNull AdapterProductListingBinding itemView) {
            super(itemView.getRoot());
            this.viewBinding = itemView;
        }

        public void bind(ProductListModel bean) {
            if (bean.getImageLists() != null && bean.getImageLists().size() > 0) {
                try {
                    Glide.with(viewBinding.getRoot().getContext()).asBitmap().load(bean.getImageLists().get(0).getUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(viewBinding.ivProduct);
                } catch (Exception e) {
                    viewBinding.ivProduct.setImageResource(R.drawable.ic_home_placeholder);
                }
            } else {
                viewBinding.ivProduct.setImageResource(R.drawable.ic_home_placeholder);
            }
            if (!bean.isSelected()) {
                viewBinding.tvShareTagName.setChecked(false);
                bean.setSelected(false);
            } else {
                viewBinding.tvShareTagName.setChecked(true);
                bean.setSelected(true);
            }

            viewBinding.ivPromote.setVisibility(View.GONE);

        }
    }
}
