package com.boost.presignin.ui.registration

import android.content.Intent
import com.boost.presignin.constant.IntentConstant
import com.framework.base.BaseFragment
import com.framework.base.FragmentContainerActivity
import com.framework.pref.UserSessionManager

const val SUCCESS_FRAGMENT = 101

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
    return when (type) {
      SUCCESS_FRAGMENT -> RegistrationSuccessFragment.newInstance()
      else -> CategoryFragment.newInstance(phoneNumber)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    for (fragment in supportFragmentManager.fragments) {
      fragment.onActivityResult(requestCode, resultCode, data)
    }
  }
}
