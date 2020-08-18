package com.taghawk.custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.taghawk.R;
import com.taghawk.databinding.LayoutReportEventBinding;

/**
/**
 * Created by appinventiv on 27/3/18.
 */

public class CustomReportDialog extends Dialog implements View.OnClickListener {

    private DialogCallback dialogCallback;
    private LayoutReportEventBinding mDialogBinding;
    private Context mActivity;
    private String reason = "";

    public CustomReportDialog(@NonNull Context mActivity, DialogCallback callback) {
        super(mActivity);
        dialogCallback = callback;
        this.mActivity = mActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogBinding = LayoutReportEventBinding.inflate(LayoutInflater.from(getContext()));
        this.setContentView(mDialogBinding.getRoot());
        getWindow().setGravity(Gravity.CENTER);
        setCancelable(false);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        initView();
    }

    private void initView() {

        mDialogBinding.tvCancel.setOnClickListener(this);
        mDialogBinding.tvCancel.setOnClickListener(this);
        mDialogBinding.tvReport.setOnClickListener(this);
        mDialogBinding.rgReport.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = group.findViewById(checkedId);
                if (button.getId() == R.id.rb_other) {
                    mDialogBinding.etOtherTxt.setVisibility(View.VISIBLE);
                } else {
                    mDialogBinding.etOtherTxt.setVisibility(View.GONE);
                    reason = button.getText().toString();
                }


            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_report:
                if (validate()) {
                    if (mDialogBinding.etOtherTxt.getVisibility() == View.VISIBLE) {
                        dialogCallback.submit(mDialogBinding.etOtherTxt.getText().toString().trim());
                    } else {
                        dialogCallback.submit(reason);
                    }
                    dismiss();
                }
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }

    }

    private boolean validate() {
        if (mDialogBinding.etOtherTxt.getVisibility() == View.VISIBLE && mDialogBinding.etOtherTxt.getText().toString().trim().length() == 0) {
            Toast.makeText(mActivity, mActivity.getString(R.string.please_enter_reason), Toast.LENGTH_SHORT).show();
            return false;
        } else if (reason.length() == 0 && mDialogBinding.etOtherTxt.getText().toString().trim().length() == 0) {
            Toast.makeText(mActivity, mActivity.getString(R.string.please_enter_reason), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
