package com.boost.marketplace.ui.details.emails

import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityEmailBinding

class EmailActivity : AppBaseActivity<ActivityEmailBinding, EmailViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_email
    }

    override fun getViewModelClass(): Class<EmailViewModel> {
        return EmailViewModel::class.java
    }
}