package com.boost.marketplace.ui.webview

import android.graphics.Bitmap
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import com.boost.marketplace.R
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.*
import kotlinx.android.synthetic.main.activity_website_view.*


class WebViewActivity : AppBaseActivity<ActivityWebsiteViewBinding, WebViewViewModel>(){

  var link: String? = null

  override fun getLayout(): Int {
    return R.layout.activity_website_view
  }

  override fun getViewModelClass(): Class<WebViewViewModel> {
    return WebViewViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    webview?.settings?.javaScriptEnabled = true
    webview?.settings?.loadWithOverviewMode = true
    webview?.settings?.useWideViewPort = true
    webview?.settings?.allowFileAccess = true
    webview?.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
    webview?.webChromeClient = WebChromeClient()
    val webSettings = webview?.settings
    webSettings?.javaScriptCanOpenWindowsAutomatically = true
    webSettings?.setSupportMultipleWindows(true)
    webSettings?.cacheMode = WebSettings.LOAD_DEFAULT
    webSettings?.domStorageEnabled = true

    link = intent.getStringExtra("link") ?: ""

    if (intent.extras != null && intent.extras!!.containsKey("title")) {
      browser_title.text = intent.extras!!.getString("title")
    }

    if (link != null) {
      webview.settings.javaScriptEnabled = true
      webview.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
          progress_bar?.visible()

          return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
          super.onPageStarted(view, url, favicon)
          progress_bar?.visible()

        }

        override fun onPageFinished(view: WebView?, url: String?) {
          super.onPageFinished(view, url)
          progress_bar.gone()
        }

        override fun onReceivedError(
          view: WebView?,
          request: WebResourceRequest?,
          error: WebResourceError?
        ) {
          progress_bar.gone()
          super.onReceivedError(view, request, error)
        }
      }
      webview.loadUrl(link!!)
    } else {
      Toast.makeText(applicationContext, "Link is Empty!!", Toast.LENGTH_LONG).show()
      finish()
    }

    web_addons_back.setOnClickListener {
      finish()
    }
  }

}
