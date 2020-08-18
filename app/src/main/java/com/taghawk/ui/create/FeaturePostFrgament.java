package com.taghawk.ui.create;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.taghawk.R;
import com.taghawk.adapters.SharedTagAdapter;
import com.taghawk.base.BaseFragment;
import com.taghawk.camera2basic.CameraTwoActivity;
import com.taghawk.constants.AppConstants;
import com.taghawk.databinding.LayoutPostedProductMarkFeaturedBinding;
import com.taghawk.model.AddProduct.AddProductModel;
import com.taghawk.model.commonresponse.CommonResponse;
import com.taghawk.model.tag.TagData;
import com.taghawk.stripe.GooglePayPayment;
import com.taghawk.stripe.Token;
import com.taghawk.ui.home.HomeActivity;
import com.taghawk.util.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class FeaturePostFrgament extends BaseFragment implements View.OnClickListener {

    private LayoutPostedProductMarkFeaturedBinding mBinding;
    private AddProductModel addProductData;
    private Activity mActivity;
    private ArrayList<TagData> mSharedTagsList;
    private SharedTagAdapter adapter;
    private HashMap<Integer, TagData> sharedTagsMap;
    private AddProductViewModel mAddProductViewModel;
    private int days;
    private double price;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.layout_posted_product_mark_featured, null, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mActivity = getActivity();
        mSharedTagsList = new ArrayList<>();
        sharedTagsMap = new HashMap<>();
//        setupSharedTag();
        getArgumentData();
        mBinding.includeHeader.ivReport.setVisibility(View.GONE);
        mBinding.includeHeader.ivShare.setVisibility(View.GONE);
        mBinding.includeHeader.tvSubDescription.setVisibility(View.VISIBLE);
        mBinding.includeHeader.tvTitle.setVisibility(View.VISIBLE);
        mBinding.includeHeader.tvDone.setVisibility(View.VISIBLE);
        mBinding.includeHeader.tvTitle.setText(getString(R.string.congratulations_title));
        mBinding.includeHeader.tvDone.setOnClickListener(this);
        mBinding.tvShareItem.setOnClickListener(this);
        mBinding.tvPostAnother.setOnClickListener(this);
        mBinding.includeHeader.ivBack.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
        mBinding.includeHeader.ivBack.setVisibility(View.GONE);
        mBinding.includeHeader.ivBack.setOnClickListener(this);
//        enableDisableNextButton(R.color.transparantwhite, false);
        mBinding.tvPlanOne.setOnClickListener(this);
        mBinding.tvPlanTwo.setOnClickListener(this);
        mBinding.tvPlanThree.setOnClickListener(this);
        mBinding.checkTags.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBinding.rvSharedTag.setVisibility(View.VISIBLE);
                } else {
                    mBinding.rvSharedTag.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddProductViewModel = ViewModelProviders.of(this).get(AddProductViewModel.class);
        mAddProductViewModel.setGenericListeners(getErrorObserver(), getFailureResponseObserver(), getLoadingStateObserver());
        mAddProductViewModel.getFeaturedLiveData().observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(@Nullable CommonResponse commonResponse) {
//                showToastShort(commonResponse.getMessage());
                openHomeActivity();
            }
        });
    }

    private void getArgumentData() {
        if (getArguments() != null) {
            addProductData = getArguments().getParcelable("DATA");
            ArrayList<TagData> mSharedTagsList = (ArrayList<TagData>) getArguments().get("SHARED_TAG_DATA");
            if (mSharedTagsList != null) {
                this.mSharedTagsList.addAll(mSharedTagsList);
                setupSharedTag();
            }
            setData();
        }
    }

    private void setData() {
        if (addProductData != null) {
            mBinding.tvPrice.setText("$" + addProductData.getmAddProductData().getFirmPrice());
            if (addProductData.getmAddProductData().getImages() != null && addProductData.getmAddProductData().getImages().size() > 0) {
                Glide.with(mActivity).asBitmap().load(addProductData.getmAddProductData().getImages().get(0).getUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(mBinding.ivProduct);
            }
            if (mSharedTagsList != null && mSharedTagsList.size() > 0) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void setupSharedTag() {
        final GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 2);
        mBinding.rvSharedTag.setLayoutManager(layoutManager);
        adapter = new SharedTagAdapter(mActivity, mSharedTagsList, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView != null) {
                    final int position = Integer.valueOf(buttonView.getTag().toString());
                    if (isChecked) {
                        mSharedTagsList.get(position).setSelected(true);
                        sharedTagsMap.put(position, mSharedTagsList.get(position));
                    } else {
                        mSharedTagsList.get(position).setSelected(false);
                        if (sharedTagsMap.size() > 0)
                            sharedTagsMap.remove(position);
                    }
                    mBinding.rvSharedTag.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(position);
                        }
                    });
                }
            }
        });
        mBinding.rvSharedTag.setAdapter(adapter);
        if (mSharedTagsList != null && mSharedTagsList.size() > 0) {
            mBinding.llTags.setVisibility(View.VISIBLE);
        } else {
            mBinding.llTags.setVisibility(View.GONE);
            mBinding.rvSharedTag.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_post_another:
                openCreateProductActivity();
                break;
            case R.id.tv_share_item:
                AppUtils.share(mActivity, addProductData.getmAddProductData().getShareLink(), "TagHawk Share Product", getString(R.string.share));
                break;
            case R.id.tv_done:
                openHomeActivity();
                break;
            case R.id.iv_back:
                openHomeActivity();
                break;
            case R.id.tv_plan_one:
                days = 1;
                price = 1.99;
                setPackedge(R.drawable.edit_field_filled_drawable, R.drawable.rounded_corner_color_primary, R.drawable.rounded_corner_color_primary, R.color.White, R.color.colorPrimary, R.color.colorPrimary);
                if (mBinding.checkTags.isChecked() && sharedTagsMap != null && sharedTagsMap.size() > 0) {
                    price = price + sharedTagsMap.size();
                }
                doPayment(price);
//                enableDisableNextButton(R.color.White, true);
                break;
            case R.id.tv_plan_two:
                days = 3;
                price = 3.99;
                setPackedge(R.drawable.rounded_corner_color_primary, R.drawable.edit_field_filled_drawable, R.drawable.rounded_corner_color_primary, R.color.colorPrimary, R.color.White, R.color.colorPrimary);
                if (mBinding.checkTags.isChecked() && sharedTagsMap != null && sharedTagsMap.size() > 0) {
                    price = price + sharedTagsMap.size();
                }
                doPayment(price);
//                enableDisableNextButton(R.color.White, true);
                break;
            case R.id.tv_plan_three:
                days = 7;
                if (mBinding.checkTags.isChecked() && sharedTagsMap != null && sharedTagsMap.size() > 0) {
                    price = price + sharedTagsMap.size();
                }
                price = 6.99;
                setPackedge(R.drawable.rounded_corner_color_primary, R.drawable.rounded_corner_color_primary, R.drawable.edit_field_filled_drawable, R.color.colorPrimary, R.color.colorPrimary, R.color.White);
                doPayment(price);
//                enableDisableNextButton(R.color.White, true);
                break;
        }
    }

    private void enableDisableNextButton(int p, boolean b) {
        mBinding.includeHeader.tvDone.setTextColor(getResources().getColor(p));
        mBinding.includeHeader.tvDone.setEnabled(b);
        mBinding.includeHeader.tvDone.setVisibility(View.VISIBLE);
    }

    private void setPackedge(int p, int p2, int p3, int color1, int color2, int color3) {
        mBinding.tvPlanOne.setBackgroundDrawable(getResources().getDrawable(p));
        mBinding.tvPlanTwo.setBackgroundDrawable(getResources().getDrawable(p2));
        mBinding.tvPlanThree.setBackgroundDrawable(getResources().getDrawable(p3));
        mBinding.tvPlanOne.setTextColor(getResources().getColor(color1));
        mBinding.tvPlanTwo.setTextColor(getResources().getColor(color2));
        mBinding.tvPlanThree.setTextColor(getResources().getColor(color3));

    }

    private void doPayment(double price) {
        Intent intent = new Intent(mActivity, GooglePayPayment.class);
        intent.putExtra(AppConstants.KEY_CONSTENT.PRICE, String.valueOf(price));
        startActivityForResult(intent, AppConstants.ACTIVITY_RESULT.G_PAY_STRIPE);
    }

    private void openCreateProductActivity() {
        Intent intent = new Intent(mActivity, CameraTwoActivity.class);
        intent.putExtra("ISFIRST", true);
        startActivity(intent);
        ((FeturedPostActivity) mActivity).finish();
    }

    private void openHomeActivity() {
        Intent intent = new Intent(mActivity, HomeActivity.class);
        intent.putExtra("IS_FROM_FEATURE_POST", true);
        startActivity(intent);
        ((FeturedPostActivity) mActivity).finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ACTIVITY_RESULT.G_PAY_STRIPE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        String rawToken = data.getExtras().getString(AppConstants.BUNDLE_DATA);
                        Token token = new Gson().fromJson(rawToken, Token.class);
                        if (token.getId() != null) {
                            chargeToken(token.getId());
                        }
                    }

                }
                break;
        }
    }

    private void chargeToken(String id) {
        if (AppUtils.isInternetAvailable(mActivity)) {
            mAddProductViewModel.markProductFeatured(addProductData.getmAddProductData().getId(), id, days, price, sharedTagsMap, mBinding.checkTags.isChecked());
        } else showNoNetworkError();
    }

}
