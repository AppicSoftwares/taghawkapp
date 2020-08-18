package com.taghawk.adapters;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taghawk.R;
import com.taghawk.countrypicker.CountryCodeSelectionActivity;
import com.taghawk.model.SelectionListBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Generic Adapter for searching data like country,state,city,Phone code etc
 **/
public class CountrySelectionAdapter extends RecyclerView.Adapter<CountrySelectionAdapter.ViewHolder> {
    private Activity mActivity;
    private List<SelectionListBean> list;
    private List<SelectionListBean> filterableList;
    private ItemFilter mFilter = new ItemFilter();

    public CountrySelectionAdapter(List<SelectionListBean> list, Activity mActivity) {
        this.mActivity = mActivity;
        this.list = list;
        this.filterableList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_selection_country, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvCountry.setText("" + filterableList.get(position).getName());
        holder.tvCode.setText("(" + filterableList.get(position).getId() + ")");

        holder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((CountryCodeSelectionActivity)mActivity).sendDataBack(filterableList.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return filterableList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_country)
        TextView tvCountry;
        @BindView(R.id.tv_code)
        TextView tvCode;
        @BindView(R.id.rl_main)
        RelativeLayout rlMain;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<SelectionListBean> listData = list;

            int count = listData.size();
            final ArrayList<SelectionListBean> nlist = new ArrayList<SelectionListBean>();

            for (int i = 0; i < count; i++) {
                if (listData.get(i).getName().toLowerCase().contains(filterString)) {
                    nlist.add(listData.get(i));
                }
            }
            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterableList = (List<SelectionListBean>) results.values;
            notifyDataSetChanged();
        }
    }


    public Filter getFilter() {
        return mFilter;
    }

}

