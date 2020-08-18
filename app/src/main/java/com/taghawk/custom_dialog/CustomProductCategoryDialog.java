package com.taghawk.custom_dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.taghawk.R;
import com.taghawk.adapters.CategoryProductListAdapter;
import com.taghawk.databinding.LayoutProductCategoryBinding;
import com.taghawk.interfaces.OnDialogItemObjectClickListener;
import com.taghawk.model.category.CategoryListResponse;

import java.util.ArrayList;

/**
 * Created by appinventiv on 27/3/18.
 */

public class CustomProductCategoryDialog extends Dialog implements View.OnClickListener {

    private OnDialogItemObjectClickListener dialogCallback;
    private LayoutProductCategoryBinding mDialogBinding;
    private Context mActivity;
    private ArrayList<CategoryListResponse> mList;

    public CustomProductCategoryDialog(@NonNull Context mActivity, ArrayList<CategoryListResponse> mList, OnDialogItemObjectClickListener callback) {
        super(mActivity);
        dialogCallback = callback;
        this.mActivity = mActivity;
        this.mList = mList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogBinding = LayoutProductCategoryBinding.inflate(LayoutInflater.from(getContext()));
        this.setContentView(mDialogBinding.getRoot());
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {
        mDialogBinding.ivClose.setOnClickListener(this);
        mDialogBinding.rvCategory.setLayoutManager(new LinearLayoutManager(mActivity));
        mDialogBinding.rvCategory.setAdapter(new CategoryProductListAdapter(mActivity, mList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryListResponse bean = (CategoryListResponse) v.getTag();
                dialogCallback.onPositiveBtnClick(bean);
                dismiss();
            }
        }));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
        }
    }
}
