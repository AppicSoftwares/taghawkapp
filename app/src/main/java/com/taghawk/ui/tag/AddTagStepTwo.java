package com.taghawk.ui.tag;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.AddTagStepTwoBinding;
import com.taghawk.model.tag.TagData;

public class AddTagStepTwo extends BaseFragment implements View.OnClickListener {

    private AddTagStepTwoBinding mBinding;
    private AppCompatActivity mActivity;
    private Bundle bundle;
    private TagData mTagReferDetail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = AddTagStepTwoBinding.inflate(inflater, container, false);
        initVariables();
        initView();
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewModel();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setUpViewModel() {

    }

    /**
     * Method to Initialize Variables
     */
    private void initVariables() {
        mActivity = (AppCompatActivity) getActivity();
//        bundle = getArguments();

        if (getArguments() != null) {
            if (getArguments().containsKey(AppConstants.TAG_KEY_CONSTENT.TAG_REFER_INFO))
                mTagReferDetail = (TagData) getArguments().getParcelable(AppConstants.TAG_KEY_CONSTENT.TAG_REFER_INFO);

        }
    }

    private void initView() {
        mBinding.tvShare.setOnClickListener(this);
        mBinding.tvReferCode.setOnClickListener(this);
        if (mTagReferDetail != null) {
            if (!TextUtils.isEmpty(mTagReferDetail.getShareCode()))
                mBinding.tvReferCode.setText(mTagReferDetail.getShareCode());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share:
                if (mTagReferDetail != null && mTagReferDetail.getShareLink() != null) {
                    performShareAction(mTagReferDetail.getShareLink());
                } else {
                    showToastShort(getResources().getString(R.string.something_went_wrong));
                }
                break;
            case R.id.tv_refer_code:
                if (mTagReferDetail != null && mTagReferDetail.getShareCode() != null) {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(getString(R.string.invite_code), mTagReferDetail.getShareCode());
                    clipboard.setPrimaryClip(clip);
                    showToastShort(getString(R.string.clipboard_copied));
                } else {
                    showToastShort(getResources().getString(R.string.something_went_wrong));
                }
                break;
        }
    }

}
