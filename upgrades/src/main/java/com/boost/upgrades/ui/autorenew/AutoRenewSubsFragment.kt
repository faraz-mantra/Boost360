package com.boost.upgrades.ui.autorenew

import android.os.Bundle
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
import kotlinx.android.synthetic.main.auto_renew_fragment.*


class AutoRenewSubsFragment : BaseFragment() {

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
        link = arguments!!.getString("link")
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AutoRenewViewModel::class.java)

        if(arguments !=null && arguments!!.containsKey("title")){
            autoRenewPageTitle.setText(arguments!!.getString("title"))
        }

        if (link != null) {
            webviewAutoRenew.getSettings().setJavaScriptEnabled(true)
//            webviewAutoRenew.findAllAsync("Your subscription is active. Next due on")
            webviewAutoRenew.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return false
                }
            }
            webviewAutoRenew.loadUrl(link)


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
}
