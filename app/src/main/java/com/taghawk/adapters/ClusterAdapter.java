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
import com.taghawk.databinding.LayoutClusterRowBinding;
import com.taghawk.model.tag.ClusterBean;
import com.taghawk.ui.tag.TagDetailsActivity;

import java.util.ArrayList;

public class ClusterAdapter extends RecyclerView.Adapter<ClusterAdapter.ViewHolderName> {

    private ArrayList<ClusterBean> mClusterBeanArrayList;
    private Context context;

    public ClusterAdapter(Context context, ArrayList<ClusterBean> mClusterBeanArrayList) {
        this.context = context;
        this.mClusterBeanArrayList = mClusterBeanArrayList;
    }

    @Override
    public ViewHolderName onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutClusterRowBinding mBinding = LayoutClusterRowBinding.inflate(layoutInflater, parent, false);
        return new ViewHolderName(mBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolderName holder, int position) {
        holder.bind(mClusterBeanArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mClusterBeanArrayList.size();
    }


    class ViewHolderName extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LayoutClusterRowBinding itemView;

        private ViewHolderName(LayoutClusterRowBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
            itemView.tvJoin.setOnClickListener(this);
        }

        public void bind(ClusterBean bean) {
            itemView.tvTagName.setText(bean.getmTagName());
            itemView.tvTagTotalMembers.setText(bean.getmTagTotalMember() + " " + context.getString(R.string.members));
            itemView.tvTagFoundedBy.setText("Founder:" + bean.getmFounder());
            itemView.tvTagType.setText(getType(bean.getmTagType()));
//            setMemberTypeButton(bean.isMember(), bean.getmTagType(), itemView);
            if (bean.getmTagImage().length() > 0)
                Glide.with(itemView.IvTagImage.getContext()).asBitmap().load(bean.getmTagImage()).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(itemView.IvTagImage);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_join:
                    Intent detailsIntent = new Intent(context, TagDetailsActivity.class);
                    detailsIntent.putExtra("TAG_ID", mClusterBeanArrayList.get(getAdapterPosition()).getmTagId());
                    context.startActivity(detailsIntent);
                    break;
            }
        }
    }

    private void setMemberTypeButton(boolean member, int getmTagType, LayoutClusterRowBinding itemView) {
        if (member) {
            itemView.tvJoin.setText(itemView.tvJoin.getContext().getString(R.string.view_details));
        } else if (getmTagType == 1) {
            itemView.tvJoin.setText(itemView.tvJoin.getContext().getString(R.string.apply));
        } else {
            itemView.tvJoin.setText(itemView.tvJoin.getContext().getString(R.string.join));
        }
    }

    private String getType(int i) {
        String type = "";
        switch (i) {
            case 1:
                type = "Private";
                break;
            case 2:
                type = "Public";
                break;
        }
        return type;

    }
}