package com.taghawk.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.AdapterProductListingBinding;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class MyProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ProductDetailsData> mList;
    private RecyclerViewCallback recyclerViewCallback;

    public MyProductListAdapter(ArrayList<ProductDetailsData> mList) {
        this.mList = mList;
        this.recyclerViewCallback = null;
    }

    public MyProductListAdapter(ArrayList<ProductDetailsData> mList, RecyclerViewCallback recyclerViewCallback) {
        this.mList = mList;
        this.recyclerViewCallback = recyclerViewCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        AdapterProductListingBinding mBinding = AdapterProductListingBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ProductListViewModel(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ProductListViewModel holder = null;
        holder = (ProductListViewModel) viewHolder;
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class ProductListViewModel extends RecyclerView.ViewHolder {
        AdapterProductListingBinding viewBinding;

        public ProductListViewModel(AdapterProductListingBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.ivProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewCallback != null)
                        recyclerViewCallback.onClick(getAdapterPosition(), v);
                    else
                        try {
                            Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
                            intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, mList.get(getAdapterPosition()).getProductId());
                            v.getContext().startActivity(intent);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                }
            });
        }

        public void bind(ProductDetailsData bean) {
            if (bean.getImageList() != null && bean.getImageList().size() > 0) {
                try {
                    Glide.with(viewBinding.getRoot().getContext()).asBitmap().load(bean.getImageList().get(0).getUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(viewBinding.ivProduct);
                } catch (Exception e) {
                    viewBinding.ivProduct.setImageResource(R.drawable.ic_home_placeholder);
                }
            } else {
                viewBinding.ivProduct.setImageResource(R.drawable.ic_home_placeholder);
            }
            viewBinding.ivPromote.setVisibility(View.GONE);
            if (bean.getViewType() == ProductDetailsData.VIEW_TYPE_PRODUCT_SELECT) {
                viewBinding.viewOverlay.setVisibility(View.VISIBLE);
                viewBinding.ivTick.setVisibility(View.VISIBLE);
            } else {
                viewBinding.viewOverlay.setVisibility(View.GONE);
                viewBinding.ivTick.setVisibility(View.GONE);
            }
//                viewBinding.setProductViewModel(bean.getImageLists().get(0));
        }
    }
}
