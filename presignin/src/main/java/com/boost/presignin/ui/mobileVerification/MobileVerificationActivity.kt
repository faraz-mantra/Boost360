package com.boost.presignin.ui.mobileVerification

import android.content.Intent
import com.framework.base.BaseFragment
import com.framework.base.FragmentContainerActivity
import com.framework.pref.clientId

const val OTP_FRAGMENT = 102
const val FP_LIST_FRAGMENT = 103

class MobileVerificationActivity : FragmentContainerActivity() {

  override fun shouldAddToBackStack(): Boolean {
    return false;
  }

  override fun getFragmentInstance(type: Int?): BaseFragment<*, *> {
    return when (type) {

      OTP_FRAGMENT -> OtpVerificationFragment.newInstance(intent.extras)
      FP_LIST_FRAGMENT -> FloatingPointAuthFragment.newInstance(intent.extras)
      else -> MobileFragment.newInstance()
    }
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