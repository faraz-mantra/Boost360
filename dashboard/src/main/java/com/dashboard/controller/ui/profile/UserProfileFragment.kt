package com.dashboard.controller.ui.profile

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.controller.ui.profile.sheet.*
import com.dashboard.databinding.FragmentUserProfileBinding
import com.dashboard.model.live.user_profile.UserProfileData
import com.dashboard.model.live.user_profile.UserProfileDataResult
import com.dashboard.viewmodel.UserProfileViewModel
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.google.gson.Gson

class UserProfileFragment : AppBaseFragment<FragmentUserProfileBinding, UserProfileViewModel>() {

    private lateinit var session: UserSessionManager
    private val TAG = "UserProfileFragment"
    private var userProfileData: UserProfileData? = null

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
        if (session.isMerchantUserDetailsSaved) {
            setDataFromPref(UserProfileDataResult.getMerchantProfileDetails(session))
        } else {
            fetchUserData()
        }
        setOnClickListener(
            binding?.imgEdit,
            binding?.viewEmptyProfile,
            binding?.edtEmail,
            binding?.viewName,
            binding?.verifyEmail,
            binding?.verifyWhatsappNo,
            binding?.viewWhatsappNo,
            binding?.viewEmail,
            binding?.viewMobileNumber,
            binding?.txtEmail,
            binding?.txtMobileNumber,
            binding?.txtWhatsappNo,
            binding?.btnLogout
        )
    }

    private fun setDataFromPref(merchantProfileDetails: UserProfileDataResult?) {
        Glide.with(this).load(merchantProfileDetails?.ImageUrl)
            .placeholder(R.drawable.placeholder_image_n).into(binding!!.imgProfile)
        binding?.txtName?.setText(merchantProfileDetails?.UserName)
        binding?.txtEmail?.setText(merchantProfileDetails?.Email)
        binding?.txtMobileNumber?.setText(merchantProfileDetails?.MobileNo)
        binding?.txtWhatsappNo?.setText(merchantProfileDetails?.FloatingPointDetails?.first()?.WhatsAppNumber)
        binding?.txtEmail?.isEnabled = true
        binding?.txtMobileNumber?.isEnabled = true
        binding?.txtWhatsappNo?.isEnabled = true

        if (merchantProfileDetails?.Email != null) {
            binding?.verifyEmail?.visible()
            if (merchantProfileDetails.IsEmailVerfied) {
                binding?.verifyEmail?.text = getString(R.string.verified)
                binding?.verifyEmail?.setTextColor(getColor(R.color.green_6FCF97))
                binding?.edtEmail?.gone()
            } else {
                binding?.verifyEmail?.text = getString(R.string.verify_cap)
                binding?.edtEmail?.visible()
                binding?.verifyEmail?.setTextColor(getColor(R.color.colorAccentLight))
            }
        }
    }

    private fun fetchUserData() {
        showProgress()
        viewModel?.getUserProfileData(session.userProfileId)
            ?.observe(viewLifecycleOwner, {
                Log.i(TAG, "fetchUserData: " + Gson().toJson(it))
                if (it.status == 200) {
                    userProfileData = it as UserProfileData
                    val userProfileResult = userProfileData?.Result
                    userProfileResult.apply {
                        UserProfileDataResult.saveMerchantProfileDetails(session, this!!)
                    }
                    setDataFromPref(userProfileResult)
                    binding?.verifyWhatsappNo?.visible()
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
                startProfileEditEmailSheet(userProfileData?.Result?.Email)

            }
            binding?.txtEmail -> {
                startProfileEditEmailSheet(userProfileData?.Result?.Email)
            }
            binding?.verifyEmail -> {
                startVerifiedMobEmailSheet(
                    VerifyOtpEmailMobileSheet.SheetType.EMAIL.name,
                    userProfileData?.Result?.Email
                )

            }
            binding?.viewMobileNumber -> {
                startVerifiedMobEmailSheet(
                    VerifyOtpEmailMobileSheet.SheetType.MOBILE.name,
                    userProfileData?.Result?.MobileNo
                )
            }
            binding?.txtMobileNumber -> {
                startVerifiedMobEmailSheet(
                    VerifyOtpEmailMobileSheet.SheetType.MOBILE.name,
                    userProfileData?.Result?.MobileNo
                )
            }
            binding?.viewWhatsappNo -> {
                startProfileEditWhatsappSheet(userProfileData?.Result?.FloatingPointDetails?.first()?.WhatsAppNumber)

            }
            binding?.verifyWhatsappNo -> {
                startProfileEditWhatsappSheet(userProfileData?.Result?.FloatingPointDetails?.first()?.WhatsAppNumber)
            }
            binding?.txtWhatsappNo -> {
                startProfileEditWhatsappSheet(userProfileData?.Result?.FloatingPointDetails?.first()?.WhatsAppNumber)
            }
            binding?.btnLogout -> {
                startLogoutSheet()
            }
        }
    }

    private fun showImagePickerSheet() {
        ImagePickerSheet().show(parentFragmentManager, ImagePickerSheet::javaClass.name)
    }


    private fun showVerifyEmailSheet() {
        VerifyOtpEmailMobileSheet().show(
            parentFragmentManager,
            VerifyOtpEmailMobileSheet::javaClass.name
        )
    }

    private fun showEditUserNameSheet() {
        val dialog = EditChangeUserNameSheet().apply {
            arguments = Bundle().apply {
                putString(
                    EditChangeUserNameSheet.IK_NAME,
                    userProfileData?.Result?.UserName
                )
            }
        }
        dialog.show(parentFragmentManager, EditChangeUserNameSheet::javaClass.name)

        Handler().postDelayed({
            dialog.dialog?.setOnDismissListener {
                fetchUserData()
            }
        }, 1000)
    }

}