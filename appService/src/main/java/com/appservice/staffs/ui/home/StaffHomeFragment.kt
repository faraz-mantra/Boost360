package com.appservice.staffs.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.StaffHomeActivityBinding
import com.appservice.staffs.model.GetStaffListingRequest
import com.appservice.staffs.model.GetStaffListingResponse
import com.appservice.staffs.ui.services.StaffServicesViewModel
import java.io.Serializable

class StaffHomeFragment : AppBaseFragment<StaffHomeActivityBinding, StaffHomeViewModel>() {
    override fun onCreateView() {
        super.onCreateView()
        setHasOptionsMenu(true)
        fetchStaffListing()
    }

    private fun fetchStaffListing() {
        viewModel?.getStaffList(GetStaffListingRequest(UserSession.fpId, 0, "", 0))?.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                200 -> {
                    val getStaffListingResponse = it as GetStaffListingResponse
                    val data = getStaffListingResponse.result?.data
                    val bundle = Bundle()
                    bundle.putSerializable("STAFF_LIST", data as Serializable)
                    startStaffFragmentActivity(FragmentType.STAFF_PROFILE_LISTING_FRAGMENT, bundle, false, isResult = false)
                }
            }
        })
    }

    companion object {
        fun newInstance(): StaffHomeFragment {
            return StaffHomeFragment()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_staff_home
    }

    override fun getViewModelClass(): Class<StaffHomeViewModel> {
        return StaffHomeViewModel::class.java
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_add -> {
                val bundle: Bundle = Bundle.EMPTY
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_ADD_FRAGMENT, bundle, clearTop = false, isResult = false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}