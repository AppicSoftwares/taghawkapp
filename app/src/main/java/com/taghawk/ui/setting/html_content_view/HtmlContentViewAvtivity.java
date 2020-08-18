package com.taghawk.ui.setting.html_content_view;


import android.os.Build;
import android.os.Bundle;

import android.text.Html;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.ActivityContentViewBinding;
import com.taghawk.model.ContentViewModel;
import com.taghawk.ui.home.HomeViewModel;

public class HtmlContentViewAvtivity extends BaseActivity implements View.OnClickListener {

    ActivityContentViewBinding mBinding;
    private int type;
    private HomeViewModel htmlViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_content_view);
        initView();
        getIntentData();
        setupViewModel();


    }

    private void initView() {
        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.includeHeader.ivCross.setOnClickListener(this);
    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_content_view;
    }

    private void setupViewModel() {
        htmlViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        htmlViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        htmlViewModel.getHtmlContentLiveData().observe(this, new Observer<ContentViewModel>() {
            @Override
            public void onChanged(@Nullable ContentViewModel contentViewModel) {
                getLoadingStateObserver().onChanged(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mBinding.tvProductDescription.setText(Html.fromHtml(contentViewModel.getMcontentView().getDescription(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    mBinding.tvProductDescription.setText(Html.fromHtml(contentViewModel.getMcontentView().getDescription()));
                }
            }
        });
        htmlViewModel.getHtmlContent(String.valueOf(type));
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.BUNDLE_DATA)) {
            type = getIntent().getExtras().getInt(AppConstants.BUNDLE_DATA);
            if (type == 1) {
                mBinding.includeHeader.tvTitle.setText(getString(R.string.privacy_policy));
            } else if (type == 2) {
                mBinding.includeHeader.tvTitle.setText(getString(R.string.terms_of_use));
            } else if (type == 3) {
                mBinding.includeHeader.tvTitle.setText(getString(R.string.faq));
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                finish();
                break;
        }
    }
}
