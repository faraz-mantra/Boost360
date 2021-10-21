package com.dashboard.controller.ui.profile.sheet

import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.dashboard.R
import com.dashboard.controller.ui.profile.UpdateProfileUiListener
import com.dashboard.databinding.SheetChangeUsernameBinding
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager

class EditChangeUserNameSheet(private val updateProfileUiListener: UpdateProfileUiListener) : BaseBottomSheetDialog<SheetChangeUsernameBinding, UserProfileViewModel>() {

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
    viewListeners()
    binding?.cetBusinessName?.setText(userName)
    setOnClickListener(binding?.btnPublish, binding?.rivCloseBottomSheet)
  }

  private fun viewListeners() {
    binding?.cetBusinessName?.doAfterTextChanged {
      val length1 = it?.length ?: 0
      binding?.ctvBusinessNameCount?.text = "${length1}/40"
      binding?.btnPublish?.isEnabled = length1 < 40
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnPublish -> {
        updateUserNameApi()
      }
      binding?.rivCloseBottomSheet -> dismiss()
    }
  }

  private fun updateUserNameApi() {
    binding?.progressBar?.visible()
    viewModel?.updateUserName(binding?.cetBusinessName?.text.toString(), UserSessionManager(baseActivity).userProfileId)?.observeOnce(viewLifecycleOwner, {
      binding?.progressBar?.gone()
      if (it.isSuccess()) {
        updateProfileUiListener.onUpdateProfile()
        dismiss()
      } else showShortToast(it.message())
    })
  }
}