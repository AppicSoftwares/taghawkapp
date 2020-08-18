package com.taghawk.ui.walkthrough;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.taghawk.BuildConfig;
import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.FragmentWalkThroughNextBinding;


/**
 * Created by app-server on 22/3/17.
 */

public class WalkThroughFirstFragment extends BaseFragment {

    private FragmentWalkThroughNextBinding mBinding;
    private AppCompatActivity mActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentWalkThroughNextBinding.inflate(inflater);
//        initYoutube();
        getArgumentsData();
        return mBinding.getRoot();
    }

    private void getArgumentsData() {
        if (getArguments() != null && getArguments().containsKey(AppConstants.BUNDLE_DATA)) {
            pageAction(getArguments().getInt(AppConstants.BUNDLE_DATA));
        }

    }

    public void pageAction(int action) {
        switch (action) {
            case 1:
                setImageFunction(getResources().getDrawable(R.drawable.videoscreen));
                break;
            case 2:
                setImageFunction(getResources().getDrawable(R.drawable.abc4));
                break;
            case 3:
                setImageFunction(getResources().getDrawable(R.drawable.abc2));
                break;
            case 4:
                setImageFunction(getResources().getDrawable(R.drawable.abc1));
                break;
        }
    }

    private void setImageFunction(Drawable drawable) {
        mBinding.img.setImageDrawable(drawable);


        mBinding.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("vishal","ljsdhf");
            }
        });
    }
    /**
     * Method to Initialize Youtube
     */
//    private void initYoutube() {
//        mActivity = (AppCompatActivity) getActivity();
//        mBinding.playerContainer.setLayoutParams(new LinearLayout.LayoutParams(AppUtils.getDisplayMetrics(mActivity).widthPixels - 100, 850));
//        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.add(R.id.player_container, youTubePlayerFragment).commit();
//        youTubePlayerFragment.initialize(AppConstants.YOUTUBE_KEY, this);
//    }

//    @Override
//    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//        if (!b)
//            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
//        youTubePlayer.cueVideo(AppUtils.getVideoId("https://www.youtube.com/watch?v=m1pnwFSdOLU&feature=youtu.be"));
//    }
//
//    @Override
//    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//    }
}
