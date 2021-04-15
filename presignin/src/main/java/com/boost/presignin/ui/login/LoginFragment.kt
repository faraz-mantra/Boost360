package com.boost.presignin.ui.login

import com.boost.presignin.R
import com.boost.presignin.databinding.FragmentLoginBinding
import com.framework.base.BaseFragment
import com.framework.extensions.onTextChanged
import com.framework.models.BaseViewModel


class LoginFragment : BaseFragment<FragmentLoginBinding, BaseViewModel>() {


    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment().apply {}
    }

    override fun getLayout(): Int {
        return  R.layout.fragment_login
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.usernameEt?.onTextChanged {
            onDataChanged();
        }
        binding?.passEt?.onTextChanged {
            onDataChanged()
        }
        binding?.forgotTv?.setOnClickListener {

        }

        binding?.forgotTv?.setOnClickListener {
            addFragmentReplace(com.framework.R.id.container, ForgetPassFragment.newInstance(),true)
        }


    }


    private fun onDataChanged(){
        val username = binding?.usernameEt?.text?.toString()
        val password = binding?.passEt?.text?.toString()
        binding?.loginBt?.isEnabled = !username.isNullOrBlank() && !password.isNullOrBlank()
    }
}