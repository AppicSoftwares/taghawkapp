package com.taghawk.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taghawk.R;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.AdapterReviewRatingBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.review_rating.ReviewRatingData;
import com.taghawk.util.AppUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;


public class RatingReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ReviewRatingData> mList;
    private String sellerName, sellerUserId;
    private OnDialogViewClickListener listener;

    public RatingReviewAdapter(ArrayList<ReviewRatingData> mList, String sellerName, String sellerUserId, OnDialogViewClickListener listener) {
        this.mList = mList;
        this.sellerName = sellerName;
        this.sellerUserId = sellerUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterReviewRatingBinding mBinding = AdapterReviewRatingBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new ReviewRatingViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ReviewRatingViewHolder holder = (ReviewRatingViewHolder) viewHolder;
        holder.bind(mList.get(i));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class ReviewRatingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterReviewRatingBinding mBinding;

        public ReviewRatingViewHolder(AdapterReviewRatingBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            mBinding.tvReply.setOnClickListener(this);
        }

        public void bind(ReviewRatingData bean) {
            mBinding.setReviewRatingViewModel(bean);
            mBinding.rating.setRating(bean.getCommentRating());
            mBinding.tvSellerName.setText(sellerName);
            mBinding.tvReply.setTag(getAdapterPosition());

            if (sellerUserId.equalsIgnoreCase(DataManager.getInstance().getUserDetails().getUserId())) {
                sellerReplyAction(bean);
            } else {
                buyerCommentAction(bean);
            }
            if (bean.getReplyComment() != null) {
                if (bean.getReplyComment().isEditedStatus()) {
                    mBinding.tvSellerReplyDate.setText(mBinding.tvSellerReplyDate.getContext().getString(R.string.edited_at) + " " + new PrettyTime().format(AppUtils.timeStampToDate(bean.getReplyComment().getEditedDate())));
                } else {
                    mBinding.tvSellerReplyDate.setText("" + new PrettyTime().format(AppUtils.timeStampToDate(bean.getReplyComment().getReplyDate())));
                }
            }
        }

        private void buyerCommentAction(ReviewRatingData bean) {
            mBinding.tvTxtReply.setVisibility(View.VISIBLE);
            mBinding.tilReply.setVisibility(View.GONE);
            mBinding.tvReply.setVisibility(View.GONE);
            if (bean.getReplyComment() == null) {
                mBinding.llReply.setVisibility(View.GONE);
                mBinding.viewLine.setVisibility(View.INVISIBLE);
            } else {
                mBinding.llReply.setVisibility(View.VISIBLE);
                mBinding.viewLine.setVisibility(View.VISIBLE);
                mBinding.tvTxtReply.setText(bean.getReplyComment().getReplyComment());
            }
        }

        private void sellerReplyAction(ReviewRatingData bean) {
            if (bean.getReplyComment() == null) {
                mBinding.tvTxtReply.setVisibility(View.GONE);
                mBinding.tvReply.setVisibility(View.VISIBLE);
                mBinding.tvSellerReplyDate.setVisibility(View.GONE);
                if (!bean.isReplyShowing()) {
                    mBinding.tvReply.setText(mBinding.tvReply.getContext().getString(R.string.reply));
                    mBinding.tilReply.setVisibility(View.GONE);
                } else {
                    if (bean.getReplyComment() != null && bean.getReplyComment().isEditedStatus()) {
                        mBinding.tilReply.setVisibility(View.GONE);
                    } else {
                        mBinding.tvReply.setText(mBinding.tvReply.getContext().getString(R.string.post));
                        mBinding.tilReply.setVisibility(View.VISIBLE);
                    }
                }
            } else if (bean.getReplyComment() != null && bean.getReplyComment().getReplyComment() != null && bean.getReplyComment().getReplyComment().length() > 0) {
                mBinding.tvTxtReply.setVisibility(View.VISIBLE);
                mBinding.tvTxtReply.setText(bean.getReplyComment().getReplyComment());
                mBinding.tvReply.setVisibility(View.VISIBLE);
                mBinding.tilReply.setVisibility(View.GONE);
                mBinding.tvReply.setText(mBinding.tvReply.getContext().getString(R.string.edit));
            } else {
                mBinding.tvReply.setVisibility(View.GONE);
                mBinding.tvTxtReply.setText(bean.getReplyComment().getReplyComment());
                mBinding.tilReply.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_reply:
                    if (mList.get(getAdapterPosition()).getReplyComment() != null && mBinding.tvReply.getText().toString().trim().equalsIgnoreCase(mBinding.tvReply.getContext().getString(R.string.edit))) {
                        mBinding.tvTxtReply.setVisibility(View.GONE);
                        mBinding.tvReply.setText(mBinding.tvReply.getContext().getString(R.string.update));
                        mBinding.tilReply.setVisibility(View.VISIBLE);
                        mBinding.etReply.setText(mList.get(getAdapterPosition()).getReplyComment().getReplyComment());
                    } else if (mBinding.tvReply.getText().toString().trim().equalsIgnoreCase(mBinding.tvReply.getContext().getString(R.string.update))) {
                        listener.onSubmit(mBinding.etReply.getText().toString().trim(), getAdapterPosition());
                    } else
                        listener.onSubmit(mBinding.etReply.getText().toString().trim(), getAdapterPosition());
                    break;
            }
        }
    }
}
