package com.appservice.ui.bankaccount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentAddAccountStartBinding
import com.framework.models.BaseViewModel
import com.framework.views.zero.FragmentZeroCase
import com.framework.views.zero.OnZeroCaseClicked
import com.framework.views.zero.RequestZeroCaseBuilder
import com.framework.views.zero.ZeroCases

class AddAccountStartFragment : AppBaseFragment<FragmentAddAccountStartBinding, BaseViewModel>(), OnZeroCaseClicked {
  private  val TAG = "AddAccountStartFragment"
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
    (baseActivity as? AccountFragmentContainerActivity)?.setToolbarTitleNew(getString(R.string.my_bank_acccount))
    addFragment(R.id.container,RequestZeroCaseBuilder(ZeroCases.MY_BANK_ACCOUNT,this,baseActivity).getRequest().build(),true)
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

  override fun primaryButtonClicked() {
    arguments?.let {
      startFragmentAccountActivity(
        FragmentType.BANK_ACCOUNT_DETAILS,
        it,
        isResult = true,
        requestCode = 202
      )
    }
  }

  override fun secondaryButtonClicked() {
    Log.i(TAG, "secondaryButtonClicked: ")
  }

  override fun ternaryButtonClicked() {
    Log.i(TAG, "ternaryButtonClicked: ")
  }

  override fun onBackPressed() {
    Log.i(TAG, "onBackPressed: ")
   baseActivity.finishAfterTransition()
  }
}
