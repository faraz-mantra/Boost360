package com.appservice.ui.aptsetting.ui

import android.view.View
import com.appservice.R
import com.appservice.ui.aptsetting.widgets.BottomSheetWhyVerifyAccount
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.AppointmentBankAddHomeBinding
import com.appservice.ui.catalog.startFragmentActivity
import com.framework.models.BaseViewModel

class FragmentAccountAddHome : AppBaseFragment<AppointmentBankAddHomeBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.appointment_bank_add_home
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  companion object {
    fun newInstance(): FragmentAccountAddHome {
      return FragmentAccountAddHome()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.btnVerifyAccount, binding?.closeBtn, binding?.btnAddBankDetails)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnAddBankDetails -> {
        startFragmentActivity(FragmentType.APPOINTMENT_ADD_ACCOUNT_DETAILS)
      }
      binding?.closeBtn -> {
        requireActivity().finish()
      }
      binding?.btnVerifyAccount -> {
        showWhyVerifyAccount()
      }
    }
  }

  private fun showWhyVerifyAccount() {
    val bottomSheetWhyVerifyAccount = BottomSheetWhyVerifyAccount()
    bottomSheetWhyVerifyAccount.show(parentFragmentManager, BottomSheetWhyVerifyAccount::class.java.name)
  }
}