package com.inventoryorder.ui.booking

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.FragmentInventoryAllBookingsBinding
import com.inventoryorder.model.bookingdetails.BookingsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.PaginationScrollListener
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity

class BookingsFragment : BaseInventoryFragment<FragmentInventoryAllBookingsBinding>(), RecyclerItemClickListener {

  private var layoutManager: LinearLayoutManager? = null

  companion object {
    fun newInstance(bundle: Bundle? = null): BookingsFragment {
      val fragment = BookingsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    layoutManager = LinearLayoutManager(baseActivity)
    layoutManager?.let { scrollPagingListener(it) }
    setRecyclerViewAdapter()
  }

  private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
    binding?.bookingRecycler?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0 && binding?.btnAdd?.visibility === View.VISIBLE) binding?.btnAdd?.hide()
        else if (dy < 0 && binding?.btnAdd?.visibility !== View.VISIBLE) binding?.btnAdd?.show()
      }

      override fun loadMoreItems() {

      }

      override val totalPageCount: Int
        get() = 0
      override val isLastPage: Boolean
        get() = true
      override val isLoading: Boolean
        get() = true
    })
  }

  private fun setRecyclerViewAdapter() {
    binding?.bookingRecycler?.post {
      val adapter = AppBaseRecyclerViewAdapter(baseActivity, BookingsModel().getList(), this)
      binding?.bookingRecycler?.layoutManager = layoutManager
      binding?.bookingRecycler?.adapter = adapter
      binding?.bookingRecycler?.let { adapter.runLayoutAnimation(it) }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val searchItem = menu.findItem(R.id.menu_item_search)
    if (searchItem != null) {
      val searchView = searchItem.actionView as SearchView
      searchView.queryHint = resources.getString(R.string.queryHintBooking)
      searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
          return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
          newText?.let { startFilter(it.trim().toLowerCase()) }
          return false
        }
      })
    }
  }

  private fun startFilter(query: String) {

  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ALL_BOOKING_ITEM_CLICKED.ordinal -> {
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.BOOKING_ITEM.name, (item as BookingsModel))
        startFragmentActivity(FragmentType.BOOKING_DETAIL_VIEW, bundle)
      }
    }
  }
}