package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.view.View
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.LayoutBusinessCategoryPreviewBinding
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.makeSectionOfTextBold

class BusinessCategoryPreviewFragment : AppBaseFragment<LayoutBusinessCategoryPreviewBinding, CategoryVideoModel>() {

  private val TAG = "BusinessCategoryPreview"

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): BusinessCategoryPreviewFragment {
      val fragment = BusinessCategoryPreviewFragment()
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
    return R.layout.layout_business_category_preview
  }

  override fun getViewModelClass(): Class<CategoryVideoModel> {
    return CategoryVideoModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.tvNextStep, binding?.layoutMobile, binding?.layoutDesktop, binding?.autocompleteSearchCategory)
    setupUi()


  }

  private fun setupUi() {
    if (categoryLiveName.isNullOrEmpty().not()) {
      val totalString = categoryLiveName + "\nin " + categoryModel?.category_Name
      binding?.autocompleteSearchCategory?.text = makeSectionOfTextBold(totalString, categoryLiveName ?: "", font = R.font.regular_medium)
    } else {
      binding?.autocompleteSearchCategory?.text = makeSectionOfTextBold(categoryModel?.category_Name ?: "", categoryModel?.category_Name ?: "", font = R.font.regular_medium)
    }
    baseActivity.glideLoad(binding?.desktopPreview?.imgDesktop!!, desktopPreview ?: "", R.drawable.mobile_preview_website)
    baseActivity.glideLoad(binding?.mobilePreview?.imgMobile!!, mobilePreview ?: "", R.drawable.mobile_preview_website)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.tvNextStep -> {
        addFragment(R.id.inner_container, SetupMyWebsiteStep2Fragment.newInstance(Bundle().apply {
          putString(IntentConstant.DESKTOP_PREVIEW.name, desktopPreview)
          putString(IntentConstant.MOBILE_PREVIEW.name, mobilePreview)
          putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
          putString(IntentConstant.CATEGORY_SUGG_UI.name, categoryLiveName)
          putSerializable(IntentConstant.CATEGORY_DATA.name, categoryModel)
          putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent?:false)
        }), true)
      }
      binding?.layoutMobile -> {
        setUpButtonSelectedUI()
      }
      binding?.layoutDesktop -> {
        setUpButtonSelectedUI(false)
      }
      binding?.autocompleteSearchCategory -> {
        baseActivity.onBackPressed()
      }
    }
  }


  private fun setUpButtonSelectedUI(isMobilePreviewMode: Boolean = true) {
    if (isMobilePreviewMode) {
      binding?.layoutMobile?.setBackgroundResource(R.drawable.ic_presignin_bg_yellow_solid_stroke)
      binding?.layoutDesktop?.setBackgroundResource(0)
      binding?.titleMobile?.setTextColor(getColor(R.color.colorAccent))
      binding?.titleDesktop?.setTextColor(getColor(R.color.black_4a4a4a))
      binding?.ivMobile?.visible()
      binding?.ivDesktop?.gone()
      binding?.desktopPreview?.root?.gone()
      binding?.mobilePreview?.root?.visible()
    } else {
      binding?.layoutMobile?.setBackgroundResource(0)
      binding?.layoutDesktop?.setBackgroundResource(R.drawable.ic_presignin_bg_yellow_solid_stroke)
      binding?.titleMobile?.setTextColor(getColor(R.color.black_4a4a4a))
      binding?.titleDesktop?.setTextColor(getColor(R.color.colorAccent))
      binding?.ivMobile?.gone()
      binding?.ivDesktop?.visible()
      binding?.desktopPreview?.root?.visible()
      binding?.mobilePreview?.root?.gone()
    }
  }
}