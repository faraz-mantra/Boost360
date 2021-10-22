package com.dashboard.controller.ui.profile.sheet

import android.view.View
import com.dashboard.R
import com.dashboard.databinding.SheetLogoutBinding
import com.dashboard.utils.WebEngageController
import com.dashboard.utils.startLogoutActivity
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import com.framework.webengageconstant.USER_MERCHANT_PROFILE_LOGOUT_LOAD

class LogoutSheet : BaseBottomSheetDialog<SheetLogoutBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_logout
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnLogout)
    WebEngageController.trackEvent(USER_MERCHANT_PROFILE_LOGOUT_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnLogout -> {
        baseActivity.startLogoutActivity()
        dismiss()
      }
    }
  }
}