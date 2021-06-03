package com.boost.presignin.ui.login

import android.content.Intent
import com.boost.presignin.ui.registration.CategoryFragment
import com.boost.presignin.ui.registration.RegistrationSuccessFragment
import com.boost.presignin.ui.registration.SUCCESS_FRAGMENT
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
    return LoginFragment.newInstance()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    for (fragment in supportFragmentManager.fragments) {
      fragment.onActivityResult(requestCode, resultCode, data)
    }
  }
}