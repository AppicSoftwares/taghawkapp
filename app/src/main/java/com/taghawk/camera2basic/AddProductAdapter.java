package com.taghawk.camera2basic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.taghawk.R;
import com.taghawk.model.home.ImageList;
import com.taghawk.ui.home.ZoomImageActivity;

import java.util.ArrayList;

public class AddProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> mFileArrayList;
    private Context mContext;
    private RecyclerListener mRecyclerLisner;

    public AddProductAdapter(Context context, ArrayList<String> fileArrayList, RecyclerListener recyclerLisner) {

        this.mFileArrayList = fileArrayList;
        this.mContext = context;
        this.mRecyclerLisner = recyclerLisner;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_image_preview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        Glide.with(mContext).load(Uri.parse(mFileArrayList.get(position)).toString()).into(viewHolder.ivpicture);
        if (position == mFileArrayList.size() - 1) {
            viewHolder.ivcross.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivcross.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return mFileArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivpicture;
        FrameLayout mainlayout;
        ImageView ivcross;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivpicture = (ImageView) itemView.findViewById(R.id.iv_preview);
            ivcross = (ImageView) itemView.findViewById(R.id.iv_cancel);
            ivcross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecyclerLisner.onItemClick(null, getAdapterPosition(), null, false);
                }
            });
            ivpicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<ImageList> IMAGES = new ArrayList<>();
                    if (IMAGES != null && IMAGES.size() > 0)
                        IMAGES.clear();
                    ImageList imageList = new ImageList();
                    imageList.setThumbUrl(mFileArrayList.get(getAdapterPosition()));
                    imageList.setUrl(mFileArrayList.get(getAdapterPosition()));
                    IMAGES.add(imageList);
                    Intent intent = new Intent(mContext, ZoomImageActivity.class);
                    intent.putExtra("ImageUrl", IMAGES);
                    mContext.startActivity(intent);
                }
            });
        }

    }
}
