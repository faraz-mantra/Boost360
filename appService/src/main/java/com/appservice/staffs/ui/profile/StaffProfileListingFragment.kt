package com.appservice.staffs.ui.profile

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentStaffListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.DataItem
import com.appservice.staffs.model.GetStaffListingRequest
import com.appservice.staffs.model.GetStaffListingResponse
import com.appservice.staffs.ui.home.UserSession
import com.appservice.staffs.ui.home.startStaffFragmentActivity
import java.util.*

class StaffProfileListingFragment : AppBaseFragment<FragmentStaffListingBinding, StaffListingViewModel>(), RecyclerItemClickListener {
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
        setHasOptionsMenu(true)
        fetchStaffListing()
    }

    private fun fetchStaffListing() {
        showProgress("Loading")
        viewModel?.getStaffList(GetStaffListingRequest(UserSession.fpId, 0, "", 0))?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            hideProgress()
            when (it.status) {
                200 -> {
                    val getStaffListingResponse = it as GetStaffListingResponse
                    val data = getStaffListingResponse.result?.data
                    binding?.rvStaffList?.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = data as ArrayList<DataItem>, itemClickListener = this@StaffProfileListingFragment)

                }
                else -> {
                    hideProgress()
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
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_add_staff -> {
                val bundle: Bundle = Bundle.EMPTY
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_ADD_FRAGMENT, bundle, clearTop = false, isResult = false)
                true
            }
            R.id.app_bar_search -> {
                showShortToast("Search here")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}