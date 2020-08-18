package com.taghawk.ui.home.filter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taghawk.R;
import com.taghawk.adapters.SellerTypeAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.CustomPostInDialog;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentFilterBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.util.AppUtils;
import com.taghawk.util.FilterManager;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.PermissionUtility;

import java.util.HashMap;
import java.util.Locale;

public class FilterFragment extends BaseFragment implements View.OnClickListener, OnMapReadyCallback, CompoundButton.OnCheckedChangeListener {

    private Activity mActivity;
    private FragmentFilterBinding mBinding;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private FilterVerificationViewModel filterVerificationViewModel;
    private SellerTypeAdapter adapter;
    private Address location;
    private Observer<HashMap<String, Object>> filterData;
    private GPSTracker gpsTracker;
    private MutableLiveData<HashMap<String, Object>> mLiveData;
    private HashMap<String, Object> mPreviousData = new HashMap<>(

    );
    private PopupWindow popup;
    private int postedWithin = 0;
    private String[] distanceArray = {"1 Mile", "5 Mile", "10 Mile", "50 Mile", "MAX"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentFilterBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        mActivity = getActivity();
        setUpSellerRating();
        mBinding.indicatorDistance.customTickTexts(distanceArray);
        mBinding.filter.ivCross.setBackgroundTintList(ContextCompat.getColorStateList(mActivity, R.color.txt_black));
        getArgumentData();
        filterVerificationViewModel = ViewModelProviders.of(this).get(FilterVerificationViewModel.class);
        filterVerificationViewModel.setGenericListeners(mLiveData, getFailureResponseObserver());
        mBinding.etLocation.setOnClickListener(this);
        mBinding.tvCurrentLocation.setOnClickListener(this);
        mBinding.filter.ivCross.setOnClickListener(this);
        mBinding.filter.tvReset.setVisibility(View.VISIBLE);
        if (!(DataManager.getInstance().getFilterLatitude() != null && DataManager.getInstance().getFilterLatitude().length() > 1)) {
            if (FilterManager.getInstance().getmFilterMap() == null || FilterManager.getInstance().getmFilterMap().size() == 0) {
                mBinding.tvCurrentLocation.performClick();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.tvCurrentLocation.performClick();
                    }
                }, 500);
            }
        } else {
            location = new Address(Locale.US);
            location.setAddressLine(0, DataManager.getInstance().getFilterLocation());
            location.setLatitude(Double.valueOf(DataManager.getInstance().getFilterLatitude()));
            location.setLongitude(Double.valueOf(DataManager.getInstance().getFilterLongitude()));
            mBinding.etLocation.setText(DataManager.getInstance().getFilterLocation());
        }
        setProductCondition();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        filterVerificationViewModel.getmLiveData().observe(this, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(@Nullable HashMap<String, Object> stringObjectHashMap) {
                if (stringObjectHashMap != null) {
                    Intent intent = new Intent();
                    intent.putExtra("FILTER_DATA", stringObjectHashMap);
                    intent.putExtra("LOCATION", location);
                    DataManager.getInstance().saveLocation(location.getAddressLine(0));
                    DataManager.getInstance().saveFilterLatitude(String.valueOf(location.getLatitude()));
                    DataManager.getInstance().saveFilterLongitude(String.valueOf(location.getLongitude()));
                    FilterManager.getInstance().setmFilterMap(stringObjectHashMap);
                    ((FilterActivity) mActivity).setResult(Activity.RESULT_OK, intent);
                    ((FilterActivity) mActivity).finish();
                } else {
                    ((FilterActivity) mActivity).finish();
                }
            }
        });
    }

    private void getArgumentData() {
        if (getArguments() != null && getArguments().getSerializable("FILTER_DATA") != null) {
            mPreviousData = (HashMap<String, Object>) getArguments().getSerializable("FILTER_DATA");
            location = (Address) getArguments().get("LOCATION");
            if (mPreviousData.size() > 0) {

                setPreviousData(mPreviousData);
            }
        }
    }

    private void setPreviousData(HashMap<String, Object> mPreviousData) {
        if (mPreviousData.containsKey(AppConstants.KEY_CONSTENT.LAT) && mPreviousData.get(AppConstants.KEY_CONSTENT.LONGI) != null) {
            if (location != null) {
//                location = AppUtils.getAddressByLatLng(mActivity, Double.valueOf(mPreviousData.get(AppConstants.KEY_CONSTENT.LAT).toString()), Double.valueOf(mPreviousData.get(AppConstants.KEY_CONSTENT.LONGI).toString()));
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                setMarker(latLng);
                mBinding.etLocation.setText(DataManager.getInstance().getFilterLocation());
            } else {
                LatLng latLng = new LatLng(Double.valueOf(mPreviousData.get(AppConstants.KEY_CONSTENT.LAT).toString()), Double.valueOf(mPreviousData.get(AppConstants.KEY_CONSTENT.LONGI).toString()));
                location = new Address(Locale.US);
                location.setAddressLine(0, DataManager.getInstance().getFilterLocation());
                location.setLatitude(Double.valueOf(mPreviousData.get(AppConstants.KEY_CONSTENT.LAT).toString()));
                location.setLongitude(Double.valueOf(mPreviousData.get(AppConstants.KEY_CONSTENT.LONGI).toString()));
                mBinding.etLocation.setText(DataManager.getInstance().getFilterLocation());
            }

        }
        if (mPreviousData.get(AppConstants.KEY_CONSTENT.PRICE_FROM) != null) {
            mBinding.etFromPriceRange.setText(mPreviousData.get(AppConstants.KEY_CONSTENT.PRICE_FROM).toString());
        }
        if (mPreviousData.get(AppConstants.KEY_CONSTENT.PRICE_TO) != null) {
            mBinding.etToPricePange.setText(mPreviousData.get(AppConstants.KEY_CONSTENT.PRICE_TO).toString());
        }
        if (mPreviousData.containsKey(AppConstants.KEY_CONSTENT.SELLER_VERIFIED) && Boolean.valueOf(mPreviousData.get(AppConstants.KEY_CONSTENT.SELLER_VERIFIED).toString()))
            mBinding.chkSellerVerified.setChecked(true);
        if (mPreviousData.containsKey(AppConstants.KEY_CONSTENT.CONDITION)) {
            String condition = mPreviousData.get(AppConstants.KEY_CONSTENT.CONDITION).toString();
            if (condition.length() > 0) {
                String[] consditionArray = condition.split(",");
                setConditionData(consditionArray);
            } else {
                setCondition(condition);
            }
        }
        if (mPreviousData.containsKey((AppConstants.KEY_CONSTENT.SELLER_RATING))) {
            int position = Integer.parseInt(mPreviousData.get(AppConstants.KEY_CONSTENT.SELLER_RATING).toString());
            adapter.notifyItemAtPostion(position - 1);
        }
        if (mPreviousData.containsKey(AppConstants.KEY_CONSTENT.POSTED_WITH_IN)) {
            int postedWithin = Integer.parseInt(mPreviousData.get(AppConstants.KEY_CONSTENT.POSTED_WITH_IN).toString());
            this.postedWithin = postedWithin;
            mBinding.etPostedWithinPange.setText(getPostedWithIn(postedWithin));
        }
        if (mPreviousData.containsKey((AppConstants.KEY_CONSTENT.DISTANCE))) {
            int distance = Integer.parseInt(mPreviousData.get(AppConstants.KEY_CONSTENT.DISTANCE).toString());
            if (distance == 1) {
                distance = 0;
            } else if (distance == 5) {
                distance = 25;
            } else if (distance == 10) {
                distance = 50;
            } else if (distance == 50) {
                distance = 75;
            }
            mBinding.indicatorDistance.setProgress(distance);
        }

    }

    private String getPostedWithIn(int postedWithin) {
        String postedWith = "";

        switch (postedWithin) {
            case 1:
                postedWith = getString(R.string.today);
                break;
            case 2:
                postedWith = getString(R.string.this_week);
                break;

            case 3:
                postedWith = getString(R.string.this_month);

                break;
            case 4:
                postedWith = getString(R.string.last_threee_month);
                break;

            case 5:
                postedWith = getString(R.string.this_year);

                break;

        }
        return postedWith;
    }

    private void setConditionData(String[] consditionArray) {
        for (int i = 0; i < consditionArray.length; i++) {
            setCondition(consditionArray[i]);
        }
    }

    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        showToastShort(failureResponse.getErrorMessage());
    }

    private void setUpSellerRating() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mBinding.rvSellerType.setLayoutManager(linearLayoutManager);
        adapter = new SellerTypeAdapter(mActivity, new String[]{"1", "2", "3", "4", "5"});
        mBinding.rvSellerType.setAdapter(adapter);
    }

    private void setProductCondition() {
        mBinding.rbNew.setOnCheckedChangeListener(this);
        mBinding.rbLikeNew.setOnCheckedChangeListener(this);
        mBinding.rbLikeNormal.setOnCheckedChangeListener(this);
        mBinding.rbGood.setOnCheckedChangeListener(this);
        mBinding.rbFlawed.setOnCheckedChangeListener(this);
        mBinding.filter.ivCross.setOnClickListener(this);
        mBinding.filter.tvReset.setOnClickListener(this);
        mBinding.tvCurrentLocation.setOnClickListener(this);
        mBinding.tvApply.setOnClickListener(this);
        mBinding.etPostedWithinPange.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_location:
                Intent in = new Intent(mActivity, ChoosseLocationActivity.class);
                startActivityForResult(in, AppConstants.ACTIVITY_RESULT.SEACH_LOACTION);
                break;
            case R.id.tv_current_location:
                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {
                    fetchCurrentLocation();
                }
                break;
            case R.id.iv_back:
                ((FilterActivity) mActivity).finish();
                break;
            case R.id.tv_apply:
                filterVerificationViewModel.validateFilter(mPreviousData, location, mBinding.etFromPriceRange.getText().toString(), mBinding.etToPricePange.getText().toString(), mBinding.rbNew.isChecked(), mBinding.rbLikeNew.isChecked(), mBinding.rbGood.isChecked(), mBinding.rbLikeNormal.isChecked(), mBinding.rbFlawed.isChecked(), adapter.getPosition(), mBinding.chkSellerVerified.isChecked(), postedWithin, mBinding.indicatorDistance.getProgress());
                break;
            case R.id.tv_reset:
                DataManager.getInstance().saveFilterLatitude(null);
                DataManager.getInstance().saveFilterLongitude(null);
                DataManager.getInstance().saveLocation(null);
                FilterManager.getInstance().setmFilterMap(null);

                Intent intent = new Intent();
                ((FilterActivity) mActivity).setResult(Activity.RESULT_OK);
                ((FilterActivity) mActivity).finish();
                break;
            case R.id.iv_cross:
                ((FilterActivity) mActivity).finish();
                break;
            case R.id.et_posted_within_pange:
                new CustomPostInDialog(mActivity, new OnDialogViewClickListener() {
                    @Override
                    public void onSubmit(String txt, int id) {
                        postedWithin = id;
                        mBinding.etPostedWithinPange.setText(txt);
                    }
                }).show();

                break;

        }
    }

    private void fetchCurrentLocation() {
        if (gpsTracker == null)
            gpsTracker = new GPSTracker(mActivity);
        if (gpsTracker.isGPSEnable()) {
            setCurrentLocationText();
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void setCurrentLocationText() {
        try {
            if (AppUtils.getAddressByLatLng(mActivity, gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0) != null) {
                location = AppUtils.getAddressByLatLng(mActivity, gpsTracker.getLatitude(), gpsTracker.getLongitude());
                setMarker(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
                mBinding.etLocation.setText(AppUtils.getAddressByLatLng(mActivity, gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0));
            } else {
                showToastLong(getString(R.string.unable_to_fatch_your_location));
            }
        } catch (Exception E) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.SEACH_LOACTION:
                if (data != null) {
                    String locAdd = data.getExtras().getString("Location");
                    location = AppUtils.getLocationFromAddress(mActivity, locAdd);
                    if (location != null) {
                        mBinding.etLocation.setText(location.getAddressLine(0));
                        setMarker(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                }
                break;
            case AppConstants.ACTIVITY_RESULT.GPS_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        new Thread().sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (gpsTracker.getLocation() != null) {
                        setCurrentLocationText();
                    }
                }
                break;

        }
    }

    private void setMarker(LatLng productDetailsModel) {
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(productDetailsModel.latitude, productDetailsModel.longitude));
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin));
        if (map != null) {
            map.getUiSettings().setAllGesturesEnabled(false);
            map.clear();
            map.addMarker(marker);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(productDetailsModel.latitude, productDetailsModel.longitude), 12));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rb_new:
                mBinding.rbNew.setChecked(isChecked);
                break;
            case R.id.rb_like_new:
                mBinding.rbLikeNew.setChecked(isChecked);
                break;
            case R.id.rb_like_normal:
                mBinding.rbLikeNormal.setChecked(isChecked);
                break;
            case R.id.rb_good:
                mBinding.rbGood.setChecked(isChecked);
                break;
            case R.id.rb_flawed:
                mBinding.rbFlawed.setChecked(isChecked);
                break;
        }
    }


    private void setCondition(String s) {
        switch (s) {
            case "1":
                mBinding.rbNew.setChecked(true);
                break;
            case "2":
                mBinding.rbLikeNew.setChecked(true);
                break;
            case "3":
                mBinding.rbGood.setChecked(true);
                break;
            case "4":
                mBinding.rbLikeNormal.setChecked(true);
                break;
            case "5":
                mBinding.rbFlawed.setChecked(true);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchCurrentLocation();
                }
                break;
        }
    }

}
