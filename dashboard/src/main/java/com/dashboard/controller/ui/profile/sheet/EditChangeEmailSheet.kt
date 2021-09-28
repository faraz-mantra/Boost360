package com.dashboard.controller.ui.profile.sheet

import android.view.View
import com.dashboard.R
import com.dashboard.databinding.SheetChangeEmailBinding
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class EditChangeEmailSheet : BaseBottomSheetDialog<SheetChangeEmailBinding, UserProfileViewModel>() {

  companion object{
    val IK_EMAIL:String="IK_EMAIL"
  }
  override fun getLayout(): Int {
    return R.layout.sheet_change_email
  }

  override fun getViewModelClass(): Class<UserProfileViewModel> {
    return UserProfileViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnPublish)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnPublish->{
        viewModel?.sendEmailOTP(binding?.cetEmail?.text.toString())
      }
    }
  }
}