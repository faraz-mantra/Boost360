package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.databinding.FragmentTodaysPickBinding
import com.framework.models.BaseViewModel

class CreatePostFragment: AppBaseFragment<FragmentTodaysPickBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_todays_pick
    }


    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
    companion object {
        fun newInstance(bundle: Bundle = Bundle()): CreatePostFragment {
            val fragment = CreatePostFragment()
            fragment.arguments = bundle
            return fragment
        }


    }
}