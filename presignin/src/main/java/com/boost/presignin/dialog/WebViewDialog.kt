package com.boost.presignin.dialog

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
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
    binding.webview.settings?.javaScriptEnabled = true
    binding.webview.settings?.loadWithOverviewMode = true
    binding.webview.settings?.useWideViewPort = true
    binding.webview.settings?.allowFileAccess = true
    binding.webview.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
    binding.webview.webChromeClient = WebChromeClient()
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
