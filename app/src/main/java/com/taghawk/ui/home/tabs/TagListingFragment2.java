package com.taghawk.ui.home.tabs;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.taghawk.Jointag.Jointag;
import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.LayoutInfoWindowBinding;
import com.taghawk.databinding.LayoutTagListing2Binding;
import com.taghawk.databinding.LayoutTagListingBinding;
import com.taghawk.model.tag.ClusterBean;
import com.taghawk.model.tag.MyItem;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tag.TagModel;
import com.taghawk.model.tag.TagSearchBean;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.home.search.SearchAcivity;
import com.taghawk.ui.tag.AddTagActivity;
import com.taghawk.ui.tag.TagDetailsActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.MyClusterRender;
import com.taghawk.util.MyClusterRender2;
import com.taghawk.util.PermissionUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class TagListingFragment2 extends BaseFragment implements OnMapReadyCallback, View.OnClickListener {

    private LayoutTagListing2Binding mTAgBinding;
    private HomeViewModel mHomeViewModel;
    private ArrayList<TagData> mTagList = new ArrayList<>();
    private ClusterManager<MyItem> mClusterManager;
    private GoogleMap mGoogleMap;
    private Activity mActivity;
    private HashMap<LatLng, TagData> allProductMap;
    private HashMap<String, TagData> productHashMap;
    private GPSTracker gpsTraker;
    private ArrayList<ClusterBean> arrayListCluster;
    private String categoryId;
    private boolean isClear;
    private boolean isFilter;
    private HashMap<String, Object> parms;
    private int members;
    private BaseActivity activity;
    private GPSTracker gpsTracker;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTAgBinding = LayoutTagListing2Binding.inflate(inflater, container, false);
        initView();


        checkLocationPermission();
        if (!gpsTracker.isGPSEnable()) {
            gpsTracker.showSettingsAlert();
        }else{

            setupMap();
            moveToMyLocation();
        }
        return mTAgBinding.getRoot();
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("location permission")
                        .setMessage("location permission")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                                Log.d("vishal123","1");
                                moveToMyLocation();
                            }
                        })
                        .create()
                        .show();


            } else {
                Log.d("vishal123","2");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                moveToMyLocation();
            }
            return false;
        } else {
            Log.d("vishal123","3");
            return true;
        }
    }


    private void initView() {
        mActivity = getActivity();
        gpsTracker = new GPSTracker(getActivity());
        fetchCurrentLocation();
        activity = ((BaseActivity) mActivity);
//        gpsTraker = new GPSTracker(mActivity);
        mTAgBinding.ivCurrentLocation.setOnClickListener(this);
        mTAgBinding.ivAddTag.setOnClickListener(this);
        mTAgBinding.tvSearch.setOnClickListener(this);
        mTAgBinding.btndone.setOnClickListener(this);
        mTAgBinding.ivClose.setOnClickListener(this);
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("vishal123","6");
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.d("vishal123","4");
                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                        setupMap();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mHomeViewModel.getTagListing().observe(this, new Observer<TagModel>() {

            @Override
            public void onChanged(@Nullable TagModel tagModel) {
                getLoadingStateObserver().onChanged(false);
                if (tagModel.getCode() == 200) {
                    mTagList.clear();
                    if (mGoogleMap != null) {
                        mGoogleMap.clear();
                    }
                    mTagList.addAll(tagModel.getmTagListData());
                    productHashMap = new HashMap<>();
                    for (int i = 0; i < mTagList.size(); i++) {
                        productHashMap.put(mTagList.get(i).getTagId(), mTagList.get(i));
                    }
                    addMarkerIntoTheMapWithZoom(productHashMap, mTagList);

                }

            }
        });
        mHomeViewModel.getTagSeachViewModel().observe(this, new Observer<TagSearchBean>() {
            @Override
            public void onChanged(@Nullable TagSearchBean tagSearchBean) {
                getLoadingStateObserver().onChanged(false);
                if (tagSearchBean.getCode() == 200) {
                    mTagList.clear();
                    mTagList.addAll(tagSearchBean.getmTagSearchList());
                    productHashMap = new HashMap<>();
                    for (int i = 0; i < mTagList.size(); i++) {
                        productHashMap.put(mTagList.get(i).getTagId(), mTagList.get(i));
                    }
                    addMarkerIntoTheMapWithZoom(productHashMap, mTagList);
                }
            }
        });
        mHomeViewModel.getTagList(AppConstants.REQUEST_CODE.TAG_LISTING, parms, members, getLcation());
    }

    private Location getLcation() {
        if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {
            if (gpsTraker != null) {
                if (gpsTraker.isGPSEnable()) {
                    moveToMyLocation();
                    return gpsTraker.getLocation();
                }
            }
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        if (mGoogleMap != null) {
            moveToMyLocation();
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
//                    View view = showInfoWindow(marker);
                    Intent detailsIntent = new Intent(mActivity, TagDetailsActivity.class);
                    detailsIntent.putExtra("TAG_ID", String.valueOf(allProductMap.get(new LatLng((marker.getPosition().latitude), marker.getPosition().longitude)).getTagId()));
                    mActivity.startActivityForResult(detailsIntent, AppConstants.ACTIVITY_RESULT.TAG_DETAILS);
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent detailsIntent = new Intent(mActivity, TagDetailsActivity.class);
                    detailsIntent.putExtra("TAG_ID", String.valueOf(allProductMap.get(new LatLng((marker.getPosition().latitude), marker.getPosition().longitude)).getTagId()));
                    mActivity.startActivityForResult(detailsIntent, AppConstants.ACTIVITY_RESULT.TAG_DETAILS);
                }
            });
        }
    }

    private void addMarkerIntoTheMapWithZoom(HashMap<String, TagData> allProduct, ArrayList<TagData> mList) {
        if (mList.size() > 0) {
            if (mGoogleMap != null) {
                mGoogleMap.clear();
                mClusterManager = new ClusterManager<MyItem>(mActivity, mGoogleMap);
                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        return true;
                    }
                });

                mGoogleMap.setOnMarkerClickListener(mClusterManager);
                mGoogleMap.setOnCameraIdleListener(mClusterManager);
                addDataToRender(mList);
                mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
                    @Override
                    public boolean onClusterClick(Cluster<MyItem> cluster) {
                        zoomMapOnClusterClick(cluster);
                        return true;
                    }
                });
                mClusterManager.setRenderer(new
                        MyClusterRender2(mActivity, mGoogleMap, mClusterManager));
                mClusterManager.cluster();
            } else {

                if (mClusterManager != null)
                    mClusterManager.clearItems();
                if (mGoogleMap != null)
                    mGoogleMap.clear();
            }
        }
    }

    private void zoomMapOnClusterClick(Cluster<MyItem> cluster) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                cluster.getPosition(), (float) Math.floor(mGoogleMap
                        .getCameraPosition().zoom + 1)), 300,
                null);
    }


    private void initRowsInCluster(ArrayList<LatLng> arrayList, ArrayList<String> productIdFromClusters) {
        arrayListCluster = new ArrayList<>();

        for (int i = 0; i < productIdFromClusters.size(); i++) {
            if (productHashMap.containsKey(productIdFromClusters.get(i))) {

                ClusterBean clusterBean = new ClusterBean();
                clusterBean.setmTagName(productHashMap.get(productIdFromClusters.get(i)).getTagName());
                clusterBean.setmTagId(productHashMap.get(productIdFromClusters.get(i)).getTagId());
                clusterBean.setmTagTotalMember("" + productHashMap.get(productIdFromClusters.get(i)).getTotalMembers());
                clusterBean.setmTagImage(productHashMap.get(productIdFromClusters.get(i)).getTagImageUrl());
                clusterBean.setmTagType(productHashMap.get(productIdFromClusters.get(i)).getTagType());
                clusterBean.setMember(productHashMap.get(productIdFromClusters.get(i)).isTagMember());
                clusterBean.setmFounder(productHashMap.get(productIdFromClusters.get(i)).getAdminName());
                arrayListCluster.add(clusterBean);
            }
        }
    }


    private void moveToMyLocation() {

        if (gpsTraker == null)
            gpsTraker = new GPSTracker(mActivity);
        if (gpsTraker.getLocation() != null) {
            LatLng coordinate = new LatLng(gpsTraker.getLocation().getLatitude(), gpsTraker.getLocation().getLongitude());
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    coordinate, 14);
            if (mGoogleMap != null)
                mGoogleMap.animateCamera(location);
        }

    }
   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!gpsTracker.isGPSEnable()) {
                        gpsTracker.showSettingsAlert();
                    } else {
                        Log.d("iiiresult","vihasl12");
//                        moveToMyLocation();
                        fetchCurrentLocation();

                    }
                }
                break;

        }
    }*/
    private void fetchCurrentLocation() {
        if (gpsTracker == null)
            gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.isGPSEnable()) {
            setCurrentLocationText();
            moveToMyLocation();
        }

        Log.d("iiiresult","vihasl1");
    }
    private Address location;
    private void setCurrentLocationText() {
        try {
            if (AppUtils.getAddressByLatLng(getActivity(), gpsTracker.getLatitude(), gpsTracker.getLongitude()).getAddressLine(0) != null) {
                location = AppUtils.getAddressByLatLng(getActivity(), gpsTracker.getLatitude(), gpsTracker.getLongitude());
                DataManager.getInstance().saveFilterLatitude(String.valueOf(location.getLatitude()));
                DataManager.getInstance().saveFilterLongitude(String.valueOf(location.getLongitude()));
                DataManager.getInstance().saveLocation(location.getAddressLine(0));
            }
        } catch (Exception E) {
        }
    }
    private void addDataToRender(ArrayList<TagData> allProduct) {
        allProductMap = new HashMap<>();
        for (int i = 0; i < allProduct.size(); i++) {
            LatLng latLng = new LatLng(Double.valueOf(allProduct.get(i).getTagLatitude()),
                    Double.valueOf(allProduct.get(i).getTagLongitude()));
            addItems(latLng.latitude, latLng.longitude, mTagList.get(i).getTagId() + "", mTagList.get(i).isTagMember(), mTagList.get(i).getTagImageUrl(), mTagList.get(i).getTagName());
            allProductMap.put(latLng, allProduct.get(i));
        }
    }


    /*
     * add the latlongs to the cluster manager
     */
    private void addItems(Double lat, Double lng, String id, boolean tagMember, String imageUrl, String tagName) {
        MyItem offsetItem = new MyItem(lat, lng, id, tagMember, imageUrl, tagName);
        mClusterManager.addItem(offsetItem);
    }

    @Nullable
    private View showInfoWindow(final Marker marker) {
        View view = null;
        LayoutInfoWindowBinding infoWindowBinding;
        infoWindowBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.layout_info_window, null, false);
//        marker.setTag();
        if (allProductMap.containsKey(new LatLng((marker.getPosition().latitude), marker.getPosition().longitude))) {
            infoWindowBinding.tvTagName.setText(String.valueOf(allProductMap.get(new LatLng((marker.getPosition().latitude), marker.getPosition().longitude)).getTagName()));
            infoWindowBinding.tvTagTotalMembers.setText(String.valueOf(allProductMap.get(new LatLng((marker.getPosition().latitude), marker.getPosition().longitude)).getTotalMembers()) + " " + getString(R.string.members));
//            Glide.with(mActivity).load(String.valueOf(allProductMap.get(new LatLng((marker.getPosition().latitude), marker.getPosition().longitude)).getTagImageUrl())).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(infoWindowBinding.ivTag);

        }

        return infoWindowBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_current_location:
                if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {
                    if (gpsTraker != null) {
                        if (gpsTraker.isGPSEnable()) {
                            moveToMyLocation();
                        } else {
                            gpsTraker.showSettingsAlert();
                        }
                    }
                }
                break;
            case R.id.iv_add_tag:
                if (!(DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    openAddTAgScreen();

                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, activity);
                }
                break;
                case R.id.tv_search:
                    Intent intent = new Intent(mActivity, SearchAcivity.class);
                        intent.putExtra("IS_FROM", 1);
                        intent.putExtra("CATEGORY", categoryId);
                        intent.putExtra("SEARCH_TITTLE", "ghjkghjk");
                        intent.putExtra("FILTER_DATA", new HashMap<>());
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.TAG_SEACHING_RESULT);
                break;
                case R.id.btndone:
                    getActivity().finishAffinity();
//        Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(new Intent(getActivity(), HomeActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;

            case R.id.iv_close:
                if (mTAgBinding.tvSearch.getText().toString().length() > 0) {
                    hideTagCloseButton();
                    mTAgBinding.tvSearch.setText("");
//                    currentFragmentTagMethod(0);
                    sortTagData(0);
                }
                break;
        }
    }

    private void hideTagCloseButton() {
        mTAgBinding.ivClose.setVisibility(View.VISIBLE);
        mTAgBinding.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_search));
        tagSearch = "";
    }

    private void openAddTAgScreen() {
        Intent intent = new Intent(mActivity, AddTagActivity.class);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.TAG_CREATED);
    }

    public void sortTagData(int members) {
        this.members = members;
        mHomeViewModel.getTagList(AppConstants.REQUEST_CODE.TAG_LISTING, parms, members, null);
    }

String tagSearch = "";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.TAG_FILTER:
                if (resultCode == Activity.RESULT_OK) {
                    isFilter = true;
                    if (data != null && data.getExtras() != null && data.getExtras().containsKey("FILTER_DATA")) {
                        parms = (HashMap<String, Object>) data.getExtras().get("FILTER_DATA");
                        if (parms != null && parms.size() > 0)
                            mHomeViewModel.getTagList(AppConstants.REQUEST_CODE.TAG_LISTING, parms, members, null);
                    } else {
                        if (parms != null)
                            parms.clear();
                        mHomeViewModel.getTagList(AppConstants.REQUEST_CODE.TAG_LISTING, parms, members, getLcation());
                    }
                }
                break;
            case AppConstants.REQUEST_CODE.TAG_SEACHING_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.getExtras() != null && data.getExtras().containsKey("TAG")) {
                        TagData tagData = (TagData) data.getExtras().get("TAG");
                        tagSearch = tagData.getTagName();
                        mTAgBinding.tvSearch.setText(tagSearch);
                        mTAgBinding.ivClose.setVisibility(View.VISIBLE);
                        if (tagSearch != null && tagSearch.length() > 0)
                            mTAgBinding.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_gallery_close));
                        else
                            mTAgBinding.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_search));
                        if (mTagList != null)
                            mTagList.clear();
                        mTagList.add(tagData);
                        productHashMap = new HashMap<>();
                        for (int i = 0; i < mTagList.size(); i++) {
                            productHashMap.put(mTagList.get(i).getTagId(), mTagList.get(i));
                        }
                        if (mTagList.size() > 0) {
                            addMarkerIntoTheMapWithZoom(productHashMap, mTagList);

                            LatLng coordinate = new LatLng(Double.valueOf(mTagList.get(0).getTagLatitude()), Double.valueOf(mTagList.get(0).getTagLongitude()));
                            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                    coordinate, 14);
                            if (mGoogleMap != null)
                                mGoogleMap.animateCamera(location);
                        }
                    } else if (data != null && data.getExtras() != null && data.getExtras().containsKey("SEARCH_KEY")) {
                        String search = data.getExtras().getString("SEARCH_KEY");
                        mTAgBinding.tvSearch.setText(search);
                        if (search != null && search.length() > 0)
                            mTAgBinding.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_gallery_close));
                        else
                        mTAgBinding.ivClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_search));
                        mHomeViewModel.getTagSearch(parms, search);
                    }
                }
                break;
            case AppConstants.ACTIVITY_RESULT.TAG_CREATED:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.getExtras() != null && data.getExtras().containsKey("TAG_CREATED")) {
                        TagData tagData = (TagData) data.getExtras().get("TAG_CREATED");
                        if (tagData != null) {
//                            mTagList.add(tagData);
//                            if (mTagList != null)
//                                mTagList.clear();
                            mTagList.add(tagData);
                            productHashMap = new HashMap<>();
                            for (int i = 0; i < mTagList.size(); i++) {
                                productHashMap.put(mTagList.get(i).getTagId(), mTagList.get(i));
                            }
                            if (mTagList.size() > 0) {
                                addMarkerIntoTheMapWithZoom(productHashMap, mTagList);

                                LatLng coordinate = new LatLng(Double.valueOf(tagData.getTagLatitude()), Double.valueOf(tagData.getTagLongitude()));
                                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                        coordinate, 14);
                                if (mGoogleMap != null)
                                    mGoogleMap.animateCamera(location);
                            }
                        }
                    }
                }
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
