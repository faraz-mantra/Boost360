package com.dashboard.controller.ui.profile.sheet

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.observe
import com.dashboard.R
import com.dashboard.databinding.SheetChangeEmailBinding
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
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
    val email =arguments?.getString(IK_EMAIL)
    binding?.cetEmail?.setText(email)
    setOnClickListener(binding?.btnPublish,binding?.rivCloseBottomSheet)
    viewListeners()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnPublish->{
        sendOtp()
      }
      binding?.rivCloseBottomSheet->dismiss()
    }
  }

  private fun sendOtp() {
    val email =binding?.cetEmail?.text.toString()
    binding?.progressBar?.visible()
    viewModel?.sendEmailOTP(email)?.observe(viewLifecycleOwner,{
      if (it.status==200){
        val dialog = VerifyOtpEmailMobileSheet().apply {
          arguments = Bundle().apply {
            putString(VerifyOtpEmailMobileSheet.IK_TYPE,VerifyOtpEmailMobileSheet.SheetType.EMAIL.name)
            putString(VerifyOtpEmailMobileSheet.IK_EMAIL_OR_MOB,email)

          }
        }
        dialog.show(parentFragmentManager,VerifyOtpEmailMobileSheet::javaClass.name)
      }
      binding?.progressBar?.gone()
      dismiss()
    })
  }

  private fun viewListeners() {
    binding?.cetEmail?.addTextChangedListener {
      binding?.btnPublish?.isEnabled = it?.length?:0>0
    }
  }
}