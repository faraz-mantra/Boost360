package com.appservice.ui.aptsetting.widgets

import android.content.Intent
import android.view.View
import com.appservice.R
import com.appservice.constant.Constants
import com.appservice.model.aptsetting.UserFpDetailsResponse
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetSuccessfullyPublishedBinding
import com.appservice.utils.WebEngageController
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.getDomainName
import com.framework.utils.fromHtml
import com.framework.webengageconstant.CLICK
import com.framework.webengageconstant.PRICING_PLAN_PAGE
import com.framework.webengageconstant.TO_BE_ADDED
import com.framework.webengageconstant.WEB_VIEW_PAGE
import com.onboarding.nowfloats.ui.webview.WebViewActivity

class BottomSheetSuccessfullyUpdated : BaseBottomSheetDialog<BottomSheetSuccessfullyPublishedBinding, BaseViewModel>() {

  private var catalogName: String? = null
  private var fpDetails: UserFpDetailsResponse? = null
  var onSuccessClicked: () -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_successfully_published
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    dialog.setCancelable(false)
    sessionManager = UserSessionManager(baseActivity)
    setOnClickListener(binding?.civCancel, binding?.visitWebsite)
    this.fpDetails = arguments?.getSerializable(IntentConstant.CATALOG_DATA.name) as? UserFpDetailsResponse
    this.catalogName = arguments?.getString(IntentConstant.CATALOG_CUSTOM_NAME.name)
    binding?.ctvLink?.text = fromHtml("<u>${sessionManager?.getDomainName()}/${Constants.CATALOG_PREFIX}$catalogName</u>")
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.civCancel -> {
        onSuccessClicked()
        dismiss()
      }
      binding?.visitWebsite -> {
        val url = "${sessionManager?.getDomainName()}/${Constants.CATALOG_PREFIX}$catalogName"
        WebEngageController.trackEvent(WEB_VIEW_PAGE, CLICK, url)
        val intent = Intent(baseActivity, WebViewActivity::class.java)
        intent.putExtra(com.onboarding.nowfloats.constant.IntentConstant.DOMAIN_URL.name, url)
        baseActivity.startActivity(intent)
      }
    }
  }
}