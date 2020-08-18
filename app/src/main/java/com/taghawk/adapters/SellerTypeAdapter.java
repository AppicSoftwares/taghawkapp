package com.taghawk.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taghawk.R;
import com.taghawk.databinding.AdapterSellerTypeBinding;

import static com.taghawk.TagHawkApplication.getContext;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class SellerTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private String[] items;
    private int lastCheckedPosition = -1;

    public SellerTypeAdapter(Context context, String[] items) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterSellerTypeBinding mBinding = AdapterSellerTypeBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new SellerTypeViewHolder(mBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        SellerTypeViewHolder holder = null;
        holder = (SellerTypeViewHolder) viewHolder;
        holder.bind(items[position]);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public int getPosition() {
        return lastCheckedPosition;
    }

    public void notifyItemAtPostion(int lastCheckedPosition) {
        this.lastCheckedPosition = lastCheckedPosition;
        notifyDataSetChanged();
    }

    private class SellerTypeViewHolder extends RecyclerView.ViewHolder {
        AdapterSellerTypeBinding viewBinding;

        public SellerTypeViewHolder(final AdapterSellerTypeBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.rating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastCheckedPosition != getAdapterPosition()) {
                        lastCheckedPosition = getAdapterPosition();
                    } else {
                        lastCheckedPosition = -1;
                    }
                    notifyDataSetChanged();

                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void bind(String bean) {
            viewBinding.rating.setText(bean);
            if (lastCheckedPosition == getAdapterPosition()) {
                setBackgoundSeller(R.drawable.ic_filter_rating_white, R.color.White, R.drawable.rounded_corner_color_primary_rating);
            } else {
                setBackgoundSeller(R.drawable.ic_rating_black, R.color.txt_black, R.drawable.rounded_corner_white_rating);
            }
        }

        private void setBackgoundSeller(int p, int p2, int p3) {
            Drawable img = getContext().getResources().getDrawable(p);
            viewBinding.rating.setTextColor(getContext().getResources().getColor(p2));
            viewBinding.rating.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            viewBinding.rating.setBackgroundDrawable(viewBinding.rating.getContext().getResources().getDrawable(p3));
        }
    }
}
