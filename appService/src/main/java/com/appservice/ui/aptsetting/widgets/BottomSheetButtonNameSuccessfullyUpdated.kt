package com.appservice.ui.aptsetting.widgets

import android.content.Intent
import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetButtonNameSuccessfullyPublishedBinding
import com.appservice.utils.WebEngageController
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.getDomainName
import com.framework.utils.fromHtml
import com.framework.webengageconstant.CLICK
import com.framework.webengageconstant.WEB_VIEW_PAGE
import com.onboarding.nowfloats.ui.webview.WebViewActivity

class BottomSheetButtonNameSuccessfullyUpdated : BaseBottomSheetDialog<BottomSheetButtonNameSuccessfullyPublishedBinding, BaseViewModel>() {

  var onSuccessClicked: () -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_button_name_successfully_published
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    dialog.setCancelable(false)
    sessionManager = UserSessionManager(baseActivity)
    setOnClickListener(binding?.civCancel, binding?.visitWebsite)
    binding?.ctvLink?.text = fromHtml("<u>${sessionManager?.getDomainName()}</u>")
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.civCancel -> {
        onSuccessClicked()
        dismiss()
      }
      binding?.visitWebsite -> {
        val url = "${sessionManager?.getDomainName()}"
        WebEngageController.trackEvent(WEB_VIEW_PAGE, CLICK, url)
        val intent = Intent(baseActivity, WebViewActivity::class.java)
        intent.putExtra(com.onboarding.nowfloats.constant.IntentConstant.DOMAIN_URL.name, url)
        baseActivity.startActivity(intent)
      }
    }
  }
}