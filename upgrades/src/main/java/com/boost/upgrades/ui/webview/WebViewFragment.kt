package com.boost.upgrades.ui.webview

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.web_view_fragment.*

class WebViewFragment : BaseFragment() {

    lateinit var root: View

    companion object {
        fun newInstance() = WebViewFragment()
    }

    private lateinit var viewModel: WebViewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.web_view_fragment, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WebViewViewModel::class.java)
        // TODO: Use the ViewModel

        webview.loadUrl("https://www.google.com/")

        web_addons_back.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }
    }

}
