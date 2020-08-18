package com.taghawk.ui.tag;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jumio.nv.NetverifySDK;
import com.taghawk.R;
import com.taghawk.adapters.AddDocumentAdapter;
import com.taghawk.adapters.ProductListAdapter;
import com.taghawk.adapters.TagProductListAdapter;
import com.taghawk.base.BaseActivity;
import com.taghawk.base.BaseFragment;
import com.taghawk.camera2basic.RecyclerListener;
import com.taghawk.constants.AppConstants;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.data.DataManager;
import com.taghawk.data.preferences.PreferenceManager;
import com.taghawk.databinding.BottomSheetDialogJoinByDocumentBinding;
import com.taghawk.databinding.BottomSheetDialogJoinByEmailBinding;
import com.taghawk.databinding.BottomSheetDialogJoinByPasswordBinding;
import com.taghawk.databinding.TagDetailsFragmentBinding;
import com.taghawk.gallery_picker.ImagesGallery;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.interfaces.RecyclerViewCallback;
import com.taghawk.model.FailureResponse;
import com.taghawk.model.chat.ChatModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.home.ImageList;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.model.request.User;
import com.taghawk.model.tag.TagData;
import com.taghawk.model.tag.TagDetailsData;
import com.taghawk.model.tag.TagDetailsModel;
import com.taghawk.ui.chat.MessagesDetailActivity;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;
import com.taghawk.ui.home.search.SearchTagShelfAcivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.PermissionUtility;
import com.taghawk.util.ResourceUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class TagDetailsFragment extends BaseFragment implements View.OnClickListener, AmazonCallback {

    private TagDetailsFragmentBinding mBinding;
    private TagViewModel mTagDetailsViewModel;
    private ArrayList<ProductListModel> mProductList;
    private TagProductListAdapter adapter;
    private Activity mActivity;
    private String tagId = "";
    private TagDetailsData mData;
    BottomSheetDialog mDialog;
    AddDocumentAdapter addDocumentAdapter;
    private ArrayList<String> mFileArrayList;
    private ArrayList<ImageList> mImageList;
    private AmazonS3 mAmazonS3;
    private int imageUploadCount;
    private String notificationId;
    private BaseActivity activity;
    private boolean isChatClicked;
    private final static String TAG = "JumioSDK_DV";
    private NetverifySDK netverifySDK;

    private int productPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = TagDetailsFragmentBinding.inflate(inflater, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        activity = ((BaseActivity) mActivity);
        mActivity = getActivity();
        mFileArrayList = new ArrayList<>();
        mImageList = new ArrayList<>();
        setUpAmazonS3();
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(localReceiver,
                new IntentFilter(AppConstants.BROAD_CAST_TAG_JOINED_ACTION));
        mBinding.includeHeader.tvReset.setVisibility(View.GONE);
        mBinding.includeHeader.ivShare.setVisibility(View.VISIBLE);
        mBinding.includeHeader.ivCross.setImageResource(R.drawable.ic_back_black);
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.includeHeader.ivShare.setOnClickListener(this);
        mBinding.includeHeader.ivCross.setOnClickListener(this);
        mBinding.tvEdit.setOnClickListener(this);
        mBinding.tvJoin.setOnClickListener(this);
        mBinding.tvViewAll.setOnClickListener(this);
        mBinding.includeHeader.ivShare.setVisibility(View.VISIBLE);
        if (getArguments() != null) {
            tagId = getArguments().getString("TAG_ID");
            notificationId = getArguments().getString(AppConstants.NOTIFICATION_ACTION.NOTIFICATION_ID, "");
        }
        setUpProductList();
    }

    private void setUpProductList() {
        mProductList = new ArrayList<>();
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        adapter = new TagProductListAdapter(mProductList, new RecyclerViewCallback() {
            @Override
            public void onClick(int position, View view) {
                Log.e("tag_product", "Position: " + position);
                productPosition = position;
                mTagDetailsViewModel.visitTag(tagId, mProductList.get(position).get_id());
            }
        });
        mBinding.rvTagProducts.setLayoutManager(layoutManager);
        mBinding.rvTagProducts.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTagDetailsViewModel = ViewModelProviders.of(this).get(TagViewModel.class);
        mTagDetailsViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mTagDetailsViewModel.mGetTagDetailsViewModel().observe(this, new Observer<TagDetailsModel>() {
            @Override
            public void onChanged(@Nullable TagDetailsModel tagDetailsModel) {
                getLoadingStateObserver().onChanged(false);
                if (tagDetailsModel.getCode() == 200) {
                    switch (tagDetailsModel.getRequestCode()) {
                        case AppConstants.REQUEST_CODE.TAG_DEtAILS:
                            mData = tagDetailsModel.getTagDetailsData();
                            setTagDetails(tagDetailsModel.getTagDetailsData());
                            break;
                    }
                } else if (tagDetailsModel.getCode() == 404) {
                    getCustomBottomDialog(getString(R.string.oops), tagDetailsModel.getMessage(), new OnDialogItemClickListener() {
                        @Override
                        public void onPositiveBtnClick() {
                            ((TagDetailsActivity) mActivity).finish();
                        }

                        @Override
                        public void onNegativeBtnClick() {

                        }
                    });
                } else {
                    showToastLong(tagDetailsModel.getMessage());
                }
            }
        });
        if (notificationId != null && notificationId.length() > 0) {
            mTagDetailsViewModel.markNotificationRead(notificationId);
        }
        mTagDetailsViewModel.hitTagDetails(tagId, AppConstants.REQUEST_CODE.TAG_DEtAILS, 6);

        mTagDetailsViewModel.getmJoinMemberLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                if (commonResponse.getCode() == 200) {
                    showToastShort(commonResponse.getMessage());
                    if (mData.getTagType() != 2 && mData.getJoinTagBy() == 1 || mData.getJoinTagBy() == 3) {
                        if (mData.getJoinTagBy() == AppConstants.TAG_VERIFICATION_METHOD.DOCUMENT)
                            mTagDetailsViewModel.updatePendingRequestCount(mData.getTagId(), false);
                        mData.setRequestStatus(4);
                        mBinding.tvJoin.setText(getString(R.string.pending));
//                        mBinding.tvJoin.setEnabled(false);
                    } else {
                        mData.setMember(true);
                        TagData tagData = new TagData();
                        tagData.setTagImageUrl(mData.getTagImageUrl());
                        tagData.setTagId(mData.getTagId());
                        tagData.setTagName(mData.getTagName());
                        mTagDetailsViewModel.joinTagOnFirebase(DataManager.getInstance().getUserDetails(), tagData);
                        mBinding.tvJoin.setText(getString(R.string.chat));
                        mBinding.tvJoin.setEnabled(true);
                    }
//                    mBinding.tvJoin.setEnabled(false);
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                }
            }
        });
        mTagDetailsViewModel.cancelPendingRequest().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
                getLoadingStateObserver().onChanged(false);
                mBinding.tvJoin.setText(getString(R.string.apply));

                mData.setRequestStatus(3);
            }
        });
        mTagDetailsViewModel.mGetVisitedTagViewModel().observe(this, new Observer<TagDetailsModel>() {
            @Override
            public void onChanged(@Nullable TagDetailsModel tagDetailsModel) {
                getLoadingStateObserver().onChanged(false);
                try {
                    Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                    intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, mProductList.get(productPosition).get_id());
                    intent.putExtra(AppConstants.KEY_CONSTENT.TAG_ID, tagId);
                    startActivity(intent);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setTagDetails(TagDetailsData mTagDetails) {
        if (mProductList != null) {
            mProductList.clear();
        }
        mProductList.addAll(mTagDetails.getmTagProducts());
        mBinding.tvTagName.setText(mTagDetails.getTagName());
        mBinding.includeHeader.tvTitle.setText(mTagDetails.getTagName());
        mBinding.tvTagDescription.setText(mTagDetails.getTagDescription());
        mBinding.tvTagFounderName.setText(mTagDetails.getOwnerName());
        if (mTagDetails.getTagImageUrl() != null && mTagDetails.getTagImageUrl().length() > 0)
            Glide.with(this).asBitmap().load(mTagDetails.getTagImageUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(mBinding.ivTagImage);
        mBinding.tvTagType.setText(getTagType(mTagDetails.getTagType()));
        mBinding.tvTagTypeNew.setText(getSubType(mTagDetails.getSubType()));
        mBinding.tvEdit.setVisibility(View.GONE);
        if (mTagDetails.isRequestStatus() == 4) {
//            mBinding.tvJoin.setEnabled(false);
            mBinding.tvJoin.setText(getString(R.string.pending));
        }
        if (mData.isCreatedByMe()) {
            mBinding.tvJoin.setText(getString(R.string.chat));
            mBinding.tvEdit.setVisibility(View.VISIBLE);

        } else if (mData.isMember()) {
            mBinding.tvJoin.setEnabled(true);
            mBinding.tvJoin.setText(getString(R.string.chat));
        }


        mBinding.tvMembers.setText(mTagDetails.getTagTotalMembers() + " " + getString(R.string.members));
        mBinding.tvViewAll.setVisibility(View.GONE);
        if (mTagDetails.getmTagProducts() != null && mTagDetails.getmTagProducts().size() > 0) {
            adapter.notifyDataSetChanged();
            if (mTagDetails.getmTagProducts().size() >= 4) {
                mBinding.tvViewAll.setVisibility(View.VISIBLE);
            } else {
                mBinding.tvViewAll.setVisibility(View.GONE);

            }
        }
//        else
//            mBinding.llViewAll.setVisibility(View.GONE);
        mBinding.tvJoin.setClickable(true);
        mBinding.tvJoin.setOnClickListener(this);
    }

    private String getTagType(int tagType) {
        if (tagType == 2) {
            mBinding.tvJoin.setText(getString(R.string.join));
            return getString(R.string.public_txt);
        } else {
            mBinding.tvJoin.setText(getString(R.string.apply));
            return getString(R.string.private_txt);
        }
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
            return getString(R.string.other);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share:
                if (mData != null)
                    AppUtils.share(mActivity, mData.getShareLink(), getString(R.string.share_tag), getString(R.string.share));
                break;
            case R.id.iv_cross:
                ((TagDetailsActivity) mActivity).finish();
                break;
            case R.id.tv_join:
                if (!(DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    if (mData != null) {
                        if (mData.isMember() || mData.isCreatedByMe()) {
                            if (!isChatClicked) {
                                isChatClicked = true;

                                final User user = new Gson().fromJson(PreferenceManager.getInstance(mActivity).getString(AppConstants.PreferenceConstants.USER_DETAILS), User.class);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child(AppConstants.FIREBASE.FIREBASE_ROOMS_NODE).child(user.getUserId()).child(mData.getTagId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ChatModel chatModel = null;
                                        if (dataSnapshot.exists())
                                            chatModel = dataSnapshot.getValue(ChatModel.class);
                                        else {
                                            chatModel = new ChatModel();
                                            chatModel.setChatMute(false);
                                            chatModel.setPinned(false);
                                            chatModel.setCreatedTimeStamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                                            chatModel.setChatType(AppConstants.FIREBASE.FIREBASE_GROUP_CHAT);
                                            chatModel.setRoomName(mData.getTagName());
                                            chatModel.setRoomImage(mData.getTagImageUrl());
                                            chatModel.setUserType(String.valueOf(user.getUserType()));
                                            chatModel.setRoomId(mData.getTagId());
                                            chatModel.setOtherUserId(mData.getTagId());
                                        }
                                        startActivityForResult(new Intent(mActivity, MessagesDetailActivity.class).putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel).putExtra(AppConstants.FIREBASE.TIMESTAMP, chatModel.getCreatedTimeStampLong()), AppConstants.ACTIVITY_RESULT.TAG_DETAILS);
                                        isChatClicked = false;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        isChatClicked = false;

                                    }
                                });
                            }
                        } else {
                            if (mData != null && mData.getTagType() == 1) {
                                if (mData.getRequestStatus() == 4) {
                                    DialogUtil.getInstance().CustomCommonBottomSheetDialog(mActivity, "", "Are you sure you want to cancel request for join this Tag.", getString(R.string.ok), getString(R.string.cancel), new DialogCallback() {
                                        @Override
                                        public void submit(String data) {
                                            if (AppUtils.isInternetAvailable(mActivity)) {
                                                mTagDetailsViewModel.acceptRejectTagRequest(DataManager.getInstance().getUserDetails().getUserId(), mData.getTagId(), 2);
                                            } else {
                                                showNoNetworkError();
                                            }
                                        }

                                        @Override
                                        public void cancel() {

                                        }
                                    });

                                } else
                                    performApplyAction();
                            } else {
                                HashMap<String, Object> params = new HashMap<>();
                                params.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, mData.getTagId());
                                mTagDetailsViewModel.joinTag(params);
                            }
                        }
                    }
                } else {
                    DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, ((BaseActivity) mActivity));
                }
                break;
            case R.id.tv_edit:
                if (!(DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                    if (mData != null && mData.isCreatedByMe()) {
                        openEditTagActivity();
                    }
                }
                break;
            case R.id.tv_view_all:
                Intent intent = new Intent(mActivity, SearchTagShelfAcivity.class);
                intent.putExtra(AppConstants.BUNDLE_DATA, mData.getTagId());
                intent.putExtra("IS_FROM", 3);
                intent.putExtra(AppConstants.TAG_KEY_CONSTENT.NAME, mData.getTagName());
                intent.putExtra(AppConstants.KEY_CONSTENT.TYPE, AppConstants.ACTIVITY_RESULT.VIEW_PRODUCT);
                mActivity.startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.TAG_DETAILS);
                break;
        }
    }

    private void openEditTagActivity() {
        Intent intent = new Intent(mActivity, EditTagActivity.class);
        intent.putExtra(AppConstants.BUNDLE_DATA, mData);
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.EDIT_TAG);
    }


    private void performApplyAction() {
        if (mData != null) {
            switch (mData.getJoinTagBy()) {
                case AppConstants.TAG_VERIFICATION_METHOD.EMAIL:
                    emailVerifyBottomSheetDialog();
                    break;
                case AppConstants.TAG_VERIFICATION_METHOD.PASSWORD:
                    passwordVerifyBottomSheetDialog();
                    break;
                case AppConstants.TAG_VERIFICATION_METHOD.DOCUMENT:
                    documentVerifyBottomSheetDialog();
                    break;
            }
        }
    }

    public void emailVerifyBottomSheetDialog() {
        mDialog = new BottomSheetDialog(mActivity);
        final BottomSheetDialogJoinByEmailBinding binding = BottomSheetDialogJoinByEmailBinding.inflate(LayoutInflater.from(mActivity));
        mDialog.setContentView(binding.getRoot());
        mDialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(mActivity.getResources().getColor(android.R.color.transparent));
        binding.tvMessage.setText(mActivity.getResources().getString(R.string.apply_email).replace(getString(R.string.tag_name_pattern), mData.getTagName()));
        binding.tvDomainText.setText(String.format("%s%s", "@", mData.getEmail()));

        binding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etVerificationEmail.getText().toString().length() == 0) {
                    showToastShort(ResourceUtils.getInstance().getString(R.string.enter_email_id));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(String.format("%s%s%s", binding.etVerificationEmail.getText().toString(), "@", mData.getEmail())).matches()) {
                    showToastShort(ResourceUtils.getInstance().getString(R.string.enter_valid_email));
                } else {
                    getLoadingStateObserver().onChanged(true);
                    HashMap<String, Object> params = new HashMap<>();
                    params.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, mData.getTagId());
                    params.put(AppConstants.TAG_KEY_CONSTENT.JOIN_TAG_BY, AppConstants.TAG_VERIFICATION_METHOD.EMAIL);
                    params.put(AppConstants.TAG_KEY_CONSTENT.REQUEST_PARAMETER, String.format("%s%s%s", binding.etVerificationEmail.getText().toString(), "@", mData.getEmail()));
                    mTagDetailsViewModel.joinTag(params);
                }
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public void passwordVerifyBottomSheetDialog() {
        mDialog = new BottomSheetDialog(mActivity);
        final BottomSheetDialogJoinByPasswordBinding binding = BottomSheetDialogJoinByPasswordBinding.inflate(LayoutInflater.from(mActivity));
        mDialog.setContentView(binding.getRoot());
        mDialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(mActivity.getResources().getColor(android.R.color.transparent));
        binding.tvMessage.setText(mActivity.getResources().getString(R.string.apply_password).replace(getString(R.string.tag_name_pattern), mData.getTagName()));
        binding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etVerificationPassword.getText().toString().length() == 0) {
                    showToastShort(ResourceUtils.getInstance().getString(R.string.please_enter_password));
                } else if (binding.etVerificationPassword.getText().toString().length() < 6) {
                    showToastShort(ResourceUtils.getInstance().getString(R.string.minimum_6_character));
                } else {
                    getLoadingStateObserver().onChanged(true);
                    HashMap<String, Object> params = new HashMap<>();
                    params.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, mData.getTagId());
                    params.put(AppConstants.TAG_KEY_CONSTENT.JOIN_TAG_BY, AppConstants.TAG_VERIFICATION_METHOD.PASSWORD);
                    params.put(AppConstants.TAG_KEY_CONSTENT.REQUEST_PARAMETER, binding.etVerificationPassword.getText().toString());
                    mTagDetailsViewModel.joinTag(params);
                }
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(mActivity, ImagesGallery.class);
                    intent.putExtra("selectedList", (Serializable) mFileArrayList);
                    intent.putExtra("title", "Select Image");
                    intent.putExtra("maxSelection", 5); // Optional
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.MULTIPLE_IMAGE_INTENT);
                }
                break;
        }
    }

    @SuppressLint("WrongConstant")
    public void documentVerifyBottomSheetDialog() {
        mDialog = new BottomSheetDialog(mActivity);
        final BottomSheetDialogJoinByDocumentBinding binding = BottomSheetDialogJoinByDocumentBinding.inflate(LayoutInflater.from(mActivity));
        mDialog.setContentView(binding.getRoot());
        mDialog.setCancelable(false);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) binding.main.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) binding.main.getParent()).setBackgroundColor(mActivity.getResources().getColor(android.R.color.transparent));
        binding.rvAddDocumentImages.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayout.HORIZONTAL, false));
        binding.tvMessage.setText(mActivity.getResources().getString(R.string.apply_documents).replace(getString(R.string.tag_name_pattern), mData.getTagName()));
        binding.tvTitleDocument.setText(String.format("%s %s", mActivity.getResources().getString(R.string.uplaod_document), mData.getDocument_type()));

        addDocumentAdapter = new AddDocumentAdapter(mActivity, mFileArrayList, new RecyclerListener() {
            @Override
            public void onItemClick(View v, int position, String number, boolean flag) {
                mFileArrayList.remove(position);
                addDocumentAdapter.notifyDataSetChanged();
            }
        });
        binding.rvAddDocumentImages.setAdapter(addDocumentAdapter);
        binding.ivAddDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PermissionUtility.isPermissionGranted(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, AppConstants.ACTIVITY_RESULT.CAMERA_PERMISSION)) {
                    Intent intent = new Intent(mActivity, ImagesGallery.class);
                    intent.putExtra("selectedList", (Serializable) mFileArrayList);
                    intent.putExtra("title", "Select Image");
                    intent.putExtra("maxSelection", 5); // Optional
                    startActivityForResult(intent, AppConstants.REQUEST_CODE.MULTIPLE_IMAGE_INTENT);
                }

            }
        });

        binding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFileArrayList == null || mFileArrayList.size() == 0) {
                    showToastShort(ResourceUtils.getInstance().getString(R.string.please_upload_document_image));
                } else {
                    getLoadingStateObserver().onChanged(true);
                    imageUploadCount = 0;
                    for (int i = 0; i < mFileArrayList.size(); i++)
                        startUpload(mFileArrayList.get(i));
                }
            }
        });
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE.MULTIPLE_IMAGE_INTENT && data != null) {
            if (data.getStringArrayListExtra("result") != null) {
                mFileArrayList.clear();
                imageUploadCount = 0;
                ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                mFileArrayList.addAll(selectionResult);
                addDocumentAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == AppConstants.ACTIVITY_RESULT.EDIT_TAG) {
            if (resultCode == Activity.RESULT_OK) {
                mTagDetailsViewModel.hitTagDetails(tagId, AppConstants.REQUEST_CODE.TAG_DEtAILS, 6);
            }
        }
        if (requestCode == AppConstants.ACTIVITY_RESULT.TAG_DETAILS) {
            if (resultCode == Activity.RESULT_OK) {
                mActivity.finish();
            }
        }
    }

    // Initilize the Amazon S3
    public void setUpAmazonS3() {
        mAmazonS3 = mAmazonS3.getInstance(mActivity, this, AppConstants.AMAZON_S3.AMAZON_POOLID, AppConstants.AMAZON_S3.BUCKET, AppConstants.AMAZON_S3.AMAZON_SERVER_URL, AppConstants.AMAZON_S3.END_POINT);
    }

    private ImageBean addDataInBean(String path) {
        ImageBean bean = new ImageBean();
        bean.setId("1");
        bean.setName("sample");
        bean.setImagePath(path);
        return bean;
    }

    // Start Uploading image to Amazon Server
    private void startUpload(String path) {
        ImageBean bean = addDataInBean(path);
        mAmazonS3.uploadImage(bean);
    }


    @Override
    public void uploadSuccess(ImageBean bean) {
        if (imageUploadCount == 0) {
            mImageList.clear();
        }
        imageUploadCount++;
        ImageList imageList = new ImageList();
        imageList.setUrl(bean.getServerUrl());
        imageList.setThumbUrl(bean.getServerUrl());
        mImageList.add(imageList);
        if (imageUploadCount == this.mFileArrayList.size()) {
            String images = "";
            for (ImageList image : mImageList) {
                if (images.equalsIgnoreCase(""))
                    images = image.getUrl();
                else
                    images = images + "," + image.getUrl();
            }
            HashMap<String, Object> params = new HashMap<>();
            params.put(AppConstants.TAG_KEY_CONSTENT.COMMUNITY_ID, mData.getTagId());
            params.put(AppConstants.TAG_KEY_CONSTENT.JOIN_TAG_BY, AppConstants.TAG_VERIFICATION_METHOD.DOCUMENT);
            params.put(AppConstants.TAG_KEY_CONSTENT.DOCUMENT_URL, images);
            mTagDetailsViewModel.joinTag(params);
        }

    }

    @Override
    public void uploadFailed(ImageBean bean) {
        imageUploadCount++;
        if (imageUploadCount == this.mFileArrayList.size()) {
            getLoadingStateObserver().onChanged(false);
        }
    }

    @Override
    public void uploadProgress(ImageBean bean) {

    }

    @Override
    public void uploadError(Exception e, ImageBean imageBean) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(localReceiver);
    }

    BroadcastReceiver localReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(AppConstants.KEY_CONSTENT.TAG_ID)) {
                String tagID = intent.getExtras().getString(AppConstants.KEY_CONSTENT.TAG_ID);
                if (tagID.equalsIgnoreCase(tagId) && AppUtils.isConnection(mActivity))
                    mTagDetailsViewModel.hitTagDetails(tagId, AppConstants.REQUEST_CODE.TAG_DEtAILS, 6);
            }
        }
    };


    @Override
    protected void onFailure(FailureResponse failureResponse) {
        super.onFailure(failureResponse);

    }

}
