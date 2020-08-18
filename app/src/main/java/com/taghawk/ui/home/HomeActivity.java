package com.taghawk.ui.home;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.base.BaseFragment;
import com.taghawk.camera2basic.CameraTwoActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.login.LoginFirebaseModel;
import com.taghawk.model.strip.CreateMercentResponse;
import com.taghawk.model.strip.GetBankDetail;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tag.TagDetailsModel;
import com.taghawk.model.update_rating_notification.UpdateRatingNotificationBean;
import com.taghawk.ui.cart.CartActivity;
import com.taghawk.ui.chat.ChatFragment;
import com.taghawk.ui.chat.PendingRequestsActivity;
import com.taghawk.ui.follow_follower.FollowFollowerActivity;
import com.taghawk.ui.gift.GiftRewardPromotionActivity;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.ui.home.tabs.TagListingFragment;
import com.taghawk.ui.notification.NotificationFragment;
import com.taghawk.ui.onboard.login.LoginActivity;
import com.taghawk.ui.profile.ProfileEditFragment;
import com.taghawk.ui.profile.ProfileFragment;
import com.taghawk.ui.setting.SettingFragment;
import com.taghawk.ui.setting.payment_details.AccountPaymentInfoFragment;
import com.taghawk.ui.setting.payment_details.PaymentDetailsActivity;
import com.taghawk.ui.tag.TagDetailsActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.PermissionUtility;

import java.util.HashMap;

public class HomeActivity extends BaseActivity implements HomeFragment.IHomeHost, ProfileFragment.IProfileHost, BottomNavigationView.OnNavigationItemSelectedListener ,HomeFragment.SendMessage{

    private BottomNavigationView bottomNavigationView;
    private String productId;
    private String deepLinkType;
    private GPSTracker gpsTracker;
    private HomeViewModel mHomeViewModel;
    private NestedScrollView nestedScroll;
    private Animator mOffsetValueAnimator;
    private LinearLayout footerLayout;
    private boolean isVisible;
    private FragmentManager mFragmentManager;
    HomeFragment mHomeFragment;
    ProfileFragment mProfileFragment;
    private Address location;
    private ProfileFragment profileFragment;
    private long back_pressed;
    private HashMap<String, Object> productFilterParms, tagFilterParms,FilterParms;
    private String userId = "";
    private Query userNodeQuery;
    private ValueEventListener valueEventListener;
    private View vBadge;
    private AccountPaymentInfoFragment accountPaymentInfoFragment;
    String catid;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        initFragments();
        initView();
        addHomeFragment();
        getIntentData();
//        getBankDetails();
        updateDeviceToke();

        String androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DataManager.getInstance().saveDeviceId(androidId);
        Log.e("androidId", "" + androidId);

        if (PermissionUtility.isPermissionGranted(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {
            if (!gpsTracker.isGPSEnable()) {
                gpsTracker.showSettingsAlert();
            }
        }
    }

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    private void getBankDetails() {
        if (AppUtils.isInternetAvailable(this)) {
            mHomeViewModel.getBankDetails();
        }
    }

    private void updateDeviceToke() {
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (task != null && task.getResult().getToken() != null) {
                        String token = task.getResult().getToken();
                        mHomeViewModel.updateDeviceToken(token);
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("IS_FROM_FEATURE_POST")) {
            boolean isFrom = (boolean) intent.getExtras().get("IS_FROM_FEATURE_POST");
            if (isFrom) {
                if (mHomeFragment != null) {
//                    mHomeFragment.callProductListApi();
                    mHomeFragment.callProductListApi1();
                }
            }
        } else if (intent != null && intent.getExtras() != null && intent.getExtras().getString(AppConstants.DEEP_INK_CONSTENT.TYPE) != null) {
            if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(AppConstants.NOTIFICATION_ACTION.ENTITY_ID)) {
                productId = intent.getExtras().getString(AppConstants.NOTIFICATION_ACTION.ENTITY_ID);
                deepLinkType = intent.getExtras().getString(AppConstants.DEEP_INK_CONSTENT.TYPE);
                if (deepLinkType != null) {
                    notificationAction(deepLinkType, productId, intent);
                }
            }
        }

    }

    private void initFragments() {
        mProfileFragment = new ProfileFragment();
        mHomeFragment = new HomeFragment();
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.NOTIFICATION_ACTION.ENTITY_ID)) {
            productId = getIntent().getExtras().getString(AppConstants.NOTIFICATION_ACTION.ENTITY_ID);
            deepLinkType = getIntent().getExtras().getString(AppConstants.DEEP_INK_CONSTENT.TYPE);
            if (deepLinkType != null) {
                notificationAction(deepLinkType, productId, getIntent());
            }
        }
    }

    private void notificationAction(String action, String productId, Intent mainIntent) {
        Intent intent = null;
        switch (action) {
            case AppConstants.NOTIFICATION_ACTION.PRODUCT_DETAILS:
                userId = mainIntent.getExtras().getString(AppConstants.DEEP_INK_CONSTENT.USER_ID);
                if (userId != null) {
                    if (AppUtils.isInternetAvailable(this)) {
                        mHomeViewModel.acceptRejectTagRequest(userId, productId, 1);
                    } else {
                        showNoNetworkError();
                    }
                }
                break;
            case AppConstants.NOTIFICATION_ACTION.TAG_REQUEST:
                Intent intent1 = new Intent(this, PendingRequestsActivity.class);
//                intent1.putExtra(AppConstants.TAG_KEY_CONSTENT.NAME, tagDetailFirebaseModel.getTagName());
                intent1.putExtra(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, productId);
//                intent1.putExtra(AppConstants.TAG_KEY_CONSTENT.IMAGE_URL, tagDetailFirebaseModel.getTagImageUrl());
                startActivity(intent1);
                break;
            case AppConstants.NOTIFICATION_ACTION.ANNOUNCEMENT:
            case AppConstants.NOTIFICATION_ACTION.TAG_JOINED_ACCEPTED:
            case AppConstants.NOTIFICATION_ACTION.TAG_UPDATED:
            case AppConstants.NOTIFICATION_ACTION.TAG_JOINED:
            case AppConstants.NOTIFICATION_ACTION.MYSELF_REMOVE_FROM_GROUP:
            case AppConstants.NOTIFICATION_ACTION.MYSELF_MADE_ADMIN:
            case AppConstants.NOTIFICATION_ACTION.TAG_DETAILS:
            case AppConstants.NOTIFICATION_ACTION.JUMIO_APPROVAL:
                Intent detailsIntent = new Intent(this, TagDetailsActivity.class);
                detailsIntent.putExtra("TAG_ID", productId);
                startActivity(detailsIntent);
                break;
            case AppConstants.NOTIFICATION_ACTION.PRODUCT_DETAILS_LINK:
            case AppConstants.NOTIFICATION_ACTION.PRODUCT_LIKE:
            case AppConstants.NOTIFICATION_ACTION.PRODUCT_ADDED:
                intent = perfromAction(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, productId, ProductDetailsActivity.class);
                startActivity(intent);
                break;
            case AppConstants.NOTIFICATION_ACTION.FOLLOWED:
                openFollowFollowingActivity();
                return;
            case AppConstants.NOTIFICATION_ACTION.INVITE_CODE_USED:
                openGiftActivity();
                break;
            case AppConstants.NOTIFICATION_ACTION.PRODUCT_ADDED_IN_CART:
                openCartActivity();
                break;
            case AppConstants.NOTIFICATION_ACTION.VERIFY_EMAIL:
                hitVerifyEmail(productId);
                break;
            case AppConstants.FIREBASE.PUSH_TYPE_MESSAGE:
                bottomNavigationView.setSelectedItemId(R.id.navigation_chat);
                break;
            case AppConstants.NOTIFICATION_ACTION.PRODUCT_SOLD:
            case AppConstants.PAYMENT:
                openPaymentHistory(true);
                break;
            case AppConstants.NOTIFICATION_ACTION.STRIPE_UPDATE:
                openPaymentHistory(false);
                break;
        }
    }

    private void openGiftActivity() {
        Intent intent = new Intent(this, GiftRewardPromotionActivity.class);
        startActivity(intent);
    }

    /*
     * open PaymentHistory Screen
     * */
    private void openPaymentHistory(boolean isCashout) {
        Intent intent = new Intent(this, PaymentDetailsActivity.class);
        intent.putExtra(AppConstants.PAYMENT, isCashout);
        startActivity(intent);
    }

    private void hitVerifyEmail(String productId) {
        mHomeViewModel.verifyEmail(productId);
    }


    private void openCartActivity() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    private Intent perfromAction(String key, String entityId, Class actionClass) {
        Intent intent = new Intent(this, actionClass);

        intent.putExtra(key, entityId);
        return intent;
    }

    private void openFollowFollowingActivity() {
        Intent intent = new Intent(this, FollowFollowerActivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, 1);
        intent.putExtra(AppConstants.KEY_CONSTENT.USER_ID, DataManager.getInstance().getUserDetails().getUserId());
        intent.putExtra("IS_OTHER_PROFILE", false);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.FOLLOWFOLLOWING);
    }


    private void initView() {
        productFilterParms = new HashMap<>();
        tagFilterParms = new HashMap<>();
        gpsTracker = new GPSTracker(this);
        fetchCurrentLocation();
        AppUtils.setStatusBar(this, getResources().getColor(R.color.White), true, 0, false);
        bottomNavigationView = ((BottomNavigationView) findViewById(R.id.bottom_navigation));
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        updateFooter();
        footerLayout = ((LinearLayout) findViewById(R.id.ll_footer));
        mHomeViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        addCountViewToChatTab();
        mHomeViewModel.logout().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse productListingModel) {
                getLoadingStateObserver().onChanged(false);
                if (productListingModel != null && productListingModel.getCode() == 200) {
//                    DataManager.getInstance().saveAccessToken("");
                    DataManager.getInstance().clearPreferences();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mHomeViewModel.getActionLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null) {
                    switch (integer) {
                        case AppConstants.ActionConstants.BACK_ACTION:
//                            if (bottomNavigationView.getSelectedItemId() == bottomNavigationView.findViewById(R.id.navigation_profile).getId()) {
                            onBackPressed();
                            if (profileFragment != null) {
                                profileFragment.setUpViews();
                            }
//                            }
                            break;
                    }
                }
            }
        });




        mHomeViewModel.getmUpdateDeviceTokenLiveData().observe(this, new Observer<UpdateRatingNotificationBean>() {
            @Override
            public void onChanged(@Nullable UpdateRatingNotificationBean bean) {
                if (bean.getCode() == 200) {
                    if (bean.getUpdateRatingNotificationData().getRatingData() != null) {
                        if (mHomeFragment != null) {
                            mHomeViewModel.updateDeviceTokenOnFirebase(DataManager.getInstance().getUserDetails().getUserId(), DataManager.getInstance().getDeviceToken());
                            mHomeFragment.updateDeviceToken(bean.getUpdateRatingNotificationData().getRatingData());
                        }
                    }
                }
            }
        });
        mHomeViewModel.getmCreateMerchentLiveData().observe(this, new Observer<CreateMercentResponse>() {
            @Override
            public void onChanged(@Nullable CreateMercentResponse createMercentResponse) {
                getLoadingStateObserver().onChanged(false);
                if (createMercentResponse.getCode() == 200) {
//                    showToastLong(createMercentResponse.getMerchentId());
                    DataManager.getInstance().saveMerchentId(createMercentResponse.getMerchentId());
                    if (PermissionUtility.isPermissionGranted(HomeActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, AppConstants.CAMERA_PERMISSION)) {
                        openAddProductScreen();
                    }
                }
            }
        });
        mHomeViewModel.getMgetBankDetailsViewModel().observe(this, new Observer<GetBankDetail>() {
            @Override
            public void onChanged(@Nullable GetBankDetail getBankDetail) {
                if (getBankDetail.getCode() == 200 || getBankDetail.getCode() == 201) {
                    if (getBankDetail.getGetBankDetailsModel() != null && getBankDetail.getGetBankDetailsModel().getAccountDetailsBean() != null) {
                        DataManager.getInstance().saveRoutingNumber(getBankDetail.getGetBankDetailsModel().getAccountDetailsBean().getRoutingNumber());
                        DataManager.getInstance().saveAccountNumber(getBankDetail.getGetBankDetailsModel().getAccountDetailsBean().getAccountNumber());
                        DataManager.getInstance().saveAccountHolderName(getBankDetail.getGetBankDetailsModel().getAccountDetailsBean().getAccountHolderName());

                    }
                }
            }
        });
        mHomeViewModel.getmAccpetRejectTagRequest().observe(this, new Observer<TagDetailsModel>() {
            @Override
            public void onChanged(@Nullable TagDetailsModel data) {
                TagData tagData = new TagData();
                tagData.setTagImageUrl(data.getTagDetailsData().getTagImageUrl());
                tagData.setTagId(data.getTagDetailsData().getTagId());
                tagData.setTagName(data.getTagDetailsData().getTagName());
                mHomeViewModel.joinTagOnFirebase(DataManager.getInstance().getUserDetails(), tagData);
                Intent detailsIntent = new Intent(HomeActivity.this, TagDetailsActivity.class);
                detailsIntent.putExtra("TAG_ID", productId);
                startActivity(detailsIntent);
            }
        });


        userNodeQuery = mHomeViewModel.getUserNodeQuery(DataManager.getInstance().getUserDetails().getUserId());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    LoginFirebaseModel loginFirebaseModel = dataSnapshot.getValue(LoginFirebaseModel.class);
                    updateBottomNavigationViewChatCount(loginFirebaseModel.getTotalUnreadCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userNodeQuery.addValueEventListener(valueEventListener);


    }

    private void updateFooter() {
    }


    @Override
    public void sendData(String message) {

    }



    private void addCountViewToChatTab() {
        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
        View vBotNavView = bottomNavigationMenuView.getChildAt(3); //replace 3 with the index of the menu item that you want to add the badge to.
        BottomNavigationItemView itemView = (BottomNavigationItemView) vBotNavView;
        vBadge = LayoutInflater.from(this).inflate(R.layout.layout_chat_count, bottomNavigationMenuView, false);
        vBadge.setVisibility(View.GONE);
        itemView.addView(vBadge);
    }


    private void updateBottomNavigationViewChatCount(int count) {
        TextView tvCount = (TextView) vBadge.findViewById(R.id.tv_count);
        tvCount.setText(String.valueOf(count));
        vBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }


    private void addHomeFragment() {
        mHomeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, productId);
        bundle.putString(AppConstants.DEEP_INK_CONSTENT.TYPE, deepLinkType);
        mHomeFragment.setArguments(bundle);
        replaceFragment(R.id.home_container, mHomeFragment, HomeFragment.class.getSimpleName());
    }

    private void addChatFragment() {
        replaceFragment(R.id.home_container, new ChatFragment(), ChatFragment.class.getSimpleName());
    }

    //Hawkdriver is in unerdevelopment// meanswhile for going live here we are putting wallet functionality
    private void addHawkDriverFragment() {
        accountPaymentInfoFragment = new AccountPaymentInfoFragment();
        replaceFragment(R.id.home_container, accountPaymentInfoFragment, AccountPaymentInfoFragment.class.getSimpleName());
    }

    public void updateProductList() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(HomeFragment.class.getSimpleName());
        if (fragment != null) {
            ((HomeFragment) fragment).updateProductList();
        }
    }

    private void addProfileFragment(boolean isOpenEditProfile) {
        profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConstants.BUNDLE_DATA, isOpenEditProfile);
        profileFragment.setArguments(bundle);
        replaceFragment(R.id.home_container, profileFragment, ProfileFragment.class.getSimpleName());
    }

    private void addNotificationFragment() {
        replaceFragment(R.id.home_container, new NotificationFragment(), NotificationFragment.class.getSimpleName());
    }

    public void addSettingFragment() {
        hideFooter(View.GONE);
        addFragmentWithBackstack(R.id.home_container, new SettingFragment(), NotificationFragment.class.getSimpleName());
    }


    @Override
    public void openChangePasswordFragment() {

    }

    @Override
    public void logOutSuccess() {

        DataManager.getInstance().clearPreferences();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected int getResourceId() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        handleDirection(View.VISIBLE);
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                addHomeFragment();
                return true;
            case R.id.navigation_hawk_driver:
                if (!(DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    addHawkDriverFragment();
                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(this, this);
                }
                return true;
            case R.id.navigation_create:
                if (!(DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    if (PermissionUtility.isPermissionGranted(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, AppConstants.CAMERA_PERMISSION)) {
                        openAddProductScreen();
                    }
                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(this, this);
                }

                return true;
            case R.id.navigation_chat:
                if (!(DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    addChatFragment();
                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(this, this);
                }
                return true;
            case R.id.navigation_profile:
                if (!(DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    addProfileFragment(false);
                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(this, this);
                }
                return true;

        }
        return false;
    }


    private void openAddProductScreen() {
        Intent intent = new Intent(this, CameraTwoActivity.class);
        intent.putExtra("ISFIRST", true);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        footerLayout.setVisibility(View.VISIBLE);
        if (mFragmentManager != null) {
            Fragment fragment = mFragmentManager.findFragmentById(R.id.home_container);
            if (fragment instanceof ProfileEditFragment) {
                mFragmentManager.popBackStackImmediate();
                if (profileFragment != null) {
                    profileFragment.setUpViews();
                }
                return;
            }
        } else {

            if (profileFragment != null) {
                if (getBackStackSize() > 0 && getCurrentFragment() != null && (getCurrentFragment() instanceof ProfileEditFragment || getCurrentFragment() instanceof SettingFragment)) {
                    popFragment();
                    profileFragment.setUpViews();
                } else {
                    if (back_pressed + 1000 > System.currentTimeMillis()) {
                        super.onBackPressed();
                    } else {
                        showToastShort(getString(R.string.exit_msg));
                    }
                    back_pressed = System.currentTimeMillis();


                }
            } else {
                if (back_pressed + 1000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                } else {
                    showToastShort(getString(R.string.exit_msg));
                }
                back_pressed = System.currentTimeMillis();


            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!gpsTracker.isGPSEnable()) {
                        gpsTracker.showSettingsAlert();
                    } else {
                        fetchCurrentLocation();
                    }
                }
                break;
            case AppConstants.CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAddProductScreen();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        DialogUtil.getInstance().showPermissionsRequiredDialog(this);
                    } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.EXPAND_STATUS_BAR)) {
                        DialogUtil.getInstance().showPermissionsRequiredDialog(this);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 64206:
                passResultToProfile(requestCode, resultCode, data);
                break;
            case AppConstants.ACTIVITY_RESULT.FOLLOWFOLLOWING:
                if (profileFragment != null) {
                    profileFragment.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case AppConstants.ACTIVITY_RESULT.TAG_DETAILS:
                if (resultCode == Activity.RESULT_OK) {
                    updateProductList();
                }
                break;
            case 1001:
                if (resultCode==Activity.RESULT_OK){
                    if (accountPaymentInfoFragment!=null){
                        accountPaymentInfoFragment.passOnActivityData(requestCode,resultCode,data);
                    }
                }
                    break;

        }

    }

    private void passResultToProfile(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof ProfileEditFragment)
                ((ProfileEditFragment) fragment).onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Object> getProductFilterParms() {
        return productFilterParms;
    }

    public void setProductFilterParms(HashMap<String, Object> productFilterParms) {
        this.productFilterParms = productFilterParms;
    }


    public HashMap<String, Object> getFilterParms() {
        return FilterParms;
    }

    public void setFilterParms(HashMap<String, Object> FilterParms) {
        this.FilterParms = FilterParms;
    }

    public HashMap<String, Object> getTagFilterParms() {
        return tagFilterParms;
    }

    public void setTagFilterParms(HashMap<String, Object> tagFilterParms) {
        this.tagFilterParms = tagFilterParms;
    }

    public void handleDirection(int visibility) {
        if (visibility == View.VISIBLE) {
            if (!isVisible) {
                isVisible = true;
                AppUtils.slideUp(footerLayout);
//                footerLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (isVisible) {
                isVisible = false;
                AppUtils.slideDown(footerLayout);
//                footerLayout.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public void openProfileEditFragment(Bundle bundle) {
        BaseFragment fragment = new ProfileEditFragment();
        fragment.setArguments(bundle);
        addFragmentWithBackstack(R.id.home_container, fragment, ProfileEditFragment.class.getName());
    }

    private void fetchCurrentLocation() {
        if (gpsTracker == null)
            gpsTracker = new GPSTracker(this);
        if (gpsTracker.isGPSEnable()) {
            setCurrentLocationText();
        }
    }

    private void setCurrentLocationText() {
        try {
            if (AppUtils.getAddressByLatLng(this, gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0) != null) {
                location = AppUtils.getAddressByLatLng(this, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                DataManager.getInstance().saveFilterLatitude(String.valueOf(location.getLatitude()));
                DataManager.getInstance().saveFilterLongitude(String.valueOf(location.getLongitude()));
                DataManager.getInstance().saveLocation(location.getAddressLine(0));
            }
        } catch (Exception E) {
        }
    }

    public void hideFooter(int visibilty) {
        footerLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userNodeQuery.removeEventListener(valueEventListener);
    }
}
