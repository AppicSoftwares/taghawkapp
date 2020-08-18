package com.taghawk.adapters;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.taghawk.R;
import com.taghawk.databinding.AdapterSearchProductListBinding;
import com.taghawk.model.home.ProductListModel;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class ProductResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ProductListModel> mList;

    private View.OnClickListener listener;

    public ProductResultAdapter(ArrayList<ProductListModel> mList, View.OnClickListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterSearchProductListBinding mBinding = AdapterSearchProductListBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ProductResultListViewModel(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ProductResultListViewModel holder = null;
        holder = (ProductResultListViewModel) viewHolder;
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class ProductResultListViewModel extends RecyclerView.ViewHolder {
        AdapterSearchProductListBinding viewBinding;

        public ProductResultListViewModel(AdapterSearchProductListBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;

            viewBinding.cardMain.setOnClickListener(listener);
            viewBinding.isLiked.setOnClickListener(listener);

        }

        public void bind(ProductListModel bean) {
            if (bean.getLiked() != null && bean.getLiked()) {
                setLikeUnLike(R.drawable.ic_like_fill, viewBinding);
            } else
                setLikeUnLike(R.drawable.ic_like_unfill, viewBinding);
            viewBinding.cardMain.setTag(getAdapterPosition());
            viewBinding.isLiked.setTag(getAdapterPosition());
            if (bean.getImageLists() != null && bean.getImageLists().size() > 0)
                Glide.with(viewBinding.getRoot().getContext()).asBitmap().load(bean.getImageLists().get(0).getUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(viewBinding.ivProduct);
            viewBinding.setProductViewModel(bean);
            viewBinding.executePendingBindings();
            if (bean.getPromoted()) {
                viewBinding.ivPromote.setVisibility(View.VISIBLE);
            } else viewBinding.ivPromote.setVisibility(View.GONE);

        }
    }


    private void setLikeUnLike(int imageResource, AdapterSearchProductListBinding mBinding) {
        mBinding.isLiked.setImageDrawable(mBinding.isLiked.getContext().getResources().getDrawable(imageResource));
    }
}

