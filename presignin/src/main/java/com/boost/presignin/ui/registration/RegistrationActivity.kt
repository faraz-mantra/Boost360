package com.boost.presignin.ui.registration

import com.boost.presignin.constant.IntentConstant
import com.framework.base.BaseFragment
import com.framework.base.FragmentContainerActivity

class RegistrationActivity : FragmentContainerActivity() {

    override fun isHideToolbar(): Boolean {
        return true
    }
    override fun shouldAddToBackStack(): Boolean {
        return false
    }
    private val phoneNumber by lazy {
        intent.getStringExtra(IntentConstant.EXTRA_PHONE_NUMBER.name)
    }
    override fun getFragmentInstance(type: Int?): BaseFragment<*, *> {
       return CategoryFragment.newInstance(phoneNumber)
    }

}