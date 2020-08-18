package com.taghawk.ui.home.filter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import com.taghawk.custom_dialog.CustomTagTypeDialog;
import com.taghawk.custom_dialog.CustomTagType_2Dialog;
import com.taghawk.databinding.FragmentTagFilterBinding;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.util.AppUtils;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.PermissionUtility;

import java.util.HashMap;

public class TagFilterFragment extends BaseFragment implements View.OnClickListener, OnMapReadyCallback {

    private Activity mActivity;
    private FragmentTagFilterBinding mBinding;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private FilterTagVerificationViewModel filterVerificationViewModel;
    private SellerTypeAdapter adapter;
    private Address location;
    private Observer<HashMap<String, Object>> filterData;
    private GPSTracker gpsTracker;
    private MutableLiveData<HashMap<String, Object>> mLiveData;
    private HashMap<String, Object> mPreviousData;
    private String[] distanceArray = {"1 Mile", "5 Mile", "10 Mile", "50 Mile", "MAX"};
    private int tagTypeId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentTagFilterBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }


    private void initView() {
        mActivity = getActivity();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        gpsTracker = new GPSTracker(mActivity);
        mPreviousData = new HashMap<>();
        mBinding.indicatorDistance.customTickTexts(distanceArray);
        getArgumentData();
        filterVerificationViewModel = ViewModelProviders.of(this).get(FilterTagVerificationViewModel.class);
        filterVerificationViewModel.setGenericListeners(mLiveData, getFailureResponseObserver());
        mBinding.etLocation.setOnClickListener(this);
        mBinding.tvCurrentLocation.setOnClickListener(this);
        mBinding.filter.ivCross.setOnClickListener(this);
        if (mPreviousData != null && mPreviousData.size() == 0)
            mBinding.tvCurrentLocation.performClick();
        setProductCondition();
        if (mPreviousData != null && mPreviousData.size() == 0)
            mBinding.tvCurrentLocation.performClick();
        filterVerificationViewModel.getmLiveData().observe(this, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(@Nullable HashMap<String, Object> stringObjectHashMap) {
                if (stringObjectHashMap != null) {
                    Intent intent = new Intent();
                    intent.putExtra("FILTER_DATA", stringObjectHashMap);
                    intent.putExtra("TAG_LOCATION", location);
                    ((TagFilterActivity) mActivity).setResult(Activity.RESULT_OK, intent);
                    ((TagFilterActivity) mActivity).finish();
                } else {
                    ((TagFilterActivity) mActivity).finish();
                }
            }
        });
    }

    private void getArgumentData() {
        if (getArguments() != null && getArguments().getSerializable("FILTER_DATA") != null) {
            mPreviousData = (HashMap<String, Object>) getArguments().getSerializable("FILTER_DATA");
            location = (Address) getArguments().get("TAG_LOCATION");
            if (mPreviousData.size() > 0) {
                setPreviousData(mPreviousData);
            }
        }
    }

    private void setPreviousData(HashMap<String, Object> mPreviousData) {
        if (mPreviousData.containsKey(AppConstants.KEY_CONSTENT.LAT) && mPreviousData.get(AppConstants.KEY_CONSTENT.LAT) != null) {
            if (location != null) {
//                location = AppUtils.getAddressByLatLng(mActivity, Double.valueOf(mPreviousData.get(AppConstants.KEY_CONSTENT.LAT).toString()), Double.valueOf(mPreviousData.get(AppConstants.KEY_CONSTENT.LONGI).toString()));
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                setMarker(latLng);
                mBinding.etLocation.setText(location.getAddressLine(0));
            }
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
        if (mPreviousData.containsKey(AppConstants.KEY_CONSTENT.TAG_TYPE)) {
            int tagType = Integer.parseInt(mPreviousData.get(AppConstants.KEY_CONSTENT.TAG_TYPE).toString());
            mBinding.etTagType.setText(getTagType(tagType));
        }

    }

    private String getTagType(int tagType) {
        String type = "";
        switch (tagType) {
            case 2:
                type = getString(R.string.public_txt);
                break;
            case 1:
                type = getString(R.string.private_txt);

                break;
        }
        return type;
    }

    private String getSubType(int subType) {
        if (subType == 1) {
            return getString(R.string.apartment);
        } else if (subType == 2) {
            return getString(R.string.university);
        }else if (subType == 3) {
            return getString(R.string.organization);
        }else if (subType == 4) {
            return getString(R.string.club);
        }else if (subType == 5) {
            return getString(R.string.other);
        } else{
            return "";
        }
    }



    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);
        showToastShort(failureResponse.getErrorMessage());
    }


    private void setProductCondition() {

        mBinding.filter.ivCross.setOnClickListener(this);
        mBinding.filter.tvReset.setVisibility(View.VISIBLE);
        mBinding.filter.tvReset.setOnClickListener(this);
        mBinding.tvCurrentLocation.setOnClickListener(this);
        mBinding.tvApply.setOnClickListener(this);
        mBinding.etTagType.setOnClickListener(this);
        mBinding.etTagMember.setOnClickListener(this);


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
                filterVerificationViewModel.validateFilter(mPreviousData, location, tagTypeId, mBinding.indicatorDistance.getProgress());
                break;
            case R.id.tv_reset:
                Intent intent = new Intent();
                ((TagFilterActivity) mActivity).setResult(Activity.RESULT_OK);
                ((TagFilterActivity) mActivity).finish();
                break;
            case R.id.iv_cross:
                ((TagFilterActivity) mActivity).finish();
                break;
            case R.id.et_tag_type:
                new CustomTagTypeDialog(mActivity, getString(R.string.private_txt), getString(R.string.public_txt), true, new OnDialogViewClickListener() {
                    @Override
                    public void onSubmit(String txt, int id) {
                        tagTypeId = id;
                        mBinding.etTagType.setText(txt);
                    }
                }).show();
                break;
            case R.id.et_tag_member:
                new CustomTagType_2Dialog(mActivity, getString(R.string.above_10_members),
                        getString(R.string.above_50_members),
                        getString(R.string.above_100_members),
                        getString(R.string.above_500_members),
                        getString(R.string.above_1000_members), true, new OnDialogViewClickListener() {
                    @Override
                    public void onSubmit(String txt, int id) {
                        mBinding.etTagMember.setText(txt);
                        //getTagTypeIdNew = id;
                        /*if (tagTypeId == 1) {
                            mBinding.llTypeContainer.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.llTypeContainer.setVisibility(View.GONE);
                        }*/
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

    public void sortTagData() {

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
