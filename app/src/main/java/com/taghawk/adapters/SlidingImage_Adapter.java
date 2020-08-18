package com.taghawk.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.taghawk.R;
import com.taghawk.model.home.ImageList;
import com.taghawk.ui.home.ZoomImageActivity;

import java.util.ArrayList;


public class SlidingImage_Adapter extends PagerAdapter {


    private ArrayList<ImageList> IMAGES;
    private LayoutInflater inflater;
    private Context context;
    private int width;
    private boolean isClicableImage;

    public SlidingImage_Adapter(Context context, ArrayList<ImageList> IMAGES, boolean isClicableImage) {
        this.context = context;
        this.IMAGES = IMAGES;
        this.width = width;
        inflater = LayoutInflater.from(context);
        this.isClicableImage = isClicableImage;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        final View imageLayout = inflater.inflate(R.layout.layout_sliding_images, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);
        Glide.with(view.getContext()).asBitmap().load(IMAGES.get(position).getUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_detail_img_placeholder)).into(imageView);
        view.addView(imageLayout, 0);
        if (isClicableImage) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(imageView.getContext(), ZoomImageActivity.class);
                    intent.putExtra("ImageUrl", IMAGES);
                    imageView.getContext().startActivity(intent);
                }
            });
        }

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}