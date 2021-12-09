package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep2Binding
import com.boost.presignin.model.category.CategoryDataModelOv2
import com.framework.extensions.afterTextChanged
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml
import com.framework.views.blur.setBlur

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
    arguments?.getSerializable(IntentConstant.CATEGORY_DATA.name) as? CategoryDataModelOv2
  }

  override fun getLayout(): Int {
    return R.layout.layout_set_up_my_website_step_2
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    binding?.includeMobileView?.blurView?.setBlur(baseActivity, 4F)
    binding?.includeMobileView?.tvCategoryName?.text = categoryModel?.category_Name ?: ""
    setOnClickListeners()
  }

  private fun setOnClickListeners() {
    binding?.tvNextStep2?.setOnClickListener {
      addFragment(
        R.id.inner_container, SetupMyWebsiteStep3Fragment.newInstance(
          Bundle().apply {
            putString(IntentConstant.DESKTOP_PREVIEW.name, desktopPreview)
            putString(IntentConstant.MOBILE_PREVIEW.name, mobilePreview)
            putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
            putString(IntentConstant.CATEGORY_SUGG_UI.name, categoryLiveName)
            putSerializable(IntentConstant.CATEGORY_DATA.name, categoryModel)
            putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent?:false)
            putString(IntentConstant.EXTRA_BUSINESS_NAME.name, binding?.businessNameInputLayout?.etInput?.text.toString())
          }), true
      )
    }

    binding?.businessNameInputLayout?.etInput?.afterTextChanged {
      binding?.tvNextStep2?.isEnabled = it.isEmpty().not()
      binding?.includeMobileView?.tvTitle?.text = it
      binding?.businessNameInputLayout?.tvWordCount?.text = fromHtml("<font color=${if (it.isEmpty()) "#9DA4B2" else "#09121F"}>${it.length}</font><font color=#9DA4B2> /40</font>")
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