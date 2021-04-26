package com.boost.presignin.ui

import com.boost.presignin.R
import com.boost.presignin.base.AppBaseActivity
import com.boost.presignin.databinding.ActivityWebPreviewBinding
import com.boost.presignin.model.RequestFloatsModel
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
        binding?.webview?.loadUrl(requestFloatsModel?.webSiteUrl!!)
        binding?.headingTv?.text = requestFloatsModel?.categoryDataModel?.category_Name
        binding?.ctvUrl?.text = requestFloatsModel?.webSiteUrl
        binding?.closeIcon?.setOnClickListener { finish() }
    }

}