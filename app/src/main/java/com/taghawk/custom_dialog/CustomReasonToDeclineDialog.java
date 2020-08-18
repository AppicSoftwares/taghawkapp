package com.taghawk.custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.taghawk.R;
import com.taghawk.databinding.DialogReasonForDeclineBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;

/**
 * Created by appinventiv on 27/3/18.
 */

public class CustomReasonToDeclineDialog extends Dialog implements View.OnClickListener {

    private OnDialogViewClickListener dialogCallback;
    private DialogReasonForDeclineBinding mDialogBinding;
    private Context mActivity;


    public CustomReasonToDeclineDialog(@NonNull Context mActivity, OnDialogViewClickListener callback) {
        super(mActivity);
        dialogCallback = callback;
        this.mActivity = mActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogBinding = DialogReasonForDeclineBinding.inflate(LayoutInflater.from(getContext()));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(mDialogBinding.getRoot());
        getWindow().setGravity(Gravity.CENTER);
//
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {
        mDialogBinding.tvSend.setOnClickListener(this);
        mDialogBinding.tvCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send:
                if (mDialogBinding.etReason.getText().toString().trim().length() > 0) {
                    dialogCallback.onSubmit(mDialogBinding.etReason.getText().toString(), 0);
                    dismiss();
                } else {
                    Toast.makeText(mActivity, "Please enter reason for decline", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }

    }
}
