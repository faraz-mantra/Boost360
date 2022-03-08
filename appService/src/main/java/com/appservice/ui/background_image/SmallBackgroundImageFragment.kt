package com.appservice.ui.background_image

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentBackgroundImageBinding
import com.appservice.databinding.FragmentSmallBackgroundImageBinding
import com.framework.models.BaseViewModel

class SmallBackgroundImageFragment : AppBaseFragment<FragmentSmallBackgroundImageBinding,BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_small_background_image
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        TODO("Not yet implemented")
    }
}