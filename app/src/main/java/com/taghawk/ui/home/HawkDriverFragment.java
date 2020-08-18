package com.taghawk.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taghawk.base.BaseFragment;
import com.taghawk.databinding.LayoutUnderDevelopmentBinding;

public class HawkDriverFragment extends BaseFragment {

    private LayoutUnderDevelopmentBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = LayoutUnderDevelopmentBinding.inflate(inflater);
        return mBinding.getRoot();
    }
}
