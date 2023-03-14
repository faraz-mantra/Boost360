package com.boost.presignin.ui.newOnboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.appservice.base.AppBaseFragment
import com.boost.presignin.R
import com.boost.presignin.constant.FragmentType
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.FragmentEnterPhoneBinding
import com.boost.presignin.dialog.WebViewDialog
import com.boost.presignin.extensions.isPhoneValid
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.ui.login.LoginActivity
import com.boost.presignin.ui.newOnboarding.bottomSheet.NeedHelpBottomSheet
import com.boost.presignin.ui.newOnboarding.categoryService.startServiceCategory
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.analytics.SentryController
import com.framework.base.BaseActivity
import com.framework.extensions.afterTextChanged
import com.framework.extensions.observeOnce
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import com.framework.pref.clientId
import com.framework.utils.fromHtml
import com.framework.utils.hideKeyBoard
import com.framework.utils.makeLinks
import com.framework.webengageconstant.*
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest

class EnterPhoneFragment : AppBaseFragment<FragmentEnterPhoneBinding, LoginSignUpViewModel>() {

  private val TAG = "EnterPhoneFragment"
  private val NUMBER_PICKER_RC = 100
  var callIconItem: ImageView? = null
  var helpTextItem: TextView? = null
  var menuItem: MenuItem? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): EnterPhoneFragment {
      val fragment = EnterPhoneFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_enter_phone
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    baseActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    setOnListeners()
    requestPhonePicker()
    WebEngageController.trackEvent(PS_LOGIN_NUMBER_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListener(binding?.tvRequestOtp, binding?.tvLoginWithEmail)
    baseActivity.startServiceCategory()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.tvRequestOtp -> {
        WebEngageController.trackEvent(SIGNUP_INITIATED, NEXT_CLICK, NO_EVENT_VALUE)
        sendOtp(binding?.phoneEt?.text.toString())
      }
      binding?.tvLoginWithEmail -> {
        WebEngageController.trackEvent(PS_LOGIN_USERNAME_CLICK, CLICK_LOGIN_USERNAME, NO_EVENT_VALUE)
        navigator?.startActivity(LoginActivity::class.java)
      }
    }
  }


  private fun sendOtp(phoneNumber: String?) {
    startFragmentFromNewOnBoardingActivity(
      activity = baseActivity, type = FragmentType.VERIFY_PHONE_FRAGMENT,
      bundle = Bundle().apply { putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber) }
    )
//    WebEngageController.trackEvent(PS_LOGIN_NUMBER_CLICK, NEXT_CLICK, NO_EVENT_VALUE)
//    baseActivity.hideKeyBoard()
//    showProgress(getString(R.string.sending_otp))
//    viewModel?.sendOtpIndia(phoneNumber?.toLong(), clientId)?.observeOnce(viewLifecycleOwner) {
//      if (it.isSuccess() && it.parseResponse()) {
//        startFragmentFromNewOnBoardingActivity(
//          activity = baseActivity, type = FragmentType.VERIFY_PHONE_FRAGMENT,
//          bundle = Bundle().apply { putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber) }
//        )
//      } else showShortToast(if (it.message.isNullOrEmpty().not()) it.message else getString(R.string.otp_not_sent))
//      hideProgress()
//    }
  }


  private fun requestPhonePicker() {
    try {
      val hintRequest = HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()
      val credentialsClient = Credentials.getClient(requireActivity())
      val intent = credentialsClient.getHintPickerIntent(hintRequest)
      startIntentSenderForResult(intent.intentSender, NUMBER_PICKER_RC, null, 0, 0, 0, null)
    } catch (e: Exception) {
      SentryController.captureException(e)
      e.printStackTrace()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == NUMBER_PICKER_RC && resultCode == Activity.RESULT_OK) {
      val cred = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
      Log.i(TAG, "onActivityResult: " + cred?.id)
      binding?.phoneEt?.setText(cred?.id.toString().replace("+91", ""))
      val isPhoneValid = binding?.phoneEt?.text.toString().isPhoneValid()
      binding?.tvRequestOtp?.isEnabled = isPhoneValid
      if (isPhoneValid) binding?.tvRequestOtp?.performClick()
    }
  }

  private fun setOnListeners() {
    binding.phoneEt.afterTextChanged {
      if (it.length == 10){
        callIconItem?.setPadding(0,0,0,0)
        helpTextItem?.visibility = View.VISIBLE
      }else{
        callIconItem?.setPadding(0,0,66,0)
        helpTextItem?.visibility = View.GONE
      }
      binding.tvRequestOtp.isEnabled = it.isPhoneValid()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_help_setup_my_website, menu)
    menuItem = menu.findItem(R.id.help_new)
    callIconItem = menuItem?.actionView?.findViewById(R.id.helpCall) as ImageView
    helpTextItem = menuItem?.actionView?.findViewById(R.id.helpLabel) as TextView
    helpTextItem?.visibility = View.GONE
    menuItem?.actionView?.setOnClickListener { menu.performIdentifierAction(menuItem!!.itemId, 0) }
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
}