package com.taghawk.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taghawk.R;
import com.taghawk.databinding.AdapterRewardsPromotionTypeViewBinding;
import com.taghawk.model.gift.GiftRewardsPromotionData;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class PromotionsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<GiftRewardsPromotionData> mList;
    private View.OnClickListener listener;

    public PromotionsListAdapter(ArrayList<GiftRewardsPromotionData> mList, View.OnClickListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterRewardsPromotionTypeViewBinding mBinding = AdapterRewardsPromotionTypeViewBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new PromtionViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        PromtionViewHolder holder = null;
        holder = (PromtionViewHolder) viewHolder;
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class PromtionViewHolder extends RecyclerView.ViewHolder {
        private AdapterRewardsPromotionTypeViewBinding mBinding;

        public PromtionViewHolder(AdapterRewardsPromotionTypeViewBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            mBinding.llMain.setOnClickListener(listener);
        }

        public void bind(GiftRewardsPromotionData bean) {
            mBinding.llMain.setTag(getAdapterPosition());
            mBinding.tvPromotionDays.setText(bean.getDays() + " " + mBinding.tvPromotionDays.getContext().getString(R.string.days_promotion));
            mBinding.rewardsPoints.setText(bean.getRewardPoints() + " " + mBinding.rewardsPoints.getContext().getString(R.string.points));
        }
    }
}
