package com.taghawk.custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.taghawk.R;
import com.taghawk.databinding.DialogEmailRequiredBinding;

/**
 * Created by appinventiv on 27/3/18.
 */

public class CustomEmailRequiredDialog extends Dialog implements View.OnClickListener {

    private DialogCallback dialogCallback;
    private String title;
    private String message;
    private DialogEmailRequiredBinding mDialogBinding;
    private Context mActivity;

    public CustomEmailRequiredDialog(@NonNull Context mActivity, DialogCallback callback) {
        super(mActivity);
        dialogCallback = callback;
        this.title = title;
        this.mActivity = mActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogBinding = DialogEmailRequiredBinding.inflate(LayoutInflater.from(getContext()));
        this.setContentView(mDialogBinding.getRoot());
        getWindow().setGravity(Gravity.CENTER);
        setCancelable(false);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {

        mDialogBinding.tvOk.setOnClickListener(this);
        mDialogBinding.tvCancel.setOnClickListener(this);
        mDialogBinding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDialogBinding.textInputEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDialogBinding.etEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dialogCallback.submit(mDialogBinding.etEmail.getText().toString().trim());
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if (validate()) {
                    dialogCallback.submit(mDialogBinding.etEmail.getText().toString().trim());
                    dismiss();
                }
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }

    }

    private boolean validate() {
        if (mDialogBinding.etEmail.getText().toString().trim().length() == 0) {
            mDialogBinding.textInputEmail.setErrorEnabled(true);
            mDialogBinding.textInputEmail.setError(mActivity.getString(R.string.enter_email));
            return false;
        }
        return true;
    }
}
