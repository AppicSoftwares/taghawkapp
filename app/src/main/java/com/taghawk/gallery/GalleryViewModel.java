package com.taghawk.gallery;



import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class GalleryViewModel extends ViewModel {

    private GalleryRepository galleryRepository;
    private LiveData<List<GalleryMediaBean>> mediaList;

    /**
     * @param galleryRepository a repository class to perform operations on gallery
     */
    public GalleryViewModel(@NonNull GalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
        mediaList = galleryRepository.getMedialist();
    }

    public LiveData<List<GalleryMediaBean>> getMediaList() {
        return mediaList;
    }


}