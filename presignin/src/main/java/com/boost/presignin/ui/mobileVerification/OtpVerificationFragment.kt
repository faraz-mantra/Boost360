package com.boost.presignin.ui.mobileVerification

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentOtpVerificationBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.ui.mobileVerification.fp.FragmentFpList
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.boost.presignin.views.otptextview.OTPListener
import com.framework.base.BaseResponse
import com.framework.extensions.observeOnce
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.framework.webengageconstant.*
import okio.Buffer
import okio.BufferedSource
import java.nio.charset.Charset

class OtpVerificationFragment : AppBaseFragment<FragmentOtpVerificationBinding, LoginSignUpViewModel>() {

  private val TAG = OtpVerificationFragment::class.java.canonicalName;

  private lateinit var countDown: com.boost.presignin.Timer.CountDownTimer;

  companion object {
    private const val PHONE_NUMBER = "phone_number"

    @JvmStatic
    fun newInstance(phoneNumber: String) =
        OtpVerificationFragment().apply {
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

  override fun onResume() {
    super.onResume()
    countDown.resume()
  }
  override fun onStop() {
    super.onStop()
    countDown.pause()
  }

  private fun onCodeSent() {
    countDown = object : com.boost.presignin.Timer.CountDownTimer(50 * 1000, 1000) {
      override fun onTick(p0: Long) {
        val resendIn = getString(R.string.psn_resend_in);
        binding?.resendTv?.text = String.format(resendIn, (p0 / 1000).toInt());

      }

      override fun onFinish() {
        val clickableSpan = object : ClickableSpan() {
          override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
            ds.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
            ds.linkColor = ContextCompat.getColor(requireContext(), R.color.colorAccent)
          }

          override fun onClick(widget: View) {
            //resend otp
            view?.invalidate()
            sendOtp(phoneNumber)
          }
        }

        val resendString = getString(R.string.psn_resend_hint)
        val resendQuery = getString(R.string.psn_resend)
        val spannable = SpannableString(resendString)
        val indexStart = resendString.indexOf(resendQuery);
        val indexEnd = resendString.length
        spannable.setSpan(clickableSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding?.resendTv?.setText(spannable, TextView.BufferType.SPANNABLE)
        binding?.resendTv?.movementMethod = LinkMovementMethod.getInstance()
      }
    }
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(BOOST_360_VERIFY_OTP, PAGE_VIEW, NO_EVENT_VALUE)
    onCodeSent()
    sendOtp(phoneNumber)
    binding?.subheading?.text = String.format(getString(R.string.code_sent_hint, phoneNumber))
    val backbutton = binding?.toolbar?.findViewById<ImageView>(R.id.back_iv)
    backbutton?.setOnClickListener {
      parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }
    binding?.pinTv?.otpListener = object : OTPListener {
      override fun onInteractionListener() {
        val otp = binding?.pinTv?.otp
        binding?.verifyButton?.isEnabled = otp != null && otp.length == 4
      }

      override fun onOTPComplete(otp: String) {

      }

    }
    binding?.verifyButton?.setOnClickListener { verify() }
  }

  private fun sendOtp(phoneNumber: String?) {
    showProgress(getString(R.string.sending_otp))
    viewModel?.sendOtpIndia(phoneNumber?.toLong(), clientId)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      if (it.isSuccess()) {
        if (parseResponse(it)) {
          binding?.wrongOtpErrorTv?.isVisible = false;
          countDown.start();
        } else showShortToast(getString(R.string.otp_not_sent))
      }
    })
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


  fun verify() {
    WebEngageController.trackEvent(BOOST_360_VERIFY_OTP, OTP_VERIFY_CLICK, NO_EVENT_VALUE)
    val otp = binding?.pinTv?.otp
    viewModel?.verifyOtp(number = phoneNumber, otp, clientId2)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        if (parseResponse(it)) {
          addFragmentReplace(com.framework.R.id.container, FragmentFpList.newInstance(phoneNumber.toString()), false)
        } else {
          binding?.wrongOtpErrorTv?.isVisible = true;
        }
      }
    })

  }
}