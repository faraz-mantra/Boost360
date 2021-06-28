package com.boost.presignin.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.boost.presignin.R
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.ActivityAccountNotFoundBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.ui.registration.RegistrationActivity
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.*

class AccountNotFoundActivity : BaseActivity<ActivityAccountNotFoundBinding, BaseViewModel>() {

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
        WebEngageController.trackEvent(PS_CREATE_LOGIN_OTHER_WAY, PAGE_VIEW, NO_EVENT_VALUE)
        binding?.retrieveAccountBt?.setOnClickListener {
            WebEngageController.trackEvent(PS_RETRY_ACCOUNT_ACCOUNT_CLICK, CLICKED, NO_EVENT_VALUE)
            startActivity(Intent(this@AccountNotFoundActivity, DesiredLoginMethodActivity::class.java))
        }
        binding?.backIv?.setOnClickListener {
            onBackPressed()
        }
        val amountSpannableString = SpannableString(" +91-$phoneNumber").apply {
            setSpan(ForegroundColorSpan(Color.rgb(0, 0, 0)), 0, length, 0)
            setSpan(StyleSpan(BOLD), 0, length, 0)
        }
        val spannable: Spannable = SpannableStringBuilder().apply {
            append(getString(R.string.psn_no_account_hint_before))
            append(amountSpannableString)
            append(getString(R.string.psn_no_account_hint_after))
        }
        binding?.subheading?.text = spannable
        binding?.createAccountBt?.setOnClickListener {
            WebEngageController.trackEvent(PS_CREATE_BUSINESS_PROFILE_CLICK, CLICKED, NO_EVENT_VALUE)
            val bundle = Bundle()
            bundle.putSerializable(IntentConstant.EXTRA_PHONE_NUMBER.name,phoneNumber)
            navigator?.startActivity(RegistrationActivity::class.java,bundle)
        }
    }
}