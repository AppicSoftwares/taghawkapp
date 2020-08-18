package com.taghawk.ui.chat;


import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.taghawk.R;
import com.taghawk.TagHawkApplication;
import com.taghawk.adapters.ViewPagerAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.custom_view.PositionedLinkedHashmap;
import com.taghawk.databinding.FragmentChatBinding;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.notification.NotificationFragment;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class ChatFragment extends BaseFragment implements View.OnClickListener {

    /**
     * A {@link HomeViewModel} object to handle all the actions and business logic
     */
    private FragmentChatBinding mBinding;
    private Activity mActivity;
    private ViewPagerAdapter viewPagerAdapter;
    private float originalBackgroundTranslationX;
    private ArgbEvaluator argbEvaluator;
    private int height;

    /**
     * This method is used to return the instance of this fragment
     *
     * @return new instance of {@link ChatFragment}
     */
    public static ChatFragment getInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentChatBinding.inflate(inflater, container, false);
        initView();
        TagHawkApplication.getInstance().setMessageTabVisible(true);
        return mBinding.getRoot();
    }

    // init views and listener
    private void initView() {
        mActivity = getActivity();
        height = mBinding.toolbar.getHeight();
        mBinding.includeHeader.ivCategory.setOnClickListener(this);
        mBinding.includeHeader.tvSearch.setOnClickListener(this);
        mBinding.flMessages.setOnClickListener(this);
        mBinding.flNotifications.setOnClickListener(this);
        mBinding.includeHeader.ivGift.setVisibility(View.GONE);
        mBinding.includeHeader.ivCategory.setVisibility(View.GONE);
        originalBackgroundTranslationX = mBinding.viewBackground.getTranslationX();
        argbEvaluator = new ArgbEvaluator();
        setupViewPager();
    }

    // setup view pager
    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new MessagesFragment(), getString(R.string.messages));
        viewPagerAdapter.addFragment(new NotificationFragment(), getString(R.string.notifications));
        mBinding.vpChat.setAdapter(viewPagerAdapter);
        mBinding.vpChat.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        mBinding.viewBackground.setTranslationX(originalBackgroundTranslationX + positionOffsetPixels / 2.25f);
                        mBinding.tvMessages.setTextColor((Integer) argbEvaluator.evaluate(positionOffset,
                                getResources().getColor(R.color.Black),
                                getResources().getColor(R.color.White)));
                        mBinding.tvNotifications.setTextColor((Integer) argbEvaluator.evaluate(positionOffset,
                                getResources().getColor(R.color.White),
                                getResources().getColor(R.color.Black)));
                        break;
                }
            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        AppBarLayout.LayoutParams layoutParams1 = (AppBarLayout.LayoutParams) mBinding.toolbar.getLayoutParams();
                        layoutParams1.height = (int) getResources().getDimension(R.dimen._45sdp);
                        mBinding.toolbar.setLayoutParams(layoutParams1);
                        mBinding.toolbar.requestLayout();
//                        mBinding.toolbar.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) mBinding.toolbar.getLayoutParams();
                        layoutParams.height = 0;
                        mBinding.toolbar.setLayoutParams(layoutParams);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_messages:
                mBinding.vpChat.setCurrentItem(0, true);
                break;
            case R.id.fl_notifications:
                mBinding.vpChat.setCurrentItem(1, true);
                break;
            case R.id.tv_search:
                Fragment fragment = viewPagerAdapter.getItem(mBinding.vpChat.getCurrentItem());
                if (fragment instanceof MessagesFragment) {
                    PositionedLinkedHashmap<String, ChatModel> map = new PositionedLinkedHashmap<>();
                    map.putAll(((MessagesFragment) fragment).pinnedMessagesHashmap);
                    map.putAll(((MessagesFragment) fragment).openMessagesHashmap);
                    if (map.size() > 0) {
                        TagHawkApplication.getInstance().setChatInboxMap(map);
                        startActivity(new Intent(mActivity, SearchChatActivity.class));
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        TagHawkApplication.getInstance().setMessageTabVisible(false);
    }
}
