package com.taghawk.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.taghawk.databinding.AdapterSharedTagsViewBinding;
import com.taghawk.model.tag.TagData;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class SharedTagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<TagData> mTagList;
    private CompoundButton.OnCheckedChangeListener listener;

    public SharedTagAdapter(Context context, ArrayList<TagData> mCategoryList, CompoundButton.OnCheckedChangeListener listener) {
        this.context = context;
        this.mTagList = mCategoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterSharedTagsViewBinding mBinding = AdapterSharedTagsViewBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new SharedTagViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        SharedTagViewHolder holder = null;
        holder = (SharedTagViewHolder) viewHolder;
        holder.bind(mTagList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTagList.size();
    }

    private class SharedTagViewHolder extends RecyclerView.ViewHolder {
        AdapterSharedTagsViewBinding viewBinding;

        public SharedTagViewHolder(AdapterSharedTagsViewBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.tvShareTagName.setOnCheckedChangeListener(listener);
        }

        public void bind(TagData bean) {
            viewBinding.tvShareTagName.setTag(getAdapterPosition());
            viewBinding.tvShareTagName.setText(bean.getTagName());
            if (!bean.isSelected()) {
                viewBinding.tvShareTagName.setChecked(false);
                bean.setSelected(false);
            } else {
                viewBinding.tvShareTagName.setChecked(true);
                bean.setSelected(true);
            }

        }

    }
}
