package com.boost.marketplace.ui.Invoice

import android.graphics.Bitmap
import android.view.View
import android.webkit.*
import android.widget.Toast
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityInvoiceBinding
import com.framework.extensions.gone
import com.framework.extensions.visible
import kotlinx.android.synthetic.main.activity_website_view.*

class InvoiceActivity : AppBaseActivity<ActivityInvoiceBinding, InvoiceViewModel>() {

    var link: String? = null


    override fun getLayout(): Int {
        return R.layout.activity_invoice
    }

    override fun getViewModelClass(): Class<InvoiceViewModel> {
      return InvoiceViewModel::class.java
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
      //  link ="https://www.getboost360.com/"

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

        binding?.download?.setOnClickListener {
            Toast.makeText(this,"ComingSoon",Toast.LENGTH_LONG).show()
        }
    }

}