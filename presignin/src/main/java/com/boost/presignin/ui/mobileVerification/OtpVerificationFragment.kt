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
import androidx.fragment.app.FragmentManager
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentOtpVerificationBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.login.VerifyOtpResponse
import com.boost.presignin.ui.AccountNotFoundActivity
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.boost.presignin.views.otptextview.OTPListener
import com.framework.extensions.observeOnce
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.framework.webengageconstant.*

class OtpVerificationFragment : AppBaseFragment<FragmentOtpVerificationBinding, LoginSignUpViewModel>() {

  private val TAG = OtpVerificationFragment::class.java.canonicalName;
  private var isCounterRunning = false

  private lateinit var countDown: com.boost.presignin.timer.CountDownTimer

  companion object {
    private const val PHONE_NUMBER = "phone_number"

    @JvmStatic
    fun newInstance(phoneNumber: String) = OtpVerificationFragment().apply {
      arguments = Bundle().apply {
        putString(PHONE_NUMBER, phoneNumber)
      }
    }
  }

  private val phoneNumber by lazy { requireArguments().getString(PHONE_NUMBER) }

  override fun getLayout(): Int {
    return R.layout.fragment_otp_verification
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(PS_VERIFY_OTP_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.subheading?.text = String.format(getString(R.string.code_sent_hint, phoneNumber))
    val backButton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backButton?.setOnClickListener {
      parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
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
  }

  private fun onCodeSent() {
    countDown = object : com.boost.presignin.timer.CountDownTimer(50 * 1000, 1000) {
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

  override fun onResume() {
    super.onResume()
    if (this::countDown.isInitialized) countDown.resume()
  }

  override fun onStop() {
    super.onStop()
    if (this::countDown.isInitialized) countDown.pause()
  }

  private fun sendOtp(phoneNumber: String?) {
    showProgress(getString(R.string.sending_otp))
    WebEngageController.trackEvent(PS_VERIFY_OTP_RESEND, OTP_RESEND_CLICK, NO_EVENT_VALUE)
    viewModel?.sendOtpIndia(phoneNumber?.toLong(), clientId)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess() && it.parseResponse()) {
        binding?.wrongOtpErrorTv?.isVisible = false
        onCodeSent()
      } else showShortToast(getString(R.string.otp_not_sent))
      hideProgress()
    })
  }

  fun verify() {
    showProgress(getString(R.string.verify_otp))
    WebEngageController.trackEvent(PS_VERIFY_OTP_VERIFY, OTP_VERIFY_CLICK, NO_EVENT_VALUE)
    val otp = binding?.pinTv?.otp
    viewModel?.verifyLoginOtp(number = phoneNumber, otp, clientId2)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        val result = it as? VerifyOtpResponse
        if (result?.Result?.authTokens.isNullOrEmpty().not()) {
          addFragmentReplace(com.framework.R.id.container, FloatingPointAuthFragment.newInstance(result?.Result), false)
        } else {
          this.parentFragmentManager.popBackStack()
          navigator?.startActivity(AccountNotFoundActivity::class.java, args = Bundle().apply { putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber) })
        }
      } else {
        binding?.wrongOtpErrorTv?.isVisible = true;
      }
      hideProgress()
    })
  }
}