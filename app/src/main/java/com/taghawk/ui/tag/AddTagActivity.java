package com.taghawk.ui.tag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.LayoutAddTagBinding;
import com.taghawk.model.tag.TagData;
import com.taghawk.util.AppUtils;

public class AddTagActivity extends BaseActivity implements View.OnClickListener, AddTagStepOne.IAddTagHost {

    private LayoutAddTagBinding mBinding;
    private AddTagStepOne fragment;
    private FragmentManager mFragmentManager;

    public TagData getTagData() {
        return tagData;
    }

    public void setTagData(TagData tagData) {
        this.tagData = tagData;
    }

    private TagData tagData;

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
        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);

        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black));
        mBinding.includeHeader.tvTitle.setText(getString(R.string.add_details));
        mBinding.includeHeader.tvReset.setVisibility(View.GONE);
        mBinding.includeHeader.ivShare.setVisibility(View.GONE);
        mBinding.includeHeader.ivCross.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.ACTIVITY_RESULT.GPS_ENABLE)
            if (fragment != null && fragment instanceof AddTagStepOne)
                fragment.onActivityResult(requestCode, resultCode, data);
    }

    private void addFilterFragment() {
        fragment = new AddTagStepOne();
        addFragmentWithBackstack(R.id.home_container, fragment, AddTagStepOne.class.getSimpleName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (fragment != null && fragment instanceof AddTagStepOne)
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                Intent intent = new Intent();
                intent.putExtra("TAG_CREATED", tagData);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void showStepTwoFragment(Bundle bundle) {
        mBinding.tvStepTwoCircle.setBackgroundDrawable(getResources().getDrawable(R.drawable.filled_color_primary_circle));
        mBinding.tvStepTwoCircle.setTextColor(getResources().getColor(R.color.White));
        mBinding.tvStepOneCircle.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_circle_border_grey));
        mBinding.tvStepOneCircle.setTextColor(getResources().getColor(R.color.txt_black));
        mBinding.includeHeader.tvTitle.setText(getString(R.string.toolbar_h_refer_friend));
        AddTagStepTwo fragment = new AddTagStepTwo();
        fragment.setArguments(bundle);
        replaceFragment(R.id.home_container, fragment, AddTagStepTwo.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
