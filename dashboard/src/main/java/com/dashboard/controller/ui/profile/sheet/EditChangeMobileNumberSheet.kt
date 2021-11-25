package com.dashboard.controller.ui.profile.sheet

import android.view.View
import androidx.core.widget.addTextChangedListener
import com.dashboard.R
import com.dashboard.databinding.SheetChangeMobileNumberBinding
import com.dashboard.utils.WebEngageController
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.webengageconstant.*

class EditChangeMobileNumberSheet : BaseBottomSheetDialog<SheetChangeMobileNumberBinding, UserProfileViewModel>() {

  private var mobile: String? = null

  companion object {
    val IK_MOB: String = "IK_MOB"
  }

  override fun getLayout(): Int {
    return R.layout.sheet_change_mobile_number
  }

  override fun getViewModelClass(): Class<UserProfileViewModel> {
    return UserProfileViewModel::class.java
  }

  override fun onCreateView() {
    mobile = arguments?.getString(IK_MOB)
    binding?.cetPhone?.setText(mobile)
    WebEngageController.trackEvent(USER_MERCHANT_PROFILE_NUMBER_PAGE, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(binding?.btnPublish, binding?.rivCloseBottomSheet)
    binding?.cetPhone?.addTextChangedListener {
      binding?.btnPublish?.isEnabled = (it?.length == 10)
    }
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnPublish -> {
        WebEngageController.trackEvent(USER_MERCHANT_PROFILE_NUMBER_PUBLISH, CLICK, NO_EVENT_VALUE)
        val mobile = binding?.cetPhone?.text.toString()
        binding?.progressBar?.visible()
        viewModel?.sendMobileOTP(mobile)?.observeOnce(viewLifecycleOwner, {
          if (it.isSuccess()) {
            startVerifyMobEmailSheet(VerifyOtpEmailMobileSheet.SheetType.MOBILE.name, mobile)
          } else showShortToast(it.message())
          binding?.progressBar?.gone()
          dismiss()
        })
      }
      binding?.rivCloseBottomSheet -> dismiss()
    }
  }

}