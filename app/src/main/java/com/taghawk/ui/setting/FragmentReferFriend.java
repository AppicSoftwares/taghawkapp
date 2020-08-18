package com.taghawk.ui.setting;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.AddTagStepTwoBinding;

public class FragmentReferFriend extends BaseFragment implements View.OnClickListener {

    private AddTagStepTwoBinding mBinding;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = AddTagStepTwoBinding.inflate(inflater);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mBinding.tvShare.setOnClickListener(this);
        mBinding.tvReferCode.setOnClickListener(this);
        mBinding.includeHeader.setVisibility(View.VISIBLE);
        mBinding.header.tvTitle.setText(getString(R.string.refer_friend));
        mBinding.header.ivCross.setOnClickListener(this);
        mBinding.tvTitle.setText("Refer your neighbors to join TagHawk and get aditional benefits.");
        mBinding.tvReferDescription.setText("Share the following referral code with your friends and get 50 reward points for both of you");
        mActivity = getActivity();
        if (DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getInvitationCode().length() > 0) {
            mBinding.tvReferCode.setText(DataManager.getInstance().getUserDetails().getInvitationCode());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share:
                performShareAction("Register on TagHawk with " + DataManager.getInstance().getUserDetails().getInvitationCode() + " and earn 50 rewards points.");
                break;
            case R.id.tv_refer_code:
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.invite_code), DataManager.getInstance().getUserDetails().getInvitationCode());
                clipboard.setPrimaryClip(clip);
                showToastShort(getString(R.string.clipboard_copied));
                break;
            case R.id.iv_cross:
                mActivity.finish();
                break;
        }
    }
}
