package com.boost.presignin.ui.login

import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentForgetPassBinding
import com.framework.base.BaseFragment
import com.framework.extensions.onTextChanged
import com.framework.models.BaseViewModel

class ForgetPassFragment : BaseFragment<FragmentForgetPassBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance() = ForgetPassFragment()
    }


    override fun getLayout(): Int {
        return R.layout.fragment_forget_pass
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.emailEt?.onTextChanged {
            binding?.getLinkBt?.isEnabled = it.isNotEmpty()
        }

    }
}