package com.taghawk.ui.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.taghawk.R;
import com.taghawk.adapters.SlidingImage_Adapter;
import com.taghawk.databinding.LayoutZoomViewBinding;
import com.taghawk.model.home.ImageList;

import java.util.ArrayList;

public class ZoomImageActivity extends AppCompatActivity implements View.OnClickListener {


    private String imageUrl = "";
    private LayoutZoomViewBinding mBinding;
    private ArrayList<ImageList> IMAGES;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_zoom_view);
        initVariables();
        if (IMAGES != null && IMAGES.size() == 1)
            loadImage(IMAGES.get(0).getUrl());
        else if (IMAGES != null && IMAGES.size() > 1) {
            setPager(IMAGES);
        }
    }

    private void setPager(ArrayList<ImageList> imageList) {
        mBinding.vpImages.setAdapter(new SlidingImage_Adapter(this, imageList, false));
        mBinding.circleIndicator.setViewPager(mBinding.vpImages);

    }

    private void initVariables() {
        mBinding.ivClose.setOnClickListener(this);
        if (getIntent() != null) {
            if (getIntent().hasExtra("ImageUrl"))
                IMAGES = (ArrayList<ImageList>) getIntent().getExtras().get("ImageUrl");
        }
        if (IMAGES != null && IMAGES.size() > 1) {
            showHideVp(View.VISIBLE, View.GONE);
        } else {
            showHideVp(View.GONE, View.VISIBLE);
        }
    }

    private void showHideVp(int visible, int gone) {
        mBinding.vpImages.setVisibility(visible);
        mBinding.circleIndicator.setVisibility(visible);
        mBinding.ivPhoto.setVisibility(gone);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                finish();
                break;
        }
    }

    private void loadImage(String imageUrl) {
        if (imageUrl != null) {
            Glide.with(this).asBitmap().load(imageUrl).apply(RequestOptions.placeholderOf(R.drawable.ic_detail_img_placeholder)).into(mBinding.ivPhoto);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}