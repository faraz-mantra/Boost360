package com.marketplace.recyclerView

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener(var layoutManager: LinearLayoutManager) :
  RecyclerView.OnScrollListener() {

  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    super.onScrolled(recyclerView, dx, dy)
    val visibleItemCount: Int = layoutManager.childCount
    val totalItemCount: Int = layoutManager.itemCount
    val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
    if (!isLoading && !isLastPage) {
      // && totalItemCount >= totalPageCount
      if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) loadMoreItems()
    }
  }

  protected abstract fun loadMoreItems()
  abstract val totalPageCount: Int
  abstract val isLastPage: Boolean
  abstract val isLoading: Boolean

  companion object {
    const val PAGE_START = 0
    const val PAGE_SIZE = 10
  }
}
