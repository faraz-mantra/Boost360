package com.boost.cart.ui.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.boost.cart.R
import com.boost.cart.CartActivity
import com.boost.cart.base_class.BaseFragment
import com.framework.extensions.gone
import com.framework.extensions.visible
import kotlinx.android.synthetic.main.web_view_fragment.*


class WebViewFragment : BaseFragment() {

  lateinit var root: View

  var link: String? = null
  private val TAG = "WebViewFragment"
  companion object {
    fun newInstance() = WebViewFragment()
  }

  private lateinit var viewModel: WebViewViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.web_view_fragment, container, false)
    link = requireArguments().getString("link") ?: ""
    Log.i(TAG, "onCreateView: "+link)
    return root
  }

  @SuppressLint("SetJavaScriptEnabled")
  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(WebViewViewModel::class.java)
    val progressBar = root.findViewById<ProgressBar>(R.id.progress_bar)
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

    if (arguments != null && requireArguments().containsKey("title")) {
      browser_title.text = requireArguments().getString("title")
    }

    if (link != null) {
      webview.settings.javaScriptEnabled = true
      webview.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
          progressBar?.visible()

          return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
          super.onPageStarted(view, url, favicon)
          progressBar?.visible()

        }

        override fun onPageFinished(view: WebView?, url: String?) {
          super.onPageFinished(view, url)
          progressBar.gone()
        }

        override fun onReceivedError(
          view: WebView?,
          request: WebResourceRequest?,
          error: WebResourceError?
        ) {
          progressBar.gone()
          super.onReceivedError(view, request, error)
        }
      }
      webview.loadUrl(link!!)
    } else {
      Toast.makeText(requireContext(), "Link is Empty!!", Toast.LENGTH_LONG).show()
      (activity as CartActivity).popFragmentFromBackStack()
    }

    web_addons_back.setOnClickListener {
      (activity as CartActivity).popFragmentFromBackStack()
    }
  }

}
