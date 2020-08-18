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


/**
 * Created by Rishabh Saxena
 * rishabh.saxena@appinventiv.com
 * Appinventiv Technologies Pvt. Ltd.
 * on 25/06/17.
 */


public class CameraTwoAdapter extends RecyclerView.Adapter<CameraTwoAdapter.MyViewHolder> {
    private ArrayList<String> mFileArrayList;
    private Context mContext;
    private RecyclerListener mRecyclerLisner;

    public CameraTwoAdapter(Context context, ArrayList<String> fileArrayList, RecyclerListener recyclerLisner) {

        this.mFileArrayList = fileArrayList;
        this.mContext = context;
        this.mRecyclerLisner = recyclerLisner;


    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_camera_image_preview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Glide.with(mContext).load(Uri.parse(mFileArrayList.get(position)).toString()).into(holder.ivpicture);
        // holder.ivpicture.setImageURI(Uri.parse(mFileArrayList.get(position).getAbsolutePath()));

        if (position == mFileArrayList.size() - 1) {
            holder.ivcross.setVisibility(View.VISIBLE);
        } else {
            holder.ivcross.setVisibility(View.VISIBLE);
        }
        holder.ivcross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerLisner.onItemClick(null, position, null, false);
            }
        });


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
            /*   mainlayout=(FrameLayout)itemView.findViewById(R.id.main_layout);*/
            ivcross = (ImageView) itemView.findViewById(R.id.iv_cancel);
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
