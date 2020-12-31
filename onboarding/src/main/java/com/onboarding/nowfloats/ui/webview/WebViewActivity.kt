package com.onboarding.nowfloats.ui.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.webkit.*
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.databinding.ActivityWebViewNBinding
import com.onboarding.nowfloats.utils.checkHttp
import com.onboarding.nowfloats.utils.getWebViewUrl

class WebViewActivity : AppBaseActivity<ActivityWebViewNBinding, BaseViewModel>() {

  private var domainUrl = ""

  override fun getLayout(): Int {
    return R.layout.activity_web_view_n
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    domainUrl = intent?.extras?.getString(IntentConstant.DOMAIN_URL.name) ?: ""
    if (domainUrl.isEmpty()) {
      showShortToast("Invalid url.")
      return
    }
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
        url?.let { setToolbarSubTitle(it) }
        binding?.progressBar?.visible()
      }

      override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        binding?.progressBar?.gone()
      }

      override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
        binding?.progressBar?.gone()
      }
    }
    binding?.webview?.loadUrl(urlData.getWebViewUrl())
  }

  override fun getToolbarTitleSize(): Float? {
    return resources.getDimension(R.dimen.heading_7)
  }

  override fun getToolbarSubTitle(): String? {
    return domainUrl.checkHttp()
  }

  override fun getToolbarTitleGravity(): Int {
    return Gravity.NO_GRAVITY
  }

  override fun getToolbarTitle(): String? {
    return resources.getString(R.string.app_name)
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    finish()
    return false
  }

  override fun onBackPressed() {
    val b = binding?.webview?.canGoBack()
    if (b != null && b) binding?.webview?.goBack() else super.onBackPressed()
  }
}



