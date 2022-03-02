package com.appservice.ui.background_image

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentPreviewBinding
import com.framework.models.BaseViewModel

class PreviewFragment : AppBaseFragment<FragmentPreviewBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_preview
    }
    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java

    }
}