package com.boost.presignin.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface.BOLD
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.TextView
import com.boost.presignin.R
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.ActivityAccountNotFoundBinding
import com.boost.presignin.ui.registration.RegistrationActivity
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

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
        binding?.retrieveAccountBt?.setOnClickListener {
            startActivity(Intent(this@AccountNotFoundActivity, DesiredLoginMethodActivity::class.java))
        }


        val amountSpannableString = SpannableString(" +91-$phoneNumber").apply {
            setSpan(ForegroundColorSpan(Color.rgb(0,0,0)), 0, length, 0)
            setSpan(StyleSpan(BOLD), 0,length,0)
        }

        val spannable: Spannable = SpannableStringBuilder().apply {
            append(getString(R.string.psn_no_account_hint_before))
            append(amountSpannableString)
            append(getString(R.string.psn_no_account_hint_after))
        }

        binding?.subheading?.text = spannable


        binding?.createAccountBt?.setOnClickListener {
            navigator?.startActivity(RegistrationActivity::class.java)
        }

    }
}