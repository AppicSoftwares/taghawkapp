package com.taghawk.ui.tag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.LayoutAddTagBinding;
import com.taghawk.model.tag.TagDetailsData;

public class EditTagActivity extends BaseActivity implements View.OnClickListener {

    private LayoutAddTagBinding mBinding;
    private EditTagFragment fragment;
    private FragmentManager mFragmentManager;
    TagDetailsData mData;

    @Override
    protected int getResourceId() {
        return R.layout.layout_add_tag;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.layout_add_tag);
        mFragmentManager = getSupportFragmentManager();
        initView();
        addFilterFragment();
    }

    private void initView() {
        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black));
        mBinding.includeHeader.tvTitle.setText(getString(R.string.tag_edit));
        mBinding.includeHeader.tvReset.setVisibility(View.GONE);
        mBinding.includeHeader.ivShare.setVisibility(View.GONE);
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        getIntentData();
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mData = (TagDetailsData) getIntent().getExtras().getParcelable(AppConstants.BUNDLE_DATA);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.ACTIVITY_RESULT.GPS_ENABLE)
            if (fragment != null && fragment instanceof EditTagFragment)
                fragment.onActivityResult(requestCode, resultCode, data);
    }

    private void addFilterFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.BUNDLE_DATA, mData);
        fragment = new EditTagFragment();
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, EditTagFragment.class.getSimpleName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
