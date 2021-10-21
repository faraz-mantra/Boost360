package com.dashboard.controller.ui.profile

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import com.boost.presignin.model.userprofile.UserProfileData
import com.boost.presignin.model.userprofile.UserProfileDataResult
import com.boost.presignin.model.userprofile.UserProfileDataResult.Companion.saveMerchantProfileDetails
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.controller.ui.profile.sheet.*
import com.dashboard.databinding.FragmentUserProfileBinding
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.pref.UserSessionManager

class UserProfileFragment : AppBaseFragment<FragmentUserProfileBinding, UserProfileViewModel>(), UpdateProfileUiListener {

  private lateinit var session: UserSessionManager
  private val TAG = "UserProfileFragment"
  private var userProfileData: UserProfileDataResult? = null

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
    setOnClickListener(
      binding?.imgEdit, binding?.viewEmptyProfile, binding?.edtEmail, binding?.viewName, binding?.verifyEmail,
      binding?.verifyWhatsappNo, binding?.viewWhatsappNo, binding?.viewEmail, binding?.viewMobileNumber,
      binding?.txtEmail, binding?.txtMobileNumber, binding?.txtWhatsappNo, binding?.btnLogout
    )
  }

  override fun onResume() {
    super.onResume()
    val merchantProfileDetails = UserProfileDataResult.getMerchantProfileDetails()
    setDataFromPref(merchantProfileDetails)
    fetchUserData()
  }
  private fun setDataFromPref(merchantProfileDetails: UserProfileDataResult?) {
    if (merchantProfileDetails?.ImageUrl.isNullOrEmpty()) {
      binding?.viewEmptyProfile?.visible()
      binding?.viewProfile?.gone()
    } else {
      binding?.viewEmptyProfile?.gone()
      binding?.viewProfile?.visible()
      binding?.imgProfile?.let { baseActivity.glideLoad(it, merchantProfileDetails?.ImageUrl ?: "", R.drawable.placeholder_image_n) }
    }

    binding?.txtName?.setText(merchantProfileDetails?.UserName ?: "")

    binding?.txtMobileNumber?.setText(merchantProfileDetails?.MobileNo ?: "")
    if (merchantProfileDetails?.MobileNo.isNullOrEmpty().not()) {
      binding?.verifyMobile?.visible()
      binding?.viewMobileNumber?.background = ContextCompat.getDrawable(baseActivity, R.drawable.rounded_view_stroke_grey)
    } else {
      binding?.verifyMobile?.gone()
      binding?.viewMobileNumber?.background = ContextCompat.getDrawable(baseActivity, R.drawable.rounded_view_stroke_grey_white)
    }

//    binding?.txtWhatsappNo?.setText(merchantProfileDetails?.FloatingPointDetails?.first()?.WhatsAppNumber ?: "")
//    binding?.txtEmail?.isEnabled = true
//    binding?.txtMobileNumber?.isEnabled = true
//    binding?.txtWhatsappNo?.isEnabled = true

    binding?.txtEmail?.setText(merchantProfileDetails?.Email ?: "")
    if (merchantProfileDetails?.Email.isNullOrEmpty()) {
      binding?.verifyEmail?.visible()
      if (merchantProfileDetails?.IsEmailVerfied == true) {
        binding?.verifyEmail?.text = getString(R.string.verified)
        binding?.verifyEmail?.setTextColor(getColor(R.color.green_6FCF97))
        binding?.edtEmail?.gone()
      } else {
        binding?.verifyEmail?.text = getString(R.string.verify_cap)
        binding?.edtEmail?.visible()
        binding?.verifyEmail?.setTextColor(getColor(R.color.colorAccentLight))
      }
      binding?.viewEmail?.background = ContextCompat.getDrawable(baseActivity, R.drawable.rounded_view_stroke_grey)
    } else {
      binding?.verifyEmail?.gone()
      binding?.edtEmail?.gone()
      binding?.viewEmail?.background = ContextCompat.getDrawable(baseActivity, R.drawable.rounded_view_stroke_grey_white)
    }
  }

  private fun fetchUserData() {
    viewModel?.getUserProfileData(session.userProfileId)?.observeOnce(viewLifecycleOwner, {
      userProfileData = (it as? UserProfileData)?.Result
      if (it.isSuccess() && userProfileData != null) {
        saveMerchantProfileDetails(userProfileData!!)
      }
      setDataFromPref(userProfileData)
    })
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.imgEdit -> {
        showImagePickerSheet()
      }
      binding?.viewEmptyProfile -> {
        showImagePickerSheet()
      }
      binding?.viewName -> {
        showEditUserNameSheet()
      }
      binding?.viewEmail -> {
        startProfileEditEmailSheet(userProfileData?.Email)

      }
      binding?.txtEmail -> {
        startProfileEditEmailSheet(userProfileData?.Email)
      }
      binding?.verifyEmail -> {
        startVerifiedMobEmailSheet(VerifyOtpEmailMobileSheet.SheetType.EMAIL.name, userProfileData?.Email)
      }
      binding?.viewMobileNumber -> {
        startVerifiedMobEmailSheet(VerifyOtpEmailMobileSheet.SheetType.MOBILE.name, userProfileData?.MobileNo)
      }
      binding?.txtMobileNumber -> {
        startVerifiedMobEmailSheet(VerifyOtpEmailMobileSheet.SheetType.MOBILE.name, userProfileData?.MobileNo)
      }
      binding?.viewWhatsappNo -> {
        startProfileEditWhatsappSheet(userProfileData?.FloatingPointDetails?.first()?.WhatsAppNumber)

      }
      binding?.verifyWhatsappNo -> {
        startProfileEditWhatsappSheet(userProfileData?.FloatingPointDetails?.first()?.WhatsAppNumber)
      }
      binding?.txtWhatsappNo -> {
        startProfileEditWhatsappSheet(userProfileData?.FloatingPointDetails?.first()?.WhatsAppNumber)
      }
      binding?.btnLogout -> {
        startLogoutSheet()
      }
    }
  }

  private fun showImagePickerSheet() {
    ImagePickerSheet(this@UserProfileFragment).show(
      parentFragmentManager,
      ImagePickerSheet::javaClass.name
    )
  }


  private fun showVerifyEmailSheet() {
    VerifyOtpEmailMobileSheet().show(
      parentFragmentManager,
      VerifyOtpEmailMobileSheet::javaClass.name
    )
  }

  private fun showEditUserNameSheet() {
    val dialog = EditChangeUserNameSheet(this@UserProfileFragment).apply {
      arguments = Bundle().apply { putString(EditChangeUserNameSheet.IK_NAME, userProfileData?.UserName) }
    }
    dialog.show(parentFragmentManager, EditChangeUserNameSheet::javaClass.name)

    Handler().postDelayed({ dialog.dialog?.setOnDismissListener { fetchUserData() } }, 1000)
  }

  override fun onUpdateProfile() {
    fetchUserData()
  }
}