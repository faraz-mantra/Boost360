package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep2Binding
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml


class SetupMyWebsiteStep2Fragment : AppBaseFragment<LayoutSetUpMyWebsiteStep2Binding, BaseViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): SetupMyWebsiteStep2Fragment {
      val fragment = SetupMyWebsiteStep2Fragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  private val whatsappConsent by lazy {
    arguments?.getBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name)
  }


  private val phoneNumber by lazy {
    arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
  }

  override fun getLayout(): Int {
    return R.layout.layout_set_up_my_website_step_2
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()

    setOnClickListeners()
  }

  private fun setOnClickListeners() {
    binding?.tvNextStep2?.setOnClickListener {
      addFragment(
        R.id.inner_container, SetupMyWebsiteStep3Fragment.newInstance(
          Bundle().apply {
            putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent == true)

            putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
            putString(IntentConstant.EXTRA_BUSINESS_NAME.name, binding?.businessNameInputLayout?.etInput?.text.toString())
          }), true
      )
    }

    binding?.businessNameInputLayout?.etInput?.afterTextChanged {
      binding?.tvNextStep2?.isEnabled = it.isEmpty().not()
      binding?.includeMobileView?.tvTitle?.text = it
      binding?.businessNameInputLayout?.tvWordCount?.text = fromHtml("<font color=#09121F>${it.length}</font><font color=#9DA4B2> /40</font>")
    }

    binding?.businessNameInputLayout?.etInput?.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        binding?.businessNameInputLayout?.etInput?.isEnabled = false
        binding?.businessNameInputLayout?.tvWordCount?.gone()
        binding?.businessNameInputLayout?.ivIcon?.visible()
      }
      false
    }

    binding?.businessNameInputLayout?.ivIcon?.setOnClickListener {
      binding?.businessNameInputLayout?.etInput?.isEnabled = true
      binding?.businessNameInputLayout?.tvWordCount?.visible()
      binding?.businessNameInputLayout?.ivIcon?.gone()
    }
  }
}