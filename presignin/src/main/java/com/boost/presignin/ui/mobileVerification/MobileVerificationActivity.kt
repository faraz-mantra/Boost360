package com.boost.presignin.ui.mobileVerification

import com.framework.base.BaseFragment
import com.framework.base.FragmentContainerActivity

class MobileVerificationActivity : FragmentContainerActivity() {

    override fun shouldAddToBackStack(): Boolean {
        return false;
    }

    override fun getFragmentInstance(type: Int?): BaseFragment<*, *> {
       return MobileFragment.newInstance()
    }

    override fun isHideToolbar(): Boolean {
        return true
    }
}