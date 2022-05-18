package com.boost.marketplace.ui.Invoice

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
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
    var manager: DownloadManager? = null


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
            webview.loadUrl("http://docs.google.com/gview?embedded=true&url="+link!!)
        } else {
            Toast.makeText(applicationContext, "Link is Empty!!", Toast.LENGTH_LONG).show()
            finish()
        }

        web_addons_back.setOnClickListener {
            finish()
        }

        binding?.download?.setOnClickListener {

            Toast.makeText(applicationContext, "Downloading", Toast.LENGTH_LONG).show()
            manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri: Uri = Uri.parse(link)
            val request: DownloadManager.Request = DownloadManager.Request(uri)
            val title:String=URLUtil.guessFileName(link,null,"application/pdf")
            request.setTitle(title)
            request.setDescription("Downloading Please wait")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            request.setMimeType("application/pdf")
            request.allowScanningByMediaScanner()
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            downloadManager.enqueue(request);
          //  downloadInvoice(link,"Invoice")
        }
        binding?.shareInvoice?.setOnClickListener {
            val pdfIntent = Intent(Intent.ACTION_SEND)
            pdfIntent.type = "application/pdf"
//          pdfIntent.putExtra(Intent.EXTRA_STREAM, link);
            pdfIntent.putExtra(Intent.EXTRA_TEXT, link)
            startActivity(Intent.createChooser(pdfIntent, "Share via"))
        }
    }
    fun downloadInvoice(url: String?, title: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        val tempTitle = title.replace("", "_")
        request.setTitle(tempTitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner()
            request.setDescription("Downloading Please wait")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$tempTitle.pdf")
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        request.setMimeType("application/pdf")
        request.allowScanningByMediaScanner()
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        downloadManager.enqueue(request)
    }

}