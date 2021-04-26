package com.boost.presignin.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.webkit.*
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseActivity
import com.boost.presignin.databinding.ActivityWebPreviewBinding
import com.boost.presignin.model.RequestFloatsModel
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel

class WebPreviewActivity : AppBaseActivity<ActivityWebPreviewBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.activity_web_preview
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        val requestFloatsModel = intent.extras?.getSerializable("request") as? RequestFloatsModel
        binding?.headingTv?.text = requestFloatsModel?.categoryDataModel?.category_Name
        binding?.ctvUrl?.text = requestFloatsModel?.webSiteUrl
        binding?.closeIcon?.setOnClickListener { finish() }
        loadData(requestFloatsModel?.webSiteUrl!!)

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
        binding?.webview?.loadUrl(urlData.checkHttp())
    }
}


fun String.checkHttp(): String {
    return if ((this.startsWith("http://") || this.startsWith("https://")).not()) "http://$this" else this
}