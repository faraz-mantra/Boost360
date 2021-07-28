package com.boost.presignin.ui.registration

import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentBusinessDetailsBinding
import com.boost.presignin.extensions.*
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.model.verification.RequestValidateEmail
import com.boost.presignin.model.verification.RequestValidatePhone
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.BaseResponse
import com.framework.extensions.observeOnce
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.framework.webengageconstant.*
import okio.Buffer
import okio.BufferedSource
import java.nio.charset.Charset

class BusinessDetailsFragment : AppBaseFragment<FragmentBusinessDetailsBinding, LoginSignUpViewModel>() {

  private var floatsRequest: CategoryFloatsRequest? = null

  companion object {
    @JvmStatic
    fun newInstance(request: CategoryFloatsRequest?) = BusinessDetailsFragment().apply {
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
    WebEngageController.trackEvent(PS_BUSINESS_PROFILE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    baseActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    floatsRequest = requireArguments().getSerializable("request") as? CategoryFloatsRequest
    binding?.phoneEt?.setText(floatsRequest?.userBusinessMobile)
    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener { goBack() }
    activity?.onBackPressedDispatcher?.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          parentFragmentManager.popBackStack()
        }
      })
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
        showShortToast(getString(R.string.enter_valid_name))
        return@setOnClickListener
      }
      if (!businessName.isBusinessNameValid()) {
        showShortToast(getString(R.string.enter_valid_business_name))
        return@setOnClickListener
      }
      if (!phone.isPhoneValid()) {
        showShortToast(getString(R.string.enter_valid_phone_number))
        return@setOnClickListener
      }
      val whatsAppNoFlag = binding?.checkbox?.isChecked ?: false

      floatsRequest?.requestProfile?.ProfileProperties?.userName = name
      floatsRequest?.userBusinessMobile = phone
      if (email.isNullOrEmpty().not()) {
        if (email.isEmailValid()) {
          floatsRequest?.requestProfile?.ProfileProperties?.userEmail = email
          floatsRequest?.userBusinessEmail = email
        } else {
          showLongToast(getString(R.string.email_invalid))
          return@setOnClickListener
        }
      } else {
        floatsRequest?.requestProfile?.ProfileProperties?.userEmail =
          "noemail-${floatsRequest?.requestProfile?.ProfileProperties?.userMobile}@noemail.com"
        floatsRequest?.userBusinessEmail = null
      }
      floatsRequest?.businessName = businessName
      floatsRequest?.requestProfile?.AuthToken = phone
      floatsRequest?.requestProfile?.ClientId = clientId
      floatsRequest?.requestProfile?.LoginKey = phone
      floatsRequest?.requestProfile?.LoginSecret = ""
      floatsRequest?.requestProfile?.Provider = "EMAIL"
      floatsRequest?.whatsAppFlag = whatsAppNoFlag
      validatePhone()
      WebEngageController.trackEvent(PS_BUSINESS_PROFILE_CLICK, CLICK, NO_EVENT_VALUE)
    }
  }

  private fun validatePhone() {
    showProgress()
    if (floatsRequest?.requestProfile?.ProfileProperties?.userMobile == floatsRequest?.userBusinessMobile) {
      hideProgress()
      validateEmail()
    } else {
      viewModel?.validateUsersPhone(
        RequestValidatePhone(
          clientId2,
          "+91",
          binding?.phoneEt?.text.toString()
        )
      )?.observeOnce(viewLifecycleOwner, {
        hideProgress()
        if (it.isSuccess()) {
          if (parseResponse(it)) {
            showShortToast(getString(R.string.this_number_is_already_in_use))
          } else {
            validateEmail()
          }
        }
      })
    }
  }

  private fun validateEmail() {
    showProgress()
    if (binding?.emailEt?.text.isNullOrEmpty()) {
      hideProgress()
      addFragmentReplace(
        com.framework.R.id.container,
        BusinessWebsiteFragment.newInstance(floatsRequest!!),
        true
      )
    } else {
      viewModel?.validateUsersEmail(
        RequestValidateEmail(
          clientId2,
          floatsRequest?.userBusinessEmail
        )
      )?.observeOnce(viewLifecycleOwner, {
        hideProgress()
        if (it.isSuccess()) {
          if (parseResponse(it)) {
            showShortToast(getString(R.string.this_email_is_already_in_use))
          } else {
            addFragmentReplace(
              com.framework.R.id.container,
              BusinessWebsiteFragment.newInstance(floatsRequest!!),
              true
            )
          }
        }
      })

    }
  }

  private fun parseResponse(it: BaseResponse): Boolean {
    return try {
      val source: BufferedSource? = it.responseBody?.source()
      source?.request(Long.MAX_VALUE)
      val buffer: Buffer? = source?.buffer
      val responseBodyString: String? = buffer?.clone()?.readString(Charset.forName("UTF-8"))
      responseBodyString.toBoolean()
    } catch (e: Exception) {
      false
    }
  }
}