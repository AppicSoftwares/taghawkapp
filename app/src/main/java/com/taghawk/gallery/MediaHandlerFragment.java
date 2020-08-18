package com.taghawk.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.model.AspectRatio;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MediaHandlerFragment extends CropFragment {

    private static int MAX_MEDIA = 10;
    private static final int MAX_MEDIA_VIDEO = 1;
    protected ArrayList<Uri> selectedVideoUris = new ArrayList<>();
    private boolean isAspectToggleEnable;
    private boolean isMultipleMediaSelected;
    private FrameLayout flAllCropViewsContainer;
    private List<CropContainerView> allCropViews = new ArrayList<>();
    private CropContainerView cropView;
    private UCropFragmentCallback callback;
    //    private MyVideoView videoView;
    private ArrayList<Uri> processedUris = new ArrayList<>();
    private int count;
    private int mediaCount;
    private int videoMediaCount = 0;

    protected void setupViews(View view,int maxImages) {
        initiateRootViews(view);
//        videoView = new MyVideoView(getContext());
//        setVideoListener();
        MediaHandlerFragment.MAX_MEDIA = maxImages;
        flAllCropViewsContainer = view.findViewById(R.id.fl_crop_view_container);
        cropView = addCropContainerView();
        allCropViews.add(cropView);
    }

//    private void setVideoListener() {
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
//                mp.setLooping(true);
//                videoView.start();
//            }
//        });
//    }

    @Override
    public void setCallback(UCropFragmentCallback callback) {
        super.setCallback(callback);
        this.callback = callback;
    }

    protected void updateImage(Uri imageUri) {
        selectedVideoUris.clear();
        processAspectRatios(imageUri);
        resetCropImageView();
        setImageData(imageUri);
//        flAllCropViewsContainer.removeView(videoView);
    }

    private void processAspectRatios(Uri imageUri) {

        if (imageUri != null) {
            float sourceARatio = getOriginalAspectRatio(imageUri);
            ArrayList<AspectRatio> aspectRatios = new ArrayList<>();
            aspectRatios.add(new AspectRatio("Square", 1, 1));
            if (sourceARatio > 1) {
                aspectRatios.add(new AspectRatio("Landscape", 5, 4));
                isAspectToggleEnable = true;
            } else if (sourceARatio < 1) {
                aspectRatios.add(new AspectRatio("Portrait", 4, 5));
                isAspectToggleEnable = true;
            } else {
                isAspectToggleEnable = false;
            }

            setAspectRatios(aspectRatios);
        }

    }

    private float getOriginalAspectRatio(Uri fileUri) {
        SizeUtils sizeUtils = new SizeUtils(fileUri);
        return sizeUtils.getAspectRatio();
    }

    protected void updateVideo(Uri videoUri) {
//        showVideo(videoUri);
        selectedVideoUris.clear();
        selectedVideoUris.add(videoUri);
    }

    protected int imageSelectedToAdd(Uri imageUri) {
        if (alreadyContains(imageUri)) {
            return bringToFront(imageUri);
        } else {
            if (mediaCount == MAX_MEDIA) {
                return MediaState.MEDIA_MAX_LIMIT_REACHED;
            }
            addNewImage(imageUri);
        }
//        if (videoView.getParent() != null)
//            flAllCropViewsContainer.removeView(videoView);
        return MediaState.MEDIA_ADDED;
    }

//    protected int videoSelectedToAdd(Uri videoUri) {
//        if (selectedVideoUris.contains(videoUri)) {
//            return bringVideoToFront(videoUri);
//        } else {
//            if (mediaCount == MAX_MEDIA)
//                return MediaState.MEDIA_MAX_LIMIT_REACHED;
//            if (videoMediaCount == MAX_MEDIA_VIDEO)
//                return MediaState.VIDEO_MAX_LIMIT_REACHED;
//            addNewVideo(videoUri);
//        }
//        return MediaState.MEDIA_ADDED;
//    }

//    protected int bringVideoToFront(Uri videoUri) {
//        if (videoView.getParent() != null) {
//            if (videoView.getVideoUri().equals(videoUri)) {
//                Uri vidUri = getReplacementVideoUri(videoUri);
//                if (vidUri != null)
//                    showVideo(vidUri);
//                else {
//                    flAllCropViewsContainer.removeView(videoView);
//                    selectedVideoUris.clear();
//                    videoMediaCount = 0;
//                }
//                mediaCount--;
//                return MediaState.MEDIA_REMOVED;
//            }
//        }
//
//        showVideo(videoUri);
//        return MediaState.MEDIA_BROUGHT_TO_TOP;
//    }

    private Uri getReplacementVideoUri(Uri videoUri) {
        selectedVideoUris.remove(videoUri);
        if (selectedVideoUris.size() == 0)
            return null;
        else return selectedVideoUris.get(selectedVideoUris.size() - 1);
    }

    protected boolean alreadyContains(Uri imageUri) {

        for (CropContainerView view : allCropViews) {
            if (view.getInputUri().equals(imageUri))
                return true;
        }

        return false;
    }

    protected void addNewImage(Uri imageUri) {
        if (allCropViews.size() == 0) {
            processAspectRatios(imageUri);
            resetCropImageView();
            setImageData(imageUri);
        } else
            cropView = addCropContainerView();
        allCropViews.add(cropView);
        setupTargetAspectRatio();
        setImageData(imageUri);
        hideOtherViews();
        mediaCount++;
    }

    protected void addNewVideo(Uri videoUri) {
//        showVideo(videoUri);
        selectedVideoUris.add(videoUri);
        mediaCount++;
        videoMediaCount++;
    }

//    private void showVideo(Uri videoUri) {
//        if (videoView.getParent() == null)
//            flAllCropViewsContainer.addView(videoView);
//        videoView.setVideoURI(videoUri);
//    }

    protected int bringToFront(Uri mediaUri) {
        if (removeIfSelectedTopMedia(mediaUri)) {
//            if (videoView.getParent() != null) {
//                flAllCropViewsContainer.removeView(videoView);
//                return MediaState.MEDIA_BROUGHT_TO_TOP;
//            }
            mediaCount--;
            return MediaState.MEDIA_REMOVED;
        }

        for (CropContainerView view : allCropViews) {
            if (mediaUri.equals(view.getInputUri())) {
                view.setVisibility(View.VISIBLE);
                cropView = view;
                initCropViewChilds();
            } else view.setVisibility(View.GONE);
        }

//        if (videoView.getParent() != null)
//            flAllCropViewsContainer.removeView(videoView);

        return MediaState.MEDIA_BROUGHT_TO_TOP;
    }

//    private boolean removeIfSelectedTopMedia(Uri mediaUri) {
//        if (cropView.getInputUri().equals(mediaUri) && allCropViews.size() > 1) {
//            allCropViews.remove(cropView);
//            flAllCropViewsContainer.removeView(cropView);
//            cropView = (CropContainerView) flAllCropViewsContainer
//                    .getChildAt(flAllCropViewsContainer.getChildCount() - 1);
//            cropView.setVisibility(View.VISIBLE);
//            cropViewChanged(cropView);
//            return true;
//        }
//        return false;
//    }

    private boolean removeIfSelectedTopMedia(Uri mediaUri) {
        if (cropView.getInputUri().equals(mediaUri) && allCropViews.size() > 1) {
            allCropViews.remove(cropView);
            flAllCropViewsContainer.removeView(cropView);

            for (int i = 0; i < flAllCropViewsContainer.getChildCount(); i++) {
                View view = flAllCropViewsContainer.getChildAt(i);

                if (view instanceof CropContainerView) {
                    cropView = (CropContainerView) view;
                    break;
                }
            }

//            cropView = (CropContainerView) flAllCropViewsContainer
//                    .getChildAt(flAllCropViewsContainer.getChildCount() - 1);
            cropView.setVisibility(View.VISIBLE);
            cropViewChanged(cropView);
            return true;
        }
        return false;
    }

    protected void resetToSingleMedia() {
        for (CropContainerView view : allCropViews) {
            if (view.getId() != cropView.getId()) {
                flAllCropViewsContainer.removeView(view);
            }
        }
        allCropViews.clear();
        allCropViews.add(cropView);
        cropViewChanged(cropView);
        processAspectRatios(cropView.getInputUri());
        setCurrentAspectRatio(null);
        mediaCount = 0;
        videoMediaCount = 0;
        selectedVideoUris.clear();
    }

//    public boolean isVideoOnTop() {
//        return videoView.getParent() != null;
//    }

    private void hideOtherViews() {
        for (CropContainerView view : allCropViews) {
            if (view.getId() != cropView.getId()) {
                view.setVisibility(View.GONE);
            }
        }
    }

    protected void removeMultiSelectedMedia() {
        resetToSingleMedia();
//        isAspectToggleEnable = !isVideoOnTop();
    }

    protected boolean getIsAspectToggleEnable() {
        return isAspectToggleEnable;
    }

    protected boolean isMultipleMediaSelected() {
        return isMultipleMediaSelected;
    }

    protected void setMultipleMediaSelected(boolean multipleMediaSelected) {
        mediaCount = 0;
        if (multipleMediaSelected) {
            allCropViews.remove(cropView);
        }
        isMultipleMediaSelected = multipleMediaSelected;
    }

    protected void cropImage() {
        for (CropContainerView cropView : allCropViews) {

            cropView.getCropImageView().cropAndSaveImage(Bitmap.CompressFormat.PNG, 50, new BitmapCropCallback() {
                @Override
                public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
//                    Log.e(TAG, "onBitmapCropped: " + resultUri.toString());
                    count++;
                    processedUris.add(resultUri);
                    if (count == allCropViews.size()) {
                        callback.onCropFinish(new UCropFragment.UCropResult(RESULT_OK, new Intent()
                                .putParcelableArrayListExtra(AppConstants.CROP_IMAGES, new ArrayList<>(processedUris))));
                        resetMediaHandling();
                    }
                }

                @Override
                public void onCropFailure(@NonNull Throwable t) {
//                    Log.e(TAG, "onCropFailure: ", t);
                    callback.onCropFinish(new UCropFragment.UCropResult(RESULT_OK, new Intent()
                            .putParcelableArrayListExtra(AppConstants.CROP_IMAGES, processedUris)));
                }
            });
        }
    }

    private void resetMediaHandling() {
        count = 0;
        processedUris.clear();
    }

    public static class MediaState {
        public static final int MEDIA_ADDED = 1;
        public static final int MEDIA_BROUGHT_TO_TOP = 2;
        public static final int MEDIA_REMOVED = 3;
        public static final int MEDIA_MAX_LIMIT_REACHED = 9;
        public static final int VIDEO_ADDED = 5;
        public static final int VIDEO_MAX_LIMIT_REACHED = 6;
    }
}
