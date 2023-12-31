package com.dashboard.controller.ui.profile.sheet

import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.dashboard.R
import com.dashboard.databinding.SheetChangeEmailBinding
import com.dashboard.utils.WebEngageController
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.showKeyBoard
import com.framework.webengageconstant.*

class EditChangeEmailSheet : BaseBottomSheetDialog<SheetChangeEmailBinding, UserProfileViewModel>() {

  companion object {
    val IK_EMAIL: String = "IK_EMAIL"
  }

  private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

  override fun getLayout(): Int {
    return R.layout.sheet_change_email
  }

  override fun getViewModelClass(): Class<UserProfileViewModel> {
    return UserProfileViewModel::class.java
  }

  override fun onCreateView() {
    val email = arguments?.getString(IK_EMAIL)
    WebEngageController.trackEvent(USER_MERCHANT_PROFILE_EMAIL_PAGE, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.cetEmail?.setText(email)
    binding?.cetEmail?.requestFocus()
    baseActivity.showKeyBoard(binding?.cetEmail)
    setOnClickListener(binding?.btnPublish, binding?.rivCloseBottomSheet)
    viewListeners()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnPublish -> {
        WebEngageController.trackEvent(USER_MERCHANT_PROFILE_EMAIL_PUBLISH, CLICK, NO_EVENT_VALUE)
        sendOtp()
      }
      binding?.rivCloseBottomSheet -> dismiss()
    }
  }

  private fun sendOtp() {
    val email = binding?.cetEmail?.text.toString().trim()
    if (email.matches(emailPattern.toRegex())) {
      binding?.progressBar?.visible()
      viewModel?.sendEmailOTP(email)?.observeOnce(viewLifecycleOwner) {
        if (it.isSuccess()) {
          startVerifyMobEmailSheet(VerifyOtpEmailMobileSheet.SheetType.EMAIL.name, email)
        }
        binding?.progressBar?.gone()
        dismiss()
      }
    } else {
      Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show()
    }
  }

  private fun viewListeners() {
    binding?.cetEmail?.addTextChangedListener {
      binding?.btnPublish?.isEnabled = it?.length ?: 0 > 0
    }
  }
}