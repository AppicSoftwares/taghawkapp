package com.taghawk.adapters;

import android.content.Context;
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
import com.taghawk.databinding.AdapterProductListingMoreBinding;
import com.taghawk.model.home.ProductDetailsData;

import java.util.ArrayList;

public class ProductListWithMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ProductDetailsData> mList;
    private View.OnClickListener listener;
    private boolean status;
    private String sellerId;

    public ProductListWithMoreAdapter(ArrayList<ProductDetailsData> mList, View.OnClickListener listener, boolean status, String sellerId) {
        this.mList = mList;
        this.listener = listener;
        this.status = status;
        this.sellerId = sellerId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == AppConstants.LIST_VIEW_TYPE) {
            AdapterProductListingMoreBinding mBinding = AdapterProductListingMoreBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            return new ProductListWithMoreAdapter.ProductListViewModel(mBinding);
        } else {
            return new LoaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_bottom_loader, viewGroup, false));
        }
    }

    public class LoaderViewHolder extends RecyclerView.ViewHolder {

        public LoaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ProductListViewModel) {
            ProductListWithMoreAdapter.ProductListViewModel holder = null;
            holder = (ProductListWithMoreAdapter.ProductListViewModel) viewHolder;
            holder.bind(mList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position).isLoading()) {
            return AppConstants.LOADER_VIEW_TYPE;
        } else
            return AppConstants.LIST_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class ProductListViewModel extends RecyclerView.ViewHolder {
        AdapterProductListingMoreBinding viewBinding;

        public ProductListViewModel(AdapterProductListingMoreBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.ivMore.setOnClickListener(listener);
            viewBinding.card.setOnClickListener(listener);
        }

        public void bind(ProductDetailsData bean) {
            viewBinding.ivMore.setTag(getAdapterPosition());
            viewBinding.card.setTag(getAdapterPosition());
            if (bean.getImageList() != null && bean.getImageList().size() > 0)
                Glide.with(viewBinding.getRoot().getContext()).asBitmap().load(bean.getImageList().get(0).getUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(viewBinding.ivProduct);
            else {
                viewBinding.ivProduct.setImageResource(R.drawable.ic_home_placeholder);
            }
            if (status && (sellerId == null || DataManager.getInstance().getUserDetails().getUserId().equalsIgnoreCase(sellerId) || sellerId.length() == 0)) {
                viewBinding.ivMore.setVisibility(View.VISIBLE);
            } else {
                viewBinding.ivMore.setVisibility(View.GONE);

            }
        }
    }
}
