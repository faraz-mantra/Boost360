package com.boost.presignin.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.boost.presignin.R
import com.boost.presignin.databinding.ActivityAccountNotFoundBinding
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

class AccountNotFoundActivity : BaseActivity<ActivityAccountNotFoundBinding,BaseViewModel>() {


    override fun getLayout(): Int {
      return  R.layout.activity_account_not_found
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
       return BaseViewModel::class.java
    }

    override fun onCreateView() {
       binding?.retrieveAccountBt?.setOnClickListener {
           startActivity(Intent(this@AccountNotFoundActivity, DesiredLoginMethodActivity::class.java))
       }
    }
}