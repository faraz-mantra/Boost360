package com.boost.presignin.ui.registration

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.webkit.*
import com.boost.presignin.R
import com.boost.presignin.databinding.WebPresignInBottomsheetBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.utils.getWebViewUrl

class WebPreSignInBottomDialog : BaseBottomSheetDialog<WebPresignInBottomsheetBinding, BaseViewModel>() {

  var onClicked: () -> Unit = { }

  private var domainUrl = ""

  override fun getLayout(): Int {
    return R.layout.web_presign_in_bottomsheet
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(domainUrl: String) {
    this.domainUrl = domainUrl
  }

  override fun onCreateView() {
    if (domainUrl.isEmpty()) dismiss()
    isCancelable = false
    binding?.goBackBtn?.setOnClickListener { dismiss() }
    loadData(domainUrl)
  }


  @SuppressLint("SetJavaScriptEnabled")
  private fun loadData(urlData: String) {
    binding?.webview?.settings?.javaScriptEnabled = true
    binding?.webview?.settings?.loadWithOverviewMode = true
    binding?.webview?.settings?.useWideViewPort = true
    binding?.webview?.settings?.allowFileAccess = true
    binding?.webview?.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
    binding?.webview?.webChromeClient = WebChromeClient()
    binding?.webview?.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        binding?.progressBar?.visible()
        view.loadUrl(url)
        return false
      }

      override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        binding?.progressBar?.visible()
      }

      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        binding?.agreeBtn?.setOnClickListener {
          dismiss()
          onClicked()
        }
        binding?.progressBar?.gone()
      }

      override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
      ) {
        super.onReceivedError(view, request, error)
        binding?.agreeBtn?.setOnClickListener { onClicked() }
        binding?.progressBar?.gone()
      }
    }
    binding?.webview?.loadUrl(urlData.getWebViewUrl())
  }

  override fun isPeekHeightSetMatch(): Boolean {
    return true
  }

  override fun isDraggable(): Boolean {
    return false
  }
}



