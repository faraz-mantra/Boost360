package com.boost.presignin.ui.login

import com.framework.base.BaseFragment
import com.framework.base.FragmentContainerActivity
import com.framework.models.BaseViewModel

class LoginActivity : FragmentContainerActivity() {


    override fun isHideToolbar(): Boolean {
        return true
    }
    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun shouldAddToBackStack(): Boolean {
        return false
    }

    override fun getFragmentInstance(type: Int?): BaseFragment<*, *> {
        return LoginFragment.newInstance();
    }
}