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
import com.taghawk.model.home.ProductDetailsData;

import java.util.ArrayList;

public class PromoteMultipleProductFromRewardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ProductDetailsData> mList;
    private View.OnClickListener listener;

    public PromoteMultipleProductFromRewardsAdapter(ArrayList<ProductDetailsData> mList, View.OnClickListener listener) {
        this.mList = mList;
        this.listener = listener;
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
            viewBinding.llMain.setOnClickListener(listener);
            viewBinding.rbSelection.setOnClickListener(listener);
        }

        public void bind(ProductDetailsData bean) {
            viewBinding.rbSelection.setVisibility(View.VISIBLE);
            if (bean.getImageList() != null && bean.getImageList().size() > 0) {
                try {
                    Glide.with(viewBinding.getRoot().getContext()).asBitmap().load(bean.getImageList().get(0).getUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(viewBinding.ivProduct);
                } catch (Exception e) {
                    viewBinding.ivProduct.setImageResource(R.drawable.ic_home_placeholder);
                }
            } else {
                viewBinding.ivProduct.setImageResource(R.drawable.ic_home_placeholder);
            }
            if (!bean.isSelected()) {
                viewBinding.rbSelection.setChecked(false);
                bean.setSelected(false);
            } else {
                viewBinding.rbSelection.setChecked(true);
                bean.setSelected(true);
            }
            viewBinding.ivPromote.setVisibility(View.GONE);
            viewBinding.llMain.setTag(getAdapterPosition());
            viewBinding.rbSelection.setTag(getAdapterPosition());

        }

    }
}
