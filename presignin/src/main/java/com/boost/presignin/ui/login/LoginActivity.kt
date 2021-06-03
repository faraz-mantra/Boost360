package com.boost.presignin.ui.login

import android.content.Intent
import com.boost.presignin.ui.mobileVerification.*
import com.boost.presignin.ui.registration.CategoryFragment
import com.boost.presignin.ui.registration.RegistrationSuccessFragment
import com.boost.presignin.ui.registration.SUCCESS_FRAGMENT
import com.framework.base.BaseFragment
import com.framework.base.FragmentContainerActivity
import com.framework.models.BaseViewModel

const val FORGOT_FRAGMENT = 104
const val LOGIN_SUCCESS_FRAGMENT = 105

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
    return when (type) {
      FORGOT_FRAGMENT -> ForgetPassFragment.newInstance(intent.extras)
      LOGIN_SUCCESS_FRAGMENT -> SuccessLoginNumberFragment.newInstance(intent.extras)
      else -> LoginFragment.newInstance()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    for (fragment in supportFragmentManager.fragments) {
      fragment.onActivityResult(requestCode, resultCode, data)
    }
  }
}