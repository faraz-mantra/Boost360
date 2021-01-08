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
import com.appservice.staffs.ui.home.startStaffFragmentActivity
import com.framework.models.BaseViewModel
import java.util.*

class StaffProfileListingFragment : AppBaseFragment<FragmentStaffListingBinding, BaseViewModel>(), RecyclerItemClickListener {
    override fun getLayout(): Int {
        return R.layout.fragment_staff_listing
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        fun newInstance(): StaffProfileListingFragment {
            return StaffProfileListingFragment()
        }
    }

    override fun onCreateView() {
        setHasOptionsMenu(true)
        val get = arguments?.get("STAFF_LIST")
        binding?.rvStaffList?.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = get as ArrayList<DataItem>, itemClickListener = this@StaffProfileListingFragment)
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
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