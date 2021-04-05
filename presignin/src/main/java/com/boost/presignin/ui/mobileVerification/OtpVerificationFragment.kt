package com.boost.presignin.ui.mobileVerification

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentOtpVerificationBinding
import com.boost.presignin.ui.AccountNotFoundActivity
import com.boost.presignin.views.otptextview.OTPListener
import com.framework.base.BaseFragment
import com.framework.models.BaseViewModel
import com.framework.utils.hideKeyBoard

class OtpVerificationFragment : BaseFragment<FragmentOtpVerificationBinding, BaseViewModel>() {

    private val TAG = OtpVerificationFragment::class.java.canonicalName;

    private lateinit var countDown : CountDownTimer;

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

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }


    override fun onStop() {
        super.onStop()
        countDown.cancel();
    }

    fun onCodeSent(){
        countDown = object : CountDownTimer(50 * 1000, 1000) {
            override fun onTick(p0: Long) {
                val resendIn = getString(R.string.psn_resend_in);
                binding?.resendTv?.text = String.format(resendIn, (p0/1000).toInt());

            }

            override fun onFinish() {
                val termsSpan = object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        showShortToast("RESENT")
                        onCodeSent()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = true
                        ds.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                        ds.linkColor = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                    }
                }

                val resendString = getString(R.string.psn_resend_hint)
                val resendQuery = getString(R.string.psn_resend)
                val spannable = SpannableString(resendString)


                val indexStart = resendString.indexOf(resendQuery);
                val indexEnd = indexStart + resendQuery.length
                spannable.setSpan(termsSpan, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                binding?.resendTv?.setText(spannable, TextView.BufferType.SPANNABLE)
            }

        }
        countDown.start();

    }

    override fun onCreateView() {
        binding?.subheading?.text = String.format(getString(R.string.code_sent_hint, phoneNumber))
        binding?.pinTv?.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                val otp = binding?.pinTv?.otp
                binding?.verifyButton?.isEnabled = otp != null && otp.length == 5
            }

            override fun onOTPComplete(otp: String) {

            }

        }
        binding?.verifyButton?.setOnClickListener {
           verify()
        }

        onCodeSent();

    }


    fun verify(){
        val otp = binding?.pinTv?.otp?:return

        //TEST ONLY
        if(otp=="12345"){
            binding?.wrongOtpErrorTv?.isVisible = true;
            return
        }

        startActivity(Intent(requireContext(), AccountNotFoundActivity::class.java))

    }
}