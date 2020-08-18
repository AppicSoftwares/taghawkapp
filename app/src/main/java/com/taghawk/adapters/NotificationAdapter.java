package com.taghawk.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taghawk.databinding.AdapterNotificationViewBinding;
import com.taghawk.model.NotificationData;
import com.taghawk.util.AppUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<NotificationData> mNotificationList;
    private View.OnClickListener listener;

    public NotificationAdapter(Context context, ArrayList<NotificationData> mCategoryList, View.OnClickListener listener) {
        this.context = context;
        this.mNotificationList = mCategoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterNotificationViewBinding mBinding = AdapterNotificationViewBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        //DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_category_view, viewGroup, false);
        return new NotificationViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        NotificationViewHolder holder = null;
        holder = (NotificationViewHolder) viewHolder;
        holder.bind(mNotificationList.get(position));
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    private class NotificationViewHolder extends RecyclerView.ViewHolder {
        AdapterNotificationViewBinding viewBinding;

        public NotificationViewHolder(AdapterNotificationViewBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            this.viewBinding.llNotification.setOnClickListener(listener);

        }

        public void bind(NotificationData bean) {
            viewBinding.llNotification.setTag(getAdapterPosition());
            viewBinding.setNotificationViewModel(bean);
            viewBinding.tvTime.setText(new PrettyTime().format(AppUtils.timeStampToDate(bean.getCreated())));
        }
    }
}