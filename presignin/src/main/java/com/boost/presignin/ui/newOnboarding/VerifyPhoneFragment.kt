package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.os.Handler
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.boost.presignin.BuildConfig
import com.boost.presignin.R
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentVerifyPhoneBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.login.VerificationRequestResult
import com.boost.presignin.model.login.VerifyOtpResponse
import com.boost.presignin.ui.mobileVerification.AuthBaseFragment
import com.boost.presignin.ui.mobileVerification.FP_LIST_FRAGMENT
import com.boost.presignin.ui.mobileVerification.MobileVerificationActivity
import com.boost.presignin.ui.newOnboarding.bottomSheet.NeedHelpBottomSheet
import com.boost.presignin.views.otptextview.OTPListener
import com.framework.base.BaseResponse
import com.framework.base.FRAGMENT_TYPE
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.clientId2
import com.framework.smsVerification.SMSReceiver
import com.framework.smsVerification.SmsManager
import com.framework.views.customViews.CustomTextView
import com.framework.webengageconstant.*
import java.util.*
import kotlin.concurrent.schedule

class VerifyPhoneFragment : AuthBaseFragment<FragmentVerifyPhoneBinding>(), SMSReceiver.OTPReceiveListener {

  private lateinit var countDown: com.boost.presignin.timer.CountDownTimer
  private var resultLogin: VerificationRequestResult? = null
  private var loginId: String? = null
  private var isCounterRunning = false
  private var isSuccessApi = false
  var callIconItem: MenuItem? = null
  var helpTextItem: MenuItem? = null
  private var resendCount = 0

  private val phoneNumber by lazy {
    arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
  }

  override fun resultLogin(): VerificationRequestResult? {
    return resultLogin
  }

  override fun authTokenData(): AuthTokenDataItem? {
    return resultLogin()?.authTokens?.firstOrNull()
  }

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
    baseActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    this.session = UserSessionManager(baseActivity)
    setOnListeners()
    setOnClickListener(binding?.tvVerifyOtp, binding?.tvResendOtpIn, binding?.tvPhoneNumber, binding?.tvGetOtpOnCall)
    WebEngageController.trackEvent(PS_VERIFY_OTP_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.tvPhoneNumber?.text = "+91 ${phoneNumber.toString()}"
    Handler().postDelayed({ onCodeSent() }, 500)
    SmsManager.initManager(baseActivity, this)
    if (!BuildConfig.FLAVOR.equals("partone") || !BuildConfig.FLAVOR.equals("jioonline")) {
      binding.tvVerifyOtp.backgroundTintList= ContextCompat.getColorStateList(context!!, R.color.disable_enable_button_selector)
      binding.chkWhatsapp.buttonTintList = ContextCompat.getColorStateList(context!!, R.color.buttonTint)
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.tvVerifyOtp -> verify()
      binding?.tvResendOtpIn -> {
        resendCount++
        val remainingCount = 5 - resendCount
        if (resendCount < 6) {
          if (remainingCount == 0) {
            showExhaustToast(v, "This was your last attempt to request OTP via SMS.")
          } else {
            showShortToast("You can resend the OTP $remainingCount times!")
          }
          WebEngageController.trackEvent(PS_LOGIN_OTP_RESENT_CLICK, CLICK, NO_EVENT_VALUE)
          binding?.pinOtpVerify?.setOTP("")
          sendOtp(phoneNumber)
        } else {
          showExhaustToast(
            v,
            "You have exceeded the number of limits. Please try again with new session"
          )
        }
      }
      binding?.tvPhoneNumber -> baseActivity.finish()
      binding?.tvGetOtpOnCall -> WebEngageController.trackEvent(PS_LOGIN_OTP_ON_CALL_CLICK, CLICK, NO_EVENT_VALUE)
    }
  }

  private fun showExhaustToast(v: View, label: String) {
    val inflater = layoutInflater
    val layout: View = inflater.inflate(
      R.layout.verify_phone_custom_toast,
      v.findViewById(R.layout.verify_phone_custom_toast) as ViewGroup?
    )
    val textLabel = layout.findViewById<TextView>(R.id.veryToastTv)
    textLabel.text = label
    val toast = Toast(v.context)
    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0)
    toast.setGravity(Gravity.BOTTOM, 0, 200)
    toast.duration = Toast.LENGTH_LONG
    toast.view = layout
    toast.show()
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
      }

      override fun onOTPComplete(otp: String) {
        verify()
      }
    }

    binding?.chkWhatsapp?.setOnCheckedChangeListener { _, b ->
      if (b) {
        WebEngageController.trackEvent(Ps_Login_WhatsApp_Consent_click, CLICK, NO_EVENT_VALUE)
      } else {
        WebEngageController.trackEvent(Ps_Login_WhatsApp_Consent_not_clicked, CLICK, NO_EVENT_VALUE)
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_help_setup_my_website, menu)
    val menuItem = menu.findItem(R.id.help_new)
    val menuLabel = menuItem?.actionView?.findViewById<CustomTextView>(R.id.help_label)
    if (!BuildConfig.FLAVOR.equals("partone") || !BuildConfig.FLAVOR.equals("jioonline")) {
      menuLabel?.setTextColor(ContextCompat.getColorStateList(context!!, R.color.buttonTint))
    }
    menuItem.actionView?.setOnClickListener {
      menu.performIdentifierAction(menuItem.itemId, 0)
    }

    val callIconItem = menuItem?.actionView?.findViewById(R.id.helpCall) as ImageView
    callIconItem.setPadding(0,0,0,0)
    menuItem.actionView?.setOnClickListener {
      menu.performIdentifierAction(menuItem.itemId, 0)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.help_new -> {
        WebEngageController.trackEvent(PS_LOGIN_OTP_NEED_HELP_CLICK, CLICK, NO_EVENT_VALUE)
        NeedHelpBottomSheet().show(parentFragmentManager, NeedHelpBottomSheet::class.java.name)
        return true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun onCodeSent() {
    binding?.tvResendOtpIn?.setTextColor(getColor(R.color.gray_DADADA))
    countDown = object : com.boost.presignin.timer.CountDownTimer(30 * 1000, 1000) {
      override fun onTick(p0: Long) {
        binding?.tvResendOtpIn?.isEnabled = false
        val resendIn = getString(R.string.psn_resend_in);
        binding?.tvResendOtpIn?.text = String.format(resendIn, (p0 / 1000).toInt());
      }

      override fun onFinish() {
        binding?.tvResendOtpIn?.isEnabled = true
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
        binding.tvResendOtpIn.setTextColor(getColor(R.color.colorAccent))
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
    viewModel?.sendOtpIndia(phoneNumber?.toLong(), clientId)?.observeOnce(viewLifecycleOwner) {
      if (it.isSuccess() && it.parseResponse()) {
        onCodeSent()
      } else showShortToast(if (it.message.isNullOrEmpty().not()) it.message else getString(R.string.otp_not_sent))
      hideProgress()
    }
  }

  fun verify() {
//    showBusinessWhatsapp()
    if (isSuccessApi) {
      if (this.resultLogin != null) apiWhatsappOptin() else moveToWelcomeScreen(phoneNumber)
    } else {
      showProgress(getString(R.string.verify_otp))
      WebEngageController.trackEvent(PS_VERIFY_OTP_VERIFY, OTP_VERIFY_CLICK, NO_EVENT_VALUE)
      val otp = binding?.pinOtpVerify?.otp
      var verifyOtp:LiveData<BaseResponse>? = null
      if (BuildConfig.FLAVOR.equals("partone") || BuildConfig.FLAVOR.equals("jioonline")) {
        verifyOtp = viewModel?.verifyLoginOtp(number = phoneNumber, otp, clientId)
      }else{
        verifyOtp = viewModel?.verifyLoginOtpVertical(number = phoneNumber, otp, clientId)
      }
      verifyOtp?.observeOnce(viewLifecycleOwner) {
        hideProgress()
        this.resultLogin = null
        if (it.isSuccess()) {
          val result = it as? VerifyOtpResponse
          binding?.tvResendOtpIn?.gone()
          if (result?.Result?.authTokens.isNullOrEmpty().not() && result?.Result?.authTokens?.size!! >= 1) {
            this.resultLogin = result.Result
            loginId = resultLogin?.loginId
            if (binding?.linearWhatsApp?.visibility == View.VISIBLE) apiWhatsappOptin() else showBusinessWhatsapp()
          } else {
            if (binding?.linearWhatsApp?.visibility == View.VISIBLE) moveToWelcomeScreen(phoneNumber) else showBusinessWhatsapp()
          }
        } else showLongToast(getString(R.string.wrong_otp_tv))
      }
    }
  }

  private fun showBusinessWhatsapp() {
    isSuccessApi = true
    binding?.acceptTncPhone.visibility = View.VISIBLE
    binding?.linearWhatsApp?.visible()
//    moveToWelcomeScreen(phoneNumber)
  }

  private fun apiWhatsappOptin() {
    if (binding?.chkWhatsapp?.isChecked == true) {
      showProgress()
      viewModel?.whatsappOptIn(0, phoneNumber, customerId = loginId)?.observeOnce(viewLifecycleOwner) {
        hideProgress()
        if (it.isSuccess()) loadNextPage() else showShortToast(it.message())
      }
    } else loadNextPage()
  }

  private fun loadNextPage() {
    if (resultLogin()?.authTokens?.size == 1) {
      authTokenData()?.createAccessTokenAuth()
    } else {
      navigator?.startActivityFinish(
        MobileVerificationActivity::class.java,
        Bundle().apply { putInt(FRAGMENT_TYPE, FP_LIST_FRAGMENT);putSerializable(IntentConstant.EXTRA_FP_LIST_AUTH.name, resultLogin()) })
    }
  }

  private fun moveToWelcomeScreen(enteredPhone: String?) {
    startFragmentFromNewOnBoardingActivity(
      activity = baseActivity, type = FragmentType.WELCOME_FRAGMENT,
      bundle = Bundle().apply {
        putString(IntentConstant.EXTRA_PHONE_NUMBER.name, enteredPhone)
        putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, binding?.chkWhatsapp?.isChecked ?: false)
      }
    )
  }

  override fun onOTPReceived(otp: String?) {
    binding?.pinOtpVerify?.post { binding?.pinOtpVerify?.setOTP(otp ?: "") }
  }
}