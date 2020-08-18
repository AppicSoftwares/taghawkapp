package com.taghawk.ui.gift;

import android.app.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.taghawk.R;
import com.taghawk.adapters.PromotionsListAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.databinding.FragmentRewardsPointsBinding;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.gift.GiftRewardsPromotionData;
import com.taghawk.model.gift.GiftRewardsPromotionModel;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;

import java.util.ArrayList;

public class GiftRewardsPromotionFragment extends BaseFragment implements View.OnClickListener {

    private ArrayList<GiftRewardsPromotionData> mPromotionList;
    private GiftRewardsPromotionViewModel mGiftPromotionViewModel;
    private Activity mActivity;
    private PromotionsListAdapter adapter;
    private FragmentRewardsPointsBinding mBinding;
    private int position;
    private GiftRewardsPromotionModel mData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = FragmentRewardsPointsBinding.inflate(inflater);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mActivity = getActivity();
        mBinding.includeHeader.tvReset.setVisibility(View.GONE);
        mBinding.includeHeader.tvTitle.setVisibility(View.VISIBLE);
        mBinding.includeHeader.tvTitle.setText(getString(R.string.my_rewards));
        mBinding.includeHeader.ivBack.setOnClickListener(this);

        setUpView();
        setSpanableText();
    }

    private void setSpanableText() {
        String str = mActivity.getString(R.string.you_can_redeem_rewards_points_lorem_know_more);
        Spannable spannable = new SpannableString(str);
        spannable.setSpan(new ForegroundColorSpan(mActivity.getResources().getColor(R.color.colorPrimary)), str.length() - 9, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new UnderlineSpan(), str.length() - 9, str.length(),  0);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View v) {
                DialogUtil.getInstance().customBottomSheetDialogKnowMore(mActivity);
            }
        }, str.length() - 9, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBinding.tvKnowMore.setText(spannable);
        mBinding.tvKnowMore.setHighlightColor(Color.TRANSPARENT);
        mBinding.tvKnowMore.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void setUpView() {
        mPromotionList = new ArrayList<>();
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        adapter = new PromotionsListAdapter(mPromotionList, this);
        mBinding.rvPromotionalOffer.setLayoutManager(layoutManager);
        mBinding.rvPromotionalOffer.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewModel();
    }

    private void setUpViewModel() {
        mGiftPromotionViewModel = ViewModelProviders.of(this).get(GiftRewardsPromotionViewModel.class);
        mGiftPromotionViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mGiftPromotionViewModel.getGiftPromotionLiveData().observe(this, new Observer<GiftRewardsPromotionModel>() {
            @Override
            public void onChanged(@Nullable GiftRewardsPromotionModel bean) {
                getLoadingStateObserver().onChanged(false);
                if (bean != null) {
                    mData = bean;
                    mBinding.tvRewards.setText("" + bean.getRewardPoint());
                    if (bean.getmPromotionList() != null && bean.getmPromotionList().size() > 0) {
                        mPromotionList.addAll(bean.getmPromotionList());
                        adapter.notifyDataSetChanged();
                    }

                }
            }
        });
        if (AppUtils.isInternetAvailable(mActivity))
            mGiftPromotionViewModel.getGiftPromotions();
        else
            showNoNetworkError();
    }

    public void updateRewardsPoints(int rewardsPoints) {
        mBinding.tvRewards.setText("" + rewardsPoints);
        mData.setRewardPoint(rewardsPoints);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                ((GiftRewardPromotionActivity) mActivity).onBackPressed();
                break;
            case R.id.ll_main:
                if (mData != null) {
                    position = (int) v.getTag();
                    ((GiftRewardPromotionActivity) mActivity).addRewardsRedeemFragment(mPromotionList.get(position), mData.getRewardPoint());
                }
                break;
        }
    }
}
