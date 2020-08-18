package com.taghawk.custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.taghawk.R;
import com.taghawk.databinding.LayoutTagType2Binding;
import com.taghawk.interfaces.OnDialogViewClickListener;

/**
 * Created by appinventiv on 27/3/18.
 */

public class CustomTagType_2Dialog extends Dialog implements View.OnClickListener {

    private OnDialogViewClickListener dialogCallback;
    private LayoutTagType2Binding mDialogBinding;
    private Context mActivity;
    private boolean isAllShow;
    private String titleOne, titleTwo, titleThree, titleFour, titleFive;

    public CustomTagType_2Dialog(@NonNull Context mActivity, String titleOne, String titleTwo, String titleThree, String titleFour, String titleFive, boolean isAllShow, OnDialogViewClickListener callback) {
        super(mActivity);
        dialogCallback = callback;
        this.mActivity = mActivity;
        this.isAllShow = isAllShow;
        this.titleOne = titleOne;
        this.titleTwo = titleTwo;
        this.titleThree = titleThree;
        this.titleFour = titleFour;
        this.titleFive = titleFive;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogBinding = LayoutTagType2Binding.inflate(LayoutInflater.from(getContext()));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(mDialogBinding.getRoot());
        getWindow().setGravity(Gravity.CENTER);
//
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {
        mDialogBinding.tvApartment.setText(titleOne);
        mDialogBinding.tvUniversities.setText(titleTwo);
        mDialogBinding.tvOrganization.setText(titleThree);
        mDialogBinding.tvClub.setText(titleFour);
        mDialogBinding.tvOther.setText(titleFive);
        mDialogBinding.tvApartment.setOnClickListener(this);
        mDialogBinding.tvAll.setOnClickListener(this);
        mDialogBinding.tvUniversities.setOnClickListener(this);
        mDialogBinding.tvOrganization.setOnClickListener(this);
        mDialogBinding.tvClub.setOnClickListener(this);
        mDialogBinding.tvOther.setOnClickListener(this);
        if (isAllShow) {
            mDialogBinding.tvAll.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_Apartment:
                dialogCallback.onSubmit(mDialogBinding.tvApartment.getText().toString(), 1);
                break;
            case R.id.tv_Universities:
                dialogCallback.onSubmit(mDialogBinding.tvUniversities.getText().toString(), 2);
                break;
            case R.id.tv_Organization:
                dialogCallback.onSubmit(mDialogBinding.tvOrganization.getText().toString(), 3);
                break;
            case R.id.tv_Club:
                dialogCallback.onSubmit(mDialogBinding.tvClub.getText().toString(), 4);
                break;
            case R.id.tv_Other:
                dialogCallback.onSubmit(mDialogBinding.tvOther.getText().toString(), 5);
                break;
            case R.id.tv_all:
                dialogCallback.onSubmit(mDialogBinding.tvAll.getText().toString(), 0);
                break;

        }
        dismiss();

    }
}
