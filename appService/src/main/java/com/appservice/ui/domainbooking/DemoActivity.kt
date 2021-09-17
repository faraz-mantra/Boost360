package com.appservice.ui.domainbooking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appservice.R
import com.appservice.databinding.ActivityDomainBookingBinding
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class DemoActivity : BaseActivity<ActivityDomainBookingBinding, BaseViewModel>() {

    override fun getLayout(): Int {
        return R.layout.fragment_book_a_domain_ssl
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
    }
}