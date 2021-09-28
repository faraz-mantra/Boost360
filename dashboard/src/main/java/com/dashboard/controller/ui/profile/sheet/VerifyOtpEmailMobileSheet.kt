package com.dashboard.controller.ui.profile.sheet

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import com.boost.presignin.views.otptextview.OTPListener
import com.dashboard.R
import com.dashboard.databinding.SheetVerifyOtpEmailNumberBinding
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class VerifyOtpEmailMobileSheet : BaseBottomSheetDialog<SheetVerifyOtpEmailNumberBinding, UserProfileViewModel>() {

  private var sheetType:String?=null
  private var emailOrMob:String?=null
  companion object{
    val IK_TYPE="IK_TYPE"
    val IK_EMAIL_OR_MOB="IK_EMAIL_OR_MOB"
  }
  enum class SheetType{
    EMAIL,
    MOBILE
  }
  override fun getLayout(): Int {
    return R.layout.sheet_verify_otp_email_number
  }

  override fun getViewModelClass(): Class<UserProfileViewModel> {
    return UserProfileViewModel::class.java
  }

  override fun onCreateView() {
    sheetType = arguments?.getString(IK_TYPE)
    emailOrMob = arguments?.getString(IK_EMAIL_OR_MOB)
    if (sheetType==SheetType.EMAIL.name){
      binding?.tvHeading?.text = getString(R.string.verify_email)
    }else{
      binding?.tvHeading?.text = getString(R.string.verify_mobile_number)
    }
    binding?.tvMobOrEmail?.text = emailOrMob

    viewListeners()
    setOnClickListener(binding?.btnPublish,binding?.btnResend,binding?.rivCloseBottomSheet)
  }

  private fun viewListeners() {
    binding?.pinTv?.otpListener = object :OTPListener{
      override fun onInteractionListener() {
        binding?.btnPublish?.isEnabled =binding?.pinTv?.otp?.length?:0==4

      }

      override fun onOTPComplete(otp: String) {
        binding?.btnPublish?.isEnabled =true

      }

    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnPublish->{
        updateEmailApi()
      }
      binding?.btnResend->{
        sendOtp()
      }
      binding?.rivCloseBottomSheet->dismiss()
    }
  }

  private fun updateEmailApi() {
    binding?.progressBar?.visible()
    if (sheetType==SheetType.EMAIL.name){
      viewModel?.updateEmail(emailOrMob,binding?.pinTv?.otp,
        UserSessionManager(requireContext()).userProfileId)?.observe(viewLifecycleOwner,{
        if (it.isSuccess()){
          dismiss()
        }else{
          binding?.tvInvalidOtp?.visible()
        }

        binding?.progressBar?.gone()

      })

    }
  }

  private fun sendOtp() {
    if (sheetType==SheetType.EMAIL.name){
      binding?.progressBar?.visible()
      viewModel?.sendEmailOTP(emailOrMob)?.observe(viewLifecycleOwner,{
        if (it.isSuccess()){
            showLongToast(getString(R.string.otp_resent))
        }
        binding?.progressBar?.gone()
      })
    }

  }
}