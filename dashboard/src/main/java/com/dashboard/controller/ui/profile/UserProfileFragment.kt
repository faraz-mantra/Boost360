package com.dashboard.controller.ui.profile

import android.os.Bundle
import android.os.Handler
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
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId2
import com.google.gson.Gson

class UserProfileFragment : AppBaseFragment<FragmentUserProfileBinding, UserProfileViewModel>() {

  private lateinit var session: UserSessionManager
  private val TAG = "UserProfileFragment"
  private var userProfileData:UserProfileData?=null
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
    showProgress()
    viewModel?.getUserProfileData(session.userProfileId)
      ?.observe(viewLifecycleOwner,{
        Log.i(TAG, "fetchUserData: "+Gson().toJson(it))
        if (it.status==200){
          userProfileData =it as UserProfileData
          Glide.with(this).load(it.Result.ImageUrl).into(binding!!.imgProfile)
          binding?.txtName?.setText(it.Result.UserName)
          binding?.txtEmail?.setText(it.Result.Email)
          binding?.txtMobileNumber?.setText(it.Result.MobileNo)
          binding?.txtWhatsappNo?.setText(it.Result.FloatingPointDetails.first().WhatsAppNumber)

          if (it.Result.Email!=null){
            binding?.verifyEmail?.visible()
            if (it.Result.IsEmailVerfied){
              binding?.verifyEmail?.text = getString(R.string.verified)
              binding?.edtEmail?.gone()
            }else{
              binding?.verifyEmail?.text=getString(R.string.verify_cap)
              binding?.edtEmail?.visible()
            }
          }



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
    val dialog = EditChangeEmailSheet().apply {
      arguments = Bundle().apply {  putString(EditChangeEmailSheet.IK_EMAIL,userProfileData?.Result?.Email)}
    }
    dialog.show(parentFragmentManager, EditChangeEmailSheet::javaClass.name)


  }

  private fun showEditWhatsappSheet() {
    EditChangeWhatsappNumberSheet().show(parentFragmentManager, EditChangeWhatsappNumberSheet::javaClass.name)
  }

  private fun showVerifyEmailSheet() {
    VerifyOtpEmailMobileSheet().show(parentFragmentManager, VerifyOtpEmailMobileSheet::javaClass.name)
  }

  private fun showEditUserNameSheet() {
    val dialog = EditChangeUserNameSheet().apply {
      arguments = Bundle().apply {  putString(EditChangeUserNameSheet.IK_NAME,userProfileData?.Result?.UserName)}
    }
    dialog.show(parentFragmentManager, EditChangeUserNameSheet::javaClass.name)

    Handler().postDelayed({
      dialog.dialog?.setOnDismissListener {
        fetchUserData()
      }
    }, 1000)
  }

  override fun onResume() {
    super.onResume()
  }
}