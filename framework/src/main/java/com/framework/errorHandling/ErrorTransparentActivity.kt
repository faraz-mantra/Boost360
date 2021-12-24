package com.framework.errorHandling

import com.framework.R
import com.framework.base.BaseActivity
import com.framework.databinding.ActivityErrorTransparentBinding
import com.framework.models.BaseViewModel

class ErrorTransparentActivity : BaseActivity<ActivityErrorTransparentBinding, BaseViewModel>() {

    override fun getLayout(): Int {
        return R.layout.activity_error_transparent
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        initUI()
    }

    private fun initUI() {
        ErrorOccurredBottomSheet().show(supportFragmentManager, ErrorOccurredBottomSheet::class.java.name)


    }
}