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
import com.taghawk.databinding.RowChatProductsBinding;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.home.ProductDetailsData;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.home.ProductListingModel;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;

public class ChatProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ProductListModel> productsList;
    private RecyclerViewCallback recyclerViewCallback;

    public ChatProductListAdapter(ArrayList<ProductListModel> productsList, RecyclerViewCallback recyclerViewCallback) {
        this.productsList = productsList;
        this.recyclerViewCallback = recyclerViewCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        RowChatProductsBinding mBinding = RowChatProductsBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ProductListViewModel(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ProductListViewModel holder = null;
        holder = (ProductListViewModel) viewHolder;
        holder.bind(productsList.get(position));
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    private class ProductListViewModel extends RecyclerView.ViewHolder implements View.OnClickListener {
        RowChatProductsBinding viewBinding;

        public ProductListViewModel(RowChatProductsBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.ivProduct.setOnClickListener(this);
            viewBinding.tvCount.setOnClickListener(this);
        }

        public void bind(ProductListModel productDetailsData) {
            RecyclerView.LayoutParams layoutParams=(RecyclerView.LayoutParams)viewBinding.cvMain.getLayoutParams();
            if(getAdapterPosition()==0)
                layoutParams.setMargins(0,(int)context.getResources().getDimension(R.dimen._10sdp),0,(int)context.getResources().getDimension(R.dimen._10sdp));
            else if (getAdapterPosition()==productsList.size()-1)
                layoutParams.setMargins((int)context.getResources().getDimension(R.dimen._10sdp),(int)context.getResources().getDimension(R.dimen._10sdp),(int)context.getResources().getDimension(R.dimen._10sdp),(int)context.getResources().getDimension(R.dimen._10sdp));
            else
                layoutParams.setMargins((int)context.getResources().getDimension(R.dimen._10sdp),(int)context.getResources().getDimension(R.dimen._10sdp),0,(int)context.getResources().getDimension(R.dimen._10sdp));
            if (productDetailsData.getViewType() == ProductDetailsData.VIEW_TYPE_PRODUCT) {
                viewBinding.ivProduct.setVisibility(View.VISIBLE);
                viewBinding.tvCount.setVisibility(View.GONE);
                if (productDetailsData.getImageLists() != null && productDetailsData.getImageLists().size() > 0)
                    AppUtils.loadCircularImage(context, productDetailsData.getImageLists().get(0).getThumbUrl(), (int) context.getResources().getDimension(R.dimen._5sdp), R.drawable.ic_home_placeholder, viewBinding.ivProduct, true);
                else
                    viewBinding.ivProduct.setImageResource(R.drawable.ic_home_placeholder);
            } else if (productDetailsData.getViewType() == ProductDetailsData.VIEW_TYPE_PRODUCT_COUNT){
                if (productDetailsData.getImageLists() != null && productDetailsData.getImageLists().size() > 0)
                    AppUtils.loadCircularImage(context, productDetailsData.getImageLists().get(0).getThumbUrl(), (int) context.getResources().getDimension(R.dimen._5sdp), R.drawable.ic_home_placeholder, viewBinding.ivProduct, true);
                else
                    viewBinding.ivProduct.setImageResource(R.drawable.ic_home_placeholder);
                viewBinding.ivProduct.setVisibility(View.VISIBLE);
                viewBinding.tvCount.setVisibility(View.VISIBLE);
                viewBinding.tvCount.setText(String.valueOf(productDetailsData.getMoreProductCount()));
                viewBinding.tvCount.append(" +");
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.iv_product:
                case R.id.tv_count:
                    recyclerViewCallback.onClick(getAdapterPosition(),view);
                    break;
            }
        }
    }
}
