package com.taghawk.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.adapters.TabOtherProfileProductsPagerAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.FragmentProfileBinding;
import com.taghawk.firebase.FirebaseManager;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.chat.ChatProductModel;
import com.taghawk.model.profileresponse.ProfileResponse;
import com.taghawk.model.profileresponse.UserDetail;
import com.taghawk.model.request.User;
import com.taghawk.ui.chat.MessagesDetailActivity;
import com.taghawk.ui.follow_follower.FollowFollowerActivity;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.review_rating.ReviewRatingActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;


public class OtherProfileFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding mBinding;
    private AppCompatActivity mActivity;
    private UserDetail mUserDetail;
    private HomeViewModel mHomeViewModel;
    private PopupWindow unverifiedPop, menuPop;
    private String sellerId;
    private boolean isFollow;
    private boolean isChatClicked;

    /**
     * This method is used to return the instance of this fragment
     *
     * @return new instance of {@link OtherProfileFragment}
     */
    public static OtherProfileFragment getInstance() {
        return new OtherProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViewModel();
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
        setUpTabLayout();

    }

    public void setUpViews() {
        try {
            if (AppUtils.isInternetAvailable(mActivity)) {
                getArgumentData();
                getLoadingStateObserver().onChanged(true);
                HashMap<String, Object> params = new HashMap<>();
                params.put(AppConstants.KEY_CONSTENT.USER_ID, sellerId);
                profileViewModel.getProfile(params, AppConstants.REQUEST_CODE.PROFILE);
            } else showNoNetworkError();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getArgumentData() {
        if (getArguments() != null) {
            sellerId = getArguments().getString(AppConstants.BUNDLE_DATA);
        }
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
                    switch (profileResponse.getRequestCode()) {
                        case AppConstants.REQUEST_CODE.PROFILE:
                            mUserDetail = profileResponse.getUserDetail();
                            if (mUserDetail != null)
                                setUpUserDetails();
                            break;
                        case AppConstants.REQUEST_CODE.FOLLOW:
                            setTextFollowing();
                            break;
                        case AppConstants.REQUEST_CODE.UNFOLLOW:
                            isFollow = false;
                            mUserDetail.setFollowing(false);
                            mBinding.tvFollow.setText(getString(R.string.follow));
                            if (mUserDetail != null && mUserDetail.getFollowers() > 0) {
                                mUserDetail.setFollowers(mUserDetail.getFollowers() - 1);
                                mBinding.tvFollowersValue.setText(String.valueOf(mUserDetail.getFollowers()));
                            }
                            break;

                    }
                }
            }
        });
        setUpViews();
    }

    private void setTextFollowing() {
        isFollow = true;
        mBinding.tvFollow.setText(getString(R.string.unfollow));
        mUserDetail.setFollowers(mUserDetail.getFollowers() + 1);
        mBinding.tvFollowersValue.setText(String.valueOf(mUserDetail.getFollowers()));
    }

    public void setUpTabLayout() {
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);//setting tab over viewpager
        TabOtherProfileProductsPagerAdapter adapter = new TabOtherProfileProductsPagerAdapter(getChildFragmentManager(), mActivity, sellerId);
        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setOffscreenPageLimit(2);

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
            if (mUserDetail != null && mUserDetail.getFullName().length() > 0)
                mBinding.tvFirstCharater.setText(mUserDetail.getFullName().toUpperCase().toString().substring(0, 1));

        }
        if (!TextUtils.isEmpty(mUserDetail.getFullName()))
            mBinding.tvUserName.setText(mUserDetail.getFullName());
        if (mUserDetail.isFollowing()) {
            mBinding.tvFollow.setText(getString(R.string.unfollow));
        }

        mBinding.tvMemberSince.setText(String.format("%s %s", getResources().getString(R.string.member_since), AppUtils.getDateStringFromTimestamp(mUserDetail.getCreated())));

        mBinding.tvCompletionInfo.setText(String.format("%s%s %s", mUserDetail.getProfileCompleted(), "%", getString(R.string.complete)));

        mBinding.tvFollowersValue.setText(String.valueOf(mUserDetail.getFollowers()));

        mBinding.tvFollowingValue.setText(String.valueOf(mUserDetail.getFollowing()));

        mBinding.pbProfile.setProgress(0);

        if (mUserDetail.getIsEmailVerified() != null && !mUserDetail.getIsEmailVerified()) {
            mBinding.flMail.setVisibility(View.GONE);
        }

        if (mUserDetail.getIsPhoneVerified() != null && !mUserDetail.getIsPhoneVerified()) {
            mBinding.flPhone.setVisibility(View.GONE);
        }

        if (mUserDetail.getIsFacebookLogin() != null && !mUserDetail.getIsFacebookLogin()) {
            mBinding.flFb.setVisibility(View.GONE);
        }

        if (mUserDetail.getOfficialIdVerified() != null && !mUserDetail.getOfficialIdVerified()) {
            mBinding.flDocument.setVisibility(View.GONE);
        }

        if (mUserDetail.getSellerRating() != null) {
            mBinding.tvRating.setText(String.valueOf(mUserDetail.getSellerRating()));
        }

        if (mUserDetail.getSellerVerified()) {
            mBinding.ivShield.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_shield_verify));
        } else {
            mBinding.ivShield.setVisibility(View.GONE);
        }
    }

    // init views and listener
    private void initView() {
        mActivity = (AppCompatActivity) getActivity();
        mBinding.includeHeader.tvTitle.setText(getString(R.string.profile));
        mBinding.includeHeader.ivImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        setVisibility();
        setListener();
        setUpViews();
    }

    private void setVisibility() {
        mBinding.includeHeader.ivSettings.setVisibility(View.GONE);
        mBinding.ivEdit.setVisibility(View.GONE);
        mBinding.tvBalance.setVisibility(View.GONE);
        mBinding.pbProfile.setVisibility(View.VISIBLE);
//        mBinding.includeHeader.ivBack.setVisibility(View.VISIBLE);
        mBinding.includeHeader.tvTitle.setVisibility(View.VISIBLE);
        mBinding.tvCompletionInfo.setVisibility(View.GONE);
        mBinding.llFollow.setVisibility(View.VISIBLE);
    }

    private void setListener() {
        mBinding.tvRating.setOnClickListener(this);
        mBinding.includeHeader.ivImg.setOnClickListener(this);
        mBinding.tvFollow.setOnClickListener(this);
        mBinding.tvFollowers.setOnClickListener(this);
        mBinding.tvFollowersValue.setOnClickListener(this);
        mBinding.tvFollowing.setOnClickListener(this);
        mBinding.tvChat.setOnClickListener(this);
        mBinding.tvFollowingValue.setOnClickListener(this);
        mBinding.ivShield.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_img:
                ((OtherProfileActivity) mActivity).finish();
                break;
            case R.id.tv_follow:
                if (mUserDetail != null && !(isFollow || mUserDetail.isFollowing()))
                    profileViewModel.followFriend(mUserDetail.getId(), AppConstants.REQUEST_CODE.FOLLOW);
                else {
                    DialogUtil.getInstance().CustomUnFollowRemoveBottomSheetDialog(mActivity, getString(R.string.unfollow_meg), mUserDetail.getFullName(), getString(R.string.unfollow), mUserDetail.getProfilePicture(), false, false, new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {
                            if (AppUtils.isInternetAvailable(mActivity))
                                profileViewModel.removeUnfollow(mUserDetail.getId(), AppConstants.UNFOLLOW_REMOVE_ACTION.UNFOLLOW, AppConstants.REQUEST_CODE.UNFOLLOW);
                            else showNoNetworkError();
                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });

                }
                break;
            case R.id.tv_chat:
                if (!isChatClicked) {
                    isChatClicked = true;
                    final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(FirebaseManager.getFirebaseRoomId(user.getUserId(), sellerId)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ChatModel chatModel = null;
                            if (dataSnapshot.exists())
                                chatModel = dataSnapshot.getValue(ChatModel.class);
                            else {
                                chatModel = new ChatModel();
                                chatModel.setPinned(false);
                                chatModel.setChatMute(false);
                                chatModel.setCreatedTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                                chatModel.setRoomName(mUserDetail.getFullName());
                                chatModel.setRoomImage(mUserDetail.getProfilePicture());
                                chatModel.setChatType(AppConstants.FIREBASE.FIREBASE_SINGLE_CHAT);
                                chatModel.setOtherUserId(sellerId);
                                chatModel.setRoomId(FirebaseManager.getFirebaseRoomId(user.getUserId(), sellerId));
                                ChatProductModel chatProductModel = new ChatProductModel();
                                chatProductModel.setProductId("");
                                chatProductModel.setProductName("");
                                chatProductModel.setProductPrice(Double.parseDouble("0"));
                                chatProductModel.setProductImage("");
                                chatModel.setProductInfo(chatProductModel);
                            }

                            startActivity(new Intent(mActivity, MessagesDetailActivity.class).putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel).putExtra(AppConstants.FIREBASE.TIMESTAMP, chatModel.getCreatedTimeStampLong()));
                            isChatClicked = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            isChatClicked = false;
                        }
                    });
                }
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
        }
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

    private void openFollowFollowingActivity(int type) {
        Intent intent = new Intent(mActivity, FollowFollowerActivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, type);
        intent.putExtra(AppConstants.KEY_CONSTENT.USER_ID, mUserDetail.getId());
        intent.putExtra("IS_OTHER_PROFILE", true);
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

    @Override
    public void onRefresh() {
        setUpViews();
    }


    @Override
    protected void onFailure(FailureResponse failureResponse) {
        mBinding.includeHeader.ivSettings.setClickable(true);
    }

    @Override
    protected void onErrorOccurred(Throwable throwable) {
        mBinding.includeHeader.ivSettings.setClickable(true);

    }
}
