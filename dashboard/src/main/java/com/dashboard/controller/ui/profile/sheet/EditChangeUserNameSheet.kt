package com.dashboard.controller.ui.profile.sheet

import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.dashboard.R
import com.dashboard.databinding.SheetChangeUsernameBinding
import com.dashboard.utils.WebEngageController
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.*

class EditChangeUserNameSheet(var click: () -> Unit) : BaseBottomSheetDialog<SheetChangeUsernameBinding, UserProfileViewModel>() {

  private var userName: String? = null

  companion object {
    val IK_NAME: String = "IK_NAME"
  }

  override fun getLayout(): Int {
    return R.layout.sheet_change_username
  }

  override fun getViewModelClass(): Class<UserProfileViewModel> {
    return UserProfileViewModel::class.java
  }

  override fun onCreateView() {
    userName = arguments?.getString(IK_NAME)
    WebEngageController.trackEvent(USER_MERCHANT_PROFILE_USERNAME_PAGE, PAGE_VIEW, NO_EVENT_VALUE)
    viewListeners()
    binding?.cetBusinessName?.setText(userName)
    setOnClickListener(binding?.btnPublish, binding?.rivCloseBottomSheet)
  }

  private fun viewListeners() {
    binding?.cetBusinessName?.doAfterTextChanged {
      val length1 = it?.length ?: 0
      binding?.ctvBusinessNameCount?.text = "${length1}/40"
      binding?.btnPublish?.isEnabled = length1 <= 40
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnPublish -> {
        WebEngageController.trackEvent(USER_MERCHANT_PROFILE_USERNAME_PUBLISH, CLICK, NO_EVENT_VALUE)
        updateUserNameApi()
      }
      binding?.rivCloseBottomSheet -> dismiss()
    }
  }

  private fun updateUserNameApi() {
    if(binding?.cetBusinessName?.text.toString().isNullOrEmpty()){
      showShortToast("Enter Valid Business Name.")
      return
    }
    binding?.progressBar?.visible()
    viewModel?.updateUserName(binding?.cetBusinessName?.text.toString(), UserSessionManager(baseActivity).userProfileId)?.observeOnce(viewLifecycleOwner, {
      binding?.progressBar?.gone()
      if (it.isSuccess()) {
        click()
        dismiss()
      } else showShortToast(it.message())
    })
  }
}