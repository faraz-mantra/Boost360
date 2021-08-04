package com.boost.presignin.ui.registration

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.view.View
import android.webkit.*
import com.boost.presignin.R
import com.boost.presignin.databinding.WebPresignInBottomsheetBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.utils.getWebViewUrl

class WebPreSignInBottomDialog :
  BaseBottomSheetDialog<WebPresignInBottomsheetBinding, BaseViewModel>() {

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
    val webSettings = binding?.webview?.settings
    webSettings?.javaScriptCanOpenWindowsAutomatically = true
    webSettings?.setSupportMultipleWindows(true)
    webSettings?.cacheMode = WebSettings.LOAD_DEFAULT
    webSettings?.domStorageEnabled = true

    binding?.webview?.webChromeClient = object :WebChromeClient(){
      override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
        val result = view!!.hitTestResult
        val data = result.extra
        val context= view.context
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
        context.startActivity(browserIntent)
        return false
      }
    }
    binding?.webview?.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        binding?.progressBar?.visible()
        return if (
          url.startsWith("mailto:") || url.startsWith("tel:") || url.startsWith("geo:")
          || url.startsWith("whatsapp:") || url.startsWith("spotify:")
        ) {
          try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
          } catch (e: Exception) {
            e.printStackTrace()
            view.loadUrl(url)
            false
          }
          true
        } else {
          view.loadUrl(url)
          false
        }
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



