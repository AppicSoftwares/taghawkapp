package com.taghawk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taghawk.databinding.AdapterProductCategoryViewBinding;
import com.taghawk.model.category.CategoryListResponse;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class CategoryProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<CategoryListResponse> mCategoryList;
    private View.OnClickListener listener;

    public CategoryProductListAdapter(Context context, ArrayList<CategoryListResponse> mCategoryList, View.OnClickListener listener) {
        this.context = context;
        this.mCategoryList = mCategoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterProductCategoryViewBinding mBinding = AdapterProductCategoryViewBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        //DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_category_view, viewGroup, false);
        return new CategoryListViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        CategoryListViewHolder holder = null;
        holder = (CategoryListViewHolder) viewHolder;
        holder.bind(mCategoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    private class CategoryListViewHolder extends RecyclerView.ViewHolder {
        AdapterProductCategoryViewBinding viewBinding;

        public CategoryListViewHolder(AdapterProductCategoryViewBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.tvCategoryName.setOnClickListener(listener);
        }

        public void bind(CategoryListResponse bean) {
            viewBinding.tvCategoryName.setTag(bean);
            viewBinding.setViewModel(bean);
        }
    }
}
