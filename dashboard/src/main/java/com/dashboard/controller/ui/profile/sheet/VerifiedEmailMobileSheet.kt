package com.dashboard.controller.ui.profile.sheet

import android.view.View
import com.dashboard.R
import com.dashboard.databinding.SheetVerifiedEmailNumberBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.setIconifiedText

class VerifiedEmailMobileSheet : BaseBottomSheetDialog<SheetVerifiedEmailNumberBinding, BaseViewModel>() {

  private var sheetType: String? = null
  private var emailOrMob: String? = null

  companion object {
    val IK_TYPE = "IK_TYPE"
    val IK_EMAIL_OR_MOB = "IK_EMAIL_OR_MOB"
  }

  enum class SheetType {
    EMAIL,
    MOBILE
  }

  override fun getLayout(): Int {
    return R.layout.sheet_verified_email_number
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    sheetType = arguments?.getString(IK_TYPE)
    emailOrMob = arguments?.getString(IK_EMAIL_OR_MOB)
    if (sheetType == SheetType.EMAIL.name) {
      if (emailOrMob.isNullOrEmpty().not()) {
        binding?.btnChangeNumber?.text = getString(R.string.add_email)
        binding?.title?.gone()
      } else {
        binding?.title?.visible()
        binding?.btnChangeNumber?.text = getString(R.string.change_email)
        binding?.title?.setIconifiedText(
          getString(R.string.your_email_verified, emailOrMob), R.drawable.ic_check_circle_d,
          "verified", R.color.green_6FCF97,
          emailOrMob, R.font.bold
        )
      }
    } else {
      if (emailOrMob.isNullOrEmpty()) {
        binding?.btnChangeNumber?.text = getString(R.string.add_number)
        binding?.title?.gone()
      } else {
        binding?.title?.visible()
        binding?.btnChangeNumber?.text = getString(R.string.change_number)
        binding?.title?.setIconifiedText(
          getString(R.string.hello_worldBlue, "+91 ${emailOrMob ?: ""}"), R.drawable.ic_check_circle_d,
          "verified", R.color.green_6FCF97,
          "+91 $emailOrMob", R.font.bold
        )
      }
    }

    setOnClickListener(binding?.btnCancel, binding?.btnChangeNumber)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnChangeNumber -> {
        if (sheetType == SheetType.EMAIL.name) {
          startProfileEditEmailSheet(emailOrMob)
        } else {
          startProfileEditMobSheet(emailOrMob)
        }
        dismiss()
      }

      binding?.btnCancel -> {
        dismiss()
      }
    }
  }
}