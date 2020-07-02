package com.onboarding.nowfloats.ui.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogWebviewTncBinding

class WebViewTNCDialog : DialogFragment() {

  private lateinit var binding: DialogWebviewTncBinding
  private lateinit var viewModel: ViewModel

  private var domainUrl = ""

  companion object {
    const val DECLINE = "decline"
    const val ACCEPT = "accept"
  }

  var onClickType: (type: String) -> Unit = { }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_webview_tnc, container, false)
    viewModel = ViewModelProviders.of(this).get(BaseViewModel::class.java)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isCancelable = false
    loadData(domainUrl)
    binding.appBarLayout.toolbar.title = "Boost"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) binding.appBarLayout.appbar.elevation = 1F
    binding.appBarLayout.toolbar.getTitleTextView()?.let { it.setToolbarTitleGravity() }
    binding.appBarLayout.toolbar.setNavigationOnClickListener { dismiss() }
    activity?.let { ContextCompat.getColor(it, R.color.white) }?.let { binding.appBarLayout.toolbar.setBackgroundColor(it) }
    binding.decline.setOnClickListener {
      dismiss()
      onClickType(DECLINE)
    }
    binding.accept.setOnClickListener {
      dismiss()
      onClickType(ACCEPT)
    }
  }

  fun setUrl(domainUrl: String) {
    this.domainUrl = domainUrl
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
        binding.btnView.visible()
        binding.progressBar.gone()
      }

      override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
        binding.progressBar.gone()
      }
    }
    binding.webview.loadUrl(urlData.getUrl())
  }

  private fun TextView.setToolbarTitleGravity() {
    textAlignment = TextView.TEXT_ALIGNMENT_GRAVITY
    gravity = Gravity.CENTER_HORIZONTAL
    (layoutParams as? Toolbar.LayoutParams)?.width = Toolbar.LayoutParams.MATCH_PARENT
  }
}

