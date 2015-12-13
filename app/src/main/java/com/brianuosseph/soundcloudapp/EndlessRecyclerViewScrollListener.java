package com.brianuosseph.soundcloudapp;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    // Minimum amount of items to have below current scroll position before loading
    private int visibleThreshold = 10;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // Total number of items in the data set after the last load
    private int previousTotalItemCount = 0;
    // True if still waiting for the last set of data to load
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLinearLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int visibleItemCount = view.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();

        // If total item count is zero and the previous isn't, assume the list is
        // invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        // If still loading, check if data set count has changed, if so it has
        // finished loading and update the current page number and total item count
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If not loading, check if past visibleThreshold and need to load more data.
        if (!loading &&
                (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount);
            loading = true;
        }
    }

    public abstract void onLoadMore(int page, int totalItemsCount);
}
