package com.boost.upgrades.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import kotlinx.android.synthetic.main.web_view_fragment.*


class WebViewFragment : BaseFragment() {

    lateinit var root: View

    var link: String? = null

    companion object {
        fun newInstance() = WebViewFragment()
    }

    private lateinit var viewModel: WebViewViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.web_view_fragment, container, false)
        link = requireArguments().getString("link")?:""
        return root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WebViewViewModel::class.java)

        if(arguments !=null && requireArguments().containsKey("title")){
            browser_title.text = requireArguments().getString("title")
        }

        if (link != null) {
            webview.settings.javaScriptEnabled = true
            webview.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return false
                }
            }
            webview.loadUrl(link!!)
        } else {
            Toast.makeText(requireContext(), "Link is Empty!!", Toast.LENGTH_LONG).show()
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        web_addons_back.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }
    }

}
