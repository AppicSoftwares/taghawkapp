package com.taghawk.adapters;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taghawk.databinding.AdapterChooseWeightBinding;
import com.taghawk.model.AddProduct.ChooseWeightModel;

import java.util.ArrayList;

public class ChooseWeightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ChooseWeightModel> mList;
    private View.OnClickListener listener;

    public ChooseWeightAdapter(ArrayList<ChooseWeightModel> mList, View.OnClickListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterChooseWeightBinding mBinding = AdapterChooseWeightBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ChooseWeightViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        ChooseWeightViewHolder holder = (ChooseWeightViewHolder) viewHolder;
        holder.bind(mList.get(i));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class ChooseWeightViewHolder extends RecyclerView.ViewHolder {
        private AdapterChooseWeightBinding mBinding;

        public ChooseWeightViewHolder(AdapterChooseWeightBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            mBinding.tvChooseWeight.setOnClickListener(listener);
        }

        public void bind(ChooseWeightModel bean) {
            mBinding.tvChooseWeight.setTag(bean);
            mBinding.tvChooseWeight.setText(bean.getWeight());
        }
    }
}
