package com.taghawk.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taghawk.R;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.AdapterFollowerBinding;
import com.taghawk.model.follow_following.FollowFollowingData;

import java.util.ArrayList;

public class FollowFollowingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<FollowFollowingData> mList;
    private View.OnClickListener listener;
    private int type;
    private boolean isOtherProfile;

    public FollowFollowingAdapter(ArrayList<FollowFollowingData> mList, int type, boolean isOtherProfile, View.OnClickListener listener) {
        this.mList = mList;
        this.listener = listener;
        this.type = type;
        this.isOtherProfile = isOtherProfile;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterFollowerBinding mBinding = AdapterFollowerBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new FollowFollowingViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        FollowFollowingViewHolder holder = (FollowFollowingViewHolder) viewHolder;
        holder.bind(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class FollowFollowingViewHolder extends RecyclerView.ViewHolder {
        private AdapterFollowerBinding binding;

        public FollowFollowingViewHolder(AdapterFollowerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.tvFollow.setOnClickListener(listener);
            binding.llMain.setOnClickListener(listener);
            binding.ivMore.setOnClickListener(listener);
        }

        public void bind(FollowFollowingData bean) {
            binding.llMain.setTag(getAdapterPosition());
            binding.tvFollow.setTag(getAdapterPosition());
            binding.ivMore.setTag(getAdapterPosition());
            binding.setFollowFollowingViewModel(bean);
            setView(bean);
        }

        private void setView(FollowFollowingData bean) {
            if (mList.get(getAdapterPosition()).getUserId().equalsIgnoreCase(DataManager.getInstance().getUserDetails().getUserId())) {
                binding.tvFollow.setVisibility(View.GONE);
                binding.ivMore.setVisibility(View.GONE);
            } else {
                if (bean.isFollowing()) {
                    setFollowFollowingBackground(R.string.following, R.color.txt_light_gray, R.drawable.shape_rectangle_stoke);
                } else {
                    setFollowFollowingBackground(R.string.follow, R.color.White, R.drawable.ic_buy_button);
                }
                if (type == 1) {
                    binding.tvFollow.setVisibility(View.VISIBLE);
                    if (!isOtherProfile)
                        binding.ivMore.setVisibility(View.VISIBLE);
                    else
                        binding.ivMore.setVisibility(View.GONE);
                } else {
                    binding.ivMore.setVisibility(View.GONE);
                }
            }


        }

        private void setFollowFollowingBackground(int p, int p2, int p3) {
            binding.tvFollow.setText(binding.getRoot().getContext().getResources().getString(p));
            binding.tvFollow.setTextColor(binding.getRoot().getContext().getResources().getColor(p2));
            binding.tvFollow.setBackgroundDrawable(binding.getRoot().getContext().getResources().getDrawable(p3));
        }
    }


}
