package com.boost.presignin.ui.mobileVerification

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentMobileBinding
import com.boost.presignin.extensions.isPhoneValid
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.ui.intro.IntroActivity
import com.boost.presignin.ui.login.LoginActivity
import com.boost.presignin.ui.registration.RegistrationActivity
import com.boost.presignin.ui.registration.SUCCESS_FRAGMENT
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.FRAGMENT_TYPE
import com.framework.extensions.observeOnce
import com.framework.extensions.onTextChanged
import com.framework.pref.clientId
import com.framework.utils.hideKeyBoard
import com.framework.utils.showKeyBoard
import com.framework.webengageconstant.*

class MobileFragment : AppBaseFragment<FragmentMobileBinding, LoginSignUpViewModel>() {


  companion object {
    private val PHONE_NUMBER = "phone_number"

    @JvmStatic
    fun newInstance() = MobileFragment().apply {}
    fun newInstance(phoneNumber: String) = MobileFragment().apply {
      arguments = Bundle().apply {
        putString(PHONE_NUMBER, phoneNumber)
      }
    }
  }

  private val phoneNumber by lazy { requireArguments().getString(PHONE_NUMBER) }

  override fun getLayout(): Int {
    return R.layout.fragment_mobile
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(PS_LOGIN_NUMBER_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.phoneEt?.onTextChanged { binding?.nextButton?.isEnabled = (it.isPhoneValid()) }
    baseActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        goBack()
      }
    })

    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener { goBack() }

    binding?.nextButton?.setOnClickListener {
      WebEngageController.trackEvent(PS_LOGIN_NUMBER_CLICK, NEXT_CLICK, NO_EVENT_VALUE)
      baseActivity.hideKeyBoard()
      sendOtp(binding?.phoneEt?.text.toString())
    }
    binding?.loginUsername?.setOnClickListener {
      WebEngageController.trackEvent(PS_LOGIN_USERNAME_CLICK, CLICK_LOGIN_USERNAME, NO_EVENT_VALUE)
      navigator?.startActivity(LoginActivity::class.java)
    }
  }


  private fun goBack() {
    baseActivity.finish()
  }

  private fun sendOtp(phoneNumber: String?) {
    showProgress(getString(R.string.sending_otp))
    viewModel?.sendOtpIndia(phoneNumber?.toLong(), clientId)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess() && it.parseResponse()) {
        navigator?.startActivity(MobileVerificationActivity::class.java, Bundle().apply {
          putInt(FRAGMENT_TYPE, OTP_FRAGMENT) ;putString(IntentConstant.EXTRA_PHONE_NUMBER.name,binding?.phoneEt?.text?.toString())
        })
      } else showShortToast(getString(R.string.otp_not_sent))
      hideProgress()
    })
  }
}