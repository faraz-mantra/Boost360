package com.boost.presignin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.boost.presignin.R
import com.boost.presignin.databinding.ActivityWebPreviewBinding
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class WebPreviewActivity : BaseActivity<ActivityWebPreviewBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.activity_web_preview
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.webview?.loadUrl("https://www.google.com")
        binding?.closeIcon?.setOnClickListener { finish() }
    }


}