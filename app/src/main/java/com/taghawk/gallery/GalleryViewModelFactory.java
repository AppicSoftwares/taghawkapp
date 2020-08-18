package com.taghawk.gallery;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GalleryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private GalleryRepository galleryRepository;

    public GalleryViewModelFactory(GalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GalleryViewModel(galleryRepository);
    }

}
