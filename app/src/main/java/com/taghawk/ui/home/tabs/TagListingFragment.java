package com.taghawk.ui.home.tabs;

import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;
import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.AdapterTagViewBinding;
import com.taghawk.databinding.LayoutInfoWindowBinding;
import com.taghawk.databinding.LayoutTagListingBinding;
import com.taghawk.model.tag.ClusterBean;
import com.taghawk.model.tag.MyItem;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tag.TagModel;
import com.taghawk.model.tag.TagSearchBean;
import com.taghawk.ui.home.HomeFragment;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.tag.AddTagActivity;
import com.taghawk.ui.tag.TagDetailsActivity;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.GPSTracker;
import com.taghawk.util.MyClusterRender;
import com.taghawk.util.PermissionUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class TagListingFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener {

    String type = "";
    private LayoutTagListingBinding mTAgBinding;
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
    private String tagType = "";
    private BaseActivity activity;

    public void displayReceivedData(String message) {
        Log.d("akjugf", message + "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTAgBinding = LayoutTagListingBinding.inflate(inflater, container, false);
        initView();
        setupMap();

        return mTAgBinding.getRoot();
    }


    public void initView() {
        mTAgBinding.ivmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (mTAgBinding.rlmap.getVisibility() == View.VISIBLE) {
                        mTAgBinding.rlmap.setVisibility(View.GONE);
                        mTAgBinding.ivAdd.setVisibility(View.VISIBLE);
                        mTAgBinding.ivmap.setImageResource(R.drawable.map);
                        mTAgBinding.rllist.setVisibility(View.VISIBLE);
                    } else {
                        mTAgBinding.ivmap.setImageResource(R.drawable.ic_list);
                        mTAgBinding.rlmap.setVisibility(View.VISIBLE);
                        mTAgBinding.ivAdd.setVisibility(View.VISIBLE);
                        mTAgBinding.rllist.setVisibility(View.GONE);
                    }
            }
        });

        mActivity = getActivity();
        activity = ((BaseActivity) mActivity);
        gpsTraker = new GPSTracker(mActivity);
        mTAgBinding.ivCurrentLocation.setOnClickListener(this);
        mTAgBinding.ivAddTag.setOnClickListener(this);
        mTAgBinding.ivAdd.setOnClickListener(this);

    }


    public void chnagelayout() {

    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
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
                    final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity.getApplicationContext());
                    HomeCategoryListAdapter homeCategoryListAdapter = new HomeCategoryListAdapter(mActivity, mTagList);
                    mTAgBinding.rvTagListing.setLayoutManager(mLayoutManager);
                    mTAgBinding.rvTagListing.setAdapter(homeCategoryListAdapter);
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

                    final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity.getApplicationContext());
                    HomeCategoryListAdapter homeCategoryListAdapter = new HomeCategoryListAdapter(mActivity, mTagList);
                    mTAgBinding.rvTagListing.setLayoutManager(mLayoutManager);
                    mTAgBinding.rvTagListing.setAdapter(homeCategoryListAdapter);
                    productHashMap = new HashMap<>();
                    for (int i = 0; i < mTagList.size(); i++) {
                        productHashMap.put(mTagList.get(i).getTagId(), mTagList.get(i));
                    }
                    addMarkerIntoTheMapWithZoom(productHashMap, mTagList);

                    if(productHashMap.size() == 0){
                        showToastShort("No Data Found");
                    }
                }
            }
        });
        mHomeViewModel.getTagList(AppConstants.REQUEST_CODE.TAG_LISTING, parms, tagType, getLcation());
    }

    private Location getLcation() {
        if (PermissionUtility.isPermissionGranted(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, AppConstants.ACTIVITY_RESULT.LOCATION_PERMISSION)) {
            if (gpsTraker != null) {
                if (gpsTraker.isGPSEnable()) {
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
                        MyClusterRender(mActivity, mGoogleMap, mClusterManager));
                mClusterManager.cluster();
            } else {

                if (mClusterManager != null)
                    mClusterManager.clearItems();
                if (mGoogleMap != null)
                    mGoogleMap.clear();
            }
        }else{
            mGoogleMap.clear();
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
            infoWindowBinding.tvTagTotalMembers.setText(allProductMap.get(new LatLng((marker.getPosition().latitude), marker.getPosition().longitude)).getTotalMembers() + " " + getString(R.string.members));
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

            case R.id.iv_add:
                if (!(DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    openAddTAgScreen();

                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, activity);
                }
                break;
        }
    }

    private void openAddTAgScreen() {
        Intent intent = new Intent(mActivity, AddTagActivity.class);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.TAG_CREATED);
    }

    public void sortTagData(String tagType) {
        this.tagType = tagType;
        try {

            mHomeViewModel.getTagList(AppConstants.REQUEST_CODE.TAG_LISTING, parms, tagType, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
                            mHomeViewModel.getTagList(AppConstants.REQUEST_CODE.TAG_LISTING, parms, tagType, null);
                    } else {
                        if (parms != null)
                            parms.clear();
                        mHomeViewModel.getTagList(AppConstants.REQUEST_CODE.TAG_LISTING, parms, tagType, getLcation());
                    }
                }
                break;
            case AppConstants.REQUEST_CODE.TAG_SEACHING_RESULT:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.getExtras() != null && data.getExtras().containsKey("TAG")) {
                        TagData tagData = (TagData) data.getExtras().get("TAG");
                        if (mTagList != null)
                            mTagList.clear();
                        mTagList.add(tagData);
                        try {


                            Context context = getActivity();
                            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                            HomeCategoryListAdapter homeCategoryListAdapter = new HomeCategoryListAdapter(context, mTagList);
                            mTAgBinding.rvTagListing.setLayoutManager(mLayoutManager);
                            mTAgBinding.rvTagListing.setAdapter(homeCategoryListAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                            final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity.getApplicationContext());
                            HomeCategoryListAdapter homeCategoryListAdapter = new HomeCategoryListAdapter(mActivity, mTagList);
                            mTAgBinding.rvTagListing.setLayoutManager(mLayoutManager);
                            mTAgBinding.rvTagListing.setAdapter(homeCategoryListAdapter);
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
       /* String type = PreferenceManager.getInstance(getActivity()).getfargtype();
        if (type.equals("1")) {
            mTAgBinding.rlmap.setVisibility(View.VISIBLE);
            mTAgBinding.rllist.setVisibility(View.GONE);

        } else {

            mTAgBinding.rlmap.setVisibility(View.GONE);
            mTAgBinding.rllist.setVisibility(View.VISIBLE);
        }*/
    }

//    @Override
//    public void onButtonPressed(String msg) {
//        Log.d("dklshfkljsadf",msg);
//    }


    public class HomeCategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private ArrayList<TagData> mCategoryList;

        public HomeCategoryListAdapter(Context context, ArrayList<TagData> mCategoryList) {
            this.context = context;
            this.mCategoryList = mCategoryList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            AdapterTagViewBinding mBinding = AdapterTagViewBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            //DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_category_view, viewGroup, false);
            return new HomeCategoryListAdapter.CategoryListViewHolder(mBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
            HomeCategoryListAdapter.CategoryListViewHolder holder = null;
            holder = (HomeCategoryListAdapter.CategoryListViewHolder) viewHolder;
            try {
                Picasso.get()
                        .load(mCategoryList.get(position).getTagImageUrl())
                        .error(R.mipmap.ic_launcher)
                        .fit()
                        .into(((CategoryListViewHolder) viewHolder).viewBinding.ivCart);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ((CategoryListViewHolder) viewHolder).viewBinding.tvName.setText(mCategoryList.get(position).getTagName());
            ((CategoryListViewHolder) viewHolder).viewBinding.tvDistance.setText(mCategoryList.get(position).getTagAddress());
            ((CategoryListViewHolder) viewHolder).viewBinding.tvMember.setText(mCategoryList.get(position).getTotalMembers() + " Member");
            ((CategoryListViewHolder) viewHolder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent detailsIntent = new Intent(mActivity, TagDetailsActivity.class);
                    detailsIntent.putExtra("TAG_ID", mCategoryList.get(position).getTagId() + "");
                    mActivity.startActivityForResult(detailsIntent, AppConstants.ACTIVITY_RESULT.TAG_DETAILS);
                }
            });
            if (mCategoryList.get(position).getTagType() == 1) {
                ((CategoryListViewHolder) viewHolder).viewBinding.tvType.setText("Public");
            } else {
                ((CategoryListViewHolder) viewHolder).viewBinding.tvType.setText("Private");
            }


        }

        @Override
        public int getItemCount() {
            return mCategoryList.size();
        }

        private class CategoryListViewHolder extends RecyclerView.ViewHolder {
            AdapterTagViewBinding viewBinding;

            public CategoryListViewHolder(AdapterTagViewBinding viewBinding) {
                super(viewBinding.getRoot());
                this.viewBinding = viewBinding;

            }

        }
    }
}
