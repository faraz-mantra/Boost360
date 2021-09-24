package com.dashboard.controller.ui.profile

import android.os.Bundle
import android.view.View
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.controller.ui.business.bottomsheet.BusinessNameBottomSheet
import com.dashboard.controller.ui.profile.sheet.*
import com.dashboard.databinding.FragmentUserProfileBinding
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class UserProfileFragment : AppBaseFragment<FragmentUserProfileBinding, BaseViewModel>() {

  private lateinit var session: UserSessionManager

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): UserProfileFragment {
      val fragment = UserProfileFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_user_profile
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    setOnClickListener(
      binding?.imgEdit, binding?.viewEmptyProfile, binding?.edtEmail, binding?.viewName, binding?.verifyEmail,
      binding?.verifyWhatsappNo, binding?.viewWhatsappNo, binding?.viewEmail, binding?.viewMobileNumber
    )
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.imgEdit -> {
        showImagePickerSheet()
      }
      binding?.viewEmptyProfile -> {
      }
      binding?.viewName -> {
        showEditUserNameSheet()
      }
      binding?.viewEmail -> {
        showEditEmailSheet()

      }
      binding?.verifyEmail -> {
        showVerifyEmailSheet()

      }
      binding?.viewMobileNumber -> {
        showEditMobileSheet()
      }
      binding?.viewWhatsappNo -> {
        showEditWhatsappSheet()

      }
      binding?.verifyWhatsappNo -> {
        showEditWhatsappSheet()
      }
    }
  }

  private fun showImagePickerSheet() {
    ImagePickerSheet().show(parentFragmentManager, ImagePickerSheet::javaClass.name)
  }

  private fun showEditMobileSheet() {
    EditChangeMobileNumberSheet().show(parentFragmentManager, EditChangeMobileNumberSheet::javaClass.name)
  }

  private fun showEditEmailSheet() {
    EditChangeEmailSheet().show(parentFragmentManager, EditChangeEmailSheet::javaClass.name)
  }

  private fun showEditWhatsappSheet() {
    EditChangeWhatsappNumberSheet().show(parentFragmentManager, EditChangeWhatsappNumberSheet::javaClass.name)
  }

  private fun showVerifyEmailSheet() {
    VerifyOtpEmailMobileSheet().show(parentFragmentManager, VerifyOtpEmailMobileSheet::javaClass.name)
  }

  private fun showEditUserNameSheet() {
    EditChangeUserNameSheet().show(parentFragmentManager, EditChangeUserNameSheet::javaClass.name)
  }
}