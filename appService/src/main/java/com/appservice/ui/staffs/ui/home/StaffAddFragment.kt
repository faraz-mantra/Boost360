package com.appservice.ui.staffs.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentStaffAddBinding
import com.appservice.ui.staffs.ui.startStaffFragmentActivity
import com.appservice.ui.staffs.ui.Constants
import com.framework.models.BaseViewModel

class StaffAddFragment : AppBaseFragment<FragmentStaffAddBinding, BaseViewModel>() {

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
    binding?.btnAddStaff?.setOnClickListener { startStaffFragmentActivity(requireActivity(), if (isDoctorProfile(sessionLocal.fP_AppExperienceCode)) FragmentType.DOCTOR_ADD_EDIT_FRAGMENT else FragmentType.STAFF_DETAILS_FRAGMENT, Bundle(), clearTop = false, isResult = false, requestCode = Constants.REQUEST_CODE) }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_add, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_add -> {
        val bundle: Bundle = Bundle.EMPTY
        startStaffFragmentActivity(requireActivity(), if (isDoctorProfile(sessionLocal.fP_AppExperienceCode)) FragmentType.DOCTOR_ADD_EDIT_FRAGMENT else FragmentType.STAFF_DETAILS_FRAGMENT, bundle, clearTop = false, isResult = false, requestCode = Constants.REQUEST_CODE)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}