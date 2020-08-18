package com.taghawk.ui.home.product_details;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taghawk.R;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.FragmentFullMapScreenBinding;
import com.taghawk.util.GPSTracker;

public class MapFullScreenFragment extends BaseFragment implements View.OnClickListener, OnMapReadyCallback {
    private GoogleMap map;

    private FragmentFullMapScreenBinding mBinding;
    private Activity mActivity;
    private Double lat, longi;
    private GPSTracker gpsTracker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentFullMapScreenBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {

        mActivity = getActivity();
        initMap();
        getArgumentsData();
        mBinding.includeHeader.ivCross.setImageDrawable(getResources().getDrawable(R.drawable.ic_back_black));
        mBinding.includeHeader.tvTitle.setText(getString(R.string.product_location));
        mBinding.includeHeader.ivCross.setOnClickListener(this);

    }

    private void initMap() {
        gpsTracker = new GPSTracker(mActivity);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void getArgumentsData() {
        if (getArguments() != null) {
            lat = getArguments().getDouble(AppConstants.KEY_CONSTENT.LAT);
            longi = getArguments().getDouble(AppConstants.KEY_CONSTENT.LONGI);
        }
    }

    private void setProductCircle() {
        if (map != null) {
            setCurrentMarker();
            map.addCircle(drawCircle(new LatLng(lat, longi)));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), 12));
        }
    }

    private CircleOptions drawCircle(LatLng point) {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(2500);

        // Border color of the circle
        circleOptions.strokeColor(Color.CYAN);

        // Fill color of the circle
        circleOptions.fillColor(Color.parseColor("#802bcefd"));

        // Border width of the circle
        circleOptions.strokeWidth(2);

        return circleOptions;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cross:
                ((MapFullScreenActivity) mActivity).finish();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null) {
            map = googleMap;
            setProductCircle();
        }
    }

    private void setCurrentMarker() {
        if (DataManager.getInstance().getFilterLatitude() != null && DataManager.getInstance().getFilterLatitude().trim().length() > 0 && Double.valueOf(DataManager.getInstance().getFilterLatitude()) > 0) {

           LatLng latLng = new LatLng(Double.valueOf(DataManager.getInstance().getFilterLatitude()) , Double.valueOf(DataManager.getInstance().getFilterLongitude()) );
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_blue_pin));
            map.addMarker(markerOptions);
        }
    }
}
