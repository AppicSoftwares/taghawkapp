package com.taghawk.gallery;


import android.net.Uri;

import java.io.Serializable;

/**
 * Created by appinventiv on 7/9/17.
 */

public class GalleryMediaBean implements Serializable {

    // Media Types -  1. Image 2. Video

    private String mediaPath;
    private int mediaType = 0;
    private int selectedPosition = 0;
    private Uri uri;
    private boolean selected;
//    private Filter mFilters;

//    public Filter getmFilters() {
//        return mFilters;
//    }
//
//    public void setmFilters(Filter mFilters) {
//        this.mFilters = mFilters;
//    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }


    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }


}
