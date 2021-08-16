package com.boost.presignin.dialog

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.boost.presignin.R
import com.boost.presignin.databinding.DialogWebviewPresignupBinding
import com.framework.extensions.gone
import com.framework.extensions.visible


class WebViewDialog : DialogFragment() {

  private lateinit var binding: DialogWebviewPresignupBinding
  private var domainUrl = ""
  private var title = ""
  private var isAcceptDeclineShow = false

  companion object {
    const val DECLINE = "decline"
    const val ACCEPT = "accept"
  }

  var onClickType: (type: String) -> Unit = { }


  fun setData(isAcceptDeclineShow: Boolean, domainUrl: String, title: String) {
    this.isAcceptDeclineShow = isAcceptDeclineShow
    this.domainUrl = domainUrl
    this.title = title
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_webview_presignup, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = false
    loadData(domainUrl)
    if (title.isEmpty()) title = resources.getString(R.string.boost360_terms_conditions)
    binding.title.text = title
    binding.backBtn.setOnClickListener { dismiss() }
  }

  override fun getTheme(): Int {
    return R.style.FullScreenDialog
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun loadData(urlData: String) {
    binding.webview.settings.javaScriptEnabled = true
    binding.webview.settings.loadWithOverviewMode = true
    binding.webview.settings.useWideViewPort = true
    binding.webview.settings.allowFileAccess = true
    binding.webview.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
    binding.webview.webChromeClient = WebChromeClient()
    val webSettings = binding.webview.settings
    webSettings.javaScriptCanOpenWindowsAutomatically = true
    webSettings.setSupportMultipleWindows(true)
    webSettings.cacheMode = WebSettings.LOAD_DEFAULT
    webSettings.domStorageEnabled = true

    binding.webview.webChromeClient = object : WebChromeClient() {
      override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
        val result = view!!.hitTestResult
        val data = result.extra
        val context = view.context
        if (data != null) {
          val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
          context.startActivity(browserIntent)
        }
        return false
      }
    }
    binding.webview.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        binding.progressBar.visible()
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
        binding.progressBar.visible()
      }

      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        binding.btnView.visibility = if (isAcceptDeclineShow) View.VISIBLE else View.GONE
        binding.progressBar.gone()
      }

      override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
      ) {
        super.onReceivedError(view, request, error)
        binding.progressBar.gone()
      }
    }
    binding.webview.loadUrl(urlData.checkHttp())
  }
}
