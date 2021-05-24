package com.boost.presignin.ui.mobileVerification

import android.content.Intent
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

}