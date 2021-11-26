package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentSetupMyWebsiteBinding
import com.boost.presignin.databinding.FragmentVerifyPhoneBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.login.VerificationRequestResult
import com.boost.presignin.model.login.VerifyOtpResponse
import com.boost.presignin.ui.mobileVerification.AuthBaseFragment
import com.boost.presignin.ui.mobileVerification.FP_LIST_FRAGMENT
import com.boost.presignin.ui.mobileVerification.MobileVerificationActivity
import com.boost.presignin.ui.newOnboarding.bottomSheet.HelpSupportSuccessBottomSheet
import com.boost.presignin.ui.newOnboarding.bottomSheet.NeedHelpBottomSheet
import com.boost.presignin.ui.registration.RegistrationActivity
import com.boost.presignin.views.otptextview.OTPListener
import com.framework.base.FRAGMENT_TYPE
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.framework.smsVerification.SMSReceiver
import com.framework.smsVerification.SmsManager
import com.framework.webengageconstant.*

class VerifyPhoneFragment : AuthBaseFragment<FragmentVerifyPhoneBinding>(), SMSReceiver.OTPReceiveListener {

    private var isCounterRunning = false

    private lateinit var countDown: com.boost.presignin.timer.CountDownTimer

    private var resultLogin: VerificationRequestResult? = null

    private val phoneNumber by lazy {
        arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
    }


    override fun resultLogin(): VerificationRequestResult? {
        return resultLogin
    }

    override fun authTokenData(): AuthTokenDataItem? {
        return if (resultLogin()?.authTokens.isNullOrEmpty()
                .not()
        ) resultLogin()?.authTokens!![0] else null
    }

    var isOtpVerified=false
    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): VerifyPhoneFragment {
            val fragment = VerifyPhoneFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_verify_phone
    }


    override fun onCreateView() {
        this.session = UserSessionManager(baseActivity)
        setOnListeners()
        initUI()
        setOnClickListener(binding?.tvVerifyOtp,binding?.tvResendOtpIn)
        WebEngageController.trackEvent(PS_VERIFY_OTP_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
        binding?.tvPhoneNumber?.text = phoneNumber.toString()


        Handler().postDelayed({ onCodeSent() }, 500)


        // init SMS Manager
        SmsManager.initManager(activity?.baseContext!!, this);
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.tvVerifyOtp->{
                verify()
            }
            binding?.tvResendOtpIn->{
                binding?.pinOtpVerify?.setOTP("")
                sendOtp(phoneNumber)
            }
        }
    }
    private fun initUI() {

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

    private fun setOnListeners() {
        binding?.pinOtpVerify?.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                val otp = binding?.pinOtpVerify?.otp
                binding?.tvVerifyOtp?.isEnabled = otp != null && otp.length == 4
                binding?.linearWhatsApp?.apply {
                    if (otp != null && otp.length == 4) visible() else gone()
                }
            }

            override fun onOTPComplete(otp: String) {
                verify()
                binding?.linearWhatsApp?.visible()
            }
        }


        binding?.chkWhatsapp?.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                WebEngageController.trackEvent(Ps_Login_WhatsApp_Consent_click, CLICK, NO_EVENT_VALUE)
            }else{
                WebEngageController.trackEvent(Ps_Login_WhatsApp_Consent_not_clicked,CLICK, NO_EVENT_VALUE)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_help_on_boarding_new, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_help_onboard -> {
                NeedHelpBottomSheet().show(parentFragmentManager,
                    NeedHelpBottomSheet::class.java.name)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onCodeSent() {
        binding?.tvResendOtpIn?.setTextColor(getColor(R.color.gray_DADADA))
        countDown = object : com.boost.presignin.timer.CountDownTimer(30 * 1000, 1000) {
            override fun onTick(p0: Long) {
                binding?.tvResendOtpIn?.isEnabled =false

                val resendIn = getString(R.string.psn_resend_in);
                binding?.tvResendOtpIn?.text = String.format(resendIn, (p0 / 1000).toInt());
            }

            override fun onFinish() {
                binding?.tvResendOtpIn?.isEnabled =true
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

                    }
                }
                val resendString = getString(R.string.psn_resend_hint)
                binding?.tvResendOtpIn?.text = resendString
                binding?.tvResendOtpIn?.setTextColor(getColor(R.color.colorAccent))
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
        viewModel?.sendOtpIndia(phoneNumber?.toLong(), clientId)?.observeOnce(viewLifecycleOwner, {
            if (it.isSuccess() && it.parseResponse()) {
                onCodeSent()
            } else showShortToast(getString(R.string.otp_not_sent))
            hideProgress()
        })
    }

    fun verify() {
        showProgress(getString(R.string.verify_otp))
        WebEngageController.trackEvent(PS_VERIFY_OTP_VERIFY, OTP_VERIFY_CLICK, NO_EVENT_VALUE)
        val otp = binding?.pinOtpVerify?.otp
        if (isOtpVerified){
            apiWhatsappOptin()
        }else{
            viewModel?.verifyLoginOtp(number = phoneNumber, otp, clientId2)
                ?.observeOnce(viewLifecycleOwner, {
                    hideProgress()
                    if (it.isSuccess()) {
                        val result = it as? VerifyOtpResponse
                        isOtpVerified = true
                        if (result?.Result?.authTokens.isNullOrEmpty().not()) {
                            if (result?.Result?.authTokens!!.size == 1) {
                                this.resultLogin = result.Result
                                authTokenData()?.createAccessTokenAuth()
                            } else {
                                binding?.linearWhatsApp?.visible()
                            }
                        } else {
                            binding?.linearWhatsApp?.visible()

                        }
                    } else {
                        showLongToast(getString(R.string.wrong_otp_tv))
                    }

                })
        }

    }

    private fun apiWhatsappOptin() {
        viewModel?.whatsappOptIn(0,phoneNumber!!,session.userProfileId!!)?.observe(viewLifecycleOwner,{

            if (it.isSuccess()){
                startFragmentFromNewOnBoardingActivity(
                    activity = requireActivity(),
                    type = com.boost.presignin.constant.FragmentType.WELCOME_FRAGMENT,
                    bundle = Bundle().apply {
                        putString(IntentConstant.EXTRA_PHONE_NUMBER.name,phoneNumber)
                        putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name,
                            binding?.chkWhatsapp?.isChecked == true
                        )

                    },
                    clearTop = true
                )
            }
        })

    }

    override fun onOTPReceived(otp: String?) {
        binding?.pinOtpVerify?.post { binding?.pinOtpVerify?.setOTP(otp ?: "") }
    }


}