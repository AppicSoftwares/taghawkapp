package com.taghawk.ui.setting;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentSettingBinding;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.setting.block_user.BlockUserActivity;
import com.taghawk.ui.setting.change_password.ChangePasswordActivity;
import com.taghawk.ui.setting.html_content_view.HtmlContentViewAvtivity;
import com.taghawk.util.AppUtils;

public class SettingFragment extends BaseFragment implements View.OnClickListener {
    private FragmentSettingBinding mBinding;
    private HomeViewModel settingViewModel;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentSettingBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mActivity = getActivity();
        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.includeHeader.tvTitle.setText(getString(R.string.setting));
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.tvLogout.setOnClickListener(this);
        mBinding.tvReferal.setOnClickListener(this);
        mBinding.changePassword.setOnClickListener(this);
        mBinding.tvBlockUsers.setOnClickListener(this);
        mBinding.tvFaq.setOnClickListener(this);
        mBinding.tvPrivacyPolicy.setOnClickListener(this);
        mBinding.tvTermsUse.setOnClickListener(this);
        mBinding.tbDisablePush.setOnClickListener(this);
        if ((DataManager.getInstance() != null && !DataManager.getInstance().getIsMuteStatus())) {
            mBinding.tbDisablePush.setChecked(true);
        } else {
            mBinding.tbDisablePush.setChecked(false);

        }

        if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getLoginType() != null && DataManager.getInstance().getUserDetails().getLoginType().equalsIgnoreCase("FACEBOOK")) {
            mBinding.changePassword.setVisibility(View.GONE);
        } else
            mBinding.changePassword.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        settingViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        settingViewModel.logout().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse productListingModel) {
                getLoadingStateObserver().onChanged(false);
                if (productListingModel != null && productListingModel.getCode() == 200) {
                    ((HomeActivity) mActivity).logOutSuccess();
                }
            }
        });
        settingViewModel.getmFeedBackLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (DataManager.getInstance() != null && !DataManager.getInstance().getIsMuteStatus()) {
                    DataManager.getInstance().saveIsMuteStatus(true);
                } else {
                    DataManager.getInstance().saveIsMuteStatus(false);

                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_logout:
                if (settingViewModel != null) {
                    mBinding.tvLogout.setClickable(false);
                    settingViewModel.hitLogOut(getDeviceId());
                }
                break;
            case R.id.tv_referal:
                openReferalScreen();
                break;
            case R.id.change_password:
                openChangePassword();
                break;
            case R.id.iv_cross:
                ((HomeActivity) mActivity).onBackPressed();
                break;
            case R.id.tv_block_users:
                openBlockUserList();
                break;
            case R.id.tv_faq:
                openHTMLcontentView(3);
                break;
            case R.id.tv_terms_use:
                openHTMLcontentView(2);
                break;
            case R.id.tv_privacy_policy:
                openHTMLcontentView(1);
                break;
            case R.id.tb_disable_push:
                if (AppUtils.isInternetAvailable(mActivity)) {
                    if (DataManager.getInstance() != null && DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().isMute()) {
                        settingViewModel.notificationOnOff(0);
                    } else {
                        settingViewModel.notificationOnOff(1);

                    }

                } else {
                    showNoNetworkError();
                }
                break;

        }


    }

    private void openBlockUserList() {
        Intent intent = new Intent(mActivity, BlockUserActivity.class);
        startActivity(intent);
    }

    private void openHTMLcontentView(int type) {
        Intent intent = new Intent(mActivity, HtmlContentViewAvtivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, type);
        startActivity(intent);
    }

    private void openChangePassword() {
        Intent intent = new Intent(mActivity, ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void openReferalScreen() {
        Intent intent = new Intent(mActivity, ReferalActivity.class);
        startActivity(intent);
    }
}
