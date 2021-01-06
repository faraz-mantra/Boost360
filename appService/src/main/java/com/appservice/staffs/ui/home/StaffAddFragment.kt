package com.appservice.staffs.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.appservice.R
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentStaffAddBinding
import com.appservice.staffs.ui.Constants
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel

class StaffAddFragment : BaseFragment<FragmentStaffAddBinding, BaseViewModel>() {

    companion object {
        fun newInstance(): StaffAddFragment {
            val args = Bundle()
            val fragment = StaffAddFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_staff_add
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
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
                startStaffFragmentActivity(requireActivity(), FragmentType.STAFF_DETAILS_FRAGMENT, bundle, clearTop = false, isResult =false,requestCode = Constants.REQUEST_CODE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}