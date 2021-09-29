package com.dashboard.controller.ui.profile.sheet

import android.content.Intent
import android.view.View
import com.dashboard.R
import com.dashboard.databinding.SheetLogoutBinding
import com.dashboard.databinding.SheetRemoveWhatsappNumberBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

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
    when(v){
      binding?.btnLogout->{
        dismiss()
        logout()
      }
    }
  }
  protected fun logout() {
    try {
      val i = Intent(baseActivity, Class.forName("com.nowfloats.helper.LogoutActivity"))
      startActivity(i)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}