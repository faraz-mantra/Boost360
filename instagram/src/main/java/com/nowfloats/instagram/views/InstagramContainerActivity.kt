package com.nowfloats.instagram.views

import com.nowfloats.instagram.base.AppBaseActivity
import com.framework.models.BaseViewModel
import com.nowfloats.instagram.R
import com.nowfloats.instagram.databinding.ActivityInstagramContainerBinding

class InstagramContainerActivity:
    AppBaseActivity<ActivityInstagramContainerBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_instagram_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        addFragmentReplace(binding?.container?.id,IGIntroScreenFragment.newInstance(),false)

    }

}