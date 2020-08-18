package com.taghawk.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taghawk.R;
import com.taghawk.databinding.AdapterBlockUserViewBinding;
import com.taghawk.model.block_user.BlockUserDetail;

import java.util.ArrayList;

public class BlockUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<BlockUserDetail> mList;
    private View.OnClickListener listener;
    private int type;
    private boolean isOtherProfile;

    public BlockUserAdapter(ArrayList<BlockUserDetail> mList, View.OnClickListener listener) {
        this.mList = mList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterBlockUserViewBinding mBinding = AdapterBlockUserViewBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new BlockUserViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        BlockUserViewHolder holder = (BlockUserViewHolder) viewHolder;
        holder.bind(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class BlockUserViewHolder extends RecyclerView.ViewHolder {
        private AdapterBlockUserViewBinding binding;

        public BlockUserViewHolder(AdapterBlockUserViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.tvUnBlock.setOnClickListener(listener);
            binding.llMain.setOnClickListener(listener);
            binding.ivMore.setOnClickListener(listener);
        }

        public void bind(BlockUserDetail bean) {
            if (!mList.get(getAdapterPosition()).isUnBlock()) {
                setFollowFollowingBackground(R.string.unblock, R.color.txt_light_gray, R.drawable.shape_rectangle_stoke);
            } else {
                setFollowFollowingBackground(R.string.block, R.color.White, R.drawable.ic_buy_button);
            }
            binding.llMain.setTag(getAdapterPosition());
            binding.tvUnBlock.setTag(getAdapterPosition());
            binding.ivMore.setTag(getAdapterPosition());
            binding.setBlockUserViewModel(bean);
        }

        private void setFollowFollowingBackground(int p, int p2, int p3) {
            binding.tvUnBlock.setText(binding.getRoot().getContext().getResources().getString(p));
            binding.tvUnBlock.setTextColor(binding.getRoot().getContext().getResources().getColor(p2));
            binding.tvUnBlock.setBackgroundDrawable(binding.getRoot().getContext().getResources().getDrawable(p3));
        }
    }


}
