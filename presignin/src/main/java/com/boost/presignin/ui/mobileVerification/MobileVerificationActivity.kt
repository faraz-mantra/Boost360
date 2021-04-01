package com.boost.presignin.ui.mobileVerification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.boost.presignin.R
import com.framework.base.BaseFragment
import com.framework.base.FragmentContainerActivity

class MobileVerificationActivity : FragmentContainerActivity() {

    override fun shouldAddToBackStack(): Boolean {
        return false;
    }

    override fun getFragmentInstance(type: Int?): BaseFragment<*, *> {
       return MobileFragment.newInstance()
    }

}