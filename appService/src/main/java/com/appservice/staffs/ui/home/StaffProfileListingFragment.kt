package com.appservice.staffs.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentStaffListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.recyclerView.PaginationScrollListener.Companion.PAGE_SIZE
import com.appservice.recyclerView.PaginationScrollListener.Companion.PAGE_START
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.DataItem
import com.appservice.staffs.model.FilterBy
import com.appservice.staffs.model.GetStaffListingRequest
import com.appservice.staffs.model.GetStaffListingResponse
import com.appservice.staffs.ui.UserSession
import com.appservice.staffs.ui.startStaffFragmentActivity
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.framework.extensions.visible
import java.util.*
import kotlin.collections.ArrayList

class StaffProfileListingFragment : AppBaseFragment<FragmentStaffListingBinding, StaffViewModel>(), RecyclerItemClickListener, SearchView.OnQueryTextListener {
    private lateinit var requestFilter: FilterBy
    private val list: ArrayList<DataItem> = ArrayList()
    private val copyList: ArrayList<DataItem> = ArrayList()

    /* Paging */
    private var isLoadingD = false
    private var TOTAL_ELEMENTS = 0
    private var currentPage = PAGE_START
    private var isLastPageD = false
    private var layoutManager: LinearLayoutManager? = null
    private var adapter: AppBaseRecyclerViewAdapter<DataItem>? = null

    override fun getLayout(): Int {
        return R.layout.fragment_staff_listing
    }

    override fun getViewModelClass(): Class<StaffViewModel> {
        return StaffViewModel::class.java
    }

    private fun removeLoader() {
        if (isLoadingD) {
            adapter?.removeLoadingFooter()
            isLoadingD = false
        }
    }

    private fun showMenuItem() {
        appBaseActivity?.getToolbar()?.menu?.findItem(R.id.app_bar_search)?.isVisible = true
    }

    private fun hideMenuItem() {
        appBaseActivity?.getToolbar()?.menu?.findItem(R.id.app_bar_search)?.isVisible = false
    }

    companion object {
        fun newInstance(): StaffProfileListingFragment {
            return StaffProfileListingFragment()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setHasOptionsMenu(true)
        hideMenuItem()
        layoutManager = LinearLayoutManager(baseActivity)
        layoutManager?.let { scrollPagingListener(it) }
        setOnClickListener(binding?.fragmentStaffAdd?.flAddStaff)
//        fetchStaffListing()
        requestFilter = FilterBy("ALL", currentPage, PAGE_SIZE)
        getStaffFilterApi(requestFilter, isFirst = true)

    }

    private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
        binding?.layoutStaffListing?.rvStaffList?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {

            override fun loadMoreItems() {
                if (!isLastPageD) {
                    isLoadingD = true
                    currentPage += requestFilter.limit ?: 0
                    requestFilter.offset = currentPage
                    getStaffFilterApi(requestFilter)
                }
            }

            override val totalPageCount: Int
                get() = TOTAL_ELEMENTS
            override val isLastPage: Boolean
                get() = isLastPageD
            override val isLoading: Boolean
                get() = isLoadingD
        })
    }


    private fun setAdapterNotify(items: ArrayList<DataItem>) {
        binding?.layoutStaffListing?.rvStaffList?.visible()
        if (adapter != null) {
            adapter?.notify(items)
        } else setAdapterProfileListing(items)
    }

    private fun setAdapterProfileListing(items: java.util.ArrayList<DataItem>) {
        binding?.layoutStaffListing?.rvStaffList?.post {
            adapter = AppBaseRecyclerViewAdapter(baseActivity, items, this)
            binding?.layoutStaffListing?.rvStaffList?.layoutManager = layoutManager
            binding?.layoutStaffListing?.rvStaffList?.adapter = adapter
            binding?.layoutStaffListing?.rvStaffList?.let { adapter?.runLayoutAnimation(it) }
        }
    }

    private fun getStaffFilterApi(filterBy: FilterBy, isFirst: Boolean = false, isRefresh: Boolean = false, isSearch: Boolean = false) {
        if (isFirst || isSearch) showProgress("")
        viewModel?.getStaffList(GetStaffListingRequest(filterBy, UserSession.fpId, ""))?.observe(viewLifecycleOwner, {
            when (it.status) {
                200 -> {
                    val getStaffListingResponse = it as GetStaffListingResponse
                    val data = getStaffListingResponse.result?.data
                    if (isSearch.not()) {
                        if (isRefresh) list.clear()
                        when {
                            data?.isNotEmpty()!! -> {
                                binding?.layoutStaffListing!!.root.visibility = View.VISIBLE
                                binding?.fragmentStaffAdd!!.root.visibility = View.GONE
                                data as ArrayList<DataItem>
                                this.list.addAll(data)
                                showMenuItem()
                                this.copyList.clear()
                                this.copyList.addAll(data)
                                TOTAL_ELEMENTS = list.size
                                list.addAll(list)
                                isLastPageD = (list.size == TOTAL_ELEMENTS)
                                setAdapterNotify(list)

                            }
                            else -> {

                                binding?.layoutStaffListing!!.root.visibility = View.GONE
                                binding?.fragmentStaffAdd!!.root.visibility = View.VISIBLE
                                hideProgress()

                            }
                        }
                    }
                }
            }
        })


    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        val staff = item as DataItem
        val bundle = Bundle()
        bundle.putSerializable("STAFF_DETAILS", staff)
        startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT, bundle, clearTop = false, isResult = false)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_stafflisting, menu)
        val searchItem = menu.findItem(R.id.app_bar_search)
        searchItem.isVisible = list.isNullOrEmpty().not()
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_staff)
        searchView.setOnQueryTextListener(this)
        searchView.clearFocus()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_add_staff -> {
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_DETAILS_FRAGMENT, clearTop = false, isResult = false)
                true
            }
            R.id.app_bar_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View) {
        when (v) {
            binding!!.fragmentStaffAdd.flAddStaff -> {
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_DETAILS_FRAGMENT, clearTop = false, isResult = false)
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }


    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            filter(newText)
        }
        return true
    }

    fun filter(queryText: String) {
        list.clear()
        if (queryText.isEmpty() && queryText.isBlank()) {
            list.addAll(copyList)
        } else {
            for (dataItem in copyList) {
                if (dataItem.name?.toLowerCase(Locale.ROOT)?.contains(queryText.toLowerCase(Locale.ROOT))!!) {
                    list.add(dataItem)
                }
            }
        }
        adapter?.updateList(list)
    }
}