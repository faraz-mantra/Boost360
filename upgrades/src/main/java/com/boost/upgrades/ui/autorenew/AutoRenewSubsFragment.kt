package com.boost.upgrades.ui.autorenew

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.ui.confirmation.AutoRenewOrderConfirmationFragment
import com.boost.upgrades.utils.Constants
import kotlinx.android.synthetic.main.auto_renew_fragment.*


class AutoRenewSubsFragment : BaseFragment("MarketPlaceAutoRenewSubsFragment") {

  lateinit var root: View

  var link: String? = null

  companion object {
    fun newInstance() = AutoRenewSubsFragment()
  }

  private lateinit var viewModel: AutoRenewViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.auto_renew_fragment, container, false)
    link = requireArguments().getString("link") ?: ""
    Log.v("weblink", " " + link)
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(AutoRenewViewModel::class.java)

    if (arguments != null && requireArguments().containsKey("title")) {
      autoRenewPageTitle.text = requireArguments().getString("title")
    }
    loadSpinner(root)
    if (link != null) {
      webviewAutoRenew.getSettings().setJavaScriptEnabled(true)
      webviewAutoRenew.getSettings().setDomStorageEnabled(true)
      webviewAutoRenew.findAllAsync("All payments for this subscription have been made")
      webviewAutoRenew.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                    view.findAllAsync("All payments for this subscription have been made")
          Log.v("shouldOverrideUrl", " " + view)
          return false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
          Log.v("onPageFinished", " " + url)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
          Log.v("onPageStarted", " " + url)
          unloadSpinner(root)
        }
      }
      webviewAutoRenew.findAllAsync("All payments for this subscription have been made")
      webviewAutoRenew.setFindListener(object : WebView.FindListener {
        override fun onFindResultReceived(p0: Int, p1: Int, p2: Boolean) {
          if (p1 > 0) {
//                        Toast.makeText(requireContext(), "Value exist in the page", Toast.LENGTH_LONG).show()
            (activity as UpgradeActivity).replaceFragment(
              AutoRenewOrderConfirmationFragment.newInstance(),
              Constants.AUTO_RENEW_ORDER_CONFIRMATION_FRAGMENT
            )
          }
        }

      })
      webviewAutoRenew.loadUrl(link!!)


    } else {
      Toast.makeText(requireContext(), "Link is Empty!!", Toast.LENGTH_LONG).show()
      (activity as UpgradeActivity).popFragmentFromBackStack()
    }

    back_button12.setOnClickListener {
      (activity as UpgradeActivity).popFragmentFromBackStack()
    }
  }

  internal class MyBrowser : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
      view.loadUrl(url)
      view.addJavascriptInterface(object : Any() {
        @JavascriptInterface
        fun performClick() //method which you call on button click on HTML page
        {

        }
      }, "modal-close") // identify which button you click

      // identify which button you click
      return true
    }
  }

  fun loadSpinner(view: View?) {
    if (progressBar != null) {
      progressBar.visibility = View.VISIBLE
    }

  }

  fun unloadSpinner(view: View?) {
    if (progressBar != null) {
      progressBar.visibility = View.GONE
    }
  }
}
