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
import com.taghawk.databinding.LayoutPostedWithInBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;

/**
 * Created by appinventiv on 27/3/18.
 */

public class CustomPostInDialog extends Dialog implements View.OnClickListener {

    private OnDialogViewClickListener dialogCallback;
    private LayoutPostedWithInBinding mDialogBinding;
    private Context mActivity;

    public CustomPostInDialog(@NonNull Context mActivity, OnDialogViewClickListener callback) {
        super(mActivity);
        dialogCallback = callback;
        this.mActivity = mActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogBinding = LayoutPostedWithInBinding.inflate(LayoutInflater.from(getContext()));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(mDialogBinding.getRoot());
        getWindow().setGravity(Gravity.CENTER);
//
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {
        mDialogBinding.tvYesterday.setOnClickListener(this);
        mDialogBinding.tvToday.setOnClickListener(this);
        mDialogBinding.tvLastThreeMonth.setOnClickListener(this);
        mDialogBinding.tvThisMonth.setOnClickListener(this);
        mDialogBinding.tvThisWeek.setOnClickListener(this);
        mDialogBinding.tvThisYear.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_today:
                dialogCallback.onSubmit(mDialogBinding.tvToday.getText().toString(), 1);
                break;
            case R.id.tv_this_month:
                dialogCallback.onSubmit(mDialogBinding.tvThisMonth.getText().toString(), 3);
                break;
            case R.id.tv_this_week:
                dialogCallback.onSubmit(mDialogBinding.tvThisWeek.getText().toString(), 2);
                break;
            case R.id.tv_this_year:
                dialogCallback.onSubmit(mDialogBinding.tvThisYear.getText().toString(), 5);
                break;
            case R.id.tv_last_three_month:
                dialogCallback.onSubmit(mDialogBinding.tvLastThreeMonth.getText().toString(), 4);
                break;
        }
        dismiss();

    }
}
