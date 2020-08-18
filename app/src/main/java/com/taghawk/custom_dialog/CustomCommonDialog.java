package com.taghawk.custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.taghawk.R;
import com.taghawk.databinding.DialogCommonBinding;

public class CustomCommonDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private String title, msg, okButton, cancelButton;
    private DialogCallback dialogCallback;
    private DialogCommonBinding mDialogBinding;

    public CustomCommonDialog(Context context, String title, String msg, String okButton, String cancelButton, DialogCallback dialogCallback) {
        super(context);
        this.context = context;
        this.title = title;
        this.msg = msg;
        this.okButton = okButton;
        this.cancelButton = cancelButton;
        this.dialogCallback = dialogCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogBinding = DialogCommonBinding.inflate(LayoutInflater.from(getContext()));
        this.setContentView(mDialogBinding.getRoot());
        getWindow().setGravity(Gravity.CENTER);
        setCancelable(false);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialogBinding.tvMessage.setText(msg);
        if (title.length() > 0)
            mDialogBinding.tvTitle.setText(title);
        else {
            mDialogBinding.tvTitle.setVisibility(View.GONE);
        }
        mDialogBinding.tvCancel.setText(cancelButton);
        mDialogBinding.tvShare.setText(okButton);
        mDialogBinding.tvShare.setOnClickListener(this);
        mDialogBinding.tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                dialogCallback.cancel();
                break;
            case R.id.tv_share:
                dialogCallback.submit("");
                dismiss();
                break;
        }
    }
}
