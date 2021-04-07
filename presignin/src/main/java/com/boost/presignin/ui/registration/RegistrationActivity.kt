package com.boost.presignin.ui.registration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.boost.presignin.R
import com.framework.base.BaseFragment
import com.framework.base.FragmentContainerActivity

class RegistrationActivity : FragmentContainerActivity() {
    override fun shouldAddToBackStack(): Boolean {
        return false
    }

    override fun getFragmentInstance(type: Int?): BaseFragment<*, *> {
       return CategoryFragment.newInstance()
    }

}