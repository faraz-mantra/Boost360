package com.onboarding.nowfloats.ui.webview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.*
import androidx.core.content.ContextCompat
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.InAppReviewUtils
import com.framework.views.customViews.CustomToolbar
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.databinding.ActivityWebViewNBinding
import com.onboarding.nowfloats.utils.getWebViewUrl

class WebViewActivity : AppBaseActivity<ActivityWebViewNBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.activity_web_view_n
  }

  companion object {
    val IK_ORDER_ID = "order_id"
    val IK_CLIENT_ID = "client_id"
    val IK_RC = 500
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    val domainUrl = intent?.extras?.getString(IntentConstant.DOMAIN_URL.name) ?: ""
    Log.i(TAG, "domainUrl: $domainUrl")
    if (domainUrl.isEmpty()) {
      showShortToast(getString(R.string.invalid_url))
      return
    }
    loadData(domainUrl.getWebViewUrl())
  }

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

    binding?.webview?.webChromeClient = object : WebChromeClient() {
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
    binding?.webview?.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        Log.i(TAG, "shouldOverrideUrlLoading: $url")
        handleOrderSuccess(url)
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

      override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
        binding?.progressBar?.gone()
      }
    }

    binding?.webview?.loadUrl(urlData)
  }

  private fun handleOrderSuccess(url: String) {
    if (url.contains("boost_order=success")) {
      val orderId = url.substring(url.indexOf("order_id=") + 9, url.indexOf("&", url.indexOf("order_id=")))
      val client_id = url.substring(url.indexOf("client_id=") + 10, url.indexOf("&", url.indexOf("client_id=")))
      val intent = Intent()
      intent.putExtra(IK_ORDER_ID, orderId)
      intent.putExtra(IK_CLIENT_ID, client_id)
      setResult(IK_RC, intent)
      finish()
    }
  }


  override fun getToolbarTitleSize(): Float? {
    return resources.getDimension(R.dimen.heading_7)
  }

  override fun getToolbarTitle(): String? {
    return resources.getString(R.string.app_name)
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }

  override fun getToolbarTitleColor(): Int? {
    return ContextCompat.getColor(this, R.color.black_4a4a4a)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    InAppReviewUtils.showInAppReview(this,InAppReviewUtils.Events.in_app_review_website_preview_cross)
    finish()
    return false
  }

  override fun onBackPressed() {
    val b = binding?.webview?.canGoBack()
    if (b != null && b) binding?.webview?.goBack() else super.onBackPressed()
  }
}



