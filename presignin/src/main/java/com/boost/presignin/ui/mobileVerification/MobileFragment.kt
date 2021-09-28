package com.boost.presignin.ui.mobileVerification

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentMobileBinding
import com.boost.presignin.extensions.isPhoneValid
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.ui.login.LoginActivity
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.base.FRAGMENT_TYPE
import com.framework.extensions.observeOnce
import com.framework.extensions.onTextChanged
import com.framework.pref.clientId
import com.framework.utils.hideKeyBoard
import com.framework.utils.showKeyBoard
import com.framework.webengageconstant.*
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest


//AppSignatureHash:- (Debug: m1jzE0DG9Z5) (Release: W5izmPg6WcR)

class MobileFragment : AppBaseFragment<FragmentMobileBinding, LoginSignUpViewModel>() {

  val NUMBER_PICKER_RC=100
  private val TAG = "MobileFragment"
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

  override fun onResume() {
    super.onResume()
    Handler().postDelayed({ baseActivity.showKeyBoard(binding?.phoneEt) }, 300)
  }

  override fun onCreateView() {
    requestPhonePicker()
    WebEngageController.trackEvent(PS_LOGIN_NUMBER_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(binding?.helpTv)
    binding?.phoneEt?.onTextChanged { binding?.nextButton?.isEnabled = (it.isPhoneValid()) }
    baseActivity.onBackPressedDispatcher.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          binding?.phoneEt?.clearFocus()
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
    binding?.helpTv?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    binding?.helpTv?.text = getString(R.string.need_help_u)
    val constraint = binding?.root as? ConstraintLayout
    binding?.phoneEt?.setOnFocusChangeListener { v, hasFocus ->
      if (hasFocus) {
        binding?.divider?.setBackgroundColor(getColor(R.color.black_4a4a4a))
      } else {
        binding?.divider?.setBackgroundColor(getColor(R.color.pinkish_grey))
      }

    }
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.helpTv -> needHelp()
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
          putInt(FRAGMENT_TYPE, OTP_FRAGMENT);putString(
          IntentConstant.EXTRA_PHONE_NUMBER.name,
          binding?.phoneEt?.text?.toString()
        )
        })
      } else showShortToast(getString(R.string.otp_not_sent))
      hideProgress()
    })
  }

  private fun requestPhonePicker() {
    val hintRequest = HintRequest.Builder()
      .setPhoneNumberIdentifierSupported(true)
      .build()
    val credentialsClient = Credentials.getClient(requireActivity())
    val intent = credentialsClient.getHintPickerIntent(hintRequest)
    try {
      startIntentSenderForResult(intent.intentSender, NUMBER_PICKER_RC, null, 0, 0, 0,null)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode === NUMBER_PICKER_RC) {
      if (resultCode === RESULT_OK) {
        val cred = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
        Log.i(TAG, "onActivityResult: "+cred?.id)
        binding?.phoneEt?.setText(cred?.id.toString().replace("+91",""))
        val isPhoneValid = binding?.phoneEt?.text.toString().isPhoneValid()
        binding?.nextButton?.isEnabled = isPhoneValid

        if (isPhoneValid){
          binding?.nextButton?.performClick()
        }
      }
    }
  }
}
