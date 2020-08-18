package com.taghawk.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taghawk.databinding.LayoutTagSearchRowBinding;
import com.taghawk.model.tag.TagData;
import com.taghawk.ui.home.search.SearchAcivity;
import com.taghawk.ui.home.search.SearchFragment;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class TagSearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<TagData> mList;
    private SearchFragment.ISearchHost iSearchHost;

    public TagSearchListAdapter(ArrayList<TagData> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutTagSearchRowBinding mBinding = LayoutTagSearchRowBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new SearchListViewModel(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        SearchListViewModel holder = null;
        holder = (SearchListViewModel) viewHolder;
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private class SearchListViewModel extends RecyclerView.ViewHolder {
        LayoutTagSearchRowBinding viewBinding;

        public SearchListViewModel(LayoutTagSearchRowBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailsIntent = new Intent();
                    detailsIntent.putExtra("TAG", mList.get(getAdapterPosition()));
                    ((SearchAcivity) v.getContext()).setResult(Activity.RESULT_OK,detailsIntent);
                    ((SearchAcivity) v.getContext()).finish();
                }
            });
        }

        public void bind(TagData bean) {

            viewBinding.setSearchData(bean);
        }
    }
}
