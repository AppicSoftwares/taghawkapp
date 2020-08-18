package com.taghawk.gallery;

import android.app.Application;



/**
 * Static methods used to inject classes needed for various activities and fragments
 */
public class InjectorUtils {

    public static GalleryViewModelFactory provideGalleryViewModelFactory(Application application) {
        return new GalleryViewModelFactory(GalleryRepository.getInstance(application));
    }

//    public static PostMediaViewModelFactory providePostMediaViewModelFactory(Application application){
//        return new PostMediaViewModelFactory(PostMediaRepository.init(application));
//    }
//
//    public static FeedsViewModelFactory provideFeedViewModelFactory(FeedsRepository feedsRepository, FeedsDataSource feedsDataSource){
//        return new FeedsViewModelFactory(feedsRepository, new FeedsDataSourceFactory(feedsDataSource));
//    }
//
//    public static BlockedUserViewModelFactory provideBlockedUsersViewModelFactory(BlockedUsersDataSourceFactory blockedUsersDataSourceFactory){
//        return new BlockedUserViewModelFactory(blockedUsersDataSourceFactory);
//    }
//
//    public static LikedUsersViewModelFactory provideLikedUsersViewModelFactory(LikedUsersDataSourceFactory likedUsersDataSourceFactory){
//        return new LikedUsersViewModelFactory(likedUsersDataSourceFactory);
//    }
//
//    public static LikedPostViewModelFactory provideLikedPostsViewModelFactory(LikedPostsDataSourceFactory likedPostsDataSourceFactory){
//        return new LikedPostViewModelFactory(likedPostsDataSourceFactory);
//    }

}