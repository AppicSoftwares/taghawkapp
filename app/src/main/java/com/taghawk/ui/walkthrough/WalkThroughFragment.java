package com.taghawk.ui.walkthrough;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.taghawk.base.BaseFragment;
import com.taghawk.databinding.FragmentWalkthroughFirstBinding;


/**
 * Created by app-server on 22/3/17.
 */

public class WalkThroughFragment extends BaseFragment {


    private FragmentWalkthroughFirstBinding mBinding;
    private AppCompatActivity mActivity;
    private String key;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mBinding = FragmentWalkthroughFirstBinding.inflate(inflater);
        return mBinding.getRoot();
    }
}
