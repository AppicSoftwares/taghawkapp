package com.taghawk.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taghawk.R;
import com.taghawk.databinding.LayoutTagSearchRowBinding;
import com.taghawk.databinding.RowTagBinding;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.tag.TagData;
import com.taghawk.ui.home.search.SearchAcivity;
import com.taghawk.ui.home.search.SearchFragment;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class MyTagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<TagData> mList;
    private RecyclerViewCallback recyclerViewCallback;

    public MyTagsAdapter(ArrayList<TagData> mList, RecyclerViewCallback recyclerViewCallback) {
        this.mList = mList;
        this.recyclerViewCallback = recyclerViewCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        RowTagBinding mBinding = RowTagBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new MyTagViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MyTagViewHolder holder = null;
        holder = (MyTagViewHolder) viewHolder;
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class MyTagViewHolder extends RecyclerView.ViewHolder {
        RowTagBinding viewBinding;

        public MyTagViewHolder(RowTagBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.llGroupMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewCallback.onClick(getAdapterPosition(), v);
                }
            });
        }

        public void bind(TagData bean) {
            AppUtils.loadCircularImage(context, bean.getTagImageUrl(),300, R.drawable.ic_home_placeholder, viewBinding.ivTag, true);
            viewBinding.tvTagName.setText(bean.getTagName());
            viewBinding.tvDescription.setText(bean.getDescription());
            viewBinding.ivTick.setVisibility(bean.isSelected() ? View.VISIBLE : View.GONE);
        }
    }
}
