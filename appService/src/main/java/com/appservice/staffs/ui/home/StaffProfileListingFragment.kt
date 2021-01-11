package com.appservice.staffs.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentStaffListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.DataItem
import com.appservice.staffs.model.FilterBy
import com.appservice.staffs.model.GetStaffListingRequest
import com.appservice.staffs.model.GetStaffListingResponse
import com.appservice.staffs.ui.profile.StaffListingViewModel
import kotlinx.android.synthetic.main.fragment_staff_profile.view.*
import java.util.*

class StaffProfileListingFragment : AppBaseFragment<FragmentStaffListingBinding, StaffListingViewModel>(), RecyclerItemClickListener, SearchView.OnQueryTextListener {
    private lateinit var list: MutableList<DataItem>
    private lateinit var copyList: MutableList<DataItem>
    private lateinit var adapter: AppBaseRecyclerViewAdapter<DataItem>
    private var isSearchShowing: Boolean = false
    override fun getLayout(): Int {
        return R.layout.fragment_staff_listing
    }

    override fun getViewModelClass(): Class<StaffListingViewModel> {
        return StaffListingViewModel::class.java
    }

    companion object {
        fun newInstance(): StaffProfileListingFragment {
            return StaffProfileListingFragment()
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setHasOptionsMenu(true)
        setOnClickListener(binding?.fragmentStaffAdd?.flAddStaff)
        fetchStaffListing()
    }

    private fun fetchStaffListing() {
        showProgress("Loading")
        viewModel?.getStaffList(GetStaffListingRequest(FilterBy("", 0, 0), UserSession.fpId, ""))?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            hideProgress()
            when (it.status) {
                200 -> {
                    val getStaffListingResponse = it as GetStaffListingResponse
                    val data = getStaffListingResponse.result?.data
                    when {

                        data?.isNotEmpty()!! -> {
                            binding?.layoutStaffListing!!.root.visibility = View.VISIBLE
                            binding?.fragmentStaffAdd!!.root.visibility = View.GONE
                            isSearchShowing = true
                            this.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = data as ArrayList<DataItem>, itemClickListener = this@StaffProfileListingFragment)
                            binding?.layoutStaffListing?.rvStaffList?.adapter = adapter
                        }
                        else -> {
                            binding?.layoutStaffListing?.root?.visibility = View.GONE
                            binding?.fragmentStaffAdd!!.root.visibility = View.VISIBLE

                        }
                    }
                }
                else -> {
                    binding?.layoutStaffListing!!.root.visibility = View.GONE
                    binding?.fragmentStaffAdd!!.root.visibility = View.VISIBLE

                }
            }
        })
        hideProgress()
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        val staff = item as DataItem
        val bundle = Bundle()
        bundle.putSerializable("STAFF_DETAILS", staff)
        startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT, bundle, clearTop = false, isResult = false)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_stafflisting, menu)
        if (!isSearchShowing) {
            val findItem = menu.findItem(R.id.app_bar_search)
            findItem.isVisible = false
        }
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search Staff"
        searchView.setOnQueryTextListener(this)
        searchView.isIconified = false
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
                showShortToast("Search here")
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


    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            filter(newText)
        }
        return true
    }

    fun filter(queryText: String) {
        list.clear()
        if (queryText.isEmpty()) {
            list.addAll(copyList)
        } else {
            for (name in copyList) {
                if (name.contains(queryText.toLowerCase(Locale.ROOT))) {
                    list.add(name)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}