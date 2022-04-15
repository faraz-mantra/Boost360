package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.view.View
import com.appservice.utils.WebEngageController
import com.appservice.utils.capitalizeUtil
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep2Binding
import com.boost.presignin.extensions.validateLetters
import com.boost.presignin.model.category.CategoryDataModel
import com.framework.extensions.afterTextChanged
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml
import com.framework.utils.showKeyBoard
import com.framework.views.blur.setBlur
import com.framework.webengageconstant.*

class SetupMyWebsiteStep2Fragment : AppBaseFragment<LayoutSetUpMyWebsiteStep2Binding, BaseViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): SetupMyWebsiteStep2Fragment {
      val fragment = SetupMyWebsiteStep2Fragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  private val phoneNumber by lazy {
    arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
  }

  private val whatsappConsent by lazy {
    arguments?.getBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name)
  }

  private val categoryLiveName by lazy {
    arguments?.getString(IntentConstant.CATEGORY_SUGG_UI.name)
  }

  private val mobilePreview by lazy {
    arguments?.getString(IntentConstant.MOBILE_PREVIEW.name)
  }

  private val desktopPreview by lazy {
    arguments?.getString(IntentConstant.DESKTOP_PREVIEW.name)
  }

  private val categoryModel by lazy {
    arguments?.getSerializable(IntentConstant.CATEGORY_DATA.name) as? CategoryDataModel
  }

  override fun getLayout(): Int {
    return R.layout.layout_set_up_my_website_step_2
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(PS_BUSINESS_PROFILE_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.includeMobileView?.blurView?.setBlur(baseActivity, 1F)
    binding?.includeMobileView?.tvCategoryName?.text = categoryModel?.getCategoryWithoutNewLine() ?: ""
    setOnClickListeners()
  }

  private fun setOnClickListeners() {
    binding?.tvNextStep2?.setOnClickListener {
      if (binding?.businessNameInputLayout?.etInput?.text.toString().validateLetters()) {
        WebEngageController.trackEvent(PS_BUSINESS_PROFILE_CLICK_NEW_UPPERCASE, CLICK, NO_EVENT_VALUE)
        addFragment(
          R.id.inner_container, SetupMyWebsiteStep3Fragment.newInstance(
            Bundle().apply {
              putString(IntentConstant.DESKTOP_PREVIEW.name, desktopPreview)
              putString(IntentConstant.MOBILE_PREVIEW.name, mobilePreview)
              putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
              putString(IntentConstant.CATEGORY_SUGG_UI.name, categoryLiveName)
              putSerializable(IntentConstant.CATEGORY_DATA.name, categoryModel)
              putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent ?: false)
              putString(IntentConstant.EXTRA_BUSINESS_NAME.name, binding?.businessNameInputLayout?.etInput?.text.toString())
            }), true
        )
      } else showShortToast(getString(R.string.business_name_format_invalid_toast))
    }

    binding?.businessNameInputLayout?.etInput?.afterTextChanged {
      binding?.tvNextStep2?.isEnabled = it.isEmpty().not()
      binding?.includeMobileView?.tvTitle?.text = it.capitalizeUtil()
      binding?.businessNameInputLayout?.tvWordCount?.text = fromHtml("<font color=${if (it.isEmpty()) "#9DA4B2" else "#09121F"}>${it.length}</font><font color=#9DA4B2> /40</font>")
    }

//    binding?.businessNameInputLayout?.etInput?.setOnEditorActionListener { v, actionId, _ ->
//      if (actionId == EditorInfo.IME_ACTION_DONE) {
//        if (binding?.businessNameInputLayout?.etInput!!.text?.trim()?.isEmpty() == false) {
//          binding?.businessNameInputLayout?.etInput?.isEnabled = false
//          binding?.businessNameInputLayout?.tvWordCount?.gone()
//          binding?.businessNameInputLayout?.ivIcon?.visible()
//        }
//      }
//      false
//    }

    binding?.businessNameInputLayout?.etInput?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
      if (hasFocus) binding?.businessNameInputLayout?.inputLayout?.setBackgroundResource(R.drawable.bg_dark_stroke_et_onboard)
    }

//    binding?.businessNameInputLayout?.ivIcon?.setOnClickListener {
//      baseActivity.showKeyBoard(binding?.businessNameInputLayout?.etInput)
//      binding?.businessNameInputLayout?.etInput?.isEnabled = true
//      binding?.businessNameInputLayout?.tvWordCount?.visible()
//      binding?.businessNameInputLayout?.ivIcon?.gone()
//    }
  }

  override fun onResume() {
    super.onResume()
    binding?.businessNameInputLayout?.etInput?.run { baseActivity.showKeyBoard(this) }
  }

}