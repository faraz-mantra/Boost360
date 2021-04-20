package com.boost.presignin.ui.mobileVerification

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.boost.presignin.R
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentOtpVerificationBinding
import com.boost.presignin.model.FPDetailsResponse
import com.boost.presignin.rest.userprofile.ResponseMobileIsRegistered
import com.boost.presignin.ui.AccountNotFoundActivity
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.boost.presignin.views.otptextview.OTPListener
import com.framework.base.BaseFragment
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import java.util.*

class OtpVerificationFragment : BaseFragment<FragmentOtpVerificationBinding, LoginSignUpViewModel>() {

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

    fun onCodeSent() {
        countDown = object : CountDownTimer(50 * 1000, 1000) {
            override fun onTick(p0: Long) {
                val resendIn = getString(R.string.psn_resend_in);
                binding?.resendTv?.text = String.format(resendIn, (p0 / 1000).toInt());

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
        val session = UserSessionManager(requireActivity())


    }


    fun verify() {
        val otp = binding?.pinTv?.otp ?: return

        //TEST ONLY
        if (otp == "12345") {
            binding?.wrongOtpErrorTv?.isVisible = true;
            return
        }
        viewModel?.checkMobileIsRegistered(phoneNumber?.toLong())?.observeOnce(viewLifecycleOwner, {
            val data = it as? ResponseMobileIsRegistered
            when (data?.result) {
                true -> {
                    //number is registered redirect to dashboard
                    //get fp details by phone number
                    viewModel?.getFpDetailsByPhone(phoneNumber?.toLong())?.observeOnce(viewLifecycleOwner, { response ->
                        if (response.isSuccess()) {
                            val data = response as? FPDetailsResponse
                            val userSessionManager = UserSessionManager(baseActivity)
                            userSessionManager.storeFPID(data?.id)
                            userSessionManager.storeFpTag(data?.tag)
                            userSessionManager.setUserLogin(true)
                            startDashboard()
                        } else {
                            showShortToast(getString(R.string.error_getting_fp_details))
                        }


                    })


                }
                else -> {
                    // number is not registered
                    navigator?.startActivity(AccountNotFoundActivity::class.java, args = Bundle().apply { putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber) })

                }
            }

        })
//        //startActivity(Intent(requireContext(), AccountNotFoundActivity::class.java))
//        navigator?

    }


    private fun startDashboard() {
        val dashBoardActivity: Class<*> = try {
            Class.forName("com.dashboard.controller.DashboardActivity")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            return
        }
        val dashboardIntent = Intent(requireContext(), dashBoardActivity)
        dashboardIntent.putExtras(requireActivity().intent)
        val bundle = Bundle()
        bundle.putParcelableArrayList("message", ArrayList())
        dashboardIntent.putExtras(bundle)
        dashboardIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(dashboardIntent)
        requireActivity().finish()
    }
}