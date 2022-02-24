package com.appservice.ui.background_image

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentBackgroundImageBinding
import com.framework.models.BaseViewModel

class CropZoomImageFragment : AppBaseFragment<FragmentBackgroundImageBinding,BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_crop_zoom
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}