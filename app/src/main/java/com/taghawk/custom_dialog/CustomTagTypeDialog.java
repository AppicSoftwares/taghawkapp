package com.taghawk.custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.taghawk.R;
import com.taghawk.databinding.LayoutTagTypeBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;

/**
 * Created by appinventiv on 27/3/18.
 */

public class CustomTagTypeDialog extends Dialog implements View.OnClickListener {

    private OnDialogViewClickListener dialogCallback;
    private LayoutTagTypeBinding mDialogBinding;
    private Context mActivity;
    private boolean isAllShow;
    private String titleOne, titleTwo;

    public CustomTagTypeDialog(@NonNull Context mActivity, String titleOne, String titleTwo, boolean isAllShow, OnDialogViewClickListener callback) {
        super(mActivity);
        dialogCallback = callback;
        this.mActivity = mActivity;
        this.isAllShow = isAllShow;
        this.titleOne = titleOne;
        this.titleTwo = titleTwo;

    }

    public CustomTagTypeDialog(@NonNull Context mActivity, String titleOne, boolean isAllShow, OnDialogViewClickListener callback) {
        super(mActivity);
        dialogCallback = callback;
        this.mActivity = mActivity;
        this.isAllShow = isAllShow;
        this.titleOne = titleOne;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogBinding = LayoutTagTypeBinding.inflate(LayoutInflater.from(getContext()));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(mDialogBinding.getRoot());
        getWindow().setGravity(Gravity.CENTER);
//
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {
        mDialogBinding.tvPrivate.setText(titleOne);
        if(titleTwo!= null && !TextUtils.isEmpty(titleTwo))
            mDialogBinding.tvPublic.setText(titleTwo);
        else
            mDialogBinding.tvPublic.setVisibility(View.GONE);
        mDialogBinding.tvPrivate.setOnClickListener(this);
        mDialogBinding.tvAll.setOnClickListener(this);
        mDialogBinding.tvPublic.setOnClickListener(this);
        if (isAllShow) {
            mDialogBinding.tvAll.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_private:
                dialogCallback.onSubmit(mDialogBinding.tvPrivate.getText().toString(), 1);
                break;
            case R.id.tv_public:
                dialogCallback.onSubmit(mDialogBinding.tvPublic.getText().toString(), 2);
//                Toast.makeText(mActivity, "This feature is currently unavailable", Toast.LENGTH_SHORT);
                break;
            case R.id.tv_all:
                dialogCallback.onSubmit(mDialogBinding.tvAll.getText().toString(), 0);
                break;

        }
        dismiss();

    }
}
