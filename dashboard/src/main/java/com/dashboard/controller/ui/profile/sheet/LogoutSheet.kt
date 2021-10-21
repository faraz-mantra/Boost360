package com.dashboard.controller.ui.profile.sheet

import android.content.DialogInterface
import android.view.ContextThemeWrapper
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.dashboard.R
import com.dashboard.databinding.SheetLogoutBinding
import com.dashboard.utils.WebEngageController
import com.dashboard.utils.startLogoutActivity
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.BOOST_LOGOUT_CLICK
import com.framework.webengageconstant.CLICK
import com.framework.webengageconstant.NO_EVENT_VALUE

class LogoutSheet : BaseBottomSheetDialog<SheetLogoutBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.sheet_logout
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnLogout)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnLogout -> {
        logoutUser()
        dismiss()
      }
    }
  }

  private fun logoutUser() {
    AlertDialog.Builder(ContextThemeWrapper(baseActivity, R.style.CustomAlertDialogTheme))
      .setCancelable(false)
      .setMessage(R.string.are_you_sure)
      .setPositiveButton(R.string.logout) { dialog: DialogInterface, _: Int ->
        WebEngageController.trackEvent(BOOST_LOGOUT_CLICK, CLICK, NO_EVENT_VALUE)
        baseActivity.startLogoutActivity()
        dialog.dismiss()
      }.setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int ->
        dialog.dismiss()
      }.show()
  }
}