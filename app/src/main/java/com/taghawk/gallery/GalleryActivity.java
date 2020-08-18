package com.taghawk.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.util.AppUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;

import java.util.ArrayList;

public class GalleryActivity extends BaseActivity implements UCropFragmentCallback, GalleryFragment.GalleryFragmentHost {

    private int maxImagesSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        Bundle extras = UCrop.getBasic().getCropOptionsBundle();
        Bundle bundle = new Bundle();
        bundle.putInt("MAX_IMAGES", maxImagesSelection);
        GalleryFragment galleryFragment = GalleryFragment.getInstance(extras);
        galleryFragment.setArguments(bundle);
        replaceFragment(R.id.fl_gallery_container, galleryFragment, GalleryFragment.class.getName());

        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);
    }

    private void getIntentData() {
        maxImagesSelection = getIntent().getExtras().getInt("MAX_IMAGES");
    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_gallery;
    }

    @Override
    public void loadingProgress(boolean showLoader) {
        if (showLoader)
            AppUtils.showDialog(this, "");
        else
            AppUtils.dismissDialog();
    }

    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {
        ArrayList<MediaBean> finalList = new ArrayList<>();

        ArrayList<Uri> cropImages = result.mResultData.getParcelableArrayListExtra(AppConstants.CROP_IMAGES);
//        ArrayList<Uri> cropVideos = result.mResultData.getParcelableArrayListExtra(AppConstant.SELECTED_VIDEOS);

//        if(cropVideos!=null && cropVideos.size()>0){
//            for (Uri uri: cropVideos){
//                MediaBean bean = new MediaBean();
////                bean.setUri(uri);
//                bean.setMediaPath(uri.toString());
//                bean.setMediaType(AppConstant.TYPE_VIDEO);
//                finalList.add(bean);
//            }
//        }
        if (cropImages != null && cropImages.size() > 0) {
            for (Uri uri : cropImages) {
                MediaBean bean = new MediaBean();
                bean.setMediaPath(uri.getPath());
                bean.setMediaType(AppConstants.TYPE_IMAGE);
                finalList.add(bean);
            }
        }


        AppUtils.dismissDialog();

        Intent filterIntent = new Intent();
        filterIntent.putExtra("result", finalList);
        setResult(Activity.RESULT_OK, filterIntent);
        finish();
    }

    @Override
    public void onCloseClicked() {
        onBackPressed();
    }

    @Override
    public void showDialog() {
        AppUtils.showDialog(this, "");
    }

    @Override
    public boolean checkStoragePermission() {
        return false;
    }

    @Override
    public void requestStoragePermission() {

    }
}
