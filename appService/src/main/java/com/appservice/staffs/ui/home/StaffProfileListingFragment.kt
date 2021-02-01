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
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentStaffListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.DataItem
import com.appservice.staffs.model.FilterBy
import com.appservice.staffs.model.GetStaffListingRequest
import com.appservice.staffs.model.GetStaffListingResponse
import com.appservice.staffs.ui.UserSession
import com.appservice.staffs.ui.startStaffFragmentActivity
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import kotlinx.android.synthetic.main.fragment_staff_profile.view.*
import java.util.*
import kotlin.collections.ArrayList

class StaffProfileListingFragment : AppBaseFragment<FragmentStaffListingBinding, StaffViewModel>(), RecyclerItemClickListener, SearchView.OnQueryTextListener {
    private val list: ArrayList<DataItem> = ArrayList()
    private val copyList: ArrayList<DataItem> = ArrayList()
    private lateinit var adapter: AppBaseRecyclerViewAdapter<DataItem>
    override fun getLayout(): Int {
        return R.layout.fragment_staff_listing
    }

    override fun getViewModelClass(): Class<StaffViewModel> {
        return StaffViewModel::class.java
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
        setOnClickListener(binding?.fragmentStaffAdd?.flAddStaff)
        fetchStaffListing()
    }

    private fun fetchStaffListing() {
        showProgress("Loading")
        viewModel?.getStaffList(GetStaffListingRequest(FilterBy("", 0, 0), UserSession.fpId, ""))?.observe(viewLifecycleOwner, {
            when (it.status) {
                200 -> {
                    val getStaffListingResponse = it as GetStaffListingResponse
                    val data = getStaffListingResponse.result?.data
                    when {
                        data?.isNotEmpty()!! -> {
                            binding?.layoutStaffListing!!.root.visibility = View.VISIBLE
                            binding?.fragmentStaffAdd!!.root.visibility = View.GONE
                            data as ArrayList<DataItem>
                            this.list.addAll(data)
                            showMenuItem()
                            this.copyList.clear()
                            this.copyList.addAll(data)
                            this.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = data, itemClickListener = this@StaffProfileListingFragment)
                            binding?.layoutStaffListing?.rvStaffList?.adapter = adapter
                            hideProgress()
                        }
                        else -> {
                            binding?.layoutStaffListing?.root?.visibility = View.GONE
                            binding?.fragmentStaffAdd!!.root.visibility = View.VISIBLE
                            hideProgress()
                        }
                    }
                }
                else -> {
                    binding?.layoutStaffListing!!.root.visibility = View.GONE
                    binding?.fragmentStaffAdd!!.root.visibility = View.VISIBLE
                    hideProgress()

                }
            }

        })
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        val staff = item as DataItem
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.STAFF_DATA.name, staff)
        startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_PROFILE_DETAILS_FRAGMENT, bundle, clearTop = false, isResult = false)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_stafflisting, menu)
        val searchItem = menu.findItem(R.id.app_bar_search)
        searchItem.isVisible = list.isNullOrEmpty().not()
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search Staff"
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
        adapter.updateList(list)
    }
}