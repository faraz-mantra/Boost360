package com.onboarding.nowfloats.ui.webview

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.webkit.*
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.WebViewBottomsheetBinding
import com.onboarding.nowfloats.utils.getWebViewUrl

class WebViewBottomDialog : BaseBottomSheetDialog<WebViewBottomsheetBinding, BaseViewModel>() {

  private var title = "Boost 360"
  private var domainUrl = ""

  override fun getLayout(): Int {
    return R.layout.web_view_bottomsheet
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(title: String, domainUrl: String) {
    this.title = title
    this.domainUrl = domainUrl
  }

  override fun onCreateView() {
    if (domainUrl.isEmpty()) {
      dismiss()
    }
    binding?.title?.text = title
    setToolbarSubTitle(domainUrl)
    binding?.close?.setOnClickListener { dismiss() }
    binding?.share?.setOnClickListener {
      shareDetail()
      dismiss()
    }
    loadData(domainUrl)
  }

  private fun shareDetail() {
    try {
      val shareIntent = Intent(Intent.ACTION_SEND)
      shareIntent.type = "text/plain"
      shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
      shareIntent.putExtra(Intent.EXTRA_TEXT, "$title\n\n$domainUrl")
      baseActivity.startActivity(
        Intent.createChooser(
          shareIntent,
          getString(R.string.share_connected_channel)
        )
      )
    } catch (e: Exception) {
      e.printStackTrace()
    }
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
        url?.let { setToolbarSubTitle(it) }
        binding?.progressBar?.visible()
      }

      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        binding?.progressBar?.gone()
      }

      override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
      ) {
        super.onReceivedError(view, request, error)
        binding?.progressBar?.gone()
      }
    }
    binding?.webview?.loadUrl(urlData.getWebViewUrl())
  }

  private fun setToolbarSubTitle(it: String) {
    binding?.website?.text = fromHtml("<u>${it}</u>")
  }

  override fun getMarginTop(): Int {
    return resources.getDimensionPixelSize(R.dimen.size_36)
  }

  override fun isPeekHeightSetMatch(): Boolean? {
    return true
  }

  override fun isDraggable(): Boolean? {
    return false
  }
}



