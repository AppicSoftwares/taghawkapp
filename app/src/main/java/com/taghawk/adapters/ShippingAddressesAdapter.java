package com.taghawk.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.taghawk.R;
import com.taghawk.model.AddressDataItem;
import com.taghawk.model.BillingAddressDataItem;
import com.taghawk.model.DeleteAddressRequest;
import com.taghawk.ui.profile.AddUpdateAddressActivity;
import com.taghawk.ui.profile.ProfileEditViewModel;
import com.taghawk.ui.shipping.FragmentAddUpdateAddress;
import com.taghawk.ui.shipping.ShippingActivity;
import com.taghawk.ui.shipping.ShippingFrgamentStepOne;
import com.taghawk.ui.shipping.ShippingViewModel;

import java.util.ArrayList;

public class ShippingAddressesAdapter extends RecyclerView.Adapter<ShippingAddressesAdapter.MyViewHolder> {

    private Activity context;
    private LayoutInflater inflater;
    private ArrayList<BillingAddressDataItem> addressesList = new ArrayList<>();
    private ViewModel viewModel;
    private String type = "";
    LinearLayout linearLayoutAddressForm, linearLayoutAddressList;


    public ShippingAddressesAdapter(Activity context, ArrayList<BillingAddressDataItem> addressesList, ViewModel viewModel, String type) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.addressesList = addressesList;
        this.viewModel = viewModel;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_shipping_address_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvFullName.setText("" + addressesList.get(position).getContact_name());
        if(addressesList.get(position).getStreet2() != null && !TextUtils.isEmpty(addressesList.get(position).getStreet2()))
            holder.tvAddress.setText("" + addressesList.get(position).getStreet1() + ", #" + addressesList.get(position).getStreet2() + ", " + addressesList.get(position).getCity() + ", " + addressesList.get(position).getState() + "\n" + "Zip code: " + addressesList.get(position).getPostal_code());
        else
            holder.tvAddress.setText("" + addressesList.get(position).getStreet1() + ", " + addressesList.get(position).getCity() + ", " + addressesList.get(position).getState() + "\n" + "Zip code: " + addressesList.get(position).getPostal_code());

        holder.tvMobile.setText("Mobile: " + addressesList.get(position).getPhone());
        if(type.equalsIgnoreCase("shipping")) {
            linearLayoutAddressForm = ((ShippingActivity) context).findViewById(R.id.linear_layout_address_form);
            linearLayoutAddressList = ((ShippingActivity) context).findViewById(R.id.linear_layout_address_list);
        } else {
            linearLayoutAddressForm = ((AddUpdateAddressActivity) context).findViewById(R.id.linear_layout_address_form);
            linearLayoutAddressList = ((AddUpdateAddressActivity) context).findViewById(R.id.linear_layout_address_list);
        }
        if(addressesList.get(position).getSelectedStatus() == 1) {
            holder.llMain.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.round_border_color_primary_2dp));
        } else {
            holder.llMain.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_border_color_white));
        }
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutAddressList.setVisibility(View.GONE);
                linearLayoutAddressForm.setVisibility(View.VISIBLE);
                if(type.equalsIgnoreCase("shipping")) {
                    ShippingFrgamentStepOne.updateAddress(addressesList.get(holder.getAdapterPosition()));
                } else {
                    FragmentAddUpdateAddress.updateAddress(addressesList.get(holder.getAdapterPosition()));
                }
            }
        });
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteAddressRequest deleteAddressRequest = new DeleteAddressRequest();
                deleteAddressRequest.setShipId(addressesList.get(holder.getAdapterPosition()).get_id());
                if(type.equalsIgnoreCase("shipping")) {
                    ((ShippingViewModel)viewModel).deleteShippingAddress(deleteAddressRequest);
                } else {
                    ((ProfileEditViewModel)viewModel).deleteShippingAddress(deleteAddressRequest);
                }

            }
        });
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equalsIgnoreCase("shipping")) {
                    ((ShippingViewModel)viewModel).moveToStepTwo(context, addressesList.get(holder.getAdapterPosition()).getContact_name(),
                            addressesList.get(holder.getAdapterPosition()).getStreet1(),
                            addressesList.get(holder.getAdapterPosition()).getStreet2(),
                            addressesList.get(holder.getAdapterPosition()).getPostal_code(),
                            addressesList.get(holder.getAdapterPosition()).getCity(),
                            addressesList.get(holder.getAdapterPosition()).getState(),
                            addressesList.get(holder.getAdapterPosition()).getPhone(),
                            "",
                            addressesList.get(holder.getAdapterPosition()).getType(),
                            0,
                            addressesList.get(holder.getAdapterPosition()).getSelectedStatus(),
                            addressesList.get(holder.getAdapterPosition()).getCountry());
                } else {
                    ((ProfileEditViewModel)viewModel).updateAddress(context, addressesList.get(holder.getAdapterPosition()).getContact_name(),
                            addressesList.get(holder.getAdapterPosition()).getStreet1(),
                            addressesList.get(holder.getAdapterPosition()).getStreet2(),
                            addressesList.get(holder.getAdapterPosition()).getPostal_code(),
                            addressesList.get(holder.getAdapterPosition()).getCity(),
                            addressesList.get(holder.getAdapterPosition()).getState(),
                            addressesList.get(holder.getAdapterPosition()).getPhone(),
                            "",
                            addressesList.get(holder.getAdapterPosition()).getType(),
                            addressesList.get(holder.getAdapterPosition()).get_id(),
                            1);
                }
            }
        });
        holder.tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equalsIgnoreCase("shipping")) {
                    ((ShippingViewModel)viewModel).moveToStepTwo(context, addressesList.get(holder.getAdapterPosition()).getContact_name(),
                            addressesList.get(holder.getAdapterPosition()).getStreet1(),
                            (!TextUtils.isEmpty(addressesList.get(holder.getAdapterPosition()).getStreet2()))?addressesList.get(holder.getAdapterPosition()).getStreet2():"",
                            addressesList.get(holder.getAdapterPosition()).getPostal_code(),
                            addressesList.get(holder.getAdapterPosition()).getCity(),
                            addressesList.get(holder.getAdapterPosition()).getState(),
                            addressesList.get(holder.getAdapterPosition()).getPhone(),
                            "",
                            addressesList.get(holder.getAdapterPosition()).getType(),
                            0,
                            addressesList.get(holder.getAdapterPosition()).getSelectedStatus(),
                            addressesList.get(holder.getAdapterPosition()).getCountry());
                } else {
                    if(addressesList.get(holder.getAdapterPosition()).getSelectedStatus() == 0) {
                        ((ProfileEditViewModel)viewModel).updateAddress(context, addressesList.get(holder.getAdapterPosition()).getContact_name(),
                                addressesList.get(holder.getAdapterPosition()).getStreet1(),
                                (!TextUtils.isEmpty(addressesList.get(holder.getAdapterPosition()).getStreet2()))?addressesList.get(holder.getAdapterPosition()).getStreet2():"",
                                addressesList.get(holder.getAdapterPosition()).getPostal_code(),
                                addressesList.get(holder.getAdapterPosition()).getCity(),
                                addressesList.get(holder.getAdapterPosition()).getState(),
                                addressesList.get(holder.getAdapterPosition()).getPhone(),
                                "",
                                addressesList.get(holder.getAdapterPosition()).getType(),
                                addressesList.get(holder.getAdapterPosition()).get_id(),
                                1);
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvFullName;
        private AppCompatTextView tvAddress;
        private AppCompatTextView tvMobile;
        private AppCompatTextView tvSelect;
        private ImageView ivEdit;
        private ImageView ivRemove;
        private LinearLayout llMain;
        private CardView cvMain;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvMobile = itemView.findViewById(R.id.tv_mobile);
            tvSelect = itemView.findViewById(R.id.tv_select);
            ivEdit = itemView.findViewById(R.id.iv_edit);
            ivRemove = itemView.findViewById(R.id.iv_remove);
            llMain = itemView.findViewById(R.id.ll_main);
            cvMain = itemView.findViewById(R.id.cv_main);

        }
    }
}
