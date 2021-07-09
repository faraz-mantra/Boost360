package com.appservice.ui.staffs.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentStaffHomeBinding
import com.appservice.ui.staffs.ui.startStaffFragmentActivity
import com.framework.models.BaseViewModel

class StaffHomeFragment : AppBaseFragment<FragmentStaffHomeBinding, BaseViewModel>() {
  override fun onCreateView() {
    super.onCreateView()
    setHasOptionsMenu(true)
    setOnClickListener(binding?.btnTakeToListing)
  }

  companion object {
    fun newInstance(): StaffHomeFragment {
      return StaffHomeFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_staff_home
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_add, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle item selection
    return when (item.itemId) {
      R.id.menu_add -> {
        startStaffFragmentActivity(baseActivity, FragmentType.STAFF_ADD_FRAGMENT, clearTop = false, isResult = false)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnTakeToListing -> startStaffFragmentActivity(baseActivity, FragmentType.STAFF_PROFILE_LISTING_FRAGMENT, Bundle(), false, isResult = false)
    }
  }
}