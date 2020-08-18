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
import com.taghawk.databinding.LayoutConditionBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;

/**
 * Created by appinventiv on 27/3/18.
 */

public class CustomProductCondition extends Dialog implements View.OnClickListener {

    private OnDialogViewClickListener dialogCallback;
    private LayoutConditionBinding mDialogBinding;
    private Context mActivity;

    public CustomProductCondition(@NonNull Context mActivity, OnDialogViewClickListener callback) {
        super(mActivity);
        dialogCallback = callback;
        this.mActivity = mActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogBinding = LayoutConditionBinding.inflate(LayoutInflater.from(getContext()));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(mDialogBinding.getRoot());
        getWindow().setGravity(Gravity.CENTER);
//
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {
        mDialogBinding.tvNew.setOnClickListener(this);
        mDialogBinding.tvNormal.setOnClickListener(this);
        mDialogBinding.tvLiklyNew.setOnClickListener(this);
        mDialogBinding.tvGood.setOnClickListener(this);
        mDialogBinding.tvFlawed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_new:
                dialogCallback.onSubmit(mDialogBinding.tvNew.getText().toString(), 1);
                break;
            case R.id.tv_normal:
                dialogCallback.onSubmit(mDialogBinding.tvNormal.getText().toString(), 3);
                break;
            case R.id.tv_likly_new:
                dialogCallback.onSubmit(mDialogBinding.tvLiklyNew.getText().toString(), 2);
                break;
            case R.id.tv_good:
                dialogCallback.onSubmit(mDialogBinding.tvGood.getText().toString(), 4);
                break;
            case R.id.tv_flawed:
                dialogCallback.onSubmit(mDialogBinding.tvFlawed.getText().toString(), 5);
                break;

        }
        dismiss();

    }
}
