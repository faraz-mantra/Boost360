package com.boost.presignin.ui.mobileVerification

import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.boost.presignin.R
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentOtpVerificationBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.login.VerificationRequestResult
import com.boost.presignin.model.login.VerifyOtpResponse
import com.boost.presignin.ui.AccountNotFoundActivity
import com.boost.presignin.ui.registration.RegistrationActivity
import com.boost.presignin.views.otptextview.OTPListener
import com.framework.base.FRAGMENT_TYPE
import com.framework.extensions.observeOnce
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.framework.smsVerification.SMSReceiver
import com.framework.smsVerification.SmsManager
import com.framework.webengageconstant.*

class OtpVerificationFragment : AuthBaseFragment<FragmentOtpVerificationBinding>(), SMSReceiver.OTPReceiveListener {

  private val TAG = OtpVerificationFragment::class.java.canonicalName;
  private var isCounterRunning = false

  private lateinit var countDown: com.boost.presignin.timer.CountDownTimer

  private var resultLogin: VerificationRequestResult? = null


  companion object {

    @JvmStatic
    fun newInstance(bundle: Bundle?) = OtpVerificationFragment().apply {
      arguments = bundle
    }
  }

  private val phoneNumber by lazy {
    arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
  }

  override fun getLayout(): Int {
    return R.layout.fragment_otp_verification
  }


  override fun resultLogin(): VerificationRequestResult? {
    return resultLogin
  }

  override fun authTokenData(): AuthTokenDataItem? {
    return if (resultLogin()?.authTokens.isNullOrEmpty().not()) resultLogin()?.authTokens!![0] else null
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(PS_VERIFY_OTP_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.subheading?.text = String.format(getString(R.string.code_sent_hint, phoneNumber))
    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener {
      baseActivity.onNavPressed()
    }
    binding?.pinTv?.otpListener = object : OTPListener {
      override fun onInteractionListener() {
        val otp = binding?.pinTv?.otp
        binding?.verifyButton?.isEnabled = otp != null && otp.length == 4
      }

      override fun onOTPComplete(otp: String) {
        verify()
      }
    }
    binding?.verifyButton?.setOnClickListener { verify() }
    Handler().postDelayed({ onCodeSent() }, 500)


    // init SMS Manager
    SmsManager.initManager(activity?.baseContext!!, this);
  }

  override fun onResume() {
    super.onResume()
    if (this::countDown.isInitialized) countDown.resume()
    SmsManager.register()
  }

  override fun onPause() {
    super.onPause()
    SmsManager.unregister()
  }


  private fun onCodeSent() {
    countDown = object : com.boost.presignin.timer.CountDownTimer(30 * 1000, 1000) {
      override fun onTick(p0: Long) {
        val resendIn = getString(R.string.psn_resend_in);
        binding?.resendTv?.text = String.format(resendIn, (p0 / 1000).toInt());
      }

      override fun onFinish() {
        isCounterRunning = false
        val clickableSpan = object : ClickableSpan() {
          override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
            ds.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
            ds.linkColor = ContextCompat.getColor(requireContext(), R.color.colorAccent)
          }

          override fun onClick(widget: View) {
            view?.invalidate()
            binding?.pinTv?.setOTP("")
            sendOtp(phoneNumber)
          }
        }

        val resendString = getString(R.string.psn_resend_hint)
        val resendQuery = getString(R.string.psn_resend)
        val spannable = SpannableString(resendString)
        val indexStart = resendString.indexOf(resendQuery)
        val indexEnd = resendString.length
        spannable.setSpan(clickableSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding?.resendTv?.setText(spannable, TextView.BufferType.SPANNABLE)
        binding?.resendTv?.movementMethod = LinkMovementMethod.getInstance()
      }
    }
    startOperation()
  }

  private fun startOperation() {
    if (!isCounterRunning) {
      isCounterRunning = true
      countDown.start()
    } else {
      countDown.cancel() // cancel
      countDown.start() // then restart
    }
  }

  override fun onStop() {
    super.onStop()
    if (this::countDown.isInitialized) countDown.pause()
  }

  private fun sendOtp(phoneNumber: String?) {
    showProgress(getString(R.string.sending_otp))
    WebEngageController.trackEvent(PS_VERIFY_OTP_RESEND, OTP_RESEND_CLICK, NO_EVENT_VALUE)
    viewModel?.sendOtpIndia(phoneNumber?.toLong(), clientId)?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess() && it.parseResponse()) {
        binding?.wrongOtpErrorTv?.isVisible = false
        onCodeSent()
      } else showShortToast(getString(R.string.otp_not_sent))
      hideProgress()
    }
  }

  fun verify() {
    showProgress(getString(R.string.verify_otp))
    WebEngageController.trackEvent(PS_VERIFY_OTP_VERIFY, OTP_VERIFY_CLICK, NO_EVENT_VALUE)
    val otp = binding?.pinTv?.otp
    viewModel?.verifyLoginOtp(number = phoneNumber, otp, clientId2)?.observeOnce(viewLifecycleOwner) {
      hideProgress()
      if (it.isSuccess()) {
        val result = it as? VerifyOtpResponse
        if (result?.Result?.authTokens.isNullOrEmpty().not() && result?.Result?.authTokens?.size!! >= 1) {
          if (result.Result?.authTokens?.size == 1) {
            this.resultLogin = result.Result
            authTokenData()?.createAccessTokenAuth()
          } else {
            navigator?.startActivityFinish(MobileVerificationActivity::class.java,
              Bundle().apply {
                putInt(FRAGMENT_TYPE, FP_LIST_FRAGMENT);putSerializable(IntentConstant.EXTRA_FP_LIST_AUTH.name, result.Result)
              })
          }
        } else {
          navigator?.startActivityFinish(RegistrationActivity::class.java,
            args = Bundle().apply { putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber) }
          )
        }
      } else {
        binding?.wrongOtpErrorTv?.isVisible = true;
      }
    }
  }

  override fun onOTPReceived(otp: String?) {
    binding?.pinTv?.post { binding?.pinTv?.setOTP(otp ?: "") }
  }
}