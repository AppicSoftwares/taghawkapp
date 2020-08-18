package com.taghawk.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.util.AppUtils;
import com.yalantis.ucrop.UCropFragmentCallback;

import java.io.File;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pub.devrel.easypermissions.EasyPermissions;

public class GalleryFragment extends MediaHandlerFragment implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.iv_close)
    AppCompatImageView ivClose;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.rl_butcket_container)
    RelativeLayout rlButcketContainer;
    @BindView(R.id.fl_crop_view_container)
    FrameLayout flCropViewContainer;
    @BindView(R.id.iv_fix_ratiosize)
    AppCompatImageView ivFixRatiosize;
    @BindView(R.id.fl_fit_size)
    FrameLayout flAspectRatio;
    @BindView(R.id.iv_select_multiple)
    AppCompatImageView ivSelectMultiple;
    @BindView(R.id.fl_select_multiple)
    FrameLayout flMultipleSelect;
    @BindView(R.id.ucrop_photobox)
    RelativeLayout uCropPhotobox;
    @BindView(R.id.rv_gallery)
    RecyclerView rvGallery;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.cl_main)
    ConstraintLayout clMain;

    private GalleryAdapter adapter;

    private View currentMediaSelectedView;

    private GalleryFragmentHost host;
    private GalleryViewModel galleryViewModel;

    int maxImages;
    /**
     * callback to observe media list after retrieving it in background
     */
    private Observer<List<GalleryMediaBean>> mediaListObserver = new Observer<List<GalleryMediaBean>>() {
        @Override
        public void onChanged(@Nullable List<GalleryMediaBean> GalleryMediaBeans) {
            if (GalleryMediaBeans != null && GalleryMediaBeans.size() > 0) {
//                deleteCurruptedImage(GalleryMediaBeans);
                adapter.submitList(GalleryMediaBeans);
                if (GalleryMediaBeans.get(0).getMediaType() == 1)
                    updateImage(GalleryMediaBeans.get(0).getUri());
                else if (GalleryMediaBeans.get(0).getMediaType() == 3) {
                    updateVideo(GalleryMediaBeans.get(0).getUri());
                }

                rvGallery.scrollToPosition(0);

            }
        }
    };

    /**
     * callback to observe bucket list containing images after retrieving it in backgound
     *//*
    private Observer<List<BucketBean>> bucketListObserver = new Observer<List<BucketBean>>() {
        @Override
        public void onChanged(@Nullable List<BucketBean> bucketList) {
//            bucketListAdapter.submitList(bucketList);
//            bucketListAdapter.notifyDataSetChanged();
            bucketAdapter.addAll(bucketList);

        }
    };*/
    public static GalleryFragment getInstance(Bundle extras) {
        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(extras);
        return fragment;
    }

    /* */

    private void deleteCurruptedImage(List<GalleryMediaBean> galleryMediaBeans) {
        for (int i = 0; i < galleryMediaBeans.size(); i++) {
            Uri uri = galleryMediaBeans.get(0).getUri();
            if (uri != null) {
                if (!new File(AppUtils.getRealPathFromURI(getActivity(), uri)).exists()) {
                    galleryMediaBeans.remove(i);
                }
            } else {
                galleryMediaBeans.remove(i);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UCropFragmentCallback) {
            setCallback((UCropFragmentCallback) context);
        } else throw new IllegalStateException("host must implement UCropFragmentCallback");

        if (context instanceof GalleryFragmentHost) {
            host = (GalleryFragmentHost) context;
        } else throw new IllegalStateException("host must implement GalleryFragmentHost");
    }


    @Override
    public void setCallback(UCropFragmentCallback callback) {
        super.setCallback(callback);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        unbinder = ButterKnife.bind(this, view);
        view.setTag(0);
        maxImages = getArguments().getInt("MAX_IMAGES", 10);
        setupViews(view, maxImages);
        initGalleryViews(view);
        initClickWidgets(view);
        initViewModel();

        return view;
    }

    private void initGalleryViews(View view) {

    /*    bucketAdapter = new ArrayAdapter<BucketBean>(getContext(), android.R.layout.simple_list_item_1);
        bucketSpinner.setAdapter(bucketAdapter);

        bucketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int type = (position == 0) ? 2 : 1;
                galleryViewModel.getFilteredGalleryList((bucketAdapter.getItem(position)).getBucketId(), type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/

        rvGallery.setLayoutManager(new GridLayoutManager(getContext(), 4));

        adapter = new GalleryAdapter(new DiffUtil.ItemCallback<GalleryMediaBean>() {
            @Override
            public boolean areItemsTheSame(GalleryMediaBean oldItem, GalleryMediaBean newItem) {
                return oldItem.getUri() != null && newItem.getUri() != null && oldItem.getUri().equals(newItem.getUri());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull GalleryMediaBean oldItem, @NonNull GalleryMediaBean newItem) {
                return oldItem.equals(newItem);
            }
        }, Objects.requireNonNull(getContext()));

        adapter.setRecyclerOnItemListener(new RecyclerOnItemListener<GalleryMediaBean>() {
            @Override
            public void onClick(View view, int position, GalleryMediaBean object, int requestCode) {
//                if (AppUtils.decodeFile(AppUtils.getRealPathFromURI(getActivity(), object.getUri())) != null) {
                rvGallery.smoothScrollToPosition(position);
                resetOldView(currentMediaSelectedView);
                currentMediaSelectedView = view;
                if (!isMultipleMediaSelected()) {
                    if (object.getMediaType() == 1) {
                        updateImage(object.getUri());
                    } else if (object.getMediaType() == 3) {
                        updateVideo(object.getUri());
                    }
                    currentMediaSelectedView.setAlpha(0.4f);
                } else {
                    if (object.getMediaType() == 1) {
                        int state = addImage(object.getUri());
                        updateViewState(view, state, position, object);
                    } else if (object.getMediaType() == 3) {
//                        int state = addVideo(object.getUri());
//                        updateViewState(view, state, position, object);
                    }
                }
//                }else {
//                    if(isMultipleMediaSelected()){
//                        if(object.getType()==1){
//                            if(alreadyContains(object.getUri())){
//                                int state = bringToFront(object.getUri());
//                                updateViewState(view, state, position, object);
//                            }else {
//                                AppUtils.showToast(getContext(),"Maximum 5 images/video can be added");
//                            }
//                        }else if(object.getType()==3){
//                            if(selectedVideoUris.contains(object.getUri())){
//                                int state = bringVideoToFront(object.getUri());
//                                updateViewState(view,state,position,object);
//                            }else {
//                                AppUtils.showToast(getContext(),"Maximum 5 images/video can be added");
//                            }
//                        }
//                    }
//                }
               /* } else {
                    AppUtils.showSnackbar(getActivity(), clMain, getResources().getString(R.string.image_not_found));
                }*/
            }

            @Override
            public boolean onLongClick(View view, int position, GalleryMediaBean object, int requestCode) {
                /*mediaCount = 0;
                setMultipleMediaSelected(!isMultipleMediaSelected());
                adapter.setInSelectedMode(isMultipleMediaSelected());
                if (isMultipleMediaSelected()) {
                    view.setAlpha(0.4f);
                } else {
                    view.setAlpha(1f);
                    removeMultiSelectedMedia();
                    updateAspectRatioToggleBehavior();
                }*/
                return false;
            }
        });

        rvGallery.setAdapter(adapter);
    }


    private void initClickWidgets(View view) {

        flAspectRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryFragment.this.toggleAspectRatio();
            }
        });

        flMultipleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryFragment.this.setMultipleMediaSelected(!GalleryFragment.this.isMultipleMediaSelected());
                adapter.setInSelectedMode(GalleryFragment.this.isMultipleMediaSelected());
                if (GalleryFragment.this.isMultipleMediaSelected()) {
                    v.setAlpha(0.4f);
                } else {
                    v.setAlpha(1f);
                    GalleryFragment.this.removeMultiSelectedMedia();
                    GalleryFragment.this.updateAspectRatioToggleBehavior();
                }
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host.showDialog();
                GalleryFragment.this.cropImage();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host.onCloseClicked();
            }
        });
    }


    private void initViewModel() {
        String perms[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (getActivity() != null && EasyPermissions.hasPermissions(getActivity(), perms)) {
            GalleryViewModelFactory factory = InjectorUtils.provideGalleryViewModelFactory(Objects.requireNonNull(getActivity()).getApplication());
            galleryViewModel = ViewModelProviders.of(this, factory).get(GalleryViewModel.class);
            galleryViewModel.getMediaList().observe(this, mediaListObserver);
        } else {
//            EasyPermissions.requestPermissions(GalleryFragment.this, getResources().getString(R.string.permission_req_msg),
//                    AppConstants.GALLERY_REQ_CODE, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.GALLERY_REQ_CODE: {
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
            }
            break;
        }
    }

    private void resetOldView(View currentMediaSelectedView) {
        if (currentMediaSelectedView != null) {
            currentMediaSelectedView.setAlpha(1f);
        }
    }


    private void updateViewState(View view, int state, int position, GalleryMediaBean object) {
        switch (state) {
            case MediaState.MEDIA_ADDED:

            case MediaState.MEDIA_BROUGHT_TO_TOP:
                ((CheckBox) currentMediaSelectedView.findViewById(R.id.cb_select_image)).setChecked(true);
                object.setSelected(true);
                view.setAlpha(0.4f);
                break;
            case MediaState.MEDIA_REMOVED:
                ((CheckBox) currentMediaSelectedView.findViewById(R.id.cb_select_image)).setChecked(false);
                object.setSelected(false);
                view.setAlpha(1f);
                break;
            case MediaState.MEDIA_MAX_LIMIT_REACHED:
//                Toast.makeText(getActivity(), getString(R.string.max_images_from_gallery), Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "cannot select more than " + maxImages + " media files", Toast.LENGTH_SHORT).show();
                break;
            case MediaState.VIDEO_MAX_LIMIT_REACHED:
                Toast.makeText(getContext(), "Only one video allowed", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private int addImage(Uri imageUri) {
//        updateAspectRatioToggleBehavior();
        return imageSelectedToAdd(imageUri);
    }


//    private int addVideo(Uri videoUri) {
//        return videoSelectedToAdd(videoUri);
//    }


    @Override
    protected void updateImage(Uri imageUri) {
        super.updateImage(imageUri);
//        updateAspectRatioToggleBehavior();
    }

    @Override
    protected void updateVideo(Uri videoUri) {
        super.updateVideo(videoUri);
        flAspectRatio.setVisibility(View.GONE);
    }

    private void updateAspectRatioToggleBehavior() {
        if (getIsAspectToggleEnable() && !isMultipleMediaSelected())
            flAspectRatio.setVisibility(View.VISIBLE);
        else
            flAspectRatio.setVisibility(View.GONE);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded()) {
            initViewModel();
        }
    }

    @Override
    public void cropImage() {
        super.cropImage();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switch (requestCode) {
            case AppConstants.GALLERY_REQ_CODE: {
                initViewModel();
            }
            break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        switch (requestCode) {
            case AppConstants.GALLERY_REQ_CODE: {
                if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//                    AppUtils.showPermissionsRequiredDialog(getActivity());
                }
            }
            break;
        }
    }


    public interface GalleryFragmentHost {
        void onCloseClicked();

        void showDialog();

        boolean checkStoragePermission();

        void requestStoragePermission();
    }

}