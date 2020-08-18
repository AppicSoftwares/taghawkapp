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
import com.taghawk.databinding.LayoutTagTypeBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;

/**
 * Created by appinventiv on 27/3/18.
 */

public class CustomAddressTypeDialog extends Dialog implements View.OnClickListener {

    private OnDialogViewClickListener dialogCallback;
    private LayoutTagTypeBinding mDialogBinding;
    private Context mActivity;
    private boolean isAllShow;

    public CustomAddressTypeDialog(@NonNull Context mActivity, boolean isAllShow, OnDialogViewClickListener callback) {
        super(mActivity);
        dialogCallback = callback;
        this.mActivity = mActivity;
        this.isAllShow = isAllShow;
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

        mDialogBinding.tvPublic.setText(mActivity.getString(R.string.office));
        mDialogBinding.tvPrivate.setText(mActivity.getString(R.string.residential));
        mDialogBinding.tvPrivate.setOnClickListener(this);
        mDialogBinding.tvAll.setVisibility(View.GONE);
        mDialogBinding.tvPublic.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_private:
                dialogCallback.onSubmit(mDialogBinding.tvPrivate.getText().toString(), 2);
                break;
            case R.id.tv_public:
                dialogCallback.onSubmit(mDialogBinding.tvPublic.getText().toString(), 2);
                break;

        }
        dismiss();

    }
}
