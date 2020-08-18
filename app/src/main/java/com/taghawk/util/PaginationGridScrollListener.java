package com.taghawk.util;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaginationGridScrollListener extends RecyclerView.OnScrollListener {

    private GridLayoutManager mGridLayoutManager;
    private boolean isLoading;
    private boolean isLastPage;
    private long thresholdItemCount = 0;
    private PaginationGridScrollListener.PaginationListenerCallbacks paginationListenerCallbacks;

    public PaginationGridScrollListener(GridLayoutManager layoutManager, PaginationGridScrollListener.PaginationListenerCallbacks callbacks) {
        mGridLayoutManager = layoutManager;
        paginationListenerCallbacks = callbacks;
    }

    public PaginationGridScrollListener(GridLayoutManager layoutManager, PaginationGridScrollListener.PaginationListenerCallbacks callbacks, long thresholdItemCount) {
        mGridLayoutManager = layoutManager;
        paginationListenerCallbacks = callbacks;
        this.thresholdItemCount = thresholdItemCount;

    }

    public void setLastPageStatus(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public void setFetchingStatus(boolean isLoadingDone) {
        this.isLoading = isLoadingDone;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = mGridLayoutManager.getChildCount();
        int totalItemCount = mGridLayoutManager.getItemCount();
        int firstVisibleItemPosition = mGridLayoutManager.findFirstVisibleItemPosition();

        if (dy >= 0 && !getFetchingStatus() && (firstVisibleItemPosition + visibleItemCount) >= totalItemCount && firstVisibleItemPosition >= 0
                && totalItemCount >= thresholdItemCount) {
            setFetchingStatus(true);
            paginationListenerCallbacks.loadMoreItems();
        }
    }

    public boolean getFetchingStatus() {
        return isLoading;
    }

    public interface PaginationListenerCallbacks {
        void loadMoreItems();
    }
}
