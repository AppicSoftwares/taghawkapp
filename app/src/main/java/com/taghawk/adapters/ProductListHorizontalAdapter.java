package com.taghawk.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.data.DataManager;
import com.taghawk.databinding.AdapterProductListingHorizontalBinding;
import com.taghawk.model.home.ProductListModel;
import com.taghawk.ui.home.HomeViewModel;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */
public class ProductListHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String type;
    int isLike = 0;
    private Context context;
    private ArrayList<ProductListModel> mList;
    private ArrayList<ProductListModel> mList2;

    public ProductListHorizontalAdapter(ArrayList<ProductListModel> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        AdapterProductListingHorizontalBinding mBinding = AdapterProductListingHorizontalBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ProductListViewModel(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ProductListViewModel holder = null;
        holder = (ProductListViewModel) viewHolder;
        holder.bind(mList.get(position));
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void hitLikeUnLike(String productId, int isLike, int requestCode) {
        HomeViewModel homeViewModel = new HomeViewModel();
        homeViewModel.getLikeUnLike(productId, isLike, requestCode);
    }

    private class ProductListViewModel extends RecyclerView.ViewHolder {
        AdapterProductListingHorizontalBinding viewBinding;

        public ProductListViewModel(AdapterProductListingHorizontalBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.ivProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
                        intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, mList.get(getAdapterPosition()).get_id());
                        v.getContext().startActivity(intent);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });

            viewBinding.tvname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
                        intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, mList.get(getAdapterPosition()).get_id());
                        v.getContext().startActivity(intent);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });
           /* viewBinding.llMain1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
                        intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, mList.get(getAdapterPosition()).get_id());
                        v.getContext().startActivity(intent);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });*/
        }

        @SuppressLint("SetTextI18n")
        public void bind(final ProductListModel bean) {
            DecimalFormat precision = new DecimalFormat("0.00");
            //viewBinding.tvname.setText("$" + precision.format(Double.parseDouble(bean.getFirmPrice())));
            viewBinding.tvname.setText("$" + bean.getFirmPrice());
            if (bean.isNegotiable()) {
                viewBinding.tvtype.setVisibility(View.VISIBLE);
            } else {
                viewBinding.tvtype.setVisibility(View.GONE);
            }


            viewBinding.tvlocation1.setText(bean.getCity() + ", " + bean.getState());


            if (bean.isNegotiable()) {
                viewBinding.tvtype.setText("Firm");
            } else {
                viewBinding.tvtype.setText("");
            }
            viewBinding.tvdis.setText(bean.getProductTitle());
            if (bean.getLiked()) {
                viewBinding.ivLike.setImageResource(R.drawable.ic_like_fill);
            } else {
                viewBinding.ivLike.setImageResource(R.drawable.ic_like_unfill);
            }
            viewBinding.ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* Log.d("vishal1234", "______________" + bean.getLiked());
                    if (!(DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                        if (bean != null) {
                            if (bean.getLiked()) {
                                isLike = 0;
                                viewBinding.ivLike.setImageResource(R.drawable.ic_like_unfill);
                                bean.setLiked(false);
                            } else {
                                isLike = 1;
                                viewBinding.ivLike.setImageResource(R.drawable.ic_like_fill);
                                bean.setLiked(true);
                            }
                            hitLikeUnLike(bean.get_id(), isLike, AppConstants.REQUEST_CODE.LIKE_PRODUCT);
                        }
                    } else {
//                        DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(context,context);
                    }*/

                    if (!(DataManager.getInstance().getUserDetails() != null && DataManager.getInstance().getUserDetails().getUserType() == AppConstants.GUEST_USER)) {
                        int status;
                        if (bean.getLiked()) {
                            status = 0;
                            bean.setLiked(false);
                            viewBinding.ivLike.setImageResource(R.drawable.ic_like_unfill);
                        } else {
                            status = 1;
                            bean.setLiked(true);
                            viewBinding.ivLike.setImageResource(R.drawable.ic_like_fill);

                        }
                        hitLikeUnLike(bean.get_id(), status, AppConstants.REQUEST_CODE.LIKE_SIMILAR_PRODUCT);
                    } else {
//                        DialogUtil.getInstance().CustomGuestUserBottomSheetDialog(mActivity, activity);
                    }

                }
            });
            if (bean.getImageLists() != null && bean.getImageLists().size() > 0) {

                try {

                    Glide.with(viewBinding.getRoot().getContext()).asBitmap().load(bean.getImageLists().get(0).getUrl()).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(viewBinding.ivProduct);

                } catch (Exception e) {
                    viewBinding.ivProduct.setImageResource(R.drawable.ic_home_placeholder);
                }
            } else {
                viewBinding.ivProduct.setImageResource(R.drawable.ic_home_placeholder);
            }
            if (mList.get(getAdapterPosition()).getPromoted()) {
                viewBinding.ivPromote.setVisibility(View.VISIBLE);
            } else
                viewBinding.ivPromote.setVisibility(View.GONE);

//                viewBinding.setProductViewModel(bean.getImageLists().get(0));
        }
    }
}
