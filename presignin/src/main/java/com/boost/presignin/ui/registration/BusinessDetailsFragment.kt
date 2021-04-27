package com.boost.presignin.ui.registration

import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentBusinessDetailsBinding
import com.boost.presignin.extensions.isBusinessNameValid
import com.boost.presignin.extensions.isNameValid
import com.boost.presignin.extensions.isPhoneValid
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.BusinessInfoModel
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.BaseFragment
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.framework.webengageconstant.*

class BusinessDetailsFragment : BaseFragment<FragmentBusinessDetailsBinding, LoginSignUpViewModel>() {

  private var floatsRequest: CategoryFloatsRequest? = null

  companion object {
    @JvmStatic
    fun newInstance(request: CategoryFloatsRequest?) =
        BusinessDetailsFragment().apply {
          arguments = Bundle().apply {
            putSerializable("request", request)
          }
        }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_business_details
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  private fun goBack() {
    parentFragmentManager.popBackStackImmediate()
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(BUSINESS_PROFILE_INFO, PAGE_VIEW, NO_EVENT_VALUE)
    baseActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    floatsRequest = requireArguments().getSerializable("request") as? CategoryFloatsRequest
    binding?.phoneEt?.setText(floatsRequest?.userBusinessMobile)
    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener { goBack() }
    binding?.phoneEt?.setOnFocusChangeListener { v, hasFocus ->
      when (hasFocus) {
        true -> binding?.civPhone?.setTintColor(getColor(R.color.orange))
        else -> binding?.civPhone?.setTintColor(getColor(R.color.et_unselected_color))
      }
    }
    binding?.confirmButton?.setOnClickListener {
      val name = binding?.nametEt?.text?.toString()
      val businessName = binding?.businessNameEt?.text?.toString();
      val email = binding?.emailEt?.text?.toString()
      val phone = binding?.phoneEt?.text?.toString()
      if (!name.isNameValid()) {
        showShortToast("Enter valid name")
        return@setOnClickListener
      }
      if (!businessName.isBusinessNameValid()) {
        showShortToast("Enter valid business name")
        return@setOnClickListener
      }
      if (!phone.isPhoneValid()) {
        showShortToast("Enter valid phone number")
        return@setOnClickListener
      }
      val whatsAppNoFlag = binding?.checkbox?.isChecked ?: false

      floatsRequest?.requestProfile?.ProfileProperties?.userName = name
      floatsRequest?.userBusinessMobile = phone
      if (email.isNullOrEmpty().not()){
        floatsRequest?.requestProfile?.ProfileProperties?.userEmail = email
        floatsRequest?.userBusinessEmail = email
      }else{
        floatsRequest?.requestProfile?.ProfileProperties?.userEmail = "noemail-${floatsRequest?.requestProfile?.ProfileProperties?.userMobile}@noemail.com"
        floatsRequest?.userBusinessEmail = ""
      }
      floatsRequest?.businessName = businessName
      floatsRequest?.requestProfile?.AuthToken = phone
      floatsRequest?.requestProfile?.ClientId = clientId
      floatsRequest?.requestProfile?.LoginKey = phone
      floatsRequest?.requestProfile?.LoginSecret = ""
      floatsRequest?.requestProfile?.Provider = "EMAIL"
      floatsRequest?.whatsAppFlag = whatsAppNoFlag
      addFragmentReplace(com.framework.R.id.container, BusinessWebsiteFragment.newInstance(floatsRequest!!), true)
      WebEngageController.trackEvent(BUSINESS_PROFILE_INFO, CLICK, NO_EVENT_VALUE)
    }
  }
}