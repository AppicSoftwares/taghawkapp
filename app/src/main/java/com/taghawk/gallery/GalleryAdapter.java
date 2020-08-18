package com.taghawk.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_view.SquareImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryAdapter extends ListAdapter<GalleryMediaBean, GalleryAdapter.GalleryViewHolder> {


    private Context mContext;
    private boolean inSelectedMode = false;
    private RecyclerOnItemListener<GalleryMediaBean> recyclerOnItemListener;

    private ArrayList<GalleryMediaBean> selectedMediaList;

    public GalleryAdapter(@NonNull DiffUtil.ItemCallback<GalleryMediaBean> diffCallback, @NonNull Context mContext) {
        super(diffCallback);
        this.mContext = mContext;
        selectedMediaList = new ArrayList<>();
    }

    public void setRecyclerOnItemListener(RecyclerOnItemListener<GalleryMediaBean> recyclerOnItemListener) {
        this.recyclerOnItemListener = recyclerOnItemListener;
    }

    public boolean isInSelectedMode() {
        return inSelectedMode;
    }

    public void setInSelectedMode(boolean inSelectedMode) {
        if (this.inSelectedMode && !inSelectedMode) {
            for (int i = 0; i < getItemCount(); i++) {
                getItem(i).setSelected(false);
            }
            selectedMediaList.clear();
        }
        this.inSelectedMode = inSelectedMode;
        notifyDataSetChanged();
    }

    public ArrayList<GalleryMediaBean> getSelectedMediaList() {
        return selectedMediaList;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        holder.bind(position);

    }


    class GalleryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        SquareImageView thumbnail;
        @BindView(R.id.cb_select_image)
        CheckBox cbSelectedImage;

        GalleryViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

//            view.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    inSelectedMode = true;
//                    recyclerOnItemListener.onLongClick(v, getAdapterPosition(), getItem(getAdapterPosition()), AppConstant.MEDIA_ON_CLICK);
//                    notifyDataSetChanged();
//                    return true;
//                }
//            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GalleryMediaBean mediaBean = getItem(getAdapterPosition());

                    recyclerOnItemListener.onClick(v, getAdapterPosition(), getItem(getAdapterPosition()), AppConstants.MEDIA_ON_CLICK);

//                    if (inSelectedMode) {
//                        if (selectedMediaList.contains(mediaBean)) {
//                            mediaBean.setSelected(false);
//                            selectedMediaList.remove(mediaBean);
//                        } else {
//                            mediaBean.setSelected(true);
//                            selectedMediaList.add(mediaBean);
//                        }
//                        notifyItemChanged(getAdapterPosition());
//
//                    }
                }
            });
        }


        void bind(int position) {
            GalleryMediaBean mediaBean = getItem(position);

            Glide.with(mContext)
                    .load(mediaBean.getUri())
                    .into(thumbnail);

            if (inSelectedMode) {
                cbSelectedImage.setVisibility(View.VISIBLE);
            } else {
                cbSelectedImage.setVisibility(View.GONE);
            }

            cbSelectedImage.setChecked(mediaBean.isSelected());

            if (mediaBean.isSelected()) {
                itemView.setAlpha(0.4f);
            } else {
                itemView.setAlpha(1f);
            }

        }

    }

}