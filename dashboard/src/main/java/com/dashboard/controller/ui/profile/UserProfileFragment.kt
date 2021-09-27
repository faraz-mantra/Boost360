package com.dashboard.controller.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.controller.ui.business.bottomsheet.BusinessNameBottomSheet
import com.dashboard.controller.ui.profile.sheet.*
import com.dashboard.databinding.FragmentUserProfileBinding
import com.dashboard.model.live.user_profile.UserProfileData
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId2
import com.google.gson.Gson

class UserProfileFragment : AppBaseFragment<FragmentUserProfileBinding, UserProfileViewModel>() {

  private lateinit var session: UserSessionManager
  private val TAG = "UserProfileFragment"
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

  override fun getViewModelClass(): Class<UserProfileViewModel> {
    return UserProfileViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(baseActivity)
    fetchUserData()
    setOnClickListener(
      binding?.imgEdit, binding?.viewEmptyProfile, binding?.edtEmail, binding?.viewName, binding?.verifyEmail,
      binding?.verifyWhatsappNo, binding?.viewWhatsappNo, binding?.viewEmail, binding?.viewMobileNumber
    )
  }

  private fun fetchUserData() {
    viewModel?.getUserProfileData(session.userProfileId)
      ?.observe(viewLifecycleOwner,{
        Log.i(TAG, "fetchUserData: "+Gson().toJson(it))
        if (it.status==200){
          it as UserProfileData
          Glide.with(this).load(it.Result.ImageUrl).into(binding!!.imgProfile)
          binding?.txtName?.setText(it.Result.UserName)
          binding?.txtEmail?.setText(it.Result.Email)
          binding?.txtMobileNumber?.setText(it.Result.MobileNo)
          binding?.txtWhatsappNo?.setText(it.Result.FloatingPointDetails.first().WhatsAppNumber)
        }

        hideProgress()

      })
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