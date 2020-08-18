package com.taghawk.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.taghawk.R;
import com.taghawk.adapters.TabProductsPagerAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentProfileBinding;
import com.taghawk.databinding.LayoutUnverifPopUpBinding;
import com.taghawk.databinding.LayoutUnverifiedPopUpBinding;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.profileresponse.BalanceResponse;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.model.profileresponse.UserDetail;
import com.taghawk.ui.follow_follower.FollowFollowerActivity;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.onboard.login.LoginActivity;
import com.taghawk.ui.review_rating.ReviewRatingActivity;
import com.taghawk.ui.setting.ReferalActivity;
import com.taghawk.ui.setting.payment_details.PaymentDetailsActivity;
import com.taghawk.util.AppUtils;

import java.text.DecimalFormat;
import java.util.HashMap;


public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    private ProfileViewModel profileViewModel;
    private ProfileFragment.IProfileHost mIProfileHost;
    private FragmentProfileBinding mBinding;
    private AppCompatActivity mActivity;
    private UserDetail mUserDetail;
    private HomeViewModel mHomeViewModel;
    private PopupWindow unverifiedPop, menuPop;
    private String sellerId = "";
    private boolean isEditOpen;
    private boolean isOpenEditFragment;

    /**
     * This method is used to return the instance of this fragment
     *
     * @return new instance of {@link ProfileFragment}
     */
    public static ProfileFragment getInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViewModel();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragment.IProfileHost) {
            mIProfileHost = (ProfileFragment.IProfileHost) context;
        } else
            throw new IllegalStateException("Host must implement ProfileFragment.IProfileHost");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initializing view model
        initView();
        setupViewPager();
        setUpViews();
        setUpTabLayout();
        try {
            mHomeViewModel.getBalance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUpViews() {
        try {
            getLoadingStateObserver().onChanged(true);
            HashMap<String, Object> params = new HashMap<>();
            profileViewModel.getProfile(params, AppConstants.REQUEST_CODE.PROFILE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // setup view pager
    private void setupViewPager() {

    }

    /**
     * Method to set Up View Model
     */
    private void setUpViewModel() {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        profileViewModel.profileViewModel().observe(this, new Observer<ProfileResponse>() {
            @Override
            public void onChanged(@Nullable ProfileResponse profileResponse) {
                getLoadingStateObserver().onChanged(false);

                if (profileResponse.getCode() == 200) {
                    mUserDetail = profileResponse.getUserDetail();
                    if (mUserDetail != null) {
                        profileViewModel.updateUserNode(mUserDetail.getId(), mUserDetail.getProfilePicture(), mUserDetail.getFullName(), mUserDetail.getEmail());
                        setSpanableText(AppUtils.numberCalculation(profileResponse.getUserDetail().getCashOutBalance()));
                        setUpUserDetails();
                        sellerId = mUserDetail.getId();
                        if (isOpenEditFragment)
                            openEditFragment();
                    }
                }
            }
        });

        mHomeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        mHomeViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mHomeViewModel.logout().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse productListingModel) {
                getLoadingStateObserver().onChanged(false);
                if (productListingModel != null && productListingModel.getCode() == 200) {
//                    DataManager.getInstance().saveAccessToken("");
                    DataManager.getInstance().clearPreferences();
                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    startActivity(intent);
                    mActivity.finish();
                }
            }
        });

        mHomeViewModel.getUpdatedProfileLiveData().observe(this, new Observer<UserDetail>() {
            @Override
            public void onChanged(@Nullable UserDetail userDetail) {
                mUserDetail = userDetail;
                if (mUserDetail != null) ;
                {
                    profileViewModel.updateUserNode(mUserDetail.getId(), mUserDetail.getProfilePicture(), mUserDetail.getFullName(), mUserDetail.getEmail());
                    setUpUserDetails();
                }

            }
        });
        mHomeViewModel.getBalanceLiveData().observe(this, new Observer<BalanceResponse>() {
            @Override
            public void onChanged(@Nullable BalanceResponse balanceResponse) {
                if (balanceResponse.getBalanceData() != null && balanceResponse.getBalanceData().getCurrentBalance() != null) {

                    try {
                        if (mUserDetail != null) {
                            setSpanableText(AppUtils.numberCalculation(balanceResponse.getBalanceData().getCashOutBalance()));
                        } else {
                            setSpanableText("0.0");
                        }
                    } catch (Exception e) {

                    }
                } else {
                    setSpanableText("0.0");
                }
            }
        });


    }

    public void openEditFragment() {
        openUserEditProfile();

    }

    public void setUpTabLayout() {
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);//setting tab over viewpager
        TabProductsPagerAdapter adapter = new TabProductsPagerAdapter(getChildFragmentManager(), mActivity, sellerId);
        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setOffscreenPageLimit(3);
        mBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setUpUserDetails() {


        if (!TextUtils.isEmpty(mUserDetail.getProfilePicture())) {
            Glide.with(mActivity).load(mUserDetail.getProfilePicture()).apply(RequestOptions.placeholderOf(R.drawable.ic_detail_user_placeholder)).into(mBinding.ivProfile);
            mBinding.tvFirstCharater.setVisibility(View.GONE);

        } else {
            mBinding.tvFirstCharater.setVisibility(View.VISIBLE);
            mBinding.tvFirstCharater.setText(mUserDetail.getFullName().toUpperCase().toString().substring(0, 1));

        }
        if (!TextUtils.isEmpty(mUserDetail.getFullName()))
            mBinding.tvUserName.setText(mUserDetail.getFullName());

        mBinding.tvMemberSince.setText(String.format("%s %s", getResources().getString(R.string.member_since), AppUtils.getDateStringFromTimestamp(mUserDetail.getCreated())));

        mBinding.tvCompletionInfo.setText(String.format("%s%s %s", mUserDetail.getProfileCompleted(), "%", getString(R.string.complete)));

        mBinding.tvFollowersValue.setText(String.valueOf(mUserDetail.getFollowers()));

        mBinding.tvFollowingValue.setText(String.valueOf(mUserDetail.getFollowing()));

        mBinding.pbProfile.setProgress(mUserDetail.getProfileCompleted() == null ? 0 : mUserDetail.getProfileCompleted());

        if (mUserDetail.getIsEmailVerified() != null && !mUserDetail.getIsEmailVerified()) {
            mBinding.ivVerifyEmail.setVisibility(View.VISIBLE);

        }
        if (mUserDetail.getIsPhoneVerified() != null && !mUserDetail.getIsPhoneVerified()) {
            mBinding.ivVerifyPhone.setVisibility(View.VISIBLE);
        } else
            mBinding.ivVerifyPhone.setVisibility(View.GONE);
        if (mUserDetail.getIsFacebookLogin() != null && !mUserDetail.getIsFacebookLogin()) {
            mBinding.ivVerifyFacebook.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivVerifyFacebook.setVisibility(View.GONE);
        }

        if (mUserDetail.getOfficialIdVerified() != null && !mUserDetail.getOfficialIdVerified()) {
            mBinding.ivVerifyDocument.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivVerifyDocument.setVisibility(View.GONE);

        }

        if (mUserDetail.getSellerRating() != null) {
            mBinding.tvRating.setText(String.valueOf(mUserDetail.getSellerRating()));
        }
        if (mUserDetail.getSellerVerified()) {
            mBinding.ivShield.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_shield_verify));
        } else {
            mBinding.ivShield.setImageDrawable(getResources().getDrawable(R.drawable.ic_unverify_sheild));
        }
    }

    private void setSpanableText(String amount) {
        try {
            String newAmount = new DecimalFormat("##.##").format(Double.valueOf(amount));
            amount = newAmount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String str = "Wallet $" + amount;
        Spannable spannable = new SpannableString(str);
        spannable.setSpan(new UnderlineSpan(), 7, str.length(), 0);
        mBinding.tvBalance.setText(spannable);
        mBinding.tvBalance.setHighlightColor(Color.TRANSPARENT);
        mBinding.tvBalance.setMovementMethod(LinkMovementMethod.getInstance());

    }

    // init views and listener
    private void initView() {
        isOpenEditFragment = getArguments().getBoolean(AppConstants.BUNDLE_DATA);
        mActivity = (AppCompatActivity) getActivity();
        mBinding.includeHeader.ivSettings.setOnClickListener(this);
        mBinding.ivEdit.setOnClickListener(this);
        mBinding.ivShield.setOnClickListener(this);
        mBinding.tvBalance.setOnClickListener(this);
        mBinding.tvFollowingValue.setOnClickListener(this);
        mBinding.tvFollowing.setOnClickListener(this);
        mBinding.tvFollowers.setOnClickListener(this);
        mBinding.tvFollowersValue.setOnClickListener(this);
        mBinding.tvRating.setOnClickListener(this);
        mBinding.ivVerifyDocument.setOnClickListener(this);
        mBinding.ivVerifyEmail.setOnClickListener(this);
        mBinding.ivVerifyPhone.setOnClickListener(this);
        mBinding.ivVerifyFacebook.setOnClickListener(this);
        mBinding.includeHeader.ivImg.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_settings:

                openSettingFragment();

                break;
            case R.id.iv_edit:
                openUserEditProfile();
                break;
            case R.id.iv_shield:
                if (mUserDetail != null && !mUserDetail.getSellerVerified()) {
                    showUnverifiedPopUp(getResources().getString(R.string.verify_phone_number_and_official_id_to_become_a_verified_seller));
                }
                break;
            case R.id.tv_click_to_verify:
                openUserEditProfile();
                unverifiedPop.dismiss();
                break;
            case R.id.tv_balance:
//                if (mUserDetail != null && mUserDetail.getShareUrl() != null)
//                    AppUtils.share(mActivity, mUserDetail.getShareUrl(), mUserDetail.getFullName() + " Profile", getString(R.string.profile_share));
                Intent intent = new Intent(mActivity, PaymentDetailsActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_followers:
            case R.id.tv_followers_value:
                openFollowFollowingActivity(1);
                break;
            case R.id.tv_following:
            case R.id.tv_following_value:
                openFollowFollowingActivity(2);
                break;
            case R.id.tv_rating:
                if (mUserDetail != null) {
                    openRatingScreen(mUserDetail);
                }
                break;
            case R.id.iv_verify_document:
                showUnverifiedPopUp("verify official ID", mBinding.ivVerifyDocument);
                break;
            case R.id.iv_verify_email:
                showUnverifiedPopUp(getString(R.string.verify_ema), mBinding.ivVerifyEmail);
                break;
            case R.id.iv_verify_facebook:
                showUnverifiedPopUp(getString(R.string.verify_favebook), mBinding.ivVerifyFacebook);
                break;
            case R.id.iv_verify_phone:
                showUnverifiedPopUp("verify phone number", mBinding.ivVerifyPhone);
                break;
            case R.id.iv_img:
                openReferalScreen();
                break;

        }
    }

    private void openSettingFragment() {

        ((HomeActivity) mActivity).addSettingFragment();
    }

    private void openRatingScreen(UserDetail bean) {
        Intent intent = new Intent(mActivity, ReviewRatingActivity.class);
        intent.putExtra(AppConstants.KEY_CONSTENT.SELLER_ID, bean.getId());
        intent.putExtra(AppConstants.KEY_CONSTENT.FULL_NAME, bean.getFullName());
        intent.putExtra(AppConstants.KEY_CONSTENT.JOIN_FROM, String.format("%s %s", getResources().getString(R.string.member_since), AppUtils.getDateStringFromTimestamp(mUserDetail.getCreated())));
        intent.putExtra(AppConstants.KEY_CONSTENT.SELLER_RATING, bean.getSellerRating());
        intent.putExtra(AppConstants.KEY_CONSTENT.IMAGES, bean.getProfilePicture());
        startActivity(intent);
    }

    private void openUserEditProfile() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.PROFILE_INFO, mUserDetail);
        mIProfileHost.openProfileEditFragment(bundle);
    }

//    @Override
//    public void onRefresh() {
//        setUpViews();
//    }

    /**
     * This interface is used to interact with the host {@link }
     */
    public interface IProfileHost {
        void openProfileEditFragment(Bundle bundle);
    }

    private void showUnverifiedPopUp(String msg) {

        LayoutUnverifiedPopUpBinding popBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_unverified_pop_up, null, false);
        unverifiedPop = new PopupWindow(mActivity);
        unverifiedPop.setContentView(popBinding.getRoot());
        unverifiedPop.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        unverifiedPop.setFocusable(true);
        unverifiedPop.setOutsideTouchable(true);
        popBinding.tvMsg.setText(msg);
        popBinding.tvClickToVerify.setOnClickListener(this);
        unverifiedPop.showAsDropDown(mBinding.ivShield);
    }

    private void showUnverifiedPopUp(String msg, View view) {

        LayoutUnverifPopUpBinding popBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_unverif_pop_up, null, false);
        unverifiedPop = new PopupWindow(mActivity);
        unverifiedPop.setContentView(popBinding.getRoot());
        unverifiedPop.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        unverifiedPop.setFocusable(true);
        unverifiedPop.setOutsideTouchable(true);
        popBinding.tvMsg.setText(msg);
        popBinding.tvClickToVerify.setOnClickListener(this);
        unverifiedPop.showAsDropDown(view);
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        mBinding.includeHeader.ivSettings.setClickable(true);
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        mBinding.includeHeader.ivSettings.setClickable(true);
    }

    private void openFollowFollowingActivity(int type) {
        Intent intent = new Intent(mActivity, FollowFollowerActivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, type);
        intent.putExtra(AppConstants.KEY_CONSTENT.USER_ID, mUserDetail.getId());
        intent.putExtra("IS_OTHER_PROFILE", false);
        mActivity.startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.FOLLOWFOLLOWING);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.FOLLOWFOLLOWING:
                if (resultCode == Activity.RESULT_OK) {
                    setUpViews();
                }
                break;
        }
    }

    private void openReferalScreen() {
        Intent intent = new Intent(mActivity, ReferalActivity.class);
        startActivity(intent);
    }
}
