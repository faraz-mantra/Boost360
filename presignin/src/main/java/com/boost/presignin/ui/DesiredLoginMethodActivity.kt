package com.boost.presignin.ui

import android.content.Intent
import android.os.Bundle
import com.boost.presignin.R
import com.boost.presignin.databinding.ActivityDesiredLoginMethodBinding
import com.boost.presignin.ui.login.LoginActivity
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

    }
}