package com.framework.views

import com.framework.R
import com.framework.base.BaseFragment
import com.framework.databinding.BlankFragmentBinding
import com.framework.models.BaseViewModel

class BlankFragment: BaseFragment<BlankFragmentBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.blank_fragment
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {

    }
}