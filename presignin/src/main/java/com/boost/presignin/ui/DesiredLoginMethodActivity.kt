package com.boost.presignin.ui

import android.content.Intent
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import com.boost.presignin.R
import com.boost.presignin.databinding.ActivityDesiredLoginMethodBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.ui.login.LoginActivity
import com.boost.presignin.ui.mobileVerification.MobileVerificationActivity
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.*

class DesiredLoginMethodActivity : BaseActivity<ActivityDesiredLoginMethodBinding, BaseViewModel>() {


  override fun getLayout(): Int {
    return R.layout.activity_desired_login_method
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(PS_DESIRED_LOGIN_SCREEN_LOAD, PAGE_VIEW, NO_EVENT_VALUE)

    binding?.usernameOrEmailBt?.setOnClickListener {
      WebEngageController.trackEvent(
        PS_DESIRED_LOGIN_USERNAME_CLICK,
        CLICK_LOGIN_USERNAME,
        NO_EVENT_VALUE
      )
      startActivity(Intent(this@DesiredLoginMethodActivity, LoginActivity::class.java))
    }
    backPressed()
    val amountSpannableString = SpannableString(getString(R.string.contact_support)).apply {
      setSpan(UnderlineSpan(), 0, length, 0)
    }
    binding?.contactSupportTv?.text = SpannableStringBuilder().apply {
      append(amountSpannableString)
    }

    binding?.anotherMethodBt?.setOnClickListener {
      WebEngageController.trackEvent(
        PS_DESIRED_LOGIN_NUMBER_CLICK,
        CLICK_DIFFERENT_NUMBER,
        NO_EVENT_VALUE
      )
      navigator?.startActivity(MobileVerificationActivity::class.java)
    }

  }

  private fun backPressed() {
    binding?.backIv?.setOnClickListener {
      finish()
    }

  }

}