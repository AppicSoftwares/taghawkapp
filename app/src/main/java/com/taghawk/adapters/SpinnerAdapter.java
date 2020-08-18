package com.taghawk.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.taghawk.R;

import java.util.ArrayList;

/**
 * Created by pardeep on 12/12/16.
 */
public class SpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SpinnerItem> nameList;

    public SpinnerAdapter(Context context, ArrayList<SpinnerItem> nameList) {
        mContext = context;
        this.nameList = nameList;
    }

    public int getCount() {
        return nameList.size();
    }

    public SpinnerItem getItem(int i) {
        return nameList.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }


    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.row_spinner, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_spinner);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();
        viewHolder.tvName.setText(nameList.get(position).getValueText());
        if (nameList.get(position).isSelected())
            viewHolder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        else
            viewHolder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.txt_black));
        return view;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewgroup) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        int position=((Spinner)viewgroup).getSelectedItemPosition();
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.row_spinner, viewgroup, false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_spinner);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();
        viewHolder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.txt_black));
        viewHolder.tvName.setText(nameList.get(position).getValueText());
        return view;
    }

    private class ViewHolder {
        private TextView tvName;

    }
}