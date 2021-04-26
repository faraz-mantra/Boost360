package com.boost.presignin.ui

import android.content.Intent
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import com.boost.presignin.R
import com.boost.presignin.databinding.ActivityDesiredLoginMethodBinding
import com.boost.presignin.ui.login.LoginActivity
import com.boost.presignin.ui.mobileVerification.MobileVerificationActivity
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class DesiredLoginMethodActivity : BaseActivity<ActivityDesiredLoginMethodBinding, BaseViewModel>() {


    override fun getLayout(): Int {
        return R.layout.activity_desired_login_method
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.usernameOrEmailBt?.setOnClickListener {
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
            navigator?.startActivity(MobileVerificationActivity::class.java)
        }

    }

    private fun backPressed() {
        binding?.backIv?.setOnClickListener {
           finish()
        }

    }
}