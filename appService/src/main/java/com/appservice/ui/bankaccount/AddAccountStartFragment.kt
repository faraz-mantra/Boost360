package com.appservice.ui.bankaccount

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentAddAccountStartBinding
import com.framework.models.BaseViewModel

class AddAccountStartFragment : AppBaseFragment<FragmentAddAccountStartBinding, BaseViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddAccountStartFragment {
      val fragment = AddAccountStartFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_add_account_start
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.closeBtn, binding?.startBtn)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 202) {
      val output = Intent()
      output.putExtra(
        IntentConstant.USER_BANK_DETAIL.name,
        data?.getSerializableExtra(IntentConstant.USER_BANK_DETAIL.name)
      )
      baseActivity.setResult(AppCompatActivity.RESULT_OK, output)
      baseActivity.finish()
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.closeBtn -> baseActivity.onNavPressed()
      binding?.startBtn -> arguments?.let {
        startFragmentAccountActivity(
          FragmentType.BANK_ACCOUNT_DETAILS,
          it,
          isResult = true,
          requestCode = 202
        )
      }
    }
  }
}
