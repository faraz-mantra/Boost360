package com.inventoryorder.ui.order

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.*
import androidx.core.app.ActivityCompat
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.inventoryorder.R
import com.inventoryorder.databinding.FragmentOrderInoiceBinding
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.utils.getWebViewUrl
import com.inventoryorder.utils.startDownloadUri

const val INVOICE_URL = "INVOICE_URL"

class OrderInvoiceFragment : BaseInventoryFragment<FragmentOrderInoiceBinding>() {

  private var domainUrl = ""

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): OrderInvoiceFragment {
      val fragment = OrderInvoiceFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    domainUrl = arguments?.getString(INVOICE_URL) ?: ""
    if (domainUrl.isEmpty()) {
      showShortToast("Invalid invoice url.")
      baseActivity.finish()
    }
    loadData(domainUrl)
    binding?.btnShare?.setOnClickListener { shareFileUrl() }
  }

  private fun shareFileUrl() {
    try {
      val waIntent = Intent(Intent.ACTION_SEND)
      waIntent.type = "text/plain"
      waIntent.putExtra(Intent.EXTRA_SUBJECT, "Order invoice")
      waIntent.putExtra(Intent.EXTRA_TEXT, domainUrl)
      baseActivity.startActivity(Intent.createChooser(waIntent, "Share invoice..."))
    } catch (e: Exception) {
      showLongToast("Error sharing invoice, please try again.")
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
        view.loadUrl(url)
        return false
      }

      override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
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

  private fun downloadUrl() {
    if (ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
        ActivityCompat.checkSelfPermission(baseActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
    } else baseActivity.startDownloadUri(domainUrl.trim())
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      100 -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          if (domainUrl.isNotEmpty()) baseActivity.startDownloadUri(domainUrl.trim())
        } else showShortToast("Permission denied to read your External storage")
        return
      }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_item_download -> {
        downloadUrl()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}


