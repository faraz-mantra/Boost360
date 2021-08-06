package com.dashboard.controller.ui.website_theme.dialog


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.dashboard.R
import com.dashboard.databinding.DialogWebviewBinding
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.onboarding.nowfloats.utils.getWebViewUrl

class WebViewDialog : DialogFragment() {

  private lateinit var binding: DialogWebviewBinding
  private var domainUrl = ""
  private var title = ""

  fun setData(domainUrl: String, title: String) {
    this.domainUrl = domainUrl
    this.title = title
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_webview, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    loadData(domainUrl)
    if (title.isEmpty()) title = resources.getString(R.string.boost360_terms_conditions)
    binding.ctvBusinessName.text = title
    binding.ctvWebsiteUrl.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    binding.ctvWebsiteUrl.text = domainUrl
    binding.backBtn.setOnClickListener { dismiss() }
  }

  override fun getTheme(): Int {
    return R.style.MaterialDialogThemeFull
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun loadData(urlData: String) {
    binding.webview.settings?.javaScriptEnabled = true
    binding.webview.settings?.loadWithOverviewMode = true
    binding.webview.settings?.useWideViewPort = true
    binding.webview.settings?.allowFileAccess = true
    binding.webview.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
    binding.webview.webChromeClient = WebChromeClient()
    val webSettings = binding.webview.settings
    webSettings.javaScriptCanOpenWindowsAutomatically = true
    webSettings.setSupportMultipleWindows(true)
    webSettings.cacheMode = WebSettings.LOAD_DEFAULT
    webSettings.domStorageEnabled = true

    binding.webview.webChromeClient = object :WebChromeClient(){
      override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
        val result = view!!.hitTestResult
        val data = result.extra
        val context= view.context
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
        context.startActivity(browserIntent)
        return false
      }
    }
    binding.webview.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        binding.progressBar.visible()
        view.loadUrl(url)
        return false
      }

      override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        binding.progressBar.visible()
      }

      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
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
    binding.webview.loadUrl(urlData.getWebViewUrl())
  }
}

