package com.boost.presignin.ui.mobileVerification

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentOtpVerificationBinding
import com.boost.presignin.ui.mobileVerification.fp.FragmentFpList
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.boost.presignin.views.otptextview.OTPListener
import com.framework.base.BaseResponse
import com.framework.extensions.observeOnce
import okio.Buffer
import okio.BufferedSource
import java.nio.charset.Charset

class OtpVerificationFragment : AppBaseFragment<FragmentOtpVerificationBinding, LoginSignUpViewModel>() {

    private val TAG = OtpVerificationFragment::class.java.canonicalName;

    private lateinit var countDown: CountDownTimer;

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


    override fun onStop() {
        super.onStop()
        countDown.cancel();
    }

    private fun onCodeSent() {
        countDown = object : CountDownTimer(50* 1000, 1000) {
            override fun onTick(p0: Long) {
                val resendIn = getString(R.string.psn_resend_in);
                binding?.resendTv?.text = String.format(resendIn, (p0 / 1000).toInt());

            }

            override fun onFinish() {
                val clickableSpan = object :ClickableSpan(){

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
        onCodeSent()
        sendOtp(phoneNumber)
        binding?.subheading?.text = String.format(getString(R.string.code_sent_hint, phoneNumber))
        binding?.pinTv?.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                val otp = binding?.pinTv?.otp
                binding?.verifyButton?.isEnabled = otp != null && otp.length == 4
            }

            override fun onOTPComplete(otp: String) {

            }

        }
        binding?.verifyButton?.setOnClickListener {
            verify()
        }
//        val session = UserSessionManager(requireActivity())


    }

    private fun sendOtp(phoneNumber: String?) {
        showProgress(getString(R.string.sending_otp))
        viewModel?.sendOtpIndia(phoneNumber?.toLong())?.observeOnce(viewLifecycleOwner, {
            hideProgress()
            if (it.status == 200) {
                if (parseResponse(it)) {
                    Toast.makeText(requireContext(), getString(R.string.otp_sent), Toast.LENGTH_SHORT).show()
                    countDown.start();
                } else {
                    showShortToast(getString(R.string.otp_not_sent))
                }
            }
        })
    }

    private fun parseResponse(it: BaseResponse): Boolean {
        try {
            val source: BufferedSource? = it.responseBody?.source()
            source?.request(Long.MAX_VALUE)
            val buffer: Buffer? = source?.buffer
            val responseBodyString: String? = buffer?.clone()?.readString(Charset.forName("UTF-8"))
            return responseBodyString.toBoolean()
        } catch (e: Exception) {
            return false
        }
    }


    fun verify() {
        val otp = binding?.pinTv?.otp
        viewModel?.verifyOtp(number = phoneNumber, otp)?.observeOnce(viewLifecycleOwner, {
            if (it.isSuccess()) {
                if (parseResponse(it)) {
                    addFragmentReplace(com.framework.R.id.container, FragmentFpList.newInstance(phoneNumber.toString()), true)
                }
                // get fp details
                else {
                    binding?.wrongOtpErrorTv?.isVisible = true;

                }
            }
        })


//        viewModel?.checkMobileIsRegistered(phoneNumber?.toLong())?.observeOnce(viewLifecycleOwner, {
//            val data = it as? ResponseMobileIsRegistered
//            when (data?.result) {
//                true -> {
//                    //number is registered redirect to dashboard
//                    //get fp details by phone number
//                    viewModel?.getFpDetailsByPhone(phoneNumber?.toLong())?.observeOnce(viewLifecycleOwner, { response ->
//                        if (response.isSuccess()) {
//                            val data = response as? FPDetailsResponse
//                            val userSessionManager = UserSessionManager(baseActivity)
//                            userSessionManager.storeFPID(data?.id)
//                            userSessionManager.storeFpTag(data?.tag)
//                            userSessionManager.setUserLogin(true)
//                            startDashboard()
//                        } else {
//                            showShortToast(getString(R.string.error_getting_fp_details))
//                        }
//
//
//                    })
//
//
//                }
//                else -> {
//                    // number is not registered
//                    navigator?.startActivity(AccountNotFoundActivity::class.java, args = Bundle().apply { putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber) })
//
//                }
//            }
//
//        })
//        //startActivity(Intent(requireContext(), AccountNotFoundActivity::class.java))
//        navigator?

    }


}