package com.taghawk.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taghawk.databinding.LayoutSearchRowBinding;
import com.taghawk.model.SearchSuggestionData;
import com.taghawk.ui.home.search.SearchFragment;
import com.taghawk.ui.home.search.SearchTagShelfFragment;

import java.util.ArrayList;

/**
 * Created by Appinventiv on 23-01-2019.
 */


public class ShelfSearchListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<SearchSuggestionData> mList;
    private SearchTagShelfFragment.ISearchHost iSearchHost;

    public ShelfSearchListAdapter(ArrayList<SearchSuggestionData> mList, SearchTagShelfFragment.ISearchHost iSearchHost) {
        this.mList = mList;
        this.iSearchHost = iSearchHost;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutSearchRowBinding mBinding = LayoutSearchRowBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
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
        LayoutSearchRowBinding viewBinding;

        public SearchListViewModel(LayoutSearchRowBinding viewBinding) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            viewBinding.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iSearchHost.openSearchResultData(mList.get(getAdapterPosition()).getTitle());
                }
            });
        }

        public void bind(SearchSuggestionData bean) {

            viewBinding.setSearchData(bean);
        }
    }
}
