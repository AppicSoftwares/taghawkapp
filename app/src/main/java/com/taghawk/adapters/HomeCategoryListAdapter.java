package com.taghawk.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taghawk.databinding.AdapterCategoryViewHomeBinding;
import com.taghawk.model.category.CategoryListResponse;
import com.taghawk.ui.home.HomeActivity;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class HomeCategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<CategoryListResponse> mCategoryList;

    public HomeCategoryListAdapter(Context context, ArrayList<CategoryListResponse> mCategoryList) {
        this.context = context;
        this.mCategoryList = mCategoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterCategoryViewHomeBinding mBinding = AdapterCategoryViewHomeBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
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
        AdapterCategoryViewHomeBinding viewBinding;

        public CategoryListViewHolder(AdapterCategoryViewHomeBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.cardcat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("CATEGORY_ID", mCategoryList.get(getAdapterPosition()).getId());
                    intent.putExtra("TITLE", mCategoryList.get(getAdapterPosition()).getName());
                    ((HomeActivity) context).setResult(Activity.RESULT_OK, intent);
                }
            });
        }

        public void bind(CategoryListResponse bean) {
            viewBinding.setViewModel(bean);
        }
    }
}