package com.boost.presignin.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseActivity
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.ActivityAccountNotFoundBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.ui.login.LoginActivity
import com.boost.presignin.ui.registration.RegistrationActivity
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.*

class AccountNotFoundActivity : AppBaseActivity<ActivityAccountNotFoundBinding, BaseViewModel>() {

  private val phoneNumber by lazy {
    intent.getStringExtra(IntentConstant.EXTRA_PHONE_NUMBER.name)
  }

  override fun getLayout(): Int {
    return R.layout.activity_account_not_found
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.helpTv)
    WebEngageController.trackEvent(PS_CREATE_LOGIN_OTHER_WAY, PAGE_VIEW, NO_EVENT_VALUE)
    binding?.usernameAccountBt?.setOnClickListener {
      WebEngageController.trackEvent(PS_RETRY_ACCOUNT_ACCOUNT_CLICK, CLICKED, NO_EVENT_VALUE)
      navigator?.startActivityForResult(LoginActivity::class.java, 300)
    }
    binding?.tryDifferentNumberBtn?.setOnClickListener {
      WebEngageController.trackEvent(PS_TRY_DIFFERENT_NUMBER_CLICK, CLICKED, NO_EVENT_VALUE)
      finish()
    }
    binding?.backIv?.setOnClickListener { onNavPressed() }

    binding?.heading?.text = "${getString(R.string.no_business_account_available_with)} +91-$phoneNumber"
    binding?.createAccountBt?.setOnClickListener {
      WebEngageController.trackEvent(PS_CREATE_BUSINESS_PROFILE_CLICK, CLICKED, NO_EVENT_VALUE)
      val bundle = Bundle()
      bundle.putSerializable(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
      navigator?.startActivity(RegistrationActivity::class.java, bundle)
    }
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when(v){
      binding?.helpTv->{
        needHelp()
      }
    }
  }
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 300 && resultCode == RESULT_OK) finish()
  }
}