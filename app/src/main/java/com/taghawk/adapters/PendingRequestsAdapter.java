package com.taghawk.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taghawk.R;
import com.taghawk.databinding.AdapterBlockUserViewBinding;
import com.taghawk.databinding.RowPendingRequestBinding;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.block_user.BlockUserDetail;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.pendingRequests.PendingRequest;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;

public class PendingRequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<PendingRequest> pendingReqList;
    private RecyclerViewCallback recyclerViewCallback;
    private Context context;

    public PendingRequestsAdapter(ArrayList<PendingRequest> pendingReqList, RecyclerViewCallback recyclerViewCallback) {
        this.pendingReqList = pendingReqList;
        this.recyclerViewCallback = recyclerViewCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        RowPendingRequestBinding mBinding = RowPendingRequestBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new PendingRequestViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        PendingRequestViewHolder holder = (PendingRequestViewHolder) viewHolder;
        holder.bind(pendingReqList.get(position));

    }

    @Override
    public int getItemCount() {
        return pendingReqList.size();
    }

    private class PendingRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RowPendingRequestBinding binding;

        public PendingRequestViewHolder(RowPendingRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.ibAccept.setOnClickListener(this);
            binding.ibReject.setOnClickListener(this);
            binding.ibChat.setOnClickListener(this);
            binding.tvViewDocument.setOnClickListener(this);
        }

        public void bind(PendingRequest pendingRequest) {
            AppUtils.loadCircularImage(context,pendingRequest.getSenderProfilePic(),300,R.drawable.ic_detail_user_placeholder,binding.ivMember,true);
            binding.tvMemberName.setText(pendingRequest.getSenderName());
        }

        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.tv_view_document:
                case R.id.ib_accept:
                case R.id.ib_reject:
                case R.id.ib_chat:
                    recyclerViewCallback.onClick(getAdapterPosition(),view);
                    break;
            }
        }
    }


}
