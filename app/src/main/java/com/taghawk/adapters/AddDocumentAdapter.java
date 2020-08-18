package com.taghawk.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.taghawk.R;
import com.taghawk.camera2basic.RecyclerListener;

import java.util.ArrayList;

public class AddDocumentAdapter extends RecyclerView.Adapter<AddDocumentAdapter.MyViewHolder> {
    private ArrayList<String> mFileArrayList;
    private Context mContext;
    private RecyclerListener mRecyclerLisner;

    public AddDocumentAdapter(Context context, ArrayList<String> fileArrayList, RecyclerListener recyclerLisner) {

        this.mFileArrayList = fileArrayList;
        this.mContext = context;
        this.mRecyclerLisner = recyclerLisner;
    }


    @Override
    public AddDocumentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_document_image_preview, parent, false);
        return new AddDocumentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddDocumentAdapter.MyViewHolder holder, final int position) {

        Glide.with(mContext).load(Uri.parse(mFileArrayList.get(position)).toString()).into(holder.ivpicture);

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
            ivcross = (ImageView) itemView.findViewById(R.id.iv_cancel);
        }
    }
}
